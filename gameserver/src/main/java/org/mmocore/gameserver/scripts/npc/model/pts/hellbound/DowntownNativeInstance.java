package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author KilRoy
 */
public class DowntownNativeInstance extends NpcInstance {
    private int i_ai0;

    public DowntownNativeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        startAbnormalEffect(AbnormalEffect.HOLD_2);
        i_ai0 = 0;
        super.onSpawn();
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (i_ai0 == 0) {
            showChatWindow(talker, "pts/hellbound/downtown_native001.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/downtown_native002.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-7801&")) {
            if (command.endsWith("reply=1")) {
                if (i_ai0 == 0) {
                    ChatUtils.say(this, NpcString.THANK_YOU_FOR_SAVING_ME);
                    ChatUtils.say(this, NpcString.GUARD_ARE_COMING_RUN);
                    stopAbnormalEffect(AbnormalEffect.HOLD_2);
                    getAI().broadcastScriptEvent(ScriptEvent.SCE_AMAZKARI_CALL, 5000);
                    i_ai0 = 1;
                    if (HellboundManager.getHellboundLevel() >= 10) {
                        HellboundManager.addConfidence(10);
                    }
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            deleteMe();
                        }
                    }, 3000L);
                } else {
                    ChatUtils.say(this, NpcString.THANK_YOU_FOR_SAVING_ME);
                    ChatUtils.say(this, NpcString.NOW_I_CAN_ESCAPE_ON_MY_OWN);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}