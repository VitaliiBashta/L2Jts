package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.model.instances.SpecialMonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class SquashInstance extends SpecialMonsterInstance {
    public final static int Young_Squash = 12774;
    public final static int High_Quality_Squash = 12775;
    public final static int Low_Quality_Squash = 12776;
    public final static int Large_Young_Squash = 12777;
    public final static int High_Quality_Large_Squash = 12778;
    public final static int Low_Quality_Large_Squash = 12779;
    public final static int King_Squash = 13016;
    public final static int Emperor_Squash = 13017;

    private HardReference<Player> _spawnerRef;

    public SquashInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public Player getSpawner() {
        return _spawnerRef.get();
    }

    public void setSpawner(Player spawner) {
        _spawnerRef = spawner.getRef();
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect,
                                boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        if (attacker.getActiveWeaponInstance() == null) {
            return;
        }

        int weaponId = attacker.getActiveWeaponInstance().getItemId();

        if (getNpcId() == Low_Quality_Large_Squash || getNpcId() == High_Quality_Large_Squash || getNpcId() == Emperor_Squash)
        // Разрешенное оружие для больших тыкв:
        // 4202 Chrono Cithara
        // 5133 Chrono Unitus
        // 5817 Chrono Campana
        // 7058 Chrono Darbuka
        // 8350 Chrono Maracas
        {
            if (weaponId != 4202 && weaponId != 5133 && weaponId != 5817 && weaponId != 7058 && weaponId != 8350) {
                return;
            }
        }

        i = 1;

        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    @Override
    public long getRegenTick() {
        return 0L;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}