package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.jts.dataparser.data.holder.recipe.RecipeMaterial;
import org.jts.dataparser.data.holder.recipe.RecipeProduct;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeItemMakeInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.mmocore.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

import java.util.Optional;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket {
    private int _recipeId;

    /**
     * packet type id 0xB8 format: cd
     */
    @Override
    protected void readImpl() {
        _recipeId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.isInStoreMode()) {
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.isProcessingRequest()) {
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        final Recipe recipe = RecipeHolder.getInstance().getRecipeId(_recipeId);
        if (recipe == null || recipe.getMaterials().length == 0) {
            activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
            return;
        }
        if (activeChar.getCurrentMp() < recipe.getMpConsume()) {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(activeChar, recipe, 0));
            return;
        }
        if (!recipe.isCommonRecipe() && recipe.getLevel() > activeChar.getSkillLevel(Skill.SKILL_CRAFTING)) {
            activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
            return;
        }
        final RecipeComponent recipeComponent = activeChar.getRecipeComponent();
        if (!recipeComponent.findRecipe(_recipeId)) {
            activeChar.sendPacket(SystemMsg.PLEASE_REGISTER_A_RECIPE, ActionFail.STATIC);
            return;
        }
        final ItemTemplateHolder holder = ItemTemplateHolder.getInstance();
        activeChar.getInventory().writeLock();
        try {
            final RecipeMaterial[] materials = recipe.getMaterials();
            for (final RecipeMaterial material : materials) {
                if (material.getCount() != 0) {
                    final Optional<ItemTemplate> itemTemplate = Optional.ofNullable(holder.getTemplate(material.getIdMaterial()));
                    if (AllSettingsConfig.ALT_GAME_UNREGISTER_RECIPE && itemTemplate.isPresent() && itemTemplate.get().getItemType() == EtcItemType.RECIPE) {
                        final Recipe rp = RecipeHolder.getInstance().getRecipeItemId(material.getIdMaterial());
                        if (recipeComponent.hasRecipe(rp)) {
                            continue;
                        }
                        activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(activeChar, recipe, 0));
                        return;
                    } else {
                        final ItemInstance item = activeChar.getInventory().getItemByItemId(material.getIdMaterial());
                        if (item == null || item.getCount() < material.getCount()) {
                            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(activeChar, recipe, 0));
                            return;
                        }
                    }
                }
            }
            for (final RecipeMaterial material : materials) {
                if (material.getCount() != 0) {
                    final Optional<ItemTemplate> itemTemplate = Optional.ofNullable(holder.getTemplate(material.getIdMaterial()));
                    if (AllSettingsConfig.ALT_GAME_UNREGISTER_RECIPE && itemTemplate.isPresent() && itemTemplate.get().getItemType() == EtcItemType.RECIPE) {
                        recipeComponent.unregisterRecipe(RecipeHolder.getInstance().getRecipeItemId(material.getIdMaterial()).getId());
                    } else {
                        if (!activeChar.getInventory().destroyItemByItemId(material.getIdMaterial(), material.getCount())) {
                            Log.audit("[RequestRecipeItemMakeSelf]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " incorrect meterial item, m.b used PH!");
                            activeChar.sendDebugMessage("Material: " + material.getIdMaterial() + " not correct deleted! Please, send this message to GM!");
                            continue;
                        }
                        activeChar.sendPacket(SystemMessage.removeItems(material.getIdMaterial(), material.getCount()));
                    }
                }
            }
        } finally {
            activeChar.getInventory().writeUnlock();
        }
        activeChar.resetWaitSitTime();
        activeChar.reduceCurrentMp(recipe.getMpConsume(), null);
        double premiumBonus = activeChar.getPremiumAccountComponent().getPremiumBonus().getCraftChance();
        if (premiumBonus <= 0)
            premiumBonus = 1;
        final Optional<RecipeProduct> product = recipe.getRandomProduct(premiumBonus);
        int success = 0;
        if (product.isPresent()) {
            final int itemId = product.get().getIdProduct();
            final int itemsCount = product.get().getCount();
            ItemFunctions.addItem(activeChar, itemId, itemsCount, true);
            success = 1;
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_MANUFACTURING_FAILURE).addItemName(recipe.getItemId()));
        }
        activeChar.sendPacket(new RecipeItemMakeInfo(activeChar, recipe, success));
    }
}
