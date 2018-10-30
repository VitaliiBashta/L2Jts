package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public final class AdminFunctions {
    public static final Location JAIL_SPAWN = new Location(-114648, -249384, -2984);

    private AdminFunctions() {
    }

    /**
     * Кикнуть игрока из игры.
     *
     * @param player - имя игрока
     * @param reason - причина кика
     * @return true если успешно, иначе false
     */
    public static boolean kick(final String player, final String reason) {
        final Player plyr = World.getPlayer(player);
        if (plyr == null) {
            return false;
        }

        return kick(plyr, reason);
    }

    public static boolean kick(final int obj_id, final String reason) {
        final Player plyr = World.getPlayer(obj_id);
        if (plyr == null) {
            return false;
        }

        return kick(plyr, reason);
    }


    public static boolean kick(final Player player, final String reason) {
        if (ServerConfig.ALLOW_CURSED_WEAPONS && ServerConfig.DROP_CURSED_WEAPONS_ON_KICK) {
            if (player.isCursedWeaponEquipped()) {
                player.setPvpFlag(0);
                CursedWeaponsManager.getInstance().dropPlayer(player);
            }
        }

        if (player.isInOfflineMode()) {
            player.setOfflineMode(false);
        }

        player.kick();

        return true;
    }

    public static String banChat(final Player adminChar, String adminName, String charName, final int val, String reason) {
        final Player player = World.getPlayer(charName);

        if (player != null) {
            charName = player.getName();
        } else if (CharacterDAO.getInstance().getObjectIdByName(charName) == 0) {
            return "Игрок " + charName + " не найден.";
        }

        if ((adminName == null || adminName.isEmpty()) && adminChar != null) {
            adminName = adminChar.getName();
        }

        if (reason == null || reason.isEmpty()) {
            reason = "не указана"; // if no args, then "не указана" default.
        }

        final String result;
        String announce = null;
        if (val == 0) //unban
        {
            if (adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat) {
                return "Вы не имеете прав на снятие бана чата.";
            }
            if (ServerConfig.BANCHAT_ANNOUNCE) {
                announce = ServerConfig.BANCHAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " снял бан чата с игрока " +
                        charName + '.'
                        : "С игрока " + charName + " снят бан чата.";
            }
            Log.add(adminName + " снял бан чата с игрока " + charName + '.', "banchat", adminChar);
            result = "Вы сняли бан чата с игрока " + charName + '.';
        } else if (val < 0) {
            if (adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue > 0) {
                return "Вы можете банить не более чем на " + adminChar.getPlayerAccess().BanChatMaxValue + " минут.";
            }
            if (ServerConfig.BANCHAT_ANNOUNCE) {
                announce = ServerConfig.BANCHAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " забанил чат игроку " + charName +
                        " на бессрочный период, причина: " + reason +
                        '.' : "Забанен чат игроку " + charName +
                        " на бессрочный период, причина: " +
                        reason + '.';
            }
            Log.add(adminName + " забанил чат игроку " + charName + " на бессрочный период, причина: " + reason + '.', "banchat", adminChar);
            result = "Вы забанили чат игроку " + charName + " на бессрочный период.";
        } else {
            if (adminChar != null && !adminChar.getPlayerAccess().CanUnBanChat && (player == null || player.getNoChannel() != 0)) {
                return "Вы не имеете права изменять время бана.";
            }
            if (adminChar != null && adminChar.getPlayerAccess().BanChatMaxValue != -1 && val > adminChar.getPlayerAccess().BanChatMaxValue) {
                return "Вы можете банить не более чем на " + adminChar.getPlayerAccess().BanChatMaxValue + " минут.";
            }
            if (ServerConfig.BANCHAT_ANNOUNCE) {
                announce = ServerConfig.BANCHAT_ANNOUNCE_NICK && adminName != null && !adminName.isEmpty() ? adminName + " забанил чат игроку " + charName +
                        " на " + val + " минут, причина: " + reason +
                        '.'
                        : "Забанен чат игроку " + charName + " на " +
                        val + " минут, причина: " + reason + '.';
            }
            Log.add(adminName + " забанил чат игроку " + charName + " на " + val + " минут, причина: " + reason + '.', "banchat", adminChar);
            result = "Вы забанили чат игроку " + charName + " на " + val + " минут.";
        }

        if (player != null) {
            updateNoChannel(player, val, reason);
        } else {
            AutoBan.ChatBan(charName, val, adminName);
        }

        if (announce != null) {
            if (ServerConfig.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
                AnnouncementUtils.announceToAll(announce);
            } else {
                AnnouncementUtils.shout(adminChar, announce, ChatType.CRITICAL_ANNOUNCE);
            }
        }

        return result;
    }

    private static void updateNoChannel(final Player player, final int time, final String reason) {
        player.updateNoChannel(time * 60000L);
        if (time == 0) {
            player.sendMessage(new CustomMessage("common.ChatUnBanned"));
        } else if (time > 0) {
            if (reason == null || reason.isEmpty()) {
                player.sendMessage(new CustomMessage("common.ChatBanned").addNumber(time));
            } else {
                player.sendMessage(new CustomMessage("common.ChatBannedWithReason").addNumber(time).addString(reason));
            }
        } else if (reason == null || reason.isEmpty()) {
            player.sendMessage(new CustomMessage("common.ChatBannedPermanently"));
        } else {
            player.sendMessage(new CustomMessage("common.ChatBannedPermanentlyWithReason").addString(reason));
        }
    }
}
