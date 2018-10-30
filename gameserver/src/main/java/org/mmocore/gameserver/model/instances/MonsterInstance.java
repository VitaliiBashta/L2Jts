package org.mmocore.gameserver.model.instances;

import gnu.trove.set.hash.TIntHashSet;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.reward.*;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList;
import org.mmocore.gameserver.object.components.npc.AggroList.HateInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.Faction;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PlayerUtils;
import org.mmocore.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages all Monsters.
 * <p/>
 * L2MonsterInstance :<BR><BR>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance </li>
 */
public class MonsterInstance extends NpcInstance {
    public static final RewardList blueChampionDrop;
    public static final RewardList redChampionDrop;
    private static final Map<Integer, Integer> crpRewardMap;

    static {
        try {
            blueChampionDrop = parseChampionDrop(AllSettingsConfig.AltBlueChampionDrop);
            redChampionDrop = parseChampionDrop(AllSettingsConfig.AltRedChampionDrop);
        } catch (Exception e) {
            _log.warn("Failed to load additional drop for Champions! Please check for syntax errors.");
        }

        String crpStr = LostDreamCustom.crpRewardMonsters;
        if (crpStr != null && !crpStr.equals(""))
            try {
                crpRewardMap = parseCrpReward(crpStr);
            } catch (Exception e) {
                _log.warn("Failed to load config with additional crp reward for monsters! Please check for syntax errors.");
            }
    }

