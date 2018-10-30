package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.ArrayList;
import java.util.List;

public class Archangel extends Fighter {
    private final Zone _zone = ReflectionUtils.getZone("[baium_epic]");
    private long _new_target = System.currentTimeMillis() + 20000;

    public Archangel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance baiumBoss = GameObjectsStorage.getByNpcId(29020);
        if (baiumBoss == null) {
            return false;
        }
        return super.thinkActive();
    }

    @Override
    protected void thinkAttack() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;
        if (_new_target < System.currentTimeMillis()) {
            List<Creature> alive = new ArrayList<Creature>();
            for (Creature target : actor.getAroundCharacters(2000, 200)) {
                if (!target.isDead()) {
                    if (target.getNpcId() == 29020) {
                        if (Rnd.chance(5)) {
                            alive.add(target);
                        }
                    } else {
                        alive.add(target);
                    }
                }
            }
            if (!alive.isEmpty()) {
                Creature rndTarget = alive.get(Rnd.get(alive.size()));
                if (rndTarget != null && (rndTarget.getNpcId() == 29020 || rndTarget.isPlayer())) {
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, rndTarget);
                    actor.getAggroList().addDamageHate(rndTarget, 100, 10);
                }
            }
            _new_target = (System.currentTimeMillis() + 20000);
        }
        super.thinkAttack();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            if (attacker != null) {
                if (attacker.getNpcId() == 29020) {
                    actor.getAggroList().addDamageHate(attacker, damage, 10);
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (actor != null && !_zone.checkIfInZone(actor)) {
            returnHome();
        }
        return false;
    }

    @Override
    protected void returnHome() {
        NpcInstance actor = getActor();
        Location sloc = actor.getSpawnedLoc();
        clearTasks();
        actor.stopMove();
        actor.getAggroList().clear(true);
        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);
        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 0));
        actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
    }
}