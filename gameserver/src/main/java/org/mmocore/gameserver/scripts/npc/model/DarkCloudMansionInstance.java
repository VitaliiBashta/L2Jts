package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.instances.DarkCloudMansion;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author KilRoy
 */
public class DarkCloudMansionInstance extends NpcInstance {
    private static final int INSTANCE_ID = 9;
    private static final Location LOCATION = new Location(139968, 150367, -3111);

    public DarkCloudMansionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 32282:
                showChatWindow(player, "pts/darkcloudmansion/beles_hideout_keeper001.htm");
                break;
            case 32288:
                if (player.getReflection().getInstancedZoneId() == INSTANCE_ID) {
                    showChatWindow(player, "pts/darkcloudmansion/room_healer_darkcloud001.htm");
                    player.getReflection().getDoor(24230002).openMe();
                } else {
                    super.showChatWindow(player, val);
                }
                break;
            case 32289:
                if (player.getReflection().getInstancedZoneId() == INSTANCE_ID) {
                    showChatWindow(player, "pts/darkcloudmansion/room_henge_darkcloud001.htm");
                    player.getReflection().getDoor(24230005).openMe();
                } else {
                    super.showChatWindow(player, val);
                }
                break;
            case 32290:
                if (player.getReflection().getInstancedZoneId() == INSTANCE_ID) {
                    showChatWindow(player, "pts/darkcloudmansion/room_pathfind_darkcloud001.htm");
                    player.getReflection().getDoor(24230004).openMe();
                } else {
                    super.showChatWindow(player, val);
                }
                break;
            case 32291:
                if (player.getReflection().getInstancedZoneId() == INSTANCE_ID) {
                    showChatWindow(player, "pts/darkcloudmansion/room_truefalse_darkcloud001.htm");
                } else {
                    super.showChatWindow(player, val);
                }
                break;
            case 32324:
                if (player.getReflection().getInstancedZoneId() == INSTANCE_ID) {
                    final DarkCloudMansion reflection = (DarkCloudMansion) player.getReflection();
                    if (reflection != null) {
                        reflection.validateMonolithStone(this, player);
                        player.sendActionFailed();
                    }
                } else {
                    super.showChatWindow(player, val);
                }
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("enterInstance")) {
            if (player.getLevel() >= 78) //&& player.getParty() != null
            {
                ReflectionUtils.simpleEnterInstancedZone(player, DarkCloudMansion.class, INSTANCE_ID);
            } else {
                showChatWindow(player, "pts/darkcloudmansion/beles_hideout_keeper001a.htm");
            }
        } else if (command.equalsIgnoreCase("exitInstance")) {
            final DarkCloudMansion reflection = (DarkCloudMansion) player.getReflection();
            if (reflection != null) {
                if (reflection.checkRewardedPlayer(player.getObjectId())) {
                    ItemFunctions.addItem(player, 9690, 1); // Contaminated Crystal
                }
                final Location newLoc = reflection.getReturnLoc() != null ? reflection.getReturnLoc() : LOCATION;
                player.teleToLocation(newLoc, ReflectionManager.DEFAULT);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}