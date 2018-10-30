package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

public class TeleporterInstance extends NpcInstance {
    //TODO: вывести птс диалоги и описать функции нубл телепортов и телепортов воснавном. (с)Mangol
    private static final long serialVersionUID = 1L;

    public TeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        String pom;
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        if (getTemplate().getHtmRoot() != null) {
            return getTemplate().getHtmRoot() + pom + ".htm";
        }
        String temp = "teleporter/" + pom + ".htm";
        if (!HtmCache.getInstance().getHtml(temp, player).isEmpty()) {
            return temp;
        }
        return "default/" + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command
        if ("NoblessTeleport".equalsIgnoreCase(actualCommand)) // TODO[K] -
        // fornonoblessitem.htm
        {
            if (player.isNoble() || AllSettingsConfig.ALLOW_NOBLE_TP_TO_ALL) {
                showChatWindow(player, "pts/fornobless.htm");
            } else {
                showChatWindow(player, "pts/fornonobless.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public boolean isTeleportNpc() {
        return true;
    }
}
