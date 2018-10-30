package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.HeroDiary;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class BossInstance extends RaidBossInstance {
    private boolean _teleportedToNest;


    /**
     * Constructor<?> for L2BossInstance. This represent all grandbosses:
     * <ul>
     * <li>29001	Queen Ant</li>
     * <li>29014	Orfen</li>
     * <li>29019	Antharas</li>
     * <li>29020	Baium</li>
     * <li>29022	Zaken</li>
     * <li>29028	Valakas</li>
     * <li>29006	Core</li>
     * </ul>
     * <br>
     * <b>For now it's nothing more than a L2Monster but there'll be a scripting<br>
     * engine for AI soon and we could add special behaviour for those boss</b><br>
     * <br>
     *
     * @param objectId ID of the instance
     * @param template L2NpcTemplate of the instance
     */
    public BossInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    protected int getMinChannelSizeForLock() {
        return 0;
    }

    @Override
    public final boolean isMovementDisabled() {
        // Core should stay anyway
        return getNpcId() == 29006 || super.isMovementDisabled();
    }

    @Override
    protected void onDeath(final Creature killer) {
        if (killer.isPlayable()) {
            Player player = killer.getPlayer();
            if (player.isInParty()) {
                for (Player member : player.getParty().getPartyMembers()) {
                    if (member.isNoble()) {
                        Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
                    }
                }
            } else if (player.isNoble()) {
                Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
            }
        }
        super.onDeath(killer);
    }

    public boolean isTeleported() {
        return _teleportedToNest;
    }

    /**
     * Used by Orfen to set 'teleported' flag, when hp goes to <50%
     *
     * @param flag
     */
    public void setTeleported(final boolean flag) {
        _teleportedToNest = flag;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
}
