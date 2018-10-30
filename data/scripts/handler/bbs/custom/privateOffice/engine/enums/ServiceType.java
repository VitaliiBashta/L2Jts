package handler.bbs.custom.privateOffice.engine.enums;
import handler.bbs.custom.privateOffice.CommunityPrivateOffice;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

import java.util.List;
import java.util.Optional;

/**
 * @author Mangol
 * @since 14.02.2016
 */
public enum ServiceType
{
	player
			{
				@Override
				public String getHtml(final Player player, final int page)
				{
					String htm = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/page/page-" + page + ".htm", player);
					final List<Services> serviceList = CommunityPrivateOffice.getServiceType(this);
					for(final Services val : serviceList)
					{
						htm = htm.replace("<?bypass_" + val.name() + "?>", val.getContentBypass());
						htm = htm.replace("<?status?>", new CustomMessage("bbs.service.status").toString(player));
						if(val.isIncluded())
						{
							htm = htm.replace("<?status_" + val.name() + "?>", new CustomMessage("bbs.service.on").toString(player));
							continue;
						}
						htm = htm.replace("<?status_" + val.name() + "?>", new CustomMessage("bbs.service.off").toString(player));
					}
					return htm;
				}
			},
	clan
			{
				@Override
				public String getHtml(final Player player, final int page)
				{
					String htm = HtmCache.getInstance().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/page/page-" + page + ".htm", player);
					final List<Services> serviceList = CommunityPrivateOffice.getServiceType(this);
					for(final Services val : serviceList)
					{
						htm = htm.replace("<?bypass_" + val.name() + "?>", val.getContentBypass());
						htm = htm.replace("<?status?>", new CustomMessage("bbs.service.status").toString(player));
						if(val.isIncluded())
						{
							htm = htm.replace("<?status_" + val.name() + "?>", new CustomMessage("bbs.service.on").toString(player));
							continue;
						}
						htm = htm.replace("<?status_" + val.name() + "?>", new CustomMessage("bbs.service.off").toString(player));;
					}
					return htm;
				}
			};

	public abstract String getHtml(final Player player, final int page);

	public static Optional<ServiceType> valueService(final Services service)
	{
		for(final ServiceType type : ServiceType.values())
		{
			if(service.getServiceType() == type)
			{
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}
}
