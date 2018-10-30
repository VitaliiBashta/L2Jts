package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.geometry.Rectangle;
import org.mmocore.gameserver.manager.naia.NaiaTowerManager;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class NaiaRoomControllerInstance extends NpcInstance {
    private static final int[][] CLOSE_DOORS = {
            // Room 1
            {
                    18250001
            },
            // Room 2
            {
                    18250002,
                    18250003
            },
            // Room 3
            {
                    18250004,
                    18250005
            },
            // Room 4
            {
                    18250006,
                    18250007
            },
            // Room 5
            {
                    18250008,
                    18250009
            },
            // Room 6
            {
                    18250010,
                    18250011
            },
            // Room 7
            {
                    18250101,
                    18250013
            },
            // Room 8
            {
                    18250014,
                    18250015
            },
            // Room 9
            {
                    18250102,
                    18250017
            },
            // Room 10
            {
                    18250018,
                    18250019
            },
            // Room 11
            {
                    18250103,
                    18250021
            },
            // Room 12
            {
                    18250022,
                    18250023
            }
    };

    /**
     * 1,3,5,6,7,8,9,10,11,12 комнаты
     */
    private static final Territory[] spawn_territory = {
            new Territory().add(new Rectangle(-46652, 245576, -45735, 246648).setZmin(-9175).setZmax(-9075)), // 0 - 1
            new Territory().add(new Rectangle(-52088, 245667, -51159, 246609).setZmin(-10037).setZmax(-9837)), // 1 - 3
            new Territory().add(new Rectangle(-46652, 245596, -45737, 246626).setZmin(-10032).setZmax(-9832)), // 2 - 5
            new Territory().add(new Rectangle(-49220, 247903, -48647, 248543).setZmin(-10027).setZmax(-9827)), // 3 - 6
            new Territory().add(new Rectangle(-52068, 245575, -51195, 246617).setZmin(-10896).setZmax(-10696)), // 4 - 7
            new Territory().add(new Rectangle(-49284, 243788, -48592, 244408).setZmin(-10892).setZmax(-10692)),    // 5 - 8
            new Territory().add(new Rectangle(-46679, 245661, -45771, 246614).setZmin(-11756).setZmax(-11556)),    // 6 - 9
            new Territory().add(new Rectangle(-49252, 247894, -48587, 248519).setZmin(-11757).setZmax(-11757)),    // 7 - 10
            new Territory().add(new Rectangle(-52080, 245665, -51174, 246660).setZmin(-12619).setZmax(-12419)),    // 8 - 11
            new Territory().add(new Rectangle(-48732, 243186, -47752, 244097).setZmin(-13423).setZmax(-13223))    // 9 - 12
    };

    private static final int[][][] FLOOR_SPAWNLIST = {
            // 1 floor
            {{22393, 3, 0}, {22394, 3, 0}},

            // 2 floor
            {
                    {22439, 1, -48146, 249597, -9124, -16280},
                    {22439, 1, -48144, 248711, -9124, 16368},
                    {22439, 1, -48704, 249597, -9104, -16380},
                    {22439, 1, -49219, 249596, -9104, -16400},
                    {22439, 1, -49715, 249601, -9104, -16360},
                    {22439, 1, -49714, 248696, -9104, 15932},
                    {22439, 1, -49225, 248710, -9104, 16512},
                    {22439, 1, -48705, 248708, -9104, 16576}
            },

            // 3 floor
            {{22441, 2, 1}, {22442, 2, 1}},

            // 4 floor
            {
                    {22440, 1, -49754, 243866, -9968, -16328},
                    {22440, 1, -49754, 242940, -9968, 16336},
                    {22440, 1, -48733, 243858, -9968, -16208},
                    {22440, 1, -48745, 242936, -9968, 16320},
                    {22440, 1, -49264, 242946, -9968, 16312},
                    {22440, 1, -49268, 243869, -9968, -16448},
                    {22440, 1, -48186, 242934, -9968, 16576},
                    {22440, 1, -48185, 243855, -9968, -16448}
            },

            // 5 floor
            {{22411, 2, 2}, {22393, 2, 2}, {22394, 2, 2}},

            // 6 floor
            {{22395, 2, 3}},

            // 7 floor
            {{22393, 3, 4}, {22394, 3, 4}, {22412, 1, 4}},

            // 8 floor
            {{22395, 2, 5}},

            // 9 floor
            {{22441, 2, 6}, {22442, 3, 6}},

            // 10 floor
            {{22395, 2, 7}},

            // 11 floor
            {{22413, 6, 8}},

            // 12 floor
            {{18490, 12, 9}}
    };

    public NaiaRoomControllerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("challengeroom")) {
            final Party party = player.getParty();
            if (party == null) {
                player.sendPacket(SystemMsg.YOU_MUST_BE_IN_A_PARTY_IN_ORDER_TO_OPERATE_THE_MACHINE);
                return;
            }

            if (!NaiaTowerManager.isValidParty(party))
                return;

            for (final Player member : party.getPartyMembers())
                if (!member.isInRange(player, 600)) {
                    player.sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
                    return;
                }


            final int roomId = getRoomId();
            if (NaiaTowerManager.isRoomDone(roomId, party)) {
                player.sendPacket(new HtmlMessage(this).setHtml("Ingenious Contraption:<br><br>The room is already challenged."));
                return;
            }

            if (NaiaTowerManager.lockRoom(roomId)) {
                NaiaTowerManager.setCurrentRoom(roomId, party);

                final int[] doors = CLOSE_DOORS[roomId];
                for (final int doorId : doors) {
                    ReflectionUtils.getDoor(doorId).closeMe();
                }

                spawnNpc(roomId);

                if (roomId == 11) {// Last special room
                    //FIXME[Hack]: разобраться в этом пиздеце и написать нормально. Допускаться должны все плееры в цц,
                    // а не только в главной пати
                    CommandChannel cc = party.getCommandChannel();
                    if (cc != null) {
                        for (Party pty : cc.getParties())
                            NaiaTowerManager.removeGroupTimer(pty);
                    } else {
                        NaiaTowerManager.removeGroupTimer(party);
                    }
                } else if (roomId != 0) // no update for 1st room
                    NaiaTowerManager.updateGroupTimer(party);
            } else
                _log.warn("TowerOfNaiaManager: failed lock room={}, player={}", roomId, player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void spawnNpc(final int roomId) {
        final int[][] spawnlist = FLOOR_SPAWNLIST[roomId];

        SimpleSpawner spawnDat;
        for (final int[] data : spawnlist)
            try {
                spawnDat = new SimpleSpawner(data[0]);
                spawnDat.setAmount(data[1]);
                // спаун по терретории
                if (data.length == 3)
                    spawnDat.setTerritory(spawn_territory[data[2]]);
                else {
                    spawnDat.setLocx(data[2]);
                    spawnDat.setLocy(data[3]);
                    spawnDat.setLocz(data[4]);
                    spawnDat.setHeading(data[5]);
                }
                spawnDat.setRespawnDelay(0);
                spawnDat.setRespawnTime(0);
                // спауним
                spawnDat.init();

                // ложим в список с НПС для этажа
                for (final NpcInstance npc : spawnDat.getAllSpawned())
                    NaiaTowerManager.addMobToRoom(roomId, npc);
            } catch (final Exception e) {
                _log.warn("TowerOfNaiaManager: spawnNpc error!", e);
            }
    }

    private int getRoomId() {
        return getNpcId() - 18494;
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        return "default/18494.htm";
    }
}