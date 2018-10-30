package org.mmocore.gameserver.model.instances;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.HeroDiary;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.team.PlayerGroup;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList.HateInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.PlayerUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class RaidBossInstance extends MonsterWithLongRespawnInstance {

    private static final long serialVersionUID = 1L;

    private static final long LOCK_RESET_INTERVAL = 300000L;
    private static final int MINION_UNSPAWN_INTERVAL = 5000; //time to unspawn minions when boss is dead, msec
    private static final int[] specialBoss = new int[]{25205, 25229, 25248, 25281, 25305, 25315, 25450, 25701, 29062, 29096};
    private final AtomicBoolean _channelLocked = new AtomicBoolean();
    protected ScheduledFuture<?> _channelLockCheckTask = null;
    private CommandChannel _lockedCommandChannel = null;
    private long _lastAttackTimeStamp = 0;

    public RaidBossInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isRaid() {
        return true;
    }

    protected int getMinionUnspawnInterval() {
        return MINION_UNSPAWN_INTERVAL;
    }

    protected int getMinChannelSizeForLock() {
        return LostDreamCustom.ccMinLockableSize == 0 ? 18 : LostDreamCustom.ccMinLockableSize;
    }

    protected void onChannelLock(final String leaderName) {
        broadcastPacket(new ExShowScreenMessage(NpcString.S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false, leaderName));
    }

    protected void onChannelUnlock() {
        broadcastPacket(new ExShowScreenMessage(NpcString.LOOTING_RULES_ARE_NO_LONGER_ACTIVE, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake,
                                     final boolean standUp, final boolean directHp, final boolean lethal) {
        _lastAttackTimeStamp = System.currentTimeMillis();

        if (getMinChannelSizeForLock() > 0) {
            final Player activePlayer = attacker.getPlayer();
            if (activePlayer != null) {
                final PlayerGroup pg = activePlayer.getPlayerGroup();
                if (pg instanceof CommandChannel) {
                    if (LostDreamCustom.ccNonLockableBosses != null && ArrayUtils.contains(LostDreamCustom.ccNonLockableBosses, getNpcId()))
                        return;
                    final CommandChannel cc = (CommandChannel) pg;
                    final Player leader = cc.getGroupLeader();
                    if (!_channelLocked.get() && leader != null && cc.getMemberCount() >= getMinChannelSizeForLock()) {
                        if (_channelLocked.compareAndSet(false, true)) {
                            _lockedCommandChannel = cc;
                            _channelLockCheckTask = ThreadPoolManager.getInstance().schedule(new ChannelLockCheckTask(), LOCK_RESET_INTERVAL);
                            onChannelLock(leader.getName());
                        }
                    }
                }
            }
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    @Override
    protected Player lockDropTo(final Player topDamager) {
        final CommandChannel cc = _lockedCommandChannel;
        if (cc != null) {
            final Player leader = cc.getGroupLeader();
            if (leader != null) {
                return leader;
            }
        }

        return topDamager;
    }

    @Override
    protected void onDeath(final Creature killer) {
        if (_channelLockCheckTask != null) {
            _channelLockCheckTask.cancel(false);
            _channelLockCheckTask = null;
        }

        final int points = getTemplate().rewardRp;
        if (points > 0) {
            calcRaidPointsReward(points);
        }

        if (this instanceof ReflectionBossInstance) {
            super.onDeath(killer);
            return;
        }

        if (killer.isPlayable()) {
            final Player player = killer.getPlayer();
            if (player.isInParty()) {
                for (final Player member : player.getParty().getPartyMembers()) {
                    if (member.isNoble()) {
                        Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
                    }
                    if (member.getCastle() != null) {
                        checkSpecialBoss(member.getCastle().getId());
                    }
                }
                player.getParty().broadCast(SystemMsg.CONGRATULATIONS_RAID_WAS_SUCCESSFUL);
            } else {
                if (player.isNoble()) {
                    Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
                }
                if (player.getCastle() != null) {
                    checkSpecialBoss(player.getCastle().getId());
                }
                player.sendPacket(SystemMsg.CONGRATULATIONS_RAID_WAS_SUCCESSFUL);
            }

            final Quest q = QuestManager.getQuest(508);
            if (q != null) {
                final Clan cl = player.getClan();
                if (cl != null && cl.getLeader().isOnline() && cl.getLeader().getPlayer().getQuestState(q) != null) {
                    final QuestState st = cl.getLeader().getPlayer().getQuestState(q);
                    st.getQuest().onKill(this, st);
                }
            }
        }

        if (hasPrivates() && getPrivatesList().hasAlivePrivates()) {
            ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                @Override
                public void runImpl() {
                    if (isDead()) {
                        getPrivatesList().unspawnPrivates();
                    }
                }
            }, getMinionUnspawnInterval());
        }
        super.onDeath(killer);
    }

    @Override
    protected void onDecay() {
        if (_channelLockCheckTask != null) {
            _channelLockCheckTask.cancel(false);
            _channelLockCheckTask = null;
        }
        _lockedCommandChannel = null;
        _channelLocked.set(false);

        super.onDecay();
    }

    //FIXME [G1ta0] разобрать этот хлам
    @SuppressWarnings("unchecked")
    private void calcRaidPointsReward(final int totalPoints) {
        // Object groupkey (L2Party/L2CommandChannel/L2Player) | [List<L2Player> group, Long GroupDdamage]
        final Map<Object, Object[]> participants = new HashMap<>();
        final double totalHp = getMaxHp();

        // Разбиваем игроков по группам. По возможности используем наибольшую из доступных групп: Command Channel → Party → StandAlone (сам плюс пет :)
        for (final HateInfo ai : getAggroList().getPlayableMap().values()) {
            final Player player = ai.attacker.getPlayer();
            final Object key = player.getParty() != null ? player.getParty().getCommandChannel() != null ? player.getParty().getCommandChannel() : player.getParty() : player.getPlayer();
            Object[] info = participants.get(key);
            if (info == null) {
                info = new Object[]{new HashSet<Player>(), (long) 0};
                participants.put(key, info);
            }

            // если это пати или командный канал то берем оттуда весь список участвующих, даже тех кто не в аггролисте
            // дубликаты не страшны - это хашсет
            if (key instanceof CommandChannel) {
                for (final Player p : ((CommandChannel) key)) {
                    if (p.isInRangeZ(this, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                        ((Set<Player>) info[0]).add(p);
                    }
                }
            } else if (key instanceof Party) {
                for (final Player p : ((Party) key).getPartyMembers()) {
                    if (p.isInRangeZ(this, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                        ((Set<Player>) info[0]).add(p);
                    }
                }
            } else {
                ((Set<Player>) info[0]).add(player);
            }

            info[1] = (Long) info[1] + ai.damage;
        }

        for (final Object[] groupInfo : participants.values()) {
            final Set<Player> players = (HashSet<Player>) groupInfo[0];
            // это та часть, которую игрок заслужил дамагом группы, но на нее может быть наложен штраф от уровня игрока
            final int perPlayer = (int) Math.round(totalPoints * (Long) groupInfo[1] / (totalHp * players.size()));
            for (final Player player : players) {
                int playerReward = perPlayer;
                // применяем штраф если нужен
                playerReward = (int) Math.round(playerReward * PlayerUtils.penaltyModifier(calculateLevelDiffForDrop(player.getLevel()), 9));
                if (playerReward == 0) {
                    continue;
                }
                player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_RAID_POINTS).addNumber(playerReward));
                RaidBossSpawnManager.getInstance().addPoints(player.getObjectId(), getNpcId(), playerReward);
            }
        }

        RaidBossSpawnManager.getInstance().updatePointsDb();
        RaidBossSpawnManager.getInstance().calculateRanking();
    }

    private void checkSpecialBoss(final int castleId) {
        if (ArrayUtils.contains(specialBoss, getNpcId())) {
            switch (castleId) {
                case 1:
                    if (GameObjectsStorage.getByNpcId(32074) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_gludio");
                    }
                    break;
                case 2:
                    if (GameObjectsStorage.getByNpcId(32082) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_dion");
                    }
                    break;
                case 3:
                    if (GameObjectsStorage.getByNpcId(32083) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_giran");
                    }
                    break;
                case 4:
                    if (GameObjectsStorage.getByNpcId(32084) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_oren");
                    }
                    break;
                case 5:
                    if (GameObjectsStorage.getByNpcId(32085) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_aden");
                    }
                    break;
                case 6:
                    if (GameObjectsStorage.getByNpcId(32086) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_innadril");
                    }
                    break;
                case 7:
                    if (GameObjectsStorage.getByNpcId(32088) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_godard");
                    }
                    break;
                case 8:
                    if (GameObjectsStorage.getByNpcId(32089) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_rune");
                    }
                    break;
                case 9:
                    if (GameObjectsStorage.getByNpcId(32087) == null) {
                        SpawnManager.getInstance().spawn("adv_manager_schuttgart");
                    }
                    break;
            }
        }
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean hasRandomWalk() {
        return false;
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    private final class ChannelLockCheckTask extends RunnableImpl {
        @Override
        public final void runImpl() {
            final long nextCheckInterval = LOCK_RESET_INTERVAL - (System.currentTimeMillis() - _lastAttackTimeStamp);
            if (nextCheckInterval < 1000L || _lockedCommandChannel == null || _lockedCommandChannel.getGroupLeader() == null) {
                _channelLockCheckTask = null;
                _lockedCommandChannel = null;
                _channelLocked.set(false);
                onChannelUnlock();
                return;
            }
            _channelLockCheckTask = ThreadPoolManager.getInstance().schedule(this, nextCheckInterval);
        }
    }
}