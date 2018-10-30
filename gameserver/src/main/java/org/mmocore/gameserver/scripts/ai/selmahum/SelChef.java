package org.mmocore.gameserver.scripts.ai.selmahum;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Felixx, SyS
 * @editor PaInKiLlEr - AI для Sel Mahum Shef (18908). - Находясь рядом, бафает скил на игрока, ресая ХП игроку. - AI проверен и работает.
 */
public class SelChef extends Fighter {
    public static final NpcString[] text = {NpcString.I_BROUGHT_THE_FOOD, NpcString.COME_AND_EAT};
    private long wait_timeout = 0;
    private long wait_timeout_heal = System.currentTimeMillis() + 30000;
    private boolean firstTime = true;

    public SelChef(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        if (_def_think) {
            doTask();
            return true;
        }
        if (System.currentTimeMillis() > wait_timeout) {
            wait_timeout = System.currentTimeMillis() + 2000;
            actor.setWalking();
            Location targetLoc = findFirePlace(actor);
            addTaskMove(targetLoc, true);
            doTask();
        }
        if (wait_timeout_heal < System.currentTimeMillis()) {
            List<Player> players = World.getAroundPlayers(actor, 100, 100);
            for (Player p : players)
                actor.doCast(SkillTable.getInstance().getSkillEntry(6330, 1), p, true);
            wait_timeout_heal = (System.currentTimeMillis() + 30000);
        }
        return false;
    }

    private Location findFirePlace(NpcInstance actor) {
        Location loc = new Location();
        List<NpcInstance> list = new ArrayList<NpcInstance>();
        for (NpcInstance npc : actor.getAroundNpc(3000, 600)) {
            if (npc.getNpcId() == 18927 && GeoEngine.canSeeTarget(actor, npc, false)) {
                list.add(npc);
                if (firstTime) {
                    // Включаем паузу что бы не зафлудить чат.
                    firstTime = false;
                    Functions.npcSay(getActor(), text[Rnd.get(text.length)]);
                    ThreadPoolManager.getInstance().schedule(new NewText(), 20000); // Время паузы
                }
            }
        }
        if (!list.isEmpty()) {
            loc = list.get(Rnd.get(list.size())).getLoc();
        } else {
            loc = Location.findPointToStay(actor, 1000, 1500);
        }
        return loc;
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    private class NewText extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;

            // Выключаем паузу
            firstTime = true;
        }
    }
}