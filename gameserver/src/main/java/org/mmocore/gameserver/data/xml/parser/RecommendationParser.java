package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.configuration.config.RecommendationConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.RecommendationHolder;

import java.io.File;
import java.util.stream.IntStream;

/**
 * Create by Mangol on 24.12.2015.
 */
public class RecommendationParser extends AbstractFileParser<RecommendationHolder> {
    private static final RecommendationParser INSTANCE = new RecommendationParser();

    protected RecommendationParser() {
        super(RecommendationHolder.getInstance());
    }

    public static RecommendationParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/recommendations.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/recommendations.dtd";
    }

    @Override
    protected void readData(final RecommendationHolder holder, final Element rootElement) throws Exception {
        final Element setting = rootElement.getChild("setting");
        RecommendationConfig.reuseTime = QuartzUtils.createCronExpression(setting.getAttributeValue("reuseDayTime"));
        rootElement.getChildren("recommendations").stream().forEach(element -> {
            final int minLevel = Integer.parseInt(element.getAttributeValue("min_level"));
            final int maxLevel = Integer.parseInt(element.getAttributeValue("max_level"));
            IntStream.rangeClosed(minLevel, maxLevel).forEach(level -> element.getChildren().stream().forEach(number -> {
                final int minRec = Integer.parseInt(number.getAttributeValue("min_rec"));
                final int maxRec = Integer.parseInt(number.getAttributeValue("max_rec"));
                final int bunusValue = Integer.parseInt(number.getAttributeValue("bunus_value"));
                IntStream.rangeClosed(minRec, maxRec).forEach(rec -> holder.addRecommendation(level, rec, bunusValue));
            }));
        });
    }
}
