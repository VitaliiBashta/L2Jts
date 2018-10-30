package handler.bbs.custom.privateOffice.services.player.appearance;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

/**
 * @author Mangol
 * @since 18.02.2016
 */
public class ChangeSexService extends CommunityBoardService
{
	@Override
	public void content(final Player player, final String bypass, final Object... params)
	{
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/change_sex.htm", player);
		String sex = "";
		switch(player.getPlayerTemplateComponent().getPlayerSex())
		{
			case FEMALE:
				sex = new CustomMessage("bbs.service.changeSex.female").toString(player);
				break;
			case MALE:
				sex = new CustomMessage("bbs.service.changeSex.male").toString(player);
				break;
		}
		htm = htm.replace("<?sex?>", sex);
		htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
		htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
		separateAndSend(htm, player);
	}

	@Override
	public void request(final Player player, final String bypass, final Object... params)
	{
		if(player == null)
		{
			return;
		}
		if(player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
		{
			player.sendPacket(new CustomMessage("bbs.service.change_sex.noCorrectRace"));
			useSaveCommand(player);
			return;
		}
		if(player.getEvent(SiegeEvent.class) != null)
		{
			player.sendPacket(new CustomMessage("bbs.service.change_sex.isEventSiege"));
			useSaveCommand(player);
			return;
		}
		if(!player.isInPeaceZone())
		{
			player.sendPacket(new CustomMessage("bbs.service.change_sex.noIsInPeaceZone"));
			useSaveCommand(player);
			return;
		}
		if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true))
		{
			useSaveCommand(player);
			return;
		}
		reply(player);
	}

	@Override
	public void reply(final Player player, final Object... params)
	{
		player.getAppearanceComponent().setHairColor(0);
		player.getAppearanceComponent().setHairStyle(0);
		player.getAppearanceComponent().setFace(0);
		player.logout();
		final PlayerSex sex = player.getPlayerTemplateComponent().getPlayerSex() == PlayerSex.MALE ? PlayerSex.FEMALE : PlayerSex.MALE;
		serviceDAO.changeSex(player, sex);
		Log.service("ChangeSexService", player, "changed sex to " + player.getPlayerTemplateComponent().getPlayerSex().name().toLowerCase() + ".");
	}

	@Override
	public Services getService()
	{
		return Services.change_sex;
	}

}
