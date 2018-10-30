package org.mmocore.gameserver.model.base;

import org.jts.dataparser.data.holder.setting.InitialStartPoint.PlayerClasses;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a player can chose.<BR><BR>
 * <p/>
 * Data :<BR><BR>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId for male or null if this class is the root</li>
 * <li>parent2 : The parent2 ClassId for female or null if parent2 like parent</li>
 * <li>level : The child level of this Class</li><BR><BR>
 */
public enum ClassId {
    fighter(0, PlayerRace.human, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.human_fighter),

    warrior(1, PlayerRace.human, fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.human_fighter),
    gladiator(2, PlayerRace.human, warrior, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.human_fighter),
    warlord(3, PlayerRace.human, warrior, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.human_fighter),
    knight(4, PlayerRace.human, fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.human_fighter),
    paladin(5, PlayerRace.human, knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Knight, PlayerClasses.human_fighter),
    dark_avenger(6, PlayerRace.human, knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Knight, PlayerClasses.human_fighter),
    rogue(7, PlayerRace.human, fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.human_fighter),
    treasure_hunter(8, PlayerRace.human, rogue, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.human_fighter),
    hawkeye(9, PlayerRace.human, rogue, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.human_fighter),

    mage(10, PlayerRace.human, null, null, ClassLevel.First, ClassType.Mystic, null, PlayerClasses.human_magician),
    wizard(11, PlayerRace.human, mage, null, ClassLevel.Second, ClassType.Mystic, null, PlayerClasses.human_magician),
    sorceror(12, PlayerRace.human, wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.human_magician),
    necromancer(13, PlayerRace.human, wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.human_magician),
    warlock(14, PlayerRace.human, wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.human_magician),
    cleric(15, PlayerRace.human, mage, null, ClassLevel.Second, ClassType.Priest, null, PlayerClasses.human_magician),
    bishop(16, PlayerRace.human, cleric, null, ClassLevel.Third, ClassType.Priest, ClassType2.Healer, PlayerClasses.human_magician),
    prophet(17, PlayerRace.human, cleric, null, ClassLevel.Third, ClassType.Priest, ClassType2.Enchanter, PlayerClasses.human_magician),

    elven_fighter(18, PlayerRace.elf, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.elf_fighter),
    elven_knight(19, PlayerRace.elf, elven_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.elf_fighter),
    temple_knight(20, PlayerRace.elf, elven_knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Knight, PlayerClasses.elf_fighter),
    sword_singer(21, PlayerRace.elf, elven_knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.elf_fighter),
    elven_scout(22, PlayerRace.elf, elven_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.elf_fighter),
    plains_walker(23, PlayerRace.elf, elven_scout, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.elf_fighter),
    silver_ranger(24, PlayerRace.elf, elven_scout, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.elf_fighter),

    elven_mage(25, PlayerRace.elf, null, null, ClassLevel.First, ClassType.Mystic, null, PlayerClasses.elf_magician),
    elven_wizard(26, PlayerRace.elf, elven_mage, null, ClassLevel.Second, ClassType.Mystic, null, PlayerClasses.elf_magician),
    spellsinger(27, PlayerRace.elf, elven_wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.elf_magician),
    elemental_summoner(28, PlayerRace.elf, elven_wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.elf_magician),
    oracle(29, PlayerRace.elf, elven_mage, null, ClassLevel.Second, ClassType.Priest, null, PlayerClasses.elf_magician),
    elder(30, PlayerRace.elf, oracle, null, ClassLevel.Third, ClassType.Priest, ClassType2.Healer, PlayerClasses.elf_magician),

    dark_fighter(31, PlayerRace.darkelf, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.darkelf_fighter),
    palus_knight(32, PlayerRace.darkelf, dark_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.darkelf_fighter),
    shillien_knight(33, PlayerRace.darkelf, palus_knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Knight, PlayerClasses.darkelf_fighter),
    bladedancer(34, PlayerRace.darkelf, palus_knight, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.darkelf_fighter),
    assassin(35, PlayerRace.darkelf, dark_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.darkelf_fighter),
    abyss_walker(36, PlayerRace.darkelf, assassin, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.darkelf_fighter),
    phantom_ranger(37, PlayerRace.darkelf, assassin, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.darkelf_fighter),

