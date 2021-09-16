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

package net.azzerial.slash.internal;

import net.azzerial.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class InteractionListener extends ListenerAdapter {

    private final CommandRegistry registry;

    /* Constructors */

    public InteractionListener(CommandRegistry registry) {
        this.registry = registry;
    }

    /* Methods */

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        final SlashCommand command = registry.getCommandById(event.getCommandIdLong());

        if (command != null) {
            final Method method = command.getHandlers().get(event.getCommandPath());

            if (method != null) {
                try {
                    method.invoke(command.getObjectInstance(), event);
                } catch (IllegalAccessException | InvocationTargetException ignored) {}
            }
        }
    }

    @Override
    public void onGenericComponentInteractionCreate(GenericComponentInteractionCreateEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        final ComponentCallback callback = ComponentRegistry.getInstance().getComponentCallback(event.getComponentId());

        if (callback != null) {
            final Method method = callback.getMethod();

            try {
                method.invoke(callback.getObjectInstance(), event);
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }
    }
}
