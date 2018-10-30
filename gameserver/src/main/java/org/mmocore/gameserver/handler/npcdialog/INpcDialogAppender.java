package org.mmocore.gameserver.handler.npcdialog;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 15:32 13.08.11
 */
public interface INpcDialogAppender {
    String getAppend(Player player, NpcInstance npc, int val);

    int[] getNpcIds();
}
