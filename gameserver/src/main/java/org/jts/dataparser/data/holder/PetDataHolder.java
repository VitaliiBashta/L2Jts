package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.petdata.PetData;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.commons.data.AbstractHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author KilRoy
 */
public class PetDataHolder extends AbstractHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetDataHolder.class);
    private static PetDataHolder INSTANCE = new PetDataHolder();
    @Element(start = "pet_stat_begin", end = "pet_stat_end")
    private List<PetData> petData;

    private PetDataHolder() {
    }

    public static PetDataHolder getInstance() {
        return INSTANCE;
    }

    public List<PetData> getPetData() {
        return petData;
    }

    public PetData getPetData(final int npcTemplateId) {
        final int templateId = 1000000 + npcTemplateId;
        PetData pet = null;
        for (final PetData pets : petData) {
            if (LinkerFactory.getInstance().findClearValue(pets.getPetName()) == templateId) {
                return pets;
            }
        }
        LOGGER.error("getPetData() returned null from templateId: " + npcTemplateId);
        return pet;
    }

    public int getPetTemplateId(final int controlItemId) {
        for (final PetData pets : petData) {
            if (pets.getControlItemId() == controlItemId) {
                return LinkerFactory.getInstance().findClearValue(pets.getPetName()) - 1000000;
            }
        }
        return 0;
    }

    public int[] getPetControlItems() {
        final int[] items = new int[petData.size() - 2];
        int i = 0;
        for (final PetData pets : petData) {
            if (pets.getControlItemId() > 1) {
                items[i++] = pets.getControlItemId();
            }
        }
        return items;
    }

    public boolean isPetControlItem(final int itemId) {
        for (final PetData pets : petData) {
            if (pets.getControlItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return petData.size();
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}