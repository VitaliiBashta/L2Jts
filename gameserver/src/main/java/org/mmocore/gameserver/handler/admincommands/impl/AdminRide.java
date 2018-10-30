package org.mmocore.gameserver.handler.admincommands.impl;

import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;

public class AdminRide implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Rider) {
            return false;
        }

        switch (command) {
            case admin_ride:
                if (activeChar.isMounted() || activeChar.getServitor() != null) {
                    activeChar.sendAdminMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                if (wordList.length != 2) {
                    activeChar.sendAdminMessage("Incorrect id.");
                    return false;
                }
                activeChar.setMount(Integer.parseInt(wordList[1]), 0, 85, -1);
                break;
            case admin_ride_wyvern:
            case admin_wr:
                if (activeChar.isMounted() || activeChar.getServitor() != null) {
                    activeChar.sendAdminMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                activeChar.setMount(PetId.WYVERN_ID, 0, 85, -1);
                break;
            case admin_ride_strider:
            case admin_sr:
                if (activeChar.isMounted() || activeChar.getServitor() != null) {
                    activeChar.sendAdminMessage("Already Have a Pet or Mounted.");
                    return false;
                }
                activeChar.setMount(PetId.STRIDER_WIND_ID, 0, 85, -1);
                break;
            case admin_unride:
            case admin_ur:
                activeChar.dismount();
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
        admin_ride,
        admin_ride_wyvern,
        admin_ride_strider,
        admin_unride,
        admin_wr,
        admin_sr,
        admin_ur
    }
}