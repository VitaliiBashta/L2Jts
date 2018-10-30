package org.mmocore.gameserver.scripts.instances;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.utils.Location;

/**
 * Класс контролирует ночного Закена
 *
 * @author pchayka
 */

public class ZakenNight extends Reflection {
    private ZoneListener _epicZoneListener = new ZoneListener();

    @Override
    protected void onCreate() {
        super.onCreate();
        getZone("[zaken_day_epic]").addListener(_epicZoneListener);
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            if (cha.isPlayable() && cha.getLevel() > 65) {
                Playable p = ((Playable) cha);
                p.paralizeMe(cha);
                p.teleToLocation(new Location(52680, 219992, -3522)); // Teleporting to the entrance
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}