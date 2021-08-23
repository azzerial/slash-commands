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

package com.github.azzerial.slash.components;

import com.github.azzerial.slash.internal.ComponentRegistry;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class SlashSelectionMenu implements Component {

    private String tag;
    private String data;
    private String placeholder;
    private int minValues;
    private int maxValues;
    private boolean disabled;
    private final List<SelectOption> options;

    /* Constructors */

    public SlashSelectionMenu(String tag, String data, String placeholder, int minValues, int maxValues, boolean disabled, List<SelectOption> options) {
        this.tag = tag;
        this.data = data;
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.disabled = disabled;
        this.options = options;
    }

    /* Getters & Setters */

    @NotNull
    @Override
    public Type getType() {
        return Type.SELECTION_MENU;
    }

    @Nullable
    @Override
    public String getId() {
        return tag == null ? null : ComponentRegistry.getInstance().formatComponentId(tag, data);
    }

    public String getTag() {
        return tag;
    }

    public SlashSelectionMenu withTag(String tag) {
        Checks.notEmpty(tag, "Tag");
        this.tag = tag;
        return this;
    }

    public String getData() {
        return data;
    }

    public SlashSelectionMenu withData(String data) {
        Checks.notEmpty(data, "Data");
        Checks.notLonger(data, 100 - ComponentRegistry.CODE_LENGTH, "Data");
        this.data = data;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public int getMinValues() {
        return minValues;
    }

    public int getMaxValues() {
        return maxValues;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public SlashSelectionMenu asDisabled() {
        this.disabled = true;
        return this;
    }

    public SlashSelectionMenu asEnabled() {
        this.disabled = false;
        return this;
    }

    public SlashSelectionMenu withDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public List<SelectOption> getOptions() {
        return options;
    }

    /* Methods */

    public static SlashSelectionMenu.Builder create(String tag) {
        return create(tag, null);
    }

    public static SlashSelectionMenu.Builder create(String tag, String data) {
        return new SlashSelectionMenu.Builder(tag, data);
    }

    public SlashSelectionMenu.Builder createCopy() {
        final SlashSelectionMenu.Builder builder = create(getId());

        builder.setRequiredRange(getMinValues(), getMaxValues());
        builder.setPlaceholder(getPlaceholder());
        builder.addOptions(getOptions());
        builder.setDisabled(isDisabled());
        return builder;
    }

    @NotNull
    @Override
    public DataObject toData() {
        final DataObject json = DataObject.empty();

        json.put("type", 3);
        json.put("custom_id", getId());
        json.put("min_values", minValues);
        json.put("max_values", maxValues);
        json.put("disabled", disabled);
        json.put("options", DataArray.fromCollection(options));
        if (placeholder != null) {
            json.put("placeholder", placeholder);
        }
        return json;
    }

    /* Nested Classes */

    public static final class Builder {

        private String tag;
        private String data;
        private String placeholder;
        private int minValues = 1;
        private int maxValues = 1;
        private boolean disabled = false;
        private final List<SelectOption> options = new ArrayList<>();

        /* Constructors */

        private Builder(String tag, String data) {
            setTag(tag);
            if (data != null) {
                setData(data);
            }
        }

        /* Getters & Setters */

        public String getTag() {
            return tag;
        }

        public SlashSelectionMenu.Builder setTag(String tag) {
            Checks.notEmpty(tag, "Tag");
            this.tag = tag;
            return this;
        }

        public String getData() {
            return data;
        }

        public SlashSelectionMenu.Builder setData(String data) {
            Checks.notEmpty(data, "Data");
            Checks.notLonger(data, 100 - ComponentRegistry.CODE_LENGTH, "Data");
            this.data = data;
            return this;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public SlashSelectionMenu.Builder setPlaceholder(String placeholder) {
            if (placeholder != null) {
                Checks.notEmpty(placeholder, "Placeholder");
                Checks.notLonger(placeholder, 100, "Placeholder");
            }
            this.placeholder = placeholder;
            return this;
        }

        public int getMinValues() {
            return minValues;
        }

        public SlashSelectionMenu.Builder setMinValues(int minValues) {
            Checks.notNegative(minValues, "Min Values");
            Checks.check(minValues <= 25, "Min Values may not be greater than 25! Provided: %d", minValues);
            this.minValues = minValues;
            return this;
        }

        public int getMaxValues() {
            return maxValues;
        }

        public SlashSelectionMenu.Builder setMaxValues(int maxValues) {
            Checks.positive(maxValues, "Max Values");
            Checks.check(maxValues <= 25, "Min Values may not be greater than 25! Provided: %d", maxValues);
            this.maxValues = maxValues;
            return this;
        }

        public SlashSelectionMenu.Builder setRequiredRange(int min, int max) {
            Checks.check(min <= max, "Min Values should be less than or equal to Max Values! Provided: [%d, %d]", min, max);
            return setMinValues(min).setMaxValues(max);
        }

        public boolean isDisabled() {
            return disabled;
        }

        public SlashSelectionMenu.Builder setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public List<SelectOption> getOptions() {
            return options;
        }

        public SlashSelectionMenu.Builder addOptions(SelectOption... options) {
            Checks.noneNull(options, "Options");
            Checks.check(this.options.size() + options.length <= 25, "Cannot have more than 25 options for a selection menu!");
            Collections.addAll(this.options, options);
            return this;
        }

        public SlashSelectionMenu.Builder addOptions(Collection<? extends SelectOption> options) {
            Checks.noneNull(options, "Options");
            Checks.check(this.options.size() + options.size() <= 25, "Cannot have more than 25 options for a selection menu!");
            this.options.addAll(options);
            return this;
        }

        public SlashSelectionMenu.Builder addOption(String label, String value) {
            return addOption(label, value, false);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, boolean isDefault) {
            return addOption(label, value, null, null, isDefault);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, String description) {
            return addOption(label, value, description, false);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, String description, boolean isDefault) {
            return addOption(label, value, description, null, isDefault);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, Emoji emoji) {
            return addOption(label, value, emoji, false);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, Emoji emoji, boolean isDefault) {
            return addOption(label, value, null, emoji, isDefault);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, String description, Emoji emoji) {
            return addOption(label, value, description, emoji, false);
        }

        public SlashSelectionMenu.Builder addOption(String label, String value, String description, Emoji emoji, boolean isDefault) {
            return addOptions(
                SelectOption.of(label, value)
                    .withDescription(description)
                    .withEmoji(emoji)
                    .withDefault(isDefault)
            );
        }

        public SlashSelectionMenu.Builder setDefaultValues(Collection<String> values) {
            Checks.noneNull(values, "Values");
            final Set<String> set = new HashSet<>(values);

            for (ListIterator<SelectOption> it = getOptions ().listIterator(); it.hasNext();) {
                final SelectOption option = it.next();
                it.set(option.withDefault(set.contains(option.getValue())));
            }
            return this;
        }

        public SlashSelectionMenu.Builder setDefaultOptions(Collection<? extends SelectOption> values) {
            Checks.noneNull(values, "Values");
            return setDefaultValues(values.stream().map(SelectOption::getValue).collect(Collectors.toSet()));
        }

        /* Methods */

        public SlashSelectionMenu build() {
            Checks.check(minValues <= maxValues, "Min values cannot be greater than max values!");
            Checks.check(options.size() <= 25, "Cannot build a selection menu with more than 25 options.");
            int min = Math.min(minValues, options.size());
            int max = Math.min(maxValues, options.size());
            return new SlashSelectionMenu(tag, data, placeholder, min, max, disabled, options);
        }
    }
}
