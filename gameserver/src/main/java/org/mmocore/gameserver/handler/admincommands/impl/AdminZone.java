package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.mapregion.DomainArea;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;


public class AdminZone implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (activeChar == null || !activeChar.getPlayerAccess().CanTeleport) {
            return false;
        }

        switch (command) {
            case admin_zone_check: {
                activeChar.sendAdminMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendAdminMessage("Zone list:");
                final List<Zone> zones = new ArrayList<>();
                World.getZones(zones, activeChar.getLoc(), activeChar.getReflection());
                for (final Zone zone : zones) {
                    activeChar.sendAdminMessage(zone.getType().toString() + ", name: " + zone.getName() + ", state: " + (zone.isActive() ? "active" : "not active") + ", inside: " + zone.checkIfInZone(activeChar) + '/' + zone.checkIfInZone(activeChar.getX(), activeChar.getY(), activeChar.getZ()));
                }

                break;
            }
            case admin_region: {
                activeChar.sendAdminMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendAdminMessage("Objects list:");
                for (final GameObject o : activeChar.getCurrentRegion()) {
                    if (o != null) {
                        activeChar.sendAdminMessage(o.toString());
                    }
                }
                break;
            }
            case admin_vis_count: {
                activeChar.sendAdminMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendAdminMessage("Players count: " + World.getAroundPlayers(activeChar).size());
                break;
            }
            case admin_pos: {
                final String pos = activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading() + " Geo [" + (activeChar.getX() - World.MAP_MIN_X >> 4) + ", " + (activeChar.getY() - World.MAP_MIN_Y >> 4) + "] Ref " + activeChar.getReflectionId();
                activeChar.sendAdminMessage("Pos: " + pos);
                break;
            }
            case admin_domain: {
                final DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, activeChar);
                final Castle castle = domain != null ? ResidenceHolder.getInstance().getResidence(Castle.class, domain.getId()) : null;
                if (castle != null) {
                    activeChar.sendAdminMessage("Domain: " + castle.getName());
                } else {
                    activeChar.sendAdminMessage("Domain: Unknown");
                }
            }
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
        admin_zone_check,
        admin_region,
        admin_pos,
        admin_vis_count,
        admin_domain
    }
}