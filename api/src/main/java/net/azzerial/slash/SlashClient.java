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

package net.azzerial.slash;

import net.azzerial.slash.internal.CommandRegistry;
import net.azzerial.slash.internal.InteractionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.EnumSet;

public final class SlashClient {

    private final JDA jda;
    private final CommandRegistry registry;
    private final EventListener listener;

    /* Constructors */

    SlashClient(JDA jda, CommandRegistry registry) {
        this.jda = jda;
        this.registry = registry;
        this.listener = new InteractionListener(registry);

        jda.addEventListener(listener);
    }

    /* Methods */

    public SlashCommand getCommand(String tag) {
        return registry.getCommand(tag);
    }

    /* Nested Classes */

    public enum Flag {
        DELETE_UNREGISTERED_COMMANDS;

        private final boolean isDefault;

        /* Constructors */

        Flag() {
            this(false);
        }

        Flag(boolean isDefault) {
            this.isDefault = isDefault;
        }

        /* Methods */

        public static EnumSet<Flag> getDefault() {
            final EnumSet<Flag> set = EnumSet.noneOf(Flag.class);

            for (Flag flag : values()) {
                if (flag.isDefault) {
                    set.add(flag);
                }
            }
            return set;
        }
    }
}
