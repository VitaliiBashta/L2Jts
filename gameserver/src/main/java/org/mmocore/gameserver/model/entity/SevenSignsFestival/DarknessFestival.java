package org.mmocore.gameserver.model.entity.SevenSignsFestival;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.instances.FestivalMonsterInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class DarknessFestival extends Reflection {
    public static final int FESTIVAL_LENGTH = 1080000; // 18 mins
    public static final int FESTIVAL_FIRST_SPAWN = 60000; // 1 min
    public static final int FESTIVAL_SECOND_SPAWN = 540000; // 9 mins
    public static final int FESTIVAL_CHEST_SPAWN = 900000; // 15 mins
    private static final Logger _log = LoggerFactory.getLogger(DarknessFestival.class);
    private final FestivalSpawn _witchSpawn;
    private final FestivalSpawn _startLocation;
    private final int _levelRange;
    private final int _cabal;
    private int currentState = 0;
    private boolean _challengeIncreased = false;
    private Future<?> _spawnTimerTask;

    public DarknessFestival(final Party party, final int cabal, final int level) {
        super();
        onCreate();
        setName("Darkness Festival");
        setParty(party);
        _levelRange = level;
        _cabal = cabal;
        startCollapseTimer(FESTIVAL_LENGTH + FESTIVAL_FIRST_SPAWN);

        if (cabal == SevenSigns.CABAL_DAWN) {
            _witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS[_levelRange]);
            _startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS[_levelRange]);
        } else {
            _witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS[_levelRange]);
            _startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS[_levelRange]);
        }

        party.setReflection(this);
        setReturnLoc(party.getGroupLeader().getLoc());
        for (final Player p : party.getPartyMembers()) {
            p.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, p.getLoc().toXYZString(), -1);
            p.getEffectList().stopAllEffects();
            p.teleToLocation(Location.findPointToStay(_startLocation.loc, 20, 100, getGeoIndex()), this);
        }

        scheduleNext();
        final NpcTemplate witchTemplate = NpcHolder.getInstance().getTemplate(_witchSpawn.npcId);
        // Spawn the festival witch for this arena
        try {
            final SimpleSpawner npcSpawn = new SimpleSpawner(witchTemplate);
            npcSpawn.setLoc(_witchSpawn.loc);
            npcSpawn.setReflection(this);
            addSpawn(npcSpawn);
            npcSpawn.doSpawn(true);
        } catch (Exception e) {
            _log.error("", e);
        }
        sendMessageToParticipants("The festival will begin in 1 minute.");
    }

    private void scheduleNext() {
        switch (currentState) {
            case 0:
                currentState = FESTIVAL_FIRST_SPAWN;

                _spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 0);
                        sendMessageToParticipants("Go!");
                        scheduleNext();
                    }
                }, FESTIVAL_FIRST_SPAWN);
                break;
            case FESTIVAL_FIRST_SPAWN:
                currentState = FESTIVAL_SECOND_SPAWN;

                _spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 2);
                        sendMessageToParticipants("Next wave arrived!");
                        scheduleNext();
                    }
                }, FESTIVAL_SECOND_SPAWN - FESTIVAL_FIRST_SPAWN);
                break;
            case FESTIVAL_SECOND_SPAWN:
                currentState = FESTIVAL_CHEST_SPAWN;

                _spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 3);
                        sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
                    }
                }, FESTIVAL_CHEST_SPAWN - FESTIVAL_SECOND_SPAWN);
                break;
        }
    }

    public void spawnFestivalMonsters(final int respawnDelay, final int spawnType) {
        int[][] spawns = null;
        switch (spawnType) {
            case 0:
            case 1:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS[_levelRange];
                break;
            case 2:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS[_levelRange];
                break;
            case 3:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS[_levelRange];
                break;
        }

        if (spawns != null) {
            for (final int[] element : spawns) {
                final FestivalSpawn currSpawn = new FestivalSpawn(element);
                final NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(currSpawn.npcId);

                final SimpleSpawner npcSpawn;
                npcSpawn = new SimpleSpawner(npcTemplate);
                npcSpawn.setReflection(this);
                npcSpawn.setLoc(currSpawn.loc);
                npcSpawn.setHeading(Rnd.get(65536));
                npcSpawn.setAmount(1);
                npcSpawn.setRespawnDelay(respawnDelay);
                npcSpawn.startRespawn();
                final FestivalMonsterInstance festivalMob = (FestivalMonsterInstance) npcSpawn.doSpawn(true);
                // Set the offering bonus to 2x or 5x the amount per kill, if this spawn is part of an increased challenge or is a festival chest.
                if (spawnType == 1) {
                    festivalMob.setOfferingBonus(2);
                } else if (spawnType == 3) {
                    festivalMob.setOfferingBonus(5);
                }
                addSpawn(npcSpawn);
            }
        }
    }

    public boolean increaseChallenge() {
        if (_challengeIncreased) {
            return false;
        }
        // Set this flag to true to make sure that this can only be done once.
        _challengeIncreased = true;
        // Spawn more festival monsters, but this time with a twist.
        spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 1);
        return true;
    }

    @Override
    public void collapse() {
        if (isCollapseStarted()) {
            return;
        }

        if (_spawnTimerTask != null) {
            _spawnTimerTask.cancel(false);
            _spawnTimerTask = null;
        }

        if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_COMPETITION && getParty() != null) {
            final Player player = getParty().getGroupLeader();
            final ItemInstance bloodOfferings = player.getInventory().getItemByItemId(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);
            final long offeringCount = bloodOfferings == null ? 0 : bloodOfferings.getCount();
            // Check if the player collected any blood offerings during the festival.
            if (player.getInventory().destroyItem(bloodOfferings)) {
                final long offeringScore = offeringCount * SevenSignsFestival.FESTIVAL_OFFERING_VALUE;
                final boolean isHighestScore = SevenSignsFestival.getInstance().setFinalScore(getParty(), _cabal, _levelRange, offeringScore);
                // Send message that the contribution score has increased.
                player.sendPacket(new SystemMessage(SystemMsg.YOUR_CONTRIBUTION_SCORE_HAS_INCREASED_BY_S1).addNumber(offeringScore));

                sendCustomMessageToParticipants("org.mmocore.gameserver.model.entity.SevenSignsFestival.Ended");
                if (isHighestScore) {
                    sendMessageToParticipants("Your score is highest!");
                }
            } else {
                player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings"));
            }
        }

        super.collapse();
    }

    private void sendMessageToParticipants(final String s) {
        for (final Player p : getPlayers()) {
            p.sendMessage(s);
        }
    }

    private void sendCustomMessageToParticipants(final String s) {
        final CustomMessage cm = new CustomMessage(s);
        for (final Player p : getPlayers()) {
            p.sendMessage(cm);
        }
    }

    public void partyMemberExited() {
        if (getParty() == null || getParty().getMemberCount() <= 1) {
            collapse();
        }
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public boolean isAutolootForced() {
        return true;
    }
}