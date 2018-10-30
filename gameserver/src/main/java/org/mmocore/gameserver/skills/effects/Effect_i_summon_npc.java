package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mangol
 * Этот эффект для спавна нпсов (незабываем описывать аи под них таймеры и тд.)
 */
public class Effect_i_summon_npc extends Effect {
    protected final Logger _log = LoggerFactory.getLogger(getClass());
    private final int npc_id;
    private final int count;

    public Effect_i_summon_npc(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        npc_id = template.getParam().getInteger("npc_id");
        count = template.getParam().getInteger("count");
    }

    @Override
    public void onStart() {
        final NpcHolder npc = NpcHolder.getInstance();
        final Creature c1 = getEffected();
        if (npc.getTemplate(npc_id) != null) {
            for (int i = 0; i < count; i++) {
                NpcUtils.createOnePrivateEx(npc_id, Location.findPointToStay(getEffected(), 40, 70), getEffected().getReflection(), c1, 0, 0);
            }
        } else {
            _log.warn(getClass().getSimpleName() + ": " + npc_id + " null!");
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
