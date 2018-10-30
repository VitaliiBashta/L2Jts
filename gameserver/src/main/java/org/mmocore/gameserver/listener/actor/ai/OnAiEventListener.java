package org.mmocore.gameserver.listener.actor.ai;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.listener.AiListener;
import org.mmocore.gameserver.object.Creature;

@FunctionalInterface
public interface OnAiEventListener extends AiListener {
    void onAiEvent(Creature actor, CtrlEvent evt, Object... args);
}
