package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.AdminFunctions;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Util;

public class AdminNochannel implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanBanChat) {
            return false;
        }

        int banChatCount = 0;
        int penaltyCount = 0;
        final int banChatCountPerDay = activeChar.getPlayerAccess().BanChatCountPerDay;
        if (banChatCountPerDay > 0) {
            final String count = activeChar.getPlayerVariables().get(PlayerVariables.BAN_CHAT_COUNT);
            if (count != null) {
                banChatCount = Integer.parseInt(count);
            }

            final String penalty = activeChar.getPlayerVariables().get(PlayerVariables.PENALTY_CHAT_COUNT);
            if (penalty != null) {
                penaltyCount = Integer.parseInt(penalty);
            }

            long LastBanChatDayTime = 0;
            final String time = activeChar.getPlayerVariables().get(PlayerVariables.LAST_BAN_CHAT_DAY_TIME);
            if (time != null) {
                LastBanChatDayTime = Long.parseLong(time);
            }

            if (LastBanChatDayTime != 0) {
                if (System.currentTimeMillis() - LastBanChatDayTime < 1000 * 60 * 60 * 24) {
                    if (banChatCount >= banChatCountPerDay) {
                        activeChar.sendAdminMessage("В сутки, вы можете выдать не более " + banChatCount + " банов чата.");
                        return false;
                    }
                } else {
                    int bonus_mod = banChatCount / 10;
                    bonus_mod = Math.max(1, bonus_mod);
                    bonus_mod = 1; // Убрать, если потребуется сделать зависимость бонуса от количества банов
                    if (activeChar.getPlayerAccess().BanChatBonusId > 0 && activeChar.getPlayerAccess().BanChatBonusCount > 0) {
                        int add_count = activeChar.getPlayerAccess().BanChatBonusCount * bonus_mod;

                        final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(activeChar.getPlayerAccess().BanChatBonusId);
                        activeChar.sendAdminMessage("Бонус за модерирование: " + add_count + ' ' + item.getName());

                        if (penaltyCount > 0) // У модератора был штраф за нарушения
                        {
                            activeChar.sendAdminMessage("Штраф за нарушения: " + penaltyCount + ' ' + item.getName());
                            activeChar.getPlayerVariables().set(PlayerVariables.PENALTY_CHAT_COUNT, String.valueOf(Math.max(0, penaltyCount - add_count)), -1); // Уменьшаем штраф
                            add_count -= penaltyCount; // Вычитаем штраф из бонуса
                        }

                        if (add_count > 0) {
                            ItemFunctions.addItem(activeChar, activeChar.getPlayerAccess().BanChatBonusId, add_count, true);
                        }
                    }
                    activeChar.getPlayerVariables().set(PlayerVariables.LAST_BAN_CHAT_DAY_TIME, String.valueOf(System.currentTimeMillis()), -1);
                    activeChar.getPlayerVariables().set(PlayerVariables.BAN_CHAT_COUNT, "0", -1);
                    banChatCount = 0;
                }
            } else {
                activeChar.getPlayerVariables().set(PlayerVariables.LAST_BAN_CHAT_DAY_TIME, String.valueOf(System.currentTimeMillis()), -1);
            }
        }

        switch (command) {
            case admin_nochannel:
            case admin_nc: {
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //nochannel charName [period] [reason]");
                    return false;
                }
                int timeval = 30; // if no args, then 30 min default.
                if (wordList.length > 2) {
                    try {
                        timeval = Integer.parseInt(wordList[2]);
                    } catch (Exception E) {
                        timeval = 30;
                    }
                }

                final String msg = AdminFunctions.banChat(activeChar, null, wordList[1], timeval, wordList.length > 3 ? Util.joinStrings(" ", wordList, 3) : null);
                activeChar.sendAdminMessage(msg);

                if (banChatCountPerDay > -1 && msg.startsWith("Вы забанили чат")) {
                    banChatCount++;
                    activeChar.getPlayerVariables().set(PlayerVariables.BAN_CHAT_COUNT, String.valueOf(banChatCount), -1);
                    activeChar.sendAdminMessage("У вас осталось " + (banChatCountPerDay - banChatCount) + " банов чата.");
                }
            }
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_nochannel,
        admin_nc
    }
}