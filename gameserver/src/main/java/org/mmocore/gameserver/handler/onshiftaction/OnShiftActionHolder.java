package org.mmocore.gameserver.handler.onshiftaction;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @date 2:38/19.08.2011
 */
public class OnShiftActionHolder extends AbstractHolder {
    private static final OnShiftActionHolder INSTANCE = new OnShiftActionHolder();

    private final Map<Class<?>, OnShiftActionHandler<?>> handlers = new HashMap<>();

    public static OnShiftActionHolder getInstance() {
        return INSTANCE;
    }

    public <T> void register(final Class<T> clazz, final OnShiftActionHandler<T> t) {
        handlers.put(clazz, t);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameObject> boolean callShiftAction(final Player player, final Class<T> clazz, final T obj, final boolean select) {
        final OnShiftActionHandler<T> l = (OnShiftActionHandler<T>) handlers.get(clazz);
        if (l == null || player.getPlayerVariables().getBoolean(PlayerVariables.NO_SHIFT)) {
            return false;
        }

        if (select && player.getTarget() != obj) {
            player.setTarget(obj);
            player.sendPacket(new MyTargetSelected(obj.getObjectId(), 0));
        }

        final boolean b = l.call(obj, player);
        player.sendPacket(ActionFail.STATIC);
        return b;
    }

    @Override
    public int size() {
        return handlers.size();
    }

    @Override
    public void clear() {
        handlers.clear();
    }
}
