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

import net.azzerial.slash.annotations.Option;
import net.azzerial.slash.annotations.Slash;
import net.azzerial.slash.annotations.Subcommand;
import net.azzerial.slash.annotations.SubcommandGroup;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.utils.Checks;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
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

    public Map<String, Method> compileHandlers(Class<?> cls, CommandData data) {
        final List<Method> methods = Arrays.stream(cls.getDeclaredMethods())
            .filter(method ->
                (method.getModifiers() & (Modifier.PROTECTED | Modifier.PRIVATE)) == 0
                    && method.isAnnotationPresent(Slash.Handler.class)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == SlashCommandEvent.class
            )
            .collect(Collectors.toList());
        final Map<String, Method> handlers = buildHandlers(cls, methods);
        final Set<String> paths = buildPaths(data);
        final Map<String, Method> mappings = new HashMap<>();

        for (String path : paths) {
            final String commandPath = path.isEmpty() ? data.getName() : data.getName() + "/" + path;

            if (handlers.containsKey(path)) {
                mappings.put(commandPath, handlers.get(path));
                continue;
            }

            final String[] parts = path.split("/");

            if (parts.length == 2 && handlers.containsKey("*/" + parts[1])) {
                mappings.put(commandPath, handlers.get("*/" + parts[1]));
            } else if (parts.length == 2 && handlers.containsKey(parts[0])) {
                mappings.put(commandPath, handlers.get(parts[0]));
            } else if (handlers.containsKey("")) {
                mappings.put(commandPath, handlers.get(""));
            }
        }
        return mappings;
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

        if (option.type().canSupportsChoices()) {
            data.addChoices(
                Arrays.stream(option.choices())
                    .map(choice -> {
                        switch (option.type()) {
                            case STRING: return new Command.Choice(choice.name(), choice.value());
                            case INTEGER: return new Command.Choice(choice.name(), Long.parseLong(choice.value()));
                            case NUMBER: return new Command.Choice(choice.name(), Double.parseDouble(choice.value()));
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

    private Map<String, Method> buildHandlers(Class<?> cls, List<Method> methods) {
        final Map<String, Method> handlers = new HashMap<>();

        for (Method method : methods) {
            final Slash.Handler handler = method.getAnnotation(Slash.Handler.class);

            if (!handlers.containsKey(handler.value())) {
                handlers.put(handler.value(), method);
            } else {
                throw new IllegalArgumentException("Multiple handlers were declared for the '" + handler.value() + "' command path in " + cls.getSimpleName() + ".class!");
            }
        }
        return handlers;
    }

    private Set<String> buildPaths(CommandData data) {
        final Set<String> paths = new HashSet<>();

        if (!data.getSubcommandGroups().isEmpty()) {
            for (SubcommandGroupData subcommandGroup : data.getSubcommandGroups()) {
                for (SubcommandData subcommand : subcommandGroup.getSubcommands()) {
                    paths.add(subcommandGroup.getName() + "/" + subcommand.getName());
                }
            }
        } else if (!data.getSubcommands().isEmpty()) {
            for (SubcommandData subcommand : data.getSubcommands()) {
                paths.add(subcommand.getName());
            }
        } else {
            paths.add("");
        }
        return paths;
    }
}
