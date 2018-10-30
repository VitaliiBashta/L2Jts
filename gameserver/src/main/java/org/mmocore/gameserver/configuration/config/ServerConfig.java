package org.mmocore.gameserver.configuration.config;

import javafx.util.Pair;
import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.commons.net.nio.impl.SelectorConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.network.authcomm.ServerType;
import org.mmocore.gameserver.stats.stat.AddStatCreature;
import org.mmocore.gameserver.stats.stat.AddStatType;
import org.mmocore.gameserver.templates.item.ArmorTemplate;
import org.mmocore.gameserver.templates.item.EtcItemTemplate;
import org.mmocore.gameserver.templates.item.ItemType;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.version.Chronicle;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Create by Mangol on 02.12.2015.
 */
@Configuration("server.json")
public class ServerConfig {
    @Setting(ignore = true)
    public static final Chronicle CHRONICLE_VERSION = Chronicle.HIGH_FIVE;
    @Setting(ignore = true)
    public static final SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
    @Setting(ignore = true)
    public static final int CNAME_MAXLEN = 32;
    @Setting(name = "GameserverHostname")
    public static String GAMESERVER_HOSTNAME = "*";
    @Setting(name = "GameserverPort")
    public static int PORTS_GAME = 7777;
    @Setting(name = "ExternalHostname")
    public static String EXTERNAL_HOSTNAME = "*";
    @Setting(name = "InternalHostname")
    public static String INTERNAL_HOSTNAME = "*";
    @Setting(name = "IpConfigEnable")
    public static boolean IPCONFIG_ENABLE = false;
    @Setting(name = "LoginPort")
    public static int GAME_SERVER_LOGIN_PORT = 9013;
    @Setting(name = "LoginHost")
    public static String GAME_SERVER_LOGIN_HOST = "127.0.0.1";
    @Setting(name = "RequestServerID")
    public static int REQUEST_ID = 0;
    @Setting(name = "AcceptAlternateID")
    public static boolean ACCEPT_ALTERNATE_ID = true;
    @Setting(name = "XmlRpcServerEnabled")
    public static boolean XML_RPC_SERVER_ENABLED = false; //TODO : не работает, сделать.
    @Setting(name = "XmlRpcServerHost")
    public static String XML_RPC_SERVER_HOST = "127.0.0.1";
    @Setting(name = "XmlRpcServerPort")
    public static int XML_RPC_SERVER_PORT = 5600;
    @Setting(name = "XmlRpcClientHost")
    public static String XML_RPC_CLIENT_HOST = "127.0.0.1";
    @Setting(name = "Autosave")
    public static boolean AUTOSAVE = true;
    @Setting(name = "CnameTemplate")
    public static String CNAME_TEMPLATE;
    @Setting(name = "ClanNameTemplate")
    public static String CLAN_NAME_TEMPLATE;
    @Setting(name = "ClanTitleTemplate")
    public static String CLAN_TITLE_TEMPLATE;
    @Setting(name = "AllyNameTemplate")
    public static String ALLY_NAME_TEMPLATE;
    @Setting(name = "ServerType", method = "authServerType")
    public static int AUTH_SERVER_SERVER_TYPE;
    @Setting(name = "ServerAgeLimit")
    public static int AUTH_SERVER_AGE_LIMIT;
    @Setting(name = "ServerGMOnly")
    public static boolean AUTH_SERVER_GM_ONLY;
    @Setting(name = "ServerBrackets")
    public static boolean AUTH_SERVER_BRACKETS;
    @Setting(name = "PvPServer")
    public static boolean AUTH_SERVER_IS_PVP;
    @Setting(name = "MinProtocolRevision")
    public static int MIN_PROTOCOL_REVISION;
    @Setting(name = "MaxProtocolRevision")
    public static int MAX_PROTOCOL_REVISION;
    @Setting(name = "RateXp")
    public static double RATE_XP;
    @Setting(name = "RateSp")
    public static double RATE_SP;
    @Setting(name = "RateDropAdena")
    public static double RATE_DROP_ADENA;
    @Setting(name = "RateDropItems")
    public static double RATE_DROP_ITEMS;
    @Setting(name = "RateDropSpoil")
    public static double RATE_DROP_SPOIL;
    @Setting(name = "RateQuestsReward")
    public static double RATE_QUESTS_REWARD;
    @Setting(name = "RateRaidBoss")
    public static double RATE_DROP_RAIDBOSS;
    @Setting(name = "RateQuestsDrop")
    public static double RATE_QUESTS_DROP;
    @Setting(name = "RateSiegeGuard")
    public static double RATE_DROP_SIEGE_GUARD;
    @Setting(name = "RatePartyMin")
    public static boolean RATE_PARTY_MIN;
    @Setting(name = "RateHellboundConfidence")
    public static double RATE_HELLBOUND_CONFIDENCE;
    @Setting(name = "RateClanRepScore")
    public static double RATE_CLAN_REP_SCORE;
    @Setting(name = "RateManor")
    public static double RATE_MANOR;
    @Setting(name = "NoRateItemIds", canNull = true)
    public static int[] NO_RATE_ITEMS = new int[0];
    @Setting(name = "NoRateRecipes")
    public static boolean NO_RATE_RECIPES;
    @Setting(name = "NoRateEquipment")
    public static boolean NO_RATE_EQUIPMENT;
    @Setting(name = "NoRateKeyMaterial")
    public static boolean NO_RATE_KEY_MATERIAL;
    @Setting(name = "RateDropCommonItems")
    public static double RATE_DROP_COMMON_ITEMS;
    @Setting(name = "AltMultiDrop")
    public static boolean ALT_MULTI_DROP;
    @Setting(name = "RateClanRepScoreMaxAffected")
    public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
    @Setting(name = "RateMobSpawn")
    public static int RATE_MOB_SPAWN;
    @Setting(name = "RateMobMinLevel", maxValue = 100)
    public static int RATE_MOB_SPAWN_MIN_LEVEL;
    @Setting(name = "RateMobMaxLevel", maxValue = 100)
    public static int RATE_MOB_SPAWN_MAX_LEVEL;
    @Setting(name = "RateRaidRegen")
    public static double RATE_RAID_REGEN;
    @Setting(name = "RateRaidDefense")
    public static double RATE_RAID_DEFENSE;
    @Setting(name = "RateRaidAttack")
    public static double RATE_RAID_ATTACK;
    @Setting(name = "RateEpicDefense")
    public static double RATE_EPIC_DEFENSE = RATE_RAID_DEFENSE;
    @Setting(name = "RateEpicAttack")
    public static double RATE_EPIC_ATTACK = RATE_RAID_ATTACK;
    @Setting(name = "RaidMaxLevelDiff")
    public static int RAID_MAX_LEVEL_DIFF;
    @Setting(name = "ParalizeOnRaidLevelDiff")
    public static boolean PARALIZE_ON_RAID_DIFF;
    @Setting(name = "GlobalShout")
    public static boolean GLOBAL_SHOUT;
    public static boolean allowGlobalChatInReflections;
    @Setting(name = "GlobalTradeChat")
    public static boolean GLOBAL_TRADE_CHAT;
    @Setting(name = "ChatRange")
    public static int CHAT_RANGE;
    @Setting(name = "ShoutOffset")
    public static int SHOUT_OFFSET;
    @Setting(name = "PremiumHeroChat")
    public static boolean PREMIUM_HEROCHAT;
    @Setting(name = "ChatMessageLimit")
    public static int CHAT_MESSAGE_MAX_LEN;
    @Setting(name = "LogChat")
    public static boolean LOG_CHAT;
    @Setting(name = "ABUSEWORD_BANCHAT")
    public static boolean ABUSEWORD_BANCHAT;
    @Setting(name = "ABUSEWORD_BAN_CHANNEL")
    public static int[] BAN_CHANNEL_LIST = new int[0];
    @Setting(name = "ABUSEWORD_UNBAN_TIMER")
    public static int ABUSEWORD_BANTIME;
    @Setting(name = "ABUSEWORD_REPLACE")
    public static boolean ABUSEWORD_REPLACE;
    @Setting(name = "ABUSEWORD_REPLACE_STRING")
    public static String ABUSEWORD_REPLACE_STRING;
    @Setting(name = "BANCHAT_ANNOUNCE")
    public static boolean BANCHAT_ANNOUNCE;
    @Setting(name = "BANCHAT_ANNOUNCE_FOR_ALL_WORLD")
    public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
    @Setting(name = "BANCHAT_ANNOUNCE_NICK")
    public static boolean BANCHAT_ANNOUNCE_NICK;
    public static boolean BANCHAT_PRIVATE_NICK;
    @Setting(name = "ChatFilterMinLevel")
    public static int CHATFILTER_MIN_LEVEL;
    @Setting(name = "ChatFilterChannels")
    public static int[] CHATFILTER_CHANNELS = new int[0];
    @Setting(name = "ChatFilterWorkType")
    public static int CHATFILTER_WORK_TYPE;
    @Setting(name = "MinNPCAnimation")
    public static int MIN_NPC_ANIMATION;
    @Setting(name = "MaxNPCAnimation")
    public static int MAX_NPC_ANIMATION;
    @Setting(name = "ServerSideNpcName")
    public static boolean SERVER_SIDE_NPC_NAME;
    @Setting(name = "ServerSideNpcTitle")
    public static boolean SERVER_SIDE_NPC_TITLE;
    @Setting(name = "ScheduledThreadPoolSize", method = "scheduledThreadPoolSize")
    public static int SCHEDULED_THREAD_POOL_SIZE;
    @Setting(name = "ExecutorThreadPoolSize", method = "executorThreadPoolSize")
    public static int EXECUTOR_THREAD_POOL_SIZE;
    @Setting(name = "EnableRunnableStats")
    public static boolean ENABLE_RUNNABLE_STATS;
    @Setting(name = "EffectTaskManagers")
    public static int EFFECT_TASK_MANAGER_COUNT = 2;
    @Setting(name = "DefaultLang")
    public static String DEFAULT_LANG;
    @Setting(name = "DeleteCharAfterDays")
    public static int DELETE_DAYS;
    @Setting(name = "DatapackRoot", method = "datapackRoot")
    public static File DATAPACK_ROOT;
    @Setting(name = "AutoRestartAt", canNull = true)
    public static String RESTART_AT_TIME = "0 0 5 * * ?";
    @Setting(name = "ShowGMLogin")
    public static boolean SHOW_GM_LOGIN;
    @Setting(name = "GmEnterInvisible")
    public static boolean GM_ENTER_INVISIBLE;
    @Setting(name = "GmEnterInvulnerable")
    public static boolean GM_ENTER_INVULNERABLE;
    @Setting(name = "GmIgnoreFriendRequest")
    public static boolean GM_IGNORE_FRIEND_REQUEST;
    @Setting(name = "GmIgnorePrivateMessages")
    public static boolean GM_IGNORE_PRIVATE_MESSAGES;
    @Setting(name = "HShift")
    public static int SHIFT_BY;
    @Setting(name = "VShift")
    public static int SHIFT_BY_Z;
    @Setting(name = "MapMinZ")
    public static int MAP_MIN_Z;
    @Setting(name = "MapMaxZ")
    public static int MAP_MAX_Z;
    @Setting(name = "DamageFromFalling")
    public static boolean DAMAGE_FROM_FALLING;
    @Setting(name = "AllowCursedWeapons")
    public static boolean ALLOW_CURSED_WEAPONS;
    @Setting(name = "DropCursedWeaponsOnKick")
    public static boolean DROP_CURSED_WEAPONS_ON_KICK;
    @Setting(name = "AllowWedding")
    public static boolean ALLOW_WEDDING;
    @Setting(name = "WeddingPrice")
    public static int WEDDING_PRICE;
    @Setting(name = "WeddingPunishInfidelity")
    public static boolean WEDDING_PUNISH_INFIDELITY;
    @Setting(name = "WeddingTeleport")
    public static boolean WEDDING_TELEPORT;
    @Setting(name = "WeddingTeleportPrice")
    public static int WEDDING_TELEPORT_PRICE;
    @Setting(name = "WeddingTeleportInterval")
    public static int WEDDING_TELEPORT_INTERVAL;
    @Setting(name = "WeddingAllowSameSex")
    public static boolean WEDDING_SAMESEX;
    @Setting(name = "WeddingFormalWear")
    public static boolean WEDDING_FORMALWEAR;
    @Setting(name = "WeddingDivorceCosts")
    public static int WEDDING_DIVORCE_COSTS;
    @Setting(name = "StartWithoutSpawn")
    public static boolean DONTLOADSPAWN;
    @Setting(name = "StartWithoutQuest")
    public static boolean DONTLOADQUEST;
    @Setting(name = "MaxSpawnNumPerOneTick")
    public static int MAX_SPAWN_NUM_PER_ONE_TICK;
    @Setting(name = "MaxReflectionsCount")
    public static int MAX_REFLECTIONS_COUNT;
    @Setting(name = "MovePacketDelay")
    public static int MOVE_PACKET_DELAY;
    @Setting(name = "AttackPacketDelay")
    public static int ATTACK_PACKET_DELAY;
    @Setting(name = "AttackEndDelay")
    public static int ATTACK_END_DELAY;
    @Setting(name = "UserInfoInterval")
    public static long USER_INFO_INTERVAL;
    @Setting(name = "BroadcastCharInfoInterval")
    public static long BROADCAST_CHAR_INFO_INTERVAL;
    @Setting(name = "MaximumOnlineUsers")
    public static int MAXIMUM_ONLINE_USERS;
    @Setting(name = "CharNumberPerServer")
    public static int CHAR_NUMBER_PER_SERVER;
    @Setting(name = "AutoDestroyDroppedItemAfter")
    public static int AUTODESTROY_ITEM_AFTER;
    @Setting(name = "AutoDestroyPlayerDroppedItemAfter")
    public static int AUTODESTROY_PLAYER_ITEM_AFTER;
    @Setting(name = "AllowWarehouse")
    public static boolean ALLOW_WAREHOUSE;
    @Setting(name = "AllowMail")
    public static boolean ALLOW_MAIL;
    @Setting(name = "WearDelay")
    public static int WEAR_DELAY;
    @Setting(name = "AllowDiscardItem")
    public static boolean ALLOW_DISCARDITEM;
    @Setting(name = "AllowWater")
    public static boolean ALLOW_WATER;
    @Setting(name = "EverybodyHasAdminRights")
    public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
    @Setting(name = "HtmCacheMode")
    public static int HTM_CACHE_MODE = HtmCache.LAZY;
    @Setting(splitter = ":", name = "MailLimitsPerDay")
    public static int[] MAIL_LIMITS_PER_DAY;
    @Setting(name = "ReceiverDelayMail", increase = 1000)
    public static int receiverDelayMail;
    @Setting(name = "SenderDelayMail", increase = 1000)
    public static int senderDelayMail;
    public static boolean customShutdownMessages;
    public static double recipeRate;
    public static double materialRate;
    public static double pieceRate;
    public static String[] disallowedNames;
    public static String[] disallowedTitles;
    @Setting(name = "add_stat_creature", method = "addStatCreature", canNull = true)
    public static List<Pair<AddStatCreature, AddStatType[]>> ADD_STAT_CREATURE = Collections.emptyList();
    @Setting(method = "parseNotRatedItems")
    public static Set<ItemType> notRatedTypes = new HashSet<>();
    public static double rateChance;
    @Setting(ignore = true)
    private final int NCPUS = Runtime.getRuntime().availableProcessors();

