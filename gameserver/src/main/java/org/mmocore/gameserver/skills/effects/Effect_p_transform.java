package org.mmocore.gameserver.skills.effects;

import org.jts.dataparser.data.holder.TransformHolder;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;

/**
 * Create by Mangol on 15.10.2015.
 */
public class Effect_p_transform extends Effect {
    private final int id;

    public Effect_p_transform(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        id = template.getParam().getInteger("argument");
    }

    @Override
    public void onStart() {
        if (!getEffected().isPlayer()) {
            return;
        }
        final Player player = getEffected().getPlayer();
        if (player == null) {
            return;
        }
        final Optional<TransformData> transform = TransformHolder.getInstance().getTransformId(id);
        if (transform.isPresent()) {
            player.setTransformation(transform.get());
        }
        final Skill skill = getSkill().getTemplate();
        if (skill.isSSPossible()) {
            if (!(AllSettingsConfig.SAVING_SPS && skill.getSkillType() == Skill.SkillType.BUFF)) {
                player.unChargeShots(skill.isMagic());
            }
        }
        super.onStart();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    @Override
    protected void onExit() {
        if (!(getEffected() instanceof Player))
            return;
        final Player player = (Player) getEffected();
        player.stopTransformation(false);
        super.onExit();
    }
}
