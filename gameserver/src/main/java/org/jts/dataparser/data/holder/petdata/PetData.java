package org.jts.dataparser.data.holder.petdata;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author KilRoy
 */
public class PetData {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetData.class);
    @IntValue
    private int pet_index; // Индекс ИД пета
    @StringValue
    private String npc_name; // NPC Template используемый петом
    @StringValue
    private String item; // Итем, контролирующий пета
    @IntValue
    private int sync_level; // При вызове, синхронизация уровня с овнером
    @IntArray
    private int[] evolve; // уровни, на которых пет эволюционирует
    @Element(start = "stat_begin", end = "stat_end")
    private List<PetLevelStat> petLevelStat;

    public int getPetIndex() {
        return pet_index;
    }

    public String getPetName() {
        return npc_name;
    }

    public String getControlItem() {
        return item;
    }

    public boolean isSyncLevel() {
        return sync_level == 1;
    }

    public int[] getPetEvolve() {
        return evolve;
    }

    public List<PetLevelStat> getLevelStat() {
        return petLevelStat;
    }

    public PetLevelStat getLevelStatForLevel(int lvl) {
        final int level = lvl == 0 ? ++lvl : lvl;
        final PetLevelStat petLvlStat = petLevelStat.stream().filter(p -> p.getLevel() == level).findFirst().orElse(null);
        if (petLvlStat == null) {
            LOGGER.error("getLevelStatForLevel(int) returned null from level: " + level + " for npcTemplate: " + npc_name);
        }
        return petLvlStat;
    }

    public int getControlItemId() {
        return item != null && !item.isEmpty() ? LinkerFactory.getInstance().findClearValue(item) : -1;
    }
}