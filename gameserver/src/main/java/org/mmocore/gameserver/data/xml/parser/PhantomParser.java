package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomHolder;
import org.mmocore.gameserver.phantoms.ai.PhantomAiType;
import org.mmocore.gameserver.phantoms.template.PhantomTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;

/**
 * Created by Hack
 * Date: 22.08.2016 6:32
 */
public class PhantomParser extends AbstractFileParser<PhantomHolder> {
    private static final PhantomParser instance = new PhantomParser();

    private PhantomParser() {
        super(PhantomHolder.getInstance());
    }

    public static PhantomParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/phantoms/phantoms.xml");
    }

    @Override
    public String getDTDFileName() {
        return "phantoms.dtd";
    }

    @Override
    protected void readData(PhantomHolder holder, Element rootElement) throws Exception {
        for (Element phantom : rootElement.getChildren("phantom")) {
            PhantomTemplate template = new PhantomTemplate();
            template.setType(PhantomAiType.valueOf(phantom.getAttributeValue("type")));
            template.setName(phantom.getAttributeValue("name"));
            template.setTitle(phantom.getAttributeValue("title"));
            template.setGrade(ItemTemplate.Grade.valueOf(phantom.getAttributeValue("grade")));
            for (Element set : phantom.getChildren("set")) {
                String name = set.getAttributeValue("name");
                switch (name) {
                    case "race":
                        template.setRace(getInteger(set));
                        break;
                    case "classId":
                        template.setClassId(getInteger(set));
                        break;
                    case "sex":
                        template.setSex(getInteger(set));
                        break;
                    case "face":
                        template.setFace(getInteger(set));
                        break;
                    case "hair":
                        template.setHair(getInteger(set));
                        break;
                    case "nameColor":
                        template.setNameColor(getIntegerDecode(set));
                        break;
                    case "titleColor":
                        template.setTitleColor(getIntegerDecode(set));
                        break;
                }
            }
            holder.addPhantomTemplate(template.getGrade(), template);
        }
    }

    private int getInteger(Element set) {
        return Integer.parseInt(set.getAttributeValue("value"));
    }

    private int getIntegerDecode(Element set) {
        return Integer.decode(set.getAttributeValue("value"));
    }
}
