package org.mmocore.gameserver.scripts.ai.plains_of_the_lizardmen;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author PaInKiLlEr
 * @company J Develop Station - AI для соло-рб Seer Ugoros (18863). - При спавне кричит в чат. - Кричит в чат каждые 5 минут на всю локацию. - Когда
 * ХП меньше 50% кричит в чат, и есть шанс что отрегенит и закричит в чат. - Если убить цветок в зоне РБ, кричит в чат. - Если игрок умер,
 * кричит в чат. - Если увидел игрока, кричит в чат а потом атакует. - При смерти кричит в чат, спавнит нпц Batracos (32740). - При смерти
 * запускает таймер 3 минуты, если игрок не вышел из зоны через нпц то телепортирует игрока и удаляет нпц Batracos. - AI проверен и работает.
 */
public class SeerUgoros extends Mystic {
    private static final int AbyssWeed = 18867;
    private static final int Batracos = 32740;
    private static int StoredAbyssWeedId;
    private boolean firstTimeAttacked1 = true;
    private boolean firstTimeAttacked2 = true;
    private boolean firstTimeAttacked3 = true;
    private boolean firstTimeAttacked4 = true;
    private boolean firstTimeAttacked5 = true;
    private boolean abyss = false;
    private ScheduledFuture<?> _task = null;

    public SeerUgoros(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        firstTimeAttacked1 = true;
        firstTimeAttacked2 = true;
        firstTimeAttacked3 = true;
        firstTimeAttacked4 = true;
        firstTimeAttacked5 = true;

        if (_task == null) {
            _task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    final NpcInstance actor = getActor();
                    if (actor == null || actor.isDead()) {
                        return;
                    }

                    ChatUtils.shout(actor, NpcString.LISTEN_OH_TANTAS_THE_BLACK_ABYSS_IS_FAMISHED_FIND_SOME_FRESH_OFFERING);
                }
            }, 300000, 300000);
        }
    }

    @Override
    protected void thinkAttack() {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        final Creature MostHated = actor.getAggroList().getMostHated();
        if (MostHated != null && MostHated instanceof Player) {
            if (MostHated.getCurrentHpPercents() <= 5 && firstTimeAttacked4) {
                firstTimeAttacked4 = false;
                Functions.npcSay(actor, NpcString.A_LIMP_CREATURE_LIKE_THIS_IS_UNWORTHY_TO_BE_AN_OFFERING_YOU_HAVE_NO_RIGHT_TO_SACRIFICE_TO_THE_BLACK_ABYSS);
            }
        }

        final List<NpcInstance> around = World.getAroundNpc(actor, 1000, 300);
        for (final NpcInstance npc : around) {
            if (npc.getNpcId() == AbyssWeed && npc.isDead() && firstTimeAttacked5) {
                firstTimeAttacked5 = false;
                Functions.npcSay(actor, NpcString.NO_HOW_DARE_YOU_STOP_ME_FROM_USING_THE_ABYSS_WEED_DO_YOU_KNOW_WHAT_YOU_HAVE_DONE);
                abyss = true;
            }
        }

        super.thinkAttack();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        final List<NpcInstance> around = World.getAroundNpc(actor, 1000, 300);
        if (actor.getCurrentHpPercents() <= 75 && firstTimeAttacked1) {
            firstTimeAttacked1 = false;
            Functions.npcSay(actor, NpcString.WHAT_A_FORMIDABLE_FOR_BUT_I_HAVE_THE_ABYSS_WEED_GIVEN_TO_ME_BY_THE_BLACK_ABYSS_LET_ME_SEE);
            for (final NpcInstance npc : around) {
                if (npc == null || npc.isDead()) {
                    continue;
                }

                // Удаляем все задания
                clearTasks();
                actor.abortAttack(true, false);
                actor.abortCast(true, false);
                if (npc.getNpcId() == AbyssWeed) {
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
                    npc.getAggroList().addDamageHate(actor, 10, 10);
                    StoredAbyssWeedId = npc.getObjectId();
                    ThreadPoolManager.getInstance().schedule(new DodieAbyssWeed(), 3000);
                }
            }
        } else if (actor.getCurrentHpPercents() <= 50 && firstTimeAttacked2) {
            firstTimeAttacked2 = false;
            Functions.npcSay(actor, NpcString.WHAT_A_FORMIDABLE_FOR_BUT_I_HAVE_THE_ABYSS_WEED_GIVEN_TO_ME_BY_THE_BLACK_ABYSS_LET_ME_SEE);
            for (final NpcInstance npc : around) {
                if (npc == null || npc.isDead()) {
                    continue;
                }

                // Удаляем все задания
                clearTasks();
                actor.abortAttack(true, false);
                actor.abortCast(true, false);
                if (npc.getNpcId() == AbyssWeed) {
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
                    npc.getAggroList().addDamageHate(actor, 10, 10);
                    StoredAbyssWeedId = npc.getObjectId();
                    ThreadPoolManager.getInstance().schedule(new DodieAbyssWeed(), 3000);
                }
            }
        } else if (actor.getCurrentHpPercents() <= 25 && firstTimeAttacked3) {
            firstTimeAttacked3 = false;
            Functions.npcSay(actor, NpcString.WHAT_A_FORMIDABLE_FOR_BUT_I_HAVE_THE_ABYSS_WEED_GIVEN_TO_ME_BY_THE_BLACK_ABYSS_LET_ME_SEE);
            for (final NpcInstance npc : around) {
                if (npc == null || npc.isDead()) {
                    continue;
                }

                // Удаляем все задания
                clearTasks();
                actor.abortAttack(true, false);
                actor.abortCast(true, false);
                if (npc.getNpcId() == AbyssWeed) {
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
                    npc.getAggroList().addDamageHate(actor, 10, 10);
                    StoredAbyssWeedId = npc.getObjectId();
                    ThreadPoolManager.getInstance().schedule(new DodieAbyssWeed(), 3000);
                }
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50)) {
            ChatUtils.shout(actor, NpcString.WELCONE_LET_US_SEE_IF_YOU_HAVE_BROUGHT_A_WORTHY_OFFERING_FOR_THE_BLACK_ABBYSS);
        }
        super.onIntentionAttack(target);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();

        if (_task != null) {
            _task.cancel(false);
            _task = null;
        }

        NpcUtils.spawnSingle(Batracos, Location.findPointToStay(actor, 250, 300), actor.getReflection());
        actor.getReflection().startCollapseTimer(180000L);

        if (killer.isPlayable()) {
            final QuestState qs = killer.getPlayer().getQuestState(288);
            if (qs != null && qs.getCond() == 1) {
                if (abyss) {
                    qs.giveItems(15497, 1);
                    qs.setCond(3);
                } else {
                    qs.giveItems(15498, 1);
                    qs.setCond(2);
                }
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    private class DodieAbyssWeed extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            if (actor == null || actor.isDead()) {
                return;
            }

            final NpcInstance npc = GameObjectsStorage.getNpc(StoredAbyssWeedId);
            npc.doDie(actor);
            actor.doCast(SkillTable.getInstance().getSkillEntry(6648, 1), actor, true); //TODO[K] - реализация скила!
            Functions.npcSay(actor, NpcString.MUHAHAHA_AH_THIS_BURNING_SENSATION_IN_MY_MOUTH_THE_POWER_OF_THE_BLACK_ABYSS_IS_REVIVING_ME);
        }
    }
}