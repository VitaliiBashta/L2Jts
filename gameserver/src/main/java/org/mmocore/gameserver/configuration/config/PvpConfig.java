package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Mangol on 29.11.2015.
 */
@Configuration("pvp.json")
public class PvpConfig {
    @Setting(name = "MinKarma")
    public static int KARMA_MIN_KARMA;

    @Setting(name = "BaseKarmaLost")
    public static int KARMA_LOST_BASE;

    @Setting(name = "CanGMDropEquipment")
    public static boolean KARMA_DROP_GM;

    @Setting(name = "ListOfNonDroppableItems", canNull = true)
    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = Arrays.asList(57, 1147, 425, 1146, 461, 10, 2368, 7, 6, 2370, 2369, 3500, 3501, 3502, 4422, 4423, 4424, 2375, 6648, 6649, 6650, 6842, 6834, 6835, 6836, 6837, 6838, 6839, 6840, 5575, 7694, 6841, 8181);

    @Setting(name = "DropAugmented")
    public static boolean DROP_ITEMS_AUGMENTED;

    @Setting(name = "DropOnDie")
    public static boolean DROP_ITEMS_ON_DIE;

    @Setting(name = "ChanceOfNormalDropBase")
    public static double NORMAL_DROPCHANCE_BASE;

    @Setting(name = "ChanceOfPKDropBase")
    public static double KARMA_DROPCHANCE_BASE;

    @Setting(name = "ChanceOfPKsDropMod")
    public static double KARMA_DROPCHANCE_MOD;

    @Setting(name = "ChanceOfDropWeapon")
    public static int DROPCHANCE_EQUIPPED_WEAPON;

    @Setting(name = "ChanceOfDropEquippment")
    public static int DROPCHANCE_EQUIPMENT;

    @Setting(name = "ChanceOfDropOther")
    public static int DROPCHANCE_ITEM;

    @Setting(name = "MaxItemsDroppable")
    public static int KARMA_DROP_ITEM_LIMIT;

    @Setting(name = "MaxDropThrowDistance")
    public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;

    @Setting(name = "MinPKToDropItems")
    public static int MIN_PK_TO_ITEMS_DROP;

    @Setting(name = "KarmaNeededToDrop")
    public static boolean KARMA_NEEDED_TO_DROP;

    @Setting(name = "PvPTime")
    public static int PVP_TIME;
}