    dark_mage(38, PlayerRace.darkelf, null, null, ClassLevel.First, ClassType.Mystic, null, PlayerClasses.darkelf_magician),
    dark_wizard(39, PlayerRace.darkelf, dark_mage, null, ClassLevel.Second, ClassType.Mystic, null, PlayerClasses.darkelf_magician),
    spellhowler(40, PlayerRace.darkelf, dark_wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.darkelf_magician),
    phantom_summoner(41, PlayerRace.darkelf, dark_wizard, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.darkelf_magician),
    shillien_oracle(42, PlayerRace.darkelf, dark_mage, null, ClassLevel.Second, ClassType.Priest, null, PlayerClasses.darkelf_magician),
    shillien_elder(43, PlayerRace.darkelf, shillien_oracle, null, ClassLevel.Third, ClassType.Priest, ClassType2.Healer, PlayerClasses.darkelf_magician),

    orc_fighter(44, PlayerRace.orc, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.orc_fighter),
    orc_raider(45, PlayerRace.orc, orc_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.orc_fighter),
    destroyer(46, PlayerRace.orc, orc_raider, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.orc_fighter),
    orc_monk(47, PlayerRace.orc, orc_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.orc_fighter),
    tyrant(48, PlayerRace.orc, orc_monk, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.orc_fighter),

    orc_mage(49, PlayerRace.orc, null, null, ClassLevel.First, ClassType.Mystic, null, PlayerClasses.orc_shaman),
    orc_shaman(50, PlayerRace.orc, orc_mage, null, ClassLevel.Second, ClassType.Mystic, null, PlayerClasses.orc_shaman),
    overlord(51, PlayerRace.orc, orc_shaman, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Enchanter, PlayerClasses.orc_shaman),
    warcryer(52, PlayerRace.orc, orc_shaman, null, ClassLevel.Third, ClassType.Mystic, ClassType2.Enchanter, PlayerClasses.orc_shaman),

    dwarven_fighter(53, PlayerRace.dwarf, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.dwarf_apprentice),
    scavenger(54, PlayerRace.dwarf, dwarven_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.dwarf_apprentice),
    bounty_hunter(55, PlayerRace.dwarf, scavenger, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.dwarf_apprentice),
    artisan(56, PlayerRace.dwarf, dwarven_fighter, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.dwarf_apprentice),
    warsmith(57, PlayerRace.dwarf, artisan, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.dwarf_apprentice),

    dummyEntry1(58, null, null, null, null, null, null, null),
    dummyEntry2(59, null, null, null, null, null, null, null),
    dummyEntry3(60, null, null, null, null, null, null, null),
    dummyEntry4(61, null, null, null, null, null, null, null),
    dummyEntry5(62, null, null, null, null, null, null, null),
    dummyEntry6(63, null, null, null, null, null, null, null),
    dummyEntry7(64, null, null, null, null, null, null, null),
    dummyEntry8(65, null, null, null, null, null, null, null),
    dummyEntry9(66, null, null, null, null, null, null, null),
    dummyEntry10(67, null, null, null, null, null, null, null),
    dummyEntry11(68, null, null, null, null, null, null, null),
    dummyEntry12(69, null, null, null, null, null, null, null),
    dummyEntry13(70, null, null, null, null, null, null, null),
    dummyEntry14(71, null, null, null, null, null, null, null),
    dummyEntry15(72, null, null, null, null, null, null, null),
    dummyEntry16(73, null, null, null, null, null, null, null),
    dummyEntry17(74, null, null, null, null, null, null, null),
    dummyEntry18(75, null, null, null, null, null, null, null),
    dummyEntry19(76, null, null, null, null, null, null, null),
    dummyEntry20(77, null, null, null, null, null, null, null),
    dummyEntry21(78, null, null, null, null, null, null, null),
    dummyEntry22(79, null, null, null, null, null, null, null),
    dummyEntry23(80, null, null, null, null, null, null, null),
    dummyEntry24(81, null, null, null, null, null, null, null),
    dummyEntry25(82, null, null, null, null, null, null, null),
    dummyEntry26(83, null, null, null, null, null, null, null),
    dummyEntry27(84, null, null, null, null, null, null, null),
    dummyEntry28(85, null, null, null, null, null, null, null),
    dummyEntry29(86, null, null, null, null, null, null, null),
    dummyEntry30(87, null, null, null, null, null, null, null),

