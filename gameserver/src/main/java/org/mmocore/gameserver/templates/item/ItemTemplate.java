package org.mmocore.gameserver.templates.item;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.jts.dataparser.data.holder.variationdata.VariationData;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.items.IItemHandler;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.StatTemplate;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.templates.item.support.CapsuledItemsContainer;
import org.quartz.CronExpression;

import java.util.*;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of :
 * <LI>L2Armor</LI>
 * <LI>L2EtcItem</LI>
 * <LI>L2Weapon</LI>
 */
public abstract class ItemTemplate extends StatTemplate {
    public static final int ITEM_ID_PC_BANG_POINTS = -100;
    public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
    public static final int ITEM_ID_FAME = -300;
    public static final int PREMIUM_POINTS = 999999;
    public static final int ITEM_ID_ADENA = 57;
    public static final int OLYMPIAD_TOKEN = 13722;
    /**
     * Item ID для замковых корон
     */
    public static final int[] ITEM_ID_CASTLE_CIRCLET = {
            0, // no castle - no circlet.. :)
            6838, // Circlet of Gludio
            6835, // Circlet of Dion
            6839, // Circlet of Giran
            6837, // Circlet of Oren
            6840, // Circlet of Aden
            6834, // Circlet of Innadril
            6836, // Circlet of Goddard
            8182, // Circlet of Rune
            8183, // Circlet of Schuttgart
    };
    public static final int ITEM_ID_FORMAL_WEAR = 6408;
    public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    public static final int TYPE1_SHIELD_ARMOR = 1;
    public static final int TYPE1_OTHER = 2;
    public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_QUEST = 3;
    public static final int TYPE2_MONEY = 4;
    public static final int TYPE2_OTHER = 5;
    public static final int TYPE2_PET_WOLF = 6;
    public static final int TYPE2_PET_HATCHLING = 7;
    public static final int TYPE2_PET_STRIDER = 8;
    public static final int TYPE2_NODROP = 9;
    public static final int TYPE2_PET_GWOLF = 10;
    public static final int TYPE2_PENDANT = 11;
    public static final int TYPE2_PET_BABY = 12;
    public static final int SLOT_NONE = 0x00000;
    public static final int SLOT_UNDERWEAR = 0x00001;
    public static final int SLOT_R_EAR = 0x00002;
    public static final int SLOT_L_EAR = 0x00004;
    public static final int SLOT_NECK = 0x00008;
    public static final int SLOT_R_FINGER = 0x00010;
    public static final int SLOT_L_FINGER = 0x00020;
    public static final int SLOT_HEAD = 0x00040;
    public static final int SLOT_R_HAND = 0x00080;
    public static final int SLOT_L_HAND = 0x00100;
    public static final int SLOT_GLOVES = 0x00200;
    public static final int SLOT_CHEST = 0x00400;
    public static final int SLOT_LEGS = 0x00800;
    public static final int SLOT_FEET = 0x01000;
    public static final int SLOT_BACK = 0x02000;
    public static final int SLOT_LR_HAND = 0x04000;
    public static final int SLOT_FULL_ARMOR = 0x08000;
    public static final int SLOT_HAIR = 0x10000;
    public static final int SLOT_FORMAL_WEAR = 0x20000;
    public static final int SLOT_COSTUME = 0x20000;
    public static final int SLOT_DHAIR = 0x40000;
    public static final int SLOT_HAIRALL = 0x80000;
    public static final int SLOT_R_BRACELET = 0x100000;
    public static final int SLOT_L_BRACELET = 0x200000;
    public static final int SLOT_DECO = 0x400000;
    public static final int SLOT_BELT = 0x10000000;
    public static final int SLOT_WOLF = -100;
    public static final int SLOT_HATCHLING = -101;
    public static final int SLOT_STRIDER = -102;
    public static final int SLOT_BABYPET = -103;
    public static final int SLOT_GWOLF = -104;
    public static final int SLOT_PENDANT = -105;
    // Все слоты, используемые броней.
    public static final int SLOTS_ARMOR = SLOT_HEAD | SLOT_L_HAND | SLOT_GLOVES | SLOT_CHEST | SLOT_LEGS | SLOT_FEET | SLOT_BACK | SLOT_FULL_ARMOR;
    // Все слоты, используемые бижей.
    public static final int SLOTS_JEWELRY = SLOT_R_EAR | SLOT_L_EAR | SLOT_NECK | SLOT_R_FINGER | SLOT_L_FINGER;
    public static final int CRYSTAL_NONE = 0;
    public static final int CRYSTAL_D = 1458;
    public static final int CRYSTAL_C = 1459;
    public static final int CRYSTAL_B = 1460;
    public static final int CRYSTAL_A = 1461;
    public static final int CRYSTAL_S = 1462;
    public static final int ATTRIBUTE_NONE = -2;
    public static final int ATTRIBUTE_FIRE = 0;
    public static final int ATTRIBUTE_WATER = 1;
    public static final int ATTRIBUTE_WIND = 2;
    public static final int ATTRIBUTE_EARTH = 3;
    public static final int ATTRIBUTE_HOLY = 4;
    public static final int ATTRIBUTE_DARK = 5;
    protected final int _itemId;
    protected final String _name;
    protected final String _html;
    protected final String _addname;
    protected final String _icon;
    protected final String _icon32;
    protected final Grade _crystalType; // default to none-grade
    private final ItemClass _class;
    private final int _weight;
    private final int _durability;
    private final int _referencePrice;
    private final int _crystalCount;
    private final boolean _temporal;
    private final boolean _stackable;
    private final boolean _crystallizable;
    //TODO Сделать поддержку премиум.
    private final boolean _premium;
    private final ReuseType _reuseType;
    private final int _reuseDelay;
    private final int _reuseGroup;
    private final int _equipReuseDelay;
    private final int _popup;
    private final boolean _isMagicWeapon;
    private final boolean _hideConsumeMessage;
    private final String _teleportLoc;
    public ItemType type;
    protected int _type1; // needed for item list (inventory)
    protected int _type2; // different lists for armor, weapon, etc
    protected final ItemAction _actionType; // action type on item
    protected int _bodyPart;
    protected SkillEntry[] _skills;
    private int _flags;
    private final int _questId;
    private SkillEntry _enchant4Skill = null; // skill that activates when item is enchanted +4 (for duals)
    private int[] _baseAttributes = new int[6];
    private Map<Integer, int[]> _enchantOptions = Collections.emptyMap();
    private List<CapsuledItemsContainer> _capsuledItems = null;
    private Condition _condition;
    private IItemHandler _handler = IItemHandler.NULL;
    private final boolean _capsuled; // Для предметов, создающих другие предметы = true
    private int _variationGroupId;
    private final String[] epicRegex = {
            "Queen", "Baium", "Core", "Valakas", "Frintezza", "Orfen", "Zaken", "Antharas"
    };

