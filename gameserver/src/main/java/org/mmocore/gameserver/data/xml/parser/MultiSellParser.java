package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.MultiSellListContainer;
import org.mmocore.gameserver.model.base.MultiSellEntry;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;

/**
 * @author VISTALL
 * @date 15:42/01.08.2011
 */
public class MultiSellParser extends AbstractDirParser<MultiSellHolder> {
    protected MultiSellParser() {
        super(MultiSellHolder.getInstance());
    }

    public static MultiSellParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/multisell");
    }

    @Override
    public boolean isIgnored(final File f) {
        return f.getPath().contains("custom");
    }

    @Override
    public String getDTDFileName() {
        return "multisell.dtd";
    }

    @Override
    protected void readData(final MultiSellHolder holder, final Element rootElement) throws Exception {
        final int id = Integer.parseInt(_currentFile.replace(".xml", ""));
        final MultiSellListContainer list = new MultiSellListContainer();

        int entryId = 0;
        for (final Element element : rootElement.getChildren()) {
            if ("config".equalsIgnoreCase(element.getName())) {
                list.setShowAll(Boolean.parseBoolean(element.getAttributeValue("showall", "true")));
                list.setNoTax(Boolean.parseBoolean(element.getAttributeValue("notax", "false")));
                list.setKeepEnchant(Boolean.parseBoolean(element.getAttributeValue("keepenchanted", "false")));
                list.setNoKey(Boolean.parseBoolean(element.getAttributeValue("nokey", "false")));
                list.setBBSAllowed(Boolean.parseBoolean(element.getAttributeValue("bbsallowed", "false")));
            } else if ("item".equalsIgnoreCase(element.getName())) {
                final MultiSellEntry e = parseEntry(element, id);
                if (e != null) {
                    e.setEntryId(entryId++);
                    list.addEntry(e);
                }
            }
        }
        addMultiSellListContainer(holder, id, list);
    }

    protected MultiSellEntry parseEntry(final Element n, final int multiSellId) {
        final MultiSellEntry entry = new MultiSellEntry();

        for (final Element element : n.getChildren()) {
            if ("ingredient".equalsIgnoreCase(element.getName())) {
                final int id = Integer.parseInt(element.getAttributeValue("id"));
                final long count = Long.parseLong(element.getAttributeValue("count"));
                final MultiSellIngredient mi = new MultiSellIngredient(id, count);
                if (element.getAttributeValue("enchant") != null) {
                    mi.setItemEnchant(Integer.parseInt(element.getAttributeValue("enchant")));
                }
                if (element.getAttributeValue("mantainIngredient") != null) {
                    mi.setMantainIngredient(Boolean.parseBoolean(element.getAttributeValue("mantainIngredient")));
                }
                //Elements
                if (element.getAttributeValue("fireAttr") != null) {
                    mi.getItemAttributes().setFire(Integer.parseInt(element.getAttributeValue("fireAttr")));
                }
                if (element.getAttributeValue("waterAttr") != null) {
                    mi.getItemAttributes().setWater(Integer.parseInt(element.getAttributeValue("waterAttr")));
                }
                if (element.getAttributeValue("earthAttr") != null) {
                    mi.getItemAttributes().setEarth(Integer.parseInt(element.getAttributeValue("earthAttr")));
                }
                if (element.getAttributeValue("windAttr") != null) {
                    mi.getItemAttributes().setWind(Integer.parseInt(element.getAttributeValue("windAttr")));
                }
                if (element.getAttributeValue("holyAttr") != null) {
                    mi.getItemAttributes().setHoly(Integer.parseInt(element.getAttributeValue("holyAttr")));
                }
                if (element.getAttributeValue("unholyAttr") != null) {
                    mi.getItemAttributes().setUnholy(Integer.parseInt(element.getAttributeValue("unholyAttr")));
                }
                if (element.getAttributeValue("life") != null) {
                    mi.setItemLifeTime(Integer.parseInt(element.getAttributeValue("life")));
                }

                entry.addIngredient(mi);
            } else if ("production".equalsIgnoreCase(element.getName())) {
                final int id = Integer.parseInt(element.getAttributeValue("id"));
                final long count = Long.parseLong(element.getAttributeValue("count"));
                final MultiSellIngredient mi = new MultiSellIngredient(id, count);
                if (element.getAttributeValue("enchant") != null) {
                    mi.setItemEnchant(Integer.parseInt(element.getAttributeValue("enchant")));
                }
                //Elements
                if (element.getAttributeValue("fireAttr") != null) {
                    mi.getItemAttributes().setFire(Integer.parseInt(element.getAttributeValue("fireAttr")));
                }
                if (element.getAttributeValue("waterAttr") != null) {
                    mi.getItemAttributes().setWater(Integer.parseInt(element.getAttributeValue("waterAttr")));
                }
                if (element.getAttributeValue("earthAttr") != null) {
                    mi.getItemAttributes().setEarth(Integer.parseInt(element.getAttributeValue("earthAttr")));
                }
                if (element.getAttributeValue("windAttr") != null) {
                    mi.getItemAttributes().setWind(Integer.parseInt(element.getAttributeValue("windAttr")));
                }
                if (element.getAttributeValue("holyAttr") != null) {
                    mi.getItemAttributes().setHoly(Integer.parseInt(element.getAttributeValue("holyAttr")));
                }
                if (element.getAttributeValue("unholyAttr") != null) {
                    mi.getItemAttributes().setUnholy(Integer.parseInt(element.getAttributeValue("unholyAttr")));
                }

                if (!AllSettingsConfig.ALT_ALLOW_SHADOW_WEAPONS && id > 0) {
                    final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(id);
                    if (item != null && item.isShadowItem() && item.isWeapon() && !AllSettingsConfig.ALT_ALLOW_SHADOW_WEAPONS) {
                        return null;
                    }
                }

                entry.addProduct(mi);
            }
        }

        if (entry.getIngredients().isEmpty() || entry.getProduction().isEmpty()) {
            _log.warn("MultiSell [" + multiSellId + "] is empty!");
            return null;
        }

        if (entry.getIngredients().size() == 1 && entry.getProduction().size() == 1 && entry.getIngredients().get(0).getItemId() == 57) {
            final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
            if (item == null) {
                _log.warn("MultiSell [" + multiSellId + "] Production [" + entry.getProduction().get(0).getItemId() + "] not found!");
                return null;
            }
            if ((multiSellId < 70000 || multiSellId > 70010) || (multiSellId >= 50000 && multiSellId <= 50034)) //FIXME hardcode. Все кроме GM Shop
            {
                if (AllSettingsConfig.showPriceWarnings && item.getReferencePrice() > entry.getIngredients().get(0).getItemCount()) {
                    _log.warn("MultiSell [" + multiSellId + "] Production '" + item.getName() + "' [" + entry.getProduction().get(0).getItemId() +
                            "] price is lower than referenced | " + item.getReferencePrice() + " > " + entry.getIngredients().get(0)
                            .getItemCount());
                }
            }
        }

        return entry;
    }

    protected void addMultiSellListContainer(final MultiSellHolder holder, final int id, final MultiSellListContainer list) {
        holder.addMultiSellListContainer(id, list);
    }

    private static class LazyHolder {
        private static final MultiSellParser INSTANCE = new MultiSellParser();
    }
}
