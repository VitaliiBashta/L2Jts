package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author : Mangol
 * @date : 24.04.14  11:31
 */
public class Effect_i_restoration extends Effect {
    private final String[] capsule;
    private int itemId;
    private long count;
    private int capsule_itemId;
    private long capsule_count;

    public Effect_i_restoration(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        final String cp = template.getParam().getString("action_capsule", null);
        capsule = cp != null ? cp.split(";") : null;
        if (capsule != null) {
            capsule_itemId = Integer.parseInt(capsule[0]);
            capsule_count = Long.parseLong(capsule[1]);
        }
        final String[] item = getTemplate().getParam().getString("extract").split(";");
        itemId = Integer.parseInt(item[0]);
        count = Long.parseLong(item[1]);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Playable playable = (Playable) getEffector();
        if (playable != null && capsule != null) {
            ItemFunctions.removeItem(playable, capsule_itemId, capsule_count, true);
        }
        if (playable != null && itemId > 0) {
            ItemFunctions.addItem(playable, itemId, count, true);
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}