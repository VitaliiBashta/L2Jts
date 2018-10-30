package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminMammon implements IAdminCommandHandler {
    final List<Integer> npcIds = new ArrayList<>();

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        npcIds.clear();

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        } else if (fullString.startsWith("admin_find_mammon")) {
            npcIds.add(31113);
            npcIds.add(31126);
            npcIds.add(31092); // Add the Marketeer of Mammon also
            int teleportIndex = -1;

            try {
                if (fullString.length() > 16) {
                    teleportIndex = Integer.parseInt(fullString.substring(18));
                }
            } catch (Exception NumberFormatException) {
                // activeChar.sendPacket(SystemMessage.sendString("Command format is
                // //find_mammon <teleportIndex>"));
            }

            findAdminNPCs(activeChar, npcIds, teleportIndex, -1);
        } else if ("admin_show_mammon".equals(fullString)) {
            npcIds.add(31113);
            npcIds.add(31126);

            findAdminNPCs(activeChar, npcIds, -1, 1);
        } else if ("admin_hide_mammon".equals(fullString)) {
            npcIds.add(31113);
            npcIds.add(31126);

            findAdminNPCs(activeChar, npcIds, -1, 0);
        } else if (fullString.startsWith("admin_list_spawns")) {
            int npcId = 0;

            try {
                npcId = Integer.parseInt(fullString.substring(18).trim());
            } catch (Exception NumberFormatException) {
                activeChar.sendAdminMessage("Command format is //list_spawns <NPC_ID>");
            }

            npcIds.add(npcId);
            findAdminNPCs(activeChar, npcIds, -1, -1);
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    public void findAdminNPCs(final Player activeChar, final List<Integer> npcIdList, final int teleportIndex, final int makeVisible) {
        int index = 0;

        for (final NpcInstance npcInst : GameObjectsStorage.getNpcs()) {
            final int npcId = npcInst.getNpcId();
            if (npcIdList.contains(npcId)) {
                if (makeVisible == 1) {
                    npcInst.spawnMe();
                } else if (makeVisible == 0) {
                    npcInst.decayMe();
                }

                if (npcInst.isVisible()) {
                    index++;

                    if (teleportIndex > -1) {
                        if (teleportIndex == index) {
                            activeChar.teleToLocation(npcInst.getLoc());
                        }
                    } else {
                        activeChar.sendAdminMessage(index + " - " + npcInst.getName() + " (" + npcInst.getObjectId() + "): " + npcInst.getX() + ' ' + npcInst.getY() + ' ' + npcInst.getZ());
                    }
                }
            }
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_find_mammon,
        admin_show_mammon,
        admin_hide_mammon,
        admin_list_spawns
    }
}