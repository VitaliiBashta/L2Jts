package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.SimpleItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.serverpackets.ExChangeNicknameNColor;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author Unknown
 * @author KilRoy
 */
public class action_nick_color extends SimpleItemHandler
{
	private static int[] itemIds = {};

	public action_nick_color()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_nick_color)
			{
				set.add(template.getItemId());
			}
		}
		itemIds = set.toArray();
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		player.sendPacket(new ExChangeNicknameNColor(item.getObjectId()));
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}