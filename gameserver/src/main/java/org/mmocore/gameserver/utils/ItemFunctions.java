package org.mmocore.gameserver.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.items.attachments.PickableAttachment;
import org.mmocore.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

public final class ItemFunctions {
    public static final int[][] catalyst = {
            // enchant catalyst list
            {12362, 14078, 14702}, // 0 - W D
            {12363, 14079, 14703}, // 1 - W C
            {12364, 14080, 14704}, // 2 - W B
            {12365, 14081, 14705}, // 3 - W A
            {12366, 14082, 14706}, // 4 - W S
            {12367, 14083, 14707}, // 5 - A D
            {12368, 14084, 14708}, // 6 - A C
            {12369, 14085, 14709}, // 7 - A B
            {12370, 14086, 14710}, // 8 - A A
            {12371, 14087, 14711}, // 9 - A S
    };

    private ItemFunctions() {
    }

    public static ItemInstance createItem(final int itemId) {
        final ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
        item.setLocation(ItemLocation.VOID);
        item.setCount(1L);

        return item;
    }

    public static boolean isArrow(final int itemId) {
        return itemId == 17 || (itemId >= 1341 && itemId <= 1345) || (itemId >= 22067 && itemId <= 22071);
    }

    /**
     * Добавляет предмет в инвентарь игрока, корректно обрабатывает нестыкуемые вещи
     *
     * @param playable Владелец инвентаря
     * @param itemId   ID предмета
     * @param count    количество
     */
    public static void addItem(final Playable playable, final int itemId, final long count) {
        addItem(playable, itemId, count, true);
    }

    /**
     * Добавляет предмет в инвентарь игрока, корректно обрабатывает нестыкуемые вещи
     *
     * @param playable Владелец инвентаря
     * @param itemId   ID предмета
     * @param count    количество
     */
    public static void addItem(final Playable playable, final int itemId, final long count, final boolean notify) {
        if (playable == null || count < 1) {
            return;
        }

        final Playable player;
        if (playable.isSummon()) {
            player = playable.getPlayer();
        } else {
            player = playable;
        }

        final ItemTemplate t = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (t.isStackable()) {
            player.getInventory().addItem(itemId, count);
        } else {
            for (long i = 0; i < count; i++) {
                player.getInventory().addItem(itemId, 1);
            }
        }

        if (notify) {
            player.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
        }
    }

    /**
     * Возвращает количество предметов в инвентаре игрока
     *
     * @param playable Владелец инвентаря
     * @param itemId   ID предмета
     * @return количество
     */
    public static long getItemCount(final Playable playable, final int itemId) {
        if (playable == null) {
            return 0;
        }
        final Playable player = playable.getPlayer();
        return player.getInventory().getCountOf(itemId);
    }

    /**
     * Удаляет по одному указанному предмету из инвентаря игрока, корректно обрабатывает нестыкуемые предметы.
     *
     * @param playable Владелец инвентаря
     * @param itemIds  ID предметов
     */
    public static void removeItems(final Playable playable, final int... itemIds) {
        for (final int itemId : itemIds) {
            removeItem(playable, itemId, 1L, true);
        }
    }

    /**
     * Удаляет предметы из инвентаря игрока, корректно обрабатывает нестыкуемые предметы
     *
     * @param playable Владелец инвентаря
     * @param itemId   ID предмета
     * @param count    количество
     * @return количество удаленных
     */
    public static long removeItem(final Playable playable, final int itemId, final long count) {
        return removeItem(playable, itemId, count, true);
    }

    /**
     * Удаляет предметы из инвентаря игрока, корректно обрабатывает нестыкуемые предметы
     *
     * @param playable Владелец инвентаря
     * @param itemId   ID предмета
     * @param count    количество
     * @param notify   оповестить игрока системным сообщением
     * @return количество удаленных
     */
    public static long removeItem(final Playable playable, final int itemId, final long count, final boolean notify) {
        long removed = 0;
        if (playable == null || count < 1) {
            return removed;
        }

        final Playable player = playable.getPlayer();

        final ItemTemplate t = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (t.isStackable()) {
            if (player.getInventory().destroyItemByItemId(itemId, count)) {
                removed = count;
            }
        } else {
            for (long i = 0; i < count; i++) {
                if (player.getInventory().destroyItemByItemId(itemId, 1)) {
                    removed++;
                }
            }
        }

        if (removed > 0 && notify) {
            player.sendPacket(SystemMessage.removeItems(itemId, removed));
        }

        return removed;
    }

