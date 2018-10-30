package org.mmocore.gameserver.scripts.npc.model.residences.clanhall;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.TimeUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * @author VISTALL
 * @date 17:40/14.06.2011
 */
public class AuctioneerInstance extends NpcInstance {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.KOREA);

    private static final int CH_PAGE_SIZE = 7;
    private static final String CH_IN_LIST =
            "\t<tr>\n" +
                    "\t\t<td width=50>\n" +
                    "\t\t\t<font color=\"aaaaff\">&^%id%;</font>\n" +
                    "\t\t</td>\n" + "\t\t<td width=100>\n" +
                    "\t\t\t<a action=\"bypass -h npc_%objectId%_info %id%\"><font color=\"ffffaa\">&%%id%;[%size%]</font></a>\n" +
                    "\t\t</td>\n" +
                    "\t\t<td width=50>%date%</td>\n" +
                    "\t\t<td width=70 align=right>\n" +
                    "\t\t\t<font color=\"aaffff\">%min_bid%</font>\n" +
                    "\t\t</td>\n" +
                    "\t</tr>";

    private static final int BIDDER_PAGE_SIZE = 10;
    private static final String BIDDER_IN_LIST =
            "\t<tr>\n" +
                    "\t\t<td width=100><font color=\"aaaaff\">&%%id%;</font></td>\n" +
                    "\t\t<td width=100><font color=\"ffffaa\">%clan_name%</font></td>\n" +
                    "\t\t<td width=70>%date%</td>\n" +
                    "\t</tr>";

    public AuctioneerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final StringTokenizer tokenizer = new StringTokenizer(command.replace("\r\n", "<br1>"));
        final String actualCommand = tokenizer.nextToken();
        if ("map".equalsIgnoreCase(actualCommand)) {
            showChatWindow(player, getMapDialog());
        }
        //=============================================================================================
        //						Выводит весь список активніх аукционов
        //=============================================================================================
        else if ("list_all".equalsIgnoreCase(actualCommand)) {
            final int page = Integer.parseInt(tokenizer.nextToken());

            final List<ClanHallAuctionEvent> events = new ArrayList<ClanHallAuctionEvent>();
            for (final ClanHall ch : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
                if (ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && ch.getSiegeEvent().isInProgress()) {
                    events.add(ch.<ClanHallAuctionEvent>getSiegeEvent());
                }
            }

            if (events.isEmpty()) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
                showChatWindow(player, 0);
                return;
            }

            int min = CH_PAGE_SIZE * page;
            int max = min + CH_PAGE_SIZE;
            if (min > events.size()) {
                min = 0;
                max = min + CH_PAGE_SIZE;
            }

            if (max > events.size()) {
                max = events.size();
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_list_clanhalls.htm");

            final StringBuilder b = new StringBuilder();
            for (int i = min; i < max; i++) {
                final ClanHallAuctionEvent event = events.get(i);
                final List<AuctionSiegeClanObject> attackers = event.getObjects(ClanHallAuctionEvent.ATTACKERS);
                final ZonedDateTime endDate = event.getEndSiegeDate();

                final String out = CH_IN_LIST.replace("%id%", String.valueOf(event.getId()))
                        .replace("%min_bid%", String.valueOf(event.getResidence().getAuctionMinBid()))
                        .replace("%size%", String.valueOf(attackers.size()))
                        .replace("%date%", TimeUtils.DATE_FORMATTER.format(endDate));

                b.append(out);
            }

            msg.replace("%list%", b.toString());
            if (events.size() > max) {
                msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
                msg.replace("%next_bypass%", "-h npc_%objectId%_list_all " + (page + 1));
            } else {
                msg.replace("%next_button%", StringUtils.EMPTY);
            }

            if (page != 0) {
                msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
                msg.replace("%prev_bypass%", "-h npc_%objectId%_list_all " + (page - 1));
            } else {
                msg.replace("%prev_button%", StringUtils.EMPTY);
            }

            player.sendPacket(msg);
        }
        //=============================================================================================
        //		Выводит стандартную инфу про КХ(выбор), если єто один из биддер - есть кнопка отменить
        //=============================================================================================
        else if ("info".equalsIgnoreCase(actualCommand)) {
            String fileName = null;

            ClanHall clanHall = null;
            SiegeClanObject siegeClan = null;
            if (tokenizer.hasMoreTokens()) {
                final int id = Integer.parseInt(tokenizer.nextToken());
                clanHall = ResidenceHolder.getInstance().getResidence(id);

                fileName = "residence2/clanhall/auction_clanhall_info_main.htm";
            } else {
                clanHall = player.getClan() == null ? null : player.getClan().getHasHideout() > 0 ? ResidenceHolder.getInstance().<ClanHall>getResidence(player.getClan()
                        .getHasHideout()) : null;
                if (clanHall != null && clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
                    if (clanHall.getSiegeEvent().isInProgress()) {
                        fileName = "residence2/clanhall/auction_clanhall_info_owner_sell.htm";
                    } else {
                        fileName = "residence2/clanhall/auction_clanhall_info_owner.htm";
                    }
                } else {
                    for (final ClanHall ch : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
                        if (ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && (siegeClan = ch.getSiegeEvent().getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player
                                .getClan())) != null) {
                            clanHall = ch;
                            break;
                        }
                    }

                    if (siegeClan == null) {
                        player.sendPacket(SystemMsg.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                        showChatWindow(player, 0);
                        return;
                    }

                    fileName = "residence2/clanhall/auction_clanhall_info_bidded.htm";
                }
            }

            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            final List<AuctionSiegeClanObject> attackers = auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile(fileName);
            msg.replace("%id%", String.valueOf(clanHall.getId()));
            msg.replace("%bigger_size%", String.valueOf(attackers.size()));
            msg.replace("%grade%", String.valueOf(clanHall.getGrade()));
            msg.replace("%rental_fee%", String.valueOf(clanHall.getRentalFee()));

            final Clan owner = clanHall.getOwner();

            msg.replace("%owner%", owner == null ? StringUtils.EMPTY : owner.getName());
            msg.replace("%owner_leader%", owner == null ? StringUtils.EMPTY : owner.getLeaderName());
            msg.replace("%description%", clanHall.getAuctionDescription());
            msg.replace("%min_bid%", String.valueOf(clanHall.getAuctionMinBid()));

            final ZonedDateTime endSiegeDate = auctionEvent.getEndSiegeDate();

            msg.replace("%date%", TimeUtils.DATE_FORMATTER.format(endSiegeDate));
            msg.replace("%hour%", String.valueOf(endSiegeDate.getHour()));

            final Duration remainingTime = Duration.between(ZonedDateTime.now(), endSiegeDate);

            msg.replace("%remaining_hour%", String.valueOf(remainingTime.toHours()));
            msg.replace("%remaining_minutes%", String.valueOf(remainingTime.toMinutes()));

            if (siegeClan != null) {
                msg.replace("%my_bid%", String.valueOf(siegeClan.getParam()));
            }

            player.sendPacket(msg);
        }
        //=============================================================================================
        //						Выводит список биддеров от аукционна
        //=============================================================================================
        else if ("bidder_list".equalsIgnoreCase(actualCommand)) {
            final int id = Integer.parseInt(tokenizer.nextToken());
            final int page = Integer.parseInt(tokenizer.nextToken());

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            final List<AuctionSiegeClanObject> attackers = auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

            if (!auctionEvent.isInProgress()) {
                return;
            }

            int min = BIDDER_PAGE_SIZE * page;
            int max = min + BIDDER_PAGE_SIZE;
            if (min > attackers.size()) {
                min = 0;
                max = min + BIDDER_PAGE_SIZE;
            }

            if (max > attackers.size()) {
                max = attackers.size();
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_bidder_list.htm");
            msg.replace("%id%", String.valueOf(id));

            final StringBuilder b = new StringBuilder();
            for (int i = min; i < max; i++) {
                final AuctionSiegeClanObject siegeClan = attackers.get(i);
                final String t = BIDDER_IN_LIST.replace("%id%", String.valueOf(id))
                        .replace("%clan_name%", siegeClan.getClan().getName())
                        .replace("%date%", TimeUtils.dateFormat(siegeClan.getDate()));
                b.append(t);
            }
            msg.replace("%list%", b.toString());

            if (attackers.size() > max) {
                msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
                msg.replace("%next_bypass%", "-h npc_%objectId%_bidder_list " + id + ' ' + (page + 1));
            } else {
                msg.replace("%next_button%", StringUtils.EMPTY);
            }

            if (page != 0) {
                msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
                msg.replace("%prev_bypass%", "-h npc_%objectId%_bidder_list " + id + ' ' + (page - 1));
            } else {
                msg.replace("%prev_button%", StringUtils.EMPTY);
            }

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Начало установки бидда, появляется окно для ввода, скок ставить
        //=============================================================================================
        else if ("bid_start".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final int id = Integer.parseInt(tokenizer.nextToken());

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress()) {
                return;
            }

            long minBid = clanHall.getAuctionMinBid();
            final AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null) {
                minBid = siegeClan.getParam();
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_bid_start.htm");
            msg.replace("%id%", String.valueOf(id));
            msg.replace("%min_bid%", String.valueOf(minBid));
            msg.replace("%clan_adena%", String.valueOf(player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA)));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Окно портведжения бида
        //=============================================================================================
        else if ("bid_next".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final int id = Integer.parseInt(tokenizer.nextToken());
            long bid = 0;
            if (tokenizer.hasMoreTokens()) {
                try {
                    bid = NUMBER_FORMAT.parse(tokenizer.nextToken()).longValue();
                } catch (final ParseException e) {
                    //
                }
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress()) {
                return;
            }

            if (!checkBid(player, auctionEvent, bid)) {
                return;
            }

            long minBid = clanHall.getAuctionMinBid();
            final AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null) {
                minBid = siegeClan.getParam();
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_bid_confirm.htm");
            msg.replace("%id%", String.valueOf(id));
            msg.replace("%bid%", String.valueOf(bid));
            msg.replace("%min_bid%", String.valueOf(minBid));

            final ZonedDateTime endSiegeDate = auctionEvent.getEndSiegeDate();

            msg.replace("%date%", TimeUtils.DATE_FORMATTER.format(endSiegeDate));
            msg.replace("%hour%", String.valueOf(endSiegeDate.getHour()));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Подтверджает бин, и появляется меню КХ
        //=============================================================================================
        else if ("bid_confirm".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final int id = Integer.parseInt(tokenizer.nextToken());
            final long bid = Long.parseLong(tokenizer.nextToken());


            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress()) {
                return;
            }

            for (final ClanHall ch : ResidenceHolder.getInstance().getResidenceList(ClanHall.class)) {
                if (clanHall != ch && ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && ch.getSiegeEvent().isInProgress() && ch.getSiegeEvent()
                        .getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player
                                .getClan()) != null) {
                    player.sendPacket(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
                    onBypassFeedback(player, "bid_start " + id);
                    return;
                }
            }

            if (!checkBid(player, auctionEvent, bid)) {
                return;
            }

            long consumeBid = bid;
            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null) {
                consumeBid -= siegeClan.getParam();
                if (bid <= siegeClan.getParam()) {
                    player.sendPacket(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID);
                    onBypassFeedback(player, "bid_start " + auctionEvent.getId());
                    return;
                }
            }

            player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, consumeBid);

            if (siegeClan != null) {
                siegeClan.setParam(bid);

                SiegeClanDAO.getInstance().update(clanHall, siegeClan);
            } else {
                siegeClan = new AuctionSiegeClanObject(ClanHallAuctionEvent.ATTACKERS, player.getClan(), bid);
                auctionEvent.addObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);

                SiegeClanDAO.getInstance().insert(clanHall, siegeClan);
            }

            player.sendPacket(SystemMsg.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);

            onBypassFeedback(player, "info");
        }
        //=============================================================================================
        //					Открывает окно для подтверджения отказа от ставки
        //=============================================================================================
        else if ("cancel_bid".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }
            final int id = Integer.parseInt(tokenizer.nextToken());

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress()) {
                return;
            }

            final AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan == null) {
                return;
            }

            final long returnVal = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);
            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_bid_cancel.htm");
            msg.replace("%id%", String.valueOf(id));
            msg.replace("%bid%", String.valueOf(siegeClan.getParam()));
            msg.replace("%return%", String.valueOf(returnVal));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Подтверджает отказ от ставки, возращается 90% сумы
        //=============================================================================================
        else if ("cancel_bid_confirm".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }
            final int id = Integer.parseInt(tokenizer.nextToken());

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(id);
            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress()) {
                return;
            }

            final AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan == null) {
                return;
            }

            final long returnVal = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);

            player.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, returnVal);
            auctionEvent.removeObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);
            SiegeClanDAO.getInstance().delete(clanHall, siegeClan);

            player.sendPacket(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID);
            showChatWindow(player, 0);
        }
        //=============================================================================================
        //					Показывает окно на подтверджения
        //=============================================================================================
        else if ("register_start".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                return;
            }

            if (clanHall.getLastSiegeDate().plusWeeks(1).isAfter(ZonedDateTime.now())) {
                player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
                onBypassFeedback(player, "info");
                return;
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_start.htm");
            msg.replace("%id%", String.valueOf(player.getClan().getHasHideout()));
            msg.replace("%adena%", String.valueOf(player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA)));
            msg.replace("%deposit%", String.valueOf(clanHall.getDeposit()));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Показывает окно на ввод, инфы про КХ, и подтверджает аукцион
        //=============================================================================================
        else if ("register_next".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            if (player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getDeposit()) {
                player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                onBypassFeedback(player, "register_start");
                return;
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_next.htm");
            msg.replace("%min_bid%", String.valueOf(clanHall.getBaseMinBid()));
            msg.replace("%last_bid%", String.valueOf(clanHall.getBaseMinBid()));  //TODO [VISTALL] get last bid

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Показывает окно на ввод, инфы про КХ, и подтверджает аукцион
        //=============================================================================================
        else if ("register_next2".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            final int day = Integer.parseInt(tokenizer.nextToken());
            long bid = -1;
            String comment = StringUtils.EMPTY;
            if (tokenizer.hasMoreTokens()) {
                try {
                    bid = Long.parseLong(tokenizer.nextToken());
                } catch (final Exception e) {
                }
            }

            if (tokenizer.hasMoreTokens()) {
                comment = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens()) {
                    comment += ' ' + tokenizer.nextToken();
                }
            }

            comment = comment.substring(0, Math.min(comment.length(), Byte.MAX_VALUE));
            if (bid <= -1) {
                onBypassFeedback(player, "register_next");
                return;
            }

            final ZonedDateTime dateTime = ZonedDateTime.now().plusHours(day);

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_confirm.htm");
            msg.replace("%description%", comment);
            msg.replace("%day%", String.valueOf(day));
            msg.replace("%bid%", String.valueOf(bid));
            msg.replace("%base_bid%", String.valueOf(clanHall.getBaseMinBid()));
            msg.replace("%hour%", String.valueOf(dateTime.getHour()));
            msg.replace("%date%", TimeUtils.dateFormat(dateTime));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Подтверждает продажу КХ
        //=============================================================================================
        else if ("register_confirm".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            if (clanHall.getLastSiegeDate().plusWeeks(1).isAfter(ZonedDateTime.now())) {
                player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
                onBypassFeedback(player, "info");
                return;
            }

            final int day = Integer.parseInt(tokenizer.nextToken());
            final long bid = Long.parseLong(tokenizer.nextToken());
            String comment = StringUtils.EMPTY;

            if (tokenizer.hasMoreTokens()) {
                comment = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens()) {
                    comment += ' ' + tokenizer.nextToken();
                }
            }

            if (bid <= -1) {
                onBypassFeedback(player, "register_next");
                return;
            }

            clanHall.setAuctionMinBid(bid);
            clanHall.setAuctionDescription(comment);
            clanHall.setAuctionLength(day);
            clanHall.setSiegeDate(ZonedDateTime.now());
            clanHall.setJdbcState(JdbcEntityState.UPDATED);
            clanHall.update();

            clanHall.getSiegeEvent().reCalcNextTime(false);

            onBypassFeedback(player, "info");
            player.sendPacket(SystemMsg.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
        } else if ("cancel_start".equals(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            final HtmlMessage msg = new HtmlMessage(this);
            msg.setFile("residence2/clanhall/auction_clanhall_cancel_confirm.htm");
            msg.replace("%deposit%", String.valueOf(clanHall.getDeposit()));

            player.sendPacket(msg);
        } else if ("cancel_confirm".equals(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            final ClanHall clanHall = ResidenceHolder.getInstance().getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            clanHall.getSiegeEvent().clearActions();
            clanHall.getSiegeEvent().removeState(SiegeEvent.PROGRESS_STATE);

            clanHall.setSiegeDate(Residence.MIN_SIEGE_DATE);
            clanHall.setLastSiegeDate(ZonedDateTime.now());
            clanHall.setAuctionDescription(StringUtils.EMPTY);
            clanHall.setAuctionLength(0);
            clanHall.setAuctionMinBid(0);
            clanHall.setJdbcState(JdbcEntityState.UPDATED);
            clanHall.update();

            final ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            final List<AuctionSiegeClanObject> siegeClans = auctionEvent.removeObjects(ClanHallAuctionEvent.ATTACKERS);
            SiegeClanDAO.getInstance().delete(clanHall);

            for (final AuctionSiegeClanObject $siegeClan : siegeClans) {
                final long returnBid = $siegeClan.getParam() - (long) ($siegeClan.getParam() * 0.1);

                $siegeClan.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, returnBid);
            }

            clanHall.getSiegeEvent().reCalcNextTime(false);
            onBypassFeedback(player, "info");
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        showChatWindow(player, "residence2/clanhall/auction_dealer001.htm");
    }

    private boolean firstChecks(final Player player) {
        if (player.getClan() == null || player.getClan().getLevel() < 2) {
            player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
            return false;
        }

        if (!player.hasPrivilege(Privilege.CH_AUCTION)) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }

        return true;
    }

    private boolean checkBid(final Player player, final ClanHallAuctionEvent auctionEvent, final long bid) {
        long consumeBid = bid;
        final AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
        if (siegeClan != null) {
            consumeBid -= siegeClan.getParam();
        }

        if (consumeBid > player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA)) {
            player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
            onBypassFeedback(player, "bid_start " + auctionEvent.getId());
            return false;
        }

        final long minBid = siegeClan == null ? auctionEvent.getResidence().getAuctionMinBid() : siegeClan.getParam();
        if (bid < minBid) {
            player.sendPacket(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            onBypassFeedback(player, "bid_start " + auctionEvent.getId());
            return false;
        }
        return true;
    }

    private String getMapDialog() {
        //"gludio", "gludin", "dion", "giran", "adena", "rune", "goddard", "schuttgart"
        return String.format("residence2/clanhall/map_agit_%s.htm", getParameters().getString("town", "gludin"));
    }
}
