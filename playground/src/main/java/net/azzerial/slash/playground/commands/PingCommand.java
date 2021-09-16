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

package net.azzerial.slash.playground.commands;

import net.azzerial.slash.annotations.Slash;
import net.azzerial.slash.components.SlashButton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("ping")
@Slash.Command(
    name = "ping",
    description = "Check the current latency"
)
public final class PingCommand {

    private final MessageEmbed initialMessage = buildPingMessage("...");

    /* Methods */

    @Slash.Handler
    public void ping(SlashCommandEvent event) {
        final long time = System.currentTimeMillis();

        event.deferReply(true)
            .addEmbeds(initialMessage)
            .addActionRow(
                SlashButton.primary("ping.refresh", "Refresh")
            )
            .flatMap(v -> {
                final long latency = System.currentTimeMillis() - time;
                final String ms = Long.toUnsignedString(latency);
                final MessageEmbed message = buildPingMessage(ms);
                return event.getHook().editOriginalEmbeds(message);
            })
            .queue();
    }

    @Slash.Button("ping.refresh")
    public void onRefresh(ButtonClickEvent event) {
        final long time = System.currentTimeMillis();

        event.deferEdit()
            .flatMap(v -> {
                final long latency = System.currentTimeMillis() - time;
                final String ms = Long.toUnsignedString(latency);
                final MessageEmbed message = buildPingMessage(ms);
                return event.getHook().editOriginalEmbeds(message);
            })
            .queue();
    }

    /* Internal */

    private MessageEmbed buildPingMessage(String ms) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setDescription("**Ping:** `" + ms + "`ms");
        return builder.build();
    }
}
