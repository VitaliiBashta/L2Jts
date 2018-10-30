package services;

public class RateBonus
{
	/*
	@Bypass("services.RateBonus:list")
	public void list(Player player, NpcInstance npc, String[] arg)
	{
		if(ServicesConfig.SERVICES_RATE_TYPE == PremiumBonus.NO_BONUS)
		{
			Functions.show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player, npc);
			return;
		}

		String html;
		if(player.getNetConnection().getPremiumBonus() >= 1.)
		{
			long endtime = player.getNetConnection().getBonusExpire();
			if(endtime >= System.currentTimeMillis() / 1000L)
			{
				html = HtmCache.getInstance().getHtml("scripts/services/RateBonusAlready.htm", player).replaceFirst("endtime", new Date(endtime * 1000L).toString());
			}
			else
			{
				html = HtmCache.getInstance().getHtml("scripts/services/RateBonus.htm", player);

				String add = "";
				for(int i = 0; i < ServicesConfig.SERVICES_RATE_BONUS_DAYS.length; i++)
				{
					final String item = ServicesConfig.SERVICES_RATE_BONUS_ITEM[i] == ItemTemplate.PREMIUM_POINTS ? "premium points" : ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.SERVICES_RATE_BONUS_ITEM[i]).getName();
					add += "<a action=\"bypass -h htmbypass_services.RateBonus:get " + i + "\">" //
					       + (int) (ServicesConfig.SERVICES_RATE_BONUS_VALUE[i] * 100 - 100) + //
					       "% for " + ServicesConfig.SERVICES_RATE_BONUS_DAYS[i] + //
					       " days - " + ServicesConfig.SERVICES_RATE_BONUS_PRICE[i] + //
					       ' ' + item + "</a><br>";
				}

				html = html.replaceFirst("%toreplace%", add);
			}
		}
		else
		{
			html = HtmCache.getInstance().getHtml("scripts/services/RateBonusNo.htm", player);
		}

		Functions.show(html, player, npc);
	}

	@Bypass("services.RateBonus:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 1)
		{
			return;
		}

		if(ServicesConfig.SERVICES_RATE_TYPE == PremiumBonus.NO_BONUS)
		{
			Functions.show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player, npc);
			return;
		}

		int i = Integer.parseInt(arg[0]);
		
		if(ServicesConfig.SERVICES_RATE_BONUS_ITEM[i] == ItemTemplate.PREMIUM_POINTS)
		{
			if(player.getPremiumAccountComponent().getPremiumPoints() >= ServicesConfig.SERVICES_RATE_BONUS_PRICE[i])
				AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ServicesConfig.SERVICES_RATE_BONUS_PRICE[i], true));
			else
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
		}

		else if(!player.getInventory().destroyItemByItemId(ServicesConfig.SERVICES_RATE_BONUS_ITEM[i], ServicesConfig.SERVICES_RATE_BONUS_PRICE[i]))
		{
			if(ServicesConfig.SERVICES_RATE_BONUS_ITEM[i] == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
			}
			return;
		}

		if(ServicesConfig.SERVICES_RATE_TYPE == PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER && AuthServerCommunication.getInstance().isShutdown())
		{
			list(player, npc, arg);
			return;
		}

		Log.add(player.getName() + '|' + player.getObjectId() + "|rate bonus|" + ServicesConfig.SERVICES_RATE_BONUS_VALUE[i] + '|' + ServicesConfig.SERVICES_RATE_BONUS_DAYS[i] + '|', "services");

		double bonus = ServicesConfig.SERVICES_RATE_BONUS_VALUE[i];
		long bonusExpire = (System.currentTimeMillis() / 1000L) + ServicesConfig.SERVICES_RATE_BONUS_DAYS[i] * 24 * 60 * 60;

		switch(ServicesConfig.SERVICES_RATE_TYPE)
		{
			case PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER:
			{
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), 1, new PremiumBonus()));
				break;
			}
			case PremiumBonus.BONUS_GLOBAL_ON_GAMESERVER:
			{
				AccountBonusDAO.getInstance().insert(player.getAccountName(), bonus, bonusExpire);
				break;
			}
		}

		player.getNetConnection().setPremiumBonus(new PremiumBonus());
		player.getPremiumAccountComponent().stopBonusTask();
		player.getPremiumAccountComponent().startBonusTask();

		if(player.getParty() != null)
		{
			player.getParty().recalculatePartyData();
		}

		player.sendPacket(new ExBR_PremiumState(player, true));

		Functions.show(HtmCache.getInstance().getHtml("scripts/services/RateBonusGet.htm", player), player, npc);
	}
	*/
}