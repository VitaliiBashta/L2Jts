package handler.bbs.custom.privateOffice.services.clan;

import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

import java.util.Date;

/**
 * Created by Hack
 * Date: 15.06.2016 4:04
 */
public class RemoveClanPenaltyService extends CommunityBoardService {
    @Override
    public Services getService() {
        return Services.remove_clan_penalty;
    }

    @Override
    public void request(Player player, String bypass, Object... params) {
        if(player.getEvent(SiegeEvent.class) != null) {
            player.sendPacket(new CustomMessage("bbs.service.clanRename.isEventSiege"));
            useSaveCommand(player);
            return;
        }
        if(player.getClan() == null) {
            player.sendPacket(new CustomMessage("bbs.service.clanRename.noClan"));
            useSaveCommand(player);
            return;
        }
        if(!player.isClanLeader()) {
            player.sendPacket(new CustomMessage("bbs.service.clanRename.notLeader"));
            useSaveCommand(player);
            return;
        }
        if(player.getClan().getExpelledMemberTime() == 0) {
            useSaveCommand(player);
            return;
        }
        if(!getCheckAndPick(player, getService().getItemId(), getService().getItemCount(), true)) {
            useSaveCommand(player);
            return;
        }
        reply(player);
    }

    @Override
    public void reply(Player player, Object... params) {
        Clan clan = player.getClan();
        clan.setExpelledMemberTime(0);
        clan.updateClanInDB();
        useSaveCommand(player);
        player.sendPacket(new CustomMessage("bbs.service.successfully"));
    }

    @Override
    public void content(Player player, String bypass, Object... params) {
        String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/clan/clan_penalty_remove.htm", player);
        Clan clan = player.getClan();
        long millis = clan == null ? 0 : clan.getExpelledMemberTime();
        if (millis != 0)
            htm = htm.replace("<?penalty_end_date?>", "<font color=c50000>" + new Date(millis).toString() + "</font>");
        else
            htm = htm.replace("<?penalty_end_date?>", "<font color=00c500>No penalty</font>"); //TODO: multi-lang [Hack]
        htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
        htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
        separateAndSend(htm, player);
    }
}
