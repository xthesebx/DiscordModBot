package com.seb.eventhandling.commands;

import com.hawolt.logger.Logger;
import com.seb.Server;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colour extends BasicCommand {
    private static boolean running = false;
    /**
     * <p>Constructor for BasicCommand.</p>
     *
     * @param event  received event to reply to it and handle the options
     * @param server the server it came from to make things easier
     */
    public Colour(SlashCommandInteractionEvent event, Server server) {
        super(event, server);
        event.deferReply().queue();
        Random rn = new Random();
        try {
            while (running) Thread.sleep(rn.nextInt(20));
        } catch (InterruptedException ignored) {
        }
        running = true;
        Guild guild = event.getGuild();
        String s = event.getOption("colour").getAsString();
        if (s.startsWith("#")) {
            s = s.substring(1);
        }
        Pattern pattern = Pattern.compile("[0-9a-fA-F]{6}");
        Matcher matcher = pattern.matcher(s);
        Role role;
        Role removerole = null;
        boolean deleterole = false;
        if (matcher.matches()) {
            for (Role tempRole : event.getMember().getRoles()) {
                if (pattern.matcher(tempRole.getName()).matches()) {
                    removerole = tempRole;
                }
            }
            role = guild.createRole().setName(s).setColor(Integer.parseInt(s, 16)).complete();
            if (removerole != null) {
                List<Role> rolegivelist = new ArrayList<>();
                rolegivelist.add(role);
                List<Role> rolegtakelist = new ArrayList<>();
                rolegtakelist.add(removerole);
                guild.modifyMemberRoles(event.getMember(), rolegivelist, rolegtakelist).complete();
            } else guild.addRoleToMember(event.getMember(), role).complete();
            if (!event.getMember().getRoles().isEmpty()) {
                /**
                 * Fuck Discord, somehow it just doesn't work 1/8 times or something for no reason so bandaid:
                 * Repeat command in few seconds.
                 * Just waiting in thread doesn't work cause it keeps the caches and they are not deletable.
                 * TODO: test, maybe unneeded catch block and fixed, not sure tho
                 */
                try {
                    guild.modifyRolePositions(true).selectPosition(role.getPosition()).moveAbove(guild.retrieveMember(event.getUser()).complete().getRoles().get(0)).complete();
                } catch (IllegalStateException e) {
                    Logger.error(e);
                    role.delete().complete();
                    event.getHook().editOriginal("just try again in a few secs, discord is an asshole i think").queue();
                }
            }
            if (guild.getMembersWithRoles(role).isEmpty()) deleterole = true;
            if (removerole != null && deleterole) removerole.delete().queue();
            event.getHook().editOriginal("gave you the role").queue();
        } else {
            event.getHook().editOriginal("please use a valid hex rgb colour code").queue();
        }
        running = false;
    }
}
