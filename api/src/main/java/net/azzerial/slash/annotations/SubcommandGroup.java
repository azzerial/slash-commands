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

package net.azzerial.slash.annotations;

import java.lang.annotation.*;

/**
 * This annotation represents a Slash Command subcommand group.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SubcommandGroup {

    /** The name of the subcommand group, must match {@code [a-z0-9-]{1,32}}.*/
    String name();
    /** The description of the subcommand group, cannot be empty or longer than {@code 100} characters.*/
    String description();
    /** The subcommand list of the subcommand group. */
    Subcommand[] subcommands() default {};
}
