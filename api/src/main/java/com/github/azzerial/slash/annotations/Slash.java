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

package com.github.azzerial.slash.annotations;

import java.lang.annotation.*;

/**
 * The annotation holding the main annotations of the Slash Commands library.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Slash {

    /**
     * This annotation labels a class as a Slash Command.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Command {

        /** The name of the Slash Command, must match {@code [a-z0-9-]{1,32}}.*/
        String name();
        /** The description of the Slash Command, cannot be empty or longer than {@code 100} characters.*/
        String description();
        /** The option list of the Slash Command. */
        Option[] options() default {};
        /** The subcommand list of the Slash Command. */
        Subcommand[] subcommands() default {};
        /** The subcommand group list of the Slash Command. */
        SubcommandGroup[] subcommandGroups() default {};
        /** The default permission of the Slash Command, whether the command is enabled by default when the app is added to a guild. */
        boolean enabled() default true;
    }

    /**
     * This annotation labels a method as a Slash Command button handler.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Button {

        /** The tag of the button. */
        String value();
    }

    /**
     * This annotation labels a method as a Slash Command handler.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Handler {

        /**
         * The path to the handler.
         */
        String value() default "";
    }

    /**
     * This annotation labels a method as a Slash Command selection menu handler.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface SelectionMenu {

        /** The tag of the selection menu. */
        String value();
    }

    /**
     * This annotations assigns an identification tag to a Slash Command for registration purposes.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Tag {

        /**
         * The value of the tag.
         */
        String value();
    }
}
