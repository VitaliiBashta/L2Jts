package org.mmocore.gameserver.scripts.ai.residences.dominion;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.taskmanager.AiTaskManager;
import org.mmocore.gameserver.utils.ChatUtils;

import java.time.Duration;
import java.time.LocalTime;

public class MercenaryCaptain extends DefaultAI {
    private static final NpcString[] MESSAGES = {
            NpcString.COURAGE_AMBITION_PASSION_MERCENARIES_WHO_WANT_TO_REALIZE_THEIR_DREAM_OF_FIGHTING_IN_THE_TERRITORY_WAR_COME_TO_ME_FORTUNE_AND_GLORY_ARE_WAITING_FOR_YOU,
            NpcString.DO_YOU_WISH_TO_FIGHT_ARE_YOU_AFRAID_NO_MATTER_HOW_HARD_YOU_TRY_YOU_HAVE_NOWHERE_TO_RUN
    };

    public MercenaryCaptain(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = AI_TASK_ATTACK_DELAY = 1000L;
    }

    private static long calcDelay() {
        LocalTime time = LocalTime.now().withMinute(55).withSecond(0);
        final LocalTime now = LocalTime.now();

        while (time.isBefore(now))
            time = time.plusHours(1L);

        return Duration.between(now, time).toMillis();
    }

    @Override
    public final synchronized void startAITask() {
        if (haveAiTask.compareAndSet(false, true))
            aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, calcDelay(), 3600000L);
    }

    @Override
    protected final synchronized void switchAITask(long NEW_DELAY) {
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        NpcString shout;
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        if (runnerEvent.isInProgress()) {
            shout = NpcString.CHARGE_CHARGE_CHARGE;
        } else {
            shout = MESSAGES[Rnd.get(MESSAGES.length)];
        }

        ChatUtils.shout(actor, shout);

        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}