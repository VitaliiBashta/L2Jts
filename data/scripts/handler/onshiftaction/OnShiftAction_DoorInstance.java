package handler.onshiftaction;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_DoorInstance extends ScriptOnShiftActionHandler<DoorInstance>
{
	@Override
	public Class<DoorInstance> getClazz()
	{
		return DoorInstance.class;
	}

	@Override
	public boolean call(DoorInstance door, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		String dialog;
		dialog = HtmCache.getInstance().getHtml("scripts/actions/admin.L2DoorInstance.onActionShift.htm", player);
		dialog = dialog.replaceFirst("%CurrentHp%", String.valueOf((int) door.getCurrentHp()));
		dialog = dialog.replaceFirst("%MaxHp%", String.valueOf(door.getMaxHp()));
		dialog = dialog.replaceAll("%ObjectId%", String.valueOf(door.getObjectId()));
		dialog = dialog.replaceFirst("%doorId%", String.valueOf(door.getDoorId()));
		dialog = dialog.replaceFirst("%pdef%", String.valueOf(door.getPDef(null)));
		dialog = dialog.replaceFirst("%mdef%", String.valueOf(door.getMDef(null, null)));
		dialog = dialog.replaceFirst("%type%", door.getDoorType().name());
		dialog = dialog.replaceFirst("%upgradeHP%", String.valueOf(door.getUpgradeHp()));
		StringBuilder b = new StringBuilder("");
		for(Event e : door.getEvents())
		{
			b.append(e.toString()).append(';');
		}
		dialog = dialog.replaceFirst("%event%", b.toString());

		HtmlMessage msg = new HtmlMessage(5);
		msg.setHtml(dialog);
		player.sendPacket(msg);
		return true;
	}
}
