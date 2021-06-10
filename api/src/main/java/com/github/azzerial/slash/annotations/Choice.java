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
 * This annotation represents a choice of a Slash Command option.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Choice {

    /** The name of the choice, cannot be empty or longer than {@code 100} characters.*/
    String name();
    /** The value of the choice, either an {@code int} or a {@code String}. In the case the value is a {@code String}, it cannot be empty or longer than {@code 100} characters.*/
    String value();
}
