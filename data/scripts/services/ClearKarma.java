package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * Created by [STIGMATED] : 28.07.12 : 21:39
 */
public class ClearKarma
{
	private static final int ADENA = 57;
	private static final long PRICE = 5;

	@Bypass("services.ClearKarma:DoClearKarma")
	public void DoClearKarma(Player player, NpcInstance npc, String[] arg)
	{
		if(npc == null || npc.getReflectionId() != player.getReflectionId() || !player.isInRangeZ(npc, player.getInteractDistance(npc)))
		{
			return;
		}

		if(ItemFunctions.getItemCount(player, ADENA) >= PRICE)
		{
			ItemFunctions.removeItem(player, ADENA, PRICE);
			player.sendMessage("Ваша карма отчищена.");
			player.setKarma(0);
		}
		else
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
	}
}
