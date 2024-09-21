package com.bothelper.event;

import com.bothelper.event.interaction.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.*;

import java.lang.reflect.*;
import java.util.List;
import java.util.Set;

public class EventHandler extends ListenerAdapter
{

    public static Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath()) // Gesamter Classpath
            .setScanners(Scanners.MethodsAnnotated));
    public final Set<Method> commandMethods;
    public final Set<Method> buttonMethods;
    public final Set<Method> stringSelectionMethods;
    public final Set<Method> entitySelectionMethods;
    public final Set<Method> messageReceivedMethods;

    public EventHandler()
    {
        commandMethods = reflections.getMethodsAnnotatedWith(OnCommand.class);
        buttonMethods = reflections.getMethodsAnnotatedWith(OnButton.class);
        stringSelectionMethods = reflections.getMethodsAnnotatedWith(OnStringSelection.class);
        entitySelectionMethods = reflections.getMethodsAnnotatedWith(OnEntitySelection.class);
        messageReceivedMethods = reflections.getMethodsAnnotatedWith(OnMessageReceived.class);
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
            if (!annotation.id().equals(event.getButton().getId()))
                continue;

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
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event)
    {
        for (Method method : stringSelectionMethods)
        {
            OnStringSelection annotation = method.getAnnotation(OnStringSelection.class);
            if (!annotation.id().equals(event.getComponent().getId()))
                continue;
            try
            {
                if (annotation.option().isEmpty() || annotation.option().equals(event.getSelectedOptions().get(0).getValue()))
                {
                    method.invoke(null, event);
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
            if (!annotation.id().equals(event.getComponent().getId()))
                continue;
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
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        for (Method method : messageReceivedMethods)
        {
            OnMessageReceived annotation = method.getAnnotation(OnMessageReceived.class);

            try
            {
                if (annotation.message().equals("") || annotation.message().equalsIgnoreCase(event.getMessage().getContentStripped()))
                {
                    method.invoke(null, event);
                }

            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("\u001B[31mPlease make this method static. " + method.getName() + "() in " + method.getDeclaringClass() + "\u001B[0m", e);
            }
        }
    }
}