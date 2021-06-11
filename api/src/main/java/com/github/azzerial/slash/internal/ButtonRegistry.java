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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ButtonRegistry {

    public static final int CODE_LENGTH = 4;
    public static final int ID_BASE = 32;
    private static final ButtonRegistry INSTANCE = new ButtonRegistry();

    private final List<String> codes;
    private final Map<String, ButtonCallback> mappings = new HashMap<>();

    /* Constructors */

    private ButtonRegistry() {
        this.codes = new LinkedList<>();

        codes.add(null);
    }

    /* Getters & Setters */

    public static ButtonRegistry getInstance() {
        return INSTANCE;
    }

    public String createButtonId(String tag, String data) {
        final int code = codes.indexOf(tag);
        return String.format(
            "%-" + CODE_LENGTH + "." + CODE_LENGTH + "s" +
            "%." + (100 - CODE_LENGTH) + "s",
            Integer.toUnsignedString(code == -1 ? 0 : code, ID_BASE),
            data == null ? "" : data
        );
    }

    public ButtonCallback getButtonCallback(String id) {
        final int code = Integer.parseUnsignedInt(parseCode(id), ID_BASE);
        final String tag = codes.get(code);
        return mappings.get(tag);
    }

    /* Methods */

    public void registerButton(String tag, ButtonCallback callback) {
        if (!codes.contains(tag)) {
            codes.add(tag);
            mappings.put(tag, callback);
        }
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
}
