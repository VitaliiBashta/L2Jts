package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.database.dao.impl.ServerVariablesDAO;
import org.mmocore.gameserver.templates.StatsSet;

public class ServerVariables {
    private static final StatsSet SERVER_VARIABLES;

    static {
        SERVER_VARIABLES = ServerVariablesDAO.getInstance().selectAll();
    }

    public static boolean getBool(final String name) {
        return SERVER_VARIABLES.getBool(name);
    }

    public static boolean getBool(final String name, final boolean defaultValue) {
        return SERVER_VARIABLES.getBool(name, defaultValue);
    }

    public static int getInt(final String name) {
        return SERVER_VARIABLES.getInteger(name);
    }

    public static int getInt(final String name, final int defult) {
        return SERVER_VARIABLES.getInteger(name, defult);
    }

    public static long getLong(final String name) {
        return SERVER_VARIABLES.getLong(name);
    }

    public static long getLong(final String name, final long defult) {
        return SERVER_VARIABLES.getLong(name, defult);
    }

    public static double getFloat(final String name) {
        return SERVER_VARIABLES.getDouble(name);
    }

    public static double getFloat(final String name, final double defult) {
        return SERVER_VARIABLES.getDouble(name, defult);
    }

    public static String getString(final String name) {
        return SERVER_VARIABLES.getString(name);
    }

    public static String getString(final String name, final String defult) {
        return SERVER_VARIABLES.getString(name, defult);
    }

    public static void set(final String name, final boolean value) {
        SERVER_VARIABLES.set(name, value);
        saveToDatabase(name);
    }

    public static void set(final String name, final int value) {
        SERVER_VARIABLES.set(name, value);
        saveToDatabase(name);
    }

    public static void set(final String name, final long value) {
        SERVER_VARIABLES.set(name, value);
        saveToDatabase(name);
    }

    public static void set(final String name, final double value) {
        SERVER_VARIABLES.set(name, value);
        saveToDatabase(name);
    }

    public static void set(final String name, final String value) {
        SERVER_VARIABLES.set(name, value);
        saveToDatabase(name);
    }

    public static void unset(final String name) {
        SERVER_VARIABLES.unset(name);
        saveToDatabase(name);
    }

    private static void saveToDatabase(final String name) {
        final String valueStr = SERVER_VARIABLES.getString(name, "");
        ServerVariablesDAO.getInstance().save(name, valueStr);
    }
}