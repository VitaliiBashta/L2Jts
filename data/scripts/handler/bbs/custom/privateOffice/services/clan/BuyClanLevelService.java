package handler.bbs.custom.privateOffice.services.clan;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowInfoUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeStatusChanged;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.SiegeUtils;

/**
 * @author Mangol
 * @since 07.03.2016
 */
public class BuyClanLevelService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.buy_clan_level;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/buyClanLevel/buy_clan_lvl.htm", player);
		final String tableEmptySkill = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/buyClanLevel/tableEmptySkill.htm", player);
		if(player.getClan() == null || !player.isClanLeader())
		{
			htm = htm.replace("<?table?>", tableEmptySkill);
		}
		else
		{
			final int clanLevel = player.getClan().getLevel();
			int correctBuyLevelIndex = -1;
			int correctBuyLevel = -1;
			for(int i = 0; i < getService().getLevels().length; i++)
			{
				final int level = getService().getLevels()[i];
				if(clanLevel + 1 == level)
				{
					correctBuyLevelIndex = i;
					correctBuyLevel = level;
				}
			}
			if(correctBuyLevel != -1)
			{
				String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/buyClanLevel/table.htm", player);
				table = table.replace("<?buy_level?>", String.valueOf(correctBuyLevel));
				table = table.replace("<?correct_clan_elvel?>", String.valueOf(clanLevel));
				table = table.replace("<?correct_clan_elvel?>", String.valueOf(clanLevel));
				table = table.replace("<?buy_bypass?>", "bypass _bbsservice:service:buy_clan_level:request:" + correctBuyLevelIndex);
				table = table.replace("<?price?>", String.valueOf(getService().getItemCounts()[correctBuyLevelIndex]));
				table = table.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemIds()[correctBuyLevelIndex]));
				htm = htm.replace("<?table?>", table);
			}
			else
			{
				htm = htm.replace("<?table?>", tableEmptySkill);
			}
		}
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.error"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(player.getClan() == null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.noClan"));
			useSaveCommand(player);
			return;
		}
		if(!player.isClanLeader())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.noClanLeader"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final int index = Converter.convert(Integer.class, str[4]);
		if(index < 0 || (index >= getService().getLevels().length ||
				index >= getService().getItemIds().length ||
				index >= getService().getItemCounts().length))
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.error"));
			useSaveCommand(player);
			return;
		}
		final int buyLevel = getService().getLevels()[index];
		if(player.getClan().getLevel() + 1 != buyLevel)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanLevel.noCorrectBuyLevel"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemIds()[index], getService().getItemCounts()[index], true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final Clan clan = player.getClan();
		final int oldLevel = clan.getLevel();
		final int newLevel = clan.getLevel() + 1;
		clan.setLevel(newLevel);
		clan.updateClanInDB();
		player.broadcastCharInfo();
		final SkillEntry entry = SkillTable.getInstance().getSkillEntry(5103, 1);
		player.broadcastPacket(new MagicSkillUse(player, player, entry.getDisplayId(), entry.getLevel(), entry.getTemplate().getHitTime(), entry.getTemplate().getReuseDelay()));
		if(clan.getLevel() >= 4)
		{
			SiegeUtils.addSiegeSkills(player);
		}
		if(clan.getLevel() == 5)
		{
			player.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);

			final PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
			final PledgeStatusChanged ps = new PledgeStatusChanged(clan);
			for(final UnitMember mbr : clan)
			{
				if(mbr.isOnline())
				{
					mbr.getPlayer().updatePledgeClass();
					mbr.getPlayer().sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
					mbr.getPlayer().broadcastCharInfo();
				}
			}
		}
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyClanLevelService", player, "buy clan level - " + newLevel + " old level - " + oldLevel);
	}

}
