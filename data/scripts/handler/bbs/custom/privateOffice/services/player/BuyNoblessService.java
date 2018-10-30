package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

import java.util.StringTokenizer;

/**
 * @author Mangol
 * @since 17.02.2016
 */
public class BuyNoblessService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/buy_nobless.htm", player);
		htm = htm.replace("<?page_con?>", getContent(player));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.buyNobless.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(player.isNoble())
		{
			player.sendPacket(new CustomMessage("bbs.service.buyNobless.isNoble"));
			useSaveCommand(player);
			return;
		}
		if(player.getSubLevel() < 75)
		{
			player.sendPacket(new CustomMessage("bbs.service.buyNobless.sub"));
			useSaveCommand(player);
			return;
		}
		String[] arr = bypass.split(":");
		if (arr.length < 5) {
			useSaveCommand(player);
			return;
		}
		int i = Integer.parseInt(arr[4]);
		if(!getCheckAndPick(player, getService().getItemIds()[i], getService().getItemCounts()[i], true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
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
		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		Log.service("BuyNoblessService", player, "acquired noble status.");
	}

	@Override
	public Services getService()
	{
		return Services.buy_nobless;
	}

	private String getContent(Player player) {
		String parseObj = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/buy_nobless_parse_obj.htm", player);
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < getService().getItemIds().length && i < getService().getItemCounts().length; i++) {
			String priceLine = "" + parseObj;
			priceLine = priceLine.replace("<?price?>", "" + getService().getItemCounts()[i]);
			priceLine = priceLine.replace("<?item_name?>", "" + getItemName(player.getLanguage(), getService().getItemIds()[i]));
			priceLine = priceLine.replace("<?button_bypass?>", "_bbsservice:service:buy_nobless:request:" + i);
			sb.append(priceLine);
		}
		return sb.toString();
	}

}
