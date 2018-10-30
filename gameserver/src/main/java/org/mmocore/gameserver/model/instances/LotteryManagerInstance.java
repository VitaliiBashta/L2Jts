package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.manager.games.LotteryManager;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;

public class LotteryManagerInstance extends NpcInstance {
    public LotteryManagerInstance(final int objectID, final NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("Loto")) {
            try {
                int val = Integer.parseInt(command.substring(5));
                showLotoWindow(player, val);
            } catch (NumberFormatException e) {
                Log.debug("L2LotteryManagerInstance: bypass: " + command + "; player: " + player, e);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        String pom;
        if (val == 0) {
            pom = "LotteryManager";
        } else {
            pom = "LotteryManager-" + val;
        }

        return "lottery/" + pom + ".htm";
    }

    public void showLotoWindow(final Player player, final int val) {
        int npcId = getTemplate().npcId;
        String filename;
        SystemMessage sm;
        HtmlMessage html = new HtmlMessage(this);

        // if loto
        if (val == 0) {
            filename = getHtmlPath(npcId, 1, player);
            html.setFile(filename);
        } else if (val >= 1 && val <= 21) {
            if (!LotteryManager.getInstance().isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(SystemMsg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.getInstance().isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(SystemMsg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            filename = getHtmlPath(npcId, 5, player);
            html.setFile(filename);

            int count = 0;
            int found = 0;

            // counting buttons and unsetting button if found
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == val) {
                    // unsetting button
                    player.setLoto(i, 0);
                    found = 1;
                } else if (player.getLoto(i) > 0) {
                    count++;
                }
            }

            // if not rearched limit 5 and not unseted value
            if (count < 5 && found == 0 && val <= 20) {
                for (int i = 0; i < 5; i++) {
                    if (player.getLoto(i) == 0) {
                        player.setLoto(i, val);
                        break;
                    }
                }
            }

            //setting pusshed buttons
            count = 0;
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) > 0) {
                    count++;
                    String button = String.valueOf(player.getLoto(i));
                    if (player.getLoto(i) < 10) {
                        button = '0' + button;
                    }
                    String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
                    String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + '"';
                    html.replace(search, replace);
                }
            }
            if (count == 5) {
                String search;
                String replace;
                if ("en".equalsIgnoreCase(player.getPlayerVariables().get(PlayerVariables.LANG))) {
                    search = "0\">Return";
                    replace = "22\">The winner selected the numbers above.";
                } else {
                    search = "0\">Назад";
                    replace = "22\">Выигрышные номера выбранные выше.";
                }
                html.replace(search, replace);
            }
            player.sendPacket(html);
        }

        if (val == 22) {
            if (!LotteryManager.getInstance().isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(SystemMsg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.getInstance().isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(SystemMsg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            int price = ServicesConfig.SERVICES_ALT_LOTTERY_PRICE;
            int lotonumber = LotteryManager.getInstance().getId();
            int enchant = 0;
            int type2 = 0;
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == 0) {
                    return;
                }
                if (player.getLoto(i) < 17) {
                    enchant += Math.pow(2, player.getLoto(i) - 1);
                } else {
                    type2 += Math.pow(2, player.getLoto(i) - 17);
                }
            }
            if (player.getAdena() < price) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(price, true);
            sm = new SystemMessage(SystemMsg.ACQUIRED_S1_S2);
            sm.addNumber(lotonumber);
            sm.addItemName(4442);
            player.sendPacket(sm);
            ItemInstance item = ItemFunctions.createItem(4442);
            item.setCustomType1(lotonumber);
            item.setEnchantLevel(enchant);
            item.setCustomType2(type2);
            player.getInventory().addItem(item);

            filename = getHtmlPath(npcId, 3, player);
            html.setFile(filename);
        } else if (val == 23) //23 - current lottery jackpot
        {
            filename = getHtmlPath(npcId, 3, player);
            html.setFile(filename);
        } else if (val == 24) {
            filename = getHtmlPath(npcId, 4, player);
            html.setFile(filename);

            int lotonumber = LotteryManager.getInstance().getId();
            StringBuilder message = new StringBuilder(8092);

            for (ItemInstance item : player.getInventory().getItems()) {
                if (item == null) {
                    continue;
                }
                if (item.getItemId() == 4442 && item.getCustomType1() < lotonumber) {
                    message.append("<a action=\"bypass -h npc_%objectId%_Loto ").append(item.getObjectId())
                            .append("\">").append(item.getCustomType1());
                    message.append(' ').append(HtmlUtils.htmlNpcString(NpcString.EVENT_NUMBER)).append(' ');
                    int[] numbers = LotteryManager.getInstance().decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
                    for (int i = 0; i < 5; i++) {
                        message.append(numbers[i]).append(' ');
                    }
                    int[] check = LotteryManager.getInstance().checkTicket(item);
                    if (check[0] > 0) {
                        message.append("- ");
                        switch (check[0]) {
                            case 1:
                                message.append(HtmlUtils.htmlNpcString(NpcString.FIRST_PRIZE));
                                break;
                            case 2:
                                message.append(HtmlUtils.htmlNpcString(NpcString.SECOND_PRIZE));
                                break;
                            case 3:
                                message.append(HtmlUtils.htmlNpcString(NpcString.THIRD_PRIZE));
                                break;
                            case 4:
                                message.append(HtmlUtils.htmlNpcString(NpcString.FOURTH_PRIZE));
                                break;
                        }
                        message.append(' ').append(check[1]).append("a.");
                    }
                    message.append("</a>");
                }
            }
            if (message.length() == 0) {
                message.append(HtmlUtils.htmlNpcString(NpcString.THERE_HAS_BEEN_NO_WINNING_LOTTERY_TICKET));
            }

            html.replace("%result%", message.toString());
        } else if (val == 25) {
            filename = getHtmlPath(npcId, 2, player);
            html.setFile(filename);
        } else if (val > 25) {
            int lotonumber = LotteryManager.getInstance().getId();
            ItemInstance item = player.getInventory().getItemByObjectId(val);
            if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber) {
                return;
            }
            int[] check = LotteryManager.getInstance().checkTicket(item);

            if (player.getInventory().destroyItem(item, 1L)) {
                player.sendPacket(SystemMessage.removeItems(4442, 1));
                int adena = check[1];
                if (adena > 0) {
                    player.addAdena(adena);
                }
            }

            return;
        }

        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%race%", String.valueOf(LotteryManager.getInstance().getId()));
        html.replace("%adena%", String.valueOf(LotteryManager.getInstance().getPrize()));
        html.replace("%ticket_price%", String.valueOf(ServicesConfig.SERVICES_LOTTERY_TICKET_PRICE));
        html.replace("%prize5%", String.valueOf(ServicesConfig.SERVICES_LOTTERY_5_NUMBER_RATE * 100));
        html.replace("%prize4%", String.valueOf(ServicesConfig.SERVICES_LOTTERY_4_NUMBER_RATE * 100));
        html.replace("%prize3%", String.valueOf(ServicesConfig.SERVICES_LOTTERY_3_NUMBER_RATE * 100));
        html.replace("%prize2%", String.valueOf(ServicesConfig.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE));
        final Instant instant = LotteryManager.getInstance().getEndDate();
        html.replace("%enddate%", TimeUtils.dateTimeFormat(instant));

        player.sendPacket(html);
        player.sendActionFailed();
    }
}
