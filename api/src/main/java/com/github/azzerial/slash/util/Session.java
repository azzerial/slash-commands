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

package com.github.azzerial.slash.util;

import com.github.azzerial.slash.internal.ButtonRegistry;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Session extends DataObject {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    private static final ScheduledExecutorService threadpool = Executors.newSingleThreadScheduledExecutor();
    private static final Map<UUID, Session> sessions = new HashMap<>();

    private final UUID uuid;

    private boolean isTimeoutSet;

    /* Constructors */

    private Session(UUID uuid) {
        super(new HashMap<>());
        this.uuid = uuid;
    }

    /* Getters & Setters */

    public String getUuid() {
        return uuid.toString();
    }

    /* Methods */

    public static Session create() {
        final UUID uuid = UUID.randomUUID();
        final Session session = new Session(uuid);

        sessions.put(uuid, session);
        return session;
    }

    public static Session load(String id) {
        Checks.notNull(id, "Id");
        if (id.length() != ButtonRegistry.CODE_LENGTH + 36 && !id.matches(UUID_REGEX)) {
            throw new IllegalArgumentException("The id is invalid!");
        }
        final UUID uuid = UUID.fromString(id.substring(ButtonRegistry.CODE_LENGTH, ButtonRegistry.CODE_LENGTH + 36));
        return sessions.remove(uuid);
    }

    public Session setTimeout(long timeout, TimeUnit unit) {
        return setTimeout(timeout, unit, null, null);
    }

    public Session setTimeout(long timeout, TimeUnit unit, InteractionHook hook, Consumer<InteractionHook> action) {
        if (isTimeoutSet) {
            throw new IllegalStateException("The timeout has already been set!");
        }
        Checks.positive(timeout, "Timeout");
        Checks.notNull(unit, "Unit");
        this.isTimeoutSet = true;
        threadpool.schedule(() -> {
            if (sessions.remove(uuid) != null && hook != null && action != null) {
                action.accept(hook);
            }
        }, timeout, unit);
        return this;
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

    @NotNull
    @Override
    public Session toData() {
        super.toData();
        return this;
    }
}
