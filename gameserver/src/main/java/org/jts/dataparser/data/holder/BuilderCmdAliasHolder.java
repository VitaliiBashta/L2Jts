package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.buildercmdalias.Command;
import org.mmocore.commons.data.AbstractHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Camelion
 * @date : 25.08.12 22:47
 */
public class BuilderCmdAliasHolder extends AbstractHolder {
    private static final BuilderCmdAliasHolder ourInstance = new BuilderCmdAliasHolder();
    @Element(start = "command_begin", end = "command_end")
    public List<Command> commands;
    private final List<String> allCommands = new ArrayList<>();

    private BuilderCmdAliasHolder() {
    }

    public static BuilderCmdAliasHolder getInstance() {
        return ourInstance;
    }

    @Override
    public void afterParsing() {
        for (final Command command : commands) {
            allCommands.add(command.command);
        }
    }

    @Override
    public int size() {
        return commands.size();
    }

    public List<Command> getCommands() {
        return commands;
    }

    public String[] getAllStringCommands() {
        return allCommands.stream().toArray(String[]::new);
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}