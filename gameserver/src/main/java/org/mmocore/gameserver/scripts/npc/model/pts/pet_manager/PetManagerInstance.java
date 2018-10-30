package org.mmocore.gameserver.scripts.npc.model.pts.pet_manager;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 02/02/2015
 * @npc 30731, 30827, 30828, 30829, 30830, 30831, 30869, 30869, 31067, 31265, 31309, 31954
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public final class PetManagerInstance extends MerchantInstance {
    private static final long serialVersionUID = 8197000210779620149L;
    // dialogs
    private static final String fnEvolutionSuccess = "pts/pet_manager/pet_evolution_success.htm";
    private static final String fnEvolutionStopped = "pts/pet_manager/pet_evolution_stopped.htm";
    private static final String fnEvolveMany_pet = "pts/pet_manager/pet_evolution_many_pet.htm";
    private static final String fnEvolveNoPet_pet = "pts/pet_manager/pet_evolution_no_pet.htm";
    private static final String fnNoPet_pet = "pts/pet_manager/pet_evolution_farpet.htm";
    // private final String fnTooFar_pet = "pts/pet_manager/pet_evolution_farpet.htm";
    private static final String fnNoProperPet_pet = "pts/pet_manager/pet_evolution_farpet.htm";
    private static final String fnNotEnoughLevel_pet = "pts/pet_manager/pet_evolution_level.htm";
    private static final String fnNotEnoughMinLv_pet = "pts/pet_manager/pet_evolution_level.htm";
    private static final String fnNoItem_pet = "pts/pet_manager/pet_evolution_no_pet.htm";
    // pet data
    private final static int item_baby_pet1 = 2375; // wolf_collar
    private final static int item_grown_pet1 = 9882; // high_wolf_collar
    private static final int class_id_baby_pet1 = 12077;
    private static final int min_lv_pet1 = 55;
    private static final int item_baby_pet2 = 6648; // baby_buffalo_panpipe
    private static final int item_grown_pet2 = 10311; // upgrade_buffalo_panpipe
    private static final int class_id_baby_pet2 = 12780;
    private static final int min_lv_pet2 = 55;
    private static final int item_baby_pet3 = 6650; // baby_kukaburo_ocarina
    private static final int item_grown_pet3 = 10313; // upgrade_kukaburo_ocarina
    private static final int class_id_baby_pet3 = 12781;
    private static final int min_lv_pet3 = 55;
    private static final int item_baby_pet4 = 6649; // baby_cougar_chime
    private static final int item_grown_pet4 = 10312; // upgrade_cougar_chime
    private static final int class_id_baby_pet4 = 12782;
    private static final int min_lv_pet4 = 55;
    private static final int item_baby_pet5 = 9882; // high_wolf_collar
    private static final int item_grown_pet5 = 10426; // high_wolf_collar_ride
    private static final int class_id_baby_pet5 = 16025;
    private static final int min_lv_pet5 = 70;
    // tickets
    private static final int ticket_kukaburo_ocarina = 7585;
    private static final int baby_kukaburo_ocarina = 6650;
    private static final int ticket_buffalo_panpipe = 7583;
    private static final int baby_buffalo_panpipe = 6648;
    private static final int ticket_cougar_chime = 7584;
    private static final int baby_cougar_chime = 6649;
    // multisell
    private static final int pet_trade = 212;
    private static final int pet_upgrade_trade = 221;

    public PetManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-1001&")) {
            if (command.endsWith("reply=0")) {
                if (player.getInventory().getCountOf(ticket_kukaburo_ocarina) > 0) {
                    ItemFunctions.removeItem(player, ticket_kukaburo_ocarina, 1);
                    final ItemInstance item = ItemFunctions.createItem(baby_kukaburo_ocarina);
                    item.setEnchantLevel(24);
                    player.getInventory().addItem(item);
                    player.sendPacket(SystemMessage.obtainItems(item));
                    player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.S2_OF_LEVEL_S1_IS_ACQUIRED, ItemNameLineHolder.getInstance().get(player.getLanguage(), baby_kukaburo_ocarina).getName(), String.valueOf(24)));
                    showChatWindow(player, "pts/pet_manager/pet_manager_trade_pet.htm");
                } else {
                    showChatWindow(player, "pts/pet_manager/pet_manager_no_ticket.htm");
                }
            } else if (command.endsWith("reply=1")) {
                if (player.getInventory().getCountOf(ticket_buffalo_panpipe) > 0) {
                    ItemFunctions.removeItem(player, ticket_buffalo_panpipe, 1);
                    final ItemInstance item = ItemFunctions.createItem(baby_buffalo_panpipe);
                    item.setEnchantLevel(25);
                    player.getInventory().addItem(item);
                    player.sendPacket(SystemMessage.obtainItems(item));
                    showChatWindow(player, "pts/pet_manager/pet_manager_trade_pet.htm");
                } else {
                    showChatWindow(player, "pts/pet_manager/pet_manager_no_ticket.htm");
                }
            } else if (command.endsWith("reply=2")) {
                if (player.getInventory().getCountOf(ticket_cougar_chime) > 0) {
                    ItemFunctions.removeItem(player, ticket_cougar_chime, 1);
                    final ItemInstance item = ItemFunctions.createItem(baby_cougar_chime);
                    item.setEnchantLevel(26);
                    player.getInventory().addItem(item);
                    player.sendPacket(SystemMessage.obtainItems(item));
                    showChatWindow(player, "pts/pt_manager/pet_manager_trade_pet.htm");
                } else {
                    showChatWindow(player, "pts/pet_manager/pet_manager_no_ticket.htm");
                }
            }
        } else if (command.startsWith("menu_select?ask=-1002&")) {
            if (command.endsWith("reply=1")) // Evolve a Wolf into a Great Wolf.
            {
                if (player.getInventory().getCountOf(item_baby_pet1) >= 2) {
                    showChatWindow(player, fnEvolveMany_pet);
                    return;
                }
                if (player.getInventory().getCountOf(item_baby_pet1) <= 0 && player.getInventory().getCountOf(item_grown_pet1) > 0) {
                    showChatWindow(player, fnEvolveNoPet_pet);
                    return;
                }
                if (player.getServitor() == null || player.getServitor().isDead()) {
                    showChatWindow(player, fnNoPet_pet);
                    return;
                }
                if (player.getServitor().getNpcId() != class_id_baby_pet1) {
                    showChatWindow(player, fnNoProperPet_pet);
                    return;
                }
                if (player.getServitor().getLevel() < min_lv_pet1) {
                    showChatWindow(player, fnNotEnoughMinLv_pet);
                    return;
                }
                if (player.getInventory().getItemByItemId(item_baby_pet1) != null) {
                    if (player.getInventory().getItemByItemId(item_baby_pet1).getEnchantLevel() < min_lv_pet1) {
                        showChatWindow(player, fnNotEnoughLevel_pet);
                        return;
                    } else {
                        if (player.getServitor() == null) {
                            showChatWindow(player, fnEvolutionStopped);
                            return;
                        }
                        int controlItemId = player.getServitor().getControlItemObjId();
                        player.getServitor().unSummon(false, false);
                        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
                        control.setItemId(item_grown_pet1);
                        control.setEnchantLevel(min_lv_pet1);
                        control.setJdbcState(JdbcEntityState.UPDATED);
                        control.update();
                        player.sendItemList(false);
                    }
                    showChatWindow(player, fnEvolutionSuccess);
                } else {
                    showChatWindow(player, fnNoItem_pet);
                }
            } else if (command.endsWith("reply=5")) // Evolve a Great Wolf into a Fenrir.
            {
                if (player.getInventory().getCountOf(item_baby_pet5) >= 2) {
                    showChatWindow(player, fnEvolveMany_pet);
                    return;
                }
                if (player.getInventory().getCountOf(item_baby_pet5) <= 0 && player.getInventory().getCountOf(item_grown_pet5) > 0) {
                    showChatWindow(player, fnEvolveNoPet_pet);
                    return;
                }
                if (player.getServitor() == null || player.getServitor().isDead()) {
                    showChatWindow(player, fnNoPet_pet);
                    return;
                } else if (player.getServitor().getNpcId() != class_id_baby_pet5) {
                    showChatWindow(player, fnNoProperPet_pet);
                    return;
                } else if (player.getServitor().getLevel() < min_lv_pet5) {
                    showChatWindow(player, fnNotEnoughMinLv_pet);
                    return;
                }
                if (player.getInventory().getItemByItemId(item_baby_pet5) != null) {
                    if (player.getInventory().getItemByItemId(item_baby_pet5).getEnchantLevel() < min_lv_pet5) {
                        showChatWindow(player, fnNotEnoughLevel_pet);
                        return;
                    } else {
                        if (player.getServitor() == null) {
                            showChatWindow(player, fnEvolutionStopped);
                            return;
                        }
                        int controlItemId = player.getServitor().getControlItemObjId();
                        player.getServitor().unSummon(false, false);
                        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
                        control.setItemId(item_grown_pet5);
                        control.setEnchantLevel(min_lv_pet5);
                        control.setJdbcState(JdbcEntityState.UPDATED);
                        control.update();
                        player.sendItemList(false);
                    }
                    showChatWindow(player, fnEvolutionSuccess);
                } else {
                    showChatWindow(player, fnNoItem_pet);
                }
            } else if (command.endsWith("reply=2")) // Evolve a Baby Buffalo into an Improved Baby Buffalo (for Warriors)
            {
                if (player.getInventory().getCountOf(item_baby_pet2) >= 2) {
                    showChatWindow(player, fnEvolveMany_pet);
                    return;
                }
                if (player.getInventory().getCountOf(item_baby_pet2) <= 0 && player.getInventory().getCountOf(item_grown_pet2) > 0) {
                    showChatWindow(player, fnEvolveNoPet_pet);
                    return;
                }
                if (player.getServitor() == null || player.getServitor().isDead()) {
                    showChatWindow(player, fnNoPet_pet);
                    return;
                } else if (player.getServitor().getNpcId() != class_id_baby_pet2) {
                    showChatWindow(player, fnNoProperPet_pet);
                    return;
                } else if (player.getServitor().getLevel() < min_lv_pet2) {
                    showChatWindow(player, fnNotEnoughMinLv_pet);
                    return;
                }
                if (player.getInventory().getItemByItemId(item_baby_pet2) != null) {
                    if (player.getInventory().getItemByItemId(item_baby_pet2).getEnchantLevel() < min_lv_pet2) {
                        showChatWindow(player, fnNotEnoughLevel_pet);
                        return;
                    } else {
                        if (player.getServitor() == null) {
                            showChatWindow(player, fnEvolutionStopped);
                            return;
                        }
                        int controlItemId = player.getServitor().getControlItemObjId();
                        player.getServitor().unSummon(false, false);
                        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
                        control.setItemId(item_grown_pet2);
                        control.setEnchantLevel(min_lv_pet2);
                        control.setJdbcState(JdbcEntityState.UPDATED);
                        control.update();
                        player.sendItemList(false);
                    }
                    showChatWindow(player, fnEvolutionSuccess);
                } else {
                    showChatWindow(player, fnNoItem_pet);
                }
            } else if (command.endsWith("reply=3")) // Evolve a Baby Kookaburra into an Improved Baby Kookaburra (for Wizards)
            {
                if (player.getInventory().getCountOf(item_baby_pet3) >= 2) {
                    showChatWindow(player, fnEvolveMany_pet);
                    return;
                }
                if (player.getInventory().getCountOf(item_baby_pet3) <= 0 && player.getInventory().getCountOf(item_grown_pet3) > 0) {
                    showChatWindow(player, fnEvolveNoPet_pet);
                    return;
                }
                if (player.getServitor() == null || player.getServitor().isDead()) {
                    showChatWindow(player, fnNoPet_pet);
                    return;
                } else if (player.getServitor().getNpcId() != class_id_baby_pet3) {
                    showChatWindow(player, fnNoProperPet_pet);
                    return;
                } else if (player.getServitor().getLevel() < min_lv_pet3) {
                    showChatWindow(player, fnNotEnoughMinLv_pet);
                    return;
                }
                if (player.getInventory().getItemByItemId(item_baby_pet3) != null) {
                    if (player.getInventory().getItemByItemId(item_baby_pet3).getEnchantLevel() < min_lv_pet3) {
                        showChatWindow(player, fnNotEnoughLevel_pet);
                        return;
                    } else {
                        if (player.getServitor() == null) {
                            showChatWindow(player, fnEvolutionStopped);
                            return;
                        }
                        int controlItemId = player.getServitor().getControlItemObjId();
                        player.getServitor().unSummon(false, false);
                        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
                        control.setItemId(item_grown_pet3);
                        control.setEnchantLevel(min_lv_pet3);
                        control.setJdbcState(JdbcEntityState.UPDATED);
                        control.update();
                        player.sendItemList(false);
                    }
                    showChatWindow(player, fnEvolutionSuccess);
                } else {
                    showChatWindow(player, fnNoItem_pet);
                }
            } else if (command.endsWith("reply=4")) // Evolve a Baby Cougar into an Improved Baby Cougar (for Mixed Professions)
            {
                if (player.getInventory().getCountOf(item_baby_pet4) >= 2) {
                    showChatWindow(player, fnEvolveMany_pet);
                    return;
                }
                if (player.getInventory().getCountOf(item_baby_pet4) <= 0 && player.getInventory().getCountOf(item_grown_pet4) > 0) {
                    showChatWindow(player, fnEvolveNoPet_pet);
                    return;
                }
                if (player.getServitor() == null || player.getServitor().isDead()) {
                    showChatWindow(player, fnNoPet_pet);
                    return;
                } else if (player.getServitor().getNpcId() != class_id_baby_pet4) {
                    showChatWindow(player, fnNoProperPet_pet);
                    return;
                } else if (player.getServitor().getLevel() < min_lv_pet4) {
                    showChatWindow(player, fnNotEnoughMinLv_pet);
                    return;
                }
                if (player.getInventory().getItemByItemId(item_baby_pet4) != null) {
                    if (player.getInventory().getItemByItemId(item_baby_pet4).getEnchantLevel() < min_lv_pet4) {
                        showChatWindow(player, fnNotEnoughLevel_pet);
                        return;
                    } else {
                        if (player.getServitor() == null) {
                            showChatWindow(player, fnEvolutionStopped);
                            return;
                        }
                        int controlItemId = player.getServitor().getControlItemObjId();
                        player.getServitor().unSummon(false, false);
                        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
                        control.setItemId(item_grown_pet4);
                        control.setEnchantLevel(min_lv_pet4);
                        control.setJdbcState(JdbcEntityState.UPDATED);
                        control.update();
                        player.sendItemList(false);
                    }
                    showChatWindow(player, fnEvolutionSuccess);
                } else {
                    showChatWindow(player, fnNoItem_pet);
                }
            }
        } else if (command.startsWith("menu_select?ask=-506&")) {
            if (command.endsWith("reply=1")) {
                MultiSellHolder.getInstance().SeparateAndSend(pet_trade, player, getObjectId(), 0);
            }
        } else if (command.startsWith("menu_select?ask=-507&")) {
            if (command.endsWith("reply=221")) {
                MultiSellHolder.getInstance().SeparateAndSend(pet_upgrade_trade, player, getObjectId(), 0);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}