    /**
     * Constructor<?> of the L2Item that fill class variables.<BR><BR>
     * <U><I>Variables filled :</I></U><BR>
     * <LI>type</LI>
     * <p/>
     * <LI>_itemId</LI>
     * <LI>_name</LI>
     * <LI>_type1 & _type2</LI>
     * <LI>_weight</LI>
     * <LI>_crystallizable</LI>
     * <LI>_stackable</LI>
     * <LI>_materialType & _crystalType & _crystlaCount</LI>
     * <LI>_durability</LI>
     * <LI>_bodypart</LI>
     * <LI>_referencePrice</LI>
     * <LI>_sellable</LI>
     *
     * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
     */
    protected ItemTemplate(final StatsSet set) {
        _itemId = set.getInteger("item_id");
        _class = set.getEnum("class", ItemClass.class, ItemClass.OTHER);
        _name = set.getString("name");
        _addname = set.getString("add_name", "");
        _icon = set.getString("icon", "");
        _icon32 = "<img src=icon." + _icon + " width=32 height=32>";
        _weight = set.getInteger("weight", 0);
        _crystallizable = set.getBool("crystallizable", false);
        _stackable = set.getBool("stackable", false);
        _crystalType = set.getEnum("crystal_type", Grade.class, Grade.NONE); // default to none-grade
        _durability = set.getInteger("durability", -1);
        _temporal = set.getBool("temporal", false);
        _bodyPart = set.getInteger("bodypart", 0);
        _referencePrice = set.getInteger("price", 0);
        _crystalCount = set.getInteger("crystal_count", 0);
        _reuseType = set.getEnum("reuse_type", ReuseType.class, ReuseType.NORMAL);
        _reuseDelay = set.getInteger("reuse_delay", 0);
        _reuseGroup = set.getInteger("delay_share_group", -_itemId);
        _popup = set.getInteger("popup", 0);
        _equipReuseDelay = set.getInteger("equip_reuse_delay", -1);
        _isMagicWeapon = set.getBool("is_magic_weapon", false);
        _premium = set.getBool("premium", false);
        _teleportLoc = set.getString("loc", "");
        _capsuled = set.getBool("capsuled", false);
        _actionType = set.getEnum("default_action", ItemAction.class, ItemAction.action_none);
        _html = set.getString("html", "");
        _questId = set.getInteger("quest_id", 0);
        _hideConsumeMessage = set.getBool("hide_consume_message", false);
        _variationGroupId = set.getInteger("variation_group_id", 0);

        for (final ItemFlags f : ItemFlags.VALUES) {
            final boolean flag = set.getBool(f.name().toLowerCase(), f.getDefaultValue());
            if (flag) {
                activeFlag(f);
            }
        }

        _funcTemplates = FuncTemplate.EMPTY_ARRAY;
        _skills = SkillEntry.EMPTY_ARRAY;
    }

