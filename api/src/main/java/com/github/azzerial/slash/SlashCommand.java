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

package com.github.azzerial.slash;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class SlashCommand {

    private static final long GLOBAL = -1L;

    private final JDA jda;
    private final String tag;
    private final CommandData data;
    private final Map<String, Method> handlers;
    private final Map<Long, AtomicReference<Command>> instances = new HashMap<>();

    /* Constructors */

    public SlashCommand(JDA jda, String tag, CommandData data, Map<String, Method> handlers) {
        this.jda = jda;
        this.tag = tag;
        this.data = data;
        this.handlers = handlers;
    }

    /* Getters & Setters */

    public String getTag() {
        return tag;
    }

    /* Methods */

    public synchronized SlashCommand deleteGlobal() {
        if (instances.containsKey(GLOBAL)) {
            final AtomicReference<Command> command = instances.get(GLOBAL);

            command.get().delete().queue();
            instances.remove(GLOBAL);
        }
        return this;
    }

    public SlashCommand deleteGuild(long id) {
        return deleteGuild(jda.getGuildById(id));
    }

    public SlashCommand deleteGuild(String id) {
        return deleteGuild(jda.getGuildById(id));
    }

    public synchronized SlashCommand deleteGuild(Guild guild) {
        Checks.notNull(guild, "Guild");
        if (instances.containsKey(guild.getIdLong())) {
            final AtomicReference<Command> command = instances.get(guild.getIdLong());

            command.get().delete().queue();
            instances.remove(guild.getIdLong());
        }
        return this;
    }

    public RestAction<List<CommandPrivilege>> retrieveGlobalPrivileges(long id) {
        return retrieveGlobalPrivileges(jda.getGuildById(id));
    }

    public RestAction<List<CommandPrivilege>> retrieveGlobalPrivileges(String id) {
        return retrieveGlobalPrivileges(jda.getGuildById(id));
    }

    public synchronized RestAction<List<CommandPrivilege>> retrieveGlobalPrivileges(Guild guild) {
        Checks.notNull(guild, "Guild");
        return instances.containsKey(GLOBAL) ?
            instances.get(GLOBAL).get().retrievePrivileges(guild) :
            null;
    }

    public RestAction<List<CommandPrivilege>> retrieveGuildPrivileges(long id) {
        return retrieveGuildPrivileges(jda.getGuildById(id));
    }

    public RestAction<List<CommandPrivilege>> retrieveGuildPrivileges(String id) {
        return retrieveGuildPrivileges(jda.getGuildById(id));
    }

    public synchronized RestAction<List<CommandPrivilege>> retrieveGuildPrivileges(Guild guild) {
        Checks.notNull(guild, "Guild");
        return instances.containsKey(guild.getIdLong()) ?
            instances.get(guild.getIdLong()).get().retrievePrivileges(guild) :
            null;
    }

    public RestAction<List<CommandPrivilege>> updateGlobalPrivileges(long id, CommandPrivilege... privileges) {
        return updateGlobalPrivileges(jda.getGuildById(id), privileges);
    }

    public RestAction<List<CommandPrivilege>> updateGlobalPrivileges(String id, CommandPrivilege... privileges) {
        return updateGlobalPrivileges(jda.getGuildById(id), privileges);
    }

    public synchronized RestAction<List<CommandPrivilege>> updateGlobalPrivileges(Guild guild, CommandPrivilege... privileges) {
        Checks.notNull(guild, "Guild");
        Checks.noneNull(privileges, "CommandPrivileges");
        return instances.containsKey(GLOBAL) ?
            instances.get(GLOBAL).get().updatePrivileges(guild, privileges) :
            null;
    }

    public RestAction<List<CommandPrivilege>> updateGlobalPrivileges(long id, Collection<CommandPrivilege> privileges) {
        return updateGlobalPrivileges(jda.getGuildById(id), privileges);
    }

    public RestAction<List<CommandPrivilege>> updateGlobalPrivileges(String id, Collection<CommandPrivilege> privileges) {
        return updateGlobalPrivileges(jda.getGuildById(id), privileges);
    }

    public synchronized RestAction<List<CommandPrivilege>> updateGlobalPrivileges(Guild guild, Collection<CommandPrivilege> privileges) {
        Checks.notNull(guild, "Guild");
        Checks.noneNull(privileges, "CommandPrivileges");
        return instances.containsKey(GLOBAL) ?
            instances.get(GLOBAL).get().updatePrivileges(guild, privileges) :
            null;
    }

    public RestAction<List<CommandPrivilege>> updateGuildPrivileges(long id, CommandPrivilege... privileges) {
        return updateGuildPrivileges(jda.getGuildById(id), privileges);
    }

    public RestAction<List<CommandPrivilege>> updateGuildPrivileges(String id, CommandPrivilege... privileges) {
        return updateGuildPrivileges(jda.getGuildById(id), privileges);
    }

    public synchronized RestAction<List<CommandPrivilege>> updateGuildPrivileges(Guild guild, CommandPrivilege... privileges) {
        Checks.notNull(guild, "Guild");
        Checks.noneNull(privileges, "CommandPrivileges");
        return instances.containsKey(guild.getIdLong()) ?
            instances.get(guild.getIdLong()).get().updatePrivileges(guild, privileges) :
            null;
    }

    public RestAction<List<CommandPrivilege>> updateGuildPrivileges(long id, Collection<CommandPrivilege> privileges) {
        return updateGuildPrivileges(jda.getGuildById(id), privileges);
    }

    public RestAction<List<CommandPrivilege>> updateGuildPrivileges(String id, Collection<CommandPrivilege> privileges) {
        return updateGuildPrivileges(jda.getGuildById(id), privileges);
    }

    public synchronized RestAction<List<CommandPrivilege>> updateGuildPrivileges(Guild guild, Collection<CommandPrivilege> privileges) {
        Checks.notNull(guild, "Guild");
        Checks.noneNull(privileges, "CommandPrivileges");
        return instances.containsKey(guild.getIdLong()) ?
            instances.get(guild.getIdLong()).get().updatePrivileges(guild, privileges) :
            null;
    }

    public synchronized SlashCommand upsertGlobal() {
        jda.upsertCommand(data)
            .queue(command -> instances.put(GLOBAL, new AtomicReference<>(command)));
        return this;
    }

    public SlashCommand upsertGuild(long id) {
        return upsertGuild(jda.getGuildById(id));
    }

    public SlashCommand upsertGuild(String id) {
        return upsertGuild(jda.getGuildById(id));
    }

    public synchronized SlashCommand upsertGuild(Guild guild) {
        Checks.notNull(guild, "Guild");
        guild.upsertCommand(data)
            .queue(command -> instances.put(guild.getIdLong(), new AtomicReference<>(command)));
        return this;
    }
}
