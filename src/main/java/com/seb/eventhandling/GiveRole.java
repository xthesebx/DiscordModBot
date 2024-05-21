package com.seb.eventhandling;

import com.seb.Main;
import com.seb.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.json.JSONObject;

import java.io.File;

public class GiveRole {

    JDA jda;

    public GiveRole(MessageReactionAddEvent event, Server server) {
        jda = server.getGuild().getJDA();
        File f = new File("saves/" + server.getGuildId() + "/" + event.getChannel().getId() + ".json");
        JSONObject object = new JSONObject(Main.read(f));
        String correctKey = "";
        for (String key : object.keySet()) {
            if (object.getJSONObject(key).getString("messageId").equals(event.getMessageId())) {
                correctKey = key;
                break;
            }
        }
        JSONObject reactions = object.getJSONObject(correctKey).getJSONObject("reactions");
        if (reactions.has(event.getEmoji().getFormatted())) {
            server.getGuild().addRoleToMember(event.getUser(), server.getGuild().getRoleById(reactions.getString(event.getEmoji().getFormatted()))).queue();
        }
    }
}
