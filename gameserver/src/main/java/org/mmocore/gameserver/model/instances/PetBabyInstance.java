package org.mmocore.gameserver.model.instances;

import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.EffectList;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public final class PetBabyInstance extends PetInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetBabyInstance.class);
    // heal
    private static final int HealTrick = 4717;
    private static final int GreaterHealTrick = 4718;
    private static final int GreaterHeal = 5195;
    private static final int BattleHeal = 5590;
    private static final int Recharge = 5200;
    private static final int Pet_Haste = 5186; // 1-2
    private static final int Pet_Vampiric_Rage = 5187; // 1-4
    @SuppressWarnings("unused")
    private static final int Pet_Regeneration = 5188; // 1-3
    private static final int Pet_Blessed_Body = 5189; // 1-6
    private static final int Pet_Blessed_Soul = 5190; // 1-6
    private static final int Pet_Guidance = 5191; // 1-3
    @SuppressWarnings("unused")
    private static final int Pet_Wind_Walk = 5192; // 1-2
    private static final int Pet_Acumen = 5193; // 1-3
    private static final int Pet_Empower = 5194; // 1-3
    private static final int Pet_Concentration = 5201; // 1-3
    private static final int Pet_Might = 5586; // 1-3
    private static final int Pet_Shield = 5587; // 1-3
    private static final int Pet_Focus = 5588; // 1-3
    private static final int Pet_Death_Wisper = 5589; // 1-3
    // debuff (unused)
    @SuppressWarnings("unused")
    private static final int WindShackle = 5196, Hex = 5197, Slow = 5198, CurseGloom = 5199;
    private static final SkillEntry[][] COUGAR_BUFFS = {
            {SkillTable.getInstance().getSkillEntry(Pet_Empower, 3), SkillTable.getInstance().getSkillEntry(Pet_Might, 3)},
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Haste, 2)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Haste, 2),
                    SkillTable.getInstance().getSkillEntry(Pet_Vampiric_Rage, 4),
                    SkillTable.getInstance().getSkillEntry(Pet_Focus, 3)
            }
    };
    private static final SkillEntry[][] BUFFALO_BUFFS = {
            {SkillTable.getInstance().getSkillEntry(Pet_Might, 3), SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6)},
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Guidance, 3),
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Guidance, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Vampiric_Rage, 4),
                    SkillTable.getInstance().getSkillEntry(Pet_Haste, 2)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Might, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Guidance, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Vampiric_Rage, 4),
                    SkillTable.getInstance().getSkillEntry(Pet_Haste, 2),
                    SkillTable.getInstance().getSkillEntry(Pet_Focus, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Death_Wisper, 3)
            }
    };
    private static final SkillEntry[][] KOOKABURRA_BUFFS = {
            {SkillTable.getInstance().getSkillEntry(Pet_Empower, 3), SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6)},
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Concentration, 6)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Concentration, 6)
            }
    };
    private static final SkillEntry[][] FAIRY_PRINCESS_BUFFS = {
            {SkillTable.getInstance().getSkillEntry(Pet_Empower, 3), SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6)},
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Concentration, 6)
            },
            {
                    SkillTable.getInstance().getSkillEntry(Pet_Empower, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Soul, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Blessed_Body, 6),
                    SkillTable.getInstance().getSkillEntry(Pet_Shield, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Acumen, 3),
                    SkillTable.getInstance().getSkillEntry(Pet_Concentration, 6)
            }
    };
    private volatile Future<?> actionTask;
    private boolean buffEnabled = true;
    public PetBabyInstance(final int objectId, final NpcTemplate template, final Player owner, final ItemInstance control, final long exp) {
        super(objectId, template, owner, control, exp);
    }
    public PetBabyInstance(final int objectId, final NpcTemplate template, final Player owner, final ItemInstance control) {
        super(objectId, template, owner, control);
    }

    public SkillEntry[] getBuffs() {
        switch (getNpcId()) {
            case PetId.IMPROVED_BABY_COUGAR_ID:
                return COUGAR_BUFFS[getBuffLevel()];
            case PetId.IMPROVED_BABY_BUFFALO_ID:
                return BUFFALO_BUFFS[getBuffLevel()];
            case PetId.IMPROVED_BABY_KOOKABURRA_ID:
                return KOOKABURRA_BUFFS[getBuffLevel()];
            case PetId.FAIRY_PRINCESS_ID:
                return FAIRY_PRINCESS_BUFFS[getBuffLevel()];
            default:
                return SkillEntry.EMPTY_ARRAY;
        }
    }

    public SkillEntry onActionTask() {
        try {
            final Player owner = getPlayer();
            if (!owner.isDead() && !owner.isInvul() && !isCastingNow()) {
                if (getEffectList().getEffectsCountForSkill(5753) > 0) // Awakening
                {
                    return null;
                }

                final boolean improved = PetUtils.isImprovedBabyPet(getNpcId());
                SkillEntry skill = null;

                if (!AllSettingsConfig.ALT_PET_HEAL_BATTLE_ONLY || owner.isInCombat()) {
                    // проверка лечения
                    final double curHp = owner.getCurrentHpPercents();
                    if (curHp < 90 && Rnd.chance((100 - curHp) / 3)) {
                        if (curHp < 33) // экстренная ситуация, сильный хил
                        {
                            skill = SkillTable.getInstance().getSkillEntry(improved ? BattleHeal : GreaterHealTrick, getHealLevel());
                        } else if (getNpcId() != PetId.IMPROVED_BABY_KOOKABURRA_ID) {
                            skill = SkillTable.getInstance().getSkillEntry(improved ? GreaterHeal : HealTrick, getHealLevel());
                        }
                    }

                    // проверка речарджа
                    if (skill == null && getNpcId() == PetId.IMPROVED_BABY_KOOKABURRA_ID) // Речардж только у Kookaburra и в комбат моде
                    {
                        final double curMp = owner.getCurrentMpPercents();
                        if (curMp < 66 && Rnd.chance((100 - curMp) / 2)) {
                            skill = SkillTable.getInstance().getSkillEntry(Recharge, getRechargeLevel());
                        }
                    }

                    if (skill != null && !isSkillDisabled(skill) && skill.checkCondition(this, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().Cast(skill, owner, false, !isFollowMode());
                        return skill;
                    }
                }

                if (!improved || owner.isInOfflineMode() || getEffectList().getEffectsCountForSkill(5771) > 0) {
                    return null;
                }

                outer:
                for (final SkillEntry buff : getBuffs()) {
                    if (getCurrentMp() < buff.getTemplate().getMpConsume2()) {
                        continue;
                    }

                    for (final Effect e : owner.getEffectList().getAllEffects()) {
                        if (checkEffect(e, buff.getTemplate())) {
                            continue outer;
                        }
                    }

                    if (!isSkillDisabled(buff) && buff.checkCondition(this, owner, false, !isFollowMode(), true)) {
                        setTarget(owner);
                        getAI().Cast(buff, owner, false, !isFollowMode());
                        return buff;
                    }
                    return null;
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("Pet [#" + getNpcId() + "] a buff task error has occurred", e);
        }

        return null;
    }

    /**
     * Возвращает true если эффект для скилла уже есть и заново накладывать не надо
     */
    private boolean checkEffect(final Effect ef, final Skill skill) {
        if (ef == null || !ef.isInUse() || !EffectList.checkStackType(ef.getTemplate(), skill.getEffectTemplates()[0])) // такого скилла нет
        {
            return false;
        }
        if (ef.getStackOrder() < skill.getEffectTemplates()[0]._stackOrder) // старый слабее
        {
            return false;
        }
        if (ef.getTimeLeft() > 10) // старый не слабее и еще не кончается - ждем
        {
            return true;
        }
        if (ef.getNext() != null) // старый не слабее но уже кончается - проверить рекурсией что там зашедулено
        {
            return checkEffect(ef.getNext(), skill);
        }
        return false;
    }

    public void stopBuffTask() {
        if (actionTask != null) {
            actionTask.cancel(false);
            actionTask = null;
        }
    }

    public void startBuffTask() {
        stopBuffTask();

        if (actionTask == null && !isDead())
            actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 5000);
    }

    public boolean isBuffEnabled() {
        return buffEnabled;
    }

    public void triggerBuff() {
        buffEnabled = !buffEnabled;
    }

    @Override
    protected void onDeath(final Creature killer) {
        stopBuffTask();
        super.onDeath(killer);
    }

    @Override
    public void doRevive() {
        super.doRevive();
        startBuffTask();
    }

    @Override
    public void unSummon(final boolean saveEffects, final boolean store) {
        stopBuffTask();
        super.unSummon(saveEffects, store);
    }

    public int getHealLevel() {
        return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 12), 1), 12);
    }

    public int getRechargeLevel() {
        return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 8), 1), 8);
    }

    public int getBuffLevel() {
        if (getNpcId() == PetId.FAIRY_PRINCESS_ID) {
            return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 3), 0), 3);
        }
        return Math.min(Math.max((getLevel() - 55) / 5, 0), 3);
    }

    @Override
    public int getSoulshotConsumeCount() {
        return 1;
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return 1;
    }

    class ActionTask extends RunnableImpl {
        @Override
        public void runImpl() {
            final SkillEntry skill = onActionTask();
            actionTask = ThreadPoolManager.getInstance().schedule(
                    new ActionTask(), skill == null ? 1000 : skill.getTemplate().getHitTime() * 333 / Math.max(getMAtkSpd(), 1) - 100);
        }
    }
}