package org.mmocore.gameserver.manager.games;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.database.dao.impl.LotteryDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.ChronoField;
import java.util.List;

/**
 * @author Keiichi
 * @author Java-man
 */
public class LotteryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryManager.class);

    private static final long MILLISECONDS_IN_MINUTE = 60 * 1000;

    protected int number;
    protected int prize;
    protected boolean isSellingTickets;
    protected boolean isStarted;
    protected Instant endTimestamp;

    private LotteryManager() {
        number = 1;
        prize = ServicesConfig.SERVICES_LOTTERY_PRIZE;
        isSellingTickets = false;
        isStarted = false;
        endTimestamp = Instant.now();

        if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
            new startLottery().run();
        }
    }

    public static LotteryManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void increasePrize(final int count) {
        prize += count;
        LotteryDAO.getInstance().updatePrize(getId(), getPrize());
    }

    private boolean restoreLotteryData() {
        final StatsSet lotteryData = LotteryDAO.getInstance().restoreLotteryData();

        if (lotteryData.isEmpty())
            return true;

        number = lotteryData.getInteger("idnr");

        if (lotteryData.getInteger("finished") == 1) {
            number++;
            prize = lotteryData.getInteger("newprize");
        } else {
            prize = lotteryData.getInteger("prize");
            endTimestamp = Instant.ofEpochSecond(lotteryData.getLong("enddate"));

            final Instant now = Instant.now();

            if (!endTimestamp.isAfter(now.plusMillis(2 * MILLISECONDS_IN_MINUTE))) {
                new finishLottery().run();
                return false;
            }

            if (endTimestamp.isAfter(now)) {
                isStarted = true;
                Duration duration = Duration.between(now, endTimestamp);
                ThreadPoolManager.getInstance().schedule(new finishLottery(), duration.toMillis());

                if (endTimestamp.isAfter(now.plusMillis(12 * MILLISECONDS_IN_MINUTE))) {
                    isSellingTickets = true;
                    duration = Duration.between(now.plusMillis(10 * MILLISECONDS_IN_MINUTE), endTimestamp);
                    ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), duration.toMillis());
                }

                return false;
            }
        }

        lotteryData.clear();

        return true;
    }

    private void announceLottery() {
        if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
            LOGGER.info("Starting ticket sell for lottery #{}.", getId());
        }
        isSellingTickets = true;
        isStarted = true;

        AnnouncementUtils.announceToAll("Lottery tickets are now available for Lucky Lottery #" + getId() + '.');
    }

    private void scheduleEndOfLottery() {
        ZonedDateTime finishTime = ZonedDateTime.ofInstant(endTimestamp, ZoneId.systemDefault());
        finishTime = finishTime.withMinute(0).withSecond(0);

        if (finishTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            finishTime = finishTime.withHour(19);
            endTimestamp = finishTime.plusWeeks(1).toInstant();
        } else {
            finishTime = finishTime.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue()).withHour(19);
            endTimestamp = finishTime.toInstant();
        }

        final Instant now = Instant.now();
        Duration duration = Duration.between(now.plusMillis(10 * MILLISECONDS_IN_MINUTE), endTimestamp);
        ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), duration.toMillis());
        duration = Duration.between(now, endTimestamp);
        ThreadPoolManager.getInstance().schedule(new finishLottery(), duration.toMillis());
    }

    public int[] decodeNumbers(int enchant, int type2) {
        final int[] res = new int[5];
        int id = 0;
        int nr = 1;

        while (enchant > 0) {
            final int val = enchant / 2;
            if (val != (double) enchant / 2) {
                res[id++] = nr;
            }
            enchant /= 2;
            nr++;
        }

        nr = 17;

        while (type2 > 0) {
            final int val = type2 / 2;
            if (val != (double) type2 / 2) {
                res[id++] = nr;
            }
            type2 /= 2;
            nr++;
        }

        return res;
    }

    public int[] checkTicket(final int id, final int enchant, final int type2) {
        final int[] res = {0, 0};

        final StatsSet lotteryTicket = LotteryDAO.getInstance().selectLotteryTicket(id);

        if (lotteryTicket.isEmpty())
            return res;

        int curenchant = lotteryTicket.getInteger("number1") & enchant;
        int curtype2 = lotteryTicket.getInteger("number2") & type2;

        if (curenchant == 0 && curtype2 == 0) {
            lotteryTicket.clear();
            return res;
        }

        int count = 0;

        for (int i = 1; i <= 16; i++) {
            final int val = curenchant / 2;
            if (val != (double) curenchant / 2) {
                count++;
            }
            final int val2 = curtype2 / 2;
            if (val2 != (double) curtype2 / 2) {
                count++;
            }
            curenchant = val;
            curtype2 = val2;
        }

        switch (count) {
            case 0:
                break;
            case 5:
                res[0] = 1;
                res[1] = lotteryTicket.getInteger("prize1");
                break;
            case 4:
                res[0] = 2;
                res[1] = lotteryTicket.getInteger("prize2");
                break;
            case 3:
                res[0] = 3;
                res[1] = lotteryTicket.getInteger("prize3");
                break;
            default:
                res[0] = 4;
                res[1] = 200;
        }

        lotteryTicket.clear();

        return res;
    }

    public int[] checkTicket(final ItemInstance item) {
        return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
    }

    public boolean isSellableTickets() {
        return isSellingTickets;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public int getId() {
        return number;
    }

    public int getPrize() {
        return prize;
    }

    public Instant getEndDate() {
        return endTimestamp;
    }

    private static class LazyHolder {
        private static final LotteryManager INSTANCE = new LotteryManager();
    }

    private class startLottery extends RunnableImpl {
        protected startLottery() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (restoreLotteryData()) {
                announceLottery();
                scheduleEndOfLottery();
                LotteryDAO.getInstance().createNewLottery(getId(), endTimestamp.getEpochSecond(), getPrize());
            }
        }
    }

    private class stopSellingTickets extends RunnableImpl {
        protected stopSellingTickets() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
                LOGGER.info("Stopping ticket sell for lottery #{}.", getId());
            }
            isSellingTickets = false;

            AnnouncementUtils.announceToAll(SystemMsg.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED);
        }
    }

    private class finishLottery extends RunnableImpl {
        protected finishLottery() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
                LOGGER.info("Ending lottery #{}" + '.', getId());
            }

            final int[] luckynums = new int[5];
            int luckynum = 0;

            for (int i = 0; i < 5; i++) {
                boolean found = true;

                while (found) {
                    luckynum = Rnd.get(20) + 1;
                    found = false;

                    for (int j = 0; j < i; j++) {
                        if (luckynums[j] == luckynum) {
                            found = true;
                        }
                    }
                }

                luckynums[i] = luckynum;
            }

            if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
                LOGGER.info("The lucky numbers are {}, {}, {}, {}, {}" + '.', luckynums[0], luckynums[1], luckynums[2], luckynums[3], luckynums[4]);
            }

            int enchant = 0;
            int type2 = 0;

            for (int i = 0; i < 5; i++) {
                if (luckynums[i] < 17) {
                    enchant += Math.pow(2, luckynums[i] - 1);
                } else {
                    type2 += Math.pow(2, luckynums[i] - 17);
                }
            }

            if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
                LOGGER.info("Encoded lucky numbers are {}, {}", enchant, type2);
            }

            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            int count4 = 0;

            final List<ImmutablePair<Integer, Integer>> lotteryItems = LotteryDAO.getInstance().selectLotteryItems(getId());

            for (final ImmutablePair<Integer, Integer> lotteryItem : lotteryItems) {
                int curenchant = lotteryItem.getLeft() & enchant;
                int curtype2 = lotteryItem.getRight() & type2;

                if (curenchant == 0 && curtype2 == 0) {
                    continue;
                }

                int count = 0;

                for (int i = 1; i <= 16; i++) {
                    final int val = curenchant / 2;

                    if (val != (double) curenchant / 2) {
                        count++;
                    }

                    final int val2 = curtype2 / 2;

                    if (val2 != (double) curtype2 / 2) {
                        count++;
                    }

                    curenchant = val;
                    curtype2 = val2;
                }

                if (count == 5) {
                    count1++;
                } else if (count == 4) {
                    count2++;
                } else if (count == 3) {
                    count3++;
                } else if (count > 0) {
                    count4++;
                }
            }

            lotteryItems.clear();

            final int prize4 = count4 * ServicesConfig.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
            int prize1 = 0;
            int prize2 = 0;
            int prize3 = 0;
            final int newprize;

            if (count1 > 0) {
                prize1 = (int) ((getPrize() - prize4) * ServicesConfig.SERVICES_LOTTERY_5_NUMBER_RATE / count1);
            }

            if (count2 > 0) {
                prize2 = (int) ((getPrize() - prize4) * ServicesConfig.SERVICES_LOTTERY_4_NUMBER_RATE / count2);
            }

            if (count3 > 0) {
                prize3 = (int) ((getPrize() - prize4) * ServicesConfig.SERVICES_LOTTERY_3_NUMBER_RATE / count3);
            }

            //TODO: Уточнить что происходит с джекпотом на оффе. Если с проигрышем всех участников джекпот уменьшается то до каких приделов.
            if (prize1 == 0 && prize2 == 0 && prize3 == 0) {
                newprize = getPrize();
            } else {
                newprize = getPrize() + prize1 + prize2 + prize3;
            }

            if (ServicesConfig.SERVICES_ALLOW_LOTTERY) {
                LOGGER.info("Jackpot for next lottery is {}.", newprize);
            }

            if (count1 > 0) {
                // There are winners.
                AnnouncementUtils.announceToAll(new SystemMessage(SystemMsg.THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS).addNumber(getId()).addNumber(getPrize()).addNumber(count1));
            } else {
                // There are no winners.
                AnnouncementUtils.announceToAll(new SystemMessage(SystemMsg.THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING).addNumber(getId()).addNumber(getPrize()));
            }

            LotteryDAO.getInstance().updateLottery(getId(), getPrize(), newprize, prize1, prize2, prize3, enchant, type2);

            ThreadPoolManager.getInstance().schedule(new startLottery(), MILLISECONDS_IN_MINUTE);
            number++;

            isStarted = false;
        }
    }
}
