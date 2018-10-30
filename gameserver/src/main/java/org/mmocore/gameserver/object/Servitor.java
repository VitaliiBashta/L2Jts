package org.mmocore.gameserver.object;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.jts.dataparser.data.holder.PCParameterHolder;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.ServitorAI;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.PvpConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.components.creatures.recorders.SummonStatsChangeRecorder;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.PetInventory;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.taskmanager.DecayTaskManager;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import static org.mmocore.gameserver.network.lineage.serverpackets.ExSetCompassZoneCode.ZONE_PVP_FLAG;
import static org.mmocore.gameserver.network.lineage.serverpackets.ExSetCompassZoneCode.ZONE_SIEGE_FLAG;

public abstract class Servitor extends Playable {
    public static final int[] BEAST_SHOTS = {6645, 6646, 6647, 20332, 20333, 20334};
    public static final int SUMMON_TYPE = 1;
    public static final int PET_TYPE = 2;
    private static final long serialVersionUID = -4706907944929436872L;
    private static final int SUMMON_DISAPPEAR_RANGE = 2500;
    private final Player _owner;
    protected long _exp = 0;
    protected int _sp = 0;
    private int _spawnAnimation = 2;
    private int _spsCharged;
    private boolean _follow = true, _depressed, _ssCharged;

    private Future<?> _decayTask;
    private Future<?> _updateEffectIconsTask;
    private ScheduledFuture<?> _broadcastCharInfoTask;
    private Future<?> _petInfoTask;

    protected Servitor(int objectId, NpcTemplate template, Player owner) {
        super(objectId, template);
        setTitle(owner.getName());
        _owner = owner;

        if (!template.getSkills().isEmpty()) {
            template.getSkills().valueCollection().forEach(this::addSkill);
        }

        setXYZ(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        _spawnAnimation = 0;

        Player owner = getPlayer();
        Party party = owner.getParty();
        if (party != null) {
            party.broadcastToPartyMembers(owner, new ExPartyPetWindowAdd(this));
        }
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public ServitorAI getAI() {
        if (_ai == null)
            synchronized (this) {
                if (_ai == null) {
                    _ai = new ServitorAI(this);
                }
            }

        return (ServitorAI) _ai;
    }

    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate) _template;
    }

    @Override
    public boolean isUndead() {
        return getTemplate().isUndead();
    }

    public abstract int getServitorType();

