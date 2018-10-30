package org.mmocore.gameserver.scripts.ai.pagan_temple;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.World;

/**
 * @author PaInKiLlEr - AI для монстра Andreas Captain Royal Guard (22175). - Если увидел игрока в радиусе 500, если его пати состовляет больше 9
 * мемберов. - Тогда выбрасывает на рандомные координаты первого увидевшего игрока. - При атаке когда остаётся ХП меньше 70%, кидают дебаф и
 * умерают. - AI проверен и работает.
 */
public class AndreasCaptainRoyalGuard extends Fighter {
    public static final Location[] locs = {new Location(-16128, -35888, -10726), new Location(-17029, -39617, -10724), new Location(-15729, -42001, -10724)};
    private static int NUMBER_OF_DEATH = 0;
    private boolean tele = true;
    private boolean talk = true;

    public AndreasCaptainRoyalGuard(NpcInstance actor) {
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

                if (player.getParty().getMemberCount() >= 9) {
                    tele = false;
                    player.teleToLocation(Rnd.get(locs));
                }
            }
        }
        return true;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        if (actor.getCurrentHpPercents() <= 70) {
            actor.doCast(SkillTable.getInstance().getSkillEntry(4612, 9), attacker, true);
            actor.doDie(attacker);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        NUMBER_OF_DEATH++;
        if (talk) {
            if (NUMBER_OF_DEATH == 39) {
                talk = false;
                // Сбрасываем память
                NUMBER_OF_DEATH = 0;
                // мы убили всех монстров на балконе, закрываем двери на балкон
                ReflectionUtils.getDoor(19160014).openMe();
                ReflectionUtils.getDoor(19160015).openMe();
                // открываем двери к алтарю
                ReflectionUtils.getDoor(19160016).openMe();
                ReflectionUtils.getDoor(19160017).openMe();
            }
        }
        super.onEvtDead(killer);
    }
}