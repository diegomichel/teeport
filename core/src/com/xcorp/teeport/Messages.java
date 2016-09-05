package com.xcorp.teeport;

public class Messages {
    public static void warning(Object clase, String string)
    {
        if(Settings.warnings)
        {
            System.out.println("WARNING "+ clase.getClass().getSimpleName() + ": " +string);
        }
    }

    public static void debug(Object clase, String string)
    {
        if(Settings.debugMap)
        {
            System.out.println("DEBUGMAP "+ clase.getClass().getSimpleName() + ": " +string);
        }
    }
}