    /**
     * @return Returns the mountable.
     */
    public boolean isMountable() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAction(final Player player, boolean shift) {
        if (isFrozen()) {
            player.sendPacket(ActionFail.STATIC);
            return;
        }

        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, (Class<Servitor>) getClass(), this, true)) {
            return;
        }

        Player owner = getPlayer();

        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(getObjectId(), 0), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP, StatusUpdate.CUR_MP, StatusUpdate.MAX_MP));
            } else {
                player.sendPacket(ActionFail.STATIC);
            }
        } else if (player == owner) {
            player.sendPacket(new PetInfo(this).update());

            if (!isInRangeZ(player, getInteractDistance(player))) {
                if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
                }
                return;
            }

            if (!player.isActionsDisabled()) {
                player.sendPacket(new PetStatusShow(this));
            }

            player.sendPacket(ActionFail.STATIC);
        } else if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
        } else {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
                if (!shift) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, AllSettingsConfig.FOLLOW_RANGE);
                } else {
                    player.sendActionFailed();
                }
            } else {
                player.sendActionFailed();
            }
        }
    }

    public long getExpForThisLevel() {
        return ExpDataHolder.getInstance().getExpForLevel(getLevel());
    }

    public long getExpForNextLevel() {
        return ExpDataHolder.getInstance().getExpForLevel(getLevel() + 1);
    }

    @Override
    public int getNpcId() {
        return getTemplate().npcId;
    }

    public final long getExp() {
        return _exp;
    }

    public final void setExp(final long exp) {
        _exp = exp;
    }

    public final int getSp() {
        return _sp;
    }

    public void setSp(final int sp) {
        _sp = sp;
    }

    @Override
    public int getBuffLimit() {
        Player owner = getPlayer();
        return (int) calcStat(Stats.BUFF_LIMIT, owner.getBuffLimit(), null, null);
    }

    public abstract int getCurrentFed();

    public abstract int getMaxFed();

    public abstract void saveEffects();

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        startDecay(8500L);

        Player owner = getPlayer();

        if (killer == null || killer == owner || killer == this || isInZoneBattle() || killer.isInZoneBattle()) {
            return;
        }

        if (killer instanceof Servitor) {
            killer = killer.getPlayer();
        }

        if (killer == null) {
            return;
        }

        if (killer.isPlayer()) {
            Player pk = (Player) killer;

            if (isInZone(ZoneType.SIEGE)) {
                return;
            }

            final SingleMatchEvent matchEvent = getEvent(SingleMatchEvent.class);
            if (owner.getPvpFlag() > 0 || owner.atMutualWarWith(pk)) {
                pk.setPvpKills(pk.getPvpKills() + 1);
            } else if ((matchEvent == null || matchEvent != pk.getEvent(SingleMatchEvent.class)) && getKarma() <= 0) {
                int pkCountMulti = Math.max(pk.getPkKills() / 2, 1);
                pk.increaseKarma(PvpConfig.KARMA_MIN_KARMA * pkCountMulti);
            }

            // Send a Server->Client UserInfo packet to attacker with its PvP Kills Counter
            pk.sendChanges();
        }
    }

    protected void startDecay(long delay) {
        stopDecay();
        _decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
    }

    protected void stopDecay() {
        if (_decayTask != null) {
            _decayTask.cancel(false);
            _decayTask = null;
        }
    }

    @Override
    protected void onDecay() {
        deleteMe();
    }

    public void endDecayTask() {
        stopDecay();
        doDecay();
    }

    @Override
    public void broadcastStatusUpdate() {
        if (!needStatusUpdate()) {
            return;
        }

        Player owner = getPlayer();

        sendStatusUpdate();

        StatusUpdate su = makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.CUR_HP);
        broadcastToStatusListeners(su);

        Party party = owner.getParty();
        if (party != null) {
            party.broadcastToPartyMembers(owner, new ExPartyPetWindowUpdate(this));
        }
    }

    public void sendStatusUpdate() {
        Player owner = getPlayer();
        owner.sendPacket(new PetStatusUpdate(this));
    }

    @Override
    protected void onUpdateZones(List<Zone> leaving, List<Zone> entering) {
        super.onUpdateZones(leaving, entering);

        final boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
        final boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;

        final boolean isInCombatZone = isInCombatZone();
        final boolean isOnSiegeField = isOnSiegeField();

        _zoneMask = 0;

        if (isInCombatZone) {
            _zoneMask |= ZONE_PVP_FLAG;
        }
        if (isOnSiegeField) {
            _zoneMask |= ZONE_SIEGE_FLAG;
        }

        if (lastInCombatZone != isInCombatZone || lastOnSiegeField != isOnSiegeField) {
            broadcastRelationChanged();
        }
    }

    @Override
    protected void onDelete() {
        Player owner = getPlayer();

        Party party = owner.getParty();
        if (party != null) {
            party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
        }
        owner.sendPacket(new PetDelete(getObjectId(), getServitorType()));
        owner.setServitor(null);

        for (int itemId : BEAST_SHOTS) {
            if (owner.getAutoSoulShot().contains(itemId)) {
                owner.removeAutoSoulShot(itemId);
                owner.sendPacket(new ExAutoSoulShot(itemId, false));
            }
        }

        stopDecay();
        super.onDelete();
    }

    public void unSummon(boolean saveEffects, boolean store) {
        if (saveEffects) {
            saveEffects();
        }

        deleteMe();
    }

    public boolean isFollowMode() {
        return _follow;
    }

    public void setFollowMode(boolean state) {
        Player owner = getPlayer();

        _follow = state;

        getAI().clearStoredIntention();
        if (_follow) {
            if (getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || (getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW && getFollowTarget() != owner)) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, 60 + (int) owner.getColRadius());
            }
        } else if (getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW && getFollowTarget() == owner) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public void updateEffectIcons() {
        if (ServerConfig.USER_INFO_INTERVAL == 0) {
            if (_updateEffectIconsTask != null) {
                _updateEffectIconsTask.cancel(false);
                _updateEffectIconsTask = null;
            }
            updateEffectIconsImpl();
            return;
        }

        if (_updateEffectIconsTask != null) {
            return;
        }

        _updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new UpdateEffectIcons(), ServerConfig.USER_INFO_INTERVAL);
    }

    public void updateEffectIconsImpl() {
        Player owner = getPlayer();
        PartySpelled ps = new PartySpelled(this, true);
        Party party = owner.getParty();
        if (party != null) {
            party.broadCast(ps);
        } else {
            owner.sendPacket(ps);
        }
    }

    public int getControlItemObjId() {
        return 0;
    }

    @Override
    public PetInventory getInventory() {
        return null;
    }

    @Override
    public void doPickupItem(final GameObject object) {
    }

    @Override
    public void doRevive() {
        super.doRevive();
        setRunning();
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        setFollowMode(true);
    }

    /**
     * Return null.<BR><BR>
     */
    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public abstract void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic);

    @Override
    public boolean unChargeShots(final boolean spirit) {
        Player owner = getPlayer();

        if (spirit) {
            if (_spsCharged != 0) {
                _spsCharged = 0;
                owner.autoShot();
                return true;
            }
        } else if (_ssCharged) {
            _ssCharged = false;
            owner.autoShot();
            return true;
        }

        return false;
    }

    @Override
    public boolean getChargedSoulShot() {
        return _ssCharged;
    }

    @Override
    public int getChargedSpiritShot() {
        return _spsCharged;
    }

    public void chargeSoulShot() {
        _ssCharged = true;
    }

    public void chargeSpiritShot(final int state) {
        _spsCharged = state;
    }

    public int getSoulshotConsumeCount() {
        return 0;
    }

    public int getSpiritshotConsumeCount() {
        return 0;
    }

    public boolean isDepressed() {
        return _depressed;
    }

    public void setDepressed(final boolean depressed) {
        _depressed = depressed;
    }

    public boolean isInRange() {
        Player owner = getPlayer();
        return getDistance(owner) < SUMMON_DISAPPEAR_RANGE;
    }

    public void teleportToOwner() {
        Player owner = getPlayer();

        setNonAggroTime(System.currentTimeMillis() + AiConfig.NONAGGRO_TIME_ONTELEPORT);
        teleToLocation(owner.getLoc(), owner.getReflection());

        if (!isDead() && _follow) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, AllSettingsConfig.FOLLOW_RANGE);
        }
    }

    public void moveToOwner() {
        //final Player owner = getPlayer();
        //moveToLocation(owner.getLoc(), 50, true);
        setRunning();
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        setFollowMode(true);
    }

    @Override
    public void broadcastCharInfo() {
        if (_broadcastCharInfoTask != null) {
            return;
        }

        _broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), ServerConfig.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public void broadcastCharInfoImpl() {
        Player owner = getPlayer();

        for (Player player : World.getAroundObservers(this)) {
            if (player == owner) {
                player.sendPacket(new PetInfo(this).update());
                player.sendPacket(new NpcInfo(this, player).update());
            } else {
                player.sendPacket(new NpcInfo(this, player).update());
            }
        }
    }

    private void sendPetInfoImpl() {
        Player owner = getPlayer();
        owner.sendPacket(new PetInfo(this).update());
    }

    public void sendPetInfo() {
        if (ServerConfig.USER_INFO_INTERVAL == 0) {
            if (_petInfoTask != null) {
                _petInfoTask.cancel(false);
                _petInfoTask = null;
            }
            sendPetInfoImpl();
            return;
        }

        if (_petInfoTask != null) {
            return;
        }

        _petInfoTask = ThreadPoolManager.getInstance().schedule(new PetInfoTask(), ServerConfig.USER_INFO_INTERVAL);
    }

    /**
     * Нужно для отображения анимации спауна, используется в пакете NpcInfo, PetInfo:
     * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
     */
    public int getSpawnAnimation() {
        return _spawnAnimation;
    }

    @Override
    public void startPvPFlag(Creature target) {
        Player owner = getPlayer();
        owner.startPvPFlag(target);
    }

    @Override
    public int getPvpFlag() {
        Player owner = getPlayer();
        return owner.getPvpFlag();
    }

    @Override
    public int getKarma() {
        Player owner = getPlayer();
        return owner.getKarma();
    }

    @Override
    public TeamType getTeam() {
        Player owner = getPlayer();
        return owner.getTeam();
    }

    @Override
    public Player getPlayer() {
        return _owner;
    }

    public abstract double getExpPenalty();

    @Override
    public synchronized SummonStatsChangeRecorder initializeStatsRecorder() {
        return new SummonStatsChangeRecorder(this);
    }

    @Override
    public SummonStatsChangeRecorder getStatsRecorder() {
        return (SummonStatsChangeRecorder) _statsRecorder;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        List<L2GameServerPacket> list = new ArrayList<>();
        Player owner = getPlayer();

        if (owner == forPlayer) {
            list.add(new PetInfo(this));
            list.add(new PartySpelled(this, true));

            if (isPet()) {
                list.add(new PetItemList((PetInstance) this));
            }
        } else {
            Party party = forPlayer.getParty();
            if (getReflection() == ReflectionManager.GIRAN_HARBOR && (owner == null || party == null || party != owner.getParty())) {
                return list;
            }
            list.add(new NpcInfo(this, forPlayer));
            if (owner != null && party != null && party == owner.getParty()) {
                list.add(new PartySpelled(this, true));
            }
            list.add(RelationChanged.update(forPlayer, this, forPlayer));
        }

        if (isInCombat()) {
            list.add(new AutoAttackStart(getObjectId()));
        }

        if (isMoving || isFollow) {
            list.add(movePacket());
        }
        return list;
    }

    @Override
    public void startAttackStanceTask() {
        startAttackStanceTask0();
        Player player = getPlayer();
        if (player != null) {
            player.startAttackStanceTask0();
        }
    }

    @Override
    public <E extends Event> E getEvent(Class<E> eventClass) {
        Player player = getPlayer();
        if (player != null) {
            return player.getEvent(eventClass);
        } else {
            return super.getEvent(eventClass);
        }
    }

    @Override
    public Set<Event> getEvents() {
        Player player = getPlayer();
        if (player != null) {
            return player.getEvents();
        } else {
            return super.getEvents();
        }
    }

    @Override
    public void sendReuseMessage(SkillEntry skill) {
        Player player = getPlayer();
        if (player != null) {
            player.sendPacket(SystemMsg.THAT_PET_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING);
        }
    }

    @Override
    public boolean startFear() {
        final boolean result = super.startFear();
        if (!result) {
            getAI().storeIntention();
        }

        return result;
    }

    @Override
    public boolean stopFear() {
        final boolean result = super.stopFear();
        if (!result) {
            getAI().restoreIntention();
        }

        return result;
    }

    @Override
    public boolean startRooted() {
        final boolean result = super.startRooted();
        if (!result) {
            getAI().storeIntention();
        }

        return result;
    }

    @Override
    public boolean stopRooted() {
        final boolean result = super.stopRooted();
        if (!result) {
            getAI().restoreIntention();
        }

        return result;
    }

    @Override
    public boolean startSleeping() {
        final boolean result = super.startSleeping();
        if (!result) {
            getAI().storeIntention();
        }

        return result;
    }

    @Override
    public boolean stopSleeping() {
        final boolean result = super.stopSleeping();
        if (!result) {
            getAI().restoreIntention();
        }

        return result;
    }

    @Override
    public boolean startStunning() {
        final boolean result = super.startStunning();
        if (!result) {
            getAI().storeIntention();
        }

        return result;
    }

    @Override
    public boolean stopStunning() {
        final boolean result = super.stopStunning();
        if (!result) {
            getAI().restoreIntention();
        }

        return result;
    }

    @Override
    public boolean startParalyzed() {
        final boolean result = super.startFear();
        if (!result) {
            getAI().storeIntention();
        }

        return result;
    }

    @Override
    public boolean stopParalyzed() {
        final boolean result = super.stopFear();
        if (!result) {
            getAI().restoreIntention();
        }

        return result;
    }

    @Override
    public int getSpeed(int baseSpeed) {
        if (isPet() && ((PetInstance) this).isHungryPet()) {
            baseSpeed = _template.getBaseWalkSpd();
        }
        return super.getSpeed(baseSpeed);
    }

    @Override
    public int getRunSpeed() {
        return getSpeed(_template.getBaseRunSpd());
    }

    @Override
    public int getWalkSpeed() {
        return getSpeed(_template.getBaseWalkSpd());
    }

    @Override
    public double getLevelMod() {
        return PCParameterHolder.getInstance().getLevelBonus().returnValue(getLevel());
    }

    private class UpdateEffectIcons extends RunnableImpl {
        @Override
        public void runImpl() {
            updateEffectIconsImpl();
            _updateEffectIconsTask = null;
        }
    }

    public class BroadcastCharInfoTask extends RunnableImpl {
        @Override
        public void runImpl() {
            broadcastCharInfoImpl();
            _broadcastCharInfoTask = null;
        }
    }

    private class PetInfoTask extends RunnableImpl {
        @Override
        public void runImpl() {
            sendPetInfoImpl();
            _petInfoTask = null;
        }
    }
}