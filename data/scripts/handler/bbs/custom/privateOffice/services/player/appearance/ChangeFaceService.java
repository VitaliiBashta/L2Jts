package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.services.player.appearance.enums.EFace;
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
public class ChangeFaceService extends CommunityBoardService
{
	@Override
	public Services getService()
	{
		return Services.change_face;
	}

	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		final ETypes[] face = EFace.getFace(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeFace/changeFace.htm", player);
		String table = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeFace/table.htm", player);
		String button = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/changeFace/button.htm", player);
		final StringBuilder buttons = new StringBuilder();
		for(int correctCount = 0; correctCount <= face.length - 1; )
		{
			if(face[correctCount].ordinal() == player.getAppearanceComponent().getFace())
			{
				correctCount++;
				continue;
			}
			String generateButton = button;
			generateButton = generateButton.replace("<?face_name?>", String.valueOf(face[correctCount].name().toUpperCase()));
			generateButton = generateButton.replace("<?face?>", String.valueOf(face[correctCount].name()));
			buttons.append(generateButton);
			correctCount++;
		}
		table = table.replace("<?buttons?>", buttons.toString());
		htm = htm.replace("<?tables?>", table);
		htm = htm.replace("<?correct_face?>", ETypes.getName(player.getAppearanceComponent().getFace()).toUpperCase());
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
				player.sendPacket(new CustomMessage("bbs.service.changeFace.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(player.getEvent(SiegeEvent.class) != null)
			{
				player.sendPacket(new CustomMessage("bbs.service.changeFace.isEventSiegeWear"));
				useSaveCommand(player);
				return;
			}
			if(!player.isInPeaceZone())
			{
				player.sendPacket(new CustomMessage("bbs.service.changeFace.noIsInPeaceZoneWear"));
				useSaveCommand(player);
				return;
			}
			final Optional<ETypes> face = ETypes.value(wearStr[5]);
			final ETypes[] faces = EFace.getFace(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
			if(!face.isPresent() || !ArrayUtils.contains(faces, face.get()))
			{
				player.sendPacket(new CustomMessage("bbs.service.changeFace.noCorrectWear"));
				useSaveCommand(player);
				return;
			}
			if(!getCheckAndPick(player, getService().getWearItemId(), getService().getWearItemCount(), true))
			{
				useSaveCommand(player);
				return;
			}
			wear(player, face.get());
			return;
		}
		final String[] styleStr = bypass.split(":");
		if(styleStr.length < 5)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeFace.noCorrectRequest"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.changeFace.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.changeFace.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		final Optional<ETypes> face = ETypes.value(styleStr[4]);
		final ETypes[] faces = EFace.getFace(player.getPlayerTemplateComponent().getPlayerRace(), player.getPlayerTemplateComponent().getPlayerSex());
		if(!face.isPresent() || !ArrayUtils.contains(faces, face.get()))
		{
			player.sendPacket(new CustomMessage("bbs.service.changeFace.noCorrect"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player, face.get());
	}

	@Override
	public void wear(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		player.getCustomPlayerComponent().setFaceWear(types.ordinal(), true);
		player.getCustomPlayerComponent().startRemoveFaceTask();
		useCommand(player, getService().getContentBypass());
		Log.service("ChangeFaceService", player, "wear face to " + types.name() + ".");
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		final ETypes types = (ETypes) params[0];
		//Остановить подборку если она есть.
		player.getCustomPlayerComponent().stopRemoveFaceTask();
		player.getAppearanceComponent().setFace(types.ordinal());
		player.broadcastUserInfo(true);
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ChangeFaceService", player, "changed face to " + types.name() + ".");
	}

}
