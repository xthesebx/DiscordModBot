package com.seb.eventhandling.commands;

import com.seb.Server;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Setup extends BasicCommand {
    /**
     * <p>Constructor for BasicCommand.</p>
     *
     * @param event  received event to reply to it and handle the options
     * @param server the server it came from to make things easier
     */
    public Setup(SlashCommandInteractionEvent event, Server server) {
        super(event, server);
    }
}
