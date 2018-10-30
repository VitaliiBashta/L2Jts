package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminKill implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC) {
            return false;
        }

        switch (command) {
            case admin_kill:
                if (wordList.length == 1) {
                    handleKill(activeChar);
                } else {
                    handleKill(activeChar, wordList[1]);
                }
                break;
            case admin_damage:
                handleDamage(activeChar, NumberUtils.toInt(wordList[1], 1));
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleKill(final Player activeChar) {
        handleKill(activeChar, null);
    }

    private void handleKill(final Player activeChar, final String player) {
        GameObject obj = activeChar.getTarget();
        if (player != null) {
            final Player plyr = World.getPlayer(player);
            if (plyr != null) {
                obj = plyr;
            } else {
                final int radius = Math.max(Integer.parseInt(player), 100);
                for (final Creature character : activeChar.getAroundCharacters(radius, 200)) {
                    if (!character.isDoor()) {
                        character.doDie(activeChar);
                    }
                }
                activeChar.sendAdminMessage("Killed within " + radius + " unit radius.");
                return;
            }
        }

        if (obj != null && obj.isCreature()) {
            final Creature target = (Creature) obj;
            target.doDie(activeChar);
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }
    }

    private void handleDamage(final Player activeChar, final int damage) {
        final GameObject obj = activeChar.getTarget();

        if (obj == null) {
            activeChar.sendPacket(SystemMsg.SELECT_TARGET);
            return;
        }

        if (!obj.isCreature()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Creature cha = (Creature) obj;
        cha.reduceCurrentHp(damage, activeChar, null, true, true, false, false, false, false, true);
        activeChar.sendAdminMessage("You gave " + damage + " damage to " + cha.getName() + '.');
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_kill,
        admin_damage,
    }
}