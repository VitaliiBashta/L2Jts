package handler.onshiftaction;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.HtmlUtils;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_PetInstance extends ScriptOnShiftActionHandler<PetInstance>
{
	@Override
	public Class<PetInstance> getClazz()
	{
		return PetInstance.class;
	}

	@Override
	public boolean call(PetInstance pet, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		String dialog;

		dialog = HtmCache.getInstance().getHtml("scripts/actions/admin.L2PetInstance.onActionShift.htm", player);
		dialog = dialog.replaceFirst("%name%", HtmlUtils.htmlNpcName(pet.getNpcId()));
		dialog = dialog.replaceFirst("%title%", String.valueOf(StringUtils.isEmpty(pet.getTitle()) ? "Empty" : pet.getTitle()));
		dialog = dialog.replaceFirst("%level%", String.valueOf(pet.getLevel()));
		dialog = dialog.replaceFirst("%class%", String.valueOf(pet.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
		dialog = dialog.replaceFirst("%xyz%", pet.getLoc().x + " " + pet.getLoc().y + ' ' + pet.getLoc().z);
		dialog = dialog.replaceFirst("%heading%", String.valueOf(pet.getLoc().h));

		dialog = dialog.replaceFirst("%owner%", String.valueOf(pet.getPlayer().getName()));
		dialog = dialog.replaceFirst("%ownerId%", String.valueOf(pet.getPlayer().getObjectId()));
		dialog = dialog.replaceFirst("%npcId%", String.valueOf(pet.getNpcId()));
		dialog = dialog.replaceFirst("%controlItemId%", String.valueOf(pet.getControlItem().getItemId()));

		dialog = dialog.replaceFirst("%exp%", String.valueOf(pet.getExp()));
		dialog = dialog.replaceFirst("%sp%", String.valueOf(pet.getSp()));

		dialog = dialog.replaceFirst("%maxHp%", String.valueOf(pet.getMaxHp()));
		dialog = dialog.replaceFirst("%maxMp%", String.valueOf(pet.getMaxMp()));
		dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) pet.getCurrentHp()));
		dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) pet.getCurrentMp()));

		dialog = dialog.replaceFirst("%pDef%", String.valueOf(pet.getPDef(null)));
		dialog = dialog.replaceFirst("%mDef%", String.valueOf(pet.getMDef(null, null)));
		dialog = dialog.replaceFirst("%pAtk%", String.valueOf(pet.getPAtk(null)));
		dialog = dialog.replaceFirst("%mAtk%", String.valueOf(pet.getMAtk(null, null)));
		dialog = dialog.replaceFirst("%accuracy%", String.valueOf(pet.getAccuracy()));
		dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(pet.getEvasionRate(null)));
		dialog = dialog.replaceFirst("%crt%", String.valueOf(pet.getCriticalHit(null, null)));
		dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(pet.getRunSpeed()));
		dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(pet.getWalkSpeed()));
		dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(pet.getPAtkSpd()));
		dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(pet.getMAtkSpd()));
		dialog = dialog.replaceFirst("%dist%", String.valueOf((int) pet.getRealDistance(player)));

		dialog = dialog.replaceFirst("%STR%", String.valueOf(pet.getSTR()));
		dialog = dialog.replaceFirst("%DEX%", String.valueOf(pet.getDEX()));
		dialog = dialog.replaceFirst("%CON%", String.valueOf(pet.getCON()));
		dialog = dialog.replaceFirst("%INT%", String.valueOf(pet.getINT()));
		dialog = dialog.replaceFirst("%WIT%", String.valueOf(pet.getWIT()));
		dialog = dialog.replaceFirst("%MEN%", String.valueOf(pet.getMEN()));


		HtmlMessage msg = new HtmlMessage(5);
		msg.setHtml(dialog);
		player.sendPacket(msg);
		return true;
	}
}
