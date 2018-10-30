package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;

public class ManaRegen
{
	private static final int ADENA = 57;
	private static final long PRICE = 5; //5 аден за 1 МП

	@Bypass("services.ManaRegen:DoManaRegen")
	public void DoManaRegen(Player player, NpcInstance npc, String[] arg)
	{
		if(npc == null || npc.getReflectionId() != player.getReflectionId() || !player.isInRangeZ(npc, player.getInteractDistance(npc)))
		{
			return;
		}

		long mp = (long) Math.floor(player.getMaxMp() - player.getCurrentMp());
		long fullCost = mp * PRICE;
		if(fullCost <= 0)
		{
			player.sendPacket(SystemMsg.NOTHING_HAPPENED);
			return;
		}
		if(ItemFunctions.getItemCount(player, ADENA) >= fullCost)
		{
			ItemFunctions.removeItem(player, ADENA, fullCost);
			player.sendPacket(new SystemMessage(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addNumber(mp));
			player.setCurrentMp(player.getMaxMp());
		}
		else
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
	}
}