package org.mmocore.gameserver.model.instances;

import org.apache.commons.lang3.tuple.Pair;
import org.jts.dataparser.data.holder.PCParameterHolder;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.maker.default_maker;
import org.mmocore.gameserver.configuration.config.*;
import org.mmocore.gameserver.data.client.holder.QuestDataHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.bypass.BypassHolder;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.listener.NpcListener;
import org.mmocore.gameserver.manager.DimensionalRiftManager;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.DimensionalRift;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.objects.TerritoryWardObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.enums.Parameter;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem.ExShowBaseAttributeCancelWindow;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExShowVariationCancelWindow;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExShowVariationMakeWindow;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.MoveToPawn;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.NpcListenerList;
import org.mmocore.gameserver.object.components.creatures.recorders.NpcStatsChangeRecorder;
import org.mmocore.gameserver.object.components.creatures.tasks.NotifyAITask;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.npc.AggroList;
import org.mmocore.gameserver.object.components.npc.PrivatesList;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.taskmanager.DecayTaskManager;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.npc.Faction;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class NpcInstance extends Creature {
    public static final String NO_CHAT_WINDOW = "noChatWindow";
    public static final String NO_RANDOM_WALK = "noRandomWalk";
    public static final String NO_CHECK_AGGRESSION = "noCheckAggression";
    public static final String NO_RANDOM_ANIMATION = "noRandomAnimation";
    public static final String TARGETABLE = "TargetEnabled";
    public static final String SHOW_NAME = "showName";
    protected static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);
    private static final long serialVersionUID = 1L;
    private final AggroList _aggroList;
    protected int _spawnAnimation = 2;
    protected boolean _hasRandomAnimation;
    protected boolean _hasRandomWalk;
    protected boolean _hasChatWindow;
    protected boolean _hasCheckAggression;
    protected Future<?> _decayTask;
    protected Spawner _spawn;
    protected Location _spawnedLoc = new Location();
    protected SpawnRange _spawnRange;
    protected boolean _unAggred = false;
    protected long _lastSocialAction;
    private ScheduledFuture<?> _minionRespawnTask;
    private String _fnHi;
    private String _fnYouAreChaotic;
    //param
    private int _param2;
    private int _param3;
    private Creature _param4;
    private int _personalAggroRange = -1;
    private int _level = 0;
    private long _dieTime = 0L;
    private int _currentLHandId;
    private int _currentRHandId;
    private double _currentCollisionRadius;
    private double _currentCollisionHeight;
    private int npcState = 0;
    private Future<?> _animationTask;
    private boolean _isTargetable;
    private boolean _showName;
    private Castle _nearestCastle;
    private Fortress _nearestFortress;
    private ClanHall _nearestClanHall;
    private Dominion _nearestDominion;
    private NpcString _nameNpcString = NpcString.NONE;
    private NpcString _titleNpcString = NpcString.NONE;
    private default_maker maker;
    private NpcInstance _master = null;
    private boolean _ignoreLeaderAction;
    private PrivatesList _privatesList = null;
    private MultiValueSet<String> _parameters = StatsSet.EMPTY;
    private int _displayId = 0;
    private ScheduledFuture<?> _broadcastCharInfoTask;
    private boolean _isBusy;
    private String _busyMessage = "";
    private boolean _isUnderground = false;

    public NpcInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        if (template == null) {
            throw new NullPointerException("No template for Npc. Please check your datapack is setup correctly.");
        }
        setUndying(true);
        setParameters(template.getAIParams());
        _hasRandomAnimation = !getParameter(NO_RANDOM_ANIMATION, false) && ServerConfig.MAX_NPC_ANIMATION > 0;
        _hasRandomWalk = !getParameter(NO_RANDOM_WALK, false);
        _hasCheckAggression = !getParameter(NO_CHECK_AGGRESSION, false);
        setHasChatWindow(!getParameter(NO_CHAT_WINDOW, false));
        setTargetable(getParameter(TARGETABLE, true));
        setShowName(getParameter(SHOW_NAME, true));
        String FN_HI = "fnHi";
        setFnHi(getParameter(FN_HI, null));
        String FN_YOU_ARE_CHAOTIC = "fnYouAreChaotic";
        setFnYouAreChaotic(getParameter(FN_YOU_ARE_CHAOTIC, null));
        if (!template.getSkills().isEmpty()) {
            template.getSkills().valueCollection().forEach(this::addSkill);
        }
        setName(template.name);
        setTitle(template.title);
        // инициализация параметров оружия
        setLHandId(getTemplate().lhand);
        setRHandId(getTemplate().rhand);
        // инициализация коллизий
        setCollisionHeight(getTemplate().getCollisionHeight());
        setCollisionRadius(getTemplate().getCollisionRadius());
        _aggroList = new AggroList(this);
        setFlying(getParameter("isFlying", false));
    }

    public static boolean canBypassCheck(final Player player, final NpcInstance npc) {
        if (npc == null || player.isDead() || !npc.isInRangeZ(player, npc.getInteractDistance(player)) || player.isTerritoryFlagEquipped()) {
            player.sendActionFailed();
            return false;
        }
        return true;
    }

    public static void showCollectionSkillList(final Player player) {
        showAcquireList(AcquireType.COLLECTION, player);
    }

    public static void showFishingSkillList(final Player player) {
        showAcquireList(AcquireType.fishingType(player), player);
    }

    public static void showClanSkillList(final Player player) {
        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            player.sendActionFailed();
            return;
        }

        showAcquireList(AcquireType.CLAN, player);
    }

    public static void showAcquireList(final AcquireType t, final Player player) {
        Collection<SkillLearn> skills = new ArrayList<>();

        if (t == AcquireType.CERTIFICATION) {
            skills.addAll(SkillAcquireHolder.getInstance().getAvailableSkills(player, t).stream().filter(skillLearn -> player.getInventory().getItemByItemId(skillLearn.getItemId()) != null).collect(Collectors.toList()));
        } else {
            skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, t);
        }

        final AcquireSkillList asl = new AcquireSkillList(t, skills.size());

        for (final SkillLearn s : skills) {
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 0);
        }

        if (skills.isEmpty()) {
            player.sendPacket(AcquireSkillDone.STATIC);
            player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    public static void showSubUnitSkillList(final Player player) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Set<SkillLearn> learns = new TreeSet<>();
        for (final SubUnit sub : player.getClan().getAllSubUnits()) {
            learns.addAll(SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.SUB_UNIT, sub));
        }

        final AcquireSkillList asl = new AcquireSkillList(AcquireType.SUB_UNIT, learns.size());

        for (final SkillLearn s : learns) {
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 1, Clan.SUBUNIT_KNIGHT4);
        }

        if (learns.isEmpty()) {
            player.sendPacket(AcquireSkillDone.STATIC);
            player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<NpcInstance> getRef() {
        return (HardReference<NpcInstance>) super.getRef();
    }

    @Override
    public CharacterAI getAI() {
        if (_ai == null) {
            synchronized (this) {
                if (_ai == null) {
                    _ai = getTemplate().getNewAI(this);
                }
            }
        }

        return _ai;
    }

    /**
     * Return the position of the spawned point.<BR><BR>
     * Может возвращать случайную точку, поэтому всегда следует кешировать результат вызова!
     */
    public Location getSpawnedLoc() {
        return getLeader() != null ? getLeader().getLoc() : _spawnedLoc;
    }

    public void setSpawnedLoc(final Location loc) {
        _spawnedLoc = loc;
    }

    public default_maker getDefaultMaker() {
        return maker;
    }

    public void setDefaultMaker(final default_maker maker) {
        this.maker = maker;
    }

    public int getRightHandItem() {
        return _currentRHandId;
    }

    public int getLeftHandItem() {
        return _currentLHandId;
    }

    public void setLHandId(final int newWeaponId) {
        _currentLHandId = newWeaponId;
    }

    public void setRHandId(final int newWeaponId) {
        _currentRHandId = newWeaponId;
    }

    public double getCollisionHeight() {
        return _currentCollisionHeight;
    }

    public void setCollisionHeight(final double offset) {
        _currentCollisionHeight = offset;
    }

    public double getCollisionRadius() {
        return _currentCollisionRadius;
    }

    public void setCollisionRadius(final double collisionRadius) {
        _currentCollisionRadius = collisionRadius;
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp, final boolean directHp, final boolean lethal) {
        if (attacker.isPlayable()) {
            getAggroList().addDamageHate(attacker, (int) damage, 0);
        }
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    @Override
    protected void onDeath(final Creature killer) {
        _dieTime = System.currentTimeMillis();
        if (isMonster() && (((MonsterInstance) this).isSeeded() || ((MonsterInstance) this).isSpoiled())) {
            startDecay(20000L); //TODO: Уточнить...
        } else {
            startDecay(getTemplate().getCorpseTime() > 0 ? getTemplate().getCorpseTime() : 1000);
        }
        // установка параметров оружия и коллизий по умолчанию
        setLHandId(getTemplate().lhand);
        setRHandId(getTemplate().rhand);
        setCollisionHeight(getTemplate().getCollisionHeight());
        setCollisionRadius(getTemplate().getCollisionRadius());
        getAI().stopAITask();
        stopAttackStanceTask();
        stopRandomAnimation();
        stopMinionRespawnTask();
        if (getLeader() != null) {
            minionDie(this, getParameter("respawn_minion", 0));
        }
        super.onDeath(killer);
    }

    public void suicide(Creature owner) {
        if (owner != null) {
            reduceCurrentHp(getMaxHp() + 1, owner, null, true, true, false, false, false, false, false);
        } else {
            reduceCurrentHp(getMaxHp() + 1, this, null, true, true, false, false, false, false, false);
        }
    }

    public long getDeadTime() {
        if (_dieTime <= 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - _dieTime;
    }

    public AggroList getAggroList() {
        return _aggroList;
    }

    public void setLeader(final NpcInstance leader, final boolean ignoreLeaderAction) {
        _master = leader;
        _ignoreLeaderAction = ignoreLeaderAction;
    }

    public NpcInstance getLeader() {
        return _master;
    }

    public void setLeader(final NpcInstance leader) {
        _master = leader;
    }

    @Override
    public boolean isMinion() {
        return getLeader() != null;
    }

    /**
     * Игнорирует оповещения об атаке, расстояние от босса до миниона и тд.
     *
     * @return
     */
    public boolean isIgnoreLeaderAction() {
        return _ignoreLeaderAction;
    }

    @Override
    public void setReflection(final Reflection reflection) {
        super.setReflection(reflection);

        if (hasPrivates()) {
            for (final NpcInstance m : getPrivatesList().getAlivePrivates()) {
                m.setReflection(reflection);
            }
        }
    }

    public void dropItem(final Player lastAttacker, final int itemId, final long itemCount) {
        if (itemCount == 0 || lastAttacker == null) {
            return;
        }

        if (lastAttacker.isAutoLootEnabled() == Player.AUTO_LOOT_ALL_EXCEPT_ARROWS && ItemFunctions.isArrow(itemId)) {
            return;
        }

        ItemInstance item;

        for (long i = 0; i < itemCount; i++) {
            item = ItemFunctions.createItem(itemId);
            getEvents().forEach(item::addEvent);

            // Set the Item quantity dropped if L2ItemInstance is stackable
            if (item.isStackable()) {
                i = itemCount; // Set so loop won't happent again
                item.setCount(itemCount); // Set item count
            }

            if (isRaid() || this instanceof ReflectionBossInstance) {
                final SystemMessage sm;
                if (itemId == ItemTemplate.ITEM_ID_ADENA) {
                    sm = new SystemMessage(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
                    sm.addName(this);
                    sm.addNumber(item.getCount());
                } else {
                    sm = new SystemMessage(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
                    sm.addName(this);
                    sm.addItemName(itemId);
                    sm.addNumber(item.getCount());
                }
                broadcastPacket(sm);
            }

            lastAttacker.doAutoLootOrDrop(item, this);
        }
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return true;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        setCurrentHpMp(getMaxHp(), getMaxMp(), true);

        _dieTime = 0L;
        _spawnAnimation = 0;

        if (getAI().isGlobalAI() || getCurrentRegion() != null && getCurrentRegion().isActive()) {
            getAI().startAITask();
            startRandomAnimation();
        }

        ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_SPAWN));

        getListeners().onSpawn();

        final String Privates = this.getParameter("Privates", null);
        if (Privates != null) {
            if (this.hasPrivates()) {
                this.getPrivatesList().useSpawnPrivates();
            } else {
                ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        getPrivatesList().createPrivates(Privates, true);
                    }
                }, 1500L);
            }
        }

    }

    @Override
    protected void onDespawn() {
        getAggroList().clear();

        getAI().onEvtDeSpawn();
        getAI().stopAITask();
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        stopRandomAnimation();

        super.onDespawn();
    }

    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate) _template;
    }

    @Override
    public int getNpcId() {
        return getTemplate().npcId;
    }

    public void setUnAggred(final boolean state) {
        _unAggred = state;
    }

    /**
     * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR><BR>
     */
    public boolean isAggressive() {
        return getAggroRange() > 0;
    }

    public int getAggroRange() {
        if (_unAggred) {
            return 0;
        }

        if (_personalAggroRange >= 0) {
            return _personalAggroRange;
        }

        return getTemplate().aggroRange;
    }

    /**
     * Устанавливает данному npc новый aggroRange.
     * Если установленый aggroRange < 0, то будет братся аггрорейндж с темплейта.
     *
     * @param aggroRange новый agrroRange
     */
    public void setAggroRange(final int aggroRange) {
        _personalAggroRange = aggroRange;
    }

    /**
     * Возвращает группу социальности
     */
    public Faction getFaction() {
        return getTemplate().getFaction();
    }

    public boolean isInFaction(final NpcInstance npc) {
        return getFaction() == npc.getFaction() && !getFaction().isIgnoreNpcId(npc.getNpcId());
    }

    @Override
    public int getMAtk(final Creature target, final SkillEntry skill) {
        return (int) (super.getMAtk(target, skill) * FormulasConfig.ALT_NPC_MATK_MODIFIER);
    }

    @Override
    public int getPAtk(final Creature target) {
        return (int) (super.getPAtk(target) * FormulasConfig.ALT_NPC_PATK_MODIFIER);
    }

    @Override
    public double getMaxHp() {
        return (int) (super.getMaxHp() * FormulasConfig.ALT_NPC_MAXHP_MODIFIER);
    }

    @Override
    public int getMaxMp() {
        return (int) (super.getMaxMp() * FormulasConfig.ALT_NPC_MAXMP_MODIFIER);
    }

    public long getExpReward() {
        return (long) calcStat(Stats.EXP, getTemplate().rewardExp, null, null);
    }

    public long getSpReward() {
        return (long) calcStat(Stats.SP, getTemplate().rewardSp, null, null);
    }

    @Override
    protected void onDelete() {
        stopDecay();
        if (_spawn != null) {
            _spawn.stopRespawn();
        }
        setSpawn(null);

        if (hasPrivates()) {
            getPrivatesList().deletePrivates();
        }

        super.onDelete();
    }

    public Spawner getSpawn() {
        return _spawn;
    }

    public void setSpawn(final Spawner spawn) {
        _spawn = spawn;
    }

    public final void decayOrDelete() {
        onDecay();
    }

    @Override
    protected void onDecay() {
        super.onDecay();

        _spawnAnimation = 2;

        if (_spawn != null) {
            _spawn.decreaseCount(this);
        } else if (!isMinion()) {
            deleteMe(); // Если этот моб заспавнен не через стандартный механизм спавна значит посмертие ему не положено и он умирает насовсем
        }
    }

    /**
     * Запустить задачу "исчезновения" после смерти
     */
    protected void startDecay(final long delay) {
        stopDecay();
        _decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
    }

    /**
     * Отменить задачу "исчезновения" после смерти
     */
    public void stopDecay() {
        if (_decayTask != null) {
            _decayTask.cancel(false);
            _decayTask = null;
        }
    }

    /**
     * Отменить и завершить задачу "исчезновения" после смерти
     */
    public void endDecayTask() {
        if (_decayTask != null) {
            _decayTask.cancel(false);
            _decayTask = null;
        }
        doDecay();
    }

    @Override
    public boolean isUndead() {
        return getTemplate().isUndead();
    }

    @Override
    public int getLevel() {
        return _level == 0 ? getTemplate().level : _level;
    }

    public void setLevel(final int level) {
        _level = level;
    }

    public int getDisplayId() {
        return _displayId > 0 ? _displayId : getTemplate().displayId;
    }

    public void setDisplayId(final int displayId) {
        _displayId = displayId;
    }

    public boolean isFnHi() {
        if (getFnHi() != null && !getFnHi().isEmpty()) {
            return true;
        }
        return false;
    }

    public String getFnHi() {
        return _fnHi;
    }

    public void setFnHi(final String fnHi) {
        _fnHi = fnHi;
    }

    public String getFnYouAreChaotic() {
        return _fnYouAreChaotic;
    }

    public void setFnYouAreChaotic(final String fnYouAreChaotic) {
        _fnYouAreChaotic = fnYouAreChaotic;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        // regular NPCs dont have weapons instancies
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        final int weaponId = getTemplate().rhand;

        if (weaponId < 1) {
            return null;
        }

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(getTemplate().rhand);

        if (!(item instanceof WeaponTemplate)) {
            return null;
        }

        return (WeaponTemplate) item;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        // regular NPCs dont have weapons instances
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        final int weaponId = getTemplate().lhand;

        if (weaponId < 1) {
            return null;
        }

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(getTemplate().lhand);

        if (!(item instanceof WeaponTemplate)) {
            return null;
        }

        return (WeaponTemplate) item;
    }

    @Override
    public void sendChanges() {
        if (isFlying()) // FIXME
        {
            return;
        }
        super.sendChanges();
    }

    @Override
    public void broadcastCharInfo() {
        if (!isVisible()) {
            return;
        }

        if (_broadcastCharInfoTask != null) {
            return;
        }

        _broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), ServerConfig.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public void broadcastCharInfoImpl() {
        for (final Player player : World.getAroundObservers(this)) {
            player.sendPacket(new NpcInfo(this, player).update());
        }
    }

    // У NPC всегда 2
    public void onRandomAnimation() {
        if (System.currentTimeMillis() - _lastSocialAction > 10000L) {
            broadcastPacket(new SocialAction(getObjectId(), SocialAction.GREETING));
            _lastSocialAction = System.currentTimeMillis();
        }
    }

    public void startRandomAnimation() {
        if (!hasRandomAnimation()) {
            return;
        }
        _animationTask = LazyPrecisionTaskManager.getInstance().addNpcAnimationTask(this);
    }

    public void stopRandomAnimation() {
        if (_animationTask != null) {
            _animationTask.cancel(false);
            _animationTask = null;
        }
    }

    public boolean hasRandomAnimation() {
        return _hasRandomAnimation;
    }

    public void setRandomAnimation(final boolean randomAnimation) {
        _hasRandomAnimation = randomAnimation;
    }

    public boolean hasRandomWalk() {
        return _hasRandomWalk;
    }

    public void setRandomWalk(final boolean randomWalk) {
        _hasRandomWalk = randomWalk;
    }

    public boolean hasCheckAggression() {
        return _hasCheckAggression;
    }

    public void setCheckAggression(final boolean checkAggression) {
        _hasCheckAggression = checkAggression;
    }

    public Castle getCastle() {
        if (getReflection() == ReflectionManager.PARNASSUS && ServicesConfig.SERVICES_PARNASSUS_NOTAX) {
            return null;
        }
        if (ServicesConfig.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.GIRAN_HARBOR) {
            return null;
        }
        if (ServicesConfig.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.PARNASSUS) {
            return null;
        }
        if (ServicesConfig.SERVICES_OFFSHORE_NO_CASTLE_TAX && isInZone(ZoneType.offshore)) {
            return null;
        }
        if (_nearestCastle == null) {
            _nearestCastle = ResidenceHolder.getInstance().getResidence(getTemplate().getCastleId());
        }
        return _nearestCastle;
    }

    public Castle getCastle(final Player player) {
        return getCastle();
    }

    public Fortress getFortress() {
        if (_nearestFortress == null) {
            _nearestFortress = ResidenceHolder.getInstance().findNearestResidence(Fortress.class, getX(), getY(), getZ(), getReflection(), 32768);
        }

        return _nearestFortress;
    }

    public ClanHall getClanHall() {
        if (_nearestClanHall == null) {
            _nearestClanHall = ResidenceHolder.getInstance().findNearestResidence(ClanHall.class, getX(), getY(), getZ(), getReflection(), 32768);
        }

        return _nearestClanHall;
    }

    public Dominion getDominion() {
        if (getReflection() != ReflectionManager.DEFAULT) {
            return null;
        }
        if (_nearestDominion == null) {
            if (getTemplate().getCastleId() == 0) {
                return null;
            }

            final Castle castle = ResidenceHolder.getInstance().getResidence(getTemplate().getCastleId());
            _nearestDominion = castle.getDominion();
        }
        return _nearestDominion;
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (!isTargetable()) {
            player.sendActionFailed();
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            }

            player.sendPacket(new ValidateLocation(this), ActionFail.STATIC);
            return;
        }

        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, NpcInstance.class, this, true)) {
            return;
        }

        if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
            return;
        }

        if (!isInRangeZ(player, getInteractDistance(player))) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
            }
            return;
        }

        final boolean checkAreChaotic = getFnYouAreChaotic() != null && !getFnYouAreChaotic().isEmpty();
        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && checkAreChaotic && player.getKarma() > 0) {
            if (player.isGM()) {
                player.sendDebugMessage("You are in chaotic mode. This NPC not sended HTML in this mode for noGM char.");
            } else {
                showChatWindow(player, getFnYouAreChaotic());
                player.sendActionFailed();
                return;
            }
        }

        // С NPC нельзя разговаривать мертвым, сидя и во время каста
        if (!AllSettingsConfig.ALLOW_TALK_WHILE_SITTING && player.isSitting() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (!isMoving() && !isRunning()) {
            player.sendPacket(new MoveToPawn(player, this, getInteractDistance(player)));
        }
        player.sendActionFailed();
        if (!player.isSitting()) {
            player.setLastNpcInteractionTime();
        }

        if (_isBusy) {
            showBusyWindow(player);
        } else if (isHasChatWindow()) {
            boolean flag = false;
            final Quest[] qlst = getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
            if (qlst != null && qlst.length > 0) {
                for (final Quest q : qlst) {
                    final QuestState qs = player.getQuestState(q);
                    if ((qs == null || !qs.isCompleted()) && q.notifyFirstTalk(this, player)) {
                        flag = true;
                    }
                }
            }
            if (!flag) {
                showChatWindow(player, 0);
            }
        }
    }

    public void showQuestWindow(final Player player, final String questName) {
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }

        if (!player.isInventoryLimit(false) || !player.isWeightLimit(false)) {
            player.sendPacket(SystemMsg.YOU_CAN_PROCEED_ONLY_WHEN_THE_INVENTORY_WEIGHT_IS_BELOW_80_PERCENT_AND_THE_QUANTITY_IS_BELOW_90_PERCENT);
            return;
        }

        int count = 0;
        for (final QuestState quest : player.getAllQuestsStates()) {
            if (quest != null && quest.getQuest().isVisible() && quest.isStarted() && quest.getCond() > 0 && !quest.getQuest().isUnderLimit()) {
                count++;
            }
        }

        if (count > 40) {
            showChatWindow(player, "pts/fullquest.htm");
            return;
        }

        if (!questName.isEmpty()) {
            final String[] qn = questName.split("_");
            if (qn.length > 1) {
                final int questId = Integer.parseInt(qn[1]);
                if (questId > 0) {
                    // Get the state of the selected quest
                    QuestState qs = player.getQuestState(questId);
                    if (qs != null) {
                        if (qs.isCompleted()) {
                            showChatWindow(player, "pts/finishedquest.htm");
                            return;
                        }
                        if (qs.getQuest().notifyTalk(this, qs)) {
                            return;
                        }
                    } else {
                        final Quest q = QuestManager.getQuest(questId);
                        if (q != null) {
                            // check for start point
                            final Quest[] qlst = getTemplate().getEventQuests(QuestEventType.QUEST_START);
                            if (qlst != null && qlst.length > 0) {
                                for (final Quest element : qlst) {
                                    if (element == q) {
                                        qs = q.newQuestState(player, Quest.CREATED);
                                        if (qs.getQuest().notifyTalk(this, qs)) {
                                            return;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        showChatWindow(player, "no-quest.htm");
    }

    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (!getTemplate().getTeleportList().isEmpty() && checkForDominionWard(player)) {
            return;
        }

        try {
            if ("TerritoryStatus".equalsIgnoreCase(command)) //TODO Выпилить в будущем. (c)Mangol
            {
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile("territorystatus.htm");
                html.replace("%npcname%", getName());

                final Castle castle = getCastle(player);
                if (castle != null && castle.getId() > 0) {
                    html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
                    html.replace("%taxpercent%", String.valueOf(castle.getTaxPercent()));

                    if (castle.getOwnerId() > 0) {
                        final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                        if (clan != null) {
                            html.replace("%clanname%", clan.getName());
                            html.replace("%clanleadername%", clan.getLeaderName());
                        } else {
                            html.replace("%clanname%", "unexistant clan");
                            html.replace("%clanleadername%", "None");
                        }
                    } else {
                        html.replace("%clanname%", "NPC");
                        html.replace("%clanleadername%", "None");
                    }
                } else {
                    html.replace("%castlename%", "Open");
                    html.replace("%taxpercent%", "0");

                    html.replace("%clanname%", "No");
                    html.replace("%clanleadername%", getName());
                }

                player.sendPacket(html);
            } else if (command.equalsIgnoreCase("menu_select?ask=-1000&reply=0"))//Юзая Этот menu_select полюбому должен быть прописать fnHi в нпс иначе нулл.
            {
                showChatWindow(player, getFnHi());
            } else if (command.equalsIgnoreCase("menu_select?ask=-1000&reply=1")) {
                final String fnFeudInfo = "pts/defaultfeudinfo.htm";
                final String fnNoFeudInfo = "pts/nofeudinfo.htm";
                final HtmlMessage html = new HtmlMessage(this);
                final Castle castle = getCastle(player);
                if (castle != null && castle.getId() > 0) {
                    if (castle.getOwnerId() > 0) {
                        final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                        if (clan != null) {
                            html.setFile(fnFeudInfo);
                            html.replace("<?my_pledge_name?>", clan.getName());
                            html.replace("<?my_owner_name?>", clan.getLeaderName());
                            html.replace("<?current_tax_rate?>", String.valueOf(castle.getTaxPercent()));
                        }
                    } else {
                        html.setFile(fnNoFeudInfo);
                    }
                    if (castle != null && castle.getId() < 7) {
                        html.replace("<?kingdom_name?>", NpcString.THE_KINGDOM_OF_ADEN);
                    } else {
                        html.replace("<?kingdom_name?>", NpcString.THE_KINGDOM_OF_ELMORE);
                    }
                    html.replace("<?feud_name?>", NpcString.valueOf(1001000 + castle.getId()));
                    player.sendPacket(html);
                }
            } else if (command.startsWith("QuestEvent")) {
                final StringTokenizer tokenizer = new StringTokenizer(command);
                tokenizer.nextToken();

                final String questName = tokenizer.nextToken();

                final int questId = Integer.parseInt(questName.split("_")[1]); //FIXME [VISTALL] может по другом?

                player.processQuestEvent(questId, command.substring(12 + questName.length()), this);
            } else if (command.startsWith("QuestTeleEvent")) {
                final String quest = command.substring(9).trim();
                if (quest.isEmpty())
                    QuestDataHolder.getInstance().useTeleport(player);
                else
                    QuestDataHolder.getInstance().useTeleport(player);
            } else if (command.startsWith("Quest")) {
                final String quest = command.substring(5).trim();
                if (quest.isEmpty()) {
                    showQuestWindow(player);
                } else {
                    showQuestWindow(player, quest);
                }
            } else if (command.startsWith("Chat")) {
                try {
                    final int val = Integer.parseInt(command.substring(5));
                    showChatWindow(player, val);
                } catch (NumberFormatException nfe) {
                    final String filename = command.substring(5).trim();
                    if (filename.isEmpty()) {
                        showChatWindow(player, "npcdefault.htm");
                    } else {
                        showChatWindow(player, filename);
                    }
                }
            } else if (command.startsWith("AttributeCancel")) {
                player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
            } else if (command.startsWith("Multisell") || command.startsWith("multisell")) {
                final String listId = command.substring(9).trim();
                final Castle castle = getCastle(player);
                MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(listId), player, getObjectId(), castle != null ? castle.getTaxRate() : 0);
            } else if (command.startsWith("EnterRift")) {
                if (checkForDominionWard(player)) {
                    return;
                }

                final StringTokenizer st = new StringTokenizer(command);
                st.nextToken(); //no need for "enterRift"

                final int b1 = Integer.parseInt(st.nextToken()); //type

                DimensionalRiftManager.getInstance().start(player, b1, this);
            } else if (command.startsWith("ChangeRiftRoom")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift) {
                    ((DimensionalRift) player.getParty().getReflection()).manualTeleport(player, this);
                } else {
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
                }
            } else if (command.startsWith("ExitRift")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift) {
                    ((DimensionalRift) player.getParty().getReflection()).manualExitRift(player, this);
                } else {
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
                }
            } else if ("SkillList".equalsIgnoreCase(command)) {
                showSkillList(player);
            } else if ("ClanSkillList".equalsIgnoreCase(command)) {
                showClanSkillList(player);
            } else if (command.startsWith("SubUnitSkillList")) {
                showSubUnitSkillList(player);
            } else if ("TransformationSkillList".equalsIgnoreCase(command)) {
                showTransformationSkillList(player, AcquireType.TRANSFORMATION);
            } else if ("CertificationSkillList".equalsIgnoreCase(command)) {
                showTransformationSkillList(player, AcquireType.CERTIFICATION);
            } else if ("CollectionSkillList".equalsIgnoreCase(command)) {
                showCollectionSkillList(player);
            } else if ("BuyTransformation".equalsIgnoreCase(command)) {
                showTransformationMultisell(player);
            } else if (command.startsWith("Augment")) {
                final int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
                if (cmdChoice == 1) {
                    player.sendPacket(SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
                } else if (cmdChoice == 2) {
                    player.sendPacket(SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
                }
            } else if (command.startsWith("Link")) {
                showChatWindow(player, command.substring(5));
            } else if (command.startsWith("Teleport")) {
                final int cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
                final TeleportLocation[] list = getTemplate().getTeleportList(cmdChoice);
                if (list != null) {
                    showTeleportList(player, list, cmdChoice);
                } else {
                    player.sendDebugMessage("This link and teleport list not corrected! Please, send this error to administration! Err=1");
                }
            } else if (command.startsWith("teleport_request")) {
                final String[] cmdLine = command.substring(17).split(" ", 5);
                if (cmdLine.length >= 4) {
                    useGateKeeper(player, this, cmdLine);
                } else {
                    player.sendDebugMessage("This link and teleport list not corrected! Please, send this error to administration! Err=2");
                }
            } else if (command.startsWith("teleport_quest_request")) {
                final String[] cmdLine = command.substring(23).split(" ", 5);
                if (cmdLine.length >= 4) {
                    useQuestGateKeeper(player, this, cmdLine);
                } else {
                    player.sendDebugMessage("This link and teleport list not corrected! Please, send this error to administration! Err=2");
                }
            } else if (command.startsWith("Tele20Lvl")) {
                final int cmdChoice = Integer.parseInt(command.substring(10, 11).trim());
                final TeleportLocation[] list = getTemplate().getTeleportList(cmdChoice);
                if (player.getLevel() > 20) {
                    showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm");
                } else if (list != null) {
                    showTeleportList(player, list, cmdChoice);
                } else {
                    player.sendDebugMessage("This link and teleport list not corrected! Please, send this error to administration! Err=3");
                }
            } else if (command.startsWith("open_gate")) {
                final int val = Integer.parseInt(command.substring(10));
                ReflectionUtils.getDoor(val).openMe();
                player.sendActionFailed();
            } else if ("TransferSkillList".equalsIgnoreCase(command)) {
                showTransferSkillList(player);
            } else if ("CertificationCancel".equalsIgnoreCase(command)) {
                CertificationFunctions.cancelCertification(this, player);
            } else if (command.startsWith("RemoveTransferSkill")) {
                final AcquireType type = AcquireType.transferType(player.getPlayerClassComponent().getActiveClassId());
                if (type == null) {
                    return;
                }

                final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(null, type);
                if (skills.isEmpty()) {
                    player.sendActionFailed();
                    return;
                }

                boolean reset = false;
                for (final SkillLearn skill : skills) {
                    if (player.getKnownSkill(skill.getId()) != null) {
                        reset = true;
                        break;
                    }
                }

                if (!reset) {
                    player.sendActionFailed();
                    return;
                }

                if (!player.reduceAdena(10000000L, true)) {
                    showChatWindow(player, "common/skill_share_healer_no_adena.htm");
                    return;
                }

                for (final SkillLearn skill : skills) {
                    if (player.removeSkill(skill.getId(), true) != null) {
                        ItemFunctions.addItem(player, skill.getItemId(), skill.getItemCount(), true);
                    }
                }
            } else if (command.startsWith("ExitFromQuestInstance")) {
                final Reflection r = player.getReflection();
                r.startCollapseTimer(60000);
                player.teleToLocation(r.getReturnLoc(), 0);
                if (command.length() > 22) {
                    try {
                        final int val = Integer.parseInt(command.substring(22));
                        showChatWindow(player, val);
                    } catch (NumberFormatException nfe) {
                        final String filename = command.substring(22).trim();
                        if (!filename.isEmpty()) {
                            showChatWindow(player, filename);
                        }
                    }
                }
            } else if (command.startsWith("birthdayHelper")) {
                for (final NpcInstance n : World.getAroundNpc(this)) {
                    if (n.getNpcId() == 32600) {
                        showChatWindow(player, "Birthday-spawned.htm");
                        return;
                    }
                }

                player.sendPacket(PlaySound.HB01);

                final int x = (int) (getX() + 40 * Math.cos(headingToRadians(getHeading() - 32768 + 8000)));
                final int y = (int) (getY() + 40 * Math.sin(headingToRadians(getHeading() - 32768 + 8000)));

                NpcUtils.spawnSingle(32600, x, y, getZ(), PositionUtils.calculateHeadingFrom(x, y, player.getX(), player.getY()), 180000);
            } else {
                final String word = command.split("\\s+")[0];

                final Pair<Object, Method> b = BypassHolder.getInstance().getBypass(word);
                if (b != null) {
                    b.getValue().invoke(b.getKey(), player, this, command.substring(word.length()).trim().split("\\s+"));
                } else {
                    _log.warn("Unknown command=[" + command + "] npcId:" + getTemplate().npcId);
                }
            }
        } catch (NumberFormatException nfe) {
            _log.warn("Invalid bypass to Server command parameter! npcId=" + getTemplate().npcId + " command=[" + command + ']', nfe);
        } catch (Exception sioobe) {
            _log.warn("Incorrect htm bypass! npcId=" + getTemplate().npcId + " command=[" + command + ']', sioobe);
        }
    }

    public void showTeleportList(final Player player, final TeleportLocation[] list, final int cmdChoice) {
        final StringBuilder sb = new StringBuilder();

        sb.append("&$556;").append("<br><br>");

        if (list != null && player.getPlayerAccess().UseTeleport) {
            boolean olyTokenFind = false;
            for (final TeleportLocation tl : list) {
                if (!olyTokenFind && tl.getItem().getItemId() == ItemTemplate.OLYMPIAD_TOKEN) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.OLYMPIAD_TOKEN) < 1) {
                        showChatWindow(player, "pts/fornonoblessitem.htm");
                        return;
                    } else {
                        olyTokenFind = true;
                    }
                }

                if (tl.getItem().getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    // TODO: DS: убрать хардкод
                    double pricemod = player.getLevel() <= AllSettingsConfig.GATEKEEPER_FREE ? 0. : player.isNoble() && cmdChoice > 1 ? AllSettingsConfig.GATEKEEPER_NOBLE_MODIFIER : AllSettingsConfig.GATEKEEPER_MODIFIER;
                    if (tl.getPrice() > 0 && pricemod > 0) {
                        //On Saturdays and Sundays from 8 PM to 12 AM, gatekeeper teleport fees decrease by 50%.
                        final Calendar calendar = Calendar.getInstance();
                        final int day = calendar.get(Calendar.DAY_OF_WEEK);
                        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        if ((day == Calendar.SUNDAY || day == Calendar.SATURDAY) && (hour >= 20 && hour <= 12)) {
                            pricemod /= 2;
                        }
                    }
                    sb.append("[npc_%objectId%_teleport_request ").append(tl.getX()).append(' ').append(tl.getY()).append(' ').append(tl.getZ());
                    if (tl.getCastleId() != 0) {
                        sb.append(' ').append(tl.getCastleId());
                    }
                    sb.append(' ').append((long) (tl.getPrice() * pricemod)).append(" @811;F;").append(tl.getName()).append('|').append(HtmlUtils.htmlNpcString(tl.getName()));
                    if (tl.getPrice() * pricemod > 0) {
                        sb.append(" - ").append((long) (tl.getPrice() * pricemod)).append(' ').append(HtmlUtils.htmlItemName(ItemTemplate.ITEM_ID_ADENA));
                    }
                    sb.append("]<br1>\n");
                } else {
                    sb.append("[npc_%objectId%_teleport_quest_request ").append(tl.getX()).append(' ')
                            .append(tl.getY()).append(' ').append(tl.getZ()).append(' ')
                            .append(tl.getPrice()).append(' ').append(tl.getItem().getItemId())
                            .append(" @811;F;").append('|').append(HtmlUtils.htmlNpcString(tl.getName()))
                            .append(" - ").append(tl.getPrice()).append(' ').append(HtmlUtils.htmlItemName(tl.getItem().getItemId()))
                            .append("]<br1>\n");
                }
            }
        } else {
            sb.append("No teleports available for you.");
        }

        final HtmlMessage html = new HtmlMessage(this);
        html.setHtml(HtmlUtils.bbParse(sb.toString()));
        player.sendPacket(html);
    }

    public void showQuestWindow(final Player player) {
        if (!player.isInventoryLimit(false) || !player.isWeightLimit(false)) {
            player.sendPacket(SystemMsg.YOU_CAN_PROCEED_ONLY_WHEN_THE_INVENTORY_WEIGHT_IS_BELOW_80_PERCENT_AND_THE_QUANTITY_IS_BELOW_90_PERCENT);
            return;
        }

        // collect awaiting quests and start points
        final List<Quest> options = new ArrayList<>();

        final List<QuestState> awaits = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK);
        final Quest[] starts = getTemplate().getEventQuests(QuestEventType.QUEST_START);

        if (awaits != null) {
            awaits.stream().filter(x -> !options.contains(x.getQuest())).forEach(x -> {
                if (x.getQuest().getId() > 0) {
                    options.add(x.getQuest());
                }
            });
        }

        if (starts != null) {
            for (final Quest x : starts) {
                if (!options.contains(x)) {
                    if (x.getId() > 0) {
                        options.add(x);
                    }
                }
            }
        }

        // Display a QuestChooseWindow (if several quests are available) or QuestWindow
        if (options.size() > 1) {
            showQuestChooseWindow(player, options.toArray(new Quest[options.size()]));
        } else if (options.size() == 1) {
            showQuestWindow(player, options.get(0).getName());
        } else {
            showQuestWindow(player, "");
        }
    }

    public void showQuestChooseWindow(final Player player, final Quest[] quests) {
        final StringBuilder sb = new StringBuilder();

        sb.append("<html><body><title>Talk about:</title><br>");

        for (final Quest q : quests) {
            if (!q.isVisible()) {
                continue;
            }

            sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(q.getName()).append("\">[").append(q.getDescr(player)).append("]</a><br>");
        }

        sb.append("</body></html>");

        final HtmlMessage html = new HtmlMessage(this);
        html.setHtml(sb.toString());
        player.sendPacket(html);
    }

    public void showChatWindow(final Player player, final int val, final Object... replace) {
        if (!getTemplate().getTeleportList().isEmpty() && checkForDominionWard(player)) {
            return;
        }

        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
        final int npcId = getNpcId();
        switch (npcId) {
            case 31111: // Gatekeeper Spirit (Disciples)
                final int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
                final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
                final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
                if (playerCabal == sealAvariceOwner && playerCabal == compWinner) {
                    switch (sealAvariceOwner) {
                        case SevenSigns.CABAL_DAWN:
                            filename += "spirit_dawn.htm";
                            break;
                        case SevenSigns.CABAL_DUSK:
                            filename += "spirit_dusk.htm";
                            break;
                        case SevenSigns.CABAL_NULL:
                            filename += "spirit_null.htm";
                            break;
                    }
                } else {
                    filename += "spirit_null.htm";
                }
                break;
            case 31112: // Gatekeeper Spirit (Disciples)
                filename += "spirit_exit.htm";
                break;
            case 30298:
                if (player.getPledgeType() == Clan.SUBUNIT_ACADEMY) {
                    filename = getHtmlPath(npcId, 1, player);
                } else {
                    filename = getHtmlPath(npcId, 0, player);
                }
                break;
            default:
                if (npcId >= 31093 && npcId <= 31094 || npcId >= 31172 && npcId <= 31201 || npcId >= 31239 && npcId <= 31254) {
                    return;
                }
                // Get the text of the selected HTML file in function of the npcId and of the page number
                filename = getHtmlPath(npcId, val, player);
                break;
        }
        if (isFnHi()) {
            final HtmlMessage packet = new HtmlMessage(this, getFnHi(), val);
            if (replace.length % 2 == 0) {
                for (int i = 0; i < replace.length; i += 2) {
                    packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
                }
            }
            player.sendPacket(packet);
        } else {
            final HtmlMessage packet = new HtmlMessage(this, filename, val);
            if (replace.length % 2 == 0) {
                for (int i = 0; i < replace.length; i += 2) {
                    packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
                }
            }
            player.sendPacket(packet);
        }

    }

    public void showChatWindow(final Player player, final String filename, final Object... replace) {
        final HtmlMessage packet = new HtmlMessage(this).setFile(filename);
        if (replace.length % 2 == 0) {
            for (int i = 0; i < replace.length; i += 2) {
                packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
            }
        }
        player.sendPacket(packet);
    }

    public void useGateKeeper(final Player player, final NpcInstance npc, final String[] param) {
        if (param.length < 4) {
            throw new IllegalArgumentException();
        }

        if (player == null) {
            return;
        }

        long price = Long.parseLong(param[param.length - 1]);

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }

        if (price > 0 && player.getAdena() < price) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (player.getMountType() == 2) {
            return;
        }

        if (player.getKarma() > 0 && getFnYouAreChaotic() != null && !getFnYouAreChaotic().isEmpty()) {
            showChatWindow(player, getFnYouAreChaotic());
            return;
        }

        if (player.getActiveWeaponFlagAttachment() != null) {
            showChatWindow(player, "pts/teleporter/flagman.htm");
            return;
        }

        if (player.getTransformationId() == 111 || player.getTransformationId() == 112 || player.getTransformationId() == 124) {
            showChatWindow(player, "pts/teleporter/q194_noteleport.htm");
            return;
        }

        /* Затычка, npc Mozella не ТПшит чаров уровень которых превышает заданный в конфиге
         * Off Like >= 56 lvl, данные по ограничению lvl'a устанавливаются в altsettings.properties.
         * TODO[K] - перепилить ТП в круме и выпилить нахер этот говнокод!!!
         */
        final NpcInstance lastNpc = player.getLastNpc();
        if (npc != null) {
            final int npcId = lastNpc.getNpcId();
            switch (npcId) {
                case 30483:
                    if (player.getLevel() >= AllSettingsConfig.CRUMA_GATEKEEPER_LVL) {
                        Functions.show("teleporter/30483-no.htm", player, lastNpc);
                        return;
                    }
                    break;
            }
        }

        final int x = Integer.parseInt(param[0]);
        final int y = Integer.parseInt(param[1]);
        final int z = Integer.parseInt(param[2]);
        final int castleId = param.length > 4 ? Integer.parseInt(param[3]) : 0;

        if (player.getReflection().isDefault()) {
            final Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
            // Нельзя телепортироваться в города, где идет осада
            if (castle != null && castle.getSiegeEvent().isInProgress()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                return;
            }
        }

        final Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());

        if (price > 0) {
            player.reduceAdena(price, true);
        }
        player.teleToLocation(pos);
    }

    public void useQuestGateKeeper(final Player player, final NpcInstance npc, final String[] param) {
        if (param.length < 5) {
            throw new IllegalArgumentException();
        }

        if (player == null) {
            return;
        }

        final long count = Long.parseLong(param[3]);
        final int item = Integer.parseInt(param[4]);

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }

        if (count > 0) {
            if (!player.getInventory().destroyItemByItemId(item, count)) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return;
            }
            player.sendPacket(SystemMessage.removeItems(item, count));
        }

        final int x = Integer.parseInt(param[0]);
        final int y = Integer.parseInt(param[1]);
        final int z = Integer.parseInt(param[2]);

        final Location pos = Location.findPointToStay(x, y, z, 20, 70, player.getGeoIndex());

        player.teleToLocation(pos);
    }

    public String getHtmlPath(final int npcId, final int val, final Player player) {
        final String pom = val == 0 ? String.valueOf(npcId) : npcId + "-" + val;

        if (getTemplate().getHtmRoot() != null) {
            return getTemplate().getHtmRoot() + pom + ".htm";
        }

        String temp = "default/" + pom + ".htm";
        if (!HtmCache.getInstance().getHtml(temp, player).isEmpty()) {
            return temp;
        }

        temp = "trainer/" + pom + ".htm";
        if (!HtmCache.getInstance().getHtml(temp, player).isEmpty()) {
            return temp;
        }

        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "npcdefault.htm";
    }

    public final boolean isBusy() {
        return _isBusy;
    }

    public void setBusy(final boolean isBusy) {
        _isBusy = isBusy;
    }

    public final String getBusyMessage() {
        return _busyMessage;
    }

    public void setBusyMessage(final String message) {
        _busyMessage = message;
    }

    public void showBusyWindow(final Player player) {
        final HtmlMessage html = new HtmlMessage(this);
        html.setFile("npcbusy.htm");
        html.replace("%npcname%", getName());
        html.replace("%playername%", player.getName());
        html.replace("%busymessage%", _busyMessage);
        player.sendPacket(html);
    }

    public void showSkillList(final Player player) {
        final ClassId classId = player.getPlayerClassComponent().getClassId();

        if (classId == null) {
            return;
        }

        final int npcId = getTemplate().npcId;

        if (getTemplate().getTeachInfo().isEmpty()) {
            _log.error("TeachInfo is empty! Npc: " + this + ", player : " + player + '!');
            player.sendActionFailed();
            return;
        }

        if (!(getTemplate().canTeach(classId) || getTemplate().canTeach(classId.getParent(player.getSex())))) {
            if (this instanceof WarehouseInstance) {
                showChatWindow(player, "warehouse/" + getNpcId() + "-noteach.htm");
            } else if (this instanceof TrainerInstance) {
                showChatWindow(player, "trainer/" + getNpcId() + "-noteach.htm");
            }

            player.sendActionFailed();
            return;
        }

        final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);

        final AcquireSkillList asl = new AcquireSkillList(AcquireType.NORMAL, skills.size());
        int counts = 0;

        for (final SkillLearn s : skills) {
            if (s.isClicked()) {
                continue;
            }

            final SkillEntry sk = SkillTable.getInstance().getSkillEntry(s.getId(), s.getLevel());
            if (sk == null || !sk.getTemplate().getCanLearn(player.getPlayerClassComponent().getClassId()) || !sk.getTemplate().canTeachBy(npcId)) {
                continue;
            }

            counts++;

            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 0);
        }

        if (counts == 0) {
            final int minlevel = SkillAcquireHolder.getInstance().getMinLevelForNewSkill(player, AcquireType.NORMAL);

            if (minlevel > 0) {
                final SystemMessage sm = new SystemMessage(SystemMsg.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addNumber(minlevel);
                player.sendPacket(sm);
            } else {
                player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
            }
            player.sendPacket(AcquireSkillDone.STATIC);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    public void showTransferSkillList(final Player player) {
        final ClassId classId = player.getPlayerClassComponent().getClassId();
        if (classId == null) {
            return;
        }

        if (player.getLevel() < 76 || classId.getLevel().ordinal() < 4) {
            final HtmlMessage html = new HtmlMessage(this);
            html.setHtml("<html><head><body>You must have 3rd class change quest completed.</body></html>");
            player.sendPacket(html);
            return;
        }

        final AcquireType type = AcquireType.transferType(player.getPlayerClassComponent().getActiveClassId());
        if (type == null) {
            return;
        }

        showAcquireList(type, player);
    }

    public void showTransformationMultisell(final Player player) {
        if (!AllSettingsConfig.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST) {
            if (!player.isQuestCompleted(136)) {
                showChatWindow(player, "trainer/" + getNpcId() + "-nobuy.htm");
                return;
            }
        }

        final Castle castle = getCastle(player);
        MultiSellHolder.getInstance().SeparateAndSend(32323, player, getObjectId(), castle != null ? castle.getTaxRate() : 0);
        player.sendActionFailed();
    }

    public void showTransformationSkillList(final Player player, final AcquireType type) {
        if (!AllSettingsConfig.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST) {
            if (!player.isQuestCompleted(136)) {
                showChatWindow(player, "trainer/" + getNpcId() + "-noquest.htm");
                return;
            }
        }

        showAcquireList(type, player);
    }

    /**
     * Нужно для отображения анимации спауна, используется в пакете NpcInfo:
     * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
     */
    public int getSpawnAnimation() {
        return _spawnAnimation;
    }

    @Override
    public double getColRadius() {
        return getCollisionRadius();
    }

    @Override
    public double getColHeight() {
        return getCollisionHeight();
    }

    public int calculateLevelDiffForDrop(final int charLevel) {
        if (!OtherConfig.DEEPBLUE_DROP_RULES) {
            return 0;
        }

        final int mobLevel = getLevel();
        // According to official data (Prima), deep blue mobs are 9 or more levels below players
        final int deepblue_maxdiff = this instanceof RaidBossInstance ? OtherConfig.DEEPBLUE_DROP_RAID_MAXDIFF : OtherConfig.DEEPBLUE_DROP_MAXDIFF;

        return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
    }

    public boolean isSevenSignsMonster() {
        return "c_dungeon_clan".equalsIgnoreCase(getFaction().getName());
    }

    @Override
    public String toString() {
        return getNpcId() + " " + getName();
    }

    public void refreshID() {
        GameObjectsStorage.remove(this);

        objectId = IdFactory.getInstance().getNextId();

        GameObjectsStorage.put(this);
    }

    public boolean isUnderground() {
        return _isUnderground;
    }

    public void setUnderground(final boolean b) {
        _isUnderground = b;
    }

    public boolean isTargetable() {
        return _isTargetable;
    }

    public void setTargetable(final boolean value) {
        _isTargetable = value;
    }

    public boolean isShowName() {
        return _showName;
    }

    public void setShowName(final boolean value) {
        _showName = value;
    }

    @Override
    public synchronized NpcListenerList initializeListeners() {
        return new NpcListenerList(this);
    }

    @Override
    public NpcListenerList getListeners() {
        return (NpcListenerList) oldListeners;
    }

    public <T extends NpcListener> boolean addListener(final T listener) {
        return getListeners().add(listener);
    }

    public <T extends NpcListener> boolean removeListener(final T listener) {
        return getListeners().remove(listener);
    }

    @Override
    public synchronized NpcStatsChangeRecorder initializeStatsRecorder() {
        return new NpcStatsChangeRecorder(this);
    }

    @Override
    public NpcStatsChangeRecorder getStatsRecorder() {
        return (NpcStatsChangeRecorder) _statsRecorder;
    }

    public int getNpcState() {
        return npcState;
    }

    public void setNpcState(final int stateId) {
        broadcastPacket(new ExChangeNpcState(getObjectId(), stateId));
        npcState = stateId;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(final Player forPlayer, final Creature dropper) {
        final List<L2GameServerPacket> list = new ArrayList<>(3);
        list.add(new NpcInfo(this, forPlayer));

        if (isInCombat()) {
            list.add(new AutoAttackStart(getObjectId()));
        }

        if (isMoving || isFollow) {
            list.add(movePacket());
        }

        return list;
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @Override
    public int getGeoZ(final Location loc) {
        if (isFlying() || isInWater() || isInBoat() || isBoat() || isDoor()) {
            return loc.z;
        }
        if (isNpc()) {
            if (_spawnRange instanceof Territory) {
                return GeoEngine.getHeight(loc, getGeoIndex());
            }
            return loc.z;
        }

        return super.getGeoZ(loc);
    }

    @Override
    public Clan getClan() {
        final Dominion dominion = getDominion();
        if (dominion == null) {
            return null;
        }
        final int lordObjectId = dominion.getLordObjectId();
        return lordObjectId == 0 ? null : dominion.getOwner();
    }

    public NpcString getNameNpcString() {
        return _nameNpcString;
    }

    public void setNameNpcString(final NpcString nameNpcString) {
        _nameNpcString = nameNpcString;
    }

    public NpcString getTitleNpcString() {
        return _titleNpcString;
    }

    public void setTitleNpcString(final NpcString titleNpcString) {
        _titleNpcString = titleNpcString;
    }

    public boolean isMerchantNpc() {
        return false;
    }

    public SpawnRange getSpawnRange() {
        return _spawnRange;
    }

    public void setSpawnRange(final SpawnRange spawnRange) {
        _spawnRange = spawnRange;
    }

    public boolean checkForDominionWard(final Player player) {
        final ItemInstance item = player.getActiveWeaponInstance();
        if (item != null && item.getAttachment() instanceof TerritoryWardObject) {
            showChatWindow(player, "flagman.htm");
            return true;
        }
        return false;
    }

    public void setParameter(final Parameter parameter, final Object val) {
        if (_parameters == StatsSet.EMPTY) {
            _parameters = new StatsSet();
        }

        _parameters.set(parameter.name().toLowerCase(), val);
    }

    public void setParameter(final String str, final Object val) {
        if (_parameters == StatsSet.EMPTY) {
            _parameters = new StatsSet();
        }

        _parameters.set(str, val);
    }

    public void removeParameter(final Object key) {
        if (_parameters.isEmpty()) {
            return;
        }
        _parameters.remove(key);
    }

    public void removeParameter(final Parameter parameter) {
        if (_parameters.isEmpty()) {
            return;
        }
        _parameters.remove(parameter.name().toLowerCase());
    }

    public int getParameter(final Parameter parameter, final int val) {
        return _parameters.getInteger(parameter.name().toLowerCase(), val);
    }

    public long getParameter(final Parameter parameter, final long val) {
        return _parameters.getLong(parameter.name().toLowerCase(), val);
    }

    public boolean getParameter(final Parameter parameter, final boolean val) {
        return _parameters.getBool(parameter.name().toLowerCase(), val);
    }

    public String getParameter(final Parameter parameter, final String val) {
        return _parameters.getString(parameter.name().toLowerCase(), val);
    }

    public int getParameter(final String str, final int val) {
        return _parameters.getInteger(str, val);
    }

    public long getParameter(final String str, final long val) {
        return _parameters.getLong(str, val);
    }

    public boolean getParameter(final String str, final boolean val) {
        return _parameters.getBool(str, val);
    }

    public String getParameter(final String str, final String val) {
        return _parameters.getString(str, val);
    }

    public MultiValueSet<String> getParameters() {
        return _parameters;
    }

    public void setParameters(final MultiValueSet<String> set) {
        if (set.isEmpty()) {
            return;
        }

        if (_parameters == StatsSet.EMPTY) {
            _parameters = new MultiValueSet<>(set.size());
        }

        _parameters.putAll(set);
    }

    public boolean isHasChatWindow() {
        return _hasChatWindow;
    }

    public void setHasChatWindow(final boolean hasChatWindow) {
        _hasChatWindow = hasChatWindow;
    }

    public void onMenuSelect(Player player, int ask, int reply) {
        if (getAI() != null)
            getAI().notifyEvent(CtrlEvent.EVT_MENU_SELECTED, player, ask, reply);
    }

    @Override
    public void setTeam(final TeamType t) {
        super.setTeam(t);
        sendChanges();
    }

    @Override
    public boolean isFearImmune() {
        return getLeader() != null ? getLeader().isFearImmune() : !isMonster() || super.isFearImmune();
    }

    @Override
    public boolean isParalyzeImmune() {
        return !isMonster() || super.isParalyzeImmune();
    }

    public int getParam2() {
        return _param2;
    }

    public void setParam2(final int i0) {
        _param2 = i0;
    }

    public int getParam3() {
        return _param3;
    }

    public void setParam3(final int i1) {
        _param3 = i1;
    }

    public Creature getParam4() {
        return _param4;
    }

    public void setParam4(final Creature arg) {
        _param4 = arg;
    }

    public boolean isTeleportNpc() {
        return false;
    }

    public PrivatesList getPrivatesList() {
        if (_privatesList == null) {
            _privatesList = new PrivatesList(this);
        }

        return _privatesList;
    }

    /**
     * Возвращает есть ли список заспавненных минионов.
     *
     * @return возвращает есть ли список заспавненных минионов.
     */
    public boolean hasPrivates() {
        return _privatesList != null && _privatesList.hasMinions();
    }

    /**
     * Возвращает есть ли список заспавненных минионов.
     *
     * @return возвращает есть ли список заспавненных минионов.
     */
    public boolean hasOnePrivateEx() {
        return _privatesList != null && _privatesList.hasOnePrivateEx();
    }

    /**
     * Возвращает рандомную точку для спавна в радиусе
     *
     * @return генерируем рандомную точку для спавна в радиусе
     */
    public Location getMinionPosition() {
        return Location.findPointToStay(this, 30, 50);
    }

    /**
     * Спавнит миниона
     *
     * @param minion - npcId миньона
     */
    public void spawnMinion(final NpcInstance minion) {
        minion.setReflection(getReflection());
        minion.setHeading(getHeading());
        minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp(), true);
        minion.spawnMe(getMinionPosition());
        if (isRunning()) {
            minion.setRunning();
        }
    }

    public void minionDie(final NpcInstance minion, final int respawn) {
        if (respawn > 0) {
            _minionRespawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                @Override
                public void runImpl() {
                    if (!_master.isAlikeDead() && _master.isVisible()) {
                        if (!minion.isVisible()) {
                            minion.refreshID();
                            _master.spawnMinion(minion);
                        }
                    }
                }
            }, respawn);
        }
    }

    public void stopMinionRespawnTask() {
        if (_minionRespawnTask != null) {
            _minionRespawnTask.cancel(false);
            _minionRespawnTask = null;
        }
    }

    @Override
    public double getLevelMod() {
        return PCParameterHolder.getInstance().getLevelBonus().returnValue(getLevel());
    }

    public class BroadcastCharInfoTask extends RunnableImpl {
        @Override
        public void runImpl() {
            broadcastCharInfoImpl();
            _broadcastCharInfoTask = null;
        }
    }
}