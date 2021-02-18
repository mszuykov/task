package com.company;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final String logsPath = "E:\\tmp";
    private static final String outFileName = "statistics.txt";

    public void report(Boolean hours) {
        TreeMap<String, Integer> m = createTreeMapFromList(findErrors(), hours);
        Path outFile = Paths.get(outFileName);
        try (BufferedWriter writer =
                     Files.newBufferedWriter(outFile, StandardCharsets.UTF_8,
                             StandardOpenOption.CREATE)) {
            for (Map.Entry<String, Integer> e : m.entrySet()) {
//                System.out.println(e.getKey() + " Количество ошибок: " + e.getValue());
                writer.write(e.getKey() + " Количество ошибок: " + e.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    private String parseLine(String line, Boolean hours) {
        String result = null;
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = format.parse(line.split(";")[0]);
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (hours) {
                result = year + "-" +
                        formatMonthsDaysHoursMinutesEtc(month) + "-" +
                        formatMonthsDaysHoursMinutesEtc(day) + ", " +
                        formatMonthsDaysHoursMinutesEtc(hour) + ".00" + "-" +
                        formatMonthsDaysHoursMinutesEtc(hour + 1) + ".00"
                ;
            } else {
                result = year + "-" +
                        formatMonthsDaysHoursMinutesEtc(month) + "-" +
                        formatMonthsDaysHoursMinutesEtc(day) + ", " +
                        formatMonthsDaysHoursMinutesEtc(hour) + "." +
                        formatMonthsDaysHoursMinutesEtc(min) + "-" +
                        formatMonthsDaysHoursMinutesEtc(hour) + "." +
                        formatMonthsDaysHoursMinutesEtc(min + 1)
                ;
            }

        } catch (ParseException e) {
            System.err.format("ParseException: %s%n", e);
        }
        return result;
    }

    private TreeMap<String, Integer> createTreeMapFromList(List<String> list, Boolean hours) {
        TreeMap<String, Integer> map = new TreeMap<>();
        for (String s : list) {
            String tmp = parseLine(s, hours);
            if (map.containsKey(tmp)) {
                map.put(tmp, map.get(tmp) + 1);
            } else {
                map.put(tmp, 1);
            }
        }
        return map;
    }

    private List<String> findErrors() {
        List<String> list = new ArrayList<>();
        Path dir = Paths.get(logsPath);
        try {
            list = Files.walk(dir)
                    .filter(file -> file.toString().endsWith(".log"))
                    .parallel()
                    .flatMap((p) -> {
                        try {
                            return Files.lines(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }).filter(s -> s.contains("ERROR"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return list;
    }

    private String formatMonthsDaysHoursMinutesEtc(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return String.valueOf(i);
        }
    }
}
