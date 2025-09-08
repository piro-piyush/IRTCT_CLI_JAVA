package org.example.ticket.utils;

public class ColorUtils {

    // Reset
    public static final String RESET = "\u001B[0m";

    // Regular Colors
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m"; // Pink/Purple
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Bold
    public static final String BOLD_BLACK   = "\u001B[1;30m";
    public static final String BOLD_RED     = "\u001B[1;31m";
    public static final String BOLD_GREEN   = "\u001B[1;32m";
    public static final String BOLD_YELLOW  = "\u001B[1;33m";
    public static final String BOLD_BLUE    = "\u001B[1;34m";
    public static final String BOLD_MAGENTA = "\u001B[1;35m";
    public static final String BOLD_CYAN    = "\u001B[1;36m";
    public static final String BOLD_WHITE   = "\u001B[1;37m";

    // Utility method for coloring text
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
}
