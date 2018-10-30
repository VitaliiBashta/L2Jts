package org.mmocore.gameserver.data.xml.parser.custom.community;

import org.jdom2.Element;
import org.mmocore.commons.converter.Converter;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.custom.community.CLeaseTransformHolder;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.BonusTemplate;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.LeaseTransformTemplate;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.TimesTemplate;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Mangol
 * @since 03.02.2016
 */
public class CLeaseTransformParser extends AbstractFileParser<CLeaseTransformHolder> {
    private static final CLeaseTransformParser INSTANCE = new CLeaseTransformParser();

    protected CLeaseTransformParser() {
        super(CLeaseTransformHolder.getInstance());
    }

    public static CLeaseTransformParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/community/leaseTransform/leaseTransform.xml");
    }

    @Override
    public String getDTDFileName() {
        return "leaseTransform.dtd";
    }

    @Override
    protected void readData(final CLeaseTransformHolder holder, final Element rootElement) {
        rootElement.getChildren().stream().forEach(element -> {
            final int id = Converter.convert(Integer.class, element.getAttributeValue("id"));
            final String nameRU = element.getAttributeValue("nameRU");
            final String nameEN = element.getAttributeValue("nameEN");
            final Element levels = element.getChild("level");
            final int minLevel = Converter.convert(Integer.class, levels.getAttributeValue("min"));
            final int maxLevel = Converter.convert(Integer.class, levels.getAttributeValue("max"));
            final Element skills = element.getChild("skill");
            final int skillId = Converter.convert(Integer.class, skills.getAttributeValue("skillId"));
            final int skillLevel = Converter.convert(Integer.class, skills.getAttributeValue("skillLevel"));
            Optional<BonusTemplate> bonusTemplate = Optional.empty();
            final Element bonus = element.getChild("bonus");
            if (bonus != null) {
                bonusTemplate = Optional.of(new BonusTemplate());
                final int str = bonus.getAttributeValue("str") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("str"));
                final int _int = bonus.getAttributeValue("int") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("int"));
                final int con = bonus.getAttributeValue("con") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("con"));
                final int dex = bonus.getAttributeValue("dex") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("dex"));
                final int wit = bonus.getAttributeValue("wit") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("wit"));
                final int men = bonus.getAttributeValue("men") == null ? 0 : Converter.convert(Integer.class, bonus.getAttributeValue("men"));
                bonusTemplate.get().setStr(str);
                bonusTemplate.get().setInt(_int);
                bonusTemplate.get().setCon(con);
                bonusTemplate.get().setDex(dex);
                bonusTemplate.get().setWit(wit);
                bonusTemplate.get().setMen(men);
            }
            final Element descriptions = element.getChild("description");
            final String descriptionRU = descriptions.getAttributeValue("ru");
            final String descriptionEN = descriptions.getAttributeValue("en");
            final LeaseTransformTemplate lease = new LeaseTransformTemplate(id, nameRU, nameEN, minLevel, maxLevel, skillId, skillLevel, descriptionRU, descriptionEN);
            lease.setBonus(bonusTemplate);
            final List<Element> times = element.getChild("times").getChildren("time");
            times.stream().forEach(time -> {
                final int key = Converter.convert(Integer.class, time.getAttributeValue("key"));
                final int minute = Converter.convert(Integer.class, time.getAttributeValue("minute"));
                final int itemId = Converter.convert(Integer.class, time.getAttributeValue("itemId"));
                final int itemCount = Converter.convert(Integer.class, time.getAttributeValue("itemCount"));
                lease.addTimes(new TimesTemplate(key, minute, itemId, itemCount));
            });
            holder.addLeaseTransform(lease);
        });
    }
}
