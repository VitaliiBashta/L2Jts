package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcSay;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

public class ChatUtils {
    private ChatUtils() {
    }

    public static void say(final Player activeChar, final Iterable<Player> players, final int range, final Say2 cs) {
        for (final Player player : players) {
            if (player.isBlockAll() || player.isInBlockList(activeChar)) {
                continue;
            }

            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            //Персонаж находится рядом с наблюдателем или точкой наблюдения
            if (activeChar.isInRangeZ(obj, range)) {
                if (!player.isInBlockList(activeChar)) {
                    player.sendPacket(cs);
                }
            }
        }
    }

    public static void say(final Player activeChar, final Say2 cs) {
        say(activeChar, World.getAroundObservers(activeChar), ServerConfig.CHAT_RANGE, cs);
    }

    public static void say(final Player activeChar, final Iterable<Player> players, final Say2 cs) {
        say(activeChar, players, ServerConfig.CHAT_RANGE, cs);
    }

    public static void say(final Player activeChar, final int range, final Say2 cs) {
        say(activeChar, World.getAroundObservers(activeChar), range, cs);
    }

    public static void shout(final Player activeChar, final Say2 cs) {
        final int rx = MapUtils.regionX(activeChar);
        final int ry = MapUtils.regionY(activeChar);
        final int offset = ServerConfig.SHOUT_OFFSET;

        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player == activeChar || player.isBlockAll() || player.isInBlockList(activeChar)) {
                continue;
            }

            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            if (activeChar.getReflection() != obj.getReflection()) {
                continue;
            }

            //Персонаж находится рядом в одном регионе с наблюдателем или точкой наблюдения
            final int tx = MapUtils.regionX(obj);
            final int ty = MapUtils.regionY(obj);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(obj, ServerConfig.CHAT_RANGE)) {
                player.sendPacket(cs);
            }
        }
    }

    public static void announce(final Player activeChar, final Say2 cs) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player == activeChar || player.isBlockAll() || player.isInBlockList(activeChar)) {
                continue;
            }

            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            if (!ServerConfig.allowGlobalChatInReflections && activeChar.getReflection() != obj.getReflection()) {
                continue;
            }

            player.sendPacket(cs);
        }
    }

    public static void say(final NpcInstance activeChar, final Iterable<Player> players, final int range, final NpcSay cs) {
        for (final Player player : players) {
            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            //Персонаж находится рядом с наблюдателем или точкой наблюдения
            if (activeChar.isInRangeZ(obj, range)) {
                player.sendPacket(cs);
            }
        }
    }

    public static void say(final NpcInstance activeChar, final NpcSay cs) {
        say(activeChar, World.getAroundObservers(activeChar), ServerConfig.CHAT_RANGE, cs);
    }

    public static void say(final NpcInstance activeChar, final Iterable<Player> players, final NpcSay cs) {
        say(activeChar, players, ServerConfig.CHAT_RANGE, cs);
    }

    public static void say(final NpcInstance activeChar, final int range, final NpcSay cs) {
        say(activeChar, World.getAroundObservers(activeChar), range, cs);
    }

    public static void say(final NpcInstance activeChar, final int range, final NpcString npcString, final String... params) {
        say(activeChar, range, new NpcSay(activeChar, ChatType.NPC_ALL, npcString, params));
    }

    public static void say(final NpcInstance npc, final NpcString npcString, final String... params) {
        say(npc, ServerConfig.CHAT_RANGE, npcString, params);
    }

    public static void say(final NpcInstance activeChar, final int range, final ChatType chatType, final NpcString npcString, final String... params) {
        say(activeChar, range, new NpcSay(activeChar, chatType, npcString, params));
    }

    public static void shout(final NpcInstance activeChar, final NpcSay cs) {
        final int rx = MapUtils.regionX(activeChar);
        final int ry = MapUtils.regionY(activeChar);
        final int offset = ServerConfig.SHOUT_OFFSET;

        for (final Player player : GameObjectsStorage.getPlayers()) {
            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            if (activeChar.getReflection() != obj.getReflection()) {
                continue;
            }

            //Персонаж находится рядом в одном регионе с наблюдателем или точкой наблюдения
            final int tx = MapUtils.regionX(obj);
            final int ty = MapUtils.regionY(obj);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(obj, ServerConfig.CHAT_RANGE)) {
                player.sendPacket(cs);
            }
        }
    }

    public static void shout(final NpcInstance activeChar, final NpcString npcString, final String... params) {
        shout(activeChar, new NpcSay(activeChar, ChatType.NPC_SHOUT, npcString, params));
    }

    public static void say(final NpcInstance activeChar, final Iterable<Player> players, final int range, final CustomMessage cm) {
        for (final Player player : players) {
            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            //Персонаж находится рядом с наблюдателем или точкой наблюдения
            if (activeChar.isInRangeZ(obj, range)) {
                player.sendPacket(new NpcSay(activeChar, ChatType.NPC_SHOUT, cm.toString(player)));
            }
        }
    }

    public static void say(final NpcInstance activeChar, final CustomMessage cm) {
        say(activeChar, World.getAroundObservers(activeChar), ServerConfig.CHAT_RANGE, cm);
    }

    public static void say(final NpcInstance activeChar, final Iterable<Player> players, final CustomMessage cm) {
        say(activeChar, players, ServerConfig.CHAT_RANGE, cm);
    }

    public static void say(final NpcInstance activeChar, final int range, final CustomMessage cm) {
        say(activeChar, World.getAroundObservers(activeChar), range, cm);
    }

    public static void shout(final NpcInstance activeChar, final CustomMessage cm) {
        final int rx = MapUtils.regionX(activeChar);
        final int ry = MapUtils.regionY(activeChar);
        final int offset = ServerConfig.SHOUT_OFFSET;

        for (final Player player : GameObjectsStorage.getPlayers()) {
            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }

            if (activeChar.getReflection() != obj.getReflection()) {
                continue;
            }

            //Персонаж находится рядом в одном регионе с наблюдателем или точкой наблюдения
            final int tx = MapUtils.regionX(obj);
            final int ty = MapUtils.regionY(obj);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(obj, ServerConfig.CHAT_RANGE)) {
                player.sendPacket(new NpcSay(activeChar, ChatType.NPC_SHOUT, cm.toString(player)));
            }
        }
    }
}
