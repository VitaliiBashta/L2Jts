package org.mmocore.gameserver.listener.actor.ai;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.listener.AiListener;
import org.mmocore.gameserver.object.Creature;

@FunctionalInterface
public interface OnAiIntentionListener extends AiListener {
    void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
