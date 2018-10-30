package org.mmocore.gameserver.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.itemdata.ItemData;
import org.mmocore.commons.converter.Converter;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.ArmorgrpLineHolder;
import org.mmocore.gameserver.data.client.holder.EtcitemgrpLineHolder;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.data.client.holder.WeapongrpLineHolder;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExUseSharedGroupItem;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.templates.client.ArmorgrpLine;
import org.mmocore.gameserver.templates.client.EtcitemgrpLine;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.templates.client.WeapongrpLine;
import org.mmocore.gameserver.templates.item.EtcItemTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.quartz.CronExpression;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class Util {
    private static final ArmorgrpLineHolder armorgrpHolder = ArmorgrpLineHolder.getInstance();
    private static final WeapongrpLineHolder weapongrpHolder = WeapongrpLineHolder.getInstance();
    private static final EtcitemgrpLineHolder etcitemgrpHolder = EtcitemgrpLineHolder.getInstance();
    private static final ItemNameLineHolder itemnameHolder = ItemNameLineHolder.getInstance();
    private static final String INSERT_QUERY = "INSERT INTO items_delayed (owner_id, item_id, count, description) VALUES(?,?,?,?)";

    /**
     * Форматтер для адены.<br>
     * Locale.KOREA заставляет его фортматировать через ",".<br>
     * Locale.FRANCE форматирует через " "<br>
     * Для форматирования через "." убрать с аргументов Locale.FRANCE
     */
    private static final NumberFormat adenaFormatter;
    private static final Pattern _pattern = Pattern.compile("<!--TEMPLET(\\d+)(.*?)TEMPLET-->", Pattern.DOTALL);

    static {
        adenaFormatter = NumberFormat.getIntegerInstance(Locale.FRANCE);
    }

    /**
     * Проверяет строку на соответсвие регулярному выражению
     *
     * @param text     Строка-источник
     * @param template Шаблон для поиска
     * @return true в случае соответвия строки шаблону
     */
    public static boolean isMatchingRegexp(final String text, final String template) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(template);
        } catch (PatternSyntaxException e) // invalid template
        {
            e.printStackTrace();
        }
        if (pattern == null) {
            return false;
        }
        final Matcher regexp = pattern.matcher(text);
        return regexp.matches();
    }

    /**
     * Return amount of adena formatted with " " delimiter
     *
     * @param amount
     * @return String formatted adena amount
     */
    public static String formatAdena(final long amount) {
        return adenaFormatter.format(amount);
    }

    /**
     * форматирует время в секундах в дни/часы/минуты/секунды
     */
    public static String formatTime(int time) {
        if (time == 0) {
            return "now";
        }
        time = Math.abs(time);
        String ret = "";
        final long numDays = time / 86400;
        time -= numDays * 86400;
        final long numHours = time / 3600;
        time -= numHours * 3600;
        final long numMins = time / 60;
        time -= numMins * 60;
        final long numSeconds = time;
        if (numDays > 0) {
            ret += numDays + "d ";
        }
        if (numHours > 0) {
            ret += numHours + "h ";
        }
        if (numMins > 0) {
            ret += numMins + "m ";
        }
        if (numSeconds > 0) {
            ret += numSeconds + "s";
        }
        return ret.trim();
    }

    public static int packInt(final int[] a, final int bits) throws Exception {
        final int m = 32 / bits;
        if (a.length > m) {
            throw new Exception("Overflow");
        }

        int result = 0;
        int next;
        final int mval = (int) Math.pow(2, bits);
        for (int i = 0; i < m; i++) {
            result <<= bits;
            if (a.length > i) {
                next = a[i];
                if (next >= mval || next < 0) {
                    throw new Exception("Overload, value is out of range");
                }
            } else {
                next = 0;
            }
            result += next;
        }
        return result;
    }

    public static int[] unpackInt(int a, final int bits) {
        final int m = 32 / bits;
        final int mval = (int) Math.pow(2, bits);
        final int[] result = new int[m];
        int next;
        for (int i = m; i > 0; i--) {
            next = a;
            a >>= bits;
            result[i - 1] = next - a * mval;
        }
        return result;
    }

    /**
     * Just alias
     */
    public static String joinStrings(final String glueStr, final String[] strings, final int startIdx, final int maxCount) {
        return Strings.joinStrings(glueStr, strings, startIdx, maxCount);
    }

    /**
     * Just alias
     */
    public static String joinStrings(final String glueStr, final String[] strings, final int startIdx) {
        return Strings.joinStrings(glueStr, strings, startIdx, -1);
    }

    public static String dumpObject(final Object o, final boolean simpleTypes, final boolean parentFields, final boolean ignoreStatics) {
        Class<?> cls = o.getClass();
        String val, type, result = '[' + (simpleTypes ? cls.getSimpleName() : cls.getName()) + '\n';
        Object fldObj;
        final List<Field> fields = new ArrayList<>();
        while (cls != null) {
            for (final Field fld : cls.getDeclaredFields()) {
                if (!fields.contains(fld)) {
                    if (ignoreStatics && Modifier.isStatic(fld.getModifiers())) {
                        continue;
                    }
                    fields.add(fld);
                }
            }
            cls = cls.getSuperclass();
            if (!parentFields) {
                break;
            }
        }

        for (final Field fld : fields) {
            fld.setAccessible(true);
            try {
                fldObj = fld.get(o);
                if (fldObj == null) {
                    val = "NULL";
                } else {
                    val = fldObj.toString();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                val = "<ERROR>";
            }
            type = simpleTypes ? fld.getType().getSimpleName() : fld.getType().toString();

            result += String.format("\t%s [%s] = %s;%n", fld.getName(), type, val);
        }

        result += "]\n";
        return result;
    }

    public static HashMap<Integer, String> parseTemplate(String html) {
        final Matcher m = _pattern.matcher(html);
        final HashMap<Integer, String> tpls = new HashMap<>();
        while (m.find()) {
            tpls.put(Integer.parseInt(m.group(1)), m.group(2));
            html = html.replace(m.group(0), "");
        }

        tpls.put(0, html);
        return tpls;
    }

    public static int[] objectToIntArray(List<Creature> list) {
        list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (list == null || list.isEmpty()) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] tmp = new int[list.size()];
        int i = 0;
        for (final Creature cr : list) {
            tmp[i++] = cr.getObjectId();
        }
        return tmp;
    }

    public static String getItemIcon(final int first) {
        final Optional<EtcitemgrpLine> etcitem = etcitemgrpHolder.getEtcitemgrp(first);
        if (etcitem.isPresent()) {
            return etcitem.get().getIcon0();
        }
        final Optional<ArmorgrpLine> armor = armorgrpHolder.getArmorgrp(first);
        if (armor.isPresent()) {
            return armor.get().getIcon0();
        }
        final Optional<WeapongrpLine> weapon = weapongrpHolder.getWeapongrp(first);
        if (weapon.isPresent()) {
            return weapon.get().getIcon0();
        }
        return StringUtils.EMPTY;
    }

    public static String getItemName(final Language lang, final int first) {
        if (first == ItemTemplate.PREMIUM_POINTS) {
            return new CustomMessage("custom.item.premiumPoint").toString(lang);
        }
        final Optional<ItemNameLine> itemName = Optional.ofNullable(itemnameHolder.get(lang, first));
        if (itemName.isPresent()) {
            return itemName.get().getName();
        }
        return new CustomMessage("custom.item.noItemName").toString(lang);
    }

    public static String formatPay(final Player player, final long count, final int item) {
        if (count > 0L) {
            return Util.formatAdena(count) + " " + getItemName(player.getLanguage(), item);
        }
        return new CustomMessage("price.free").toString(player);
    }

    public static String RGBtoBGR(String color) {
        String colorOut = "";
        if (color != null && color.length() == 6) {
            colorOut = color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2);
        }
        return colorOut;
    }

    public static long getWeaklyDate(int dayOfWeek, int hourOfDay, int minutesOfHour, int randomMinutes) {
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault())
                .with(ChronoField.DAY_OF_WEEK, dayOfWeek)
                .plusHours(hourOfDay)
                .plusMinutes(Rnd.get(minutesOfHour, minutesOfHour + randomMinutes));
        if (!date.isAfter(ZonedDateTime.now(ZoneId.systemDefault())))
            date.plusWeeks(1);
        return date.toInstant().toEpochMilli();
    }

    public static long getWeaklyDate(int dayOfWeek, int hourOfDay, int minutesOfHour) {
        return getWeaklyDate(dayOfWeek, hourOfDay, minutesOfHour, 0);
    }

    public static long getCronMillis(CronExpression cron) {
        return cron.getNextValidTimeAfter(new Date()).getTime();
    }

    public static double percentOf(double maxValue, double requiredValue) {
        return requiredValue / (maxValue / 100.);
    }

    //TODO[Hack]: в парсер конфигов
    public static <K, V> Map<K, V> parseConfigMapThrowable(String str, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> map = new HashMap<>();
        String[] strs = str.trim().split(";");
        for (String obj : strs) {
            String[] values = obj.split(",");
            map.put(Converter.convert(keyClass, values[0]), Converter.convert(valueClass, values[1]));
        }
        return map;
    }

    public static <K, V> Map<K, V> parseConfigMap(String str, Class<K> keyClass, Class<V> valueClass) {
        try {
            return parseConfigMapThrowable(str, keyClass, valueClass);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<K, V>();
        }
    }

    public static <T> List<T> parseTemplateConfig(String str, Constructor<T> templateConstructor) throws Exception {
        List<T> list = new ArrayList<>();
        String[] strs = str.trim().split(";");
        for (String obj : strs) {
            String[] values = obj.split(",");
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < values.length; i++)
                args.add(Converter.convert(templateConstructor.getParameterTypes()[i], values[i]));
            list.add(templateConstructor.newInstance(args.toArray()));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parseTemplateConfig(String str, Class<T> templateClass) throws Exception {
        return parseTemplateConfig(str, (Constructor<T>) templateClass.getConstructors()[0]);
    }

    public static void useItem(Player activeChar, int _objectId, boolean _ctrlPressed) {
        if (activeChar == null) {
            return;
        }
        activeChar.setActive();
        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }
        final int itemId = item.getItemId();
        if (activeChar.isInStoreMode()) {
            if (PetDataHolder.getInstance().isPetControlItem(item.getItemId())) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
            } else {
                activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP);
            }
            return;
        }
        if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540)) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }
        if (activeChar.isSharedGroupDisabled(item.getTemplate().getReuseGroup())) {
            activeChar.sendReuseMessage(item);
            return;
        }
        if (!item.getTemplate().testCondition(activeChar, item)) {
            return;
        }
        if (activeChar.getInventory().isLockedItem(item)) {
            return;
        }
        if (item.getTemplate().isForPet()) {
            activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_EQUIP_A_PET_ITEM);
            return;
        }
        if (activeChar.isOutOfControl() || activeChar.isDead() || activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed()) {
            activeChar.sendActionFailed();
            return;
        }
        if (item.getTemplate().isQuest() && item.getTemplate().getActionType() == ItemData.ItemAction.action_none && (item.getTemplate().getAttachedSkills() == null || item.getTemplate().getAttachedSkills().length == 0)) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS);
            return;
        }
        //FIXME[Hack]: быдлокод. Убрать после переписи ивент двигла
        if (activeChar.isInActivePvpEvent()
                && (item.getTemplate().getItemType() == EtcItemTemplate.EtcItemType.SCROLL || item.getTemplate().getItemType() == EtcItemTemplate.EtcItemType.POTION)
                && !ArrayUtils.contains(EventsConfig.allowedItemsOnPVP, item.getItemId())) {
            return;
        }

        if (activeChar.isInLastHero()
                && (item.getTemplate().getItemType() == EtcItemTemplate.EtcItemType.SCROLL || item.getTemplate().getItemType() == EtcItemTemplate.EtcItemType.POTION)
                && !ArrayUtils.contains(EventsConfig.allowedItems, item.getItemId())) {
            return;
        }
        if (item.isPopup()) {
            final ConfirmDlg dlg = new ConfirmDlg(SystemMsg.valueOf(item.getPopup()), 0);
            activeChar.ask(dlg, new OnAnswerListener() {
                @Override
                public void sayYes() {
                    final boolean success = item.getTemplate().getHandler().useItem(activeChar, item, _ctrlPressed);
                    if (success) {
                        final long nextTimeUse = item.getTemplate().getReuseType().next(item);
                        if (nextTimeUse >= System.currentTimeMillis()) {
                            final TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, item.getTemplate().getReuseDelay());
                            activeChar.addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);
                            if (item.getTemplate().getReuseDelay() > 0) {
                                activeChar.sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
                            }
                        }
                    }
                }

                @Override
                public void sayNo() {
                }
            });
        } else {
            final boolean success = item.getTemplate().getHandler().useItem(activeChar, item, _ctrlPressed);
            if (success) {
                final long nextTimeUse = item.getTemplate().getReuseType().next(item);
                if (nextTimeUse >= System.currentTimeMillis()) {
                    final TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, item.getTemplate().getReuseDelay());
                    activeChar.addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);
                    if (item.getTemplate().getReuseDelay() > 0) {
                        activeChar.sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
                    }
                }
            }
        }
    }

    public static boolean addOfflineItem(int chObjId, int itemId, int itemCount, String reason) {
        try {
            if (chObjId <= 0)
                return false;
            PreparedStatement st = DatabaseFactory.getInstance().getConnection().prepareStatement(INSERT_QUERY);
            st.setInt(1, chObjId);
            st.setInt(2, itemId);
            st.setInt(3, itemCount);
            st.setString(4, reason);
            st.execute();
            DbUtils.closeQuietly(st);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getItemName(int itemId) {
        ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (template != null)
            return template.getName();
        else
            return "";
    }

    public static void clearItems(Player player, int... itemIds) {
        Inventory inventory = player.getInventory();
        for (int id : itemIds)
            inventory.removeItemByItemId(id, inventory.getCountOf(id));
    }

    public static boolean getPay(Player player, int itemId, long itemCount) {
        if (player.getInventory().destroyItemByItemId(itemId, itemCount)) {
            player.sendPacket(SystemMessage.removeItems(itemId, itemCount));
            return true;
        } else {
            player.sendMessage(new CustomMessage("util.enoughItemCount").addString(formatPay(player, itemCount, itemId)));
            return false;
        }
    }

    public static void addItem(Player player, int itemId, long itemCount) {
        player.getInventory().addItem(itemId, itemCount);
        player.sendPacket(SystemMessage.obtainItems(itemId, itemCount, 0));
    }

    public static boolean removeItem(Player player, ItemInstance item) {
        if (player.getInventory().destroyItem(item)) {
            player.sendPacket(SystemMessage.removeItems(item));
            return true;
        } else {
            player.sendMessage("You don't have enough items!");
            return false;
        }
    }

    public static boolean checkIsAllowedName(String name) {
        for (String disallowedName : ServerConfig.disallowedNames)
            if (disallowedName.toLowerCase().equals(name.toLowerCase()))
                return false;
        return true;
    }

    public static boolean checkIsAllowedTitle(String title) {
        for (String disallowedTitle : ServerConfig.disallowedTitles)
            if (disallowedTitle.toLowerCase().equals(title.toLowerCase()))
                return false;
        return true;
    }
}