    duelist(88, PlayerRace.human, gladiator, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.human_fighter),
    dreadnought(89, PlayerRace.human, warlord, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.human_fighter),
    phoenix_knight(90, PlayerRace.human, paladin, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Knight, PlayerClasses.human_fighter),
    hell_knight(91, PlayerRace.human, dark_avenger, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Knight, PlayerClasses.human_fighter),
    sagittarius(92, PlayerRace.human, hawkeye, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.human_fighter),
    adventurer(93, PlayerRace.human, treasure_hunter, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.human_fighter),

    archmage(94, PlayerRace.human, sorceror, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.human_magician),
    soultaker(95, PlayerRace.human, necromancer, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.human_magician),
    arcana_lord(96, PlayerRace.human, warlock, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.human_magician),
    cardinal(97, PlayerRace.human, bishop, null, ClassLevel.Fourth, ClassType.Priest, ClassType2.Healer, PlayerClasses.human_magician),
    hierophant(98, PlayerRace.human, prophet, null, ClassLevel.Fourth, ClassType.Priest, ClassType2.Enchanter, PlayerClasses.human_magician),

    eva_templar(99, PlayerRace.elf, temple_knight, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Knight, PlayerClasses.elf_fighter),
    sword_muse(100, PlayerRace.elf, sword_singer, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.elf_fighter),
    wind_rider(101, PlayerRace.elf, plains_walker, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.elf_fighter),
    moonlight_sentinel(102, PlayerRace.elf, silver_ranger, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.elf_fighter),

    mystic_muse(103, PlayerRace.elf, spellsinger, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.elf_magician),
    elemental_master(104, PlayerRace.elf, elemental_summoner, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.elf_magician),
    eva_saint(105, PlayerRace.elf, elder, null, ClassLevel.Fourth, ClassType.Priest, ClassType2.Healer, PlayerClasses.elf_magician),

    shillien_templar(106, PlayerRace.darkelf, shillien_knight, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Knight, PlayerClasses.darkelf_fighter),
    spectral_dancer(107, PlayerRace.darkelf, bladedancer, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.darkelf_fighter),
    ghost_hunter(108, PlayerRace.darkelf, abyss_walker, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.darkelf_fighter),
    ghost_sentinel(109, PlayerRace.darkelf, phantom_ranger, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.darkelf_fighter),

    storm_screamer(110, PlayerRace.darkelf, spellhowler, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Wizard, PlayerClasses.darkelf_magician),
    spectral_master(111, PlayerRace.darkelf, phantom_summoner, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Summoner, PlayerClasses.darkelf_magician),
    shillien_saint(112, PlayerRace.darkelf, shillien_elder, null, ClassLevel.Fourth, ClassType.Priest, ClassType2.Healer, PlayerClasses.darkelf_magician),

    titan(113, PlayerRace.orc, destroyer, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.orc_fighter),
    grand_khauatari(114, PlayerRace.orc, tyrant, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.orc_fighter),

    dominator(115, PlayerRace.orc, overlord, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Enchanter, PlayerClasses.orc_shaman),
    doomcryer(116, PlayerRace.orc, warcryer, null, ClassLevel.Fourth, ClassType.Mystic, ClassType2.Enchanter, PlayerClasses.orc_shaman),

    fortune_seeker(117, PlayerRace.dwarf, bounty_hunter, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.dwarf_apprentice),
    maestro(118, PlayerRace.dwarf, warsmith, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.dwarf_apprentice),

    dummyEntry31(119, null, null, null, null, null, null, null),
    dummyEntry32(120, null, null, null, null, null, null, null),
    dummyEntry33(121, null, null, null, null, null, null, null),
    dummyEntry34(122, null, null, null, null, null, null, null),

