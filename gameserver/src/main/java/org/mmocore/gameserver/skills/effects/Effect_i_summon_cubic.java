package org.mmocore.gameserver.skills.effects;

import org.jts.dataparser.data.holder.CubicDataHolder;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;

/**
 * Create by Mangol on 24.09.2015.
 */
public class Effect_i_summon_cubic extends Effect {
    private final String[] _st;
    private final int _id;
    private final int _level;

    public Effect_i_summon_cubic(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _st = template.getParam().getString("argument").split(";");
        _id = Integer.parseInt(_st[0]);
        _level = Integer.parseInt(_st[1]);
    }

    @Override
    public void onStart() {
        final Optional<DefaultCubicData> cubic = Optional.ofNullable(CubicDataHolder.getInstance().getCubicTemplate(_id, _level));
        if (cubic.isPresent()) {
            if (getEffected().isPlayer()) {
                final Player player = (Player) getEffected();
                if (!player.getCubics().isEmpty()) {
                    final Optional<CubicComponent> engine = Optional.ofNullable(player.getCubicSlot(cubic.get().slot));
                    if (engine.isPresent()) {
                        engine.get().delete(false);
                    }
                }
                player.addCubic(cubic.get());
            }
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
