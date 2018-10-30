package handler.onshiftaction;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_ItemInstance extends ScriptOnShiftActionHandler<ItemInstance>
{
	@Override
	public Class<ItemInstance> getClazz()
	{
		return ItemInstance.class;
	}

	@Override
	public boolean call(ItemInstance item, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		String dialog;

		dialog = HtmCache.getInstance().getHtml("scripts/actions/admin.L2ItemInstance.onActionShift.htm", player);
		dialog = dialog.replaceFirst("%name%", String.valueOf(item.getTemplate().getName()));
		dialog = dialog.replaceFirst("%objId%", String.valueOf(item.getObjectId()));
		dialog = dialog.replaceFirst("%itemId%", String.valueOf(item.getItemId()));
		dialog = dialog.replaceFirst("%grade%", String.valueOf(item.getCrystalType()));
		dialog = dialog.replaceFirst("%count%", String.valueOf(item.getCount()));

		Player owner = GameObjectsStorage.getPlayer(item.getOwnerId()); //FIXME [VISTALL] несовсем верно, может быть CCE при условии если овнер не игрок
		dialog = dialog.replaceFirst("%owner%", String.valueOf(owner == null ? "none" : owner.getName()));
		dialog = dialog.replaceFirst("%ownerId%", String.valueOf(item.getOwnerId()));

		for(Element e : Element.VALUES)
		{
			dialog = dialog.replaceFirst('%' + e.name().toLowerCase() + "Val%", String.valueOf(item.getAttributeElementValue(e, true)));
		}

		dialog = dialog.replaceFirst("%attrElement%", String.valueOf(item.getAttributeElement()));
		dialog = dialog.replaceFirst("%attrValue%", String.valueOf(item.getAttributeElementValue()));

		dialog = dialog.replaceFirst("%enchLevel%", String.valueOf(item.getEnchantLevel()));
		dialog = dialog.replaceFirst("%type%", String.valueOf(item.getItemType()));

		dialog = dialog.replaceFirst("%dropTime%", String.valueOf(item.getDropTimeOwner()));
		//dialog = dialog.replaceFirst("%dropOwner%", String.valueOf(item.getDropOwnerId()));
		//dialog = dialog.replaceFirst("%dropOwnerId%", String.valueOf(item.getDropOwnerId()));


		HtmlMessage msg = new HtmlMessage(5);
		msg.setHtml(dialog);
		player.sendPacket(msg);
		return true;
	}
}
