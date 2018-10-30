package org.mmocore.gameserver.model.entity.events.impl.reflection.object;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.entity.events.impl.reflection.raid.RAiRaidBoss;
import org.mmocore.gameserver.model.entity.events.impl.reflection.raid.RRaidBossInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class RRaidBossObject {
    private final RBaseController controller;
    private RRaidBossInstance npc;

    public RRaidBossObject(final RBaseController controller) {
        this.controller = controller;
    }

    public void spawnObject() {
        final int[] loc = controller.getEvent().getSet().getIntegerArray("boss_location");
        final Location location = new Location(loc[0], loc[1], loc[2]);
        final NpcTemplate template = NpcHolder.getInstance().getTemplate(25038);
        npc = new RRaidBossInstance(IdFactory.getInstance().getNextId(), template);
        npc.setHeading(location.h < 0 ? Rnd.get(0xFFFF) : location.h);
        npc.setSpawnedLoc(location);
        npc.setLevel(85);
        npc.setName("Event Boss");
        npc.setTitle("");
        npc.setReflection(controller.getEvent().getReflection());
        npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
        RAiRaidBoss rAiRaidBoss = new RAiRaidBoss(npc);
        rAiRaidBoss.setController(controller);
        npc.setAI(rAiRaidBoss);
        npc.spawnMe(npc.getSpawnedLoc());
    }

    public void despawnObject() {
        if (npc != null) {
            npc.deleteMe();
        }
    }

    public RRaidBossInstance getRaidBoss() {
        return npc;
    }
}
