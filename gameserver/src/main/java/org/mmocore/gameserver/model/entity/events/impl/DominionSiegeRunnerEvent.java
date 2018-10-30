package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.configuration.config.ResidenceConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 15:24/14.02.2011
 */
public class DominionSiegeRunnerEvent extends Event {
    public static final String REGISTRATION = "registration";
    public static final String BATTLEFIELD = "battlefield";
    // quests
    private final Map<ClassId, Quest> classQuests = new EnumMap<>(ClassId.class);
    private final List<Quest> breakQuests = new ArrayList<>();
    // dominions
    private final List<Dominion> registeredDominions = new ArrayList<>(9);
    private ZonedDateTime startTime = ZonedDateTime.now();
    private int state;

    public DominionSiegeRunnerEvent(final MultiValueSet<String> set) {
        super(set);

        startTime = Residence.MIN_SIEGE_DATE;
    }

    @Override
    public void initEvent() {
        final ZonedDateTime dateTime = generateNextDate(DayOfWeek.SATURDAY);
        if (registeredDominions.size() >= 2 && startTime.isEqual(dateTime))
            reCalcNextTime(false);

        addState(SiegeEvent.REGISTRATION_STATE);
    }

    @Override
    public void startEvent() {
        addState(SiegeEvent.PROGRESS_STATE);

        super.startEvent();

        for (final Dominion d : registeredDominions) {
            d.getSiegeEvent().clearActions();
            d.getSiegeEvent().registerActions();

            d.getSiegeEvent().startEvent();
        }

        broadcastToWorld(SystemMsg.TERRITORY_WAR_HAS_BEGUN);
    }

    @Override
    public void stopEvent() {
        addState(SiegeEvent.REGISTRATION_STATE);
        removeState(SiegeEvent.PROGRESS_STATE);

        super.stopEvent();

        for (final Dominion d : registeredDominions) {
            d.getSiegeEvent().clearActions();

            d.getSiegeEvent().stopEvent();
        }

        broadcastToWorld(SystemMsg.TERRITORY_WAR_HAS_ENDED);
    }

