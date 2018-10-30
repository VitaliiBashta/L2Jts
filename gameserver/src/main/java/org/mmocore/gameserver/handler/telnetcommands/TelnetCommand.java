package org.mmocore.gameserver.handler.telnetcommands;

import org.apache.commons.lang3.ArrayUtils;

public abstract class TelnetCommand implements Comparable<TelnetCommand> {
    private final String command;
    private final String[] acronyms;

    public TelnetCommand(final String command) {
        this(command, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public TelnetCommand(final String command, final String... acronyms) {
        this.command = command;
        this.acronyms = acronyms;
    }

    public String getCommand() {
        return command;
    }

    public String[] getAcronyms() {
        return acronyms;
    }

    public abstract String getUsage();

    /**
     * Handle command and return result
     *
     * @param args arguments
     * @return result for output
     */
    public abstract String handle(String[] args);

    public boolean equals(final String command) {
        for (final String acronym : acronyms) {
            if (command.equals(acronym)) {
                return true;
            }
        }
        return this.command.equalsIgnoreCase(command);
    }

    @Override
    public String toString() {
        return command;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return true;
        }
        if (o instanceof TelnetCommand) {
            return command.equals(((TelnetCommand) o).command);
        }
        return false;
    }

    @Override
    public int compareTo(final TelnetCommand o) {
        return command.compareTo(o.command);
    }
}
