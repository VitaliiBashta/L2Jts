package org.mmocore.gameserver.model.team;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.entity.DimensionalRift;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Party implements PlayerGroup {
    public static final int MAX_SIZE = 9;

    public static final int ITEM_LOOTER = 0;
    public static final int ITEM_RANDOM = 1;
    public static final int ITEM_RANDOM_SPOIL = 2;
    public static final int ITEM_ORDER = 3;
    public static final int ITEM_ORDER_SPOIL = 4;

    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final int[] LOOT_SYSSTRINGS = {487, 488, 798, 799, 800};
    private final long id;
    private final List<Player> members = new CopyOnWriteArrayList<>();
    public double rateExp;
    public double rateSp;
    public double rateDrop;
    public double rateAdena;
    public double rateSpoil;
    private int partyLvl;
    private int itemDistribution;
    private int itemOrder;
    private int dimensionalRift;
    private Reflection reflection;
    private CommandChannel commandChannel;
    private ScheduledFuture<?> positionTask;
    private volatile int requestChangeLoot = -1;
    private long requestChangeLootTimer;
    private Set<Integer> changeLootAnswers;
    private volatile Future<?> checkTask;

    /**
     * constructor ensures party has always one member - leader
     *
     * @param leader           создатель парти
     * @param itemDistribution режим распределения лута
     */
    public Party(final Player leader, final int itemDistribution) {
        id = ID_GENERATOR.getAndIncrement();
        this.itemDistribution = itemDistribution;
        members.add(leader);
        partyLvl = leader.getLevel();
        rateExp = leader.getPremiumAccountComponent().getPremiumBonus().getRateXp();
        rateSp = leader.getPremiumAccountComponent().getPremiumBonus().getRateSp();
        rateAdena = leader.getPremiumAccountComponent().getPremiumBonus().getDropAdena();
        rateDrop = leader.getPremiumAccountComponent().getPremiumBonus().getDropItems();
        rateSpoil = leader.getPremiumAccountComponent().getPremiumBonus().getDropSpoil();
    }

    public static void TeleportParty(final List<Player> members, final Location dest) {
        for (final Player _member : members) {
            if (_member == null) {
                continue;
            }
            _member.teleToLocation(dest);
        }
    }

    public static void TeleportParty(final List<Player> members, final Territory territory, final Location dest) {
        if (!territory.isInside(dest.x, dest.y)) {
            Log.add("TeleportParty: dest is out of territory", "errors");
            Thread.dumpStack();
            return;
        }
        final int base_x = members.get(0).getX();
        final int base_y = members.get(0).getY();

        for (final Player _member : members) {
            if (_member == null) {
                continue;
            }
            int diff_x = _member.getX() - base_x;
            int diff_y = _member.getY() - base_y;
            final Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);
            while (!territory.isInside(loc.x, loc.y)) {
                diff_x = loc.x - dest.x;
                diff_y = loc.y - dest.y;
                if (diff_x != 0) {
                    loc.x -= diff_x / Math.abs(diff_x);
                }
                if (diff_y != 0) {
                    loc.y -= diff_y / Math.abs(diff_y);
                }
            }
            _member.teleToLocation(loc);
        }
    }

    public static void RandomTeleportParty(final List<Player> members, final Territory territory) {
        for (final Player member : members) {
            member.teleToLocation(Territory.getRandomLoc(territory, member.getGeoIndex()));
        }
    }

    public long getId() {
        return id;
    }

    /**
     * @return number of party members
     */
    public int getMemberCount() {
        return members.size();
    }

    public int getMemberCountInRange(final Player player, final int range) {
        int count = 0;
        for (final Player member : members) {
            if (member == player || member.isInRangeZ(player, range)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return all party members
     */
    public List<Player> getPartyMembers() {
        return members;
    }

    public List<Integer> getPartyMembersObjIds() {
        return members.stream().map(Player::getObjectId).collect(Collectors.toList());
    }

    public List<Playable> getPartyMembersWithPets() {
        final List<Playable> result = new ArrayList<>();
        for (final Player member : members) {
            result.add(member);
            if (member.getServitor() != null) {
                result.add(member.getServitor());
            }
        }
        return result;
    }

    /**
     * true if player is party leader
     */
    public boolean isLeader(final Player player) {
        return getGroupLeader() == player;
    }

    /**
     * Возвращает лидера партии
     *
     * @return Player Лидер партии
     */
    @Override
    public Player getGroupLeader() {
        if (members.isEmpty())
            return null;

        return members.get(0);
    }

    /**
     * Broadcasts packet to every party member
     *
     * @param msg packet to broadcast
     */
    @Override
    public void broadCast(final IBroadcastPacket... msg) {
        members.stream().filter(member -> member != null && !member.isLogoutStarted()).forEach(member -> member.sendPacket(msg));
    }

    /**
     * Рассылает текстовое сообщение всем членам группы
     *
     * @param msg сообщение
     */
    public void broadcastMessageToPartyMembers(final String msg) {
        this.broadCast(new SystemMessage(SystemMsg.S1).addString(msg));
    }

    /**
     * Рассылает пакет всем членам группы исключая указанного персонажа<BR><BR>
     */
    public void broadcastToPartyMembers(final Player exclude, final IBroadcastPacket... msg) {
        members.stream().filter(member -> exclude != member).forEach(member -> {
            member.sendPacket(msg);
        });
    }

    public boolean containsMember(final Player player) {
        return members.contains(player);
    }

    public int indexOf(final Player player) {
        return members.indexOf(player);
    }

    /**
     * adds new member to party
     *
     * @param player L2Player to add
     */
    public boolean addPartyMember(final Player player) {
        final Player leader = getGroupLeader();
        if (leader == null) {
            return false;
        }

        if (members.isEmpty()) {
            return false;
        }
        if (members.contains(player)) {
            return false;
        }
        if (members.size() == MAX_SIZE) {
            return false;
        }
        members.add(player);

        if (requestChangeLoot != -1) {
            finishLootRequest(false); // cancel on invite
        }

        player.setParty(this);
        player.getListeners().onPartyInvite();

        Servitor pet;

        final List<L2GameServerPacket> pplayer = new ArrayList<>(20);

        //sends new member party window for all members
        //we do all actions before adding member to a list, this speeds things up a little
        pplayer.add(new PartySmallWindowAll(this, player));
        pplayer.add(new SystemMessage(SystemMsg.YOU_HAVE_JOINED_S1S_PARTY).addName(leader));

        // список пакетов, которые розсылаются другим членам пати, они статичны и неменяются в зависимости кому они посылаются
        final List<L2GameServerPacket> addInfo = new ArrayList<>(4 + members.size() * 4);
        addInfo.add(new SystemMessage(SystemMsg.C1_HAS_JOINED_THE_PARTY).addName(player));
        addInfo.add(new PartySmallWindowAdd(this, player));
        addInfo.add(new PartySpelled(player, true));
        if ((pet = player.getServitor()) != null) {
            addInfo.add(new ExPartyPetWindowAdd(pet));
            addInfo.add(new PartySpelled(pet, true));
        }

        final PartyMemberPosition pmp = new PartyMemberPosition();
        for (final Player member : members) {
            if (member != player) {
                final List<L2GameServerPacket> pmember = new ArrayList<>(addInfo.size() + 4);
                pmember.addAll(addInfo);
                pmember.add(new PartyMemberPosition().add(player));
                pmember.add(RelationChanged.update(member, player, member));
                member.sendPacket(pmember);

                pplayer.add(new PartySpelled(member, true));
                if ((pet = member.getServitor()) != null) {
                    pplayer.add(new PartySpelled(pet, true));
                    pet.broadcastCharInfoImpl();
                }
                pplayer.add(RelationChanged.update(player, member, player)); //FIXME на ЛВ переделано!
                pmp.add(member);
            }
        }

        pplayer.add(pmp);
        // Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
        if (isInCommandChannel()) {
            pplayer.add(ExMPCCOpen.STATIC);
        }

        player.sendPacket(pplayer);

        startUpdatePositionTask();
        recalculatePartyData();

        if (isInReflection() && getReflection() instanceof DimensionalRift) {
            ((DimensionalRift) getReflection()).partyMemberInvited();
        }

        final MatchingRoom currentRoom = player.getMatchingRoom();
        final MatchingRoom room = leader.getMatchingRoom();
        if (currentRoom != null && currentRoom != room) {
            currentRoom.removeMember(player, false);
        }
        if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
            room.addMemberForce(player);
        } else {
            MatchingRoomManager.getInstance().removeFromWaitingList(player);
        }

        return true;
    }

    /**
     * Удаляет все связи
     */
    public void dissolveParty() {
        for (final Player p : members) {
            p.sendPacket(PartySmallWindowDeleteAll.STATIC);
            p.setParty(null);
        }

        members.clear();

        setDimensionalRift(null);
        setCommandChannel(null);
        stopUpdatePositionTask();
    }

    /**
     * removes player from party
     *
     * @param player L2Player to remove
     */
    public boolean removePartyMember(final Player player, final boolean kick) {
        final boolean isLeader = isLeader(player);

        if (!members.remove(player))
            return false;

        boolean dissolve = members.size() == 1;

        player.getListeners().onPartyLeave();

        player.setParty(null);
        recalculatePartyData();

        final List<IBroadcastPacket> pplayer = new ArrayList<>(4 + members.size() * 2);

        // Отсылаемы вышедшему пакет закрытия СС
        if (isInCommandChannel()) {
            pplayer.add(ExMPCCClose.STATIC);
        }
        if (kick) {
            pplayer.add(SystemMsg.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
        } else {
            pplayer.add(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
        }
        pplayer.add(PartySmallWindowDeleteAll.STATIC);

        final Servitor pet;
        final List<L2GameServerPacket> outsInfo = new ArrayList<>(3);
        if ((pet = player.getServitor()) != null) {
            outsInfo.add(new ExPartyPetWindowDelete(pet));
        }
        outsInfo.add(new PartySmallWindowDelete(player));
        if (kick) {
            outsInfo.add(new SystemMessage(SystemMsg.C1_WAS_EXPELLED_FROM_THE_PARTY).addName(player));
        } else {
            outsInfo.add(new SystemMessage(SystemMsg.C1_HAS_LEFT_THE_PARTY).addName(player));
        }

        List<L2GameServerPacket> pmember;
        for (final Player member : members) {
            pmember = new ArrayList<>(2 + outsInfo.size());
            pmember.addAll(outsInfo);
            pmember.add(RelationChanged.update(member, player, member));
            member.sendPacket(pmember);
            pplayer.add(RelationChanged.update(player, member, player));
        }

        player.sendPacket(pplayer);

        final Reflection reflection = getReflection();

        if (reflection instanceof DarknessFestival) {
            ((DarknessFestival) reflection).partyMemberExited();
        } else if (isInReflection() && getReflection() instanceof DimensionalRift) {
            ((DimensionalRift) getReflection()).partyMemberExited(player);
        }
        if (reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null) {
            player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.DEFAULT);
        }

        final Player leader = getGroupLeader();
        final MatchingRoom room = leader != null ? leader.getMatchingRoom() : null;

        if (dissolve) {
            // Если в партии остался 1 человек, то удаляем ее из СС
            if (isInCommandChannel()) {
                commandChannel.removeParty(this);
            } else if (reflection != null) {
                //lastMember.teleToLocation(getReflection().getReturnLoc(), 0);
                //getReflection().stopCollapseTimer();
                //getReflection().collapse();
                if (reflection.getInstancedZone() != null && reflection.getInstancedZone().isCollapseOnPartyDismiss()) {
                    if (reflection.getParty() == this) // TODO: убрать затычку
                    {
                        reflection.startCollapseTimer(reflection.getInstancedZone().getTimerOnCollapse() * 1000);
                    }
                    if (leader != null && leader.getReflection() == reflection) {
                        leader.broadcastPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
                    }
                }
            }
            if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
                if (isLeader) // Вышел/отвалился лидер, остался один партиец, пати и комната распускаются
                {
                    room.disband();
                } else // Вышел/кикнули единственного партийца, комната переходит к лидеру, пати распускается
                {
                    room.removeMember(player, kick);
                }
            }

            dissolveParty();
        } else {
            if (isInCommandChannel() && commandChannel.getGroupLeader() == player) {
                commandChannel.setChannelLeader(leader);
            }

            if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
                room.removeMember(player, kick);
            }

            if (isLeader) {
                updateLeaderInfo();
            }
        }

        if (checkTask != null) {
            checkTask.cancel(true);
            checkTask = null;
        }

        return true;
    }

    public boolean changePartyLeader(final Player player) {
        final Player leader = getGroupLeader();

        // Меняем местами нового и текущего лидера
        final int index = indexOf(player);
        if (index == -1) {
            return false;
        }
        members.set(0, player);
        members.set(index, leader);

        leader.sendPacket(ExReplyHandOverPartyMaster.FALSE);
        player.sendPacket(ExReplyHandOverPartyMaster.TRUE);

        updateLeaderInfo();

        if (isInCommandChannel() && commandChannel.getGroupLeader() == leader) {
            commandChannel.setChannelLeader(player);
        }

        return true;
    }

    private void updateLeaderInfo() {
        final Player leader = getGroupLeader();
        if (leader == null) // некрасиво, но иначе NPE.
        {
            return;
        }

        final SystemMessage msg = new SystemMessage(SystemMsg.C1_HAS_BECOME_THE_PARTY_LEADER).addName(leader);

        for (final Player member : members) {
            member.sendPacket(PartySmallWindowDeleteAll.STATIC, new PartySmallWindowAll(this, member), msg);
        }

        // броадкасты состояний
        for (final Player member : members) {
            broadcastToPartyMembers(member, new PartySpelled(member, true)); // Показываем иконки
            if (member.getServitor() != null) {
                this.broadCast(new ExPartyPetWindowAdd(member.getServitor())); // Показываем окошки петов
            }
            // broadcastToPartyMembers(member, new PartyMemberPosition(member)); // Обновляем позицию на карте
        }

        final MatchingRoom room = leader.getMatchingRoom();
        if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
            room.setLeader(leader);
        }
    }

    /**
     * finds a player in the party by name
     *
     * @param name имя для поиска
     * @return найденый L2Player или null если не найдено
     */
    public Player getPlayerByName(final String name) {
        for (final Player member : members) {
            if (name.equalsIgnoreCase(member.getName())) {
                return member;
            }
        }
        return null;
    }

    /**
     * distribute item(s) to party members
     *
     * @param player
     * @param item
     */
    public void distributeItem(final Player player, final ItemInstance item, final NpcInstance fromNpc) {
        if (ArrayUtils.contains(AllSettingsConfig.customItemDistribute, item.getItemId()))
            distributeAdena(player, item, fromNpc);
        else
            distributeItem0(player, item, fromNpc);

    }

    private void distributeItem0(final Player player, final ItemInstance item, final NpcInstance fromNpc) {
        Player target = null;

        List<Player> ret = null;
        switch (itemDistribution) {
            case ITEM_RANDOM:
            case ITEM_RANDOM_SPOIL: {
                ret = new ArrayList<>(members.size());
                for (final Player member : members) {
                    if (member.isInRangeZ(player, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) && !member.isDead() && member.getInventory().validateCapacity(
                            item) && member.getInventory().validateWeight(item)) {
                        ret.add(member);
                    }
                }

                target = ret.isEmpty() ? null : ret.get(Rnd.get(ret.size()));
                break;
            }
            case ITEM_ORDER:
            case ITEM_ORDER_SPOIL: {
                ret = new CopyOnWriteArrayList<>(members);
                while (target == null && !ret.isEmpty()) {
                    final int looter = itemOrder;
                    itemOrder++;
                    if (itemOrder > ret.size() - 1) {
                        itemOrder = 0;
                    }

                    final Player looterPlayer = looter < ret.size() ? ret.get(looter) : null;

                    if (looterPlayer != null) {
                        if (!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) &&
                                ItemFunctions.canAddItem(looterPlayer, item)) {
                            target = looterPlayer;
                        } else {
                            ret.remove(looterPlayer);
                        }
                    }
                }

                if (target == null) {
                    return;
                }
                break;
            }
            case ITEM_LOOTER:
            default:
                target = player;
                break;
        }

        if (target == null) {
            target = player;
        }

        if (target.pickupItem(item, Log.PartyPickup)) {
            if (fromNpc == null) {
                player.broadcastPacket(new GetItem(item, player.getObjectId()));
            }

            player.broadcastPickUpMsg(item);
            item.pickupMe();

            broadcastToPartyMembers(target, SystemMessage.obtainItemsBy(item, target));
        } else {
            item.dropToTheGround(player, fromNpc);
        }
    }

    private void distributeAdena(final Player player, final ItemInstance item, final NpcInstance fromNpc) {
        if (player == null) {
            return;
        }

        final List<Player> membersInRange = new ArrayList<>();

        if (item.getCount() < members.size()) {
            membersInRange.add(player);
        } else {
            membersInRange.addAll(members.stream().filter(member -> !member.isDead() && (member == player || player.isInRangeZ(member, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) &&
                    ItemFunctions.canAddItem(player, item)).collect(Collectors.toList()));
        }

        if (membersInRange.isEmpty()) {
            membersInRange.add(player);
        }

        final long totalAdena = item.getCount();
        final long amount = totalAdena / membersInRange.size();
        final long ost = totalAdena % membersInRange.size();

        for (final Player member : membersInRange) {
            final long count = member == player ? amount + ost : amount;
            member.getInventory().addItem(item.getItemId(), count);
            member.getListeners().onItemPickup(item);
            member.sendPacket(SystemMessage.obtainItems(item.getItemId(), count, 0));
        }

        if (fromNpc == null) {
            player.broadcastPacket(new GetItem(item, player.getObjectId()));
        }

        item.pickupMe();
    }

    public void distributeXpAndSp(final double xpReward, final double spReward, final List<Player> rewardedMembers, final Creature lastAttacker, final MonsterInstance monster) {
        recalculatePartyData();

        final List<Player> mtr = new ArrayList<>();
        int partyLevel = lastAttacker.getLevel();
        int partyLvlSum = 0;
        final boolean checkDominionTransform = rewardedMembers.stream().anyMatch(Creature::isDominionTransform);
        // Если кто то из членов группы обладает трансформацией территориальных воин, тогда EXP/SP не получит никто
        if (checkDominionTransform) {
            return;
        }
        // считаем минимальный/максимальный уровень
        for (final Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            partyLevel = Math.max(partyLevel, member.getLevel());
        }
        // составляем список игроков, удовлетворяющих требованиям
        for (final Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            if (member.getLevel() <= partyLevel - 15) {
                continue;
            }
            partyLvlSum += member.getLevel();
            mtr.add(member);
        }

        if (mtr.isEmpty()) {
            return;
        }

        // бонус за пати
        final double bonus = AllSettingsConfig.ALT_PARTY_BONUS[mtr.size() - 1];

        // количество эксп и сп для раздачи на всех
        final double XP = xpReward * bonus;
        final double SP = spReward * bonus;

        for (final Player member : mtr) {
            double lvlPenalty = PlayerUtils.penaltyModifier(monster.calculateLevelDiffForDrop(member.getLevel()), 9);
            final int lvlDiff = partyLevel - member.getLevel();
            if (lvlDiff >= 10 && lvlDiff <= 14) {
                lvlPenalty *= 0.3D;
            }

            // отдаем его часть с учетом пенальти
            double memberXp = XP * lvlPenalty * member.getLevel() / partyLvlSum;
            double memberSp = SP * lvlPenalty * member.getLevel() / partyLvlSum;

            // больше чем соло не дадут
            memberXp = Math.min(memberXp, xpReward);
            memberSp = Math.min(memberSp, spReward);

            member.addExpAndCheckBonus(monster, (long) memberXp, (long) memberSp, memberXp / xpReward);
        }

        recalculatePartyData();
    }

    public void recalculatePartyData() {
        partyLvl = 0;
        double rateExp = 0.;
        double rateSp = 0.;
        double rateDrop = 0.;
        double rateAdena = 0.;
        double rateSpoil = 0.;
        double minRateExp = Double.MAX_VALUE;
        double minRateSp = Double.MAX_VALUE;
        double minRateDrop = Double.MAX_VALUE;
        double minRateAdena = Double.MAX_VALUE;
        double minRateSpoil = Double.MAX_VALUE;
        int count = 0;

        for (final Player member : members) {
            final int level = member.getLevel();
            partyLvl = Math.max(partyLvl, level);
            count++;

            rateExp += member.getPremiumAccountComponent().getPremiumBonus().getRateXp();
            rateSp += member.getPremiumAccountComponent().getPremiumBonus().getRateSp();
            rateDrop += member.getPremiumAccountComponent().getPremiumBonus().getDropItems();
            rateAdena += member.getPremiumAccountComponent().getPremiumBonus().getDropAdena();
            rateSpoil += member.getPremiumAccountComponent().getPremiumBonus().getDropSpoil();

            minRateExp = Math.min(minRateExp, member.getPremiumAccountComponent().getPremiumBonus().getRateXp());
            minRateSp = Math.min(minRateSp, member.getPremiumAccountComponent().getPremiumBonus().getRateSp());
            minRateDrop = Math.min(minRateDrop, member.getPremiumAccountComponent().getPremiumBonus().getDropItems());
            minRateAdena = Math.min(minRateAdena, member.getPremiumAccountComponent().getPremiumBonus().getDropAdena());
            minRateSpoil = Math.min(minRateSpoil, member.getPremiumAccountComponent().getPremiumBonus().getDropSpoil());
        }

        this.rateExp = ServerConfig.RATE_PARTY_MIN ? minRateExp : rateExp / count;
        this.rateSp = ServerConfig.RATE_PARTY_MIN ? minRateSp : rateSp / count;
        this.rateDrop = ServerConfig.RATE_PARTY_MIN ? minRateDrop : rateDrop / count;
        this.rateAdena = ServerConfig.RATE_PARTY_MIN ? minRateAdena : rateAdena / count;
        this.rateSpoil = ServerConfig.RATE_PARTY_MIN ? minRateSpoil : rateSpoil / count;
    }

    public int getLevel() {
        return partyLvl;
    }

    public int getLootDistribution() {
        return itemDistribution;
    }

    public boolean isDistributeSpoilLoot() {
        boolean rv = false;

        if (itemDistribution == ITEM_RANDOM_SPOIL || itemDistribution == ITEM_ORDER_SPOIL) {
            rv = true;
        }

        return rv;
    }

    public boolean isInDimensionalRift() {
        return dimensionalRift > 0 && getDimensionalRift() != null;
    }

    public DimensionalRift getDimensionalRift() {
        return dimensionalRift == 0 ? null : (DimensionalRift) ReflectionManager.getInstance().get(dimensionalRift);
    }

    public void setDimensionalRift(final DimensionalRift dr) {
        dimensionalRift = dr == null ? 0 : dr.getId();
    }

    public boolean isInReflection() {
        if (reflection != null) {
            return true;
        }
        if (commandChannel != null) {
            return commandChannel.isInReflection();
        }
        return false;
    }

    public Reflection getReflection() {
        if (reflection != null) {
            return reflection;
        }
        if (commandChannel != null) {
            return commandChannel.getReflection();
        }
        return null;
    }

    public void setReflection(final Reflection reflection) {
        this.reflection = reflection;
    }

    public boolean isInCommandChannel() {
        return commandChannel != null;
    }

    public CommandChannel getCommandChannel() {
        return commandChannel;
    }

    public void setCommandChannel(final CommandChannel channel) {
        commandChannel = channel;
    }

    /**
     * Телепорт всей пати в одну точку (x,y,z)
     */
    public void Teleport(final int x, final int y, final int z) {
        TeleportParty(getPartyMembers(), new Location(x, y, z));
    }

    /**
     * Телепорт всей пати в одну точку dest
     */
    public void Teleport(final Location dest) {
        TeleportParty(getPartyMembers(), dest);
    }

    /**
     * Телепорт всей пати на территорию, игроки расставляются рандомно по территории
     */
    public void Teleport(final Territory territory) {
        RandomTeleportParty(getPartyMembers(), territory);
    }

    /**
     * Телепорт всей пати на территорию, лидер попадает в точку dest, а все остальные относительно лидера
     */
    public void Teleport(final Territory territory, final Location dest) {
        TeleportParty(getPartyMembers(), territory, dest);
    }

    private void startUpdatePositionTask() {
        if (positionTask == null) {
            positionTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new UpdatePositionTask(), 1000, 1000);
        }
    }

    private void stopUpdatePositionTask() {
        if (positionTask != null) {
            positionTask.cancel(false);
        }
    }

    public void requestLootChange(final byte type) {
        if (requestChangeLoot != -1) {
            if (System.currentTimeMillis() > requestChangeLootTimer) {
                finishLootRequest(false);
            } else {
                return;
            }
        }
        requestChangeLoot = type;
        final int additionalTime = 45000; // timeout 45sec, guess
        requestChangeLootTimer = System.currentTimeMillis() + additionalTime;
        changeLootAnswers = new CopyOnWriteArraySet<>();
        checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ChangeLootCheck(), additionalTime + 1000, 5000);
        broadcastToPartyMembers(getGroupLeader(), new ExAskModifyPartyLooting(getGroupLeader().getName(), type));
        final SystemMessage sm = new SystemMessage(SystemMsg.REQUESTING_APPROVAL_FOR_CHANGING_PARTY_LOOT_TO_S1);
        sm.addSysString(LOOT_SYSSTRINGS[type]);
        getGroupLeader().sendPacket(sm);
    }

    public void answerLootChangeRequest(final Player member, final boolean answer) {
        if (requestChangeLoot == -1) {
            return;
        }
        if (changeLootAnswers.contains(member.getObjectId())) {
            return;
        }
        if (!answer) {
            finishLootRequest(false);
            return;
        }
        changeLootAnswers.add(member.getObjectId());
        if (changeLootAnswers.size() >= getMemberCount() - 1) {
            finishLootRequest(true);
        }
    }

    private void finishLootRequest(final boolean success) {
        if (requestChangeLoot == -1) {
            return;
        }
        if (checkTask != null) {
            checkTask.cancel(false);
            checkTask = null;
        }
        if (success) {
            this.broadCast(new ExSetPartyLooting(1, requestChangeLoot));
            itemDistribution = requestChangeLoot;
            final SystemMessage sm = new SystemMessage(SystemMsg.PARTY_LOOT_WAS_CHANGED_TO_S1_);
            sm.addSysString(LOOT_SYSSTRINGS[requestChangeLoot]);
            this.broadCast(sm);
        } else {
            this.broadCast(new ExSetPartyLooting(0, (byte) 0));
            this.broadCast(new SystemMessage(SystemMsg.PARTY_LOOT_CHANGE_WAS_CANCELLED));
        }
        changeLootAnswers.clear();
        changeLootAnswers = null;
        requestChangeLoot = -1;
        requestChangeLootTimer = 0;
    }

    @Override
    public Iterator<Player> iterator() {
        return members.iterator();
    }

    private class UpdatePositionTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            final List<Player> update = new LazyArrayList<>(MAX_SIZE);

            for (final Player member : members) {
                final Location loc = member.getLastPartyPosition();
                if (loc == null || member.getDistance(loc) > 256) //TODO подкорректировать
                {
                    member.setLastPartyPosition(member.getLoc());
                    update.add(member);
                }
            }

            if (!update.isEmpty()) {
                for (final Player member : members) {
                    final PartyMemberPosition pmp = new PartyMemberPosition();
                    update.stream().filter(m -> m != member).forEach(pmp::add);
                    if (pmp.size() > 0) {
                        member.sendPacket(pmp);
                    }
                }
            }

            update.clear();
        }
    }

    private class ChangeLootCheck extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (System.currentTimeMillis() > Party.this.requestChangeLootTimer) {
                Party.this.finishLootRequest(false);
            }
        }
    }
}