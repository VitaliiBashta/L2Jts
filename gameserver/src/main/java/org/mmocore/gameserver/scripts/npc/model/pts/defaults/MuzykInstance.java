package org.mmocore.gameserver.scripts.npc.model.pts.defaults;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class MuzykInstance extends NpcInstance {
    private static final int q_musicnote_love = 4408;
    private static final int q_musicnote_battle = 4409;
    private static final int q_musicnote_journey = 4410;
    private static final int q_musicnote_celebration = 4418;
    private static final int q_musicnote_comedy = 4419;
    private static final int q_musicnote_solitude = 4420;
    private static final int q_musicnote_feast = 4421;
    private static final int echo_crystal_journey = 4411;
    private static final int echo_crystal_battle = 4412;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_comedy = 4417;

    public MuzykInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 31042:
                showChatWindow(player, "pts/default/muzyk001.htm");
                break;
            case 31043:
                showChatWindow(player, "pts/default/muzyko001.htm");
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=362&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            }

            switch (getNpcId()) {
                case 31042:
                    if (command.endsWith("reply=1")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_journey) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_03.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_journey) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_02.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_journey) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_journey, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_01.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=2")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_battle) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_06.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_battle) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_05.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_battle) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_battle, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_04.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=3")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_love) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_09.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_love) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_08.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_love) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_love, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_07.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=4")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_solitude) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_12.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_solitude) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_11.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_solitude) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_solitude, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_10.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=5")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_feast) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_15.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_feast) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_14.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_feast) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_feast, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_13.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=6")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_comedy) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_03.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_comedy) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_02.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_comedy) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_comedy, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_16.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=7")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_celebration) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_03.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_celebration) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_02.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_celebration) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_celebration, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyk_q0362_17.htm");
                            break;
                        }
                    }
                    break;
                case 31043:
                    if (command.endsWith("reply=1")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_journey) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_03.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_journey) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_02.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_journey) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_journey, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_01.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=2")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_battle) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_06.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_battle) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_05.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_battle) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_battle, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_04.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=3")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_love) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_09.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_love) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_08.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_love) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_love, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_07.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=4")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_solitude) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_12.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_solitude) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_11.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_solitude) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_solitude, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_10.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=5")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_feast) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_15.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_feast) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_14.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_feast) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_feast, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_13.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=6")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_comedy) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_06.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_comedy) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_05.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_comedy) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_comedy, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_16.htm");
                            break;
                        }
                    } else if (command.endsWith("reply=7")) {
                        if (ItemFunctions.getItemCount(player, q_musicnote_celebration) < 1) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_06.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_celebration) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) < 200) {
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_05.htm");
                            break;
                        } else if (ItemFunctions.getItemCount(player, q_musicnote_celebration) > 0 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 200) {
                            ItemFunctions.addItem(player, echo_crystal_celebration, 1);
                            ItemFunctions.removeItem(player, ItemTemplate.ITEM_ID_ADENA, 200);
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_362_BardsMandolin/muzyko_q0362_17.htm");
                            break;
                        }
                    }
                    break;
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}