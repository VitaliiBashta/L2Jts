package org.mmocore.gameserver.model.entity.events.impl.reflection;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public enum RTeamType {
    NONE,
    TEAM_1(35423, "FF0000", "Red"),
    TEAM_2(35426, "0000FF", "Blue"),
    TEAM_3(35424, "FFFF00", "Yellow"),
    TEAM_4(35425, "008000", "Green");
    int npcId;
    String color;
    String nameTeam;

    RTeamType(int npcId, String color, String name) {
        this.npcId = npcId;
        this.nameTeam = name;
        this.color = color;
    }

    RTeamType() {

    }

    public String getColor() {
        return color;
    }

    public String getNameTeam() {
        return nameTeam;
    }

    public int getNpcId() {
        return npcId;
    }
}
