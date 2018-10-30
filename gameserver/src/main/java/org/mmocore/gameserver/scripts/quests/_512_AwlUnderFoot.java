package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listener.event.OnStartStopListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class _512_AwlUnderFoot extends Quest {
    private final static int INSTANCE_ZONE_ID = 13; // Castles Dungeon

    private final static int FragmentOfTheDungeonLeaderMark = 9798;
    private final static int RewardMarksCount = 1500;
    private final static int KnightsEpaulette = 9912;

    private static final Map<Integer, Prison> _prisons = new ConcurrentHashMap<Integer, Prison>();

    private static final int RhiannaTheTraitor = 25546;
    private static final int TeslaTheDeceiver = 25549;
    private static final int SoulHunterChakundel = 25552;

    private static final int DurangoTheCrusher = 25553;
    private static final int BrutusTheObstinate = 25554;
    private static final int RangerKarankawa = 25557;
    private static final int SargonTheMad = 25560;

    private static final int BeautifulAtrielle = 25563;
    private static final int NagenTheTomboy = 25566;
    private static final int JaxTheDestroyer = 25569;

    private static final int[] type1 = new int[]{RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel};
    private static final int[] type2 = new int[]{DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad};
    private static final int[] type3 = new int[]{BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer};

    public _512_AwlUnderFoot() {
        super(false);

        // Wardens
        addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
        addQuestItem(FragmentOfTheDungeonLeaderMark);
        addKillId(RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel, DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad, BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);
        addLevelCheck(70);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("gludio_prison_keeper_q0512_03.htm") || event.equalsIgnoreCase("gludio_prison_keeper_q0512_05.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("exit")) {
            st.exitQuest(true);
            return null;
        } else if (event.equalsIgnoreCase("enter"))
            if (st.getState() == CREATED || !check(st.getPlayer()))
                return "gludio_prison_keeper_q0512_01a.htm";
            else
                return enterPrison(st.getPlayer());
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        switch (isAvailableFor(st.getPlayer())) {
            case LEVEL:
                return "gludio_prison_keeper_q0512_02.htm";
            default:
                if (!check(st.getPlayer()))
                    return "gludio_prison_keeper_q0512_01a.htm";
                if (st.getState() == CREATED)
                    return "gludio_prison_keeper_q0512_01.htm";
                if (st.ownItemCount(FragmentOfTheDungeonLeaderMark) > 0) {
                    st.giveItems(KnightsEpaulette, st.ownItemCount(FragmentOfTheDungeonLeaderMark));
                    st.takeItems(FragmentOfTheDungeonLeaderMark, -1);
                    st.soundEffect(SOUND_FINISH);
                    return "gludio_prison_keeper_q0512_08.htm";
                }
                return "gludio_prison_keeper_q0512_09.htm";
        }
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        for (Prison prison : _prisons.values())
            if (prison.getReflectionId() == npc.getReflectionId()) {
                switch (npc.getNpcId()) {
                    case RhiannaTheTraitor:
                    case TeslaTheDeceiver:
                    case SoulHunterChakundel:
                        prison.initSpawn(type2[Rnd.get(type2.length)], false);
                        break;
                    case DurangoTheCrusher:
                    case BrutusTheObstinate:
                    case RangerKarankawa:
                    case SargonTheMad:
                        prison.initSpawn(type3[Rnd.get(type3.length)], false);
                        break;
                    case BeautifulAtrielle:
                    case NagenTheTomboy:
                    case JaxTheDestroyer:
                        Party party = st.getPlayer().getParty();
                        if (party != null) {
                            ArrayList<QuestState> players = new ArrayList<QuestState>(party.getMemberCount());
                            for (Player member : party.getPartyMembers()) {
                                if (member != null && member.isInRange(st.getPlayer(), 1500)) {
                                    QuestState qs = member.getQuestState(this);
                                    if (qs != null && qs.isStarted())
                                        players.add(qs);
                                }
                            }
                            if (!players.isEmpty()) {
                                int itemCount = RewardMarksCount / players.size();
                                for (QuestState qs : players) {
                                    qs.giveItems(FragmentOfTheDungeonLeaderMark, itemCount);
                                    qs.soundEffect(SOUND_ITEMGET);
                                    qs.getPlayer().sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                                }
                            } else {
                                st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount);
                                st.soundEffect(SOUND_ITEMGET);
                                st.getPlayer().sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                            }
                        } else {
                            st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount);
                            st.soundEffect(SOUND_ITEMGET);
                            st.getPlayer().sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                        }
                        Reflection r = ReflectionManager.getInstance().get(prison.getReflectionId());
                        if (r != null)
                            r.startCollapseTimer(300000); // Всех боссов убили, запускаем коллапс через 5 минут
                        break;
                }
                break;
            }

        return null;
    }

    private boolean check(Player player) {
        Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
        if (castle == null)
            return false;
        Clan clan = player.getClan();
        if (clan == null)
            return false;
        if (clan.getClanId() != castle.getOwnerId())
            return false;
        return true;
    }

    private String enterPrison(Player player) {
        Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
        if (castle == null || castle.getOwner() != player.getClan())
            return "gludio_prison_keeper_q0512_01a.htm";

        if (player.getEvent(DominionSiegeEvent.class) != null || player.getEvent(CastleSiegeEvent.class) != null)
            return "gludio_prison_keeper_q0512_01b.htm";

        if (!areMembersSameClan(player))
            return "gludio_prison_keeper_q0512_01a.htm";

        if (player.canEnterInstance(INSTANCE_ZONE_ID)) {
            InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(INSTANCE_ZONE_ID);
            Prison prison = null;

            Reflection reflection = null;

            if (!_prisons.isEmpty()) {
                prison = _prisons.get(castle.getId());
                // Если игрок пытается перезайти в инстанс (в случае смерти/SOE).
                if (prison != null && prison.isLocked()) {
                    reflection = ReflectionManager.getInstance().get(prison.getReflectionId());
                    if (reflection == null || !reflection.isVisitor(player)) {
                        player.sendPacket(new SystemMessage(SystemMsg.C1_MAY_NOT_REENTER_YET).addName(player));
                        return null;
                    }
                }
            }

            // Если игрок успел перезайти до коллапса - не создаем новый инст, а продолжаемый старый.
            if (reflection == null) {
                prison = new Prison(castle.getId(), iz);
                _prisons.put(prison.getCastleId(), prison);
                reflection = ReflectionManager.getInstance().get(prison.getReflectionId());
            }

            reflection.setReturnLoc(player.getLoc());

            for (Player member : player.getParty().getPartyMembers()) {
                if (member != player)
                    newQuestState(member, STARTED);
                member.setReflection(reflection);
                member.teleToLocation(iz.getTeleportCoord());
                member.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, reflection.getReturnLoc().toXYZString(), -1);
                member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
            }

            player.getParty().setReflection(reflection);
            reflection.setParty(player.getParty());
            reflection.startCollapseTimer(iz.getTimelimit() * 60 * 1000L);
            player.getParty().broadCast(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(iz.getTimelimit()));

            prison.initSpawn(type1[Rnd.get(type1.length)], true);
        }
        return null;
    }

    private boolean areMembersSameClan(Player player) {
        if (player.getParty() == null)
            return true;
        for (Player p : player.getParty().getPartyMembers())
            if (p.getClan() != player.getClan())
                return false;
        return true;
    }

    private class Prison {
        private int _castleId;
        private int _reflectionId;
        private long _lastEnter;

        private ScheduledFuture<?> _spawnTask;

        public Prison(int id, InstantZone iz) {
            try {
                Reflection r = new Reflection();
                r.init(iz);
                _reflectionId = r.getId();
                _castleId = id;
                _lastEnter = System.currentTimeMillis();

                Castle castle = ResidenceHolder.getInstance().getResidence(_castleId);

                castle.getSiegeEvent().addListener(new OnStartStopListnerImpl());
                castle.getDominion().getSiegeEvent().addListener(new OnStartStopListnerImpl());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void initSpawn(int npcId, boolean first) {
            if (_spawnTask != null && first)
                return;
            _spawnTask = ThreadPoolManager.getInstance().schedule(new PrisonSpawnTask(npcId), first ? 60000 : 180000);
        }

        public int getReflectionId() {
            return _reflectionId;
        }

        public int getCastleId() {
            return _castleId;
        }

        public boolean isLocked() {
            return System.currentTimeMillis() - _lastEnter < 4 * 60 * 60 * 1000L;
        }

        private class PrisonSpawnTask extends RunnableImpl {
            int _npcId;

            public PrisonSpawnTask(int npcId) {
                _npcId = npcId;
            }

            @Override
            public void runImpl() throws Exception {
                addSpawnToInstance(_npcId, new Location(12152, -49272, -3008, 25958), 0, _reflectionId);
            }
        }
    }

    private class OnStartStopListnerImpl implements OnStartStopListener {
        @Override
        public void onStart(Event event) {
            try {
                Castle castle;

                for (Residence residence : ResidenceHolder.getInstance().getResidences())
                    if (residence.getSiegeEvent().equals(event)) {
                        // Если эвент принадлежит замку -> закрываем инстанс.
                        if (residence instanceof Castle)
                            collapseRelfections(residence.getId());
                            // Если эвент принадлежит территории замка -> закрываем инстанс.
                        else if (residence instanceof Dominion) {
                            castle = ((Dominion) residence).getCastle();
                            collapseRelfections(castle.getId());
                        }
                    }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStop(Event event) {
        }

        public void collapseRelfections(int fortId) {
            Prison prison = _prisons.get(fortId);
            if (prison != null) {
                Reflection ref = ReflectionManager.getInstance().get(prison.getReflectionId());
                if (ref != null)
                    ref.collapse();
            }
        }
    }
}