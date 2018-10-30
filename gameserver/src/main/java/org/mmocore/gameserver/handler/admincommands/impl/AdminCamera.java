package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.network.lineage.serverpackets.CameraMode;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

public class AdminCamera implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        final GameObject target = activeChar.getTarget();

        switch (command) {
            case admin_freelook: {
                if (fullString.length() > 15) {
                    fullString = fullString.substring(15);
                } else {
                    activeChar.sendAdminMessage("Usage: //freelook 1 or //freelook 0");
                    return false;
                }

                final int mode = Integer.parseInt(fullString);
                if (mode == 1) {
                    activeChar.setInvisibleType(InvisibleType.NORMAL);
                    activeChar.setIsInvul(true);
                    activeChar.setNoChannel(-1);
                    activeChar.setFlying(true);
                } else {
                    activeChar.setInvisibleType(InvisibleType.NONE);
                    activeChar.setIsInvul(false);
                    activeChar.setNoChannel(0);
                    activeChar.setFlying(false);
                }
                activeChar.sendPacket(new CameraMode(mode));

                break;
            }
            case admin_cam: {
                if (wordList.length != 12) {
                    activeChar.sendMessage("Usage: //cam force angle1 angle2 time range duration relYaw relPitch isWide relAngle");
                    return false;
                }
                activeChar.specialCamera(target, Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), Integer.parseInt(wordList[3]),
                        Integer.parseInt(wordList[4]), Integer.parseInt(wordList[5]), Integer.parseInt(wordList[6]),
                        Integer.parseInt(wordList[7]), Integer.parseInt(wordList[8]), Integer.parseInt(wordList[9]),
                        Integer.parseInt(wordList[10]));
                break;
            }
            case admin_camex: {
                if (wordList.length != 10) {
                    activeChar.sendMessage("Usage: //camex force angle1 angle2 time duration relYaw relPitch isWide relAngle");
                    return false;
                }
                activeChar.specialCameraEx(target, Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), Integer.parseInt(wordList[3]),
                        Integer.parseInt(wordList[4]), Integer.parseInt(wordList[5]), Integer.parseInt(wordList[6]),
                        Integer.parseInt(wordList[7]), Integer.parseInt(wordList[8]), Integer.parseInt(wordList[9]));
                break;
            }
            case admin_cam3: {
                if (wordList.length != 12) {
                    activeChar.sendMessage("Usage: //cam3 force angle1 angle2 time range duration relYaw relPitch isWide relAngle unk");
                    return false;
                }
                activeChar.specialCamera3(target, Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), Integer.parseInt(wordList[3]),
                        Integer.parseInt(wordList[4]), Integer.parseInt(wordList[5]), Integer.parseInt(wordList[6]),
                        Integer.parseInt(wordList[7]), Integer.parseInt(wordList[8]), Integer.parseInt(wordList[9]),
                        Integer.parseInt(wordList[10]), Integer.parseInt(wordList[11]));
                break;
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
        admin_freelook,
        admin_cam,
        admin_camex,
        admin_cam3
    }
}