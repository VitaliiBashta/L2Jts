package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.HeartInfinityAttack;
import org.mmocore.gameserver.scripts.instances.HeartInfinityDefence;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class AbyssGazeInstance extends NpcInstance {
    private static final int ekimusIzId = 121;
    private static final int hoidefIzId = 122;

    public AbyssGazeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("request_permission")) {
            if (SoIManager.getCurrentStage() == 2 || SoIManager.getCurrentStage() == 5) {
                showChatWindow(player, "default/32540-2.htm");
                return;
            } else if (SoIManager.getCurrentStage() == 3 && SoIManager.isSeedOpen()) {
                showChatWindow(player, "default/32540-3.htm");
                return;
            } else {
                showChatWindow(player, "default/32540-1.htm");
                return;
            }
        } else if (command.equalsIgnoreCase("request_ekimus")) {
            if (SoIManager.getCurrentStage() == 2) {
                ReflectionUtils.simpleEnterInstancedZone(player, HeartInfinityAttack.class, ekimusIzId);
            }
        } else if (command.equalsIgnoreCase("enter_seed")) {
            if (SoIManager.getCurrentStage() == 3) {
                SoIManager.teleportInSeed(player);
                return;
            }
        } else if (command.equalsIgnoreCase("hoi_defence")) {
            if (SoIManager.getCurrentStage() == 5) {
                ReflectionUtils.simpleEnterInstancedZone(player, HeartInfinityDefence.class, hoidefIzId);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}