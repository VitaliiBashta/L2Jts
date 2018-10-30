package org.mmocore.gameserver.scripts.quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class _423_TakeYourBestShot extends Quest {
    private static final int Johnny = 32744;
    private static final int Batracos = 32740;
    private static final int TantaGuard = 18862;
    private static final int SeerUgorosPass = 15496;
    private static final int[] TantaClan = {
            22768,
            22769,
            22770,
            22771,
            22772,
            22773,
            22774
    };

    public _423_TakeYourBestShot() {
        super(true);
        addStartNpc(Johnny);
        addTalkId(Batracos);
        addKillId(TantaGuard);
        addKillId(TantaClan);
        addLevelCheck(82);
        addQuestCompletedCheck(249);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("johnny_q423_04.htm")) {
            st.exitQuest(true);
        } else if (event.equalsIgnoreCase("johnny_q423_05.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Johnny) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                    case QUEST:
                        htmltext = "johnny_q423_00.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "johnny_q423_01.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "johnny_q423_06.htm";
            } else if (cond == 2) {
                htmltext = "johnny_q423_07.htm";
            }
        } else if (npcId == Batracos) {
            if (cond == 1) {
                htmltext = "batracos_q423_01.htm";
            } else if (cond == 2) {
                htmltext = "batracos_q423_02.htm";
                st.giveItems(SeerUgorosPass, 1);
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1) {
            if (ArrayUtils.contains(TantaClan, npcId) && Rnd.chance(1.5)) {
                Location loc = st.getPlayer().getLoc();
                addSpawn(TantaGuard, loc.x, loc.y, loc.z, 0, 100, 120000);
            } else if (npcId == TantaGuard && st.ownItemCount(SeerUgorosPass) < 1) {
                st.setCond(2);
            }
        }
        return null;
    }


}