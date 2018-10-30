package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.Future;

/**
 * @author KilRoy
 */
public class ShhadaiInstance extends NpcInstance {
    private static final int PosX = 9032;
    private static final int PosY = 253063;
    private static final int PosZ = -1928;
    private static final int PosX_temp = 16882;
    private static final int PosY_temp = 238952;
    private static final int PosZ_temp = 9776;
    private int i_ai3;
    private Future<?> thread;

    public ShhadaiInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        final boolean isNight = GameTimeManager.getInstance().isNowNight();

        if (isNight) {
            teleToLocation(PosX, PosY, PosZ);
            i_ai3 = 1;
        } else {
            teleToLocation(PosX_temp, PosY_temp, PosZ_temp);
            i_ai3 = 0;
        }

        thread = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new RunnableImpl() {
            @Override
            public void runImpl() {
                final boolean isNight = GameTimeManager.getInstance().isNowNight();
                if (isNight) {
                    if (i_ai3 != 1) {
                        teleToLocation(PosX, PosY, PosZ);
                        i_ai3 = 1;
                    }
                } else if (i_ai3 == 1) {
                    teleToLocation(PosX_temp, PosY_temp, PosZ_temp);
                    i_ai3 = 0;
                }
            }
        }, 10000L, 10000L);
    }

    @Override
    protected void onDespawn() {
        if (thread != null) {
            thread.cancel(false);
            thread = null;
        }
        super.onDespawn();
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
                showChatWindow(player, "pts/hellbound/shhadai002.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}