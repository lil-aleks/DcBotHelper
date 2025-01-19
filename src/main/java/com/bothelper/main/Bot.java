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
import java.util.Arrays;
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

    public static void startBot(String token, Activity activity) throws InterruptedException
    {
        runBot(token, OnlineStatus.ONLINE, activity, ChunkingFilter.ALL, MemberCachePolicy.ALL,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_INVITES);
    }

    public static void startBot(String token, OnlineStatus status, Activity activity, ChunkingFilter filter, MemberCachePolicy memberCachePolicy, GatewayIntent... gatewayIntents) throws InterruptedException
    {
        runBot(token, status, activity, filter, memberCachePolicy, gatewayIntents);
    }

    private static void runBot(String token, OnlineStatus status, Activity activity, ChunkingFilter filter, MemberCachePolicy memberCachePolicy, GatewayIntent... gatewayIntents) throws InterruptedException
    {
        Bot.token = token;
        if (isRunning())
        {
            System.out.println("\u001B[31mThe Bot was tried to start but is already running.\u001B[0m");
            return;
        }

        bot = JDABuilder.createDefault(token)
                .setStatus(status) // optional
                .setActivity(activity) //optional
                .setChunkingFilter(filter)
                .setMemberCachePolicy(memberCachePolicy)
                .enableIntents(
                        Arrays.asList(gatewayIntents)
                )
                .addEventListeners(
                        new EventHandler()
                )
                .build().awaitReady();
    }
}
