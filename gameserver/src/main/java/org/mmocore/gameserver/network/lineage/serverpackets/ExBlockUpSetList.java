package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.List;

/**
 * @author KilRoy
 */
public class ExBlockUpSetList extends GameServerPacket {
    private int objectId;
    private String name;
    private boolean isRedTeam;
    private final int blockUpType;
    private int seconds;
    private List<Player> bluePlayers;
    private List<Player> redPlayers;
    private int roomNumber;

    public ExBlockUpSetList(final List<Player> redPlayers, final List<Player> bluePlayers, final int roomNumber, final int blockUpType) {
        this.blockUpType = blockUpType;
        this.redPlayers = redPlayers;
        this.bluePlayers = bluePlayers;
        this.roomNumber = roomNumber - 1;
    }

    public ExBlockUpSetList(final Player player, final boolean isRedTeam, final int blockUpType) {
        this.blockUpType = blockUpType;
        objectId = player.getObjectId();
        name = player.getName();
        this.isRedTeam = isRedTeam;
    }

    public ExBlockUpSetList(final int seconds, final int blockUpType) {
        this.blockUpType = blockUpType;
        this.seconds = seconds;
    }

    public ExBlockUpSetList(final int blockUpType) {
        this.blockUpType = blockUpType;
    }

    @Override
    protected void writeData() {
        writeD(blockUpType);
        switch (blockUpType) {
            case 0:
                writeD(roomNumber);
                writeD(0xffffffff);

                writeD(bluePlayers.size());
                for (final Player player : bluePlayers) {
                    writeD(player.getObjectId());
                    writeS(player.getName());
                }
                writeD(redPlayers.size());
                for (final Player player : redPlayers) {
                    writeD(player.getObjectId());
                    writeS(player.getName());
                }
                break;
            case 1:
                writeD(0xffffffff);
                writeD(isRedTeam ? 0x01 : 0x00);
                writeD(objectId);
                writeS(name);
                break;
            case 2:
                writeD(0xffffffff);
                writeD(isRedTeam ? 0x01 : 0x00);
                writeD(objectId);
                break;
            case 3:
                writeD(seconds);
                break;
            case 4:
                break;
            case 5:
                writeD(objectId);
                writeD(isRedTeam ? 0x01 : 0x00);
                writeD(isRedTeam ? 0x00 : 0x01);
                break;
            case -1:
                break;
        }
    }
}