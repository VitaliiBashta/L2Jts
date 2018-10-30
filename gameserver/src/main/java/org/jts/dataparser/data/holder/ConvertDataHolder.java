package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.convertdata.ConvertData;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 25.08.12 22:47
 */
public class ConvertDataHolder extends AbstractHolder {
    private static ConvertDataHolder ourInstance = new ConvertDataHolder();
    @Element(start = "convert_begin", end = "convert_end")
    public List<ConvertData> convertDatas;

    private ConvertDataHolder() {
    }

    public static ConvertDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return convertDatas.size();
    }

    public List<ConvertData> getConvertDatas() {
        return convertDatas;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}