package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminHeal implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Heal) {
            return false;
        }

        switch (command) {
            case admin_heal:
                if (wordList.length == 1) {
                    handleRes(activeChar);
                } else {
                    handleRes(activeChar, wordList[1]);
                }
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleRes(final Player activeChar) {
        handleRes(activeChar, null);
    }

    private void handleRes(final Player activeChar, final String player) {

        GameObject obj = activeChar.getTarget();
        if (player != null) {
            final Player plyr = World.getPlayer(player);

            if (plyr != null) {
                obj = plyr;
            } else {
                final int radius = Math.max(Integer.parseInt(player), 100);
                for (final Creature character : activeChar.getAroundCharacters(radius, 200)) {
                    character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
                    if (character.isPlayer()) {
                        character.setCurrentCp(character.getMaxCp());
                    }
                }
                activeChar.sendAdminMessage("Healed within " + radius + " unit radius.");
                return;
            }
        }

        if (obj == null) {
            obj = activeChar;
        }

        if (obj instanceof Creature) {
            final Creature target = (Creature) obj;
            target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
            if (target.isPlayer()) {
                target.setCurrentCp(target.getMaxCp());
            }
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
        admin_heal
    }
}