package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_fire_elemental_s extends ai_br_fire_elemental_base {
    public ai_br_fire_elemental_s(NpcInstance actor) {
        super(actor);
        SKILL_BURN = s_br_fire_elemental_burn;
        life_time = 70;
        max_hp = 25;
        dalay_flame = 12;
        dalay_flame_offset = 3;
        rate_flame = 100;
        rate_cold = 50;
        time_counter = 3;
        time_critical = 10;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ChatUtils.say(getActor(), NpcString.WHO_DARES_OPPOSE_THE_MAJESTY_OF_FIRE);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (Rnd.get(100) < 52) {
            getActor().dropItem(killer.getPlayer(), br_haste_juice, 2);
        }
        if (Rnd.get(10) < 4) {
            getActor().dropItem(killer.getPlayer(), br_focus_juice, 2);
        }
        if (Rnd.get(100) < 26) {
            getActor().dropItem(killer.getPlayer(), br_cash_quick_healing_potion, 2);
        }
        if (Rnd.get(100) < 100) {
            getActor().dropItem(killer.getPlayer(), br_cash_greater_healing_potion, 3);
        }
        if (Rnd.get(10) < 8) {
            getActor().dropItem(killer.getPlayer(), br_box_rune_of_eva_a, 1);
        }
        if (Rnd.get(100) < 1) {
            getActor().dropItem(killer.getPlayer(), br_box_rune_of_eva_b, 1);
        }
        if (Rnd.get(100) < 8) {
            getActor().dropItem(killer.getPlayer(), br_pigevent_scroll_of_escape, 1);
        }
        if (Rnd.get(100) < 2) {
            getActor().dropItem(killer.getPlayer(), br_pigevent_scroll_of_resurrection, 1);
        }
        if (Rnd.get(10) < 2) {
            getActor().dropItem(killer.getPlayer(), br_box_circlet_of_eva_a, 1);
        }
        ChatUtils.say(getActor(), NpcString.MY_BODY_IS_COOLING_OH_GRAN_KAIN_FORGIVE_ME);
        sendScriptEventOneNpc(ScriptEvent.SEND_DIE_FROM_FE, 111, 8, 0);
        super.onEvtDead(killer);
    }
}