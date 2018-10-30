package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.World;

import java.io.File;

public class AdminGeodata implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanReload) {
            return false;
        }

        switch (command) {
            case admin_geo_z:
                activeChar.sendAdminMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(activeChar.getLoc(), activeChar.getReflectionId()) + " Loc_Z = " + activeChar.getZ());
                break;
            case admin_geo_type:
                final int type = GeoEngine.getType(activeChar.getX(), activeChar.getY(), activeChar.getReflectionId());
                activeChar.sendAdminMessage("GeoEngine: Geo_Type = " + type);
                break;
            case admin_geo_nswe:
                String result = "";
                final byte nswe = GeoEngine.getNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getReflectionId());
                if ((nswe & 8) == 0) {
                    result += " N";
                }
                if ((nswe & 4) == 0) {
                    result += " S";
                }
                if ((nswe & 2) == 0) {
                    result += " W";
                }
                if ((nswe & 1) == 0) {
                    result += " E";
                }
                activeChar.sendAdminMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
                break;
            case admin_geo_los:
                if (activeChar.getTarget() != null) {
                    if (GeoEngine.canSeeTarget(activeChar, activeChar.getTarget(), false)) {
                        activeChar.sendAdminMessage("GeoEngine: Can See Target");
                    } else {
                        activeChar.sendAdminMessage("GeoEngine: Can't See Target");
                    }
                } else {
                    activeChar.sendAdminMessage("None Target!");
                }
                break;
            case admin_geo_load:
                if (wordList.length != 3) {
                    activeChar.sendAdminMessage("Usage: //geo_load <regionX> <regionY>");
                } else {
                    try {
                        final byte rx = Byte.parseByte(wordList[1]);
                        final byte ry = Byte.parseByte(wordList[2]);
                        if (rx < GeodataConfig.GEO_X_FIRST || ry < GeodataConfig.GEO_Y_FIRST || rx > GeodataConfig.GEO_X_LAST || ry > GeodataConfig.GEO_Y_LAST) {
                            activeChar.sendAdminMessage("Region [" + rx + "," + ry + "] is out of range!");
                            return false;
                        }

                        final File geoFile = new File(ServerConfig.DATAPACK_ROOT, String.format("%2d_%2d.l2j", rx, ry));
                        if (!geoFile.exists()) {
                            activeChar.sendMessage("Region [" + rx + "," + ry + "] not found!");
                            return false;
                        }

                        if (GeoEngine.LoadGeodataFile(rx, ry, geoFile)) {
                            GeoEngine.LoadGeodata(rx, ry, 0);
                            activeChar.sendAdminMessage("GeoEngine: Region [" + rx + ',' + ry + "] loaded.");
                        } else {
                            activeChar.sendAdminMessage("GeoEngine: Region [" + rx + ',' + ry + "] not loaded.");
                        }
                    } catch (Exception e) {
                        activeChar.sendMessage(new CustomMessage("common.Error"));
                    }
                }
                break;
            case admin_geo_dump:
                if (wordList.length > 2) {
                    GeoEngine.DumpGeodataFileMap(Byte.parseByte(wordList[1]), Byte.parseByte(wordList[2]));
                    activeChar.sendAdminMessage("Geo square saved " + wordList[1] + '_' + wordList[2]);
                }
                GeoEngine.DumpGeodataFile(activeChar.getX(), activeChar.getY());
                activeChar.sendAdminMessage("Actual geo square saved.");
                break;
            case admin_geo_trace:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Usage: //geo_trace on|off");
                    return false;
                }
                if ("on".equalsIgnoreCase(wordList[1])) {
                    activeChar.getPlayerVariables().set(PlayerVariables.GM_TRACE, "1", -1);
                } else if ("off".equalsIgnoreCase(wordList[1])) {
                    activeChar.getPlayerVariables().remove(PlayerVariables.GM_TRACE);
                } else {
                    activeChar.sendAdminMessage("Usage: //geo_trace on|off");
                }
                break;
            case admin_geo_map:
                final int x = (activeChar.getX() - World.MAP_MIN_X >> 15) + GeodataConfig.GEO_X_FIRST;
                final int y = (activeChar.getY() - World.MAP_MIN_Y >> 15) + GeodataConfig.GEO_Y_FIRST;

                activeChar.sendAdminMessage("GeoMap: " + x + '_' + y);
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
        admin_geo_z,
        admin_geo_type,
        admin_geo_nswe,
        admin_geo_los,
        admin_geo_load,
        admin_geo_dump,
        admin_geo_trace,
        admin_geo_map
    }
}