package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AdminFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.StringTokenizer;

public class AdminMenu implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        if (fullString.startsWith("admin_teleport_character_to_menu")) {
            final String[] data = fullString.split(" ");
            if (data.length == 5) {
                final String playerName = data[1];
                final Player player = World.getPlayer(playerName);
                if (player != null) {
                    teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), activeChar);
                }
            }
        } else if (fullString.startsWith("admin_recall_char_menu")) {
            try {
                final String targetName = fullString.substring(23);
                final Player player = World.getPlayer(targetName);
                teleportCharacter(player, activeChar.getLoc(), activeChar);
            } catch (StringIndexOutOfBoundsException e) {
            }
        } else if (fullString.startsWith("admin_goto_char_menu")) {
            try {
                final String targetName = fullString.substring(21);
                final Player player = World.getPlayer(targetName);
                teleportToCharacter(activeChar, player);
            } catch (StringIndexOutOfBoundsException e) {
            }
        } else if ("admin_kill_menu".equals(fullString)) {
            GameObject obj = activeChar.getTarget();
            final StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String player = st.nextToken();
                final Player plyr = World.getPlayer(player);
                if (plyr == null) {
                    activeChar.sendAdminMessage("Player " + player + " not found in game.");
                }
                obj = plyr;
            }
            if (obj != null && obj.isCreature()) {
                final Creature target = (Creature) obj;
                target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null, true, true, true, false, false, false, true);
            } else {
                activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            }
        } else if (fullString.startsWith("admin_kick_menu")) {
            final StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String player = st.nextToken();
                if (AdminFunctions.kick(player, "kick")) {
                    activeChar.sendAdminMessage("Player kicked.");
                }
            }
        }

        activeChar.sendPacket(new HtmlMessage(5).setFile("admin/charmanage.htm"));
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void teleportCharacter(final Player player, final Location loc, final Player activeChar) {
        if (player != null) {
            player.sendMessage("Admin is teleporting you.");
            player.teleToLocation(loc);
        }
    }

    private void teleportToCharacter(final Player activeChar, final GameObject target) {
        final Player player;
        if (target != null && target.isPlayer()) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (player.getObjectId() == activeChar.getObjectId()) {
            activeChar.sendAdminMessage("You cannot self teleport.");
        } else {
            activeChar.teleToLocation(player.getLoc());
            activeChar.sendAdminMessage("You have teleported to character " + player.getName() + '.');
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_char_manage,
        admin_teleport_character_to_menu,
        admin_recall_char_menu,
        admin_goto_char_menu,
        admin_kick_menu,
        admin_kill_menu,
        admin_ban_menu,
        admin_unban_menu
    }
}