    private void parseNotRatedItems(final String value) {
        List<Class<? extends Enum<? extends ItemType>>> enums = new ArrayList<>(3);
        enums.add(WeaponTemplate.WeaponType.class);
        enums.add(ArmorTemplate.ArmorType.class);
        enums.add(EtcItemTemplate.EtcItemType.class);
        List<Enum<? extends ItemType>> eValues = new ArrayList<>();
        enums.forEach(c -> eValues.addAll(Arrays.asList(c.getEnumConstants())));
        StringTokenizer st = new StringTokenizer(value, ",");
        while (st.hasMoreTokens()) {
            String eName = st.nextToken().trim();
            eValues.stream()
                    .filter(e -> e.name().equalsIgnoreCase(eName))
                    .forEach(e -> notRatedTypes.add((ItemType) e));
        }
    }

    private void authServerType(final String value) {
        final ServerType t = ServerType.valueOf(value.toUpperCase());
        AUTH_SERVER_SERVER_TYPE |= t.getMask();
    }

    private void scheduledThreadPoolSize(final int value) {
        int val = value;
        if (val <= 0) {
            val = NCPUS * 4;
        }
        SCHEDULED_THREAD_POOL_SIZE = val;
    }

    private void executorThreadPoolSize(final int value) {
        int val = value;
        if (val <= 0) {
            val = NCPUS * 2;
        }
        EXECUTOR_THREAD_POOL_SIZE = val;
    }

    private void datapackRoot(final String value) {
        try {
            DATAPACK_ROOT = new File(value).getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStatCreature(final String value) {
        final List<Pair<AddStatCreature, AddStatType[]>> list = new ArrayList<>();
        final String[] typeCreatureBlock = value.split(";");
        for (final String typeBlock : typeCreatureBlock) {
            final String[] blocks = typeBlock.split("@");
            final AddStatCreature statCreature = AddStatCreature.valueOf(blocks[0]);
            final String[] typeStats = blocks[1].split(":");
            final AddStatType[] types = new AddStatType[typeStats.length];
            IntStream.range(0, typeStats.length).forEach(i -> types[i] = AddStatType.valueOf(typeStats[i]));
            list.add(new Pair<>(statCreature, types));
        }
        ADD_STAT_CREATURE = list;
    }
}
