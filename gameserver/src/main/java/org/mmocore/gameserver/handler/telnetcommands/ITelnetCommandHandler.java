package org.mmocore.gameserver.handler.telnetcommands;

import java.util.Set;

@FunctionalInterface
public interface ITelnetCommandHandler {
    /**
     * Get handler commands
     *
     * @return
     */
    Set<TelnetCommand> getCommands();

}
