package org.mmocore.gameserver.scripts.npc.model.pts.gracia;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author KilRoy
 */
public class MageNotingaleInstance extends NpcInstance {
    private static final Location LOCATION = new Location(-184545, 243120, 1581);
    private static final Location LOCATION_1 = new Location(-192361, 254528, 3598);
    private static final Location LOCATION_2 = new Location(-174600, 219711, 4424);
    private static final Location LOCATION_3 = new Location(-181989, 208968, 4424);
    private static final Location LOCATION_4 = new Location(-252898, 235845, 5343);
    private static final Location LOCATION_5 = new Location(-212819, 209813, 4288);
    private static final Location LOCATION_6 = new Location(-246899, 251918, 4352);

    public MageNotingaleInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=255&")) {
            if (command.endsWith("reply=1")) {
                if (player.getQuestState(10273) != null && player.getQuestState(10273).isCompleted()) {
                    showChatWindow(player, "pts/gracia/mage_notingale003.htm");
                } else {
                    showChatWindow(player, "pts/gracia/mage_notingale002.htm");
                    player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION));
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION));
                        }
                    }, 200L);
                }
            } else if (command.endsWith("reply=2")) {
                if (player.getQuestState(10273) != null && player.getQuestState(10273).isCompleted()) {
                    showChatWindow(player, "pts/gracia/mage_notingale004.htm");
                } else {
                    showChatWindow(player, "pts/gracia/mage_notingale002.htm");
                    player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION));
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION));
                        }
                    }, 200L);
                }
            } else if (command.endsWith("reply=3")) {
                if (player.getQuestState(10273) != null && player.getQuestState(10273).isCompleted()) {
                    showChatWindow(player, "pts/gracia/mage_notingale005.htm");
                } else {
                    showChatWindow(player, "pts/gracia/mage_notingale002.htm");
                    player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION));
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION));
                        }
                    }, 200L);
                }
            } else if (command.endsWith("reply=21")) {
                showChatWindow(player, "pts/gracia/mage_notingale0041.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_1));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_1));
                    }
                }, 200L);
            } else if (command.endsWith("reply=22")) {
                showChatWindow(player, "pts/gracia/mage_notingale0042.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_2));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_2));
                    }
                }, 200L);
            } else if (command.endsWith("reply=23")) {
                showChatWindow(player, "pts/gracia/mage_notingale0043.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_3));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_3));
                    }
                }, 200L);
            } else if (command.endsWith("reply=24")) {
                showChatWindow(player, "pts/gracia/mage_notingale0044.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_4));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_4));
                    }
                }, 200L);
            } else if (command.endsWith("reply=31")) {
                showChatWindow(player, "pts/gracia/mage_notingale0051.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_5));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_5));
                    }
                }, 200L);
            } else if (command.endsWith("reply=32")) {
                showChatWindow(player, "pts/gracia/mage_notingale0052.htm");
                player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, LOCATION_6));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, LOCATION_6));
                    }
                }, 200L);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}