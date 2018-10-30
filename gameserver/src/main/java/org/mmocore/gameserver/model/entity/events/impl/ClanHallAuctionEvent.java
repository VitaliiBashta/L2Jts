package org.mmocore.gameserver.model.entity.events.impl;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.manager.PlayerMessageStack;
import org.mmocore.gameserver.model.entity.events.actions.StartStopAction;
import org.mmocore.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author VISTALL
 * @date 15:24/14.02.2011
 */
public class ClanHallAuctionEvent extends SiegeEvent<ClanHall, AuctionSiegeClanObject> {
    private ZonedDateTime endSiegeDate = ZonedDateTime.now();

    public ClanHallAuctionEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void reCalcNextTime(final boolean onStart) {
        clearActions();
        onTimeActions.clear();

        final Clan owner = getResidence().getOwner();

        endSiegeDate = Residence.MIN_SIEGE_DATE;

        // первый старт
        if (getResidence().getAuctionLength() == 0 && owner == null) {
            ZonedDateTime siegeDate = ZonedDateTime.now();
            siegeDate = siegeDate.with(DayOfWeek.MONDAY).withHour(15).withMinute(0).withSecond(0).withNano(0);
            getResidence().setSiegeDate(siegeDate);

            getResidence().setAuctionLength(7);
            getResidence().setAuctionMinBid(getResidence().getBaseMinBid());
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();

            onTimeActions.clear();
            addOnTimeAction(0, new StartStopAction(EVENT, true));
            addOnTimeAction(getResidence().getAuctionLength() * 86400, new StartStopAction(EVENT, false));

            endSiegeDate = getResidence().getSiegeDate().plusDays(getResidence().getAuctionLength());

            registerActions();
        } else if (getResidence().getAuctionLength() == 0 && owner != null) {
            // КХ куплен
        } else {
            //final long endDate = getResidence().getSiegeDate().getTimeInMillis() + getResidence().getAuctionLength() * 86400000L;
            final ZonedDateTime endDate = getResidence().getSiegeDate().plusDays(getResidence().getAuctionLength());
            // дата окончания далека от текущей деты
            if (!endDate.isAfter(ZonedDateTime.now()) && !onStart) {
                getResidence().setSiegeDate(ZonedDateTime.now());
            }

            endSiegeDate = getResidence().getSiegeDate().plusDays(getResidence().getAuctionLength());

            onTimeActions.clear();
            addOnTimeAction(0, new StartStopAction(EVENT, true));
            addOnTimeAction(getResidence().getAuctionLength() * 86400, new StartStopAction(EVENT, false));

            registerActions();
        }
    }

    @Override
    public void stopEvent(final boolean step) {
        final List<AuctionSiegeClanObject> siegeClanObjects = removeObjects(ATTACKERS);
        // сортуруем с Макс к мин
        final AuctionSiegeClanObject[] clans = siegeClanObjects.toArray(new AuctionSiegeClanObject[siegeClanObjects.size()]);
        Arrays.sort(clans, SiegeClanObject.SiegeClanComparator.getInstance());

        final Clan oldOwner = getResidence().getOwner();
        final AuctionSiegeClanObject winnerSiegeClan = clans.length > 0 ? clans[0] : null;

        // если есть победитель(тоисть больше 1 клана)
        if (winnerSiegeClan != null) {
            // розсылаем мессагу, возращаем всем деньги
            final SystemMessage msg = new SystemMessage(SystemMsg.THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN).addString(winnerSiegeClan.getClan()
                    .getName());
            for (final AuctionSiegeClanObject siegeClan : siegeClanObjects) {
                final Player player = siegeClan.getClan().getLeader().getPlayer();
                if (player != null) {
                    player.sendPacket(msg);
                } else {
                    PlayerMessageStack.getInstance().mailto(siegeClan.getClan().getLeaderId(), msg);
                }

                if (siegeClan != winnerSiegeClan) {
                    final long returnBid = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);

                    siegeClan.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, returnBid);
                }
            }

            SiegeClanDAO.getInstance().delete(getResidence());

            // если был овнер, возращаем депозит
            if (oldOwner != null) {
                oldOwner.getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, getResidence().getDeposit() + winnerSiegeClan.getParam());
            }

            getResidence().setAuctionLength(0);
            getResidence().setAuctionMinBid(0);
            getResidence().setAuctionDescription(StringUtils.EMPTY);
            getResidence().setSiegeDate(Residence.MIN_SIEGE_DATE);
            getResidence().setLastSiegeDate(Residence.MIN_SIEGE_DATE);
            getResidence().setOwnDate(ZonedDateTime.now());
            getResidence().setJdbcState(JdbcEntityState.UPDATED);

            getResidence().changeOwner(winnerSiegeClan.getClan());
            getResidence().startCycleTask();
        } else {
            if (oldOwner != null) {
                final Player player = oldOwner.getLeader().getPlayer();
                if (player != null) {
                    player.sendPacket(SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
                } else {
                    PlayerMessageStack.getInstance()
                            .mailto(oldOwner.getLeaderId(), SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED.packet(null));
                }
            }
        }

        super.stopEvent(step);
    }

    @Override
    public void findEvent(final Player player) {
        //
    }

    @Override
    public AuctionSiegeClanObject newSiegeClan(final String type, final int clanId, final long param, final Instant date) {
        final Clan clan = ClanTable.getInstance().getClan(clanId);
        return clan == null ? null : new AuctionSiegeClanObject(type, clan, param, date);
    }

    public ZonedDateTime getEndSiegeDate() {
        return endSiegeDate;
    }
}
