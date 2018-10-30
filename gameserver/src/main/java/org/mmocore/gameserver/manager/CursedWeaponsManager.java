package org.mmocore.gameserver.manager;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.CursedWeaponDataHolder;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.CursedWeaponsDAO;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.etcitems.CursedWeapon;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public class CursedWeaponsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CursedWeaponsManager.class);

    private static final CursedWeapon[] EMPTY_CURSED_WEAPONS = new CursedWeapon[0];

    private static final CursedWeaponsManager INSTANCE = new CursedWeaponsManager();
    private static final int CURSEDWEAPONS_MAINTENANCE_INTERVAL = 5 * 60 * 1000; // 5 min in millisec
    private final TIntObjectHashMap<CursedWeapon> _cursedWeaponsMap;
    private CursedWeapon[] _cursedWeapons;
    private ScheduledFuture<?> _removeTask;

    private CursedWeaponsManager() {
        _cursedWeaponsMap = new TIntObjectHashMap<>();
        _cursedWeapons = EMPTY_CURSED_WEAPONS;

        if (!ServerConfig.ALLOW_CURSED_WEAPONS) {
            return;
        }

        load();
        cancelTask();

        LOGGER.info("CursedWeaponsManager: Loaded " + _cursedWeapons.length + " cursed weapon(s).");
        ThreadPoolManager.getInstance().schedule(() -> {
            CursedWeaponsDAO.getInstance().restore();
            CursedWeaponsDAO.getInstance().checkConditions();
        }, 15000);
    }

    public static CursedWeaponsManager getInstance() {
        return INSTANCE;
    }

    private void load() {
        for (final org.jts.dataparser.data.holder.cursedweapondata.CursedWeapon cw : CursedWeaponDataHolder.getInstance().getCursedWeapons()) {
            final int itemId = LinkerFactory.getInstance().findClearValue(cw.item_name);
            final ItemTemplate itemTemplate = ItemTemplateHolder.getInstance().getTemplate(itemId);
            if (itemTemplate == null) {
                LOGGER.warn("CursedWeaponsManager: itemTemplate null, id = " + itemId);
                continue;
            }
            final SkillEntry skillEntry = itemTemplate.getAttachedSkills()[0];
            if (skillEntry == null) {
                LOGGER.warn("CursedWeaponsManager: itemTemplate skill null");
                continue;
            }
            final int skillId = skillEntry.getId();
            final int skillMaxLevel = SkillTable.getInstance().getMaxLevel(skillId);
            final CursedWeapon cws = new CursedWeapon(itemId, cw.item_name, skillId, skillMaxLevel);
            cws.setDropRate(cw.create_rate_per_npc * 10000);
            cws.setDurationMin(cw.expire_by_nonpk / 100);
            cws.setDurationMax(cw.life_time / 100);
            cws.setDurationLost(cw.expire_by_drop / 60);
            cws.setDisapearChance(cw.drop_rate_ondying / 10);
            cws.setStageKills(10);
            cws.setTransformationId(cw.transform_id);
            //cws.setTransformationName(cw.item_name);

            _cursedWeaponsMap.put(itemId, cws);
        }
        _cursedWeapons = _cursedWeaponsMap.values(new CursedWeapon[_cursedWeaponsMap.size()]);
    }

    private void cancelTask() {
        if (_removeTask != null) {
            _removeTask.cancel(false);
            _removeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RemoveTask(), CURSEDWEAPONS_MAINTENANCE_INTERVAL, CURSEDWEAPONS_MAINTENANCE_INTERVAL);
        }
    }

    public void saveData() {
        for (final CursedWeapon cw : _cursedWeapons) {
            CursedWeaponsDAO.getInstance().saveData(cw);
        }
    }

    /**
     * вызывается, когда проклятое оружие оказывается в инвентаре игрока
     */
    public void checkPlayer(final Player player, final ItemInstance item) {
        if (player == null || item == null || player.isInOlympiadMode()) {
            return;
        }

        final CursedWeapon cw = _cursedWeaponsMap.get(item.getItemId());
        if (cw == null) {
            return;
        }

        if (player.getObjectId() == cw.getPlayerId() || cw.getPlayerId() == 0 || cw.isDropped()) {
            activate(player, item);
            showUsageTime(player, cw);
        } else {
            // wtf? how you get it?
            LOGGER.warn("CursedWeaponsManager: " + player + " tried to obtain " + item + " in wrong way");
            player.getInventory().destroyItem(item, item.getCount());
        }
    }

    public void activate(final Player player, final ItemInstance item) {
        if (player == null || player.isInOlympiadMode()) {
            return;
        }
        final CursedWeapon cw = _cursedWeaponsMap.get(item.getItemId());
        if (cw == null) {
            return;
        }

        if (player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
        {
            if (player.getCursedWeaponEquippedId() != item.getItemId()) {
                final CursedWeapon cw2 = _cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
                cw2.setNbKills(cw2.getStageKills() - 1);
                cw2.increaseKills();
            }

            // erase the newly obtained cursed weapon
            CursedWeaponsDAO.getInstance().endOfLife(cw);
            player.getInventory().destroyItem(item, 1);
        } else if (cw.getTimeLeft() > 0) {
            cw.activate(player, item);
            CursedWeaponsDAO.getInstance().saveData(cw);
            announce(new SystemMessage(SystemMsg.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addItemName(cw.getItemId()));
        } else {
            CursedWeaponsDAO.getInstance().endOfLife(cw);
            player.getInventory().destroyItem(item, 1);
        }
    }

    public void doLogout(final Player player) {
        for (final CursedWeapon cw : _cursedWeapons) {
            if (player.getInventory().getItemByItemId(cw.getItemId()) != null) {
                cw.setPlayer(null);
                cw.setItem(null);
            }
        }
    }

    /**
     * drop from L2NpcInstance killed by L2Player
     */
    public void dropAttackable(final NpcInstance attackable, final Player killer) {
        if (killer.isInOlympiadMode() || killer.isCursedWeaponEquipped() || _cursedWeapons.length == 0 || !killer.getReflection().equals(ReflectionManager.DEFAULT)) {
            return;
        }

        synchronized (_cursedWeapons) {
            CursedWeapon[] cursedWeapons = EMPTY_CURSED_WEAPONS;
            for (final CursedWeapon cw : _cursedWeapons) {
                if (cw.isActive()) {
                    continue;
                }
                cursedWeapons = ArrayUtils.add(cursedWeapons, cw);
            }

            if (cursedWeapons.length > 0) {
                final CursedWeapon cw = cursedWeapons[Rnd.get(cursedWeapons.length)];
                if (Rnd.get(100000000) <= cw.getDropRate()) {
                    cw.create(attackable, killer);
                }
            }
        }
    }

    /**
     * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
     * Вызывается при смерти игрока.
     */
    public void dropPlayer(final Player player) {
        final CursedWeapon cw = _cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
        if (cw == null) {
            return;
        }

        if (cw.dropIt(null, null, player)) {
            CursedWeaponsDAO.getInstance().saveData(cw);
            announce(new SystemMessage(SystemMsg.S2_WAS_DROPPED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addItemName(cw.getItemId()));
        } else {
            CursedWeaponsDAO.getInstance().endOfLife(cw);
        }
    }

    public void increaseKills(final int itemId) {
        final CursedWeapon cw = _cursedWeaponsMap.get(itemId);
        if (cw != null) {
            cw.increaseKills();
            CursedWeaponsDAO.getInstance().saveData(cw);
        }
    }

    public int getLevel(final int itemId) {
        final CursedWeapon cw = _cursedWeaponsMap.get(itemId);
        return cw != null ? cw.getLevel() : 0;
    }

    public void announce(final SystemMessage sm) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(sm);
        }
    }

    public void showUsageTime(final Player player, final int itemId) {
        final CursedWeapon cw = _cursedWeaponsMap.get(itemId);
        if (cw != null) {
            showUsageTime(player, cw);
        }
    }

    public void showUsageTime(final Player player, final CursedWeapon cw) {
        final SystemMessage sm = new SystemMessage(SystemMsg.S1_HAS_S2_MINUTES_OF_USAGE_TIME_REMAINING);
        sm.addItemName(cw.getItemId());
        sm.addNumber(cw.getTimeLeft() / 60000);
        player.sendPacket(sm);
    }

    public boolean isCursed(final int itemId) {
        return _cursedWeaponsMap.containsKey(itemId);
    }

    public CursedWeapon[] getCursedWeapons() {
        return _cursedWeapons;
    }

    public int[] getCursedWeaponsIds() {
        return _cursedWeaponsMap.keys();
    }

    public CursedWeapon getCursedWeapon(final int itemId) {
        return _cursedWeaponsMap.get(itemId);
    }

    private class RemoveTask extends RunnableImpl {
        @Override
        public void runImpl() {
            for (final CursedWeapon cw : _cursedWeapons) {
                if (cw.isActive() && cw.getTimeLeft() <= 0) {
                    CursedWeaponsDAO.getInstance().endOfLife(cw);
                }
            }
        }
    }
}