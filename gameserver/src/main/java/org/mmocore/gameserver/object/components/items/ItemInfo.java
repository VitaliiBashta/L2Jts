package org.mmocore.gameserver.object.components.items;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.templates.item.ItemTemplate;

public class ItemInfo {
    private int ownerId;
    private int lastChange;
    private int type1;
    private int objectId;
    private int itemId;
    private long count;
    private int type2;
    private int customType1;
    private boolean isEquipped;
    private int bodyPart;
    private int enchantLevel;
    private int customType2;
    private int _variationStoneId;
    private int _variation1Id;
    private int _variation2Id;
    private int shadowLifeTime;
    private int attackElement = Element.NONE.getId();
    private int attackElementValue;
    private int defenceFire;
    private int defenceWater;
    private int defenceWind;
    private int defenceEarth;
    private int defenceHoly;
    private int defenceUnholy;
    private int equipSlot;
    private int temporalLifeTime;
    private int[] enchantOptions = ItemInstance.EMPTY_ENCHANT_OPTIONS;

    public ItemInfo() {
    }

    public ItemInfo(ItemInstance item) {
        setOwnerId(item.getOwnerId());
        setObjectId(item.getObjectId());
        setItemId(item.getItemId());
        setCount(item.getCount());
        setCustomType1(item.getCustomType1());
        setEquipped(item.isEquipped());
        setEnchantLevel(item.getEnchantLevel());
        setCustomType2(item.getCustomType2());
        setVariationStoneId(item.getVariationStoneId());
        setVariation1Id(item.getVariation1Id());
        setVariation2Id(item.getVariation2Id());
        setShadowLifeTime(item.getShadowLifeTime());
        setAttackElement(item.getAttackElement().getId());
        setAttackElementValue(item.getAttackElementValue());
        setDefenceFire(item.getDefenceFire());
        setDefenceWater(item.getDefenceWater());
        setDefenceWind(item.getDefenceWind());
        setDefenceEarth(item.getDefenceEarth());
        setDefenceHoly(item.getDefenceHoly());
        setDefenceUnholy(item.getDefenceUnholy());
        setEquipSlot(item.getEquipSlot());
        setTemporalLifeTime(item.getTemporalLifeTime());
        setEnchantOptions(item.getEnchantOptions());
    }

    public ItemTemplate getItem() {
        return ItemTemplateHolder.getInstance().getTemplate(getItemId());
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getLastChange() {
        return lastChange;
    }

    public void setLastChange(int lastChange) {
        this.lastChange = lastChange;
    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
        if (itemId > 0) {
            setType1(getItem().getType1());
            setType2(getItem().getType2ForPackets());
            setBodyPart(getItem().getBodyPart());
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public int getCustomType1() {
        return customType1;
    }

    public void setCustomType1(int customType1) {
        this.customType1 = customType1;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public int getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(int bodyPart) {
        this.bodyPart = bodyPart;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getShadowLifeTime() {
        return shadowLifeTime;
    }

    public void setShadowLifeTime(int shadowLifeTime) {
        this.shadowLifeTime = shadowLifeTime;
    }

    public int getCustomType2() {
        return customType2;
    }

    public void setCustomType2(int customType2) {
        this.customType2 = customType2;
    }

    public int getAttackElement() {
        return attackElement;
    }

    public void setAttackElement(int attackElement) {
        this.attackElement = attackElement;
    }

    public int getAttackElementValue() {
        return attackElementValue;
    }

    public void setAttackElementValue(int attackElementValue) {
        this.attackElementValue = attackElementValue;
    }

    public int getDefenceFire() {
        return defenceFire;
    }

    public void setDefenceFire(int defenceFire) {
        this.defenceFire = defenceFire;
    }

    public int getDefenceWater() {
        return defenceWater;
    }

    public void setDefenceWater(int defenceWater) {
        this.defenceWater = defenceWater;
    }

    public int getDefenceWind() {
        return defenceWind;
    }

    public void setDefenceWind(int defenceWind) {
        this.defenceWind = defenceWind;
    }

    public int getDefenceEarth() {
        return defenceEarth;
    }

    public void setDefenceEarth(int defenceEarth) {
        this.defenceEarth = defenceEarth;
    }

    public int getDefenceHoly() {
        return defenceHoly;
    }

    public void setDefenceHoly(int defenceHoly) {
        this.defenceHoly = defenceHoly;
    }

    public int getDefenceUnholy() {
        return defenceUnholy;
    }

    public void setDefenceUnholy(int defenceUnholy) {
        this.defenceUnholy = defenceUnholy;
    }

    public int getEquipSlot() {
        return equipSlot;
    }

    public void setEquipSlot(int equipSlot) {
        this.equipSlot = equipSlot;
    }

    public int getTemporalLifeTime() {
        return temporalLifeTime;
    }

    public void setTemporalLifeTime(int temporalLifeTime) {
        this.temporalLifeTime = temporalLifeTime;
    }

    public int getVariationStoneId() {
        return _variationStoneId;
    }

    public void setVariationStoneId(int val) {
        this._variationStoneId = val;
    }

    public int getVariation1Id() {
        return _variation1Id;
    }

    public void setVariation1Id(int val) {
        this._variation1Id = val;
    }

    public int getVariation2Id() {
        return _variation2Id;
    }

    public void setVariation2Id(int val) {
        this._variation2Id = val;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (getObjectId() == 0) {
            return getItemId() == ((ItemInfo) obj).getItemId();
        }
        return getObjectId() == ((ItemInfo) obj).getObjectId();
    }

    @Override
    public int hashCode() {
        int result = objectId;
        result = 31 * result + itemId;
        return result;
    }

    public int[] getEnchantOptions() {
        return enchantOptions;
    }

    public void setEnchantOptions(int[] enchantOptions) {
        this.enchantOptions = enchantOptions;
    }
}
