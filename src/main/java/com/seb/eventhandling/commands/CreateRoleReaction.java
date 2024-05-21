package com.seb.eventhandling.commands;

import com.seb.Main;
import com.seb.Server;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONObject;

import java.io.File;

public class CreateRoleReaction extends BasicCommand {
    /**
     * <p>Constructor for BasicCommand.</p>
     *
     * @param event  received event to reply to it and handle the options
     * @param server the server it came from to make things easier
     */
    public CreateRoleReaction(SlashCommandInteractionEvent event, Server server) {
        super(event, server);
        OptionMapping optionChannelId = event.getOption("channelid");
        OptionMapping optionReactionName = event.getOption("name");
        String channelid = optionChannelId.getAsString();
        File f = new File("saves/" + server.getGuildId() + "/" + channelid + ".json");
        if (!f.exists()) Main.write("{}", f);
        JSONObject textBefore = new JSONObject(Main.read(f));
        String messageid = server.getGuild().getJDA().getTextChannelById(channelid).sendMessage("React with the role you want to receive").complete().getId();
        event.reply("ok").queue();
        JSONObject messageObject = new JSONObject();
        messageObject.put("messageId", messageid);
        messageObject.put("reactions", new JSONObject());
        textBefore.put(optionReactionName.getAsString(), messageObject);
        Main.write(textBefore.toString(), f);
    }
}
