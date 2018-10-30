package services;

import gnu.trove.iterator.TIntObjectIterator;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.mysql;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.PlayerClass;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.AccountPlayerInfo;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Rename
{
	@Bypass("services.Rename:rename_page")
	public void rename_page(Player player, NpcInstance npc, String[] arg)
	{
		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/rename_char.htm");
		msg.replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_CHANGE_NICK_ITEM));
		msg.replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_CHANGE_NICK_PRICE));
		player.sendPacket(msg);
	}

	@Bypass("services.Rename:rename")
	public void rename(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_err01.htm"));
			return;
		}

		if(player.isHero() || player.isClanLeader())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_err03.htm"));
			return;
		}

		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_err03.htm"));
			return;
		}

		String name = arg[0];
		if(!Util.isMatchingRegexp(name, ServerConfig.CNAME_TEMPLATE)
				|| NpcNameLineHolder.getInstance().isBlackListContainsName(name)
				|| !Util.checkIsAllowedName(name))
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_err01.htm"));
			return;
		}

		if(CharacterDAO.getInstance().getObjectIdByName(name) > 0)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_err02.htm"));
			return;
		}

		if(ServicesConfig.SERVICES_CHANGE_NICK_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_CHANGE_NICK_PRICE)
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_CHANGE_NICK_PRICE, true));
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
				return;
			}
		}
		else if(ItemFunctions.getItemCount(player, ServicesConfig.SERVICES_CHANGE_NICK_ITEM) < ServicesConfig.SERVICES_CHANGE_NICK_PRICE)
		{
			if(ServicesConfig.SERVICES_CHANGE_NICK_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}
		else
			ItemFunctions.removeItem(player, ServicesConfig.SERVICES_CHANGE_NICK_ITEM, ServicesConfig.SERVICES_CHANGE_NICK_PRICE, true);

		String oldName = player.getName();
		player.reName(name, true);

		Log.add("Character " + '(' + player.getObjectId() + ") " + oldName + " renamed to " + name, "renames");

		player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_char_msg01.htm").replace("%old_name%", oldName).replace("%new_name%", name));
	}

	@Bypass("services.Rename:separate_page")
	public void separate_page(Player player, NpcInstance npc, String[] arg)
	{
		if(player.getPlayerClassComponent().getSubClasses().size() == 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err01.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/subclass_separate.htm");
		msg.replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_SEPARATE_SUB_ITEM));
		msg.replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_SEPARATE_SUB_PRICE));

		String item = HtmCache.getInstance().getHtml("scripts/services/subclass_separate_list.htm", player);

		StringBuilder sb = new StringBuilder();
		for(SubClass s : player.getPlayerClassComponent().getSubClasses().values())
		{
			if(!s.isBase() && s.getClassId() != ClassId.inspector.getId() && s.getClassId() != ClassId.judicator.getId())
			{
				sb.append(item.replace("%class_id%", String.valueOf(s.getClassId())));
			}
		}

		msg.replace("%list%", sb.toString());

		player.sendPacket(msg);
	}

	@Bypass("services.Rename:separate")
	public void separate(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 2)
		{
			return;
		}

		if(player.getPlayerClassComponent().getSubClasses().size() == 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err01.htm"));
			return;
		}

		if(!player.getPlayerClassComponent().getActiveClass().isBase())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err03.htm"));
			return;
		}

		if(player.getPlayerClassComponent().getActiveClass().getLevel() < 75)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err04.htm"));
			return;
		}

		if(player.isHero())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err05.htm"));
			return;
		}

		int classtomove = Integer.parseInt(arg[0]);
		int newcharid = 0;

		TIntObjectIterator<AccountPlayerInfo> iterator = player.getAccountChars().iterator();
		for(int i = player.getAccountChars().size(); i > 0; i--)
		{
			iterator.advance();
			if(iterator.value().getName().equalsIgnoreCase(arg[1]))
			{
				newcharid = iterator.key();
				break;
			}
		}

		if(newcharid == 0)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err06.htm"));
			return;
		}

		if(classtomove == ClassId.inspector.getId() || classtomove == ClassId.judicator.getId())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err02.htm"));
			return;
		}

		if(mysql.simple_get_int("level", "character_subclasses", "char_obj_id=" + newcharid + " AND level > 1") > 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err07.htm"));
			return;
		}

		if(!player.isInPeaceZone() || !player.getReflection().isDefault())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/subclass_separate_err08.htm"));
			return;
		}

		if(ItemFunctions.getItemCount(player, ServicesConfig.SERVICES_SEPARATE_SUB_ITEM) < ServicesConfig.SERVICES_SEPARATE_SUB_PRICE)
		{
			if(ServicesConfig.SERVICES_SEPARATE_SUB_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}

		ItemFunctions.removeItem(player, ServicesConfig.SERVICES_CHANGE_BASE_ITEM, ServicesConfig.SERVICES_CHANGE_BASE_PRICE, true);

		mysql.set("DELETE FROM character_subclasses WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills_save WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_hennas WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_shortcuts WHERE object_id=" + newcharid);
		mysql.set("DELETE FROM character_variables WHERE obj_id=" + newcharid);

		mysql.set("UPDATE character_subclasses SET char_obj_id=" + newcharid + ", isBase=1, certification=0 WHERE char_obj_id=" + player.getObjectId() + " AND class_id=" + classtomove);
		mysql.set("UPDATE character_skills SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_skills_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_hennas SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_shortcuts SET object_id=" + newcharid + " WHERE object_id=" + player.getObjectId() + " AND class_index=" + classtomove);

		// TODO: TransferSkills нигде не добавляется
		//mysql.set("UPDATE character_variables SET obj_id=" + newcharid + " WHERE obj_id=" + player.getObjectId() + " AND name like 'TransferSkills%'");

		player.getPlayerClassComponent().modifySubClass(classtomove, 0);

		player.logout();

		Log.add("Character " + '(' + player.getObjectId() + ") " + player + " subclass separated to " + arg[1], "services");
	}

	@Bypass("services.Rename:changebase_page")
	public void changebase_page(Player player, NpcInstance npc, String[] arg)
	{
		if(player.getPlayerClassComponent().getSubClasses().size() == 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err01.htm"));
			return;
		}

		if(!player.getPlayerClassComponent().getActiveClass().isBase())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/changebase.htm");
		msg.replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_CHANGE_BASE_ITEM));
		msg.replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_CHANGE_BASE_PRICE));

		List<SubClass> possible = new ArrayList<SubClass>();
		if(player.getPlayerClassComponent().getActiveClass().isBase())
		{
			possible.addAll(player.getPlayerClassComponent().getSubClasses().values());
			possible.remove(player.getPlayerClassComponent().getSubClasses().get(player.getPlayerClassComponent().getBaseClassId()));

			for(SubClass s : player.getPlayerClassComponent().getSubClasses().values())
			{
				for(SubClass s2 : player.getPlayerClassComponent().getSubClasses().values())
				{
					if(s != s2 && !PlayerClass.areClassesComportable(PlayerClass.values()[s.getClassId()], PlayerClass.values()[s2.getClassId()]) || s2.getLevel() < 75)
					{
						possible.remove(s2);
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		if(!possible.isEmpty())
		{
			String item = HtmCache.getInstance().getHtml("scripts/services/changebase_list.htm", player);
			for(SubClass s : possible)
			{
				sb.append(item.replace("%class_id%", String.valueOf(s.getClassId())));
			}
		}

		msg.replace("%list%", sb.toString());

		player.sendPacket(msg);
	}

	@Bypass("services.Rename:changebase")
	public void changebase(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 1)
		{
			return;
		}

		if(player.getPlayerClassComponent().getSubClasses().size() == 1)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err01.htm"));
			return;
		}

		if(!player.getPlayerClassComponent().getActiveClass().isBase())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err02.htm"));
			return;
		}

		if(!player.isInPeaceZone() || !player.getReflection().isDefault())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err03.htm"));
			return;
		}

		if(player.isHero())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/changebase_err04.htm"));
			return;
		}

		if(ItemFunctions.getItemCount(player, ServicesConfig.SERVICES_CHANGE_BASE_ITEM) < ServicesConfig.SERVICES_CHANGE_BASE_PRICE)
		{
			if(ServicesConfig.SERVICES_CHANGE_BASE_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}

		ItemFunctions.removeItem(player, ServicesConfig.SERVICES_CHANGE_BASE_ITEM, ServicesConfig.SERVICES_CHANGE_BASE_PRICE, true);

		int target = Integer.parseInt(arg[0]);
		SubClass newBase = player.getPlayerClassComponent().getSubClasses().get(target);

		player.getPlayerClassComponent().getActiveClass().setBase(false);
		player.getPlayerClassComponent().getActiveClass().setCertification(newBase.getCertification());

		newBase.setCertification(0);
		player.getPlayerClassComponent().getActiveClass().setExp(player.getExp());
		player.checkSkills();

		newBase.setBase(true);

		player.getPlayerClassComponent().setBaseClass(target);

		player.getAppearanceComponent().setHairColor(0);
		player.getAppearanceComponent().setHairStyle(0);
		player.getAppearanceComponent().setFace(0);

		Olympiad.unRegisterNoble(player);

		player.logout();

		Log.add("Character " + '(' + player.getObjectId() + ") " + player + " base changed to " + target, "services");
	}

	@Bypass("services.Rename:changesex_page")
	public void changesex_page(Player player, NpcInstance npc, String[] arg)
	{
		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/sex_change.htm");
		msg.replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_CHANGE_SEX_ITEM));
		msg.replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_CHANGE_SEX_PRICE));
		player.sendPacket(msg);
	}

	@Bypass("services.Rename:changesex")
	public void changesex(Player player, NpcInstance npc, String[] arg)
	{
		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/sex_change_err01.htm"));
			return;
		}

		if(!player.isInPeaceZone() || !player.getReflection().isDefault())
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/sex_change_err02.htm"));
			return;
		}

		if(ServicesConfig.SERVICES_CHANGE_SEX_ITEM == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_CHANGE_SEX_PRICE)
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_CHANGE_SEX_PRICE, true));
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
				return;
			}
		}
		else if(ItemFunctions.getItemCount(player, ServicesConfig.SERVICES_CHANGE_SEX_ITEM) < ServicesConfig.SERVICES_CHANGE_SEX_PRICE)
		{
			if(ServicesConfig.SERVICES_CHANGE_SEX_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}
		else
			ItemFunctions.removeItem(player, ServicesConfig.SERVICES_CHANGE_SEX_ITEM, ServicesConfig.SERVICES_CHANGE_SEX_PRICE, true);

		Connection con = null;
		PreparedStatement offline = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?");
			offline.setInt(1, player.getSex() == 1 ? 0 : 1);
			offline.setInt(2, player.getObjectId());
			offline.executeUpdate();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			DbUtils.closeQuietly(con, offline);
		}

		player.getAppearanceComponent().setHairColor(0);
		player.getAppearanceComponent().setHairStyle(0);
		player.getAppearanceComponent().setFace(0);

		player.logout();

		Log.add("Character " + '(' + player.getObjectId() + ") " + player + " sex changed to " + (player.getSex() == 1 ? "male" : "female"), "renames");
	}

	@Bypass("services.Rename:rename_clan_page")
	public void rename_clan_page(Player player, NpcInstance npc, String[] arg)
	{
		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/rename_clan.htm");
		msg.replace("%item_id%", String.valueOf(ServicesConfig.SERVICES_CHANGE_CLAN_NAME_ITEM));
		msg.replace("%item_count%", String.valueOf(ServicesConfig.SERVICES_CHANGE_CLAN_NAME_PRICE));
		player.sendPacket(msg);
	}

	@Bypass("services.Rename:rename_clan")
	public void rename_clan(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 1)
		{
			return;
		}

		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		if(!Util.isMatchingRegexp(arg[0], ServerConfig.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_clan_err01.htm"));
			return;
		}
		if(ClanTable.getInstance().getClanByName(arg[0]) != null)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_clan_err02.htm"));
			return;
		}

		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_clan_err03.htm"));
			return;
		}

		if(ItemFunctions.getItemCount(player, ServicesConfig.SERVICES_CHANGE_CLAN_NAME_ITEM) < ServicesConfig.SERVICES_CHANGE_CLAN_NAME_PRICE)
		{
			if(ServicesConfig.SERVICES_CHANGE_CLAN_NAME_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}

		ItemFunctions.removeItem(player, ServicesConfig.SERVICES_CHANGE_CLAN_NAME_ITEM, ServicesConfig.SERVICES_CHANGE_CLAN_NAME_PRICE, true);

		String name = arg[0];
		SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		String oldName = sub.getName();
		sub.setName(name, true);
		player.getClan().broadcastClanStatus(true, true, false);

		Log.add("Character " + '(' + player.getObjectId() + ") " + player.getName() + " rename clan " + oldName + " to new " + name, "renames");

		player.sendPacket(new HtmlMessage(5).setFile("scripts/services/rename_clan_msg01.htm").replace("%old_name%", oldName).replace("%new_name%", name));
	}
}
