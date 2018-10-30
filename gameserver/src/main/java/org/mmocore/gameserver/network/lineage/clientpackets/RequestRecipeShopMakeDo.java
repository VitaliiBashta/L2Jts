package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.jts.dataparser.data.holder.recipe.RecipeMaterial;
import org.jts.dataparser.data.holder.recipe.RecipeProduct;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeShopItemInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ManufactureItem;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TradeHelper;

import java.util.Optional;

public class RequestRecipeShopMakeDo extends L2GameClientPacket {
    private int _manufacturerId;
    private int _recipeId;
    private long _price;

    @Override
    protected void readImpl() {
        _manufacturerId = readD();
        _recipeId = readD();
        _price = readQ();
    }

    @Override
    protected void runImpl() {
        final Player buyer = getClient().getActiveChar();
        if (buyer == null) {
            return;
        }
        if (buyer.isActionsDisabled()) {
            buyer.sendActionFailed();
            return;
        }
        if (buyer.isInStoreMode()) {
            buyer.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (buyer.isInTrade()) {
            buyer.sendActionFailed();
            return;
        }
        if (buyer.isFishing()) {
            buyer.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }
        if (!buyer.getPlayerAccess().UseTrade) {
            buyer.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
            return;
        }
        final Player manufacturer = (Player) buyer.getVisibleObject(_manufacturerId);
        if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(buyer, manufacturer.getInteractDistance(buyer))) {
            buyer.sendActionFailed();
            return;
        }
        Optional<Recipe> recipe = Optional.empty();
        for (final ManufactureItem mi : manufacturer.getCreateList()) {
            if (mi.getRecipeId() == _recipeId) {
                if (_price == mi.getCost()) {
                    recipe = Optional.ofNullable(RecipeHolder.getInstance().getRecipeId(_recipeId));
                    break;
                }
            }
        }
        if (!recipe.isPresent()) {
            buyer.sendActionFailed();
            return;
        }
        if (!recipe.get().isCommonRecipe() && recipe.get().getLevel() > manufacturer.getSkillLevel(Skill.SKILL_CRAFTING)) {
            buyer.sendActionFailed();
            return;
        }
        int success = 0;
        final RecipeComponent recipeComponent = manufacturer.getRecipeComponent();
        if (!recipeComponent.findRecipe(_recipeId)) {
            buyer.sendActionFailed();
            return;
        }
        if (manufacturer.getCurrentMp() < recipe.get().getMpConsume()) {
            manufacturer.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            buyer.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeShopItemInfo(buyer, manufacturer, _recipeId, _price, success));
            return;
        }
        buyer.getInventory().writeLock();
        try {
            if (buyer.getAdena() < _price) {
                buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, _recipeId, _price, success));
                return;
            }
            final RecipeMaterial[] materials = recipe.get().getMaterials();
            for (final RecipeMaterial material : materials) {
                if (material.getCount() != 0) {
                    final ItemInstance item = buyer.getInventory().getItemByItemId(material.getIdMaterial());
                    if (item == null || material.getCount() > item.getCount()) {
                        buyer.sendPacket(SystemMsg.NOT_ENOUGH_MATERIALS, new RecipeShopItemInfo(buyer, manufacturer, _recipeId, _price, success));
                        return;
                    }
                }
            }
            if (!buyer.reduceAdena(_price, false)) {
                buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, _recipeId, _price, success));
                return;
            }
            for (final RecipeMaterial material : materials) {
                if (material.getCount() != 0) {
                    if (!buyer.getInventory().destroyItemByItemId(material.getIdMaterial(), material.getCount())) {
                        Log.audit("[RecipeShopMakeDo]", "Player(ID:" + buyer.getObjectId() + ") name: " + buyer.getName() + " incorrect meterial item, m.b used PH!");
                        buyer.sendDebugMessage("Material: " + material.getIdMaterial() + " not correct deleted! Please, send this message to GM!");
                        continue;
                    }
                    buyer.sendPacket(SystemMessage.removeItems(material.getIdMaterial(), material.getCount()));
                }
            }
            final long tax = TradeHelper.getTax(manufacturer, _price);
            if (tax > 0) {
                _price -= tax;
                manufacturer.sendMessage(new CustomMessage("trade.HavePaidTax").addNumber(tax));
            }
            manufacturer.addAdena(_price);
        } finally {
            buyer.getInventory().writeUnlock();
        }
        manufacturer.reduceCurrentMp(recipe.get().getMpConsume(), null);
        manufacturer.sendStatusUpdate(false, false, StatusUpdate.CUR_MP);
        double premiumBonus = buyer.getPremiumAccountComponent().getPremiumBonus().getCraftChance();
        if (premiumBonus <= 0)
            premiumBonus = 1;
        final Optional<RecipeProduct> product = recipe.get().getRandomProduct(premiumBonus);
        if (product.isPresent()) {
            final int itemId = product.get().getIdProduct();
            final long itemsCount = product.get().getCount();
            ItemFunctions.addItem(buyer, itemId, itemsCount, true);
            SystemMessage sm;
            if (itemsCount > 1) {
                sm = new SystemMessage(SystemMsg.C1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA);
                sm.addString(manufacturer.getName());
                sm.addItemName(itemId);
                sm.addNumber(itemsCount);
                sm.addNumber(_price);
                buyer.sendPacket(sm);
                sm = new SystemMessage(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA);
                sm.addString(buyer.getName());
                sm.addItemName(itemId);
                sm.addNumber(itemsCount);
                sm.addNumber(_price);
                manufacturer.sendPacket(sm);
            } else {
                sm = new SystemMessage(SystemMsg.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
                sm.addString(manufacturer.getName());
                sm.addItemName(itemId);
                sm.addNumber(_price);
                buyer.sendPacket(sm);
                sm = new SystemMessage(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA);
                sm.addString(buyer.getName());
                sm.addItemName(itemId);
                sm.addNumber(_price);
                manufacturer.sendPacket(sm);
            }
            success = 1;
        } else {
            buyer.sendPacket(new SystemMessage(SystemMsg.S1_MANUFACTURING_FAILURE).addItemName(recipe.get().getItemId()));
        }
        buyer.sendChanges();
        buyer.sendPacket(new RecipeShopItemInfo(buyer, manufacturer, _recipeId, _price, success));
    }
}