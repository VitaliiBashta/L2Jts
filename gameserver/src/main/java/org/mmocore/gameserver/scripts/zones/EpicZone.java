package org.mmocore.gameserver.scripts.zones;

import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

public class EpicZone implements OnInitScriptListener {

    @Override
    public void onInit() {
        ZoneListener zoneListener = new ZoneListener();
        Zone zone = ReflectionUtils.getZone("[queen_ant_epic]");
        zone.addListener(zoneListener);
    }

    public static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            if (zone.getParams() == null || !cha.isPlayable() || cha.getPlayer().isGM()) {
                return;
            }
            if (cha.getLevel() > zone.getParams().getInteger("levelLimit")) {
                if (cha.isPlayer()) {
                    cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.banishMsg"));
                }
                cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {

        }
    }
}
