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

import com.github.azzerial.slash.annotations.Slash;
import com.github.azzerial.slash.internal.util.UnsignedBase512;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public final class ComponentRegistry {

    public static final int CODE_LENGTH = 4;
    public static final Pattern ID_PATTERN;
    private static final ComponentRegistry INSTANCE = new ComponentRegistry();

    static {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i != CODE_LENGTH; i++) {
            sb.append('[' + UnsignedBase512.NUMERALS + "]{").append(i + 1).append('}');
            for (int n = CODE_LENGTH - i - 1; n != 0; n--) {
                sb.append(' ');
            }
            if (i + 1 != CODE_LENGTH) {
                sb.append('|');
            }
        }
        sb.append(")(.)?");

        ID_PATTERN = Pattern.compile(sb.toString());
    }

    private final List<String> codes;
    private final Map<String, ComponentCallback> mappings = new HashMap<>();

    /* Constructors */

    private ComponentRegistry() {
        this.codes = new LinkedList<>();

        codes.add(null);
    }

    /* Getters & Setters */

    public static ComponentRegistry getInstance() {
        return INSTANCE;
    }

    public String formatComponentId(String tag, String data) {
        final int code = codes.indexOf(tag);
        return String.format(
            "%-" + CODE_LENGTH + "." + CODE_LENGTH + "s" +
            "%." + (100 - CODE_LENGTH) + "s",
            UnsignedBase512.toString(code == -1 ? 0 : code),
            data == null ? "" : data
        ).trim();
    }

    public ComponentCallback getComponentCallback(String id) {
        if (!ID_PATTERN.matcher(id).matches()) {
            return null;
        }
        final int code = UnsignedBase512.parseInt(parseCode(id));
        final String tag = codes.get(code);
        return mappings.get(tag);
    }

    /* Methods */

    public void registerComponent(Object obj) {
        Checks.notNull(obj, "Obj");
        registerButtons(obj);
        registerSelectionMenus(obj);
    }

    /* Internal */

    private String parseCode(String s) {
        return s == null || s.isEmpty() ?
            null :
            s.substring(0, Math.min(CODE_LENGTH, s.length())).trim();
    }

    private String parseData(String s) {
        return s == null || s.length() <= CODE_LENGTH ?
            null :
            s.substring(CODE_LENGTH);
    }

    private void registerButtons(Object obj) {
        final Class<?> cls = obj.getClass();

        Arrays.stream(cls.getDeclaredMethods())
            .filter(method ->
                (method.getModifiers() & (Modifier.PROTECTED | Modifier.PRIVATE)) == 0
                    && method.isAnnotationPresent(Slash.Button.class)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == ButtonClickEvent.class
            )
            .sorted(Comparator.comparing(Method::getName))
            .forEach(method -> {
                final String tag = method.getAnnotation(Slash.Button.class).value();

                if (!tag.isEmpty()) {
                    registerComponentMapping(tag, new ComponentCallback(obj, method));
                }
            });
    }

    private void registerSelectionMenus(Object obj) {
        final Class<?> cls = obj.getClass();

        Arrays.stream(cls.getDeclaredMethods())
            .filter(method ->
                (method.getModifiers() & (Modifier.PROTECTED | Modifier.PRIVATE)) == 0
                    && method.isAnnotationPresent(Slash.SelectionMenu.class)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == SelectionMenuEvent.class
            )
            .sorted(Comparator.comparing(Method::getName))
            .forEach(method -> {
                final String tag = method.getAnnotation(Slash.SelectionMenu.class).value();

                if (!tag.isEmpty()) {
                    registerComponentMapping(tag, new ComponentCallback(obj, method));
                }
            });
    }

    private void registerComponentMapping(String tag, ComponentCallback callback) {
        if (!codes.contains(tag)) {
            codes.add(tag);
            mappings.put(tag, callback);
        }
    }
}
