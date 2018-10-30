package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public final class TrainerInstance extends NpcInstance // deprecated?
{
    public TrainerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        String pom = "";
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "trainer/" + pom + ".htm";
    }
}
