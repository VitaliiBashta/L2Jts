package handler.bbs.custom.buffer;
import gnu.trove.list.array.TIntArrayList;
import handler.bbs.ScriptBbsHandler;
import handler.bbs.dao.buffer.CommunityBufferDAO;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.listener.actor.OnReviveListener;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.object.components.player.community.Buff;
import org.mmocore.gameserver.object.components.player.community.BuffScheme;
import handler.bbs.model.buffer.GenerateElement;
import org.mmocore.gameserver.object.components.player.community.Scheme;
import org.mmocore.gameserver.object.components.player.community.BuffTask;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CBufferConfig;
import org.mmocore.gameserver.data.xml.holder.custom.community.CBufferHolder;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.custom.CaptureTeamFlagEvent;
import org.mmocore.gameserver.scripts.events.custom.TeamVsTeamEvent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.custom.community.VipBuffTemplate;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: Выпилить в будущем эту херню.
 */
public class CommunityBuffer extends ScriptBbsHandler
{
	private static final Logger log = LoggerFactory.getLogger(CommunityBuffer.class);
	private static final CBufferHolder holder = CBufferHolder.getInstance();
	private static final CommunityBufferDAO dao = CommunityBufferDAO.getInstance();
	private static final TIntArrayList allowBuff = new TIntArrayList(CBufferConfig.allowedBuffs);
	private static List<String> pageBuffPlayer;
	private static List<String> pageBuffPet;
	private static List<String> pageVipBuffPlayer;
	private static List<String> pageVipBuffPet;
	private static List<VipBuffTemplate> vipBuffs;
	private static final StringBuilder buffSchemes = new StringBuilder();

	public String[] getBypassCommands()
	{
		return new String[] { "_bbsbuffer", "_bbsplayerbuffer", "_bbspetbuffer", "_bbscastbuff", "_bbscastgroupbuff", "_bbsbuffersave", "_bbsbufferuse", "_bbsbufferdelete", "_bbsbufferheal", "_bbsbufferremovebuffs", "_bbsplayervipbuffer", "_bbspetvipbuffer" };
	}

