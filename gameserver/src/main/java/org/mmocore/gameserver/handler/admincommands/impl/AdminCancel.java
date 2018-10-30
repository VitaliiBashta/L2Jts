package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminCancel implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        switch (command) {
            case admin_cancel:
                handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleCancel(final Player activeChar, final String targetName) {
        GameObject obj = activeChar.getTarget();
        if (targetName != null) {
            final Player plyr = World.getPlayer(targetName);
            if (plyr != null) {
                obj = plyr;
            } else {
                try {
                    final int radius = Math.max(Integer.parseInt(targetName), 100);
                    for (final Creature character : activeChar.getAroundCharacters(radius, 200)) {
                        character.getEffectList().stopAllEffects();
                    }
                    activeChar.sendAdminMessage("Apply Cancel within " + radius + " unit radius.");
                    return;
                } catch (NumberFormatException e) {
                    activeChar.sendAdminMessage("Enter valid player name or radius");
                    return;
                }
            }
        }

        if (obj == null) {
            obj = activeChar;
        }
        if (obj.isCreature()) {
            ((Creature) obj).getEffectList().stopAllEffects();
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_cancel
    }
}