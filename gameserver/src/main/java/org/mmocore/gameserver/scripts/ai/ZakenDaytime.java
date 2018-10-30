package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.instances.ZakenDay;

/**
 * Daytime Zaken.
 * - иногда призывает 4х мобов id: 29026
 *
 * @author pchayka
 */
public class ZakenDaytime extends Fighter {
    private static final long _spawnReuse = 60000L;
    private static final int _summonId = 29026;
    private long _spawnTimer = 0L;
    private final NpcInstance actor = getActor();
    final Reflection r = actor.getReflection();

    public ZakenDaytime(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void thinkAttack() {
        if (actor.getCurrentHpPercents() < 70 && _spawnTimer + _spawnReuse < System.currentTimeMillis()) {
            for (int i = 0; i < 4; i++) {
                final NpcInstance add = r.addSpawnWithoutRespawn(_summonId, actor.getLoc(), 250);
                add.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 2000);
            }
            _spawnTimer = System.currentTimeMillis();
        }
        super.thinkAttack();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        ((ZakenDay) r).notifyZakenDeath();
        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));
        r.setReenterTime(System.currentTimeMillis());
        super.onEvtDead(killer);
    }
}