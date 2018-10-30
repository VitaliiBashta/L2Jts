package org.mmocore.gameserver.model.instances;

import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowQuestInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 * N.F: 25064, 25134, 25487
 */
public class AdventurerInstance extends NpcInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdventurerInstance.class);

    public AdventurerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        if (getFnHi() == null || getFnHi().isEmpty()) {
            final int npcId = 1000000 + getNpcId();
            final String html = LinkerFactory.getInstance().findLinkFromValue(npcId);
            if (html != null && !html.isEmpty()) {
                showChatWindow(player, "pts/adventurer_agent/" + html + "001.htm");
            } else {
                LOGGER.error("Not find NPC: " + getNpcId() + " and him html not sended.");
            }
        }
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-18&")) {
            if (command.endsWith("reply=1")) {
                player.sendPacket(ExShowQuestInfo.STATIC);
            } else if (command.endsWith("reply=2")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_20_29.htm");
            } else if (command.endsWith("reply=3")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_30_39.htm");
            } else if (command.endsWith("reply=4")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_40_49.htm");
            } else if (command.endsWith("reply=5")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_50_59.htm");
            } else if (command.endsWith("reply=6")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_60_69.htm");
            } else if (command.endsWith("reply=7")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_70_79.htm");
            } else if (command.endsWith("reply=8")) {
                showChatWindow(player, "pts/adventurer_agent/boss_info/list_80_89.htm");
            }
        } else if (command.startsWith("show_radar?id=")) {
            try {
                final int bossId = Integer.parseInt(command.substring(14));
                switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossId)) {
                    case ALIVE:
                    case DEAD:
                        final Spawner spawn = RaidBossSpawnManager.getInstance().getSpawnTable().get(bossId);

                        final Location loc = spawn.getCurrentSpawnRange().getRandomLoc(spawn.getReflection().getGeoIndex());
                        player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, loc), new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, loc));
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, loc), new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.FLAG_ON_MAP, loc));
                            }
                        }, 200);
                        break;
                    case UNDEFINED:
                        LOGGER.error("Not find BOSS: " + bossId + " m.b him spawned in instance or on Gracia.");
                        break;
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid Bypass to Server command parameter.");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}