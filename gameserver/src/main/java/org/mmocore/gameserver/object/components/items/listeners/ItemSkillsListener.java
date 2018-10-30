package org.mmocore.gameserver.object.components.items.listeners;

import org.mmocore.gameserver.listener.inventory.OnEquipListener;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillCoolTime;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;

public final class ItemSkillsListener implements OnEquipListener {
    private static final ItemSkillsListener _instance = new ItemSkillsListener();

    public static ItemSkillsListener getInstance() {
        return _instance;
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        Player player = (Player) actor;

        ItemTemplate it = item.getTemplate();

        SkillEntry[] itemSkills = it.getAttachedSkills();
        SkillEntry enchant4Skill = it.getEnchant4Skill();

        player.removeTriggers(it);

        if (itemSkills != null && itemSkills.length > 0) {
            for (SkillEntry itemSkill : itemSkills) {
                if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
                    int level = player.getSkillLevel(itemSkill.getId());
                    int newlevel = level - 1;
                    if (newlevel > 0) {
                        player.addSkill(SkillTable.getInstance().getSkillEntry(itemSkill.getId(), newlevel).copyTo(SkillEntryType.EQUIP), false);
                    } else {
                        player.removeSkillById(itemSkill.getId());
                    }
                } else {
                    player.removeSkill(itemSkill, false);
                }
            }
        }

        if (enchant4Skill != null) {
            player.removeSkill(enchant4Skill, false);
        }

        if (itemSkills != null && itemSkills.length > 0 || enchant4Skill != null) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
        }
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        Player player = (Player) actor;

        SkillEntry[] itemSkills = null;
        SkillEntry enchant4Skill = null;

        ItemTemplate it = item.getTemplate();

        itemSkills = it.getAttachedSkills();

        if (item.getEnchantLevel() >= 4) {
            enchant4Skill = it.getEnchant4Skill();
        }

        // Для оружия при несоотвествии грейда скилы не выдаем
        if (it.getType2() == ItemTemplate.TYPE2_WEAPON && player.getWeaponsExpertisePenalty() > 0) {
            return;
        }

        player.addTriggers(it);

        boolean needSendInfo = false;
        if (itemSkills.length > 0) {
            for (SkillEntry itemSkill : itemSkills) {
                if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
                    int level = player.getSkillLevel(itemSkill.getId());
                    int newlevel = level;
                    if (level > 0) {
                        if (SkillTable.getInstance().getSkillEntry(itemSkill.getId(), level + 1) != null) {
                            newlevel = level + 1;
                        }
                    } else {
                        newlevel = 1;
                    }
                    if (newlevel != level) {
                        player.addSkill(SkillTable.getInstance().getSkillEntry(itemSkill.getId(), newlevel).copyTo(SkillEntryType.EQUIP), false);
                    }
                } else if (player.getSkillLevel(itemSkill.getId()) < itemSkill.getLevel()) {
                    player.addSkill(itemSkill.copyTo(SkillEntryType.EQUIP), false);

                    if (itemSkill.getTemplate().isActive()) {
                        long reuseDelay = it.getEquipReuseDelay();
                        if (reuseDelay < 0) {
                            reuseDelay = Math.min(Formulas.calcSkillReuseDelay(player, itemSkill), 30000);
                        }

                        if (reuseDelay > 0 && !player.isSkillDisabled(itemSkill)) {
                            player.disableSkill(itemSkill, reuseDelay);
                            needSendInfo = true;
                        }
                    }
                }
            }
        }

        if (enchant4Skill != null) {
            player.addSkill(enchant4Skill.copyTo(SkillEntryType.EQUIP), false);
        }

        if (itemSkills.length > 0 || enchant4Skill != null) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
            if (needSendInfo) {
                player.sendPacket(new SkillCoolTime(player));
            }
        }
    }
}