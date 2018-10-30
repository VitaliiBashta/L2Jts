package org.mmocore.gameserver.scripts.ai.selmahum;

import org.mmocore.commons.utils.Rnd;
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
 * @author PaInKiLlEr - AI для моба Sel Mahum Recruit (22780) и Sel Mahum Recruit (22782) и Sel Mahum Soldier (22783) и Sel Mahum Recruit (22784) и
 * Sel Mahum Soldier (22785). - При атаке ругается в чат с шансом 20%, агрит главного моба. - AI проверен и работает.
 */
public class SelMahumRecruit extends Fighter {
    public static final NpcString[] text = {NpcString.THE_DRILLMASTER_IS_DEAD, NpcString.LINE_UP_THE_RANKS};
    private long wait_timeout = System.currentTimeMillis() + 180000;
    private List<NpcInstance> arm = new ArrayList<NpcInstance>();
    private boolean firstTimeAttacked = true;

    public SelMahumRecruit(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (wait_timeout < System.currentTimeMillis()) {
            wait_timeout = (System.currentTimeMillis() + Rnd.get(150, 200) * 1000);
            actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
        }

        if (arm == null || arm.isEmpty()) {
            for (NpcInstance npc : getActor().getAroundNpc(750, 750)) {
                if (npc != null && (npc.getNpcId() == 22775 || npc.getNpcId() == 22776 || npc.getNpcId() == 22778 || npc.getNpcId() == 22780 || npc.getNpcId() == 22782 || npc.getNpcId() == 22783 || npc.getNpcId() == 22784 || npc.getNpcId() == 22785))
                    arm.add(npc);
            }
        }
        return true;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        for (NpcInstance npc : arm) {
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));

            if (npc.isDead()) {
                if (Rnd.chance(20)) {
                    if (firstTimeAttacked) {
                        firstTimeAttacked = false;
                        Functions.npcSay(actor, text[Rnd.get(text.length)]);
                    }
                }
                actor.moveToLocation(actor.getSpawnedLoc(), 0, true);
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }
}