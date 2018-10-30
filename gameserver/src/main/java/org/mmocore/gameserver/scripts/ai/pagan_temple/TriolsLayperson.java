package org.mmocore.gameserver.scripts.ai.pagan_temple;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

/**
 * @author PaInKiLlEr - AI для монстра Triols Layperson (22142). - Если увидел игрока в радиусе 500, если его пати состовляет больше 4 мемберов. -
 * Тогда выбрасывает на рандомные координаты первого увидевшего игрока. - AI проверен и работает.
 */
public class TriolsLayperson extends Fighter {
    public static final Location[] locs = {new Location(-16128, -35888, -10726), new Location(-17029, -39617, -10724), new Location(-15729, -42001, -10724)};
    private boolean tele = true;

    public TriolsLayperson(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (tele) {
            for (Player player : World.getAroundPlayers(actor, 500, 500)) {
                if (player == null || !player.isInParty())
                    continue;

                if (player.getParty().getMemberCount() >= 5) {
                    tele = false;
                    player.teleToLocation(Rnd.get(locs));
                }
            }
        }

        return true;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        tele = true;
        super.onEvtDead(killer);
    }
}