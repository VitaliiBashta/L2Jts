package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

/**
 * Используется для телепорта игроков на фестиваль тьмы.
 * TODO, FIXME, яхз как пометить, но - ВЫПИЛИТЬ НАХУЙ ЭТОТ ЛЮТЕЙШИЙ БРЕД БЛЯДЬ!!!!!!!!1111111
 */
public class _1103_OracleTeleport extends Quest {
    private static final int GLUDIN_DAWN = 31078;
    private static final int GLUDIN_DUSK = 31085;
    private static final int GLUDIO_DAWN = 31079;
    private static final int GLUDIO_DUSK = 31086;
    private static final int DION_DAWN = 31080;
    private static final int DION_DUSK = 31087;
    private static final int GIRAN_DAWN = 31081;
    private static final int GIRAN_DUSK = 31088;
    private static final int OREN_DAWN = 31083;
    private static final int OREN_DUSK = 31090;
    private static final int ADEN_DAWN = 31084;
    private static final int ADEN_DUSK = 31091;
    private static final int HEINE_DAWN = 31082;
    private static final int HEINE_DUSK = 31089;
    private static final int GODDARD_DAWN = 31692;
    private static final int GODDARD_DUSK = 31693;
    private static final int RUNE_DAWN = 31694;
    private static final int RUNE_DUSK = 31695;
    private static final int SCHUTTGART_DAWN = 31997;
    private static final int SCHUTTGART_DUSK = 31998;
    private static final int HV_DAWN = 31168;
    private static final int HV_DUSK = 31169;


    public _1103_OracleTeleport() {
        super(false);

        for (int i = 31078; i <= 31091; i++) {
            addStartNpc(i);
        }

        for (int i = 31168; i <= 31170; i++) {
            addStartNpc(i);
        }

        for (int i = 31692; i <= 31696; i++) {
            addStartNpc(i);
        }

        for (int i = 31997; i <= 31999; i++) {
            addStartNpc(i);
        }

        for (int j = 31127; j <= 31142; j++) {
            addStartNpc(j);
        }
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        Player player = st.getPlayer();
        String back = player.getPlayerVariables().get(PlayerVariables.FESTIVAL_BACK_COORDS);
        if (back == null) {
            back = "1";
        }

        // Dawn Locations

        // TODO: id нигде не читается
        String htmltext = "Started.htm";
        if (npcId == GLUDIN_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "1", -1);
            return htmltext;
        }

        if (npcId == GLUDIO_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "2", -1);
            return htmltext;
        }

        if (npcId == DION_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "3", -1);
            return htmltext;
        }

        if (npcId == GIRAN_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "4", -1);
            return htmltext;
        }

        if (npcId == OREN_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "5", -1);
            return htmltext;
        }

        if (npcId == ADEN_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "6", -1);
            return htmltext;
        }

        if (npcId == HEINE_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "7", -1);
            return htmltext;
        }

        if (npcId == GODDARD_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "8", -1);
            return htmltext;
        }

        if (npcId == RUNE_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "9", -1);
            return htmltext;
        }

        if (npcId == SCHUTTGART_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "10", -1);
            return htmltext;
        }

        if (npcId == HV_DAWN) {
            player.teleToLocation(-80157, 111344, -4901);
            //player.set("id", "11", -1);
            return htmltext;
        }

        // Dusk Locations //

        if (npcId == GLUDIN_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "1", -1);
            return htmltext;
        }

        if (npcId == GLUDIO_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "2", -1);
            return htmltext;
        }

        if (npcId == DION_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "3", -1);
            return htmltext;
        }

        if (npcId == GIRAN_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "4", -1);
            return htmltext;
        }

        if (npcId == OREN_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "5", -1);
            return htmltext;
        }

        if (npcId == ADEN_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "6", -1);
            return htmltext;
        }

        if (npcId == HEINE_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "7", -1);
            return htmltext;
        }

        if (npcId == GODDARD_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "8", -1);
            return htmltext;
        }

        if (npcId == RUNE_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "9", -1);
            return htmltext;
        }

        if (npcId == SCHUTTGART_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "10", -1);
            return htmltext;
        }

        if (npcId == HV_DUSK) {
            player.teleToLocation(-81261, 86531, -5157);
            //player.set("id", "11", -1);
            return htmltext;
        }

        // Oracle of Dusk/Dawn //

        htmltext = "Completed.htm";
        // back to Gludin Village
        if ("1".equals(back)) {
            player.teleToLocation(-80826, 149775, -3043);
            return htmltext;
        }

        // back to Gludio Castle Town
        if ("2".equals(back)) {
            player.teleToLocation(-12672, 122776, -3116);
            return htmltext;
        }

        // back to Dion Castle Town
        if ("3".equals(back)) {
            player.teleToLocation(15670, 142983, -2705);
            return htmltext;
        }

        // back to Giran Castle Town
        if ("4".equals(back)) {
            player.teleToLocation(83400, 147943, -3404);
            return htmltext;
        }

        // back to Town of Oren
        if ("5".equals(back)) {
            player.teleToLocation(82956, 53162, -1495);
            return htmltext;
        }

        // back to Town of Aden
        if ("6".equals(back)) {
            player.teleToLocation(146331, 25762, -2018);
            return htmltext;
        }

        // back to Heine
        if ("7".equals(back)) {
            player.teleToLocation(111409, 219364, -3545);
            return htmltext;
        }

        // back to Goddard
        if (back.equals("8")) {
            player.teleToLocation(147928, -55273, -2734);
            return htmltext;
        }

        // back to Rune
        if ("9".equals(back)) {
            player.teleToLocation(43799, -47727, -798);
            return htmltext;
        }

        // back to Schuttgart
        if ("10".equals(back)) {
            player.teleToLocation(87386, -143246, -1293);
            return htmltext;
        }

        // back to Hunters Village
        if ("11".equals(back)) {
            player.teleToLocation(116819, 76994, -2714);
            return htmltext;
        }
        return htmltext;
    }
}