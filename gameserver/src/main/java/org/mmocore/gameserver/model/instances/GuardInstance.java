package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class GuardInstance extends NpcInstance {
    private static final long serialVersionUID = 1L;
    private final int NoFnHi;

    public GuardInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        NoFnHi = getParameter("NoFnHi", 0);
        setUndying(false);
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return attacker.isMonster() && ((MonsterInstance) attacker).isAggressive() || attacker.isPlayable() && attacker.getKarma() > 0;
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (NoFnHi == 1) {
            return;
        } else {
            super.showChatWindow(player, val);
        }
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp, final boolean directHp, final boolean lethal) {
        getAggroList().addDamageHate(attacker, (int) damage, 0);
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, lethal);
    }
}