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

public final class Buffer {

    /* Nested Classes */

    public static final class Reader {

        private final String buffer;

        private int i = ButtonRegistry.CODE_LENGTH;

        /* Constructors */

        public Reader(String buffer) {
            this.buffer = buffer;
        }

        /* Methods */

        public static Reader of(String buffer) {
            return new Reader(buffer);
        }

        public Data read(int size) {
            final String s = buffer.substring(i, Math.min(buffer.length(), i + size));

            i += s.length();
            return new Data(s);
        }

        /* Nested Classes */

        public static final class Data {

            private final String s;

            /* Constructors */

            private Data(String s) {
                this.s = s;
            }

            /* Methods */

            public String asString() {
                return s.trim();
            }

            public boolean asBoolean() {
                return asInt(2) == 1;
            }

            public int asInt() {
                return Integer.parseInt(s.trim());
            }

            public int asInt(int base) {
                return Integer.parseInt(s.trim(), base);
            }

            public int asUnsignedInt() {
                return Integer.parseUnsignedInt(s.trim());
            }

            public int asUnsignedInt(int base) {
                return Integer.parseUnsignedInt(s.trim(), base);
            }

            public long asLong() {
                return Long.parseLong(s.trim());
            }

            public long asLong(int base) {
                return Long.parseLong(s.trim(), base);
            }

            public long asUnsignedLong() {
                return Long.parseUnsignedLong(s.trim());
            }

            public long asUnsignedLong(int base) {
                return Long.parseUnsignedLong(s.trim(), base);
            }

            public boolean isEmpty() {
                return s.isEmpty();
            }
        }
    }

    public static final class Writer {

        private final StringBuilder sb = new StringBuilder();

        /* Constructors */

        public Writer() {}

        /* Methods */

        public static Writer create() {
            return new Writer();
        }

        public Writer write(int size, String s) {
            if (sb.length() + size > 100 - ButtonRegistry.CODE_LENGTH) {
                throw new OutOfMemoryError("Required allocation size is greater than the available one!");
            }
            sb.append(String.format("%-" + size + "." + size + "s", s));
            return this;
        }

        public Writer write(boolean b) {
            return write(1, b ? "1" : "0");
        }

        public Writer write(int size, int i) {
            return write(size, i, 10);
        }

        public Writer write(int size, int i, int base) {
            return write(size, Integer.toString(i, base));
        }

        public Writer write(int size, long l) {
            return write(size, l, 10);
        }

        public Writer write(int size, long l, int base) {
            return write(size, Long.toString(l, base));
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
