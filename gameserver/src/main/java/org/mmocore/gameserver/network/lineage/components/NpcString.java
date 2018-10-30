package org.mmocore.gameserver.network.lineage.components;

import java.util.NoSuchElementException;

/**
 * @author VISTALL
 * @date 15:13/28.12.2010
 */
public enum NpcString {
    NONE(-1),
    // Text: What did you just do to me?
    WHAT_DID_YOU_JUST_DO_TO_ME(2004),
    // Text: Are you trying to tame me? Don't do that!
    ARE_YOU_TRYING_TO_TAME_ME_DONT_DO_THAT(2005),
    // Text: Don't give such a thing.  You can endanger yourself!
    DONT_GIVE_SUCH_A_THING(2006),
    // Text: Yuck!  What is this?  It tastes terrible!
    YUCK__WHAT_IS_THIS__IT_TASTES_TERRIBLE(2007),
    // Text: I'm hungry.  Give me a little more, please.
    IM_HUNGRY(2008),
    // Text: Who awoke me?
    WHO_AWOKE_ME(2150),
    // Text: My master has instructed me to be your guide, %s.
    MY_MASTER_HAS_INSTRUCTED_ME_TO_BE_YOUR_GUIDE_S1(2151),
    // Text: Please check this bookcase, %s.
    PLEASE_CHECK_THIS_BOOKCASE_S1(2152),
    // Text: That sign!
    THAT_SIGN(2450),
    // Text: Did you call me, $s1?
    DID_YOU_CALL_ME_S1(2250),
    // Text: That box was sealed by my master, $s1! Don't touch it!
    THAT_BOX_WAS_SEALED_BY_MY_MASTER_S1_DONT_TOUCH_IT(2550),
    // Text: You've ended my immortal life! You're protected by the feudal lord, aren't you?
    YOUVE_ENDED_MY_IMMORTAL_LIFE_YOURE_PROTECTED_BY_THE_FEUDAL_LORD_ARENT_YOU(2551),
    // Text: I'm confused! Maybe it's time to go back.
    IM_CONFUSED_MAYBE_ITS_TIME_TO_GO_BACK(2251),
    // Text: What is this?  Is this edible?
    WHAT_IS_THIS__IS_THIS_EDIBLE(2009),
    // Text: Don't worry about me.
    DONT_WORRY_ABOUT_ME(2010),
    // Text: Thank you.  That was delicious!
    THANK_YOU(2011),
    // Text: I think I am starting to like you!
    I_THINK_I_AM_STARTING_TO_LIKE_YOU(2012),
    // Text: Eeeeek!  Eeeeek!
    EEEEEK__EEEEEK(2013),
    // Text: Don't keep trying to tame me.  I don't want to be tamed.
    DONT_KEEP_TRYING_TO_TAME_ME(2014),
    //Text: %s! I must kill you. Blame your own curiosity.
    S1_I_MUST_KILL_YOU_BLAME_YOUR_OWN_CURIOSITY(6051),
    // Text: You have good luck. I shall return.
    YOU_HAVE_GOOD_LUCK_I_SHALL_RETURN(6052),
    // Text: You are strong. This was a mistake.
    YOU_ARE_STRONG_THIS_WAS_A_MISTAKE(6053),
    //Text: Who are you to join in the battle? How upsetting.
    WHO_ARE_YOU_TO_JOIN_IN_THE_BATTLE_HOW_UPSETTING(6054),
    //Text: %s, did you come to help me?
    S1_DID_YOU_COME_TO_HELP_ME(6451),
    //Text: Drats! How could I be so wrong??
    DRATS_HOW_COULD_I_BE_SO_WRONG(6551),
    //Text: %s! Step back from the confounded box! I will take it myself!
    S1_STEP_BACK_FROM_THE_CONFOUNDED_BOX_I_WILL_TAKE_IT_MYSELF(6552),
    //Text: %s! I will be back soon. Stay there and don't you dare wander off!
    S1_I_WILL_BE_BACK_SOON_STAY_THERE_AND_DONT_YOU_DARE_WANDER_OFF(6553),
    //Text: Grr. I've been hit...
    GRR_IVE_BEEN_HIT(6554),
    //Text: Grr! Who are you and why have you stopped me?
    GRR_WHO_ARE_YOU_AND_WHY_HAVE_YOU_STOPPED_ME(6555),
    // Text: I am late!
    I_AM_LATE(6556),
    //Text: Good luck!
    GOOD_LUCK(6557),
    // Text: %s! You seek the forbidden knowledge and I cannot let you have it!
    S1_YOU_SEEK_THE_FORBIDDEN_KNOWLEDGE_AND_I_CANNOT_LET_YOU_HAVE_IT(6750),
    // Text: Is this all I am allowed to have?...
    IS_THIS_ALL_I_AM_ALLOWED_TO_HAVE(6751),
    // Text: You defeated me, but our doom approaches...
    YOU_DEFEATED_ME_BUT_OUR_DOOM_APPROACHES(6752),
    // Text: %s! Who are you? Why are you bothering my minions?
    S1_WHO_ARE_YOU_WHY_ARE_YOU_BOTHERING_MY_MINIONS(6753),
    // Text: Beefcake!!
    BEEFCAKE(6754),
    // Text: Grr! Why are you sticking your nose in our business?
    GRR_WHY_ARE_YOU_STICKING_YOUR_NOSE_IN_OUR_BUSINESS(6755),
    // Text: Farewell and watch your back!
    FAREWELL_AND_WATCH_YOUR_BACK(6756),
    // Text: Kamael! Good to see you. I have something to ask you...
    KAMAEL_GOOD_TO_SEE_YOU_I_HAVE_SOMETHING_TO_ASK_YOU(6757),
    // Text: %s! Go get him!!
    S1_GO_GET_HIM(6758),
    // Text: %s! What are you doing? Attack him!
    S1_WHAT_ARE_YOU_DOING_ATTACK_HIM(6759),
    // Text: %s! Is ? your full potential?
    S1_IS_YOUR_FULL_POTENTIAL(6760),
    // Text: Thanks! I must go and hunt down those that oppose me.
    THANKS_I_MUST_GO_AND_HUNT_DOWN_THOSE_THAT_OPPOSE_ME(6761),
    // Text: You are so stubborn... I must follow him now...
    YOU_ARE_SO_STUBBORN_I_MUST_FOLLOW_HIM_NOW(6762),
    // Text: Seek enlightenment from the Tablet.
    SEEK_ENLIGHTENMENT_FROM_THE_TABLE(6763),
    // Text: Arrogant beings! You are all doomed!
    ARROGANT_BEINGS_YOU_ARE_ALL_DOOMED(6764),
    // Text: My time in your world has come to an end. Consider yourselves lucky...
    MY_TIME_IN_YOUR_WORLD_HAS_COME_TO_AN_END_CONSIDER_YOURSELVES_LUCKY(6765),
    // Text: %s! How dare you!!!
    S1_HOW_DARE_YOU(6766),
    // Text: %s! Ahhaa! Your god forsakes you!
    S1_AHHAA_YOUR_GOD_FORSAKES_YOU(6767),
    // Text: You would fight me, a messenger of the gods?
    YOU_WOULD_FIGHT_ME_A_MESSENGER_OF_THE_GODS(8466),
    // Text: The door to the 3rd floor of the altar is now open.
    THE_DOOR_TO_THE_3RD_FLOOR_OF_THE_ALTAR_IS_NOW_OPEN(10079),
    // Text: You, $s1, you attacked Wendy. Prepare to die!
    YOU_S1_YOU_ATTACKED_WENDY_PREPARE_TO_DIE(11450),
    //Text: %s, your enemy was driven out. I will now withdraw and await your next command.
    S1_YOUR_ENEMY_WAS_DRIVEN_OUT_I_WILL_NOW_WITHDRAW_AND_AWAIT_YOUR_NEXT_COMMAND(11451),
    // Text: This enemy is far too powerful for me to fight. I must withdraw.
    THIS_ENEMY_IS_FAR_TOO_POWERFUL_FOR_ME_TO_FIGHT_I_MUST_WITHDRAW(11452),
    // Text: The radio signal detector is responding. # A suspicious pile of stones catches your eye.
    THE_RADIO_SIGNAL_DETECTOR_IS_RESPONDING_A_SUSPICIOUS_PILE_OF_STONES_CATCHES_YOUR_EYE(11453),
    //Text: The Veiled Creator...
    THE_VEILED_CREATOR(17951),
    //Text: The Conspiracy of the Ancient Race
    THE_CONSPIRACY_OF_THE_ANCIENT_RACE(17952),
    //Text: Chaos and Time...
    CHAOS_AND_TIME(17953),
    // Text: Intruder Alert! The alarm will self-destruct in 2 minutes.
    INTRUDER_ALERT_THE_ALARM_WILL_SELF_DESTRUCT_IN_2_MINUTES(18451),
    // Text: 18452	a,The alarm will self-destruct in 60 seconds. Enter passcode to override.\0
    THE_ALARM_WILL_SELF_DESTRUCT_IN_60_SECOND_ENTER_PASSCODE_TO_OVERRIDE(18452),
    // Text: 18453	a,The alarm will self-destruct in 30 seconds. Enter passcode to override.\0
    THE_ALARM_WILL_SELF_DESTRUCT_IN_30_SECOND_ENTER_PASSCODE_TO_OVERRIDE(18453),
    // Text: 18454	a,The alarm will self-destruct in 10 seconds. Enter passcode to override.\0
    THE_ALARM_WILL_SELF_DESTRUCT_IN_10_SECOND_ENTER_PASSCODE_TO_OVERRIDE(18454),
    // Text: 18455	a,Recorder crushed.\0
    RECORDER_CRUSHED(18455),
    //Text: Next time, you will not escape!
    NEXT_TIME_YOU_WILL_NOT_ESCAPE(19305),
    //Text: %s! You may have won this time... But next time, I will surely capture you!
    S1_YOU_MAY_HAVE_WON_THIS_TIME_BUT_NEXT_TIME_I_WILL_SURELY_CAPTURE_YOU(19306),
    // Text: Intruder! Protect the Priests of Dawn!
    INTRUDER_PROTECT_THE_PRIESTS_OF_DAWN(19504),
    // Text: Who are you?! A new face like you can't approach this place!
    WHO_ARE_YOU_A_NEW_FACE_LIKE_YOU_CANT_APPROACH_THIS_PLACE(19505),
    //Text: You are not the owner of that item.
    YOU_ARE_NOT_THE_OWNER_OF_THAT_ITEM(19806),
    //Text: I absolutely cannot give it to you! It is my precious jewel!
    I_ABSOLUTELY_CANNOT_GIVE_IT_TO_YOU_IT_IS_MY_PRECIOUS_JEWEL(22933),
    //Text: I'll take your lives later!
    ILL_TAKE_YOUR_LIVES_LATER(22934),
    //Text: That sword is really...!
    THAT_SWORD_IS_REALLY(22935),
    //Text: No! I haven't completely finished the command for destruction and slaughter yet!!!
    NO_I_HAVENT_COMPLETELY_FINISHED_THE_COMMAND_FOR_DESTRUCTION_AND_SLAUGHTER_YET(22936),
    // Text: Who dares to try and steal my noble blood?
    WHO_DARES_TO_AND_STEAL_MY_NOBLE_BLOOD(23434),
    // Text: %s! Finally, we meet!
    S1_FINALLY_WE_MEET(23651),
    // Text: Hmm, where did my friend go?
    HMM_WHERE_DID_MY_FRIEND_GO(23652),
    // Text: %s! Did you wait for long?
    S1_DID_YOU_WAIT_FOR_LONG(23661),
    // Text: Did you bring what I asked, %s?
    DID_YOU_BRING_WHAT_I_ASKED_S1(23671),
    // Text: Best of luck with your future endeavours.
    BEST_OF_LUCK_WITH_YOUR_FUTURE_ENDEAVOURS(23653),
    // Text: Hmm? Is someone approaching?
    HMM_IS_SOMEONE_APPROACHING(23681),
    // Text: Graaah, we're being attacked!
    GRAAAH_WERE_BEING_ATTACKED(23682),
    // Text: %s, has everything been found?
    S1_HAS_EVERYTHING_BEEN_FOUND(23685),
    // Text: In that case, I wish you good luck.
    IN_THAT_CASE_I_WISH_YOU_GOOD_LUCK(23683),
    // Text: Safe travels!
    SAFE_TRAVELS(23687),
    //Text: Don't interrupt my rest again
    DONT_INTERRUPT_MY_REST_AGAIN(33409),
    //Text: You're a great devil now...
    YOURE_A_GREAT_DEVIL_NOW(33410),
    //Text: Oh, it's not an opponent of mine. Ha, ha, ha!
    OH_ITS_NOT_AN_OPPONENT_OF_MINE_HA_HA_HA(33411),
    //Text: Oh... Great Demon King...
    OH_GREAT_DEMON_KING(33412),
    //Text: Revenge is Overlord Ramsebalius of the evil world!
    REVENGE_IS_OVERLORD_RAMSEBALIUS_OF_THE_EVIL_WORLD(33413),
    //Text: Bonaparterius, Abyss King, will punish you
    BONAPARTERIUS_ABYSS_KING_WILL_PUNISH_YOU(33414),
    //Text: OK, everybody pray fervently!
    OK_EVERYBODY_PRAY_FERVENTLY(33415),
    //Text: Both hands to heaven! Everybody yell together!
    BOTH_HANDS_TO_HEAVEN_EVERYBODY_YELL_TOGETHER(33416),
    //Text: One! Two! May your dreams come true!
    ONE_TWO_MAY_YOUR_DREAMS_COME_TRUE(33417),
    //Text: Who killed my underling devil?
    WHO_KILLED_MY_UNDERLING_DEVIL(33418),
    //Text: I will make your love come true~ love, love, love~
    I_WILL_MAKE_YOUR_LOVE_COME_TRUE_LOVE_LOVE_LOVE(33420),
    //Text: I have wisdom in me. I am the box of wisdom!
    I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM(33421),
    //Text: Oh, oh, oh!
    OH_OH_OH(33422),
    //Text: Do you want us to love you? Oh.
    DO_YOU_WANT_US_TO_LOVE_YOU_OH(33423),
    //Text: Who is calling the Lord of Darkness!
    WHO_IS_CALLING_THE_LORD_OF_DARKNESS(33424),
    //Text: I am a great empire, Bonaparterius!
    I_AM_A_GREAT_EMPIRE_BONAPARTERIUS(33425),
    //Text: Let your head down before the Lord!
    LET_YOUR_HEAD_DOWN_BEFORE_THE_LORD(33426),
    // Text: Ha, that was fun! If you wish to find the key, search the corpse.
    HA_THAT_WAS_FUN_IF_YOU_WISH_TO_FIND_THE_KEY_SEARCH_THE_CORPSE(34830),
    // Text: You fools will get what's coming to you!
    YOU_FOOLS_WILL_GET_WHATS_COMING_TO_YOU(34832),
    // Text: You guys wouldn't know... the seven seals are...  Arrrgh!
    YOU_GUYS_WOULDNT_KNOW(34835),
    // Text: That doesn't belong to you. Don't touch it!
    THAT_DOESNT_BELONG_TO_YOU(34837),
    // Text: Get out of my sight, you infidels!
    GET_OUT_OF_MY_SIGHT_YOU_INFIDELS(34838),
    // Text: I have the key. Why don't you come and take it?
    I_HAVE_THE_KEY(34831),
    // Text: Sorry about this, but I must kill you now.
    SORRY_ABOUT_THIS_BUT_I_MUST_KILL_YOU_NOW(34833),
    // Text: I shall drench this mountain with your blood!
    I_SHALL_DRENCH_THIS_MOUNTAIN_WITH_YOUR_BLOOD(34836),
    // Text: We don't have any further business to discuss... Have you searched the corpse for the key?
    WE_DONT_HAVE_ANY_FURTHER_BUSINESS_TO_DISCUSS(34839),
    // Text: Who dares summon the Merchant of Mammon?!
    WHO_DARES_SUMMON_THE_MERCHANT_OF_MAMMON(19604),
    // Text: We will be with you always...
    WE_WILL_BE_WITH_YOU_ALWAYS(19805),
    // Text: We'll take the property of the ancient empire!
    WELL_TAKE_THE_PROPERTY_OF_THE_ANCIENT_EMPIRE(33511),
    // Text: Show me the pretty sparkling things! They're all mine!
    SHOW_ME_THE_PRETTY_SPARKLING_THINGS_THEY_RE_ALL_MINE(33512),
    // Text: You childish fool, do you think you can catch me?
    YOU_CHILDISH_FOOL_DO_YOU_THINK_YOU_CAN_CATCH_ME(40306),
    // Text: I must do something about this shameful incident...
    I_MUST_DO_SOMETHING_ABOUT_THIS_SHAMEFUL_INCIDENT(40307),
    // Text: $s1 received a $s2 item as a reward from the separated soul.
    S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL(45650),
    // Text: The sacred flame is ours!
    THE_SACRED_FLAME_IS_OURS(40909),
    // Text: As you wish, master!
    AS_YOU_WISH_MASTER(40913),
    // Text: Arrghh...we shall never.. surrender...
    ARRGHH_WE_SHALL_NEVER_SURRENDER(40910),
    // Text: My dear friend of $s1, who has gone on ahead of me!
    MY_DEAR_FRIEND_OF_S1_WHO_HAS_GONE_ON_AHEAD_OF_ME(41651),
    //Text: Listen to Tejakar Gandi, young Oroka! The spirit of the slain leopard is calling you, %s!
    LISTEN_TO_TEJAKAR_GANDI_YOUNG_OROKA_THE_SPIRIT_OF_THE_SLAIN_LEOPARD_IS_CALLING_YOU_S1(41652),
    //Text: Hey! Everybody watch the eggs!
    HEY_EVERYBODY_WATCH_THE_EGGS(42046),
    //Text: I thought I'd caught one share... Whew!
    I_THOUGHT_ID_CAUGHT_ONE_SHARE_WHEW(42047),
    //Text: The stone... the Elven stone... broke...
    THE_STONE_THE_ELVEN_STONE_BROKE(42048),
    //Text: If the eggs get taken, we're dead!
    IF_THE_EGGS_GET_TAKEN_WERE_DEAD(42049),
    // Text: Give me a Fairy Leaf...!
    GIVE_ME_A_FAIRY_LEAF(42111),
    //Text: Why do you bother me again?
    WHY_DO_YOU_BOTHER_ME_AGAIN(42112),
    //Text: Hey, you've already drunk the essence of wind!
    HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_WIND(42113),
    //Text: Leave now, before you incur the wrath of the guardian ghost...
    LEAVE_NOW_BEFORE_YOU_INCUR_THE_WRATH_OF_THE_GUARDIAN_GHOST(42114),
    //Text: Hey, you've already drunk the essence of a star!
    HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_A_STAR(42115),
    //Text: Hey, you've already drunk the essence of dusk!
    HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_DUSK(42116),
    //Text: Hey, you've already drunk the essence of the abyss!
    HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_THE_ABYSS(42117),
    // Text: We must protect the fairy tree!
    WE_MUST_PROTECT_THE_FAIRY_TREE(42118),
    // Text: Get out of the sacred tree, you scoundrels!
    GET_OUT_OF_THE_SACRED_TREE_YOU_SCOUNDRELS(42119),
    // Text: Death to the thieves of the pure water of the world!
    DEATH_TO_THE_THIEVES_OF_THE_PURE_WATER_OF_THE_WORLD(42120),
    // Text: Att... attack... $s1. Ro... rogue... $s2..
    ATT__ATTACK__S1__RO__ROGUE__S2(46350),
    // Text: ##########Bingo!##########
    BINGO(50110),
    // Text: Blood and honor!
    BLOOD_AND_HONOR(50338),
    // Text: Curse of the gods on the one that defiles the property of the empire!
    CURSE_OF_THE_GODS_ON_THE_ONE_THAT_DEFIELS_THE_PROPERTY_OF_THE_EMPIRE(50339),
    // Text: War and death!
    WAR_AND_DEATH(50340),
    // Text: Ambition and power!
    AMBITION_AND_POWER(50341),
    // Text: The furnace will go out. Watch and see.
    THE_FURNACE_WILL_GO_OUT(60000),
    // Text: There's about 1 minute left!
    THERES_ABOUT_1_MINUTE_LEFT(60001),
    // Text: There's just 10 seconds left!
    THERES_JUST_10_SECONDS_LEFT(60002),
    // Text: Now, light the furnace's fire.
    NOW_LIGHT_THE_FURNACES_FIRE(60003),
    // Text: Time is up and you have failed. Any more will be difficult.
    TIME_IS_UP_AND_YOU_HAVE_FAILED(60004),
    // Text: Oh, you've succeeded.
    OH_YOUVE_SUCCEEDED(60005),
    // Text: Ah, is this failure? But it looks like I can keep going.
    AH_IS_THIS_FAILURE_BUT_IT_LOOKS_LIKE_I_CAN_KEEP_GOING(60006),
    // Text: Ah, I've failed. Going further will be difficult.
    AH_IVE_FAILED(60007),
    // Text: Furnace of Balance
    FURNACE_OF_BALANCE(60008),
    // Text: Furnace of Protection
    FURNACE_OF_PROTECTION(60009),
    // Text: Furnace of Will
    FURNACE_OF_WILL(60010),
    // Text: Furnace of Magic
    FURNACE_OF_MAGIC(60011),
    // Text: Divine energy is beginning to encircle.
    DIVINE_ENERGY_IS_BEGINNING_TO_ENCIRCLE(60012),
    // Text: For the glory of Solina!
    FOR_THE_GLORY_OF_SOLINA1(60013),
    // Text: Punish all those who tread footsteps in this place.
    PUNISH_ALL_THOSE_WHO_TREAD_FOOTSTEPS_IN_THIS_PLACE(60014),
    // Text: We are the sword of truth, the sword of Solina.
    WE_ARE_SWORD_OF_TRUSH_THE_SWORD_OF_SOLINA(60015),
    // Text: We raise our blades for the glory of Solina.
    WE_RAISE_OUR_BLADES_FOR_THE_GLORY_OF_SOLINA(60016),
    // Text: For the glory of Solina!
    FOR_THE_GLORY_OF_SOLINA2(60017),
    // Text: Who is calling me?
    WHO_IS_CALLING_ME(60403),
    // Text: Can light exist without darkness?
    CAN_LIGHT_EXIST_WITHOUT_DARKNESS(60404),
    //Text: Listen, you villagers. Our liege, who will soon become a lord, has defeated the Headless Knight. You can now rest easy!
    LISTEN_YOU_VILLAGERS_OUR_LIEGE_WHO_WILL_SOON_BECAME_A_LORD_HAS_DEFEATED_THE_HEADLESS_KNIGHT(70854),
    //$s1 has become lord of the Town of Gludio. Long may he reign!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GLUDIO(70859),
    //Text: $s1 has become lord of the Town of Dion. Long may he reign!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_DION(70959),
    // Text: $s1 has become the lord of the Town of Giran. May there be glory in the territory of Giran!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GIRAN(71059),
    // Text: $s1 has become the lord of the Town of Oren. May there be glory in the territory of Oren!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_OREN(71259),
    // Text: $s1 has become the lord of the Town of Aden. May there be glory in the territory of Aden!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN(71351),
    // Text: $s1 has become the lord of the Town of Schuttgart. May there be glory in the territory of Schuttgart!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_SCHUTTGART(71459),
    // Text: $s1 has become the lord of the Town of Innadril. May there be glory in the territory of Innadril!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_INNADRIL(71159),
    // Text: $s1 has become the lord of the Town of Rune. May there be glory in the territory of Rune!
    S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_RUNE(71659),
    // Text: $s1 Territory Badge(s) and $s2 Adenas
    S1_TERRITORY_BADGES_AND_S2_ADENAS(71753),
    // Text: 90 Territory Badges, $s1 score(s) in Individual Fame and $s2 Adenas
    _90_TERRITORY_BADGES_S1_SCORES_IN_INDIVIDUAL_FAME_AND_S2_ADENAS(71754),
    // Text: 90 Territory Badges, 450 scores in Individual Fame and $s2 Adenas
    _90_TERRITORY_BADGES_450_SCORES_IN_INDIVIDUAL_FAME_AND_S1_ADENAS(71755),
    // Text: Protect the catapult of Gludio!
    PROTECT_THE_CATAPULT_OF_GLUDIO(72951),
    // Text: Protect the catapult of Dion!
    PROTECT_THE_CATAPULT_OF_DION(72952),
    // Text: Protect the catapult of Giran!
    PROTECT_THE_CATAPULT_OF_GIRAN(72953),
    // Text: Protect the catapult of Oren!
    PROTECT_THE_CATAPULT_OF_OREN(72954),
    // Text: Protect the catapult of Aden!
    PROTECT_THE_CATAPULT_OF_ADEN(72955),
    // Text: Protect the catapult of Innadril!
    PROTECT_THE_CATAPULT_OF_INNADRIL(72956),
    // Text: Protect the catapult of Goddard!
    PROTECT_THE_CATAPULT_OF_GODDARD(72957),
    // Text: Protect the catapult of Rune!
    PROTECT_THE_CATAPULT_OF_RUNE(72958),
    // Text: Protect the catapult of Schuttgart!
    PROTECT_THE_CATAPULT_OF_SCHUTTGART(72959),
    // Text: The catapult of Gludio has been destroyed!
    THE_CATAPULT_OF_GLUDIO_HAS_BEEN_DESTROYED(72961),
    // Text: The catapult of Dion has been destroyed!
    THE_CATAPULT_OF_DION_HAS_BEEN_DESTROYED(72962),
    // Text: The catapult of Giran has been destroyed!
    THE_CATAPULT_OF_GIRAN_HAS_BEEN_DESTROYED(72963),
    // Text: The catapult of Oren has been destroyed!
    THE_CATAPULT_OF_OREN_HAS_BEEN_DESTROYED(72964),
    // Text: The catapult of Aden has been destroyed!
    THE_CATAPULT_OF_ADEN_HAS_BEEN_DESTROYED(72965),
    // Text: The catapult of Innadril has been destroyed!
    THE_CATAPULT_OF_INNADRIL_HAS_BEEN_DESTROYED(72966),
    // Text: The catapult of Goddard has been destroyed!
    THE_CATAPULT_OF_GODDARD_HAS_BEEN_DESTROYED(72967),
    // Text: The catapult of Rune has been destroyed!
    THE_CATAPULT_OF_RUNE_HAS_BEEN_DESTROYED(72968),
    // Text: The catapult of Schuttgart has been destroyed!
    THE_CATAPULT_OF_SCHUTTGART_HAS_BEEN_DESTROYED(72969),
    // Text: Protect the supplies safe of Gludio!
    PROTECT_THE_SUPPLIES_SAFE_OF_GLUDIO(73051),
    // Text: Protect the supplies safe of Dion!
    PROTECT_THE_SUPPLIES_SAFE_OF_DION(73052),
    // Text: Protect the supplies safe of Giran!
    PROTECT_THE_SUPPLIES_SAFE_OF_GIRAN(73053),
    // Text: Protect the supplies safe of Oren!
    PROTECT_THE_SUPPLIES_SAFE_OF_OREN(73054),
    // Text: Protect the supplies safe of Aden!
    PROTECT_THE_SUPPLIES_SAFE_OF_ADEN(73055),
    // Text: Protect the supplies safe of Innadril!
    PROTECT_THE_SUPPLIES_SAFE_OF_INNADRIL(73056),
    // Text: Protect the supplies safe of Goddard!
    PROTECT_THE_SUPPLIES_SAFE_OF_GODDARD(73057),
    // Text: Protect the supplies safe of Rune!
    PROTECT_THE_SUPPLIES_SAFE_OF_RUNE(73058),
    // Text: Protect the supplies safe of Schuttgart!
    PROTECT_THE_SUPPLIES_SAFE_OF_SCHUTTGART(73059),
    // Text: The supplies safe of Gludio has been destroyed!
    THE_SUPPLIES_SAFE_OF_GLUDIO_HAS_BEEN_DESTROYED(73061),
    // Text: The supplies safe of Dion has been destroyed!
    THE_SUPPLIES_SAFE_OF_DION_HAS_BEEN_DESTROYED(73062),
    // Text: The supplies safe of Giran has been destroyed!
    THE_SUPPLIES_SAFE_OF_GIRAN_HAS_BEEN_DESTROYED(73063),
    // Text: The supplies safe of Oren has been destroyed!
    THE_SUPPLIES_SAFE_OF_OREN_HAS_BEEN_DESTROYED(73064),
    // Text: The supplies safe of Aden has been destroyed!
    THE_SUPPLIES_SAFE_OF_ADEN_HAS_BEEN_DESTROYED(73065),
    // Text: The supplies safe of Innadril has been destroyed!
    THE_SUPPLIES_SAFE_OF_INNADRIL_HAS_BEEN_DESTROYED(73066),
    // Text: The supplies safe of Goddard has been destroyed!
    THE_SUPPLIES_SAFE_OF_GODDARD_HAS_BEEN_DESTROYED(73067),
    // Text: The supplies safe of Rune has been destroyed!
    THE_SUPPLIES_SAFE_OF_RUNE_HAS_BEEN_DESTROYED(73068),
    // Text: The supplies safe of Schuttgart has been destroyed!
    THE_SUPPLIES_SAFE_OF_SCHUTTGART_HAS_BEEN_DESTROYED(73069),
    // Text: Protect the Military Association Leader of Gludio!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GLUDIO(73151),
    // Text: Protect the Military Association Leader of Dion!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_DION(73152),
    // Text: Protect the Military Association Leader of Giran!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GIRAN(73153),
    // Text: Protect the Military Association Leader of Oren!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_OREN(73154),
    // Text: Protect the Military Association Leader of Aden!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_ADEN(73155),
    // Text: Protect the Military Association Leader of Innadril!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_INNADRIL(73156),
    // Text: Protect the Military Association Leader of Goddard!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GODDARD(73157),
    // Text: Protect the Military Association Leader of Rune!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_RUNE(73158),
    // Text: Protect the Military Association Leader of Schuttgart!
    PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_SCHUTTGART(73159),
    // Text: The Military Association Leader of Gludio is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_GLUDIO_IS_DEAD(73161),
    // Text: The Military Association Leader of Dion is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_DION_IS_DEAD(73162),
    // Text: The Military Association Leader of Giran is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_GIRAN_IS_DEAD(73163),
    // Text: The Military Association Leader of Oren is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_OREN_IS_DEAD(73164),
    // Text: The Military Association Leader of Aden is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_ADEN_IS_DEAD(73165),
    // Text: The Military Association Leader of Innadril is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_INNADRIL_IS_DEAD(73166),
    // Text: The Military Association Leader of Goddard is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_GODDARD_IS_DEAD(73167),
    // Text: The Military Association Leader of Rune is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_RUNE_IS_DEAD(73168),
    // Text: The Military Association Leader of Schuttgart is dead!
    THE_MILITARY_ASSOCIATION_LEADER_OF_SCHUTTGART_IS_DEAD(73169),
    // Text: Protect the Religious Association Leader of Gludio!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GLUDIO(73251),
    // Text: Protect the Religious Association Leader of Dion!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_DION(73252),
    // Text: Protect the Religious Association Leader of Giran!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GIRAN(73253),
    // Text: Protect the Religious Association Leader of Oren!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_OREN(73254),
    // Text: Protect the Religious Association Leader of Aden!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_ADEN(73255),
    // Text: Protect the Religious Association Leader of Innadril!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_INNADRIL(73256),
    // Text: Protect the Religious Association Leader of Goddard!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GODDARD(73257),
    // Text: Protect the Religious Association Leader of Rune!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_RUNE(73258),
    // Text: Protect the Religious Association Leader of Schuttgart!
    PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_SCHUTTGART(73259),
    // Text: The Religious Association Leader of Gludio is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GLUDIO_IS_DEAD(73261),
    // Text: The Religious Association Leader of Dion is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_DION_IS_DEAD(73262),
    // Text: The Religious Association Leader of Giran is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GIRAN_IS_DEAD(73263),
    // Text: The Religious Association Leader of Oren is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_OREN_IS_DEAD(73264),
    // Text: The Religious Association Leader of Aden is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_ADEN_IS_DEAD(73265),
    // Text: The Religious Association Leader of Innadril is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_INNADRIL_IS_DEAD(73266),
    // Text: The Religious Association Leader of Goddard is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GODDARD_IS_DEAD(73267),
    // Text: The Religious Association Leader of Rune is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_RUNE_IS_DEAD(73268),
    // Text: The Religious Association Leader of Schuttgart is dead!
    THE_RELIGIOUS_ASSOCIATION_LEADER_OF_SCHUTTGART_IS_DEAD(73269),
    // Text: Protect the Economic Association Leader of Gludio!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_GLUDIO(73351),
    // Text: Protect the Economic Association Leader of Dion!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_DION(73352),
    // Text: Protect the Economic Association Leader of Giran!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_GIRAN(73353),
    // Text: Protect the Economic Association Leader of Oren!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_OREN(73354),
    // Text: Protect the Economic Association Leader of Aden!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_ADEN(73355),
    // Text: Protect the Economic Association Leader of Innadril!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_INNADRIL(73356),
    // Text: Protect the Economic Association Leader of Goddard!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_GODDARD(73357),
    // Text: Protect the Economic Association Leader of Rune!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_RUNE(73358),
    // Text: Protect the Economic Association Leader of Schuttgart!
    PROTECT_THE_ECONOMIC_ASSOCIATION_LEADER_OF_SCHUTTGART(73359),
    // Text: The Economic Association Leader of Gludio is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_GLUDIO_IS_DEAD(73361),
    // Text: The Economic Association Leader of Dion is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_DION_IS_DEAD(73362),
    // Text: The Economic Association Leader of Giran is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_GIRAN_IS_DEAD(73363),
    // Text: The Economic Association Leader of Oren is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_OREN_IS_DEAD(73364),
    // Text: The Economic Association Leader of Aden is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_ADEN_IS_DEAD(73365),
    // Text: The Economic Association Leader of Innadril is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_INNADRIL_IS_DEAD(73366),
    // Text: The Economic Association Leader of Goddard is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_GODDARD_IS_DEAD(73367),
    // Text: The Economic Association Leader of Rune is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_RUNE_IS_DEAD(73368),
    // Text: The Economic Association Leader of Schuttgart is dead!
    THE_ECONOMIC_ASSOCIATION_LEADER_OF_SCHUTTGART_IS_DEAD(73369),
    // Text: Defeat $s1 enemy knights!
    DEFEAT_S1_ENEMY_KNIGHTS(73451),
    // Text: You have defeated $s2 of $s1 knights.
    YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS(73461),
    // Text: You weakened the enemy's defense!
    YOU_WEAKENED_THE_ENEMYS_DEFENSE(73462),
    // Text: Defeat $s1 warriors and rogues!
    DEFEAT_S1_WARRIORS_AND_ROGUES(73551),
    // Text: You have defeated $s2 of $s1 warriors and rogues.
    YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES(73561),
    // Text: You weakened the enemy's attack!
    YOU_WEAKENED_THE_ENEMYS_ATTACK(73562),
    // Text: Defeat $s1 wizards and summoners!
    DEFEAT_S1_WIZARDS_AND_SUMMONERS(73651),
    // Text: You have defeated $s2 of $s1 enemies.
    YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES(73661),
    // Text: You weakened the enemy's magic!
    YOU_WEAKENED_THE_ENEMYS_MAGIC(73662),
    // Text: Defeat $s1 healers and buffers.
    DEFEAT_S1_HEALERS_AND_BUFFERS(73751),
    // Text: You have defeated $s2 of $s1 healers and buffers.
    YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS(73761),
    // Text: You have weakened the enemy's support!
    YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT(73762),
    // Text: Defeat $s1 warsmiths and overlords.
    DEFEAT_S1_WARSMITHS_AND_OVERLORDS(73851),
    // Text: You have defeated $s2 of $s1 warsmiths and overlords.
    YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS(73861),
    // Text: You destroyed the enemy's professionals!
    YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS(73862),
    // Text: There's nothing you can't say. I can't listen to you anymore!
    THRES_NOTHING_YOU_CANT_SAY_I_CANT_LISTEN_TO_YOU_ANYMORE(528551),
    // Text: You advanced bravely but got such a tiny result. Hohoho
    YOU_ADVANCED_BRABELY_BUT_GOT_SUCH_A_TINY_RESULT_HO_HO_HO(528651),
    // Text: A non-permitted target has been discovered.
    A_NONPERMITTED_TARGET_HAS_BEEN_DISCOVERED(1000001),
    // Text: Intruder removal system initiated.
    INTRUDER_REMOVAL_SYSTEM_INITIATED(1000002),
    // Text: Removing intruders.
    REMOVING_INTRUDERS(1000003),
    // Text: A fatal error has occurred.
    A_FATAL_ERROR_HAS_OCCURRED(1000004),
    // Text: System is being shut down...
    SYSTEM_IS_BEING_SHUT_DOWN(1000005),
    // Text: ......
    DOT_DOT_DOT_DOT_DOT_DOT_DOT(1000006),
    // Text: We shall see about that!
    WE_SHALL_SEE_ABOUT_THAT(1000007),
    // Text: I will definitely repay this humiliation!
    I_WILL_DEFINITELY_REPAY_THIS_HUMILIATION(1000008),
    // Text: Retreat!
    RETREAT(1000009),
    // Text: Invader!
    INVADER(1000016),
    // Text: Am I the neighborhood drum for beating!
    AM_I_THE_NEIGHBORHOOD_DRUM_FOR_BEATING(1000022),
    // Text: Follow me if you want!
    FOLLOW_ME_IF_YOU_WANT(1000023),
    // Text: $s1. Stop kidding yourself about your own powerlessness!
    S1__STOP_KIDDING_YOURSELF_ABOUT_YOUR_OWN_POWERLESSNESS(1000028),
    // Text: $s1. I'll make you feel what true fear is!
    S1_ILL_MAKE_YOU_FEEL_WHAT_TRUE_FEAR_IS(1000029),
    // Text: You're really stupid to have challenged me. $s1! Get ready!
    YOURE_REALLY_STUPID_TO_HAVE_CHALLENGED_ME_S1_GET_READY(1000030),
    // Text: $s1. Do you think that's going to work?!
    S1_DO_YOU_THINK_THATS_GOING_TO_WORK(1000031),
    // Text: Return
    RETURN(1000170),
    // Text: Event Number
    EVENT_NUMBER(1000172),
    // Text: First Prize
    FIRST_PRIZE(1000173),
    // Text: Second Prize
    SECOND_PRIZE(1000174),
    // Text: Third Prize
    THIRD_PRIZE(1000175),
    // Text: Fourth Prize
    FOURTH_PRIZE(1000176),
    // Text: There has been no winning lottery ticket.
    THERE_HAS_BEEN_NO_WINNING_LOTTERY_TICKET(1000177),
    // Text: Your lucky numbers have been selected above.
    YOUR_LUCKY_NUMBERS_HAVE_BEEN_SELECTED_ABOVE(1000179),
    // Text: Prepare to die, foreign invaders! I am Gustav, the eternal ruler of this fortress and I have taken up my sword to repel thee!
    PREPARE_TO_DIE_FOREIGN_INVADERS_I_AM_GUSTAV_THE_ETERNAL_RULER_OF_THIS_FORTRESS_AND_I_HAVE_TAKEN_UP_MY_SWORD_TO_REPEL_THEE(1000275),
    // Text: Glory to Aden, the Kingdom of the Lion! Glory to Sir Gustav, our immortal lord!
    GLORY_TO_ADEN_THE_KINGDOM_OF_THE_LION_GLORY_TO_SIR_GUSTAV_OUR_IMMORTAL_LORD(1000276),
    // Text: Soldiers of Gustav, go forth and destroy the invaders!
    SOLDIERS_OF_GUSTAV_GO_FORTH_AND_DESTROY_THE_INVADERS(1000277),
    // Text: This is unbelievable! Have I really been defeated? I shall return and take your head!
    THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD(1000278),
    // Text: Could it be that I have reached my end? I cannot die without honor, without the permission of Sir Gustav!
    COULD_IT_BE_THAT_I_HAVE_REACHED_MY_END_I_CANNOT_DIE_WITHOUT_HONOR_WITHOUT_THE_PERMISSION_OF_SIR_GUSTAV(1000279),
    // Text: Ah, the bitter taste of defeat... I fear my torments are not over...
    AH_THE_BITTER_TASTE_OF_DEFEAT_I_FEAR_MY_TORMENTS_ARE_NOT_OVER(1000280),
    // Text: $s1! Come on, I'll take you on!
    CONE_ON_ILL_TAKE_YOU_ON(1000287),
    // Text: $s1! How dare you interrupt our fight! Hey guys, help!
    S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP(1000288),
    // Text: Dear ultimate power!!!
    DEAR_ULTIMATE_POWER(1000290),
    // This world will soon be annihilated!
    THIS_WORLD_WILL_SOON_BE_ANNIHILATED(1000303),
    // Text: $s1! Watch your back!
    S1_WATCH_YOUR_BACK(1000381),
    // Text: Your rear is practically unguarded!
    YOUR_REAR_IS_PRACTICALLY_UNGUARDED(1000382),
    // Text: $s1! Hey! We're having a duel here!
    S1_HEY_WERE_HAVING_A_DUEL_HERE(1000388),
    // Text: How dare you interrupt a sacred duel! You must be taught a lesson!
    HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON(1000391),
    // Text: Die, you coward!
    DIE_YOU_COWARD(1000392),
    // Text: I never thought I'd use this against a novice!
    I_NEVER_THOUGHT_ID_USE_THIS_AGAINST_A_NOVICE(1000395),
    // Text: Show yourselves!
    SHOW_YOURSELVES(1000403),
    // All is lost! Prepare to meet the goddess of death!
    ALL_IS_LOST__PREPARE_TO_MEET_THE_GODDESS_OF_DEATH(1000415),
    //All is lost! The prophecy of destruction has been fulfilled!
    ALL_IS_LOST__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED(1000416),
    // The end of time has come! The prophecy of destruction has been fulfilled!
    THE_END_OF_TIME_HAS_COME__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED(1000417),
    // The day of judgment is near!
    THE_DAY_OF_JUDGMENT_IS_NEAR(1000305),
    // The prophecy of darkness has been fulfilled!
    THE_PROPHECY_OF_DARKNESS_HAS_BEEN_FULFILLED(1000421),
    // As foretold in the prophecy of darkness, the era of chaos has begun!
    AS_FORETOLD_IN_THE_PROPHECY_OF_DARKNESS__THE_ERA_OF_CHAOS_HAS_BEGUN(1000422),
    // The prophecy of darkness has come to pass!
    THE_PROPHECY_OF_DARKNESS_HAS_COME_TO_PASS(1000423),
    // I bestow upon you a blessing!
    I_BESTOW_UPON_YOU_A_BLESSING(1000306),
    // $s1! I give you the blessing of prophecy!
    S1__I_GIVE_YOU_THE_BLESSING_OF_PROPHECY(1000424),
    // Herald of the new era, open your eyes!
    HERALD_OF_THE_NEW_ERA__OPEN_YOUR_EYES(1000426),
    // $s1! I bestow upon you the authority of the abyss!
    S1__I_BESTOW_UPON_YOU_THE_AUTHORITY_OF_THE_ABYSS(1000425),
    // You don't have any hope! Your end has come!
    YOU_DONT_HAVE_ANY_HOPE__YOUR_END_HAS_COME(1000420),
    // A curse upon you!
    A_CURSE_UPON_YOU(1000304),
    // $s1! You bring an ill wind!
    S1__YOU_BRING_AN_ILL_WIND(1000418),
    // $s1! You might as well give up!
    S1__YOU_MIGHT_AS_WELL_GIVE_UP(1000419),
    // Text: The defenders of $s1 castle will be teleported to the inner castle.
    THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE(1000443),
    // Text: **unregistered**
    __UNREGISTERED__(1000495),
    // Text: Competition
    COMPETITION(1000507),
    // Text: Seal Validation
    SEAL_VALIDATION(1000508),
    // Text: Preparation
    PREPARATION(1000509),
    // Text: Dusk
    DUSK(1000510),
    // Text: Dawn
    DAWN(1000511),
    // Text: No Owner
    NO_OWNER(1000512),
    // Text: This place is dangerous, $s1. Please turn back.
    THIS_PLACE_IS_DANGEROUS_S1__PLEASE_TURN_BACK(1000513),
    // Arrogant fool! You dare to challenge me, the Ruler of Flames? Here is your reward!
    VALAKAS_ARROGAANT_FOOL_YOU_DARE_TO_CHALLENGE_ME(1000519),
    // Text: A delivery for Mr. Lector? Very good!
    A_DELIVERY_FOR_MR_LECTOR__VERY_GOOD(1010201),
    // Text: I need a break!
    I_NEED_A_BREAK(1010202),
    // Text: Hello, Mr. Lector! Long time no see, Mr. Jackson!
    HELLO_MR_LECTOR_LONG_TIME_NO_SEE_MR_JACKSON(1010203),
    // Text: Lulu!
    LULU(1010204),
    // Text: Where has he gone?
    WHERE_HAS_HE_GONE(1010205),
    // Text: Have you seen Windawood?
    HAVE_YOU_SEEN_WINDAWOOD(1010206),
    // Text: Where did he go?
    WHERE_DID_HE_GO(1010207),
    // Text: The Mother Tree is slowly dying.
    THE_MOTHER_TREE_IS_SLOWLY_DYING(1010208),
    // Text: How can we save the Mother Tree?
    HOW_CAN_WE_SAVE_THE_MOTHER_TREE(1010209),
    // Text: The Mother Tree is always so gorgeous!
    THE_MOTHER_TREE_IS_ALWAYS_SO_GORGEOUS(1010210),
    // Text: Lady Mirabel, may the peace of the lake be with you!
    LADY_MIRABEL_MAY_THE_PEACE_OF_THE_LAKE_BE_WITH_YOU(1010211),
    // Text: You're a hard worker, Rayla!
    YOURE_A_HARD_WORKER_RAYLA(1010212),
    // Text: You're a hard worker!
    YOURE_A_HARD_WORKER(1010213),
    // Text: The mass of darkness will start in a couple of days. Pay more attention to the guard!
    THE_MASS_OF_DARKNESS_WILL_START_IN_A_COUPLE_OF_DAYS_PAY_MORE_ATTENTION_TO_THE_GUARD(1010214),
    // Text: Have you seen Torocco today?
    HAVE_YOU_SEEN_TOROCCO_TODAY(1010215),
    // Text: Have you seen Torocco?
    HAVE_YOU_SEEN_TOROCCO(1010216),
    // Text: Where is that fool hiding?
    WHERE_IS_THAT_FOOL_HIDING(1010217),
    // Text: Care to go a round?
    CARE_TO_GO_A_ROUND(1010218),
    // Text: Have a nice day, Mr. Garita and Mion!
    HAVE_A_NICE_DAY_MR_GARITA_AND_MION(1010219),
    // Text: Mr. Lid, Murdoc, and Airy! How are you doing?
    MR_LID_MURDOC_AND_AIRY_HOW_ARE_YOU_DOING(1010220),
    // Text: A black moon... Now do you understand that he has opened his eyes?
    A_BLACK_MOON_NOW_DO_YOU_UNDERSTAND_THAT_HE_HAS_OPENED_HIS_EYES(1010221),
    // Text: Clouds of blood are gathering. Soon, it will start to rain. The rain of crimson blood...
    CLOUDS_OF_BLOOD_ARE_GATHERING_SOON_IT_WILL_START_TO_RAIN_THE_RAIN_OF_CRIMSON_BLOOD(1010222),
    // Text: While the foolish light was asleep, the darkness will awaken first. Uh huh huh...
    WHILE_THE_FOOLISH_LIGHT_WAS_ASLEEP_THE_DARKNESS_WILL_AWAKEN_FIRST_UH(1010223),
    // Text: It is the deepest darkness. With its arrival, the world will soon die.
    IT_IS_THE_DEEPEST_DARKNESS_WITH_ITS_ARRIVAL_THE_WORLD_WILL_SOON_DIE(1010224),
    // Text: Death is just a new beginning. Huhu... Fear not.
    DEATH_IS_JUST_A_NEW_BEGINNING_HUHU_FEAR_NOT(1010225),
    // Text: Ahh! Beautiful goddess of death! Cover over the filth of this world with your darkness!
    AHH_BEAUTIFUL_GODDES_OF_DEATH_COVER_OVER_THE_FILTH_OF_THOS_WORLD_YOUR_DARKNESS(1010226),
    // Text: The goddess's resurrection has already begun. Huhu... Insignificant creatures like you can do nothing!
    THE_GODDESS_RESURRECTION_HAS_ALREADY_BEGUN_HUHU_INSIGNIFICANT_CREATURES_LIKE_YOU_CAN_DO_NOTHING(1010227),
    // Text: I will tell fish not to take your bait!
    I_WILL_TELL_FISH_NOT_TO_TAKE_YOUR_BAIT(1010416),
    // Text: Your bait was too delicious! Now, I will kill you!
    YOUR_BAIT_WAS_TOO_DELICIOUS_NOW_I_WILL_KILL_YOU(1010441),
    // Text: Command Channel Leader $s1, Beleth's Ring has been acquired.
    COMMAND_CHANNEL_LEADER_S1_BELETHS_RING_HAS_BEEN_ACQUIRED(1801004),
    // Valakas finds your attacks to be annoying and disruptive to his concentration. Keep it up!
    VALAKAS_FINDS_YOU_ATTACKS_ANNOYING_SILENCE(1801071),
    // Valakas' P.Def. is momentarily decreased because a warrior sliced a great gash in his side!
    VALAKAS_PDEF_ISM_DECREACED_SLICED_DASH(1801072),
    // Your attacks have overwhelmed Valakas, momentarily distracting him from his rage! Now's the time to attack!
    VALAKAS_OVERWHELMED_BY_ATTACK_NOW_TIME_ATTACK(1801073),
    // Your ranged attacks are provoking Valakas. If this continues, you might find yourself in a dangerous situation.
    VALAKAS_RANGED_ATTACKS_PROVOKED(1801074),
    // Your sneaky counterattacks have heightened Valakas' rage, increasing his attack power.
    VALAKAS_HEIGHTENED_BY_COUNTERATTACKS(1801075),
    // Your ranged attacks have enraged Valakas, causing him to attack his target relentlessly.
    VALAKAS_RANGED_ATTACKS_ENRAGED_TARGET_FREE(1801076),
    // Text: The beast ate the feed, but it isn't showing a response, perhaps because it's already full.
    THE_BEAST_ATE_THE_FEED_BUT_IT_ISNT_SHOWING_A_RESPONSE_PERHAPS_BECAUSE_ITS_ALREADY_FULL(1801091),
    // Text: The beast spit out the feed instead of eating it.
    THE_BEAST_SPIT_OUT_THE_FEED_INSTEAD_OF_EATING_IT(1801092),
    // Text: The beast spit out the feed and is instead attacking you!
    THE_BEAST_SPIT_OUT_THE_FEED_AND_IS_INSTEAD_ATTACKING_YOU(1801093),
    // Text: $s1 is leaving you because you don't have enough Golden Spices.
    S1_IS_LEAVING_YOU_BECAUSE_YOU_DONT_HAVE_ENOUGH_GOLDEN_SPICES(1801094),
    // Text: $s1 is leaving you because you don't have enough Crystal Spices.
    S1_IS_LEAVING_YOU_BECAUSE_YOU_DONT_HAVE_ENOUGH_CRYSTAL_SPICES(1801095),
    // Text: The evil Fire Dragon Valakas has been defeated!
    VALAKAS_THE_EVIL_FIRE_DRAGON_VALAKAS_DEFEATED(1900151),
    // Text: You cannot hope to defeat me with your meager strength.
    S1_ANTHARAS_YOU_CANNOT_HOPE_TO_DEFEAT_ME(1000520),
    // Text: The evil Land Dragon Antharas has been defeated!
    ANTHARAS_THE_EVIL_LAND_DRAGON_ANTHARAS_DEFEATED(1900150),
    // Text: Earth energy is gathering near  Antharas's legs.
    ANTHARAS_EARTH_ENERGY_GATHERING_LEGS(1900155),
    // Text: Antharas starts to absorb the earth energy.
    ANTHARAS_STARTS_ABSORB_EARTH_ENERGY(1900156),
    // Text: Antharas raises its thick tail.
    ANTHARAS_RAISES_ITS_THICK_TAIL(1900157),
    // Text: You are overcome by the strength of Antharas.
    ANTHARAS_YOU_ARE_OVERCOME_(1900158),
    // Text: Antharas's eyes are filled with rage.
    ANTHARAS_EYES_FILLED_WITH_RAGE(1900159),
    // Text: Show respect to the heroes who defeated the evil dragon and protected this Aden world!
    SHOW_RESPECT_TO_THE_HEROES_WHO_DEFEATED_THE_EVIL_DRAGON_AND_PROTECTED_THIS_ADEN_WORLD(1900172),
    // Text: Shout to celebrate the victory of the heroes!
    SHOUT_TO_CELEBRATE_THE_VICTORY_OF_THE_HEROES(1900173),
    // Text: Praise the achievement of the heroes and receive Nevit's blessing!
    PRAISE_THE_ACHIVEMENT_OF_THE_HEROES_AND_RECEIVE_NEVITS_BLESSING(1900174),
    // Text: Requiem of Hatred
    REQUIEM_OF_HATRED(1000522),
    // Text: Fugue of Jubilation
    FUGUE_OF_JUBILATION(1000523),
    // Text: Frenetic Toccata
    FRENETIC_TOCCATA(1000524),
    // Text: Hypnotic Mazurka
    HYPNOTIC_MAZURKA(1000525),
    // Text: Mournful Chorale Prelude
    MOURNFUL_CHORALE_PRELUDE(1000526),
    // Text: Rondo of Solitude
    RONDO_OF_SOLITUDE(1000527),
    //Text: The Kingdom of Aden
    THE_KINGDOM_OF_ADEN(1001000),
    // Text: Gludio
    GLUDIO(1001001),
    // Text: Dion
    DION(1001002),
    // Text: Giran
    GIRAN(1001003),
    // Text: Oren
    OREN(1001004),
    // Text: Aden
    ADEN(1001005),
    // Text: Innadril
    INNADRIL(1001006),
    // Text: The Kingdom of Elmore
    THE_KINGDOM_OF_ELMORE(1001100),
    // Text: Goddard
    GODDARD(1001007),
    // Text: Rune
    RUNE(1001008),
    // Text: Schuttgart
    SCHUTTGART(1001009),
    // Text: I will catch you!
    I_WILL_CATCH_YOU(1010457),
    // Text: Who dares to covet the throne of our castle!  Leave immediately or you will pay the price of your audacity with your very own blood!
    WHO_DARES_TO_COVET_THE_THRONE_OF_OUR_CASTLE__LEAVE_IMMEDIATELY_OR_YOU_WILL_PAY_THE_PRICE_OF_YOUR_AUDACITY_WITH_YOUR_VERY_OWN_BLOOD(1010623),
    // Text: Hmm, those who are not of the bloodline are coming this way to take over the castle?!  Humph!  The bitter grudges of the dead.  You must not make light of their power!
    HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE__HUMPH__THE_BITTER_GRUDGES_OF_THE_DEAD(1010624),
    // Text: Aargh...!  If I die, then the magic force field of blood will...!
    AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL(1010625),
    // Text: It's not over yet...  It won't be... over... like this... Never...
    ITS_NOT_OVER_YET__IT_WONT_BE__OVER__LIKE_THIS__NEVER(1010626),
    // Text: Oooh! Who poured nectar on my head while I was sleeping?
    OOOH_WHO_POURED_NECTAR_ON_MY_HEAD_WHILE_I_WAS_SLEEPING(1010627),
    // Text: Undecided
    UNDECIDED(1010635),
    // Text: Heh Heh... I see that the feast has begun! Be wary! The curse of the Hellmann family has poisoned this land!
    HEH_HEH_I_SEE_THAT_THE_FEAST_HAS_BEGAN_BE_WARY_THE_CURSE_OF_THE_HELLMANN_FAMILY_HAS_POISONED_THIS_LAND(1010636),
    // Text: Arise, my faithful servants! You, my people who have inherited the blood.  It is the calling of my daughter.  The feast of blood will now begin!
    ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD(1010637),
    // Text: Grarr! For the next 2 minutes or so, the game arena are will be cleaned. Throw any items you don't need to the floor now.
    GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED(1010639),
    // Text: You cannot carry a weapon without authorization!
    YOU_CANNOT_CARRY_A_WEAPON_WITHOUT_AUTHORIZATION(1121006),
    // Text: Match begins in $s1 minute(s). Please gather around the administrator.
    MATCH_BEGINS_IN_S1_MINUTES(1800080),
    // Text: The match is automatically canceled because you are too far from the admission manager.
    THE_MATCH_IS_AUTOMATICALLY_CANCELED_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_ADMISSION_MANAGER(1800081),
    // Text: It's not easy to obtain.
    ITS_NOT_EASY_TO_OBTAIN(1800107),
    // Text: You're out of your mind coming here...
    YOURE_OUT_OF_YOUR_MIND_COMING_HERE(1800108),
    // Text: Match cancelled. Opponent did not meet the stadium admission requirements.
    MATCH_CANCELLED(1800123),
    // Text: Begin stage 1
    BEGIN_STAGE_1_FREYA(1801086),
    // Text: Begin stage 2
    BEGIN_STAGE_2_FREYA(1801087),
    // Text: Begin stage 3
    BEGIN_STAGE_3_FREYA(1801088),
    // Text: Begin stage 4
    BEGIN_STAGE_4_FREYA(1801089),
    // Text: Time remaining until next battle
    TIME_REMAINING_UNTIL_NEXT_BATTLE(1801090),
    // Text: Freya has started to move.
    FREYA_HAS_STARTED_TO_MOVE(1801097),
    // Text: $s1 of Balance
    S1_OF_BALANCE(1801100),
    // Text: Swift $s1
    SWIFT_S1(1801101),
    // Text: $s1 of Blessing
    S1_OF_BLESSING(1801102),
    // Text: Sharp $s1
    SHARP_S1(1801103),
    // Text: Useful $s1
    USEFUL_S1(1801104),
    // Text: Reckless $s1
    RECKLESS_S1(1801105),
    // Text: Alpen Kookaburra
    ALPEN_KOOKABURRA(1801106),
    // Text: Alpen Cougar
    ALPEN_COUGAR(1801107),
    // Text: Alpen Buffalo
    ALPEN_BUFFALO(1801108),
    // Text: Alpen Grendel
    ALPEN_GRENDEL(1801109),
    // Text: Battle end limit time
    BATTLE_END_LIMIT_TIME(1801110),
    // Text: How dare you attack my recruits!!
    HOW_DARE_YOU_ATTACK_MY_RECRUITS(1801112),
    // Text: Who is disrupting the order?!
    WHO_IS_DISPURTING_THE_ORDER(1801113),
    // Text: We have broken through the gate! Destroy the encampment and move to the Command Post!
    WE_HAVE_BROKEN_THROUGH_THE_GATE_DESTROY_THE_ENCAMPMENT_AND_MOVE_TO_THE_COMMAND_POST(1300001),
    // Text: The command gate has opened! Capture the flag quickly and raise it high to proclaim our victory!
    THE_COMMAND_GATE_HAS_OPENED_CAPTURE_THE_FLAG_QUICKLY_AND_RAISE_IT_HIGH_TO_PROCLAIM_OUR_VICTORY(1300002),
    // Text: The gods have forsaken us... Retreat!!
    THE_GODS_HAVE_FORSAKEN_US__RETREAT(1300003),
    // Text: You may have broken our arrows, but you will never break our will! Archers, retreat!
    YOU_MAY_HAVE_BROKEN_OUR_ARROWS_BUT_YOU_WILL_NEVER_BREAK_OUR_WILL_ARCHERS_RETREAT(1300004),
    // Text: At last! The Magic Field that protects the fortress has weakened! Volunteers, stand back!
    AT_LAST_THE_MAGIC_FIELD_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK(1300005),
    // Text: Aiieeee! Command Center! This is guard unit! We need backup right away!
    AIIEEEE_COMMAND_CENTER_THIS_IS_GUARD_UNIT_WE_NEED_BACKUP_RIGHT_AWAY(1300006),
    // Text: Fortress power disabled.
    FORTRESS_POWER_DISABLED(1300007),
    // Text: Machine No. 1 - Power Off!
    MACHINE_NO_1_POWER_OFF(1300009),
    // Text: Machine No. 2  - Power Off!
    MACHINE_NO_2_POWER_OFF(1300010),
    // Text: Machine No. 3  - Power Off!
    MACHINE_NO_3_POWER_OFF(1300011),
    // Text: Spirit of Fire, unleash your power! Burn the enemy!!
    SPIRIT_OF_FIRE_UNLEASH_YOUR_POWER_BURN_THE_ENEMY(1300014),
    // Text: Do you need my power? You seem to be struggling.
    DO_YOU_NEED_MY_POWER_YOU_SEEM_TO_BE_STRUGGLING(1300016),
    // Text: Don't think that it's gonna end like this. Your ambition will soon be destroyed as well.
    DONT_THINK_THAT_ITS_GONNA_END_LIKE_THIS(1300018),
    // Text: I feel so much grief that I can't even take care of myself. There isn't any reason for me to stay here any longer.
    I_FEEL_SO_MUCH_GRIEF_THAT_I_CANT_EVEN_TAKE_CARE_OF_MYSELF(1300020),
    // Text: Independent State
    INDEPENDENT_STATE(1300122),
    // Text: Nonpartisan
    NONPARTISAN(1300123),
    // Text: Contract State
    CONTRACT_STATE(1300124),
    // Text: First password has been entered.
    FIRST_PASSWORD_HAS_BEEN_ENTERED(1300125),
    // Text: Second password has been entered.
    SECOND_PASSWORD_HAS_BEEN_ENTERED(1300126),
    // Text: Password has not been entered.
    PASSWORD_HAS_NOT_BEEN_ENTERED(1300127),
    // Text: Attempt $s1 / 3 is in progress. => This is the third attempt on $s1.
    ATTEMPT_S1__3_IS_IN_PROGRESS(1300128),
    // Text: The 1st Mark is correct.
    THE_1ST_MARK_IS_CORRECT(1300129),
    // Text: The 2nd Mark is correct.
    THE_2ND_MARK_IS_CORRECT(1300130),
    // Text: The Marks have not been assembled.
    THE_MARKS_HAVE_NOT_BEEN_ASSEMBLED(1300131),
    // Text: Olympiad class-free team match is going to begin in Arena $s1 in a moment.
    OLYMPIAD_CLASSFREE_TEAM_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT(1300132),
    // Text: Domain Fortress
    DOMAIN_FORTRESS(1300133),
    // Text: Boundary Fortress
    BOUNDARY_FORTRESS(1300134),
    // Text: $s1hour $s2minute
    S1HOUR_S2MINUTE(1300135),
    // Text: How dare you!
    HOW_DARE_YOU(1300148),
    // Text: Begin stage 1!
    BEGIN_STAGE_1(1300150),
    // Text: Begin stage 2!
    BEGIN_STAGE_2(1300151),
    // Text: Begin stage 3!
    BEGIN_STAGE_3(1300152),
    // Text: The Central Stronghold's compressor is working.
    THE_CENTRAL_S_COMPRESSOR_IS_WORKING(1300154),
    // Text: Stronghold I's compressor is working.
    THE_CENTRAL_I_S_COMPRESSOR_IS_WORKING(1300155),
    // Text: Stronghold II's compressor is working.
    THE_CENTRAL_II_S_COMPRESSOR_IS_WORKING(1300156),
    // Text: Stronghold III's compressor is working.
    THE_CENTRAL_III_S_COMPRESSOR_IS_WORKING(1300157),
    // Text: The Central Stronghold's compressor has been destroyed.
    THE_CENTRAL_STRONGHOLD_COMPRESSOR_HAS_BEEN_DESTROYED(1300158),
    // Text: Stronghold I's compressor has been destroyed.
    STRONGHOLD_I_S_COMPRESSOR_HAS_BEEN_DESTROYED(1300159),
    // Text: Stronghold II's compressor has been destroyed.
    STRONGHOLD_II_S_COMPRESSOR_HAS_BEEN_DESTROYED(1300160),
    // Text: Stronghold III's compressor has been destroyed.
    STRONGHOLD_III_S_COMPRESSOR_HAS_BEEN_DESTROYED(1300161),
    // Text: What a predicament... my attempts were unsuccessful.
    WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL(1300162),
    // Text: Courage! Ambition! Passion! Mercenaries who want to realize their dream of fighting in the territory war, come to me! Fortune and glory are waiting for you!
    COURAGE_AMBITION_PASSION_MERCENARIES_WHO_WANT_TO_REALIZE_THEIR_DREAM_OF_FIGHTING_IN_THE_TERRITORY_WAR_COME_TO_ME_FORTUNE_AND_GLORY_ARE_WAITING_FOR_YOU(
            1300163),
    // Text: Do you wish to fight? Are you afraid? No matter how hard you try, you have nowhere to run. But if you face it head on, our mercenary troop will help you out!
    DO_YOU_WISH_TO_FIGHT_ARE_YOU_AFRAID_NO_MATTER_HOW_HARD_YOU_TRY_YOU_HAVE_NOWHERE_TO_RUN(1300164),
    // Text: Charge! Charge! Charge!
    CHARGE_CHARGE_CHARGE(1300165),
    // Text: Olympiad class-free individual match is going to begin in Arena $s1 in a moment.
    OLYMPIAD_CLASSFREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT(1300166),
    // Text: Olympiad class individual match is going to begin in Arena $s1 in a moment.
    OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT(1300167),
    // Text: Care to challenge fate and test your luck?
    CARE_TO_CHALLENGE_FATE_AND_TEST_YOUR_LUCK(1600023),
    // Text: Don't pass up the chance to win an S80 weapon.
    DONT_PASS_UP_THE_CHANCE_TO_WIN_AN_S80_WEAPON(1600024),
    // Text: Your skill is impressive. I'll admit that you are good enough to pass. Take the key and leave this place.
    YOUR_SKILL_IS_IMPRESSIVE_ILL_ADMIT_THAT_YOU_ARE_GOOD_ENOUGH_TO_PASS_TAKE_THE_KEY_AND_LEAVE_THIS_PLACE(1800071),
    // Text: What are you doing? Hurry up and help me!
    WHAT_ARE_YOU_DOING_HURRY_UP_AND_HELP_ME(1800177),
    //Text: Ohh...oh...oh...
    OHHOHOH(1800209),
    // Text: The airship has been summoned. It will automatically depart in 5 minutes.
    AIRSHIP_IS_SUMMONED_IS_DEPART_IN_5_MINUTES(1800219),
    // Text: The regularly scheduled airship has arrived. It will depart for the Aden continent in 1 minute.
    AIRSHIP_IS_ARRIVED_IT_WILL_DEPART_TO_ADEN_IN_1_MINUTE(1800220),
    // Text: The regularly scheduled airship that flies to the Aden continent has departed.
    AIRSHIP_IS_DEPARTED_TO_ADEN(1800221),
    // Text: The regularly scheduled airship has arrived. It will depart for the Gracia continent in 1 minute.
    AIRSHIP_IS_ARRIVED_IT_WILL_DEPART_TO_GRACIA_IN_1_MINUTE(1800222),
    // Text: The regularly scheduled airship that flies to the Gracia continent has departed.
    AIRSHIP_IS_DEPARTED_TO_GRACIA(1800223),
    // Text: Another airship has been summoned to the wharf. Please try again later.
    IN_AIR_HARBOR_ALREADY_AIRSHIP_DOCKED_PLEASE_WAIT_AND_TRY_AGAIN(1800224),
    // Text: Attack
    ATTACK(1800243),
    // Text: Defend
    DEFEND(1800244),
    // Text: Grunt... What's... wrong with me...
    GRUNT_WHAT_S_WRONG_WITH_ME(1800828),
    // Text: ...Grunt... Oh...
    GRUNT_OH(1800829),
    // Text: The grave robber warrior has been filled with dark energy and is attacking you!
    THE_GRAVE_ROBBER_WARRIOR_HAS_BEEN_FILLED_WITH_DARK_ENERGY_AND_IS_ATTACKING_YOU(1800830),
    // Text: Alert! Alert! Damage detection recognized. Countermeasures enabled.
    ALERT_ALERT_DAMAGE_DETECTION_RECOGNIZED(1800851),
    // Text: The purification field is being attacked. Guardian Spirits! Protect the Magic Force!!
    THE_PURIFICATION_FIELD_IS_BEING_ATTACKED(1800854),
    // Text: Maguen appearance!!!
    MAGUEN_APPEARANCE(1801149),
    // Text: There are 5 minutes remaining to register for Kratei's cube match.
    THERE_ARE_5_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH(1800203),
    // Text: There are 3 minutes remaining to register for Kratei's cube match.
    THERE_ARE_3_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH(1800204),
    // Text: There are 1 minutes remaining to register for Kratei's cube match.
    THERE_ARE_1_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH(1800205),
    // Text: The match will begin in $s1 minute(s).
    THE_MATCH_WILL_BEGIN_IN_S1_MINUTES(1800206),
    // Text: The match will begin shortly.
    THE_MATCH_WILL_BEGIN_SHORTLY(1800207),
    // Text: Registration for the next match will end at %s minutes after the hour.
    REGISTRATION_FOR_THE_NEXT_MATCH_WILL_END_AT_S1_MINUTES_AFTER_HOUR(1800208),
    // Text: Even though you bring something called a gift among your humans, it would just be problematic for me...
    EVEN_THOUGH_YOU_BRING_SOMETHING_CALLED_A_GIFT_AMONG_YOUR_HUMANS_IT_WOULD_JUST_BE_PROBLEMATIC_FOR_ME(1801190),
    // Text: I just don't know what expression I should have it appeared on me. Are human's emotions like this feeling?
    I_JUST_DONT_KNOW_WHAT_EXPRESSION_I_SHOULD_HAVE_IT_APPEARED_ON_ME(1801191),
    // Text: The feeling of thanks is just too much distant memory for me...
    THE_FEELING_OF_THANKS_IS_JUST_TOO_MUCH_DISTANT_MEMORY_FOR_ME(1801192),
    // Text: But I kind of miss it... Like I had felt this feeling before...
    BUT_I_KIND_OF_MISS_IT(1801193),
    // Text: I am Ice Queen Freya... This feeling and emotion are nothing but a part of Melissa'a memories.
    I_AM_ICE_QUEEN_FREYA(1801194),
    // Text: Dear $s1... Think of this as my appreciation for the gift. Take this with you. There's nothing strange about it. It's just a bit of my capriciousness...
    DEAR_S1(1801195),
    // Text: Dear $s1... Think of this as my appreciation for the gift. Take this with you. There's nothing strange about it. It's just a bit of my capriciousness...\0
    DEAR_S1_THINK_OF_THIS_AS_MY_APPECIATION_FOR_THE_GIFT_TAKE_THIS_WITH_YOU_THERES_NOTHING_STRANGE_ABOUT_IT_ITS_JUST_A_BIT_OF_MY_CAPRICIOUSNESS(1801195),
    // Text: The kindness of somebody is not a bad feeling... Dear $s1, I will take this gift out of respect your kindness.\0
    THE_KIDNESS_OF_SOMEBODY_IS_NOT_A_BAD_FEELING_DEAR_S1_I_WILL_TAKE_THIS_GIFT_OUT_OF_RESPECT_YOUR_KIDNESS(1801196),
    // Text: Rulers of the seal! I bring you wondrous gifts!
    RULERS_OF_THE_SEAL_I_BRING_YOU_WONDROUS_GIFTS(1000431),
    // Text: Rulers of the seal! I have some excellent weapons to show you!
    RULERS_OF_THE_SEAL_I_HAVE_SOME_EXCELLENT_WEAPONS_TO_SHOW_YOU(1000432),
    // Text: I've been so busy lately, in addition to planning my trip!
    IVE_BEEN_SO_BUSY_LATELY_IN_ADDITION_TO_PLANNING_MY_TRIP(1000433),
    // Text: Behold the mighty power of Baylor, foolish mortal!
    BEHOLD_THE_MIGHTY_POWER_OF_BAYLOR_FOOLISH_MORTAL(1800058),
    // Text: No one is going to survive!
    NO_ONE_IS_GOING_TO_SURVIVE(1800059),
    // Text: You'll see what hell is like...
    YOU_LL_SEE_WHAT_HELL_IS_LIKE(1800060),
    // Text: You will be put in jail!
    YOU_WILL_BE_PUT_IN_JAIL(1800061),
    // Text: Worthless creature... Go to hell!
    WORTLESS_CREATURE_GO_TO_HELL(1800062),
    // Text: I'll give you something that you'll never forget!
    I_LL_GIVE_YOU_SOMETHING_THAT_YOU_LL_NEVER_FORGET(1800063),
    // Text: Demon King Beleth, give me the power! Aaahh!!!
    DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH(1800067),
    // Text: No....... I feel the power of Fafurion.......
    NO_I_FEEL_THE_POWER_OF_FAFURION(1800068),
    // Text: Messenger, inform the patrons of the Keucereus Alliance Base! We're gathering brave adventurers to attack Tiat's Mounted Troop that's rooted in the Seed of Destruction.
    MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_WERE_GATHERING_BRAVE_ADVENTURERS_TO_ATTACK_TIATS_MOUNTED_TROOP_THATS_ROOTED_IN_THE_SEED_OF_DESTRUCTION(1800695),
    // Text: Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Destruction is currently secured under the flag of the Keucereus Alliance!
    MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_DESTRUCTION_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE(1800696),
    // Text: Messenger, inform the patrons of the Keucereus Alliance Base! Tiat's Mounted Troop is currently trying to retake Seed of Destruction! Commit all the available reinforcements into Seed of Destruction!
    MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_TIATS_MOUNTED_TROOP_IS_CURRENTLY_TRYING_TO_RETAKE_SEED_OF_DESTRUCTION_COMMIT_ALL_THE_AVAILABLE_REINFORCEMENTS_INTO_SEED_OF_DESTRUCTION(1800697),
    // Text: Messenger, inform the brothers in Kucereus' clan outpost! Brave adventurers who have challenged the Seed of Infinity are currently infiltrating the Hall of Erosion through the defensively weak Hall of Suffering!
    MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_BRAVE_ADVENTURERS_WHO_HAVE_CHALLENGED_THE_SEED_OF_INFINITY_ARE_CURRENTLY_INFILTRATING_THE_HALL_OF_EROSION_THROUGH_THE_DEFENSIVELY_WEAK_HALL_OF_SUFFERING(1800698),
    // Text: Messenger, inform the brothers in Kucereus' clan outpost! Sweeping the Seed of Infinity is currently complete to the Heart of the Seed. Ekimus is being directly attacked, and the Undead remaining in the Hall of Suffering are being eradicated!
    MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_SWEEPING_THE_SEED_OF_INFINITY_IS_CURRENTLY_COMPLETE_TO_THE_HEART_OF_THE_SEED(1800699),
    // Text: Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Infinity is currently secured under the flag of the Keucereus Alliance!
    MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_INFINITY_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE(1800700),
    // Text: Messenger, inform the patrons of the Keucereus Alliance Base! The resurrected Undead in the Seed of Infinity are pouring into the Hall of Suffering and the Hall of Erosion!
    MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_RESURRECTED_UNDEAD_IN_THE_SEED_OF_INFINITY_ARE_POURING_INTO_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION(1800702),
    // Text: Messenger, inform the brothers in Kucereus' clan outpost! Ekimus is about to be revived by the resurrected Undead in Seed of Infinity. Send all reinforcements to the Heart and the Hall of Suffering!
    MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_EKIMUS_IS_ABOUT_TO_BE_REVIVED_BY_THE_RESURRECTED_UNDEAD_IN_SEED_OF_INFINITY(1800703),
    // Text: Stabbing three times!
    STABBING_THREE_TIMES(1800704),
    // Text: If you have items, please give them to me.
    IF_YOU_HAVE_ITEMS_PLEASE_GIVE_THEM_TO_ME(1800279),
    // Text: My stomach is empty.
    MY_STOMACH_IS_EMPTY(1800280),
    // Text: I'm hungry, I'm hungry!
    IM_HUNGRY_IM_HUNGRY(1800281),
    // Text: I'm still not full...
    IM_STILL_NOT_FULL(1800282),
    // Text: I'm still hungry~
    IM_STILL_HUNGRY(1800283),
    // Text: I feel a little woozy...
    I_FEEL_A_LITTLE_WOOZY(1800284),
    // Text: Give me something to eat.
    GIVE_ME_SOMETHING_TO_EAT(1800285),
    // Text: Now it's time to eat~
    NOW_ITS_TIME_TO_EAT(1800286),
    // Text: I also need a dessert.
    I_ALSO_NEED_A_DESSERT(1800287),
    // Text: I'm still hungry.
    IM_STILL_HUNGRY_(1800288),
    // Text: I'm full now, I don't want to eat anymore.
    IM_FULL_NOW_I_DONT_WANT_TO_EAT_ANYMORE(1800289),
    // Text: Elapsed Time :
    ELAPSED_TIME(1911119),
    // Text: Time Remaining :
    TIME_REMAINING(1911120),
    // Text: Strong magic power can be felt from somewhere!!
    I_FEEL_STRONG_MAGIC_FLOW(1801111),
    // Text: Magic power so strong that it could make you lose your mind can be felt from somewhere!!
    MAGIC_POWER_SO_STRONG_THAT_IT_COULD_MAKE_YOU_LOSE_YOUR_MIND_CAN_BE_FELT_FROM_SOMEWHERE(1801189),
    // Text: The space feels like its gradually starting to shake.
    THE_SPACE_FEELS_LIKE_ITS_GRADUALLY_STARTING_TO_SHAKE(1801124),
    // Text: Archer. Give your breath for the intruder!
    ARCHER(1801120),
    // Text: My knights. Show your loyalty!!
    MY_KNIGHTS(1801121),
    // Text: I can take it no longer!!!
    I_CAN_TAKE_IT_NO_LONGER(1801122),
    // Text: Archer. Heed my call!
    ARCHER_(1801123),
    // Text: I haven't eaten anything, I'm so weak~
    I_HAVENT_EATEN_ANYTHING_IM_SO_WEAK(1800290),
    // Text: We must search high and low in every room for the reading desk that contains the book we seek.
    WE_MUST_SEARCH_HIGH_AND_LOW_IN_EVERY_ROOM_FOR_THE_READING_DESK_THAT_CONTAINS_THE_BOOK_WE_SEEK(1029450),
    // Text: Remember the content of the books that you found. You can't take them out with you.
    REMEMBER_THE_CONTENT_OF_THE_BOOKS_THAT_YOU_FOUND(1029451),
    // Text: It seems that you cannot remember to the room of the watcher who found the book.
    IT_SEEMS_THAT_YOU_CANNOT_REMEMBER_TO_THE_ROOM_OF_THE_WATCHER_WHO_FOUND_THE_BOOK(1029452),
    // Text: Your work here is done, so return to the central guardian.
    YOUR_WORK_HERE_IS_DONE_SO_RETURN_TO_THE_CENTRAL_GUARDIAN(1029453),
    // Text: The guardian of the seal doesn't seem to get injured at all until the barrier is destroyed.
    THE_GUARDIAN_OF_THE_SEAL_DOESNT_SEEM_TO_GET_INJURED_AT_ALL_UNTIL_THE_BARRIER_IS_DESTROYED(1029550),
    // Text: The device located in the room in front of the guardian of the seal is definitely the barrier that controls the guardian's power.
    THE_DEVICE_LOCATED_IN_THE_ROOM_IN_FRONT_OF_THE_GUARDIAN_OF_THE_SEAL_IS_DEFINITELY_THE_BARRIER_THAT_CONTROLS_THE_GUARDIANS_POWER(1029551),
    // Text: To remove the barrier, you must find the relics that fit the barrier and activate the device.
    TO_REMOVE_THE_BARRIER_YOU_MUST_FIND_THE_RELICS_THAT_FIT_THE_BARRIER_AND_ACTIVATE_THE_DEVICE(1029552),
    // Text: All the guardians were defeated, and the seal was removed. Teleport to the center.
    ALL_THE_GUARDIANS_WERE_DEFEATED_AND_THE_SEAL_WAS_REMOVED(1029553),
    // Text: What took so long? I waited for ever.
    WHAT_TOOK_SO_LONG_I_WAITED_FOR_EVER(1029350),
    // Text: I must ask Librarian Sophia about the book.
    I_MUST_ASK_LIBRARIAN_SOPHIA_ABOUT_THE_BOOK(1029351),
    // Text: This library... It's huge but there aren't many useful books, right?
    THIS_LIBRARY(1029352),
    // Text: An underground library... I hate damp and smelly places...
    AN_UNDERGROUND_LIBRARY(1029353),
    // Text: The book that we seek is certainly here. Search inch by inch.
    THE_BOOK_THAT_WE_SEEK_IS_CERTAINLY_HERE(1029354),
    // Text: You foolish invaders who disturb the rest of Solina, be gone from this place.
    YOU_FOOLISH_INVADERS_WHO_DISTURB_THE_REST_OF_SOLINA_BE_GONE_FROM_THIS_PLACE(1029460),
    // Text: I know not what you seek, but this truth cannot be handled by mere humans.
    I_KNOW_NOT_WHAT_YOU_SEEK_BUT_THIS_TRUTH_CANNOT_BE_HANDLED_BY_MERE_HUMANS(1029461),
    // Text: I will not stand by and watch your foolish actions. I warn you, leave this place at once.
    I_WILL_NOT_STAND_BY_AND_WATCH_YOUR_FOOLISH_ACTIONS(1029462),
    // Text: (Queen Ant)  # $s1's Command Channel has looting rights.
    QUEEN_ANT_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS(1800001),
    // Text: (Core)  # $s1's Command Channel has looting rights.
    CORE_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS(1800002),
    // Text: (Orphen)  # $s1's Command Channel has looting rights.
    ORPHEN_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS(1800003),
    // Text: (Zaken)  # $s1's Command Channel has looting rights.
    ZAKEN_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS(1800004),
    // Text: (Queen Ant) Looting rules are no longer active.
    QUEEN_ANT_LOOTING_RULES_ARE_NO_LONGER_ACTIVE(1800005),
    // Text: (Core) Looting rules are no longer active.
    CORE_LOOTING_RULES_ARE_NO_LONGER_ACTIVE(1800006),
    // Text: (Orphen) Looting rules are no longer active.
    ORPHEN_LOOTING_RULES_ARE_NO_LONGER_ACTIVE(1800007),
    // Text: (Zaken) Looting rules are no longer active.
    ZAKEN_LOOTING_RULES_ARE_NO_LONGER_ACTIVE(1800008),
    // Text: # $s1's Command Channel has looting rights.
    S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS(1800009),
    // Text: Looting rules are no longer active.
    LOOTING_RULES_ARE_NO_LONGER_ACTIVE(1800010),
    // Text: I'm the real one!
    I_M_THE_REAL(1800043),
    // Text: Pick me!
    PICK_ME(1800044),
    // Text: Trust me!
    TRUST_ME(1800045),
    // Text: Not that dude, I'm the real one!
    NOT_THAT_DUDE_I_M_THE_REAL_ONE(1800046),
    // Text: Don't be fooled! Don't be fooled! I'm the real one!!
    DONT_BE_FOOLED_DONT_BE_FOOLED_I_M_THE_REAL(1800047),
    // Text: Just act like the real one! Oops!
    JUST_ACT_LIKE_THE_REAL_ONE(1800048),
    // Text: You've been fooled!
    YOU_VE_BEEN_FOOLED(1800049),
    // Text: Sorry, but...I'm the fake one.
    SORRY_BUT_I_M_THE_FAKE_ONE(1800050),
    // Text: I'm the real one! Phew!!
    I_M_THE_REAL_ONE_PHEW(1800051),
    // Text: Can't you even find out?
    CANT_YOU_EVEN_FIND_OUT(1800052),
    // Text: Find me!
    FIND_ME(1800053),
    // Text: Huh?! How did you know it was me?
    HUH_HOW_DID_YOU_KNOW_IT_WAS_ME(1800054),
    // Text: Excellent choice! Teehee!
    EXCELLENT_CHOISE_TEEHEE(1800055),
    // Text: You've done well!
    YOU_VE_DONE_WELL(1800056),
    // Text: Oh... very sensible?
    OH_VERY_SENSIBLE(1800057),
    // Text: You have done well in finding me, but I cannot just hand you the key!
    YOU_HAVE_DONE_WELL_IN_FINDING_ME_BUT_I_CANNOT_JUST_HAND_YOU_THE_KEY(1800078),
    // Text: The drillmaster is dead!
    THE_DRILLMASTER_IS_DEAD(1801114),
    // Text: Line up the ranks!!
    LINE_UP_THE_RANKS(1801115),
    // Text: I brought the food.
    I_BROUGHT_THE_FOOD(1801116),
    // Text: Come and eat.
    COME_AND_EAT(1801117),
    // Text: Looks delicious.
    LOOKS_DELICIOUS(1801118),
    // Text: Let's go eat.
    LETS_GO_EAT(1801119),
    // Text: You $s1! Attack them!
    YOU_S1_ATTACK_THEM(1800182),
    // Text: Come out! My subordinate! I summon you to drive them out!
    COME_OUT_MY_SUBORDINATE_I_SUMMON_YOU_TO_DRIVE_THEM_OUT(1800183),
    // Text: There's not much I can do, but I will risk my life to help you!
    THERES_NOT_MUCH_I_CAN_DO_BUT_I_WILL_RISK_MY_LIFE_TO_HELP_YOU(1800184),
    // Text: Ohh...oh...oh...
    OHH_OH_OH(1800209),
    // Text: Hall of Suffering
    HALL_OF_SUFFERING(1800240),
    // Text: Hall of Erosion
    HALL_OF_EROSION(1800241),
    // Text: Heart of Immortality
    HEART_OF_IMMORTALITY(1800242),
    // Text: You can hear the undead of Ekimus rushing toward you. $s1 $s2, it has now begun!
    YOU_CAN_HEAR_THE_UNDEAD_OF_EKIMUS_RUSHING_TOWARD_YOU(1800263),
    // Text: The tumor inside $s1 has been destroyed! \nIn order to draw out the cowardly Cohemenes, you must destroy all the tumors!
    THE_TUMOR_INSIDE_S1_HAS_BEEN_DESTROYED_NIN_ORDER_TO_DRAW_OUT_THE_COWARDLY_COHEMENES_YOU_MUST_DESTROY_ALL_THE_TUMORS(1800274),
    // Text: The tumor inside $s1 has completely revived. \nThe restrengthened Cohemenes has fled deeper inside the seed...
    THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED(1800275),
    // Text: All the tumors inside $s1 have been destroyed! Driven into a corner, Cohemenes appears close by!
    ALL_THE_TUMORS_INSIDE_S1_HAVE_BEEN_DESTROYED_DRIVEN_INTO_A_CORNER_COHEMENES_APPEARS_CLOSE_BY(1800299),
    // Text: $s1's party has moved to a different location through the crack in the tumor!
    S1S_PARTY_HAS_MOVED_TO_A_DIFFERENT_LOCATION_THROUGH_THE_CRACK_IN_THE_TUMOR(1800247),
    // Text: $s1's party has entered the Chamber of Ekimus through the crack in the tumor!
    S1S_PARTY_HAS_ENTERED_THE_CHAMBER_OF_EKIMUS_THROUGH_THE_CRACK_IN_THE_TUMOR(1800248),
    // Text: Ekimus has sensed abnormal activity. \nThe advancing party is forcefully expelled!
    EKIMUS_HAS_SENSED_ABNORMAL_ACTIVITY(1800249),
    // Text: C'mon, c'mon! Show your face, you little rats! Let me see what the doomed weaklings are scheming!
    CMON_CMON_SHOW_YOUR_FACE_YOU_LITTLE_RATS_LET_ME_SEE_WHAT_THE_DOOMED_WEAKLINGS_ARE_SCHEMING(1800233),
    // Text: Impressive.... Hahaha it's so much fun, but I need to chill a little while.  Argekunte, clear the way!
    IMPRESSIVE(1800234),
    // Text: Kyahaha! Since the tumor has been resurrected, I no longer need to waste my time on you!
    KYAHAHA_SINCE_THE_TUMOR_HAS_BEEN_RESURRECTED_I_NO_LONGER_NEED_TO_WASTE_MY_TIME_ON_YOU(1800235),
    // Text: Keu... I will leave for now... But don't think this is over... The Seed of Infinity can never die...
    KEU(1800236),
    // Text: $s1 minute(s) are remaining.
    S1_MINUTES_ARE_REMAINING(1010643),
    // Text: Congratulations! You have succeeded at $s1 $s2! The instance will shortly expire.
    CONGRATULATIONS_YOU_HAVE_SUCCEEDED_AT_S1_S2_THE_INSTANCE_WILL_SHORTLY_EXPIRE(1800245),
    // Text: You have failed at $s1 $s2... The instance will shortly expire.
    YOU_HAVE_FAILED_AT_S1_S2(1800246),
    // Text: You will participate in $s1 $s2 shortly. Be prepared for anything.
    YOU_WILL_PARTICIPATE_IN_S1_S2_SHORTLY(1800262),
    // Text: I shall accept your challenge, $s1! Come and die in the arms of immortality!
    I_SHALL_ACCEPT_YOUR_CHALLENGE_S1_COME_AND_DIE_IN_THE_ARMS_OF_IMMORTALITY(1800261),
    // Text: The tumor inside $s1 that has provided energy \n to Ekimus is destroyed!
    THE_TUMOR_INSIDE_S1_THAT_HAS_PROVIDED_ENERGY_N_TO_EKIMUS_IS_DESTROYED(1800302),
    // Text: The tumor inside $s1 has been completely resurrected \n and started to energize Ekimus again...
    THE_TUMOR_INSIDE_S1_HAS_BEEN_COMPLETELY_RESURRECTED_N_AND_STARTED_TO_ENERGIZE_EKIMUS_AGAIN(1800303),
    // Text: With all connections to the tumor severed, Ekimus has lost its power to control the Feral Hound!
    WITH_ALL_CONNECTIONS_TO_THE_TUMOR_SEVERED_EKIMUS_HAS_LOST_ITS_POWER_TO_CONTROL_THE_FERAL_HOUND(1800269),
    // Text: With the connection to the tumor restored, Ekimus has regained control over the Feral Hound...
    WITH_THE_CONNECTION_TO_THE_TUMOR_RESTORED_EKIMUS_HAS_REGAINED_CONTROL_OVER_THE_FERAL_HOUND(1800270),
    // Text: There is no party currently challenging Ekimus. \n If no party enters within $s1 seconds, the attack on the Heart of Immortality will fail...
    THERE_IS_NO_PARTY_CURRENTLY_CHALLENGING_EKIMUS(1800229),
    // Text: You can feel the surging energy of death from the tumor.
    YOU_CAN_FEEL_THE_SURGING_ENERGY_OF_DEATH_FROM_THE_TUMOR(1800264),
    // Text: The area near the tumor is full of ominous energy.
    THE_AREA_NEAR_THE_TUMOR_IS_FULL_OF_OMINOUS_ENERGY(1800265),
    // Text: The tumor inside $s1 has been destroyed! \nThe nearby Undead that were attacking Seed of Life start losing their energy and run away!
    THE_TUMOR_INSIDE_S1_HAS_BEEN_DESTROYED_NTHE_NEARBY_UNDEAD_THAT_WERE_ATTACKING_SEED_OF_LIFE_START_LOSING_THEIR_ENERGY_AND_RUN_AWAY(1800300),
    // Text: The tumor inside $s1 has completely revived. \nRecovered nearby Undead are swarming toward Seed of Life...
    THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED_(1800301),
    // Text: The tumor inside $s1 has been destroyed! \nThe speed that Ekimus calls out his prey has slowed down!
    THE_TUMOR_INSIDE_S1_HAS_BEEN_DESTROYED_NTHE_SPEED_THAT_EKIMUS_CALLS_OUT_HIS_PREY_HAS_SLOWED_DOWN(1800304),
    // Text: The tumor inside $s1 has completely revived. \nEkimus started to regain his energy and is desperately looking for his prey...
    THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED__(1800305),
    // Text: Bring more, more souls...!
    BRING_MORE_MORE_SOULS(1800306),
    // Text: $s1! That stranger must be defeated. Here is the ultimate help!
    S1_THAT_STRANGER_MUST_BE_DEFEATED_HERE_IS_THE_ULTIMATE_HELP(1800845),
    // Text: Hurry hurry
    HURRY_HURRY(1800882),
    // Text: I am not that type of person who stays in one place for a long time
    I_AM_NOT_THAT_TYPE_OF_PERSON_WHO_STAYS_IN_ONE_PLACE_FOR_A_LONG_TIME(1800883),
    // Text: It's hard for me to keep standing like this
    ITS_HARD_FOR_ME_TO_KEEP_STANDING_LIKE_THIS(1800884),
    // Text: Why don't I go that way this time
    WHY_DONT_I_GO_THAT_WAY_THIS_TIME(1800885),
    // Text: Welcome!
    WELCOME(1800886),
    // Text: Welcome, $s1! Let us see if you have brought a worthy offering for the Black Abyss!
    WELCONE_LET_US_SEE_IF_YOU_HAVE_BROUGHT_A_WORTHY_OFFERING_FOR_THE_BLACK_ABBYSS(1801078),
    // Text: What a formidable foe! But I have the Abyss Weed given to me by the Black Abyss! Let me see...
    WHAT_A_FORMIDABLE_FOR_BUT_I_HAVE_THE_ABYSS_WEED_GIVEN_TO_ME_BY_THE_BLACK_ABYSS_LET_ME_SEE(1801079),
    // Text: Muhahaha! Ah, this burning sensation in my mouth! The power of the Black Abyss is reviving me!
    MUHAHAHA_AH_THIS_BURNING_SENSATION_IN_MY_MOUTH_THE_POWER_OF_THE_BLACK_ABYSS_IS_REVIVING_ME(1801080),
    // Text: No! How dare you stop me from using the Abyss Weed... Do you know what you have done?!
    NO_HOW_DARE_YOU_STOP_ME_FROM_USING_THE_ABYSS_WEED_DO_YOU_KNOW_WHAT_YOU_HAVE_DONE(1801081),
    // Text: A limp creature like this is unworthy to be an offering... You have no right to sacrifice to the Black Abyss!
    A_LIMP_CREATURE_LIKE_THIS_IS_UNWORTHY_TO_BE_AN_OFFERING_YOU_HAVE_NO_RIGHT_TO_SACRIFICE_TO_THE_BLACK_ABYSS(1801082),
    // Text: Listen, oh Tantas! The Black Abyss is famished! Find some fresh offerings!
    LISTEN_OH_TANTAS_THE_BLACK_ABYSS_IS_FAMISHED_FIND_SOME_FRESH_OFFERING(1801083),
    // Text: Ha, ha, ha!...
    HA_HA_HA(7164),
    // Text: The Soul Coffin has awakened Ekimus. If $s1 more Soul Coffin(s) are created, the defense of the Heart of Immortality will fail...
    THE_SOUL_COFFIN_HAS_AWAKENED_EKIMUS(1800232),
    // Text: Hello? Is anyone there?
    HELLO_IS_ANYONE_THERE(1800034),
    // Text: Is no one there? How long have I been hiding? I have been starving for days and cannot hold out anymore!
    IS_NO_ONE_THERE_HOW_LONG_HAVE_I_BEEN_HIDING_I_HAVE_BEEN_STARVING_FOR_DAYS_AND_CANNOT_HOLD_OUT_ANYMORE(1800035),
    // Text: If someone would give me some of those tasty Crystal Fragments, I would gladly tell them where Tears is hiding! Yummy, yummy!
    IF_SOMEONE_WOULD_GIVE_ME_SOME_OF_THOSE_TASTY_CRYSTAL_FRAGMENTS_I_WOULD_GLADLY_TELL_THEM_WHERE_TEARS_IS_HIDING_YUMMY_YUMMY(1800036),
    // Text: Hey! You from above the ground! Let's share some Crystal Fragments, if you have any.
    HEY_YOU_FROM_ABOVE_THE_GROUND_LETS_SHARE_SOME_CRYSTAL_FRAGMENTS_IF_YOU_HAVE_ANY(1800037),
    // Text: Crispy and cold feeling! Teehee! Delicious!!
    CRISPY_AND_COLD_FEELING_TEEHEE_DELICIOUS(1800038),
    // Text: Yummy! This is so tasty.
    YUMMY_THIS_IS_SO_TASTY(1800039),
    // Text: Sniff, sniff! Give me more Crystal Fragments!
    SNIFF_SNIFF_GIVE_ME_MORE_CRYSTAL_FRAGMENTS(1800040),
    // Text: How insensitive! It's not nice to give me just a piece! Can't you give me more?
    HOW_INSENSITIVE_ITS_NOT_NICE_TO_GIVE_ME_JUST_A_PIECE_CANT_YOU_GIVE_ME_MORE(1800041),
    // Text: Ah - I'm hungry!
    AH__IM_HUNGRY(1800042),
    // Text: No...You knew my weakness...
    NO__YOU_KNEW_MY_WEAKNESS(1800033),
    // Text: $s1.. You don't have a Red Crystal...
    S1_YOU_DONT_HAVE_RED_CRYSTAL(1800027),
    // Text: $s1.. You don't have a Blue Crystal...
    S1_YOU_DONT_HAVE_BLUE_CRYSTAL(1800028),
    // Text: $s1.. You don't have a Clear Crystal...
    S1_YOU_DONT_HAVE_CLEAR_CRYSTAL(1800029),
    // Text: $s1.. If you are too far away from me...I can't let you go...
    S1_IF_YOU_ARE_TOO_FAR_AWAY_FROM_ME__I_CANT_LET_YOU_GO(1800030),
    // Text: Yum-yum, yum-yum
    YUMYUM_YUMYUM(1800291),
    // Text: Delivery duty complete. \\n Go find the Newbie Guide.
    DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE(4151),
    // Text: Acquisition of Soulshot for beginners complete. \\n Go find the Newbie Guide.
    ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE(4152),
    // Text: Acquisition of Weapon Exchange coupon for beginners complete. \\n Go speak with the Newbie Guide.
    ACQUISITION_OF_WEAPON_EXCHANGE_COUPON_FOR_BEGINNERS_COMPLETE_GO_SPEAK_WITH_THE_NEWBIE_GUIDE(4153),
    // Text: Acquisition of race-specific weapon complete. \\n Go find the Newbie Guide.
    ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE(4154),
    // Text: Last duty complete. \\n Go find the Newbie Guide.
    LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE(4155),
    // Text: The awarded Airship Summon License has been received.
    THE_AWARDED_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_RECEIVED(1800276),
    // Text: Your goal will be obstructed or be under a restraint.
    YOUR_GOAL_WILL_BE_OBSTRUCTED_OR_BE_UNDER_A_RESTRAINT(1800309),
    // Text: You may face an unforeseen problem on your course toward the goal.
    YOU_MAY_FACE_AN_UNFORESEEN_PROBLEM_ON_YOUR_COURSE_TOWARD_THE_GOAL(1800310),
    // Text: You may feel nervous and anxious because of unfavorable situations.
    YOU_MAY_FEEL_NERVOUS_AND_ANXIOUS_BECAUSE_OF_UNFAVORABLE_SITUATIONS(1800311),
    // Text: Be warned when the situation is difficult because you may lose your judgment and make an irrational mistake.
    BE_WARNED_WHEN_THE_SITUATION_IS_DIFFICULT_BECAUSE_YOU_MAY_LOSE_YOUR_JUDGMENT_AND_MAKE_AN_IRRATIONAL_MISTAKE(1800312),
    // Text: You may meet a trustworthy person or a good opportunity.
    YOU_MAY_MEET_A_TRUSTWORTHY_PERSON_OR_A_GOOD_OPPORTUNITY(1800313),
    // Text: Your downward life starts taking an upturn.
    YOUR_DOWNWARD_LIFE_STARTS_TAKING_AN_UPTURN(1800314),
    // Text: You will attract attention from people with your popularity.
    YOU_WILL_ATTRACT_ATTENTION_FROM_PEOPLE_WITH_YOUR_POPULARITY(1800315),
    // Text: Your star of fortune says there'll be fish snapping at your bait.
    YOUR_STAR_OF_FORTUNE_SAYS_THERELL_BE_FISH_SNAPPING_AT_YOUR_BAIT(1800316),
    // Text: There may be a conflict born of your dogmatic procedures.
    THERE_MAY_BE_A_CONFLICT_BORN_OF_YOUR_DOGMATIC_PROCEDURES(1800317),
    // Text: Your wisdom and creativity may lead you to success with your goal.
    YOUR_WISDOM_AND_CREATIVITY_MAY_LEAD_YOU_TO_SUCCESS_WITH_YOUR_GOAL(1800318),
    // Text: You may accomplish your goals if you diligently pursue them without giving up.
    YOU_MAY_ACCOMPLISH_YOUR_GOALS_IF_YOU_DILIGENTLY_PURSUE_THEM_WITHOUT_GIVING_UP(1800319),
    // Text: You may get help if you go through the front door without seeking tricks or maneuvers.
    YOU_MAY_GET_HELP_IF_YOU_GO_THROUGH_THE_FRONT_DOOR_WITHOUT_SEEKING_TRICKS_OR_MANEUVERS(1800320),
    // Text: A good result is on the way if you set a goal and bravely proceed toward it.
    A_GOOD_RESULT_IS_ON_THE_WAY_IF_YOU_SET_A_GOAL_AND_BRAVELY_PROCEED_TOWARD_IT(1800321),
    // Text: Everything will be smoothly managed no matter how difficult.
    EVERYTHING_WILL_BE_SMOOTHLY_MANAGED_NO_MATTER_HOW_DIFFICULT(1800322),
    // Text: Be firm and carefully scrutinize circumstances even when things are difficult.
    BE_FIRM_AND_CAREFULLY_SCRUTINIZE_CIRCUMSTANCES_EVEN_WHEN_THINGS_ARE_DIFFICULT(1800323),
    // Text: Always think over to find neglected problems you haven't taken care of yet.
    ALWAYS_THINK_OVER_TO_FIND_NEGLECTED_PROBLEMS_YOU_HAVENT_TAKEN_CARE_OF_YET(1800324),
    // Text: Financial fortune will greet your poor life.
    FINANCIAL_FORTUNE_WILL_GREET_YOUR_POOR_LIFE(1800325),
    // Text: You may acquire wealth and fame after unlucky circumstances.
    YOU_MAY_ACQUIRE_WEALTH_AND_FAME_AFTER_UNLUCKY_CIRCUMSTANCES(1800326),
    // Text: The difficult situations will turn to hope with unforeseen help.
    THE_DIFFICULT_SITUATIONS_WILL_TURN_TO_HOPE_WITH_UNFORESEEN_HELP(1800327),
    // Text: A great task will result in success.
    A_GREAT_TASK_WILL_RESULT_IN_SUCCESS(1800328),
    // Text: You may encounter a precious person who will lift your loneliness and discord.
    YOU_MAY_ENCOUNTER_A_PRECIOUS_PERSON_WHO_WILL_LIFT_YOUR_LONELINESS_AND_DISCORD(1800329),
    // Text: People around you will encourage your every task in the future.
    PEOPLE_AROUND_YOU_WILL_ENCOURAGE_YOUR_EVERY_TASK_IN_THE_FUTURE(1800330),
    // Text: Everything will be smoothly managed.
    EVERYTHING_WILL_BE_SMOOTHLY_MANAGED(1800331),
    // Text: You will meet a person who can cherish your values if you maintain good ties with people.
    YOU_WILL_MEET_A_PERSON_WHO_CAN_CHERISH_YOUR_VALUES_IF_YOU_MAINTAIN_GOOD_TIES_WITH_PEOPLE(1800332),
    // Text: Maintain cooperative attitude since you will meet someone to be of help.
    MAINTAIN_COOPERATIVE_ATTITUDE_SINCE_YOU_WILL_MEET_SOMEONE_TO_BE_OF_HELP(1800333),
    // Text: Keep your moderation and ego in check even in successful phases of your life.
    KEEP_YOUR_MODERATION_AND_EGO_IN_CHECK_EVEN_IN_SUCCESSFUL_PHASES_OF_YOUR_LIFE(1800334),
    // Text: When it comes to work, lifestyle and relationships you'll be better off to go by the text rather than tricks.
    WHEN_IT_COMES_TO_WORK_LIFESTYLE_AND_RELATIONSHIPS_YOULL_BE_BETTER_OFF_TO_GO_BY_THE_TEXT_RATHER_THAN_TRICKS(1800335),
    // Text: Your task will receive substantial support since the surroundings will fully develop.
    YOUR_TASK_WILL_RECEIVE_SUBSTANTIAL_SUPPORT_SINCE_THE_SURROUNDINGS_WILL_FULLY_DEVELOP(1800336),
    // Text: Your star of fortune indicate a success with mental and material assistance.
    YOUR_STAR_OF_FORTUNE_INDICATE_A_SUCCESS_WITH_MENTAL_AND_MATERIAL_ASSISTANCE(1800337),
    // Text: You will enjoy popularity with your creative talents and clever acts.
    YOU_WILL_ENJOY_POPULARITY_WITH_YOUR_CREATIVE_TALENTS_AND_CLEVER_ACTS(1800338),
    // Text: People will line up to be of assistance to you.
    PEOPLE_WILL_LINE_UP_TO_BE_OF_ASSISTANCE_TO_YOU(1800339),
    // Text: You may meet someone to share your journey.
    YOU_MAY_MEET_SOMEONE_TO_SHARE_YOUR_JOURNEY(1800340),
    // Text: You may achieve connections in many fields.
    YOU_MAY_ACHIEVE_CONNECTIONS_IN_MANY_FIELDS(1800341),
    // Text: An attitude that continually studies and explores is needed, and always be sincere.
    AN_ATTITUDE_THAT_CONTINUALLY_STUDIES_AND_EXPLORES_IS_NEEDED_AND_ALWAYS_BE_SINCERE(1800342),
    // Text: It's an image of a butterfly on a flower in warm spring air.
    ITS_AN_IMAGE_OF_A_BUTTERFLY_ON_A_FLOWER_IN_WARM_SPRING_AIR(1800343),
    // Text: Your goals will move smoothly with peace and happiness in your life.
    YOUR_GOALS_WILL_MOVE_SMOOTHLY_WITH_PEACE_AND_HAPPINESS_IN_YOUR_LIFE(1800344),
    // Text: Love may sprout its leaves when you treat those around you with care.
    LOVE_MAY_SPROUT_ITS_LEAVES_WHEN_YOU_TREAT_THOSE_AROUND_YOU_WITH_CARE(1800345),
    // Text: You may climb into a higher position with others' trust if you faithfully carry out your duties.
    YOU_MAY_CLIMB_INTO_A_HIGHER_POSITION_WITH_OTHERS_TRUST_IF_YOU_FAITHFULLY_CARRY_OUT_YOUR_DUTIES(1800346),
    // Text: Everything can fall apart if you greedily aim by pure luck.
    EVERYTHING_CAN_FALL_APART_IF_YOU_GREEDILY_AIM_BY_PURE_LUCK(1800347),
    // Text: Do not underestimate the importance of meeting people.
    DO_NOT_UNDERESTIMATE_THE_IMPORTANCE_OF_MEETING_PEOPLE(1800348),
    // Text: An arrow will coalesce into the bow.
    AN_ARROW_WILL_COALESCE_INTO_THE_BOW(1800349),
    // Text: A bony limb of a tree may bear its fruit.
    A_BONY_LIMB_OF_A_TREE_MAY_BEAR_ITS_FRUIT(1800350),
    // Text: You will be rewarded for your efforts and accomplishments.
    YOU_WILL_BE_REWARDED_FOR_YOUR_EFFORTS_AND_ACCOMPLISHMENTS(1800351),
    // Text: No matter where it lies, your faithful drive leads you to success.
    NO_MATTER_WHERE_IT_LIES_YOUR_FAITHFUL_DRIVE_LEADS_YOU_TO_SUCCESS(1800352),
    // Text: People will be attracted to your loyalties.
    PEOPLE_WILL_BE_ATTRACTED_TO_YOUR_LOYALTIES(1800353),
    // Text: You may trust yourself rather than others' talks.
    YOU_MAY_TRUST_YOURSELF_RATHER_THAN_OTHERS_TALKS(1800354),
    // Text: Creative thinking away from the old viewpoint may help you.
    CREATIVE_THINKING_AWAY_FROM_THE_OLD_VIEWPOINT_MAY_HELP_YOU(1800355),
    // Text: Patience without being impetuous of the results will only bear a positive outcome.
    PATIENCE_WITHOUT_BEING_IMPETUOUS_OF_THE_RESULTS_WILL_ONLY_BEAR_A_POSITIVE_OUTCOME(1800356),
    // Text: The dead will come alive.
    THE_DEAD_WILL_COME_ALIVE(1800357),
    // Text: There will be a shocking incident.
    THERE_WILL_BE_A_SHOCKING_INCIDENT(1800358),
    // Text: You will enjoy a huge success after unforeseen luck comes before you.
    YOU_WILL_ENJOY_A_HUGE_SUCCESS_AFTER_UNFORESEEN_LUCK_COMES_BEFORE_YOU(1800359),
    // Text: Do not give up since there may be a miraculous rescue from the course of despair.
    DO_NOT_GIVE_UP_SINCE_THERE_MAY_BE_A_MIRACULOUS_RESCUE_FROM_THE_COURSE_OF_DESPAIR(1800360),
    // Text: An attitude to try one's best to pursue the goal is needed.
    AN_ATTITUDE_TO_TRY_ONES_BEST_TO_PURSUE_THE_GOAL_IS_NEEDED(1800361),
    // Text: You may get a shot in the arm in your life after meeting a good person.
    YOU_MAY_GET_A_SHOT_IN_THE_ARM_IN_YOUR_LIFE_AFTER_MEETING_A_GOOD_PERSON(1800362),
    // Text: You may get a big help in the course of your life.
    YOU_MAY_GET_A_BIG_HELP_IN_THE_COURSE_OF_YOUR_LIFE(1800363),
    // Text: A rare opportunity will come to you so you may prosper.
    A_RARE_OPPORTUNITY_WILL_COME_TO_YOU_SO_YOU_MAY_PROSPER(1800364),
    // Text: A hungry falcon will have meat.
    A_HUNGRY_FALCON_WILL_HAVE_MEAT(1800365),
    // Text: A household in need will acquire a fortune and meat.
    A_HOUSEHOLD_IN_NEED_WILL_ACQUIRE_A_FORTUNE_AND_MEAT(1800366),
    // Text: A hard situation will come to its end with materialistic and mental help from others.
    A_HARD_SITUATION_WILL_COME_TO_ITS_END_WITH_MATERIALISTIC_AND_MENTAL_HELP_FROM_OTHERS(1800367),
    // Text: If you set a firm goal without surrender, there will be a person who can offer help and care.
    IF_YOU_SET_A_FIRM_GOAL_WITHOUT_SURRENDER_THERE_WILL_BE_A_PERSON_WHO_CAN_OFFER_HELP_AND_CARE(1800368),
    // Text: You'll gain others' trust when you maintain a sincere and honest attitude.
    YOULL_GAIN_OTHERS_TRUST_WHEN_YOU_MAINTAIN_A_SINCERE_AND_HONEST_ATTITUDE(1800369),
    // Text: Be independent at all times.
    BE_INDEPENDENT_AT_ALL_TIMES(1800370),
    // Text: It's a wagon with no wheels.
    ITS_A_WAGON_WITH_NO_WHEELS(1800371),
    // Text: You've set a goal but there may be obstacles in reality.
    YOUVE_SET_A_GOAL_BUT_THERE_MAY_BE_OBSTACLES_IN_REALITY(1800372),
    // Text: You're running toward the goal but there won't be as many outcomes as you thought.
    YOURE_RUNNING_TOWARD_THE_GOAL_BUT_THERE_WONT_BE_AS_MANY_OUTCOMES_AS_YOU_THOUGHT(1800373),
    // Text: There are many things to consider after encountering hindrances.
    THERE_ARE_MANY_THINGS_TO_CONSIDER_AFTER_ENCOUNTERING_HINDRANCES(1800374),
    // Text: A reckless move may bring a failure.
    A_RECKLESS_MOVE_MAY_BRING_A_FAILURE(1800375),
    // Text: You may lose people's trust if you lack prudence at all times.
    YOU_MAY_LOSE_PEOPLES_TRUST_IF_YOU_LACK_PRUDENCE_AT_ALL_TIMES(1800376),
    // Text: You may need to reflect on yourself with deliberation and wait for an opportunity.
    YOU_MAY_NEED_TO_REFLECT_ON_YOURSELF_WITH_DELIBERATION_AND_WAIT_FOR_AN_OPPORTUNITY(1800377),
    // Text: A poor scholar receives a stipend.
    A_POOR_SCHOLAR_RECEIVES_A_STIPEND(1800378),
    // Text: A scholar gets a pass toward fame and fortune.
    A_SCHOLAR_GETS_A_PASS_TOWARD_FAME_AND_FORTUNE(1800379),
    // Text: Your ambition and dream will come true.
    YOUR_AMBITION_AND_DREAM_WILL_COME_TRUE(1800380),
    // Text: Complicated problems around you may start being solved one after another.
    COMPLICATED_PROBLEMS_AROUND_YOU_MAY_START_BEING_SOLVED_ONE_AFTER_ANOTHER(1800381),
    // Text: You will have a good result if you diligently pursue one goal without being dragged from your past.
    YOU_WILL_HAVE_A_GOOD_RESULT_IF_YOU_DILIGENTLY_PURSUE_ONE_GOAL_WITHOUT_BEING_DRAGGED_FROM_YOUR_PAST(1800382),
    // Text: You may need to rid yourself of old and worn habits.
    YOU_MAY_NEED_TO_RID_YOURSELF_OF_OLD_AND_WORN_HABITS(1800383),
    // Text: Be responsible with your tasks but do not hesitate to ask for colleagues' help.
    BE_RESPONSIBLE_WITH_YOUR_TASKS_BUT_DO_NOT_HESITATE_TO_ASK_FOR_COLLEAGUES_HELP(1800384),
    // Text: Fish transforms into a dragon.
    FISH_TRANSFORMS_INTO_A_DRAGON(1800385),
    // Text: Your dream may come true, and fame and fortune will come to you.
    YOUR_DREAM_MAY_COME_TRUE_AND_FAME_AND_FORTUNE_WILL_COME_TO_YOU(1800386),
    // Text: What you've planed will be accomplished.
    WHAT_YOUVE_PLANED_WILL_BE_ACCOMPLISHED(1800387),
    // Text: You may acquire money or a new opportunity from a place you wouldn't have thought of.
    YOU_MAY_ACQUIRE_MONEY_OR_A_NEW_OPPORTUNITY_FROM_A_PLACE_YOU_WOULDNT_HAVE_THOUGHT_OF(1800388),
    // Text: There will be many offers to you, you may think them over carefully.
    THERE_WILL_BE_MANY_OFFERS_TO_YOU_YOU_MAY_THINK_THEM_OVER_CAREFULLY(1800389),
    // Text: It may be a good idea not to become involved in others' business.
    IT_MAY_BE_A_GOOD_IDEA_NOT_TO_BECOME_INVOLVED_IN_OTHERS_BUSINESS(1800390),
    // Text: Everything will go smoothly but be aware of danger from being careless and remiss.
    EVERYTHING_WILL_GO_SMOOTHLY_BUT_BE_AWARE_OF_DANGER_FROM_BEING_CARELESS_AND_REMISS(1800391),
    // Text: If you sincerely care for someone you love, a big reward will return to you.
    IF_YOU_SINCERELY_CARE_FOR_SOMEONE_YOU_LOVE_A_BIG_REWARD_WILL_RETURN_TO_YOU(1800392),
    // Text: A remedy is on its way for a serious illness.
    A_REMEDY_IS_ON_ITS_WAY_FOR_A_SERIOUS_ILLNESS(1800393),
    // Text: You may acquire a precious medicine to recover after suffering a disease of a serious nature.
    YOU_MAY_ACQUIRE_A_PRECIOUS_MEDICINE_TO_RECOVER_AFTER_SUFFERING_A_DISEASE_OF_A_SERIOUS_NATURE(1800394),
    // Text: You may realize your dream by meeting a man of distinction at a difficult time.
    YOU_MAY_REALIZE_YOUR_DREAM_BY_MEETING_A_MAN_OF_DISTINCTION_AT_A_DIFFICULT_TIME(1800395),
    // Text: You may suffer one or two hardships on your journey.
    YOU_MAY_SUFFER_ONE_OR_TWO_HARDSHIPS_ON_YOUR_JOURNEY(1800396),
    // Text: If you keep smiling without despair, people will come to trust you and offer help.
    IF_YOU_KEEP_SMILING_WITHOUT_DESPAIR_PEOPLE_WILL_COME_TO_TRUST_YOU_AND_OFFER_HELP(1800397),
    // Text: Seek stability rather than dynamics in your life.
    SEEK_STABILITY_RATHER_THAN_DYNAMICS_IN_YOUR_LIFE(1800398),
    // Text: It's a good idea to be careful and secure at all times.
    ITS_A_GOOD_IDEA_TO_BE_CAREFUL_AND_SECURE_AT_ALL_TIMES(1800399),
    // Text: You can't perform the job with bound hands.
    YOU_CANT_PERFORM_THE_JOB_WITH_BOUND_HANDS(1800400),
    // Text: You may lose your drive and feel lost.
    YOU_MAY_LOSE_YOUR_DRIVE_AND_FEEL_LOST(1800401),
    // Text: You may be unable to concentrate with so many problems occurring.
    YOU_MAY_BE_UNABLE_TO_CONCENTRATE_WITH_SO_MANY_PROBLEMS_OCCURRING(1800402),
    // Text: Your achievement unfairly may go somewhere else.
    YOUR_ACHIEVEMENT_UNFAIRLY_MAY_GO_SOMEWHERE_ELSE(1800403),
    // Text: Do not start a task that's not clear to you.
    DO_NOT_START_A_TASK_THATS_NOT_CLEAR_TO_YOU(1800404),
    // Text: You will need to be prepared for all events.
    YOU_WILL_NEED_TO_BE_PREPARED_FOR_ALL_EVENTS(1800405),
    // Text: Someone will acknowledge you if you relentlessly keep trying and do not give up when facing hardships.
    SOMEONE_WILL_ACKNOWLEDGE_YOU_IF_YOU_RELENTLESSLY_KEEP_TRYING_AND_DO_NOT_GIVE_UP_WHEN_FACING_HARDSHIPS(1800406),
    // Text: You may perfect yourself like a dragon's horn decorates the dragon.
    YOU_MAY_PERFECT_YOURSELF_LIKE_A_DRAGONS_HORN_DECORATES_THE_DRAGON(1800407),
    // Text: Your true value starts to shine.
    YOUR_TRUE_VALUE_STARTS_TO_SHINE(1800408),
    // Text: Your steady pursuit of new information and staying ahead of others will raise your value.
    YOUR_STEADY_PURSUIT_OF_NEW_INFORMATION_AND_STAYING_AHEAD_OF_OTHERS_WILL_RAISE_YOUR_VALUE(1800409),
    // Text: Maintaining confidence with work or relationships may bring good results.
    MAINTAINING_CONFIDENCE_WITH_WORK_OR_RELATIONSHIPS_MAY_BRING_GOOD_RESULTS(1800410),
    // Text: Learn to work with others since overconfidence will bear wrath.
    LEARN_TO_WORK_WITH_OTHERS_SINCE_OVERCONFIDENCE_WILL_BEAR_WRATH(1800411),
    // Text: The dragon now acquires an eagle's wings.
    THE_DRAGON_NOW_ACQUIRES_AN_EAGLES_WINGS(1800412),
    // Text: As the dragon flies high in the sky, your goals and dreams may come true.
    AS_THE_DRAGON_FLIES_HIGH_IN_THE_SKY_YOUR_GOALS_AND_DREAMS_MAY_COME_TRUE(1800413),
    // Text: Luck enters into your work, hobby, family and love.
    LUCK_ENTERS_INTO_YOUR_WORK_HOBBY_FAMILY_AND_LOVE(1800414),
    // Text: Whatever you do, it will accompany winning.
    WHATEVER_YOU_DO_IT_WILL_ACCOMPANY_WINNING(1800415),
    // Text: It's as good as it gets with unforeseen fortune rolling your way.
    ITS_AS_GOOD_AS_IT_GETS_WITH_UNFORESEEN_FORTUNE_ROLLING_YOUR_WAY(1800416),
    // Text: A greedy act with no prudence will bring a surprise at the end.
    A_GREEDY_ACT_WITH_NO_PRUDENCE_WILL_BRING_A_SURPRISE_AT_THE_END(1800417),
    // Text: Think carefully and act with caution at all times.
    THINK_CAREFULLY_AND_ACT_WITH_CAUTION_AT_ALL_TIMES(1800418),
    // Text: If a tree doesn't have its roots, there will be no fruit.
    IF_A_TREE_DOESNT_HAVE_ITS_ROOTS_THERE_WILL_BE_NO_FRUIT(1800419),
    // Text: Hard work doesn't bear fruit.
    HARD_WORK_DOESNT_BEAR_FRUIT(1800420),
    // Text: Financial difficulties may bring an ordeal.
    FINANCIAL_DIFFICULTIES_MAY_BRING_AN_ORDEAL(1800421),
    // Text: What used to be well managed may stumble one after another.
    WHAT_USED_TO_BE_WELL_MANAGED_MAY_STUMBLE_ONE_AFTER_ANOTHER(1800422),
    // Text: A feeling of frustration may follow disappointment.
    A_FEELING_OF_FRUSTRATION_MAY_FOLLOW_DISAPPOINTMENT(1800423),
    // Text: Be cautioned as unharnessed behavior at difficult times can ruin relationships.
    BE_CAUTIONED_AS_UNHARNESSED_BEHAVIOR_AT_DIFFICULT_TIMES_CAN_RUIN_RELATIONSHIPS(1800424),
    // Text: Curtail greed and be grateful for small returns as modesty is needed.
    CURTAIL_GREED_AND_BE_GRATEFUL_FOR_SMALL_RETURNS_AS_MODESTY_IS_NEEDED(1800425),
    // Text: The person that came under your wings will leave.
    THE_PERSON_THAT_CAME_UNDER_YOUR_WINGS_WILL_LEAVE(1800426),
    // Text: Your work and relationship with colleagues will be well managed if you maintain your devotion.
    YOUR_WORK_AND_RELATIONSHIP_WITH_COLLEAGUES_WILL_BE_WELL_MANAGED_IF_YOU_MAINTAIN_YOUR_DEVOTION(1800427),
    // Text: Calculating your profit in relationships without displaying any courteous manners will bring malicious gossip and ruin your value.
    CALCULATING_YOUR_PROFIT_IN_RELATIONSHIPS_WITHOUT_DISPLAYING_ANY_COURTEOUS_MANNERS_WILL_BRING_MALICIOUS_GOSSIP_AND_RUIN_YOUR_VALUE(1800428),
    // Text: Consider other's situations and treat them sincerely at all times.
    CONSIDER_OTHERS_SITUATIONS_AND_TREAT_THEM_SINCERELY_AT_ALL_TIMES(1800429),
    // Text: Do not loosen up with your precautions.
    DO_NOT_LOOSEN_UP_WITH_YOUR_PRECAUTIONS(1800430),
    // Text: Reflect other's opinions as a mistake always lies ahead of an arbitrary decision.
    REFLECT_OTHERS_OPINIONS_AS_A_MISTAKE_ALWAYS_LIES_AHEAD_OF_AN_ARBITRARY_DECISION(1800431),
    // Text: A blind man goes right through the door.
    A_BLIND_MAN_GOES_RIGHT_THROUGH_THE_DOOR(1800432),
    // Text: A heart falls into hopelessness as things are in disarray.
    A_HEART_FALLS_INTO_HOPELESSNESS_AS_THINGS_ARE_IN_DISARRAY(1800433),
    // Text: Hopelessness may fill your heart as your work falls into a maze.
    HOPELESSNESS_MAY_FILL_YOUR_HEART_AS_YOUR_WORK_FALLS_INTO_A_MAZE(1800434),
    // Text: Difficulties lie ahead of an unforeseen problem even with your hard work.
    DIFFICULTIES_LIE_AHEAD_OF_AN_UNFORESEEN_PROBLEM_EVEN_WITH_YOUR_HARD_WORK(1800435),
    // Text: There may be more occasions you will want to ask favors from others as you lose confidence in you.
    THERE_MAY_BE_MORE_OCCASIONS_YOU_WILL_WANT_TO_ASK_FAVORS_FROM_OTHERS_AS_YOU_LOSE_CONFIDENCE_IN_YOU(1800436),
    // Text: Be brave and ambitious as no bird can fly into the sky by staying in their nest.
    BE_BRAVE_AND_AMBITIOUS_AS_NO_BIRD_CAN_FLY_INTO_THE_SKY_BY_STAYING_IN_THEIR_NEST(1800437),
    // Text: It's a good idea not to start an unclear task and always look for someone you can trust and rely upon.
    ITS_A_GOOD_IDEA_NOT_TO_START_AN_UNCLEAR_TASK_AND_ALWAYS_LOOK_FOR_SOMEONE_YOU_CAN_TRUST_AND_RELY_UPON(1800438),
    // Text: Hunting won't be successful as the falcon lacks its claws.
    HUNTING_WONT_BE_SUCCESSFUL_AS_THE_FALCON_LACKS_ITS_CLAWS(1800439),
    // Text: A prepared plan won't move smoothly.
    A_PREPARED_PLAN_WONT_MOVE_SMOOTHLY(1800440),
    // Text: An easy task may fail if one is consumed by greed and overconfidence.
    AN_EASY_TASK_MAY_FAIL_IF_ONE_IS_CONSUMED_BY_GREED_AND_OVERCONFIDENCE(1800441),
    // Text: Impatience may lie ahead as the situation is unfavorable.
    IMPATIENCE_MAY_LIE_AHEAD_AS_THE_SITUATION_IS_UNFAVORABLE(1800442),
    // Text: Thoughtful foresight is needed before a disaster may fall upon you.
    THOUGHTFUL_FORESIGHT_IS_NEEDED_BEFORE_A_DISASTER_MAY_FALL_UPON_YOU(1800443),
    // Text: Refrain from dictatorial acts as caring for others around you with dignity is much needed.
    REFRAIN_FROM_DICTATORIAL_ACTS_AS_CARING_FOR_OTHERS_AROUND_YOU_WITH_DIGNITY_IS_MUCH_NEEDED(1800444),
    // Text: Things are messy with no good sign.
    THINGS_ARE_MESSY_WITH_NO_GOOD_SIGN(1800445),
    // Text: You may fall into a vexing situation as bad circumstances will arise.
    YOU_MAY_FALL_INTO_A_VEXING_SITUATION_AS_BAD_CIRCUMSTANCES_WILL_ARISE(1800446),
    // Text: Relationships with people may be contrary to your expectations.
    RELATIONSHIPS_WITH_PEOPLE_MAY_BE_CONTRARY_TO_YOUR_EXPECTATIONS(1800447),
    // Text: Do not seek a quick fix as the problem needs a fundamental resolution.
    DO_NOT_SEEK_A_QUICK_FIX_AS_THE_PROBLEM_NEEDS_A_FUNDAMENTAL_RESOLUTION(1800448),
    // Text: Seek peace in your heart as vulgar display of your emotions may harm you.
    SEEK_PEACE_IN_YOUR_HEART_AS_VULGAR_DISPLAY_OF_YOUR_EMOTIONS_MAY_HARM_YOU(1800449),
    // Text: Information for success may come from the conversations with people around you.
    INFORMATION_FOR_SUCCESS_MAY_COME_FROM_THE_CONVERSATIONS_WITH_PEOPLE_AROUND_YOU(1800450),
    // Text: Be confident and act reliantly at all times.
    BE_CONFIDENT_AND_ACT_RELIANTLY_AT_ALL_TIMES(1800451),
    // Text: A child gets a treasure.
    A_CHILD_GETS_A_TREASURE(1800452),
    // Text: Good fortune and opportunity may lie ahead as if one's born in a golden spoon.
    GOOD_FORTUNE_AND_OPPORTUNITY_MAY_LIE_AHEAD_AS_IF_ONES_BORN_IN_A_GOLDEN_SPOON(1800453),
    // Text: Your life flows as if it's on a silk surface and unexpected fortune and success may come to you.
    YOUR_LIFE_FLOWS_AS_IF_ITS_ON_A_SILK_SURFACE_AND_UNEXPECTED_FORTUNE_AND_SUCCESS_MAY_COME_TO_YOU(1800454),
    // Text: Temporary luck may come to you with no effort.
    TEMPORARY_LUCK_MAY_COME_TO_YOU_WITH_NO_EFFORT(1800455),
    // Text: Plan ahead with patience but execute with swiftness.
    PLAN_AHEAD_WITH_PATIENCE_BUT_EXECUTE_WITH_SWIFTNESS(1800456),
    // Text: The abilities to amend, foresee and analyze may raise your value.
    THE_ABILITIES_TO_AMEND_FORESEE_AND_ANALYZE_MAY_RAISE_YOUR_VALUE(1800457),
    // Text: Bigger mistakes will be on the road if you fail to correct a small mistake.
    BIGGER_MISTAKES_WILL_BE_ON_THE_ROAD_IF_YOU_FAIL_TO_CORRECT_A_SMALL_MISTAKE(1800458),
    // Text: Don't be evasive to accept new findings or experiences.
    DONT_BE_EVASIVE_TO_ACCEPT_NEW_FINDINGS_OR_EXPERIENCES(1800459),
    // Text: Don't be irritated as the situations don't move as planned.
    DONT_BE_IRRITATED_AS_THE_SITUATIONS_DONT_MOVE_AS_PLANNED(1800460),
    // Text: Be warned as you may be overwhelmed by surroundings if you lack a clear opinion.
    BE_WARNED_AS_YOU_MAY_BE_OVERWHELMED_BY_SURROUNDINGS_IF_YOU_LACK_A_CLEAR_OPINION(1800461),
    // Text: You may live an affluent life even without possessions.
    YOU_MAY_LIVE_AN_AFFLUENT_LIFE_EVEN_WITHOUT_POSSESSIONS(1800462),
    // Text: You will gain popularity as you help people with money you earnestly earned.
    YOU_WILL_GAIN_POPULARITY_AS_YOU_HELP_PEOPLE_WITH_MONEY_YOU_EARNESTLY_EARNED(1800463),
    // Text: Your heart and body may be in health.
    YOUR_HEART_AND_BODY_MAY_BE_IN_HEALTH(1800464),
    // Text: Be warned as you may be dragged to an unwanted direction if not cautious.
    BE_WARNED_AS_YOU_MAY_BE_DRAGGED_TO_AN_UNWANTED_DIRECTION_IF_NOT_CAUTIOUS(1800465),
    // Text: You may meet many new people but it will be difficult to find a perfect person who wins your heart.
    YOU_MAY_MEET_MANY_NEW_PEOPLE_BUT_IT_WILL_BE_DIFFICULT_TO_FIND_A_PERFECT_PERSON_WHO_WINS_YOUR_HEART(1800466),
    // Text: There may be an occasion where you are consoled by people.
    THERE_MAY_BE_AN_OCCASION_WHERE_YOU_ARE_CONSOLED_BY_PEOPLE(1800467),
    // Text: It may not be a good time for a change even if there's tedium in daily life.
    IT_MAY_NOT_BE_A_GOOD_TIME_FOR_A_CHANGE_EVEN_IF_THERES_TEDIUM_IN_DAILY_LIFE(1800468),
    // Text: The money you spend for yourself may act as an investment and bring you a return.
    THE_MONEY_YOU_SPEND_FOR_YOURSELF_MAY_ACT_AS_AN_INVESTMENT_AND_BRING_YOU_A_RETURN(1800469),
    // Text: The money you spend for others will be wasted so be cautious.
    THE_MONEY_YOU_SPEND_FOR_OTHERS_WILL_BE_WASTED_SO_BE_CAUTIOUS(1800470),
    // Text: Be warned so as not to have unnecessary expenses.
    BE_WARNED_SO_AS_NOT_TO_HAVE_UNNECESSARY_EXPENSES(1800471),
    // Text: Your star indicated such good luck, participate in bonus giveaways or events.
    YOUR_STAR_INDICATED_SUCH_GOOD_LUCK_PARTICIPATE_IN_BONUS_GIVEAWAYS_OR_EVENTS(1800472),
    // Text: You may grab unexpected luck.
    YOU_MAY_GRAB_UNEXPECTED_LUCK(1800473),
    // Text: The person in your heart may naturally come to you.
    THE_PERSON_IN_YOUR_HEART_MAY_NATURALLY_COME_TO_YOU(1800474),
    // Text: There will be a good result if you keep your own pace regardless of others' judgement.
    THERE_WILL_BE_A_GOOD_RESULT_IF_YOU_KEEP_YOUR_OWN_PACE_REGARDLESS_OF_OTHERS_JUDGEMENT(1800475),
    // Text: Be warned as unexpected luck may be wasted with your reckless comments.
    BE_WARNED_AS_UNEXPECTED_LUCK_MAY_BE_WASTED_WITH_YOUR_RECKLESS_COMMENTS(1800476),
    // Text: Overconfidence will convince you to carry a task above your reach and there will be consequences.
    OVERCONFIDENCE_WILL_CONVINCE_YOU_TO_CARRY_A_TASK_ABOVE_YOUR_REACH_AND_THERE_WILL_BE_CONSEQUENCES(1800477),
    // Text: Momentarily delay an important decision.
    MOMENTARILY_DELAY_AN_IMPORTANT_DECISION(1800478),
    // Text: Trouble spots lie ahead when talking to superiors or people close to you.
    TROUBLE_SPOTS_LIE_AHEAD_WHEN_TALKING_TO_SUPERIORS_OR_PEOPLE_CLOSE_TO_YOU(1800479),
    // Text: Be warned as your words can hurt others or others' words can hurt you.
    BE_WARNED_AS_YOUR_WORDS_CAN_HURT_OTHERS_OR_OTHERS_WORDS_CAN_HURT_YOU(1800480),
    // Text: Make a loud boast and you may have to pay to cover unnecessary expenses.
    MAKE_A_LOUD_BOAST_AND_YOU_MAY_HAVE_TO_PAY_TO_COVER_UNNECESSARY_EXPENSES(1800481),
    // Text: Skillful evasion is needed when dealing with people who pick fights as a disaster may arise from it.
    SKILLFUL_EVASION_IS_NEEDED_WHEN_DEALING_WITH_PEOPLE_WHO_PICK_FIGHTS_AS_A_DISASTER_MAY_ARISE_FROM_IT(1800482),
    // Text: Keep a low profile as too strong an opinion will attract adverse reactions.
    KEEP_A_LOW_PROFILE_AS_TOO_STRONG_AN_OPINION_WILL_ATTRACT_ADVERSE_REACTIONS(1800483),
    // Text: Do not unnecessarily provoke misunderstanding as you may be involved in malicious gossip.
    DO_NOT_UNNECESSARILY_PROVOKE_MISUNDERSTANDING_AS_YOU_MAY_BE_INVOLVED_IN_MALICIOUS_GOSSIP(1800484),
    // Text: Check your belongings as you may lose what you possess.
    CHECK_YOUR_BELONGINGS_AS_YOU_MAY_LOSE_WHAT_YOU_POSSESS(1800485),
    // Text: Be flexible enough to play up to others.
    BE_FLEXIBLE_ENOUGH_TO_PLAY_UP_TO_OTHERS(1800486),
    // Text: Pay special attention when meeting or talking to people as relationships may go amiss.
    PAY_SPECIAL_ATTENTION_WHEN_MEETING_OR_TALKING_TO_PEOPLE_AS_RELATIONSHIPS_MAY_GO_AMISS(1800487),
    // Text: When the important moment arrives, decide upon what you truly want without measuring others' judgement.
    WHEN_THE_IMPORTANT_MOMENT_ARRIVES_DECIDE_UPON_WHAT_YOU_TRULY_WANT_WITHOUT_MEASURING_OTHERS_JUDGEMENT(1800488),
    // Text: Luck will always follow you if you travel and read many books.
    LUCK_WILL_ALWAYS_FOLLOW_YOU_IF_YOU_TRAVEL_AND_READ_MANY_BOOKS(1800489),
    // Text: Head to a place that needs your advice as good ideas and wisdom will flourish.
    HEAD_TO_A_PLACE_THAT_NEEDS_YOUR_ADVICE_AS_GOOD_IDEAS_AND_WISDOM_WILL_FLOURISH(1800490),
    // Text: Someone's life may change upon your advice.
    SOMEONES_LIFE_MAY_CHANGE_UPON_YOUR_ADVICE(1800491),
    // Text: It's a proper time to plan for the future rather than a short term plan.
    ITS_A_PROPER_TIME_TO_PLAN_FOR_THE_FUTURE_RATHER_THAN_A_SHORT_TERM_PLAN(1800492),
    // Text: Many thoughtful plans at present time will be of great help in the future.
    MANY_THOUGHTFUL_PLANS_AT_PRESENT_TIME_WILL_BE_OF_GREAT_HELP_IN_THE_FUTURE(1800493),
    // Text: Patience may be needed as a big quarrel arises between you and a person close to you.
    PATIENCE_MAY_BE_NEEDED_AS_A_BIG_QUARREL_ARISES_BETWEEN_YOU_AND_A_PERSON_CLOSE_TO_YOU(1800494),
    // Text: Do not ask for financial help when the time is difficult. Your pride will be hurt without gaining any money.
    DO_NOT_ASK_FOR_FINANCIAL_HELP_WHEN_THE_TIME_IS_DIFFICULT(1800495),
    // Text: Connection with a special person starts with a mere incident.
    CONNECTION_WITH_A_SPECIAL_PERSON_STARTS_WITH_A_MERE_INCIDENT(1800496),
    // Text: Stubbornness regardless of the matter will only bear danger.
    STUBBORNNESS_REGARDLESS_OF_THE_MATTER_WILL_ONLY_BEAR_DANGER(1800497),
    // Text: Keep good manners and value taciturnity as light-heartedness may bring misfortune.
    KEEP_GOOD_MANNERS_AND_VALUE_TACITURNITY_AS_LIGHTHEARTEDNESS_MAY_BRING_MISFORTUNE(1800498),
    // Text: You may meet the opposite sex.
    YOU_MAY_MEET_THE_OPPOSITE_SEX(1800499),
    // Text: Greed by wanting to take wealth may bring unfortunate disaster.
    GREED_BY_WANTING_TO_TAKE_WEALTH_MAY_BRING_UNFORTUNATE_DISASTER(1800500),
    // Text: Loss is ahead, refrain from investing. Try to save the money in your pockets.
    LOSS_IS_AHEAD_REFRAIN_FROM_INVESTING(1800501),
    // Text: Your wealth luck is dim, avoid any offers.
    YOUR_WEALTH_LUCK_IS_DIM_AVOID_ANY_OFFERS(1800502),
    // Text: A bigger challenge may be when delaying today's work.
    A_BIGGER_CHALLENGE_MAY_BE_WHEN_DELAYING_TODAYS_WORK(1800503),
    // Text: There will be difficulty, but a good result may be ahead when facing it responsibly.
    THERE_WILL_BE_DIFFICULTY_BUT_A_GOOD_RESULT_MAY_BE_AHEAD_WHEN_FACING_IT_RESPONSIBLY(1800504),
    // Text: Even with some difficulties, expand the range of your scope where you are in charge. It will return to you as help.
    EVEN_WITH_SOME_DIFFICULTIES_EXPAND_THE_RANGE_OF_YOUR_SCOPE_WHERE_YOU_ARE_IN_CHARGE(1800505),
    // Text: Focus on maintaining organized surroundings to help reduce your losses.
    FOCUS_ON_MAINTAINING_ORGANIZED_SURROUNDINGS_TO_HELP_REDUCE_YOUR_LOSSES(1800506),
    // Text: Luck lies ahead when waiting for people rather than following them.
    LUCK_LIES_AHEAD_WHEN_WAITING_FOR_PEOPLE_RATHER_THAN_FOLLOWING_THEM(1800507),
    // Text: Do not offer your hand first even when things are hasty. The relationship may fall apart.
    DO_NOT_OFFER_YOUR_HAND_FIRST_EVEN_WHEN_THINGS_ARE_HASTY(1800508),
    // Text: Your wealth luck is rising, there will be some good result.
    YOUR_WEALTH_LUCK_IS_RISING_THERE_WILL_BE_SOME_GOOD_RESULT(1800509),
    // Text: You may fall in danger each time when acting upon improvisation.
    YOU_MAY_FALL_IN_DANGER_EACH_TIME_WHEN_ACTING_UPON_IMPROVISATION(1800510),
    // Text: Be warned as a childishly act before elders may ruin everything.
    BE_WARNED_AS_A_CHILDISHLY_ACT_BEFORE_ELDERS_MAY_RUIN_EVERYTHING(1800511),
    // Text: Things will move effortlessly but luck will vanish with your audacity.
    THINGS_WILL_MOVE_EFFORTLESSLY_BUT_LUCK_WILL_VANISH_WITH_YOUR_AUDACITY(1800512),
    // Text: Luck may be continued only when humility is maintained after success.
    LUCK_MAY_BE_CONTINUED_ONLY_WHEN_HUMILITY_IS_MAINTAINED_AFTER_SUCCESS(1800513),
    // Text: A new person may appear to create a love triangle.
    A_NEW_PERSON_MAY_APPEAR_TO_CREATE_A_LOVE_TRIANGLE(1800514),
    // Text: Look for someone with a similar style. It will open up for the good.
    LOOK_FOR_SOMEONE_WITH_A_SIMILAR_STYLE(1800515),
    // Text: An offer may soon be made to collaborate a task but delaying it will be a good idea.
    AN_OFFER_MAY_SOON_BE_MADE_TO_COLLABORATE_A_TASK_BUT_DELAYING_IT_WILL_BE_A_GOOD_IDEA(1800516),
    // Text: Partnership is out of luck, avoid someone who rushes you to start a collaboration.
    PARTNERSHIP_IS_OUT_OF_LUCK_AVOID_SOMEONE_WHO_RUSHES_YOU_TO_START_A_COLLABORATION(1800517),
    // Text: Focus on networking with like-minded people. They may join you for a big mission in the future.
    FOCUS_ON_NETWORKING_WITH_LIKEMINDED_PEOPLE(1800518),
    // Text: Be warned when someone says you are innocent as that's not a compliment.
    BE_WARNED_WHEN_SOMEONE_SAYS_YOU_ARE_INNOCENT_AS_THATS_NOT_A_COMPLIMENT(1800519),
    // Text: You may be scammed. Be cautious as there may be a big loss by underestimating others.
    YOU_MAY_BE_SCAMMED(1800520),
    // Text: Luck at decision-making is dim, avoid subjective conclusions and rely on universal common-sense.
    LUCK_AT_DECISIONMAKING_IS_DIM_AVOID_SUBJECTIVE_CONCLUSIONS_AND_RELY_ON_UNIVERSAL_COMMONSENSE(1800521),
    // Text: Your weakness may invite hardships, cautiously take a strong position as needed.
    YOUR_WEAKNESS_MAY_INVITE_HARDSHIPS_CAUTIOUSLY_TAKE_A_STRONG_POSITION_AS_NEEDED(1800522),
    // Text: Be wary of someone who talks and entertains too much. The person may bring you misfortune.
    BE_WARY_OF_SOMEONE_WHO_TALKS_AND_ENTERTAINS_TOO_MUCH(1800523),
    // Text: You may enjoy a beginner's luck.
    YOU_MAY_ENJOY_A_BEGINNERS_LUCK(1800524),
    // Text: Your wealth luck is strong but you should know when to withdraw.
    YOUR_WEALTH_LUCK_IS_STRONG_BUT_YOU_SHOULD_KNOW_WHEN_TO_WITHDRAW(1800525),
    // Text: Already acquired wealth can be lost by greed.
    ALREADY_ACQUIRED_WEALTH_CAN_BE_LOST_BY_GREED(1800526),
    // Text: Even if you can complete it by yourself, it's a good idea to have someone help you.
    EVEN_IF_YOU_CAN_COMPLETE_IT_BY_YOURSELF_ITS_A_GOOD_IDEA_TO_HAVE_SOMEONE_HELP_YOU(1800527),
    // Text: Make harmony with people the priority. Stubbornness may bring hardships.
    MAKE_HARMONY_WITH_PEOPLE_THE_PRIORITY(1800528),
    // Text: There may be a chance when you can see a new aspect of a close friend.
    THERE_MAY_BE_A_CHANCE_WHEN_YOU_CAN_SEE_A_NEW_ASPECT_OF_A_CLOSE_FRIEND(1800529),
    // Text: Try to be close to someone different from you without any stereotypical judgement.
    TRY_TO_BE_CLOSE_TO_SOMEONE_DIFFERENT_FROM_YOU_WITHOUT_ANY_STEREOTYPICAL_JUDGEMENT(1800530),
    // Text: Good luck in becoming a leader with many followers. However, it'll only be after hard work.
    GOOD_LUCK_IN_BECOMING_A_LEADER_WITH_MANY_FOLLOWERS(1800531),
    // Text: Your wealth luck is rising, expenditures will be followed by substantial income as you are able to sustain.
    YOUR_WEALTH_LUCK_IS_RISING_EXPENDITURES_WILL_BE_FOLLOWED_BY_SUBSTANTIAL_INCOME_AS_YOU_ARE_ABLE_TO_SUSTAIN(1800532),
    // Text: Be cautious as your wealth luck can be either very good or very bad.
    BE_CAUTIOUS_AS_YOUR_WEALTH_LUCK_CAN_BE_EITHER_VERY_GOOD_OR_VERY_BAD(1800533),
    // Text: Be warned as a small argument can distance you from a close friend.
    BE_WARNED_AS_A_SMALL_ARGUMENT_CAN_DISTANCE_YOU_FROM_A_CLOSE_FRIEND(1800534),
    // Text: There is luck in love with a new person.
    THERE_IS_LUCK_IN_LOVE_WITH_A_NEW_PERSON(1800535),
    // Text: A bigger fortune will be followed by your good deed.
    A_BIGGER_FORTUNE_WILL_BE_FOLLOWED_BY_YOUR_GOOD_DEED(1800536),
    // Text: There may be a relationship breaking, try to eliminate misunderstandings.
    THERE_MAY_BE_A_RELATIONSHIP_BREAKING_TRY_TO_ELIMINATE_MISUNDERSTANDINGS(1800537),
    // Text: Be cautious not to be emotionally moved even if it's convincing.
    BE_CAUTIOUS_NOT_TO_BE_EMOTIONALLY_MOVED_EVEN_IF_ITS_CONVINCING(1800538),
    // Text: Smiling will bring good luck.
    SMILING_WILL_BRING_GOOD_LUCK(1800539),
    // Text: It's a good idea to let go of a small loss.
    ITS_A_GOOD_IDEA_TO_LET_GO_OF_A_SMALL_LOSS(1800540),
    // Text: Conveying your own truth may be difficult and easy misunderstandings will follow.
    CONVEYING_YOUR_OWN_TRUTH_MAY_BE_DIFFICULT_AND_EASY_MISUNDERSTANDINGS_WILL_FOLLOW(1800541),
    // Text: There is good luck in a place with many people.
    THERE_IS_GOOD_LUCK_IN_A_PLACE_WITH_MANY_PEOPLE(1800542),
    // Text: Try to avoid directness if you can.
    TRY_TO_AVOID_DIRECTNESS_IF_YOU_CAN(1800543),
    // Text: Value substance opposed to the sake honor and look beyond what's in front of you.
    VALUE_SUBSTANCE_OPPOSED_TO_THE_SAKE_HONOR_AND_LOOK_BEYOND_WHATS_IN_FRONT_OF_YOU(1800544),
    // Text: Expanding a relationship with humor may be a good idea.
    EXPANDING_A_RELATIONSHIP_WITH_HUMOR_MAY_BE_A_GOOD_IDEA(1800545),
    // Text: An enjoyable event may be ahead if you accept a simple bet.
    AN_ENJOYABLE_EVENT_MAY_BE_AHEAD_IF_YOU_ACCEPT_A_SIMPLE_BET(1800546),
    // Text: Being level-headed not focusing on emotions may help with relationships.
    BEING_LEVELHEADED_NOT_FOCUSING_ON_EMOTIONS_MAY_HELP_WITH_RELATIONSHIPS(1800547),
    // Text: It's a good idea to take care of matters in sequential order without measuring their importance.
    ITS_A_GOOD_IDEA_TO_TAKE_CARE_OF_MATTERS_IN_SEQUENTIAL_ORDER_WITHOUT_MEASURING_THEIR_IMPORTANCE(1800548),
    // Text: A determined act after prepared research will attract people.
    A_DETERMINED_ACT_AFTER_PREPARED_RESEARCH_WILL_ATTRACT_PEOPLE(1800549),
    // Text: A little humor may bring complete attention to you.
    A_LITTLE_HUMOR_MAY_BRING_COMPLETE_ATTENTION_TO_YOU(1800550),
    // Text: It may not be a good time for an important decision, be wary of temptations and avoid monetary dealings.
    IT_MAY_NOT_BE_A_GOOD_TIME_FOR_AN_IMPORTANT_DECISION_BE_WARY_OF_TEMPTATIONS_AND_AVOID_MONETARY_DEALINGS(1800551),
    // Text: Pay special attention to advice from a close friend.
    PAY_SPECIAL_ATTENTION_TO_ADVICE_FROM_A_CLOSE_FRIEND(1800552),
    // Text: There may be moderate solutions to every problem when they're viewed from a 3rd party's point of view.
    THERE_MAY_BE_MODERATE_SOLUTIONS_TO_EVERY_PROBLEM_WHEN_THEYRE_VIEWED_FROM_A_3RD_PARTYS_POINT_OF_VIEW(1800553),
    // Text: Dealings with close friends only bring frustration and headache, politely decline and mention another chance.
    DEALINGS_WITH_CLOSE_FRIENDS_ONLY_BRING_FRUSTRATION_AND_HEADACHE_POLITELY_DECLINE_AND_MENTION_ANOTHER_CHANCE(1800554),
    // Text: There may be a problem at completion if the basic matters are not considered from the beginning.
    THERE_MAY_BE_A_PROBLEM_AT_COMPLETION_IF_THE_BASIC_MATTERS_ARE_NOT_CONSIDERED_FROM_THE_BEGINNING(1800555),
    // Text: Distinguishing business from a private matter is needed to succeed.
    DISTINGUISHING_BUSINESS_FROM_A_PRIVATE_MATTER_IS_NEEDED_TO_SUCCEED(1800556),
    // Text: A change in rules may be helpful when problems are persistent.
    A_CHANGE_IN_RULES_MAY_BE_HELPFUL_WHEN_PROBLEMS_ARE_PERSISTENT(1800557),
    // Text: Preparing for an unforeseen situation will be difficult when small matters are ignored.
    PREPARING_FOR_AN_UNFORESEEN_SITUATION_WILL_BE_DIFFICULT_WHEN_SMALL_MATTERS_ARE_IGNORED(1800558),
    // Text: Refrain from getting involved in others' business, try to be loose as a goose.
    REFRAIN_FROM_GETTING_INVOLVED_IN_OTHERS_BUSINESS_TRY_TO_BE_LOOSE_AS_A_GOOSE(1800559),
    // Text: Being neutral is a good way to go, but clarity may be helpful contrary to your hesitance.
    BEING_NEUTRAL_IS_A_GOOD_WAY_TO_GO_BUT_CLARITY_MAY_BE_HELPFUL_CONTRARY_TO_YOUR_HESITANCE(1800560),
    // Text: Be cautious of your own actions, the past may bring misunderstandings.
    BE_CAUTIOUS_OF_YOUR_OWN_ACTIONS_THE_PAST_MAY_BRING_MISUNDERSTANDINGS(1800561),
    // Text: Pay attention to time management, emotions may waste your time.
    PAY_ATTENTION_TO_TIME_MANAGEMENT_EMOTIONS_MAY_WASTE_YOUR_TIME(1800562),
    // Text: Heroism will be rewarded, but be careful not to display arrogance or lack of sincerity.
    HEROISM_WILL_BE_REWARDED_BUT_BE_CAREFUL_NOT_TO_DISPLAY_ARROGANCE_OR_LACK_OF_SINCERITY(1800563),
    // Text: If you want to maintain relationship connections, offer reconciliation to those who had misunderstandings with you.
    IF_YOU_WANT_TO_MAINTAIN_RELATIONSHIP_CONNECTIONS_OFFER_RECONCILIATION_TO_THOSE_WHO_HAD_MISUNDERSTANDINGS_WITH_YOU(1800564),
    // Text: Step forward to solve others' problems when they are unable.
    STEP_FORWARD_TO_SOLVE_OTHERS_PROBLEMS_WHEN_THEY_ARE_UNABLE(1800565),
    // Text: There may be a little loss, but think of it as an investment for yourself.
    THERE_MAY_BE_A_LITTLE_LOSS_BUT_THINK_OF_IT_AS_AN_INVESTMENT_FOR_YOURSELF(1800566),
    // Text: Avarice bears a bigger greed, being satisfied with moderation is needed.
    AVARICE_BEARS_A_BIGGER_GREED_BEING_SATISFIED_WITH_MODERATION_IS_NEEDED(1800567),
    // Text: A rational analysis is needed as unplanned actions may bring criticism.
    A_RATIONAL_ANALYSIS_IS_NEEDED_AS_UNPLANNED_ACTIONS_MAY_BRING_CRITICISM(1800568),
    // Text: Reflect upon your shortcomings before criticizing others.
    REFLECT_UPON_YOUR_SHORTCOMINGS_BEFORE_CRITICIZING_OTHERS(1800569),
    // Text: Follow-up care is always needed after an emergency evasion.
    FOLLOWUP_CARE_IS_ALWAYS_NEEDED_AFTER_AN_EMERGENCY_EVASION(1800570),
    // Text: You may look for a new challenge but vast knowledge is required.
    YOU_MAY_LOOK_FOR_A_NEW_CHALLENGE_BUT_VAST_KNOWLEDGE_IS_REQUIRED(1800571),
    // Text: When one puts aside their ego any misunderstanding will be solved.
    WHEN_ONE_PUTS_ASIDE_THEIR_EGO_ANY_MISUNDERSTANDING_WILL_BE_SOLVED(1800572),
    // Text: Listen to the advice that's given to you with a humble attitude.
    LISTEN_TO_THE_ADVICE_THATS_GIVEN_TO_YOU_WITH_A_HUMBLE_ATTITUDE(1800573),
    // Text: Equilibrium is achieved when one understands a downshift is evident after the rise.
    EQUILIBRIUM_IS_ACHIEVED_WHEN_ONE_UNDERSTANDS_A_DOWNSHIFT_IS_EVIDENT_AFTER_THE_RISE(1800574),
    // Text: What you sow is what you reap, faithfully follow the plan.
    WHAT_YOU_SOW_IS_WHAT_YOU_REAP_FAITHFULLY_FOLLOW_THE_PLAN(1800575),
    // Text: Meticulous preparation is needed as spontaneous actions only bear mental and monetary losses.
    METICULOUS_PREPARATION_IS_NEEDED_AS_SPONTANEOUS_ACTIONS_ONLY_BEAR_MENTAL_AND_MONETARY_LOSSES(1800576),
    // Text: The right time to bear fruit is delayed while the farmer ponders opinions.
    THE_RIGHT_TIME_TO_BEAR_FRUIT_IS_DELAYED_WHILE_THE_FARMER_PONDERS_OPINIONS(1800577),
    // Text: Help each other among close friends.
    HELP_EACH_OTHER_AMONG_CLOSE_FRIENDS(1800578),
    // Text: Obsessing over a small profit will place people apart.
    OBSESSING_OVER_A_SMALL_PROFIT_WILL_PLACE_PEOPLE_APART(1800579),
    // Text: Don't cling to the result of a gamble.
    DONT_CLING_TO_THE_RESULT_OF_A_GAMBLE(1800580),
    // Text: Small troubles and arguments are ahead, face them with a mature attitude.
    SMALL_TROUBLES_AND_ARGUMENTS_ARE_AHEAD_FACE_THEM_WITH_A_MATURE_ATTITUDE(1800581),
    // Text: Neglecting a promise may put you in distress.
    NEGLECTING_A_PROMISE_MAY_PUT_YOU_IN_DISTRESS(1800582),
    // Text: Delay any dealings as you may easily omit addressing what's important to you.
    DELAY_ANY_DEALINGS_AS_YOU_MAY_EASILY_OMIT_ADDRESSING_WHATS_IMPORTANT_TO_YOU(1800583),
    // Text: A comparison to others may be helpful.
    A_COMPARISON_TO_OTHERS_MAY_BE_HELPFUL(1800584),
    // Text: What you've endured will return as a benefit.
    WHAT_YOUVE_ENDURED_WILL_RETURN_AS_A_BENEFIT(1800585),
    // Text: Try to be courteous to the opposite sex and follow a virtuous path.
    TRY_TO_BE_COURTEOUS_TO_THE_OPPOSITE_SEX_AND_FOLLOW_A_VIRTUOUS_PATH(1800586),
    // Text: Joy may come from small things.
    JOY_MAY_COME_FROM_SMALL_THINGS(1800587),
    // Text: Be confident in your actions as good luck shadows the result.
    BE_CONFIDENT_IN_YOUR_ACTIONS_AS_GOOD_LUCK_SHADOWS_THE_RESULT(1800588),
    // Text: Be confident without hesitation when your honesty is above reproach in dealings.
    BE_CONFIDENT_WITHOUT_HESITATION_WHEN_YOUR_HONESTY_IS_ABOVE_REPROACH_IN_DEALINGS(1800589),
    // Text: A matter related to a close friend can isolate you, keep staying on the right path.
    A_MATTER_RELATED_TO_A_CLOSE_FRIEND_CAN_ISOLATE_YOU_KEEP_STAYING_ON_THE_RIGHT_PATH(1800590),
    // Text: Too much focus on the result may bring continuous misfortune.
    TOO_MUCH_FOCUS_ON_THE_RESULT_MAY_BRING_CONTINUOUS_MISFORTUNE(1800591),
    // Text: Be tenacious until the finish as halfway abandonment causes a troubled ending.
    BE_TENACIOUS_UNTIL_THE_FINISH_AS_HALFWAY_ABANDONMENT_CAUSES_A_TROUBLED_ENDING(1800592),
    // Text: There will be no advantage in a group deal.
    THERE_WILL_BE_NO_ADVANTAGE_IN_A_GROUP_DEAL(1800593),
    // Text: Refrain from stepping-up but take a moment to ponder to be flexible with situations.
    REFRAIN_FROM_STEPPINGUP_BUT_TAKE_A_MOMENT_TO_PONDER_TO_BE_FLEXIBLE_WITH_SITUATIONS(1800594),
    // Text: There will be a small opportunity when information is best utilized.
    THERE_WILL_BE_A_SMALL_OPPORTUNITY_WHEN_INFORMATION_IS_BEST_UTILIZED(1800595),
    // Text: Belongings are at loose ends, keep track of the things you value.
    BELONGINGS_ARE_AT_LOOSE_ENDS_KEEP_TRACK_OF_THE_THINGS_YOU_VALUE(1800596),
    // Text: What you sow is what you reap, try your best.
    WHAT_YOU_SOW_IS_WHAT_YOU_REAP_TRY_YOUR_BEST(1800597),
    // Text: With the beginner's attitude, shortcomings can be easily mended.
    WITH_THE_BEGINNERS_ATTITUDE_SHORTCOMINGS_CAN_BE_EASILY_MENDED(1800598),
    // Text: When facing difficulties, seek a totally different direction.
    WHEN_FACING_DIFFICULTIES_SEEK_A_TOTALLY_DIFFERENT_DIRECTION(1800599),
    // Text: Lifetime savings can disappear with one-time greed.
    LIFETIME_SAVINGS_CAN_DISAPPEAR_WITH_ONETIME_GREED(1800600),
    // Text: With your heart avoid extremes and peace will stay.
    WITH_YOUR_HEART_AVOID_EXTREMES_AND_PEACE_WILL_STAY(1800601),
    // Text: Be cautious as instant recklessness may bring malicious gossip.
    BE_CAUTIOUS_AS_INSTANT_RECKLESSNESS_MAY_BRING_MALICIOUS_GOSSIP(1800602),
    // Text: Be tenacious to the end because a strong luck with winning is ahead.
    BE_TENACIOUS_TO_THE_END_BECAUSE_A_STRONG_LUCK_WITH_WINNING_IS_AHEAD(1800603),
    // Text: Be kind to and care for those close to you, they may help in the future.
    BE_KIND_TO_AND_CARE_FOR_THOSE_CLOSE_TO_YOU_THEY_MAY_HELP_IN_THE_FUTURE(1800604),
    // Text: Positivity may bring good results.
    POSITIVITY_MAY_BRING_GOOD_RESULTS(1800605),
    // Text: Be gracious to cover a close friend's fault.
    BE_GRACIOUS_TO_COVER_A_CLOSE_FRIENDS_FAULT(1800606),
    // Text: Be prepared for an expected cost.
    BE_PREPARED_FOR_AN_EXPECTED_COST(1800607),
    // Text: Be considerate to others and avoid focusing only on winning or a wound will be left untreated.
    BE_CONSIDERATE_TO_OTHERS_AND_AVOID_FOCUSING_ONLY_ON_WINNING_OR_A_WOUND_WILL_BE_LEFT_UNTREATED(1800608),
    // Text: An accessory or decoration may bring a good luck.
    AN_ACCESSORY_OR_DECORATION_MAY_BRING_A_GOOD_LUCK(1800609),
    // Text: Only reflection and humility may bring success.
    ONLY_REFLECTION_AND_HUMILITY_MAY_BRING_SUCCESS(1800610),
    // Text: A small misunderstanding may cause quarrels.
    A_SMALL_MISUNDERSTANDING_MAY_CAUSE_QUARRELS(1800611),
    // Text: Avoid advancing beyond your ability and focus on the flowing stream.
    AVOID_ADVANCING_BEYOND_YOUR_ABILITY_AND_FOCUS_ON_THE_FLOWING_STREAM(1800612),
    // Text: Considering others with a good heart before self-interest will bring a triumph.
    CONSIDERING_OTHERS_WITH_A_GOOD_HEART_BEFORE_SELFINTEREST_WILL_BRING_A_TRIUMPH(1800613),
    // Text: Visiting a place you've never been before may bring luck.
    VISITING_A_PLACE_YOUVE_NEVER_BEEN_BEFORE_MAY_BRING_LUCK(1800614),
    // Text: A good thing may happen in a place with a few people.
    A_GOOD_THING_MAY_HAPPEN_IN_A_PLACE_WITH_A_FEW_PEOPLE(1800615),
    // Text: Being high-strung can cause loss of trust from others because it can be viewed as light-hearted, act sincerely but yet do not lack humor.
    BEING_HIGHSTRUNG_CAN_CAUSE_LOSS_OF_TRUST_FROM_OTHERS_BECAUSE_IT_CAN_BE_VIEWED_AS_LIGHTHEARTED_ACT_SINCERELY_BUT_YET_DO_NOT_LACK_HUMOR(1800616),
    // Text: Perfection at the finish can cover faulty work in the process.
    PERFECTION_AT_THE_FINISH_CAN_COVER_FAULTY_WORK_IN_THE_PROCESS(1800617),
    // Text: Abstain from laziness, much work brings many gains and satisfactory rewards.
    ABSTAIN_FROM_LAZINESS_MUCH_WORK_BRINGS_MANY_GAINS_AND_SATISFACTORY_REWARDS(1800618),
    // Text: Staying busy rather than being stationary will help.
    STAYING_BUSY_RATHER_THAN_BEING_STATIONARY_WILL_HELP(1800619),
    // Text: Handling the work by yourself may lead you into temptation.
    HANDLING_THE_WORK_BY_YOURSELF_MAY_LEAD_YOU_INTO_TEMPTATION(1800620),
    // Text: Pay attention to any small advice without being indifferent.
    PAY_ATTENTION_TO_ANY_SMALL_ADVICE_WITHOUT_BEING_INDIFFERENT(1800621),
    // Text: Small things make up big things so even value trivial matters.
    SMALL_THINGS_MAKE_UP_BIG_THINGS_SO_EVEN_VALUE_TRIVIAL_MATTERS(1800622),
    // Text: Action toward the result rather than waiting for the right circumstances may lead you to a fast success.
    ACTION_TOWARD_THE_RESULT_RATHER_THAN_WAITING_FOR_THE_RIGHT_CIRCUMSTANCES_MAY_LEAD_YOU_TO_A_FAST_SUCCESS(1800623),
    // Text: Don't try to save small expenditures, it will lead to future returns.
    DONT_TRY_TO_SAVE_SMALL_EXPENDITURES_IT_WILL_LEAD_TO_FUTURE_RETURNS(1800624),
    // Text: Be cautious to control emotions as temptations are nearby.
    BE_CAUTIOUS_TO_CONTROL_EMOTIONS_AS_TEMPTATIONS_ARE_NEARBY(1800625),
    // Text: Be warned as neglecting a matter because it's small can cause you trouble.
    BE_WARNED_AS_NEGLECTING_A_MATTER_BECAUSE_ITS_SMALL_CAN_CAUSE_YOU_TROUBLE(1800626),
    // Text: Spend when needed rather than trying to unconditionally save.
    SPEND_WHEN_NEEDED_RATHER_THAN_TRYING_TO_UNCONDITIONALLY_SAVE(1800627),
    // Text: Prejudice will take you to a small gain with a big loss.
    PREJUDICE_WILL_TAKE_YOU_TO_A_SMALL_GAIN_WITH_A_BIG_LOSS(1800628),
    // Text: Sweet food may bring good luck.
    SWEET_FOOD_MAY_BRING_GOOD_LUCK(1800629),
    // Text: You may be paid for what you're owed or for your past loss.
    YOU_MAY_BE_PAID_FOR_WHAT_YOURE_OWED_OR_FOR_YOUR_PAST_LOSS(1800630),
    // Text: There may be conflict in basic matters.
    THERE_MAY_BE_CONFLICT_IN_BASIC_MATTERS(1800631),
    // Text: Be observant to close friends' small behaviors while refraining from excessive kindness.
    BE_OBSERVANT_TO_CLOSE_FRIENDS_SMALL_BEHAVIORS_WHILE_REFRAINING_FROM_EXCESSIVE_KINDNESS(1800632),
    // Text: Do not show your distress nor lose your smile.
    DO_NOT_SHOW_YOUR_DISTRESS_NOR_LOSE_YOUR_SMILE(1800633),
    // Text: Showing change may be of help.
    SHOWING_CHANGE_MAY_BE_OF_HELP(1800634),
    // Text: The intended result may be on your way if the time is perfectly managed.
    THE_INTENDED_RESULT_MAY_BE_ON_YOUR_WAY_IF_THE_TIME_IS_PERFECTLY_MANAGED(1800635),
    // Text: Hardships may arise if flexibility is not well played.
    HARDSHIPS_MAY_ARISE_IF_FLEXIBILITY_IS_NOT_WELL_PLAYED(1800636),
    // Text: Keep cool headed because carelessness or inattentiveness may cause misfortune.
    KEEP_COOL_HEADED_BECAUSE_CARELESSNESS_OR_INATTENTIVENESS_MAY_CAUSE_MISFORTUNE(1800637),
    // Text: Be cautious as you may get hurt after last night's sinister dream.
    BE_CAUTIOUS_AS_YOU_MAY_GET_HURT_AFTER_LAST_NIGHTS_SINISTER_DREAM(1800638),
    // Text: A strong wealth luck is ahead but be careful with emotions that may bring losses.
    A_STRONG_WEALTH_LUCK_IS_AHEAD_BUT_BE_CAREFUL_WITH_EMOTIONS_THAT_MAY_BRING_LOSSES(1800639),
    // Text: Proceed as you wish when it's pertinent to the person you like.
    PROCEED_AS_YOU_WISH_WHEN_ITS_PERTINENT_TO_THE_PERSON_YOU_LIKE(1800640),
    // Text: You may deepen the relationship with the opposite sex through conversation.
    YOU_MAY_DEEPEN_THE_RELATIONSHIP_WITH_THE_OPPOSITE_SEX_THROUGH_CONVERSATION(1800641),
    // Text: Investment into solid material may bring profit.
    INVESTMENT_INTO_SOLID_MATERIAL_MAY_BRING_PROFIT(1800642),
    // Text: Investment into what you enjoy may be of help.
    INVESTMENT_INTO_WHAT_YOU_ENJOY_MAY_BE_OF_HELP(1800643),
    // Text: Being busy may help catching up with many changes.
    BEING_BUSY_MAY_HELP_CATCHING_UP_WITH_MANY_CHANGES(1800644),
    // Text: Choose substance over honor.
    CHOOSE_SUBSTANCE_OVER_HONOR(1800645),
    // Text: Remember to decline any financial dealings because a good deed may return as resentment.
    REMEMBER_TO_DECLINE_ANY_FINANCIAL_DEALINGS_BECAUSE_A_GOOD_DEED_MAY_RETURN_AS_RESENTMENT(1800646),
    // Text: Be careful not to make a mistake with a new person.
    BE_CAREFUL_NOT_TO_MAKE_A_MISTAKE_WITH_A_NEW_PERSON(1800647),
    // Text: Do not be obsessive over a dragged out project since it won't get any better with more time.
    DO_NOT_BE_OBSESSIVE_OVER_A_DRAGGED_OUT_PROJECT_SINCE_IT_WONT_GET_ANY_BETTER_WITH_MORE_TIME(1800648),
    // Text: Do not yield what's rightfully yours or tolerate losses.
    DO_NOT_YIELD_WHATS_RIGHTFULLY_YOURS_OR_TOLERATE_LOSSES(1800649),
    // Text: There's luck in relationships so become interested in the opposite sex.
    THERES_LUCK_IN_RELATIONSHIPS_SO_BECOME_INTERESTED_IN_THE_OPPOSITE_SEX(1800650),
    // Text: Seeking others' help rather than trying by yourself may result in two birds with one stone.
    SEEKING_OTHERS_HELP_RATHER_THAN_TRYING_BY_YOURSELF_MAY_RESULT_IN_TWO_BIRDS_WITH_ONE_STONE(1800651),
    // Text: Persuading the other may result in your gain.
    PERSUADING_THE_OTHER_MAY_RESULT_IN_YOUR_GAIN(1800652),
    // Text: A good opportunity may come when keeping patience without excessiveness.
    A_GOOD_OPPORTUNITY_MAY_COME_WHEN_KEEPING_PATIENCE_WITHOUT_EXCESSIVENESS(1800653),
    // Text: The opposite sex may bring fortune.
    THE_OPPOSITE_SEX_MAY_BRING_FORTUNE(1800654),
    // Text: Doing favor for other people may bring fortune in the future.
    DOING_FAVOR_FOR_OTHER_PEOPLE_MAY_BRING_FORTUNE_IN_THE_FUTURE(1800655),
    // Text: Luck may stay near if a smile is kept during difficult times.
    LUCK_MAY_STAY_NEAR_IF_A_SMILE_IS_KEPT_DURING_DIFFICULT_TIMES(1800656),
    // Text: You may reveal your true self like iron is molten into an strong sword.
    YOU_MAY_REVEAL_YOUR_TRUE_SELF_LIKE_IRON_IS_MOLTEN_INTO_AN_STRONG_SWORD(1800657),
    // Text: Your value will shine as your potential is finally realized.
    YOUR_VALUE_WILL_SHINE_AS_YOUR_POTENTIAL_IS_FINALLY_REALIZED(1800658),
    // Text: Tenacious efforts in solving a difficult mission or hardship may bring good results as well as realizing your hidden potential.
    TENACIOUS_EFFORTS_IN_SOLVING_A_DIFFICULT_MISSION_OR_HARDSHIP_MAY_BRING_GOOD_RESULTS_AS_WELL_AS_REALIZING_YOUR_HIDDEN_POTENTIAL(1800659),
    // Text: People will appreciate your positivity and joyful entertaining.
    PEOPLE_WILL_APPRECIATE_YOUR_POSITIVITY_AND_JOYFUL_ENTERTAINING(1800660),
    // Text: Things will move smoothly with your full wisdom and abilities.
    THINGS_WILL_MOVE_SMOOTHLY_WITH_YOUR_FULL_WISDOM_AND_ABILITIES(1800661),
    // Text: You may meet a sage who can help you find the right path.
    YOU_MAY_MEET_A_SAGE_WHO_CAN_HELP_YOU_FIND_THE_RIGHT_PATH(1800662),
    // Text: Keen instinct and foresight will shine their values.
    KEEN_INSTINCT_AND_FORESIGHT_WILL_SHINE_THEIR_VALUES(1800663),
    // Text: You may bring good luck to those around you.
    YOU_MAY_BRING_GOOD_LUCK_TO_THOSE_AROUND_YOU(1800664),
    // Text: Your goal may be realized when emotional details are well defined.
    YOUR_GOAL_MAY_BE_REALIZED_WHEN_EMOTIONAL_DETAILS_ARE_WELL_DEFINED(1800665),
    // Text: You may enjoy affluence after meeting a precious person.
    YOU_MAY_ENJOY_AFFLUENCE_AFTER_MEETING_A_PRECIOUS_PERSON(1800666),
    // Text: You may meet the opposite sex who has materialistic attractions.
    YOU_MAY_MEET_THE_OPPOSITE_SEX_WHO_HAS_MATERIALISTIC_ATTRACTIONS(1800667),
    // Text: A big success will follow all possible efforts in competition.
    A_BIG_SUCCESS_WILL_FOLLOW_ALL_POSSIBLE_EFFORTS_IN_COMPETITION(1800668),
    // Text: A consequence from past actions will be on display.
    A_CONSEQUENCE_FROM_PAST_ACTIONS_WILL_BE_ON_DISPLAY(1800669),
    // Text: Whatever happened to you and the other person will replay, but this time, the opposite will be the result.
    WHATEVER_HAPPENED_TO_YOU_AND_THE_OTHER_PERSON_WILL_REPLAY_BUT_THIS_TIME_THE_OPPOSITE_WILL_BE_THE_RESULT(1800670),
    // Text: You may need to sacrifice for a higher cause.
    YOU_MAY_NEED_TO_SACRIFICE_FOR_A_HIGHER_CAUSE(1800671),
    // Text: You may lose an item but will gain honor.
    YOU_MAY_LOSE_AN_ITEM_BUT_WILL_GAIN_HONOR(1800672),
    // Text: A new trial or start may be successful as luck shadows changes.
    A_NEW_TRIAL_OR_START_MAY_BE_SUCCESSFUL_AS_LUCK_SHADOWS_CHANGES(1800673),
    // Text: Be sophisticated without showing your true emotions as tricks and materialistic temptations lie ahead.
    BE_SOPHISTICATED_WITHOUT_SHOWING_YOUR_TRUE_EMOTIONS_AS_TRICKS_AND_MATERIALISTIC_TEMPTATIONS_LIE_AHEAD(1800674),
    // Text: Do not attempt a dangerous adventure.
    DO_NOT_ATTEMPT_A_DANGEROUS_ADVENTURE(1800675),
    // Text: Do not be afraid of change. A risk will be another opportunity.
    DO_NOT_BE_AFRAID_OF_CHANGE(1800676),
    // Text: Be confident and act tenaciously at all times. You may be able to accomplish to perfection during somewhat unstable situations.
    BE_CONFIDENT_AND_ACT_TENACIOUSLY_AT_ALL_TIMES(1800677),
    // Text: You may expect a bright and hopeful future.
    YOU_MAY_EXPECT_A_BRIGHT_AND_HOPEFUL_FUTURE(1800678),
    // Text: A rest will promise a bigger development.
    A_REST_WILL_PROMISE_A_BIGGER_DEVELOPMENT(1800679),
    // Text: Fully utilize positive views.
    FULLY_UTILIZE_POSITIVE_VIEWS(1800680),
    // Text: Positive thinking and energetic actions will take you to the center of the glorious stage.
    POSITIVE_THINKING_AND_ENERGETIC_ACTIONS_WILL_TAKE_YOU_TO_THE_CENTER_OF_THE_GLORIOUS_STAGE(1800681),
    // Text: Your self confidence and intuition may solve the difficulties.
    YOUR_SELF_CONFIDENCE_AND_INTUITION_MAY_SOLVE_THE_DIFFICULTIES(1800682),
    // Text: Everything is brilliant and joyful, share it with others. A bigger fortune will follow.
    EVERYTHING_IS_BRILLIANT_AND_JOYFUL_SHARE_IT_WITH_OTHERS(1800683),
    // Text: A fair assessment and reward for past actions lie ahead.
    A_FAIR_ASSESSMENT_AND_REWARD_FOR_PAST_ACTIONS_LIE_AHEAD(1800684),
    // Text: Pay accurately the old liability or debt, if applicable. A new joy lies ahead.
    PAY_ACCURATELY_THE_OLD_LIABILITY_OR_DEBT_IF_APPLICABLE(1800685),
    // Text: An excessive humility can harm you back.
    AN_EXCESSIVE_HUMILITY_CAN_HARM_YOU_BACK(1800686),
    // Text: A reward for the past work will come through.
    A_REWARD_FOR_THE_PAST_WORK_WILL_COME_THROUGH(1800687),
    // Text: Your past fruitless effort will finally be rewarded with something unexpected.
    YOUR_PAST_FRUITLESS_EFFORT_WILL_FINALLY_BE_REWARDED_WITH_SOMETHING_UNEXPECTED(1800688),
    // Text: There's strong luck in a revival, abandon the old and create the new.
    THERES_STRONG_LUCK_IN_A_REVIVAL_ABANDON_THE_OLD_AND_CREATE_THE_NEW(1800689),
    // Text: You may gain materialistic or mental aid from close friends.
    YOU_MAY_GAIN_MATERIALISTIC_OR_MENTAL_AID_FROM_CLOSE_FRIENDS(1800690),
    // Text: A good beginning is awaiting you.
    A_GOOD_BEGINNING_IS_AWAITING_YOU(1800691),
    // Text: You may meet the person you've longed to see.
    YOU_MAY_MEET_THE_PERSON_YOUVE_LONGED_TO_SEE(1800692),
    // Text: You may sustain a loss due to your kindness.
    YOU_MAY_SUSTAIN_A_LOSS_DUE_TO_YOUR_KINDNESS(1800693),
    // Text: Closely observe people who pass by since you may meet a precious person who can help you.
    CLOSELY_OBSERVE_PEOPLE_WHO_PASS_BY_SINCE_YOU_MAY_MEET_A_PRECIOUS_PERSON_WHO_CAN_HELP_YOU(1800694),
    //Text: Well done. %s. Your help is much appreciated.
    WELL_DONE_S1_YOUR_HELP_IS_MUCH_APPRECIATED(1800847),
    // Text: All will pay a severe price to me and these here.
    ALL_WILL_PAY_A_SEVERE_PRICE_TO_ME_AND_THESE_HERE(1800849),
    // Text: All is vanity! but this cannot be the end!
    ALL_IS_VANITY_BUT_THIS_CANNOT_BE_THE_END(1800862),
    // Text: DIE!!!!
    THOSE_WHO_ARE_IN_FRONT_OF_MY_EYES_WILL_BE_DESTROYED(1800863),
    // Text: I want to sleep. Do not bother me!
    I_AM_TIRED_DO_NOT_WAKE_ME_UP_AGAIN(1800864),
    // Text: When inventory weight/number are more than 80%, the Life Stone from the Beginning cannot be acquired.
    WHEN_INVENTORY_WEIGHTNUMBER_ARE_MORE_THAN_80_THE_LIFE_STONE_FROM_THE_BEGINNING_CANNOT_BE_ACQUIRED(1800879),
    // Text: Life Stone from the Beginning acquired
    LIFE_STONE_FROM_THE_BEGINNING_ACQUIRED(1800878),
    // Text: Amazing. $s1 took 100 of these soul stone fragments. What a complete swindler.
    AMAZING_S1_TOOL_100_OF_THESE_SOUL_STONE_FRAGMENT_WHAT_A_COMPLETE_SWINDLER(1801155),
    // Text: Hmm? Hey, did you give $s1 something? But it was just 1. Haha.
    HMM_HEY_DID_YOU_GIVE_S1_SOMETHING_BUT_IT_WAS_JUST_1_HAHA(1801156),
    // Text: $s1 pulled one with $s2 digits. Lucky~ Not bad~
    S1_PULLED_ONE_WITH_S2_DIGITS_LUCKY_NOT_BAD(1801157),
    // Text: It's better than losing it all, right? Or does this feel worse?
    IT_S_BETTER_THAN_LOSING_IT_ALL_RIGHT_OR_DOES_THIS_FEEL_WORSE(1801158),
    // Text: Ahem~!  $s1 has no luck at all. Try praying.
    AHEM_S1_HAS_NO_LUCK_AT_ALL_TRY_PRAYING(1801159),
    // Text: Ah... It's over. What kind of guy is that? Damn... Fine, you $s1, take it and get outta here.
    AH_ITS_OVER_WHAT_KIND_OF_GUY_IT_THAT_DAMN_FINE_YOU_S1_TAKE_IT_AND_GET_OUTTA_HERE(1801160),
    // Text: A big piece is made up of little pieces. So here's a little piece~
    A_BIG_PIECE_IS_MADE_UP_OF_LITTLE_PIECES_SO_HERE_S_A_LITTLE_PIECE(1801161),
    // Text: You don't feel bad, right? Are you sad? But don't cry~
    YOU_DON_T_FEEL_BAD_RIGHT_ARE_YOU_SAD_BUT_DON_T_CRY(1801162),
    // Text: OK~ Who's next? It all depends on your fate and luck, right? At least come and take a look.
    OK_WHO_S_NEXT_IT_ALL_DEPENDS_ON_YOUR_FATE_AND_LUCK_RIGHT_AT_LEAST_COME_AND_TAKE_A_LOOK(1801163),
    // Text: No one else? Don't worry~ I don't bite. Haha~!
    NO_ONE_ELSE_DONT_WORRY_I_DON_T_BITE_HAHA(1801164),
    // Text: There was someone who won 10,000 from me. A warrior shouldn't just be good at fighting, right? You've gotta be good in everything.
    THERE_WAS_SOMEONE_WHO_WON_10000_FROM_ME_A_WARRIOR_SHOULDN_T_JUST_BE_GOOD_AT_FIGHTING_RIGHT_YOU_VE_GOTTA_BE_GOOD_IN_EVERYTHING(1801165),
    // Text: OK~ Master of luck? That's you? Haha~! Well, anyone can come after all.
    OK_MASTER_OF_LUCK_THAT_S_YOU_HAHA_WELL_ANYONE_CAN_COME_AFTER_ALL(1801166),
    // Text: Shedding blood is a given on the battlefield. At least it's safe here.
    SHEDDING_BLOOD_IS_A_GIVEN_ON_THE_BATTLEFIELD_AT_LEAST_IT_S_SAFE_HERE(1801167),
    // Text: Ho, ho, ho! Happy holidays! ]
    HO_HO_HO_HAPPY_HOLIDAYS(1900016),
    //Text: Not even the gods themselves could touch me. But you, %s, you dare challenge me?! Ignorant mortal!
    NOT_EVEN_THE_GODS_THEMSELVES_COULD_TOUCH_ME_BUT_YOU_S_YOU_DARE_CHALLENGE_ME_IGNORANT_MORTAL(1000521),
    //Text: How dare you wake me!  Now you shall die!
    HOW_DARE_YOU_WAKE_ME_NOW_YOU_SHALL_DIE(22937),
    // Text: 1900134	[I must raise the reindeer quickly. This year's Christmas gifts have to be delivered...]
    I_MUST_RAISE_THE_REINDEER_QUICKLY_THIS_YEARS_CHRISTMAS_GIFTS_HAVE_TO_BE_BELIVERED(1900134),
    // Text: Ugh, I have butterflies in my stomach.. The show starts soon...
    UGH_I_HAVE_BUTTERFILIES_IN_MY_STOMATCH_THE_SHOW_STARTS_SOON(1800082),
    // Text: Thank you all for coming here tonight.
    THANK_YOU_ALL_FOR_COMING_HERE_TONIGHT(1800083),
    // Text: It is an honor to have the special show today.
    IT_IS_AN_HONOR_TO_HAVE_THE_SPECIAL_SHOW_TODAY(1800084),
    // Text: Fantasy Isle is fully committed to your happiness.
    FANTASY_ISLE_IS_FULLY_COMITTED_TO_YOUR_HAPPINESS(1800085),
    // Text: Now I'd like to introduce the most beautiful singer in Aden. Please welcome...Leyla Mira!
    NOW_ID_LIKE_TO_INTRODUCE_THE_MOST_BEAUTIFUL_SINGER_IN_ADEN_PLEASE_WELCOME_LEYLA_MIRA(1800086),
    // Text: Here she comes!
    HERE_SHE_COMES(1800087),
    // Text: Thank you very much, Leyla!
    THANK_YOU_VERY_MUCH_LEYLA(1800088),
    // Text: Now we're in for a real treat.
    NOW_WERE_IN_FOR_A_REAL_TREAT(1800089),
    // Text: Just back from their world tourвЂ¦put your hands together for the Fantasy Isle Circus!
    JUST_BACK_FROM_THEIR_WORLD_TOUR_PUT_YOUR_HANDS_TOGETHER_FOR_THE_FANTAST_ISLE_CIRCUS(1800090),
    // Text: Come on ~ everyone
    COME_ON_EVERYONE(1800091),
    // Text: Did you like it? That was so amazing.
    DID_YOU_lIKE_IT_THAT_WAS_SO_AMAZING(1800092),
    // Text: Now we also invited individuals with special talents.
    NOW_WE_ALSO_INVITED_INDIVIDUALS_WITH_SPECIAL_TALENTS(1800093),
    // Text: Let's welcome the first person here!
    LETS_WELCOME_THE_FIRST_PERSON_HERE(1800094),
    // Text: ;;;;;;Oh
    OH(1800095),
    // Text: Okay, now here comes the next person. Come on up please.
    OKAY_NOW_HERE_CONES_THE_NEXT_PERSON_COME_ON_UP_PLEASE(1800096),
    // Text: Oh, it looks like something great is going to happen, right?
    OH_IT_LOOKS_LIKE_SOMETHING_GREAT_IS_GOING_TO_HAPPEN_RIGHT(1800097),
    // Text: Oh, my
    OH_MY(1800098),
    // Text: That's g- .. great. Now, here comes the last person.
    THATS_GREAT_NOW_HERE_CONES_THE_LAST_PERSON(1800099),
    // Text: Now this is the end of today's show.
    NOW_THIS_IS_THE_END_OF_TODAY_SHOW(1800100),
    // Text: How was it? I hope you all enjoyed it.
    HOW_WAS_IT_I_HOPE_YOU_ALL_ENJOYED_IT(1800101),
    // Text: Please remember that Fantasy Isle is always planning a lot of great shows for you.
    PLEASE_REMEMBER_THAT_FANTASY_ISLE_IS_ALWAYS_PLANNING_A_LOT_OF_GREAT_SHOWS_FOR_YOU(1800102),
    // Text: Well, I wish I could continue all night long, but this is it for today. Thank you.
    WELL_I_WISH_I_COULD_CONTINUE_ALL_NIGHT_LONG_BUT_THIS_IS_IT_FOR_TODAY_THANK_YOU(1800103),
    // Text: We love you.
    WE_LOVE_YOU(1800104),
    // Text: How come people are not here... We are about to start the show.. Hmm
    HOW_COME_PEOPLE_ARE_NOT_HERE_WE_ARE_ABOUT_TO_START_THE_SHOW_HMM(1800105),
    // Text: Who's there? If you disturb the temper of the great Land Dragon Antharas, I will never forgive you!
    WHO_S_THERE_IF_YOU_DISTURB_THE_TEMPER_OF_THE_GREAT_LAND_DRAGON_ANTHARAS_I_WILL_NEVER_FORGIVE_YOU(1811137),
    //Text: Although we currently have control of it, the enemy is pushing back with a powerful attack.
    ALTHOUGH_WE_CURRENTLY_HAVE_CONTROL_OF_IT_THE_ENEMY_IS_PUSHING_BACK_WITH_A_POWERFUL_ATTACK(1800710),
    //Text: It's under occupation by our forces, and I heard that Kucereus' clan is organizing the remnants.
    ITS_UNDER_OCCUPATION_BY_OUR_FORCES_AND_I_HEARD_THAT_KUCEREUS_CLAN_IS_ORGANIZING_THE_REMNANTS(1800709),
    //Text: It's currently occupied by the enemy and our troops are attacking.
    ITS_CURRENTLY_OCCUPIED_BY_THE_ENEMY_AND_OUR_TROOPS_ARE_ATTACKING(1800708),
    //Text: It's under the enemy's occupation, and the military forces of adventurers and clan members are unleashing an onslaught upon the Hall of Suffering and the Hall of Erosion.
    ITS_UNDER_THE_ENEMYS_OCCUPATION_AND_THE_MILITARY_FORCES_OF_ADVENTURERS_AND_CLAN_MEMBERS_ARE_UNLEASHING_AN_ONSLAUGHT_UPON_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION(1800711),
    //Text: It's under enemy occupation, but the situation is currently favorable, and an infiltration route to the Heart has been secured. All that is left is the final battle with Ekimus and the clean-up of his followers hiding in the Hall of Suffering!
    ITS_UNDER_ENEMY_OCCUPATION_BUT_THE_SITUATION_IS_CURRENTLY_FAVORABLE_AND_AN_INFILTRATION_ROUTE_TO_THE_HEART_HAS_BEEN_SECURED_ALL_THAT_IS_LEFT_IS_THE_FINAL_BATTLE_WITH_EKIMUS_AND_THE_CLEANUP_OF_HIS_FOLLOWERS_HIDING_IN_THE_HALL_OF_SUFFERING(1800712),
    //Text: Our forces have occupied it and are currently investigating the depths.
    OUR_FORCES_HAVE_OCCUPIED_IT_AND_ARE_CURRENTLY_INVESTIGATING_THE_DEPTHS(1800713),
    //Text: It's under occupation by our forces, but the enemy has resurrected and is attacking toward the Hall of Suffering and the Hall of Erosion.
    ITS_UNDER_OCCUPATION_BY_OUR_FORCES_BUT_THE_ENEMY_HAS_RESURRECTED_AND_IS_ATTACKING_TOWARD_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION(1800714),
    //Text: It's under occupation by our forces, but the enemy has already overtaken the Hall of Erosion and is driving out our forces from the Hall of Suffering toward the Heart. It seems that Ekimus will revive shortly.
    ITS_UNDER_OCCUPATION_BY_OUR_FORCES_BUT_THE_ENEMY_HAS_ALREADY_OVERTAKEN_THE_HALL_OF_EROSION_AND_IS_DRIVING_OUT_OUR_FORCES_FROM_THE_HALL_OF_SUFFERING_TOWARD_THE_HEART_IT_SEEMS_THAT_EKIMUS_WILL_REVIVE_SHORTLY(1800715),
    // Text: How could I lose against these worthless creatures...?
    HOW_COULD_I_LOSE_AGAINST_THESE_WORTHLESS_CREATURES(1911113),
    // Text: Foolish creatures... the flames of hell are drawing closer.
    FOOLISH_CREATURES_THE_FLAMES_OF_HELL_ARE_DRAWING_CLOSER(1911114),
    // Text: No matter how you struggle this place will soon be covered with your blood.
    NO_MATTER_HOW_YOU_STRUGGLE_THIS_PLACE_WILL_SOON_BE_COVERED_WITH_YOUR_BLOOD(1911115),
    // Text: Those who set foot in this place shall not leave alive.
    THOSE_WHO_SET_FOOT_IN_THIS_PLACE_SHALL_NOT_LEAVE_ALIVE(1911116),
    // Text: Worthless creatures, I will grant you eternal sleep in fire and brimstone.
    WORTHLESS_CREATURES_I_WILL_GRANT_YOU_ETERNAL_SLEEP_IN_FIRE_AND_BRIMSTONE(1911117),
    // Text: If you wish to see hell, I will grant you your wish.
    IF_YOU_WISH_TO_SEE_HELL_I_WILL_GRANT_YOU_WISH(1911118),
    // Text: You will soon become the sacrifice for us, those full of deceit and sin whom you despise!
    YOU_WILL_SOON_BECOME_THE_SACRIFICE_FOR_US_THOSE_FULL_OF_DECEIT_AND_SIN_WHOM_YOU_DESPISE(1911111),
    // Text: My brethren who are stronger than me will punish you. You will soon be covered in your own blood and crying in anguish!
    MY_BRETHREN_WHO_ARE_STRONGER_THAT_ME_WILL_PUNISH_YOU_YOU_WILL_SOON_BE_COVERED_IN_YOUR_OWN_BLOOD_AND_CRYING_IN_ANGUISH(1911112),
    // Text: Killed by $s1
    KILLED_BY_S1(1120301),
    // Text: Keu... I will leave for now... But don't think this is over... The Seed of Infinity can never die...
    KEU_I_WILL_LEAVE_FOR_NOW_BUT_DONT_THINK_THIS_IS_OVER_THE_SEED_OF_INFINITY_CAN_NEVER_DIE(1800236),
    // Text: Huh? The sky looks funny. What's that?
    HUH_THE_SKY_LOOKS_FUNNY_WHATS_THAT(1800225),
    // Text: Obstruction Wand bewitched ! This is
    OBSTRUCTION_WAND_BEWITCHED_THIS_IS(1800226),
    // Text: Be careful...! Something's coming...!
    BE_CAREFUL_SOMETHINGS_COMING(1800227),
    // Text: The resupply time of %s hour(s) %s minute(s) %s second(s) remain for the Gift of Energy.
    THE_RESUPPLY_TIME_OF_S1_HOURS_S2_MINUTES_S3_SECONDS_REMAIN_FOR_THE_GIFT_OF_ENERGY(1900138),
    // Text: How dare you awaken me. Feel the pain of the flames.
    HOW_DARE_YOU_AWAKEN_ME_FEEL_THE_PAIN_OF_THE_FLAMES(1900028),
    // Text: Who dares oppose the majesty of fire...?
    WHO_DARES_OPPOSE_THE_MAJESTY_OF_FIRE(1900029),
    // Text: If you attack me right now, you're really going to get it!!!
    IF_YOU_ATTACK_ME_RIGHT_NOW_YOURE_REALLY_GOING_TO_GET_IT(1900034),
    // Text: Just you wait! I'm going to show you my killer technique.
    JUST_YOU_WAIT_IM_GOING_TO_SHOW_YOU_MY_KILLER_TECHNIQUE(1900035),
    // Text: Cold... This place...  Is this where I die...?
    COLD_THIS_PLACE_IS_THIS_WHERE_I_DIE(1900038),
    // Text: My body is cooling. Oh, Gran Kain... Forgive me...
    MY_BODY_IS_COOLING_OH_GRAN_KAIN_FORGIVE_ME(1900039),
    // Text: Idiot! I only incur damage from bare-handed attacks!
    IDIOT_I_ONLY_INCUR_DAMAGE_FROM_BARE_HANDED_ATTACKS(1900040),
    //Text: a,I don't wanna die... sniff.. I... don't want to die like that..\0
    I_DONT_WANNA_DIE_SNIFF_I_DONT_WANT_TO_DIE_LIKE_THAT(1900152),
    // Text: a,Don't...come.. Go away! Ahh...ahhh!\0
    DONT_CONE_GO_AWAY_AHH_AHHH(1900153),
    // Text: ,Give you life to the Goddess of Death! Kahahaha!\0
    GIVE_YOU_LIFE_TO_GODDES_OF_DEATH_KAHAHAHA(1900154),
    // Text: I heard the sound. Sacrifice your blood now. You'll resurrect later! Kehehehe!\0
    I_HEARD_THE_SOUND_SACRIFICE_YOUR_BLOOD_NOW(1900160),
    // Text: a,Don't die! But, you will. I will! You too! Hehehe!\0
    DONT_DIE_BUT_YOU_WILL(1900161),
    // Text: You summoned me, so you must be confident, huh? Here I come! Jack game!
    YOU_SUMMONED_ME_SO_YOU_MUST_BE_CONFIDENT_HUH_HERE_I_COME_JACK_GAME(1900041),
    // Text: Hello. Let's have a good Jack game.
    ABC1(1900042),
    // Text: I'm starting! Now, show me the card you want!
    ABC2(1900043),
    // Text: We'll start now! Show me the card you want!
    ABC3(1900044),
    // Text: I'm showing the Rotten Pumpkin Card!
    ABC4(1900045),
    // Text: I'll be showing the Rotten Pumpkin Card!
    ABC5(1900046),
    // Text: I'm showing the Jack Pumpkin Card!
    ABC6(1900047),
    // Text: I'll be showing the Jack Pumpkin Card!
    ABC7(1900048),
    // Text: That's my precious Fantastic Chocolate Banana Ultra Favor Candy!!! I'm definitely winning the next round!!
    ABC8(1900049),
    // Text: It's my precious candy, but... I'll happily give it to you~!
    ABC9(1900050),
    // Text: I'm out of candy. I'll give you my toy chest instead.
    ABC10(1900051),
    // Text: Since I'm out of candy, I will give you my toy chest instead.
    ABC11(1900052),
    // Text: You're not peeking at my card, are you? This time, I'll wager a special scroll.
    ABC12(1900053),
    // Text: We're getting serious now. If you win again, I'll give you a special scroll.
    ABC13(1900054),
    // Text: You could probably enter the underworld pro league!
    ABC14(1900055),
    // Text: Even pros can't do this much. You're amazing.
    ABC15(1900056),
    // Text: Who's the monster here?! This time, I'll bet my precious Transformation Stick.
    ABC16(1900057),
    // Text: I lost again. I won't lose this time. I'm betting my Transformation Stick.
    ABC17(1900058),
    // Text: Lost again! Hmph. Next time, I'll bet an incredible gift! Wait for it if you want!
    ABC18(1900059),
    // Text: You're too good. Next time, I'll give you an incredible gift! Please wait for it.
    ABC19(1900060),
    // Text: My pride can't handle you winning anymore!
    ABC20(1900061),
    // Text: I would be embarrassed to lose again here...
    ABC21(1900062),
    // Text: What's your name? I'm gonna remember you!
    ABC22(1900063),
    // Text: People from the above ground world are really good at games.
    ABC23(1900064),
    // Text: You've played a lot in the underworld, haven't you?!
    ABC24(1900065),
    // Text: I've never met someone so good before.
    ABC25(1900066),
    // Text: 13 wins in a row. You're pretty lucky today, huh?
    ABC26(1900067),
    // Text: I never thought I would see 13 wins in a row.
    ABC27(1900068),
    // Text: This is the highest record in my life! Next time, I'll give you my treasure -- the Golden Jack O'Lantern!
    ABC28(1900069),
    // Text: Even pros can't do 14 wins in a row...! Next time, I'll give you my treasure, the Golden Jack O'Lantern.
    ABC29(1900070),
    // Text: I can't do this anymore! You win! In all my 583 years, you're the best that I've seen!
    ABC30(1900071),
    // Text: Playing any more is meaningless. You were my greatest opponent.
    ABC31(1900072),
    // Text: I won this round...! It was fun.
    ABC32(1900073),
    // Text: I won this round. It was enjoyable.
    ABC33(1900074),
    // Text: Above world people are so fun...! Then, see you later!
    ABC34(1900075),
    // Text: Call me again next time. I want to play again with you.
    ABC35(1900076),
    // Text: You wanna play some more? I'm out of presents, but I'll give you candy!
    ABC36(1900077),
    // Text: Will you play some more? I don't have any more presents, but I will give you candy if you win.
    ABC37(1900078),
    // Text: You're the best. Out of all the Jack's game players I've ever met... I give up!
    ABC38(1900079),
    // Text: Wowww. Awesome. Really. I have never met someone as good as you before. Now... I can't play anymore.
    ABC39(1900080),
    // Text: %s has won %s Jack's games in a row.
    S1_ABC40_S2(1900081),
    // Text: Congratulations! %s has won %s Jack's games in a row.
    S1_ABC41_S2(1900082),
    // Text: Congratulations on getting 1st place in Jack's game!
    ABC42(1900083),
    // Text: Hello~! I'm Belldandy. Congratulations on getting 1st place in Jack's game. If you go and find my sibling Skooldie in the village, you'll get an amazing gift! Let's play Jack's game again!
    ABC43(1900084),
    // Text: Hmm. You're playing Jack's game for the first time, huh? You couldn't even take out your card at the right time~! My goodness...
    ABC44(1900085),
    // Text: Oh. You're not very familiar with Jack's game, right? You didn't take out your card at the right time...
    ABC45(1900086),
    // Text: You have to use the card skill on the mask before the gauge above my head disappears.
    ABC46(1900087),
    // Text: You must use the card skill on the mask before the gauge above my head disappears.
    ABC47(1900088),
    // Text: If you show the same card as me, you win. If they're different, I win. Understand? Now, let's go!
    ABC48(1900089),
    // Text: You will win if you show the same card as me. It's my victory if the cards are different. Well, let's start again~
    ABC49(1900090),
    // Text: Ack! You didn't show a card? You have to use the card skill before the gauge disappears. Hmph. Then, I'm going.
    ABC50(1900091),
    // Text: Ahh. You didn't show a card. You must use the card skill at the right time. It's unfortunate. Then, I will go now~
    ABC51(1900092),
    // Text: Let's learn about the Jack's game together~! You can play with me 3 times.
    ABC52(1900093),
    // Text: Let's start! Show the card you want! The card skill is attached to the mask.
    ABC53(1900094),
    // Text: You showed the same card as me, so you win.
    ABC54(1900095),
    // Text: You showed a different card from me, so you lose.
    ABC55(1900096),
    // Text: That was practice, so there's no candy even if you win~
    ABC56(1900097),
    // Text: It's unfortunate. Let's practice one more time.
    ABC57(1900098),
    // Text: You gotta show the card at the right time. Use the card skill you want before the gauge above my head disappears!
    ABC58(1900099),
    // Text: The card skills are attached to the Jack O'Lantern mask, right? That's what you use.
    ABC59(1900100),
    // Text: You win if you show the same card as me, and I win if the cards are different. OK, let's go~
    ABC60(1900101),
    // Text: You didn't show a card again? We'll try again later. I'm gonna go now~
    ABC61(1900102),
    // Text: Now, do you understand a little about Jack's game? The real game's with Uldie and Belldandy. Well, see you later!\0
    NOW_DO_YOU_UNDERSTAND_A_LITTLE_ABOUT_JACKS_GAME_THE_REAL_GAME_WITH_ULDIE_AND_BELLANDY_WELL_SEE_YOU_LATER(1900103),
    // Text: Lucky! I'm Lucky, the spirit that loves adena\0
    LUCKY_I_M_LUCKY_THE_SPIRIT_THAT_LOVES_ADENA(1900139),
    // Text: Lucky! I want to eat adena. Give it to me!
    LUCKY_I_WANT_TO_EAT_ADENA_GIVE_IT_TO_ME(1900140),
    // Text: Yummy. Thanks! Lucky!
    YUMMY_THANKS_LUCKY(1900142),
    // Text: Grrrr... Yuck…
    GRRRR_YUCK(1900143),
    // Text: Lucky! No more adena? Oh... I'm so heavy!
    LUCKY_NO_MORE_ADENA_OH_I_M_SO_HEAVY(1900145),
    // Text: Lucky! I'm full~ Thanks for the yummy adena! Oh... I'm so heavy!
    LUCKY_I_M_FULL_THANKS_FOR_THE_YUMMY_ADENA_OH_I_M_SO_HEAVY(1900146),
    // Text: Oh! My wings disappeared! Are you gonna hit me? If you hit me, I'll throw up everything that I ate!
    OH_MY_WINGS_DISAPPEARED_ARE_YOU_GONNA_HIT_ME_IF_YOU_HIT_ME_I_LL_THROW_UP_EVERYTHING_THAT_I_ATE(1900148),
    // Text: Oh! My wings... Ack! Are you gonna hit me?! Scary, scary! If you hit me, something bad will happen!
    OH_MY_WINGS_ACK_ARE_YOU_GONNA_HIT_ME_SCARY_SCARY_IF_YOU_HIT_ME_SOMETHING_BAD_WILL_HAPPEN(1900149),
    // Text: $s1 second(s) remaining.
    S1_SECONDS_REMAINING(1800079),
    // Text: Arg! The pain is more than I can stand!
    ARG_THE_PAIN_IS_MORE_THAN_I_CAN_STAND(1800185),
    // Text: Ahh! How did he find my weakness?
    AHH_HOW_DID_HE_FIND_MY_WEAKNESS(1800186),
    // Text: If you thought that my subordinates would be so few, you are mistaken!
    IF_YOU_THOUGHT_THAT_MY_SUBORDINATES_WOULD_BE_SO_FEW_YOU_ARE_MISTAKEN(1800180),
    // Text: There's not much I can do, but I want to help you.
    THERES_NOT_MUCH_I_CAN_DO_BUT_I_WANT_TO_HELP_YOU(1800181),
    // Text: Emergency! Emergency! The outer wall is weakening rapidly!
    EMERGENCY_EMERGENCY_THE_OTHER_WALL_IS_WEAKING_RAPIDLY(1800197),
    // Text: Enough fooling around. Get ready to die!
    ENOUGH_FOOLING_ARROUND_GET_READY_TO_DIE(1000407),
    // Text: It... will... kill... everyone...
    IT_WILL_KILL_EVERYONE(1000138),
    // Text: Eeek... I feel sick...yow...!
    EEEK_I_FEEL_SICK_YOW(1010451),
    // Text: Alright, now Leodas is yours!
    ALRIGHT_NOW_LEODAS_IS_YOURS(1800111),
    // Text: Hun.. hungry
    HUN_HUNGRY(1800073),
    // Text: Thank you for the rescue. It's a small gift.
    THANK_YOU_FOR_THE_RESCUE_ITS_A_SMALL_GIFT(1800026),
    // Text: The candles can lead you to Zaken. Destroy him
    THE_CANDLES_CAN_LEAD_YOU_TO_ZAKEN(1800866),
    // Text: Who dares awkawen the mighty Zaken?
    WHO_DARES_AWKAWEN_THE_MIGHTY_ZAKEN(1800867),
    // Text: Ye not be finding me below the drink!
    YE_NOT_BE_FINDING_ME_BELOW_THE_DRINK(1800868),
    // Text: Ye must be three sheets to the wind if yer lookin for me there.
    YE_MUST_BE_THREE_SHEETS_TO_THE_WIND_IF_YER_LOOKIN_FOR_ME_THERE(1800869),
    // Text: Ye not be finding me in the Crows Nest.
    YE_NOT_BE_FINDING_ME_IN_THE_CROWS_NEST(1800870),
    // Text: Thank you for saving me.
    THANK_YOU_FOR_SAVING_ME(1000503),
    // Text: Guards are coming, run!
    GUARD_ARE_COMING_RUN(1800120),
    S2_OF_LEVEL_S1_IS_ACQUIRED(1800877),
    // Text: Now I can escape on my own!
    NOW_I_CAN_ESCAPE_ON_MY_OWN(1800121);

    private final int _id;
    private final int _size;

    NpcString(final int id) {
        _id = id;

        if (name().contains("S4")) {
            _size = 4;
        } else if (name().contains("S3")) {
            _size = 3;
        } else if (name().contains("S2")) {
            _size = 2;
        } else if (name().contains("S1")) {
            _size = 1;
        } else {
            _size = 0;
        }
    }

    public static NpcString valueOf(final int id) {
        for (final NpcString m : values()) {
            if (m.getId() == id) {
                return m;
            }
        }

        throw new NoSuchElementException("Not find NpcString by id: " + id);
    }

    public int getId() {
        return _id;
    }

    public int getSize() {
        return _size;
    }
}
