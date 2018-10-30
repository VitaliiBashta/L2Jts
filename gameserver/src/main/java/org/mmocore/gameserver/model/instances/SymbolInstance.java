package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.tasks.DeleteTask;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.taskmanager.EffectTaskManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


public class SymbolInstance extends NpcInstance {
    private final Creature _owner;
    private final SkillEntry _skill;
    private ScheduledFuture<?> _targetTask;
    private ScheduledFuture<?> _destroyTask;

    public SymbolInstance(final int objectId, final NpcTemplate template, final Creature owner, final SkillEntry skill) {
        super(objectId, template);
        _owner = owner;
        _skill = skill;

        setReflection(owner.getReflection());
        setLevel(owner.getLevel());
        setTitle(owner.getName());
    }

    public Creature getOwner() {
        return _owner;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        _destroyTask = ThreadPoolManager.getInstance().schedule(new DeleteTask(this), 120000L);

        _targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() throws Exception {
                for (final Creature target : getAroundCharacters(200, 200)) {
                    if (_skill.checkTarget(_owner, target, null, false, false) == null) {
                        final List<Creature> targets = new ArrayList<>();

                        if (!_skill.getTemplate().isAoE()) {
                            targets.add(target);
                        } else {
                            for (final Creature t : getAroundCharacters(_skill.getTemplate().getSkillRadius(), 128)) {
                                if (_skill.checkTarget(_owner, t, null, false, false) == null) {
                                    targets.add(target);
                                }
                            }
                        }

                        _skill.useSkill(SymbolInstance.this, targets);
                    }
                }
            }
        }, 1000L, 3000);
    }

    @Override
    protected void onDelete() {
        if (_destroyTask != null) {
            _destroyTask.cancel(false);
        }
        _destroyTask = null;
        if (_targetTask != null) {
            _targetTask.cancel(false);
        }
        _targetTask = null;
        super.onDelete();
    }

    @Override
    public int getPAtk(final Creature target) {
        final Creature owner = getOwner();
        return owner == null ? 0 : owner.getPAtk(target);
    }

    @Override
    public int getMAtk(final Creature target, final SkillEntry skill) {
        final Creature owner = getOwner();
        return owner == null ? 0 : owner.getMAtk(target, skill);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return false;
    }

    @Override
    public boolean isInvul() {
        return true;
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
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
    }

    @Override
    public void showChatWindow(final Player player, final String filename, final Object... replace) {
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
    }

    @Override
    public boolean isTargetable() {
        return false;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}
