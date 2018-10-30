package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.impl.AerialCleftEvent;
import org.mmocore.gameserver.model.entity.events.objects.AerialCleftPlayerObject;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.List;

/**
 * @author KilRoy
 */
public class ExCleftList extends GameServerPacket {
    public static final ExCleftList STATIC_CLOSE = new ExCleftList(CleftType.CLOSE);
    private AerialCleftEvent event;
    private CleftType cleftType;
    private TeamType teamType;
    private TeamType teamTypeFrom;
    private Player player;
    private List<AerialCleftPlayerObject> teamList;

    public ExCleftList(final CleftType cleftType, final Player player, final TeamType teamType) {
        this.cleftType = cleftType;
        this.teamType = teamType;
        this.player = player;
    }

    public ExCleftList(final CleftType cleftType, final Player player, final TeamType teamTypeFrom, final TeamType teamTypeTo) {
        this.cleftType = cleftType;
        this.teamTypeFrom = teamTypeFrom;
        this.teamType = teamTypeTo;
        this.player = player;
    }

    public ExCleftList(final CleftType cleftType, final AerialCleftEvent event, final TeamType teamType) {
        this.cleftType = cleftType;
        this.event = event;
        this.teamType = teamType;
        teamList = event.getObjects(teamType);
    }

    public ExCleftList(final CleftType cleftType) {
        this.cleftType = cleftType;
    }

    @Override
    protected void writeData() {
        writeD(cleftType.getType());
        switch (cleftType) {
            case CLOSE:
                break;
            case TOTAL:
                writeD(event.getTotalMemberCount(teamType));
                writeD(event.getTotalMatchScore(teamType));
                writeD(teamType.ordinal());
                for (final AerialCleftPlayerObject player : teamList) {
                    writeD(player.getObjectId());
                    writeS(player.getName());
                }
                break;
            case ADD:
                writeD(teamType.ordinal());
                writeD(player.getObjectId());
                writeS(player.getName());
                break;
            case REMOVE:
                writeD(teamType.ordinal());
                writeD(player.getObjectId());
                break;
            case TEAM_CHANGE:
                writeD(player.getObjectId());
                writeD(teamTypeFrom.ordinal());
                writeD(teamType.ordinal());
                break;
        }
    }

    public static enum CleftType {
        CLOSE(-1),
        TOTAL(0),
        ADD(1),
        REMOVE(2),
        TEAM_CHANGE(3);

        private int type;

        CleftType(final int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}