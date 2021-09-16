/*
 * Copyright 2021 Robin Mercier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.azzerial.slash.util;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.azzerial.slash.internal.ComponentRegistry.CODE_LENGTH;

public final class Session extends DataObject {

    public static final long DEFAULT_TIMEOUT = 60_000L;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    private static final ScheduledExecutorService threadpool = Executors.newSingleThreadScheduledExecutor();
    private static final Map<UUID, Session> sessions = new HashMap<>();

    private final UUID uuid;
    private final Map<UUID, DataObject> storage = new HashMap<>();

    private final long timeout;
    private final TimeUnit unit;
    private final InteractionHook hook;
    private final BiConsumer<InteractionHook, Session> action;
    private ScheduledFuture<?> thread;

    /* Constructors */

    private Session(Session session, Map<String, Object> data) {
        this(session.uuid, data, session.timeout, session.unit, session.hook, session.action);
    }

    private Session(UUID uuid, Map<String, Object> data, long timeout, TimeUnit unit, InteractionHook hook, BiConsumer<InteractionHook, Session> action) {
        super(data);
        Checks.notNull(uuid, "UUID");
        Checks.notNegative(timeout, "Timeout");
        this.uuid = uuid;
        this.timeout = timeout;
        this.unit = unit;
        this.hook = hook;
        this.action = action;

        startTimeoutThread();
    }

    /* Getters & Setters */

    public String getUuid() {
        return uuid.toString();
    }

    /* Methods */

    public static Session create() {
        return create(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT, null, null);
    }

    public static Session create(long timeout, TimeUnit unit) {
        return create(timeout, unit, null, null);
    }

    public static Session create(long timeout, TimeUnit unit, InteractionHook hook, BiConsumer<InteractionHook, Session> action) {
        Checks.positive(timeout, "Timeout");
        Checks.notNull(unit, "Unit");
        final UUID uuid = UUID.randomUUID();
        final Session session = new Session(uuid, new HashMap<>(), timeout, unit, hook, action);

        sessions.put(uuid, session);
        return session;
    }

    public static Session load(String id) {
        return get(id, false);
    }

    public static Session renew(String id) {
        return get(id, true);
    }

    @NotNull
    @Override
    public Session remove(@NotNull String key) {
        super.remove(key);
        return this;
    }

    @NotNull
    @Override
    public Session putNull(@NotNull String key) {
        super.putNull(key);
        return this;
    }

    @NotNull
    @Override
    public Session put(@NotNull String key, @Nullable Object value) {
        super.put(key, value);
        return this;
    }

    public String store(Consumer<DataObject> consumer) {
        final UUID uuid = UUID.randomUUID();
        final DataObject data = DataObject.fromJson(toJson());

        consumer.accept(data);
        storage.put(uuid, data);
        return getUuid() + uuid;
    }

    /* Internal */

    private static Session get(String id, boolean renew) {
        Checks.notNull(id, "Id");
        if (id.length() != CODE_LENGTH + 36 && id.length() != CODE_LENGTH + 72) {
            throw new IllegalArgumentException("The id is invalid!");
        }

        final String sessionStr = id.substring(CODE_LENGTH, CODE_LENGTH + 36);
        final String storageStr = id.length() == CODE_LENGTH + 72 ?
            id.substring(CODE_LENGTH  +36, CODE_LENGTH + 72) :
            null;

        if (!sessionStr.matches(UUID_REGEX) || (storageStr != null && !storageStr.matches(UUID_REGEX))) {
            throw new IllegalArgumentException("The id is invalid!");
        }

        final UUID sessionUuid = UUID.fromString(sessionStr);
        Session session = sessions.remove(sessionUuid);

        if (session == null) {
            return null;
        } else if (session.thread != null && !session.thread.isDone()) {
            session.thread.cancel(true);
        }

        final UUID storageUuid = storageStr != null ? UUID.fromString(storageStr) : null;
        final DataObject data = session.storage.get(storageUuid);

        if (data != null && !data.keys().isEmpty()) {
            session = new Session(session, data.toMap());
        }
        if (renew) {
            session.startTimeoutThread();
            sessions.put(session.uuid, session);
        }
        return session;
    }

    private void startTimeoutThread() {
        if (timeout > 0 && unit != null) {
            if (thread != null && !thread.isDone()) {
                return;
            }

            this.thread = threadpool.schedule(() -> {
                if (sessions.remove(uuid) != null && hook != null && action != null) {
                    action.accept(hook, this);
                }
            }, timeout, unit);
        } else {
            this.thread = null;
        }
    }
}
