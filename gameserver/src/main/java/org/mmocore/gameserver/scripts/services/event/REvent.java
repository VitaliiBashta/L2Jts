package org.mmocore.gameserver.scripts.services.event;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RTeamType;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RSnapshotObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class REvent extends Functions {

    public static void stats(Player player) {
        if (player == null) {
            return;
        }
        final ReflectionEvent event = player.getEvent(ReflectionEvent.class);
        if (event == null) {
            player.sendMessage(new CustomMessage("event.r.noEvent"));
        } else if (event.getEventState() != REventState.NONE && event.getEventState() != REventState.FINISH) {
            String htm = HtmCache.getInstance().getHtml("event/re/index.htm", player);
            String table = HtmCache.getInstance().getHtml("event/re/table.htm", player);
            StringBuilder builder = new StringBuilder();
            final HtmlMessage dialog = new HtmlMessage(5);
            for (final Map.Entry<RTeamType, RBaseController> controller : event.getBaseControllerMap().entrySet()) {
                final RTeamType type = controller.getKey();
                final int sizePoint = controller.getValue().getPoint();
                htm = htm.replace("<?" + type.getNameTeam() + "?>", String.valueOf(sizePoint));
                String block = table;
                block = block.replace("<?color?>", type.getColor());
                block = block.replace("<?name?>", type.getNameTeam());
                block = block.replace("<?size?>", String.valueOf(sizePoint));
                builder.append(block);
            }
            htm = htm.replace("<?tables?>", builder.toString());
            htm = htm.replace("<?kill_point?>", String.valueOf(event.getKillPoint()));
            htm = htm.replace("<?max_point?>", String.valueOf(event.getMaxPoint()));
            htm = htm.replace("<?steal_flag?>", String.valueOf(event.getStealFlagPoint()));
            htm = htm.replace("<?time_end?>", event.getEndTimeTask() != null ? String.valueOf(event.getEndTimeTask().getDelay(TimeUnit.MINUTES)) : "0");
            final Optional<RSnapshotObject> point = event.getBaseController(player.getEventComponent().getTeam(event.getClass())).getPlayers().stream().filter(b -> b != null && b.getPlayer() != null).findFirst();
            htm = htm.replace("<?my?>", String.valueOf(point.isPresent() ? point.get().getCountPoint() : 0));
            player.sendPacket(dialog.setHtml(htm));
        } else {
            player.sendMessage(new CustomMessage("event.r.noState"));
        }
    }

    @Bypass("services.REvent:generateStats")
    public void generateStats(Player player, NpcInstance npc, String[] arg) {
        stats(player);
    }
}
