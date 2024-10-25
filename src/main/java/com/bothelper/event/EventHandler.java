package com.bothelper.event;

import com.bothelper.event.interaction.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventHandler extends ListenerAdapter
{

    public static Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(Scanners.MethodsAnnotated));
    public final Set<Method> commandMethods;
    public final Set<Method> buttonMethods;
    public final Set<Method> stringSelectionMethods;
    public final Set<Method> entitySelectionMethods;
    public final Set<Method> messageReceivedMethods;
    public final Set<Method> modalMethods;
    public final Set<Method> guildMethods;

    public EventHandler()
    {
        commandMethods = reflections.getMethodsAnnotatedWith(OnCommand.class);
        buttonMethods = reflections.getMethodsAnnotatedWith(OnButton.class);
        stringSelectionMethods = reflections.getMethodsAnnotatedWith(OnStringSelection.class);
        entitySelectionMethods = reflections.getMethodsAnnotatedWith(OnEntitySelection.class);
        messageReceivedMethods = reflections.getMethodsAnnotatedWith(OnMessageReceived.class);
        modalMethods = reflections.getMethodsAnnotatedWith(OnModal.class);
        guildMethods = reflections.getMethodsAnnotatedWith(OnGuild.class);
    }

    public String[] withVariables(String methodId, String interactionId)
    {
        if (!methodId.contains("{"))
            return null;
        if (methodId.replaceAll("\\{.*?}", "").equals(interactionId.replaceAll("\\{.*?}", "")))
        {
            Pattern pattern = Pattern.compile("\\{(.*?)}");
            Matcher matcher = pattern.matcher(interactionId);

            List<String> matches = new ArrayList<>();

            while (matcher.find())
            {
                matches.add(matcher.group(1));
            }

            return matches.toArray(new String[0]);
        }
        return null;
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        for (Method method : commandMethods)
        {
            OnCommand annotation = method.getAnnotation(OnCommand.class);
            if (!annotation.name().equals(event.getName()))
                continue;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
            return;

        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        for (Method method : buttonMethods)
        {
            OnButton annotation = method.getAnnotation(OnButton.class);
            String[] args = withVariables(annotation.id(), event.getButton().getId());
            if (!annotation.id().equals(event.getButton().getId()) && args == null)
                continue;
            try
            {
                if (args.length == 1)
                {
                    method.invoke(null, event, args[0]);
                } else
                {
                    method.invoke(null, event, args);
                }

            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + ", or check for the parameters.\u001B[0m", e);
            }
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event)
    {
        for (Method method : stringSelectionMethods)
        {
            OnStringSelection annotation = method.getAnnotation(OnStringSelection.class);
            String[] args = withVariables(annotation.id(), event.getComponent().getId());
            if (!annotation.id().equals(event.getComponent().getId()) && args == null)
                continue;
            try
            {
                if (annotation.option().isEmpty() || annotation.option().equals(event.getSelectedOptions().get(0).getValue()))
                {
                    if (args.length == 1)
                    {
                        method.invoke(null, event, args[0]);
                    } else
                    {
                        method.invoke(null, event, args);
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event)
    {
        for (Method method : entitySelectionMethods)
        {
            OnEntitySelection annotation = method.getAnnotation(OnEntitySelection.class);
            String[] args = withVariables(annotation.id(), event.getComponent().getId());
            if (!annotation.id().equals(event.getComponent().getId()) && args == null)
                continue;
            try
            {
                if (args.length == 1)
                {
                    method.invoke(null, event, args[0]);
                } else
                {
                    method.invoke(null, event, args);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        for (Method method : messageReceivedMethods)
        {
            OnMessageReceived annotation = method.getAnnotation(OnMessageReceived.class);

            String[] args = withVariables(annotation.message(), event.getMessage().getContentStripped());
            if (!(annotation.message().isEmpty() || annotation.message().equalsIgnoreCase(event.getMessage().getContentStripped())) && args == null)
                continue;
            try
            {
                if (args.length == 1)
                {
                    method.invoke(null, event, args[0]);
                } else
                {
                    method.invoke(null, event, args);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event)
    {
        for (Method method : modalMethods)
        {
            OnModal annotation = method.getAnnotation(OnModal.class);

            String[] args = withVariables(annotation.id(), event.getModalId());
            if (!annotation.id().equals(event.getModalId()) && args == null)
                continue;

            try
            {
                if (args.length == 1)
                {
                    method.invoke(null, event, args[0]);
                } else
                {
                    method.invoke(null, event, args);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.JOIN)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.LEAVE)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.MEMBER_JOIN)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.MEMBER_LEAVE)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.VOICE_UPDATE)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.INVITE_CREATE)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event)
    {
        for (Method method : guildMethods)
        {

            OnGuild annotation = method.getAnnotation(OnGuild.class);

            if (annotation.action() != Guild.INVITE_DELETE)
                return;

            try
            {
                method.invoke(null, event);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }
}