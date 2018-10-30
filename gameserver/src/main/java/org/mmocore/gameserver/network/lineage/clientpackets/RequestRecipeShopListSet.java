package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeShopMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ManufactureItem;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.mmocore.gameserver.utils.TradeHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestRecipeShopListSet extends L2GameClientPacket {
    private int[] _recipes;
    private long[] _prices;
    private int _count;

    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _recipes = new int[_count];
        _prices = new long[_count];
        for (int i = 0; i < _count; i++) {
            _recipes[i] = readD();
            _prices[i] = readQ();
            if (_prices[i] < 0) {
                _count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        final Player manufacturer = getClient().getActiveChar();
        if (manufacturer == null || _count == 0) {
            return;
        }

        if (!TradeHelper.checksIfCanOpenStore(manufacturer, Player.STORE_PRIVATE_MANUFACTURE)) {
            manufacturer.sendActionFailed();
            return;
        }

        if (_count > OtherConfig.MAX_PVTCRAFT_SLOTS) {
            manufacturer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        final List<ManufactureItem> createList = new CopyOnWriteArrayList<>();
        final RecipeComponent recipeComponent = manufacturer.getRecipeComponent();
        for (int i = 0; i < _count; i++) {
            final int recipeId = _recipes[i];
            final long price = _prices[i];
            Recipe recipe = recipeComponent.getRecipe(recipeId);
            if (recipe == null) {
                continue;
            } else if (!recipe.isCommonRecipe() && recipe.getLevel() > manufacturer.getSkillLevel(Skill.SKILL_CRAFTING)) {
                manufacturer.sendActionFailed();
                return;
            }
            final ManufactureItem mi = new ManufactureItem(recipeId, price);
            createList.add(mi);
        }

        if (!createList.isEmpty()) {
            manufacturer.setCreateList(createList);
            manufacturer.saveTradeList();
            manufacturer.setPrivateStoreType(Player.STORE_PRIVATE_MANUFACTURE);
            manufacturer.broadcastPacket(new RecipeShopMsg(manufacturer));
            manufacturer.sitDown(null);
            manufacturer.broadcastCharInfo();
        }

        manufacturer.sendActionFailed();
    }
}