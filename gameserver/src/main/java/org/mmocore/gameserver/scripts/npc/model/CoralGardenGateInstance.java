package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.CrystalCaverns;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class CoralGardenGateInstance extends NpcInstance {
    private static final int INSTANCE_ID = 10;

    public CoralGardenGateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32281:
                super.showChatWindow(player, "pts/crystalcave/telecube_crystalcave001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_coralg")) {

            ReflectionUtils.simpleEnterInstancedZone(player, CrystalCaverns.class, INSTANCE_ID);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}