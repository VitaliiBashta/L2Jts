package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.holder.instantzonedata.ArmorData;
import org.jts.dataparser.data.holder.itemdata.*;
import org.jts.dataparser.data.holder.itemdata.item.ec_cond.*;
import org.jts.dataparser.data.holder.itemdata.item.use_cond.*;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Camelion
 * @date : 27.08.12 17:09
 */
public class ItemDataHolder extends AbstractHolder {
    private static ItemDataHolder ourInstance = new ItemDataHolder();
    @Element(start = "item_begin", end = "item_end", objectFactory = ItemDataObjectFactory.class)
    public List<ItemData> items;
    @Element(start = "set_begin", end = "set_end")
    public List<ItemSet> sets;

    private ItemDataHolder() {
    }

    public static ItemDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return items.size() + sets.size();
    }

    public List<ItemData> getItemDatas() {
        return items;
    }

    public List<ItemSet> getItemSets() {
        return sets;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    public static class ItemDataObjectFactory implements IObjectFactory<ItemData> {
        public static Pattern pattern = Pattern.compile("(\\S++)");

        @Override
        public ItemData createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            Matcher m = pattern.matcher(data);
            if (m.find()) {
                String type = m.group(1);
                switch (type) {
                    case "weapon":
                        return new WeaponData();
                    case "armor":
                        return new ArmorData();
                    case "etcitem":
                        return new EtcitemData();
                    case "asset":
                        return new AssetData();
                    case "accessary":
                        return new AccessaryData();
                    case "questitem":
                        return new QuestItemData();
                }
            }
            return null;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignore
        }
    }

    public static class UseCondObjectFactory implements IObjectFactory<DefaultUseCond> {
        @Override
        public DefaultUseCond createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            String[] parts = data.toString().split(";");
            data.delete(0, parts[0].length() + 1);
            switch (parts[0]) {
                case "uc_transmode_exclude":
                    return new UCTransmodeExclude();
                case "uc_transmode_include":
                    return new UCTransmodeInclude();
                case "uc_category":
                    return new UCCategory();
                case "uc_race":
                    return new UCRace();
                case "uc_requiredlevel":
                    return new UCRequiredLevel();
                case "uc_in_residence_siege_field":
                    return new UCInResidenceSiegeField();
                case "uc_inzone_num":
                    return new UCInzoneNum();
                case "uc_level":
                    return new UCLevel();
                case "uc_restart_point":
                    return new UCRestartPoint();
            }
            return null;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignore
        }
    }

    public static class EquipCondObjectFactory implements IObjectFactory<DefaultEquipCond> {
        @Override
        public DefaultEquipCond createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            String[] parts = data.toString().split(";");
            data.delete(0, parts[0].length() + 1);
            switch (parts[0]) {
                case "ec_race":
                    return new ECRace();
                case "ec_category":
                    return new ECCategory();
                case "ec_hero":
                    return new ECHero();
                case "ec_castle":
                    return new ECCastle();
                case "ec_castle_num":
                    return new ECCastleNum();
                case "ec_clan_leader":
                    return new ECClanLeader();
                case "ec_sex":
                    return new ECSex();
                case "ec_agit":
                    return new ECAgit();
                case "ec_agit_num":
                    return new ECAgitNum();
                case "ec_nobless":
                    return new ECNobless();
                case "ec_academy":
                    return new ECAcademy();
                case "ec_social_class":
                    return new ECSocialClass();
                case "ec_subjob":
                    return new ECSubjob();
                case "ec_requiredlevel":
                    return new ECRequiredLevel();
                case "ec_fortress":
                    return new ECFortress();
                case "ec_chao":
                    return new ECChao();
                case "ec_inzone_num":
                    return new ECInzoneNum();
            }
            return null;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignore
        }
    }
}