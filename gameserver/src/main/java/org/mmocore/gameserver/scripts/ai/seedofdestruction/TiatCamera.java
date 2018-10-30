package org.mmocore.gameserver.scripts.ai.seedofdestruction;


import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pchayka
 */
public class TiatCamera extends DefaultAI {
    private List<Player> _players = new ArrayList<Player>();

    public TiatCamera(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.startDamageBlocked();
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        for (Player p : World.getAroundPlayers(actor, 300, 300)) {
            if (!_players.contains(p)) {
                p.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_OPENING);
                _players.add(p);
            }
        }
        return true;
    }
}