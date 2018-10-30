package org.mmocore.gameserver.phantoms.action;

import org.jts.dataparser.data.holder.UserBasicActionHolder;
import org.jts.dataparser.data.holder.userbasicaction.UserBasicAction;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.object.GameObject;

import java.util.Optional;

/**
 * Created by Hack
 * Date: 23.08.2016 20:07
 */
public class RandomUserAction extends AbstractPhantomAction {
    @Override
    public long getDelay() {
        return 0;
    }

    @Override
    public void run() {
        int actionId = PhantomConfig.userActions[Rnd.get(PhantomConfig.userActions.length)];
        final Optional<UserBasicAction> actionOptional = UserBasicActionHolder.getInstance().getAction(actionId);
        if (!actionOptional.isPresent()) {
            log.warn("Unhandled user action for phantom: " + actor.getName());
            return;
        }
        final UserBasicAction action = actionOptional.get();
        final Optional<IUserBasicActionHandler> handler = action.getHandler();
        if (handler.isPresent()) {
            final Optional<GameObject> target = Optional.ofNullable(actor.getTarget());
            handler.get().useAction(actor, actionId, action.getOption(), action.getUseSkill(), target, false, false);
        }
    }
}
