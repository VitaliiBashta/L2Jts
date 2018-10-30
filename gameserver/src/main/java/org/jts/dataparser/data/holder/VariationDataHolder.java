package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.variationdata.VariationData;
import org.jts.dataparser.data.holder.variationdata.VariationFeeData;
import org.jts.dataparser.data.holder.variationdata.VariationItemData;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Mangol
 */
public class VariationDataHolder extends AbstractHolder {
    private static final VariationDataHolder ourInstance = new VariationDataHolder();
    @Element(start = "variation_begin", end = "variation_end")
    private List<VariationData> variation_data;
    @Element(start = "item_group_begin", end = "item_group_end")
    private List<VariationItemData> item_group;
    @Element(start = "fee_begin", end = "fee_end")
    private List<VariationFeeData> fee_data;

    public static VariationDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return variation_data.size() + item_group.size() + fee_data.size();
    }

    public List<VariationItemData> getVariationItemGroup() {
        return item_group;
    }

    public List<VariationData> getVariationData() {
        return variation_data;
    }

    public List<VariationFeeData> getVariationFreeData() {
        return fee_data;
    }

    @Override
    public void clear() {

    }
}
