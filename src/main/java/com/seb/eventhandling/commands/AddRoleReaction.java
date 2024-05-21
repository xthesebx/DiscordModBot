package com.seb.eventhandling.commands;

import com.seb.Main;
import com.seb.Server;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONObject;

import java.io.File;

public class AddRoleReaction extends BasicCommand {
    /**
     * <p>Constructor for BasicCommand.</p>
     *
     * @param event  received event to reply to it and handle the options
     * @param server the server it came from to make things easier
     */
    public AddRoleReaction(SlashCommandInteractionEvent event, Server server) {
        super(event, server);
        OptionMapping optionChannelId = event.getOption("channelid");
        OptionMapping optionReactionName = event.getOption("name");
        OptionMapping role = event.getOption("role");
        OptionMapping emote = event.getOption("emote");
        String channelid = optionChannelId.getAsString();
        File f = new File("saves/" + server.getGuildId() + "/" + channelid + ".json");
        JSONObject object = new JSONObject(Main.read(f));
        object.getJSONObject(optionReactionName.getAsString()).getJSONObject("reactions").put(emote.getAsString(), role.getAsRole().getId());
        String messageid = object.getJSONObject(optionReactionName.getAsString()).getString("messageId");
        server.getGuild().getJDA().getTextChannelById(optionChannelId.getAsString()).retrieveMessageById(messageid).queue((message -> {
            message.editMessage(message.getContentRaw() + "\n" + emote.getAsString() + " for " + role.getAsRole().getName()).queue();
            message.addReaction(Emoji.fromFormatted(emote.getAsString())).queue();
        }));
        Main.write(object.toString(), f);
        event.reply("ok").queue();
    }
}
