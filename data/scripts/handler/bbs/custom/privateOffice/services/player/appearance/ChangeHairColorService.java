package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.services.player.appearance.enums.EHairColor;
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
 * @since 01.03.2016
 */
public class ChangeHairColorService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.change_hair_color;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final ETypes[] hairColors = EHairColor.getHairColor(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairColor/hairColor.htm", player);
		String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairColor/table.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/hairColor/button.htm", player);
		final StringBuilder buttons = new StringBuilder();
		for(int correctCount = 0; correctCount <= hairColors.length - 1; )
		{
			if(hairColors[correctCount].ordinal() == player.getAppearanceComponent().getHairColor())
			{
				correctCount++;
				continue;
			}
			String generateButton = button;
			generateButton = generateButton.replace("<?color_name?>", String.valueOf(hairColors[correctCount].name().toUpperCase()));
			generateButton = generateButton.replace("<?color?>", String.valueOf(hairColors[correctCount].name()));
			buttons.append(generateButton);
			correctCount++;
		}
		table = table.replace("<?buttons?>", buttons.toString());
		htm = htm.replace("<?tables?>", table);
		htm = htm.replace("<?correct_color?>", ETypes.getName(player.getAppearanceComponent().getHairColor()).toUpperCase());
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
				player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(player.getEvent(SiegeEvent.class) != null)
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairColor.isEventSiegeWear"));
				useSaveCommand(player);
				return;
			}
			if(!player.isInPeaceZone())
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noIsInPeaceZoneWear"));
				useSaveCommand(player);
				return;
			}
			final Optional<ETypes> hairColor = ETypes.value(wearStr[5]);
			final ETypes[] hairColors = EHairColor.getHairColor(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
			if(!hairColor.isPresent() || !ArrayUtils.contains(hairColors, hairColor.get()))
			{
				player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(!getCheckAndPick(player, getService().getWearItemId(), getService().getWearItemCount(), true))
			{
				useSaveCommand(player);
				return;
			}
			wear(player, hairColor.get());
			return;
		}
		final String[] styleStr = bypass.split(":");
		if(styleStr.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noCorrectRequest"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairColor.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final Optional<ETypes> hairColor = ETypes.value(styleStr[4]);
		final ETypes[] hairColors = EHairColor.getHairColor(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		if(!hairColor.isPresent() || !ArrayUtils.contains(hairColors, hairColor.get()))
		{
			player.sendPacket(new CustomMessage("bbs.service.changeHairColor.noCorrect"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, hairColor.get());
	}

	@Override
	public void wear(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		player.getCustomPlayerComponent().setHairColorWear(types.ordinal(), true);
		player.getCustomPlayerComponent().startRemoveHairColorTask();
		useCommand(player, getService().getContentBypass());
		Log.service("ChangeHairColorService", player, "wear hair color to " + types.name() + ".");
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		//Остановить примерку если она есть.
		player.getCustomPlayerComponent().stopRemoveHairColorTask();
		player.getAppearanceComponent().setHairColor(types.ordinal());
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ChangeHairColorService", player, "changed hair color to " + types.name() + ".");
	}

}
