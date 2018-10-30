package org.mmocore.gameserver.scripts.ai.pts.gracia.aerial_cleft;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.events.impl.AerialCleftEvent;
import org.mmocore.gameserver.model.entity.events.impl.AerialCleftEvent.DestroyType;
import org.mmocore.gameserver.model.entity.events.objects.AerialCleftPlayerObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class ai_emery_facility extends Fighter {
    private int zoneType;
    private int destroyPoint;
    private String areaDataName;
    private DestroyType towerType;

    public ai_emery_facility(NpcInstance actor) {
        super(actor);
        zoneType = actor.getTemplate().getAIParams().getInteger("ZoneType", 0);
        destroyPoint = actor.getTemplate().getAIParams().getInteger("DestroyPoint", 0);
        areaDataName = actor.getTemplate().getAIParams().getString("AreaDataName", "none");
    }

    @Override
    protected void onEvtSpawn() {
        final NpcInstance actor = getActor();
        actor.startImmobilized();
        switch (actor.getNpcId()) {
            case 22553:
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.THE_CENTRAL_S_COMPRESSOR_IS_WORKING, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22554:
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.THE_CENTRAL_I_S_COMPRESSOR_IS_WORKING, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22555:
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.THE_CENTRAL_II_S_COMPRESSOR_IS_WORKING, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22556:
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.THE_CENTRAL_III_S_COMPRESSOR_IS_WORKING, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
        }
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        Player player = killer.getPlayer();
        if (player == null) {
            return;
        }

        final NpcInstance actor = getActor();
        switch (actor.getNpcId()) {
            case 22553:
                towerType = DestroyType.CENTRAL_COMPRESSOR;
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.THE_CENTRAL_STRONGHOLD_COMPRESSOR_HAS_BEEN_DESTROYED, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22554:
                towerType = DestroyType.I_COMPRESSOR;
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.STRONGHOLD_I_S_COMPRESSOR_HAS_BEEN_DESTROYED, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22555:
                towerType = DestroyType.II_COMPRESSOR;
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.STRONGHOLD_II_S_COMPRESSOR_HAS_BEEN_DESTROYED, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
            case 22556:
                towerType = DestroyType.III_COMPRESSOR;
                actor.broadcastPacket(new ExShowScreenMessage(NpcString.STRONGHOLD_III_S_COMPRESSOR_HAS_BEEN_DESTROYED, 6000, ScreenMessageAlign.TOP_CENTER));
                break;
        }
        AerialCleftEvent aerialCleftEvent = getActor().getEvent(AerialCleftEvent.class);
        if (aerialCleftEvent != null) {
            AerialCleftPlayerObject particlePlayer = aerialCleftEvent.getParticlePlayer(player);
            particlePlayer.setPoints(particlePlayer.getPoints() + destroyPoint);
            aerialCleftEvent.updatePoints(particlePlayer, towerType, zoneType, destroyPoint);
        }
    }
}