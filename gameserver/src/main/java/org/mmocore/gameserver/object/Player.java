package org.mmocore.gameserver.object;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.atlassian.util.concurrent.LazyReference;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jts.dataparser.data.holder.ExpDataHolder;
import org.jts.dataparser.data.holder.PCParameterHolder;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.jts.dataparser.data.holder.pcparameter.PCParameterUtils;
import org.jts.dataparser.data.holder.pcparameter.common.LevelParameter;
import org.jts.dataparser.data.holder.petdata.PetData;
import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.jts.dataparser.data.holder.transform.TCommon;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.jts.dataparser.data.holder.transform.type.TransformType;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.lang.reference.ExpirationReference;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.PlayableAI.NextAction;
import org.mmocore.gameserver.ai.PlayerAI;
import org.mmocore.gameserver.configuration.config.*;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.configuration.config.custom.AcpConfig;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.configuration.config.gmAccess.GmAccessConfig;
import org.mmocore.gameserver.data.xml.holder.*;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.*;
import org.mmocore.gameserver.handler.items.IItemHandler;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.impl.PlayerEnterLeaveTransformListener;
import org.mmocore.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import org.mmocore.gameserver.listener.actor.player.impl.SummonAnswerListener;
import org.mmocore.gameserver.manager.*;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import org.mmocore.gameserver.model.*;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.model.base.*;
import org.mmocore.gameserver.model.entity.DimensionalRift;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.entity.olympiad.CompType;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.*;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.model.pledge.*;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.team.PlayerGroup;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.model.visual.VisualParams;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.*;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.TeleportToLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.object.components.items.*;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.object.components.items.attachments.PickableAttachment;
import org.mmocore.gameserver.object.components.items.warehouse.PcFreight;
import org.mmocore.gameserver.object.components.items.warehouse.PcWarehouse;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.WarehouseType;
import org.mmocore.gameserver.object.components.player.*;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunishComponent;
import org.mmocore.gameserver.object.components.player.community.ICommunityComponent;
import org.mmocore.gameserver.object.components.player.community.RefineComponent;
import org.mmocore.gameserver.object.components.player.cubicdata.AgathionComponent;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.object.components.player.custom.acp.AcpComponent;
import org.mmocore.gameserver.object.components.player.event.EventComponent;
import org.mmocore.gameserver.object.components.player.friend.FriendComponent;
import org.mmocore.gameserver.object.components.player.interfaces.MacroComponent;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCutComponent;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.object.components.player.inventory.PcRefund;
import org.mmocore.gameserver.object.components.player.nevit.NevitComponent;
import org.mmocore.gameserver.object.components.player.player_class.PlayerClassComponent;
import org.mmocore.gameserver.object.components.player.premium.PremiumAccountComponent;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.mmocore.gameserver.object.components.player.recomendation.RecommendationComponent;
import org.mmocore.gameserver.object.components.player.tasks.*;
import org.mmocore.gameserver.object.components.player.template.PlayerTemplateComponent;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMarkComponent;
import org.mmocore.gameserver.object.components.player.transformdata.TransformComponent;
import org.mmocore.gameserver.object.components.player.vitality.VitalityComponent;
import org.mmocore.gameserver.object.components.variables.AccountVariablesComponent;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.object.components.variables.PlayerVariablesComponent;
import org.mmocore.gameserver.phantoms.model.Phantom;
import org.mmocore.gameserver.scripts.events.custom.CustomInstantTeamEvent;
import org.mmocore.gameserver.scripts.services.autoenchant.EnchantParams;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.skills.effects.EffectCharge;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.skills.effects.Effect_p_recovery_vp;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.taskmanager.AutoSaveManager;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.templates.item.support.FishTemplate;
import org.mmocore.gameserver.templates.item.support.LureTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.mmocore.gameserver.world.WorldRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.mmocore.gameserver.network.lineage.serverpackets.ExSetCompassZoneCode.*;

public class Player extends Playable implements PlayerGroup {
    // lecture
    public static final int INITIAL_MARK = 1;
    public static final int EVANGELIST_MARK = 2;
    public static final int OFF_MARK = 3;
    public static final int OBSERVER_NONE = 0;
    public static final int OBSERVER_STARTING = 1;
    public static final int OBSERVER_STARTED = 3;
    public static final int OBSERVER_LEAVING = 2;
    public static final int STORE_PRIVATE_NONE = 0;
    public static final int STORE_PRIVATE_SELL = 1;
    public static final int STORE_PRIVATE_BUY = 3;
    public static final int STORE_PRIVATE_MANUFACTURE = 5;
    public static final int STORE_OBSERVING_GAMES = 7;
    public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
    public static final int RANK_VAGABOND = 0;
    public static final int RANK_VASSAL = 1;
    public static final int RANK_HEIR = 2;
    public static final int RANK_KNIGHT = 3;
    public static final int RANK_WISEMAN = 4;
    public static final int RANK_BARON = 5;
    public static final int RANK_VISCOUNT = 6;
    public static final int RANK_COUNT = 7;
    public static final int RANK_MARQUIS = 8;
    public static final int RANK_DUKE = 9;
    public static final int RANK_GRAND_DUKE = 10;
    public static final int RANK_DISTINGUISHED_KING = 11;
    public static final int RANK_EMPEROR = 12; // unused
    public static final int[] EXPERTISE_LEVELS = {0, 20, 40, 52, 61, 76, 80, 84, Integer.MAX_VALUE};
    public static final String NOT_USED = "not_used";
    public static final int AUTO_LOOT_NONE = 0;
    public static final int AUTO_LOOT_ALL = 1;
    public static final int AUTO_LOOT_ALL_EXCEPT_ARROWS = 2;
    private static final long serialVersionUID = 6456172901858263166L;
    private static final Logger _log = LoggerFactory.getLogger(Player.class);
    /**
     * new loto ticket *
     */
    public final int[] _loto = new int[5];
    /**
     * new race ticket *
     */
    public final int[] _race = new int[2];
    public final AntiFlood antiFlood = AntiFlood.create(this);
    /**
     * The table containing all Quests began by the L2Player
     */
    private final TIntObjectMap<QuestState> quests = new TIntObjectHashMap<>();
    /**
     * dyes
     */
    private final DyeData[] dyes = new DyeData[3];
    private final AtomicBoolean _isLogout = new AtomicBoolean();
    private final AtomicBoolean _isMiniMapOpened = new AtomicBoolean();
    private final Set<Integer> _activeSoulShots = new CopyOnWriteArraySet<>();
    private final AtomicInteger _observerMode = new AtomicInteger(0);
    private final Map<Integer, String> _blockList = new ConcurrentSkipListMap<>(); // characters blocked with '/block <charname>' cmd
    private final Fishing _fishing = new Fishing(this);
    private final Lock _storeLock = new ReentrantLock();
    private final List<String> _blockedActions = new ArrayList<>();
    private final Map<Integer, TimeStamp> sharedGroupReuses = new ConcurrentHashMap<>();
    private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<>();
    private final CustomPlayerComponent customPlayerComponent = CustomPlayerComponent.create(this);
    private final AppearanceComponent appearanceComponent = new AppearanceComponent(this);
    private final PremiumAccountComponent premiumAccountComponent = new PremiumAccountComponent(this);
    private final RecommendationComponent recommendationComponent = new RecommendationComponent(this);
    private final NevitComponent nevitComponent = new NevitComponent(this);
    private final ShortCutComponent shortCutComponent = new ShortCutComponent(this);
    private final MacroComponent macroComponent = new MacroComponent(this);
    private final FriendComponent friendComponent = new FriendComponent(this);
    private final EnchantParams enchantParams = new EnchantParams();
    private final PvpComponent pvpComponent = new PvpComponent(this);
    private final RefineComponent refineComponent = new RefineComponent();
    private final RecipeComponent recipeComponent = new RecipeComponent(this);
    private final LazyReference<TeleportBookMarkComponent> teleportBookMarkComponent = new LazyReference<TeleportBookMarkComponent>() {
        @Override
        protected TeleportBookMarkComponent create() throws Exception {
            return new TeleportBookMarkComponent(Player.this);
        }
    };
    private final LazyReference<PlayerTemplateComponent> playerTemplateComponent = new LazyReference<PlayerTemplateComponent>() {
        @Override
        protected PlayerTemplateComponent create() throws Exception {
            return new PlayerTemplateComponent(Player.this);
        }
    };
    private final VitalityComponent vitalityComponent = new VitalityComponent(this);
    private final PcInventory _inventory = new PcInventory(this);
    private final Warehouse _warehouse = new PcWarehouse(this);
    private final ItemContainer _refund = new PcRefund(this);
    private final PcFreight _freight = new PcFreight(this);
    private final BypassStorage _bypassStorage = new BypassStorage();
    private final LazyReference<BotPunishComponent> botPunishComponent = new LazyReference<BotPunishComponent>() {
        @Override
        protected BotPunishComponent create() throws Exception {
            return new BotPunishComponent(Player.this);
        }
    };
    private final LazyReference<PlayerClassComponent> playerClassComponent = new LazyReference<PlayerClassComponent>() {
        @Override
        protected PlayerClassComponent create() throws Exception {
            return new PlayerClassComponent(Player.this);
        }
    };
    //TODO[Hack]: запилить лоадер компонентов
    private final PlayerVariablesComponent playerVariables = new PlayerVariablesComponent(this, !isPhantom());
    private final EventComponent eventComponent = EventComponent.createEventComponent(this);
    private final VisualParams visualParams = new VisualParams();
    private final int incorrectValidateCount = 0;
    private final AtomicBoolean isActive = new AtomicBoolean();
    public boolean sittingTaskLaunched;
    /**
     * The current higher Expertise of the Player (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7)
     */
    public int expertiseIndex = 0;
    public int _telemode = 0;
    public boolean entering = true;
    /**
     * Эта точка проверяется при нештатном выходе чара, и если не равна null чар возвращается в нее
     * Используется например для возвращения при падении с виверны
     * Поле heading используется для хранения денег возвращаемых при сбое
     */
    public Location _stablePoint = null;
    /**
     * 0=White, 1=Purple, 2=PurpleBlink
     */
    protected int _pvpFlag;
    private GameClient _connection;
    private final LazyReference<AccountVariablesComponent> accountVariables = new LazyReference<AccountVariablesComponent>() {
        @Override
        protected AccountVariablesComponent create() throws Exception {
            return new AccountVariablesComponent(getNetConnection(), !isPhantom());
        }
    };
    private String _login;
    private String _ip;
    private int karma, _pkKills, _pvpKills;
    private int _fame;
    private int _deleteTimer;
    private long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;
    private long _uptime;
    private long dieTime;
    /**
     * Time on login in game
     */
    private String passwordHash;
    private long _lastAccess;
    private boolean _overloaded;
    /**
     * Time counter when L2Player is sitting
     */
    private int _waitTimeWhenSit;
    private int autoLoot = AllSettingsConfig.AUTO_LOOT ? AUTO_LOOT_ALL : AUTO_LOOT_NONE;
    private boolean _autoLootHerbs = AllSettingsConfig.AUTO_LOOT_HERBS;
    /**
     * The Private Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
     */
    private int _privatestore;
    /**
     * Данные для магазина рецептов
     */
    private String _manufactureName;
    private List<ManufactureItem> createList = Collections.emptyList();
    /**
     * Данные для магазина продажи
     */
    private String _sellStoreName;
    private List<TradeItem> sellList = Collections.emptyList();
    private List<TradeItem> packageSellList = Collections.emptyList();
    /**
     * Данные для магазина покупки
     */
    private String _buyStoreName;
    private List<TradeItem> buyList = Collections.emptyList();
    /**
     * Данные для обмена
     */
    private List<TradeItem> _tradeList = Collections.emptyList();
    private int dyeSTR, dyeINT, dyeDEX, dyeMEN, dyeWIT, dyeCON;
    private Party _party;
    private Location _lastPartyPosition;
    private Clan _clan;
    private int _pledgeClass = 0, _pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;
    /**
     * GM Stuff
     */
    private int _accessLevel;
    private PlayerAccess playerAccess = new PlayerAccess();
    private boolean _messageRefusal = false, _tradeRefusal = false, _blockAll = false;
    /**
     * The L2Summon of the Player
     */
    private Servitor _summon = null;
    private boolean _riding;
    private DecoyInstance _decoy = null;
    private Map<Integer, CubicComponent> _cubics = null;
    private Request _request;
    private ItemInstance _arrowItem;
    /**
     * The fists Weapon of the Player (used when no weapon is equipped)
     */
    private WeaponTemplate fistsWeaponItem;
    private TIntObjectMap<AccountPlayerInfo> playersOnAccount = new TIntObjectHashMap<>(6);
    private ItemInstance _enchantScroll = null;
    private WarehouseType _usingWHType;
    private boolean _isOnline = false;
    private AtomicBoolean noCarrier = new AtomicBoolean();
    private AtomicBoolean loadingAfterCarrier = new AtomicBoolean();
    /**
     * The NpcInstance corresponding to the last Folk which one the player talked.
     */
    private HardReference<NpcInstance> _lastNpc = HardReferences.emptyRef();
    /**
     * тут храним мультиселл с которым работаем
     */
    private MultiSellListContainer _multisell = null;
    private ObservePoint _observePoint;
    private int _handysBlockCheckerEventArena = -1;
    private boolean _hero = false;
    /**
     * True if the L2Player is in a boat
     */
    private Boat _boat;
    private Location _inBoatPosition;
    private boolean _block_transform;
    private boolean _isSitting;
    private StaticObjectInstance _sittingObject;
    private boolean _noble = false;
    private boolean _inOlympiadMode;
    private OlympiadGame _olympiadGame;
    private OlympiadGame _olympiadObserveGame;
    private int _olympiadSide = -1;
    /**
     * ally with ketra or varka related wars
     */
    private int _varka = 0;
    private int _ketra = 0;
    private int _ram = 0;
    private byte[] _keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
    private int _cursedWeaponEquippedId = 0;
    private boolean _isFishing;
    private Future<?> _taskWater;
    private Future<?> _autoSaveTask;
    private Future<?> _kickTask;
    private Future<?> _unjailTask;
    private ScheduledFuture<?> noCarrierSchedule;
    private boolean offline = false;
    private int _lectureMark;
    private Future<?> _lectureEndTask;
    private TIntObjectMap<String> postFriends = TroveUtils.emptyIntObjectMap();
    private boolean _notShowBuffAnim = false;
    private boolean _notShowTraders = false;
    private boolean _debug = false;
    private long _dropDisabled;
    private long _lastItemAuctionInfoRequest;
    private long _lastNpcInteractionTime = 0;
    private Pair<Integer, OnAnswerListener> askDialog;
    private PlayerEnterLeaveTransformListener transformListener;
    private boolean _matchingRoomWindowOpened = false;
    private MatchingRoom _matchingRoom;
    private PetitionMainGroup _petitionGroup;
    private List<TrapInstance> _traps = Collections.emptyList();
    private List<int[]> _savedServitors = Collections.emptyList();
    private Future<?> _enableRelationTask;
    private List<NpcInstance> _tamedBeasts = Collections.emptyList();
    private boolean _forceVisualFlag;
    private boolean isOlympiadEnchant;
    private boolean isTalismanStack;
    private boolean isHwidLockVisual;
    private String enterHwid;
    private AtomicBoolean isInLastHero = new AtomicBoolean();
    /**
     * Подсистемы\компоненты
     */
    private TransformComponent transformation;
    private AcpComponent acpComponent;
    private ICommunityComponent communityComponent;
    private AgathionComponent _agathion;
    private Future<?> _updateEffectIconsTask;
    private ScheduledFuture<?> _broadcastCharInfoTask;
    private int _polyNpcId;
    private Future<?> _userInfoTask;
    private int _mountNpcId;
    private int _mountObjId;
    private int _mountLevel;
    private int _mountCurrentFed;
    private int _mountMaxFed;
    private int[] _mountCurrentFedId;
    private ScheduledFuture<?> _mountFeedTask = null;
    private boolean _maried = false;
    private int _partnerId = 0;
    private int _coupleId = 0;
    private boolean _maryrequest = false;
    private boolean _maryaccepted = false;
    private boolean _charmOfCourage = false;
    private int _increasedForce = 0;
    private int _consumedSouls = 0;
    private long _lastFalling;
    private int _useSeed = 0;
    private Future<?> _PvPRegTask;
    private long _lastPvpAttack;
    private long _lastAttackPacket = 0;
    private volatile long _lastMovePacket = 0;
    private ExpirationReference<Double> lastMovePacketDestinationDiff;
    private Location _groundSkillLoc;
    private int _buyListId;
    private int _movieId = 0;
    private boolean _isInMovie;
    // ------------------- Quest Engine ----------------------
    private ItemInstance _petControlItem = null;
    private Future<?> _hourlyTask;
    private int _hoursInGame = 0;
    private boolean _agathionResAvailable = false;
    /**
     * _userSession - испольюзуется для хранения временных переменных.
     */
    private Map<String, String> _userSession;

    /**
     * Конструктор для Player. Напрямую не вызывается, для создания игрока используется PlayerManager.create
     */
    public Player(final int objectId, final String accountName) {
        super(objectId, null);
        _login = accountName;
        getAppearanceComponent().setNameColor(0xFFFFFF, false);
        getAppearanceComponent().setTitleColor(0xFFFF77, false);
        getPlayerClassComponent().setBaseClass(getPlayerClassComponent().getClassId().getId());
    }

    /**
     * Constructor<?> of L2Player (use L2Character constructor).<BR><BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to create an empty skills slot and copy basic Calculator set to this L2Player </li>
     * <li>Create a L2Radar object</li>
     * <li>Retrieve from the database all items of this L2Player and add them to _inventory </li>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR><BR>
     *
     * @param objectId Identifier of the object to initialized
     */
    private Player(final int objectId) {
        this(objectId, null);

        _ai = new PlayerAI(this);

        if (!ServerConfig.EVERYBODY_HAS_ADMIN_RIGHTS) {
            setPlayerAccess(GmAccessConfig.gmlist.get(objectId));
        } else {
            setPlayerAccess(GmAccessConfig.gmlist.get(0));
        }
        if (AcpConfig.allowAcp) {
            acpComponent = new AcpComponent(this);
        }
    }

    /**
     * Create a new L2Player and add it in the characters table of the database.<BR><BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Create a new L2Player with an account name </li>
     * <li>Set the name, the Hair Style, the Hair Color and	the Face type of the L2Player</li>
     * <li>Add the player in the characters table of the database</li><BR><BR>
     *
     * @param accountName The name of the L2Player
     * @param name        The name of the L2Player
     * @param hairStyle   The hair style Identifier of the L2Player
     * @param hairColor   The hair color Identifier of the L2Player
     * @param face        The face type Identifier of the L2Player
     * @return The L2Player added to the database or null
     */
    public static Player create(final int classId, final int raceId, final int sex, final String accountName, final String name, final int hairStyle, final int hairColor, final int face) {
        // Create a new L2Player with an account name
        final Player player = new Player(IdFactory.getInstance().getNextId(), accountName);

        player.setName(name);
        player.setTitle("");
        player.getAppearanceComponent().setHairStyle(hairStyle);
        player.getAppearanceComponent().setHairColor(hairColor);
        player.getAppearanceComponent().setFace(face);
        player.getPlayerTemplateComponent().setPlayerRace(PlayerRace.values()[raceId]);
        player.getPlayerTemplateComponent().setPlayerSex(PlayerSex.values()[sex]);
        player.setCreateTime(System.currentTimeMillis());

        // Add the player in the characters table of the database
        if (!CharacterDAO.getInstance().insert(player))
            return null;

        final double hp = PCParameterUtils.getLevelParameter(1, PCParameterUtils.getClassDataInfoFor(classId).getHp());
        final double mp = PCParameterUtils.getLevelParameter(1, PCParameterUtils.getClassDataInfoFor(classId).getMp());
        final double cp = PCParameterUtils.getLevelParameter(1, PCParameterUtils.getClassDataInfoFor(classId).getCp());

        if (!CharacterDAO.getInstance().insertSub(player.getObjectId(), classId, hp, mp, cp, hp, mp, cp))
            return null;
        return player;
    }

    /**
     * Retrieve a L2Player from the characters table of the database and add it in _allObjects of the L2World
     *
     * @return The L2Player loaded from the database
     */
    public static Player restore(final int objectId) {
        Player player = null;
        Connection con = null;
        Statement statement = null;
        Statement statement2 = null;
        PreparedStatement statement3 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        try {
            // Retrieve the Player from the characters table of the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement2 = con.createStatement();
            rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
            rset2 = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `isBase`=1 LIMIT 1");

            if (rset.next() && rset2.next()) {
                final int classId = rset2.getInt("class_id");
                final PlayerSex playerSex = PlayerSex.values()[rset.getInt("sex")];
                final PlayerRace playerRace = PlayerRace.values()[rset.getInt("race")];

                player = new Player(objectId);

                player.getPlayerTemplateComponent().setPlayerRace(playerRace);
                player.getPlayerTemplateComponent().setPlayerSex(playerSex);
                player.loadInstanceReuses();
                player.getPremiumAccountComponent().loadPremiumItemList();
                player.getTeleportBookMarkComponent().setTpBookmarkSize(rset.getInt("bookmarks"));
                player.getTeleportBookMarkComponent().restoreMarks();
                player.getFriendComponent().restore();
                player.postFriends = CharacterPostFriendDAO.getInstance().select(player);
                player._savedServitors = CharacterServitorDAO.getInstance().select(player);
                CharacterGroupReuseDAO.getInstance().select(player);

                player.getPlayerClassComponent().setBaseClass(classId);
                player._login = rset.getString("account_name");
                player.setName(rset.getString("char_name"));

                player.getAppearanceComponent().setFace(rset.getInt("face"));
                player.getAppearanceComponent().setHairStyle(rset.getInt("hairStyle"));
                player.getAppearanceComponent().setHairColor(rset.getInt("hairColor"));
                player.setHeading(0);

                player.setKarma(rset.getInt("karma"));
                player.setPvpKills(rset.getInt("pvpkills"));
                player.setPkKills(rset.getInt("pkkills"));
                player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
                if (player.getLeaveClanTime() > 0 && player.canJoinClan()) {
                    player.setLeaveClanTime(0);
                }
                player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
                if (player.getDeleteClanTime() > 0 && player.canCreateClan()) {
                    player.setDeleteClanTime(0);
                }

                player.setNoChannel(rset.getLong("nochannel") * 1000L);
                if (player.getNoChannel() > 0 && player.getNoChannelRemained() < 0) {
                    player.setNoChannel(0);
                }

                player.setOnlineTime(rset.getLong("onlinetime") * 1000L);

                final int clanId = rset.getInt("clanid");
                if (clanId > 0) {
                    player.setClan(ClanTable.getInstance().getClan(clanId));
                    player.setPledgeType(rset.getInt("pledge_type"));
                    player.setPowerGrade(rset.getInt("pledge_rank"));
                    player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
                    player.setApprentice(rset.getInt("apprentice"));
                }

                player.setCreateTime(rset.getLong("createtime") * 1000L);
                player.setDeleteTimer(rset.getInt("deletetime"));

                player.setTitle(rset.getString("title"));

                if (player.playerVariables.get(PlayerVariables.TITLE_COLOR) != null) {
                    player.getAppearanceComponent().setTitleColor(Integer.decode("0x" + player.playerVariables.get(PlayerVariables.TITLE_COLOR)));
                }

                if (player.playerVariables.get(PlayerVariables.NAME_COLOR) == null) {
                    if (player.isGM()) {
                        player.getAppearanceComponent().setNameColor(OtherConfig.GM_NAME_COLOUR);
                    } else if (player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId()) {
                        player.getAppearanceComponent().setNameColor(OtherConfig.CLANLEADER_NAME_COLOUR);
                    } else {
                        player.getAppearanceComponent().setNameColor(OtherConfig.NORMAL_NAME_COLOUR);
                    }
                } else {
                    player.getAppearanceComponent().setNameColor(Integer.decode("0x" + player.playerVariables.get(PlayerVariables.NAME_COLOR)));
                }

                if (player.getPlayerVariables().get(PlayerVariables.TALISMAN_STACK) != null)
                    player.setTalismanStack(true);

                if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
                    player.setAutoLoot(player.playerVariables.getInt(PlayerVariables.AUTO_LOOT, AllSettingsConfig.AUTO_LOOT ? AUTO_LOOT_ALL : AUTO_LOOT_NONE));
                    player.setAutoLootHerbs(player.playerVariables.getBoolean(PlayerVariables.AUTO_LOOT_HERBS, AllSettingsConfig.AUTO_LOOT_HERBS));
                }

                player.setUptime(System.currentTimeMillis());
                player.setLastAccess(rset.getLong("lastAccess"));

                player.getRecommendationComponent().setRecomHave(rset.getInt("rec_have"));
                player.getRecommendationComponent().setRecomLeft(rset.getInt("rec_left"));
                player.getRecommendationComponent().setRecomBonusTime(rset.getInt("rec_bonus_time"));

                if (player.playerVariables.get(PlayerVariables.REC_LEFT_TODAY) != null) {
                    player.getRecommendationComponent().setRecomLeftToday(Integer.parseInt(player.playerVariables.get(PlayerVariables.REC_LEFT_TODAY)));
                } else {
                    player.getRecommendationComponent().setRecomLeftToday(0);
                }

                player.getNevitComponent().setPoints(rset.getInt("hunt_points"), rset.getInt("hunt_time"));

                player.setKeyBindings(rset.getBytes("key_bindings"));
                player.getPremiumAccountComponent().setPcBangPoints(rset.getInt("pcBangPoints"));

                player.setFame(rset.getInt("fame"));

                if (OlympiadConfig.ENABLE_OLYMPIAD) {
                    player.setHero(Hero.getInstance().isHero(player.getObjectId()));
                    player.setNoble(Olympiad.isNoble(player.getObjectId()));
                }

                player.updatePledgeClass();

                int reflection = 0;

                if (player.playerVariables.get(PlayerVariables.JAILED) != null && System.currentTimeMillis() / 1000 < Integer.parseInt(player.playerVariables.get(PlayerVariables.JAILED)) + 60) {
                    player._unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), Integer.parseInt(player.playerVariables.get(PlayerVariables.JAILED)) * 1000L);
                    player.setXYZ(-114648, -249384, -2984);
                    reflection = ReflectionManager.JAIL.getId();
					/*NOTUSED*String[] re = player.playerVariables.get("jailedFrom").split(";");
					Location loc = new Location(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));*/
                    player.sitDown(null);
                    player.block();
                } else {
                    player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
                    final String ref = player.playerVariables.get(PlayerVariables.REFLECTION);
                    if (ref != null) {
                        reflection = Integer.parseInt(ref);
                        if (reflection > 0) // не портаем назад из ГХ, парнаса, джайла
                        {
                            final String back = player.playerVariables.get(PlayerVariables.BACK_COORDINATES);
                            if (back != null) {
                                player.setLoc(Location.parseLoc(back));
                                player.playerVariables.remove(PlayerVariables.BACK_COORDINATES);
                            }
                            reflection = 0;
                        }
                    }
                }

                player.setReflection(reflection);

                try {
                    EventHolder.getInstance().findEvent(player);
                } catch (final Exception e) {
                    _log.error("EventHolder returned exception in player init(restore): ", e);
                }

                player.getInventory().restore();
                player.getRecipeComponent().restore();
                player.getPlayerClassComponent().restoreCharSubClasses();
                // Активируем CW после загрузки сабов, иначе скиллы будут удалены
                for (final ItemInstance item : player.getInventory().getItems()) {
                    if (item.isCursed()) {
                        CursedWeaponsManager.getInstance().checkPlayer(player, item);
                    }
                }

                // 4 очка в минуту оффлайна
                player.getVitalityComponent().setVitality(rset.getInt("vitality") + (int) ((System.currentTimeMillis() / 1000L - rset.getLong("lastAccess")) / 15.));

                try {
                    final String var = player.playerVariables.get(PlayerVariables.EXPAND_INVENTORY);
                    if (var != null) {
                        player.getPremiumAccountComponent().setExpandInventory(Integer.parseInt(var));
                    }
                } catch (final Exception e) {
                    _log.error("", e);
                }
                try {
                    final String var = player.playerVariables.get(PlayerVariables.EXPAND_PRIVATE_STORE);
                    if (var != null) {
                        player.getPremiumAccountComponent().setPrivateStore(Integer.parseInt(var));
                    }
                } catch (final Exception e) {
                    _log.error("", e);
                }

                try {
                    final String var = player.playerVariables.get(PlayerVariables.EXPAND_WAHEHOUSE);
                    if (var != null) {
                        player.getPremiumAccountComponent().setExpandWarehouse(Integer.parseInt(var));
                    }
                } catch (final Exception e) {
                    _log.error("", e);
                }

                try {
                    final String var = player.playerVariables.get(PlayerVariables.NO_ANIMATION_OF_CAST);
                    if (var != null) {
                        player.setNotShowBuffAnim(var.equals("1"));
                    }
                } catch (final Exception e) {
                    _log.error("", e);
                }

                try {
                    final String var = player.playerVariables.get(PlayerVariables.NO_TRADERS);
                    if (var != null) {
                        player.setNotShowTraders(Boolean.parseBoolean(var));
                    }
                } catch (final Exception e) {
                    _log.error("", e);
                }

                statement3 = con.prepareStatement("SELECT obj_Id, char_name, createtime FROM characters WHERE account_name=? AND obj_Id!=?");
                statement3.setString(1, player._login);
                statement3.setInt(2, objectId);
                rset3 = statement3.executeQuery();
                while (rset3.next()) {
                    player.playersOnAccount.put(rset3.getInt("obj_Id"), new AccountPlayerInfo(rset3.getInt("createtime"), rset3.getString("char_name")));
                }

                DbUtils.close(statement3, rset3);

                player.setLectureMark(ExtConfig.EX_LECTURE_MARK ? AccountLectureMarkDAO.getInstance().select(player.getAccountName()) : 0, false);

                //if(!player.isGM())
                {
                    final List<Zone> zones = new LazyArrayList<>();
                    World.getZones(zones, player.getLoc(), player.getReflection());

                    if (!zones.isEmpty()) {
                        for (final Zone zone : zones) {
                            if (zone.getType() == ZoneType.no_restart) {
                                if (System.currentTimeMillis() / 1000L - player.getLastAccess() > zone.getRestartTime()) {
                                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart"));
                                    player.setLoc(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE));
                                }
                            } else if (zone.getType() == ZoneType.SIEGE) {
                                final Residence residence = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
                                SiegeEvent<?, ?> siegeEvent = null;
                                for (final Event e : player.getEvents()) {
                                    if (e instanceof SiegeEvent && ((SiegeEvent) e).getResidence() == residence) {
                                        siegeEvent = (SiegeEvent) e;
                                        break;
                                    }
                                }

                                if (siegeEvent != null) {
                                    player.setLoc(siegeEvent.getEnterLoc(player, zone));
                                } else {
                                    player.setLoc(residence.getNotOwnerRestartPoint(player));
                                }
                            }
                        }
                    }

