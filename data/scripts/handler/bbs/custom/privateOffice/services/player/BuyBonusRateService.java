package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.data.xml.holder.custom.PremiumHolder;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.BonusRequest;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_PremiumState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;
import org.mmocore.gameserver.templates.custom.premium.PremiumPackage;
import org.mmocore.gameserver.templates.custom.premium.PremiumType;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumRates;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumTime;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * @author Mangol
 * @since 21.03.2016
 */
public class BuyBonusRateService extends CommunityBoardService
{
	private static final PremiumHolder premiumHolder = PremiumHolder.getInstance();

	@Override
	public Services getService()
	{
		return Services.premium_account;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		if(player.getPremiumAccountComponent().hasBonus())
		{
			final ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochSecond(player.getPremiumAccountComponent().getPremiumBonus().getBonusExpire()), ZoneId.systemDefault());
			player.sendMessage(new CustomMessage("premium.time").addString(TimeUtils.dateTimeFormat(time)));
			useSaveCommand(player);
		}
		else if(bypass.contains("home"))
		{
			String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/premiumAccount.htm", player);
			final String tableHome = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/home/tableHome.htm", player);
			final StringBuilder builder = new StringBuilder();
			int i = 1;
			for(final PremiumPackage premiumPackage : premiumHolder.getPremiumPackages().values())
			{
				if(!premiumPackage.isShow())
				{
					continue;
				}
				if(i > 3)
				{
					break;
				}
				String str = tableHome;
				str = str.replace("<?icon?>", premiumPackage.getIcon());
				str = str.replace("<?namePack?>", premiumPackage.getName(player));
				str = str.replace("<?bypass?>", "bypass _bbsservice:service:premium_account:content:info_pack:" + premiumPackage.getId());
				builder.append(str);
				i++;
			}
			htm = htm.replace("<?list_table?>", builder.toString());
			separateAndSend(htm, player);
		}
		else if(bypass.contains("info_pack"))
		{
			final String[] str = bypass.split(":");
			if(str.length < 6)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final int idPack = convert(Integer.class, str[5]);
			final Optional<PremiumPackage> premiumPackage = premiumHolder.getPremiumPackage(idPack);
			if(!premiumPackage.isPresent())
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/pack/premiumPack.htm", player);
			final String buyButton = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/pack/buyButton.htm", player);
			final String rateList = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/pack/rate.htm", player);
			final StringBuilder buyBuilder = new StringBuilder();
			for(final PremiumTime time : premiumPackage.get().getTimes().values())
			{
				String button = buyButton;
				button = button.replace("<?rate_day?>", String.valueOf(time.getDays()));
				button = button.replace("<?rate_hour?>", String.valueOf(time.getHour()));
				button = button.replace("<?rate_minute?>", String.valueOf(time.getMinute()));
				button = button.replace("<?price?>", String.valueOf(time.getPrice()));
				button = button.replace("<?item_name?>", String.valueOf(getItemName(player.getLanguage(), time.getItemId())));
				button = button.replace("<?bypass?>", "bypass _bbsservice:service:premium_account:request:buy_pack:" + premiumPackage.get().getId() + ":" + time.getId());
				buyBuilder.append(button);
			}
			final StringBuilder rateBuilder = new StringBuilder();
			for(final PremiumRates rate : premiumPackage.get().getPremiumRates().values())
			{
				String rateStr = rateList;
				rateStr = rateStr.replace("<?rate_name?>", rate.getType().getName(player));
				rateStr = rateStr.replace("<?rate_power?>", String.valueOf(rate.getValue()));
				rateStr = rateStr.replace("<?rate_pr?>", String.valueOf((int)Math.round(rate.getValue() * 100. - 100.)));
				rateBuilder.append(rateStr);
			}
			htm = htm.replace("<?pack_name?>", premiumPackage.get().getName(player));
			htm = htm.replace("<?buy_button?>", buyBuilder.toString());
			htm = htm.replace("<?rate_name_list?>", rateBuilder.toString());
			separateAndSend(htm, player);
		}
		/*
		else if(bypass.contains("personal_setting"))
		{
			String bypassSelect = "bypass _bbsservice:service:premium_account:content:personal_select:<?info?>";
			String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/personalSetting/personalSetting.htm", player);
			final String typeHtm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/personalSetting/type.htm", player);
			final StringBuilder timeList = new StringBuilder();
			final StringBuilder listRatesBuilder = new StringBuilder();
			String timeStr = typeHtm;
			timeStr = timeStr.replace("<?name?>", new CustomMessage("bbs.service.premiumAccount.time").toString(player));
			timeStr = timeStr.replace("<?vars?>", "time");
			for(final PremiumTime time : premiumHolder.getPersonalSetting().getTimes().values())
			{
				timeList.append(time.getDays()).append("/").append(time.getHour()).append("/").append(time.getMinute()).append(";");
			}
			timeStr = timeStr.replace("<?list?>", timeList.toString());
			listRatesBuilder.append(timeStr);
			for(final Map.Entry<PremiumType, Map<Double, PremiumRates>> premiumRate : premiumHolder.getPersonalSetting().getRates().rowMap().entrySet())
			{
				final PremiumType type = premiumRate.getKey();
				String str = typeHtm;
				str = str.replace("<?name?>", type.getName(player));
				str = str.replace("<?vars?>", type.name());
				final StringBuilder varsBuild = new StringBuilder();
				for(final PremiumRates premiumRates : premiumRate.getValue().values())
				{
					varsBuild.append(premiumRates.getValue()).append(";");
				}
				str = str.replace("<?list?>", varsBuild.toString());
				listRatesBuilder.append(str);
			}
			final StringBuilder bypassInfoBuilder = new StringBuilder();
			for(final PremiumType type : PremiumType.values())
			{
				if(premiumHolder.getPersonalSetting().getRates().rowMap().containsKey(type))
				{
					bypassInfoBuilder.append(" $").append(type.name());
				}
				bypassInfoBuilder.append(" -1");
			}
			bypassSelect = bypassSelect.replace("<?info?>", bypassInfoBuilder.toString());
			htm = htm.replace("<?bypass?>", bypassSelect);
			htm = htm.replace("<?list_rates?>", listRatesBuilder.toString());
			separateAndSend(htm, player);
		}
		else if(bypass.contains("personal_select"))
		{
			//bypass _bbsservice:service:premium_account:content:personal_select: $time $xp $sp $adena $drop $spoil $epaulette
			final String[] str = bypass.split(":");
			if(str.length < 6)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final String[] selectStr = str[5].trim().split(" ");
			if(selectStr.length < 7)
			{

				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final String[] timeStr = selectStr[0].split("/");
			if(timeStr.length < 3)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			int days;
			int hours;
			int minute;
			double xp = -1;
			double sp = -1;
			double adena = -1;
			double spoil = -1;
			double drop = -1;
			double epaulette = -1;
			final Map<PremiumType, Double> rates = new HashMap<>();
			try
			{
				days = convert(Integer.class, timeStr[0]);
				hours = convert(Integer.class, timeStr[1]);
				minute = convert(Integer.class, timeStr[2]);
				xp = convert(Double.class, selectStr[1]);
				if(xp > 1)
				{
					rates.put(PremiumType.xp, xp);
				}
				sp = convert(Double.class, selectStr[2]);
				if(sp > 1)
				{
					rates.put(PremiumType.sp, sp);
				}
				adena = convert(Double.class, selectStr[3]);
				if(adena > 1)
				{
					rates.put(PremiumType.adena, adena);
				}
				drop = convert(Double.class, selectStr[4]);
				if(drop > 1)
				{
					rates.put(PremiumType.drop, drop);
				}
				spoil = convert(Double.class, selectStr[5]);
				if(spoil > 1)
				{
					rates.put(PremiumType.spoil, spoil);
				}
				epaulette = convert(Double.class, selectStr[6]);
				if(epaulette > 1)
				{
					rates.put(PremiumType.epaulette, epaulette);
				}
			}
			catch(final Exception ignore)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			if(rates.isEmpty())
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final Optional<PremiumTime> time = premiumHolder.getPersonalSetting().getTimes().values().stream().filter(t -> t.getDays() == days && t.getHour() == hours && t.getMinute() == minute).findFirst();
			if(!time.isPresent())
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/personalSetting/personalSelect.htm", player);
			final String rateList = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/personalSetting/rate.htm", player);
			final String priceList = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/premium/personalSetting/price.htm", player);
			final StringBuilder rateBuilder = new StringBuilder();
			final StringBuilder priceBuilder = new StringBuilder();
			String timesStr = priceList;
			timesStr = timesStr.replace("<?rate_name?>", new CustomMessage("bbs.service.premiumAccount.time").toString(player));
			timesStr = timesStr.replace("<?setting_price?>", String.valueOf(time.get().getPrice()));
			timesStr = timesStr.replace("<?setting_item?>", String.valueOf(getItemName(player.getLanguage(), time.get().getItemId())));
			priceBuilder.append(timesStr);
			for(final Map.Entry<PremiumType, Double> rate : rates.entrySet())
			{
				if(!premiumHolder.getPersonalSetting().isPremiumContains(rate.getKey(), rate.getValue()))
				{
					player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
					useSaveCommand(player);
					return;
				}
				final String rateName = rate.getKey().getName(player);
				String rateStr = rateList;
				rateStr = rateStr.replace("<?rate_name?>", rateName);
				rateStr = rateStr.replace("<?rate_power?>", String.valueOf(rate.getValue()));
				rateStr = rateStr.replace("<?rate_pr?>", String.valueOf((rate.getValue() - 1.0) * 100));
				rateBuilder.append(rateStr);
				String priceStr = priceList;
				priceStr = priceStr.replace("<?rate_name?>", rateName);
				priceStr = priceStr.replace("<?setting_price?>", String.valueOf(rate.getValue()));
				priceStr = priceStr.replace("<?setting_item?>", String.valueOf(rate.getValue()));
				priceBuilder.append(priceStr);
			}
			htm = htm.replace("<?list_rates?>", rateBuilder.toString());
			htm = htm.replace("<?list_price?>", priceBuilder.toString());
			String bypassRequest = "bypass _bbsservice:service:premium_account:request:personal_setting:<?info?>";
			bypassRequest = bypassRequest.replace("<?info?>", " " + selectStr[0] + " " + xp + " " + sp + " " + adena + " " + drop + " " + spoil + " " + epaulette);
			htm = htm.replace("<?bypass?>", bypassRequest);
			separateAndSend(htm, player);
		}
		*/
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		if(player.getPremiumAccountComponent().hasBonus())
		{
			player.sendPacket(new CustomMessage("bbs.service.premiumAccount.active"));
			useSaveCommand(player);
			return;
		}
		if(CServiceConfig.rateBonusType == PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER && AuthServerCommunication.getInstance().isShutdown())
		{
			player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
			useSaveCommand(player);
			return;
		}
		if(bypass.contains("buy_pack"))
		{
			final String[] str = bypass.split(":");
			if(str.length < 7)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final int idPack = convert(Integer.class, str[5]);
			final int idTime = convert(Integer.class, str[6]);
			final PremiumPackage premiumPackage = premiumHolder.getPremiumPackages().get(idPack);
			if(premiumPackage == null)
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			final Optional<PremiumTime> premiumTime = premiumPackage.getTime(idTime);
			if(!premiumTime.isPresent())
			{
				player.sendPacket(new CustomMessage("bbs.service.premiumAccount.error"));
				useSaveCommand(player);
				return;
			}
			if(!getCheckAndPick(player, premiumTime.get().getItemId(), premiumTime.get().getPrice(), true))
			{
				useSaveCommand(player);
				return;
			}
			reply(player, premiumPackage, premiumTime.get());
		}
	}

	@Override
	public void reply(Player player, Object... params)
	{
		final PremiumPackage premiumPackage = (PremiumPackage) params[0];
		final PremiumTime premiumTime = (PremiumTime) params[1];
		final PremiumBonus premiumBonus = new PremiumBonus();
		premiumPackage.getPremiumRates().entrySet().stream().forEach(p -> {
			final PremiumType type = p.getKey();
			if(type == PremiumType.xp)
			{
				premiumBonus.setRateXp(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if(type == PremiumType.sp)
			{
				premiumBonus.setRateSp(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if(type == PremiumType.adena)
			{
				premiumBonus.setDropAdena(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if(type == PremiumType.drop)
			{
				premiumBonus.setDropItems(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if(type == PremiumType.spoil)
			{
				premiumBonus.setDropSpoil(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if(type == PremiumType.epaulette)
			{
				premiumBonus.setBonusEpaulette(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if (type == PremiumType.enchantChance) {
				premiumBonus.setEnchantChance(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if (type == PremiumType.attributeChance) {
				premiumBonus.setAttributeChance(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
			else if (type == PremiumType.craftChance) {
				premiumBonus.setCraftChance(p.getValue().getValue() > 1. ? p.getValue().getValue() : 1.);
			}
		});
		final ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).
				plusDays(premiumTime.getDays()).
				plusHours(premiumTime.getHour()).
				plusMinutes(premiumTime.getMinute());
		premiumBonus.setBonusExpire(time.toEpochSecond());
		switch(CServiceConfig.rateBonusType)
		{
			case PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER:
			{
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), 1, premiumBonus));
				break;
			}
			case PremiumBonus.BONUS_GLOBAL_ON_GAMESERVER:
			{
				AccountBonusDAO.getInstance().insert(player.getAccountName(), premiumBonus);
				break;
			}
		}
		player.getNetConnection().setPremiumBonus(premiumBonus);
		player.getPremiumAccountComponent().stopBonusTask();
		player.getPremiumAccountComponent().startBonusTask();
		if(player.getParty() != null)
		{
			player.getParty().recalculatePartyData();
		}
		player.sendMessage(new CustomMessage("premium.time").addString(TimeUtils.dateTimeFormat(time)));
		player.sendPacket(new ExBR_PremiumState(player, true));
		if(CustomConfig.subscriptionAllow && !player.isGM() && CustomConfig.buyPremiumBuySubscription)
		{
			final long timeSub = Duration.between(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()), time).toMillis();
			final CustomPlayerComponent playerComponent = player.getCustomPlayerComponent();
			playerComponent.saveSubscriptionTimeTask(false);
			playerComponent.stopSubscriptionTask();
			playerComponent.addTimeSubscription(timeSub);
			player.sendChanges();
			if(!player.isInZone(ZoneType.peace_zone))
			{
				playerComponent.startSubscription();
			}
		}
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("BuyBonusRateService", player, "buy premium " +
				" xp " + premiumBonus.getRateXp() +
				" xp " + premiumBonus.getRateSp() +
				" adena " + premiumBonus.getDropAdena() +
				" drop " + premiumBonus.getDropItems() +
				" spoil " + premiumBonus.getDropSpoil() +
				" epaulette " + premiumBonus.getBonusEpaulette());
	}
}