    public String getTeleportLoc() {
        return _teleportLoc;
    }

    /**
     * Returns the itemType.
     *
     * @return Enum
     */
    public ItemType getItemType() {
        return type;
    }

    public String getIcon() {
        return _icon;
    }

    /**
     * Возвращает готовую для отображения в html строку вида
     * <img src=icon.иконка width=32 height=32>
     */
    public String getIcon32() {
        return _icon32;
    }

    /**
     * Returns the durability of th item
     *
     * @return int
     */
    public final int getDurability() {
        return _durability;
    }

    public final boolean isTemporal() {
        return _temporal;
    }

    /**
     * Returns the ID of the item
     *
     * @return int
     */
    public final int getItemId() {
        return _itemId;
    }

    public abstract long getItemMask();

    /**
     * Returns the type 2 of the item
     *
     * @return int
     */
    public final int getType2() {
        return _type2;
    }

    public final int getBaseAttributeValue(final Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return _baseAttributes[element.getId()];
    }

    public void setBaseAtributeElements(final int[] val) {
        _baseAttributes = val;
    }

    public final int getType2ForPackets() {
        int type2 = _type2;
        switch (_type2) {
            case TYPE2_PET_WOLF:
            case TYPE2_PET_HATCHLING:
            case TYPE2_PET_STRIDER:
            case TYPE2_PET_GWOLF:
            case TYPE2_PET_BABY:
                if (_bodyPart == ItemTemplate.SLOT_CHEST) {
                    type2 = TYPE2_SHIELD_ARMOR;
                } else {
                    type2 = TYPE2_WEAPON;
                }
                break;
            case TYPE2_PENDANT:
                type2 = TYPE2_ACCESSORY;
                break;
        }
        return type2;
    }

    /**
     * Returns the weight of the item
     *
     * @return int
     */
    public final int getWeight() {
        return _weight;
    }

