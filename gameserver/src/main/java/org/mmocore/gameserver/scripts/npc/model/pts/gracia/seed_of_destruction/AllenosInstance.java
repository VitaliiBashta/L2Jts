package org.mmocore.gameserver.scripts.npc.model.pts.gracia.seed_of_destruction;

import org.mmocore.gameserver.manager.SoDManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka, KilRoy
 */
public final class AllenosInstance extends NpcInstance {
    private static final int tiatIzId = 110;

    public AllenosInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (SoDManager.isAttackStage()) {
            showChatWindow(player, "pts/gracia/seed_of_destruction/servant_of_Kserth001.htm");
        } else if (SoDManager.isOpened()) {
            showChatWindow(player, "pts/gracia/seed_of_destruction/servant_of_Kserth002.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-1101&")) {
            if (command.endsWith("reply=1")) {
                if (player.getTransformationId() == 260 || player.getTransformationId() == 8 || player.getTransformationId() == 9) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_A_SEED_WHILE_IN_A_FLYING_TRANSFORMATION_STATE);
                    return;
                }

                ReflectionUtils.simpleEnterInstancedZone(player, tiatIzId);
            } else if (command.endsWith("reply=2")) {
                showChatWindow(player, "pts/gracia/seed_of_destruction/servant_of_Kserth001c.htm");
            } else if (command.endsWith("reply=11")) {
                if (player.getTransformationId() == 260 || player.getTransformationId() == 8 || player.getTransformationId() == 9) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_A_SEED_WHILE_IN_A_FLYING_TRANSFORMATION_STATE);
                    return;
                }

                SoDManager.teleportIntoSeed(player);
            } else if (command.endsWith("reply=12")) {
                showChatWindow(player, "pts/gracia/seed_of_destruction/servant_of_Kserth002a.htm");
            } else if (command.endsWith("reply=21")) {
                showChatWindow(player, "pts/gracia/seed_of_destruction/servant_of_Kserth003a.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}