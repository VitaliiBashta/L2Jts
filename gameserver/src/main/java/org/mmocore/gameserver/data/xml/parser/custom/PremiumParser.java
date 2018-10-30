package org.mmocore.gameserver.data.xml.parser.custom;

import org.jdom2.Element;
import org.mmocore.commons.converter.Converter;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.custom.PremiumHolder;
import org.mmocore.gameserver.templates.custom.premium.PremiumPackage;
import org.mmocore.gameserver.templates.custom.premium.PremiumType;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumRates;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumTime;

import java.io.File;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public class PremiumParser extends AbstractFileParser<PremiumHolder> {
    private static final PremiumParser INSTANCE = new PremiumParser();

    protected PremiumParser() {
        super(PremiumHolder.getInstance());
    }

    public static PremiumParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/premium/premium.xml");
    }

    @Override
    public String getDTDFileName() {
        return "premium.dtd";
    }

    @Override
    protected void readData(final PremiumHolder holder, final Element rootElement) throws Exception {
        rootElement.getChildren().stream().forEach(premium -> {
			/*
			final Element optionElement = element.getChild("personalSetting");
			if(optionElement != null)
			{
				holder.setPersonalSetting(new PremiumPersonalSetting());
				optionElement.getChildren().stream().forEach(optionElem -> {
					final Element timesElement = optionElem.getChild("times");
					timesElement.getChildren().stream().forEach(times -> {
						final int id = Converter.convert(Integer.class, times.getAttributeValue("id"));
						final int days = Converter.convert(Integer.class, times.getAttributeValue("days"));
						final int hour = Converter.convert(Integer.class, times.getAttributeValue("hour"));
						final int minute = Converter.convert(Integer.class, times.getAttributeValue("minute"));
						final int item_id = Converter.convert(Integer.class, times.getAttributeValue("item_id"));
						final long item_count = Converter.convert(Long.class, times.getAttributeValue("item_count"));
						final PremiumTime premiumTime = new PremiumTime(id, days, hour, minute, item_id, item_count);
						holder.getPersonalSetting().addTime(premiumTime);
					});
					optionElem.getChildren("rates").stream().forEach(ratesElement -> {
						final PremiumType premiumType = PremiumType.valueOf(ratesElement.getAttributeValue("type"));
						ratesElement.getChildren("rate").stream().forEach(rate -> {
							final int id = Converter.convert(Integer.class, rate.getAttributeValue("id"));
							final int value = Converter.convert(Integer.class, rate.getAttributeValue("value"));
							final int item_id = Converter.convert(Integer.class, rate.getAttributeValue("item_id"));
							final long item_count = Converter.convert(Long.class, rate.getAttributeValue("item_count"));
							final PremiumRates premiumRates = new PremiumRates(premiumType, id, value, item_id, item_count);
							holder.getPersonalSetting().addRate(premiumRates);
						});
					});
				});
			}
			*/
            final int idPremium = Converter.convert(Integer.class, premium.getAttributeValue("id"));
            final String icon = premium.getAttributeValue("icon");
            final boolean show = premium.getAttributeValue("show") == null ? false : Converter.convert(Boolean.class, premium.getAttributeValue("show"));
            final String nameRU = premium.getAttributeValue("nameRU");
            final String nameEN = premium.getAttributeValue("nameEN");
            final PremiumPackage premiumPackage = new PremiumPackage(idPremium, icon, nameRU, nameEN, show);
            final Element timesElement = premium.getChild("times");
            timesElement.getChildren().stream().forEach(times -> {
                final int id = Converter.convert(Integer.class, times.getAttributeValue("id"));
                final int days = Math.min(364, Converter.convert(Integer.class, times.getAttributeValue("days")));
                final int hour = Math.min(23, Converter.convert(Integer.class, times.getAttributeValue("hour")));
                final int minute = Math.min(59, Converter.convert(Integer.class, times.getAttributeValue("minute")));
                final int item_id = Converter.convert(Integer.class, times.getAttributeValue("item_id"));
                final long item_count = Converter.convert(Long.class, times.getAttributeValue("item_count"));
                final PremiumTime premiumTime = new PremiumTime(id, days, hour, minute, item_id, item_count);
                premiumPackage.addTime(premiumTime);
            });
            final Element rates = premium.getChild("bonus_rate");
            rates.getChildren().stream().forEach(rate -> {
                final PremiumType premiumType = PremiumType.valueOf(rate.getAttributeValue("type"));
                final double value = Converter.convert(Double.class, rate.getAttributeValue("value"));
                final PremiumRates premiumRates = new PremiumRates(premiumType, value);
                premiumPackage.addRate(premiumRates);
            });
            holder.addPremiumPackage(premiumPackage);
        });
    }
}
