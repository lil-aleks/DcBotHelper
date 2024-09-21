package com.bothelper.main;

import com.bothelper.event.interaction.CmdOption;
import com.bothelper.event.interaction.OnCommand;
import com.bothelper.event.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Bot
{

    public static boolean isRunning()
    {
        return bot != null;
    }

    public static JDA bot;
    public static String token;

    public static void startBot(String token) throws InterruptedException
    {
        Bot.token = token;
        if (isRunning())
        {
            System.out.println("\u001B[31mThe Bot was tried to start but is already running.\u001B[0m");
            return;
        }

        bot = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE) // optional
                .setActivity(Activity.playing("nothing")) //optional
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.DIRECT_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_INVITES
                )
                .addEventListeners(
                        new EventHandler()
                )
                .build().awaitReady();

        List<SlashCommandData> data = new ArrayList<>();
        Set<Method> annotatedMethods = EventHandler.reflections.getMethodsAnnotatedWith(OnCommand.class);
        for (Method method : annotatedMethods)
        {
            OnCommand annotation = method.getAnnotation(OnCommand.class);
            if (annotation.options().length == 0)
            {
                data.add(Commands.slash(annotation.name(), annotation.description()));
            } else
            {
                List<OptionData> options = new ArrayList<>();
                for (CmdOption option : annotation.options())
                {
                    options.add(new OptionData(option.type(),option.name(),option.description(),option.required()));
                }
                data.add(Commands.slash(annotation.name(), annotation.description()).addOptions(options));
            }

        }
        bot.updateCommands().addCommands(
                data
        ).queue();
    }
}
