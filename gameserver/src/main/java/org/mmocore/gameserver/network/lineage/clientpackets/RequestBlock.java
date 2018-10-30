package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RequestBlock extends L2GameClientPacket {
    // format: cd(S)
    private static final Logger _log = LoggerFactory.getLogger(RequestBlock.class);

    private static final int BLOCK = 0;
    private static final int UNBLOCK = 1;
    private static final int BLOCKLIST = 2;
    private static final int ALLBLOCK = 3;
    private static final int ALLUNBLOCK = 4;

    private Integer _type;
    private String targetName = null;

    @Override
    protected void readImpl() {
        _type = readD(); //0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock

        if (_type == BLOCK || _type == UNBLOCK) {
            targetName = readS(16);
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        switch (_type) {
            case BLOCK:
                activeChar.addToBlockList(targetName);
                break;
            case UNBLOCK:
                activeChar.removeFromBlockList(targetName);
                break;
            case BLOCKLIST:
                final Collection<String> blockList = activeChar.getBlockList();

                if (blockList != null) {
                    activeChar.sendPacket(SystemMsg._IGNORE_LIST_);

                    for (final String name : blockList) {
                        activeChar.sendMessage(name);
                    }

                    activeChar.sendPacket(SystemMsg.__EQUALS__);
                }
                break;
            case ALLBLOCK:
                activeChar.setBlockAll(true);
                activeChar.sendPacket(SystemMsg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
                activeChar.sendEtcStatusUpdate();
                break;
            case ALLUNBLOCK:
                activeChar.setBlockAll(false);
                activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
                activeChar.sendEtcStatusUpdate();
                break;
            default:
                _log.info("Unknown 0x0a block type: " + _type);
        }
    }
}