                    zones.clear();

                    if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false)) {
                        player.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
                    }
                }

                player.restoreBlockList();
                player.getMacroComponent().restore();

                //FIXME [VISTALL] нужно ли?
                player.refreshExpertisePenalty();
                player.refreshOverloaded();

                player.getWarehouse().restore();
                player.getFreight().restore();

                player.restoreTradeList();
                if (player.playerVariables.get(PlayerVariables.STORE_MODE) != null) {
                    player.setPrivateStoreType(Integer.parseInt(player.playerVariables.get(PlayerVariables.STORE_MODE)));
                    player.setSitting(true);
                }

                player.updateKetraVarka();
                player.updateRam();
                player.getRecommendationComponent().checkRecom();
                CharacterProductDAO.getInstance().select(con, player);
                player.setHwidLockVisual(HwidLocksDAO.getInstance().getHwid(player.getAccountName()) != null);

                if (!player._savedServitors.isEmpty()) {
                    for (final int[] ar : player._savedServitors) {
                        if (ar[0] == Servitor.PET_TYPE) {
                            final ItemInstance controlItem = player.getInventory().getItemByObjectId(ar[1]);
                            if (controlItem != null && PetDataHolder.getInstance().isPetControlItem(controlItem.getItemId())) {
                                player.setPetControlItem(controlItem);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error("Could not restore char data for playerObjId: " + objectId, e);
        } finally {
            DbUtils.closeQuietly(statement2, rset2);
            DbUtils.closeQuietly(statement3, rset3);
            DbUtils.closeQuietly(con, statement, rset);
        }
        return player;
    }

    public static String getVarFromPlayer(final int objId, final String var) {
        String value = null;
        Connection con = null;
        PreparedStatement offline = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT value FROM character_variables WHERE obj_id = ? AND name = ?");
            offline.setInt(1, objId);
            offline.setString(2, var);
            rs = offline.executeQuery();
            if (rs.next()) {
                value = Strings.stripSlashes(rs.getString("value"));
            }
        } catch (final Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, offline, rs);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<Player> getRef() {
        return (HardReference<Player>) super.getRef();
    }

    public PremiumAccountComponent getPremiumAccountComponent() {
        return premiumAccountComponent;
    }

    public RecommendationComponent getRecommendationComponent() {
        return recommendationComponent;
    }

    public NevitComponent getNevitComponent() {
        return nevitComponent;
    }

    public MacroComponent getMacroComponent() {
        return macroComponent;
    }

    // ----------------- End of Quest Engine -------------------

    public ShortCutComponent getShortCutComponent() {
        return shortCutComponent;
    }

    public FriendComponent getFriendComponent() {
        return friendComponent;
    }

    public TeleportBookMarkComponent getTeleportBookMarkComponent() {
        return teleportBookMarkComponent.get();
    }

    public PlayerTemplateComponent getPlayerTemplateComponent() {
        return playerTemplateComponent.get();
    }

    public VitalityComponent getVitalityComponent() {
        return vitalityComponent;
    }

    public BypassStorage getBypassStorage() {
        return _bypassStorage;
    }

    public BotPunishComponent getBotPunishComponent() {
        return botPunishComponent.get();
    }

    public PlayerClassComponent getPlayerClassComponent() {
        return playerClassComponent.get();
    }

    public PlayerVariablesComponent getPlayerVariables() {
        return playerVariables;
    }

    public AccountVariablesComponent getAccountVariables() {
        return accountVariables.get();
    }

    public String getAccountName() {
        if (_connection == null) {
            return _login;
        }
        return _connection.getLogin();
    }

    public String getIP() {
        if (_connection == null) {
            return _ip;
        }
        return _connection.getIpAddr();
    }

    public String getLastIp() {
        return _ip;
    }

    public void setLastIp(final String ip) {
        _ip = ip;
    }

    /**
     * Возвращает список персонажей на аккаунте, за исключением текущего
     *
     * @return Список персонажей
     */
    public TIntObjectMap<AccountPlayerInfo> getAccountChars() {
        return playersOnAccount;
    }

    @Override
    public PlayerAI getAI() {
        return (PlayerAI) _ai;
    }

    @Override
    public void sendReuseMessage(final SkillEntry skill) {
        if (isCastingNow()) {
            return;
        }
        final TimeStamp sts = getSkillReuse(skill);
        if (sts == null || !sts.hasNotPassed()) {
            return;
        }
        final long timeleft = sts.getReuseCurrent();
        if (!AllSettingsConfig.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500) {
            return;
        }
        final long hours = timeleft / 3600000;
        final long minutes = (timeleft - hours * 3600000) / 60000;
        final long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
        if (hours > 0) {
            sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
        } else if (minutes > 0) {
            sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
        } else {
            sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
        }
    }

    @Override
    public final int getLevel() {
        return getPlayerClassComponent().getActiveClass() == null ? 1 : getPlayerClassComponent().getActiveClass().getLevel();
    }

    public void setLevel(int level) {
        addExpAndSp(ExpDataHolder.getInstance().getExpForLevel(level) - ExpDataHolder.getInstance().getExpForLevel(getLevel()), 0);
    }

    public int getSex() {
        return getPlayerTemplateComponent().getPlayerSex().ordinal();
    }

    public void offline() {
        getBotPunishComponent().requestReportsPoints(true, getBotPunishComponent().getReportsPoints());
        if (_connection != null) {
            _connection.setActiveChar(null);
            _connection.close(ServerClose.STATIC);
            setNetConnection(null);
        }

        if (ServicesConfig.SERVICES_OFFLINE_TRADE_CHANGE_NAME_COLOR) {
            getAppearanceComponent().setNameColor(ServicesConfig.SERVICES_OFFLINE_TRADE_NAME_COLOR);
        }
        setOfflineMode(true);
        playerVariables.set(PlayerVariables.OFFLINE, String.valueOf(System.currentTimeMillis() / 1000L), -1);

        if (ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0) {
            startKickTask(ServicesConfig.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK * 1000L);
        }

        final Party party = getParty();
        if (party != null) {
            if (isFestivalParticipant()) {
                party.broadcastMessageToPartyMembers(getName() + " has been removed from the upcoming festival.");
            }
            leaveParty();
        }

        if (getServitor() != null) {
            getServitor().unSummon(false, false);
        }

        CursedWeaponsManager.getInstance().doLogout(this);

        //TODO: нужно ли?
        if (isTransformed()) {
            stopTransformation(true);
        }

        if (isInOlympiadMode()) {
            Olympiad.logoutPlayer(this);
        }

        broadcastCharInfo();

        //TODO [VISTALL] call stopAllTimers() ?
        stopWaterTask();
        getPremiumAccountComponent().stopBonusTask();
        stopHourlyTask();
        getVitalityComponent().stopVitalityTask();
        getPremiumAccountComponent().stopPcBangPointsTask();
        stopAutoSaveTask();
        getRecommendationComponent().stopRecomBonusTask(true);
        stopLectureTask();
        stopQuestTimers();
        stopEnableUserRelationTask();
        getNevitComponent().stopTasksOnLogout();

        try {
            getInventory().store();
        } catch (final Throwable t) {
            _log.error("", t);
        }

        try {
            store(false);
        } catch (final Throwable t) {
            _log.error("", t);
        }
    }

    public void kick(final boolean botPunish) {
        if (botPunish)
            getBotPunishComponent().requestReportsPoints(true, getBotPunishComponent().getReportsPoints());
        if (_connection != null) {
            _connection.close(LeaveWorld.STATIC);
            setNetConnection(null);
        }
        prepareToLogout();
        deleteMe();
    }

    /**
     * Соединение закрывается, клиент закрывается, персонаж сохраняется и удаляется из игры
     */
    public void kick() {
        kick(true);
    }

    /**
     * Соединение не закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
     */
    public void restart() {
        getBotPunishComponent().requestReportsPoints(true, getBotPunishComponent().getReportsPoints());
        if (_connection != null) {
            _connection.setActiveChar(null);
            setNetConnection(null);
        }
        prepareToLogout();
        deleteMe();
    }

    @Override
    public void deleteMe() {
        noCarrier.set(false);
        super.deleteMe();
    }

    /**
     * Соединение закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
     */
    public void logout() {
        getBotPunishComponent().requestReportsPoints(true, getBotPunishComponent().getReportsPoints());
        if (_connection != null) {
            _connection.close(ServerClose.STATIC);
            setNetConnection(null);
        }
        prepareToLogout();
        deleteMe();
    }

    private void prepareToLogout() {
        if (_isLogout.getAndSet(true)) {
            return;
        }

        if (isOlympiadEnchant())
            setOlympiadEnchant(false);

        setNetConnection(null);
        setIsOnline(false);

        getListeners().onExit();

        if (isFlying() && !checkLandingState()) {
            _stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);
        }

        if (isCastingNow()) {
            abortCast(true, true);
        }

        final Party party = getParty();
        if (party != null) {
            if (isFestivalParticipant()) {
                party.broadcastMessageToPartyMembers(getName() + " has been removed from the upcoming festival."); //TODO [G1ta0] custom message
            }
            leaveParty();
        }

        CursedWeaponsManager.getInstance().doLogout(this);

        if (_olympiadObserveGame != null) {
            _olympiadObserveGame.removeSpectator(this);
        }

        if (isInOlympiadMode()) {
            Olympiad.logoutPlayer(this);
        }

        stopFishing();

        if (_stablePoint != null) {
            teleToLocation(_stablePoint);
        }

        final Servitor pet = getServitor();
        if (pet != null) {
            pet.unSummon(true, true);

            CharacterServitorDAO.getInstance().insert(this, pet);
        }

        if (isMounted()) {
            PetDAO.getInstance().updateMount(getMountObjId(), getMountCurrentFed());
        }

        getFriendComponent().notifyFriends(false);

        if (isProcessingRequest()) {
            getRequest().cancel();
        }

        stopAllTimers();

        if (isInBoat()) {
            getBoat().removePlayer(this);
        }

        final SubUnit unit = getSubUnit();
        final UnitMember member = unit == null ? null : unit.getUnitMember(getObjectId());
        if (member != null) {
            final int sponsor = member.getSponsor();
            final int apprentice = getApprentice();
            final PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
            for (final Player clanMember : _clan.getOnlineMembers(getObjectId())) {
                clanMember.sendPacket(memberUpdate);
                if (clanMember.getObjectId() == sponsor) {
                    clanMember.sendPacket(new SystemMessage(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT).addString(_name));
                } else if (clanMember.getObjectId() == apprentice) {
                    clanMember.sendPacket(new SystemMessage(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_OUT).addString(_name));
                }
            }
            member.setPlayerInstance(this, true);
        }

        final FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if (attachment != null) {
            attachment.onLogout(this);
        }

        if (CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null) {
            CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
        }

        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT) {
            if (getBotPunishComponent().isBeingPunished()) {
                try {
                    BotReportManager.getInstance().savePlayerPunish(this);
                } catch (final Exception e) {
                    _log.warn("Player is not store bot punish: " + e);
                }
            }
        }

        final MatchingRoom room = getMatchingRoom();
        if (room != null) {
            if (room.getGroupLeader() == this) {
                room.disband();
            } else {
                room.removeMember(this, false);
            }
        }
        setMatchingRoom(null);

        MatchingRoomManager.getInstance().removeFromWaitingList(this);

        destroyAllTraps();

        if (_decoy != null) {
            _decoy.unSummon();
            _decoy = null;
        }

        _tamedBeasts.forEach(NpcInstance::deleteMe);

        stopPvPFlag();

        final Reflection ref = getReflection();

        if (ref != ReflectionManager.DEFAULT) {
            if (ref.getReturnLoc() != null) {
                _stablePoint = ref.getReturnLoc();
            }

            ref.removeObject(this);
        }

        if (!isPhantom()) {
            try {
                getInventory().store();
                getRefund().clear();
            } catch (final Throwable t) {
                _log.error("", t);
            }

            try {
                store(false);
            } catch (final Throwable t) {
                _log.error("", t);
            }
        }
    }

    public QuestState getQuestState(final int questId) {
        questRead.lock();
        try {
            return quests.get(questId);
        } finally {
            questRead.unlock();
        }
    }

    public QuestState getQuestState(final Quest quest) {
        return getQuestState(quest.getId());
    }

    public boolean isQuestCompleted(final int quest) {
        final QuestState q = getQuestState(quest);
        return q != null && q.isCompleted();
    }

    public void addQuestState(final QuestState qs) {
        questWrite.lock();
        try {
            quests.put(qs.getQuest().getId(), qs);
        } finally {
            questWrite.unlock();
        }
    }

    public void removeQuestState(final int quest) {
        questWrite.lock();
        try {
            quests.remove(quest);
        } finally {
            questWrite.unlock();
        }
    }

    public Quest[] getAllActiveQuests() {
        final List<Quest> quests = new ArrayList<>(this.quests.size());
        questRead.lock();
        try {
            for (final QuestState qs : this.quests.valueCollection()) {
                if (qs.isStarted()) {
                    quests.add(qs.getQuest());
                }
            }
        } finally {
            questRead.unlock();
        }
        return quests.toArray(new Quest[quests.size()]);
    }

    public QuestState[] getAllQuestsStates() {
        questRead.lock();
        try {
            return quests.values(new QuestState[quests.size()]);
        } finally {
            questRead.unlock();
        }
    }

    public List<QuestState> getQuestsForEvent(final NpcInstance npc, final QuestEventType event) {
        final List<QuestState> states = new ArrayList<>();
        final Quest[] quests = npc.getTemplate().getEventQuests(event);
        QuestState qs;
        if (quests != null) {
            for (final Quest quest : quests) {
                qs = getQuestState(quest);
                if (qs != null && !qs.isCompleted()) {
                    states.add(getQuestState(quest));
                }
            }
        }
        return states;
    }

    public void processQuestEvent(final int questId, String event, final NpcInstance npc) {
        if (event == null) {
            event = "";
        }
        QuestState qs = getQuestState(questId);
        if (qs == null) {
            final Quest q = QuestManager.getQuest(questId);
            if (q == null) {
                _log.warn("Player.processQuestEvent(int, String, Objest): Quest " + questId + " not found!");
                return;
            }
            qs = q.newQuestState(this, Quest.CREATED);
        }
        if (qs == null || qs.isCompleted()) {
            return;
        }
        qs.getQuest().notifyEvent(event, qs, npc);
        sendPacket(new QuestList(this));
    }

    public void createNewQuestState(final int questId) {
        QuestState qs = getQuestState(questId);
        if (qs == null) {
            final Quest quest = QuestManager.getQuest(questId);
            if (quest == null) {
                _log.warn("Player.createNewQuestState(int): Quest " + questId + " not found!");
                return;
            }
            qs = quest.newQuestStateAndNotSave(this, Quest.CREATED);
        }
        if (qs == null || qs.isCompleted()) {
            return;
        }
    }

    /**
     * Проверка на переполнение инвентаря и перебор в весе для квестов и эвентов
     *
     * @return true если ве проверки прошли успешно
     */
    public boolean isQuestContinuationPossible(final boolean msg) {
        if (getWeightPenalty() >= 3 || getInventoryLimit() * 0.9 < getInventory().getSize() || OtherConfig.INVENTORY_BASE_QUEST * 0.9 < getInventory().getQuestSize()) {
            if (msg) {
                sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            }
            return false;
        }
        return true;
    }

    /**
     * Проверка на переполнение перебор в весе
     *
     * @return true если ве проверки прошли успешно
     */
    public boolean isWeightLimit(final boolean msg) {
        if (getWeightPenalty() >= 3) {
            if (msg) {
                sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            }
            return false;
        }
        return true;
    }

    /**
     * Проверка на переполнение инвентаря
     *
     * @return true если ве проверки прошли успешно
     */
    public boolean isInventoryLimit(final boolean msg) {
        if (getInventoryLimit() * 0.8 < getInventory().getSize()) {
            if (msg) {
                sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            }
            return false;
        }
        return true;
    }

    /**
     * Останавливаем и запоминаем все квестовые таймеры
     */
    public void stopQuestTimers() {
        for (final QuestState qs : getAllQuestsStates()) {
            if (qs.isStarted()) {
                qs.pauseQuestTimers();
            } else {
                qs.stopQuestTimers();
            }
        }
    }

    /**
     * Восстанавливаем все квестовые таймеры
     */
    public void resumeQuestTimers() {
        for (final QuestState qs : getAllQuestsStates()) {
            qs.resumeQuestTimers();
        }
    }

    public boolean isCastleLord(final int castleId) {
        return _clan != null && isClanLeader() && _clan.getCastle() == castleId;
    }

    /**
     * Проверяет является ли этот персонаж владельцем крепости
     *
     * @param fortressId
     * @return true если владелец
     */
    public boolean isFortressLord(final int fortressId) {
        return _clan != null && isClanLeader() && _clan.getHasFortress() == fortressId;
    }

    public int getPkKills() {
        return _pkKills;
    }

    public void setPkKills(final int pkKills) {
        _pkKills = pkKills;
    }

    public long getCreateTime() {
        return _createTime;
    }

    public void setCreateTime(final long createTime) {
        _createTime = createTime;
    }

    public int getDeleteTimer() {
        return _deleteTimer;
    }

    public void setDeleteTimer(final int deleteTimer) {
        _deleteTimer = deleteTimer;
    }

    public int getCurrentLoad() {
        return getInventory().getTotalWeight();
    }

    public long getLastAccess() {
        return _lastAccess;
    }

    public void setLastAccess(final long value) {
        _lastAccess = value;
    }

    @Override
    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        if (karma < 0) {
            karma = 0;
        }

        if (this.karma == karma) {
            return;
        }

        this.karma = karma;

        sendChanges();

        if (getServitor() != null) {
            getServitor().broadcastCharInfo();
        }
    }

    public int getAgathionId() {
        return _agathion == null ? 0 : _agathion.getId();
    }

    public int getAgathionNpcId() {
        return _agathion == null ? 0 : _agathion.getNpcId();
    }

    public AgathionComponent getAgathion() {
        return _agathion;
    }

    public void addAgathion(final Agathion template) {
        _agathion = new AgathionComponent(this, template);
    }

    public void deleteAgathion() {
        if (_agathion != null) {
            _agathion.delete();
            _agathion = null;
            broadcastCharInfo();
        }
    }

    @Override
    public void updateEffectIcons() {
        if (entering || isLogoutStarted()) {
            return;
        }

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

    private void updateEffectIconsImpl() {
        final Effect[] effects = getEffectList().getAllFirstEffects();
        Arrays.parallelSort(effects, EffectsComparator.getInstance());

        final PartySpelled ps = new PartySpelled(this, false);
        final AbnormalStatusUpdate mi = new AbnormalStatusUpdate();

        for (final Effect effect : effects) {
            if (effect.isInUse()) {
                if (effect.getStackType() == EffectTemplate.HP_RECOVER_CAST) {
                    sendPacket(new ShortBuffStatusUpdate(effect));
                } else {
                    effect.addIcon(mi);
                }
                if (_party != null) {
                    effect.addPartySpelledIcon(ps);
                }
            }
        }

        sendPacket(mi);
        if (_party != null) {
            _party.broadCast(ps);
        }

        if (isInOlympiadMode() && isOlympiadCompStart()) {
            final OlympiadGame olymp_game = _olympiadGame;
            if (olymp_game != null) {
                final ExOlympiadSpelledInfo olympiadSpelledInfo = new ExOlympiadSpelledInfo();

                for (final Effect effect : effects) {
                    if (effect != null && effect.isInUse()) {
                        effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);
                    }
                }

                if (olymp_game.getType() == CompType.CLASSED || olymp_game.getType() == CompType.NON_CLASSED) {
                    for (final Player member : olymp_game.getTeamMembers(this)) {
                        member.sendPacket(olympiadSpelledInfo);
                    }
                }

                for (final Player member : olymp_game.getSpectators()) {
                    member.sendPacket(olympiadSpelledInfo);
                }
            }
        }
    }

    public int getWeightPenalty() {
        return getSkillLevel(4270, 0);
    }

    public void refreshOverloaded() {
        if (isLogoutStarted() || getMaxLoad() <= 0) {
            return;
        }

        setOverloaded(getCurrentLoad() > getMaxLoad());
        final double weightproc = 100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null)) / getMaxLoad();
        final int newWeightPenalty;

        if (weightproc < 50) {
            newWeightPenalty = 0;
        } else if (weightproc < 66.6) {
            newWeightPenalty = 1;
        } else if (weightproc < 80) {
            newWeightPenalty = 2;
        } else if (weightproc < 100) {
            newWeightPenalty = 3;
        } else {
            newWeightPenalty = 4;
        }

        final int current = getWeightPenalty();
        if (current == newWeightPenalty) {
            return;
        }

        if (newWeightPenalty > 0) {
            super.addSkill(SkillTable.getInstance().getSkillEntry(4270, newWeightPenalty));
        } else {
            super.removeSkill(getKnownSkill(4270));
        }

        sendPacket(new SkillList(this));
        sendEtcStatusUpdate();
        updateStats();
    }

    public int getArmorsExpertisePenalty() {
        return getSkillLevel(6213, 0);
    }

    public int getWeaponsExpertisePenalty() {
        return getSkillLevel(6209, 0);
    }

    public int getExpertisePenalty(final ItemInstance item) {
        if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
            return getWeaponsExpertisePenalty();
        } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) {
            return getArmorsExpertisePenalty();
        }
        return 0;
    }

    public void refreshExpertisePenalty() {
        if (isLogoutStarted()) {
            return;
        }

        // Calculate the current higher Expertise of the L2Player
        final int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
        int i = 0;
        for (i = 0; i < EXPERTISE_LEVELS.length; i++) {
            if (level < EXPERTISE_LEVELS[i + 1]) {
                break;
            }
        }

        boolean skillUpdate = false; // Для того, чтобы лишний раз не посылать пакеты
        // Add the Expertise skill corresponding to its Expertise level
        if (expertiseIndex != i) {
            expertiseIndex = i;
            if (expertiseIndex > 0) {
                addSkill(SkillTable.getInstance().getSkillEntry(239, expertiseIndex), false);
                skillUpdate = true;
            }
        }

        int newWeaponPenalty = 0;
        int newArmorPenalty = 0;
        final ItemInstance[] items = getInventory().getPaperdollItems();
        for (final ItemInstance item : items) {
            if (item != null) {
                final int crystaltype = item.getTemplate().getCrystalType().ordinal();
                if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
                    if (crystaltype > newWeaponPenalty) {
                        newWeaponPenalty = crystaltype;
                    }
                } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) {
                    if (crystaltype > newArmorPenalty) {
                        newArmorPenalty = crystaltype;
                    }
                }
            }
        }

        newWeaponPenalty -= expertiseIndex;
        if (newWeaponPenalty <= 0) {
            newWeaponPenalty = 0;
        } else if (newWeaponPenalty >= 4) {
            newWeaponPenalty = 4;
        }

        newArmorPenalty -= expertiseIndex;
        if (newArmorPenalty <= 0) {
            newArmorPenalty = 0;
        } else if (newArmorPenalty >= 4) {
            newArmorPenalty = 4;
        }

        int weaponExpertise = getWeaponsExpertisePenalty();
        int armorExpertise = getArmorsExpertisePenalty();

        if (weaponExpertise != newWeaponPenalty) {
            weaponExpertise = newWeaponPenalty;
            if (newWeaponPenalty > 0) {
                super.addSkill(SkillTable.getInstance().getSkillEntry(6209, weaponExpertise));
            } else {
                super.removeSkill(getKnownSkill(6209));
            }
            skillUpdate = true;
        }
        if (armorExpertise != newArmorPenalty) {
            armorExpertise = newArmorPenalty;
            if (newArmorPenalty > 0) {
                super.addSkill(SkillTable.getInstance().getSkillEntry(6213, armorExpertise));
            } else {
                super.removeSkill(getKnownSkill(6213));
            }
            skillUpdate = true;
        }

        if (skillUpdate) {
            getInventory().validateItemsSkills();

            sendPacket(new SkillList(this));
            sendEtcStatusUpdate();
            updateStats();
        }
    }

    public int getPvpKills() {
        return _pvpKills;
    }

    public void setPvpKills(final int pvpKills) {
        _pvpKills = pvpKills;
    }

    public void addClanPointsOnProfession(final int id) {
        if (getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.VALUES[id].isOfLevel(ClassLevel.Second)) {
            _clan.incReputation(100, true, "Academy");
        } else if (getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.VALUES[id].isOfLevel(ClassLevel.Third)) {
            int earnedPoints = 0;
            if (getLvlJoinedAcademy() <= 16) {
                earnedPoints = 650;
            } else if (getLvlJoinedAcademy() >= 39) {
                earnedPoints = 190;
            } else {
                earnedPoints = 650 - (getLvlJoinedAcademy() - 16) * 20;
            }

            _clan.removeClanMember(getObjectId());

            final SystemMessage sm = new SystemMessage(SystemMsg.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS);
            sm.addString(getName());
            sm.addNumber(_clan.incReputation(earnedPoints, true, "Academy"));
            _clan.broadcastToOnlineMembers(sm);
            _clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(getName()), this);

            setClan(null);
            setTitle("");
            sendPacket(SystemMsg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN);
            setLeaveClanTime(0);

            broadcastCharInfo();

            sendPacket(PledgeShowMemberListDeleteAll.STATIC);

            ItemFunctions.addItem(this, 8181, 1, true);
        }
    }

    public long getExp() {
        return getPlayerClassComponent().getActiveClass() == null ? 0 : getPlayerClassComponent().getActiveClass().getExp();
    }

    public long getMaxExp() {
        return getPlayerClassComponent().getActiveClass() == null ? ExpDataHolder.getInstance().getExpForLevel(PlayerUtils.getMaxLevel() + 1) : getPlayerClassComponent().getActiveClass().getMaxExp();
    }

    public ItemInstance getEnchantScroll() {
        return _enchantScroll;
    }

    public void setEnchantScroll(final ItemInstance scroll) {
        _enchantScroll = scroll;
    }

    public WeaponTemplate getFistsWeaponItem() {
        return fistsWeaponItem;
    }

    public void setFistsWeaponItem(final WeaponTemplate weaponItem) {
        fistsWeaponItem = weaponItem;
    }

    public WeaponTemplate findFistsWeaponItem(final int classId) {
        //human fighter fists
        if (classId >= 0x00 && classId <= 0x09) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(246);
        }

        //human mage fists
        if (classId >= 0x0a && classId <= 0x11) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(251);
        }

        //elven fighter fists
        if (classId >= 0x12 && classId <= 0x18) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(244);
        }

        //elven mage fists
        if (classId >= 0x19 && classId <= 0x1e) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(249);
        }

        //dark elven fighter fists
        if (classId >= 0x1f && classId <= 0x25) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(245);
        }

        //dark elven mage fists
        if (classId >= 0x26 && classId <= 0x2b) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(250);
        }

        //orc fighter fists
        if (classId >= 0x2c && classId <= 0x30) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(248);
        }

        //orc mage fists
        if (classId >= 0x31 && classId <= 0x34) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(252);
        }

        //dwarven fists
        if (classId >= 0x35 && classId <= 0x39) {
            return (WeaponTemplate) ItemTemplateHolder.getInstance().getTemplate(247);
        }

        return null;
    }

    public void addExpAndCheckBonus(final MonsterInstance mob, final double noRateExp, final double noRateSp, final double partyVitalityMod) {
        if (getPlayerClassComponent().getActiveClass() == null) {
            return;
        }
        // Начисление душ камаэлям
        final double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0, mob, null);
        if (neededExp > 0 && noRateExp > neededExp) {
            mob.broadcastPacket(new ExSpawnEmitter(mob, this));
            ThreadPoolManager.getInstance().schedule(new SoulConsumeTask(this), 1000);
        }
        double vitalityBonus = 0.;
        final int npcLevel = mob.getLevel();
        if (AllSettingsConfig.ALT_VITALITY_ENABLED) {
            // Вроде по хв5 Если что выебать -> (c)Mangol
            final boolean blessActive = getNevitComponent().isBlessingActive();
            if (getEffectList().getEffectByType(EffectType.p_recharge_vital_point_noncount) != null) {
                vitalityBonus = mob.isRaid() ? 0. : 2.;
            } else {
                vitalityBonus = mob.isRaid() ? 0. : getVitalityComponent().getVitalityLevel(blessActive) / 2.;
            }
            vitalityBonus *= AllSettingsConfig.ALT_VITALITY_RATE;
            if (noRateExp > 0) {
                if (!mob.isRaid()) {
                    if (!(playerVariables.getBoolean(PlayerVariables.NO_EXP) && getExp() == ExpDataHolder.getInstance().getExpForLevel(getLevel() + 1) - 1)) {
                        double points = ((((noRateExp / (npcLevel * npcLevel)) * 100) / 9) * AllSettingsConfig.ALT_VITALITY_CONSUME_RATE) * partyVitalityMod;
                        if (getLevel() >= 79) {
                            points *= 2.0;
                        } else if (getLevel() >= 77) {
                            points *= 1.5;
                        }
                        if (blessActive) {
                            getVitalityComponent().setVitality(getVitalityComponent().getVitality() + points);
                            sendPacket(SystemMsg.YOU_HAVE_GAINED_VITALITY_POINTS);
                        } else if (getEffectList().getEffectByType(EffectType.p_recovery_vp) != null) {
                            final Effect_p_recovery_vp p_recovery_vp = (Effect_p_recovery_vp) getEffectList().getEffectByType(EffectType.p_recovery_vp);
                            if (p_recovery_vp.isRecovery()) {
                                getVitalityComponent().setVitality(getVitalityComponent().getVitality() + points);
                                sendPacket(SystemMsg.YOU_HAVE_GAINED_VITALITY_POINTS);
                            }
                        } else {
                            getVitalityComponent().setVitality(getVitalityComponent().getVitality() - points);
                        }
                    }
                } else {
                    getVitalityComponent().setVitality(getVitalityComponent().getVitality() + AllSettingsConfig.ALT_VITALITY_RAID_BONUS);
                }
            }
        }
        // При первом вызове, активируем таймеры бонусов.
        if (!isInPeaceZone()) {
            getRecommendationComponent().setRecomTimerActive(true);
            getNevitComponent().startAdventTask();
            if ((getLevel() - npcLevel) <= 9) {
                final int nevitPoints = (int) Math.round(((noRateExp / (npcLevel * npcLevel)) * 100) / 20); // TODO:
                // Формула
                // от
                // балды.
                getNevitComponent().addPoints(nevitPoints);
            }
        }
        double rateXP = ServerConfig.RATE_XP;
        double rateSP = ServerConfig.RATE_SP;
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            rateXP = Math.max(1.0, ServerConfig.RATE_XP / CustomConfig.cutRateXP);
            rateSP = Math.max(1.0, ServerConfig.RATE_SP / CustomConfig.cutRateSP);
        }
        final long normalExp = (long) (noRateExp * (rateXP * getRateExp() + vitalityBonus) * getRecommendationComponent().getRecomBonusMul());
        final long normalSp = (long) (noRateSp * (rateSP * getRateSp() + vitalityBonus));
        final long expWithoutBonus = (long) (noRateExp * rateXP * getRateExp());
        final long spWithoutBonus = (long) (noRateSp * rateSP * getRateSp());
        addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true);
    }

    @Override
    public void addExpAndSp(final long exp, final long sp) {
        addExpAndSp(exp, sp, 0, 0, false, false);
    }

    public void addExpAndSp(long addToExp, long addToSp, final long bonusAddExp, final long bonusAddSp, final boolean applyRate, final boolean applyToPet) {
        if (getPlayerClassComponent().getActiveClass() == null) {
            return;
        }

        if (applyRate) {
            double rateXP = ServerConfig.RATE_XP;
            double rateSP = ServerConfig.RATE_SP;
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                rateXP = Math.max(1.0, ServerConfig.RATE_XP / CustomConfig.cutRateXP);
                rateSP = Math.max(1.0, ServerConfig.RATE_SP / CustomConfig.cutRateSP);
            }
            addToExp *= rateXP * getRateExp();
            addToSp *= rateSP * getRateSp();
        }

        final Servitor pet = getServitor();
        if (addToExp > 0) {
            if (applyToPet) {
                if (pet != null && pet.isInRangeZ(this, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) && !pet.isDead()) {
                    if (pet.isPet()) {
                        final PetInstance petInstance = (PetInstance) pet;
                        if (pet.getExpPenalty() > 0f) {
                            if (pet.getLevel() > getLevel() - 20 && pet.getLevel() < getLevel() + 5) {
                                pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
                                addToExp *= 1. - pet.getExpPenalty();
                            } else {
                                pet.addExpAndSp((long) (addToExp * pet.getExpPenalty() / 5.), 0);
                                addToExp *= 1. - pet.getExpPenalty() / 5.;
                            }
                        } else if (petInstance.getData().isSyncLevel()) {
                            pet.addExpAndSp(pet.getExpForThisLevel(), 0);
                        }
                    } else if (pet.isSummon()) {
                        addToExp *= 1. - pet.getExpPenalty();
                    }
                }
            }

            // Remove Karma when the player kills L2MonsterInstance
            //TODO [G1ta0] двинуть в метод начисления наград при убйистве моба
            if (!isCursedWeaponEquipped() && addToSp > 0 && karma > 0) {
                // karma -= addToSp / (Config.KARMA_SP_DIVIDER *
                // Config.RATE_SP);
                final int karmaLost = Formulas.calculateKarmaLost(this, addToExp);
                if (karmaLost > 0) {
                    karma -= karmaLost;
                }
            }

            if (karma < 0) {
                karma = 0;
            }

            final long max_xp = playerVariables.getBoolean(PlayerVariables.NO_EXP) ? ExpDataHolder.getInstance().getExpForLevel(getLevel() + 1) - 1 : getMaxExp();
            addToExp = Math.min(addToExp, max_xp - getExp());
        }

        final int oldLvl = getPlayerClassComponent().getActiveClass().getLevel();

        getPlayerClassComponent().getActiveClass().addExp(addToExp);
        getPlayerClassComponent().getActiveClass().addSp(addToSp);

        if (addToExp > 0 && addToSp > 0 && (bonusAddExp > 0 || bonusAddSp > 0)) {
            sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addNumber(addToExp).addNumber(bonusAddExp).addNumber(addToSp).addNumber((int) bonusAddSp));
        } else if (addToSp > 0 && addToExp == 0) {
            sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
        } else if (addToSp > 0 && addToExp > 0) {
            sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
        } else if (addToSp == 0 && addToExp > 0) {
            sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));
        }

        final int level = getPlayerClassComponent().getActiveClass().getLevel();
        if (level != oldLvl) {
            final int levels = level - oldLvl;
            if (levels > 0) {
                getNevitComponent().addPoints(1950);
            }
            getListeners().onLevelUp(level);
            levelSet(levels);
        }
        updateStats();
    }

    /**
     * Give Expertise skill of this level.<BR><BR>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the Level of the L2Player </li>
     * <li>Add the Expertise skill corresponding to its Expertise level</li>
     * <li>Update the overloaded status of the L2Player</li><BR><BR>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T give other free skills (SP needed = 0)</B></FONT><BR><BR>
     *
     * @param send
     */
    public void rewardSkills(final boolean send) {
        boolean update = false;
        if (AllSettingsConfig.AUTO_LEARN_SKILLS) {
            for (SkillLearn skillLearn : SkillAcquireHolder.getInstance().getNormalSkillsClassId(this).values()) {
                final SkillEntry sk = SkillTable.getInstance().getSkillEntry(skillLearn.getId(), skillLearn.getLevel());
                if (sk == null || !sk.getTemplate().getCanLearn(getPlayerClassComponent().getClassId()) || (!AllSettingsConfig.AUTO_LEARN_FORGOTTEN_SKILLS && skillLearn.isClicked())) {
                    continue;
                }
                if (getSkillLevel(sk.getId()) < sk.getLevel())
                    addSkill(sk, true);
            }
            update = true;
/*
			int unLearnable = 0;
			Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
			while(skills.size() > unLearnable) {
				unLearnable = 0;
				for(final SkillLearn s : skills) {
					final SkillEntry sk = SkillTable.getInstance().getSkillEntry(s.getId(), s.getLevel());
					if(sk == null || !sk.getTemplate().getCanLearn(getPlayerClassComponent().getClassId()) || (!AllSettingsConfig.AUTO_LEARN_FORGOTTEN_SKILLS && s.isClicked())) {
						unLearnable++;
						continue;
					}
					addSkill(sk, true);
				}
				skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
			}
			update = true;*/
        } else
        // Скиллы дающиеся бесплатно не требуют изучения
        {
            for (final SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)) {
                if (skill.getCost() == 0 && skill.getItemId() == 0) {
                    final SkillEntry sk = SkillTable.getInstance().getSkillEntry(skill.getId(), skill.getLevel());
                    addSkill(sk, true);
                    if (!getShortCutComponent().getAllShortCuts().isEmpty() && sk.getLevel() > 1) {
                        getShortCutComponent().getAllShortCuts().stream().filter(sc -> sc.getId() == sk.getId() && sc.getType() == ShortCut.TYPE_SKILL).forEach(sc -> {
                            final ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), sk.getLevel(), 1);
                            sendPacket(new ShortCutRegister(this, newsc));
                            getShortCutComponent().registerShortCut(newsc);
                        });
                    }
                    update = true;
                }
            }
        }
        if (isTransformed()) {
            update = true;
            //Генерируем новые скилы трансформации и выдаем их.
            getTransformation().addGiveAllSkills(getLevel());
        }
        if (send && update) {
            sendPacket(new SkillList(this));
        }

        updateStats();
    }

    public int getIntSp() {
        return (int) getSp();
    }

    public long getSp() {
        return getPlayerClassComponent().getActiveClass() == null ? 0 : getPlayerClassComponent().getActiveClass().getSp();
    }

    public void setSp(final long sp) {
        if (getPlayerClassComponent().getActiveClass() != null) {
            getPlayerClassComponent().getActiveClass().setSp(sp);
        }
    }

    public int getClanId() {
        return _clan == null ? 0 : _clan.getClanId();
    }

    public long getLeaveClanTime() {
        return _leaveClanTime;
    }

    public void setLeaveClanTime(final long time) {
        _leaveClanTime = time;
    }

    public long getDeleteClanTime() {
        return _deleteClanTime;
    }

    public void setDeleteClanTime(final long time) {
        _deleteClanTime = time;
    }

    public long getNoChannel() {
        return _NoChannel;
    }

    public void setNoChannel(final long time) {
        _NoChannel = time;
        if (_NoChannel > 2145909600000L || _NoChannel < 0) {
            _NoChannel = -1;
        }

        if (_NoChannel > 0) {
            _NoChannelBegin = System.currentTimeMillis();
        } else {
            _NoChannelBegin = 0;
        }
    }

    public long getNoChannelRemained() {
        if (_NoChannel == 0) {
            return 0;
        } else if (_NoChannel < 0) {
            return -1;
        } else {
            final long remained = _NoChannel - System.currentTimeMillis() + _NoChannelBegin;
            if (remained < 0) {
                return 0;
            }

            return remained;
        }
    }

    public void setLeaveClanCurTime() {
        _leaveClanTime = System.currentTimeMillis();
    }

    public void setDeleteClanCurTime() {
        _deleteClanTime = System.currentTimeMillis();
    }

    public boolean canJoinClan() {
        if (_leaveClanTime == 0) {
            return true;
        }
        if (System.currentTimeMillis() - _leaveClanTime >= Clan.EXPELLED_MEMBER_PENALTY) {
            _leaveClanTime = 0;
            return true;
        }
        return false;
    }

    public boolean canCreateClan() {
        if (_deleteClanTime == 0) {
            return true;
        }
        if (System.currentTimeMillis() - _deleteClanTime >= Clan.CREATE_PLEDGE_PENALTY) {
            _deleteClanTime = 0;
            return true;
        }
        return false;
    }

    public IBroadcastPacket canJoinParty(final Player inviter, final Player target) {
        final Request request = getRequest();
        if (request != null && request.isInProgress() && request.getOtherPlayer(this) != inviter) {
            return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter); // занят
        }
        if (isBlockAll() || getMessageRefusal()) // всех нафиг
        {
            return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
        }
        if (isInParty()) // уже
        {
            return new SystemMessage(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
        }
        if (inviter.getReflection() != getReflection()) // в разных инстантах
        {
            if (inviter.getReflection() != ReflectionManager.DEFAULT && getReflection() != ReflectionManager.DEFAULT) {
                return SystemMsg.INVALID_TARGET.packet(inviter);
            }
        }
        if (isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped()) // зарич
        {
            return SystemMsg.INVALID_TARGET.packet(inviter);
        }
        if (inviter.isInOlympiadMode() || isInOlympiadMode()) // олимпиада
        {
            return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
        }
        if (inviter.getTeam() != target.getTeam()) // в разных командах
        {
            return SystemMsg.INVALID_TARGET.packet(inviter);
        }
        return null;
    }

    @Override
    public PcInventory getInventory() {
        return _inventory;
    }

    @Override
    public long getWearedMask() {
        return _inventory.getWearedMask();
    }

    public PcFreight getFreight() {
        return _freight;
    }

    public boolean isSitting() {
        return _isSitting;
    }

    public void setSitting(final boolean val) {
        _isSitting = val;
    }

    public boolean getSittingTask() {
        return sittingTaskLaunched;
    }

    @Override
    public void sitDown(final StaticObjectInstance throne) {
        if (isSitting() || sittingTaskLaunched || isAlikeDead()) {
            return;
        }

        if (isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isMoving) {
            getAI().setNextAction(NextAction.REST, null, null, false, false);
            return;
        }

        resetWaitSitTime();
        getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

        if (throne == null) {
            broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
        } else {
            broadcastPacket(new ChairSit(this, throne));
        }

        _sittingObject = throne;
        setSitting(true);
        sittingTaskLaunched = true;
        ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500);
    }

    @Override
    public void standUp() {
        if (!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead()) {
            return;
        }

        // Chameleon Rest
        getEffectList().stopEffect(296);

        getAI().clearNextAction();
        broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));

        _sittingObject = null;
        sittingTaskLaunched = true;
        ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500);
    }

    public void updateWaitSitTime() {
        if (_waitTimeWhenSit < 200) {
            _waitTimeWhenSit += 2;
        }
    }

    public int getWaitSitTime() {
        return _waitTimeWhenSit;
    }

    public void resetWaitSitTime() {
        _waitTimeWhenSit = 0;
    }

    public Warehouse getWarehouse() {
        return _warehouse;
    }

    public ItemContainer getRefund() {
        return _refund;
    }

    public long getAdena() {
        return getInventory().getAdena();
    }

    public boolean reduceAdena(final long adena) {
        return reduceAdena(adena, false);
    }

    /**
     * Забирает адену у игрока.<BR><BR>
     *
     * @param adena  - сколько адены забрать
     * @param notify - отображать системное сообщение
     * @return true если сняли
     */
    public boolean reduceAdena(final long adena, final boolean notify) {
        if (adena < 0) {
            return false;
        }
        if (adena == 0) {
            return true;
        }
        final boolean result = getInventory().reduceAdena(adena);
        if (notify && result) {
            sendPacket(SystemMessage.removeItems(ItemTemplate.ITEM_ID_ADENA, adena));
        }
        return result;
    }

    public ItemInstance addAdena(final long adena) {
        return addAdena(adena, false);
    }

    /**
     * Добавляет адену игроку.<BR><BR>
     *
     * @param adena  - сколько адены дать
     * @param notify - отображать системное сообщение
     * @return L2ItemInstance - новое количество адены
     */
    public ItemInstance addAdena(final long adena, final boolean notify) {
        if (adena < 1) {
            return null;
        }
        final ItemInstance item = getInventory().addAdena(adena);
        if (item != null && notify) {
            sendPacket(SystemMessage.obtainItems(ItemTemplate.ITEM_ID_ADENA, adena, 0));
        }
        return item;
    }

    public GameClient getNetConnection() {
        return _connection;
    }

    public void setNetConnection(final GameClient connection) {
        _connection = connection;
    }

    public int getRevision() {
        return _connection == null ? 0 : _connection.getRevision();
    }

    public boolean isConnected() {
        return _connection != null && _connection.isConnected();
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (isFrozen()) {
            player.sendPacket(ActionFail.STATIC);
            return;
        }

        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, Player.class, this, true)) {
            player.sendPacket(ActionFail.STATIC);
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(getObjectId(), 0)); // The color to display in the select window is White
            } else {
                player.sendPacket(ActionFail.STATIC);
            }
        } else if (getPrivateStoreType() != STORE_PRIVATE_NONE) {
            if (player.getObjectId() == getObjectId()) {
                sendActionFailed();
                return;
            }
            if (getDistance(player) > getInteractDistance(player) && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                if (!shift) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
                } else {
                    player.sendPacket(ActionFail.STATIC);
                }
            } else {
                player.doInteract(this);
            }
        } else if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
        } else if (player != this) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
                if (!shift && !isDead()) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, AllSettingsConfig.FOLLOW_RANGE);
                } else {
                    player.sendPacket(ActionFail.STATIC);
                }
            } else {
                player.sendPacket(ActionFail.STATIC);
            }
        } else {
            player.sendPacket(ActionFail.STATIC);
        }
    }

    @Override
    public void broadcastStatusUpdate() {
        if (!needStatusUpdate()) //По идее еше должно срезать траффик. Будут глюки с отображением - убрать это условие.
        {
            return;
        }

        final StatusUpdate su = makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.MAX_MP, StatusUpdate.MAX_CP, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP);
        sendPacket(su);

        // Check if a party is in progress
        if (isInParty())
        // Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2Player of the Party
        {
            getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdate(this));
        }

        final SingleMatchEvent duelEvent = getEvent(SingleMatchEvent.class);
        if (duelEvent != null) {
            duelEvent.onStatusUpdate(this);
        }

        if (isInOlympiadMode() && isOlympiadCompStart()) {
            if (_olympiadGame != null) {
                _olympiadGame.broadcastInfo(this, null, false);
            }
        }
    }

    @Override
    public void broadcastCharInfo() {
        broadcastUserInfo(false);
    }

    /**
     * Отправляет UserInfo даному игроку и CharInfo всем окружающим.<BR><BR>
     * <p/>
     * <B><U> Концепт</U> :</B><BR><BR>
     * Сервер шлет игроку UserInfo.
     * Сервер вызывает метод	для рассылки CharInfo<BR><BR>
     * <p/>
     * <B><U> Действия</U> :</B><BR><BR>
     * <li>Отсылка игроку UserInfo(личные и общие данные)</li>
     * <li>Отсылка другим игрокам CharInfo(Public data only)</li><BR><BR>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Внимание</U> : НЕ ПОСЫЛАЙТЕ UserInfo другим игрокам либо CharInfo даному игроку.<BR>
     * НЕ ВЫЗЫВАЕЙТЕ ЭТОТ МЕТОД КРОМЕ ОСОБЫХ ОБСТОЯТЕЛЬСТВ(смена сабкласса к примеру)!!! Траффик дико кушается у игроков и начинаются лаги.<br>
     * Используйте метод {@link Player#sendChanges()}</B></FONT><BR><BR>
     */
    public void broadcastUserInfo(boolean force) {
        sendUserInfo(force);

        if (!isVisible() || isInvisible()) {
            return;
        }

        if (ServerConfig.BROADCAST_CHAR_INFO_INTERVAL == 0) {
            force = true;
        }

        if (force) {
            if (_broadcastCharInfoTask != null) {
                _broadcastCharInfoTask.cancel(false);
                _broadcastCharInfoTask = null;
            }
            broadcastCharInfoImpl();
            return;
        }

        if (_broadcastCharInfoTask != null) {
            return;
        }

        _broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), ServerConfig.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public boolean isPolymorphed() {
        return _polyNpcId != 0;
    }

    public int getPolyId() {
        return _polyNpcId;
    }

    public void setPolyId(final int polyid) {
        _polyNpcId = polyid;

        teleToLocation(getLoc());
        broadcastUserInfo(true);
    }

    private void broadcastCharInfoImpl() {
        if (!isVisible() || isInvisible()) {
            return;
        }

        final L2GameServerPacket ci = isPolymorphed() ? new NpcInfo(this) : new CharInfo(this);
        final L2GameServerPacket exCi = new ExBR_ExtraUserInfo(this);
        L2GameServerPacket dominion = null;
        final DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        // пакетка розсыдается в том случаии, если идет ТВ, или ТВ неначалось а игрок изменил имья(Защитик Замка)
        if (siegeEvent != null && (siegeEvent.isInProgress() || siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(getObjectId()))) {
            dominion = new ExDominionWarStart(this);
        }

        for (final Player player : World.getAroundObservers(this)) {
            player.sendPacket(ci, exCi);
            player.sendPacket(RelationChanged.update(player, this, player));
            if (dominion != null) {
                player.sendPacket(dominion);
            }
        }
    }

    public void sendEtcStatusUpdate() {
        if (!isVisible()) {
            return;
        }

        sendPacket(new EtcStatusUpdate(this));
    }

    private void sendUserInfoImpl() {
        sendPacket(new UserInfo(this), new ExBR_ExtraUserInfo(this));
        final DominionSiegeEvent dominionSiegeEvent = getEvent(DominionSiegeEvent.class);
        if (dominionSiegeEvent != null && (dominionSiegeEvent.isInProgress() || dominionSiegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(getObjectId()))) {
            sendPacket(new ExDominionWarStart(this));
        }
    }

    public void sendUserInfo() {
        sendUserInfo(false);
    }

    public void sendUserInfo(final boolean force) {
        if (!isVisible() || entering || isLogoutStarted()) {
            return;
        }

        if (ServerConfig.USER_INFO_INTERVAL == 0 || force) {
            if (_userInfoTask != null) {
                _userInfoTask.cancel(false);
                _userInfoTask = null;
            }
            sendUserInfoImpl();
            return;
        }

        if (_userInfoTask != null) {
            return;
        }

        _userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(), ServerConfig.USER_INFO_INTERVAL);
    }

    @Override
    public StatusUpdate makeStatusUpdate(final int... fields) {
        final StatusUpdate su = new StatusUpdate(getObjectId());
        for (final int field : fields) {
            switch (field) {
                case StatusUpdate.CUR_HP:
                    su.addAttribute(field, (int) getCurrentHp());
                    break;
                case StatusUpdate.MAX_HP:
                    su.addAttribute(field, (int) getMaxHp());
                    break;
                case StatusUpdate.CUR_MP:
                    su.addAttribute(field, (int) getCurrentMp());
                    break;
                case StatusUpdate.MAX_MP:
                    su.addAttribute(field, getMaxMp());
                    break;
                case StatusUpdate.CUR_LOAD:
                    su.addAttribute(field, getCurrentLoad());
                    break;
                case StatusUpdate.MAX_LOAD:
                    su.addAttribute(field, getMaxLoad());
                    break;
                case StatusUpdate.PVP_FLAG:
                    su.addAttribute(field, _pvpFlag);
                    break;
                case StatusUpdate.KARMA:
                    su.addAttribute(field, getKarma());
                    break;
                case StatusUpdate.CUR_CP:
                    su.addAttribute(field, (int) getCurrentCp());
                    break;
                case StatusUpdate.MAX_CP:
                    su.addAttribute(field, getMaxCp());
                    break;
            }
        }
        return su;
    }

    public void sendStatusUpdate(final boolean broadCast, final boolean withPet, final int... fields) {
        if (fields.length == 0 || entering && !broadCast) {
            return;
        }

        final StatusUpdate su = makeStatusUpdate(fields);
        if (!su.hasAttributes()) {
            return;
        }

        final List<IBroadcastPacket> packets = new ArrayList<>(withPet ? 2 : 1);
        if (withPet && getServitor() != null) {
            packets.add(getServitor().makeStatusUpdate(fields));
        }

        packets.add(su);

        if (!broadCast) {
            sendPacket(packets);
        } else if (entering) {
            broadcastPacketToOthers(packets);
        } else {
            broadcastPacket(packets);
        }
    }

    /**
     * @return the Alliance Identifier of the L2Player.<BR><BR>
     */
    public int getAllyId() {
        return _clan == null ? 0 : _clan.getAllyId();
    }

    @Override
    public void sendPacket(final IBroadcastPacket p) {
        if (!isConnected()) {
            return;
        }

        final L2GameServerPacket temp = p.packet(this);
        if (temp == null) {
            return;
        }

        _connection.sendPacket(temp);
    }

    @Override
    public void sendPacket(final IBroadcastPacket... packets) {
        if (!isConnected()) {
            return;
        }

        for (final IBroadcastPacket p : packets) {
            if (p == null) {
                continue;
            }
            final L2GameServerPacket temp = p.packet(this);
            if (temp == null) {
                continue;
            }

            _connection.sendPacket(temp);
        }
    }

    @Override
    public void sendPacket(final List<? extends IBroadcastPacket> packets) {
        if (!isConnected()) {
            return;
        }

        for (final IBroadcastPacket p : packets) {
            sendPacket(p);
        }
    }

    public void doInteract(final GameObject target) {
        if (target == null || isActionsDisabled()) {
            sendActionFailed();
            return;
        }
        if (target.isPlayer()) {
            if (target.getDistance(this) <= getInteractDistance(target)) {
                final Player temp = (Player) target;

                if (temp.getPrivateStoreType() == STORE_PRIVATE_SELL || temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE) {
                    sendPacket(new PrivateStoreListSell(this, temp));
                    sendActionFailed();
                } else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUY) {
                    sendPacket(new PrivateStoreListBuy(this, temp));
                    sendActionFailed();
                } else if (temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE) {
                    sendPacket(new RecipeShopSellList(this, temp));
                    sendActionFailed();
                }
                sendActionFailed();
            } else if (getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
            }
        } else {
            target.onAction(this, false);
        }
    }

    public void doAutoLootOrDrop(final ItemInstance item, final NpcInstance fromNpc) {
        final boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

        if ((fromNpc.isRaid() || fromNpc instanceof ReflectionBossInstance) && !AllSettingsConfig.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot) {
            item.dropToTheGround(this, fromNpc);
            return;
        }

        // Herbs
        if (item.isHerb()) {
            if (!_autoLootHerbs && !forceAutoloot) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
            final SkillEntry[] skills = item.getTemplate().getAttachedSkills();
            if (skills.length > 0) {
                for (final SkillEntry skill : skills) {
                    altUseSkill(skill, this);
                    if (getServitor() != null && getServitor().isSummon() && !getServitor().isDead()) {
                        getServitor().altUseSkill(skill, getServitor());
                    }
                }
            }
            item.deleteMe();
            return;
        }

        if (autoLoot == AUTO_LOOT_NONE && !forceAutoloot) {
            item.dropToTheGround(this, fromNpc);
            return;
        }
        // Check if the L2Player is in a Party
        if (!isInParty()) {
            if (!pickupItem(item, Log.Pickup)) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
        } else {
            getParty().distributeItem(this, item, fromNpc);
        }

        broadcastPickUpMsg(item);
    }

    @Override
    public void doPickupItem(final GameObject object) {
        // Check if the L2Object to pick up is a L2ItemInstance
        if (!object.isItem()) {
            _log.warn("trying to pickup wrong target." + getTarget());
            return;
        }

        sendActionFailed();
        stopMove();

        final ItemInstance item = (ItemInstance) object;

        synchronized (item) {
            if (!item.isVisible()) {
                return;
            }

            // Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
            if (!ItemFunctions.checkIfCanPickup(this, item)) {
                final SystemMessage sm;
                if (item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    sm = new SystemMessage(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    sm.addNumber(item.getCount());
                } else {
                    sm = new SystemMessage(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    sm.addItemName(item.getItemId());
                }
                sendPacket(sm);
                return;
            }

            // Herbs
            if (item.isHerb()) {
                final SkillEntry[] skills = item.getTemplate().getAttachedSkills();
                if (skills.length > 0) {
                    for (final SkillEntry skill : skills) {
                        altUseSkill(skill, this);
                        if (getServitor() != null && getServitor().isSummon() && !getServitor().isDead()) {
                            getServitor().altUseSkill(skill, getServitor());
                        }
                    }
                }

                broadcastPacket(new GetItem(item, getObjectId()));
                item.deleteMe();
                return;
            }

            final FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

            if (!isInParty() || attachment != null) {
                if (pickupItem(item, Log.Pickup)) {
                    broadcastPacket(new GetItem(item, getObjectId()));
                    broadcastPickUpMsg(item);
                    item.pickupMe();
                }
            } else {
                getParty().distributeItem(this, item, null);
            }
        }
    }

    public boolean pickupItem(final ItemInstance item, final String log) {
        final PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

        if (!ItemFunctions.canAddItem(this, item)) {
            return false;
        }

        Log.items(this, log, item);
        sendPacket(SystemMessage.obtainItems(item));
        getInventory().addItem(item);
        getListeners().onItemPickup(item);

        if (attachment != null) {
            attachment.pickUp(this);
        }

        sendChanges();
        return true;
    }

    public void setObjectTarget(final GameObject target) {
        setTarget(target);
        if (target == null) {
            return;
        }

        if (target == getTarget()) {
            if (target.isNpc()) {
                final NpcInstance npc = (NpcInstance) target;
                sendPacket(new MyTargetSelected(npc.getObjectId(), getLevel() - npc.getLevel()));
                sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
                sendPacket(new ValidateLocation(npc), ActionFail.STATIC);
            } else {
                sendPacket(new MyTargetSelected(target.getObjectId(), 0));
            }
        }
    }

    @Override
    public void setTarget(GameObject newTarget) {
        // Check if the new target is visible
        if (newTarget != null && !newTarget.isVisible()) {
            newTarget = null;
        }

        // Can't target and attack festival monsters if not participant
        if (newTarget instanceof FestivalMonsterInstance && !isFestivalParticipant()) {
            newTarget = null;
        }

        final Party party = getParty();

        // Can't target and attack rift invaders if not in the same room
        if (party != null && party.isInDimensionalRift()) {
            final int riftType = party.getDimensionalRift().getType();
            final int riftRoom = party.getDimensionalRift().getCurrentRoom();
            if (newTarget != null && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ())) {
                newTarget = null;
            }
        }

        final GameObject oldTarget = getTarget();

        if (oldTarget != null) {
            if (oldTarget == newTarget) {
                return;
            }

            // Remove the L2Player from the _statusListener of the old target if it was a L2Character
            if (oldTarget.isCreature()) {
                ((Creature) oldTarget).removeStatusListener(this);
            }

            broadcastPacket(new TargetUnselected(this));
        }

        if (newTarget != null) {
            // Add the L2Player to the _statusListener of the new target if it's a L2Character
            if (newTarget.isCreature()) {
                ((Creature) newTarget).addStatusListener(this);
            }

            broadcastPacket(new TargetSelected(getObjectId(), newTarget.getObjectId(), getLoc()));
        }

        super.setTarget(newTarget);
    }

    /**
     * @return the active weapon instance (always equipped in the right hand).<BR><BR>
     */
    @Override
    public ItemInstance getActiveWeaponInstance() {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
    }

    /**
     * @return the active weapon item (always equipped in the right hand).<BR><BR>
     */
    @Override
    public WeaponTemplate getActiveWeaponItem() {
        final ItemInstance weapon = getActiveWeaponInstance();

        if (weapon == null) {
            return getFistsWeaponItem();
        }

        return (WeaponTemplate) weapon.getTemplate();
    }

    /**
     * @return the secondary weapon instance (always equipped in the left hand).<BR><BR>
     */
    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
    }

    /**
     * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR><BR>
     */
    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        final ItemInstance weapon = getSecondaryWeaponInstance();

        if (weapon == null) {
            return getFistsWeaponItem();
        }

        final ItemTemplate item = weapon.getTemplate();

        if (item instanceof WeaponTemplate) {
            return (WeaponTemplate) item;
        }

        return null;
    }

    public boolean isWearingArmor(final ArmorType armorType) {
        final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);

        if (chest == null) {
            return armorType == ArmorType.NONE;
        }

        if (chest.getItemType() != armorType) {
            return false;
        }

        if (chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR) {
            return true;
        }

        final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);

        return legs == null ? armorType == ArmorType.NONE : legs.getItemType() == armorType;
    }

    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake,
                                final boolean standUp, final boolean directHp, final boolean canReflect, final boolean transferDamage,
                                final boolean isDot, final boolean sendMessage) {
        reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, false);
    }

    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake,
                                final boolean standUp, final boolean directHp, final boolean canReflect, final boolean transferDamage,
                                final boolean isDot, final boolean sendMessage, final boolean lethal) {
        if (attacker == null || isDead() || (attacker.isDead() && !isDot)) {
            return;
        }

        // 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
        if (attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10) {
            // ПК не может нанести урон чару с блессингом
            if (attacker.getKarma() > 0 && getEffectList().getEffectsBySkillId(5182) != null && !isInZone(ZoneType.SIEGE)) {
                return;
            }
            // чар с блессингом не может нанести урон ПК
            if (getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.SIEGE)) {
                return;
            }
        }

        // Reduce the current HP of the L2Player
        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    public double reduceCurrentCp(double damage) {
        if (getCurrentCp() > 0) {
            double cp = getCurrentCp();
            if (cp >= damage) {
                cp -= damage;
                damage = 0;
            } else {
                damage -= cp;
                cp = 0;
            }

            setCurrentCp(cp);
        }
        return damage;
    }

    @Override
    protected void onReduceCurrentHp(double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp,
                                     final boolean directHp) {
        onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, false);
    }

    @Override
    protected void onReduceCurrentHp(double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp,
                                     final boolean directHp, final boolean lethal) {
        if (standUp) {
            standUp();
            if (isFakeDeath()) {
                breakFakeDeath();
            }
        }

        final double fullDamage = damage;

        if (!directHp && attacker.isPlayable())
            damage = reduceCurrentCp(damage);

        final double hp = getCurrentHp();

        if (isInOlympiadMode()) {
            final OlympiadGame game = _olympiadGame;
            if (this != attacker && (skill == null || skill.getTemplate().isOffensive()) && attacker.isPlayer()) // считаем дамаг от простых ударов и атакующих скиллов
            {
                game.addDamage(attacker.getPlayer(), fullDamage);
            }

            if (hp - damage <= 1) // если хп <= 1 - убит
            {
                if (game.getType().equals(CompType.TEAM)) {
                    super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);

                    if (game.doDie(this)) // Все умерли
                    {
                        game.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                        game.endGame(20, false);
                    }

                    return;
                } else {
                    setCurrentHp(1, false);
                    attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    game.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                    game.endGame(20, false);
                    attacker.sendActionFailed();
                    return;
                }
            }
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }

    @Override
    public void onMagicUseTimer(final Creature aimingTarget, final SkillEntry skillEntry, final boolean forceUse) {
        if (!skillEntry.getTemplate().isHandler() && !skillEntry.getTemplate().isTrigger()) {
            if (isTransformed() && !getAllSkills().contains(skillEntry)) {
                onCastEndTime(skillEntry);
                return;
            }
        }

        super.onMagicUseTimer(aimingTarget, skillEntry, forceUse);
    }

    private void altDeathPenalty(final Creature killer) {
        // Reduce the Experience of the Player in function of the calculated Death Penalty
        if (isInZoneBattle()) {
            return;
        }
        if (getNevitComponent().isBlessingActive()) {
            return;
        }
        deathPenalty(killer);
    }

    public final boolean atWarWith(final Player player) {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId());
    }

    public boolean atMutualWarWith(Player player) {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
    }

    public final void doPurePk(final Player killer) {
        // Check if the attacker has a PK counter greater than 0
        final int pkCountMulti = Math.max(killer.getPkKills() / 2, 1);

        // Calculate the level difference Multiplier between attacker and killed L2Player
        //final int lvlDiffMulti = Math.max(killer.getLevel() / _level, 1);

        // Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
        // Add karma to attacker and increase its PK counter
        killer.increaseKarma(PvpConfig.KARMA_MIN_KARMA * pkCountMulti); // * lvlDiffMulti);
        killer.setPkKills(killer.getPkKills() + 1);
    }

    public final void doKillInPeace(final Player killer) // Check if the L2Player killed haven't Karma
    {
        final SiegeEvent<?, ?> siegeEvent = killer.getEvent(SiegeEvent.class);
        if (siegeEvent != null && !siegeEvent.canPK(this, killer)) {
            return;
        }

        if (karma <= 0) {
            doPurePk(killer);
        } else {
            killer.setPvpKills(killer.getPvpKills() + 1);
        }
    }

    public void checkAddItemToDrop(final List<ItemInstance> array, final List<ItemInstance> items, final int maxCount) {
        for (int i = 0; i < maxCount && !items.isEmpty(); i++) {
            array.add(items.remove(Rnd.get(items.size())));
        }
    }

    public FlagItemAttachment getActiveWeaponFlagAttachment() {
        final ItemInstance item = getActiveWeaponInstance();
        if (item == null || !(item.getAttachment() instanceof FlagItemAttachment)) {
            return null;
        }
        return (FlagItemAttachment) item.getAttachment();
    }

    public boolean canAttackFreely(final Player attacker) {
        for (Event e : attacker.getEvents()) {
            if (e.canAttack(this, attacker, null, false, false)) {
                if (e instanceof CustomInstantTeamEvent) {
                    if (e.isInProgress())
                        return true;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    protected void doPKPVPManage(Creature killer) {
        final FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if (attachment != null) {
            attachment.onDeath(this, killer);
        }


        if (killer == null || killer == _summon || killer == this) {
            return;
        }

        if ((isInZoneBattle() || killer.isInZoneBattle()) && !OtherConfig.warPvp) {
            return;
        }

        if (killer instanceof Servitor && (killer = killer.getPlayer()) == null) {
            return;
        }

        // Processing Karma/PKCount/PvPCount for killer
        if (killer.isPlayer()) {
            final Player pk = (Player) killer;
            final int repValue = getLevel() - pk.getLevel() >= 20 ? 2 : 1;
            final boolean war = atMutualWarWith(pk);

            //TODO [VISTALL] fix it
            if (war /*|| _clan.getSiege() != null && _clan.getSiege() == pk.getClan().getSiege() && (_clan.isDefender() && pk.getClan().isAttacker() || _clan.isAttacker() && pk.getClan().isDefender())*/) {
                if (pk.getClan().getReputationScore() > 0 && _clan.getLevel() >= 5 && _clan.getReputationScore() > 0 && pk.getClan().getLevel() >= 5) {
                    final int decRep = _clan.incReputation(-repValue, true, "ClanWar");
                    _clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.YOUR_CLAN_MEMBER_C1_WAS_KILLED_S2_POINTS_HAVE_BEEN_DEDICTED_FROM_YOUR_CLAN_REPUTATION_SCORE).addString(getName()).addNumber(-decRep), new SystemMessage(SystemMsg.S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLANS_REPUTATION_SCORE).addNumber(decRep));
                    final int incRep = pk.getClan().incReputation(repValue, true, "ClanWar");
                    pk.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMsg.FOR_KILLING_AN_OPPOSING_CLAN_MEMBER_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENTS_CLAN_REPUTATION_SCORE).addNumber(incRep), new SystemMessage(SystemMsg.YOUR_CLAN_HAS_ADDED_S1_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(incRep));
                }
            }
            if (OtherConfig.warPvp && (isInZoneBattle() || isOnSiegeField())) {
                pk.setPvpKills(pk.getPvpKills() + 1);
            } else {
                if (isOnSiegeField()) {
                    return;
                }

                if (_pvpFlag > 0 || war || canAttackFreely(pk)) {
                    pk.setPvpKills(pk.getPvpKills() + 1);
                } else {
                    doKillInPeace(pk);
                }
            }
            pk.sendChanges();
        }

        final int karma = this.karma;
        decreaseKarma(PvpConfig.KARMA_LOST_BASE);

        // в нормальных условиях вещи теряются только при смерти от гварда или игрока
        // кроме того, альт на потерю вещей при сметри позволяет терять вещи при смтери от монстра
        final boolean isPvP = killer.isPlayable() || killer instanceof GuardInstance;

        if (killer.isMonster() && !PvpConfig.DROP_ITEMS_ON_DIE // если убил монстр и альт выключен
                || isPvP // если убил игрок или гвард и
                && (_pkKills < PvpConfig.MIN_PK_TO_ITEMS_DROP // количество пк слишком мало
                || karma == 0 && PvpConfig.KARMA_NEEDED_TO_DROP) // кармы нет
                || isFestivalParticipant() // в фестивале вещи не теряются
                || !killer.isMonster() && !isPvP) // в прочих случаях тоже
        {
            return;
        }

        // No drop from GM's
        if (!PvpConfig.KARMA_DROP_GM && isGM()) {
            return;
        }
        final long timeMillis = getPlayerVariables().getLong(PlayerVariables.NO_DROP_PK);
        if (timeMillis > System.currentTimeMillis()) {
            return;
        }

        final int max_drop_count = isPvP ? PvpConfig.KARMA_DROP_ITEM_LIMIT : 1;

        final double dropRate; // базовый шанс в процентах
        if (isPvP) {
            dropRate = _pkKills * PvpConfig.KARMA_DROPCHANCE_MOD + PvpConfig.KARMA_DROPCHANCE_BASE;
        } else {
            dropRate = PvpConfig.NORMAL_DROPCHANCE_BASE;
        }

        int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

        for (int i = 0; i < Math.ceil(dropRate / 100) && i < max_drop_count; i++) {
            if (Rnd.chance(dropRate)) {
                final int rand = Rnd.get(PvpConfig.DROPCHANCE_EQUIPPED_WEAPON + PvpConfig.DROPCHANCE_EQUIPMENT + PvpConfig.DROPCHANCE_ITEM) + 1;
                if (rand > PvpConfig.DROPCHANCE_EQUIPPED_WEAPON + PvpConfig.DROPCHANCE_EQUIPMENT) {
                    dropItemCount++;
                } else if (rand > PvpConfig.DROPCHANCE_EQUIPPED_WEAPON) {
                    dropEquipCount++;
                } else {
                    dropWeaponCount++;
                }
            }
        }

        final List<ItemInstance> drop = new LazyArrayList<>(); // общий массив с результатами выбора
        // временные
        final List<ItemInstance> dropItem = new LazyArrayList<>(dropItemCount);
        final List<ItemInstance> dropEquip = new LazyArrayList<>(dropEquipCount);
        final List<ItemInstance> dropWeapon = new LazyArrayList<>(dropWeaponCount);

        getInventory().writeLock();
        try {
            for (final ItemInstance item : getInventory().getItems()) {
                if (!item.canBeDropped(this, true) || PvpConfig.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId())) {
                    continue;
                }

                if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
                    dropWeapon.add(item);
                } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) {
                    dropEquip.add(item);
                } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER) {
                    dropItem.add(item);
                }
            }

            checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
            checkAddItemToDrop(drop, dropEquip, dropEquipCount);
            checkAddItemToDrop(drop, dropItem, dropItemCount);

            // Dropping items, if present
            if (drop.isEmpty()) {
                return;
            }

            for (ItemInstance item : drop) {
                if (item.isAugmented() && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) {
                    item.setVariationStoneId(0);
                    item.setVariation1Id(0);
                    item.setVariation2Id(0);
                }

                item = getInventory().removeItem(item);
                Log.items(this, Log.PvPDrop, item);

                if (item.getEnchantLevel() > 0) {
                    sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
                } else {
                    sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
                }

                if (killer.isPlayable() && ((!AllSettingsConfig.AUTO_LOOT && AllSettingsConfig.AUTO_LOOT_PK) || this.isInFlyingTransform())) {
                    killer.getPlayer().getInventory().addItem(item);
                    Log.items(this, Log.Pickup, item);

                    killer.getPlayer().sendPacket(SystemMessage.obtainItems(item));
                } else {
                    item.dropToTheGround(this, Location.findAroundPosition(this, PvpConfig.KARMA_RANDOM_DROP_LOCATION_LIMIT));
                }
            }
        } finally {
            getInventory().writeUnlock();

            drop.clear();
            dropItem.clear();
            dropEquip.clear();
            dropWeapon.clear();
        }
    }

    @Override
    protected void onDeath(final Creature killer) {
        //Check for active charm of luck for death penalty
        getDeathPenalty().checkCharmOfLuck();

        if (isInStoreMode()) {
            setPrivateStoreType(Player.STORE_PRIVATE_NONE);
        }
        if (isProcessingRequest()) {
            final Request request = getRequest();
            if (isInTrade()) {
                final Player partner = request.getOtherPlayer(this);
                sendPacket(SendTradeDone.FAIL);
                partner.sendPacket(SendTradeDone.FAIL);
            }
            request.cancel();
        }
        //cubicdata
        deleteCubics();
        deleteAgathion();

        boolean checkPvp = true;
        if (ServerConfig.ALLOW_CURSED_WEAPONS) {
            if (isCursedWeaponEquipped()) {
                CursedWeaponsManager.getInstance().dropPlayer(this);
                checkPvp = false;
            } else if (killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped()) {
                CursedWeaponsManager.getInstance().increaseKills(((Player) killer).getCursedWeaponEquippedId());
                checkPvp = false;
            }
        }

        if (checkPvp) {
            doPKPVPManage(killer);

            altDeathPenalty(killer);
        }

        //And in the end of process notify death penalty that owner died :)
        getDeathPenalty().notifyDead(killer);

        setIncreasedForce(0);

        if (isInParty() && getParty().isInReflection() && getParty().getReflection() instanceof DimensionalRift) {
            ((DimensionalRift) getParty().getReflection()).memberDead(this);
        }

        stopWaterTask();
        stopMountFeedTask();

        if (!isSalvation() && isOnSiegeField() && isCharmOfCourage()) {
            ask(new ConfirmDlg(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false, 60000));
            setCharmOfCourage(false);
        }

        if (AllSettingsConfig.AltKillAnnounce && killer != null && killer.isPlayer())
            AnnouncementUtils.announceToAll(killer.getName() + " убил игрока " + getName() + "!");

        super.onDeath(killer);
    }

    public void restoreExp() {
        restoreExp(100.);
    }

    public void restoreExp(final double percent) {
        if (percent == 0) {
            return;
        }

        int lostexp = 0;

        final String lostexps = playerVariables.get(PlayerVariables.LOST_EXP);
        if (lostexps != null) {
            lostexp = Integer.parseInt(lostexps);
            playerVariables.remove(PlayerVariables.LOST_EXP);
        }

        if (lostexp != 0) {
            addExpAndSp((long) (lostexp * percent / 100), 0);
        }
    }

    public void deathPenalty(final Creature killer) {
        if (killer == null) {
            return;
        }
        final boolean atwar = killer.getPlayer() != null && atWarWith(killer.getPlayer());

        double deathPenaltyBonus = getDeathPenalty().getLevel() * AllSettingsConfig.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
        if (deathPenaltyBonus < 2) {
            deathPenaltyBonus = 1;
        } else {
            deathPenaltyBonus /= 2;
        }

        // The death steal you some Exp: 10-40 lvl 8% loose
        double percentLost = 8.0;

        final int level = getLevel();
        if (level >= 79) {
            percentLost = 1.0;
        } else if (level >= 78) {
            percentLost = 1.5;
        } else if (level >= 76) {
            percentLost = 2.0;
        } else if (level >= 40) {
            percentLost = 4.0;
        }

        if (AllSettingsConfig.ALT_DEATH_PENALTY) {
            double rateXP = ServerConfig.RATE_XP;
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                rateXP = Math.max(1.0, ServerConfig.RATE_XP / CustomConfig.cutRateXP);
            }
            percentLost = percentLost * rateXP + _pkKills * AllSettingsConfig.ALT_PK_DEATH_RATE;
        }

        if (isFestivalParticipant() || atwar) {
            percentLost /= 4.0;
        }

        // Calculate the Experience loss
        int lostexp = (int) Math.round((ExpDataHolder.getInstance().getExpForLevel(level + 1) - ExpDataHolder.getInstance().getExpForLevel(level)) * percentLost / 100);
        lostexp *= deathPenaltyBonus;

        lostexp = (int) calcStat(Stats.EXP_LOST, lostexp, killer, null);

        // На зарегистрированной осаде нет потери опыта, на чужой осаде - как при обычной смерти от *моба*
        if (isOnSiegeField()) {
            final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
            if (siegeEvent != null) {
                lostexp = 0;
            }

            if (siegeEvent != null) {
                final List<Effect> effect = getEffectList().getEffectsBySkillId(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                if (effect != null) {
                    final int syndromeLvl = effect.get(0).getSkill().getLevel();
                    if (syndromeLvl < 5) {
                        getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
                        skill.getEffects(this, this, false, false);
                    } else if (syndromeLvl == 5) {
                        getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
                        skill.getEffects(this, this, false, false);
                    }
                } else {
                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
                    skill.getEffects(this, this, false, false);
                }
            }
        }

        if (AllSettingsConfig.ALT_GAME_DELEVEL) {
            final long before = getExp();
            addExpAndSp(-lostexp, 0);
            final long lost = before - getExp();
            if (lost > 0)
                playerVariables.set(PlayerVariables.LOST_EXP, String.valueOf(lost), -1);
        }
    }

    public Request getRequest() {
        return _request;
    }

    public void setRequest(final Request transaction) {
        _request = transaction;
    }

    /**
     * Проверка, занят ли игрок для ответа на зарос
     *
     * @return true, если игрок не может ответить на запрос
     */
    public boolean isBusy() {
        return isProcessingRequest() || isOutOfControl() || isInOlympiadMode() || AllowJoinPartyOnPvpEvent() || isInStoreMode() || isInDuel() || getMessageRefusal() || isBlockAll() || isInvisible();
    }

    public boolean AllowJoinPartyOnPvpEvent() {
        if (EventsConfig.TVTAllowJoinParty || EventsConfig.CFTAllowJoinParty) {
            return false;
        }
        return getTeam() != TeamType.NONE;
    }

    public boolean isProcessingRequest() {
        if (_request == null) {
            return false;
        }
        if (!_request.isInProgress()) {
            return false;
        }
        return true;
    }

    public boolean isInTrade() {
        return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
    }

    public List<L2GameServerPacket> addVisibleObject(final GameObject object, final Creature dropper) {
        if (isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || !object.isVisible()) {
            return Collections.emptyList();
        }

        return object.addPacketList(this, dropper);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(final Player forPlayer, final Creature dropper) {
        if (isInvisible() && forPlayer.getObjectId() != getObjectId()) {
            return Collections.emptyList();
        }

        if (getPrivateStoreType() != STORE_PRIVATE_NONE && forPlayer.playerVariables.getBoolean(PlayerVariables.NO_TRADERS)) {
            return Collections.emptyList();
        }

        final List<L2GameServerPacket> list = new ArrayList<>();
        if (forPlayer.getObjectId() != getObjectId()) {
            list.add(isPolymorphed() ? new NpcInfo(this) : new CharInfo(this));
        }

        list.add(new ExBR_ExtraUserInfo(this));

        if (isSitting() && _sittingObject != null) {
            list.add(new ChairSit(this, _sittingObject));
        }

        if (getPrivateStoreType() != STORE_PRIVATE_NONE) {
            if (getPrivateStoreType() == STORE_PRIVATE_BUY) {
                list.add(new PrivateStoreMsgBuy(this));
            } else if (getPrivateStoreType() == STORE_PRIVATE_SELL) {
                list.add(new PrivateStoreMsgSell(this));
            } else if (getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE) {
                list.add(new ExPrivateStoreWholeMsg(this));
            } else if (getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE) {
                list.add(new RecipeShopMsg(this));
            }
            if (forPlayer.isInZonePeace()) // Мирным торговцам не нужно посылать больше пакетов, для экономии траффика
            {
                return list;
            }
        }

        if (isCastingNow()) {
            final Creature castingTarget = getCastingTarget();
            final SkillEntry castingSkill = getCastingSkill();
            final long animationEndTime = getAnimationEndTime();
            if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && getAnimationEndTime() > 0) {
                list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
            }
        }

        if (isInCombat()) {
            list.add(new AutoAttackStart(getObjectId()));
        }

        list.add(RelationChanged.update(forPlayer, this, forPlayer));
        final DominionSiegeEvent dominionSiegeEvent = getEvent(DominionSiegeEvent.class);
        if (dominionSiegeEvent != null && (dominionSiegeEvent.isInProgress() || dominionSiegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(getObjectId()))) {
            list.add(new ExDominionWarStart(this));
        }

        if (isInBoat()) {
            list.add(getBoat().getOnPacket(this, getInBoatPosition()));
        } else {
            if (isMoving || isFollow) {
                list.add(movePacket());
            }
        }
        return list;
    }

    public List<L2GameServerPacket> removeVisibleObject(final GameObject object, final List<L2GameServerPacket> list) {
        if (isLogoutStarted() || object == null || object.getObjectId() == getObjectId()) // FIXME	|| isTeleporting()
        {
            return Collections.emptyList();
        }

        if (object == getServitor() && isTeleporting()) // не удаляем пета у игрока при телепорте
        {
            return Collections.emptyList();
        }

        final List<L2GameServerPacket> result = list == null ? object.deletePacketList() : list;

        if (!isInObserverMode()) {
            getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
        }

        return result;
    }

    private void levelSet(final int levels) {
        if (levels > 0) {
            sendPacket(SystemMsg.YOUR_LEVEL_HAS_INCREASED);
            broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));

            setCurrentHpMp(getMaxHp(), getMaxMp());
            setCurrentCp(getMaxCp());
        } else if (levels < 0) {
            if (AllSettingsConfig.ALT_REMOVE_SKILLS_ON_DELEVEL) {
                checkSkills();
            }
        }

        // Recalculate the party level
        if (isInParty()) {
            getParty().recalculatePartyData();
        }

        if (_clan != null) {
            _clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
        }

        if (_matchingRoom != null) {
            _matchingRoom.broadcastPlayerUpdate(this);
        }

        // Give Expertise skill of this level
        rewardSkills(true);
    }

    /**
     * Удаляет все скиллы, которые учатся на уровне большем, чем текущий+maxDiff
     */
    public void checkSkills() {
        for (final SkillEntry sk : getAllSkillsArray()) {
            SkillTreeTable.checkSkill(this, sk);
        }
    }

    public void startTimers() {
        startAutoSaveTask();
        getPremiumAccountComponent().startPcBangPointsTask();
        startHourlyTask();
        getPremiumAccountComponent().startBonusTask();
        getInventory().startTimers();
        resumeQuestTimers();
    }

    public void stopAllTimers() {
        //cubicdata
        deleteCubics();
        deleteAgathion();

        stopWaterTask();
        getPremiumAccountComponent().stopBonusTask();
        stopHourlyTask();
        stopKickTask();
        stopMountFeedTask();
        getVitalityComponent().stopVitalityTask();
        getPremiumAccountComponent().stopPcBangPointsTask();
        stopAutoSaveTask();
        getRecommendationComponent().stopRecomBonusTask(true);
        getInventory().stopAllTimers();
        stopQuestTimers();
        stopLectureTask();
        stopEnableUserRelationTask();
        getNevitComponent().stopTasksOnLogout();
    }

    @Override
    public Servitor getServitor() {
        return _summon;
    }

    public void setServitor(final Servitor summon) {
        boolean isPet = false;
        if (_summon != null && _summon.isPet()) {
            isPet = true;
        }

        _summon = summon;
        autoShot();
        if (summon == null) {
            if (isPet) {
                setPetControlItem(null);
            }

            getEffectList().stopEffect(4140);
        }
    }

    // честно спижжено из fork by n3konation
    public void scheduleDelete() {
        if (isLogoutStarted() || isInOfflineMode())
            return;
        if (!AllSettingsConfig.AltAllowNoCarrier) {
            stopMove();
            prepareToLogout();
            deleteMe();
            return;
        }
        final long time = getPlayerVariables().getInt(PlayerVariables.NO_CARRIER_TIME, 60) * 1000;
        if (time <= 0) {
            stopMove();
            prepareToLogout();
            deleteMe();
            return;
        }
        if (!noCarrier.compareAndSet(false, true))
            return;
        stopMove();
        // сохраняем сразу, не ждем отложенного удаления игрока.
        //		if(time > 0L) // не, не сохраняем
        //store(true);

        stopNoCarrierSchedule();
        noCarrierSchedule = ThreadPoolManager.getInstance().schedule(() -> {
            kick(false);
        }, time);
        broadcastCharInfo();
    }

    @Override
    protected void onDelete() {
        super.onDelete();

        // Убираем фэйк в точке наблюдения
        if (_observePoint != null) {
            _observePoint.deleteMe();
        }

        //Send friendlists to friends that this player has logged off
        getFriendComponent().notifyFriends(false);

        getTeleportBookMarkComponent().getTpBookMarks().clear();
        _inventory.clear();
        _warehouse.clear();
        _summon = null;
        _arrowItem = null;
        fistsWeaponItem = null;
        playersOnAccount = null;
        _enchantScroll = null;
        _lastNpc = HardReferences.emptyRef();
        _observePoint = null;
    }

    public List<TradeItem> getTradeList() {
        return _tradeList;
    }

    public void setTradeList(final List<TradeItem> list) {
        _tradeList = list;
    }

    public String getSellStoreName() {
        return _sellStoreName;
    }

    public void setSellStoreName(final String name) {
        _sellStoreName = Strings.stripToSingleLine(name);
    }

    public void setSellList(final boolean packageSell, final List<TradeItem> list) {
        if (packageSell) {
            packageSellList = list;
        } else {
            sellList = list;
        }
    }

    public List<TradeItem> getSellList() {
        return getSellList(_privatestore == STORE_PRIVATE_SELL_PACKAGE);
    }

    public List<TradeItem> getSellList(final boolean packageSell) {
        return packageSell ? packageSellList : sellList;
    }

    public String getBuyStoreName() {
        return _buyStoreName;
    }

    public void setBuyStoreName(final String name) {
        _buyStoreName = Strings.stripToSingleLine(name);
    }

    public List<TradeItem> getBuyList() {
        return buyList;
    }

    public void setBuyList(final List<TradeItem> list) {
        buyList = list;
    }

    public String getManufactureName() {
        return _manufactureName;
    }

    public void setManufactureName(final String name) {
        _manufactureName = Strings.stripToSingleLine(name);
    }

    public List<ManufactureItem> getCreateList() {
        return createList;
    }

    public void setCreateList(final List<ManufactureItem> list) {
        createList = list;
    }

    public boolean isInStoreMode() {
        return _privatestore != STORE_PRIVATE_NONE;
    }

    public int getPrivateStoreType() {
        return _privatestore;
    }

    public void setPrivateStoreType(final int type) {
        _privatestore = type;
        if (type != STORE_PRIVATE_NONE) {
            playerVariables.set(PlayerVariables.STORE_MODE, String.valueOf(type), -1);
        } else {
            playerVariables.remove(PlayerVariables.STORE_MODE);
        }
    }

    @Override
    public Clan getClan() {
        return _clan;
    }

    /**
     * Set the _clan object, _clanId, _clanLeader Flag and title of the L2Player.<BR><BR>
     *
     * @param clan the clat to set
     */
    public void setClan(final Clan clan) {
        if (_clan != null && _clan != clan) {
            playerVariables.remove(PlayerVariables.CAN_WAREHOUSE_WITHDRAW);
        }

        final Clan oldClan = _clan;
        if (oldClan != null && clan == null) {
            getAllSkills().stream().filter(skill -> skill.getEntryType() == SkillEntryType.CLAN).forEach(skill -> {
                removeSkill(skill, false);
            });
        }

        _clan = clan;

        if (clan == null) {
            _pledgeType = Clan.SUBUNIT_NONE;
            _pledgeClass = 0;
            _powerGrade = 0;
            _apprentice = 0;
            getInventory().validateItems();
            return;
        }

        if (!clan.isAnyMember(getObjectId())) {
            setClan(null);
            if (!isNoble()) {
                setTitle("");
            }
        }
    }

    public SubUnit getSubUnit() {
        return _clan == null ? null : _clan.getSubUnit(_pledgeType);
    }

    public ClanHall getClanHall() {
        final int id = _clan != null ? _clan.getHasHideout() : 0;
        return ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
    }

    public Castle getCastle() {
        final int id = _clan != null ? _clan.getCastle() : 0;
        return ResidenceHolder.getInstance().getResidence(Castle.class, id);
    }

    public Fortress getFortress() {
        final int id = _clan != null ? _clan.getHasFortress() : 0;
        return ResidenceHolder.getInstance().getResidence(Fortress.class, id);
    }

    public Alliance getAlliance() {
        return _clan == null ? null : _clan.getAlliance();
    }

    public boolean isClanLeader() {
        return _clan != null && getObjectId() == _clan.getLeaderId();
    }

    public boolean isAllyLeader() {
        return getAlliance() != null && getAlliance().getLeader().getLeaderId() == getObjectId();
    }

    @Override
    public void reduceArrowCount() {
        if (AllSettingsConfig.unlimitedArrowsBolts)
            return;
        sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
        if (!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L)) {
            getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            _arrowItem = null;
        }
    }

    /**
     * Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2Player then return True.
     */
    protected boolean checkAndEquipArrows() {
        // Check if nothing is equipped in left hand
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null) {
            final ItemInstance activeWeapon = getActiveWeaponInstance();
            if (activeWeapon != null) {
                if (activeWeapon.getItemType() == WeaponType.BOW) {
                    _arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
                } else if (activeWeapon.getItemType() == WeaponType.CROSSBOW) {
                    getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
                }
            }

            // Equip arrows needed in left hand
            if (_arrowItem != null) {
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
            }
        } else
        // Get the L2ItemInstance of arrows equipped in left hand
        {
            _arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        }

        return _arrowItem != null;
    }

    public long getUptime() {
        return System.currentTimeMillis() - _uptime;
    }

    public void setUptime(final long time) {
        _uptime = time;
    }

    public boolean isInParty() {
        return _party != null;
    }

    public void joinParty(final Party party) {
        if (party != null) {
            party.addPartyMember(this);
        }
    }

    public void leaveParty() {
        if (isInParty()) {
            _party.removePartyMember(this, false);
        }
    }

    public Party getParty() {
        return _party;
    }

    public void setParty(final Party party) {
        _party = party;
    }

    public Location getLastPartyPosition() {
        return _lastPartyPosition;
    }

    public void setLastPartyPosition(final Location loc) {
        _lastPartyPosition = loc;
    }

    public boolean isGM() {
        return playerAccess != null && playerAccess.IsGM;
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    @Override
    public int getAccessLevel() {
        return _accessLevel;
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    public void setAccessLevel(final int level) {
        _accessLevel = level;
    }

    public PlayerAccess getPlayerAccess() {
        return playerAccess;
    }

    public void setPlayerAccess(final PlayerAccess pa) {
        if (pa != null) {
            if (!pa.GMAllowedIP.contains(NOT_USED)) {
                if (getIP().contains(pa.GMAllowedIP)) {
                    playerAccess = pa;
                } else {
                    playerAccess = new PlayerAccess();
                    _log.warn("Player|IP: (" + getName() + "|" + getIP() + ") trying enter in world from GMAccount - and him not allowed this IP. GMAllowedIP: " + pa.GMAllowedIP);
                    GmManager.broadcastMessageToGMs("Player|IP: (" + getName() + "|" + getIP() + ") trying enter in world from GMAccount - and him not allowed this IP. GMAllowedIP: " + pa.GMAllowedIP);
                }
            } else {
                playerAccess = pa;
            }
        } else {
            playerAccess = new PlayerAccess();
        }

        setAccessLevel(isGM() || playerAccess.Menu ? 100 : 0);
    }

    @Override
    public double getLevelMod() {
        if (isCombatTransformed()) {
            return getTransformation().getLevelBonus();
        }
        return PCParameterHolder.getInstance().getLevelBonus().returnValue(getLevel());
    }

    public double getPcKarmaBonus() {
        return PCParameterHolder.getInstance().getPcKarmaIncrease().returnValue(getLevel());
    }

    /**
     * Update Stats of the L2Player client side by sending Server->Client packet UserInfo/StatusUpdate to this L2Player and CharInfo/StatusUpdate to all players around (broadcast).<BR><BR>
     */
    @Override
    public void updateStats() {
        if (entering || isLogoutStarted()) {
            return;
        }

        refreshOverloaded();
        refreshExpertisePenalty();
        super.updateStats();
    }

    @Override
    public void sendChanges() {
        if (entering || isLogoutStarted()) {
            return;
        }
        super.sendChanges();
    }

    /**
     * Send a Server->Client StatusUpdate packet with Karma to the L2Player and all L2Player to inform (broadcast).
     */
    public void updateKarma(final boolean flagChanged) {
        sendStatusUpdate(true, true, StatusUpdate.KARMA);
        if (flagChanged) {
            broadcastRelationChanged();
        }
    }

    public boolean isOnline() {
        return _isOnline;
    }

    public void setIsOnline(final boolean isOnline) {
        _isOnline = isOnline;
    }

    public void setOnlineStatus(final boolean isOnline) {
        _isOnline = isOnline;
        updateOnlineStatus();
        if (isOnline) {
            stopNoCarrierSchedule();
            if (noCarrier.compareAndSet(true, false)) {
                broadcastCharInfo();
                loadingAfterCarrier.compareAndSet(false, true);
                if (isInParty()) {
                    this.sendPacket(PartySmallWindowDeleteAll.STATIC, new PartySmallWindowAll(this.getParty(), this), ExMPCCOpen.STATIC);
                    if (this.getServitor() != null)
                        this.broadCast(new ExPartyPetWindowAdd(this.getServitor()));
                }
            }
        }
    }

    public void updateOnlineStatus() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
            statement.setInt(1, isOnline() && !isInOfflineMode() ? 1 : 0);
            statement.setLong(2, System.currentTimeMillis() / 1000L);
            statement.setInt(3, getObjectId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    public void increaseKarma(final long add_karma) {
        final boolean flagChanged = karma == 0;
        long new_karma = karma + add_karma;

        if (new_karma > Integer.MAX_VALUE) {
            new_karma = Integer.MAX_VALUE;
        }

        if (karma == 0 && new_karma > 0) {
            if (_pvpFlag > 0) {
                _pvpFlag = 0;
                if (_PvPRegTask != null) {
                    _PvPRegTask.cancel(true);
                    _PvPRegTask = null;
                }
                sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);
            }

            karma = (int) new_karma;
        } else {
            karma = (int) new_karma;
        }

        updateKarma(flagChanged);
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    public void decreaseKarma(final int i) {
        final boolean flagChanged = karma > 0;
        karma -= i;
        if (karma <= 0) {
            karma = 0;
            updateKarma(flagChanged);
        } else {
            updateKarma(false);
        }
    }

    /**
     * Update Player stats in the characters table of the database.
     */
    public void store(final boolean fast) {
        if (!_storeLock.tryLock()) {
            return;
        }

        try {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement(//
                        "UPDATE characters SET face=?,hairStyle=?,hairColor=?,x=?,y=?,z=?" + //
                                ",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,hunt_points=?,hunt_time=?,clanid=?,deletetime=?," + //
                                "title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
                                "onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,vitality=?,fame=?,bookmarks=?,last_hwid=? WHERE obj_Id=? LIMIT 1");
                statement.setInt(1, getAppearanceComponent().getFace());
                statement.setInt(2, getAppearanceComponent().getHairStyle());
                statement.setInt(3, getAppearanceComponent().getHairColor());
                if (_stablePoint == null) // если игрок находится в точке в которой его сохранять не стоит (например на виверне) то сохраняются последние координаты
                {
                    statement.setInt(4, getX());
                    statement.setInt(5, getY());
                    statement.setInt(6, getZ());
                } else {
                    statement.setInt(4, _stablePoint.x);
                    statement.setInt(5, _stablePoint.y);
                    statement.setInt(6, _stablePoint.z);
                }
                statement.setInt(7, getKarma());
                statement.setInt(8, getPvpKills());
                statement.setInt(9, getPkKills());
                statement.setInt(10, getRecommendationComponent().getRecomHave());
                statement.setInt(11, getRecommendationComponent().getRecomLeft());
                statement.setInt(12, getRecommendationComponent().getRecomBonusTime());
                statement.setInt(13, getNevitComponent().getPoints());
                statement.setInt(14, getNevitComponent().getTime());
                statement.setInt(15, getClanId());
                statement.setInt(16, getDeleteTimer());
                statement.setString(17, _title);
                statement.setInt(18, _accessLevel);
                statement.setInt(19, isOnline() && !isInOfflineMode() ? 1 : 0);
                statement.setLong(20, getLeaveClanTime() / 1000L);
                statement.setLong(21, getDeleteClanTime() / 1000L);
                statement.setLong(22, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
                statement.setInt(23, (int) (_onlineBeginTime > 0 ? (_onlineTime + System.currentTimeMillis() - _onlineBeginTime) / 1000L : _onlineTime / 1000L));
                statement.setInt(24, getPledgeType());
                statement.setInt(25, getPowerGrade());
                statement.setInt(26, getLvlJoinedAcademy());
                statement.setInt(27, getApprentice());
                statement.setBytes(28, getKeyBindings());
                statement.setInt(29, getPremiumAccountComponent().getPcBangPoints());
                statement.setString(30, getName());
                statement.setInt(31, (int) getVitalityComponent().getVitality());
                statement.setInt(32, getFame());
                statement.setInt(33, getTeleportBookMarkComponent().getTpBookmarkSize());
                String hwid = getNetConnection() != null ? getNetConnection().getHWID() : getEnterHwid();
                statement.setString(34, hwid != null ? hwid : "");
                statement.setInt(35, getObjectId());

                statement.executeUpdate();
            } catch (final Exception e) {
                _log.error("Could not store char data: " + this + '!', e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }

            GameStats.increaseUpdatePlayerBase();

            getPlayerClassComponent().storeCharSubClasses();

            if (!fast) {
                CharacterEffectDAO.getInstance().insert(this);
                CharacterGroupReuseDAO.getInstance().insert(this);
                storeDisableSkills();
                storeBlockList();
            }
        } finally {
            _storeLock.unlock();
        }
    }

    /**
     * Add a skill to the L2Player skills and its Func objects to the calculator set of the L2Player and save update in the character_skills table of the database.
     *
     * @return The L2Skill replaced or null if just added a new L2Skill
     */
    public SkillEntry addSkill(final SkillEntry newSkill, final boolean store) {
        if (newSkill == null) {
            return null;
        }

        // Add a skill to the L2Player skills and its Func objects to the calculator set of the L2Player
        final SkillEntry oldSkill = super.addSkill(newSkill);
        if (isTransformed()) {
            getTransformation().setStateUpdate(true);
        }
        if (newSkill == oldSkill) {
            return oldSkill;
        }

        // Add or update a L2Player skill in the character_skills table of the database
        if (store && !isPhantom()) {
            CharacterSkillDAO.getInstance().replace(this, newSkill);
        }

        return oldSkill;
    }

    public SkillEntry removeSkill(final SkillEntry skill, final boolean fromDB) {
        if (skill == null) {
            return null;
        }
        if (isTransformed()) {
            getTransformation().setStateUpdate(true);
        }
        return removeSkill(skill.getId(), fromDB);
    }

    /**
     * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
     *
     * @return The L2Skill removed
     */
    public SkillEntry removeSkill(final int id, final boolean fromDB) {
        final SkillEntry oldSkill = super.removeSkillById(id);

        if (fromDB && oldSkill != null) {
            CharacterSkillDAO.getInstance().delete(this, oldSkill);
        }

        return oldSkill;
    }

    public void storeDisableSkills() {
        Connection con = null;
        Statement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getPlayerClassComponent().getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

            if (skillReuses.isEmpty()) {
                return;
            }

            final SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
            synchronized (skillReuses) {
                StringBuilder sb;
                for (final TimeStamp timeStamp : skillReuses.values()) {
                    if (timeStamp.hasNotPassed()) {
                        sb = new StringBuilder("(");
                        sb.append(getObjectId()).append(',');
                        sb.append(timeStamp.getId()).append(',');
                        sb.append(timeStamp.getLevel()).append(',');
                        sb.append(getPlayerClassComponent().getActiveClassId()).append(',');
                        sb.append(timeStamp.getEndTime()).append(',');
                        sb.append(timeStamp.getReuseBasic()).append(')');
                        b.write(sb.toString());
                    }
                }
            }
            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            _log.warn("Could not store disable skills data: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void restoreDisableSkills() {
        skillReuses.clear();

        Connection con = null;
        Statement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + getObjectId() + " AND class_index=" + getPlayerClassComponent().getActiveClassId());
            while (rset.next()) {
                final int skillId = rset.getInt("skill_id");
                final int skillLevel = rset.getInt("skill_level");
                final long endTime = rset.getLong("end_time");
                final long rDelayOrg = rset.getLong("reuse_delay_org");
                final long curTime = System.currentTimeMillis();

                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skillId, skillLevel);

                if (skill != null && endTime - curTime > 500) {
                    skillReuses.put(skill.hashCode(), new TimeStamp(skill, endTime, rDelayOrg));
                }
            }
            DbUtils.close(statement);

            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getPlayerClassComponent().getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
        } catch (final Exception e) {
            _log.error("Could not restore active skills data!", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public int getDyeEmptySlots() {
        int totalSlots = 1 + getPlayerClassComponent().getClassId().level();
        for (int i = 0; i < 3; i++) {
            if (dyes[i] != null) {
                totalSlots--;
            }
        }

        if (totalSlots <= 0) {
            return 0;
        }

        return totalSlots;

    }

    /**
     * Remove a dye of the Player, save update in the character_dyes table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.<BR><BR>
     */
    public boolean removeDye(int slot) {
        if (slot < 1 || slot > 3) {
            return false;
        }

        slot--;

        if (dyes[slot] == null) {
            return false;
        }

        final DyeData dye = dyes[slot];
        final int dyeID = dye.dye_item_id;

        dyes[slot] = null;

        CharacterDyeDAO.getInstance().delete(this, slot + 1);

        recalcDyeStats();
        sendPacket(new HennaInfo(this));
        sendUserInfo(true);

        ItemFunctions.addItem(this, dyeID, dye.cancel_count, true);

        return true;
    }

    /**
     * Add a dye to the Player, save update in the character_dyes table of the database and send Server->Client HennaInfo/UserInfo packet to this Player.<BR><BR>
     *
     * @param dye DyeData РґР»СЏ РґРѕР±Р°РІР»РµРЅРёСЏ
     */
    public boolean addDye(final DyeData dye) {
        if (getDyeEmptySlots() == 0) {
            sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (dyes[i] == null) {
                dyes[i] = dye;

                recalcDyeStats();

                CharacterDyeDAO.getInstance().insert(this, i + 1, dye.dye_id);

                sendPacket(new HennaInfo(this));
                sendUserInfo(true);

                return true;
            }
        }

        return false;
    }

    /**
     * Calculate dyes modifiers of this L2Player.
     */
    public void recalcDyeStats() {
        dyeINT = 0;
        dyeSTR = 0;
        dyeCON = 0;
        dyeMEN = 0;
        dyeWIT = 0;
        dyeDEX = 0;

        for (int i = 0; i < 3; i++) {
            final DyeData dye = dyes[i];
            if (dye == null) {
                continue;
            }
            if (!PlayerUtils.canPlayerWearDye(this, dye)) {
                continue;
            }

            dyeINT += dye._int;
            dyeSTR += dye.str;
            dyeMEN += dye.men;
            dyeCON += dye.con;
            dyeWIT += dye.wit;
            dyeDEX += dye.dex;
        }

        if (dyeINT > 5) {
            dyeINT = 5;
        }
        if (dyeSTR > 5) {
            dyeSTR = 5;
        }
        if (dyeMEN > 5) {
            dyeMEN = 5;
        }
        if (dyeCON > 5) {
            dyeCON = 5;
        }
        if (dyeWIT > 5) {
            dyeWIT = 5;
        }
        if (dyeDEX > 5) {
            dyeDEX = 5;
        }
    }

    /**
     * @param slot id слота у перса
     * @return the dye of this L2Player corresponding to the selected slot.<BR><BR>
     */
    public DyeData getDye(final int slot) {
        if (slot < 1 || slot > 3) {
            return null;
        }
        return dyes[slot - 1];
    }

    public int getDyeStatINT() {
        return dyeINT;
    }

    public int getDyeStatSTR() {
        return dyeSTR;
    }

    public int getDyeStatCON() {
        return dyeCON;
    }

    public int getDyeStatMEN() {
        return dyeMEN;
    }

    public int getDyeStatWIT() {
        return dyeWIT;
    }

    public int getDyeStatDEX() {
        return dyeDEX;
    }

    @Override
    public boolean consumeItem(final int itemConsumeId, final long itemCount) {
        if (getInventory().destroyItemByItemId(itemConsumeId, itemCount)) {
            if (!ItemTemplateHolder.getInstance().getTemplate(itemConsumeId).isHideConsumeMessage()) {
                sendPacket(SystemMessage.removeItems(itemConsumeId, itemCount));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeItemMp(final int itemId, final int mp) {
        for (final ItemInstance item : getInventory().getPaperdollItems()) {
            if (item != null && item.getItemId() == itemId) {
                final int newMp = item.getLifeTime() - mp;
                if (newMp >= 0) {
                    item.setLifeTime(newMp);
                    sendPacket(new InventoryUpdate().addModifiedItem(item));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * @return True if the L2Player is a Mage.<BR><BR>
     */
    @Override
    public boolean isMageClass() {
        return getPlayerClassComponent().getClassId().isMage();
    }

    public boolean isMounted() {
        return _mountNpcId > 0;
    }

    public final boolean isRiding() {
        return _riding;
    }

    public final void setRiding(final boolean mode) {
        _riding = mode;
    }

    /**
     * Проверяет, можно ли приземлиться в этой зоне.
     *
     * @return можно ли приземлится
     */
    public boolean checkLandingState() {
        if (isInZone(ZoneType.no_landing)) {
            return false;
        }

        final SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
        if (siege != null) {
            final Residence unit = siege.getResidence();
            if (unit != null && getClan() != null && isClanLeader() && (getClan().getCastle() == unit.getId() || getClan().getHasFortress() == unit.getId())) {
                return true;
            }
            return false;
        }

        return true;
    }

    public void setMount(final int npcId, final int controlObjectId, final int level, int currentFed) {
        if (isCursedWeaponEquipped()) {
            return;
        }

        final PetData info = PetDataHolder.getInstance().getPetData(npcId);
        if (info == null) {
            return;
        }

        switch (npcId) {
            case PetId.STRIDER_WIND_ID:
            case PetId.STRIDER_STAR_ID:
            case PetId.STRIDER_TWILIGHT_ID:
            case PetId.RED_STRIDER_WIND_ID:
            case PetId.RED_STRIDER_STAR_ID:
            case PetId.RED_STRIDER_TWILIGHT_ID:
            case PetId.GUARDIANS_STRIDER_ID:
                setRiding(true);
                if (isNoble()) {
                    addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_STRIDER_ASSAULT, 1), false);
                }
                break;
            case PetId.WYVERN_ID:
                setFlying(true);
                setLoc(getLoc().changeZ(32));
                addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_WYVERN_BREATH, 1), false);
                break;
            case PetId.WGREAT_WOLF_ID:
            case PetId.FENRIR_WOLF_ID:
            case PetId.WFENRIR_WOLF_ID:
                setRiding(true);
                break;
        }

        if (npcId > 0) {
            unEquipWeapon();
        }

        _mountNpcId = npcId;
        _mountObjId = controlObjectId;
        _mountLevel = level;
        _mountCurrentFed = currentFed;
        _mountMaxFed = info.getLevelStatForLevel(level).getMaxMeal();
        _mountCurrentFedId = info.getLevelStatForLevel(level).getFood();

        broadcastUserInfo(true); // нужно послать пакет перед Ride для корректного снятия оружия с заточкой
        if (_mountCurrentFed >= 0) {
            sendPacket(new SetupGauge(this, SetupGauge.GREEN, _mountCurrentFed * 10000, _mountMaxFed * 10000));
            startMountFeedTask();
        }
        broadcastPacket(new Ride(this));
        broadcastUserInfo(true); // нужно послать пакет после Ride для корректного отображения скорости

        sendPacket(new SkillList(this));
    }

    public void dismount() {
        if (!isMounted()) {
            return;
        }

        setFlying(false);
        setRiding(false);
        if (isTransformed()) {
            stopTransformation();
        }

        removeSkillById(Skill.SKILL_STRIDER_ASSAULT);
        removeSkillById(Skill.SKILL_WYVERN_BREATH);
        getEffectList().stopEffect(Skill.SKILL_HINDER_STRIDER);
        sendPacket(new SetupGauge(this, SetupGauge.GREEN, 0, 0));

        stopMountFeedTask();
        PetDAO.getInstance().updateMount(_mountObjId, _mountCurrentFed);

        _mountNpcId = 0;
        _mountObjId = 0;
        _mountLevel = 0;
        _mountCurrentFed = -1;
        _mountCurrentFedId = null;

        broadcastPacket(new Ride(this));
        broadcastUserInfo(true);

        sendPacket(new SkillList(this));
    }

    public void unEquipWeapon() {
        ItemInstance wpn = getSecondaryWeaponInstance();
        if (wpn != null) {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        wpn = getActiveWeaponInstance();
        if (wpn != null) {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        abortAttack(true, true);
        abortCast(true, true);
    }

    @Override
    public int getSpeed(int baseSpeed) {
        if (isCommonTransformed()) {
            final TCommon common = getTransformation().getCommon();
            if (isInWater()) {
                return (int) calcStat(Stats.RUN_SPEED, isRunning() ? common.moving_speed[3] : common.moving_speed[2], null, null);
            } else if (isFlying()) {
                return (int) calcStat(Stats.RUN_SPEED, isRunning() ? common.moving_speed[5] : common.moving_speed[4], null, null);
            } else {
                return (int) calcStat(Stats.RUN_SPEED, isRunning() ? common.moving_speed[1] : common.moving_speed[0], null, null);
            }
        } else if (isMounted()) {
            final PetData petData = PetDataHolder.getInstance().getPetData(_mountNpcId);
            if (petData != null) {
                final double hungryModifier = getMountCurrentFed() * 100 / getMountMaxFed() <= petData.getLevelStatForLevel(_mountLevel).getHungryLimit() ? 0.5 : 1;
                int speed = 187; // На всякий случай, установим стоковую скорость равную скорости страйдера.
                if (isInWater()) {
                    speed = isRunning() ? petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[3] : petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[2];
                } else if (isFlying()) {
                    speed = isRunning() ? petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[5] : petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[4];
                } else {
                    speed = isRunning() ? petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[1] : petData.getLevelStatForLevel(_mountLevel).getSpeedOnRide()[0];
                }
                return (int) calcStat(Stats.RUN_SPEED, speed * hungryModifier, null, null);
            }
            return (int) calcStat(Stats.RUN_SPEED, baseSpeed, null, null);
        }
        return super.getSpeed(baseSpeed);
    }

    public int getMountNpcId() {
        return _mountNpcId;
    }

    public int getMountObjId() {
        return _mountObjId;
    }

    public int getMountLevel() {
        return _mountLevel;
    }

    public int getMountCurrentFed() {
        return _mountCurrentFed;
    }

    public void setMountCurrentFed(final int currentFed) {
        _mountCurrentFed = currentFed;
    }

    public int getMountMaxFed() {
        return _mountMaxFed;
    }

    private void startMountFeedTask() {
        stopMountFeedTask();
        if (isMounted() && getMountCurrentFed() >= 0) {
            _mountFeedTask = ThreadPoolManager.getInstance().schedule(new MountFeedTask(this), 10000L);
        }
    }

    private void stopMountFeedTask() {
        final ScheduledFuture<?> task = _mountFeedTask;
        if (task != null) {
            task.cancel(false);
            _mountFeedTask = null;
        }
    }

    public void updateMountFed() //FIXME[K] - доделать обновление скорости при hungrylimit тут и в getSpeed
    {
        if (isDead() || !isMounted()) {
            return;
        }

        final int hungryLimit = PetDataHolder.getInstance().getPetData(_mountNpcId).getLevelStatForLevel(_mountLevel).getHungryLimit();
        if (!isMovementDisabled() && _mountCurrentFed * 100 / getMountMaxFed() <= hungryLimit) {
            for (ItemInstance food : getInventory().getItems()) {
                if (ArrayUtils.contains(_mountCurrentFedId, food.getItemId()) && food.getTemplate().testCondition(this, food)) {
                    if (food.getTemplate().getHandler().useItem(this, food, false)) {
                        break;
                    }
                }
            }
        }
        sendChanges();

        final int consumeNormal = PetDataHolder.getInstance().getPetData(_mountNpcId).getLevelStatForLevel(_mountLevel).getConsumeMealInNormalOnRide();
        final int consumeBattle = PetDataHolder.getInstance().getPetData(_mountNpcId).getLevelStatForLevel(_mountLevel).getConsumeMealInBattleOnRide();
        _mountCurrentFed = Math.max(_mountCurrentFed - (isInCombat() ? consumeBattle : consumeNormal), 0);
        sendPacket(new SetupGauge(this, SetupGauge.GREEN, _mountCurrentFed * 10000, _mountMaxFed * 10000));
        startMountFeedTask();
    }

    public void sendDisarmMessage(final ItemInstance wpn) {
        final SystemMessage sm;
        if (wpn.getEnchantLevel() > 0) {
            sm = new SystemMessage(SystemMsg.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
            sm.addNumber(wpn.getEnchantLevel());
        } else {
            sm = new SystemMessage(SystemMsg.S1_HAS_BEEN_DISARMED);
        }
        sm.addItemName(wpn.getItemId());
        sendPacket(sm);
    }

    /**
     * Р’РѕР·РІСЂР°С‰Р°РµС‚ С‚РёРї РёСЃРїРѕР»СЊР·СѓРµРјРѕРіРѕ СЃРєР»Р°РґР°.
     *
     * @return null РёР»Рё С‚РёРї СЃРєР»Р°РґР°:<br>
     * <ul>
     * <li>WarehouseType.PRIVATE
     * <li>WarehouseType.CLAN
     * <li>WarehouseType.CASTLE
     * </ul>
     */
    public WarehouseType getUsingWarehouseType() {
        return _usingWHType;
    }

    /**
     * Устанавливает тип используемого склада.
     *
     * @param type тип склада:<BR>
     *             <ul>
     *             <li>WarehouseType.PRIVATE
     *             <li>WarehouseType.CLAN
     *             <li>WarehouseType.CASTLE
     *             </ul>
     */
    public void setUsingWarehouseType(final WarehouseType type) {
        _usingWHType = type;
    }

    public Map<Integer, CubicComponent> getCubics() {
        return _cubics == null ? Collections.emptyMap() : _cubics;
    }

    public CubicComponent getCubicSlot(final int slotId) {
        return _cubics.get(slotId);
    }

    public boolean isCubicSlot(final int slotId) {
        return _cubics.containsKey(slotId);
    }

    public void deleteCubics() {
        if (_cubics != null) {
            getCubics().values().forEach(c -> c.delete(true));
            getCubics().clear();
        }
    }

    public void addCubic(final DefaultCubicData cubic) {
        if (_cubics == null) {
            _cubics = new ConcurrentHashMap<>(3);
        }
        _cubics.put(cubic.slot, new CubicComponent(this, cubic));
    }

    public void removeCubicSlot(final int slotId, final boolean broadCast) {
        if (_cubics != null) {
            _cubics.remove(slotId);
            if (broadCast) {
                broadcastCharInfo();
            }
        }
    }

    public void removeCubicsSlot(final int[] slotsId, final boolean broadCast) {
        if (_cubics != null) {
//			boolean checkRemove = IntStream.of(slotsId).anyMatch(slotId -> _cubics::remove != null);
            if (/*checkRemove &&*/ broadCast) {
                broadcastCharInfo();
            }
        }
    }

    @Override
    public String toString() {
        return getName() + '[' + getObjectId() + ']';
    }

    /**
     * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).<BR><BR>
     */
    public int getEnchantEffect() {
        final ItemInstance wpn = getActiveWeaponInstance();

        if (wpn == null) {
            return 0;
        }

        return Math.min(127, wpn.getEnchantLevel());
    }

    /**
     * @return the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public NpcInstance getLastNpc() {
        return _lastNpc.get();
    }

    /**
     * Set the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public void setLastNpc(final NpcInstance npc) {
        if (npc == null) {
            _lastNpc = HardReferences.emptyRef();
        } else {
            _lastNpc = npc.getRef();
        }
    }

    public MultiSellListContainer getMultisell() {
        return _multisell;
    }

    public void setMultisell(final MultiSellListContainer multisell) {
        _multisell = multisell;
    }

    /**
     * @return True if L2Player is a participant in the Festival of Darkness.<BR><BR>
     */
    public boolean isFestivalParticipant() {
        return getReflection() instanceof DarknessFestival;
    }

    @Override
    public boolean unChargeShots(final boolean spirit) {
        final ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return false;
        }

        if (spirit) {
            weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
        } else {
            weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
        }

        autoShot();
        return true;
    }

    public boolean unChargeFishShot() {
        final ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return false;
        }
        weapon.setChargedFishshot(false);
        autoShot();
        return true;
    }

    public void autoShot() {
        for (final Integer shotId : _activeSoulShots) {
            final ItemInstance item = getInventory().getItemByItemId(shotId);
            if (item == null) {
                removeAutoSoulShot(shotId);
                continue;
            }
            final IItemHandler handler = item.getTemplate().getHandler();
            if (handler == null) {
                continue;
            }
            handler.useItem(this, item, false);
        }
    }

    public boolean getChargedFishShot() {
        final ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getChargedFishshot();
    }

    @Override
    public boolean getChargedSoulShot() {
        final ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getChargedSoulshot() == ItemInstance.CHARGED_SOULSHOT;
    }

    @Override
    public int getChargedSpiritShot() {
        final ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return 0;
        }
        return weapon.getChargedSpiritshot();
    }

    public void addAutoSoulShot(final Integer itemId) {
        _activeSoulShots.add(itemId);
    }

    public void removeAutoSoulShot(final Integer itemId) {
        _activeSoulShots.remove(itemId);
    }

    public Set<Integer> getAutoSoulShot() {
        return _activeSoulShots;
    }

    public int getClanPrivileges() {
        if (_clan == null) {
            return 0;
        }
        if (isClanLeader()) {
            return Clan.CP_ALL;
        }
        if (_powerGrade < 1 || _powerGrade > 9) {
            return 0;
        }
        final RankPrivs privs = _clan.getRankPrivs(_powerGrade);
        if (privs != null) {
            return privs.getPrivs();
        }
        return 0;
    }

    public void teleToClosestTown() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
    }

    public void teleToCastle() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
    }

    public void teleToFortress() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_FORTRESS), ReflectionManager.DEFAULT);
    }

    public void teleToClanhall() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
    }

    @Override
    public void sendMessage(final CustomMessage message) {
        sendPacket(message);
    }

    @Override
    public void teleToLocation(final int x, final int y, final int z, final Reflection r) {
        if (isDeleted()) {
            return;
        }

        super.teleToLocation(x, y, z, r);
        if (getServitor() != null) {
            getServitor().teleportToOwner();
        }
    }

    @Override
    public boolean onTeleported() {
        if (!super.onTeleported()) {
            return false;
        }

        if (isFakeDeath()) {
            breakFakeDeath();
        }

        if (isInBoat()) {
            setLoc(getBoat().getLoc());
        }

        // 15 секунд после телепорта на персонажа не агрятся мобы
        setNonAggroTime(System.currentTimeMillis() + AiConfig.NONAGGRO_TIME_ONTELEPORT);

        spawnMe();

        setLastClientPosition(getLoc());
        setLastServerPosition(getLoc());

        if (isPendingRevive()) {
            doRevive();
        }

        sendActionFailed();

        getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

        if (isLockedTarget() && getTarget() != null) {
            sendPacket(new MyTargetSelected(getTarget().getObjectId(), 0));
        }

        sendUserInfo(true);
        return true;
    }

    public boolean enterObserverMode(final Location loc) {
        final WorldRegion observerRegion = World.getRegion(loc);
        if (observerRegion == null) {
            return false;
        }

        if (!_observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING)) {
            return false;
        }

        setTarget(null);
        stopMove();
        sitDown(null);
        setFlying(true);

        // Очищаем все видимые обьекты
        World.removeObjectsFromPlayer(this, false);

        _observePoint = new ObservePoint(this);
        _observePoint.setLoc(loc);
        _observePoint.startImmobilized();

        // Отображаем надпись над головой
        broadcastCharInfo();

        // Переходим в режим обсервинга
        sendPacket(new ObserverStart(loc));

        return true;
    }

    public void appearObserverMode() {
        if (!_observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED)) {
            return;
        }

        _observePoint.spawnMe();

        World.showObjectsToPlayer(this, true);

        final OlympiadGame game = getOlympiadObserveGame();
        if (game != null) {
            game.addSpectator(this);
            game.broadcastInfo(null, this, true);
        }
    }

    public void leaveObserverMode() {
        if (!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING)) {
            return;
        }

        // Очищаем все видимые обьекты
        World.removeObjectsFromPlayer(this, true);

        _observePoint.deleteMe();
        _observePoint = null;

        setTarget(null);
        stopMove();

        // Выходим из режима обсервинга
        sendPacket(new ObserverEnd(getLoc()));
    }

    public void returnFromObserverMode() {
        if (!_observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE)) {
            return;
        }

        // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
        setLastClientPosition(null);
        setLastServerPosition(null);

        standUp();
        setFlying(false);

        broadcastCharInfo();

        World.showObjectsToPlayer(this, false);
    }

    public void enterOlympiadObserverMode(final Location loc, final OlympiadGame game, final Reflection reflect) {
        final WorldRegion observerRegion = World.getRegion(loc);
        if (observerRegion == null) {
            return;
        }

        final OlympiadGame oldGame = getOlympiadObserveGame();
        if (!_observerMode.compareAndSet(oldGame != null ? OBSERVER_STARTED : OBSERVER_NONE, OBSERVER_STARTING)) {
            return;
        }

        setTarget(null);
        stopMove();

        // Очищаем все видимые обьекты
        World.removeObjectsFromPlayer(this, false);

        if (oldGame != null) {
            oldGame.removeSpectator(this);
            sendPacket(ExOlympiadMatchEnd.STATIC);

            _observePoint.decayMe();
        } else {
            // Отображаем надпись над головой
            broadcastCharInfo();

            // Меняем интерфейс
            sendPacket(new ExOlympiadMode(3));

            _observePoint = new ObservePoint(this);
        }

        _observePoint.setLoc(loc);
        _observePoint.setReflection(reflect);

        setOlympiadObserveGame(game);

        // "Телепортируемся"
        sendPacket(new TeleportToLocation(this, loc));
    }

    public void leaveOlympiadObserverMode(final boolean removeFromGame) {
        final OlympiadGame game = getOlympiadObserveGame();
        if (game == null) {
            return;
        }
        if (!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING)) {
            return;
        }

        if (removeFromGame) {
            game.removeSpectator(this);
        }
        setOlympiadObserveGame(null);

        // Очищаем все видимые обьекты
        World.removeObjectsFromPlayer(this, true);

        _observePoint.deleteMe();
        _observePoint = null;

        setTarget(null);
        stopMove();

        // Меняем интерфейс
        sendPacket(new ExOlympiadMode(0));
        sendPacket(ExOlympiadMatchEnd.STATIC);
        updateEffectIcons();

        // "Телепортируемся"
        sendPacket(new TeleportToLocation(this, getLoc()));
    }

    public int getOlympiadSide() {
        return _olympiadSide;
    }

    public void setOlympiadSide(final int i) {
        _olympiadSide = i;
    }

    public boolean isInObserverMode() {
        return getObserverMode() > 0;
    }

    public int getObserverMode() {
        return _observerMode.get();
    }

    public ObservePoint getObservePoint() {
        return _observePoint;
    }

    public int getTeleMode() {
        return _telemode;
    }

    public void setTeleMode(final int mode) {
        _telemode = mode;
    }

    public void setLoto(final int i, final int val) {
        _loto[i] = val;
    }

    public int getLoto(final int i) {
        return _loto[i];
    }

    public void setRace(final int i, final int val) {
        _race[i] = val;
    }

    public int getRace(final int i) {
        return _race[i];
    }

    public boolean getMessageRefusal() {
        return _messageRefusal;
    }

    public void setMessageRefusal(final boolean mode) {
        _messageRefusal = mode;
    }

    public boolean getTradeRefusal() {
        return _tradeRefusal;
    }

    public void setTradeRefusal(final boolean mode) {
        _tradeRefusal = mode;
    }

    public void addToBlockList(final String charName) {
        if (charName == null || charName.equalsIgnoreCase(getName()) || isInBlockList(charName)) {
            // уже в списке
            sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        final Player block_target = World.getPlayer(charName);

        if (block_target != null) {
            if (block_target.isGM()) {
                sendPacket(SystemMsg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
                return;
            }
            _blockList.put(block_target.getObjectId(), block_target.getName());
            sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(block_target.getName()));
            block_target.sendPacket(new SystemMessage(SystemMsg.S1_HAS_PLACED_YOU_ON_HISHER_IGNORE_LIST).addString(getName()));
            return;
        }

        final int charId = CharacterDAO.getInstance().getObjectIdByName(charName);

        if (charId == 0) {
            // чар не существует
            sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        if (GmAccessConfig.gmlist.containsKey(charId) && GmAccessConfig.gmlist.get(charId).IsGM) {
            sendPacket(SystemMsg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
            return;
        }
        _blockList.put(charId, charName);
        sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(charName));
    }

    public void removeFromBlockList(final String charName) {
        int charId = 0;
        for (final int blockId : _blockList.keySet()) {
            if (charName.equalsIgnoreCase(_blockList.get(blockId))) {
                charId = blockId;
                break;
            }
        }
        if (charId == 0) {
            sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_YOUR_IGNORE_LIST);
            return;
        }
        sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST).addString(_blockList.remove(charId)));
        final Player block_target = GameObjectsStorage.getPlayer(charId);
        if (block_target != null) {
            block_target.sendMessage(getName() + " has removed you from his/her Ignore List."); //В системных(619 == 620) мессагах ошибка ;)
        }
    }

    public boolean isInBlockList(final Player player) {
        return isInBlockList(player.getObjectId());
    }

    public boolean isInBlockList(final int charId) {
        return _blockList.containsKey(charId);
    }

    public boolean isInBlockList(final String charName) {
        for (final Entry<Integer, String> entry : _blockList.entrySet()) {
            if (charName.equalsIgnoreCase(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void restoreBlockList() {
        _blockList.clear();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?");
            statement.setInt(1, getObjectId());
            rs = statement.executeQuery();
            while (rs.next()) {
                final int targetId = rs.getInt("target_Id");
                final String name = rs.getString("char_name");
                if (name == null) {
                    continue;
                }
                _blockList.put(targetId, name);
            }
        } catch (final SQLException e) {
            _log.warn("Can't restore player blocklist " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    private void storeBlockList() {
        Connection con = null;
        Statement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + getObjectId());

            if (_blockList.isEmpty()) {
                return;
            }

            final SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");

            synchronized (_blockList) {
                StringBuilder sb;
                for (final Entry<Integer, String> e : _blockList.entrySet()) {
                    sb = new StringBuilder("(");
                    sb.append(getObjectId()).append(',');
                    sb.append(e.getKey()).append(')');
                    b.write(sb.toString());
                }
            }
            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            _log.warn("Can't store player blocklist " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public boolean isBlockAll() {
        return _blockAll;
    }

    public void setBlockAll(final boolean state) {
        _blockAll = state;
    }

    public Collection<String> getBlockList() {
        return _blockList.values();
    }

    public Map<Integer, String> getBlockListMap() {
        return _blockList;
    }

    @Override
    public boolean isHero() {
        return _hero;
    }

    public void setHero(final boolean hero) {
        _hero = hero;
    }

    public void setIsInOlympiadMode(final boolean b) {
        _inOlympiadMode = b;
    }

    public boolean isInOlympiadMode() {
        return _inOlympiadMode;
    }

    public boolean isOlympiadGameStart() {
        return _olympiadGame != null && _olympiadGame.getState() == 1;
    }

    public boolean isOlympiadCompStart() {
        return _olympiadGame != null && _olympiadGame.getState() == 2;
    }

    public void updateNobleSkills() {
        if (isNoble()) {
            if (isClanLeader() && getClan().getCastle() > 0) {
                super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_WYVERN_AEGIS, 1));
            }
            super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_NOBLESSE_BLESSING, 1));
            super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_SUMMON_CP_POTION, 1));
            super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_FORTUNE_OF_NOBLESSE, 1));
            super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_HARMONY_OF_NOBLESSE, 1));
            super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_SYMPHONY_OF_NOBLESSE, 1));
        } else {
            if (LostDreamCustom.addNoblesseBlessing)
                super.addSkill(SkillTable.getInstance().getSkillEntry(Skill.SKILL_NOBLESSE_BLESSING, 1));
            else
                super.removeSkillById(Skill.SKILL_NOBLESSE_BLESSING);
            super.removeSkillById(Skill.SKILL_WYVERN_AEGIS);
            super.removeSkillById(Skill.SKILL_SUMMON_CP_POTION);
            super.removeSkillById(Skill.SKILL_FORTUNE_OF_NOBLESSE);
            super.removeSkillById(Skill.SKILL_HARMONY_OF_NOBLESSE);
            super.removeSkillById(Skill.SKILL_SYMPHONY_OF_NOBLESSE);
        }
    }

    public boolean isNoble() {
        return _noble;
    }

    public void setNoble(final boolean noble) {
        if (noble) //broadcast skill animation: Presentation - Attain Noblesse
        {
            broadcastPacket(new MagicSkillUse(this, this, 6673, 1, 1000, 0));
        }
        _noble = noble;
    }

    public int getSubLevel() {
        return getPlayerClassComponent().isSubClassActive() ? getLevel() : 0;
    }

    /* varka silenos and ketra orc quests related functions */
    public void updateKetraVarka() {
        if (ItemFunctions.getItemCount(this, 7215) > 0) {
            _ketra = 5;
        } else if (ItemFunctions.getItemCount(this, 7214) > 0) {
            _ketra = 4;
        } else if (ItemFunctions.getItemCount(this, 7213) > 0) {
            _ketra = 3;
        } else if (ItemFunctions.getItemCount(this, 7212) > 0) {
            _ketra = 2;
        } else if (ItemFunctions.getItemCount(this, 7211) > 0) {
            _ketra = 1;
        } else if (ItemFunctions.getItemCount(this, 7225) > 0) {
            _varka = 5;
        } else if (ItemFunctions.getItemCount(this, 7224) > 0) {
            _varka = 4;
        } else if (ItemFunctions.getItemCount(this, 7223) > 0) {
            _varka = 3;
        } else if (ItemFunctions.getItemCount(this, 7222) > 0) {
            _varka = 2;
        } else if (ItemFunctions.getItemCount(this, 7221) > 0) {
            _varka = 1;
        } else {
            _varka = 0;
            _ketra = 0;
        }
    }

    public int getVarka() {
        return _varka;
    }

    public int getKetra() {
        return _ketra;
    }

    public void updateRam() {
        if (ItemFunctions.getItemCount(this, 7247) > 0) {
            _ram = 2;
        } else if (ItemFunctions.getItemCount(this, 7246) > 0) {
            _ram = 1;
        } else {
            _ram = 0;
        }
    }

    public int getRam() {
        return _ram;
    }

    public int getPledgeType() {
        return _pledgeType;
    }

    public void setPledgeType(final int typeId) {
        _pledgeType = typeId;
    }

    public int getLvlJoinedAcademy() {
        return _lvlJoinedAcademy;
    }

    public void setLvlJoinedAcademy(final int lvl) {
        _lvlJoinedAcademy = lvl;
    }

    public int getPledgeClass() {
        return _pledgeClass;
    }

    public void updatePledgeClass() {
        final int CLAN_LEVEL = _clan == null ? -1 : _clan.getLevel();
        final boolean IN_ACADEMY = _clan != null && Clan.isAcademy(_pledgeType);
        final boolean IS_GUARD = _clan != null && Clan.isRoyalGuard(_pledgeType);
        final boolean IS_KNIGHT = _clan != null && Clan.isOrderOfKnights(_pledgeType);

        boolean IS_GUARD_CAPTAIN = false, IS_KNIGHT_COMMANDER = false, IS_LEADER = false;

        final SubUnit unit = getSubUnit();
        if (unit != null) {
            final UnitMember unitMember = unit.getUnitMember(getObjectId());
            if (unitMember == null) {
                _log.warn("Player: unitMember null, clan: " + _clan.getClanId() + "; pledgeType: " + unit.getType());
                return;
            }
            IS_GUARD_CAPTAIN = Clan.isRoyalGuard(unitMember.getLeaderOf());
            IS_KNIGHT_COMMANDER = Clan.isOrderOfKnights(unitMember.getLeaderOf());
            IS_LEADER = unitMember.getLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
        }

        switch (CLAN_LEVEL) {
            case -1:
                _pledgeClass = RANK_VAGABOND;
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                if (IS_LEADER) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_VASSAL;
                }
                break;
            case 4:
                if (IS_LEADER) {
                    _pledgeClass = RANK_KNIGHT;
                } else {
                    _pledgeClass = RANK_HEIR;
                }
                break;
            case 5:
                if (IS_LEADER) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else {
                    _pledgeClass = RANK_HEIR;
                }
                break;
            case 6:
                if (IS_LEADER) {
                    _pledgeClass = RANK_BARON;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_KNIGHT;
                }
                break;
            case 7:
                if (IS_LEADER) {
                    _pledgeClass = RANK_COUNT;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_KNIGHT;
                } else if (IS_KNIGHT_COMMANDER) {
                    _pledgeClass = RANK_BARON;
                } else if (IS_KNIGHT) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_WISEMAN;
                }
                break;
            case 8:
                if (IS_LEADER) {
                    _pledgeClass = RANK_MARQUIS;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_COUNT;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (IS_KNIGHT_COMMANDER) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (IS_KNIGHT) {
                    _pledgeClass = RANK_KNIGHT;
                } else {
                    _pledgeClass = RANK_BARON;
                }
                break;
            case 9:
                if (IS_LEADER) {
                    _pledgeClass = RANK_DUKE;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_MARQUIS;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_BARON;
                } else if (IS_KNIGHT_COMMANDER) {
                    _pledgeClass = RANK_COUNT;
                } else if (IS_KNIGHT) {
                    _pledgeClass = RANK_WISEMAN;
                } else {
                    _pledgeClass = RANK_VISCOUNT;
                }
                break;
            case 10:
                if (IS_LEADER) {
                    _pledgeClass = RANK_GRAND_DUKE;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (IS_KNIGHT) {
                    _pledgeClass = RANK_BARON;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_DUKE;
                } else if (IS_KNIGHT_COMMANDER) {
                    _pledgeClass = RANK_MARQUIS;
                } else {
                    _pledgeClass = RANK_COUNT;
                }
                break;
            case 11:
                if (IS_LEADER) {
                    _pledgeClass = RANK_DISTINGUISHED_KING;
                } else if (IN_ACADEMY) {
                    _pledgeClass = RANK_VASSAL;
                } else if (IS_GUARD) {
                    _pledgeClass = RANK_COUNT;
                } else if (IS_KNIGHT) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (IS_GUARD_CAPTAIN) {
                    _pledgeClass = RANK_GRAND_DUKE;
                } else if (IS_KNIGHT_COMMANDER) {
                    _pledgeClass = RANK_DUKE;
                } else {
                    _pledgeClass = RANK_MARQUIS;
                }
                break;
        }

        if (_hero && _pledgeClass < RANK_MARQUIS) {
            _pledgeClass = RANK_MARQUIS;
        } else if (_noble && _pledgeClass < RANK_BARON) {
            _pledgeClass = RANK_BARON;
        }
    }

    public int getPowerGrade() {
        return _powerGrade;
    }

    public void setPowerGrade(final int grade) {
        _powerGrade = grade;
    }

    public int getApprentice() {
        return _apprentice;
    }

    public void setApprentice(final int apprentice) {
        _apprentice = apprentice;
    }

    public int getSponsor() {
        return _clan == null ? 0 : _clan.getAnyMember(getObjectId()).getSponsor();
    }

    public Language getLanguage() {
        if (ExtConfig.ALT_ENABLE_CLIENT_LANGUAGE_CHECK && _connection != null) {
            switch (_connection.getLanguageType()) {
                case 0: // Korea
                case 1: // English
                    return Language.ENGLISH;
                case 2: // Japan
                case 3: // Taiwan
                case 4: // China
                case 5: // Thailand
                case 6: // Philippine
                case 7: // Indonesia
                case 8: // Russia
                    return Language.RUSSIAN;
            }
        } else {
            final String lang = playerVariables.get(PlayerVariables.LANG);
            if (lang == null || "en".equalsIgnoreCase(lang) || "e".equalsIgnoreCase(lang) || "eng".equalsIgnoreCase(lang)) {
                return Language.ENGLISH;
            }
            if ("ru".equalsIgnoreCase(lang) || "r".equalsIgnoreCase(lang) || "rus".equalsIgnoreCase(lang)) {
                return Language.RUSSIAN;
            }
        }
        return Language.ENGLISH;
    }

    public boolean isLangRus() {
        return getLanguage() == Language.RUSSIAN;
    }

    public int isAtWarWith(final Integer id) {
        return _clan == null || !_clan.isAtWarWith(id) ? 0 : 1;
    }

    public int isAtWar() {
        return _clan == null || _clan.isAtWarOrUnderAttack() <= 0 ? 0 : 1;
    }

    public void stopWaterTask() {
        if (_taskWater != null) {
            _taskWater.cancel(false);
            _taskWater = null;
            sendPacket(new SetupGauge(this, SetupGauge.CYAN, 0));
            sendChanges();
        }
    }

    public void startWaterTask() {
        if (isDead()) {
            stopWaterTask();
        } else if (ServerConfig.ALLOW_WATER && _taskWater == null) {
            final int timeinwater = (int) (calcStat(Stats.BREATH, getPlayerTemplateComponent().getPcBreathBonus(), null, null) * 1000L);
            sendPacket(new SetupGauge(this, SetupGauge.CYAN, timeinwater));
            _taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
            sendChanges();
        }
    }

    public void doRevive(final double percent) {
        restoreExp(percent);
        doRevive();
    }

    @Override
    public void doRevive() {
        super.doRevive();
        if (isInWater()) {
            startWaterTask();
        }
        setAgathionRes(false);
        playerVariables.remove(PlayerVariables.LOST_EXP);
        startMountFeedTask();
        updateEffectIcons();
        autoShot();
    }

    public void reviveRequest(final Player reviver, final double percent, final boolean pet) {
        final ReviveAnswerListener reviveAsk = askDialog != null && askDialog.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) askDialog.getValue() : null;
        if (reviveAsk != null) {
            if (reviveAsk.isForPet() == pet && reviveAsk.getPower() >= percent) {
                reviver.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                return;
            }
            if (pet && !reviveAsk.isForPet()) {
                reviver.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
                return;
            }
            if (pet && isDead()) {
                reviver.sendPacket(SystemMsg.YOU_CANNOT_RESURRECT_THE_OWNER_OF_A_PET_WHILE_THEIR_PET_IS_BEING_RESURRECTED);
                return;
            }
        }

        if (pet && getServitor() != null && getServitor().isDead() || !pet && isDead()) {
            final ConfirmDlg pkt = new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0);
            pkt.addName(reviver).addString(Math.round(percent) + " percent");

            ask(pkt, new ReviveAnswerListener(this, percent, pet, 0));
        }
    }

    public void summonCharacterRequest(final Player summoner, final int itemConsumeId, final int itemConsumeCount) {
        final ConfirmDlg cd = new ConfirmDlg(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 30000);
        cd.addName(summoner).addZoneName(summoner.getLoc());

        ask(cd, new SummonAnswerListener(summoner, this, itemConsumeId, itemConsumeCount, 30000));
    }

    public void updateNoChannel(final long time) {
        setNoChannel(time);
        AutoBanDAO.getInstance().banChat(getObjectId(), _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
        sendPacket(new EtcStatusUpdate(this));
    }

    @Override
    public boolean isInBoat() {
        return _boat != null;
    }

    public boolean isAirshipCaptain() {
        return _boat != null && _boat instanceof ClanAirShip && ((ClanAirShip) _boat).getDriver() != null && ((ClanAirShip) _boat).getDriver().getObjectId() == getObjectId();
    }

    public Boat getBoat() {
        return _boat;
    }

    public void setBoat(final Boat boat) {
        _boat = boat;
    }

    public Location getInBoatPosition() {
        return _inBoatPosition;
    }

    public void setInBoatPosition(final Location loc) {
        _inBoatPosition = loc;
    }

    /**
     * Через delay миллисекунд выбросит игрока из игры
     */
    public void startKickTask(final long delayMillis) {
        stopKickTask();
        _kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delayMillis);
    }

    public void stopKickTask() {
        if (_kickTask != null) {
            _kickTask.cancel(false);
            _kickTask = null;
        }
    }

    @Override
    public int getInventoryLimit() {
        return (int) calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
    }

    public int getWarehouseLimit() {
        return (int) calcStat(Stats.STORAGE_LIMIT, 0, null, null);
    }

    public int getTradeLimit() {
        return (int) calcStat(Stats.TRADE_LIMIT, 0, null, null);
    }

    public int getDwarvenRecipeLimit() {
        return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50, null, null) + AllSettingsConfig.ALT_ADD_RECIPES;
    }

    public int getCommonRecipeLimit() {
        return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50, null, null) + AllSettingsConfig.ALT_ADD_RECIPES;
    }

    /**
     * Возвращает тип атакующего элемента
     */
    public Element getAttackElement() {
        return Formulas.getAttackElement(this, null);
    }

    /**
     * Возвращает силу атаки элемента
     *
     * @return значение атаки
     */
    public int getAttack(final Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return (int) calcStat(element.getAttack(), 0., null, null);
    }

    /**
     * Возвращает защиту от элемента
     *
     * @return значение защиты
     */
    public int getDefence(final Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return (int) calcStat(element.getDefence(), 0., null, null);
    }

    public boolean getAndSetLastItemAuctionRequest() {
        if (_lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis()) {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return true;
        } else {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return false;
        }
    }

    @Override
    public int getNpcId() {
        return -2;
    }

    public GameObject getVisibleObject(final int id) {
        if (getObjectId() == id) {
            return this;
        }

        GameObject target = null;

        if (getTargetId() == id) {
            target = getTarget();
        }

        if (target == null && _party != null) {
            for (final Player p : _party.getPartyMembers()) {
                if (p != null && p.getObjectId() == id) {
                    target = p;
                    break;
                }
            }
        }

        if (target == null) {
            target = World.getAroundObjectById(this, id);
        }

        return target == null || target.isInvisible() ? null : target;
    }

    @Override
    public int getPAtk(final Creature target) {
        if (isCommonTransformed()) {
            return (int) calcStat(Stats.POWER_ATTACK, getTransformation().getCommon().base_physical_attack, target, null);
        } else if (isMounted()) {
            final PetData petData = PetDataHolder.getInstance().getPetData(_mountNpcId);
            if (petData != null) {
                return (int) calcStat(Stats.POWER_ATTACK, petData.getLevelStatForLevel(_mountLevel).getPAttackOnRide(), target, null);
            }
        }
        final double init = getActiveWeaponInstance() == null ? getPlayerTemplateComponent().getBasePhysicalAttack() : 0;
        return (int) calcStat(Stats.POWER_ATTACK, init, target, null);
    }

    @Override
    public int getMAtk(final Creature target, final SkillEntry skill) {
        if (isCommonTransformed()) {
            return (int) calcStat(Stats.MAGIC_ATTACK, getTransformation().getCommon().base_magical_attack, target, null);
        } else if (isMounted()) {
            final PetData petData = PetDataHolder.getInstance().getPetData(_mountNpcId);
            if (petData != null) {
                return (int) calcStat(Stats.MAGIC_ATTACK, petData.getLevelStatForLevel(_mountLevel).getMAttackOnRide(), target, skill);
            }
        }
        if (skill != null && skill.getTemplate().getMatak() > 0) {
            return skill.getTemplate().getMatak();
        }
        final double init = getActiveWeaponInstance() == null ? getPlayerTemplateComponent().getBaseMagicAttack() : 0;
        return (int) calcStat(Stats.MAGIC_ATTACK, init, target, skill);
    }

    @Override
    public int getPDef(final Creature target) {
        int init = 0; //double init = 4.; //empty cloak and underwear slots
        final Inventory inventory = getInventory();
        final ItemInstance chest = inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if (chest == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[0] : getPlayerTemplateComponent().getBaseDefend()[0];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null && (chest == null || chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR)) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[1] : getPlayerTemplateComponent().getBaseDefend()[1];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[2] : getPlayerTemplateComponent().getBaseDefend()[2];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_FEET) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[3] : getPlayerTemplateComponent().getBaseDefend()[3];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[4] : getPlayerTemplateComponent().getBaseDefend()[4];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_UNDER) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[5] : getPlayerTemplateComponent().getBaseDefend()[5];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_BACK) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_defend[6] : getPlayerTemplateComponent().getBaseDefend()[6];
        }

        return (int) calcStat(Stats.POWER_DEFENCE, init, target, null);
    }

    @Override
    public int getMDef(final Creature target, final SkillEntry skill) {
        int init = 0; //double init = 0.;
        final Inventory inventory = getInventory();
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_REAR) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_magic_defend[0] : getPlayerTemplateComponent().getBaseMagicDefend()[0];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_magic_defend[1] : getPlayerTemplateComponent().getBaseMagicDefend()[1];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_magic_defend[2] : getPlayerTemplateComponent().getBaseMagicDefend()[2];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_magic_defend[3] : getPlayerTemplateComponent().getBaseMagicDefend()[3];
        }
        if (inventory.getPaperdollItem(Inventory.PAPERDOLL_NECK) == null) {
            init += isCombatTransformed() ? getTransformation().getCombat().base_magic_defend[4] : getPlayerTemplateComponent().getBaseMagicDefend()[4];
        }

        return (int) calcStat(Stats.MAGIC_DEFENCE, init, target, skill);
    }

    @Override
    public int getBaseAtkRange() {
        if (isCommonTransformed()) {
            return getTransformation().getCommon().base_attack_range;
        }
        return getPlayerTemplateComponent().getBaseAttackRange();
    }

    @Override
    public int getCriticalHit(Creature target, SkillEntry skill) {
        if (isCommonTransformed()) {
            return (int) calcStat(Stats.CRITICAL_BASE, getTransformation().getCommon().base_critical_prob, target, skill);
        }
        return (int) calcStat(Stats.CRITICAL_BASE, getPlayerTemplateComponent().getBaseCriticalRate(), target, skill);
    }

    @Override
    public int getRunSpeed() {
        if (isCommonTransformed()) {
            return getSpeed((int) getTransformation().getCommon().moving_speed[1]);
        }
        return getSpeed((int) getPlayerTemplateComponent().getMovingSpeed()[1]);
    }

    @Override
    public int getWalkSpeed() {
        if (isCommonTransformed()) {
            return getSpeed((int) getTransformation().getCommon().moving_speed[0]);
        }
        return getSpeed((int) getPlayerTemplateComponent().getMovingSpeed()[0]);
    }

    @Override
    public double getMovementSpeedMultiplier() {
        if (isRunning()) {
            return getRunSpeed() * 1. / getPlayerTemplateComponent().getMovingSpeed()[1];
        }
        return getWalkSpeed() * 1. / getPlayerTemplateComponent().getMovingSpeed()[0];
    }

    public double getSwimSpeedMultiplier() {
        return getWalkSpeed() * 1. / getPlayerTemplateComponent().getMovingSpeed()[1];
    }

    @Override
    public int getSwimRunSpeed() {
        return (int) calcStat(Stats.RUN_SPEED, getPlayerTemplateComponent().getMovingSpeed()[3], null, null);
    }

    @Override
    public int getSwimWalkSpeed() {
        return (int) calcStat(Stats.RUN_SPEED, getPlayerTemplateComponent().getMovingSpeed()[2], null, null);
    }

    @Override
    public int getMAtkSpd() {
        if (isCommonTransformed()) {
            final double speed = getTransformation().getCommon().base_attack_speed + 33;
            return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, isMageClass() ? (speed / 2) : speed, null, null);
        }
        final double speed = getPlayerTemplateComponent().getBaseAttackSpeed() + 33;
        return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, isMageClass() ? (speed / 2) : speed, null, null);
    }

    @Override
    public int getPAtkSpd() {
        if (isMounted()) {
            final PetData petData = PetDataHolder.getInstance().getPetData(_mountNpcId);
            if (petData != null) {
                return (int) calcStat(Stats.POWER_ATTACK_SPEED, petData.getLevelStatForLevel(_mountLevel).getAttackSpeedOnRide(), null, null);
            }
        } else if (getActiveWeaponItem() != null) {
            if (isCommonTransformed()) {
                final int init = (int) calcStat(Stats.ATK_BASE, getTransformation().getCommon().base_attack_speed, null, null);
                return (int) calcStat(Stats.POWER_ATTACK_SPEED, init, null, null);
            } else {
                final int init = (int) calcStat(Stats.ATK_BASE, getPlayerTemplateComponent().getBaseAttackSpeed(), null, null);
                return (int) calcStat(Stats.POWER_ATTACK_SPEED, init, null, null);
            }
        } else if (isCommonTransformed()) {
            return (int) calcStat(Stats.POWER_ATTACK_SPEED, getTransformation().getCommon().base_attack_speed, null, null);
        }
        return (int) calcStat(Stats.POWER_ATTACK_SPEED, getPlayerTemplateComponent().getBaseAttackSpeed(), null, null);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public boolean isCursedWeaponEquipped() {
        return _cursedWeaponEquippedId != 0;
    }

    public int getCursedWeaponEquippedId() {
        return _cursedWeaponEquippedId;
    }

    public void setCursedWeaponEquippedId(final int value) {
        _cursedWeaponEquippedId = value;
    }

    @Override
    public boolean isImmobilized() {
        return super.isImmobilized() || isOverloaded() || isSitting() || isFishing();
    }

    @Override
    public boolean isBlocked() {
        return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted();
    }

    @Override
    public boolean isInvul() {
        return super.isInvul() || isInMovie();
    }

    public boolean isOverloaded() {
        return _overloaded;
    }

    /**
     * if True, the L2Player can't take more item
     */
    public void setOverloaded(final boolean overloaded) {
        _overloaded = overloaded;
    }

    public boolean isFishing() {
        return _isFishing;
    }

    public Fishing getFishing() {
        return _fishing;
    }

    public void setFishing(final boolean value) {
        _isFishing = value;
    }

    public void startFishing(final FishTemplate fish, final LureTemplate lureId) {
        _fishing.setFish(fish);
        _fishing.setLureId(lureId);
        _fishing.startFishing();
    }

    public void stopFishing() {
        _fishing.stopFishing();
    }

    public Location getFishLoc() {
        return _fishing.getFishLoc();
    }

    @Override
    public double getRateAdena() {
        return _party == null ? getPremiumAccountComponent().getPremiumBonus().getDropAdena() : _party.rateAdena;
    }

    @Override
    public double getRateItems() {
        return _party == null ? getPremiumAccountComponent().getPremiumBonus().getDropItems() : _party.rateDrop;
    }

    @Override
    public double getRateExp() {
        return calcStat(Stats.EXP, (_party == null ? getPremiumAccountComponent().getPremiumBonus().getRateXp() : _party.rateExp), null, null);
    }

    @Override
    public double getRateSp() {
        return calcStat(Stats.SP, (_party == null ? getPremiumAccountComponent().getPremiumBonus().getRateSp() : _party.rateSp), null, null);
    }

    @Override
    public double getRateSpoil() {
        return _party == null ? getPremiumAccountComponent().getPremiumBonus().getDropSpoil() : _party.rateSpoil;
    }

    public boolean isMaried() {
        return _maried;
    }

    public void setMaried(final boolean state) {
        _maried = state;
    }

    public boolean isMaryRequest() {
        return _maryrequest;
    }

    public void setMaryRequest(final boolean state) {
        _maryrequest = state;
    }

    public boolean isMaryAccepted() {
        return _maryaccepted;
    }

    public void setMaryAccepted(final boolean state) {
        _maryaccepted = state;
    }

    public int getPartnerId() {
        return _partnerId;
    }

    public void setPartnerId(final int partnerid) {
        _partnerId = partnerid;
    }

    public int getCoupleId() {
        return _coupleId;
    }

    public void setCoupleId(final int coupleId) {
        _coupleId = coupleId;
    }

    /**
     * Сброс реюза всех скилов персонажа.
     */
    public void resetReuse() {
        skillReuses.clear();
        sharedGroupReuses.clear();
    }

    public DeathPenalty getDeathPenalty() {
        return getPlayerClassComponent().getActiveClass() == null ? null : getPlayerClassComponent().getActiveClass().getDeathPenalty(this);
    }

    public boolean isCharmOfCourage() {
        return _charmOfCourage;
    }

    public void setCharmOfCourage(final boolean val) {
        _charmOfCourage = val;

        if (!val) {
            getEffectList().stopEffect(Skill.SKILL_CHARM_OF_COURAGE);
        }

        sendEtcStatusUpdate();
    }

    @Override
    public int getIncreasedForce() {
        return _increasedForce;
    }

    @Override
    public void setIncreasedForce(int i) {
        i = Math.min(i, EffectCharge.MAX_CHARGE);
        i = Math.max(i, 0);

        if (i != 0 && i > _increasedForce) {
            sendPacket(new SystemMessage(SystemMsg.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1).addNumber(i));
        }

        _increasedForce = i;
        sendEtcStatusUpdate();
    }

    @Override
    public int getConsumedSouls() {
        return _consumedSouls;
    }

    @Override
    public void setConsumedSouls(int i, final NpcInstance monster) {
        if (i == _consumedSouls) {
            return;
        }

        final int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

        if (i > max) {
            i = max;
        }

        if (i <= 0) {
            _consumedSouls = 0;
            sendEtcStatusUpdate();
            return;
        }

        if (_consumedSouls != i) {
            final int diff = i - _consumedSouls;
            if (diff > 0) {
                final SystemMessage sm = new SystemMessage(SystemMsg.YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_ITS_NOW_S2);
                sm.addNumber(diff);
                sm.addNumber(i);
                sendPacket(sm);
            }
        } else if (max == i) {
            sendPacket(SystemMsg.SOUL_CANNOT_BE_INCREASED_ANYMORE);
            return;
        }

        _consumedSouls = i;
        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean isFalling() {
        return System.currentTimeMillis() - _lastFalling < 5000;
    }

    public void falling(final int height) {
        if (!ServerConfig.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat()) {
            return;
        }
        _lastFalling = System.currentTimeMillis();
        final int damage = (int) calcStat(Stats.FALL, getMaxHp() / 2000 * height, null, null);
        if (damage > 0) {
            final int curHp = (int) getCurrentHp();
            if (curHp - damage < 1) {
                setCurrentHp(1, false);
            } else {
                setCurrentHp(curHp - damage, false);
            }
            sendPacket(new SystemMessage(SystemMsg.YOU_RECEIVED_S1_FALLING_DAMAGE).addNumber(damage));
        }
    }

    /**
     * Системные сообщения о текущем состоянии хп
     * TODO: DS: переделать весь этот бред
     */
    @Override
    public void checkHpMessages(final double curHp, final double newHp) {
        //сюда пасивные скиллы
        final int[] hp = {
                30,
                30
        };
        final int[] skills = {
                290,
                291
        };

        //сюда активные эффекты
        final int[] effects_skills_id = {
                139,
                176,
                292,
                292
        };
        final int[] effects_hp = {
                30,
                30,
                30,
                60
        };

        final double percent = getMaxHp() / 100.0d;
        final double curHpPercent = curHp / percent;
        final double newHpPercent = newHp / percent;
        boolean needsUpdate = false;
        int hpcalc;

        //check for passive skills
        for (int i = 0; i < skills.length; i++) {
            final int level = getSkillLevel(skills[i]);
            if (level > 0) {
                hpcalc = hp[i];
                if (curHpPercent > hpcalc) {
                    if (newHpPercent <= hpcalc) {
                        sendPacket(new SystemMessage(SystemMsg.SINCE_YOUR_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
                        needsUpdate = true;
                    }
                } else if (newHpPercent > hpcalc) {
                    sendPacket(new SystemMessage(SystemMsg.SINCE_YOUR_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
                    needsUpdate = true;
                }
            }
        }

        //check for active effects
        for (int i = 0; i < effects_skills_id.length; i++) {
            if (getEffectList().getEffectsBySkillId(effects_skills_id[i]) != null) {
                hpcalc = effects_hp[i];
                if (curHpPercent > hpcalc) {
                    if (newHpPercent <= hpcalc) {
                        sendPacket(new SystemMessage(SystemMsg.SINCE_YOUR_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(effects_skills_id[i], 1));
                        needsUpdate = true;
                    }
                } else if (newHpPercent > hpcalc) {
                    sendPacket(new SystemMessage(SystemMsg.SINCE_YOUR_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(effects_skills_id[i], 1));
                    needsUpdate = true;
                }
            }
        }

        if (needsUpdate) {
            sendChanges();
        }
    }

    /**
     * Системные сообщения для темных эльфов о вкл/выкл ShadowSence (skill id = 294)
     */
    public void checkDayNightMessages() {
        final int level = getSkillLevel(294);
        if (level > 0) {
            if (GameTimeManager.getInstance().isNowNight()) {
                sendPacket(new SystemMessage(SystemMsg.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
            } else {
                sendPacket(new SystemMessage(SystemMsg.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
            }
        }
        sendChanges();
    }

    //TODO [G1ta0] переработать в лисенер?
    @Override
    protected void onUpdateZones(final List<Zone> leaving, final List<Zone> entering) {
        super.onUpdateZones(leaving, entering);

        if ((leaving == null || leaving.isEmpty()) && (entering == null || entering.isEmpty())) {
            return;
        }

        final boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
        final boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
        final boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
        final boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
        //FIXME G1ta0 boolean lastInSSQZone = (_zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG;

        final boolean isInCombatZone = isInCombatZone();
        final boolean isInDangerArea = isInDangerArea();
        final boolean isOnSiegeField = isOnSiegeField();
        final boolean isInPeaceZone = isInPeaceZone();
        final boolean isInSSQZone = isInSSQZone();

        // обновляем компас, только если персонаж в мире
        final int lastZoneMask = _zoneMask;
        _zoneMask = 0;

        if (isInCombatZone) {
            _zoneMask |= ZONE_PVP_FLAG;
        }
        if (isInDangerArea) {
            _zoneMask |= ZONE_ALTERED_FLAG;
        }
        if (isOnSiegeField) {
            _zoneMask |= ZONE_SIEGE_FLAG;
        }
        if (isInPeaceZone) {
            _zoneMask |= ZONE_PEACE_FLAG;
        }
        if (isInSSQZone) {
            _zoneMask |= ZONE_SSQ_FLAG;
        }

        if (lastZoneMask != _zoneMask) {
            sendPacket(new ExSetCompassZoneCode(this));
        }

        if (lastInCombatZone != isInCombatZone) {
            broadcastRelationChanged();
        }

        if (lastInDangerArea != isInDangerArea) {
            sendPacket(new EtcStatusUpdate(this));
        }

        if (lastOnSiegeField != isOnSiegeField) {
            broadcastRelationChanged();
            if (isOnSiegeField) {
                sendPacket(SystemMsg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            } else {
                sendPacket(SystemMsg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                if (!isTeleporting() && getPvpFlag() == 0) {
                    startPvPFlag(null);
                }
            }
        }

        if (lastInPeaceZone != isInPeaceZone) {
            if (isInPeaceZone) {
                getRecommendationComponent().setRecomTimerActive(false);
                if (getNevitComponent().isActive()) {
                    getNevitComponent().stopAdventTask(true);
                }
                getVitalityComponent().startVitalityTask();
            } else {
                getVitalityComponent().stopVitalityTask();
            }
        }

        if (isInWater()) {
            startWaterTask();
        } else {
            stopWaterTask();
        }
    }

    public void startAutoSaveTask() {
        if (!ServerConfig.AUTOSAVE) {
            return;
        }
        if (_autoSaveTask == null) {
            _autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
        }
    }

    public void stopAutoSaveTask() {
        if (_autoSaveTask != null) {
            _autoSaveTask.cancel(false);
        }
        _autoSaveTask = null;
    }

    public void startUnjailTask(final Player player, final int time) {
        if (_unjailTask != null) {
            _unjailTask.cancel(false);
        }
        _unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), time * 60000L);
    }

    public void stopUnjailTask() {
        if (_unjailTask != null) {
            _unjailTask.cancel(false);
        }
        _unjailTask = null;
    }

    @Override
    public void sendMessage(final String message) {
        sendPacket(new SystemMessage(SystemMsg.S1).addString(message));
    }

    public void sendAdminMessage(final String message) {
        sendPacket(new Say2(0, ChatType.ALL, "SYS", NpcString.NONE, message));
    }

    public void sendHTMLMessage(final String message) {
        sendPacket(new Say2(0, ChatType.ALL, "HTML", NpcString.NONE, message));
    }

    public void sendDebugMessage(final String message) {
        sendPacket(new Say2(0, ChatType.ALL, "BUG", NpcString.NONE, message));
    }

    public int getUseSeed() {
        return _useSeed;
    }

    public void setUseSeed(final int id) {
        _useSeed = id;
    }

    public int getRelation(final Player target) {
        int result = 0;

        if (getClan() != null) {
            result |= RelationChanged.RELATION_CLAN_MEMBER;
            if (getClan() == target.getClan())
                result |= RelationChanged.RELATION_CLAN_MATE;
            if (getClan().getAllyId() != 0)
                result |= RelationChanged.RELATION_ALLY_MEMBER;
        }

        if (isClanLeader())
            result |= RelationChanged.RELATION_LEADER;

        final Party party = getParty();
        if (party != null && party == target.getParty()) {
            result |= RelationChanged.RELATION_HAS_PARTY;

            switch (party.getPartyMembers().indexOf(this)) {
                case 0:
                    result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
                    break;
                case 1:
                    result |= RelationChanged.RELATION_PARTY4; // 0x8
                    break;
                case 2:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
                    break;
                case 3:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
                    break;
                case 4:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
                    break;
                case 5:
                    result |= RelationChanged.RELATION_PARTY3; // 0x4
                    break;
                case 6:
                    result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
                    break;
                case 7:
                    result |= RelationChanged.RELATION_PARTY2; // 0x2
                    break;
                case 8:
                    result |= RelationChanged.RELATION_PARTY1; // 0x1
                    break;
            }
        }

        final Clan clan1 = getClan();
        Clan clan2 = target.getClan();
        if (clan1 != null && clan2 != null) {
            if (target.getPledgeType() != Clan.SUBUNIT_ACADEMY && getPledgeType() != Clan.SUBUNIT_ACADEMY) {
                if (clan2.isAtWarWith(clan1.getClanId())) {
                    result |= RelationChanged.RELATION_1SIDED_WAR;
                    if (clan1.isAtWarWith(clan2.getClanId()))
                        result |= RelationChanged.RELATION_MUTUAL_WAR;
                }
            }
            if (getBlockCheckerArena() != -1) {
                result |= RelationChanged.RELATION_IN_SIEGE;
                ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
                if (holder.getPlayerTeam(this) == 0)
                    result |= RelationChanged.RELATION_ENEMY;
                else
                    result |= RelationChanged.RELATION_ALLY;
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }

        for (final Event e : getEvents()) {
            result = e.getRelation(this, target, result);
        }

        return result;
    }

    public long getlastPvpAttack() {
        return _lastPvpAttack;
    }

    @Override
    public void startPvPFlag(final Creature target) {
        if (karma > 0) {
            return;
        }

        long startTime = System.currentTimeMillis();
        if (target != null && target.getPvpFlag() != 0) {
            startTime -= PvpConfig.PVP_TIME / 2;
        }
        if (_pvpFlag != 0 && _lastPvpAttack > startTime) {
            return;
        }

        _lastPvpAttack = startTime;

        updatePvPFlag(1);

        if (_PvPRegTask == null) {
            _PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
        }
    }

    public void stopPvPFlag() {
        if (_PvPRegTask != null) {
            _PvPRegTask.cancel(false);
            _PvPRegTask = null;
        }
        updatePvPFlag(0);
    }

    public void updatePvPFlag(final int value) {
        if (_handysBlockCheckerEventArena != -1) {
            return;
        }
        if (_pvpFlag == value) {
            return;
        }

        setPvpFlag(value);

        sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);

        broadcastRelationChanged();
    }

    @Override
    public int getPvpFlag() {
        return _pvpFlag;
    }

    public void setPvpFlag(final int pvpFlag) {
        _pvpFlag = pvpFlag;
    }

    public boolean isInDuel() {
        return getEvent(DuelEvent.class) != null;
    }

    public List<NpcInstance> getTamedBeasts() {
        return _tamedBeasts;
    }

    public void addTamedBeast(final NpcInstance tamedBeast) {
        if (_tamedBeasts.equals(Collections.<NpcInstance>emptyList())) {
            _tamedBeasts = new CopyOnWriteArrayList<>();
        }

        _tamedBeasts.add(tamedBeast);
    }

    public void removeTamedBeast(final NpcInstance b) {
        _tamedBeasts.remove(b);
    }

    public long getLastAttackPacket() {
        return _lastAttackPacket;
    }

    public void setLastAttackPacket() {
        _lastAttackPacket = System.currentTimeMillis();
    }

    public long getLastMovePacket() {
        return _lastMovePacket;
    }

    public void setLastMovePacket() {
        _lastMovePacket = System.currentTimeMillis();
    }

    public double getLastMovePacketDestinationDiff() {
        if (lastMovePacketDestinationDiff == null)
            return Double.MIN_VALUE;

        return lastMovePacketDestinationDiff.get();
    }

    public void setLastMovePacketDestinationDiff(final double lastMovePacketDestinationDiff) {
        this.lastMovePacketDestinationDiff = new ExpirationReference<>(lastMovePacketDestinationDiff, Double.MIN_VALUE, ServerConfig.MOVE_PACKET_DELAY);
    }

    public byte[] getKeyBindings() {
        return _keyBindings;
    }

    public void setKeyBindings(byte[] keyBindings) {
        if (keyBindings == null) {
            keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        _keyBindings = keyBindings;
    }

    /**
     * Возвращает трансформацию
     *
     * @return Transformation трансформации
     */
    public TransformComponent getTransformation() {
        return transformation;
    }

    public void setTransformation(final TransformData transformdata) {
        transformation = TransformComponent.createStartTransform(this, transformdata);
        transformListener = new PlayerEnterLeaveTransformListener();
        addListener(transformListener);
        getListeners().onEnterTranform(transformation);
        final int sum_id = transformation.getId();
        if (sum_id == 221 || sum_id == 219 || sum_id == 220 || sum_id == 259) {
            Servitor servitor = getServitor();
            if (servitor != null) {
                servitor.unSummon(true, true);
            }
        }
        sendPacket(new ExBasicActionList(transformation.getCommon().action));
        sendPacket(new SkillList(this));
        sendPacket(new ShortCutInit(this));
        for (final int shotId : this.getAutoSoulShot()) {
            sendPacket(new ExAutoSoulShot(shotId, true));
        }
        updateStats();
        broadcastCharInfo();
    }

    public int getTransformationId() {
        return isTransformed() ? getTransformation().getId() : 0;
    }

    public void stopTransformation() {
        stopTransformation(true);
    }

    public void stopTransformation(final boolean stopEffect) {
        if (isTransformed()) {
            final Effect effect = getEffectList().getEffectByType(EffectType.p_transform);
            if (stopEffect && effect != null) {
                effect.exit();
            } else {
                transformation.stopTransform();
                transformation = null;
                sendPacket(new ExBasicActionList());
                sendPacket(new SkillList(this));
                updateStats();
                broadcastCharInfo();
                getListeners().onLeaveTranform();
                removeListener(transformListener);
            }
        }
    }

    /**
     * В Трансформации ли Чар?
     */
    public boolean isTransformed() {
        return transformation != null;
    }

    public boolean isCombatTransformed() {
        if (isTransformed()) {
            final TransformType type = getTransformation().getData().type;
            return type == TransformType.COMBAT || type == TransformType.CURSED || type == TransformType.FLYING || type == TransformType.PURE_STAT;
        }
        return false;
    }

    public boolean isCommonTransformed() {
        return isTransformed() && getTransformation().getData().type != TransformType.MODE_CHANGE;
    }

    public boolean isInFlyingTransform() {
        return isTransformed() && getTransformation().getData().type == TransformType.FLYING;
    }

    public boolean isInRidingTransform() {
        return isTransformed() && getTransformation().getData().type == TransformType.RIDING_MODE;
    }

    /**
     * Возвращает коллекцию скиллов, с учетом текущей трансформации
     */
    @Override
    public Collection<SkillEntry> getAllSkills() {
        if (isTransformed()) {
            return getTransformation().getAllSkills();
        }
        return super.getAllSkills();
    }

    /**
     * Возвращает коллекцию скиллов, без учета трансформации
     */
    public Collection<SkillEntry> getSkills() {
        return skills.values();
    }

    public Location getGroundSkillLoc() {
        return _groundSkillLoc;
    }

    public void setGroundSkillLoc(final Location location) {
        _groundSkillLoc = location;
    }

    /**
     * Персонаж в процессе выхода из игры
     *
     * @return возвращает true если процесс выхода уже начался
     */
    public boolean isLogoutStarted() {
        return _isLogout.get();
    }

    public boolean setMiniMapOpened(final boolean value, final boolean valueNext) {
        return _isMiniMapOpened.compareAndSet(value, valueNext);
    }

    public boolean isMiniMapOpened() {
        return _isMiniMapOpened.get();
    }

    public void setOfflineMode(final boolean val) {
        if (!val) {
            playerVariables.remove(PlayerVariables.OFFLINE);
        }
        offline = val;
    }

    public boolean isInOfflineMode() {
        return offline;
    }

    public void saveTradeList() {
        final StringBuilder stringBuilder = new StringBuilder();

        if (sellList == null || sellList.isEmpty()) {
            playerVariables.remove(PlayerVariables.SELL_LIST);
        } else {
            for (final TradeItem i : sellList) {
                stringBuilder.append(i.getObjectId()).append(';').append(i.getCount()).append(';').append(i.getOwnersPrice()).append(':');
            }
            playerVariables.set(PlayerVariables.SELL_LIST, stringBuilder.toString(), -1);

            stringBuilder.setLength(0);

            if (getSellStoreName() != null) {
                playerVariables.set(PlayerVariables.SELL_STORE_NAME, getSellStoreName(), -1);
            }
        }

        if (packageSellList == null || packageSellList.isEmpty()) {
            playerVariables.remove(PlayerVariables.PACKAGE_SELL_LIST);
        } else {
            for (final TradeItem i : packageSellList) {
                stringBuilder.append(i.getObjectId()).append(';').append(i.getCount()).append(';').append(i.getOwnersPrice()).append(':');
            }
            playerVariables.set(PlayerVariables.PACKAGE_SELL_LIST, stringBuilder.toString(), -1);

            stringBuilder.setLength(0);

            if (getSellStoreName() != null) {
                playerVariables.set(PlayerVariables.SELL_STORE_NAME, getSellStoreName(), -1);
            }
        }

        if (buyList == null || buyList.isEmpty()) {
            playerVariables.remove(PlayerVariables.BUY_LIST);
        } else {
            for (final TradeItem i : buyList) {
                stringBuilder.append(i.getItemId()).append(';').append(i.getCount()).append(';').append(i.getOwnersPrice()).append(';').append(i.getEnchantLevel()).append(':');
            }
            playerVariables.set(PlayerVariables.BUY_LIST, stringBuilder.toString(), -1);

            stringBuilder.setLength(0);

            if (getBuyStoreName() != null) {
                playerVariables.set(PlayerVariables.BUY_STORE_NAME, getBuyStoreName(), -1);
            }
        }

        if (createList == null || createList.isEmpty()) {
            playerVariables.remove(PlayerVariables.CREATE_LIST);
        } else {
            for (final ManufactureItem i : createList) {
                stringBuilder.append(i.getRecipeId()).append(';').append(i.getCost()).append(':');
            }
            playerVariables.set(PlayerVariables.CREATE_LIST, stringBuilder.toString(), -1);
            if (getManufactureName() != null) {
                playerVariables.set(PlayerVariables.MANUFACTURE_NAME, getManufactureName(), -1);
            }
        }

        stringBuilder.setLength(0);
    }

    public void restoreTradeList() {
        String var;
        var = playerVariables.get(PlayerVariables.SELL_LIST);
        if (var != null) {
            sellList = new CopyOnWriteArrayList<>();
            final String[] items = var.split(":");
            for (final String item : items) {
                if (item != null && item.isEmpty()) {
                    continue;
                }
                final String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }

                final int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                final long price = Long.parseLong(values[2]);

                final ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if (count < 1 || itemToSell == null) {
                    continue;
                }

                if (count > itemToSell.getCount()) {
                    count = itemToSell.getCount();
                }

                final TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                sellList.add(i);
            }
            var = playerVariables.get(PlayerVariables.SELL_STORE_NAME);
            if (var != null) {
                setSellStoreName(var);
            }
        }
        var = playerVariables.get(PlayerVariables.PACKAGE_SELL_LIST);
        if (var != null) {
            packageSellList = new CopyOnWriteArrayList<>();
            final String[] items = var.split(":");
            for (final String item : items) {
                if (item != null && item.isEmpty()) {
                    continue;
                }
                final String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }

                final int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                final long price = Long.parseLong(values[2]);

                final ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if (count < 1 || itemToSell == null) {
                    continue;
                }

                if (count > itemToSell.getCount()) {
                    count = itemToSell.getCount();
                }

                final TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                packageSellList.add(i);
            }
            var = playerVariables.get(PlayerVariables.SELL_STORE_NAME);
            if (var != null) {
                setSellStoreName(var);
            }
        }
        var = playerVariables.get(PlayerVariables.BUY_LIST);
        if (var != null) {
            buyList = new CopyOnWriteArrayList<>();
            final String[] items = var.split(":");
            for (final String item : items) {
                if (item != null && item.isEmpty()) {
                    continue;
                }
                final String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }
                final TradeItem i = new TradeItem();
                i.setItemId(Integer.parseInt(values[0]));
                i.setCount(Long.parseLong(values[1]));
                i.setOwnersPrice(Long.parseLong(values[2]));
                if (values.length == 4)
                    i.setEnchantLevel(Integer.parseInt(values[3]));
                buyList.add(i);
            }
            var = playerVariables.get(PlayerVariables.BUY_STORE_NAME);
            if (var != null) {
                setBuyStoreName(var);
            }
        }
        var = playerVariables.get(PlayerVariables.CREATE_LIST);
        if (var != null) {
            createList = new CopyOnWriteArrayList<>();
            final String[] items = var.split(":");
            final RecipeComponent recipeComponent = getRecipeComponent();
            for (final String item : items) {
                if (item != null && item.isEmpty()) {
                    continue;
                }
                final String[] values = item.split(";");
                if (values.length < 2) {
                    continue;
                }
                final int recId = Integer.parseInt(values[0]);
                final long price = Long.parseLong(values[1]);
                if (recipeComponent.findRecipe(recId)) {
                    createList.add(new ManufactureItem(recId, price));
                }
            }
            var = playerVariables.get(PlayerVariables.MANUFACTURE_NAME);
            if (var != null) {
                setManufactureName(var);
            }
        }
    }

    public DecoyInstance getDecoy() {
        return _decoy;
    }

    public void setDecoy(final DecoyInstance decoy) {
        _decoy = decoy;
    }

    public int getMountType() {
        switch (getMountNpcId()) {
            case PetId.STRIDER_WIND_ID:
            case PetId.STRIDER_STAR_ID:
            case PetId.STRIDER_TWILIGHT_ID:
            case PetId.RED_STRIDER_WIND_ID:
            case PetId.RED_STRIDER_STAR_ID:
            case PetId.RED_STRIDER_TWILIGHT_ID:
            case PetId.GUARDIANS_STRIDER_ID:
                return 1;
            case PetId.WYVERN_ID:
                return 2;
            case PetId.WGREAT_WOLF_ID:
            case PetId.FENRIR_WOLF_ID:
            case PetId.WFENRIR_WOLF_ID:
                return 3;
        }
        return 0;
    }

    @Override
    public double getColRadius() {
        if (isCommonTransformed()) {
            return getTransformation().getCommon().collision_box[0];
        } else if (isMounted()) {
            final int mountTemplate = getMountNpcId();
            if (mountTemplate != 0) {
                final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
                if (mountNpcTemplate != null) {
                    return mountNpcTemplate.getCollisionRadius();
                }
            }
        }
        return getPlayerTemplateComponent().getPcCollisionBox()[0];
    }

    @Override
    public double getColHeight() {
        if (isCommonTransformed()) {
            return getTransformation().getCommon().collision_box[1];
        } else if (isMounted()) {
            final int mountTemplate = getMountNpcId();
            if (mountTemplate != 0) {
                final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
                if (mountNpcTemplate != null) {
                    return mountNpcTemplate.getCollisionHeight();
                }
            }
        }
        return getPlayerTemplateComponent().getPcCollisionBox()[1];
    }

    @Override
    public void setReflection(final Reflection reflection) {
        if (getReflection() == reflection) {
            return;
        }

        super.setReflection(reflection);

        if (_summon != null && !_summon.isDead()) {
            _summon.setReflection(reflection);
        }

        if (reflection != ReflectionManager.DEFAULT && !isInOlympiadMode()) {
            final String var = playerVariables.get(PlayerVariables.REFLECTION);
            if (var == null || !var.equals(String.valueOf(reflection.getId()))) {
                playerVariables.set(PlayerVariables.REFLECTION, String.valueOf(reflection.getId()), -1);
            }
        } else {
            playerVariables.remove(PlayerVariables.REFLECTION);
        }

        if (getPlayerClassComponent().getActiveClass() != null) {
            getInventory().validateItems();
            // Для квеста _129_PailakaDevilsLegacy FIXME[K] - убрать отсюда нахер!
            if (getServitor() != null && (getServitor().getNpcId() == 14916 || getServitor().getNpcId() == 14917)) {
                getServitor().unSummon(false, false);
            }
        }
    }

    public boolean isTerritoryFlagEquipped() {
        final ItemInstance weapon = getActiveWeaponInstance();
        return weapon != null && weapon.getTemplate().isTerritoryFlag();
    }

    public int getBuyListId() {
        return _buyListId;
    }

    public void setBuyListId(final int listId) {
        _buyListId = listId;
    }

    public int getFame() {
        return _fame;
    }

    private void setFame(final int fame) {
        _fame = fame;
    }

    public void setFame(int fame, final String log) {
        fame = Math.min(FormulasConfig.LIM_FAME, fame);
        if (log != null && !log.isEmpty()) {
            Log.add(_name + '|' + (fame - _fame) + '|' + fame + '|' + log, "fame");
        }
        if (fame > _fame) {
            sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S1_REPUTATION).addNumber(fame - _fame));
        }
        setFame(fame);
        sendChanges();
    }

    public int getIncorrectValidateCount() {
        return incorrectValidateCount;
    }

    public int setIncorrectValidateCount(final int count) {
        return incorrectValidateCount;
    }

    public boolean isNotShowBuffAnim() {
        return _notShowBuffAnim;
    }

    public void setNotShowBuffAnim(final boolean value) {
        _notShowBuffAnim = value;
    }

    public void enterMovieMode() {
        if (isInMovie()) //already in movie
        {
            return;
        }

        setTarget(null);
        stopMove();
        setIsInMovie(true);
        //sendPacket(new CameraMode(1));
    }

    // DS: CameraMode(0) молча выключает режим EnterChat на клиенте, временно убрано поскольку работает и без этого
    // TODO: отсниффить и сделать как должно быть
    public void leaveMovieMode() {
        if (!isInMovie()) {
            return;
        }

        setIsInMovie(false);
        //sendPacket(new CameraMode(0));
        //broadcastCharInfo();
    }

    public final void specialCamera(final GameObject creature, final int force, final int angle1, final int angle2,
                                    final int time, final int range, final int duration, final int relYaw, final int relPitch,
                                    final int isWide, final int relAngle) {
        sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle));
    }

    public final void specialCameraEx(final GameObject creature, final int force, final int angle1, final int angle2,
                                      final int time, final int duration, final int relYaw, final int relPitch, final int isWide,
                                      final int relAngle) {
        sendPacket(new SpecialCamera(creature, this, force, angle1, angle2, time, duration, relYaw, relPitch, isWide, relAngle));
    }

    public final void specialCamera3(final GameObject creature, final int force, final int angle1, final int angle2,
                                     final int time, final int range, final int duration, final int relYaw, final int relPitch,
                                     final int isWide, final int relAngle, final int unk) {
        sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle, unk));
    }

    public int getMovieId() {
        return _movieId;
    }

    public void setMovieId(final int id) {
        _movieId = id;
    }

    public boolean isInMovie() {
        return _isInMovie;
    }

    public void setIsInMovie(final boolean state) {
        _isInMovie = state;
    }

    public void showQuestMovie(final SceneMovie movie) {
        if (isInMovie()) //already in movie
        {
            return;
        }

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movie.getId());
        setIsInMovie(true);
        sendPacket(movie.packet(this));
    }

    public void showQuestMovie(final int movieId) {
        if (isInMovie()) //already in movie
        {
            return;
        }

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movieId);
        setIsInMovie(true);
        sendPacket(new ExStartScenePlayer(movieId));
    }

    public void setAutoLoot(final int value) {
        if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
            autoLoot = value;
            playerVariables.set(PlayerVariables.AUTO_LOOT, String.valueOf(value), -1);
        }
    }

    public void setAutoLootHerbs(final boolean enable) {
        if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
            _autoLootHerbs = enable;
            playerVariables.set(PlayerVariables.AUTO_LOOT_HERBS, String.valueOf(enable), -1);
        }
    }

    public int isAutoLootEnabled() {
        return autoLoot;
    }

    public boolean isAutoLootHerbsEnabled() {
        return _autoLootHerbs;
    }

    public final void reName(final String name, final boolean saveToDB) {
        setName(name);
        if (saveToDB) {
            CharacterDAO.getInstance().updateName(getObjectId(), name);
        }
        broadcastCharInfo();
    }

    public final void reName(final String name) {
        reName(name, false);
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    public int getTalismanCount() {
        return (int) calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
    }

    public boolean getOpenCloak() {
        return AllSettingsConfig.ALT_OPEN_CLOAK_SLOT || (int) calcStat(Stats.CLOAK_SLOT, 0, null, null) > 0;
    }

    public final void disableDrop(final int time) {
        _dropDisabled = System.currentTimeMillis() + time;
    }

    public final boolean isDropDisabled() {
        return _dropDisabled > System.currentTimeMillis();
    }

    public ItemInstance getPetControlItem() {
        return _petControlItem;
    }

    public void setPetControlItem(final ItemInstance item) {
        _petControlItem = item;
    }

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive() {
        setNonAggroTime(0);

        if (isActive.getAndSet(true)) {
            return;
        }

        onActive();
    }

    private void onActive() {
        setNonAggroTime(0);
        sendPacket(SystemMsg.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);

        if (!_savedServitors.isEmpty()) {
            ThreadPoolManager.getInstance().execute(new ServitorSummonTask(this));
        }
    }

    public void summonPet(final ItemInstance controlItem, final Location loc) {
        if (getServitor() != null) {
            return;
        }

        if (controlItem == null) {
            return;
        }

        final int npcId = PetDataHolder.getInstance().getPetTemplateId(controlItem.getItemId());
        if (npcId == 0) {
            return;
        }

        final NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
        if (petTemplate == null) {
            return;
        }

        final PetInstance pet = PetDAO.getInstance().select(this, controlItem, petTemplate);
        if (pet == null) {
            return;
        }

        setServitor(pet);

        if (!pet.isExistsInDatabase()) {
            pet.setCurrentHp(pet.getMaxHp(), false);
            pet.setCurrentMp(pet.getMaxMp());
            pet.setCurrentFed(pet.getMaxFed());
            //TODO : нужно ли это тут ?
            //pet.updateControlItem();
        }

        pet.getInventory().restore();

        pet.setNonAggroTime(System.currentTimeMillis() + AiConfig.NONAGGRO_TIME_ONTELEPORT);
        pet.setReflection(getReflection());
        pet.spawnMe(loc);
        pet.moveToOwner();
        pet.getInventory().validateItems();

        if (pet instanceof PetBabyInstance) {
            ((PetBabyInstance) pet).startBuffTask();
        }

        PetEffectDAO.getInstance().select(pet);

        getListeners().onSummonServitor(pet);
    }

    public void summonSummon(final int skillId) {
        if (getServitor() != null) {
            return;
        }

        final SummonInstance summon = SummonDAO.getInstance().select(this, skillId);
        if (summon == null) {
            return;
        }

        setServitor(summon);

        summon.setNonAggroTime(System.currentTimeMillis() + AiConfig.NONAGGRO_TIME_ONTELEPORT);
        summon.setHeading(getHeading());
        summon.setReflection(getReflection());
        summon.spawnMe(Location.findPointToStay(this, 10, 20));
        summon.setRunning();
        summon.setFollowMode(true);

        if (summon.getSkillLevel(4140) > 0) {
            summon.altUseSkill(SkillTable.getInstance().getSkillEntry(4140, summon.getSkillLevel(4140)), this);
        }

        SummonEffectDAO.getInstance().select(summon);

        final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent != null) {
            siegeEvent.updateSiegeSummon(this, summon);
        }

        getListeners().onSummonServitor(summon);
    }

    public List<TrapInstance> getTraps() {
        return _traps;
    }

    public void addTrap(final TrapInstance trap) {
        if (_traps == Collections.<TrapInstance>emptyList()) {
            _traps = new CopyOnWriteArrayList<>();
        }

        _traps.add(trap);
    }

    public void removeTrap(final TrapInstance trap) {
        _traps.remove(trap);
    }

    public void destroyAllTraps() {
        for (final TrapInstance t : _traps) {
            t.deleteMe();
        }
    }

    public int getBlockCheckerArena() {
        return _handysBlockCheckerEventArena;
    }

    public void setBlockCheckerArena(final byte arena) {
        _handysBlockCheckerEventArena = arena;
    }

    @Override
    public synchronized PlayerListenerList initializeListeners() {
        return new PlayerListenerList(this);
    }

    @Override
    public PlayerListenerList getListeners() {
        return (PlayerListenerList) oldListeners;
    }

    @Override
    public synchronized PlayerStatsChangeRecorder initializeStatsRecorder() {
        return new PlayerStatsChangeRecorder(this);
    }

    @Override
    public PlayerStatsChangeRecorder getStatsRecorder() {
        return (PlayerStatsChangeRecorder) _statsRecorder;
    }

    public int getHoursInGame() {
        _hoursInGame++;
        return _hoursInGame;
    }

    public void startHourlyTask() {
        if (_hourlyTask == null)
            _hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
    }

    public void stopHourlyTask() {
        if (_hourlyTask != null) {
            _hourlyTask.cancel(false);
            _hourlyTask = null;
        }
    }

    public boolean isAgathionResAvailable() {
        return _agathionResAvailable;
    }

    public void setAgathionRes(final boolean val) {
        _agathionResAvailable = val;
    }

    public boolean isClanAirShipDriver() {
        return isInBoat() && getBoat().isClanAirShip() && ((ClanAirShip) getBoat()).getDriver() == this;
    }

    public String getSessionVar(final String key) {
        if (_userSession == null) {
            return null;
        }
        return _userSession.get(key);
    }

    public void setSessionVar(final String key, final String val) {
        if (_userSession == null) {
            _userSession = new ConcurrentHashMap<>();
        }

        if (val == null || val.isEmpty()) {
            _userSession.remove(key);
        } else {
            _userSession.put(key, val);
        }
    }

    public boolean isNotShowTraders() {
        return _notShowTraders;
    }

    public void setNotShowTraders(final boolean notShowTraders) {
        _notShowTraders = notShowTraders;
    }

    public boolean isDebug() {
        return _debug;
    }

    public void setDebug(final boolean b) {
        _debug = b;
    }

    public void sendItemList(final boolean show) {
        final ItemInstance[] items = getInventory().getItems();
        final LockType lockType = getInventory().getLockType();
        final int[] lockItems = getInventory().getLockItems();
        final int allSize = items.length;
        int questItemsSize = 0;
        int agathionItemsSize = 0;
        for (final ItemInstance item : items) {
            if (item.getTemplate().isQuest()) {
                questItemsSize++;
            }
            if (item.getAgathionMaxEnergy() > 0) {
                agathionItemsSize++;
            }
        }

        sendPacket(new ItemList(allSize - questItemsSize, items, show, lockType, lockItems));
        if (questItemsSize > 0) {
            sendPacket(new ExQuestItemList(questItemsSize, items, lockType, lockItems));
        }
        if (agathionItemsSize > 0) {
            sendPacket(new ExBR_AgathionEnergyInfo(agathionItemsSize, items));
        }
    }

    public int getBeltInventoryIncrease() {
        final ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
        if (item != null && item.getTemplate().getAttachedSkills() != null) {
            for (final SkillEntry skill : item.getTemplate().getAttachedSkills()) {
                for (final FuncTemplate func : skill.getTemplate().getAttachedFuncs()) {
                    if (func._stat == Stats.INVENTORY_LIMIT) {
                        return (int) func._value;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public boolean checkCoupleAction(final Player target) {
        if (target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_PRIVATE_SHOP_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isFishing()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_FISHING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isInCombat()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isCursedWeaponEquipped()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isInOlympiadMode()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        final DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_TERRITORY_WAR).addName(target));
            return false;
        }
        if (target.isOnSiegeField()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isInBoat() || target.getMountNpcId() != 0) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isTeleporting()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isTransformed()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        if (target.isDead()) {
            sendPacket(new SystemMessage(SystemMsg.C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
            return false;
        }
        return true;
    }

    @Override
    public void startAttackStanceTask() {
        startAttackStanceTask0();
        final Servitor summon = getServitor();
        if (summon != null) {
            summon.startAttackStanceTask0();
        }
    }

    @Override
    public void displayGiveDamageMessage(final Creature target, final int damage, final boolean crit, final boolean miss, final boolean shld, final boolean magic) {
        super.displayGiveDamageMessage(target, damage, crit, miss, shld, magic);
        if (crit) {
            if (magic) {
                sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);
            } else {
                sendPacket(new SystemMessage(SystemMsg.C1_LANDED_A_CRITICAL_HIT).addName(this));
            }
        }

        if (miss) {
            sendPacket(new SystemMessage(SystemMsg.C1S_ATTACK_WENT_ASTRAY).addName(this));
        }
    }

    @Override
    public void displayReceiveDamageMessage(final Creature attacker, final int damage, final int toPet, final int reflected) {
        super.displayReceiveDamageMessage(attacker, damage, toPet, reflected);

        if (attacker != this && !isDead()) {
            sendPacket(new SystemMessage(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addNumber(damage));
            if (reflected > 0) {
                sendPacket(new SystemMessage(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(attacker).addName(this).addNumber(reflected));
            }
        }
    }

    public TIntObjectMap<String> getPostFriends() {
        return postFriends;
    }

    public boolean isSharedGroupDisabled(final int groupId) {
        final TimeStamp sts = sharedGroupReuses.get(groupId);
        if (sts == null) {
            return false;
        }
        if (sts.hasNotPassed()) {
            return true;
        }
        sharedGroupReuses.remove(groupId);
        return false;
    }

    public TimeStamp getSharedGroupReuse(final int groupId) {
        return sharedGroupReuses.get(groupId);
    }

    public void addSharedGroupReuse(final int group, final TimeStamp stamp) {
        sharedGroupReuses.put(group, stamp);
    }

    public Set<Entry<Integer, TimeStamp>> getSharedGroupReuses() {
        return sharedGroupReuses.entrySet();
    }

    public void sendReuseMessage(final ItemInstance item) {
        final TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
        if (sts == null || !sts.hasNotPassed()) {
            return;
        }

        final long timeleft = sts.getReuseCurrent();
        final long hours = timeleft / 3600000;
        final long minutes = (timeleft - hours * 3600000) / 60000;
        final long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);

        if (hours > 0) {
            sendPacket(new SystemMessage(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().getItemId()).addNumber(hours).addNumber(minutes).addNumber(seconds));
        } else if (minutes > 0) {
            sendPacket(new SystemMessage(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().getItemId()).addNumber(minutes).addNumber(seconds));
        } else {
            sendPacket(new SystemMessage(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().getItemId()).addNumber(seconds));
        }
    }

    public void ask(final ConfirmDlg dlg, final OnAnswerListener listener) {
        if (askDialog != null) {
            return;
        }
        final int rnd = Rnd.nextInt();
        askDialog = new ImmutablePair<>(rnd, listener);
        dlg.setRequestId(rnd);
        sendPacket(dlg);
    }

    public Pair<Integer, OnAnswerListener> getAskListener(final boolean clear) {
        if (!clear) {
            return askDialog;
        } else {
            final Pair<Integer, OnAnswerListener> ask = askDialog;
            askDialog = null;
            return ask;
        }
    }

    @Override
    public boolean isDead() {
        return (isInOlympiadMode() || isInDuel()) ? getCurrentHp() <= 1. : super.isDead();
    }

    public boolean hasPrivilege(final Privilege privilege) {
        return _clan != null && (getClanPrivileges() & privilege.mask()) == privilege.mask();
    }

    public MatchingRoom getMatchingRoom() {
        return _matchingRoom;
    }

    public void setMatchingRoom(final MatchingRoom matchingRoom) {
        _matchingRoom = matchingRoom;
        if (matchingRoom == null)
            _matchingRoomWindowOpened = false;
    }

    public boolean isMatchingRoomWindowOpened() {
        return _matchingRoomWindowOpened;
    }

    public void setMatchingRoomWindowOpened(boolean b) {
        _matchingRoomWindowOpened = b;
    }

    public void dispelBuffs() {
        getEffectList().getAllEffects().stream().filter(e -> !e.getSkill().getTemplate().isOffensive() && !e.getSkill().getTemplate().isNewbie() && e.isCancelable() && !e.getSkill().getTemplate().isPreservedOnDeath()).forEach(e -> {
            sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
            e.exit();
        });
        if (getServitor() != null) {
            getServitor().getEffectList().getAllEffects().stream().filter(e -> !e.getSkill().getTemplate().isOffensive() && !e.getSkill().getTemplate().isNewbie() && e.isCancelable() && !e.getSkill().getTemplate().isPreservedOnDeath()).forEach(Effect::exit);
        }
    }

    public void setInstanceReuse(final int id, final long time) {
        sendPacket(new SystemMessage(SystemMsg.INSTANT_ZONE_S1_ENTRY_HAS_BEEN_RESTRICTED).addString(getName()));
        _instancesReuses.put(id, time);
        CharacterInstancesDAO.getInstance().setInstanceReuse(getObjectId(), id, time);
    }

    public void removeInstanceReuse(final int id) {
        if (_instancesReuses.remove(id) != null) {
            CharacterInstancesDAO.getInstance().removeInstanceReuse(getObjectId(), id);
        }
    }

    public void removeAllInstanceReuses() {
        _instancesReuses.clear();
        CharacterInstancesDAO.getInstance().removeAllInstanceReuses(getObjectId());
    }

    public void removeInstanceReusesByGroupId(final int groupId) {
        InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId).stream().filter(i -> getInstanceReuse(i) != null).forEach(this::removeInstanceReuse);
    }

    public Long getInstanceReuse(final int id) {
        return _instancesReuses.get(id);
    }

    public Map<Integer, Long> getInstanceReuses() {
        return _instancesReuses;
    }

    private void loadInstanceReuses() {
        _instancesReuses.putAll(CharacterInstancesDAO.getInstance().loadInstanceReuses(getObjectId()));
    }

    public Reflection getActiveReflection() {
        for (final Reflection r : ReflectionManager.getInstance().getAll()) {
            if (r != null && r.getVisitors().length > 0 && ArrayUtils.contains(r.getVisitors(), getObjectId())) {
                return r;
            }
        }
        return null;
    }

    public boolean canEnterInstance(final int instancedZoneId) {
        final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

        if (isDead()) {
            return false;
        }

        if (getEvent(SingleMatchEvent.class) != null) {
            return false;
        }

        if (ReflectionManager.getInstance().size() > ServerConfig.MAX_REFLECTIONS_COUNT) {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        if (iz == null) {
            sendPacket(SystemMsg.SYSTEM_ERROR);
            return false;
        }

        if (ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels()) {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        return iz.getEntryType().canEnter(this, iz);
    }

    public boolean canReenterInstance(final int instancedZoneId) {
        if (getEvent(SingleMatchEvent.class) != null) {
            return false;
        }
        final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
        if (getActiveReflection() != null && getActiveReflection().getInstancedZoneId() != instancedZoneId) {
            sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
            return false;
        }
        if (iz.isDispelBuffs()) {
            dispelBuffs();
        }
        return iz.getEntryType().canReEnter(this, iz);
    }

    @Override
    public void broadCast(final IBroadcastPacket... packet) {
        sendPacket(packet);
    }

    @Override
    public int getMemberCount() {
        return 1;
    }

    @Override
    public Player getGroupLeader() {
        return this;
    }

    @Override
    public Iterator<Player> iterator() {
        return Collections.singleton(this).iterator();
    }

    public PlayerGroup getPlayerGroup() {
        if (getParty() != null) {
            if (getParty().getCommandChannel() != null) {
                return getParty().getCommandChannel();
            } else {
                return getParty();
            }
        } else {
            return this;
        }
    }

    public boolean isActionBlocked(final String action) {
        return _blockedActions.contains(action);
    }

    public void blockActions(final String... actions) {
        Collections.addAll(_blockedActions, actions);
    }

    public void unblockActions(final String... actions) {
        for (final String action : actions) {
            _blockedActions.remove(action);
        }
    }

    public OlympiadGame getOlympiadGame() {
        return _olympiadGame;
    }

    public void setOlympiadGame(final OlympiadGame olympiadGame) {
        _olympiadGame = olympiadGame;
    }

    public OlympiadGame getOlympiadObserveGame() {
        return _olympiadObserveGame;
    }

    public void setOlympiadObserveGame(final OlympiadGame olympiadObserveGame) {
        _olympiadObserveGame = olympiadObserveGame;
    }

    public void addRadar(final int x, final int y, final int z) {
        sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
    }

    public void addRadarWithMap(final int x, final int y, final int z) {
        ThreadPoolManager.getInstance().schedule(() -> sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.FLAG_ON_MAP, x, y, z)), 500);
    }

    public void removeRadar() {
        sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, 0, 0, 0));
    }

    public PetitionMainGroup getPetitionGroup() {
        return _petitionGroup;
    }

    public void setPetitionGroup(final PetitionMainGroup petitionGroup) {
        _petitionGroup = petitionGroup;
    }

    public int getLectureMark() {
        return _lectureMark;
    }

    public void setLectureMark(int lectureMark, final boolean broadcast) {
        if (lectureMark != 0) {
            final TreeSet<Long> set = new TreeSet<>();
            set.add(getCreateTime());
            set.addAll(getAccountChars().valueCollection().stream().map(AccountPlayerInfo::getCreateTime).collect(Collectors.toList()));

            final Calendar firstCreate = Calendar.getInstance();
            firstCreate.setTimeInMillis(set.pollFirst());
            firstCreate.add(Calendar.MONTH, 6);
            firstCreate.set(Calendar.DAY_OF_MONTH, 1);

            if (lectureMark == -1) {
                lectureMark = INITIAL_MARK;
            }

            switch (lectureMark) {
                case INITIAL_MARK:
                    // если после создания первого чара, прошло 6 месяцев
                    if (firstCreate.getTimeInMillis() < System.currentTimeMillis()) {
                        lectureMark = OFF_MARK;
                    }
                    break;
                case EVANGELIST_MARK:
                    // если после создания первого чара, непрошло 6 месяцев
                    if (firstCreate.getTimeInMillis() > System.currentTimeMillis()) {
                        lectureMark = OFF_MARK;
                    }
                    break;
            }

            if (_lectureMark == INITIAL_MARK) {
                _lectureEndTask = ThreadPoolManager.getInstance().schedule(new LectureInitialPeriodEndTask(this), firstCreate.getTimeInMillis() - System.currentTimeMillis());
            } else {
                stopLectureTask();
            }

            AccountLectureMarkDAO.getInstance().replace(getAccountName(), lectureMark);
        }

        _lectureMark = lectureMark;

        if (broadcast) {
            broadcastUserInfo(true);
        }
    }

    public void stopLectureTask() {
        if (_lectureEndTask != null) {
            _lectureEndTask.cancel(false);
            _lectureEndTask = null;
        }
    }

    public boolean isUserRelationActive() {
        return _enableRelationTask == null;
    }

    public void startEnableUserRelationTask(final long time, final SiegeEvent<?, ?> siegeEvent) {
        if (_enableRelationTask != null) {
            return;
        }

        _enableRelationTask = ThreadPoolManager.getInstance().schedule(new EnableUserRelationTask(this, siegeEvent), time);
    }

    public void stopEnableUserRelationTask() {
        if (_enableRelationTask != null) {
            _enableRelationTask.cancel(false);
            _enableRelationTask = null;
        }
    }

    public void setLastNpcInteractionTime() {
        _lastNpcInteractionTime = System.currentTimeMillis();
    }

    public boolean canMoveAfterInteraction() {
        return System.currentTimeMillis() - _lastNpcInteractionTime > 1500L;
    }

    public DyeData[] getDyes() {
        return dyes;
    }

    @Override
    public void setTeam(final TeamType t) {
        super.setTeam(t);
        sendChanges();
        final Servitor summon = getServitor();
        if (summon != null) {
            summon.sendChanges();
        }
    }

    public List<int[]> getSavedServitors() {
        final List<int[]> list = _savedServitors;
        _savedServitors = Collections.emptyList();
        return list;
    }

    @Override
    public int getINT() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinINT(), Math.min(getPlayerTemplateComponent().getParameter().getMaxINT(), (int) calcStat(Stats.STAT_INT, getTransformation().getCombat().basic_stat[1], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutINT);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinINT(), Math.min(getPlayerTemplateComponent().getParameter().getMaxINT(), (int) calcStat(Stats.STAT_INT, getPlayerTemplateComponent().getParameter().getBaseINT(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutINT);
        }
        return stat;
    }

    @Override
    public int getSTR() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinSTR(), Math.min(getPlayerTemplateComponent().getParameter().getMaxSTR(), (int) calcStat(Stats.STAT_STR, getTransformation().getCombat().basic_stat[0], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutSTR);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinSTR(), Math.min(getPlayerTemplateComponent().getParameter().getMaxSTR(), (int) calcStat(Stats.STAT_STR, getPlayerTemplateComponent().getParameter().getBaseSTR(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutSTR);
        }
        return stat;
    }

    @Override
    public int getCON() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinCON(), Math.min(getPlayerTemplateComponent().getParameter().getMaxCON(), (int) calcStat(Stats.STAT_CON, getTransformation().getCombat().basic_stat[2], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutCON);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinCON(), Math.min(getPlayerTemplateComponent().getParameter().getMaxCON(), (int) calcStat(Stats.STAT_CON, getPlayerTemplateComponent().getParameter().getBaseCON(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutCON);
        }
        return stat;
    }

    @Override
    public int getMEN() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinMEN(), Math.min(getPlayerTemplateComponent().getParameter().getMaxMEN(), (int) calcStat(Stats.STAT_MEN, getTransformation().getCombat().basic_stat[5], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutMEN);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinMEN(), Math.min(getPlayerTemplateComponent().getParameter().getMaxMEN(), (int) calcStat(Stats.STAT_MEN, getPlayerTemplateComponent().getParameter().getBaseMEN(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutMEN);
        }
        return stat;
    }

    @Override
    public int getDEX() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinDEX(), Math.min(getPlayerTemplateComponent().getParameter().getMaxDEX(), (int) calcStat(Stats.STAT_DEX, getTransformation().getCombat().basic_stat[3], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutDEX);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinDEX(), Math.min(getPlayerTemplateComponent().getParameter().getMaxDEX(), (int) calcStat(Stats.STAT_DEX, getPlayerTemplateComponent().getParameter().getBaseDEX(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutDEX);
        }
        return stat;
    }

    @Override
    public int getWIT() {
        if (isCombatTransformed()) {
            final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinWIT(), Math.min(getPlayerTemplateComponent().getParameter().getMaxWIT(), (int) calcStat(Stats.STAT_WIT, getTransformation().getCombat().basic_stat[4], null, null)));
            if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
                return (int) Math.max(1, stat / CustomConfig.cutWIT);
            }
            return stat;
        }
        final int stat = Math.max(getPlayerTemplateComponent().getParameter().getMinWIT(), Math.min(getPlayerTemplateComponent().getParameter().getMaxWIT(), (int) calcStat(Stats.STAT_WIT, getPlayerTemplateComponent().getParameter().getBaseWIT(), null, null)));
        if (CustomConfig.subscriptionAllow && !isGM() && !getCustomPlayerComponent().isSubscriptionActive()) {
            return (int) Math.max(1, stat / CustomConfig.cutWIT);
        }
        return stat;
    }

    @Override
    public int getMaxCp() {
        if (isCombatTransformed()) {
            return (int) calcStat(Stats.MAX_CP, getTransformation().getTable(LevelParameter.cp), null, null);
        }
        return (int) calcStat(Stats.MAX_CP, getPlayerTemplateComponent().getLevelParameter(getLevel(), LevelParameter.cp), null, null);
    }

    @Override
    public double getMaxHp() {
        if (isCombatTransformed()) {
            return (int) calcStat(Stats.MAX_HP, getTransformation().getTable(LevelParameter.hp), null, null);
        }
        return (int) calcStat(Stats.MAX_HP, getPlayerTemplateComponent().getLevelParameter(getLevel(), LevelParameter.hp), null, null);
    }

    @Override
    public int getMaxMp() {
        if (isCombatTransformed()) {
            return (int) calcStat(Stats.MAX_MP, getTransformation().getTable(LevelParameter.mp), null, null);
        }
        return (int) calcStat(Stats.MAX_MP, getPlayerTemplateComponent().getLevelParameter(getLevel(), LevelParameter.mp), null, null);
    }

    @Override
    public double getHpRegen() {
        if (isCombatTransformed()) {
            return calcStat(Stats.REGENERATE_HP_RATE, getTransformation().getRegen(LevelParameter.hp));
        }
        return calcStat(Stats.REGENERATE_HP_RATE, getPlayerTemplateComponent().getBaseRegen(LevelParameter.hp));
    }

    @Override
    public double getMpRegen() {
        if (isCombatTransformed()) {
            return calcStat(Stats.REGENERATE_MP_RATE, getTransformation().getRegen(LevelParameter.mp));
        }
        return calcStat(Stats.REGENERATE_MP_RATE, getPlayerTemplateComponent().getBaseRegen(LevelParameter.mp));
    }

    @Override
    public double getCpRegen() {
        if (isCombatTransformed()) {
            return calcStat(Stats.REGENERATE_CP_RATE, getTransformation().getRegen(LevelParameter.cp));
        }
        return calcStat(Stats.REGENERATE_CP_RATE, getPlayerTemplateComponent().getBaseRegen(LevelParameter.cp));
    }

    @Override
    public int getRandomDamage() {
        final WeaponTemplate weaponItem = getActiveWeaponItem();
        if (weaponItem == null) {
            if (isCommonTransformed()) {
                return getTransformation().getCommon().base_random_damage;
            }
            return getPlayerTemplateComponent().getBaseRandomDamage();
        }
        return weaponItem.getRandomDamage();
    }

    public boolean isBlockTransform() {
        return _block_transform;
    }

    public void setBlockTransform(final boolean block) {
        _block_transform = block;
    }

    public ICommunityComponent getCommunityComponent() {
        return communityComponent;
    }

    public void setCommunityComponent(final ICommunityComponent communityComponent) {
        this.communityComponent = communityComponent;
    }

    public AcpComponent getAcpComponent() {
        return acpComponent;
    }

    @Override
    public final void setCurrentCp(final double newCp, final boolean sendInfo) {
        super.setCurrentCp(newCp, sendInfo);
        getListeners().onCurrentCp(getCurrentCp(), getCurrentCpPercents());
    }

    @Override
    public final void setCurrentHp(final double newHp, final boolean canRessurect, final boolean sendInfo) {
        super.setCurrentHp(newHp, canRessurect, sendInfo);
        getListeners().onCurrentHp(getCurrentHp(), getCurrentHpPercents());
    }

    @Override
    public final void setCurrentMp(final double newMp, final boolean sendInfo) {
        super.setCurrentMp(newMp, sendInfo);
        getListeners().onCurrentMp(getCurrentMp(), getCurrentMpPercents());
    }

    public AppearanceComponent getAppearanceComponent() {
        return appearanceComponent;
    }

    public CustomPlayerComponent getCustomPlayerComponent() {
        return customPlayerComponent;
    }

    public EnchantParams getEnchantParams() {
        return enchantParams;
    }

    public boolean isForceVisualFlag() {
        return _forceVisualFlag || getPlayerVariables().getInt(PlayerVariables.HIDE_VISUAL) == 1;
    }

    public void setForceVisualFlag(boolean v) {
        _forceVisualFlag = v;
        getPlayerVariables().set(PlayerVariables.HIDE_VISUAL, v ? 1 : 0, -1);
    }

    public boolean isPhantom() {
        return this instanceof Phantom;
    }

    public boolean isOlympiadEnchant() {
        return isOlympiadEnchant;
    }

    public void setOlympiadEnchant(boolean olympiadEnchant) {
        if (olympiadEnchant) {
            for (ItemInstance item : getInventory().getItems()) {
                if (!item.canBeEnchanted())
                    continue;
                if (item.isWeapon())
                    item.setTemporaryEnchant(getTempOlyEnch(item, LostDreamCustom.enchantOlyWeapon));
                if (item.isArmor())
                    item.setTemporaryEnchant(getTempOlyEnch(item, LostDreamCustom.enchantOlyArmor));
                if (item.isAccessory())
                    item.setTemporaryEnchant(getTempOlyEnch(item, LostDreamCustom.enchantOlyJewelry));
            }
        } else {
            for (ItemInstance item : getInventory().getItems())
                item.setTemporaryEnchant(-1);
        }
        isOlympiadEnchant = olympiadEnchant;
    }

    private int getTempOlyEnch(ItemInstance item, int configLvl) {
        return LostDreamCustom.enchantOlyExceptLowerItems ? Math.min(item.getEnchantLevel(), configLvl) : configLvl;
    }

    public PvpComponent getPvpComponent() {
        return pvpComponent;
    }

    public boolean isTalismanStack() {
        return isTalismanStack;
    }

    public void setTalismanStack(boolean talismanStack) {
        isTalismanStack = talismanStack;
    }

    public long getDieTime() {
        return dieTime;
    }

    public void setDieTime() {
        this.dieTime = System.currentTimeMillis();
    }

    public RefineComponent getRefineComponent() {
        return refineComponent;
    }

    public long getOnlineTime() {
        return _onlineTime;
    }

    public void setOnlineTime(final long time) {
        _onlineTime = time;
        _onlineBeginTime = System.currentTimeMillis();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isHwidLockVisual() {
        return isHwidLockVisual;
    }

    public void setHwidLockVisual(boolean hwidLockVisual) {
        isHwidLockVisual = hwidLockVisual;
    }

    public String getHwid() {
        return getNetConnection() != null ? getNetConnection().getHWID() : null;
    }

    public String getEnterHwid() {
        return enterHwid;
    }

    public void setEnterHwid(String enterHwid) {
        this.enterHwid = enterHwid;
    }

    public boolean isNoCarrier() {
        return noCarrier.get();
    }

    public boolean isLoadingAfterCarrier() {
        return loadingAfterCarrier.get();
    }

    public boolean stopLoadingAfterCarrier() {
        return loadingAfterCarrier.compareAndSet(true, false);
    }

    public EventComponent getEventComponent() {
        return eventComponent;
    }

    public ScheduledFuture<?> getNoCarrierSchedule() {
        return noCarrierSchedule;
    }

    public void stopNoCarrierSchedule() {
        if (noCarrierSchedule != null) {
            noCarrierSchedule.cancel(true);
            noCarrierSchedule = null;
        }
    }

    public RecipeComponent getRecipeComponent() {
        return recipeComponent;
    }

    public boolean isInLastHero() {
        return isInLastHero.get();
    }

    public boolean setInLastHero(boolean val) {
        return isInLastHero.compareAndSet(!val, val);
    }

    public VisualParams getVisualParams() {
        return visualParams;
    }

    private class UpdateEffectIcons extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            updateEffectIconsImpl();
            _updateEffectIconsTask = null;
        }
    }

    public class BroadcastCharInfoTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            broadcastCharInfoImpl();
            _broadcastCharInfoTask = null;
        }
    }

    private class UserInfoTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            sendUserInfoImpl();
            _userInfoTask = null;
        }
    }
}