package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.model.reference.L2Reference;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

/**
 * @author VISTALL
 * @date 20:20/03.01.2011
 */
public class ControlKeyInstance extends GameObject {
    protected final HardReference<ControlKeyInstance> reference;

    public ControlKeyInstance() {
        super(IdFactory.getInstance().getNextId());
        reference = new L2Reference<>(this);
    }

    @Override
    public HardReference<ControlKeyInstance> getRef() {
        return reference;
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));
            return;
        }

        player.sendActionFailed();
    }
}
