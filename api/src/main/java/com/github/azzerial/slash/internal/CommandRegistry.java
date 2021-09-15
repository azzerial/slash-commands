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

import com.github.azzerial.slash.SlashCommand;
import com.github.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CommandRegistry {

    private final JDA jda;
    private final Map<String, SlashCommand> registry = new HashMap<>();
    private final AnnotationCompiler annotationCompiler = new AnnotationCompiler();

    /* Constructors */

    public CommandRegistry(JDA jda) {
        this.jda = jda;
    }

    /* Getters & Setters */

    public SlashCommand getCommand(String tag) {
        return registry.get(tag);
    }

    public SlashCommand getCommandById(long id) {
        return registry.values().stream()
            .filter(command -> command.getCommandIds().contains(id))
            .findFirst()
            .orElse(null);
    }

    public Collection<SlashCommand> getCommands() {
        return registry.values();
    }

    /* Methods */

    public SlashCommand registerCommand(Object obj) {
        final SlashCommand command = compileCommand(obj);

        ComponentRegistry.getInstance().registerComponent(obj);
        registry.put(command.getTag(), command);
        return command;
    }

    /* Internal */

    private SlashCommand compileCommand(Object obj) {
        final Class<?> cls = obj.getClass();
        final Slash.Tag tag = cls.getAnnotation(Slash.Tag.class);
        final Slash.Command command = cls.getAnnotation(Slash.Command.class);

        if (tag == null) {
            throw new IllegalArgumentException("Provided " + cls.getSimpleName() + ".class is not annotated with @Slash.Tag!");
        }
        if (command == null) {
            throw new IllegalArgumentException("Provided " + cls.getSimpleName() + ".class is not annotated with @Slash.Command!");
        }
        if (registry.containsKey(tag.value())) {
            throw new IllegalArgumentException("Tried to register " + cls.getSimpleName() + ".class, but the '" + tag.value() + "' tag was already in use!");
        }

        final CommandData data = annotationCompiler.compileCommand(command);
        final Map<String, Method> handlers = annotationCompiler.compileHandlers(cls, data);
        return new SlashCommand(jda, tag.value(), data, obj, handlers);
    }
}
