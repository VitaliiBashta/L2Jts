package org.mmocore.gameserver.taskmanager.tasks.custom;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.taskmanager.tasks.AutomaticTask;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.quartz.SimpleScheduleBuilder.repeatHourlyForever;

/**
 * @author Mangol
 * @since 01.05.2016
 */
public class SubscriptionGiftTask extends AutomaticTask {
    @Override
    protected void doTask() {
        if (!CustomConfig.subscriptionAllow || !CustomConfig.subscriptionRandomBonus) {
            return;
        }
        final List<Player> storage = GameObjectsStorage.getPlayerStream().filter(p -> !p.isGM() && p.isOnline()).collect(Collectors.toList());
        if (!storage.isEmpty()) {
            final Player player = Rnd.get(storage);
            final CustomPlayerComponent component = player.getCustomPlayerComponent();
            component.saveSubscriptionTimeTask(false);
            component.stopSubscriptionTask();
            component.addTimeSubscription(TimeUnit.HOURS.toMillis(CustomConfig.subscriptionRandomHourAdd));
            AnnouncementUtils.announceToAll(new CustomMessage("subscription.gift").addString(player.getName()));
            AnnouncementUtils.announceToAll(new CustomMessage("subscription.giftTime").addString(String.valueOf(CustomConfig.subscriptionRandomHour)).addString(String.valueOf(CustomConfig.subscriptionRandomHourAdd)));
            if (!player.isInZone(ZoneType.peace_zone)) {
                player.sendChanges();
                component.startSubscription();
            }
        }
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger().startNow().withSchedule(repeatHourlyForever(CustomConfig.subscriptionRandomHour)).build();
    }
}
