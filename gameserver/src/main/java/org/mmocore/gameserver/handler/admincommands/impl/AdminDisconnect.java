package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;


public class AdminDisconnect implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanKick) {
            return false;
        }

        switch (command) {
            case admin_disconnect:
            case admin_kick:
                final Player player;
                if (wordList.length == 1) {
                    // Обработка по таргету
                    final GameObject target = activeChar.getTarget();
                    if (target == null) {
                        activeChar.sendAdminMessage("Select character or specify player name.");
                        break;
                    }
                    if (!target.isPlayer()) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        break;
                    }
                    player = (Player) target;
                } else {
                    // Обработка по нику
                    player = World.getPlayer(wordList[1]);
                    if (player == null) {
                        activeChar.sendAdminMessage("Character " + wordList[1] + " not found in game.");
                        break;
                    }
                }

                if (player.getObjectId() == activeChar.getObjectId()) {
                    activeChar.sendAdminMessage("You can't logout your character.");
                    break;
                }

                activeChar.sendAdminMessage("Character " + player.getName() + " disconnected from server.");

                if (player.isInOfflineMode()) {
                    player.setOfflineMode(false);
                    player.kick();
                    return true;
                }

                player.sendMessage(new CustomMessage("admincommandhandlers.AdminDisconnect.YoureKickedByGM"));

                ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        player.kick();
                    }
                }, 500);
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
        admin_disconnect,
        admin_kick
    }
}