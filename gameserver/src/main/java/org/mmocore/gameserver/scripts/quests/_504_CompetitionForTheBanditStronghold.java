package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 */
public class _504_CompetitionForTheBanditStronghold extends Quest {
    // NPC
    private static final int MESSENGER = 35437;
    // MOBS
    private static final int TARLK_BUGBEAR = 20570;
    private static final int TARLK_BUGBEAR_WARRIOR = 20571;
    private static final int TARLK_BUGBEAR_HIGH_WARRIOR = 20572;
    private static final int TARLK_BASILISK = 20573;
    private static final int ELDER_TARLK_BASILISK = 20574;

    // ITEMS
    private static final int AMULET = 4332;
    private static final int ALIANCE_TROPHEY = 5009;
    private static final int CONTEST_CERTIFICATE = 4333;

    public _504_CompetitionForTheBanditStronghold() {
        super(PARTY_ALL);

        addStartNpc(MESSENGER);
        addTalkId(MESSENGER);
        addKillId(TARLK_BUGBEAR);
        addKillId(TARLK_BUGBEAR_WARRIOR);
        addKillId(TARLK_BUGBEAR_HIGH_WARRIOR);
        addKillId(TARLK_BASILISK);
        addKillId(ELDER_TARLK_BASILISK);
        addQuestItem(CONTEST_CERTIFICATE, AMULET, ALIANCE_TROPHEY);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmlText = event;
        if (event.equalsIgnoreCase("azit_messenger_q0504_02.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.giveItems(CONTEST_CERTIFICATE, 1);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmlText;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.getPlayer();
        Clan clan = player.getClan();
        ClanHall clanhall = ResidenceHolder.getInstance().getResidence(35);

        if (clanhall.getSiegeEvent().isRegistrationOver()) {
            htmltext = null;
            showHtmlFile(player, "azit_messenger_q0504_03.htm", false, "%siege_time%", TimeUtils.dateTimeFormat(clanhall.getSiegeDate()));
        } else if (clan == null || player.getObjectId() != clan.getLeaderId()) {
            htmltext = "azit_messenger_q0504_05.htm";
        } else if (player.getObjectId() == clan.getLeaderId() && clan.getLevel() < 4) {
            htmltext = "azit_messenger_q0504_04.htm";
        } else if (clanhall.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) != null) {
            htmltext = "azit_messenger_q0504_06.htm";
        } else if (clan.getHasHideout() > 0) {
            htmltext = "azit_messenger_q0504_10.htm";
        } else {
            if (cond == 0) {
                htmltext = "azit_messenger_q0504_01.htm";
            } else if (st.ownItemCount(CONTEST_CERTIFICATE) == 1 && st.ownItemCount(AMULET) < 30) {
                htmltext = "azit_messenger_q0504_07.htm";
            } else if (st.ownItemCount(ALIANCE_TROPHEY) >= 1) {
                htmltext = "azit_messenger_q0504_07a.htm";
            } else if (st.ownItemCount(CONTEST_CERTIFICATE) == 1 && st.ownItemCount(AMULET) == 30) {
                st.takeItems(AMULET, -1);
                st.takeItems(CONTEST_CERTIFICATE, -1);
                st.giveItems(ALIANCE_TROPHEY, 1);
                st.soundEffect(SOUND_FINISH);
                st.setCond(-1);
                htmltext = "azit_messenger_q0504_08.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.ownItemCount(AMULET) < 30) {
            st.giveItems(AMULET, 1);
            st.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }


}