    /**
     * Returns if the item is crystallizable
     *
     * @return boolean
     */
    public final boolean isCrystallizable() {
        return _crystallizable && !isStackable() && getCrystalType() != Grade.NONE && getCrystalCount() > 0;
    }

    /**
     * Return the type of crystal if item is crystallizable
     *
     * @return int
     */
    public final Grade getCrystalType() {
        return _crystalType;
    }

    /**
     * Returns the grade of the item.<BR><BR>
     * <U><I>Concept :</I></U><BR>
     * In fact, this fucntion returns the type of crystal of the item.
     *
     * @return int
     */
    public final Grade getItemGrade() {
        return getCrystalType();
    }

    /**
     * Returns the quantity of crystals for crystallization
     *
     * @return int
     */
    public final int getCrystalCount() {
        return _crystalCount;
    }

    public final int getCrystalCount(final int enchantLevel, final boolean enchantFail) {
        int result = _crystalCount;
        if (enchantFail) {
            result /= 2;
        }

        if (enchantLevel > 0) {
            final int enchantBonus = isWeapon() ? getItemGrade().cryMod : getItemGrade().cryMod / 10;
            result += enchantLevel * enchantBonus;
            final int overEnchant = enchantLevel - 3;
            if (overEnchant > 0) {
                final int overEnchantBonus = isWeapon() ? getItemGrade().cryMod : getItemGrade().cryMod / 5;
                result += overEnchant * overEnchantBonus;
            }
        }

        return result;
    }

    /**
     * Returns the name of the item
     *
     * @return String
     */
    public final String getName() {
        return _name;
    }

    /**
     * Returns the additional name of the item
     *
     * @return String
     */
    public final String getAdditionalName() {
        return _addname;
    }

    /**
     * Return the part of the body used with the item.
     *
     * @return int
     */
    public final int getBodyPart() {
        return _bodyPart;
    }

    /**
     * Returns the type 1 of the item
     *
     * @return int
     */
    public final int getType1() {
        return _type1;
    }

    public ItemAction getActionType() {
        return _actionType;
    }

    public String getDefaultHtml() {
        return _html;
    }

    public int getQuestId() {
        return _questId;
    }

    /**
     * Returns if the item is stackable
     *
     * @return boolean
     */
    public final boolean isStackable() {
        return _stackable;
    }

    /**
     * Returns the price of reference of the item
     *
     * @return int
     */
    public final int getReferencePrice() {
        return _referencePrice;
    }

    /**
     * Returns if item is for hatchling
     *
     * @return boolean
     */
    public boolean isForHatchling() {
        return _type2 == TYPE2_PET_HATCHLING;
    }

    /**
     * Returns if item is for strider
     *
     * @return boolean
     */
    public boolean isForStrider() {
        return _type2 == TYPE2_PET_STRIDER;
    }

    /**
     * Returns if item is for wolf
     *
     * @return boolean
     */
    public boolean isForWolf() {
        return _type2 == TYPE2_PET_WOLF;
    }

    public boolean isForPetBaby() {
        return _type2 == TYPE2_PET_BABY;
    }

    /**
     * Returns if item is for great wolf
     *
     * @return boolean
     */
    public boolean isForGWolf() {
        return _type2 == TYPE2_PET_GWOLF;
    }

    /**
     * Магическая броня для петов
     */
    public boolean isPendant() {
        return _type2 == TYPE2_PENDANT;
    }

    public boolean isForPet() {
        return _type2 == TYPE2_PENDANT || _type2 == TYPE2_PET_HATCHLING || _type2 == TYPE2_PET_WOLF || _type2 == TYPE2_PET_STRIDER ||
                _type2 == TYPE2_PET_GWOLF || _type2 == TYPE2_PET_BABY;
    }

