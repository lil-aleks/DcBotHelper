package com.bothelper.event.interaction;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface CmdOption
{
    String name();

    String description();

    OptionType type();

    boolean required() default false;
}
