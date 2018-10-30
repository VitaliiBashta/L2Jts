package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.services.player.appearance.enums.EHairStyle;
import handler.bbs.custom.privateOffice.services.player.appearance.enums.ETypes;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

import java.util.Optional;

/**
 * @author Mangol
 * @since 29.02.2016
 */
public class ChangeHairStyleService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.change_hair_style;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final ETypes[] hairStyle = EHairStyle.getHairStyle(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairStyle/hairStyle.htm", player);
		String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairStyle/table.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairStyle/button.htm", player);
		final StringBuilder buttons = new StringBuilder();
		for(int correctCount = 0; correctCount <= hairStyle.length - 1; )
		{
			if(hairStyle[correctCount].ordinal() == player.getAppearanceComponent().getHairStyle())
			{
				correctCount++;
				continue;
			}
			String generateButton = button;
			generateButton = generateButton.replace("<?style_name?>", String.valueOf(hairStyle[correctCount].name().toUpperCase()));
			generateButton = generateButton.replace("<?style?>", String.valueOf(hairStyle[correctCount].name()));
			buttons.append(generateButton);
			correctCount++;
		}
		table = table.replace("<?buttons?>", buttons.toString());
		htm = htm.replace("<?tables?>", table);
		htm = htm.replace("<?correct_style?>", ETypes.getName(player.getAppearanceComponent().getHairStyle()).toUpperCase());
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		htm = htm.replace("<?price_wear?>", String.valueOf(getService().getWearItemCount()));
		htm = htm.replace("<?item_name_wear?>", getItemName(player.getLanguage(), getService().getWearItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		if(bypass.contains("wear"))
		{
			final String[] wearStr = bypass.split(":");
			if(wearStr.length < 6)
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(player.getEvent(SiegeEvent.class) != null)
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.isEventSiegeWear"));
				useSaveCommand(player);
				return;
			}
			if(!player.isInPeaceZone())
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noIsInPeaceZoneWear"));
				useSaveCommand(player);
				return;
			}
			final Optional<ETypes> style = ETypes.value(wearStr[5]);
			final ETypes[] hairStyle = EHairStyle.getHairStyle(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
			if(!style.isPresent() || !ArrayUtils.contains(hairStyle, style.get()))
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(!getCheckAndPick(player, getService().getWearItemId(), getService().getWearItemCount(), true))
			{
				useSaveCommand(player);
				return;
			}
			wear(player, style.get());
			return;
		}
		final String[] styleStr = bypass.split(":");
		if(styleStr.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noCorrectRequest"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final Optional<ETypes> style = ETypes.value(styleStr[4]);
		final ETypes[] hairStyle = EHairStyle.getHairStyle(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		if(!style.isPresent() || !ArrayUtils.contains(hairStyle, style.get()))
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairStyle.noCorrect"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, style.get());
	}

	@Override
	public void wear(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		player.getCustomPlayerComponent().setHairStyleWear(types.ordinal(), true);
		player.getCustomPlayerComponent().startRemoveHairStyleTask();
		useCommand(player, getService().getContentBypass());
		Log.service("ChangeHairStyleService", player, "wear hair style to " + types.name() + ".");
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		//Остановить примерку если она есть.
		player.getCustomPlayerComponent().stopRemoveHairStyleTask();
		player.getAppearanceComponent().setHairStyle(types.ordinal());
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ChangeHairStyleService", player, "changed hair style to " + types.name() + ".");
	}

}