    /**
     * Kamael
     */
    kamael_m_soldier(123, PlayerRace.kamael, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.kamael_m_soldier),
    kamael_f_soldier(124, PlayerRace.kamael, null, null, ClassLevel.First, ClassType.Fighter, null, PlayerClasses.kamael_f_soldier),
    trooper(125, PlayerRace.kamael, kamael_m_soldier, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.kamael_m_soldier),
    warder(126, PlayerRace.kamael, kamael_f_soldier, null, ClassLevel.Second, ClassType.Fighter, null, PlayerClasses.kamael_f_soldier),
    berserker(127, PlayerRace.kamael, trooper, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_m_soldier),
    m_soul_breaker(128, PlayerRace.kamael, trooper, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_m_soldier),
    f_soul_breaker(129, PlayerRace.kamael, warder, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_f_soldier),
    arbalester(130, PlayerRace.kamael, warder, null, ClassLevel.Third, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.kamael_f_soldier),
    doombringer(131, PlayerRace.kamael, berserker, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_m_soldier),
    m_soul_hound(132, PlayerRace.kamael, m_soul_breaker, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_m_soldier),
    f_soul_hound(133, PlayerRace.kamael, f_soul_breaker, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Warrior, PlayerClasses.kamael_f_soldier),
    trickster(134, PlayerRace.kamael, arbalester, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Rogue, PlayerClasses.kamael_f_soldier),
    inspector(135, PlayerRace.kamael, trooper, warder, ClassLevel.Third, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.kamael_m_soldier), //Aditional
    judicator(136, PlayerRace.kamael, inspector, null, ClassLevel.Fourth, ClassType.Fighter, ClassType2.Enchanter, PlayerClasses.kamael_f_soldier); //Aditional

    public static final ClassId[] VALUES = values();

    /**
     * The Identifier of the Class<?>
     */
    private final int _id;

    /**
     * The Race object of the class
     */
    private final PlayerRace _race;

    /**
     * The parent ClassId for male or null if this class is a root
     */
    private final ClassId _parent;

    /**
     * The parent2 ClassId for female or null if parent2 class is parent
     */
    private final ClassId _parent2;

    private final ClassType2 _type2;

    private final ClassType _type;

    private final ClassLevel _level;

    private final PlayerClasses _playerClasses;

    /**
     * Constructor<?> of ClassId.<BR><BR>
     */
    ClassId(final int id, final PlayerRace race, final ClassId parent, final ClassId parent2, final ClassLevel level, final ClassType type, final ClassType2 classType2, final PlayerClasses playerClasses) {
        _id = id;
        _race = race;
        _parent = parent;
        _parent2 = parent2;
        _level = level;
        _type = type;
        _type2 = classType2;
        _playerClasses = playerClasses;
    }

    /**
     * Return the Identifier of the Class.<BR><BR>
     */
    public final int getId() {
        return _id;
    }

    /**
     * Return True if the class is a mage class.<BR><BR>
     */
    public final boolean isMage() {
        return _type.isMagician();
    }

    /**
     * Return the Race object of the class.<BR><BR>
     */
    public final PlayerRace getRace() {
        return _race;
    }

    public final boolean isOfRace(PlayerRace race) {
        return _race == race;
    }

    /**
     * Return True if this Class<?> is a child of the selected ClassId.<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final boolean childOf(final ClassId cid) {
        if (_parent == null) {
            return false;
        }

        if (_parent == cid || _parent2 == cid) {
            return true;
        }

        return _parent.childOf(cid);

    }

    /**
     * Return True if this Class<?> is equal to the selected ClassId or a child of the selected ClassId.<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final boolean equalsOrChildOf(final ClassId cid) {
        return this == cid || childOf(cid);
    }

    /**
     * Return the child level of this Class<?> (0=root, 1=child leve 1...).<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final int level() {
        if (_parent == null) {
            return 0;
        }

        return 1 + _parent.level();
    }

    public final ClassId getParent(final int sex) {
        return sex == 0 || _parent2 == null ? _parent : _parent2;
    }

    public final ClassLevel getLevel() {
        return _level;
    }

    public final boolean isOfLevel(ClassLevel level) {
        return _level == level;
    }

    public ClassType2 getType2() {
        return _type2;
    }

    public ClassType getType() {
        return _type;
    }

    public PlayerClasses getPlayerClasses() {
        return _playerClasses;
    }

    public final boolean isOfType(ClassType type) {
        return _type == type;
    }
}