    public static boolean isClanApellaItem(final int itemId) {
        return itemId >= 7860 && itemId <= 7879 || itemId >= 9830 && itemId <= 9839;
    }

    public static SystemMsg checkIfCanEquip(final PetInstance pet, final ItemInstance item) {
        if (!item.isEquipable()) {
            return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;
        }

        final int petId = pet.getNpcId();

        if (item.getTemplate().isPendant() //
                || PetUtils.isWolf(petId) && item.getTemplate().isForWolf() //
                || PetUtils.isHatchling(petId) && item.getTemplate().isForHatchling() //
                || PetUtils.isStrider(petId) && item.getTemplate().isForStrider() //
                || PetUtils.isGWolf(petId) && item.getTemplate().isForGWolf() //
                || PetUtils.isBabyPet(petId) && item.getTemplate().isForPetBaby() //
                || PetUtils.isImprovedBabyPet(petId) && item.getTemplate().isForPetBaby() //
                ) {
            return null;
        }

        return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;
    }

    /**
     * Проверяет возможность носить эту вещь.
     *
     * @return null, если вещь носить можно, либо SystemMessage, который можно показать игроку
     */
    public static IBroadcastPacket checkIfCanEquip(final Player player, final ItemInstance item) {
        //FIXME [G1ta0] черезмерный хардкод, переделать на условия
        final int itemId = item.getItemId();
        final int targetSlot = item.getTemplate().getBodyPart();
        final Clan clan = player.getClan();

        // Геройское оружие и Wings of Destiny Circlet
        if ((item.isHeroWeapon() || item.getItemId() == 6842) && (!player.isHero() && !player.getCustomPlayerComponent().isTemporalHero())) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        // камаэли и хеви/робы/щиты/сигилы
        if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael &&
                (item.getItemType() == ArmorType.HEAVY || item.getItemType() == ArmorType.MAGIC || item.getItemType() == ArmorType.SIGIL ||
                        item.getItemType() == WeaponType.NONE)) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        // не камаэли и рапиры/арбалеты/древние мечи
        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael &&
                (item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.RAPIER || item.getItemType() == WeaponType.ANCIENTSWORD)) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        if (itemId >= 7850 && itemId <= 7859 && player.getLvlJoinedAcademy() == 0) // Clan Oath Armor
        {
            return SystemMsg.THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY;
        }

        if (isClanApellaItem(itemId) && player.getPledgeClass() < Player.RANK_WISEMAN) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        if (item.getItemType() == WeaponType.DUALDAGGER && player.getSkillLevel(923) < 1) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        // Замковые короны, доступные для всех членов клана
        if (ArrayUtils.contains(ItemTemplate.ITEM_ID_CASTLE_CIRCLET, itemId) &&
                (clan == null || itemId != ItemTemplate.ITEM_ID_CASTLE_CIRCLET[clan.getCastle()])) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        // Корона лидера клана, владеющего замком
        if (itemId == 6841 && (clan == null || !player.isClanLeader() || clan.getCastle() == 0)) {
            return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
        }

        // Нельзя одевать оружие, если уже одето проклятое оружие. Проверка двумя способами, для надежности.
        if (targetSlot == ItemTemplate.SLOT_LR_HAND || targetSlot == ItemTemplate.SLOT_L_HAND || targetSlot == ItemTemplate.SLOT_R_HAND) {
            if (itemId != player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) && CursedWeaponsManager.getInstance().isCursed(
                    player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND))) {
                return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
            }
            if (player.isCursedWeaponEquipped() && itemId != player.getCursedWeaponEquippedId()) {
                return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
            }
        }

        // Плащи
        if (item.getTemplate().isCloak()) {
            // Can be worn by Knights or higher ranks who own castle
            if (item.getName().contains("Knight") && (player.getPledgeClass() < Player.RANK_KNIGHT || player.getCastle() == null)) {
                return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
            }

            // Плащи для камаэлей
            if (item.getName().contains("Kamael") && player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael) {
                return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
            }

            // Плащи можно носить только с S80 или S84 сетом
            if (!player.getOpenCloak()) {
                return SystemMsg.THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NOT_COMPLETE;
            }
        }

        if (targetSlot == ItemTemplate.SLOT_DECO) {
            int count = player.getTalismanCount();
            if (count <= 0) {
                return new SystemMessage(SystemMsg.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(itemId);
            }

            ItemInstance deco;
            for (int slot = Inventory.PAPERDOLL_DECO1; slot <= Inventory.PAPERDOLL_DECO6; slot++) {
                deco = player.getInventory().getPaperdollItem(slot);
                if (deco != null) {
                    if (deco == item) {
                        return null; // талисман уже одет и количество слотов больше нуля
                    }
                    // Проверяем на количество слотов и одинаковые талисманы
                    if (--count <= 0 || deco.getItemId() == itemId) {
                        return new SystemMessage(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId);
                    }
                }
            }
        }
        return null;
    }

    public static boolean checkIfCanPickup(final Playable playable, final ItemInstance item) {
        final Player player = playable.getPlayer();
        return item.getDropTimeOwner() <= System.currentTimeMillis() || item.getDropPlayers().contains(player.getObjectId());
    }

    public static boolean canAddItem(final Player player, final ItemInstance item) {
        if (!player.getInventory().validateWeight(item)) {
            player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return false;
        }

        if (!player.getInventory().validateCapacity(item)) {
            player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            return false;
        }

        if (!item.getTemplate().getHandler().pickupItem(player, item)) {
            return false;
        }

        final PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;
        if (attachment != null && !attachment.canPickUp(player)) {
            return false;
        }

        return true;
    }

    /**
     * Проверяет возможность передачи вещи
     *
     * @param player
     * @param item
     * @return
     */
    public static boolean checkIfCanDiscard(final Player player, final ItemInstance item) {
        if (item.isHeroWeapon()) {
            return false;
        }

        if (PetDataHolder.getInstance().isPetControlItem(item.getItemId()) && player.isMounted()) {
            return false;
        }

        if (PetDataHolder.getInstance().isPetControlItem(item.getItemId())) {
            for (final ItemInstance itemsPet : ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(player.getObjectId(), ItemLocation.PET_INVENTORY)) //FIXME[K] - возможно стоит выполнять в синхронизации с инвентарем.
            {
                if (itemsPet.getLocation() == ItemLocation.PET_INVENTORY) {
                    return false;
                }
            }
        }

        if (player.getPetControlItem() == item) {
            return false;
        }

        if (player.getEnchantScroll() == item) {
            return false;
        }

        if (item.isCursed()) {
            return false;
        }

        if (item.getTemplate().isQuest()) {
            return false;
        }

        return true;
    }

    public static int[] getEnchantCatalystId(final ItemInstance item) {
        if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_A:
                    return catalyst[3];
                case ItemTemplate.CRYSTAL_B:
                    return catalyst[2];
                case ItemTemplate.CRYSTAL_C:
                    return catalyst[1];
                case ItemTemplate.CRYSTAL_D:
                    return catalyst[0];
                case ItemTemplate.CRYSTAL_S:
                    return catalyst[4];
            }
        } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) {
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_A:
                    return catalyst[8];
                case ItemTemplate.CRYSTAL_B:
                    return catalyst[7];
                case ItemTemplate.CRYSTAL_C:
                    return catalyst[6];
                case ItemTemplate.CRYSTAL_D:
                    return catalyst[5];
                case ItemTemplate.CRYSTAL_S:
                    return catalyst[9];
            }
        }
        return new int[]{0, 0, 0};
    }

    public static double getCatalystPower(final int itemId) {
        for (int i = 0; i < catalyst.length; i++) {
            for (final int id : catalyst[i]) {
                if (id == itemId) {
                    switch (i) {
                        case 0:
                            return 0.20;
                        case 1:
                            return 0.18;
                        case 2:
                            return 0.15;
                        case 3:
                            return 0.12;
                        case 4:
                            return 0.10;
                        case 5:
                            return 0.35;
                        case 6:
                            return 0.27;
                        case 7:
                            return 0.23;
                        case 8:
                            return 0.18;
                        case 9:
                            return 0.15;
                    }
                }
            }
        }

        return 0;
    }

    /**
     * Проверяет соответствие уровня заточки и вообще катализатор ли это или левый итем
     *
     * @param item
     * @param catalyst
     * @return true если катализатор соответствует
     */
    public static boolean checkCatalyst(final ItemInstance item, final ItemInstance catalyst) {
        if (item == null || catalyst == null) {
            return false;
        }

        final int current = item.getEnchantLevel();
        if (current < (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? 4 : 3) || current > 8) {
            return false;
        }

        for (final int catalystRequired : getEnchantCatalystId(item)) {
            if (catalystRequired == catalyst.getItemId()) {
                return true;
            }
        }

        return false;
    }
}
