package org.mmocore.gameserver.object.components.items.listeners;

import org.mmocore.gameserver.listener.inventory.OnEquipListener;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

public final class AccessoryListener implements OnEquipListener {
    private static final AccessoryListener _instance = new AccessoryListener();

    public static AccessoryListener getInstance() {
        return _instance;
    }

    @Override
    public void onUnequip(final int slot, final ItemInstance item, final Playable actor) {
        if (!item.isEquipable()) {
            return;
        }
        final Player player = (Player) actor;
        if (item.getBodyPart() == ItemTemplate.SLOT_L_BRACELET && item.getTemplate().getAttachedSkills().length > 0) {
            final int agathionId = player.getAgathionId();
            if (agathionId > 0) {
                player.deleteAgathion();
            }
        }
        if (item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet()) {
            player.sendUserInfo(true);
        }
        // TODO [G1ta0] отладить отображение аксессуаров
        //player.sendPacket(new ItemList(player, false));
        else {
            player.broadcastCharInfo();
        }
    }

    @Override
    public void onEquip(final int slot, final ItemInstance item, final Playable actor) {
        if (!item.isEquipable()) {
            return;
        }
        final Player player = (Player) actor;
        if (item.getBodyPart() == ItemTemplate.SLOT_R_BRACELET) {
            final int count = player.getTalismanCount();
            ItemInstance deco;
            for (int i = Inventory.PAPERDOLL_DECO1 + count; i <= Inventory.PAPERDOLL_DECO6; i++) {
                deco = player.getInventory().getPaperdollItem(i);
                if (deco != null) {
                    player.getInventory().unEquipItem(deco);
                }
            }
        }
        if (item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet()) {
            player.sendUserInfo(true);
        }
        // TODO [G1ta0] отладить отображение аксессуаров
        //player.sendPacket(new ItemList(player, false));
        else {
            player.broadcastCharInfo();
        }
    }
}