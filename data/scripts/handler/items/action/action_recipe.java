package handler.items.action;
import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;
import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeBookItemList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_recipe extends ScriptItemHandler
{
	private static int[] itemIds;

	public action_recipe()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_recipe)
			{
				set.add(template.getItemId());
			}
		}
		itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
		{
			return false;
		}
		final Recipe rp = RecipeHolder.getInstance().getRecipeItemId(item.getItemId());
		if(rp == null)
		{
			return false;
		}
		final Player player = (Player) playable;
		final RecipeComponent recipeComponent = player.getRecipeComponent();
		if(!rp.isCommonRecipe())
		{
			if(player.getDwarvenRecipeLimit() > 0)
			{
				if(recipeComponent.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit())
				{
					player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
					return false;
				}

				if(rp.getLevel() > player.getSkillLevel(Skill.SKILL_CRAFTING))
				{
					player.sendPacket(SystemMsg.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
					return false;
				}
				if(recipeComponent.hasRecipe(rp))
				{
					player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
					return false;
				}
				if(!player.getInventory().destroyItem(item, 1L))
				{
					player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
					return false;
				}
				recipeComponent.registerRecipe(rp.getId());
				recipeComponent.addDwarfRecipe(rp);
				player.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
				player.sendPacket(new RecipeBookItemList(player, true));
				return true;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
			}
		}
		else if(player.getCommonRecipeLimit() > 0)
		{
			if(recipeComponent.getCommonRecipeBook().size() >= player.getCommonRecipeLimit())
			{
				player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
				return false;
			}
			if(recipeComponent.hasRecipe(rp))
			{
				player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
				return false;
			}
			if(!player.getInventory().destroyItem(item, 1L))
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
				return false;
			}
			recipeComponent.registerRecipe(rp.getId());
			recipeComponent.addCommonRecipe(rp);
			player.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
			player.sendPacket(new RecipeBookItemList(player, false));
			return true;
		}
		else
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		}
		return false;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}