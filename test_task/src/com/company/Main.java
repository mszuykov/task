package com.company;

public class Main {

    public static void main(String[] args) {
        App app = new App();
        // Programm arguments > 'm' - report for every minute, wo arguments - for every hour.
        if (args.length > 0 && args[0].equalsIgnoreCase("m"))
            app.report(false);
        else
            app.report(true);
    }
}
