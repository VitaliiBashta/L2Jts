package org.mmocore.gameserver.scripts.events.custom;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.listener.actor.OnDeathFromUndyingListener;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.List;

/**
 * @author VISTALL
 * @date 15:49/22.08.2011
 */
public class TeamVsTeamEvent extends CustomInstantTeamEvent {
    private OnDeathFromUndyingListener _onDeathFromUndyingListener = new OnDeathFromUndyingListenerImpl();
    private Territory[] _teleportLocs = new Territory[]{new Territory().add(new CustomPolygon(6).add(149878, 47505).add(150262, 47513).add(150502, 47233).add(150507, 46300).add(150256, 46002).add(149903, 46005).setZmin(-3408).setZmax(-3308)), new Territory().add(new CustomPolygon(6).add(149027, 46005).add(148686, 46003).add(148448, 46302).add(148449, 47231).add(148712, 47516).add(149014, 47527).setZmin(-3408).setZmax(-3308))};

    public TeamVsTeamEvent(MultiValueSet<String> set) {
        super(set);
    }

    private static void buffCreature(Creature creature, int id, int lvl) {
        SkillTable.getInstance().getSkillEntry(id, lvl).getEffects(creature, creature, false, false);
    }

    @Override
    protected int getInstantId() {
        return 500;
    }

	/*
	@Override
	public void onAddEvent(GameObject o)
	{
		if(o.isPlayer())
		{
			o.getPlayer().addListener(_onDeathFromUndyingListener);
		}
	}
	*/

    @Override
    protected Location getTeleportLoc(TeamType team) {
        return _teleportLocs[team.ordinal2()].getRandomLoc(_reflection.getGeoIndex());
    }

    @Override
    public void onRemoveEvent(GameObject o) {
        super.onRemoveEvent(o);
        if (o.isPlayer()) {
            o.getPlayer().removeListener(_onDeathFromUndyingListener);
            o.getPlayer().setUndying(false);
            if (o.getPlayer().isDead())
                o.getPlayer().doRevive(100);
        }
    }

    @Override
    public void teleportPlayers(String name, ZoneType zoneType) {
        super.teleportPlayers(name, zoneType);
        for (TeamType team : TeamType.VALUES) {
            preparePlayer(getPlayers(team));
            setParties(getPlayers(team));
        }
    }

    private void preparePlayer(List<Player> team) {
        if (team == null || team.size() == 0) {
            return;
        }
        for (Player player : team) {
            if (player == null)
                return;
            player.addListener(_onDeathFromUndyingListener);
            player.setUndying(true);
            Servitor servitor = player.getServitor();
            if (!EventsConfig.TVTAllowSummonTeleport) {
                if (servitor != null)
                    servitor.unSummon(true, true);
            }
            if (EventsConfig.TVTRemoveEffects) {
                player.getEffectList().stopAllEffects();
                if (servitor != null)
                    servitor.getEffectList().stopAllEffects();
            }
            if (EventsConfig.TVTAllowCustomBuffs) {
                if (player.isMageClass()) {
                    buffList(EventsConfig.TVTMageBuffs, player);
                    if (servitor != null)
                        buffList(EventsConfig.TVTMageBuffs, servitor);
                } else {
                    buffList(EventsConfig.TVTFighterBuffs, player);
                    if (servitor != null)
                        buffList(EventsConfig.TVTFighterBuffs, servitor);
                }
            }
            if (EventsConfig.TVTForbidTransformations)
                player.stopTransformation();
        }
    }

    @Override
    public void onDie(Player player) {
        if (!isInProgress())
            return;
        TeamType team = player.getTeam();
        if (team == TeamType.NONE)
            return;

        player.stopAttackStanceTask();
        player.startFrozen();
        player.getTeam().revert().incrementPoints();
        World.removeObjectFromPlayers(player);
        ThreadPoolManager.getInstance().schedule(new DieTask(player), 10000);
		/*
		player.setTeam(TeamType.NONE);
		for(Player $player : World.getAroundPlayers(player)) {
			$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
			if(player.getServitor() != null)
				$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getServitor());
		}
		player.sendChanges();
		List<DuelSnapshotObject> objs = getObjects(team);
		objs.stream().filter(obj -> obj.getPlayer() == player).forEach(DuelSnapshotObject::setDead);
		checkForWinner();
		*/
    }

    @Override
    public void onKill(Creature killer) {
        if (killer == null || killer.getPlayer() == null)
            return;
        for (int i = 0; i < _killRewardItems.length; i++) {
            killer.getPlayer().getInventory().addItem(_killRewardItems[i], _killRewardCounts[i]);
        }
    }

    @Override
    public synchronized void checkForWinner() {
        if (_state == State.NONE) {
            return;
        }
		/*
		TeamType winnerTeam = null;
		for(TeamType team : TeamType.VALUES)
		{
			final List<DuelSnapshotObject> objects = getObjects(team);
			boolean allDead = true;
			for(DuelSnapshotObject d : objects)
			{
				if(!d.isDead())
				{
					allDead = false;
				}
			}
			if(allDead)
			{
				winnerTeam = team.revert();
				break;
			}
		}
		*/
        int maxPoints = 0;
        TeamType topTeam = null;
        for (TeamType team : TeamType.VALUES)
            if (team.getPoints() > maxPoints) {
                maxPoints = team.getPoints();
                topTeam = team;
            }

        if (topTeam != null && maxPoints != 0) {
            _winner = topTeam;
            stopEvent();
        }
    }

    @Override
    protected boolean canWalkInWaitTime() {
        return false;
    }

    @Override
    protected void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit) {
        duelSnapshotObject.setDead();

        if (exit)
            duelSnapshotObject.clear();
    }

    @Override
    protected void actionUpdate(boolean start, Player player) {
        if (start && !isInProgress())
            return;
        player.setUndying(start);
    }

    @Override
    protected String getEventName(Player player) {
        return new CustomMessage("scripts.events.tvtName").toString(player);
    }

    private void cleanse(Player player) {
        if (player == null)
            return;
        player.getEffectList().getAllEffects().stream().filter(Effect::isOffensive).forEach(Effect::exit);
    }

    @Override
    public void action(String name, boolean start) {
        if (start && !EventsConfig.isTvtActive)
            return;
        super.action(name, start);
    }

    private class DieTask implements Runnable {
        private Player player;

        public DieTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (player == null || player.getReflection() != _reflection || player.getTeam() == TeamType.NONE)
                return;
            cleanse(player);
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
            player.setUndying(true);
            player.teleToLocation(getTeleportLoc(player.getTeam()), _reflection);
            player.stopFrozen();
            if (player.isMageClass())
                buffPlayerPet(EventsConfig.TVTMageBuffs, player);
            else
                buffPlayerPet(EventsConfig.TVTFighterBuffs, player);
        }
    }
}