    /**
     * Add the L2Skill skill to the list of skills generated by the item
     *
     * @param skill : L2Skill
     */
    public void attachSkill(final SkillEntry skill) {
        _skills = ArrayUtils.add(_skills, skill);
    }

    public SkillEntry[] getAttachedSkills() {
        return _skills;
    }

    public SkillEntry getFirstSkill() {
        if (_skills.length > 0) {
            return _skills[0];
        }
        return null;
    }

    /**
     * @return skill that player get when has equipped weapon +4  or more  (for duals SA)
     */
    public SkillEntry getEnchant4Skill() {
        return _enchant4Skill;
    }

    public void setEnchant4Skill(final SkillEntry enchant4Skill) {
        _enchant4Skill = enchant4Skill;
    }

    /**
     * Returns the name of the item
     *
     * @return String
     */
    @Override
    public String toString() {
        return _itemId + " " + _name;
    }

    /**
     * Определяет призрачный предмет или нет
     *
     * @return true, если предмет призрачный
     */
    public boolean isShadowItem() {
        return _durability > 0 && !isTemporal();
    }

    public boolean isCommonItem() {
        return _name.startsWith("Common Item - ");
    }

    public boolean isSealedItem() {
        return _name.startsWith("Sealed");
    }

    public boolean isAltSeed() {
        return _name.contains("Alternative");
    }

    public ItemClass getItemClass() {
        return _class;
    }

    /**
     * Является ли вещь аденой или камнем печати
     */
    public boolean isAdena() {
        return _itemId == 57;// || _itemId == 6360 || _itemId == 6361 || _itemId == 6362;
    }

    public boolean isEquipment() {
        return _type1 != TYPE1_ITEM_QUESTITEM_ADENA;
    }

    public boolean isKeyMatherial() {
        return _class == ItemClass.PIECES;
    }

    public boolean isRecipe() {
        return _class == ItemClass.RECIPIES;
    }

    public boolean isTerritoryAccessory() {
        return _itemId >= 13740 && _itemId <= 13748 || _itemId >= 14592 && _itemId <= 14600 || _itemId >= 14664 && _itemId <= 14672 ||
                _itemId >= 14801 && _itemId <= 14809 || _itemId >= 15282 && _itemId <= 15299;
    }

    public boolean isArrow() {
        return type == EtcItemType.ARROW;
    }

    public boolean isBelt() {
        return _bodyPart == SLOT_BELT;
    }

    public boolean isBracelet() {
        return _bodyPart == SLOT_R_BRACELET || _bodyPart == SLOT_L_BRACELET;
    }

    public boolean isUnderwear() {
        return _bodyPart == SLOT_UNDERWEAR;
    }

    public boolean isCloak() {
        return _bodyPart == SLOT_BACK;
    }

    public boolean isTalisman() {
        return _bodyPart == SLOT_DECO;
    }

    public boolean isHerb() {
        return type == EtcItemType.HERB;
    }

    public boolean isAttributeCrystal() {
        return _itemId == 9552 || _itemId == 9553 || _itemId == 9554 || _itemId == 9555 || _itemId == 9556 || _itemId == 9557;
    }

    public boolean isHeroWeapon() {
        return _itemId >= 6611 && _itemId <= 6621 || _itemId >= 9388 && _itemId <= 9390;
    }

    public boolean isCursed() {
        return CursedWeaponsManager.getInstance().isCursed(_itemId);
    }

    public boolean isMercenaryTicket() {
        return type == EtcItemType.MERCENARY_TICKET;
    }

    public boolean isFortressFlag() {
        return _itemId == 9819;
    }

    public boolean isTerritoryFlag() {
        return _itemId == 13560 || _itemId == 13561 || _itemId == 13562 || _itemId == 13563 || _itemId == 13564 || _itemId == 13565 ||
                _itemId == 13566 || _itemId == 13567 || _itemId == 13568;
    }

    public boolean isRod() {
        return getItemType() == WeaponType.ROD;
    }

