package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;

/**
 * @author VISTALL
 * @date 23/03/2011
 */
public class ConfirmDlg extends SysMsgContainer<ConfirmDlg> {
    private final int time;
    private int requestId;

    public ConfirmDlg(final SystemMsg msg, final int time) {
        super(msg);
        this.time = time;
    }

    @Override
    protected final void writeData() {
        writeElements();
        writeD(time);
        writeD(requestId);
    }

    public void setRequestId(final int requestId) {
        this.requestId = requestId;
    }
}