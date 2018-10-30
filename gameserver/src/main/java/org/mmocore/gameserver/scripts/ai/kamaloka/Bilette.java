package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 66й камалоки
 *
 * @author pchayka
 */
public class Bilette extends Fighter {
    private static final int _followerId = 18574;  // Follower of Bilette
    private final static long _spawnInterval = 60000L;
    private final static int _spawnLimit = 15;
    private final NpcInstance actor = getActor();
    private long _spawnTimer = 0L;
    private int _spawnCounter = 0;

    public Bilette(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void thinkAttack() {
        if (_spawnTimer == 0) {
            _spawnTimer = System.currentTimeMillis();
        }
        if (_spawnCounter < _spawnLimit && _spawnTimer + _spawnInterval < System.currentTimeMillis()) {
            final NpcInstance follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
            follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000);
            _spawnTimer = System.currentTimeMillis();
            _spawnCounter++;
        }
        super.thinkAttack();
    }
}