    private final Lock harvestLock = new ReentrantLock();
    private final Lock absorbLock = new ReentrantLock();
    private final Lock sweepLock = new ReentrantLock();
    /**
     * crops
     */
    private boolean _isSeeded;
    private int _seederId;
    private boolean _altSeed;
    private RewardItem _harvestItem;
    private int overhitAttackerId;
    /**
     * Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill
     */
    private double _overhitDamage;
    /**
     * The table containing all players objectID that successfully absorbed the soul of this L2NpcInstance
     */
    private TIntHashSet _absorbersIds;
    /**
     * True if a Dwarf has used Spoil on this L2NpcInstance
     */
    private boolean _isSpoiled;
    private int spoilerId;
    /**
     * Table containing all Items that a Dwarf can Sweep on this L2NpcInstance
     */
    private List<RewardItem> _sweepItems;
    private int _isChampion;
    public MonsterInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);

        setUndying(false);
    }

    /**
     * For example: 4037,1,5,95;57,1,1,100;6673,250,500,33
     *
     * @param str - string with reward splitted by ; and reward params by ,
     * @return complete rewardList
     */
    private static RewardList parseChampionDrop(String str) {
        RewardList list = new RewardList(RewardType.NOT_RATED_NOT_GROUPED, true);
        for (String data : str.split(";")) {
            String[] params = data.split(",");
            double chance = Double.parseDouble(params[3]) * 10000;
            RewardGroup group = new RewardGroup(chance);
            group.addData(new RewardData(Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                    Integer.parseInt(params[2]), chance));
            list.add(group);
        }
        return list;
    }

    private static Map<Integer, Integer> parseCrpReward(String str) {
        Map<Integer, Integer> result = new HashMap<>();
        String[] info = str.split(";");
        for (String reward : info) {
            String[] finalInfo = reward.split(",");
            result.put(Integer.parseInt(finalInfo[0]), Integer.parseInt(finalInfo[1]));
        }
        return result;
    }

    @Override
    public boolean isMovementDisabled() {
        // Невозможность ходить для этих мобов
        return getNpcId() == 18344 || getNpcId() == 18345 || super.isMovementDisabled();
    }

    @Override
    public boolean isLethalImmune() {
        return _isChampion > 0 || getNpcId() == 22215 || getNpcId() == 22216 || getNpcId() == 22217 || getParameter("isLethalImmune", false) || super.isLethalImmune();
    }

    @Override
    public boolean isFearImmune() {
        return _isChampion > 0 || super.isFearImmune();
    }

    @Override
    public boolean isParalyzeImmune() {
        return _isChampion > 0 || super.isParalyzeImmune();
    }

    /**
     * Return True if the attacker is not another L2MonsterInstance.<BR><BR>
     */
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return !attacker.isMonster();
    }

    public int getChampion() {
        return _isChampion;
    }

    public void setChampion(final int level) {
        if (level == 0) {
            removeSkillById(4407);
            _isChampion = 0;
        } else {
            addSkill(SkillTable.getInstance().getSkillEntry(4407, level));
            _isChampion = level;
        }
    }

    public void setChampion() {
        if (getReflection().canChampions() && canChampion()) {
            final double random = Rnd.nextDouble();
            if (AllSettingsConfig.ALT_CHAMPION_CHANCE2 / 100. >= random) {
                setChampion(2);
            } else if ((AllSettingsConfig.ALT_CHAMPION_CHANCE1 + AllSettingsConfig.ALT_CHAMPION_CHANCE2) / 100. >= random) {
                setChampion(1);
            } else {
                setChampion(0);
            }
        } else {
            setChampion(0);
        }
    }

    public boolean canChampion() {
        return !isMinion() && getTemplate().rewardExp > 0;
    }

    @Override
    public TeamType getTeam() {
        return getChampion() == 2 ? TeamType.RED : getChampion() == 1 ? TeamType.BLUE : TeamType.NONE;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        setCurrentHpMp(getMaxHp(), getMaxMp(), true);
    }

    @Override
    protected void onDespawn() {
        setOverhitDamage(0);
        setOverhitAttacker(null);
        clearSweep();
        clearHarvest();
        clearAbsorbers();

        super.onDespawn();
    }

    @Override
    public Location getMinionPosition() {
        return Location.findPointToStay(this, 100, 150);
    }

    @Override
    public void spawnMinion(final NpcInstance minion) {
        if (minion.isMonster()) {
            if (getChampion() == 2) {
                ((MonsterInstance) minion).setChampion(1);
            } else {
                ((MonsterInstance) minion).setChampion(0);
            }
        }
        super.spawnMinion(minion);
    }

    @Override
    protected void onDeath(final Creature killer) {
        if (!isIgnoreDrop())
            calculateRewards(killer);
        if (!isIgnoreCrp())
            if (crpRewardMap != null)
                doCrpReward(killer);
        super.onDeath(killer);
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake,
                                     final boolean standUp, final boolean directHp, final boolean lethal) {
        if (skill != null && skill.getTemplate().isOverhit()) {
            // Calculate the over-hit damage
            // Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
            final double overhitDmg = (getCurrentHp() - damage) * -1;
            if (overhitDmg <= 0) {
                setOverhitDamage(0);
                setOverhitAttacker(null);
            } else {
                setOverhitDamage(overhitDmg);
                setOverhitAttacker(attacker);
            }
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    public void calculateRewards(Creature lastAttacker) {
        final Creature topDamager = getAggroList().getTopDamager();
        if (lastAttacker == null || !lastAttacker.isPlayable()) {
            lastAttacker = topDamager;
        }

        if (lastAttacker == null || !lastAttacker.isPlayable()) {
            return;
        }

        final Player killer = lastAttacker.getPlayer();
        if (killer == null) {
            return;
        }

        final Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

        final Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        if (quests != null && quests.length > 0) {
            List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
            if (isRaid() && AllSettingsConfig.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
            {
                players = new ArrayList<>();
                for (final Playable pl : aggroMap.keySet()) {
                    if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE))) {
                        if (!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
                        {
                            players.add(pl.getPlayer());
                        }
                    }
                }
            } else if (killer.getParty() != null && killer.getParty().getCommandChannel() != null) // если киллер в ЦЦ - берем всю цц
            {
                players = new ArrayList<Player>(killer.getParty().getCommandChannel().getMemberCount());
                for (Party p : killer.getParty().getCommandChannel().getParties())
                    for (Player pl : p.getPartyMembers())
                        if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE * 2) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE * 2)))
                            players.add(pl);
            } else if (killer.getParty() != null) // если киллер в пати без cc
            {
                players = new ArrayList<>(killer.getParty().getMemberCount());
                for (final Player pl : killer.getParty().getPartyMembers()) {
                    if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE))) {
                        players.add(pl);
                    }
                }
            }

            for (final Quest quest : quests) {
                Player toReward = killer;
                if (quest.getParty() != Quest.PARTY_NONE && players != null) {
                    if (isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
                    {
                        for (final Player pl : players) {
                            final QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted()) {
                                quest.notifyKill(this, qs);
                            }
                        }
                        toReward = null;
                    } else { // иначе выбираем одного
                        final List<Player> interested = new ArrayList<>(players.size());
                        for (final Player pl : players) {
                            final QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
                            {
                                interested.add(pl);
                            }
                        }

                        if (interested.isEmpty()) {
                            continue;
                        }

                        toReward = interested.get(Rnd.get(interested.size()));
                        if (toReward == null) {
                            toReward = killer;
                        }
                    }
                }

                if (toReward != null) {
                    final QuestState qs = toReward.getQuestState(quest);
                    if (qs != null && !qs.isCompleted()) {
                        quest.notifyKill(this, qs);
                    }
                }
            }
        }

        final Map<Player, RewardInfo> rewards = new HashMap<>();
        for (final HateInfo info : aggroMap.values()) {
            if (info.damage <= 1) {
                continue;
            }
            final Playable attacker = (Playable) info.attacker;
            final Player player = attacker.getPlayer();
            final RewardInfo reward = rewards.get(player);
            if (reward == null) {
                rewards.put(player, new RewardInfo(player, info.damage));
            } else {
                reward.addDamage(info.damage);
            }
        }

        final Player[] attackers = rewards.keySet().toArray(new Player[rewards.size()]);
        double[] xpsp = new double[2];

        for (final Player attacker : attackers) {
            if (attacker.isDead()) {
                continue;
            }

            final RewardInfo reward = rewards.get(attacker);

            if (reward == null) {
                continue;
            }

            final Party party = attacker.getParty();
            final double maxHp = getMaxHp();

            xpsp[0] = 0.;
            xpsp[1] = 0.;

            if (party == null) {
                final int damage = (int) Math.min(reward._dmg, maxHp);
                if (damage > 0) {
                    if (isInRangeZ(attacker, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                        xpsp = calculateExpAndSp(attacker.getLevel(), damage);
                    }

                    xpsp[0] = applyOverhit(killer, xpsp[0]);
                    //Начисляется игроку только в том случае если он не в трансформации териториальных воин
                    if (!attacker.isDominionTransform()) {
                        attacker.addExpAndCheckBonus(this, (long) xpsp[0], (long) xpsp[1], 1.);
                    }
                }
                rewards.remove(attacker);
            } else {
                int partyDmg = 0;
                int partylevel = 1;
                final List<Player> rewardedMembers = new ArrayList<>();
                for (final Player partyMember : party.getPartyMembers()) {
                    final RewardInfo ai = rewards.remove(partyMember);
                    if (partyMember.isDead() || !isInRangeZ(partyMember, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                        continue;
                    }
                    if (ai != null) {
                        partyDmg += ai._dmg;
                    }

                    rewardedMembers.add(partyMember);
                    if (partyMember.getLevel() > partylevel) {
                        partylevel = partyMember.getLevel();
                    }
                }
                partyDmg = (int) Math.min(partyDmg, maxHp);
                if (partyDmg > 0) {
                    xpsp = calculateExpAndSp(partylevel, partyDmg);
                    final double partyMul = partyDmg / maxHp;
                    xpsp[0] *= partyMul;
                    xpsp[1] *= partyMul;
                    xpsp[0] = applyOverhit(killer, xpsp[0]);
                    party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
                }
            }
        }

        // Check the drop of a cursed weapon
        CursedWeaponsManager.getInstance().dropAttackable(this, killer);

        if (topDamager == null || !topDamager.isPlayable()) {
            return;
        }

        for (final Map.Entry<RewardType, RewardList> entry : getTemplate().getRewards().entrySet()) {
            rollRewards(entry, lastAttacker, topDamager);
        }

        if (_isChampion > 0)
            addChampionItems(lastAttacker, topDamager);
    }

    private void addChampionItems(Creature lastAttacker, Creature topDamager) {
        RewardList rewardList = _isChampion == 1 ? blueChampionDrop : redChampionDrop;

        Map.Entry<RewardType, RewardList> entry = new Map.Entry<RewardType, RewardList>() {
            @Override
            public RewardType getKey() {
                return RewardType.NOT_RATED_NOT_GROUPED;
            }

            @Override
            public RewardList getValue() {
                return rewardList;
            }

            @Override
            public RewardList setValue(RewardList value) {
                return rewardList;
            }
        };
        rollRewards(entry, lastAttacker, topDamager, false);
    }

    @Override
    public void onRandomAnimation() {
        if (System.currentTimeMillis() - _lastSocialAction > 10000L) {
            broadcastPacket(new SocialAction(getObjectId(), 1));
            _lastSocialAction = System.currentTimeMillis();
        }
    }

    @Override
    public void startRandomAnimation() {
        //У мобов анимация обрабатывается в AI
    }

    @Override
    public int getKarma() {
        return 0;
    }

    public void addAbsorber(final Player attacker) {
        // The attacker must not be null
        if (attacker == null) {
            return;
        }

        if (getCurrentHpPercents() > 50) {
            return;
        }

        absorbLock.lock();
        try {
            if (_absorbersIds == null) {
                _absorbersIds = new TIntHashSet();
            }

            _absorbersIds.add(attacker.getObjectId());
        } finally {
            absorbLock.unlock();
        }
    }

    public boolean isAbsorbed(final Player player) {
        absorbLock.lock();
        try {
            if (_absorbersIds == null) {
                return false;
            }
            if (!_absorbersIds.contains(player.getObjectId())) {
                return false;
            }
        } finally {
            absorbLock.unlock();
        }
        return true;
    }

    public void clearAbsorbers() {
        absorbLock.lock();
        try {
            if (_absorbersIds != null) {
                _absorbersIds.clear();
            }
        } finally {
            absorbLock.unlock();
        }
    }

    public RewardItem takeHarvest() {
        harvestLock.lock();
        try {
            final RewardItem harvest;
            harvest = _harvestItem;
            clearHarvest();
            return harvest;
        } finally {
            harvestLock.unlock();
        }
    }

    public void clearHarvest() {
        harvestLock.lock();
        try {
            _harvestItem = null;
            _altSeed = false;
            _seederId = 0;
            _isSeeded = false;
        } finally {
            harvestLock.unlock();
        }
    }

    public boolean setSeeded(final Player player, final int seedId, final boolean altSeed) {
        harvestLock.lock();
        try {
            if (isSeeded()) {
                return false;
            }
            _isSeeded = true;
            _altSeed = altSeed;
            _seederId = player.getObjectId();
            _harvestItem = new RewardItem(Manor.getInstance().getCropType(seedId));
            // Количество всходов от xHP до (xHP + xHP/2)
            if (getTemplate().rateHp > 1) {
                _harvestItem.count = Rnd.get(Math.round(getTemplate().rateHp), Math.round(1.5 * getTemplate().rateHp * ServerConfig.RATE_MANOR));
            }
        } finally {
            harvestLock.unlock();
        }

        return true;
    }

    public boolean isSeeded(final Player player) {
        //засиден этим игроком, и смерть наступила не более 20 секунд назад
        return isSeeded() && _seederId == player.getObjectId() && getDeadTime() < 20000L;
    }

    public boolean isSeeded() {
        return _isSeeded;
    }

    /**
     * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
     */
    public boolean isSpoiled() {
        return _isSpoiled;
    }

    public boolean isSpoiled(final Player player) {
        if (!isSpoiled()) // если не заспойлен то false
        {
            return false;
        }

        //заспойлен этим игроком, и смерть наступила не более 20 секунд назад
        if (player.getObjectId() == spoilerId && getDeadTime() < 20000L) {
            return true;
        }

        if (player.isInParty()) {
            for (final Player pm : player.getParty().getPartyMembers()) {
                if (pm.getObjectId() == spoilerId && getDistance(pm) < AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Set the spoil state of this L2NpcInstance.<BR><BR>
     *
     * @param player
     */
    public boolean setSpoiled(final Player player) {
        sweepLock.lock();
        try {
            if (isSpoiled()) {
                return false;
            }
            _isSpoiled = true;
            spoilerId = player.getObjectId();
        } finally {
            sweepLock.unlock();
        }
        return true;
    }

    /**
     * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
     */
    public boolean isSweepActive() {
        sweepLock.lock();
        try {
            return _sweepItems != null && !_sweepItems.isEmpty();
        } finally {
            sweepLock.unlock();
        }
    }

    public List<RewardItem> takeSweep() {
        sweepLock.lock();
        try {
            final List<RewardItem> sweep = _sweepItems;
            clearSweep();
            return sweep;
        } finally {
            sweepLock.unlock();
        }
    }

    public void clearSweep() {
        sweepLock.lock();
        try {
            _isSpoiled = false;
            spoilerId = 0;
            _sweepItems = null;
        } finally {
            sweepLock.unlock();
        }
    }

    protected Player lockDropTo(final Player topDamager) {
        return topDamager;
    }

    protected void rollRewards(final Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, final Creature topDamager) {
        rollRewards(entry, lastAttacker, topDamager, true);
    }

    protected void rollRewards(final Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, final Creature topDamager, boolean useModifer) {
        final Creature activeChar;
        final Player activePlayer;

        final RewardType type = entry.getKey();
        if (type == RewardType.SWEEP) {
            if (!isSpoiled()) {
                return;
            }
            activeChar = lastAttacker;
            activePlayer = lastAttacker.getPlayer();
        } else {
            activeChar = topDamager;
            activePlayer = lockDropTo(topDamager.getPlayer());
        }

        if (activePlayer == null || !activePlayer.isConnected()) {
            return;
        }

        final int diff = calculateLevelDiffForDrop(topDamager.getLevel());
        double mod = useModifer ? calcStat(Stats.REWARD_MULTIPLIER, 1., activeChar, null) : 1.;
        mod *= PlayerUtils.penaltyModifier(diff, 9);

        final RewardList list = entry.getValue();
        final List<RewardItem> rewardItems = list.roll(activePlayer, mod, this instanceof RaidBossInstance);
        switch (type) {
            case SWEEP:
                _sweepItems = rewardItems;
                break;
            default:
                for (final RewardItem drop : rewardItems) {
                    // Если в моба посеяно семя, причем не альтернативное - не давать никакого дропа, кроме адены.
                    if (isSeeded() && !_altSeed && !drop.isAdena) {
                        continue;
                    }
                    dropItem(activePlayer, drop.itemId, drop.count);
                }
                break;
        }
    }

    private void doCrpReward(Creature killer) {
        Creature topDamager;
        try {
            topDamager = getAggroList().getTopDamager();
        } catch (NullPointerException e) {
            topDamager = killer;
        }
        if (topDamager == null || topDamager.getPlayer() == null || !crpRewardMap.containsKey(getNpcId()))
            return;
        Player player = topDamager.getPlayer();
        Party party = player.getParty();
        if (party != null && party.getCommandChannel() != null)
            addCrpReward(party.getCommandChannel());
        else if (party != null)
            addCrpReward(party);
        else
            addCrpReward(player);
    }

    private void addCrpReward(Player player) {
        if (player == null || player.getClan() == null)
            return;
        AggroList list = getAggroList();
        AggroList.AggroInfo playerInfo = list.get(player);
        if (playerInfo == null)
            return;
        AggroList.AggroInfo aggroInfo = list.get(list.getMostHated());
        if (aggroInfo == null)
            return;
        int maxHate = aggroInfo.hate;
        if (maxHate == 0)
            return;
        if (Util.percentOf(maxHate, playerInfo.hate) > LostDreamCustom.crpRewardMinHatePercent)
            player.getClan().incReputation(crpRewardMap.get(getNpcId()), false, "Killing crp-config monster");
    }

    private void addCrpReward(Party party) {
        for (Player player : party.getPartyMembers())
            addCrpReward(player);
    }

    private void addCrpReward(CommandChannel cc) {
        for (Party party : cc.getParties())
            addCrpReward(party);
    }

    private double[] calculateExpAndSp(final int level, final long damage) {
        int diff = level - getLevel();
        if (level > 77 && diff > 3 && diff <= 5) // kamael exp penalty
        {
            diff += 3;
        }

        double xp = getExpReward() * damage / getMaxHp();
        double sp = getSpReward() * damage / getMaxHp();

        if (diff > 5) {
            final double mod = Math.pow(.83, diff - 5);
            xp *= mod;
            sp *= mod;
        }

        xp = Math.max(0., xp);
        sp = Math.max(0., sp);

        return new double[]{xp, sp};
    }

    private double applyOverhit(final Player killer, double xp) {
        if (xp > 0 && killer.getObjectId() == overhitAttackerId) {
            final int overHitExp = calculateOverhitExp(xp);
            killer.sendPacket(SystemMsg.OVERHIT, new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S1_BONUS_EXPERIENCE_FROM_A_SUCCESSFUL_OVERHIT).addNumber(overHitExp));
            xp += overHitExp;
        }
        return xp;
    }

    @Override
    public void setOverhitAttacker(final Creature attacker) {
        overhitAttackerId = attacker == null ? 0 : attacker.getObjectId();
    }

    public double getOverhitDamage() {
        return _overhitDamage;
    }

    @Override
    public void setOverhitDamage(final double damage) {
        _overhitDamage = damage;
    }

    public int calculateOverhitExp(final double normalExp) {
        double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
        if (overhitPercentage > 25) {
            overhitPercentage = 25;
        }
        final double overhitExp = overhitPercentage / 100 * normalExp;
        setOverhitAttacker(null);
        setOverhitDamage(0);
        return (int) Math.round(overhitExp);
    }

    @Override
    public boolean isAggressive() {
        return (AllSettingsConfig.ALT_CHAMPION_CAN_BE_AGGRO || getChampion() == 0) && super.isAggressive();
    }

    @Override
    public Faction getFaction() {
        return AllSettingsConfig.ALT_CHAMPION_CAN_BE_SOCIAL || getChampion() == 0 ? super.getFaction() : Faction.NONE;
    }

    @Override
    public boolean isMonster() {
        return true;
    }

    @Override
    public Clan getClan() {
        return null;
    }

    public boolean isIgnoreDrop() {
        return false;
    }

    public boolean isIgnoreCrp() {
        return false;
    }

    @Override
    public double getMaxHp() {
        return super.getMaxHp();
    }

    protected static final class RewardInfo {
        protected final Creature _attacker;
        protected int _dmg = 0;

        public RewardInfo(final Creature attacker, final int dmg) {
            _attacker = attacker;
            _dmg = dmg;
        }

        public void addDamage(int dmg) {
            if (dmg < 0) {
                dmg = 0;
            }

            _dmg += dmg;
        }

        @Override
        public int hashCode() {
            return _attacker.getObjectId();
        }
    }
}