    public boolean isWeapon() {
        return getType2() == ItemTemplate.TYPE2_WEAPON;
    }

    public boolean isArmor() {
        return getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR;
    }

    public boolean isFullArmor() {
        return _bodyPart == ItemTemplate.SLOT_FULL_ARMOR;
    }

    public boolean isAccessory() {
        return getType2() == ItemTemplate.TYPE2_ACCESSORY;
    }

    public boolean isQuest() {
        return getType2() == ItemTemplate.TYPE2_QUEST;
    }

    public boolean canBeEnchanted() {
        if (isCursed()) {
            return false;
        }
        if (isQuest()) // DS: проверить и убрать
        {
            return false;
        }

        return isEnchantable();
    }

    /**
     * Returns if item is equipable
     *
     * @return boolean
     */
    public boolean isEquipable() {
        return getItemType() == EtcItemType.BAIT || getItemType() == EtcItemType.ARROW || getItemType() == EtcItemType.BOLT || !(getBodyPart() == 0 || this instanceof EtcItemTemplate);
    }

    public boolean testCondition(final Playable player, final ItemInstance instance) {
        if (_condition == null) {
            return true;
        }

        final boolean res = _condition.test(player, instance);
        if (!res && _condition.getSystemMsg() != null) {
            if (_condition.getSystemMsg().size() > 0) {
                player.sendPacket(new SystemMessage(_condition.getSystemMsg()).addItemName(getItemId()));
            } else {
                player.sendPacket(_condition.getSystemMsg());
            }
        }

        return res;
    }

    public void setCondition(final Condition condition) {
        _condition = condition;
    }

    public boolean isEnchantable() {
        return hasFlag(ItemFlags.ENCHANTABLE);
    }

    public boolean isTradeable() {
        return hasFlag(ItemFlags.TRADEABLE);
    }

    public boolean isDestroyable() {
        return hasFlag(ItemFlags.DESTROYABLE);
    }

    public boolean isDropable() {
        return hasFlag(ItemFlags.DROPABLE);
    }

    public final boolean isSellable() {
        return hasFlag(ItemFlags.SELLABLE);
    }

    public final boolean isAugmentable() {
        return hasFlag(ItemFlags.AUGMENTABLE) || isPvp() && AllSettingsConfig.allowAugmentPvp;
    }

    public final boolean isAttributable() {
        return hasFlag(ItemFlags.ATTRIBUTABLE) || isPvp() && AllSettingsConfig.allowAttributePvp;
    }

    public final boolean isStoreable() {
        return hasFlag(ItemFlags.STOREABLE);
    }

    public final boolean isFreightable() {
        return hasFlag(ItemFlags.FREIGHTABLE);
    }

    public boolean hasFlag(final ItemFlags f) {
        return (_flags & f.mask()) == f.mask();
    }

    private void activeFlag(final ItemFlags f) {
        _flags |= f.mask();
    }

    public IItemHandler getHandler() {
        return _handler;
    }

    public void setHandler(final IItemHandler handler) {
        _handler = handler;
    }

    public int getReuseDelay() {
        return _reuseDelay;
    }

    public int getReuseGroup() {
        return _reuseGroup;
    }

    public int getDisplayReuseGroup() {
        return _reuseGroup < 0 ? -1 : _reuseGroup;
    }

    public int getEquipReuseDelay() {
        return _equipReuseDelay;
    }

    public void addEnchantOptions(final int level, final int[] options) {
        if (_enchantOptions.isEmpty())
            _enchantOptions = new HashMap<>();

        _enchantOptions.put(level, options);
    }

    public Map<Integer, int[]> getEnchantOptions() {
        return _enchantOptions;
    }

    public ReuseType getReuseType() {
        return _reuseType;
    }

    public boolean isMagicWeapon() {
        return _isMagicWeapon;
    }

    public boolean isHideConsumeMessage() {
        return _hideConsumeMessage;
    }

