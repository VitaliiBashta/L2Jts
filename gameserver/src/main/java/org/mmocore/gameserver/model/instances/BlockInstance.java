package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 23:18/12.12.2010
 */
public class BlockInstance extends NpcInstance {
    private boolean _isRed;

    public BlockInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    public boolean isRed() {
        return _isRed;
    }

    public void setRed(final boolean red) {
        _isRed = red;
        broadcastCharInfo();
    }

    public void changeColor() {
        setRed(!_isRed);
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
    }

    @Override
    public boolean isNameAbove() {
        return false;
    }

    @Override
    public int getFormId() {
        return _isRed ? 0x53 : 0;
    }

    @Override
    public boolean isInvul() {
        return true;
    }
}
