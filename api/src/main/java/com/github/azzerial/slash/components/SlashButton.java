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

import com.github.azzerial.slash.internal.ButtonRegistry;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SlashButton implements Component {

    private String tag;
    private String data;
    private String label;
    private ButtonStyle style;
    private String url;
    private boolean disabled;
    private Emoji emoji;

    /* Constructors */

    SlashButton(String tag, String data, String label, ButtonStyle style, boolean disabled, Emoji emoji) {
        this(tag, data, label, style, null, disabled, emoji);
    }

    SlashButton(String tag, String data, String label, ButtonStyle style, String url, boolean disabled, Emoji emoji) {
        this.tag = tag;
        this.data = data;
        this.label = label;
        this.style = style;
        this.url = url;
        this.disabled = disabled;
        this.emoji = emoji;
    }

    /* Getters & Setters */

    @NotNull
    @Override
    public Type getType() {
        return Type.BUTTON;
    }

    @Nullable
    @Override
    public String getId() {
        return tag == null ? null : ButtonRegistry.getInstance().createButtonId(tag, data);
    }

    public String getTag() {
        return tag;
    }

    public SlashButton withTag(String tag) {
        Checks.notEmpty(tag, "Tag");
        this.tag = tag;
        return this;
    }

    public String getData() {
        return data;
    }

    public SlashButton withData(String data) {
        Checks.notEmpty(data, "Data");
        Checks.notLonger(data, 100 - ButtonRegistry.CODE_LENGTH, "Data");
        this.data = data;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public SlashButton withLabel(String label) {
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, 80, "Label");
        this.label = label;
        return this;
    }

    public ButtonStyle getStyle() {
        return style;
    }

    public String getUrl() {
        return url;
    }

    public SlashButton withUrl(String url) {
        Checks.notEmpty(url, "Url");
        Checks.notLonger(url, 512, "Url");
        this.url = url;
        this.style = ButtonStyle.LINK;
        return this;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public SlashButton withEmoji(Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public SlashButton asDisabled() {
        this.disabled = true;
        return this;
    }

    public SlashButton asEnabled() {
        this.disabled = false;
        return this;
    }

    public SlashButton withDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    /* Methods */

    public static SlashButton primary(String tag, String label) {
        Checks.notEmpty(tag, "Tag");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, 80, "Label");
        return new SlashButton(tag, null, label, ButtonStyle.PRIMARY, false, null);
    }

    public static SlashButton primary(String tag, Emoji emoji) {
        Checks.notEmpty(tag, "Tag");
        Checks.notNull(emoji, "Emoji");
        return new SlashButton(tag, null, "", ButtonStyle.PRIMARY, false, emoji);
    }

    public static SlashButton secondary(String tag, String label) {
        Checks.notEmpty(tag, "Tag");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, 80, "Label");
        return new SlashButton(tag, null, label, ButtonStyle.SECONDARY, false, null);
    }

    public static SlashButton secondary(String tag, Emoji emoji) {
        Checks.notEmpty(tag, "Tag");
        Checks.notNull(emoji, "Emoji");
        return new SlashButton(tag, null, "", ButtonStyle.SECONDARY, false, emoji);
    }

    public static SlashButton success(String tag, String label) {
        Checks.notEmpty(tag, "Tag");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, 80, "Label");
        return new SlashButton(tag, null, label, ButtonStyle.SUCCESS, false, null);
    }

    public static SlashButton success(String tag, Emoji emoji) {
        Checks.notEmpty(tag, "Tag");
        Checks.notNull(emoji, "Emoji");
        return new SlashButton(tag, null, "", ButtonStyle.SUCCESS, false, emoji);
    }

    public static SlashButton danger(String tag, String label) {
        Checks.notEmpty(tag, "Tag");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, 80, "Label");
        return new SlashButton(tag, null, label, ButtonStyle.DANGER, false, null);
    }

    public static SlashButton danger(String tag, Emoji emoji) {
        Checks.notEmpty(tag, "Tag");
        Checks.notNull(emoji, "Emoji");
        return new SlashButton(tag, null, "", ButtonStyle.DANGER, false, emoji);
    }

    public static SlashButton link(String url, String label) {
        Checks.notEmpty(url, "Url");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(url, 512, "Url");
        Checks.notLonger(label, 80, "Label");
        return new SlashButton(null, null, label, ButtonStyle.LINK, url, false, null);
    }

    public static SlashButton link(String url, Emoji emoji) {
        Checks.notEmpty(url, "Url");
        Checks.notLonger(url, 512, "Url");
        Checks.notNull(emoji, "Emoji");
        return new SlashButton(null, null, "", ButtonStyle.LINK, url, false, null);
    }

    public static SlashButton of(ButtonStyle style, String tagOrUrl, String label) {
        Checks.check(style != ButtonStyle.UNKNOWN, "The button style cannot be UNKNOWN!");
        Checks.notNull(style, "Style");
        if (style == ButtonStyle.LINK) {
            return link(tagOrUrl, label);
        }
        Checks.notNull(label, "Label");
        Checks.notLonger(label, 80, "Label");
        Checks.notEmpty(tagOrUrl, "Tag");
        return new SlashButton(tagOrUrl, null, label, style, false, null);
    }

    public static SlashButton of(ButtonStyle style, String tagOrUrl, Emoji emoji) {
        Checks.check(style != ButtonStyle.UNKNOWN, "The button style cannot be UNKNOWN!");
        Checks.notNull(style, "Style");
        if (style == ButtonStyle.LINK) {
            return link(tagOrUrl, emoji);
        }
        Checks.notNull(emoji, "Emoji");
        Checks.notEmpty(tagOrUrl, "Tag");
        return new SlashButton(tagOrUrl, null, "", style, false, emoji);
    }

    public static SlashButton of(ButtonStyle style, String tagOrUrl, String label, Emoji emoji) {
        if (label != null) {
            return of(style, tagOrUrl, label).withEmoji(emoji);
        } else if (emoji != null) {
            return of(style, tagOrUrl, emoji);
        }
        throw new IllegalArgumentException("Cannot build a button without a label and emoji. At least one has to be provided as non-null.");
    }

    @NotNull
    @Override
    public DataObject toData() {
        final DataObject json = DataObject.empty();

        json.put("type", 2);
        json.put("label", label);
        json.put("style", style.getKey());
        json.put("disabled", disabled);
        if (emoji != null) {
            json.put("emoji", emoji);
        }
        if (url != null) {
            json.put("url", url);
        } else {
            json.put("custom_id", getId());
        }
        return json;
    }
}
