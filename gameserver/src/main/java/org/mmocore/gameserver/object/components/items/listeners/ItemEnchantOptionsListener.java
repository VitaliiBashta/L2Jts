package org.mmocore.gameserver.object.components.items.listeners;

import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.listener.inventory.OnEquipListener;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillCoolTime;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;
import org.mmocore.gameserver.templates.OptionDataTemplate;

/**
 * @author VISTALL
 * @date 19:34/19.05.2011
 */
public final class ItemEnchantOptionsListener implements OnEquipListener {
    private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();

    public static ItemEnchantOptionsListener getInstance() {
        return _instance;
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }
        Player player = actor.getPlayer();

        boolean sendList = false, sendReuseList = false;
        for (int i : item.getEnchantOptions()) {
            OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
            if (template == null) {
                continue;
            }

            player.addStatFuncs(template.getStatFuncs(template));
            for (SkillEntry skill : template.getSkills()) {
                sendList = true;
                player.addSkill(skill.copyTo(SkillEntryType.EQUIP), false);

                if (player.isSkillDisabled(skill)) {
                    sendReuseList = true;
                }
            }

            for (TriggerInfo triggerInfo : template.getTriggerList()) {
                player.addTrigger(triggerInfo);
            }
        }

        if (sendList) {
            player.sendPacket(new SkillList(player));
        }
        if (sendReuseList) {
            player.sendPacket(new SkillCoolTime(player));
        }
        player.sendChanges();
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }

        Player player = actor.getPlayer();

        boolean needSendInfo = false;
        for (int i : item.getEnchantOptions()) {
            OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
            if (template == null) {
                continue;
            }

            player.removeStatsByOwner(template);
            for (SkillEntry skill : template.getSkills()) {
                player.removeSkill(skill, false);
                needSendInfo = true;
            }
            for (TriggerInfo triggerInfo : template.getTriggerList()) {
                player.removeTrigger(triggerInfo);
            }
        }

        if (needSendInfo) {
            player.sendPacket(new SkillList(player));
        }
        player.sendChanges();
    }
}