    @Override
    public void announce(final int val) {
        switch (val) {
            case -20:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES);
                break;
            case -10:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_10_MINUTES);
                break;
            case -5:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_5_MINUTES);
                break;
            case -1:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_BEGINS_IN_1_MINUTE);
                break;
            case 3600:
                broadcastToWorld(new SystemMessage(SystemMsg.THE_TERRITORY_WAR_WILL_END_IN_S1HOURS).addNumber(val / 3600));
                break;
            case 600:
            case 300:
            case 60:
                broadcastToWorld(new SystemMessage(SystemMsg.THE_TERRITORY_WAR_WILL_END_IN_S1MINUTES).addNumber(val / 60));
                break;
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                broadcastToWorld(new SystemMessage(SystemMsg.S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR).addNumber(val));
                break;
        }
    }

    public ZonedDateTime getSiegeDate() {
        return startTime;
    }

    public void setSiegeDate(final ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();

        registerActions();
    }

    @Override
    protected Instant startTime() {
        return startTime.toInstant();
    }

    @Override
    public EventType getType() {
        return EventType.MAIN_EVENT;
    }
    //========================================================================================================================================================================
    //                                                         Broadcast
    //========================================================================================================================================================================

    public void broadcastTo(final IBroadcastPacket packet) {
        for (final Dominion dominion : registeredDominions) {
            dominion.getSiegeEvent().broadcastTo(packet);
        }
    }

    public void broadcastTo(final L2GameServerPacket packet) {
        for (final Dominion dominion : registeredDominions) {
            dominion.getSiegeEvent().broadcastTo(packet);
        }
    }

    //========================================================================================================================================================================
    //                                                         Getters/Setters
    //========================================================================================================================================================================

    @Override
    public boolean isInProgress() {
        return hasState(SiegeEvent.PROGRESS_STATE);
    }

    public void addState(final int b) {
        state |= b;

        for (final Dominion d : registeredDominions) {
            d.getSiegeEvent().addState(b);
        }
    }

    public void removeState(final int b) {
        state &= ~b;

        for (final Dominion d : registeredDominions) {
            d.getSiegeEvent().removeState(b);
        }

        switch (b) {
            case SiegeEvent.REGISTRATION_STATE:
                broadcastToWorld(SystemMsg.THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED);
                break;
        }
    }

    public boolean hasState(final int val) {
        return (state & val) == val;
    }

    public boolean isRegistrationOver() {
        return !hasState(SiegeEvent.REGISTRATION_STATE);
    }

    public void addClassQuest(final ClassId c, final Quest quest) {
        classQuests.put(c, quest);
    }

    public Quest getClassQuest(final ClassId c) {
        return classQuests.get(c);
    }

    public void addBreakQuest(final Quest q) {
        breakQuests.add(q);
    }

    public List<Quest> getBreakQuests() {
        return breakQuests;
    }

    //========================================================================================================================================================================
    //                                                         Overrides Event
    //========================================================================================================================================================================
    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION)) {
            if (start) {
                addState(SiegeEvent.REGISTRATION_STATE);
            } else {
                removeState(SiegeEvent.REGISTRATION_STATE);
            }
        } else if (name.equalsIgnoreCase(BATTLEFIELD)) {
            if (start) {
                addState(DominionSiegeEvent.BATTLEFIELD_CHAT_STATE);

                initDominions();
            } else {
                removeState(DominionSiegeEvent.BATTLEFIELD_CHAT_STATE);

                clearDominions();
            }
        } else {
            super.action(name, start);
        }
    }

    public synchronized void registerDominion(final Dominion d, final boolean onStart) {
        final List<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);

        final ZonedDateTime dateIfNotSet = generateNextDate(DayOfWeek.SUNDAY);

        final boolean dominionAction;
        if (onStart) {
            dominionAction = true;
            registeredDominions.add(d);

            if (startTime.isEqual(Residence.MIN_SIEGE_DATE)) {
                final ZonedDateTime current = ZonedDateTime.now();
                ZonedDateTime nextDate = generateNextDate(DayOfWeek.SATURDAY);
                nextDate = nextDate.minusWeeks(ResidenceConfig.CASTLE_SIEGE_PERIOD);

                int minDayOfYear = Integer.MAX_VALUE, maxDayOfYear = Integer.MIN_VALUE;
                for (final Castle castle : castles) {
                    final ZonedDateTime siegeDate = castle.getSiegeDate();

                    final int val = (siegeDate.isEqual(Residence.MIN_SIEGE_DATE) ? dateIfNotSet : siegeDate).getDayOfYear();
                    if (val < minDayOfYear) {
                        minDayOfYear = val;
                    }

                    if (val > maxDayOfYear) {
                        maxDayOfYear = val;
                    }
                }

                startTime = nextDate;

                loop:
                {
                    // если не все осады закончили
                    if (minDayOfYear != maxDayOfYear) {
                        break loop;
                    }

                    // если сервак запустился в воскр. когда будут осады, но их ищо нету
                    if (current.getDayOfYear() == minDayOfYear) {
                        break loop;
                    }

                    // если сервак запустился после ТВ
                    if (nextDate.getDayOfYear() == current.getDayOfYear()) {
                        break loop;
                    }

                    startTime = startTime.plusWeeks(ResidenceConfig.CASTLE_SIEGE_PERIOD);
                }
            }

            d.setSiegeDate(startTime);
        } else {
            dominionAction = !registeredDominions.contains(d);
            if (dominionAction) {
                registeredDominions.add(d);
            }

            int minDayOfYear = Integer.MAX_VALUE, maxDayOfYear = Integer.MIN_VALUE;
            for (final Castle castle : castles) {
                final ZonedDateTime siegeDate = castle.getSiegeDate();

                final int val = (siegeDate.isEqual(Residence.MIN_SIEGE_DATE) ? dateIfNotSet : siegeDate).getDayOfYear();
                if (val < minDayOfYear) {
                    minDayOfYear = val;
                }

                if (val > maxDayOfYear) {
                    maxDayOfYear = val;
                }
            }

            if (minDayOfYear == maxDayOfYear && registeredDominions.size() > 1) {
                startTime = generateNextDate(DayOfWeek.SATURDAY);

                reCalcNextTime(false);
            }
        }

        if (dominionAction) {
            d.getSiegeEvent().spawnAction(DominionSiegeEvent.TERRITORY_NPC, true);
            d.rewardSkills();
        }
    }

    public synchronized void unRegisterDominion(final Dominion d) {
        if (!registeredDominions.contains(d))
            return;

        registeredDominions.remove(d);

        d.getSiegeEvent().spawnAction(DominionSiegeEvent.TERRITORY_NPC, false);
        d.setSiegeDate(Residence.MIN_SIEGE_DATE);

        // если уже ток 1 доминион - чистим таймер
        if (registeredDominions.size() == 1) {
            clearActions();

            startTime = Residence.MIN_SIEGE_DATE;
        }
    }

    private ZonedDateTime generateNextDate(final DayOfWeek day) {
        ZonedDateTime startTime = ResidenceConfig.CASTLE_VALIDATION_DATE.with(day);

        if (startTime.isBefore(ResidenceConfig.CASTLE_VALIDATION_DATE))
            startTime = startTime.plusWeeks(1);

        startTime = startTime.withHour(20);

        startTime = validateDominionDate(startTime, ResidenceConfig.CASTLE_SIEGE_PERIOD);

        return startTime;
    }

    private ZonedDateTime validateDominionDate(final ZonedDateTime startTime, final int add) {
        ZonedDateTime newStartTime = startTime.withMinute(0).withSecond(0).withNano(0);

        while (newStartTime.isBefore(ZonedDateTime.now()))
            newStartTime = newStartTime.plusWeeks(add);

        return newStartTime;
    }

    public List<Dominion> getRegisteredDominions() {
        return registeredDominions;
    }

    //========================================================================================================================================================================
    //                                                        Dominion actions
    //========================================================================================================================================================================

    private void initDominions() {
        // овнеры являются дефендерами територий
        for (final Dominion d : registeredDominions) {
            final DominionSiegeEvent siegeEvent = d.getSiegeEvent();
            final SiegeClanObject ownerClan = new SiegeClanObject(SiegeEvent.DEFENDERS, siegeEvent.getResidence().getOwner(), 0);

            siegeEvent.addObject(SiegeEvent.DEFENDERS, ownerClan);
        }

        // проверка на 2х реги, и чиста ревардов от другой територии, если зареган был
        for (final Dominion d : registeredDominions) {
            final List<SiegeClanObject> defenders = d.getSiegeEvent().getObjects(DominionSiegeEvent.DEFENDERS);
            for (final SiegeClanObject siegeClan : defenders) {
                // листаем мемберов от клана
                for (final UnitMember member : siegeClan.getClan()) {
                    for (final Dominion d2 : registeredDominions) {
                        final DominionSiegeEvent siegeEvent2 = d2.getSiegeEvent();
                        final List<Integer> defenderPlayers2 = siegeEvent2.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);

                        defenderPlayers2.remove(Integer.valueOf(member.getObjectId()));

                        // если у игрока есть реварды от другой з територии - обнуляем
                        if (d != d2) {
                            siegeEvent2.clearReward(member.getObjectId());
                        }
                    }
                }
            }

            final List<Integer> defenderPlayers = d.getSiegeEvent().getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
            for (final Integer playerObjectId : defenderPlayers) {
                for (final Dominion d2 : registeredDominions) {
                    if (d != d2) {
                        final DominionSiegeEvent siegeEvent2 = d2.getSiegeEvent();
                        final List<Integer> defenderPlayers2 = siegeEvent2.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);

                        defenderPlayers2.remove(playerObjectId);

                        // если у игрока есть реварды от другой з територии - обнуляем
                        siegeEvent2.clearReward(playerObjectId);
                    }
                }
            }
        }

        // переносим с другоим доминионов дефендеров - в аттакеры
        for (final Dominion d : registeredDominions) {
            final DominionSiegeEvent ds = d.getSiegeEvent();
            for (final Dominion d2 : registeredDominions) {
                if (d2 == d) {
                    continue;
                }

                final DominionSiegeEvent ds2 = d2.getSiegeEvent();

                ds.addObjects(SiegeEvent.ATTACKERS, ds2.<Serializable>getObjects(SiegeEvent.DEFENDERS));
                ds.addObjects(DominionSiegeEvent.ATTACKER_PLAYERS, ds2.<Serializable>getObjects(DominionSiegeEvent.DEFENDER_PLAYERS));
            }

            // добавляем у всех кто онлайн евент, невызываем broadcastCharInfo - ибо ненужно, оно само вызовется если заюзается скрол
            for (final Player player : ds.getOnlinePlayers()) {
                player.sendPacket(ExDominionChannelSet.ACTIVE);
                player.addEvent(ds);
            }
        }
    }

    private void clearDominions() {
        broadcastToWorld(SystemMsg.THE_BATTLEFIELD_CHANNEL_HAS_BEEN_DEACTIVATED);
        for (final Dominion d : registeredDominions) {
            final DominionSiegeEvent siegeEvent = d.getSiegeEvent();
            for (final Player player : siegeEvent.getOnlinePlayers()) {
                player.sendPacket(ExDominionChannelSet.DEACTIVE);
                if (siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(player.getObjectId())) {
                    player.broadcastUserInfo(true);
                    player.removeEvent(siegeEvent);

                    if (player.isInParty()) {
                        player.getParty().broadcastToPartyMembers(player, new ExPartyMemberRenamed(player), new PartySmallWindowUpdate(player));
                    }
                } else {
                    player.removeEvent(siegeEvent);
                }
            }

            siegeEvent.removeObjects(DominionSiegeEvent.DISGUISE_PLAYERS);
            siegeEvent.removeObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
            siegeEvent.removeObjects(DominionSiegeEvent.DEFENDERS);
            siegeEvent.removeObjects(DominionSiegeEvent.ATTACKER_PLAYERS);
            siegeEvent.removeObjects(DominionSiegeEvent.ATTACKERS);
        }
    }
}
