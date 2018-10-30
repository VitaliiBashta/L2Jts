package org.mmocore.gameserver.scripts.npc.model.pts.events;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.NcSoft.EvasInferno.EvasInferno;
import org.mmocore.gameserver.scripts.events.NcSoft.EvasInferno.EvasInferno.EventStage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class BrArchbishopOfEvaInstance extends NpcInstance {
    private static final int br_box_of_scroll_master = 20573;
    private static final int br_box_of_scroll_master2 = 20574;
    private static final int br_box_of_experience_master = 20575;
    private static final int br_cert_scroll_1 = 20604;
    private static final int br_cert_scroll_2 = 20605;
    private static final int br_cert_scroll_3 = 20606;

    public BrArchbishopOfEvaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=50000&")) {
            if (command.endsWith("reply=1")) {
                if (EvasInferno.getCurrentEventStage() != EventStage.STAGE6) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop008.htm");
                } else if (ItemFunctions.getItemCount(player, br_box_of_scroll_master) > 0 || ItemFunctions.getItemCount(player, br_cert_scroll_1) > 0) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop007.htm");
                } else {
                    ItemFunctions.addItem(player, br_box_of_scroll_master, 1);
                    ItemFunctions.addItem(player, br_cert_scroll_1, 1);
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop009.htm");
                }
            } else if (command.endsWith("reply=2")) {
                if (EvasInferno.getCurrentEventStage() != EventStage.STAGE10) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop008.htm");
                } else if (ItemFunctions.getItemCount(player, br_box_of_scroll_master2) > 0 || ItemFunctions.getItemCount(player, br_cert_scroll_2) > 0) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop007.htm");
                } else {
                    final long i0 = ItemFunctions.getItemCount(player, br_cert_scroll_1);
                    if (i0 > 0) {
                        ItemFunctions.removeItem(player, br_cert_scroll_1, i0);
                    }
                    ItemFunctions.addItem(player, br_box_of_scroll_master2, 1);
                    ItemFunctions.addItem(player, br_cert_scroll_2, 1);
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop010.htm");
                }
            } else if (command.endsWith("reply=3")) {
                if (EvasInferno.getCurrentEventStage() != EventStage.STAGE14) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop008.htm");
                } else if (ItemFunctions.getItemCount(player, br_box_of_experience_master) > 0 || ItemFunctions.getItemCount(player, br_cert_scroll_3) > 0) {
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop007.htm");
                } else {
                    final long i0 = ItemFunctions.getItemCount(player, br_cert_scroll_1);
                    if (i0 > 0) {
                        ItemFunctions.removeItem(player, br_cert_scroll_1, i0);
                    }
                    final long i1 = ItemFunctions.getItemCount(player, br_cert_scroll_2);
                    if (i1 > 0) {
                        ItemFunctions.removeItem(player, br_cert_scroll_2, i1);
                    }
                    ItemFunctions.addItem(player, br_box_of_experience_master, 1);
                    ItemFunctions.addItem(player, br_cert_scroll_3, 1);
                    showChatWindow(player, "pts/events/evas_inferno/br_fire_elemental_archbishop011.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}