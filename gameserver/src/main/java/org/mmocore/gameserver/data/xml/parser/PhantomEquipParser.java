package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomArmorHolder;
import org.mmocore.gameserver.data.xml.holder.PhantomEquipHolder;
import org.mmocore.gameserver.phantoms.template.PhantomArmorTemplate;
import org.mmocore.gameserver.phantoms.template.PhantomEquipTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 23.08.2016 2:33
 */
public class PhantomEquipParser extends AbstractFileParser<PhantomEquipHolder> {
    private static final PhantomEquipParser instance = new PhantomEquipParser();

    private PhantomEquipParser() {
        super(PhantomEquipHolder.getInstance());
    }

    public static PhantomEquipParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/phantoms/equipment/class_equip.xml");
    }

    @Override
    public String getDTDFileName() {
        return "class_equip.dtd";
    }

    @Override
    protected void readData(PhantomEquipHolder holder, Element rootElement) {
        for (Element pClass : rootElement.getChildren("class")) {
            PhantomEquipTemplate template = new PhantomEquipTemplate();
            template.setClassId(Integer.parseInt(pClass.getAttributeValue("id")));
            for (Element items : pClass.getChildren("items")) {
                ItemTemplate.Grade grade = ItemTemplate.Grade.valueOf(items.getAttributeValue("grade"));
                List<Integer> weaponIds = new ArrayList<>();
                List<Integer> shieldIds = new ArrayList<>();
                List<PhantomArmorTemplate> armorTemplates = new ArrayList<>();
                for (Element weapon : items.getChildren("weapon"))
                    weaponIds.add(Integer.parseInt(weapon.getAttributeValue("item_id")));
                for (Element shield : items.getChildren("shield"))
                    shieldIds.add(Integer.parseInt(shield.getAttributeValue("item_id")));
                for (Element armor : items.getChildren("armor")) {
                    armorTemplates.add(PhantomArmorHolder.getInstance()
                            .getSet(Integer.parseInt(armor.getAttributeValue("set_id"))));
                }
                template.addWeaponList(grade, weaponIds);
                template.addShieldList(grade, shieldIds);
                template.addArmorList(grade, armorTemplates);
            }
            holder.addClassEquip(template.getClassId(), template);
        }
    }
}
