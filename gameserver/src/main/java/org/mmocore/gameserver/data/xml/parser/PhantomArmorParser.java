package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomArmorHolder;
import org.mmocore.gameserver.phantoms.template.PhantomArmorTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 23.08.2016 0:15
 */
public class PhantomArmorParser extends AbstractFileParser<PhantomArmorHolder> {
    private static final PhantomArmorParser instance = new PhantomArmorParser();

    private PhantomArmorParser() {
        super(PhantomArmorHolder.getInstance());
    }

    public static PhantomArmorParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/phantoms/equipment/armor.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armor.dtd";
    }

    @Override
    protected void readData(PhantomArmorHolder holder, Element rootElement) {
        for (Element set : rootElement.getChildren("set")) {
            int setId = Integer.parseInt(set.getAttributeValue("set_id"));
            List<Integer> items = new ArrayList<>();
            PhantomArmorTemplate template = new PhantomArmorTemplate();
            for (Element item : set.getChildren("item"))
                template.addId(Integer.parseInt(item.getAttributeValue("id")));
            holder.addSet(setId, template);
        }
    }
}
