package org.mmocore.gameserver.handler.admincommands.impl;


import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class AdminDelete implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC) {
            return false;
        }

        switch (command) {
            case admin_delete:
                final GameObject obj = wordList.length == 1 ? activeChar.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(wordList[1]));
                if (obj != null && obj.isNpc()) {
                    final NpcInstance target = (NpcInstance) obj;
                    target.deleteMe();

                    final Spawner spawn = target.getSpawn();
                    if (spawn != null) {
                        spawn.stopRespawn();
                    }
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                }
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
        admin_delete
    }
}