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

package com.github.azzerial.slash.playground;

import com.github.azzerial.slash.SlashClient;
import com.github.azzerial.slash.SlashClientBuilder;
import com.github.azzerial.slash.playground.commands.PingCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public final class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    /* Methods */

    public static void main(String[] args) {
        // get the .env config variables
        final String token = System.getenv("token");
        final String guildId = System.getenv("guild_id");

        try {
            // create the JDA instance
            final JDA jda = JDABuilder
                .createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .build()
                .awaitReady();
            // create the SlashClient instance
            final SlashClient slash = SlashClientBuilder
                .create(jda)
                .addCommand(new PingCommand()) // register the ping command
                .build();

            // add the command to a guild if not already added
            slash.getCommand("ping").upsertGuild(guildId);
        } catch (LoginException e) {
            logger.error("The bot token was invalid!");
        } catch (InterruptedException e) {
            logger.error("Could not connect to Discord!");
        }
    }
}
