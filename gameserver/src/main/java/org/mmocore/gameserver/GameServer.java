package org.mmocore.gameserver;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jts.dataparser.PTSDataLoader;
import org.jts.protection.Protection;
import org.mmocore.commons.database.installer.DatabaseInstaller;
import org.mmocore.commons.lang.StatsUtils;
import org.mmocore.commons.listener.ListenerList;
import org.mmocore.commons.net.nio.impl.SelectorThread;
import org.mmocore.commons.net.xmlrpc.XmlRpcServer;
import org.mmocore.commons.versioning.Version;
import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.configuration.config.*;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.configuration.loader.ConfigLoader;
import org.mmocore.gameserver.data.BoatHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.data.xml.Parsers;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.data.xml.holder.StaticObjectHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterMinigameScoreDAO;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bypass.BypassHolder;
import org.mmocore.gameserver.handler.items.ItemHandler;
import org.mmocore.gameserver.handler.npcdialog.NpcDialogAppenderHolder;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.handler.usercommands.UserCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.listener.GameListener;
import org.mmocore.gameserver.listener.game.OnShutdownListener;
import org.mmocore.gameserver.listener.game.OnStartListener;
import org.mmocore.gameserver.listeners.Listeners;
import org.mmocore.gameserver.manager.*;
import org.mmocore.gameserver.manager.games.FishingChampionShipManager;
import org.mmocore.gameserver.manager.games.LotteryManager;
import org.mmocore.gameserver.manager.games.MiniGameScoreManager;
import org.mmocore.gameserver.manager.naia.NaiaCoreManager;
import org.mmocore.gameserver.manager.naia.NaiaTowerManager;
import org.mmocore.gameserver.manager.tests.TestsManager;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.MonsterRace;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneManager;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollManager;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.GamePacketHandler;
import org.mmocore.gameserver.network.lineage.components.ServerOpcodeManager;
import org.mmocore.gameserver.network.telnet.TelnetServer;
import org.mmocore.gameserver.network.xmlrpc.handler.WorldHandler;
import org.mmocore.gameserver.phantoms.PhantomLoader;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.PetSkillsTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.taskmanager.TaskLoader;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Strings;
import org.mmocore.gameserver.utils.ThymeleafJob;
import org.mmocore.gameserver.utils.TradeHelper;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.World;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameServer {
    public static final int AUTH_SERVER_PROTOCOL = 2;
    public static final int AUTH_SERVER_PROTOCOL_IPСONFIG = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);
    private static final Path DATABASE_FILES_DIR = Paths.get("sql/");

    public static GameServer _instance;
    private final SelectorThread<GameClient> _selectorThreads;
    private final Version version;
    private final GameServerListenerList _listeners;
    private final EmbeddedCacheManager cacheManager = new DefaultCacheManager("infinispan.xml");
    private final Scheduler scheduler;
    private final int _serverStarted;
    private TelnetServer telnetServer;
    private XmlRpcServer xmlRpcServer;
    private Listeners globalListeners;

    public GameServer() throws Exception {
        _instance = this;
        _serverStarted = time();
        _listeners = new GameServerListenerList();
        globalListeners = new Listeners();
        new File("./logs/").mkdir();

        final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();

        version = new Version(GameServer.class);
        LOGGER.info("=================================================");
        LOGGER.info("Revision: ................ " + version.getRevisionNumber());
        LOGGER.info("Build date: .............. " + version.getBuildDate());
        LOGGER.info("Compiler version: ........ " + version.getBuildJdk());
        LOGGER.info("=================================================");
        LOGGER.info("================== Configuration ================");
        ConfigLoader.loading();
        LOGGER.info("=============== end Configuration ===============");

        // Initialize opcode list
        ServerOpcodeManager.getInstance();
        // Check binding address
        checkFreePorts();
        // Initialize database
        DatabaseFactory.getInstance().doStart();

        HikariDataSource hikariDataSource = DatabaseFactory.getInstance().getConnectionPool();
        DatabaseInstaller.start(hikariDataSource, DATABASE_FILES_DIR);

        final IdFactory _idFactory = IdFactory.getInstance();
        if (!_idFactory.isInitialized()) {
            LOGGER.error("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }

        ThreadPoolManager.getInstance();

        /* Секция загрузки менеджеров итемов. Нужно грузить до скриптов, т.к они используют данные менеджеры! */
        PTSDataLoader.load();
        EnchantScrollManager.load();
        AttributeStoneManager.load();
        LifeStoneManager.load();

        Scripts.getInstance();

        GeoEngine.load();

        Strings.reload();

        GameTimeManager.getInstance();

        World.init();

        Parsers.parseAll();

        HtmCache.getInstance().load();

        ThymeleafJob.getInstance();

        CrestCache.getInstance();

        CharacterDAO.getInstance();

        ClanTable.getInstance();

        SkillTreeTable.getInstance();

        VariationManager.getInstance().load();

        PetSkillsTable.getInstance();

        SpawnManager.getInstance().spawnAll();

        SpawnMakerManager.getInstance().makerInit();

        BoatHolder.getInstance().spawnAll();

        StaticObjectHolder.getInstance().spawnAll();

        RaidBossSpawnManager.getInstance();

        Scripts.getInstance().init();

        TestsManager.getInstance();

        DimensionalRiftManager.getInstance();

        AnnouncementUtils.autoAnnouncementsLaunch();

        LotteryManager.getInstance();

        PlayerMessageStack.getInstance();

        MonsterRace.getInstance();

        SevenSigns.getInstance();
        SevenSignsFestival.getInstance();
        SevenSigns.getInstance().updateFestivalScore();

        // TODO Возможно вырезать AutoSpawnManager.getInstance();

        SevenSigns.getInstance().spawnSevenSignsNPC();

        if (OlympiadConfig.ENABLE_OLYMPIAD) {
            Olympiad.load();
            Hero.getInstance();
        }

        PetitionManager.getInstance();

        CursedWeaponsManager.getInstance();

        if (!ServerConfig.ALLOW_WEDDING) {
            CoupleManager.getInstance();
            LOGGER.info("CoupleManager initialized");
        }

        ItemHandler.getInstance();

        AdminCommandHandler.getInstance().log();
        UserCommandHandler.getInstance().log();
        VoicedCommandHandler.getInstance().log();
        NpcDialogAppenderHolder.getInstance().log();
        BbsHandlerHolder.getInstance().log();
        BypassHolder.getInstance().log();
        OnShiftActionHolder.getInstance().log();

        if (ExtConfig.EX_JAPAN_MINIGAME) {
            CharacterMinigameScoreDAO.getInstance().select();
        }

        // start tasks
        TaskLoader.getInstance().load(scheduler);

        LOGGER.info("=[Events]=========================================");
        ResidenceHolder.getInstance().callInit();
        EventHolder.getInstance().callInit();
        LOGGER.info("==================================================");

        CastleManorManager.getInstance();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        LOGGER.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        CoupleManager.getInstance();

        if (AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance();
        }

        HellboundManager.getInstance();

        NaiaTowerManager.getInstance();
        NaiaCoreManager.getInstance();

        SoDManager.getInstance();
        SoIManager.getInstance();

        MiniGameScoreManager.getInstance();

        GmManager.load();

        ItemAuctionManager.getInstance().load();

        //===============Секция загрузки произвольно-дополнительных систем
        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT)
            BotReportManager.getInstance();
        //Protection section
        Protection.getInstance();

        if (PhantomConfig.allowPhantoms)
            PhantomLoader.getInstance().load();
        //Protection section end
        //===============Конец

        Shutdown.getInstance().schedule(ServerConfig.RESTART_AT_TIME, Shutdown.RESTART);

        final GamePacketHandler gph = new GamePacketHandler();

        final InetAddress serverAddr = "*".equalsIgnoreCase(ServerConfig.GAMESERVER_HOSTNAME) ? null : InetAddress.getByName(ServerConfig.GAMESERVER_HOSTNAME);
        _selectorThreads = new SelectorThread<>(ServerConfig.SELECTOR_CONFIG, gph, gph, gph, null);
        _selectorThreads.openServerSocket(serverAddr, ServerConfig.PORTS_GAME);
        _selectorThreads.start();

        getListeners().onStart();

        if (ServicesConfig.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART) {
            LOGGER.info("Restoring offline traders...");
            final int count = TradeHelper.restoreOfflineTraders();
            LOGGER.info("Restored " + count + " offline traders.");
        }

        LOGGER.info("GameServer started.");

        AuthServerCommunication.getInstance().startThread();

        if (TelnetConfig.IS_TELNET_ENABLED) {
            telnetServer = new TelnetServer();
        } else {
            LOGGER.info("Telnet server is currently disabled.");
        }

        // Initialize Xml-Rpc server
        final PropertyHandlerMapping pMapping = new PropertyHandlerMapping();
        pMapping.addHandler("WorldHandler", WorldHandler.class);
        xmlRpcServer = new XmlRpcServer(ServerConfig.XML_RPC_SERVER_HOST, ServerConfig.XML_RPC_SERVER_PORT, pMapping);
        xmlRpcServer.startServer(ServerConfig.XML_RPC_CLIENT_HOST);

        LOGGER.info("=================================================");
        final String memUsage = String.valueOf(StatsUtils.getMemUsage());
        for (final String line : memUsage.split("\n")) {
            LOGGER.info(line);
        }
        LOGGER.info("=================================================");
    }

    public static GameServer getInstance() {
        return _instance;
    }

    public static void main(final String... args) throws Exception {
        new GameServer();
    }

    public static void checkFreePorts() {
        boolean binded = false;
        while (!binded) {
            try {
                final ServerSocket ss;
                if ("*".equalsIgnoreCase(ServerConfig.GAMESERVER_HOSTNAME)) {
                    ss = new ServerSocket(ServerConfig.PORTS_GAME);
                } else {
                    ss = new ServerSocket(ServerConfig.PORTS_GAME, 50, InetAddress.getByName(ServerConfig.GAMESERVER_HOSTNAME));
                }
                ss.close();
                binded = true;
            } catch (Exception e) {
                LOGGER.warn("Port " + ServerConfig.PORTS_GAME + " is allready binded. Please free it and restart server.");
                binded = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                }
            }
        }
    }

    public SelectorThread<GameClient> getSelectorThreads() {
        return _selectorThreads;
    }

    public int time() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public int uptime() {
        return time() - _serverStarted;
    }

    public GameServerListenerList getListeners() {
        return _listeners;
    }

    public <T extends GameListener> boolean addListener(final T listener) {
        return _listeners.add(listener);
    }

    public <T extends GameListener> boolean removeListener(final T listener) {
        return _listeners.remove(listener);
    }

    public Version getVersion() {
        return version;
    }

    public TelnetServer getTelnetServer() {
        return telnetServer;
    }

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Listeners globalListeners() {
        return globalListeners;
    }

    public class GameServerListenerList extends ListenerList<GameServer> {
        public void onStart() {
            getListeners(OnStartListener.class).forEach(OnStartListener::onStart);
        }

        public void onShutdown() {
            getListeners(OnShutdownListener.class).forEach(OnShutdownListener::onShutdown);
        }
    }
}