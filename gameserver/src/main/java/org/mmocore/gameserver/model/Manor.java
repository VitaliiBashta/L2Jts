package org.mmocore.gameserver.model;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: перенести на хмл
 */
public class Manor {
    private static final Logger _log = LoggerFactory.getLogger(Manor.class);

    private static Map<Integer, SeedData> _seeds;

    public Manor() {
        _seeds = new ConcurrentHashMap<>();
        parseData();
    }

    public static Manor getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<Integer> getAllCrops() {
        final List<Integer> crops = new ArrayList<>();
        for (final SeedData seed : _seeds.values()) {
            if (!crops.contains(seed.getCrop()) && seed.getCrop() != 0 && !crops.contains(seed.getCrop())) {
                crops.add(seed.getCrop());
            }
        }
        return crops;
    }

    public Map<Integer, SeedData> getAllSeeds() {
        return _seeds;
    }

    public int getSeedBasicPrice(final int seedId) {
        final ItemTemplate seedItem = ItemTemplateHolder.getInstance().getTemplate(seedId);
        if (seedItem != null) {
            return seedItem.getReferencePrice();
        }
        return 0;
    }

    public int getSeedBasicPriceByCrop(final int cropId) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return getSeedBasicPrice(seed.getId());
            }
        }
        return 0;
    }

    public int getCropBasicPrice(final int cropId) {
        final ItemTemplate cropItem = ItemTemplateHolder.getInstance().getTemplate(cropId);
        if (cropItem != null) {
            return cropItem.getReferencePrice();
        }
        return 0;
    }

    public int getMatureCrop(final int cropId) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return seed.getMature();
            }
        }
        return 0;
    }

    /**
     * Returns price which lord pays to buy one seed
     *
     * @param seedId
     * @return seed price
     */
    public long getSeedBuyPrice(final int seedId) {
        final long buyPrice = getSeedBasicPrice(seedId) / 10;
        return buyPrice >= 0 ? buyPrice : 1;
    }

    public int getSeedMinLevel(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getLevel() - 5;
        }
        return -1;
    }

    public int getSeedMaxLevel(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getLevel() + 5;
        }
        return -1;
    }

    public int getSeedLevelByCrop(final int cropId) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return seed.getLevel();
            }
        }
        return 0;
    }

    public int getSeedLevel(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getLevel();
        }
        return -1;
    }

    public boolean isAlternative(final int seedId) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getId() == seedId) {
                return seed.isAlternative();
            }
        }
        return false;
    }

    public int getCropType(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getCrop();
        }
        return -1;
    }

    public int getRewardItem(final int cropId, final int type) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return seed.getReward(type); // there can be several
            }
        }
        // seeds with same crop, but
        // reward should be the same for
        // all
        return -1;
    }

    public long getRewardAmountPerCrop(final int castle, final int cropId, final int type) {
        final CropProcure cs = ResidenceHolder.getInstance().getResidence(Castle.class, castle).getCropProcure(CastleManorManager.PERIOD_CURRENT).get(cropId);
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return cs.getPrice() / getCropBasicPrice(seed.getReward(type));
            }
        }
        return -1;
    }

    public int getRewardItemBySeed(final int seedId, final int type) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getReward(type);
        }
        return 0;
    }

    /**
     * Return all crops which can be purchased by given castle
     *
     * @param castleId
     * @return
     */
    public List<Integer> getCropsForCastle(final int castleId) {
        final List<Integer> crops = new ArrayList<>();
        for (final SeedData seed : _seeds.values()) {
            if (seed.getManorId() == castleId && !crops.contains(seed.getCrop())) {
                crops.add(seed.getCrop());
            }
        }
        return crops;
    }

    /**
     * Return list of seed ids, which belongs to castle with given id
     *
     * @param castleId - id of the castle
     * @return seedIds - list of seed ids
     */
    public List<SeedData> getSeedsForCastle(int castleId) {
        final List<SeedData> seeds = new ArrayList<SeedData>(20);
        for (SeedData seed : _seeds.values()) {
            if (seed.getManorId() == castleId) {
                seeds.add(seed);
            }
        }
        return seeds;
    }

    /**
     * Returns castle id where seed can be sowned<br>
     *
     * @param seedId
     * @return castleId
     */
    public int getCastleIdForSeed(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getManorId();
        }
        return 0;
    }

    public long getSeedSaleLimit(final int seedId) {
        final SeedData seed = _seeds.get(seedId);
        if (seed != null) {
            return seed.getSeedLimit();
        }
        return 0;
    }

    public long getCropPuchaseLimit(final int cropId) {
        for (final SeedData seed : _seeds.values()) {
            if (seed.getCrop() == cropId) {
                return seed.getCropLimit();
            }
        }
        return 0;
    }

    private void parseData() {
        LineNumberReader lnr = null;
        try {
            final File seedData = new File(ServerConfig.DATAPACK_ROOT, "data/seeds.csv");
            lnr = new LineNumberReader(new BufferedReader(new FileReader(seedData)));

            String line = null;
            while ((line = lnr.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                final SeedData seed = parseList(line);
                _seeds.put(seed.getId(), seed);
            }

            _log.info("ManorManager: Loaded " + _seeds.size() + " seeds");
        } catch (FileNotFoundException e) {
            _log.info("seeds.csv is missing in data folder");
        } catch (Exception e) {
            _log.error("Error while loading seeds!", e);
        } finally {
            try {
                if (lnr != null) {
                    lnr.close();
                }
            } catch (Exception e1) {
            }
        }
    }

    private SeedData parseList(final String line) {
        final StringTokenizer st = new StringTokenizer(line, ";");

        final int seedId = Integer.parseInt(st.nextToken()); // seed id
        final int level = Integer.parseInt(st.nextToken()); // seed level
        final int cropId = Integer.parseInt(st.nextToken()); // crop id
        final int matureId = Integer.parseInt(st.nextToken()); // mature crop id
        final int type1R = Integer.parseInt(st.nextToken()); // type I reward
        final int type2R = Integer.parseInt(st.nextToken()); // type II reward
        final int manorId = Integer.parseInt(st.nextToken()); // id of manor, where seed can be farmed
        final int isAlt = Integer.parseInt(st.nextToken()); // alternative seed
        final long limitSeeds = Integer.parseInt(st.nextToken()); // limit for seeds
        final long limitCrops = Integer.parseInt(st.nextToken()); // limit for crops

        final SeedData seed = new SeedData(level, cropId, matureId);
        seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);

        return seed;
    }

    public static class SeedData {
        private final int _level; // seed level
        private final int _crop; // crop type
        private final int _mature; // mature crop type
        private int _id;
        private int _type1;
        private int _type2;
        private int _manorId; // id of manor (castle id) where seed can be farmed
        private int _isAlternative;
        private long _limitSeeds;
        private long _limitCrops;

        public SeedData(final int level, final int crop, final int mature) {
            _level = level;
            _crop = crop;
            _mature = mature;
        }

        public void setData(final int id, final int t1, final int t2, final int manorId, final int isAlt, final long lim1, final long lim2) {
            _id = id;
            _type1 = t1;
            _type2 = t2;
            _manorId = manorId;
            _isAlternative = isAlt;
            _limitSeeds = lim1;
            _limitCrops = lim2;
        }

        public int getManorId() {
            return _manorId;
        }

        public int getId() {
            return _id;
        }

        public int getCrop() {
            return _crop;
        }

        public int getMature() {
            return _mature;
        }

        public int getReward(final int type) {
            return type == 1 ? _type1 : _type2;
        }

        public int getLevel() {
            return _level;
        }

        public boolean isAlternative() {
            return _isAlternative == 1;
        }

        public long getSeedLimit() {
            return _limitSeeds;
        }

        public long getCropLimit() {
            return _limitCrops;
        }
    }

    private static class LazyHolder {
        private static final Manor INSTANCE = new Manor();
    }
}
