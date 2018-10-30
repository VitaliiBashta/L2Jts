package handler.bbs.custom.privateOffice.services.clan.buyClanSkill;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.custom.community.CBuyClanSkillHolder;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.custom.community.BuyClanSkillTemplate;
import org.mmocore.gameserver.utils.Log;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Mangol
 * @since 05.03.2016
 */
public class BuyClanSkillService extends CommunityBoardService
{
	private static final CBuyClanSkillHolder holder = CBuyClanSkillHolder.getInstance();

	@Override
	public Services getService()
	{
		return Services.buy_clan_skill;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final String htm;
		if(bypass.contains("info"))
		{
			final String[] str = bypass.split(":");
			if(str.length < 7)
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.error"));
				useSaveCommand(player);
				return;
			}
			if(!player.isClanLeader())
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.noClanLeader"));
				useSaveCommand(player);
				return;
			}
			if(player.getClan().getLevel() < 5)
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.noClanFiveLevel"));
				useSaveCommand(player);
				return;
			}
			final int id = Converter.convert(Integer.class, str[5]);
			final int level = Converter.convert(Integer.class, str[6]);
			final Optional<BuyClanSkillTemplate> buyClanSkillTemplate = holder.getClanSkill(id, level);
			if(!buyClanSkillTemplate.isPresent())
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.error"));
				useSaveCommand(player);
				return;
			}
			if(buyClanSkillTemplate.get().getMinLevel() > player.getClan().getLevel())
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.clanSkillNotLevel"));
				useSaveCommand(player);
				return;
			}
			final Optional<SkillEntry> skillEntry = player.getClan().getSkills().stream().filter(s -> (s.getId() == buyClanSkillTemplate.get().getId())).findFirst();
			if(skillEntry.isPresent())
			{
				final int entryLevel = skillEntry.get().getLevel();
				if(!(entryLevel == buyClanSkillTemplate.get().getLevel() - 1))
				{
					player.sendPacket(new CustomMessage("bbs.service.clanSkill.skillNotCorrect"));
					useSaveCommand(player);
					return;
				}
			}
			htm = getInformation(player, buyClanSkillTemplate.get());
		}
		else
		{
			final String[] str = bypass.split(":");
			if(str.length < 5)
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.error"));
				useSaveCommand(player);
				return;
			}
			final int page = Converter.convert(Integer.class, str[4]);
			htm = getContent(player, page);
		}
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		final String[] str = bypass.split(":");
		if(str.length < 6)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.error"));
			useSaveCommand(player);
			return;
		}
		if(!player.isClanLeader())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.noClanLeader"));
			useSaveCommand(player);
			return;
		}
		if(player.getClan().getLevel() < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.noClanFiveLevel"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final int id = convert(Integer.class, str[4]);
		final int level = convert(Integer.class, str[5]);
		final Optional<BuyClanSkillTemplate> buyClanSkillTemplate = holder.getClanSkill(id, level);
		if(!buyClanSkillTemplate.isPresent())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.error"));
			useSaveCommand(player);
			return;
		}
		if(buyClanSkillTemplate.get().getMinLevel() > player.getClan().getLevel())
		{
			player.sendPacket(new CustomMessage("bbs.service.clanSkill.clanSkillNotLevel"));
			useSaveCommand(player);
			return;
		}
		final Optional<SkillEntry> skillEntry = player.getClan().getSkills().stream().filter(s -> (s.getId() == buyClanSkillTemplate.get().getId())).findFirst();
		if(skillEntry.isPresent())
		{
			final int entryLevel = skillEntry.get().getLevel();
			if(!(entryLevel == buyClanSkillTemplate.get().getLevel() - 1))
			{
				player.sendPacket(new CustomMessage("bbs.service.clanSkill.skillNotCorrect"));
				useSaveCommand(player);
				return;
			}
		}
		if(!getCheckAndPick(player, buyClanSkillTemplate.get().getItemId(), buyClanSkillTemplate.get().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, buyClanSkillTemplate.get());
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final BuyClanSkillTemplate template = (BuyClanSkillTemplate) params[0];
		final SkillEntry skillEntry = SkillTable.getInstance().getSkillEntry(template.getId(), template.getLevel());
		player.getClan().addSkill(skillEntry, true);
		player.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skillEntry));
		useCommand(player, "_bbsservice:service:buy_clan_skill:content:1");
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyClanSkillService", player, "buy skill id;level - " + template.getId() + ";" + template.getLevel());
	}

	private String getInformation(final Player player, final BuyClanSkillTemplate param)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/info/clan_skill_info.htm", player);
		htm = htm.replace("<?correct_clan_level?>", String.valueOf(player.getClan().getLevel()));
		htm = htm.replace("<?price?>", String.valueOf(param.getItemCount()));
		htm = htm.replace("<?icon?>", (param.getIcon()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), param.getItemId()));
		htm = htm.replace("<?skill_name?>", param.getName(player));
		htm = htm.replace("<?skill_level?>", String.valueOf(param.getLevel()));
		htm = htm.replace("<?skill_desc?>", String.valueOf(param.getDesc(player)));
		final String bypassRequest = "bypass _bbsservice:service:buy_clan_skill:request:" + param.getId() + ":" + param.getLevel();
		htm = htm.replace("<?buy_bypass?>", bypassRequest);
		return htm;
	}

	private String getContent(final Player player, final int page)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/clan_skill.htm", player);
		if(player.isClanLeader())
		{
			final Collection<SkillEntry> clanSkills = player.getClan().getSkills();
			final List<BuyClanSkillTemplate> buyClanSkills = BuyClanSkillUtil.getAvaliableList(holder.getValuesClanSkills(), clanSkills, player.getClan().getLevel());
			if(buyClanSkills.isEmpty())
			{
				final String emptySkill = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/tableEmptySkill.htm", player);
				htm = htm.replace("<?next_back_table?>", "");
				htm = htm.replace("<?tables?>", emptySkill);
				htm = htm.replace("<?correct_clan_level?>", String.valueOf(player.getClan().getLevel()));
			}
			else
			{
				final int countLine = 2;
				final int countColumn = 4;
				final int countColor = countColumn * countLine;
				final int minPageSkill = (page * countColor) - countColor;
				final int maxPageSkill = page * countColor;
				String nextBackButton = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/next_back_button.htm", player);
				String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/table.htm", player);
				String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/button_info.htm", player);
				int correctCount = minPageSkill;
				int correctLine = 1;
				final StringBuilder build = new StringBuilder();
				begin:
				for(; correctLine <= countLine; )
				{
					final StringBuilder buttons = new StringBuilder();
					int correctColumnWrite = 1;
					for(; correctCount <= maxPageSkill; )
					{
						if(correctCount > buyClanSkills.size() - 1)
						{
							if(correctColumnWrite > 1 && correctColumnWrite <= countColumn)
							{
								String tableReplace = table;
								tableReplace = tableReplace.replace("<?buttons?>", buttons.toString());
								build.append(tableReplace);
							}
							break begin;
						}
						final BuyClanSkillTemplate buyClanSkillTemplate = buyClanSkills.get(correctCount);
						if(buyClanSkillTemplate == null)
						{
							break begin;
						}
						String generateButton = button;
						generateButton = generateButton.replace("<?icon?>", buyClanSkillTemplate.getIcon());
						final String skillNames = buyClanSkillTemplate.getName(player).replace(" ", "<br1>");
						generateButton = generateButton.replace("<?skill_name?>", skillNames);
						generateButton = generateButton.replace("<?skill_level?>", String.valueOf(buyClanSkillTemplate.getLevel()));
						generateButton = generateButton.replace("<?info_bypass?>", "bypass _bbsservice:service:buy_clan_skill:content:info:" + buyClanSkillTemplate.getId() + ":" + buyClanSkillTemplate.getLevel());
						buttons.append(generateButton);
						correctCount++;
						// Находимся на последнем столбце, переходим на следующую строку.
						if(correctColumnWrite == countColumn)
						{
							String tableReplace = table;
							tableReplace = tableReplace.replace("<?buttons?>", buttons.toString());
							build.append(tableReplace);
							correctLine++;
							continue begin;
						}
						correctColumnWrite++;
					}
				}
				nextBackButton = nextBackButton.replace("<?page?>", String.valueOf(page));
				nextBackButton = nextBackButton.replace("<?nextPage?>", buyClanSkills.size() > maxPageSkill ? String.valueOf(page + 1) : String.valueOf(page));
				nextBackButton = nextBackButton.replace("<?backPage?>", String.valueOf(page > 1 ? page - 1 : page));
				htm = htm.replace("<?next_back_table?>", nextBackButton);
				htm = htm.replace("<?correct_clan_level?>", String.valueOf(player.getClan().getLevel()));
				htm = htm.replace("<?tables?>", build.toString());
			}
		}
		else
		{
			final String emptySkill = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clanSkill/tableEmptySkill.htm", player);
			htm = htm.replace("<?correct_clan_level?>", new CustomMessage("bbs.service.clanSkill.noClan").toString(player));
			htm = htm.replace("<?next_back_table?>", "");
			htm = htm.replace("<?tables?>", emptySkill);
		}
		return htm;
	}

}
