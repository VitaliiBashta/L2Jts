package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.MultiSellListContainer;
import org.mmocore.gameserver.model.base.MultiSellEntry;
import org.mmocore.gameserver.model.base.MultiSellIngredient;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.MultiSellList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiSellHolder extends AbstractHolder {
    private final TIntObjectHashMap<MultiSellListContainer> entries = new TIntObjectHashMap<>();

    private MultiSellHolder() {
    }

    public static MultiSellHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public MultiSellListContainer getList(final int id) {
        return entries.get(id);
    }

    public void addMultiSellListContainer(final int id, final MultiSellListContainer list) {
        if (entries.containsKey(id)) {
            _log.warn("MultiSell redefined: " + id);
        }

        list.setListId(id);
        entries.put(id, list);
    }

    public void addCustomMultiSellListContainer(final int id, final MultiSellListContainer list) {
        list.setListId(id);
        entries.put(id, list);
    }

    public MultiSellListContainer remove(final String s) {
        return remove(new File(s));
    }

    public MultiSellListContainer remove(final File f) {
        return remove(Integer.parseInt(f.getName().replaceAll(".xml", "")));
    }

    public MultiSellListContainer remove(final int id) {
        return entries.remove(id);
    }

    private long[] parseItemIdAndCount(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        final String[] a = s.split(":");
        try {
            final long id = Integer.parseInt(a[0]);
            final long count = a.length > 1 ? Long.parseLong(a[1]) : 1;
            return new long[]{id, count};
        } catch (Exception e) {
            error("", e);
            return null;
        }
    }

    public MultiSellEntry parseEntryFromStr(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        final String[] a = s.split("->");
        if (a.length != 2) {
            return null;
        }

        final long[] ingredient;
        final long[] production;
        if ((ingredient = parseItemIdAndCount(a[0])) == null || (production = parseItemIdAndCount(a[1])) == null) {
            return null;
        }

        final MultiSellEntry entry = new MultiSellEntry();
        entry.addIngredient(new MultiSellIngredient((int) ingredient[0], ingredient[1]));
        entry.addProduct(new MultiSellIngredient((int) production[0], production[1]));
        return entry;
    }

    public void SeparateAndSend(final int listId, final Player player, final int npcObjectId, final double taxRate) {
        if (player.isGM())
            player.sendAdminMessage("Multisell: " + listId + ".xml");
        for (final int i : AllSettingsConfig.ALT_DISABLED_MULTISELL) {
            if (i == listId) {
                player.sendMessage(new CustomMessage("common.Disabled"));
                return;
            }
        }

        final MultiSellListContainer list = getList(listId);
        if (list == null) {
            player.sendMessage(new CustomMessage("common.Disabled"));
            return;
        }

        SeparateAndSend(list, player, npcObjectId, taxRate);
    }

    public void SeparateAndSend(MultiSellListContainer list, final Player player, final int npcObjectId, final double taxRate) {
        list = generateMultiSell(list, player, npcObjectId, taxRate);

        MultiSellListContainer temp = new MultiSellListContainer();
        int page = 1;

        temp.setListId(list.getListId());

        // Запоминаем отсылаемый лист, чтобы не подменили
        player.setMultisell(list);

        for (final MultiSellEntry e : list.getEntries()) {
            if (temp.getEntries().size() == OtherConfig.MULTISELL_SIZE) {
                player.sendPacket(new MultiSellList(temp, page, 0));
                page++;
                temp = new MultiSellListContainer();
                temp.setListId(list.getListId());
            }
            temp.addEntry(e);
        }

        player.sendPacket(new MultiSellList(temp, page, 1));
    }

    private MultiSellListContainer generateMultiSell(final MultiSellListContainer container, final Player player, final int npcObjectId, final double taxRate) {
        final MultiSellListContainer list = new MultiSellListContainer();
        list.setListId(container.getListId());

        // Все мультиселлы из датапака
        final boolean enchant = container.isKeepEnchant();
        final boolean notax = container.isNoTax();
        final boolean showall = container.isShowAll();
        final boolean nokey = container.isNoKey();
        final boolean BBSAllowed = container.isBBSAllowed();

        list.setShowAll(showall);
        list.setKeepEnchant(enchant);
        list.setNoTax(notax);
        list.setNoKey(nokey);
        list.setBBSAllowed(BBSAllowed);
        list.setNpcObjectId(npcObjectId);

        final ItemInstance[] items = player.getInventory().getItems();
        for (final MultiSellEntry origEntry : container.getEntries()) {
            final MultiSellEntry ent = origEntry.clone();

            // Обработка налога, если лист не безналоговый
            // Адены добавляются в лист если отсутствуют или прибавляются к существующим
            final List<MultiSellIngredient> ingridients;
            if (!notax && taxRate > 0.) {
                double tax = 0;
                long adena = 0;
                ingridients = new ArrayList<>(ent.getIngredients().size() + 1);
                for (final MultiSellIngredient i : ent.getIngredients()) {
                    if (i.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                        adena += i.getItemCount();
                        tax += i.getItemCount() * taxRate;
                        continue;
                    }
                    ingridients.add(i);
                    if (i.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
                    //FIXME hardcoded. Налог на клановую репутацию. Формула проверена на с6 и соответсвует на 100%.
                    //TODO Проверить на корейском(?) оффе налог на банг поинты и fame
                    {
                        tax += i.getItemCount() / 120 * 1000 * taxRate * 100;
                    }
                    if (i.getItemId() < 1) {
                        continue;
                    }

                    final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(i.getItemId());
                    if (item.isStackable()) {
                        tax += item.getReferencePrice() * i.getItemCount() * taxRate;
                    }
                }

                adena = Math.round(adena + tax);
                if (adena > 0) {
                    ingridients.add(new MultiSellIngredient(ItemTemplate.ITEM_ID_ADENA, adena));
                }

                ent.setTax(Math.round(tax));

                ent.getIngredients().clear();
                ent.getIngredients().addAll(ingridients);
            } else {
                ingridients = ent.getIngredients();
            }

            // Если стоит флаг "показывать все" не проверять наличие ингридиентов
            if (showall) {
                list.addEntry(ent);
            } else {
                final List<Integer> itms = new ArrayList<>();
                // Проверка наличия у игрока ингридиентов
                for (final MultiSellIngredient ingredient : ingridients) {
                    final ItemTemplate template = ingredient.getItemId() <= 0 ? null : ItemTemplateHolder.getInstance().getTemplate(ingredient.getItemId());
                    if (ingredient.getItemId() <= 0 || nokey || template.isEquipment()) {
                        if (ingredient.getItemId() == 12374) // Mammon's Varnish Enhancer
                        {
                            continue;
                        }

                        //TODO: а мы должны тут сверять count?
                        if (ingredient.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE) {
                            if (!itms.contains(ingredient.getItemId()) && player.getClan() != null && player.getClan().getReputationScore() >= ingredient.getItemCount()) {
                                itms.add(ingredient.getItemId());
                            }
                            continue;
                        } else if (ingredient.getItemId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS) {
                            if (!itms.contains(ingredient.getItemId()) && player.getPremiumAccountComponent().getPcBangPoints() >= ingredient.getItemCount()) {
                                itms.add(ingredient.getItemId());
                            }
                            continue;
                        } else if (ingredient.getItemId() == ItemTemplate.ITEM_ID_FAME) {
                            if (!itms.contains(ingredient.getItemId()) && player.getFame() >= ingredient.getItemCount()) {
                                itms.add(ingredient.getItemId());
                            }
                            continue;
                        }

                        for (final ItemInstance item : items) {
                            if (item.getItemId() == ingredient.getItemId() && !item.isEquipped() && item.canBeExchanged(player)) {
                                //FIX ME если перевалит за long - косяк(VISTALL)
                                if (itms.contains(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000 : ingredient.getItemId())) // Не проверять одинаковые вещи
                                {
                                    continue;
                                }

                                if (item.getEnchantLevel() < ingredient.getItemEnchant()) // Некоторые мультиселлы требуют заточки
                                {
                                    continue;
                                }

                                if (item.isStackable() && item.getCount() < ingredient.getItemCount()) {
                                    break;
                                }

                                itms.add(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000 : ingredient.getItemId());
                                final MultiSellEntry possibleEntry = new MultiSellEntry(enchant ? ent.getEntryId() + item.getEnchantLevel() * 100000 : ent.getEntryId());

                                for (final MultiSellIngredient p : ent.getProduction()) {
                                    if (enchant && template.canBeEnchanted()) //TODO[K] - Возможно понадобится проверять итем по грейду, тут!
                                    {
                                        p.setVisual(item.getVisualItemId());
                                        p.setFlag(item.getCustomFlags());
                                        p.setCostume(item.isCostume());
                                        p.setItemEnchant(item.getEnchantLevel());
                                        p.setItemAttributes(item.getAttributes().clone());
                                    }
                                    possibleEntry.addProduct(p);
                                }

                                for (final MultiSellIngredient ig : ingridients) {
                                    if (enchant && ig.getItemId() > 0 && ItemTemplateHolder.getInstance().getTemplate(ig.getItemId())
                                            .canBeEnchanted()) //TODO[K] - Возможно понадобится проверять итем по грейду, тут!
                                    {
                                        ig.setVisual(item.getVisualItemId());
                                        ig.setFlag(item.getCustomFlags());
                                        ig.setCostume(item.isCostume());
                                        ig.setItemEnchant(item.getEnchantLevel());
                                        ig.setItemAttributes(item.getAttributes().clone());
                                    }
                                    possibleEntry.addIngredient(ig);
                                }

                                list.addEntry(possibleEntry);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public void clear() {
        entries.clear();
    }

    private static class LazyHolder {
        private static final MultiSellHolder INSTANCE = new MultiSellHolder();
    }
}