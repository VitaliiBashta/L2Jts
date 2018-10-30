package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister, KilRoy
 * @date 14/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _419_GetaPet extends Quest {
    // npc
    private static final int pet_manager_martin = 30731;
    private static final int gatekeeper_belladonna = 30256;
    private static final int metty = 30072;
    private static final int elliany = 30091;
    // mobs
    private static final int lesser_dark_horror = 20025;
    private static final int stopper = 20034;
    private static final int giant_spider = 20103;
    private static final int dark_horror = 20105;
    private static final int poker = 20106;
    private static final int blader = 20108;
    private static final int hook_spider = 20308;
    private static final int hunter_tarantula = 20403;
    private static final int crimson_spider = 20460;
    private static final int pincer_spider = 20466;
    private static final int kasha_spider = 20474;
    private static final int kasha_poker_spider = 20476;
    private static final int kasha_blade_spider = 20478;
    private static final int plunder_tarantula = 20508;
    private static final int carmine_spider = 22244;
    // questitem
    private static final int animal_lovers_list = 3417;
    private static final int animal_slayer_list1 = 3418;
    private static final int animal_slayer_list2 = 3419;
    private static final int animal_slayer_list3 = 3420;
    private static final int animal_slayer_list4 = 3421;
    private static final int animal_slayer_list5 = 3422;
    private static final int q_animal_slayer_list6 = 10164;
    private static final int bloody_fang = 3423;
    private static final int bloody_claw = 3424;
    private static final int bloody_nail = 3425;
    private static final int bloody_kasha_fang = 3426;
    private static final int bloody_tarantula_nail = 3427;
    private static final int q_bloody_carmine_nail = 10165;
    private static final int wolf_collar = 2375;

    public _419_GetaPet() {
        super(false);
        addStartNpc(pet_manager_martin);
        addTalkId(gatekeeper_belladonna, metty, elliany);
        addKillId(lesser_dark_horror, stopper, giant_spider, dark_horror, poker, blader, hook_spider, hunter_tarantula, crimson_spider, pincer_spider, kasha_spider, kasha_poker_spider, kasha_blade_spider, plunder_tarantula, carmine_spider);
        addQuestItem(animal_lovers_list, animal_slayer_list1, animal_slayer_list2, animal_slayer_list3, animal_slayer_list4, animal_slayer_list5, q_animal_slayer_list6, bloody_fang, bloody_claw, bloody_nail, bloody_kasha_fang, bloody_tarantula_nail, q_bloody_carmine_nail);
        addLevelCheck(15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("get_a_pet");
        int npcId = npc.getNpcId();
        if (npcId == pet_manager_martin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                switch (st.getPlayer().getPlayerTemplateComponent().getPlayerRace()) {
                    case human:
                        st.giveItems(animal_slayer_list1, 1);
                        htmltext = "pet_manager_martin_q0419_04.htm";
                        break;
                    case elf:
                        st.giveItems(animal_slayer_list2, 1);
                        htmltext = "pet_manager_martin_q0419_05.htm";
                        break;
                    case darkelf:
                        st.giveItems(animal_slayer_list3, 1);
                        htmltext = "pet_manager_martin_q0419_06.htm";
                        break;
                    case orc:
                        st.giveItems(animal_slayer_list4, 1);
                        htmltext = "pet_manager_martin_q0419_07.htm";
                        break;
                    case dwarf:
                        st.giveItems(animal_slayer_list5, 1);
                        htmltext = "pet_manager_martin_q0419_08.htm";
                        break;
                    case kamael:
                        st.giveItems(q_animal_slayer_list6, 1);
                        htmltext = "pet_manager_martin_q0419_08a.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_11"))
                htmltext = "pet_manager_martin_q0419_03.htm";
            else if (event.equalsIgnoreCase("reply_10")) {
                switch (st.getPlayer().getPlayerTemplateComponent().getPlayerRace()) {
                    case human:
                        if (st.ownItemCount(animal_slayer_list1) == 1 && st.ownItemCount(bloody_fang) >= 50) {
                            st.takeItems(animal_slayer_list1, -1);
                            st.takeItems(bloody_fang, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                    case elf:
                        if (st.ownItemCount(animal_slayer_list2) == 1 && st.ownItemCount(bloody_claw) >= 50) {
                            st.takeItems(animal_slayer_list2, -1);
                            st.takeItems(bloody_claw, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                    case darkelf:
                        if (st.ownItemCount(animal_slayer_list3) == 1 && st.ownItemCount(bloody_nail) >= 50) {
                            st.takeItems(animal_slayer_list3, -1);
                            st.takeItems(bloody_nail, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                    case orc:
                        if (st.ownItemCount(animal_slayer_list4) == 1 && st.ownItemCount(bloody_kasha_fang) >= 50) {
                            st.takeItems(animal_slayer_list4, -1);
                            st.takeItems(bloody_kasha_fang, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                    case dwarf:
                        if (st.ownItemCount(animal_slayer_list5) == 1 && st.ownItemCount(bloody_tarantula_nail) >= 50) {
                            st.takeItems(animal_slayer_list5, -1);
                            st.takeItems(bloody_tarantula_nail, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                    case kamael:
                        if (st.ownItemCount(q_animal_slayer_list6) == 1 && st.ownItemCount(q_bloody_carmine_nail) >= 50) {
                            st.takeItems(q_animal_slayer_list6, -1);
                            st.takeItems(q_bloody_carmine_nail, -1);
                            st.giveItems(animal_lovers_list, 1);
                        }
                        break;
                }
                st.setMemoState("get_a_pet", String.valueOf(0), true);
                htmltext = "pet_manager_martin_q0419_12.htm";
            }
            if (event.equalsIgnoreCase("reply_1") && (GetMemoState & 15) == 10 && st.ownItemCount(animal_lovers_list) == 1) {
                st.takeItems(animal_lovers_list, -1);
                st.giveItems(wolf_collar, 1);
                st.soundEffect(SOUND_FINISH);
                htmltext = "pet_manager_martin_q0419_15.htm";
                st.exitQuest(true);
            } else if (event.equalsIgnoreCase("reply_1")) {
                int i8 = GetMemoState;
                i8 = i8 + 1;
                int i9 = 0;
                int i0 = 0;
                int i1 = 0;
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                while (i9 == 0) {
                    int i6 = Rnd.get(14) + 4;
                    int i7 = 1;
                    int i5;
                    for (i5 = 1; i5 <= i6; i5 = i5 + 1) {
                        i7 = i7 * 2;
                    }
                    if ((i7 & GetMemoState) == 0 && i6 < 18) {
                        if (i6 == 4) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_20.htm", st.getPlayer());
                            i0 = 1110000;
                        } else if (i6 == 5) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_21.htm", st.getPlayer());
                            i0 = 1110005;
                        } else if (i6 == 6) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_22.htm", st.getPlayer());
                            i0 = 1110010;
                        } else if (i6 == 7) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_23.htm", st.getPlayer());
                            i0 = 1110015;
                        } else if (i6 == 8) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_24.htm", st.getPlayer());
                            i0 = 1110020;
                        } else if (i6 == 9) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_25.htm", st.getPlayer());
                            i0 = 1110025;
                        } else if (i6 == 10) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_26.htm", st.getPlayer());
                            i0 = 1110030;
                        } else if (i6 == 11) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_27.htm", st.getPlayer());
                            i0 = 1110035;
                        } else if (i6 == 12) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_28.htm", st.getPlayer());
                            i0 = 1110040;
                        } else if (i6 == 13) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_29.htm", st.getPlayer());
                            i0 = 1110045;
                        } else if (i6 == 14) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_30.htm", st.getPlayer());
                            i0 = 1110050;
                        } else if (i6 == 15) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_31.htm", st.getPlayer());
                            i0 = 1110055;
                        } else if (i6 == 16) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_32.htm", st.getPlayer());
                            i0 = 1110060;
                        } else if (i6 == 17) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_419_GetaPet/pet_manager_martin_q0419_33.htm", st.getPlayer());
                            i0 = 1110065;
                        }
                        i9 = 1;
                        i8 = i8 | i7;
                    }
                }
                st.setMemoState("get_a_pet", String.valueOf(i8), true);
                i9 = 1;
                i8 = 0;
                while (i9 < 5) {
                    int i6 = Rnd.get(4) + 1;
                    int i7 = 1;
                    int i5;
                    for (i5 = 1; i5 <= i6; i5 = i5 + 1) {
                        i7 = i7 * 2;
                    }
                    if ((i7 & i8) == 0 && i6 < 5) {
                        if (i9 == 1) {
                            i1 = i6;
                        } else if (i9 == 2) {
                            i2 = i6;
                        } else if (i9 == 3) {
                            i3 = i6;
                        } else if (i9 == 4) {
                            i4 = i6;
                        }
                        i9 = i9 + 1;
                        i8 = i8 | i7;
                    }
                }
                htmltext = htmltext.replace("<?reply1?>", getGeneratedLink(i0 + i1, st.getPlayer()));
                htmltext = htmltext.replace("<?reply2?>", getGeneratedLink(i0 + i2, st.getPlayer()));
                htmltext = htmltext.replace("<?reply3?>", getGeneratedLink(i0 + i3, st.getPlayer()));
                htmltext = htmltext.replace("<?reply4?>", getGeneratedLink(i0 + i4, st.getPlayer()));
                htmltext = htmltext.replace("<?reply5?>", getGeneratedLink(i0 + 5, st.getPlayer()));
            } else if (event.equalsIgnoreCase("reply_0")) {
                st.setMemoState("get_a_pet", String.valueOf(0), true);
                htmltext = "pet_manager_martin_q0419_14.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("get_a_pet");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pet_manager_martin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_martin_q0419_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "pet_manager_martin_q0419_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_martin) {
                    if (st.ownItemCount(q_animal_slayer_list6) == 1 && st.ownItemCount(q_bloody_carmine_nail) < 50) {
                        if (st.ownItemCount(q_bloody_carmine_nail) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(q_animal_slayer_list6) == 1 && st.ownItemCount(q_bloody_carmine_nail) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_slayer_list1) == 1 && st.ownItemCount(bloody_fang) < 50) {
                        if (st.ownItemCount(bloody_fang) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(animal_slayer_list1) == 1 && st.ownItemCount(bloody_fang) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_slayer_list2) == 1 && st.ownItemCount(bloody_claw) < 50) {
                        if (st.ownItemCount(bloody_claw) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(animal_slayer_list2) == 1 && st.ownItemCount(bloody_claw) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_slayer_list3) == 1 && st.ownItemCount(bloody_nail) < 50) {
                        if (st.ownItemCount(bloody_nail) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(animal_slayer_list3) == 1 && st.ownItemCount(bloody_nail) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_slayer_list4) == 1 && st.ownItemCount(bloody_kasha_fang) < 50) {
                        if (st.ownItemCount(bloody_kasha_fang) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(animal_slayer_list4) == 1 && st.ownItemCount(bloody_kasha_fang) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_slayer_list5) == 1 && st.ownItemCount(bloody_tarantula_nail) < 50) {
                        if (st.ownItemCount(bloody_tarantula_nail) == 0)
                            htmltext = "pet_manager_martin_q0419_09.htm";
                        else
                            htmltext = "pet_manager_martin_q0419_10.htm";
                    } else if (st.ownItemCount(animal_slayer_list5) == 1 && st.ownItemCount(bloody_tarantula_nail) >= 50)
                        htmltext = "pet_manager_martin_q0419_11.htm";
                    else if (st.ownItemCount(animal_lovers_list) == 1 && (GetMemoState & 14) != 14 && (GetMemoState & 1879048192) != 1879048192)
                        htmltext = "pet_manager_martin_q0419_16.htm";
                    else if (st.ownItemCount(animal_lovers_list) == 1 && ((GetMemoState & 14) == 14 || (GetMemoState & 1879048192) == 1879048192)) {
                        st.setMemoState("get_a_pet", String.valueOf(1879048192), true);
                        htmltext = "pet_manager_martin_q0419_13.htm";
                    }
                } else if (npcId == gatekeeper_belladonna) {
                    if (st.ownItemCount(animal_lovers_list) == 1) {
                        st.setMemoState("get_a_pet", String.valueOf(GetMemoState | 2), true);
                        htmltext = "gatekeeper_belladonna_q0419_01.htm";
                    }
                } else if (npcId == metty) {
                    if (st.ownItemCount(animal_lovers_list) == 1) {
                        st.setMemoState("get_a_pet", String.valueOf(GetMemoState | 4), true);
                        htmltext = "metty_q0419_01.htm";
                    }
                } else if (npcId == elliany) {
                    if (st.ownItemCount(animal_lovers_list) == 1) {
                        st.setMemoState("get_a_pet", String.valueOf(GetMemoState | 8), true);
                        htmltext = "elliany_q0419_01.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    private String getGeneratedLink(final int fStringId, final Player player) {
        String value = "";
        switch (fStringId) {
            case 1110001:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Can be used for item transportation.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Используется для транспортировки предметов.</a><br>";
                break;
            case 1110002:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Can help during hunting by assisting in attacks. </a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Помогает во время охоты, увеличивая уровень атаки.</a><br>";
                break;
            case 1110003:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Can be sent to the village to buy items.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Может быть послан в деревню, чтобы купить предметы.</a><br>";
                break;
            case 1110004:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Can be traded or sold to a new owner for adena.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Питомца можно выгодно продать.</a><br>";
                break;
            case 1110005:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110006:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">When taking down a monster, always have a pet's company.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Во время охоты на монстров обязательно берите с собой питомца.</a><br>";
                break;
            case 1110007:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Tell your pet to pick up items.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Прикажите питомцу собирать предметы.</a><br>";
                break;
            case 1110008:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Tell your pet to attack monsters first.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Питомец должен вступить в бой после хозяина.</a><br>";
                break;
            case 1110009:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Let your pet do what it wants.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Предоставьте питомцу свободу действий.</a><br>";
                break;
            case 1110010:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110011:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">10 hours</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">10 ч</a><br>";
                break;
            case 1110012:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">15 hours</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">15 ч</a><br>";
                break;
            case 1110013:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">24 hours</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">24 ч</a><br>";
                break;
            case 1110014:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">25 hours</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">25 ч</a><br>";
                break;
            case 1110015:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110016:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Dire Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Жуткий Волк</a><br>";
                break;
            case 1110017:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Air Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Небесный Волк</a><br>";
                break;
            case 1110018:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Turek Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волк Турек</a><br>";
                break;
            case 1110019:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Kasha Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волк Кхаши</a><br>";
                break;
            case 1110020:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110021:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">It's tail is always pointing straight down.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Его хвост всегда направлен вниз.</a><br>";
                break;
            case 1110022:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">It's tail is always curled up.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Его хвост всегда направлен вверх.</a><br>";
                break;
            case 1110023:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">It's tail is always wagging back and forth.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Всегда виляет хвостом.</a><br>";
                break;
            case 1110024:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">What are you talking about?! A wolf doesn't have a tail.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">О чем Вы говорите! У волка нет хвоста.</a><br>";
                break;
            case 1110025:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110026:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Raccoon</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Енот</a><br>";
                break;
            case 1110027:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Jackal</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Шакал</a><br>";
                break;
            case 1110028:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Fox</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Лиса</a><br>";
                break;
            case 1110029:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Shepherd Dog</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Пастушья собака</a><br>";
                break;
            case 1110030:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Нет ответа</a><br>";
                break;
            case 1110031:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">1.4 km</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">1,4 км</a><br>";
                break;
            case 1110032:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">2.4 km</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">2,4 км</a><br>";
                break;
            case 1110033:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">3.4 km</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">3,4 км</a><br>";
                break;
            case 1110034:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">4.4 km</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">4,4 км</a><br>";
                break;
            case 1110035:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110036:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Male</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Самец</a><br>";
                break;
            case 1110037:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Female</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Самка</a><br>";
                break;
            case 1110038:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">A baby that was born last year</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Детеныш, родившийся в прошлом году.</a><br>";
                break;
            case 1110039:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">A baby that was born two years ago</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Детеныш, родившийся 2 года назад.</a><br>";
                break;
            case 1110040:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110041:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Goat</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Козел</a><br>";
                break;
            case 1110042:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Meat of a dead animal</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Падаль</a><br>";
                break;
            case 1110043:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Berries</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Ягоды</a><br>";
                break;
            case 1110044:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Wild Bird</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Дичь</a><br>";
                break;
            case 1110045:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Нет ответа</a><br>";
                break;
            case 1110046:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Breeding season is January-February.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Период размножения январь — февраль. </a><br>";
                break;
            case 1110047:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Pregnancy is nine months.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Период беременности 9 месяцев. </a><br>";
                break;
            case 1110048:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Babies are born in April-June.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Детеныши появляются в апреле — июне. </a><br>";
                break;
            case 1110049:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Has up to ten offspring at one time.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">На свет появляется до 10 детенышей за раз. </a><br>";
                break;
            case 1110050:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110051:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">3-6 years</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">3-6 лет</a><br>";
                break;
            case 1110052:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">6-9 years</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">6-9 лет</a><br>";
                break;
            case 1110053:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">9-12 years</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">9-12 лет</a><br>";
                break;
            case 1110054:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">12-15 years</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">12-15 лет</a><br>";
                break;
            case 1110055:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110056:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Wolves gather and move in groups of 7-13 animals.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волки сбиваются в стаи по 7-13 особей.</a><br>";
                break;
            case 1110057:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Wolves can eat a whole calf in one sitting.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волк может съесть теленка за один присест.</a><br>";
                break;
            case 1110058:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">If they have water, wolves can live for 5-6 days without eating anything.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волк может выдержать 5-6 дней без пищи при наличии воды.</a><br>";
                break;
            case 1110059:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">A pregnant wolf makes its home in a wide open place to have its babies.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Беременная волчица создает для своих детенышей дом на широком открытом месте.</a><br>";
                break;
            case 1110060:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110061:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">A grown wolf is still not as heavy as a fully-grown male adult human.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Даже взрослый волк весит меньше взрослого мужчины. </a><br>";
                break;
            case 1110062:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">A wolf changes into a werewolf during a full-moon.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Во время полнолуния волк превращается в оборотня.</a><br>";
                break;
            case 1110063:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">The color of a wolf's fur is the same as the place where it lives.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Цвет шкуры волка вне зависимости от среды обитания остается одинаковым.</a><br>";
                break;
            case 1110064:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">A wolf enjoys eating Dwarves.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Волк любит полакомиться гномами.</a><br>";
                break;
            case 1110065:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            case 1110066:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Talking Island - Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Говорящий Остров - Волк</a><br>";
                break;
            case 1110067:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Dark Forest - Ashen Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Темный лес - Пепельный Волк</a><br>";
                break;
            case 1110068:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Elven Forest - Gray Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Эльфийский лес - Серый Волк</a><br>";
                break;
            case 1110069:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Orc - Black Wolf</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_1\">Плато Бессмертия - Черный Волк</a><br>";
                break;
            case 1110070:
                if (!player.isLangRus())
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">None of the above.</a><br>";
                else
                    value = "<a action=\"bypass -h npc_%objectId%_QuestEvent _419_GetaPet reply_0\">Нет ответа</a><br>";
                break;
            default:
                value = "not find link, please send this message to administration!";
                break;
        }
        return value;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == lesser_dark_horror) {
            if (st.ownItemCount(animal_slayer_list3) == 1) {
                if (st.ownItemCount(bloody_nail) < 50 && Rnd.get(100) < 60) {
                    st.giveItems(bloody_nail, 1);
                    if (st.ownItemCount(bloody_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == stopper) {
            if (st.ownItemCount(animal_slayer_list3) == 1) {
                if (st.ownItemCount(bloody_nail) < 50 && Rnd.get(100) < 100) {
                    st.giveItems(bloody_nail, 1);
                    if (st.ownItemCount(bloody_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == giant_spider) {
            if (st.ownItemCount(animal_slayer_list1) == 1) {
                if (st.ownItemCount(bloody_fang) < 50 && Rnd.get(100) < 60) {
                    st.giveItems(bloody_fang, 1);
                    if (st.ownItemCount(bloody_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == dark_horror) {
            if (st.ownItemCount(animal_slayer_list3) == 1) {
                if (st.ownItemCount(bloody_nail) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(bloody_nail, 1);
                    if (st.ownItemCount(bloody_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == poker) {
            if (st.ownItemCount(animal_slayer_list1) == 1) {
                if (st.ownItemCount(bloody_fang) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(bloody_fang, 1);
                    if (st.ownItemCount(bloody_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == blader) {
            if (st.ownItemCount(animal_slayer_list1) == 1) {
                if (st.ownItemCount(bloody_fang) < 50 && Rnd.get(100) < 100) {
                    st.giveItems(bloody_fang, 1);
                    if (st.ownItemCount(bloody_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == hook_spider) {
            if (st.ownItemCount(animal_slayer_list2) == 1) {
                if (st.ownItemCount(bloody_claw) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(bloody_claw, 1);
                    if (st.ownItemCount(bloody_claw) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == hunter_tarantula) {
            if (st.ownItemCount(animal_slayer_list5) == 1) {
                if (st.ownItemCount(bloody_tarantula_nail) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(bloody_tarantula_nail, 1);
                    if (st.ownItemCount(bloody_tarantula_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == crimson_spider) {
            if (st.ownItemCount(animal_slayer_list2) == 1) {
                if (st.ownItemCount(bloody_claw) < 50 && Rnd.get(100) < 60) {
                    st.giveItems(bloody_claw, 1);
                    if (st.ownItemCount(bloody_claw) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == pincer_spider) {
            if (st.ownItemCount(animal_slayer_list2) == 1) {
                if (st.ownItemCount(bloody_claw) < 50 && Rnd.get(100) < 100) {
                    st.giveItems(bloody_claw, 1);
                    if (st.ownItemCount(bloody_claw) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == kasha_spider) {
            if (st.ownItemCount(animal_slayer_list4) == 1) {
                if (st.ownItemCount(bloody_kasha_fang) < 50 && Rnd.get(100) < 60) {
                    st.giveItems(bloody_kasha_fang, 1);
                    if (st.ownItemCount(bloody_kasha_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == kasha_poker_spider) {
            if (st.ownItemCount(animal_slayer_list4) == 1) {
                if (st.ownItemCount(bloody_kasha_fang) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(bloody_kasha_fang, 1);
                    if (st.ownItemCount(bloody_kasha_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == kasha_blade_spider) {
            if (st.ownItemCount(animal_slayer_list4) == 1) {
                if (st.ownItemCount(bloody_kasha_fang) < 50 && Rnd.get(100) < 100) {
                    st.giveItems(bloody_kasha_fang, 1);
                    if (st.ownItemCount(bloody_kasha_fang) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == plunder_tarantula) {
            if (st.ownItemCount(animal_slayer_list5) == 1) {
                if (st.ownItemCount(bloody_tarantula_nail) < 50 && Rnd.get(100) < 100) {
                    st.giveItems(bloody_tarantula_nail, 1);
                    if (st.ownItemCount(bloody_tarantula_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == carmine_spider) {
            if (st.ownItemCount(q_animal_slayer_list6) == 1) {
                if (st.ownItemCount(q_bloody_carmine_nail) < 50 && Rnd.get(100) < 75) {
                    st.giveItems(q_bloody_carmine_nail, 1);
                    if (st.ownItemCount(q_bloody_carmine_nail) >= 50)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}