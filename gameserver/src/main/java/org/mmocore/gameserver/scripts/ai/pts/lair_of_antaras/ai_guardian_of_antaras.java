package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.data.xml.holder.SuperPointHolder;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_guardian_of_antaras extends Fighter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ai_guardian_of_antaras.class);
    private static final int guardian_helper = 18967;
    private static final int guardian_manger = 18968;
    private static final SuperPoint SuperPointName1 = SuperPointHolder.getInstance().getSuperPointsByName("24_21_course1");
    private static final SuperPoint SuperPointName2 = SuperPointHolder.getInstance().getSuperPointsByName("24_21_course2");
    private static final SuperPoint SuperPointName3 = SuperPointHolder.getInstance().getSuperPointsByName("24_21_course3");
    private static final SuperPoint SuperPointName4 = SuperPointHolder.getInstance().getSuperPointsByName("24_21_course4");
    private int i_ai2;
    private int i_ai1;
    private int i_ai0;
    private int i_ai4;
    private int i_ai5;
    private int i_ai6;

    public ai_guardian_of_antaras(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 250;
        AI_TASK_ATTACK_DELAY = 1000;
        MAX_PURSUE_RANGE = 2000;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        actor.block();
        actor.setInvisibleType(InvisibleType.NORMAL);
        i_ai2 = 0;
        i_ai1 = 0;
        i_ai4 = actor.getX();
        i_ai5 = actor.getY();
        i_ai6 = actor.getZ();
        ThreadPoolManager.getInstance().schedule(() -> {
            i_ai0 = actor.getParameter("SuperPointMethod", 0);
            switch (i_ai0) {
                case 0:
                    LOGGER.warn("AI parameter is not seted! Wrong spawn, this guardian_of_antaras despawned.");
                    for (int i = 0; i < 4; i++) {
                        sendScriptEvent(ScriptEvent.SCE_WATCHER_DEAD, getActor().getLeader() == null ? 0 : getActor().getLeader().getObjectId(), i, null);
                    }
                    actor.deleteMe();
                    break;
                case 1:
                    setSuperPoint(SuperPointName1);
                    break;
                case 2:
                    setSuperPoint(SuperPointName2);
                    break;
                case 3:
                    setSuperPoint(SuperPointName3);
                    break;
                case 4:
                    setSuperPoint(SuperPointName4);
                    break;
            }
            AddTimerEx(1001, 60000);
            nodeArrived();
        }, 5000L);
    }

    private void nodeArrived() {
        if (i_ai2 < 1) {
            if (getSuperPoint() != null) {
                getActor().unblock();
                getActor().setInvisibleType(InvisibleType.NONE);
                i_ai2 = 1;
                ChatUtils.shout(getActor(), NpcString.WHO_S_THERE_IF_YOU_DISTURB_THE_TEMPER_OF_THE_GREAT_LAND_DRAGON_ANTHARAS_I_WILL_NEVER_FORGIVE_YOU);
            } else {
                LOGGER.warn("Not find current superPoint. Check him!");
                sendScriptEvent(ScriptEvent.SCE_WATCHER_DEAD, getActor().getLeader() == null ? 0 : getActor().getLeader().getObjectId(), i_ai0, null);
                getActor().deleteMe();
            }
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 1001 && getActor() != null && !getActor().isDead()) {
            if (i_ai1 == 1 && !getActor().isMoving() && !getActor().isBlocked() && !getActor().isAttackingNow() && !getActor().isCastingNow()) {
                if (getActor().getX() == i_ai4 && getActor().getY() == i_ai5 && getActor().getZ() == i_ai6) {
                    sendScriptEvent(ScriptEvent.SCE_WATCHER_DEAD, getActor().getLeader() == null ? 0 : getActor().getLeader().getObjectId(), i_ai0, null);
                    getActor().deleteMe();
                }
            }
            i_ai4 = getActor().getX();
            i_ai5 = getActor().getY();
            i_ai6 = getActor().getZ();
            AddTimerEx(1001, 60000);
        }
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        final NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null) {
            return;
        }

        switch (skill.getId()) {
            case 92:
                actor.getEffectList().stopEffect(92);
                break;
            case 1394:
                actor.getEffectList().stopEffect(1394);
                break;
            case 279:
                actor.getEffectList().stopEffect(279);
                break;
            case 367:
                actor.getEffectList().stopEffect(367);
                break;
            case 1169:
                actor.getEffectList().stopEffect(1169);
                break;
            case 1201:
                actor.getEffectList().stopEffect(1201);
                break;
            case 403:
                actor.getEffectList().stopEffect(403);
                break;
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (attacker.isPlayer() && !attacker.isDead()) {
            if (attacker.getPlayer().getParty() != null) {
                final int attack = Rnd.get(100);
                if (attack < 10) {
                    attacker.getPlayer().getParty().getPartyMembers().stream().filter(player -> player.isMageClass() && player.isInRange(getActor(), 1500)).forEach(player -> {
                        getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(6743, 1));
                    });
                } else if (attack < 5) {
                    NpcUtils.spawnSingleStablePoint(guardian_helper, getActor().getX() + 50, getActor().getY() + 50, getActor().getZ(), 3 * 60 * 1000);
                    NpcUtils.spawnSingleStablePoint(guardian_helper, getActor().getX() + 20, getActor().getY() + 20, getActor().getZ(), 3 * 60 * 1000);
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    public boolean checkAggression(Creature target) {
        final NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible() && i_ai2 > 0) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            i_ai1 = 1;
            return true;
        }
        return false;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        sendScriptEvent(ScriptEvent.SCE_WATCHER_DEAD, getActor().getLeader() == null ? 0 : getActor().getLeader().getObjectId(), i_ai0, null);
        super.onEvtDead(killer);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }

    @Override
    protected void teleportHome() {
    }
}