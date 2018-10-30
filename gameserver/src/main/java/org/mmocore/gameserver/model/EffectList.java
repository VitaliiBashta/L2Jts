package org.mmocore.gameserver.model;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.skills.skillclasses.Transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EffectList {
    public static final int NONE_SLOT_TYPE = -1;
    public static final int BUFF_SLOT_TYPE = 0;
    public static final int MUSIC_SLOT_TYPE = 1;
    public static final int TRIGGER_SLOT_TYPE = 2;
    public static final int DEBUFF_SLOT_TYPE = 3;

    public static final int DEBUFF_LIMIT = 8;
    public static final int TRIGGER_LIMIT = 12;

    private final Creature _actor;
    private final Lock _lock = new ReentrantLock();
    private List<Effect> _effects;

    public EffectList(final Creature owner) {
        _actor = owner;
    }

    public static int getSlotType(final Effect e) {
        if (e.getSkill().getTemplate().isPassive() || e.getSkill().getTemplate().isToggle() || e.getSkill().getTemplate() instanceof Transformation || e.getStackType() == EffectTemplate.HP_RECOVER_CAST || e.getEffectType() == EffectType.CharmOfCourage) {
            return NONE_SLOT_TYPE;
        } else if (e.getSkill().getTemplate().isOffensive()) {
            return DEBUFF_SLOT_TYPE;
        } else if (e.getSkill().getTemplate().isMusic()) {
            return MUSIC_SLOT_TYPE;
        } else if (e.getSkill().getTemplate().isTrigger()) {
            return TRIGGER_SLOT_TYPE;
        } else {
            return BUFF_SLOT_TYPE;
        }
    }

    public static boolean checkStackType(final EffectTemplate ef1, final EffectTemplate ef2) {
        if (ef1._stackType != EffectTemplate.NO_STACK && ef1._stackType.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        if (ef1._stackType != EffectTemplate.NO_STACK && ef1._stackType.equalsIgnoreCase(ef2._stackType2)) {
            return true;
        }
        if (ef1._stackType2 != EffectTemplate.NO_STACK && ef1._stackType2.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        if (ef1._stackType2 != EffectTemplate.NO_STACK && ef1._stackType2.equalsIgnoreCase(ef2._stackType2)) {
            return true;
        }
        return false;
    }

    void lock() {
        _lock.lock();
    }

    void unlock() {
        _lock.unlock();
    }

    /**
     * Возвращает число эффектов соответствующее данному скиллу
     */
    public int getEffectsCountForSkill(final int skill_id) {
        if (isEmpty()) {
            return 0;
        }

        int count = 0;

        for (final Effect e : _effects) {
            if (e.getSkill().getId() == skill_id) {
                count++;
            }
        }

        return count;
    }

    public Effect getEffectByType(final EffectType et) {
        if (isEmpty()) {
            return null;
        }

        for (final Effect e : _effects) {
            if (e.getEffectType() == et) {
                return e;
            }
        }

        return null;
    }

    public List<Effect> getEffectsBySkill(final SkillEntry skill) {
        if (skill == null) {
            return null;
        }
        return getEffectsBySkillId(skill.getId());
    }

    public List<Effect> getEffectsBySkillId(final int skillId) {
        if (isEmpty()) {
            return null;
        }

        final List<Effect> list = new ArrayList<>(2);
        list.addAll(_effects.stream().filter(e -> e.getSkill().getId() == skillId).collect(Collectors.toList()));

        return list.isEmpty() ? null : list;
    }

    public Effect getEffectByIndexAndType(final int skillId, final EffectType type) {
        if (isEmpty()) {
            return null;
        }
        for (final Effect e : _effects) {
            if (e.getSkill().getId() == skillId && e.getEffectType() == type) {
                return e;
            }
        }

        return null;
    }

    public Effect getEffectByStackType(final String type) {
        if (isEmpty()) {
            return null;
        }
        for (final Effect e : _effects) {
            if (type.equals(e.getStackType())) {
                return e;
            }
        }

        return null;
    }

    public int getStackOrder(final int skill_id) {
        if (isEmpty()) {
            return 0;
        }
        for (final Effect e : _effects) {
            if (e.getSkill().getId() == skill_id) {
                return e.getStackOrder();
            }
        }
        return 0;
    }

    public boolean containEffectFromSkills(final int skillId) {
        if (isEmpty()) {
            return false;
        }

        int sId;
        for (final Effect e : _effects) {
            sId = e.getSkill().getId();
            if (skillId == sId) {
                return true;
            }
        }

        return false;
    }

    public boolean containEffectFromSkills(final int[] skillIds) {
        if (isEmpty()) {
            return false;
        }

        int skillId;
        for (final Effect e : _effects) {
            skillId = e.getSkill().getId();
            if (ArrayUtils.contains(skillIds, skillId)) {
                return true;
            }
        }

        return false;
    }

    public List<Effect> getAllEffects() {
        if (_effects == null) {
            return Collections.emptyList();
        }
        return _effects;
    }

    public boolean isEmpty() {
        return _effects == null || _effects.isEmpty();
    }

    /**
     * Возвращает первые эффекты для всех скиллов. Нужно для отображения не
     * более чем 1 иконки для каждого скилла.
     */
    public Effect[] getAllFirstEffects() {
        if (isEmpty()) {
            return Effect.EMPTY_EFFECT_ARRAY;
        }

        final TIntObjectHashMap<Effect> map = new TIntObjectHashMap<>();

        for (final Effect e : _effects) {
            map.put(e.getSkill().getId(), e);
        }

        return map.values(new Effect[map.size()]);
    }

    private void checkSlotLimit(final Effect newEffect) {
        if (_effects == null) {
            return;
        }

        final int slotType = getSlotType(newEffect);
        if (slotType == NONE_SLOT_TYPE) {
            return;
        }

        int size = 0;
        final TIntArrayList skillIds = new TIntArrayList();
        for (final Effect e : _effects) {
            if (e.isInUse()) {
                if (e.getSkill() == newEffect.getSkill()) // мы уже имеем эффект от этого скилла
                {
                    return;
                }

                if (!skillIds.contains(e.getSkill().getId())) {
                    final int subType = getSlotType(e);
                    if (subType == slotType) {
                        size++;
                        skillIds.add(e.getSkill().getId());
                    }
                }
            }
        }

        int limit = 0;
        switch (slotType) {
            case BUFF_SLOT_TYPE:
                limit = _actor.getBuffLimit();
                break;
            case MUSIC_SLOT_TYPE:
                limit = AllSettingsConfig.ALT_DANCE_SONG_LIMIT;
                break;
            case DEBUFF_SLOT_TYPE:
                limit = DEBUFF_LIMIT;
                break;
            case TRIGGER_SLOT_TYPE:
                limit = TRIGGER_LIMIT;
                break;
        }

        if (size < limit) {
            return;
        }

        int skillId = 0;
        for (final Effect e : _effects) {
            if (e.isInUse()) {
                if (getSlotType(e) == slotType) {
                    skillId = e.getSkill().getId();
                    break;
                }
            }
        }

        if (skillId != 0) {
            stopEffect(skillId);
        }
    }

    public void addEffect(final Effect effect) {
        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
        final double hp = _actor.getCurrentHp();
        final double mp = _actor.getCurrentMp();
        final double cp = _actor.getCurrentCp();

        final String stackType = effect.getStackType();

        lock();
        try {
            if (_effects == null) {
                _effects = new CopyOnWriteArrayList<>();
            }

            if (stackType == EffectTemplate.NO_STACK)
            // Удаляем такие же эффекты
            {
                for (final Effect e : _effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (e.getStackType() == EffectTemplate.NO_STACK && e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() == effect.getEffectType())
                    // Если оставшаяся длительность старого эффекта больше чем длительность нового, то оставляем старый.
                    {
                        if (effect.getTimeLeft() > e.getTimeLeft()) {
                            e.exit();
                        } else {
                            return;
                        }
                    }
                }
            } else
            // Проверяем, нужно ли накладывать эффект, при совпадении StackType.
            // Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
            // Если условия подходят - удаляем старый.
            {
                for (final Effect e : _effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (!checkStackType(e.getTemplate(), effect.getTemplate())) {
                        continue;
                    }

                    if (e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() != effect.getEffectType()) {
                        break;
                    }

                    // Эффекты со StackOrder == -1 заменить нельзя (например, Root).
                    if (e.getStackOrder() == -1) {
                        return;
                    }

                    if (!e.maybeScheduleNext(effect)) {
                        return;
                    }
                }
            }

            // Проверяем на лимиты бафов/дебафов
            checkSlotLimit(effect);

            // Добавляем новый эффект
            if (!_effects.add(effect)) {
                return;
            }

            effect.setInUse(true);
        } finally {
            unlock();
        }

        // Запускаем эффект
        effect.start();

        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
        if (effect.refreshHpOnAdd()) {
            _actor.setCurrentHp(hp, false);
        }
        if (effect.refreshMpOnAdd()) {
            _actor.setCurrentMp(mp);
        }
        if (effect.refreshCpOnAdd()) {
            _actor.setCurrentCp(cp);
        }

        // Обновляем иконки
        _actor.updateStats();
        _actor.updateEffectIcons();
    }

    /**
     * Удаление эффекта из списка
     *
     * @param effect эффект для удаления
     */
    public void removeEffect(final Effect effect) {
        if (effect == null) {
            return;
        }

        lock();
        try {
            if (_effects == null) {
                return;
            }

            if (!_effects.remove(effect)) {
                return;
            }
        } finally {
            unlock();
        }

        _actor.updateStats();
        _actor.updateEffectIcons();
    }

    public void stopAllEffects() {
        if (isEmpty()) {
            return;
        }

        for (final Effect e : _effects) {
            e.exit();
        }

        _actor.updateStats();
        _actor.updateEffectIcons();
    }

    public void stopEffect(final int skillId) {
        if (isEmpty()) {
            return;
        }

        _effects.stream().filter(e -> e.getSkill().getId() == skillId).forEach(Effect::exit);
    }

    public void stopEffect(final SkillEntry skill) {
        if (skill != null) {
            stopEffect(skill.getId());
        }
    }

    public void stopEffectByDisplayId(final int skillId) {
        if (isEmpty()) {
            return;
        }

        _effects.stream().filter(e -> e.getSkill().getDisplayId() == skillId).forEach(Effect::exit);
    }

    public void stopEffects(final EffectType type) {
        if (isEmpty()) {
            return;
        }

        _effects.stream().filter(e -> e.getEffectType() == type).forEach(Effect::exit);
    }

    /**
     * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все эффекты (не только указанный).
     */
    public void stopAllSkillEffects(final EffectType type) {
        if (isEmpty()) {
            return;
        }

        _effects.stream().filter(e -> e.getEffectType() == type).forEach(e -> {
            stopEffect(e.getSkill());
        });
    }

    public Optional<Effect> getEffect(Predicate<Effect> predicate) {
        if (isEmpty())
            return Optional.empty();

        return _effects.stream().filter(predicate).findFirst();
    }
}