package org.mmocore.gameserver.scripts.npc.model.pts.stakato_nest;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author : Mangol
 */
public class KintaijinInstance extends NpcInstance {
    private static final int telpos_1_x = 80456;
    private static final int telpos_1_y = -52322;
    private static final int telpos_1_z = -5640;
    private static final int telpos_2_x = 88718;
    private static final int telpos_2_y = -46214;
    private static final int telpos_2_z = -4640;
    private static final int telpos_3_x = 87464;
    private static final int telpos_3_y = -54221;
    private static final int telpos_3_z = -5120;
    private static final int telpos_4_x = 80848;
    private static final int telpos_4_y = -49426;
    private static final int telpos_4_z = -5128;
    private static final int telpos_5_x = 87682;
    private static final int telpos_5_y = -43291;
    private static final int telpos_5_z = -4128;

    public KintaijinInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/stakato_nest/kintaijin001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-240&")) {
            if (command.endsWith("reply=1")) {
                QuestState qs = player.getQuestState(240);
                if (qs != null && qs.isCompleted()) {
                    showChatWindow(player, "pts/stakato_nest/kintaijin002.htm");
                } else {
                    showChatWindow(player, "pts/stakato_nest/kintaijin002a.htm");
                }
            } else if (command.endsWith("reply=10")) {
                Party party = player.getParty();
                if (party == null) {
                    player.teleToLocation(telpos_1_x, telpos_1_y, telpos_1_z);
                } else {
                    party.getPartyMembers().stream().filter(member -> member != null && member.isInRange(player, 2000)).forEach(member -> {
                        member.teleToLocation(telpos_1_x, telpos_1_y, telpos_1_z);
                    });
                }
            } else if (command.endsWith("reply=11")) {
                Party party = player.getParty();
                if (party == null) {
                    player.teleToLocation(telpos_2_x, telpos_2_y, telpos_2_z);
                } else {
                    party.getPartyMembers().stream().filter(member -> member != null && member.isInRange(player, 2000)).forEach(member -> {
                        member.teleToLocation(telpos_2_x, telpos_2_y, telpos_2_z);
                    });
                }
            } else if (command.endsWith("reply=12")) {
                Party party = player.getParty();
                if (party == null) {
                    player.teleToLocation(telpos_3_x, telpos_3_y, telpos_3_z);
                } else {
                    party.getPartyMembers().stream().filter(member -> member != null && member.isInRange(player, 2000)).forEach(member -> {
                        member.teleToLocation(telpos_3_x, telpos_3_y, telpos_3_z);
                    });
                }
            } else if (command.endsWith("reply=13")) {
                Party party = player.getParty();
                if (party == null) {
                    player.teleToLocation(telpos_4_x, telpos_4_y, telpos_4_z);
                } else {
                    party.getPartyMembers().stream().filter(member -> member != null && member.isInRange(player, 2000)).forEach(member -> {
                        member.teleToLocation(telpos_4_x, telpos_4_y, telpos_4_z);
                    });
                }
            } else if (command.endsWith("reply=14")) {
                Party party = player.getParty();
                if (party == null) {
                    player.teleToLocation(telpos_5_x, telpos_5_y, telpos_5_z);
                } else {
                    party.getPartyMembers().stream().filter(member -> member != null && member.isInRange(player, 2000)).forEach(member -> {
                        member.teleToLocation(telpos_5_x, telpos_5_y, telpos_5_z);
                    });
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
