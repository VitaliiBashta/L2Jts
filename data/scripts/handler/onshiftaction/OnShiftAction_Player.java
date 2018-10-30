package handler.onshiftaction;

import org.mmocore.gameserver.handler.admincommands.impl.AdminEditChar;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_Player extends ScriptOnShiftActionHandler<Player>
{
	@Override
	public Class<Player> getClazz()
	{
		return Player.class;
	}

	@Override
	public boolean call(Player p, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		AdminEditChar.showCharacterList(player, p);
		return true;
	}
}
