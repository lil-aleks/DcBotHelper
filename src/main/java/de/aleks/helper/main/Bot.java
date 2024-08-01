package de.aleks.helper.main;

import de.aleks.helper.event.EventHandler;
import de.aleks.helper.event.interaction.OnCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;

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

    public static void main(String[] args) throws InterruptedException
    {
        startBot("");
    }

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
                //      .setActivity(Activity.streaming(status,streamurl))  Activity optional
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
            data.add(Commands.slash(annotation.name(), annotation.description()));
        }
        bot.updateCommands().addCommands(
                data
        ).queue();
    }
}
