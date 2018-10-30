package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author KilRoy
 */
@Deprecated
public class action_show_adventurer_guide_book extends ScriptItemHandler
{
	private static int[] itemIds;

	public action_show_adventurer_guide_book()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_show_adventurer_guide_book)
			{
				set.add(template.getItemId());
			}
		}
		itemIds = set.toArray();
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