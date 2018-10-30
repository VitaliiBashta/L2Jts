package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.GuardInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author Mangol
 */
public class AltarPlayerInstance extends GuardInstance {
    public AltarPlayerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(final Player talker, final int val, final Object... arg) {
        if (talker.getKarma() > 0) {
            if (Rnd.get(3) < 1) {
                ChatUtils.say(this, NpcString.valueOf(1900160), talker.getName());
            } else if (Rnd.get(9) < 1) {
                ChatUtils.say(this, NpcString.valueOf(1900161), talker.getName());
            } else {
                super.showChatWindow(talker, val);
            }
        } else {
            super.showChatWindow(talker, val);
        }
    }
}
