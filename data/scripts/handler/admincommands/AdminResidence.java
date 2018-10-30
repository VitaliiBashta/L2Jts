package handler.admincommands;

import org.mmocore.gameserver.scripts.npc.model.residences.fortress.siege.BackupPowerUnitInstance;
import org.mmocore.gameserver.scripts.npc.model.residences.fortress.siege.PowerControlUnitInstance;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 15:10/06.03.2011
 */
public class AdminResidence extends ScriptAdminCommand
{
	private static enum Commands
	{
		admin_residence_list,
		admin_residence,
		admin_set_owner,
		admin_set_siege_time,
		// dominion
		admin_start_dominion_war,
		admin_stop_dominion_war,
		admin_set_dominion_time,
		//
		admin_quick_siege_start,
		admin_quick_siege_stop,
		// fortress
		admin_backup_unit_info,
		admin_fortress_spawn_flags
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
		{
			return false;
		}

		final Residence r;
		final SiegeEvent<?, ?> event;
		ZonedDateTime siegeDate;
		HtmlMessage msg;
		final DominionSiegeRunnerEvent runnerEvent;
		switch(command)
		{
			case admin_residence_list:
				msg = new HtmlMessage(5);
				msg.setFile("admin/residence/residence_list.htm");

				StringBuilder replyMSG = new StringBuilder(200);
				ResidenceHolder.getInstance().getResidences().stream().filter(residence -> residence != null).forEach(residence -> {
					replyMSG.append("<tr><td>");
					replyMSG.append("<a action=\"bypass -h admin_residence ").append(residence.getId()).append("\">").append(HtmlUtils.htmlResidenceName(residence.getId())).append("</a>");
					replyMSG.append("</td><td>");

					Clan owner = residence.getOwner();
					if(owner == null)
					{
						replyMSG.append("NPC");
					}
					else
					{
						replyMSG.append(owner.getName());
					}

					replyMSG.append("</td></tr>");
				});
				msg.replace("%residence_list%", replyMSG.toString());
				activeChar.sendPacket(msg);
				break;
			case admin_residence:
				if(wordList.length != 2)
				{
					return false;
				}
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
				{
					return false;
				}
				event = r.getSiegeEvent();
				msg = new HtmlMessage(5);
				if(r instanceof Dominion)
				{
					msg.setFile("admin/residence/dominion_siege_info.htm");
					msg.replace("%residence%", HtmlUtils.htmlResidenceName(r.getId()));
					msg.replace("%id%", String.valueOf(r.getId()));
					msg.replace("%owner%", r.getOwner() == null ? "NPC" : r.getOwner().getName());

					StringBuilder builder = new StringBuilder(100);
					List<SiegeClanObject> clans = event.getObjects(SiegeEvent.ATTACKERS);
					for(SiegeClanObject clan : clans)
					{
						builder.append("<tr>").append("<td>").append(clan.getClan().getName()).append("</td>").append("<td>").append(clan.getClan().getLeaderName()).append("</td>")
								.append("<td>").append(SiegeEvent.ATTACKERS).append("</td>").append("</tr>");
					}

					clans = event.getObjects(SiegeEvent.DEFENDERS);
					for(SiegeClanObject clan : clans)
					{
						builder.append("<tr>").append("<td>").append(clan.getClan().getName()).append("</td>").append("<td>").append(clan.getClan().getLeaderName()).append("</td>")
								.append("<td>").append(SiegeEvent.DEFENDERS).append("</td>").append("</tr>");
					}

					msg.replace("%clans%", builder.toString());

					builder = new StringBuilder(100);
					List<Integer> players = event.getObjects(DominionSiegeEvent.ATTACKER_PLAYERS);
					for(int i : players)
					{
						Player player = GameObjectsStorage.getPlayer(i);
						builder.append("<tr>").append("<td>").append(i).append("</td>").append("<td>").append(player == null ? "null" : player.getName()).append("</td>")
								.append("<td>").append(DominionSiegeEvent.ATTACKER_PLAYERS).append("</td>").append("</tr>");
					}

					players = event.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
					for(int i : players)
					{
						Player player = GameObjectsStorage.getPlayer(i);
						builder.append("<tr>").append("<td>").append(i).append("</td>").append("<td>").append(player == null ? "null" : player.getName()).append("</td>")
								.append("<td>").append(DominionSiegeEvent.DEFENDER_PLAYERS).append("</td>").append("</tr>");
					}
					msg.replace("%players%", builder.toString());
				}
				else
				{
					msg.setFile("admin/residence/siege_info.htm");
					msg.replace("%residence%", HtmlUtils.htmlResidenceName(r.getId()));
					msg.replace("%id%", String.valueOf(r.getId()));
					msg.replace("%owner%", r.getOwner() == null ? "NPC" : r.getOwner().getName());
					msg.replace("%cycle%", String.valueOf(r.getCycle()));
					msg.replace("%paid_cycle%", String.valueOf(r.getPaidCycle()));
					msg.replace("%reward_count%", String.valueOf(r.getRewardCount()));
					msg.replace("%left_time%", String.valueOf(r.getCycleDelay()));

					StringBuilder clans = new StringBuilder(100);
					for(Map.Entry<Object, List<Serializable>> entry : event.getObjects().entrySet())
					{
						for(Serializable o : entry.getValue())
						{
							if(o instanceof SiegeClanObject)
							{
								SiegeClanObject siegeClanObject = (SiegeClanObject) o;
								clans.append("<tr>").append("<td>").append(siegeClanObject.getClan().getName()).append("</td>").append("<td>").append(siegeClanObject.getClan()
										                                                                                                                      .getLeaderName())
										.append("</td>").append("<td>").append(siegeClanObject.getType()).append("</td>").append("</tr>");
							}
						}
					}
					msg.replace("%clans%", clans.toString());
				}

				msg.replace("%hour%", String.valueOf(r.getSiegeDate().getHour()));
				msg.replace("%minute%", String.valueOf(r.getSiegeDate().getMinute()));
				msg.replace("%day%", String.valueOf(r.getSiegeDate().getDayOfMonth()));
				msg.replace("%month%", String.valueOf(r.getSiegeDate().getMonth().getValue()));
				msg.replace("%year%", String.valueOf(r.getSiegeDate().getYear()));
				activeChar.sendPacket(msg);
				break;
			case admin_set_owner:
				if(wordList.length != 3)
				{
					return false;
				}
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
				{
					return false;
				}
				Clan clan = null;
				String clanName = wordList[2];
				if(!clanName.equalsIgnoreCase("org/mmocore/gameserver/scripts/npc"))
				{
					clan = ClanTable.getInstance().getClanByName(clanName);
					if(clan == null)
					{
						activeChar.sendPacket(SystemMsg.INCORRECT_NAME);
						AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
						return false;
					}
				}

				event = r.getSiegeEvent();

				event.clearActions();

				if(r instanceof Dominion)
				{
					r.changeOwner(clan);
				}
				else
				{
					if(clan != null)
					{
						r.setLastSiegeDate(ZonedDateTime.now());
					}
					r.setOwnDate(clan == null ? Residence.MIN_SIEGE_DATE : ZonedDateTime.now());
					r.changeOwner(clan);

					event.reCalcNextTime(false);
				}
				break;
			case admin_set_siege_time:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
				{
					return false;
				}

				siegeDate = r.getSiegeDate();
				for(int i = 2; i < wordList.length; i++)
				{
					ChronoField type;
					int val = Integer.parseInt(wordList[i]);
					switch(i)
					{
						case 2:
							type = ChronoField.HOUR_OF_DAY;
							break;
						case 3:
							type = ChronoField.MINUTE_OF_HOUR;
							break;
						case 4:
							type = ChronoField.DAY_OF_MONTH;
							break;
						case 5:
							type = ChronoField.MONTH_OF_YEAR;
							val -= 1;
							break;
						case 6:
							type = ChronoField.YEAR;
							break;
						default:
							continue;
					}
					siegeDate = siegeDate.with(type, val);
					r.setSiegeDate(siegeDate);
				}
				event = r.getSiegeEvent();

				event.clearActions();
				r.setSiegeDate(siegeDate);
				event.registerActions();
				r.setJdbcState(JdbcEntityState.UPDATED);
				r.update();

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
			case admin_quick_siege_start:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
				{
					return false;
				}

				siegeDate = ZonedDateTime.now();
				if(wordList.length >= 3)
				{
					siegeDate = siegeDate.plusSeconds(Integer.parseInt(wordList[2]));
				}
				event = r.getSiegeEvent();

				event.clearActions();
				r.setSiegeDate(siegeDate);
				event.registerActions();
				r.setJdbcState(JdbcEntityState.UPDATED);
				r.update();

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
			case admin_quick_siege_stop:
				r = ResidenceHolder.getInstance().getResidence(Integer.parseInt(wordList[1]));
				if(r == null)
				{
					return false;
				}

				event = r.getSiegeEvent();

				event.clearActions();
				ThreadPoolManager.getInstance().execute(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						event.stopEvent();
					}
				});

				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
				break;
			case admin_start_dominion_war:
				siegeDate = ZonedDateTime.now();
				if(wordList.length >= 2)
				{
					siegeDate = siegeDate.plusSeconds(Integer.parseInt(wordList[1]));
				}

				runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
				runnerEvent.clearActions();

				for(Fortress f : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
				{
					f.getSiegeEvent().clearActions();
					if(f.getSiegeEvent().isInProgress())
					{
						f.getSiegeEvent().stopEvent();
					}

					f.getSiegeEvent().removeObjects(SiegeEvent.ATTACKERS);
					SiegeClanDAO.getInstance().delete(f);
				}

				for(Dominion d : runnerEvent.getRegisteredDominions())
				{
					d.getSiegeEvent().clearActions();
					d.setSiegeDate(siegeDate);
				}

				runnerEvent.setSiegeDate(siegeDate);
				runnerEvent.registerActions();
				runnerEvent.printInfo();
				break;
			case admin_stop_dominion_war:
				runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
				runnerEvent.clearActions();
				ThreadPoolManager.getInstance().execute(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						for(Fortress f : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
						{
							if(f.getSiegeEvent().isInProgress())
							{
								f.getSiegeEvent().stopEvent();
							}
						}

						runnerEvent.stopEvent();
					}
				});
				break;
			case admin_backup_unit_info:
				GameObject target = activeChar.getTarget();
				if(!(target instanceof PowerControlUnitInstance) && !(target instanceof BackupPowerUnitInstance))
				{
					return false;
				}

				List<String> t = new ArrayList<String>(3);
				if(target instanceof PowerControlUnitInstance)
				{
					for(int i : ((PowerControlUnitInstance) target).getGenerated())
					{
						t.add(String.valueOf(i));
					}
				}
				else
				{
					for(int i : ((BackupPowerUnitInstance) target).getGenerated())
					{
						t.add(i == 0 ? "A" : i == 1 ? "B" : i == 2 ? "C" : "D");
					}
				}

				activeChar.sendAdminMessage("Password: " + t.toString());
				return true;
			case admin_fortress_spawn_flags:
				if(wordList.length != 2)
				{
					return false;
				}
				Fortress fortress = ResidenceHolder.getInstance().getResidence(Fortress.class, Integer.parseInt(wordList[1]));
				if(fortress == null)
				{
					return false;
				}
				FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
				if(!siegeEvent.isInProgress())
				{
					return false;
				}
				boolean[] f = siegeEvent.getBarrackStatus();
				for(int i = 0; i < f.length; i++)
				{
					siegeEvent.barrackAction(i, true);
				}
				siegeEvent.spawnFlags();
				return true;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	@Override
	public String[] getAdminCommandString()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
