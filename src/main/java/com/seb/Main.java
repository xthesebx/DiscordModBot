package com.seb;

import com.hawolt.logger.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;

public class Main extends ListenerAdapter {

    private final HashMap<String, Server> map = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        new Main();
    }

    public Main() throws InterruptedException {
        //
        // read(new File("saves/" + "465880611907698688" + "/" + "1232779790503641218" + ".json"));

        File env = new File("apikey.env");
        JDA jda = JDABuilder.createDefault(read(env).strip()).enableIntents(GatewayIntent.MESSAGE_CONTENT).enableIntents(GatewayIntent.GUILD_MESSAGES).enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING).setStatus(OnlineStatus.OFFLINE).setMemberCachePolicy(MemberCachePolicy.NONE).build();
        jda.addEventListener(this);
        jda.awaitReady();
        if (!new File("saves").exists()) new File("saves").mkdir();
        for (Guild guild : jda.getGuilds()) {
            map.put(guild.getId(), new Server(guild));
            if (!new File("saves/" + guild.getId()).exists()) new File("saves/" + guild.getId()).mkdir();
        }
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.customStatus("Giving and Taking Roles"));
        //jda.getTextChannelById("1178730393478889512").sendMessage("<:P_:1233867607279665172>").queue();
    }

    /**
     * reads text of a file
     *
     * @param file the file to read from
     * @return String builder with text from the File
     */
    public static String read (File file) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                writer.write("");
                writer.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            String temp;
            while (true) {
                temp = reader.readLine();
                if (temp == null) break;
                text.append(temp);
            }
            reader.close();
        } catch (IOException ignored) {
        }
        return text.toString();
    }

    /**
     * write text to a file
     *
     * @param text text to write
     * @param file file to write the text to
     */
    public static void write(String text, File file) {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            writer.write(text);
            writer.close();
        } catch (IOException ignored) {

        }
    }

    /**
     * {@inheritDoc}
     *
     * giving the command to corresponding server object
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        assert event.getGuild() != null;
        map.get(event.getGuild().getId()).onSlashCommandInteraction(event);
    }

    /**
     * {@inheritDoc}
     *
     * giving the buttoninteraction to the corresponding server object
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        assert event.getGuild() != null;
        map.get(event.getGuild().getId()).onButtonInteraction(event);
    }

    /**
     * {@inheritDoc}
     *
     * adding new server object to map to add new guilds
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);
        map.put(event.getGuild().getId(), new Server(event.getGuild()));
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getUser().isBot()) return;
        map.get(event.getGuild().getId()).onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);
        if (event.getUser().isBot()) return;
        map.get(event.getGuild().getId()).onMessageReactionRemove(event);
    }
}