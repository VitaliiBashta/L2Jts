package org.mmocore.gameserver.object.components.player;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.manager.games.FishingChampionShipManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.support.*;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс отвечающий за рыбную ловлю
 *
 * @author G1ta0 (переработка)
 */
public class Fishing {
    public static final int FISHING_NONE = 0;
    public static final int FISHING_STARTED = 1;
    public static final int FISHING_WAITING = 2;
    public static final int FISHING_COMBAT = 3;
    private final Player _fisher;
    private final AtomicInteger _state;
    private final Location _fishLoc = new Location();
    private int _time;
    private int _stop;
    private int _gooduse;
    private int _anim;
    private int _combatMode = -1;
    private int _deceptiveMode;
    private int _fishCurHP;
    private FishTemplate _fish;
    private LureTemplate _lureId;
    private Future<?> _fishingTask;

    public Fishing(final Player fisher) {
        _fisher = fisher;
        _state = new AtomicInteger(FISHING_NONE);
    }

    private static void showMessage(final Player fisher, final int dmg, final int pen, final SkillType skillType, final int messageId) {
        switch (messageId) {
            case 1:
                if (skillType == SkillType.PUMPING) {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_PUMPING_IS_SUCCESSFUL_CAUSING_S1_DAMAGE).addNumber(dmg));
                    if (pen == 50) {
                        fisher.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
                        fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTY_S1).addNumber(pen));
                    }
                } else {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOU_REEL_THAT_FISH_IN_CLOSER_AND_CAUSE_S1_DAMAGE).addNumber(dmg));
                    if (pen == 50) {
                        fisher.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
                        fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTY_S1).addNumber(pen));
                    }
                }
                break;
            case 2:
                if (skillType == SkillType.PUMPING) {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOU_FAILED_TO_DO_ANYTHING_WITH_THE_FISH_AND_IT_REGAINS_S1_HP).addNumber(dmg));
                } else {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOU_FAILED_TO_REEL_THAT_FISH_IN_FURTHER_AND_IT_REGAINS_S1_HP).addNumber(dmg));
                }
                break;
            case 3:
                if (skillType == SkillType.PUMPING) {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_PUMPING_IS_SUCCESSFUL_CAUSING_S1_DAMAGE).addNumber(dmg));
                    if (pen == 50) {
                        fisher.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
                        fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTY_S1).addNumber(pen));
                    }
                } else {
                    fisher.sendPacket(new SystemMessage(SystemMsg.YOU_REEL_THAT_FISH_IN_CLOSER_AND_CAUSE_S1_DAMAGE).addNumber(dmg));
                    if (pen == 50) {
                        fisher.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
                        fisher.sendPacket(new SystemMessage(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTY_S1).addNumber(pen));
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void spawnPenaltyMonster(final Player fisher) {
        final int npcId = 18319 + Math.min(fisher.getLevel() / 11, 7); // 18319-18326

        final MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(npcId));
        npc.setSpawnedLoc(Location.findPointToStay(fisher, 100, 120));
        npc.setReflection(fisher.getReflection());
        npc.setHeading(fisher.getHeading() - 32768);
        npc.spawnMe(npc.getSpawnedLoc());
        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, fisher, Rnd.get(1, 100));
    }

    public static int getRandomFishLvl(final Player player) {
        int skilllvl = 0;

        // Проверка на Fisherman's Potion
        final Effect effect = player.getEffectList().getEffectByStackType("fishPot");
        if (effect != null) {
            skilllvl = (int) effect.getSkill().getTemplate().getPower();
        } else {
            skilllvl = player.getSkillLevel(1315);
        }

        if (skilllvl <= 0) {
            return 1;
        }

        int randomlvl;
        final int check = Rnd.get(100);

        if (check < 50) {
            randomlvl = skilllvl;
        } else if (check <= 85) {
            randomlvl = skilllvl - 1;
            if (randomlvl <= 0) {
                randomlvl = 1;
            }
        } else {
            randomlvl = skilllvl + 1;
        }

        randomlvl = Math.min(27, Math.max(1, randomlvl));

        return randomlvl;
    }

    public static FishGroup getRandomFishType(FishGrade grade, int lureId) {
        int check = Rnd.get(100);
        FishGroup type = FishGroup.WIDE;
        switch (grade) {
            case EASY: // fish for novices
                switch (lureId) {
                    case 7807: // green lure, preferred by fast-moving (nimble)
                        // fish (type 5)
                        if (check <= 54) {
                            type = FishGroup.EASY_SWIFT;
                        } else if (check <= 77) {
                            type = FishGroup.EASY_WIDE;
                        } else {
                            type = FishGroup.EASY_UGLY;
                        }
                        break;
                    case 7808: // purple lure, preferred by fat fish (type 4)
                        if (check <= 54) {
                            type = FishGroup.EASY_WIDE;
                        } else if (check <= 77) {
                            type = FishGroup.EASY_UGLY;
                        } else {
                            type = FishGroup.EASY_SWIFT;
                        }
                        break;
                    case 7809: // yellow lure, preferred by ugly fish (type 6)
                        if (check <= 54) {
                            type = FishGroup.EASY_UGLY;
                        } else if (check <= 77) {
                            type = FishGroup.EASY_SWIFT;
                        } else {
                            type = FishGroup.EASY_WIDE;
                        }
                        break;
                    case 8486: // prize-winning fishing lure for beginners
                        if (check <= 33) {
                            type = FishGroup.EASY_WIDE;
                        } else if (check <= 66) {
                            type = FishGroup.EASY_SWIFT;
                        } else {
                            type = FishGroup.EASY_UGLY;
                        }
                        break;
                }
                break;
            case NORMAL: // normal fish
                switch (lureId) {
                    case 7610:
                    case 7611:
                    case 7612:
                    case 7613:
                        type = FishGroup.BOX;
                        break;
                    case 6519: // all theese lures (green) are prefered by
                        // fast-moving (nimble) fish (type 1)
                    case 8505:
                    case 6520:
                    case 6521:
                    case 8507:
                        if (check <= 54) {
                            type = FishGroup.SWIFT;
                        } else if (check <= 74) {
                            type = FishGroup.WIDE;
                        } else if (check <= 94) {
                            type = FishGroup.UGLY;
                        } else {
                            type = FishGroup.BOX;
                        }
                        break;
                    case 6522: // all theese lures (purple) are prefered by fat
                        // fish (type 0)
                    case 8508:
                    case 6523:
                    case 6524:
                    case 8510:
                        if (check <= 54) {
                            type = FishGroup.WIDE;
                        } else if (check <= 74) {
                            type = FishGroup.SWIFT;
                        } else if (check <= 94) {
                            type = FishGroup.UGLY;
                        } else {
                            type = FishGroup.BOX;
                        }
                        break;
                    case 6525: // all theese lures (yellow) are prefered by ugly
                        // fish (type 2)
                    case 8511:
                    case 6526:
                    case 6527:
                    case 8513:
                        if (check <= 55) {
                            type = FishGroup.UGLY;
                        } else if (check <= 74) {
                            type = FishGroup.SWIFT;
                        } else if (check <= 94) {
                            type = FishGroup.WIDE;
                        } else {
                            type = FishGroup.BOX;
                        }
                        break;
                    case 8484: // prize-winning fishing lure
                        if (check <= 33) {
                            type = FishGroup.WIDE;
                        } else if (check <= 66) {
                            type = FishGroup.SWIFT;
                        } else {
                            type = FishGroup.UGLY;
                        }
                        break;
                }
                break;
            case HARD: // upper grade fish, luminous lure
                switch (lureId) {
                    case 8506: // green lure, preferred by fast-moving (nimble)
                        // fish (type 8)
                        if (check <= 54) {
                            type = FishGroup.HARD_SWIFT;
                        } else if (check <= 77) {
                            type = FishGroup.HARD_WIDE;
                        } else {
                            type = FishGroup.HARD_UGLY;
                        }
                        break;
                    case 8509: // purple lure, preferred by fat fish (type 7)
                        if (check <= 54) {
                            type = FishGroup.HARD_WIDE;
                        } else if (check <= 77) {
                            type = FishGroup.HARD_UGLY;
                        } else {
                            type = FishGroup.HARD_SWIFT;
                        }
                        break;
                    case 8512: // yellow lure, preferred by ugly fish (type 9)
                        if (check <= 54) {
                            type = FishGroup.HARD_UGLY;
                        } else if (check <= 77) {
                            type = FishGroup.HARD_SWIFT;
                        } else {
                            type = FishGroup.HARD_WIDE;
                        }
                        break;
                    case 8485: // prize-winning fishing lure
                        if (check <= 33) {
                            type = FishGroup.HARD_WIDE;
                        } else if (check <= 66) {
                            type = FishGroup.HARD_SWIFT;
                        } else {
                            type = FishGroup.HARD_UGLY;
                        }
                        break;
                }
        }
        return type;
    }

    public static FishGrade getGroupForLure(int lureId) {
        switch (lureId) {
            case 7807: // green for beginners
            case 7808: // purple for beginners
            case 7809: // yellow for beginners
            case 8486: // prize-winning for beginners
                return FishGrade.EASY;
            case 8506: // green luminous
            case 8509: // purple luminous
            case 8512: // yellow luminous
            case 8485: // prize-winning luminous
                return FishGrade.HARD;
            default:
                return FishGrade.NORMAL;
        }
    }

    public static FishGrade getLureGrade(int lureId) {
        switch (lureId) {
            case 6519:
            case 6522:
            case 6525:
            case 8505:
            case 8508:
            case 8511:
                return FishGrade.EASY;
            case 6521:
            case 6524:
            case 6527:
            case 8507:
            case 8510:
            case 8513:
                return FishGrade.HARD;
            case 6520:
            case 6523:
            case 6526:
            case 7610:
            case 7611:
            case 7612:
            case 7613:
            case 7807:
            case 7808:
            case 7809:
            case 8484:
            case 8485:
            case 8486:
            case 8496:
            case 8497:
            case 8498:
            case 8499:
            case 8500:
            case 8501:
            case 8502:
            case 8503:
            case 8504:
            case 8506:
            case 8509:
            case 8512:
            case 8548:
                return FishGrade.NORMAL;
            default:
                return FishGrade.NORMAL;
        }

    }

    public void setFish(final FishTemplate fish) {
        _fish = fish;
    }

    public LureTemplate getLureId() {
        return _lureId;
    }

    public void setLureId(final LureTemplate lureId) {
        _lureId = lureId;
    }

    public Location getFishLoc() {
        return _fishLoc;
    }

    public void setFishLoc(final Location loc) {
        _fishLoc.x = loc.x;
        _fishLoc.y = loc.y;
        _fishLoc.z = loc.z;
    }

    /**
     * Начинаем рыбалку, запускаем задачу ожидания рыбешки
     */
    public void startFishing() {
        if (!_state.compareAndSet(FISHING_NONE, FISHING_STARTED)) {
            return;
        }

        _fisher.setFishing(true);
        _fisher.broadcastCharInfo();
        _fisher.broadcastPacket(new ExFishingStart(_fisher, _fish.getGroup(), _fisher.getFishLoc(), _lureId.getLureType()));
        _fisher.sendPacket(SystemMsg.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);

        startLookingForFishTask();
    }

    /**
     * Отменяем рыбалку, завершаем текущую задачу
     */
    public void stopFishing() {
        if (_state.getAndSet(FISHING_NONE) == FISHING_NONE) {
            return;
        }

        stopFishingTask();

        _fisher.setFishing(false);
        _fisher.broadcastPacket(new ExFishingEnd(_fisher, false));
        _fisher.broadcastCharInfo();
        _fisher.sendPacket(SystemMsg.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
    }

    /**
     * Заканчиваем рыбалку, в случае удачи или неудачи, завершаем текущую задачу
     */
    public void endFishing(final boolean win) {
        if (!_state.compareAndSet(FISHING_COMBAT, FISHING_NONE)) {
            return;
        }

        stopFishingTask();

        _fisher.setFishing(false);
        _fisher.broadcastPacket(new ExFishingEnd(_fisher, win));
        _fisher.broadcastCharInfo();
        _fisher.sendPacket(SystemMsg.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
    }

    private void stopFishingTask() {
        if (_fishingTask != null) {
            _fishingTask.cancel(false);
            _fishingTask = null;
        }
    }

    private void startLookingForFishTask() {
        if (!_state.compareAndSet(FISHING_STARTED, FISHING_WAITING)) {
            return;
        }

        long checkDelay = 10000L;

        switch (_fish.getGrade()) {
            case EASY:
                checkDelay = Math.round(_fish.getGutsCheckTime() * 1.33);
                break;
            case NORMAL:
                checkDelay = _fish.getGutsCheckTime();
                break;
            case HARD:
                checkDelay = Math.round(_fish.getGutsCheckTime() * 0.66);
                break;
        }

        _fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LookingForFishTask(), 10000L, checkDelay);
    }

    public boolean isInCombat() {
        return _state.get() == FISHING_COMBAT;
    }

    private void startFishCombat() {
        if (!_state.compareAndSet(FISHING_WAITING, FISHING_COMBAT)) {
            return;
        }

        _stop = 0;
        _gooduse = 0;
        _anim = 0;
        _time = _fish.getCombatDuration();
        _fishCurHP = _fish.getHp();
        _combatMode = Rnd.chance(20) ? 1 : 0;
        switch (getLureGrade(_lureId.getItemId())) {
            case EASY:
            case NORMAL:
                _deceptiveMode = 0;
                break;
            case HARD:
                _deceptiveMode = Rnd.chance(10) ? 1 : 0;
        }
        final ExFishingStartCombat efsc = new ExFishingStartCombat(_fisher, _time, _fish.getHp(), _combatMode, _lureId.getLureType(), _deceptiveMode);
        _fisher.broadcastPacket(efsc);
        _fisher.sendPacket(SystemMsg.YOUVE_GOT_A_BITE);

        _fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FishCombatTask(), 1000L, 1000L);
    }

    private void changeHp(final int hp, final int pen) {
        _fishCurHP -= hp;
        if (_fishCurHP < 0) {
            _fishCurHP = 0;
        }

        _fisher.broadcastPacket(new ExFishingHpRegen(_fisher, _time, _fishCurHP, _combatMode, _gooduse, _anim, pen, _deceptiveMode));

        _gooduse = 0;
        _anim = 0;
        if (_fishCurHP > _fish.getHp() * 2) {
            _fishCurHP = _fish.getHp() * 2;
            doDie(false);
        } else if (_fishCurHP == 0) {
            doDie(true);
        }
    }

    private void doDie(boolean win) {
        stopFishingTask();
        if (win) {
            if (!_fisher.isInPeaceZone() && Rnd.chance(5)) {
                win = false;
                _fisher.sendPacket(SystemMsg.YOU_CAUGHT_SOMETHING_SMELLY_AND_SCARY_MAYBE_YOU_SHOULD_THROW_IT_BACK);
                spawnPenaltyMonster(_fisher);
            } else {
                _fisher.sendPacket(SystemMsg.YOU_CAUGHT_SOMETHING);
                final ItemInstance item = ItemFunctions.createItem(_fish.getItemId());
                if (_fisher.getWeightPenalty() >= 3 || _fisher.getInventoryLimit() * 0.8 < _fisher.getInventory().getSize()) {
                    _fisher.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    item.dropToTheGround(_fisher, _fisher.getLoc());
                } else {
                    _fisher.getInventory().addItem(item);
                    _fisher.sendPacket(SystemMessage.obtainItems(item));
                }
                FishingChampionShipManager.getInstance().newFish(_fisher, _lureId.getItemId());
            }
        }
        endFishing(win);
    }

    public void useFishingSkill(final int dmg, final int pen, final SkillType skillType) {
        if (!isInCombat()) {
            return;
        }

        final int mode;
        if (skillType == SkillType.REELING && !GameTimeManager.getInstance().isNowNight()) {
            mode = 1;
        } else if (skillType == SkillType.PUMPING && GameTimeManager.getInstance().isNowNight()) {
            mode = 1;
        } else {
            mode = 0;
        }

        _anim = mode + 1;
        if (Rnd.chance(10)) {
            _fisher.sendPacket(SystemMsg.THE_FISH_HAS_RESISTED_YOUR_ATTEMPT_TO_BRING_IT_IN);
            _gooduse = 0;
            changeHp(0, pen);
            return;
        }

        if (_combatMode == mode) {
            if (_deceptiveMode == 0) {
                showMessage(_fisher, dmg, pen, skillType, 1);
                _gooduse = 1;
                changeHp(dmg, pen);
            } else {
                showMessage(_fisher, dmg, pen, skillType, 2);
                _gooduse = 2;
                changeHp(-dmg, pen);
            }
        } else if (_deceptiveMode == 0) {
            showMessage(_fisher, dmg, pen, skillType, 2);
            _gooduse = 2;
            changeHp(-dmg, pen);
        } else {
            showMessage(_fisher, dmg, pen, skillType, 3);
            _gooduse = 1;
            changeHp(dmg, pen);
        }
    }

    /**
     * LookingForFishTask
     */
    protected class LookingForFishTask extends RunnableImpl {
        private final long _endTaskTime;

        protected LookingForFishTask() {
            _endTaskTime = System.currentTimeMillis() + _fish.getStartCombatTime() + 10000L;
        }

        @Override
        public void runImpl() {
            if (System.currentTimeMillis() >= _endTaskTime) {
                _fisher.sendPacket(SystemMsg.YOUR_BAIT_WAS_STOLEN_BY_THAT_FISH);
                stopFishingTask();
                endFishing(false);
                return;
            }

            if (!GameTimeManager.getInstance().isNowNight() && _lureId.getLureType().equals(LureType.NIGHT)) {
                _fisher.sendPacket(SystemMsg.YOUR_BAIT_WAS_STOLEN_BY_THAT_FISH);
                stopFishingTask();
                endFishing(false);
                return;
            }

            final int check = Rnd.get(1000);

            if (_fish.getGuts() > check) {
                stopFishingTask();
                startFishCombat();
            }
        }
    }

    private class FishCombatTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_fishCurHP >= _fish.getHp() * 2) {
                // The fish got away
                _fisher.sendPacket(SystemMsg.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
                doDie(false);
            } else if (_time <= 0) {
                // Time is up, so that fish got away
                _fisher.sendPacket(SystemMsg.THAT_FISH_IS_MORE_DETERMINED_THAN_YOU_ARE__IT_SPIT_THE_HOOK);
                doDie(false);
            } else {
                _time--;

                if (_combatMode == 1 && _deceptiveMode == 0 || _combatMode == 0 && _deceptiveMode == 1) {
                    _fishCurHP += _fish.getHpRegen();
                }

                if (_stop == 0) {
                    _stop = 1;
                    if (Rnd.chance(30)) {
                        _combatMode = _combatMode == 0 ? 1 : 0;
                    }

                    if (_fish.getGrade() == FishGrade.HARD) {
                        if (Rnd.chance(10)) {
                            _deceptiveMode = _deceptiveMode == 0 ? 1 : 0;
                        }
                    }
                } else {
                    _stop--;
                }

                final ExFishingHpRegen efhr = new ExFishingHpRegen(_fisher, _time, _fishCurHP, _combatMode, 0, _anim, 0, _deceptiveMode);
                if (_anim != 0) {
                    _fisher.broadcastPacket(efhr);
                } else {
                    _fisher.sendPacket(efhr);
                }
            }
        }
    }
}