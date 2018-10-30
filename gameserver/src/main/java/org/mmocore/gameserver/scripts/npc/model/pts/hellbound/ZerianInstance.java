package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.List;

/**
 * @author KilRoy
 */
public class ZerianInstance extends NpcInstance {
    private static final int Pass_Skill = 2357;
    private static final int pos_x = -22204;
    private static final int pos_y = 277056;
    private static final int pos_z = -15023;

    public ZerianInstance(int objectId, NpcTemplate template) {
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

        if (command.startsWith("menu_select?ask=-1006&")) {
            if (command.endsWith("reply=1")) {
                final int i0 = HellboundManager.getHellboundLevel();
                if (i0 >= 11) {
                    if (player.getParty() == null || !player.getParty().isLeader(player)) {
                        showChatWindow(player, "pts/hellbound/zerian002a.htm");
                    } else {
                        final List<Player> members = player.getParty().getPartyMembers();
                        int i1 = 0;
                        for (final Player players : members) {
                            if (players == null || !isInRange(players, 300) || players.getEffectList().getEffectsBySkillId(Pass_Skill) == null) {
                                i1++;
                            }
                        }
                        if (i1 == 0) {
                            player.getParty().Teleport(pos_x, pos_y, pos_z);
                        } else {
                            showChatWindow(player, "pts/hellbound/zerian002.htm");
                        }
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/zerian002b.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}