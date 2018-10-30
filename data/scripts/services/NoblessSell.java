package services;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Player;

public class NoblessSell
{
	@Bypass("services.NoblessSell:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || player.isNoble())
		{
			return;
		}

		if(player.getSubLevel() < 75)
		{
			player.sendMessage("Вы должны быть на сабе не ниже 75 уровня.");
			return;
		}

		if(player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_NOBLESS_SELL_ITEM, ServicesConfig.SERVICES_NOBLESS_SELL_PRICE))
		{
			makeSubQuests(player, npc, arg);
			becomeNoble(player, npc, arg);
		}
		else if(ServicesConfig.SERVICES_NOBLESS_SELL_ITEM == 57)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}
	}

	@Bypass("services.NoblessSell:makeSubQuests")
	public void makeSubQuests(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
		{
			return;
		}
		Quest q = QuestManager.getQuest(234);
		QuestState qs = player.getQuestState(q.getId());
		if(qs != null)
		{
			qs.exitQuest(true);
		}
		q.newQuestState(player, Quest.COMPLETED);

		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
		{
			q = QuestManager.getQuest(236);
			qs = player.getQuestState(q.getId());
			if(qs != null)
			{
				qs.exitQuest(true);
			}
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest(235);
			qs = player.getQuestState(q.getId());
			if(qs != null)
			{
				qs.exitQuest(true);
			}
			q.newQuestState(player, Quest.COMPLETED);
		}
	}

	@Bypass("services.NoblessSell:becomeNoble")
	public void becomeNoble(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || player.isNoble())
		{
			return;
		}

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastUserInfo(true);
	}
}