package org.mmocore.gameserver.skills.skillclasses;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.BaseStats;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Resurrect extends Skill {
    public static final List<Event> GLOBAL = new ArrayList<>();

    private final boolean _canPet;

    public Resurrect(final StatsSet set) {
        super(set);
        _canPet = set.getBool("canPet", false);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove,
                                  final boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }

        if (target == null || !target.equals(activeChar) && !target.isDead()) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        final Player player = (Player) activeChar;
        final Player pcTarget = target.getPlayer();

        if (pcTarget == null) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        if (player.isInOlympiadMode() || pcTarget.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        if (oneTarget()) {
            final List<Event> events = new ArrayList<>(GLOBAL.size() + 2);
            events.addAll(GLOBAL);
            events.addAll(target.getZoneEvents());

            for (final Event e : events) {
                if (!e.canResurrect(activeChar, target, forceUse, false)) {
                    player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }
            }

            if (target.isPet()) {
                final Pair<Integer, OnAnswerListener> ask = pcTarget.getAskListener(false);
                final ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue()
                        : null;
                if (reviveAsk != null) {
                    if (reviveAsk.isForPet()) {
                        activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                    } else {
                        activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
                    }
                    return false;
                }
                if (!(_canPet || _targetType == SkillTargetType.TARGET_PET)) {
                    player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                    return false;
                }
            } else if (target.isPlayer()) {
                final Pair<Integer, OnAnswerListener> ask = pcTarget.getAskListener(false);
                final ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue()
                        : null;

                if (reviveAsk != null) {
                    if (reviveAsk.isForPet()) {
                        activeChar.sendPacket(
                                SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
                    } else {
                        activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
                    }
                    return false;
                }
                if (_targetType == SkillTargetType.TARGET_PET) {
                    player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                    return false;
                }
                // Check to see if the player is in a festival.
                if (pcTarget.isFestivalParticipant()) {
                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Resurrect"));
                    return false;
                }
            }
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        double percent = _power;

        if (percent < 100 && !isHandler()) {
            final double wit_bonus = _power * (BaseStats.WIT.calcBonus(activeChar) - 1);
            percent += wit_bonus > 20 ? 20 : wit_bonus;
            if (percent > 90) {
                percent = 90;
            }
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (target.getPlayer() == null) {
                    continue;
                }

                if (target.isPet() && _canPet) {
                    if (target.getPlayer().equals(activeChar)) {
                        ((PetInstance) target).doRevive(percent);
                    } else {
                        target.getPlayer().reviveRequest((Player) activeChar, percent, true);
                    }
                } else if (target.isPlayer()) {
                    if (_targetType == SkillTargetType.TARGET_PET) {
                        continue;
                    }

                    final Player targetPlayer = (Player) target;

                    final Pair<Integer, OnAnswerListener> ask = targetPlayer.getAskListener(false);
                    final ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener
                            ? (ReviveAnswerListener) ask.getValue() : null;
                    if (reviveAsk != null) {
                        continue;
                    }

                    if (targetPlayer.isFestivalParticipant()) {
                        continue;
                    }

                    targetPlayer.reviveRequest((Player) activeChar, percent, false);
                } else {
                    continue;
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    @Override
    public List<Creature> getTargets(final Creature activeChar, final Creature aimingTarget, final boolean forceUse) {
        if (oneTarget()) {
            return super.getTargets(activeChar, aimingTarget, forceUse);
        } else {
            final List<Creature> list = super.getTargets(activeChar, aimingTarget, forceUse);
            final Iterator<Creature> iterator = list.iterator();
            while (iterator.hasNext()) {
                final Creature target = iterator.next();
                if (target instanceof Servitor) //[Hack]: на петов не должен идти масс рес
                    iterator.remove();
                final List<Event> events = new ArrayList<>(GLOBAL.size() + 2);
                events.addAll(GLOBAL);
                events.addAll(target.getZoneEvents());
                events.stream().filter(e -> !e.canResurrect(activeChar, target, true, true)).forEach(e -> {
                    iterator.remove();
                });
            }
            return list;
        }
    }
}