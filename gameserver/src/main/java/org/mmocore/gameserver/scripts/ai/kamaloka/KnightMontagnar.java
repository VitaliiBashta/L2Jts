package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Босс 56й камалоки
 *
 * @author pchayka
 */

public class KnightMontagnar extends Fighter {
    private static final int _followerId = 18569;  // Follower of Montagnar
    private final static long _spawnInterval = 60000L;
    private final static int _spawnLimit = 6;
    private final static long _orderInterval = 24000L;
    private final NpcInstance actor = getActor();
    private long _spawnTimer = 0L;
    private int _spawnCounter = 0;
    private long _orderTimer = 0L;

    public KnightMontagnar(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void thinkAttack() {
        if (_spawnTimer == 0) {
            _spawnTimer = System.currentTimeMillis();
        }
        if (_spawnCounter < _spawnLimit && _spawnTimer + _spawnInterval < System.currentTimeMillis()) {
            final NpcInstance follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
            follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000000);
            _spawnTimer = System.currentTimeMillis();
            _spawnCounter++;
        }
        if (_spawnCounter > 0 && _orderTimer + _orderInterval < System.currentTimeMillis()) {
            final List<Player> aggressionList = new ArrayList<Player>();
            for (final Creature p : actor.getAroundCharacters(1500, 200)) {
                if (p.isPlayer() && !p.isDead()) {
                    aggressionList.add(p.getPlayer());
                }
            }

            if (!aggressionList.isEmpty()) {
                final Player aggressionTarget = aggressionList.get(Rnd.get(aggressionList.size()));
                if (aggressionTarget != null) {
                    ChatUtils.say(actor, NpcString.YOU_S1_ATTACK_THEM, aggressionTarget.getName());
                    _orderTimer = System.currentTimeMillis();
                    for (final NpcInstance minion : actor.getReflection().getNpcs()) {
                        if (minion.getNpcId() == _followerId) {
                            minion.getAggroList().clear(false);
                            minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, aggressionTarget, 1000000);
                        }
                    }
                }
            }
        }
        super.thinkAttack();
    }
}