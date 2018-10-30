package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

//d(?) - 1
//d(?)d(?)d(?)d(?)d(?)S(?)S(?) - 2
//d(?) : d(?)d(?)d(?)d(?) - 3:3.1
//d(?)d(?)d(?)d(?)d(?)d(?)d(?)d(?)d(?) - 4
//d(?)d(?)d(?)S(?) - 5
//d(?)d(?) - 6
//d(?)d(?)d(?) - 7
//d(?)d(?)d(?)d(?)d(?) - 8
//d(?)d(?)d(?)d(?)d(?) - 9
public class ExCleftState extends GameServerPacket {
    private CleftState cleftState;

    public ExCleftState(final CleftState cleftState) {
        this.cleftState = cleftState;
    }

    @Override
    protected void writeData() {
        writeD(cleftState.ordinal());
        switch (cleftState) {
            case TOTAL:
                //dddddSS - BTeam Point:%d CatID:%d CatName:%s RemainSec:%d RTeam Point:%d CatID:%d CatName:%s RemainSec:%d
                writeD(0x00); // blueTeamPoint
                writeD(0x00); // blueCaTID
                writeD(0x00); // redTeamPoint
                writeD(0x00); // redCaTID
                writeD(0x00); // remainSec
                writeS(""); // catNameBlue
                writeS(""); // catNameRed
                writeD(0x02); // team ordinal (red/blue)
                for (int i = 0; i < 2; i++) {
                    writeD(0x00); // playerID
                    writeD(0x00); // playerKill
                    writeD(0x00); // playerDeath
                    writeD(0x00); // playerKillTOWER
                }
                break;
            case TOWER_DESTROY:
                writeD(0x00); // remainSec
                writeD(0x00); // blueTeamPoint
                writeD(0x00); // redTeamPoint
                writeD(0x00); // teamIdDestroyTower
                writeD(0x00); // destroyTowerType
                writeD(0x00); // playerIdDestroyTower
                writeD(0x00); // totalCleftTowerCount
                writeD(0x00); // playerKillCount
                writeD(0x00); // playerDeathCount
                break;
            case CAT_UPDATE:
                writeD(0x00); // remainSec
                writeD(0x00); // teamId
                writeD(0x00); // catId
                writeS(""); // catName
                break;
            case RESULT:
                writeD(0x00); // winTeamId
                writeD(0x00); // loseTeamId
                break;
            case PVP_KILL:
                writeD(0x00); // blueTeamPoint
                writeD(0x00); // redTeamPoint
                writeD(0x02); // team ordinal (red/blue)
                for (int i = 0; i < 2; i++) {
                    writeD(0x00); // killTeamId
                    writeD(0x00); // killPlayerId
                    writeD(0x00); // killTeamCleftTowerKillCount
                    writeD(0x00); // playerKillCount
                    writeD(0x00); // playerDeathCount
                }
                //writeD(0x00); // remainSec
                break;
        }
    }

    public enum CleftState {
        TOTAL(0),
        TOWER_DESTROY(1),
        CAT_UPDATE(2),
        RESULT(3),
        PVP_KILL(4);

        private int cleftState;

        CleftState(final int cleftState) {
            this.cleftState = cleftState;
        }

        public int getState() {
            return cleftState;
        }
    }
}