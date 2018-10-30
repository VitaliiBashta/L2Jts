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
import org.mmocore.gameserver.templates.OptionDataTemplate;

public final class ItemAugmentationListener implements OnEquipListener {
    private static final ItemAugmentationListener _instance = new ItemAugmentationListener();

    public static ItemAugmentationListener getInstance() {
        return _instance;
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }
        if (!item.isAugmented()) {
            return;
        }

        Player player = actor.getPlayer();

        // При несоотвествии грейда аугмент не применяется
        if (player.getExpertisePenalty(item) > 0) {
            return;
        }

        int[] stats = new int[2];
        stats[0] = item.getVariation1Id();
        stats[1] = item.getVariation2Id();

        boolean sendList = false;
        boolean sendReuseList = false;
        for (int i : stats) {
            OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
            if (template == null) {
                continue;
            }

            player.addStatFuncs(template.getStatFuncs(template));

            for (SkillEntry skill : template.getSkills()) {
                sendList = true;
                player.addSkill(skill.copyTo(SkillEntryType.EQUIP));

                if (player.isSkillDisabled(skill)) {
                    sendReuseList = true;
                }
            }

            player.addTriggers(template);
        }

        if (sendList) {
            player.sendPacket(new SkillList(player));
        }

        if (sendReuseList) {
            player.sendPacket(new SkillCoolTime(player));
        }

        player.updateStats();
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable()) {
            return;
        }
        if (!item.isAugmented()) {
            return;
        }

        Player player = actor.getPlayer();

        int[] stats = new int[2];
        stats[0] = item.getVariation1Id();
        stats[1] = item.getVariation2Id();

        boolean sendList = false;
        for (int i : stats) {
            OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
            if (template == null) {
                continue;
            }

            player.removeStatsByOwner(template);

            for (SkillEntry skill : template.getSkills()) {
                sendList = true;
                player.removeSkill(skill.getId(), false);
            }

            player.removeTriggers(template);
        }

        if (sendList) {
            player.sendPacket(new SkillList(player));
        }

        player.updateStats();
    }
}