    public final List<CapsuledItemsContainer> getCapsuledItems() {
        return _capsuledItems;
    }

    public void addCapsuledItem(CapsuledItemsContainer extractable) {
        if (_capsuledItems == null)
            _capsuledItems = new ArrayList<CapsuledItemsContainer>();

        _capsuledItems.add(extractable);
    }

    public boolean isCapsuled() {
        return _capsuled;
    }

    public boolean isPremium() {
        return _premium;
    }

    public int getPopup() {
        return _popup;
    }

    public VariationData.WeaponType getWeapontType() {
        if (_isMagicWeapon) {
            return VariationData.WeaponType.mage;
        }
        return VariationData.WeaponType.warrior;
    }

    public int getVariationGroupId() {
        return _variationGroupId;
    }

    public void setVariationGroupId(final int variationGroupId) {
        _variationGroupId = variationGroupId;
    }

    // FIXME [Hack]: временный кастыль
    public boolean isPvp() {
        return getName().contains("PvP") || getName().contains("Pvp");
    }

    // FIXME [Hack]: временный кастыль
    public boolean isFoundation() {
        if (isWeapon())
            return getName().contains(" - ");
        else if (isArmor() && !isCloak() && getBodyPart() != ItemTemplate.SLOT_L_HAND)
            return _skills.length > 0;
        else if (isAccessory() && !isEpicJew())
            return _skills.length > 0;
        else
            return false;
    }

    // Йобаный стыд, что же я делаю..
    public boolean isEpicJew() {
        if (isAccessory())
            for (String rgx : epicRegex)
                if (_name.contains(rgx))
                    return true;
        return false;
    }

    public enum ReuseType {
        NORMAL(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME,
                SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME) {
            @Override
            public long next(final ItemInstance item) {
                return System.currentTimeMillis() + item.getTemplate().getReuseDelay();
            }
        },
        EVERY_DAY_AT_6_30(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME,
                SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME,
                SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME) {
            private final CronExpression _pattern = QuartzUtils.createCronExpression("0 30 6 * * ?");

            @Override
            public long next(final ItemInstance item) {
                return _pattern.getNextValidTimeAfter(new Date()).getTime();
            }
        };

        private final SystemMsg[] _messages;

        ReuseType(final SystemMsg... msg) {
            _messages = msg;
        }

        public abstract long next(ItemInstance item);

        public SystemMsg[] getMessages() {
            return _messages;
        }
    }

    public enum ItemClass {
        ALL,
        WEAPON,
        ARMOR,
        JEWELRY,
        ACCESSORY,
        /**
         * Soul/Spiritshot, Potions, Scrolls
         */
        CONSUMABLE,
        /**
         * Common craft matherials
         */
        MATHERIALS,
        /**
         * Special (item specific) craft matherials
         */
        PIECES,
        /**
         * Crafting recipies
         */
        RECIPIES,
        /**
         * Skill learn books
         */
        SPELLBOOKS,
        /**
         * Dyes, lifestones
         */
        MISC,
        /**
         * All other
         */
        OTHER
    }

    public enum Grade {
        NONE(CRYSTAL_NONE, 0, 0),
        D(CRYSTAL_D, 90, 1),
        C(CRYSTAL_C, 45, 2),
        B(CRYSTAL_B, 67, 3),
        A(CRYSTAL_A, 145, 4),
        S(CRYSTAL_S, 250, 5),
        S80(CRYSTAL_S, 250, 5),
        S84(CRYSTAL_S, 250, 5);

        /**
         * ID соответствующего грейду кристалла
         */
        public final int cry;
        public final int cryMod;
        /**
         * ID грейда, без учета уровня S
         */
        public final int externalOrdinal;

        Grade(final int crystal, final int crystalMod, final int ext) {
            cry = crystal;
            cryMod = crystalMod;
            externalOrdinal = ext;
        }
    }
}