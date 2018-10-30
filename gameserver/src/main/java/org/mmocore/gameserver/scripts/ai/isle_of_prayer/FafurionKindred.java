package org.mmocore.gameserver.scripts.ai.isle_of_prayer;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Diamond
 * @corrected n0nam3, KilRoy
 */
public class FafurionKindred extends Fighter {
    private static final int DETRACTOR1 = 22270;
    private static final int DETRACTOR2 = 22271;
    private static final int Spirit_of_the_Lake = 2368;
    private static final int Water_Dragon_Scale = 9691;
    private static final int Water_Dragon_Claw = 9700;
    private static final FuncTemplate ft = new FuncTemplate(null, "Mul", Stats.HEAL_EFFECTIVNESS, 0x90, 0);
    ScheduledFuture<?> poisonTask;

    public FafurionKindred(NpcInstance actor) {
        super(actor);
        actor.addStatFunc(ft.getFunc(this));
    }

    private void cleanUp() {
        if (poisonTask != null) {
            poisonTask.cancel(false);
            poisonTask = null;
        }
    }

    private void dropItem(NpcInstance actor, int id) {
        final ItemInstance item = ItemFunctions.createItem(id);
        item.setCount(1);
        item.dropToTheGround(actor, Location.findPointToStay(actor, 100));
    }

    public void privateDead(final int npcId) {
        if (getActor() != null && !getActor().isDead()) {
            if (npcId == DETRACTOR1) {
                AddTimerEx(1101, 1000);
            } else if (npcId == DETRACTOR2) {
                AddTimerEx(1102, 1000);
            }
        }
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        poisonTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PoisonTask(), 3000, 3000);
        NpcUtils.createOnePrivateEx(DETRACTOR1, actor.getX() + 50, actor.getY(), actor.getZ(), actor, 0, 0);
        NpcUtils.createOnePrivateEx(DETRACTOR1, actor.getX() + 100, actor.getY(), actor.getZ(), actor, 0, 0);
        NpcUtils.createOnePrivateEx(DETRACTOR2, actor.getX(), actor.getY() + 50, actor.getZ(), actor, 0, 0);
        NpcUtils.createOnePrivateEx(DETRACTOR2, actor.getX(), actor.getY() + 100, actor.getZ(), actor, 0, 0);
        AddTimerEx(1103, 30 * 1000);
        AddTimerEx(1104, 5 * 60 * 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == 1101) {
            NpcUtils.createOnePrivateEx(DETRACTOR1, Location.findPointToStay(actor, 120, 200), actor.getReflection(), actor, 0, 0);
        } else if (timer_id == 1102) {
            NpcUtils.createOnePrivateEx(DETRACTOR2, Location.findPointToStay(actor, 120, 200), actor.getReflection(), actor, 0, 0);
        } else if (timer_id == 1103) {
            if (actor.getSpawnedLoc() != null && !actor.isInRange(actor.getSpawnedLoc(), 500)) {
                returnHome(true);
            }
            AddTimerEx(1103, 30 * 1000);
        } else if (timer_id == 1104) {
            dropItem(actor, Water_Dragon_Scale);
            if (Rnd.get(100) < 33) {
                dropItem(actor, Water_Dragon_Claw);
            }
            AddTimerEx(1105, 1000);
        } else if (timer_id == 1105) {
            cleanUp();
            actor.deleteMe();
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        cleanUp();

        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        final NpcInstance actor = getActor();
        if (actor.isDead() || skill == null) {
            return;
        }
        // Лечим
        if (caster.getCastingTarget() == actor && skill.getId() == Spirit_of_the_Lake) {
            actor.setCurrentHp(actor.getCurrentHp() + 3000, false);
            actor.getAggroList().remove(caster, true);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private class PoisonTask extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            if (actor != null && !actor.isDead()) {
                actor.reduceCurrentHp(500, actor, null, true, false, true, false, false, false, false); // Травим дракошу ядом
            }
        }
    }
}