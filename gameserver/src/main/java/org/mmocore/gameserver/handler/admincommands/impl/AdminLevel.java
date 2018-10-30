package org.mmocore.gameserver.handler.admincommands.impl;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.PlayerUtils;

public class AdminLevel implements IAdminCommandHandler {
    private void setLevel(final Player activeChar, final GameObject target, final int level) {
        if (target == null || !(target.isPlayer() || target.isPet())) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        if (level < 1 || level > PlayerUtils.getMaxLevel()) {
            activeChar.sendAdminMessage("You must specify level 1 - " + PlayerUtils.getMaxLevel());
            return;
        }
        if (target.isPlayer()) {
            final long exp_add = ExpDataHolder.getInstance().getExpForLevel(level) - ((Player) target).getExp();
            ((Player) target).addExpAndSp(exp_add, 0);
            return;
        }
        if (target.isPet()) {
            final long expAdd = PetDataHolder.getInstance().getPetData(((PetInstance) target).getNpcId()).getLevelStatForLevel(level).getExp() - ((PetInstance) target).getExp();
            ((PetInstance) target).addExpAndSp(expAdd, 0);
        }
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        final GameObject target = activeChar.getTarget();
        if (target == null || !(target.isPlayer() || target.isPet())) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }
        final int level;

        switch (command) {
            case admin_add_level:
            case admin_addLevel:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //addLevel level");
                    return false;
                }
                try {
                    level = Integer.parseInt(wordList[1]);
                } catch (NumberFormatException e) {
                    activeChar.sendAdminMessage("You must specify level");
                    return false;
                }
                setLevel(activeChar, target, level + ((Creature) target).getLevel());
                break;
            case admin_set_level:
            case admin_setLevel:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //setLevel level");
                    return false;
                }
                try {
                    level = Integer.parseInt(wordList[1]);
                } catch (NumberFormatException e) {
                    activeChar.sendAdminMessage("You must specify level");
                    return false;
                }
                setLevel(activeChar, target, level);
                break;
        }

        return true;
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
        admin_add_level,
        admin_addLevel,
        admin_set_level,
        admin_setLevel,
    }
}