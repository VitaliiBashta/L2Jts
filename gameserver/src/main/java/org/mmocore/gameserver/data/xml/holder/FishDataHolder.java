package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.item.support.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @date 8:31/19.07.2011
 */
public class FishDataHolder extends AbstractHolder {
    private static final FishDataHolder INSTANCE = new FishDataHolder();
    private final List<FishTemplate> fishes = new ArrayList<>();
    private final TIntObjectMap<LureTemplate> lures = new TIntObjectHashMap<>();
    private final TIntObjectMap<Map<LureType, Map<FishGroup, Integer>>> distributionsForZones = new TIntObjectHashMap<>();

    public static FishDataHolder getInstance() {
        return INSTANCE;
    }

    public void addFish(final FishTemplate fishTemplate) {
        fishes.add(fishTemplate);
    }

    public void addLure(final LureTemplate template) {
        lures.put(template.getItemId(), template);
    }

    public void addDistribution(final int id, final LureType lureType, final Map<FishGroup, Integer> map) {
        Map<LureType, Map<FishGroup, Integer>> byLureType = distributionsForZones.get(id);
        if (byLureType == null) {
            distributionsForZones.put(id, byLureType = new EnumMap<>(LureType.class));
        }
        byLureType.put(lureType, map);
    }

    public List<FishTemplate> getFish(int lvl, FishGroup type, FishGrade grade) {
        List<FishTemplate> result = getAllFish().stream().filter(f -> f.getLevel() == lvl && f.getGroup() == type && f.getGrade() == grade).collect(Collectors.toList());
        if (result.isEmpty()) {
            _log.warn("Cant Find Any Fish!? - Lvl: " + lvl + " Type: " + type + " Grade: " + grade.toString());
        }
        return result;
    }

    public LureTemplate getLure(int id) {
        return lures.get(id);
    }

    public FishTemplate getFish(int id) {
        return fishes.get(id);
    }

    public Collection<FishTemplate> getAllFish() {
        return fishes;
    }

    @Override
    public void log() {
        info("load " + fishes.size() + " fish(es).");
        info("load " + lures.size() + " lure(s).");
        info("load " + distributionsForZones.size() + " distribution(s).");
    }

    @Override
    public void clear() {
        fishes.clear();
        lures.clear();
        distributionsForZones.clear();
    }
}
