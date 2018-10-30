package org.mmocore.gameserver.object.components.variables;

/**
 * @author Java-man
 */
public enum PlayerVariables implements Variables {
    OFFLINE,

    NAME_COLOR,
    TITLE_COLOR,

    NO_DROP_PK,

    TEMPORAL_HERO,

    OLD_TITLE,

    REFLECTION,
    BACK_COORDINATES,
    LAST_HERO_BACK_COORDINATES,
    TEAM_VS_TEAM_BACK_COORDINATES,

    LOST_EXP,

    JAILED,
    JAILED_FROM,

    TRADE_BAN,

    NEVIT,

    B_MARKET_ADENA,

    MY_BIRTHDAY_RECEIVE_YEAR,

    AUTO_LOOT,
    AUTO_LOOT_HERBS,

    STORE_MODE,
    PACKAGE_SELL_LIST,
    SELL_LIST,
    SELL_STORE_NAME,
    BUY_LIST,
    BUY_STORE_NAME,
    CREATE_LIST,
    MANUFACTURE_NAME,

    // из .cfg
    LANG,
    DROPLIST_ICONS,
    NO_EXP,
    NO_TRADERS,
    NO_ANIMATION_OF_CAST,
    NO_SHIFT,
    TRANSLIT,
    NO_CARRIER_TIME,

    CAN_WAREHOUSE_WITHDRAW,

    // квесты
    QUEST_STATE_OF_255_BLUE_GEM_GIVEN,
    QUEST_STATE_OF_255_ADENA_GIVEN,
    QUEST_STATE_OF_255_TUTORIAL_VOICE_0,
    QUEST_STATE_OF_255_TUTORIAL_VOICE_1,
    QUEST_STATE_OF_726,
    QUEST_STATE_OF_727,
    QUEST_STATE_OF_132_ORE_GIVEN,
    QUEST_STATE_OF_450,

    // Newbie Guide Quests
    NG_57,
    // Newbie Guide
    NG_207,
    NG_208,

    // еще квесты
    NEWBIE_WEAPON,
    NEWBIE_ARMOR,
    DC_BACK_COORDS,

    // еще квесты
    DD1,
    DD2,
    DD3,

    // Флаг для квестов на первую профессию.
    profession_145,

    PROF2_1,
    PROF2_2,
    PROF2_3,

    GM_TRACE,
    PENALTY_CHAT_COUNT,
    BAN_CHAT_COUNT,
    LAST_BAN_CHAT_DAY_TIME,
    FESTIVAL_BACK_COORDS,

    BAIUM_PERMISSION,

    LAST_LURE,

    REC_LEFT_TODAY,

    EXPAND_PRIVATE_STORE,
    EXPAND_INVENTORY,
    EXPAND_WAHEHOUSE,

    BUY_LIMITED_HOURGLASS_20,
    BUY_LIMITED_HOURGLASS_59,
    BUY_LIMITED_HOURGLASS_91,
    BUY_LIMITED_HOURGLASS_112,
    BUY_LIMITED_HOURGLASS_136,
    BUY_LIMITED_HOURGLASS_155,
    BUY_LIMITED_HOURGLASS_165,
    BUY_LIMITED_NEVITS_VOICE,
    BUY_LIMITED_XMAS_PACK,
    BUY_LIMITED_FREYA_PRESENT,
    BUY_LIMITED_VITALITY_BUFF,
    BUY_LIMITED_YOGI_SCROLL,

    EVENT_GIVED_EXP,
    EVENT_CURRENT_DAY_REWARD,

    MAGUEN_PARAM,

    //Dress system
    HIDE_VISUAL,
    TALISMAN_STACK,

    ITEM_922,
    SUBSCRIPTION,
    SUBSCRIPTION_DAY_GIFT,
    SNOOP_TARGET(false);

    private final boolean keepInDatabase;

    PlayerVariables() {
        keepInDatabase = true;
    }

    PlayerVariables(final boolean keepInDatabase) {
        this.keepInDatabase = keepInDatabase;
    }

    @Override
    public boolean isKeepInDatabase() {
        return keepInDatabase;
    }
}
