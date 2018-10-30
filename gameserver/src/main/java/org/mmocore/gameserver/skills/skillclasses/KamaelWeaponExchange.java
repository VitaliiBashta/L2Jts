package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAutoSoulShot;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ShortCutInit;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.WeaponTemplate;

import java.util.List;


public class KamaelWeaponExchange extends Skill {
    public KamaelWeaponExchange(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player p = (Player) activeChar;
        if (p.isInStoreMode() || p.isProcessingRequest()) {
            return false;
        }

        final ItemInstance item = activeChar.getActiveWeaponInstance();
        if (item != null && (((WeaponTemplate) item.getTemplate()).getKamaelConvert() == 0
                && !(LostDreamCustom.allowTransformPvp && item.getTemplate().isPvp()))) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_CONVERT_THIS_ITEM);
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final Player player = (Player) activeChar;
        final ItemInstance item = activeChar.getActiveWeaponInstance();
        if (item == null) {
            return;
        }

        final int itemId = ((WeaponTemplate) item.getTemplate()).getKamaelConvert();

        if (itemId == 0) {
            return;
        }

        player.getInventory().unEquipItem(item);
        player.sendPacket(new InventoryUpdate().addRemovedItem(item));
        item.setItemId(itemId);

        player.sendPacket(new ShortCutInit(player));
        for (final int shotId : player.getAutoSoulShot()) {
            player.sendPacket(new ExAutoSoulShot(shotId, true));
        }

        player.sendPacket(new InventoryUpdate().addNewItem(item));
        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemNameWithAugmentation(item));
        player.getInventory().equipItem(item);
    }
}