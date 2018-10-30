package org.mmocore.gameserver.object.components.items.listeners;

import org.mmocore.gameserver.data.xml.holder.ArmorSetsHolder;
import org.mmocore.gameserver.listener.inventory.OnEquipListener;
import org.mmocore.gameserver.model.ArmorSet;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;

import java.util.ArrayList;
import java.util.List;


public final class ArmorSetListener implements OnEquipListener {
    private static final ArmorSetListener _instance = new ArmorSetListener();

    public static ArmorSetListener getInstance() {
        return _instance;
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }

        Player player = (Player) actor;

        // checks if player worns chest item
        ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if (chestItem == null) {
            return;
        }

        // checks if there is armorset for chest item that player worns
        ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
        if (armorSet == null) {
            return;
        }

        boolean update = false;
        // checks if equipped item is part of set
        if (armorSet.containItem(slot, item.getItemId())) {
            if (armorSet.containAll(player)) {
                List<SkillEntry> skills = armorSet.getSkills();
                for (SkillEntry skill : skills) {
                    player.addSkill(skill.copyTo(SkillEntryType.EQUIP), false);
                    update = true;
                }

                if (armorSet.containShield(player)) // has shield from set
                {
                    skills = armorSet.getShieldSkills();
                    for (SkillEntry skill : skills) {
                        player.addSkill(skill.copyTo(SkillEntryType.EQUIP), false);
                        update = true;
                    }
                }
                if (armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
                {
                    skills = armorSet.getEnchant6skills();
                    for (SkillEntry skill : skills) {
                        player.addSkill(skill.copyTo(SkillEntryType.EQUIP), false);
                        update = true;
                    }
                }
            }
        } else if (armorSet.containShield(item.getItemId())) {
            if (armorSet.containAll(player)) {
                List<SkillEntry> skills = armorSet.getShieldSkills();
                for (SkillEntry skill : skills) {
                    player.addSkill(skill.copyTo(SkillEntryType.EQUIP), false);
                    update = true;
                }
            }
        }

        if (update) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
        }
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }

        Player player = (Player) actor;

        boolean remove = false;
        List<SkillEntry> removeSkillId1 = new ArrayList<>(1); // set skill
        List<SkillEntry> removeSkillId2 = new ArrayList<>(1); // shield skill
        List<SkillEntry> removeSkillId3 = new ArrayList<>(1); // enchant +6 skill

        if (slot == Inventory.PAPERDOLL_CHEST) {
            ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(item.getItemId());
            if (armorSet == null) {
                return;
            }

            remove = true;
            removeSkillId1 = armorSet.getSkills();
            removeSkillId2 = armorSet.getShieldSkills();
            removeSkillId3 = armorSet.getEnchant6skills();

        } else {
            ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            if (chestItem == null) {
                return;
            }

            ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
            if (armorSet == null) {
                return;
            }

            if (armorSet.containItem(slot, item.getItemId())) // removed part of set
            {
                remove = true;
                removeSkillId1 = armorSet.getSkills();
                removeSkillId2 = armorSet.getShieldSkills();
                removeSkillId3 = armorSet.getEnchant6skills();
            } else if (armorSet.containShield(item.getItemId())) // removed shield
            {
                remove = true;
                removeSkillId2 = armorSet.getShieldSkills();
            }
        }

        boolean update = false;
        if (remove) {
            for (SkillEntry skill : removeSkillId1) {
                player.removeSkill(skill.getId(), false);
                update = true;
            }
            for (SkillEntry skill : removeSkillId2) {
                player.removeSkill(skill.getId(), false);
                update = true;
            }
            for (SkillEntry skill : removeSkillId3) {
                player.removeSkill(skill.getId(), false);
                update = true;
            }
        }

        if (update) {
            if (!player.getInventory().isRefresh) {
                // При снятии вещей из состава S80 или S84 сета снимаем плащ
                if (!player.getOpenCloak() && player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_BACK, null) != null) {
                    player.sendPacket(SystemMsg.YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE);
                }
            }

            player.sendPacket(new SkillList(player));
            player.updateStats();
        }
    }
}