package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.MultiSellListContainer;
import org.mmocore.gameserver.model.base.MultiSellEntry;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemAttributes;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class RequestMultiSellChoose extends L2GameClientPacket {
    private int _listId;
    private int _entryId;
    private long _amount;

    @Override
    protected void readImpl() {
        _listId = readD();
        _entryId = readD();
        _amount = readQ();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _amount < 1) {
            return;
        }

        final MultiSellListContainer list1 = activeChar.getMultisell();
        if (list1 == null) {
            activeChar.sendActionFailed();
            activeChar.setMultisell(null);
            return;
        }

        if (activeChar.isGM())
            activeChar.sendAdminMessage("Multisell: " + list1.getListId() + ".xml");

        // Проверяем, не подменили ли id
        if (list1.getListId() != _listId) {
            Log.audit("[MultiSellChoose]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " changed list id, m.b used PH!");
            activeChar.sendActionFailed();
            activeChar.setMultisell(null);
            return;
        }

        if (list1.getNpcObjectId() > 0) {
            NpcInstance npc = activeChar.getLastNpc();
            final GameObject target = activeChar.getTarget();
            if (npc == null && target != null && target.isNpc()) {
                npc = (NpcInstance) target;
            }

            // Не тот NPC или слишком далеко
            if (npc == null || npc.getObjectId() != list1.getNpcObjectId() || !activeChar.isInRangeZ(npc, activeChar.getInteractDistance(npc))) {
                activeChar.sendActionFailed();
                activeChar.setMultisell(null);
                return;
            }
        } else
            // Запрещенный мультиселл из BBS (без NPC)
            if (!list1.isBBSAllowed()) {
                activeChar.sendActionFailed();
                activeChar.setMultisell(null);
                return;
            }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        MultiSellEntry entry = null;
        for (final MultiSellEntry $entry : list1.getEntries()) {
            if ($entry.getEntryId() == _entryId) {
                entry = $entry;
                break;
            }
        }

        if (entry == null) {
            return;
        }

        final boolean keepenchant = list1.isKeepEnchant();
        final boolean notax = list1.isNoTax();
        final List<ItemData> items = new ArrayList<>();

        final PcInventory inventory = activeChar.getInventory();

        long totalPrice = 0;

        final NpcInstance merchant = activeChar.getLastNpc();
        final Castle castle = merchant != null ? merchant.getCastle(activeChar) : null;

        inventory.writeLock();
        try {
            final long tax = Math.multiplyExact(entry.getTax(), _amount);

            long slots = 0;
            long weight = 0;
            for (final MultiSellIngredient i : entry.getProduction()) {
                if (i.getItemId() <= 0) {
                    continue;
                }
                final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(i.getItemId());

                weight = Math.addExact(weight, Math.multiplyExact(Math.multiplyExact(i.getItemCount(), _amount), item.getWeight()));
                if (item.isStackable()) {
                    if (inventory.getItemByItemId(i.getItemId()) == null) {
                        slots++;
                    }
                } else {
                    slots = Math.addExact(slots, _amount);
                }
            }

            if (!inventory.validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                activeChar.sendActionFailed();
                return;
            }

            if (!inventory.validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                activeChar.sendActionFailed();
                return;
            }

            if (entry.getIngredients().isEmpty()) {
                activeChar.sendActionFailed();
                activeChar.setMultisell(null);
                return;
            }

            // Перебор всех ингридиентов, проверка наличия и создание списка забираемого
            for (final MultiSellIngredient ingridient : entry.getIngredients()) {
                final int ingridientItemId = ingridient.getItemId();
                final long ingridientItemCount = ingridient.getItemCount();
                final int ingridientEnchant = ingridient.getItemEnchant();
                final long totalAmount = !ingridient.getMantainIngredient() ? Math.multiplyExact(ingridientItemCount, _amount) : ingridientItemCount;

                if (ingridientItemId == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE) {
                    if (activeChar.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
                        return;
                    }

                    if (activeChar.getClan().getReputationScore() < totalAmount) {
                        activeChar.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                        return;
                    }

                    if (activeChar.getClan().getLeaderId() != activeChar.getObjectId()) {
                        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_A_CLAN_LEADER).addString(activeChar.getName()));
                        return;
                    }
                    if (!ingridient.getMantainIngredient()) {
                        items.add(new ItemData(ingridientItemId, totalAmount, null));
                    }
                } else if (ingridientItemId == ItemTemplate.ITEM_ID_PC_BANG_POINTS) {
                    if (activeChar.getPremiumAccountComponent().getPcBangPoints() < totalAmount) {
                        activeChar.sendPacket(SystemMsg.YOU_ARE_SHORT_OF_ACCUMULATED_POINTS);
                        return;
                    }
                    if (!ingridient.getMantainIngredient()) {
                        items.add(new ItemData(ingridientItemId, totalAmount, null));
                    }
                } else if (ingridientItemId == ItemTemplate.ITEM_ID_FAME) {
                    if (activeChar.getFame() < totalAmount) {
                        activeChar.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_REPUTATION_TO_DO_THAT);
                        return;
                    }
                    if (!ingridient.getMantainIngredient()) {
                        items.add(new ItemData(ingridientItemId, totalAmount, null));
                    }
                } else {
                    final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(ingridientItemId);

                    if (!template.isStackable()) {
                        for (int i = 0; i < ingridientItemCount * _amount; i++) {
                            final List<ItemInstance> list = inventory.getItemsByItemId(ingridientItemId);
                            // Если энчант имеет значение - то ищем вещи с точно таким энчантом
                            if (keepenchant) {
                                ItemInstance itemToTake = null;
                                for (final ItemInstance item : list) {
                                    final ItemData itmd = new ItemData(item.getItemId(), item.getCount(), item);
                                    if ((item.getEnchantLevel() == ingridientEnchant || !item.getTemplate().isEquipment()) && !items.contains(itmd) && !item.isEquipped() && item.canBeExchanged(activeChar)) {
                                        itemToTake = item;
                                        break;
                                    }
                                }

                                if (itemToTake == null) {
                                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                    return;
                                }

                                if (!ingridient.getMantainIngredient()) {
                                    items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake));
                                }
                            }
                            // Если энчант не обрабатывается берется вещь с наименьшим энчантом
                            else {
                                ItemInstance itemToTake = null;
                                for (final ItemInstance item : list) {
                                    if (!items.contains(new ItemData(item.getItemId(), item.getCount(), item)) && (itemToTake == null || item.getEnchantLevel() < itemToTake.getEnchantLevel()) && !item.isEquipped() && !item.isShadowItem() && !item.isTemporalItem() && (!item.isAugmented() || AllSettingsConfig.ALT_ALLOW_DROP_AUGMENTED) && ItemFunctions.checkIfCanDiscard(activeChar, item)) {
                                        itemToTake = item;
                                        if (itemToTake.getEnchantLevel() == 0) {
                                            break;
                                        }
                                    }
                                }

                                if (itemToTake == null) {
                                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                    return;
                                }

                                if (!ingridient.getMantainIngredient()) {
                                    items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake));
                                }
                            }
                        }
                    } else {
                        if (ingridientItemId == ItemTemplate.ITEM_ID_ADENA) {
                            totalPrice = Math.addExact(totalPrice, Math.multiplyExact(ingridientItemCount, _amount));
                        }
                        final ItemInstance item = inventory.getItemByItemId(ingridientItemId);

                        if (item == null || item.getCount() < totalAmount) {
                            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                            return;
                        }

                        if (!ingridient.getMantainIngredient()) {
                            items.add(new ItemData(item.getItemId(), totalAmount, item));
                        }
                    }
                }

                if (activeChar.getAdena() < totalPrice) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                }
            }

            ItemAttributes attributes = null;
            int enchantLevel = 0;
            int variationStoneId = 0;
            int variation1Id = 0;
            int variation2Id = 0;
            for (final ItemData id : items) {
                final long count = id.getCount();
                if (count > 0) {
                    if (id.getId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE) {
                        activeChar.getClan().incReputation((int) -count, false, "MultiSell");
                        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLANS_REPUTATION_SCORE).addNumber(count));
                    } else if (id.getId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS) {
                        activeChar.getPremiumAccountComponent().reducePcBangPoints((int) count);
                    } else if (id.getId() == ItemTemplate.ITEM_ID_FAME) {
                        activeChar.setFame(activeChar.getFame() - (int) count, "MultiSell");
                        activeChar.sendPacket(new SystemMessage(SystemMsg.S2_S1_HAS_DISAPPEARED).addNumber(count).addString("Fame"));
                    } else {
                        if (inventory.destroyItem(id.getItem(), count)) {
                            if (keepenchant && id.getItem().canBeEnchanted()) //TODO[K] - Возможно понадобится проверять итем по грейду, тут!
                            {
                                enchantLevel = id.getItem().getEnchantLevel();
                                attributes = id.getItem().getAttributes();
                                variationStoneId = id.getItem().getVariationStoneId();
                                variation1Id = id.getItem().getVariation1Id();
                                variation2Id = id.getItem().getVariation2Id();
                            }

                            activeChar.sendPacket(SystemMessage.removeItems(id.getId(), count));
                            continue;
                        }

                        Log.audit("[MultiSellChoose]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong item count!");
                        return;
                    }
                }
            }

            if (tax > 0 && !notax) {
                if (castle != null) {
                    activeChar.sendMessage(new CustomMessage("trade.HavePaidTax").addNumber(tax));
                    if (merchant != null && merchant.getReflection() == ReflectionManager.DEFAULT) {
                        castle.addToTreasury(tax, true, false);
                    }
                }
            }

            for (final MultiSellIngredient in : entry.getProduction()) {
                if (in.getItemId() <= 0) {
                    if (in.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE) {
                        activeChar.getClan().incReputation((int) (in.getItemCount() * _amount), false, "MultiSell");
                        activeChar.sendPacket(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_ADDED_S1_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(in.getItemCount() * _amount));
                    } else if (in.getItemId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS) {
                        activeChar.getPremiumAccountComponent().addPcBangPoints((int) (in.getItemCount() * _amount), false);
                    } else if (in.getItemId() == ItemTemplate.ITEM_ID_FAME) {
                        activeChar.setFame(activeChar.getFame() + (int) (in.getItemCount() * _amount), "MultiSell");
                    }
                } else if (ItemTemplateHolder.getInstance().getTemplate(in.getItemId()).isStackable()) {
                    final long total = Math.multiplyExact(in.getItemCount(), _amount);
                    ItemFunctions.addItem(activeChar, in.getItemId(), total, true);
                } else {
                    for (int i = 0; i < _amount; i++) {
                        final ItemInstance product = ItemFunctions.createItem(in.getItemId());

                        if (keepenchant) {
                            if (product.canBeEnchanted()) //TODO[K] - Возможно понадобится проверять итем по грейду, тут!
                            {
                                product.setEnchantLevel(enchantLevel);
                                product.setVisualItemId(in.getVisual());
                                product.setCostume(in.isCostume());
                                final int flag = in.getFlag();
                                if (flag != 0)
                                    product.setCustomFlags(flag);
                                if (attributes != null) {
                                    product.setAttributes(attributes.clone());
                                }
                                if (variationStoneId > 0) {
                                    product.setVariationStoneId(variationStoneId);
                                }
                                if (variation1Id != 0) {
                                    product.setVariation1Id(variation1Id);
                                }
                                if (variation2Id != 0) {
                                    product.setVariation2Id(variation2Id);
                                }
                            }
                        } else {
                            final int time = in.getItemLifeTime();
                            if (time > 0) {
                                product.setCustomFlags(ItemInstance.FLAG_TEMPORAL);
                                product.setLifeTime((int) (System.currentTimeMillis() / 1000L) + time);
                            }
                            product.setVisualItemId(in.getVisual());
                            product.setCostume(in.isCostume());
                            final int flag = in.getFlag();
                            if (flag != 0)
                                product.setCustomFlags(flag);
                            product.setEnchantLevel(in.getItemEnchant());
                            product.setAttributes(in.getItemAttributes().clone());
                        }

                        activeChar.sendPacket(SystemMessage.obtainItems(product));
                        inventory.addItem(product);
                    }
                }
            }
        } catch (ArithmeticException ae) {
            Log.audit("[EnchantItemAttribute]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " exception in main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        } finally {
            inventory.writeUnlock();
        }

        activeChar.sendChanges();

        if (!list1.isShowAll()) // Если показывается только то, на что хватает материалов обновить окно у игрока
        {
            MultiSellHolder.getInstance().SeparateAndSend(list1, activeChar, list1.getNpcObjectId(), castle == null ? 0 : castle.getTaxRate());
        }
    }

    private static class ItemData {
        private final int _id;
        private final long _count;
        private final ItemInstance _item;

        public ItemData(final int id, final long count, final ItemInstance item) {
            _id = id;
            _count = count;
            _item = item;
        }

        public int getId() {
            return _id;
        }

        public long getCount() {
            return _count;
        }

        public ItemInstance getItem() {
            return _item;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ItemData)) {
                return false;
            }

            final ItemData i = (ItemData) obj;

            return _id == i._id && _count == i._count && _item == i._item;
        }
    }
}