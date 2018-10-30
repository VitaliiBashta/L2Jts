package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.entity.MonsterRace;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.mmocore.gameserver.network.lineage.components.SystemMsg.*;

public class RaceManagerInstance extends NpcInstance {
    public static final int LANES = 8;
    public static final int WINDOW_START = 0;
    protected static final int[][] codes = {{-1, 0}, {0, 15322}, {13765, -1}};
    protected static final int[] cost = {100, 500, 1000, 5000, 10000, 20000, 50000, 100000};
    //Time Constants
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    //States
    private static final int ACCEPTING_BETS = 0;
    private static final int WAITING = 1;
    private static final int STARTING_RACE = 2;
    private static final int RACE_END = 3;
    protected static MonRaceInfo packet;
    private static Set<RaceManagerInstance> managers;
    private static int _raceNumber = 1;
    private static int minutes = 5;
    private static int state = RACE_END;
    private static boolean notInitialized = true;

    public RaceManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        if (notInitialized) {
            notInitialized = false;

            _raceNumber = ServerVariables.getInt("monster_race", 1);
            List<Race> history = new ArrayList<>();
            managers = new CopyOnWriteArraySet<>();

            final ThreadPoolManager s = ThreadPoolManager.getInstance();
            s.scheduleAtFixedRate(new Announcement(TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1), 0, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1), 30 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1), MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1), MINUTE + 30 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES), 2 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES), 3 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES), 4 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES), 5 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1), 6 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1), 7 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTES), 7 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTES), 8 * MINUTE, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS), 8 * MINUTE + 30 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS), 8 * MINUTE + 50 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 55 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 56 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 57 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 58 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 59 * SECOND, 10 * MINUTE);
            s.scheduleAtFixedRate(new Announcement(THEYRE_OFF), 9 * MINUTE, 10 * MINUTE);
        }
        managers.add(this);
    }

    public void removeKnownPlayer(final Player player) {
        for (int i = 0; i < 8; i++) {
            player.sendPacket(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
        }
    }

    public void makeAnnouncement(final SystemMsg type) {
        final SystemMessage sm = new SystemMessage(type);
        switch (type) {
            case TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1:
            case NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1:
                if (state != ACCEPTING_BETS) {
                    state = ACCEPTING_BETS;
                    startRace();
                }
                sm.addNumber(_raceNumber);
                break;
            case TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES:
                sm.addNumber(minutes);
                minutes--;
                break;
            case MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS:
                sm.addNumber(_raceNumber);
                minutes--;
                break;
            case MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTES:
                sm.addNumber(minutes);
                sm.addNumber(_raceNumber);
                minutes--;
                break;
            case THE_RACE_WILL_BEGIN_IN_S1_SECONDS:
                sm.addNumber(minutes);
                minutes--;
                break;
            case TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1:
                sm.addNumber(_raceNumber);
                state = WAITING;
                minutes = 2;
                break;
            case MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS:
            case MONSTER_RACE_S1_IS_FINISHED:
                sm.addNumber(_raceNumber);
                minutes = 5;
                break;
            case FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_S2:
                state = RACE_END;
                sm.addNumber(MonsterRace.getInstance().getFirstPlace());
                sm.addNumber(MonsterRace.getInstance().getSecondPlace());
                break;
        }

        broadcast(sm);

        if (type == THEYRE_OFF) {
            state = STARTING_RACE;
            startRace();
            minutes = 5;
        }
    }

    protected void broadcast(final L2GameServerPacket pkt) {
        for (final RaceManagerInstance manager : managers) {
            if (!manager.isDead()) {
                manager.broadcastPacketToOthers(pkt);
            }
        }
    }

    public void sendMonsterInfo() {
        broadcast(packet);
    }

    private void startRace() {
        final MonsterRace race = MonsterRace.getInstance();
        if (state == STARTING_RACE) {
            //state++;
            final PlaySound SRace = new PlaySound("S_Race");
            broadcast(SRace);
            //TODO исправить 121209259 - обжект айди, ток неизвестно какого обьекта (VISTALL)
            final PlaySound SRace2 = new PlaySound(PlaySound.Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559));
            broadcast(SRace2);
            packet = new MonRaceInfo(codes[1][0], codes[1][1], race.getMonsters(), race.getSpeeds());
            sendMonsterInfo();

            ThreadPoolManager.getInstance().schedule(new RunRace(), 5000);
        } else {
            //state++;
            race.newRace();
            race.newSpeeds();
            packet = new MonRaceInfo(codes[0][0], codes[0][1], race.getMonsters(), race.getSpeeds());
            sendMonsterInfo();
        }

    }

    @Override
    public void onBypassFeedback(final Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("BuyTicket") && state != ACCEPTING_BETS) {
            player.sendPacket(MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
            command = "Chat 0";
        }
        if (command.startsWith("ShowOdds") && state == ACCEPTING_BETS) {
            player.sendPacket(MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
            command = "Chat 0";
        }

        if (command.startsWith("BuyTicket")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 0) {
                player.setRace(0, 0);
                player.setRace(1, 0);
            }
            if (val == 10 && player.getRace(0) == 0 || val == 20 && player.getRace(0) == 0 && player.getRace(1) == 0) {
                val = 0;
            }
            showBuyTicket(player, val);
        } else if ("ShowOdds".equals(command)) {
            showOdds(player);
        } else if ("ShowInfo".equals(command)) {
            showMonsterInfo(player);
        } else if ("calculateWin".equals(command)) {
            //displayCalculateWinnings(player);
        } else if ("viewHistory".equals(command)) {
            //displayHistory(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public void showOdds(final Player player) {
        if (state == ACCEPTING_BETS) {
            return;
        }
        final int npcId = getTemplate().npcId;
        final String filename;
        String search;
        final HtmlMessage html = new HtmlMessage(this);
        filename = getHtmlPath(npcId, 5, player);
        html.setFile(filename);
        for (int i = 0; i < 8; i++) {
            final int n = i + 1;
            search = "Mob" + n;
            html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
        }
        html.replace("1race", String.valueOf(_raceNumber));
        player.sendPacket(html);
        player.sendActionFailed();
    }

    public void showMonsterInfo(final Player player) {
        final int npcId = getTemplate().npcId;
        final String filename;
        String search;
        final HtmlMessage html = new HtmlMessage(this);
        filename = getHtmlPath(npcId, 6, player);
        html.setFile(filename);
        for (int i = 0; i < 8; i++) {
            final int n = i + 1;
            search = "Mob" + n;
            html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
        }
        player.sendPacket(html);
        player.sendActionFailed();
    }

    public void showBuyTicket(final Player player, final int val) {
        if (state != ACCEPTING_BETS) {
            return;
        }
        final int npcId = getTemplate().npcId;
        final String filename;
        String search;
        final String replace;
        final HtmlMessage html = new HtmlMessage(this);
        if (val < 10) {
            filename = getHtmlPath(npcId, 2, player);
            html.setFile(filename);
            for (int i = 0; i < 8; i++) {
                final int n = i + 1;
                search = "Mob" + n;
                html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
            }
            search = "No1";
            if (val == 0) {
                html.replace(search, "");
            } else {
                html.replace(search, String.valueOf(val));
                player.setRace(0, val);
            }
        } else if (val < 20) {
            if (player.getRace(0) == 0) {
                return;
            }
            filename = getHtmlPath(npcId, 3, player);
            html.setFile(filename);
            html.replace("0place", String.valueOf(player.getRace(0)));
            search = "Mob1";
            replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
            html.replace(search, replace);
            search = "0adena";
            if (val == 10) {
                html.replace(search, "");
            } else {
                html.replace(search, String.valueOf(cost[val - 11]));
                player.setRace(1, val - 10);
            }
        } else if (val == 20) {
            if (player.getRace(0) == 0 || player.getRace(1) == 0) {
                return;
            }
            filename = getHtmlPath(npcId, 4, player);
            html.setFile(filename);
            html.replace("0place", String.valueOf(player.getRace(0)));
            search = "Mob1";
            replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
            html.replace(search, replace);
            search = "0adena";
            final int price = cost[player.getRace(1) - 1];
            html.replace(search, String.valueOf(price));
            search = "0tax";
            final int tax = 0;
            html.replace(search, String.valueOf(tax));
            search = "0total";
            final int total = price + tax;
            html.replace(search, String.valueOf(total));
        } else {
            if (player.getRace(0) == 0 || player.getRace(1) == 0) {
                return;
            }
            if (player.getAdena() < cost[player.getRace(1) - 1]) {
                player.sendPacket(YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            final int ticket = player.getRace(0);
            final int priceId = player.getRace(1);
            player.setRace(0, 0);
            player.setRace(1, 0);
            player.reduceAdena(cost[priceId - 1], true);
            final SystemMessage sm = new SystemMessage(ACQUIRED_S1_S2);
            sm.addNumber(_raceNumber);
            sm.addItemName(4443);
            player.sendPacket(sm);
            final ItemInstance item = ItemFunctions.createItem(4443);
            item.setEnchantLevel(_raceNumber);
            item.setCustomType1(ticket);
            item.setCustomType2(cost[priceId - 1] / 100);
            player.getInventory().addItem(item);
            return;
        }
        html.replace("1race", String.valueOf(_raceNumber));
        player.sendPacket(html);
        player.sendActionFailed();
    }

    public MonRaceInfo getPacket() {
        return packet;
    }

    class Announcement extends RunnableImpl {
        private final SystemMsg type;

        public Announcement(final SystemMsg type) {
            this.type = type;
        }

        @Override
        public void runImpl() {
            makeAnnouncement(type);
        }
    }

    public class Race {
        private final Info[] info;

        public Race(final Info[] info) {
            this.info = info;
        }

        public Info getLaneInfo(final int lane) {
            return info[lane];
        }

        public class Info {
            private final int id;
            private final int place;
            private final int odds;
            private final int payout;

            public Info(final int id, final int place, final int odds, final int payout) {
                this.id = id;
                this.place = place;
                this.odds = odds;
                this.payout = payout;
            }

            public int getId() {
                return id;
            }

            public int getOdds() {
                return odds;
            }

            public int getPayout() {
                return payout;
            }

            public int getPlace() {
                return place;
            }
        }
    }

    class RunRace extends RunnableImpl {
        @Override
        public void runImpl() {
            packet = new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
            sendMonsterInfo();
            ThreadPoolManager.getInstance().schedule(new RunEnd(), 30000);
        }
    }

    class RunEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            makeAnnouncement(FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_S2);
            makeAnnouncement(MONSTER_RACE_S1_IS_FINISHED);
            _raceNumber++;
            ServerVariables.set("monster_race", _raceNumber);

            for (int i = 0; i < 8; i++) {
                broadcast(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
            }
        }
    }
}
