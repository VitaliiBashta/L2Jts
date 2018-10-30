package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Mangol
 */
public class AltarGuardDeadInstance extends NpcInstance {
    private static final String fnYouAreHero = "pts/altar/g_altar_guard_dead002.htm";

    public AltarGuardDeadInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (talker.isHero()) {
            showChatWindow(talker, fnYouAreHero);
        } else {
            super.showChatWindow(talker, val);
        }
    }
}
