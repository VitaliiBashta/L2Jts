package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

import java.util.concurrent.Future;

/**
 * @author KilRoy
 */
public class CitizenNativeInstance extends NpcInstance {
    private Future<?> foodThreadNative;

    public CitizenNativeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        final NpcInstance actor = this;
        foodThreadNative = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (actor != null && !actor.isDead()) {
                    if (HellboundManager.getHellboundLevel() <= 5) {
                        ChatUtils.say(actor, NpcString.HUN_HUNGRY);
                        actor.suicide(null);
                    }
                } else {
                    if (foodThreadNative != null) {
                        foodThreadNative.cancel(false);
                        foodThreadNative = null;
                    }
                }
            }
        }, 10 * 60 * 1000L, 10 * 60 * 1000L);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (i0 <= 5) {
            showChatWindow(talker, "pts/hellbound/citizen_native_npc001.htm");
        } else if (i0 >= 6) {
            showChatWindow(talker, "pts/hellbound/citizen_native_npc002.htm");
        }
    }
}