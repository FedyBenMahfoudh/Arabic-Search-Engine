package org.example.arabicsearchengine.cli;

import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;

/**
 * Utility class for formatting CLI output.
 * Handles Arabic text display and console styling.
 */
public class OutputFormatter {
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════";
    private static final String THIN_SEP = "───────────────────────────────────────────────────────";

    // ANSI color codes (may not work on all terminals)
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    private final boolean useColors;

    public OutputFormatter() {
        // Check if terminal supports colors
        this.useColors = System.console() != null &&
                !System.getProperty("os.name").toLowerCase().contains("win");
    }

    public OutputFormatter(boolean useColors) {
        this.useColors = useColors;
    }

    public void printWelcome() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("    محرك البحث الصرفي العربي");
        System.out.println("    Arabic Morphological Search Engine");
        System.out.println(SEPARATOR);
        System.out.println("    نظام تحليل الجذور والأوزان العربية");
        System.out.println("    Root-Pattern Analysis System");
        System.out.println(SEPARATOR);
        System.out.println();
    }

    public void printGoodbye() {
        System.out.println();
        System.out.println(THIN_SEP);
        System.out.println("    مع السلامة! - Goodbye!");
        System.out.println(THIN_SEP);
    }

    public void printHeader(String text) {
        System.out.println();
        if (useColors) {
            System.out.println(CYAN + BOLD + "╔══ " + text + " ══╗" + RESET);
        } else {
            System.out.println("╔══ " + text + " ══╗");
        }
    }

    public void printSeparator() {
        System.out.println(THIN_SEP);
    }

    public void printSuccess(String message) {
        if (useColors) {
            System.out.println(GREEN + "✓ " + message + RESET);
        } else {
            System.out.println("[✓] " + message);
        }
    }

    public void printError(String message) {
        if (useColors) {
            System.out.println(RED + "✗ " + message + RESET);
        } else {
            System.out.println("[✗] " + message);
        }
    }

    public void printWarning(String message) {
        if (useColors) {
            System.out.println(YELLOW + "⚠ " + message + RESET);
        } else {
            System.out.println("[!] " + message);
        }
    }

    public void printRoot(Root root) {
        System.out.println("  ┌─────────────────────────────");
        System.out.println("  │ الجذر (Root): " + root.getRootLetters());
        System.out.println("  │ R1: " + root.getR1() + ", R2: " + root.getR2() + ", R3: " + root.getR3());
        System.out.println("  │ التكرار (Frequency): " + root.getFrequency());
        System.out.println("  │ المشتقات (Derivatives): " + root.getDerivedWords().size());
        System.out.println("  └─────────────────────────────");
    }

    public void printPattern(Pattern pattern) {
        System.out.println("  ┌─────────────────────────────");
        System.out.println("  │ الوزن (Pattern): " + pattern.getPatternId());
        System.out.println("  │ البنية (Structure): " + pattern.getStructure());
        System.out.println("  │ الوصف (Description): " + pattern.getDescription());
        System.out.println("  └─────────────────────────────");
    }

    /**Formats Arabic text for RTL display.*/
    public String formatArabic(String text) {
        // Add RTL mark for proper display
        return "\u200F" + text;
    }


    public void printTableRow(String col1, String col2) {
        System.out.printf("  │ %-15s │ %-20s │%n", col1, col2);
    }

}
