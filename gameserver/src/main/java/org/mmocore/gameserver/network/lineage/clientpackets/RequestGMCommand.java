package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.world.World;

public class RequestGMCommand extends L2GameClientPacket {
    private String _targetName;
    private int _command;

    @Override
    protected void readImpl() {
        _targetName = readS();
        _command = readD();
        // readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        final Player target = World.getPlayer(_targetName);
        if (player == null || target == null) {
            return;
        }
        if (!player.getPlayerAccess().CanViewChar) {
            return;
        }

        switch (_command) {
            case 1:
                player.sendPacket(new GMViewCharacterInfo(target));
                player.sendPacket(new GMHennaInfo(target));
                break;
            case 2:
                if (target.getClan() != null) {
                    for (final SubUnit subUnit : target.getClan().getAllSubUnits()) {
                        player.sendPacket(new GMViewPledgeInfo(target.getName(), target.getClan(), subUnit));
                    }
                }
                break;
            case 3:
                player.sendPacket(new GMViewSkillInfo(target));
                break;
            case 4:
                player.sendPacket(new GMViewQuestInfo(target));
                break;
            case 5:
                final ItemInstance[] items = target.getInventory().getItems();
                int questSize = 0;
                for (final ItemInstance item : items) {
                    if (item.getTemplate().isQuest()) {
                        questSize++;
                    }
                }
                player.sendPacket(new GMViewItemList(target, items, items.length - questSize));
                player.sendPacket(new ExGMViewQuestItemList(target, items, questSize));

                player.sendPacket(new GMHennaInfo(target));
                break;
            case 6:
                player.sendPacket(new GMViewWarehouseWithdrawList(target));
                break;
        }
    }
}