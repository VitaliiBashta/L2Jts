package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.configuration.config.gmAccess.GmAccessConfig;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GmManager {
    private static final List<Player> gms = new ArrayList<>(GmAccessConfig.gmlist.size());

    public static void load() {
        CharListenerList.addGlobal(new OnPlayerEnterListenerImpl());
        CharListenerList.addGlobal(new OnPlayerExitListenerImpl());
    }

    public static void addGm(final Player gm) {
        if (!gms.contains(gm))
            gms.add(gm);
    }

    public static void removeGm(final Player gm) {
        if (gms.contains(gm))
            gms.remove(gm);
    }

    public static List<Player> getAllGMs() {
        return gms;
    }

    public static List<Player> getAllVisibleGMs() {
        final List<Player> gmList = new ArrayList<>(gms.size());
        gmList.addAll(getAllGMs().stream().filter(player -> !player.isInvisible()).collect(Collectors.toList()));
        return gmList;
    }

    public static void sendListToPlayer(final Player player) {
        final List<Player> gmList = getAllVisibleGMs();
        if (gmList.isEmpty()) {
            player.sendPacket(SystemMsg.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
            return;
        }

        player.sendPacket(SystemMsg.GM_LIST);

        gmList.stream().filter(gm -> gm != null && !gm.getName().isEmpty()).forEach(gm -> {
            final SystemMessage message = new SystemMessage(SystemMsg.GM__C1);
            player.sendPacket(message.addString(gm.getName()));
        });
    }

    public static void broadcastToGMs(final L2GameServerPacket packet) {
        for (final Player gm : getAllGMs())
            gm.sendPacket(packet);
    }

    public static void broadcastMessageToGMs(final String message) {
        for (final Player gm : getAllGMs())
            gm.sendMessage(message);
    }

    private static class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            if (player.isGM())
                addGm(player);
        }
    }

    private static class OnPlayerExitListenerImpl implements OnPlayerExitListener {
        @Override
        public void onPlayerExit(final Player player) {
            if (player.isGM())
                removeGm(player);
        }
    }
}