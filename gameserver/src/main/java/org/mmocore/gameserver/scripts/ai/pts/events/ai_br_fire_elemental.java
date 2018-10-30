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
public class ai_br_fire_elemental extends ai_br_fire_elemental_base {
    public ai_br_fire_elemental(NpcInstance actor) {
        super(actor);
        SKILL_BURN = s_br_fire_elemental_burn2;
        max_hp = 10;
        life_time = 70;
        max_hp = 15;
        dalay_flame = 12;
        dalay_flame_offset = 3;
        rate_flame = 100;
        rate_cold = 5;
        time_counter = 3;
        time_critical = 10;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ChatUtils.say(getActor(), NpcString.HOW_DARE_YOU_AWAKEN_ME_FEEL_THE_PAIN_OF_THE_FLAMES);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (killer != null && killer.isPlayer()) {
            if (Rnd.get(100) < 20) {
                getActor().dropItem(killer.getPlayer(), br_might_juice, 1);
            }
            if (Rnd.get(100) < 100) {
                getActor().dropItem(killer.getPlayer(), br_cash_greater_healing_potion, 1);
            }
            if (Rnd.get(100) < 10) {
                getActor().dropItem(killer.getPlayer(), br_cash_quick_healing_potion, 1);
            }
            if (Rnd.get(1000) < 125) {
                getActor().dropItem(killer.getPlayer(), br_box_rune_of_eva_a, 1);
            }
            if (Rnd.get(100) < 1) {
                getActor().dropItem(killer.getPlayer(), br_pigevent_scroll_of_escape, 1);
            }
            if (Rnd.get(100) < 3) {
                getActor().dropItem(killer.getPlayer(), br_box_circlet_of_eva_a, 1);
            }
        }
        ChatUtils.say(getActor(), NpcString.COLD_THIS_PLACE_IS_THIS_WHERE_I_DIE);
        sendScriptEventOneNpc(ScriptEvent.SEND_DIE_FROM_FE, 111, 1, 0);
        super.onEvtDead(killer);
    }
}