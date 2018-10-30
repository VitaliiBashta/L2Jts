package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.EnchantScroll;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollInfo;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollManager;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollType;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractEnchantPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll.EnchantResult;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public class RequestEnchantItem extends AbstractEnchantPacket {
    private int objectId, catalystObjId;

    @Override
    protected void readImpl() {
        objectId = readD();
        catalystObjId = readD();
    }

    @Override
    public void runImpl() {
        Player player = getClient().getActiveChar();

        if (player == null)
            return;

        if (!isValidPlayer(player)) {
            player.setEnchantScroll(null);
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.sendActionFailed();
            return;
        }

        PcInventory inventory = player.getInventory();
        inventory.writeLock();
        try {
            ItemInstance item = inventory.getItemByObjectId(this.objectId);
            ItemInstance scroll = player.getEnchantScroll();
            ItemInstance catalyst = this.catalystObjId > 0 ? inventory.getItemByObjectId(this.catalystObjId) : null;

            if (!ItemFunctions.checkCatalyst(item, catalyst)) {
                catalyst = null;
            }

            if ((item == null) || (scroll == null)) {
                Log.audit("[EnchantItem]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " used packetHack or another programm, and send wrong packet!");
                player.sendActionFailed();
                return;
            }

            EnchantScrollInfo esi = EnchantScrollManager.getScrollInfo(scroll.getItemId());
            if (esi == null) {
                Log.audit("[EnchantItem]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " used packetHack or another programm, and send wrong packet!");
                player.sendActionFailed();
                return;
            }

            if (item.getEnchantLevel() >= esi.getMax() || item.getEnchantLevel() < esi.getMin()) {
                player.sendPacket(EnchantResult.CANCEL);
                player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.sendActionFailed();
                return;
            }

            if (!checkItem(item, esi)) {
                player.sendPacket(EnchantResult.CANCEL);
                player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                player.sendActionFailed();
                return;
            }

            if (!checkReuseTime(player)) {
                player.sendMessage(player.isLangRus() ? "Слишком быстро!" : "Too fast!");
                player.sendActionFailed();
                return;
            }

            if ((!inventory.destroyItem(scroll, 1L)) || ((catalyst != null) && (!inventory.destroyItem(catalyst, 1L)))) {
                Log.audit("[EnchantItem]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " used packetHack or another programm, and send wrong packet!");
                player.sendPacket(EnchantResult.CANCEL);
                player.sendActionFailed();
                return;
            }
            boolean equipped = false;
            if ((equipped = item.isEquipped())) {
                inventory.isRefresh = true;
                inventory.unEquipItem(item);
            }

            int chance = 0;
            if (esi.getType() == EnchantScrollType.DIVINE || esi.getType() == EnchantScrollType.CRYSTALL)
                chance = 100;
            else
                chance = getChance(item, esi);

            if (catalyst != null)
                chance += chance * ItemFunctions.getCatalystPower(catalyst.getItemId());

            if (esi.getType() == EnchantScrollType.ANCIENT || esi.getType() == EnchantScrollType.ITEM_MALL)
                chance += chance * 0.10D;

            double premiumBonus = player.getPremiumAccountComponent().getPremiumBonus().getEnchantChance();
            if (premiumBonus > 0)
                chance *= premiumBonus;
            chance = Math.min(chance, 100);
            if (Rnd.chance(chance)) {
                if (AllSettingsConfig.oneCLickItemEnchant)
                    item.setEnchantLevel(esi.getMax());
                else
                    item.setEnchantLevel(item.getEnchantLevel() + 1);
                item.setJdbcState(JdbcEntityState.UPDATED);
                item.update();

                if (equipped) {
                    inventory.equipItem(item);
                    inventory.isRefresh = false;
                }

                player.sendPacket(new InventoryUpdate().addModifiedItem(item));

                player.sendPacket(EnchantResult.SUCESS);

                if (OtherConfig.SHOW_ENCHANT_EFFECT_RESULT) {
                    player.broadcastPacket(new L2GameServerPacket[]{
                            new SystemMessage(SystemMsg.C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3).addName(player).addNumber(item.getEnchantLevel()).addItemName(item.getItemId())
                    });
                    player.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(player, player, 5965, 1, 500, 1500L)});
                }
            } else {
                switch (esi.getType()) {
                    case NORMAL:
                        if (item.isEquipped()) {
                            player.sendDisarmMessage(item);
                        }
                        if (!inventory.destroyItem(item, 1L)) {
                            player.sendActionFailed();
                            return;
                        }
                        int crystalId = item.getCrystalType().cry;
                        if ((crystalId > 0) && (item.getTemplate().getCrystalCount() > 0)) {
                            int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87D);
                            if (item.getEnchantLevel() > 3)
                                crystalAmount = (int) (crystalAmount + item.getTemplate().getCrystalCount() * 0.25D * (item.getEnchantLevel() - 3));
                            if (crystalAmount < 1) {
                                crystalAmount = 1;
                            }
                            player.sendPacket(new EnchantResult(1, crystalId, crystalAmount));
                            ItemFunctions.addItem(player, crystalId, crystalAmount, true);
                        } else {
                            player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);
                        }
                        break;
                    case OLF_BLESSED:
                    case BLESSED:
                    case ITEM_MALL:
                    case CRYSTALL:
                        if (esi.getType() == EnchantScrollType.OLF_BLESSED)
                            item.setEnchantLevel(ServicesConfig.olfBlessedFailEnchantLvl);
                        else
                            item.setEnchantLevel(ServicesConfig.blessedFailEnchantLvl);
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();

                        if (equipped) {
                            inventory.equipItem(item);
                            inventory.isRefresh = false;
                        }

                        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
                        player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
                        player.sendPacket(EnchantResult.BLESSED_FAILED);
                        break;
                    case DESTRUCTION:
                        if (LostDreamCustom.customDestructionEnchant) {
                            if (item.getEnchantLevel() > ServicesConfig.SAFE_ENCHANT_COMMON)
                                item.setEnchantLevel(item.getEnchantLevel() - 1);
                        }
                    case ANCIENT:
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();
                        if (equipped) {
                            inventory.equipItem(item);
                            inventory.isRefresh = false;
                        }
                        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
                        player.sendPacket(EnchantResult.ANCIENT_FAILED);
                        break;
                    default:
                        break;
                }
            }

        } finally {
            inventory.writeUnlock();

            player.setEnchantScroll(null);
            player.updateStats();
        }
    }
}