package com.github.joelgodofwar.mmh.util;

//import org.bukkit.ChatColor;

public class Ansi {
    public static final String RESET = "\u001B[0m";

    public static final String BOLD = "\u001B[1m";
    public static final String NORMAL = "\u001B[2m";

    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String RAPID_BLINK = "\u001B[6m";

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";


    public static final String LIGHT_BLACK = "\u001B[90m";
    public static final String LIGHT_RED = "\u001B[91m";
    public static final String LIGHT_GREEN = "\u001B[92m";
    public static final String LIGHT_YELLOW = "\u001B[93m";
    public static final String LIGHT_BLUE = "\u001B[94m";
    public static final String LIGHT_MAGENTA = "\u001B[95m";
    public static final String LIGHT_CYAN = "\u001B[96m";
    public static final String LIGHT_WHITE = "\u001B[97m";

    public static String stripAnsi(String string) {
        string = string.replace("" + Ansi.BLACK, "").replace("" + Ansi.BLINK, "").replace("" + Ansi.BLUE, "").replace("" + Ansi.BOLD, "")
                .replace("" + Ansi.CYAN, "").replace("" + Ansi.GREEN, "").replace("" + Ansi.ITALIC, "").replace("" + Ansi.LIGHT_BLACK, "")
                .replace("" + Ansi.LIGHT_BLUE, "").replace("" + Ansi.LIGHT_CYAN, "").replace("" + Ansi.LIGHT_GREEN, "").replace("" + Ansi.LIGHT_MAGENTA, "")
                .replace("" + Ansi.LIGHT_RED, "").replace("" + Ansi.LIGHT_WHITE, "").replace("" + Ansi.LIGHT_YELLOW, "").replace("" + Ansi.MAGENTA, "")
                .replace("" + Ansi.NORMAL, "").replace("" + Ansi.RAPID_BLINK, "").replace("" + Ansi.RED, "").replace("" + Ansi.RESET, "").replace("" + Ansi.UNDERLINE, "")
                .replace("" + Ansi.WHITE, "").replace("" + Ansi.YELLOW, "");
        return string;
    }
}