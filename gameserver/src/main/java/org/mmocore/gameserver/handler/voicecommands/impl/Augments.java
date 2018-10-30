package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;
import org.mmocore.gameserver.templates.OptionDataTemplate;

public class Augments implements IVoicedCommandHandler {
    private final String[] _commandList = {"augments"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        if (!player.isGM()) {
            player.sendMessage(new CustomMessage("common.command404"));
            return false;
        }
        int variation1Id, variation2Id, variationIdHigh, variationIdLow;
        StringBuilder info;
        for (int slot = 0; slot < Inventory.PAPERDOLL_MAX; slot++) {
            variation1Id = player.getInventory().getPaperdollVariation1Id(slot);
            variation2Id = player.getInventory().getPaperdollVariation2Id(slot);
            if (variation1Id == 0 || variation2Id == 0) {
                continue;
            }
            variationIdHigh = variation2Id;
            variationIdLow = variation1Id;
            info = new StringBuilder(30);
            info.append(slot);
            info.append(' ');
            info.append(variationIdHigh);
            info.append(':');
            info.append(variationIdLow);
            getInfo(info, variationIdHigh);
            getInfo(info, variationIdLow);
            player.sendMessage(info.toString());
        }
        return true;
    }

    private void getInfo(final StringBuilder info, final int id) {
        final OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(id);
        if (template != null) {
            if (!template.getSkills().isEmpty()) {
                for (final SkillEntry s : template.getSkills()) {
                    info.append(' ');
                    info.append(s.getId());
                    info.append('/');
                    info.append(s.getLevel());
                }
            }
            if (!template.getTriggerList().isEmpty()) {
                for (final TriggerInfo t : template.getTriggerList()) {
                    info.append(' ');
                    info.append(t.id);
                    info.append('/');
                    info.append(t.level);
                    info.append(' ');
                    info.append(t.getType());
                    info.append(':');
                    info.append(t.getChance());
                }
            }
        }
    }
}