	public void onBypassCommand(Player player, String bypass)
	{
		if(!CBufferConfig.enableBuffer)
		{
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		if(!check(player))
		{
			useCommand(player, "_bbshome");
			return;
		}
		String html;
		if(bypass.equals("_bbsbuffer"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/index.htm", player);
			html = html.replace("%scheme%", buffSchemes.toString());
			String template = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/my-sheme.htm", player);
			String block;
			String list = null;
			html = html.replace("{buffTime}", String.valueOf(CBufferConfig.time));
			for(final String name : player.getCommunityComponent().getBuffSchemes().keySet())
			{
				block = template;
				block = block.replace("{bypass}", "bypass _bbsbufferuse " + name + " $Who");
				block = block.replace("{name}", name);
				block = block.replace("{delete}", "bypass _bbsbufferdelete " + name);
				list = list + block;
			}
			final int priceId = CBufferConfig.price[0];
			int priceCount = CBufferConfig.price[1];
			if(CBufferConfig.priceMod)
			{
				priceCount *= player.getLevel();
			}
			if(CBufferConfig.premiumMod && player.getPremiumAccountComponent().hasBonus())
			{
				priceCount = (int) (priceCount * CBufferConfig.paMod);
			}
			if(player.getPlayerClassComponent().getActiveClass().getLevel() < CBufferConfig.freeLevel)
			{
				priceCount = 0;
			}
			html = html.replace("%price%", formatPay(player, priceCount, priceId));
			if(list != null)
			{
				html = html.replace("%buffgrps%", list);
			}
			else
			{
				html = html.replace("%buffgrps%", getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/my-sheme-empty.htm", player));
			}
		}
		else if(bypass.startsWith("_bbsplayerbuffer"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player);
			final StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			final int page = Integer.parseInt(st1.nextToken());
			if(pageBuffPlayer.get(page) != null)
			{
				html = html.replace("%content%", pageBuffPlayer.get(page));
			}
		}
		else if(bypass.startsWith("_bbsplayervipbuffer"))
		{
			if (LostDreamCustom.vipAccessByItemExists
					&& player.getInventory().getItemByItemId(LostDreamCustom.vipAccessItemId) != null
					|| LostDreamCustom.vipAccessForPremium && player.getPremiumAccountComponent().hasBonus()
					|| !LostDreamCustom.vipAccessForPremium && !LostDreamCustom.vipAccessByItemExists) {
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player);
				final StringTokenizer st1 = new StringTokenizer(bypass, ":");
				st1.nextToken();
				final int page = Integer.parseInt(st1.nextToken());
				if(pageVipBuffPlayer.get(page) != null)
				{
					html = html.replace("%content%", pageVipBuffPlayer.get(page));
				}
			} else {
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player)
						.replaceFirst("%content%", "You are not a vip member!");
			}
		}
		else if(bypass.startsWith("_bbspetbuffer"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player);
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if(pageBuffPet.get(page) != null)
			{
				html = html.replace("%content%", pageBuffPet.get(page));
			}
		}
		else if(bypass.startsWith("_bbspetvipbuffer"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player);
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if(pageVipBuffPet.get(page) != null)
			{
				html = html.replace("%content%", pageVipBuffPet.get(page));
			}
		}
		else if(bypass.startsWith("_bbscastbuff"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/buffer/scheme.htm", player);
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();

			final int id = Integer.parseInt(st1.nextToken());
			final int level = Integer.parseInt(st1.nextToken());
			final int page = Integer.parseInt(st1.nextToken());
			final String type = st1.nextToken();
			final boolean isVip = st1.hasMoreTokens();

			Playable playable = null;
			if("Player".equals(type))
			{
				playable = player;
			}
			else if("Pet".equals(type))
			{
				playable = player.getServitor();
			}
			final int check = allowBuff.indexOf(id);
			final int priceId = CBufferConfig.price[0];
			int priceCount = CBufferConfig.price[1];
			if(CBufferConfig.priceMod)
			{
				priceCount *= player.getLevel();
			}
			if (isVip && getVipBuffTemplate(id, level) != null)
				buffVip(player, playable, id, level);
			else
			if(playable != null && check != -1 && allowBuff.get(check + 1) <= level)
			{
				if(player.getPlayerClassComponent().getActiveClass().getLevel() < CBufferConfig.freeLevel)
				{
					buffList(Collections.singletonList(new Buff(id, level)), playable);
				}
				else if(getPay(player, priceId, priceCount, true))
				{
					buffList(Collections.singletonList(new Buff(id, level)), playable);
				}
			}
			if("Player".equals(type))
			{
				html = html.replace("%content%", isVip ? pageVipBuffPlayer.get(page) : pageBuffPlayer.get(page));
			}
			else if("Pet".equals(type))
			{
				html = html.replace("%content%", isVip ? pageVipBuffPet.get(page) : pageBuffPet.get(page));
			}
		}
		else
		{
			if(bypass.startsWith("_bbscastgroupbuff"))
			{
				final StringTokenizer st1 = new StringTokenizer(bypass, " ");
				st1.nextToken();
				final int id = Integer.parseInt(st1.nextToken());
				final String type = st1.nextToken();
				final BuffScheme buffScheme = holder.getBuffScheme(id);
				final int priceId = buffScheme.getPriceId();
				final long priceCount = buffScheme.getPriceCount();

				Playable playable = null;
				if("Player".equals(type))
				{
					playable = player;
				}
				else if("Pet".equals(type))
				{
					playable = player.getServitor();
				}
				if(playable != null)
				{
					if(player.getPlayerClassComponent().getActiveClass().getLevel() < CBufferConfig.freeLevel)
					{
						buffList(buffScheme.getBuffIds(), playable);
					}
					else if(getPay(player, priceId, priceCount, true))
					{
						buffList(buffScheme.getBuffIds(), playable);
					}
				}
				useCommand(player, "_bbsbuffer");
				return;
			}
			if(bypass.startsWith("_bbsbuffersave"))
			{
				if(player.getCommunityComponent().getBuffSchemes().size() >= 5)
				{
					player.sendMessage("You can create only 4 scheme!");
					useCommand(player, "_bbsbuffer");
					return;
				}
				final StringTokenizer st1 = new StringTokenizer(bypass, " ");
				if(st1.countTokens() < 3)
				{
					useCommand(player, "_bbsbuffer");
					return;
				}
				st1.nextToken();

				final String name = st1.nextToken();
				final String type = st1.nextToken();

				Playable playable = null;
				if("Player".equals(type))
				{
					playable = player;
				}
				else if("Pet".equals(type))
				{
					playable = player.getServitor();
				}
				if(playable == null)
				{
					return;
				}
				if(playable.getEffectList().getAllEffects().size() == 0)
				{
					useCommand(player, "_bbsbuffer");
					return;
				}
				if(player.getCommunityComponent().getBuffScheme(name).isPresent())
				{
					player.sendMessage("Такое название уже существует!");
					useCommand(player, "_bbsbuffer");
					return;
				}
				if(getPay(player, CBufferConfig.saveId, CBufferConfig.savePrice, true))
				{
					final StringBuilder buffs = new StringBuilder();
					final Scheme scheme = new Scheme(name);
					for(final Effect effect : playable.getEffectList().getAllEffects())
					{
						final SkillEntry skill = effect.getSkill();
						final int id = skill.getId();
						int level = skill.getLevel();
						final int check = allowBuff.indexOf(skill.getId());
						level = level > allowBuff.get(check + 1) ? allowBuff.get(check + 1) : level;
						if(check != -1)
						{
							buffs.append(id).append(",").append(level).append(";");
							scheme.addBuff(id, level);
						}
					}
					dao.insert(player, buffs.toString(), scheme);
				}
				useCommand(player, "_bbsbuffer");
				return;
			}
			if(bypass.startsWith("_bbsbufferuse"))
			{
				StringTokenizer st1 = new StringTokenizer(bypass, " ");
				st1.nextToken();
				String name = st1.nextToken();
				String type = st1.nextToken();

				Playable playable = null;
				if("Player".equals(type))
				{
					playable = player;
				}
				else if("Pet".equals(type))
				{
					playable = player.getServitor();
				}
				int priceId = CBufferConfig.price[0];
				int priceCount = CBufferConfig.price[1];
				if(CBufferConfig.priceMod)
				{
					priceCount *= player.getLevel();
				}
				if(CBufferConfig.priceMod && player.getPremiumAccountComponent().hasBonus())
				{
					priceCount = (int) (priceCount * CBufferConfig.paMod);
				}
				if(playable != null)
				{
					final List<Buff> buffs = new ArrayList<>();
					final Optional<Scheme> my = player.getCommunityComponent().getBuffScheme(name);
					if(my.isPresent())
					{
						if(player.getPlayerClassComponent().getActiveClass().getLevel() > CBufferConfig.freeLevel)
						{
							priceCount *= my.get().getBuffs().size();
						}
						else
						{
							priceCount = 0;
						}
						if(getPay(player, priceId, priceCount, true))
						{
							buffs.addAll(my.get().getBuffs().entrySet().stream().filter(scheme -> allowBuff.indexOf(scheme.getKey()) != -1).map(scheme -> new Buff(scheme.getKey(), scheme.getValue())).collect(Collectors.toList()));
						}
						buffList(buffs, playable);
					}
				}
				useCommand(player, "_bbsbuffer");
				return;
			}
			if(bypass.startsWith("_bbsbufferdelete"))
			{
				final StringTokenizer st1 = new StringTokenizer(bypass, " ");
				st1.nextToken();
				final String name = st1.nextToken();
				dao.delete(player, name);
				useCommand(player, "_bbsbuffer");
				return;
			}
			if(bypass.startsWith("_bbsbufferheal"))
			{
				if(!CBufferConfig.recover)
				{
					player.sendMessage(new CustomMessage("scripts.services.off"));
				}
				else if(CBufferConfig.recoverPvPFlag && player.getPvpFlag() > 0 || CBufferConfig.recoverBattle && player.isInCombat())
				{
					player.sendMessage(new CustomMessage("scripts.services.combat.enable"));
				}
				else
				{
					StringTokenizer st1 = new StringTokenizer(bypass, " ");
					st1.nextToken();
					String type = st1.nextToken();
					String target = st1.nextToken();

					Playable playable = null;
					if("Player".equals(target))
					{
						playable = player;
					}
					else if("Pet".equals(target))
					{
						playable = player.getServitor();
					}
					if(playable != null)
					{
						if((CBufferConfig.peaceRecover) && (!playable.isInPeaceZone()))
						{
							if(playable.isPlayer())
							{
								player.sendMessage(new CustomMessage("scripts.services.buffer.peaceZone"));
							}
							useCommand(player, "_bbsbuffer");
							return;
						}
						if(getPay(player, CBufferConfig.recoverPrice[0], CBufferConfig.recoverPrice[1], true))
						{
							if("HP".equals(type))
							{
								if(playable.getCurrentHp() != playable.getMaxHp())
								{
									playable.setCurrentHp(playable.getMaxHp(), true, true);
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
							else if("MP".equals(type))
							{
								if(playable.getCurrentMp() != playable.getMaxMp())
								{
									playable.setCurrentMp(playable.getMaxMp(), true);
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
							else if("CP".equals(type))
							{
								if(playable.getCurrentCp() != playable.getMaxCp())
								{
									playable.setCurrentCp(playable.getMaxCp(), true);
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
						}
					}
				}
				useCommand(player, "_bbsbuffer");
				return;
			}
			if(bypass.startsWith("_bbsbufferremovebuffs"))
			{
				if(!CBufferConfig.clear)
				{
					player.sendMessage(new CustomMessage("scripts.services.off"));
				}
				else
				{
					StringTokenizer st1 = new StringTokenizer(bypass, " ");
					st1.nextToken();
					String type = st1.nextToken();

					Playable playable = null;
					if("Player".equals(type))
					{
						playable = player;
					}
					else if("Pet".equals(type))
					{
						playable = player.getServitor();
					}
					if(playable != null)
					{
						playable.getEffectList().getAllEffects().stream().filter(effect -> (effect.getSkill().getSkillType() == Skill.SkillType.BUFF) || (effect.getSkill().getSkillType() == Skill.SkillType.COMBATPOINTHEAL)).forEach(org.mmocore.gameserver.model.Effect::exit);
						playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
					}
				}
				useCommand(player, "_bbsbuffer");
				return;
			}
			player.sendMessage("Error");
			return;
		}
		separateAndSend(html, player);
	}

	private static void genPage(final List<String> list, final String type) {
		genPage(list, type, false);
	}

	private static void genPage(final List<String> list, final String type, boolean isVip)
	{
		final StringBuilder sb = new StringBuilder("<table><tr>");
		sb.append("<td width=70>Navigation: </td>");
		for(int i = 0; i < list.size(); i++)
		{
			sb.append(GenerateElement.buttonTD(String.valueOf(i + 1), "_bbs" + type + (isVip ? "vip" : "") + "buffer:" + i, 30, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title"));
		}
		sb.append("<td>").append(GenerateElement.button("Back", "_bbsbuffer", 60, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title")).append("</td></tr></table><br><br>");
		for(int i = 0; i < list.size(); i++)
		{
			list.set(i, sb.toString() + list.get(i));
		}
	}

	private static void genPageBuff(final List<String> list, final int start, final String type)
	{
		final StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		boolean next = false;
		for(; i < allowBuff.size(); i += 2)
		{
			if(next && i % 12 == 0)
			{
				buffPages.append("</tr><tr>");
			}
			if(next && i % 48 == 0)
			{
				break;
			}
			buffPages.append("<td>").append(buttonBuff(allowBuff.get(i), allowBuff.get(i + 1), list.size(), type)).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");
		list.add(buffPages.toString());
		if(i + 2 <= allowBuff.size())
		{
			genPageBuff(list, i, type);
		}
	}

	private static void genVipPageBuff(final List<String> list, final int start, final String type) {
		final StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		boolean next = false;
		for(; i < vipBuffs.size(); i++)
		{
			if(next && i % 6 == 0)
			{
				buffPages.append("</tr><tr>");
			}
			if(next && i % 18 == 0)
			{
				break;
			}
			VipBuffTemplate vipBuffTemplate = getVipBuffTemplate(vipBuffs.get(i).getId(), vipBuffs.get(i).getLevel());
			if (vipBuffTemplate != null)
				buffPages.append("<td>").append(buttonBuff(vipBuffs.get(i).getId(), vipBuffs.get(i).getLevel(), list.size(),
						type, vipBuffTemplate.getItemId(), vipBuffTemplate.getItemCount())).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");
		list.add(buffPages.toString());
		if(i + 1 <= vipBuffs.size())
		{
			genVipPageBuff(list, i, type);
		}
	}

	private static String buttonBuff(int id, int level, int page, String type) {
		return buttonBuff(id, level, page, type, 0, 0);
	}

	private static String buttonBuff(int id, int level, int page, String type, int itemId, int itemCount)
	{
		String skillId = Integer.toString(id);
		StringBuilder sb = new StringBuilder("<table width=100>");
		String icon;

		if(skillId.length() < 4)
			icon = 0 + skillId;
		else if(skillId.length() < 3)
			icon = 00 + skillId;
		else if(id == 4700 || id == 4699)
			icon = "1331";
		else if(id == 4702 || id == 4703)
			icon = "1332";
		else if(id == 1517)
			icon = "1499";
		else if(id == 1518)
			icon = "1502";
		else
			icon = skillId;

		String name = SkillTable.getInstance().getSkillEntry(id, level).getName();
		name = name.replace("Dance of the", "D.");
		name = name.replace("Dance of", "D.");
		name = name.replace("Song of", "S.");
		name = name.replace("Improved", "I.");
		name = name.replace("Awakening", "A.");
		name = name.replace("Blessing", "Bless.");
		name = name.replace("Protection", "Protect.");
		name = name.replace("Critical", "C.");
		name = name.replace("Condition", "Con.");
		sb.append("<tr><td><center><img src=icon.skill").append(icon).append(" width=32 height=32><br>");
		if (itemId != 0)
			sb.append("<font color=437AD0>Price: </font>" + itemCount + " ")
					.append(ItemTemplateHolder.getInstance().getTemplate(itemId).getName()).append("<br>");
		sb.append("<font color=F2C202>Level ").append(level).append("</font></center></td></tr>");
		sb.append(GenerateElement.buttonTR(name, "_bbscastbuff:" + id + ":" + level + ":" + page + ":" + type + (itemId != 0 ? ":vip" : ""), 100, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title"));
		sb.append("</table>");
		return sb.toString();
	}

	private static void buffList(List<Buff> list, Playable playable)
	{
		new BuffTask(list, playable).run();
	}

	private boolean check(Player player)
	{
		if(OlympiadConfig.ENABLE_OLYMPIAD && (Olympiad.isRegisteredInComp(player) || player.isInOlympiadMode()))
		{
			player.sendMessage(new CustomMessage("scripts.services.olympiad.enable"));
			return false;
		}
		if(!CBufferConfig.inSiege && player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage(new CustomMessage("scripts.services.siege.disable"));
			return false;
		}
		if(!CBufferConfig.inPvP && player.getPvpFlag() > 0 || !CBufferConfig.inBattle && (player.isInCombat() || player.isMovementDisabled()))
		{
			player.sendMessage(new CustomMessage("scripts.services.combat.enable"));
			return false;
		}
		if(!CBufferConfig.onEvents && player.getTeam() != TeamType.NONE)
		{
			if (player.getEvent(TeamVsTeamEvent.class) != null) {
				if (!CBufferConfig.onTvt) {
					player.sendMessage(new CustomMessage("scripts.services.events"));
					return false;
				}
			} else if (player.getEvent(CaptureTeamFlagEvent.class) != null) {
				if (!CBufferConfig.onCtf) {
					player.sendMessage(new CustomMessage("scripts.services.events"));
					return false;
				}
			} else {
				player.sendMessage(new CustomMessage("scripts.services.events"));
				return false;
			}
		}
		if(!checkLevel(player.getLevel()))
		{
			final int min = CBufferConfig.minLevel;
			final int max = CBufferConfig.maxLevel;
			player.sendMessage(new CustomMessage("scripts.services.minMaxLevel").addNumber(min).addNumber(max));
			return false;
		}
		if (player.getDieTime() + CBufferConfig.rejectAfterDeathSec * 1000 > System.currentTimeMillis()) {
//			player.sendMessage(new CustomMessage("scripts.services.combat.enable"));
			AnnouncementUtils.splashToPlayer(player, "Недоступно после смерти!", "Unable after death!");
			return false;
		}
		if (player.isDead())
			return false;
		return true;
	}

	private boolean checkLevel(int level)
	{
		return level >= CBufferConfig.minLevel && level <= CBufferConfig.maxLevel;
	}

	public void onInit()
	{
		super.onInit();
		pageBuffPlayer = new ArrayList<>();
		pageBuffPet = new ArrayList<>();
		pageVipBuffPlayer = new ArrayList<>();
		pageVipBuffPet = new ArrayList<>();
		genPageBuff(pageBuffPlayer, 0, "Player");
		genPage(pageBuffPlayer, "player");
		genPageBuff(pageBuffPet, 0, "Pet");
		genPage(pageBuffPet, "pet");
		if (LostDreamCustom.vipBuffs != null && !LostDreamCustom.vipBuffs.equals("")) {
			parseVipBuffs();
			genVipPageBuff(pageVipBuffPlayer, 0, "Player");
			genPage(pageVipBuffPlayer, "player", true);
			genVipPageBuff(pageVipBuffPet, 0, "Pet");
			genPage(pageVipBuffPet, "pet", true);
		}
		PlayerListenerList.addGlobal(new OnReviveListener() {
			@Override
			public void onRevive(Creature actor) {
				if (actor == null || actor.getPlayer() == null)
					return;
				actor.getPlayer().setDieTime();
			}
		});
	}

	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	private void parseVipBuffs() {
		try {
			vipBuffs = Util.parseTemplateConfig(LostDreamCustom.vipBuffs, VipBuffTemplate.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buffVip(Player player, Playable playable, int id, int lvl) {
		VipBuffTemplate vipBuffTemplate = getVipBuffTemplate(id, lvl);
		if (vipBuffTemplate == null)
			return;

		if(getPay(player, vipBuffTemplate.getItemId(), vipBuffTemplate.getItemCount(), true))
		{
			buffList(Collections.singletonList(new Buff(id, lvl)), playable);
		}
	}

	private static VipBuffTemplate getVipBuffTemplate(int id, int lvl) {
		for (VipBuffTemplate template : vipBuffs)
			if (template.getId() == id && template.getLevel() == lvl)
				return template;
		return null;
	}
}
