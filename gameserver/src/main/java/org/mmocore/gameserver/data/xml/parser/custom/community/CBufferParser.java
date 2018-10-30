package org.mmocore.gameserver.data.xml.parser.custom.community;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.custom.community.CBufferHolder;
import org.mmocore.gameserver.object.components.player.community.Buff;
import org.mmocore.gameserver.object.components.player.community.BuffScheme;

import java.io.File;

/**
 * Create by Mangol on 13.12.2015.
 */
public class CBufferParser extends AbstractFileParser<CBufferHolder> {
    private static final CBufferParser INSTANCE = new CBufferParser();

    protected CBufferParser() {
        super(CBufferHolder.getInstance());
    }

    public static CBufferParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/community/buffer/buffer.xml");
    }


    @Override
    public String getDTDFileName() {
        return "buffer.dtd";
    }

    @Override
    protected void readData(final CBufferHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int schemeId = Integer.parseInt(element.getAttributeValue("id"));
            final String name = element.getAttributeValue("name");
            final int priceId = Integer.parseInt(element.getAttributeValue("priceId"));
            final long count = Long.parseLong(element.getAttributeValue("count"));
            final BuffScheme buffScheme = new BuffScheme(schemeId, name, priceId, count);
            for (final Element skill : element.getChildren("buff")) {
                final int skillId = Integer.parseInt(skill.getAttributeValue("id"));
                final int level = Integer.parseInt(skill.getAttributeValue("level"));
                final Buff buff = new Buff(skillId, level);
                buffScheme.addBuff(buff);
            }
            holder.addBuffScheme(buffScheme);
        }
    }
}
