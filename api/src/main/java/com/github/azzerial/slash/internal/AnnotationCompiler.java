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

package com.github.azzerial.slash.internal;

import com.github.azzerial.slash.annotations.Option;
import com.github.azzerial.slash.annotations.Slash;
import com.github.azzerial.slash.annotations.Subcommand;
import com.github.azzerial.slash.annotations.SubcommandGroup;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class AnnotationCompiler {

    /* Methods */

    public CommandData compileCommand(Slash.Command command) {
        final CommandData data = new CommandData(command.name(), command.description());

        data.setDefaultEnabled(command.enabled());
        if (command.subcommands().length != 0) {
            data.addSubcommands(
                Arrays.stream(command.subcommands())
                    .map(this::compileSubcommand)
                    .collect(Collectors.toList())
            );
        }
        if (command.subcommandGroups().length != 0) {
            data.addSubcommandGroups(
                Arrays.stream(command.subcommandGroups())
                    .map(this::compileSubcommandGroup)
                    .collect(Collectors.toList())
            );
        }
        if (command.options().length != 0) {
            data.addOptions(
                Arrays.stream(command.options())
                    .map(this::compileOption)
                    .collect(Collectors.toList())
            );
        }
        return data;
    }

    /* Internal */

    private OptionData compileOption(Option option) {
        Checks.notNull(option, "Option");
        final OptionData data = new OptionData(
            OptionType.fromKey(option.type().ordinal() + 3),
            option.name(),
            option.description(),
            option.required()
        );

        if (option.type() == com.github.azzerial.slash.annotations.OptionType.STRING
            || option.type() == com.github.azzerial.slash.annotations.OptionType.INTEGER) {
            data.addChoices(
                Arrays.stream(option.choices())
                    .map(choice -> {
                        switch (option.type()) {
                            case STRING: return new Command.Choice(choice.name(), choice.value());
                            case INTEGER: return new Command.Choice(choice.name(), Integer.parseInt(choice.value()));
                            default: return null;
                        }
                    })
                    .collect(Collectors.toList())
            );
        }
        return data;
    }

    private SubcommandData compileSubcommand(Subcommand subcommand) {
        Checks.notNull(subcommand, "Subcommand");
        return new SubcommandData(subcommand.name(), subcommand.description())
            .addOptions(
                Arrays.stream(subcommand.options())
                    .map(this::compileOption)
                    .collect(Collectors.toList())
            );
    }

    private SubcommandGroupData compileSubcommandGroup(SubcommandGroup subcommandGroup) {
        Checks.notNull(subcommandGroup, "SubcommandGroup");
        return new SubcommandGroupData(subcommandGroup.name(), subcommandGroup.description())
            .addSubcommands(
                Arrays.stream(subcommandGroup.subcommands())
                    .map(this::compileSubcommand)
                    .collect(Collectors.toList())
            );
    }
}
