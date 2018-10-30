package handler.bbs.custom.privateOffice;

import handler.bbs.ScriptBbsHandler;
import handler.bbs.custom.privateOffice.engine.enums.ServiceType;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.engine.interfaces.IService;
import handler.bbs.custom.privateOffice.engine.listener.AnswerServiceListener;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.object.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 09.02.2016
 */
public class CommunityPrivateOffice extends ScriptBbsHandler
{
	public static final Map<Services, IService> services = new HashMap<>();
	private static final Map<ServiceType, List<Services>> serviceType = new HashMap<>();

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbsservice" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(bypass.equals("_bbsservice"))
		{
			final String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/base.htm", player);
			separateAndSend(htm, player);
		}
		else if(bypass.startsWith("_bbsservice:"))
		{
			final String[] name = bypass.split(":");
			if(name[1].equals("serviceType"))
			{
				if(name.length < 4)
				{
					player.sendMessage(new CustomMessage("common.Disabled"));
					useSaveCommand(player);
					return;
				}
				final ServiceType serviceType = ServiceType.valueOf(name[2]);
				final int page = Converter.convert(Integer.class, name[3]);
				final String html = serviceType.getHtml(player, page);
				saveCommand(player, bypass, true);
				separateAndSend(html, player);
			}
			else
			{
				switch(name[1])
				{
					case "service":
						useService(player, bypass, name);
						break;
					default:
						useSaveCommand(player);
						break;
				}
			}
		}
		else
		{
			useSaveCommand(player);
		}
	}

	private static void useService(final Player player, String bypass, final String[] param)
	{
		final Optional<Services> services = Services.value(param[2]);
		if(services.isPresent())
		{
			final Optional<IService> service = getService(services.get());
			if(service.isPresent())
			{
				final Optional<ServiceType> officeType = ServiceType.valueService(services.get());
				if(officeType.isPresent() && service.get().isIncluded(player))
				{
					if(bypass.contains("content"))
					{
						if(!service.get().validContentBypass(player, bypass))
						{
							return;
						}
						service.get().content(player, bypass);
					}
					else if(bypass.contains("request"))
					{
						if(!service.get().validRequestBypass(player, bypass))
						{
							return;
						}
						if(services.get().isAnswer())
						{
							if(service.get().validAnswer(player))
							{
								return;
							}
							final ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 60000);
							ask.addString(new CustomMessage("bbs.service.ask").addString(player.getName()).toString(player));
							player.ask(ask, new AnswerServiceListener(service.get(), player, bypass, 60000));
						}
						else
						{
							service.get().request(player, bypass);
						}
					}
				}
			}
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{

	}

	@Override
	public void onInit()
	{
		super.onInit();

		for(final ServiceType type : ServiceType.values())
		{
			serviceType.put(type, Services.listServiceType(type));
		}
	}

	public static Map<Services, IService> getServices()
	{
		return services;
	}

	public static Optional<IService> getService(final Services service)
	{
		return Optional.ofNullable(services.get(service));
	}

	public static List<Services> getServiceType(final ServiceType type)
	{
		return serviceType.get(type);
	}
}
