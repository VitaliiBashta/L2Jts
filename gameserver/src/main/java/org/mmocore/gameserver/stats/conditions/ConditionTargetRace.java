package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.base.NpcRace;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetRace extends Condition {
    private final NpcRace _race;

    public ConditionTargetRace(final String race) {
        // Раса определяется уровнем(1-25) скила 4416
        if ("Undead".equalsIgnoreCase(race)) {
            _race = NpcRace.UNDEAD;
        } else if ("MagicCreatures".equalsIgnoreCase(race)) {
            _race = NpcRace.MAGIC_CREATURE;
        } else if ("Beasts".equalsIgnoreCase(race)) {
            _race = NpcRace.BEAST;
        } else if ("Animals".equalsIgnoreCase(race)) {
            _race = NpcRace.ANIMAL;
        } else if ("Plants".equalsIgnoreCase(race)) {
            _race = NpcRace.PLANT;
        } else if ("Humanoids".equalsIgnoreCase(race)) {
            _race = NpcRace.Humanoids;
        } else if ("Spirits".equalsIgnoreCase(race)) {
            _race = NpcRace.Spirits;
        } else if ("Angels".equalsIgnoreCase(race)) {
            _race = NpcRace.Angels;
        } else if ("Demons".equalsIgnoreCase(race)) {
            _race = NpcRace.Demons;
        } else if ("Dragons".equalsIgnoreCase(race)) {
            _race = NpcRace.DRAGON;
        } else if ("Giants".equalsIgnoreCase(race)) {
            _race = NpcRace.GIANT;
        } else if ("Bugs".equalsIgnoreCase(race)) {
            _race = NpcRace.BUG;
        } else if ("Fairies".equalsIgnoreCase(race)) {
            _race = NpcRace.Fairies;
        } else if ("Humans".equalsIgnoreCase(race)) {
            _race = NpcRace.Humans;
        } else if ("Elves".equalsIgnoreCase(race)) {
            _race = NpcRace.Elves;
        } else if ("DarkElves".equalsIgnoreCase(race)) {
            _race = NpcRace.DarkElves;
        } else if ("Orcs".equalsIgnoreCase(race)) {
            _race = NpcRace.Orcs;
        } else if ("Dwarves".equalsIgnoreCase(race)) {
            _race = NpcRace.Dwarves;
        } else if ("Others".equalsIgnoreCase(race)) {
            _race = NpcRace.Others;
        } else if ("NonLivingBeings".equalsIgnoreCase(race)) {
            _race = NpcRace.NonLivingBeings;
        } else if ("SiegeWeapons".equalsIgnoreCase(race)) {
            _race = NpcRace.SiegeWeapons;
        } else if ("DefendingArmy".equalsIgnoreCase(race)) {
            _race = NpcRace.DefendingArmy;
        } else if ("Mercenaries".equalsIgnoreCase(race)) {
            _race = NpcRace.Mercenaries;
        } else if ("UnknownCreature".equalsIgnoreCase(race)) {
            _race = NpcRace.UnknownCreature;
        } else if ("Kamael".equalsIgnoreCase(race)) {
            _race = NpcRace.Kamael;
        } else {
            throw new IllegalArgumentException("ConditionTargetRace: Invalid race name: " + race);
        }
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        return (target.isSummon() || target.isNpc()) && _race == ((NpcTemplate) target.getTemplate()).getRace();
    }
}