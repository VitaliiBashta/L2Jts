package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.listener.actor.player.impl.SnoopPlayerSayListener;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author VISTALL
 * @date 20:49/15.09.2011
 */
public class AdminSnoop implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().CanSnoop) {
            return false;
        }
        if (wordList.length != 3 || !"on".equalsIgnoreCase(wordList[2]) && !"off".equalsIgnoreCase(wordList[2])) {
            activeChar.sendAdminMessage("USAGE: //snoop [TARGET_NAME] [on|off]");
            return false;
        }

        boolean on = "on".equalsIgnoreCase(wordList[2]);
        if (on) {
            String currentSnoop = activeChar.getPlayerVariables().get(PlayerVariables.SNOOP_TARGET);
            if (currentSnoop == null) {
                Player target = GameObjectsStorage.getPlayer(wordList[1]);
                if (target == null) {
                    activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
                    return false;
                }

                target.addListener(new SnoopPlayerSayListener(activeChar));
                activeChar.getPlayerVariables().set(PlayerVariables.SNOOP_TARGET, wordList[1], -1);

                activeChar.sendAdminMessage("SNOOP: you snoop target: " + wordList[1]);
                return true;
            } else {
                activeChar.sendAdminMessage("SNOOP: you already snooped target: " + currentSnoop);
            }
        } else {
            String currentSnoop = (String) activeChar.getPlayerVariables().remove(PlayerVariables.SNOOP_TARGET);
            if (currentSnoop == null) {
                activeChar.sendAdminMessage("SNOOP: you not snoop any target");
            } else {
                Player target = GameObjectsStorage.getPlayer(currentSnoop);
                if (target == null) {
                    activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
                } else {
                    for (SnoopPlayerSayListener listener : target.getListeners().getListeners(SnoopPlayerSayListener.class))
                        if (listener.getOwner() == activeChar) {
                            target.removeListener(listener);
                            break;
                        }
                }
                activeChar.sendAdminMessage("SNOOP: you cancel snoop for target: " + currentSnoop);
            }
        }

        return false;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_snoop,
    }
}