package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.HennaEquipList;
import org.mmocore.gameserver.network.lineage.serverpackets.HennaUnequipList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 08/02/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public final class SymbolMakerInstance extends NpcInstance {
    private static final long serialVersionUID = 8190627702628412725L;

    public SymbolMakerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-16&")) {
            if (command.endsWith("reply=1")) {
                player.sendPacket(new HennaEquipList(player));
            } else if (command.endsWith("reply=2")) {
                player.sendPacket(new HennaUnequipList(player));
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}