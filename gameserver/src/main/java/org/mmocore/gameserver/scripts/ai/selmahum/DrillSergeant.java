package org.mmocore.gameserver.scripts.ai.selmahum;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PaInKiLlEr - AI для моба Sel Mahum Drill Sergeant (22775), Sel Mahum Training Officer (22776), Sel Mahum Drill Sergeant (22778). - При
 * атаке ругается в чат, агрит тренерующихся мобов. - Юзает рандомные социалки и заставляет тренерующихся мобов повторять за ним 3 раза. - AI
 * проверен и работает.
 */
public class DrillSergeant extends Fighter {
    public static final NpcString[] text = {NpcString.HOW_DARE_YOU_ATTACK_MY_RECRUITS, NpcString.WHO_IS_DISPURTING_THE_ORDER};
    private static final int[] recruits = {22780, 22782, 22783, 22784, 22785};
    private long wait_timeout = System.currentTimeMillis() + 20000;
    private List<NpcInstance> arm = new ArrayList<NpcInstance>();
    private boolean firstTimeAttacked = true;

    public DrillSergeant(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 1000;
    }

    @Override
    protected boolean thinkActive() {
        final int social = Rnd.get(4, 7);
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (wait_timeout < System.currentTimeMillis()) {
            if (arm == null) // ну тут такого быть не может О_о
                arm = new ArrayList<NpcInstance>();
            if (arm.isEmpty())
                for (NpcInstance npc : actor.getAroundNpc(750, 750))
                    if (ArrayUtils.contains(recruits, npc.getNpcId()))
                        arm.add(npc);

            wait_timeout = (System.currentTimeMillis() + Rnd.get(20, 30) * 1000);

            actor.broadcastPacket(new SocialAction(actor.getObjectId(), social));
            actor.setHeading(actor.getSpawnedLoc().h);

            int time = 2000;
            for (int i = 0; i <= 2; i++) {
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (NpcInstance voin : arm) {
                            voin.setHeading(voin.getSpawnedLoc().h);
                            voin.broadcastPacket(new SocialAction(voin.getObjectId(), social));
                        }
                    }
                }, time);
                time += 2000;
            }
        }
        return true;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (attacker.isDead())
            actor.moveToLocation(actor.getSpawnedLoc(), 0, true);

        if (arm != null && !arm.isEmpty()) {
            NpcInstance npc = arm.get(arm.size() - 1);
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
        }

        if (firstTimeAttacked) {
            firstTimeAttacked = false;
            Functions.npcSay(actor, text[Rnd.get(text.length)]);
        }

        super.onEvtAttacked(attacker, skill, damage);
    }
}