package org.mmocore.gameserver.object.components.items;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.jts.dataparser.data.holder.CubicDataHolder;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.jts.dataparser.data.holder.variationdata.VariationData;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.database.dao.JdbcEntity;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.PvpConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneGrade;
import org.mmocore.gameserver.network.lineage.serverpackets.DropItem;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SpawnItem;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.ItemAttachment;
import org.mmocore.gameserver.object.components.items.listeners.ItemEnchantOptionsListener;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.taskmanager.ItemAutoDestroyTaskManager;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate.Grade;
import org.mmocore.gameserver.templates.item.ItemTemplate.ItemClass;
import org.mmocore.gameserver.templates.item.ItemType;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public final class ItemInstance extends GameObject implements JdbcEntity {
    public static final int[] EMPTY_ENCHANT_OPTIONS = new int[3];
    public static final int CHARGED_NONE = 0;
    public static final int CHARGED_SOULSHOT = 1;
    public static final int CHARGED_SPIRITSHOT = 1;
    public static final int CHARGED_BLESSED_SPIRITSHOT = 2;
    public static final int FLAG_NO_DROP = 1 << 0;
    public static final int FLAG_NO_TRADE = 1 << 1;
    public static final int FLAG_NO_TRANSFER = 1 << 2;
    public static final int FLAG_NO_CRYSTALLIZE = 1 << 3;
    public static final int FLAG_NO_ENCHANT = 1 << 4;
    public static final int FLAG_NO_DESTROY = 1 << 5;
    public static final int FLAG_TEMPORAL = 1 << 10;
    public static final int FLAG_COSTUME = 1 << 11;
    private static final long serialVersionUID = 3162753878915133228L;
    /**
     * ID of the owner
     */
    private int ownerId;
    //public static final int FLAG_NO_UNEQUIP = 1 << 6;
    //public static final int FLAG_ALWAYS_DROP_ON_DIE = 1 << 7;
    //public static final int FLAG_EQUIP_ON_PICKUP = 1 << 8;
    //public static final int FLAG_NO_RIDER_PICKUP = 1 << 9;
    //public static final int FLAG_PET_EQUIPPED = 1 << 10;
    /**
     * ID of the item
     */
    private int itemId;
    /**
     * Quantity of the item
     */
    private long count;
    /**
     * Level of enchantment of the item
     */
    private int enchantLevel = -1;
    /**
     * Location of the item
     */
    private ItemLocation loc;
    /**
     * Slot where item is stored
     */
    private int locData;
    /**
     * Custom item types (used loto, race tickets)
     */
    private int customType1;
    private int customType2;
    /**
     * Время жизни временных вещей
     */
    private int lifeTime;
    /**
     * Спецфлаги для конкретного инстанса
     */
    private int customFlags;
    /**
     * Атрибуты вещи
     */
    private ItemAttributes attrs = new ItemAttributes();
    /**
     * Аугментация вещи
     */
    private int[] _enchantOptions = EMPTY_ENCHANT_OPTIONS;
    /**
     * Object L2Item associated to the item
     */
    private ItemTemplate template;
    /**
     * Флаг, что вещь одета, выставляется в инвентаре *
     */
    private boolean isEquipped;
    /**
     * Item drop time for autodestroy task
     */
    private long _dropTime;
    private TIntSet _dropPlayers = TroveUtils.EMPTY_INT_SET;
    private long _dropTimeOwner;
    private int _chargedSoulshot = CHARGED_NONE;
    private int _chargedSpiritshot = CHARGED_NONE;
    private boolean _chargedFishtshot = false;
    private int _agathionEnergy;
    private ItemAttachment _attachment;
    private JdbcEntityState _state = JdbcEntityState.CREATED;
    private Future<?> autoDestroyTask;
    private ScheduledFuture<?> _timerTask;
    private int _variationStoneId;
    private int _variation1Id;
    private int _variation2Id;
    private int agathionMaxEnergy;
    private int visualItemId;
    private int temporaryEnchant = -1;
    private boolean isCostume;
    private boolean isJdbcUnmodifed;

    public ItemInstance(int objectId) {
        super(objectId);
    }

    /**
     * Constructor<?> of the L2ItemInstance from the objectId and the itemId.
     *
     * @param objectId : int designating the ID of the object in the world
     * @param itemId   : int designating the ID of the item
     */
    public ItemInstance(int objectId, int itemId) {
        super(objectId);
        setItemId(itemId);
        setLifeTime(getTemplate().isTemporal() ? (int) (System.currentTimeMillis() / 1000L) + getTemplate().getDurability() * 60 : getTemplate().getDurability());
        if (getAgathionMaxEnergy() <= 0) {
            final Agathion agathion = CubicDataHolder.getInstance().getAgathions().values().stream().filter(a -> a.item_ids.length > 0 && a.item_ids[0] == getItemId()).findFirst().orElse(null);
            if (agathion != null) {
                setAgathionMaxEnergy(agathion.max_energy);
                setAgathionEnergy(agathion.energy);
            }
        }
        setLocData(-1);
        setEnchantLevel(0);
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int id) {
        itemId = id;
        template = ItemTemplateHolder.getInstance().getTemplate(id);
        // TODO: зачем это ?
        // setCustomFlags(getCustomFlags());
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        if (count < 0) {
            count = 0;
        }
        if (!isStackable() && count > 1L) {
            this.count = 1L;
            Log.audit("[ItemInstance]", "PlayerObjId: " + (getPlayer() != null ? getPlayer().getObjectId() : getItemId()) + " seted on not stackable item: " + getItemId() + ", " + count + " count.");
            return;
        }
        this.count = count;
    }

    public int getEnchantLevel() {
        return isTemporaryEnchant() ? temporaryEnchant : enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        final int old = this.enchantLevel;
        this.enchantLevel = enchantLevel;
        if (old != this.enchantLevel && !getTemplate().getEnchantOptions().isEmpty()) {
            Player player = GameObjectsStorage.getPlayer(ownerId);
            if (isEquipped() && player != null) {
                ItemEnchantOptionsListener.getInstance().onUnequip(getEquipSlot(), this, player);
            }
            int[] enchantOptions = getTemplate().getEnchantOptions().get(this.enchantLevel);
            _enchantOptions = enchantOptions == null ? EMPTY_ENCHANT_OPTIONS : enchantOptions;
            if (isEquipped() && player != null) {
                ItemEnchantOptionsListener.getInstance().onEquip(getEquipSlot(), this, player);
            }
        }
    }

    public int getTrueEnchantLevel() {
        return enchantLevel;
    }

    public String getLocName() {
        return loc.name();
    }

    public void setLocName(String loc) {
        this.loc = ItemLocation.valueOf(loc);
    }

    public ItemLocation getLocation() {
        return loc;
    }

    public void setLocation(ItemLocation loc) {
        this.loc = loc;
    }

    public int getLocData() {
        return locData;
    }

    public void setLocData(int locData) {
        this.locData = locData;
    }

    public int getCustomType1() {
        return customType1;
    }

    public void setCustomType1(int newtype) {
        customType1 = newtype;
    }

    public int getCustomType2() {
        return customType2;
    }

    public void setCustomType2(int newtype) {
        customType2 = newtype;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = Math.max(0, lifeTime);
    }

    public int getCustomFlags() {
        return customFlags;
    }

    public void setCustomFlags(int flags) {
        customFlags = flags;
    }

    public ItemAttributes getAttributes() {
        return attrs;
    }

    public void setAttributes(ItemAttributes attrs) {
        this.attrs = attrs;
    }

    public int getShadowLifeTime() {
        if (!isShadowItem()) {
            return 0;
        }
        return getLifeTime();
    }

    public int getTemporalLifeTime() {
        if (!isTemporalItem()) {
            return 0;
        }
        return getLifeTime() - (int) (System.currentTimeMillis() / 1000L);
    }

    public void startTimer(Runnable r) {
        _timerTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(r, 0, 60000L);
    }

    public void stopTimer() {
        if (_timerTask != null) {
            _timerTask.cancel(false);
            _timerTask = null;
        }
    }

    /**
     * Returns if item is equipable
     *
     * @return boolean
     */
    public boolean isEquipable() {
        return template.isEquipable();
    }

    /**
     * Returns if item is equipped
     *
     * @return boolean
     */
    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public void reEquip(Player player) {
        if (this.isEquipped()) {
            player.getInventory().unEquipItem(this);
            player.getInventory().equipItem(this);
        }
    }

    public int getBodyPart() {
        return template.getBodyPart();
    }

    /**
     * Returns the slot where the item is stored
     *
     * @return int
     */
    public int getEquipSlot() {
        return getLocData();
    }

    /**
     * Returns the characteristics of the item
     *
     * @return L2Item
     */
    public ItemTemplate getTemplate() {
        return template;
    }

    public void setDropTime(long time) {
        _dropTime = time;
    }

    public long getLastDropTime() {
        return _dropTime;
    }

    public long getDropTimeOwner() {
        return _dropTimeOwner;
    }

    /**
     * Returns the type of item
     *
     * @return Enum
     */
    public ItemType getItemType() {
        return template.getItemType();
    }

    public boolean isArmor() {
        return template.isArmor();
    }

    public boolean isAccessory() {
        return template.isAccessory();
    }

    public boolean isWeapon() {
        return template.isWeapon();
    }

    /**
     * Returns the reference price of the item
     *
     * @return int
     */
    public int getReferencePrice() {
        return AllSettingsConfig.allItemsForOneAdena ? 1 : template.getReferencePrice();
    }

    /**
     * Returns if item is stackable
     *
     * @return boolean
     */
    public boolean isStackable() {
        return template.isStackable();
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, ItemInstance.class, this, false)) {
            return;
        }
        if (player.isCursedWeaponEquipped() && CursedWeaponsManager.getInstance().isCursed(itemId)) {
            return;
        }
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this, null);
    }

    public boolean isAugmented() {
        return getVariation1Id() != 0 || getVariation2Id() != 0;
    }

    public boolean isPopup() {
        return getPopup() != 0;
    }

    public int getPopup() {
        return template.getPopup();
    }

    /**
     * Returns the type of charge with SoulShot of the item.
     *
     * @return int (CHARGED_NONE, CHARGED_SOULSHOT)
     */
    public int getChargedSoulshot() {
        return _chargedSoulshot;
    }

    /**
     * Sets the type of charge with SoulShot of the item
     *
     * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
     */
    public void setChargedSoulshot(int type) {
        _chargedSoulshot = type;
    }

    /**
     * Returns the type of charge with SpiritShot of the item
     *
     * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
     */
    public int getChargedSpiritshot() {
        return _chargedSpiritshot;
    }

    /**
     * Sets the type of charge with SpiritShot of the item
     *
     * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
     */
    public void setChargedSpiritshot(int type) {
        _chargedSpiritshot = type;
    }

    public boolean getChargedFishshot() {
        return _chargedFishtshot;
    }

    public void setChargedFishshot(boolean type) {
        _chargedFishtshot = type;
    }

    /**
     * This function basically returns a set of functions from
     * L2Item/L2Armor/L2Weapon, but may add additional
     * functions, if this particular item instance is enhanched
     * for a particular player.
     *
     * @return Func[]
     */
    public Func[] getStatFuncs() {
        final FuncTemplate[] attachedFuncs = template.getAttachedFuncs();
        final List<Func> funcs = new LazyArrayList<>(attachedFuncs.length + Element.VALUES.length);
        if (attachedFuncs.length > 0) {
            for (FuncTemplate t : attachedFuncs) {
                Func f = t.getFunc(this);
                if (f != null) {
                    funcs.add(f);
                }
            }
        }
        for (Element e : Element.VALUES) {
            if (isWeapon()) {
                funcs.add(new FuncAttack(e, 0x40, this));
            }
            if (isArmor()) {
                funcs.add(new FuncDefence(e, 0x40, this));
            }
        }
        Func[] result = Func.EMPTY_FUNC_ARRAY;
        if (!funcs.isEmpty()) {
            result = funcs.toArray(new Func[funcs.size()]);
        }
        funcs.clear();
        return result;
    }

    /**
     * Return true if item is hero-item
     *
     * @return boolean
     */
    public boolean isHeroWeapon() {
        return template.isHeroWeapon();
    }

    /**
     * Return true if item can be destroyed
     */
    public boolean canBeDestroyed(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) {
            return false;
        }
        if (isHeroWeapon()) {
            return false;
        }
        if (PetDataHolder.getInstance().isPetControlItem(getItemId()) && player.isMounted()) {
            return false;
        }
        if (player.getPetControlItem() == this) {
            return false;
        }
        if (player.getEnchantScroll() == this) {
            return false;
        }
        if (isCursed()) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.isDestroyable();
    }

    /**
     * Return true if item can be dropped
     */
    public boolean canBeDropped(Player player, boolean pk) {
        if (player.isGM()) {
            boolean isDropable = true;
            if ((customFlags & FLAG_NO_DROP) == FLAG_NO_DROP) {
                isDropable = false;
            } else if (isShadowItem()) {
                isDropable = false;
            } else if (isTemporalItem()) {
                isDropable = false;
            } else if (isAugmented() && (!pk || !PvpConfig.DROP_ITEMS_AUGMENTED) && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) {
                isDropable = false;
            } else if (!ItemFunctions.checkIfCanDiscard(player, this)) {
                isDropable = false;
            }
            if (!isDropable || !template.isDropable()) {
                player.sendAdminMessage("You now droped NON_DROPABLE item, be careful! ItemId: " + getItemId());
            }
            return true;
        }
        if ((customFlags & FLAG_NO_DROP) == FLAG_NO_DROP) {
            return false;
        }
        if (isShadowItem()) {
            return false;
        }
        if (isTemporalItem()) {
            return false;
        }
        if (isAugmented() && (!pk || !PvpConfig.DROP_ITEMS_AUGMENTED) /* && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED */) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.isDropable();
    }

    public boolean canBeTraded(Player player) {
        if (isEquipped()) {
            return false;
        }
        if (player.isGM()) {
            return true;
        }
        if ((customFlags & FLAG_NO_TRADE) == FLAG_NO_TRADE) {
            return false;
        }
        if (isShadowItem()) {
            return false;
        }
        if (isTemporalItem()) {
            return false;
        }
        if (isAugmented() && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        if (LostDreamCustom.allowTradePvp && getTemplate() != null && getTemplate().isPvp())
            return true;
        return template.isTradeable();
    }

    /**
     * Можно ли продать в магазин NPC
     */
    public boolean canBeSold(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) {
            return false;
        }
        if (getItemId() == ItemTemplate.ITEM_ID_ADENA) {
            return false;
        }
        if (template.getReferencePrice() == 0) {
            return false;
        }
        if (isShadowItem()) {
            return false;
        }
        if (isTemporalItem()) {
            return false;
        }
        if (isAugmented() && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) {
            return false;
        }
        if (isEquipped()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.isSellable();
    }

    /**
     * Можно ли положить на клановый склад
     */
    public boolean canBeStored(Player player, boolean privatewh) {
        if ((customFlags & FLAG_NO_TRANSFER) == FLAG_NO_TRANSFER) {
            return false;
        }
        if (!getTemplate().isStoreable()) {
            return false;
        }
        if (!privatewh && (isShadowItem() || isTemporalItem())) {
            return false;
        }
        if (!privatewh && isAugmented() && !AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) {
            return false;
        }
        if (isEquipped()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return privatewh || template.isTradeable();
    }

    public boolean canBeCrystallized(Player player) {
        if ((customFlags & FLAG_NO_CRYSTALLIZE) == FLAG_NO_CRYSTALLIZE) {
            return false;
        }
        if (isShadowItem()) {
            return false;
        }
        if (isTemporalItem()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.isCrystallizable();
    }

    public boolean canBeEnchanted() {
        if ((customFlags & FLAG_NO_ENCHANT) == FLAG_NO_ENCHANT) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.canBeEnchanted();
    }

    public boolean canBeAugmented(Player player, LifeStoneGrade lsg) {
        if (!canBeEnchanted())
            return false;
        if (isAugmented())
            return false;
        if (isCommonItem())
            return false;
        if (isTerritoryAccessory())
            return false;
        if (getTemplate().getItemGrade().ordinal() < Grade.C.ordinal())
            return false;
        if (!getTemplate().isAugmentable())
            return false;
        if (isAccessory())
            return lsg == LifeStoneGrade.ACCESSORY;
        if (isArmor())
            return false;
        if (isWeapon())
            return (lsg != LifeStoneGrade.ACCESSORY && lsg != LifeStoneGrade.UNDERWEAR);
        if (isTemporaryEnchant())
            return false;
        return true;
    }

    public boolean canBeExchanged(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY) {
            return false;
        }
        if (isShadowItem()) {
            return false;
        }
        if (isTemporalItem()) {
            return false;
        }
        if (!ItemFunctions.checkIfCanDiscard(player, this)) {
            return false;
        }
        if (isTemporaryEnchant())
            return false;
        return template.isDestroyable();
    }

    public boolean isTerritoryAccessory() {
        return template.isTerritoryAccessory();
    }

    public boolean isShadowItem() {
        return template.isShadowItem();
    }

    public boolean isTemporalItem() {
        return template.isTemporal();
    }

    public boolean isCommonItem() {
        return template.isCommonItem();
    }

    public boolean isAltSeed() {
        return template.isAltSeed();
    }

    public boolean isCursed() {
        return template.isCursed();
    }

    /**
     * Бросает на землю лут с NPC
     */
    public void dropToTheGround(Player lastAttacker, NpcInstance fromNpc) {
        Creature dropper = fromNpc;
        if (dropper == null) {
            dropper = lastAttacker;
        }
        Location pos = Location.findAroundPosition(dropper, 100);
        // activate non owner penalty
        if (lastAttacker != null) // lastAttacker в данном случае top damager
        {
            _dropPlayers = new TIntHashSet(1, 2.0F);
            for (Player member : lastAttacker.getPlayerGroup()) {
                _dropPlayers.add(member.getObjectId());
            }
            _dropTimeOwner = System.currentTimeMillis() + AllSettingsConfig.NONOWNER_ITEM_PICKUP_DELAY + (fromNpc != null && fromNpc.isRaid() ? 285000 : 0);
        }
        // Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
        dropMe(dropper, pos);
    }

    /**
     * Бросает вещь на землю туда, где ее можно поднять
     */
    public void dropToTheGround(Creature dropper, Location dropPos) {
        if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex())) {
            dropMe(dropper, dropPos);
        } else {
            dropMe(dropper, dropper.getLoc());
        }
    }

    /**
     * Бросает вещь на землю из инвентаря туда, где ее можно поднять
     */
    public void dropToTheGround(Playable dropper, Location dropPos) {
        setLocation(ItemLocation.VOID);
        if (getJdbcState().isPersisted()) {
            setJdbcState(JdbcEntityState.UPDATED);
            update();
        }
        if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex())) {
            dropMe(dropper, dropPos);
        } else {
            dropMe(dropper, dropper.getLoc());
        }
    }

    /**
     * Init a dropped L2ItemInstance and add it in the world as a visible object.<BR><BR>
     * <p>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion </li>
     * <li>Add the L2ItemInstance dropped to _visibleObjects of its L2WorldRegion</li>
     * <li>Add the L2ItemInstance dropped in the world as a <B>visible</B> object</li><BR><BR>
     * <p>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of L2World </B></FONT><BR><BR>
     * <p>
     * <B><U> Assert </U> :</B><BR><BR>
     * <li> this instanceof L2ItemInstance</li>
     * <li> _worldRegion == null <I>(L2Object is invisible at the beginning)</I></li><BR><BR>
     * <p>
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Drop item</li>
     * <li> Call Pet</li><BR>
     *
     * @param dropper Char that dropped item
     * @param loc     drop coordinates
     */
    public void dropMe(Creature dropper, Location loc) {
        dropMe(dropper, loc, true);
    }

    public void dropMe(Creature dropper, Location loc, final boolean autoDestroy) {
        if (dropper != null) {
            setReflection(dropper.getReflection());
        }
        spawnMe0(loc, dropper);
        if (autoDestroy) {
            // Add drop to auto destroy item task
            if (isHerb() || ServerConfig.AUTODESTROY_ITEM_AFTER > 0 && !isCursed() && !getTemplate().isFortressFlag())
                startAutoDestroyTask(dropper);
        }
    }

    public final void pickupMe() {
        stopAutoDestroyTask();
        decayMe();
        setReflection(ReflectionManager.DEFAULT);
    }

    public ItemClass getItemClass() {
        return template.getItemClass();
    }

    /**
     * Возвращает защиту от элемента.
     *
     * @return значение защиты
     */
    public int getDefence(Element element) {
        return isArmor() ? getAttributeElementValue(element, true) : 0;
    }

    /**
     * Возвращает защиту от элемента: огонь.
     *
     * @return значение защиты
     */
    public int getDefenceFire() {
        return getDefence(Element.FIRE);
    }

    /**
     * Возвращает защиту от элемента: вода.
     *
     * @return значение защиты
     */
    public int getDefenceWater() {
        return getDefence(Element.WATER);
    }

    /**
     * Возвращает защиту от элемента: воздух.
     *
     * @return значение защиты
     */
    public int getDefenceWind() {
        return getDefence(Element.WIND);
    }

    /**
     * Возвращает защиту от элемента: земля.
     *
     * @return значение защиты
     */
    public int getDefenceEarth() {
        return getDefence(Element.EARTH);
    }

    /**
     * Возвращает защиту от элемента: свет.
     *
     * @return значение защиты
     */
    public int getDefenceHoly() {
        return getDefence(Element.HOLY);
    }

    /**
     * Возвращает защиту от элемента: тьма.
     *
     * @return значение защиты
     */
    public int getDefenceUnholy() {
        return getDefence(Element.UNHOLY);
    }

    /**
     * Возвращает значение элемента.
     *
     * @return
     */
    public int getAttributeElementValue(Element element, boolean withBase) {
        return attrs.getValue(element) + (withBase ? template.getBaseAttributeValue(element) : 0);
    }

    /**
     * Возвращает элемент атрибуции предмета.<br>
     */
    public Element getAttributeElement() {
        return attrs.getElement();
    }

    public int getAttributeElementValue() {
        return attrs.getValue();
    }

    public Element getAttackElement() {
        Element element = isWeapon() ? getAttributeElement() : Element.NONE;
        if (element == Element.NONE) {
            for (Element e : Element.VALUES) {
                if (template.getBaseAttributeValue(e) > 0) {
                    return e;
                }
            }
        }
        return element;
    }

    public int getAttackElementValue() {
        return isWeapon() ? getAttributeElementValue(getAttackElement(), true) : 0;
    }

    /**
     * Устанавливает элемент атрибуции предмета.<br>
     * Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -1 - None)
     *
     * @param element элемент
     * @param value
     */
    public void setAttributeElement(Element element, int value) {
        attrs.setValue(element, value);
    }

    /**
     * Проверяет, является ли данный инстанс предмета хербом
     *
     * @return true если предмет является хербом
     */
    public boolean isHerb() {
        return getTemplate().isHerb();
    }

    public Grade getCrystalType() {
        return template.getCrystalType();
    }

    @Override
    public String getName() {
        return getTemplate().getName();
    }

    @Override
    public void save() {
        if (getPlayer() != null && getPlayer().isPhantom())
            return;
        ItemsDAO.getInstance().save(this);
    }

    @Override
    public void update() {
        ItemsDAO.getInstance().update(this);
    }

    @Override
    public void delete() {
        ItemsDAO.getInstance().delete(this);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        //FIXME кажись дроппер у нас есть в итеме как переменная, ток проверить время? [VISTALL]
        L2GameServerPacket packet = null;
        if (dropper != null) {
            packet = new DropItem(this, dropper.getObjectId());
        } else {
            packet = new SpawnItem(this);
        }
        return Collections.singletonList(packet);
    }

    /**
     * Returns the item in String format
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTemplate().getItemId());
        sb.append(' ');
        if (getEnchantLevel() > 0) {
            sb.append('+');
            sb.append(getEnchantLevel());
            sb.append(' ');
        }
        sb.append(getTemplate().getName());
        if (!getTemplate().getAdditionalName().isEmpty()) {
            sb.append(' ');
            sb.append('\\').append(getTemplate().getAdditionalName()).append('\\');
        }
        sb.append(' ');
        sb.append('(');
        sb.append(getCount());
        sb.append(')');
        sb.append('[');
        sb.append(getObjectId());
        sb.append(']');
        return sb.toString();
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return _state;
    }

    @Override
    public void setJdbcState(JdbcEntityState state) {
        if (isJdbcUnmodifed())
            return;
        _state = state;
    }

    public void setPhantomItem() {
        setJdbcState(JdbcEntityState.DELETED);
        setJdbcUnmodifed(true);
    }

    @Override
    public boolean isItem() {
        return true;
    }

    public ItemAttachment getAttachment() {
        return _attachment;
    }

    public void setAttachment(ItemAttachment attachment) {
        ItemAttachment old = _attachment;
        _attachment = attachment;
        if (_attachment != null) {
            _attachment.setItem(this);
        }
        if (old != null) {
            old.setItem(null);
        }
    }

    public int getAgathionEnergy() {
        return _agathionEnergy;
    }

    public void setAgathionEnergy(int agathionEnergy) {
        _agathionEnergy = Math.max(0, agathionEnergy);
    }

    public int[] getEnchantOptions() {
        if (isTemporaryEnchant()) {
            int[] enchantOptions = getTemplate().getEnchantOptions().get(temporaryEnchant);
            return enchantOptions == null ? EMPTY_ENCHANT_OPTIONS : enchantOptions;
        } else {
            return _enchantOptions;
        }
    }

    public TIntSet getDropPlayers() {
        return _dropPlayers;
    }

    private void startAutoDestroyTask(Creature dropper) {
        if (autoDestroyTask == null)
            autoDestroyTask = ItemAutoDestroyTaskManager.getInstance().addAutoDestroyTask(this, dropper);
    }

    private void stopAutoDestroyTask() {
        if (autoDestroyTask != null)
            autoDestroyTask.cancel(false);
    }

    public int getAgathionMaxEnergy() {
        return agathionMaxEnergy;
    }

    public void setAgathionMaxEnergy(final int agathionMaxEnergy) {
        this.agathionMaxEnergy = agathionMaxEnergy;
    }

    public int getVariationStoneId() {
        return _variationStoneId;
    }

    public void setVariationStoneId(final int stoneId) {
        _variationStoneId = stoneId;
    }

    public int getVariation1Id() {
        return _variation1Id;
    }

    public void setVariation1Id(final int val) {
        _variation1Id = val;
    }

    public int getVariation2Id() {
        return _variation2Id;
    }

    public void setVariation2Id(final int val) {
        _variation2Id = val;
    }

    public VariationData.WeaponType getWeapontType() {
        return template.getWeapontType();
    }

    public int getVisualItemId() {
        return visualItemId;
    }

    public void setVisualItemId(int visualItemId) {
        this.visualItemId = visualItemId;
    }

    public boolean isCostume() {
        return isCostume;
    }

    public void setCostume(boolean costume) {
        isCostume = costume;
    }

    public int getTemporaryEnchant() {
        return temporaryEnchant;
    }

    public boolean isTemporaryEnchant() {
        return temporaryEnchant != -1;
    }

    public void setTemporaryEnchant(int temporaryEnchant) {
        this.temporaryEnchant = temporaryEnchant;
    }

    public boolean isJdbcUnmodifed() {
        return isJdbcUnmodifed;
    }

    public void setJdbcUnmodifed(boolean jdbcUnmodifed) {
        isJdbcUnmodifed = jdbcUnmodifed;
    }

    public boolean isVisualItem() {
        return getVisualItemId() != 0;
    }

    /**
     * Enumeration of locations for item
     */
    public enum ItemLocation {
        VOID,
        INVENTORY,
        PAPERDOLL,
        PET_INVENTORY,
        PET_PAPERDOLL,
        WAREHOUSE,
        CLANWH,
        FREIGHT, // востановлен, используется в Dimension Manager
        MAIL
    }

    public class FuncAttack extends Func {
        private final Element element;

        public FuncAttack(Element element, int order, Object owner) {
            super(element.getAttack(), order, owner);
            this.element = element;
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue + getAttributeElementValue(element, true);
        }
    }

    public class FuncDefence extends Func {
        private final Element element;

        public FuncDefence(Element element, int order, Object owner) {
            super(element.getDefence(), order, owner);
            this.element = element;
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue + getAttributeElementValue(element, true);
        }
    }
}