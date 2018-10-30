package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PetitionGroupHolder;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.model.petition.PetitionSubGroup;
import org.mmocore.gameserver.utils.Language;

import java.io.File;

/**
 * @author VISTALL
 * @date 7:22/25.07.2011
 */
public class PetitionGroupParser extends AbstractFileParser<PetitionGroupHolder> {
    private static final PetitionGroupParser _instance = new PetitionGroupParser();

    private PetitionGroupParser() {
        super(PetitionGroupHolder.getInstance());
    }

    public static PetitionGroupParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/petition_group.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/petition_group.dtd";
    }

    @Override
    protected void readData(final PetitionGroupHolder holder, final Element rootElement) throws Exception {
        for (final Element groupElement : rootElement.getChildren("group")) {
            final PetitionMainGroup group = new PetitionMainGroup(Integer.parseInt(groupElement.getAttributeValue("id")));
            holder.addPetitionGroup(group);

            for (final Element subElement : groupElement.getChildren()) {
                if ("name".equals(subElement.getName())) {
                    group.setName(Language.valueOf(subElement.getAttributeValue("lang")), subElement.getText());
                } else if ("description".equals(subElement.getName())) {
                    group.setDescription(Language.valueOf(subElement.getAttributeValue("lang")), subElement.getText());
                } else if ("sub_group".equals(subElement.getName())) {
                    final PetitionSubGroup subGroup = new PetitionSubGroup(Integer.parseInt(subElement.getAttributeValue("id")),
                            subElement.getAttributeValue("handler"));
                    group.addSubGroup(subGroup);
                    for (final Element sub2Element : subElement.getChildren()) {
                        if ("name".equals(sub2Element.getName())) {
                            subGroup.setName(Language.valueOf(sub2Element.getAttributeValue("lang")), sub2Element.getText());
                        } else if ("description".equals(sub2Element.getName())) {
                            subGroup.setDescription(Language.valueOf(sub2Element.getAttributeValue("lang")), sub2Element.getText());
                        }
                    }
                }
            }
        }
    }
}
