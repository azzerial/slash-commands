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

package com.github.azzerial.slash;

import com.github.azzerial.slash.internal.CommandRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Collection;
import java.util.List;

public final class SlashClientBuilder {

    private final JDA jda;
    private final CommandRegistry registry;

    private boolean deleteUnregisteredCommands = false;

    /* Constructors */

    private SlashClientBuilder(JDA jda) {
        this.jda = jda;
        this.registry = new CommandRegistry(jda);
    }

    /* Methods */

    public static SlashClientBuilder create(JDA jda) {
        Checks.notNull(jda, "JDA");
        return new SlashClientBuilder(jda);
    }

    public SlashClientBuilder addCommand(Object command) {
        Checks.notNull(command, "Command");
        registry.registerCommand(command);
        return this;
    }

    public SlashClientBuilder addCommands(Object... commands) {
        Checks.notNull(commands, "Commands");
        for (Object command : commands) {
            addCommand(command);
        }
        return this;
    }

    public SlashClientBuilder deleteUnregisteredCommands(boolean enabled) {
        this.deleteUnregisteredCommands = enabled;
        return this;
    }

    public SlashClient build() {
        Checks.check(jda.getStatus() == JDA.Status.CONNECTED, "JDA is not JDA.Status.CONNECTED! Maybe you forgot to call JDA#awaitReady()?");
        final Collection<SlashCommand> commands = registry.getCommands();

        loadGlobalCommands(commands);
        loadGuildCommands(commands);
        return new SlashClient(jda, registry);
    }

    /* Internal */

    private void loadGlobalCommands(Collection<SlashCommand> commands) {
        final List<Command> cmds = jda.retrieveCommands().complete();

        for (SlashCommand command : commands) {
            for (Command cmd : cmds) {
                if (cmd.getName().equals(command.getData().getName())) {
                    command.putCommand(SlashCommand.GLOBAL, cmd);
                } else if (deleteUnregisteredCommands) {
                    cmd.delete().queue();
                }
            }
        }
    }

    private void loadGuildCommands(Collection<SlashCommand> commands) {
        jda.getGuilds()
            .forEach(guild -> {
                final List<Command> cmds = guild.retrieveCommands().complete();

                for (SlashCommand command : commands) {
                    for (Command cmd : cmds) {
                        if (cmd.getName().equals(command.getData().getName())) {
                            command.putCommand(guild.getIdLong(), cmd);
                        } else if (deleteUnregisteredCommands) {
                            cmd.delete().queue();
                        }
                    }
                }
            });
    }
}
