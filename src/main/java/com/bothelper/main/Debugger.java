package com.bothelper.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Debugger
{
    private static final List<String> messageKeys = new ArrayList<>();
    private static final Map<String, Long> pingKeys = new HashMap<>();

    public static void log(String message, String key)
    {
        if (!messageKeys.contains(key))
            return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");
        String formatted = LocalDateTime.now().format(formatter);

        System.out.println("\\u001B[33m[Debugger] " + formatted + " | " + message + "\u001B[0m");
    }

    public static void activate(String key)
    {
        messageKeys.add(key);
    }

    public static void getPing(String key)
    {
        if (!pingKeys.containsKey(key))
        {
            log("Couldn´t ping with this key: " + key, "ping." + key);
            return;
        }
        long now = System.currentTimeMillis();
        long ping = now - pingKeys.get(key);
        log("Pig of " + key + ": " + ping + "ms.", "ping." + key);
        pingKeys.remove(key);
    }

    public static void calcPing(String key)
    {
        pingKeys.put(key, System.currentTimeMillis());
    }
}
