package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class update_announcer extends DefaultAI {

    private static final String fnHTML002 = "pts/updateannouncer/update_announcer002.htm";
    private static final String fnHTML002a = "pts/updateannouncer/update_announcer002a.htm";
    private static final String fnHTML002b = "pts/updateannouncer/update_announcer002b.htm";
    private static final String fnHTML002c = "pts/updateannouncer/update_announcer002c.htm";
    private static final String fnHTML002d = "pts/updateannouncer/update_announcer002d.htm";
    private static final String fnHTML002e = "pts/updateannouncer/update_announcer002e.htm";
    private static final String fnHTML002f = "pts/updateannouncer/update_announcer002f.htm";
    private static final String fnHTML002g = "pts/updateannouncer/update_announcer002g.htm";
    private static final String fnHTML002h = "pts/updateannouncer/update_announcer002h.htm";
    private static final String fnHTML002i = "pts/updateannouncer/update_announcer002i.htm";
    private static final String fnNotAllowed = "pts/updateannouncer/update_announcer006.htm";

    public update_announcer(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (ask == -7801) {
            switch (reply) {
                case 1:
                    actor.showChatWindow(player, fnHTML002);
                    break;
                case 2:
                    collectTeleportList(6, player);
                    break;
                case 3:
                    collectTeleportList(7, player);
                    break;
                case 91:
                    actor.showChatWindow(player, fnHTML002a);
                    break;
                case 92:
                    actor.showChatWindow(player, fnHTML002b);
                    break;
                case 93:
                    actor.showChatWindow(player, fnHTML002c);
                    break;
                case 94:
                    actor.showChatWindow(player, fnHTML002d);
                    break;
                case 95:
                    actor.showChatWindow(player, fnHTML002e);
                    break;
                case 96:
                    actor.showChatWindow(player, fnHTML002f);
                    break;
                case 97:
                    actor.showChatWindow(player, fnHTML002g);
                    break;
                case 98:
                    actor.showChatWindow(player, fnHTML002h);
                    break;
                case 99:
                    actor.showChatWindow(player, fnHTML002i);
                    break;
                case 10:
                    collectTeleportList(1, player);
                    break;
                case 11:
                    collectTeleportList(2, player);
                    break;
                case 12:
                    collectTeleportList(3, player);
                    break;
                case 13:
                    collectTeleportList(4, player);
                    break;
                case 14:
                    collectTeleportList(5, player);
                    break;
            }
        }

    }

    private void collectTeleportList(final int listId, final Player player) {
        switch (listId) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                if (player.getAdena() < 10000) {
                    getActor().showChatWindow(player, fnNotAllowed);
                    return;
                }
                break;
            case 6:
                if (player.getAdena() < 100000) {
                    getActor().showChatWindow(player, fnNotAllowed);
                    return;
                }
                break;
        }
        final TeleportLocation[] list = getActor().getTemplate().getTeleportList(listId);
        if (list != null)
            getActor().showTeleportList(player, list, listId);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}