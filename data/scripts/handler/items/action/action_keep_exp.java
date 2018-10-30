package handler.items.action;

import handler.items.ScriptItemHandler;

import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author KilRoy
 */
@Deprecated
public class action_keep_exp extends ScriptItemHandler
{
	private static int[] itemIds = {};

	public action_keep_exp()
	{
		//final TIntHashSet set = new TIntHashSet();
		//for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		//{
		//	if(template == null)
		//	{
		//		continue;
		//	}
		//	if(template.getActionType() == ItemAction.action_keep_exp)
		//	{
		//		set.add(template.getItemId());
		//	}
		//}
		//itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
		{
			return false;
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}