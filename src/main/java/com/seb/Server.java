package com.seb;

import com.seb.eventhandling.GiveRole;
import com.seb.eventhandling.TakeRole;
import com.seb.eventhandling.commands.AddRoleReaction;
import com.seb.eventhandling.commands.Colour;
import com.seb.eventhandling.commands.CreateRoleReaction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class Server {

    final Guild guild;
    final String guildId;

    public Server (Guild guild) {
        this.guild = guild;
        this.guildId = guild.getId();
    }

    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "createrolereaction" -> new CreateRoleReaction(event, this);
            case "addrolereaction" -> new AddRoleReaction(event, this);
            case "colour" -> new Colour(event, this);
        }
    }

    public void onButtonInteraction (ButtonInteractionEvent event) {

    }

    public void onMessageReactionAdd (MessageReactionAddEvent event) {
        new GiveRole(event, this);
    }

    public void onMessageReactionRemove (MessageReactionRemoveEvent event) {
        new TakeRole(event, this);
    }

    public String getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return guild;
    }
}
