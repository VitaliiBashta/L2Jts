package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminRes implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().Res) {
            return false;
        }

        if (fullString.startsWith("admin_res ")) {
            handleRes(activeChar, wordList[1]);
        }
        if ("admin_res".equals(fullString)) {
            handleRes(activeChar);
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
                try {
                    final int radius = Math.max(Integer.parseInt(player), 100);
                    for (final Creature character : activeChar.getAroundCharacters(radius, radius)) {
                        handleRes(character);
                    }
                    activeChar.sendAdminMessage("Resurrected within " + radius + " unit radius.");
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

        if (obj instanceof Creature) {
            handleRes((Creature) obj);
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }
    }

    private void handleRes(final Creature target) {
        if (!target.isDead()) {
            return;
        }

        if (target.isPlayable()) {
            if (target.isPlayer()) {
                ((Player) target).doRevive(100.);
            } else {
                ((Playable) target).doRevive();
            }
        } else if (target.isNpc()) {
            ((NpcInstance) target).stopDecay();
        }

        target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp(), true);
        target.setCurrentCp(target.getMaxCp());
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_res
    }
}