package org.mmocore.gameserver.scripts.npc.model.pts.defaults;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author Magister
 * @date 22/02/2015
 * @npc 32330
 * @memo Используется для входа в инстанс, для квеста _179_IntoTheLargeCavern.
 */
public final class GardenGuardInstance extends NpcInstance {
    private static final long serialVersionUID = -6872479784699703701L;
    private static final int inzone_id = 11;

    public GardenGuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-511&")) {
            if (command.endsWith("reply=1")) {
                if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael && player.getQuestState(179) != null && player.getQuestState(179).isCompleted()) {
                    showChatWindow(player, "pts/default/ndgarden_enterer003.htm");
                    return;
                } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael && player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First) && player.getQuestState(179) != null && player.getQuestState(179).isStarted()) {
                    if (player.getParty() != null && player.getParty().isLeader(player)) {
                        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
                        showChatWindow(player, "pts/default/ndgarden_enterer004.htm");
                        return;
                    } else
                        showChatWindow(player, "pts/default/ndgarden_enterer006.htm");
                } else
                    showChatWindow(player, "pts/default/ndgarden_enterer002.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}