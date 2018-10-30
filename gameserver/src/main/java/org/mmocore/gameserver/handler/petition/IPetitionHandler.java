package org.mmocore.gameserver.handler.petition;

import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 22:15/25.07.2011
 */
@FunctionalInterface
public interface IPetitionHandler {
    void handle(Player player, int id, String txt);
}
