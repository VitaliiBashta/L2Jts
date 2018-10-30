package handler.bbs.custom.privateOffice.services.player;

import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.OlympiadNobleDAO;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;

/**
 * Created by Hack
 * Date: 27.09.2016 6:10
 */
public class OlympiadResetService extends CommunityBoardService {
    private final int defaultPoints = 10;
    @Override
    public Services getService() {
        return Services.olympiad_reset;
    }

    @SuppressWarnings("all")
    @Override
    public void content(Player player, String bypass, Object... params) {
        String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/olympiad_reset.htm", player);
        htm = htm.replace("<?current_pts?>", getPoints(player) + "");
        htm = htm.replace("<?price_item?>", ItemTemplateHolder.getInstance().getTemplate(getService().getItemId()).getName());
        htm = htm.replace("<?price_count?>", getService().getItemCount() + "");
        separateAndSend(htm, player);
    }

    @Override
    public void request(Player player, String bypass, Object... params) {
        if(!player.isNoble()) {
            player.sendMessage(player.isLangRus() ? "Только для дворян!" : "Only for noblesses!");
            useSaveCommand(player);
            return;
        }
        int currentPoints = getPoints(player);
        if (currentPoints <= 0 || currentPoints >= defaultPoints) {
            player.sendMessage(player.isLangRus() ? "Неверное количество очков!" : "Incorrect points count!");
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
        final StatsSet stats = Olympiad.nobles.get(player.getObjectId());
        stats.set(Olympiad.POINTS, defaultPoints);
        OlympiadNobleDAO.getInstance().replace(player.getObjectId());
    }

    private int getPoints(Player player) {
        if (!player.isNoble())
            return 0;
        else
            return OlympiadNobleDAO.getInstance().getPoints(player.getObjectId());
    }
}
