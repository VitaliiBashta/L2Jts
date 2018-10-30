package handler.voicecommands;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterVariablesDAO;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CWHPrivileges extends ScriptIVoiceHandler
{
	private String[] _commandList = { "clan" };

	@Override
	public boolean useVoicedCommand(final String command, final Player activeChar, final String args)
	{
		if(activeChar.getClan() == null)
		{
			return false;
		}
		if("clan".equals(command))
		{
			if(AllSettingsConfig.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER && !activeChar.isClanLeader())
			{
				return false;
			}
			if(!((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS))
			{
				return false;
			}
			if(args != null)
			{
				final String[] param = args.split(" ");
				if(param.length > 0)
				{
					if("allowwh".equalsIgnoreCase(param[0]) && param.length > 1)
					{
						final UnitMember cm = activeChar.getClan().getAnyMember(param[1]);
						if(cm != null && cm.getPlayer() != null) // цель онлайн
						{
							if(cm.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.CAN_WAREHOUSE_WITHDRAW))
							{
								cm.getPlayer().getPlayerVariables().remove(PlayerVariables.CAN_WAREHOUSE_WITHDRAW);
								activeChar.sendMessage("Privilege removed successfully");
							}
							else
							{
								cm.getPlayer().getPlayerVariables().set(PlayerVariables.CAN_WAREHOUSE_WITHDRAW, "1", -1);
								activeChar.sendMessage("Privilege given successfully");
							}
						}
						else if(cm != null) // цель оффлайн
						{
							final boolean givenOrRemoved = CharacterVariablesDAO.getInstance().changeClanWarehousePrivileges(cm.getObjectId());

							if(givenOrRemoved)
							{
								activeChar.sendMessage("Privilege given successfully");
							}
							else
							{
								activeChar.sendMessage("Privilege removed successfully");
							}
						}
						else
						{
							activeChar.sendMessage("Player not found.");
						}
					}
					else if("list".equalsIgnoreCase(param[0]))
					{
						final IntStream membersStream = activeChar.getClan().getAllMembers().stream().mapToInt(UnitMember::getObjectId);
						final List<Integer> memberWithVariableIds = CharacterVariablesDAO.getInstance()
								.selectPlayersWithSpecifiedVariable(membersStream, PlayerVariables.CAN_WAREHOUSE_WITHDRAW);

						final StringBuilder sb = new StringBuilder("<html><body>Clan CP (.clan)<br><br><table>");

						final List<UnitMember> membersWithVariable = activeChar.getClan().getAllMembers().stream()
								.filter(member -> memberWithVariableIds.contains(member.getObjectId())).collect(Collectors.toList());

						membersWithVariable.forEach(member ->
								                            sb.append("<tr><td width=10></td><td width=60>").append(member.getName())
										                            .append("</td><td width=20><button width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan allowwh ")
										                            .append(member.getName()).append("\" value=\"Remove\">").append("<br></td></tr>"));
						sb.append("<tr><td width=10></td><td width=20><button width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>");

						Functions.show(sb.toString(), activeChar, null);
						return true;
					}
				}
			}
			String dialog = HtmCache.getInstance().getHtml("scripts/services/clan.htm", activeChar);
			if(ServicesConfig.SERVICES_EXPAND_CWH_ENABLED)
			{
				dialog = dialog.replaceFirst("%whextprice%", ServicesConfig.SERVICES_EXPAND_CWH_PRICE + " " + ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_EXPAND_CWH_ITEM).getName());
			}
			else
			{
				dialog = dialog.replaceFirst("%whextprice%", "service disabled");
			}
			Functions.show(dialog, activeChar, null);
			return true;
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}