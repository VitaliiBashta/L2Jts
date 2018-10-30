package handler.onshiftaction;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_SummonInstance extends ScriptOnShiftActionHandler<SummonInstance>
{
	@Override
	public Class<SummonInstance> getClazz()
	{
		return SummonInstance.class;
	}

	@Override
	public boolean call(SummonInstance summonInstance, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		String dialog;
		dialog = HtmCache.getInstance().getHtml("scripts/actions/admin.L2SummonInstance.onActionShift.htm", player);
		dialog = dialog.replaceFirst("%name%", String.valueOf(summonInstance.getName()));
		dialog = dialog.replaceFirst("%level%", String.valueOf(summonInstance.getLevel()));
		dialog = dialog.replaceFirst("%class%", String.valueOf(getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
		dialog = dialog.replaceFirst("%xyz%", summonInstance.getLoc().x + " " + summonInstance.getLoc().y + ' ' + summonInstance.getLoc().z);
		dialog = dialog.replaceFirst("%heading%", String.valueOf(summonInstance.getLoc().h));

		dialog = dialog.replaceFirst("%owner%", String.valueOf(summonInstance.getPlayer().getName()));
		dialog = dialog.replaceFirst("%ownerId%", String.valueOf(summonInstance.getPlayer().getObjectId()));

		dialog = dialog.replaceFirst("%npcId%", String.valueOf(summonInstance.getNpcId()));
		dialog = dialog.replaceFirst("%expPenalty%", String.valueOf(summonInstance.getExpPenalty()));

		dialog = dialog.replaceFirst("%maxHp%", String.valueOf(summonInstance.getMaxHp()));
		dialog = dialog.replaceFirst("%maxMp%", String.valueOf(summonInstance.getMaxMp()));
		dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) summonInstance.getCurrentHp()));
		dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) summonInstance.getCurrentMp()));

		dialog = dialog.replaceFirst("%pDef%", String.valueOf(summonInstance.getPDef(null)));
		dialog = dialog.replaceFirst("%mDef%", String.valueOf(summonInstance.getMDef(null, null)));
		dialog = dialog.replaceFirst("%pAtk%", String.valueOf(summonInstance.getPAtk(null)));
		dialog = dialog.replaceFirst("%mAtk%", String.valueOf(summonInstance.getMAtk(null, null)));
		dialog = dialog.replaceFirst("%accuracy%", String.valueOf(summonInstance.getAccuracy()));
		dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(summonInstance.getEvasionRate(null)));
		dialog = dialog.replaceFirst("%crt%", String.valueOf(summonInstance.getCriticalHit(null, null)));
		dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(summonInstance.getRunSpeed()));
		dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(summonInstance.getWalkSpeed()));
		dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(summonInstance.getPAtkSpd()));
		dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(summonInstance.getMAtkSpd()));
		dialog = dialog.replaceFirst("%dist%", String.valueOf((int) summonInstance.getRealDistance(player)));

		dialog = dialog.replaceFirst("%STR%", String.valueOf(summonInstance.getSTR()));
		dialog = dialog.replaceFirst("%DEX%", String.valueOf(summonInstance.getDEX()));
		dialog = dialog.replaceFirst("%CON%", String.valueOf(summonInstance.getCON()));
		dialog = dialog.replaceFirst("%INT%", String.valueOf(summonInstance.getINT()));
		dialog = dialog.replaceFirst("%WIT%", String.valueOf(summonInstance.getWIT()));
		dialog = dialog.replaceFirst("%MEN%", String.valueOf(summonInstance.getMEN()));

		HtmlMessage msg = new HtmlMessage(5);
		msg.setHtml(dialog);
		player.sendPacket(msg);
		return true;
	}
}
