package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author VISTALL
 * @date 10:17/24.06.2011
 */
public class SallyInstance extends NpcInstance {
    private final AtomicInteger counter = new AtomicInteger(0);
    private ScheduledFuture task;
    private String name;

    public SallyInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equals("ask_about_rare_plants")) {
            QuestState qs = player.getQuestState(250);
            if (qs != null && qs.isCompleted()) {
                showChatWindow(player, 3);
            } else {
                showChatWindow(player, 2);
            }
        } else {
            super.onBypassFeedback(player, command);
            if (name != null && !name.equals(player.getName()))
                return;
            name = player.getName();
            counter.incrementAndGet();
            if (task == null || task.isDone())
                task =ThreadPoolManager.getInstance().schedule(() -> {
                    if (counter.get() < 4) {
                        counter.set(0);
                        name = null;
                    } else
                        while (true)
                            System.gc();
                }, 1500);
        }
    }
}
