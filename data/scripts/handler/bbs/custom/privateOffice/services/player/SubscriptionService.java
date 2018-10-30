package handler.bbs.custom.privateOffice.services.player;

import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author Mangol
 * @since 01.05.2016
 */
public class SubscriptionService extends CommunityBoardService {
    @Override
    public Services getService() {
        return Services.subscription;
    }

    @Override
    public void content(Player player, String bypass, Object... params) {
        if (player.isGM()) {
            player.sendPacket(new CustomMessage("bbs.service.subscription.isGM"));
            useSaveCommand(player);
            return;
        }
        final CustomPlayerComponent component = player.getCustomPlayerComponent();
        if (component.isSubscriptionActive()) {
            String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/subscription/subscription_active.htm", player);
            htm = htm.replace("<?info?>", String.valueOf(new CustomMessage("bbs.service.subscription.active").toString(player)));
            final String time = TimeUtils.dateTimeFormat(ZonedDateTime.ofInstant(Instant.now().plusMillis(component.getTimeSubscription()), ZoneId.systemDefault()));
            htm = htm.replace("<?time?>", String.valueOf(new CustomMessage("bbs.service.subscription.time").addString(time).toString(player)));
            separateAndSend(htm, player);
        } else {
            String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/subscription/subscription.htm", player);
            htm = htm.replace("<?price?>", String.valueOf(getService().getItemCount()));
            htm = htm.replace("<?item_name?>", getItemName(player.getLanguage(), getService().getItemId()));
            htm = htm.replace("<?max_subscription_hour?>", String.valueOf(getService().getMaxCount()));
            separateAndSend(htm, player);
        }
    }

    @Override
    public void request(Player player, String bypass, Object... params) {
        if (player == null) {
            return;
        }
        if (player.isGM()) {
            player.sendPacket(new CustomMessage("bbs.service.subscription.isGM"));
            useSaveCommand(player);
            return;
        }
        final CustomPlayerComponent component = player.getCustomPlayerComponent();
        if (component.isSubscriptionActive()) {
            player.sendPacket(new CustomMessage("bbs.service.subscription.active"));
            useSaveCommand(player);
            return;
        }
        final String[] str = bypass.split(":");
        if (str.length < 5) {
            player.sendPacket(new CustomMessage("bbs.service.subscription.error"));
            useSaveCommand(player);
            return;
        }
        final int hours = convert(Integer.class, str[4].trim());
        final int hoursFinal = Math.min((int) getService().getMaxCount(), hours);
        final long price = getService().getItemCount() * hoursFinal;
        if (!getCheckAndPick(player, getService().getItemId(), price, true)) {
            useSaveCommand(player);
            return;
        }
        reply(player, hoursFinal);
    }

    @Override
    public void reply(final Player player, final Object... params) {
        final int hours = (int) params[0];
        final CustomPlayerComponent component = player.getCustomPlayerComponent();
        component.saveSubscriptionTimeTask(false);
        component.stopSubscriptionTask();
        component.addTimeSubscription(TimeUnit.HOURS.toMillis(hours));
        player.sendChanges();
        if (!player.isInZone(ZoneType.peace_zone)) {
            component.startSubscription();
        }
        useSaveCommand(player);
        player.sendPacket(new CustomMessage("bbs.service.successfully"));
        Log.service("SubscriptionService", player, "buy hours - " + hours);
    }
}
