package org.mmocore.gameserver.scripts.ai;

import gnu.trove.map.TIntObjectMap;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Diamond
 */
public class Baylor extends DefaultAI {
    static final int[] cleric_group = {16, 30, 43, 97, 105, 112};
    private static final int Water_Dragon_Claw = 2360;
    final SkillEntry Berserk; // Increases P. Atk. and P. Def.
    final SkillEntry Invincible; // Неуязвимость при 30% hp
    final SkillEntry Imprison; // Помещает одиночную цель в тюрьму, рейндж 600
    final SkillEntry GroundStrike; // Массовая атака, 2500 каст
    final SkillEntry JumpAttack; // Массовая атака, 2500 каст
    final SkillEntry StrongPunch; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
    final SkillEntry Stun1; // Массовое оглушение, 5000 каст
    //final L2Skill Stun4; // Не работает?
    final SkillEntry Stun2; // Массовое оглушение, 3000 каст
    final SkillEntry Stun3; // Массовое оглушение, 2000 каст
    final int PresentationBalor2 = 5402; // Прыжок, удар по земле
    final int PresentationBalor3 = 5403; // Электрическая аура
    final int PresentationBalor4 = 5404; // Электрическая аура, в конце сияние
    final int PresentationBalor10 = 5410; // Не работает?
    final int PresentationBalor11 = 5411; // Не работает?
    final int PresentationBalor12 = 5412; // Массовый удар
    private boolean _isUsedInvincible = false;
    private boolean isUsedTeleport = false;
    private int _claw_count = 0;
    private long _last_claw_time = 0;

    public Baylor(NpcInstance actor) {
        super(actor);

        TIntObjectMap<SkillEntry> skills = getActor().getTemplate().getSkills();

        Berserk = skills.get(5224);
        Invincible = skills.get(5225);
        Imprison = skills.get(5226);
        GroundStrike = skills.get(5227);
        JumpAttack = skills.get(5228);
        StrongPunch = skills.get(5229);
        Stun1 = skills.get(5230);
        Stun2 = skills.get(5231);
        Stun3 = skills.get(5232);
        //Stun4 = skills.get(5401);
    }

    @Override
    protected void onEvtSpawn() {
        ThreadPoolManager.getInstance().schedule(new SpawnSocial(), 20000);
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        super.onEvtSeeSpell(skill, caster);

        NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null) {
            return;
        }

        if (System.currentTimeMillis() - _last_claw_time > 5000) {
            _claw_count = 0;
        }

        if (skill.getId() == Water_Dragon_Claw) {
            _claw_count++;
            _last_claw_time = System.currentTimeMillis();
        }

        Player player = caster.getPlayer();
        if (player == null) {
            return;
        }

        int count = 1;
        Party party = player.getParty();
        if (party != null) {
            count = party.getMemberCount();
        }

        // Снимаем неуязвимость
        if (_claw_count >= count) {
            _claw_count = 0;
            actor.getEffectList().stopEffect(Invincible);
            ChatUtils.shout(actor, NpcString.NO_I_FEEL_THE_POWER_OF_FAFURION);
        }
    }

    @Override
    protected boolean createNewTask() {
        clearTasks();
        Creature target;
        if ((target = prepareTarget()) == null) {
            return false;
        }

        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return false;
        }

        double distance = actor.getDistance(target);
        double actor_hp_precent = actor.getCurrentHpPercents();

        if (actor_hp_precent < 30 && !_isUsedInvincible) {
            _isUsedInvincible = true;
            addTaskBuff(actor, Invincible);
            final int rnd = Rnd.get(100);
            if (rnd < 33)
                ChatUtils.shout(actor, NpcString.BEHOLD_THE_MIGHTY_POWER_OF_BAYLOR_FOOLISH_MORTAL);
            else if (rnd < 66)
                ChatUtils.shout(actor, NpcString.NO_ONE_IS_GOING_TO_SURVIVE);
            else
                ChatUtils.shout(actor, NpcString.YOU_LL_SEE_WHAT_HELL_IS_LIKE);
            return true;
        }

        if (!isUsedTeleport) {
            isUsedTeleport = true;
            addTaskBuff(actor, Imprison);
            final int rnd = Rnd.get(100);
            if (rnd < 33)
                ChatUtils.shout(actor, NpcString.YOU_WILL_BE_PUT_IN_JAIL);
            else if (rnd < 66)
                ChatUtils.shout(actor, NpcString.WORTLESS_CREATURE_GO_TO_HELL);
            else
                ChatUtils.shout(actor, NpcString.I_LL_GIVE_YOU_SOMETHING_THAT_YOU_LL_NEVER_FORGET);
            ThreadPoolManager.getInstance().schedule(() -> {
                boolean teleported = false;
                for (final Player players : getActor().getReflection().getPlayers()) {
                    if (!teleported && ArrayUtils.contains(cleric_group, players.getPlayerClassComponent().getClassId().getId())) {
                        teleported = true;
                        players.teleToLocation(155097, 142934, -12704);
                    }
                }
            }, 1000L);
            return true;
        }

        int rnd_per = Rnd.get(100);
        if (rnd_per < 7 && actor.getEffectList().getEffectsBySkill(Berserk) == null) {
            addTaskBuff(actor, Berserk);
            ChatUtils.shout(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH);
            return true;
        }

        if (rnd_per < 15 || rnd_per < 33 && actor.getEffectList().getEffectsBySkill(Berserk) != null) {
            return chooseTaskAndTargets(StrongPunch, target, distance);
        }

        if (!actor.isAMuted() && rnd_per < 50) {
            return chooseTaskAndTargets(null, target, distance);
        }

        Map<SkillEntry, Integer> skills = new HashMap<SkillEntry, Integer>();

        addDesiredSkill(skills, target, distance, GroundStrike);
        addDesiredSkill(skills, target, distance, JumpAttack);
        addDesiredSkill(skills, target, distance, StrongPunch);
        addDesiredSkill(skills, target, distance, Stun1);
        addDesiredSkill(skills, target, distance, Stun2);
        addDesiredSkill(skills, target, distance, Stun3);

        SkillEntry skill = selectTopSkill(skills);
        if (skill != null && !skill.getTemplate().isOffensive()) {
            target = actor;
        }

        return chooseTaskAndTargets(skill, target, distance);
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        getActor().getReflection().setReenterTime(System.currentTimeMillis());
        super.onEvtDead(killer);
    }

    private class SpawnSocial extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor != null) {
                actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, PresentationBalor2, 1, 4000, 0));
            }
        }
    }
}