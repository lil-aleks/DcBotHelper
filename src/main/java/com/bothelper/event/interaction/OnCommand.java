package com.bothelper.event.interaction;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnCommand
{

    String name();

    String description();

    String[] allowedRoles() default {};

    Permission[] allowedPermissions() default {};

    CmdOption[] options() default {};
}
