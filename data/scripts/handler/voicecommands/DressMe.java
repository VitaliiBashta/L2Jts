package handler.voicecommands;

import handler.bbs.abstracts.AbstractCommunityBoard;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.*;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.dress.DressArmorData;
import org.mmocore.gameserver.model.dress.DressCloakData;
import org.mmocore.gameserver.model.dress.DressShieldData;
import org.mmocore.gameserver.model.dress.DressWeaponData;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.CharInfo;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ItemType;
import org.mmocore.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hack
 * Date: 17.08.2016 18:41
 * @author L2CCCP
 */
public class DressMe implements IVoicedCommandHandler, OnInitScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(DressMe.class);

    private static Map<Integer, DressWeaponData> SWORD;
    private static Map<Integer, DressWeaponData> BLUNT;
    private static Map<Integer, DressWeaponData> DAGGER;
    private static Map<Integer, DressWeaponData> BOW;
    private static Map<Integer, DressWeaponData> POLE;
    private static Map<Integer, DressWeaponData> FIST;
    private static Map<Integer, DressWeaponData> DUAL;
    private static Map<Integer, DressWeaponData> DUALFIST;
    private static Map<Integer, DressWeaponData> BIGSWORD;
    private static Map<Integer, DressWeaponData> ROD;
    private static Map<Integer, DressWeaponData> BIGBLUNT;
    private static Map<Integer, DressWeaponData> CROSSBOW;
    private static Map<Integer, DressWeaponData> RAPIER;
    private static Map<Integer, DressWeaponData> ANCIENTSWORD;
    private static Map<Integer, DressWeaponData> DUALDAGGER;

    private String[] _commandList = new String[] {
            "dressme",
            "dressme-armor",
            "dressme-cloak",
            "dressme-shield",
            "dressme-weapon",
            "dress-armor",
            "dress-cloak",
            "dress-shield",
            "dress-weapon",
            "dress-armorpage",
            "dress-cloakpage",
            "dress-shieldpage",
            "dress-weaponpage",
            "dressinfo",
            "undressme",
            "undressme-armor",
            "undressme-cloak",
            "undressme-shield",
            "undressme-weapon" };

    @Override
    public boolean useVoicedCommand(String command, Player player, String args)
    {
        if(command.equals("dressme"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index.htm");
            html.replace("%weapons%", Util.formatAdena(DressWeaponHolder.getInstance().size()));
            html.replace("%armors%", Util.formatAdena(DressArmorHolder.getInstance().size()));
            html.replace("%shields%", Util.formatAdena(DressShieldHolder.getInstance().size()));
            html.replace("%cloaks%", Util.formatAdena(DressCloakHolder.getInstance().size()));
            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dressme-armor"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index-armor.htm");
            String template = HtmCache.getInstance().getHtml("command/dressme/template-armor.htm", player);
            String block = "";
            String list = "";

            if(args == null)
                args = "1";

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            for(int i = (page - 1) * perpage; i < DressArmorHolder.getInstance().size(); i++)
            {
                DressArmorData dress = DressArmorHolder.getInstance().getArmor(i + 1);
                if(dress != null)
                {
                    block = template;

                    String dress_name = dress.getName();

                    if(dress_name.length() > 29)
                        dress_name = dress_name.substring(0, 29) + "...";

                    block = block.replace("{bypass}", "bypass -h user_dress-armorpage " + (i + 1));
                    block = block.replace("{name}", dress_name);
                    block = block.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
                    block = block.replace("{icon}", ItemTemplateHolder.getInstance().getTemplate(dress.getChest()).getIcon());
                    list += block;
                }

                counter++;

                if(counter >= perpage)
                    break;
            }

            double count = Math.ceil((double) DressArmorHolder.getInstance().size() / (double) perpage);
            int inline = 1;
            String navigation = "";

            for(int i = 1; i <= count; i++)
            {
                if(i == page)
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                else
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";

                if(inline % 7 == 0)
                    navigation += "</tr><tr>";

                inline++;
            }

            if(navigation.equals(""))
                navigation = "<td width=30 align=center valign=top>...</td>";

            html.replace("%list%", list);
            html.replace("%navigation%", navigation);

            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dressme-cloak"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index-cloak.htm");
            String template = HtmCache.getInstance().getHtml("command/dressme/template-cloak.htm", player);
            String block = "";
            String list = "";

            if(args == null)
                args = "1";

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            for(int i = (page - 1) * perpage; i < DressCloakHolder.getInstance().size(); i++)
            {
                DressCloakData cloak = DressCloakHolder.getInstance().getCloak(i + 1);
                if(cloak != null)
                {
                    block = template;

                    String cloak_name = cloak.getName();

                    if(cloak_name.length() > 29)
                        cloak_name = cloak_name.substring(0, 29) + "...";

                    block = block.replace("{bypass}", "bypass -h user_dress-cloakpage " + (i + 1));
                    block = block.replace("{name}", cloak_name);
                    block = block.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
                    block = block.replace("{icon}", Util.getItemIcon(cloak.getCloakId()));
                    list += block;
                }

                counter++;

                if(counter >= perpage)
                    break;
            }

            double count = Math.ceil((double) DressCloakHolder.getInstance().size() / (double) perpage);
            int inline = 1;
            String navigation = "";

            for(int i = 1; i <= count; i++)
            {
                if(i == page)
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                else
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";

                if(inline % 7 == 0)
                    navigation += "</tr><tr>";

                inline++;
            }

            if(navigation.equals(""))
                navigation = "<td width=30 align=center valign=top>...</td>";

            html.replace("%list%", list);
            html.replace("%navigation%", navigation);

            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dressme-shield"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index-shield.htm");
            String template = HtmCache.getInstance().getHtml("command/dressme/template-shield.htm", player);
            String block = "";
            String list = "";

            if(args == null)
                args = "1";

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            for(int i = (page - 1) * perpage; i < DressShieldHolder.getInstance().size(); i++)
            {
                DressShieldData shield = DressShieldHolder.getInstance().getShield(i + 1);
                if(shield != null)
                {
                    block = template;

                    String shield_name = shield.getName();

                    if(shield_name.length() > 29)
                        shield_name = shield_name.substring(0, 29) + "...";

                    block = block.replace("{bypass}", "bypass -h user_dress-shieldpage " + (i + 1));
                    block = block.replace("{name}", shield_name);
                    block = block.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
                    block = block.replace("{icon}", Util.getItemIcon(shield.getShieldId()));
                    list += block;
                }

                counter++;

                if(counter >= perpage)
                    break;
            }

            double count = Math.ceil((double) DressShieldHolder.getInstance().size() / (double) perpage);
            int inline = 1;
            String navigation = "";

            for(int i = 1; i <= count; i++)
            {
                if(i == page)
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                else
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";

                if(inline % 7 == 0)
                    navigation += "</tr><tr>";

                inline++;
            }

            if(navigation.equals(""))
                navigation = "<td width=30 align=center valign=top>...</td>";

            html.replace("%list%", list);
            html.replace("%navigation%", navigation);

            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dressme-weapon"))
        {
            ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
            if(slot == null)
            {
                player.sendMessage("Error: Weapon must be equiped!");
                return false;
            }

            ItemType type = slot.getItemType();

            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index-weapon.htm");
            String template = HtmCache.getInstance().getHtml("command/dressme/template-weapon.htm", player);
            String block = "";
            String list = "";

            if(args == null)
                args = "1";

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;
            Map<Integer, DressWeaponData> map = new HashMap<Integer, DressWeaponData>();

            map = initMap(type.toString());

            if(map == null)
            {
                player.sendMessage(player.isLangRus() ? "Ваше оружие не поддерживается!" : "Your weapon type is not supported!");
                _log.error("Dress me system: Weapon Map is null.");
                return false;
            }

            for(int i = (page - 1) * perpage; i < map.size(); i++)
            {
                DressWeaponData weapon = map.get(i + 1);
                if(weapon != null)
                {
                    block = template;

                    String cloak_name = weapon.getName();

                    if(cloak_name.length() > 29)
                        cloak_name = cloak_name.substring(0, 29) + "...";

                    block = block.replace("{bypass}", "bypass -h user_dress-weaponpage " + weapon.getId());
                    block = block.replace("{name}", cloak_name);
                    block = block.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
                    block = block.replace("{icon}", Util.getItemIcon(weapon.getId()));
                    list += block;
                }

                counter++;

                if(counter >= perpage)
                    break;
            }

            double count = Math.ceil((double) map.size() / (double) perpage);
            int inline = 1;
            String navigation = "";

            for(int i = 1; i <= count; i++)
            {
                if(i == page)
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                else
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";

                if(inline % 7 == 0)
                    navigation += "</tr><tr>";

                inline++;
            }

            if(navigation.equals(""))
                navigation = "<td width=30 align=center valign=top>...</td>";

            html = html.replace("%list%", list);
            html = html.replace("%navigation%", navigation);

            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dress-armorpage"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);
            DressArmorData dress = DressArmorHolder.getInstance().getArmor(set);
            if(dress != null)
            {
                HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/dress-armor.htm");

                Inventory inv = player.getInventory();

                ItemInstance my_chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                html.replace("%my_chest_icon%", my_chest == null ? "icon.NOIMAGE" : my_chest.getTemplate().getIcon());
                ItemInstance my_legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
                html.replace("%my_legs_icon%", my_legs == null ? "icon.NOIMAGE" : my_legs.getTemplate().getIcon());
                ItemInstance my_gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
                html.replace("%my_gloves_icon%", my_gloves == null ? "icon.NOIMAGE" : my_gloves.getTemplate().getIcon());
                ItemInstance my_feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
                html.replace("%my_feet_icon%", my_feet == null ? "icon.NOIMAGE" : my_feet.getTemplate().getIcon());

                html.replace("%bypass%", "bypass -h user_dress-armor " + set);
                html.replace("%name%", dress.getName());
                html.replace("%price%", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));

                // тело должно быть всегда
                ItemTemplate chest = ItemTemplateHolder.getInstance().getTemplate(dress.getChest());
                html.replace("%chest_icon%", chest.getIcon());
                html.replace("%chest_name%", chest.getName());
                html.replace("%chest_grade%", chest.getItemGrade().name());

                if(dress.getLegs() != -1) {
                    ItemTemplate legs = ItemTemplateHolder.getInstance().getTemplate(dress.getLegs());
                    html.replace("%legs_icon%", legs.getIcon());
                    html.replace("%legs_name%", legs.getName());
                    html.replace("%legs_grade%", legs.getItemGrade().name());
                } else {
                    html.replace("%legs_icon%", "icon.NOIMAGE");
                    html.replace("%legs_name%", "<font color=FF0000>...</font>");
                    html.replace("%legs_grade%", "NO");
                }

                if (dress.getGloves() != -1) {
                    ItemTemplate gloves = ItemTemplateHolder.getInstance().getTemplate(dress.getGloves());
                    html.replace("%gloves_icon%", gloves.getIcon());
                    html.replace("%gloves_name%", gloves.getName());
                    html.replace("%gloves_grade%", gloves.getItemGrade().name());
                } else {
                    html.replace("%gloves_icon%", "icon.NOIMAGE");
                    html.replace("%gloves_name%", "<font color=FF0000>...</font>");
                    html.replace("%gloves_grade%", "NO");
                }

                if (dress.getFeet() != -1) {
                    ItemTemplate feet = ItemTemplateHolder.getInstance().getTemplate(dress.getFeet());
                    html.replace("%feet_icon%", feet.getIcon());
                    html.replace("%feet_name%", feet.getName());
                    html.replace("%feet_grade%", feet.getItemGrade().name());
                } else {
                    html.replace("%feet_icon%", "icon.NOIMAGE");
                    html.replace("%feet_name%", "<font color=FF0000>...</font>");
                    html.replace("%feet_grade%", "NO");
                }

                player.sendPacket(html);
                return true;
            }
            else
                return false;

        }
        else if(command.equals("dress-cloakpage"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);
            DressCloakData cloak = DressCloakHolder.getInstance().getCloak(set);
            if(cloak != null)
            {
                HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/dress-cloak.htm");

                Inventory inv = player.getInventory();

                ItemInstance my_cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);
                html = html.replace("%my_cloak_icon%", my_cloak == null ? "icon.NOIMAGE" : my_cloak.getTemplate().getIcon());

                html = html.replace("%bypass%", "bypass -h user_dress-cloak " + cloak.getId());
                html = html.replace("%name%", cloak.getName());
                html = html.replace("%price%", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));

                ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(cloak.getCloakId());
                html = html.replace("%item_icon%", item.getIcon());
                html = html.replace("%item_name%", item.getName());
                html = html.replace("%item_grade%", item.getItemGrade().name());

                player.sendPacket(html);
                return true;
            }
            else
                return false;
        }
        else if(command.equals("dress-shieldpage"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);
            DressShieldData shield = DressShieldHolder.getInstance().getShield(set);
            if(shield != null)
            {
                HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/dress-shield.htm");

                Inventory inv = player.getInventory();

                ItemInstance my_shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                html = html.replace("%my_shield_icon%", my_shield == null ? "icon.NOIMAGE" : my_shield.getTemplate().getIcon());

                html = html.replace("%bypass%", "bypass -h user_dress-shield " + shield.getId());
                html = html.replace("%name%", shield.getName());
                html = html.replace("%price%", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));

                ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(shield.getShieldId());
                html = html.replace("%item_icon%", item.getIcon());
                html = html.replace("%item_name%", item.getName());
                html = html.replace("%item_grade%", item.getItemGrade().name());

                player.sendPacket(html);
                return true;
            }
            else
                return false;
        }
        else if(command.equals("dress-weaponpage"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);
            DressWeaponData weapon = DressWeaponHolder.getInstance().getWeapon(set);
            if(weapon != null)
            {
                HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/dress-weapon.htm");

                Inventory inv = player.getInventory();

                ItemInstance my_weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

                html = html.replace("%my_weapon_icon%", my_weapon == null ? "icon.NOIMAGE" : my_weapon.getTemplate().getIcon());

                html = html.replace("%bypass%", "bypass -h user_dress-weapon " + weapon.getId());
                html = html.replace("%name%", weapon.getName());
                html = html.replace("%price%", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));

                ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(weapon.getId());
                html = html.replace("%item_icon%", item.getIcon());
                html = html.replace("%item_name%", item.getName());
                html = html.replace("%item_grade%", item.getItemGrade().name());

                player.sendPacket(html);
                return true;
            }
            else
                return false;
        }
        else if(command.equals("dressinfo"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/info.htm");
            player.sendPacket(html);
            return true;
        }
        else if(command.equals("dress-armor"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);

            DressArmorData dress = DressArmorHolder.getInstance().getArmor(set);
            Inventory inv = player.getInventory();

            ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);

            if(chest == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте кирасу!" : "Error: Chest must be equiped.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            ItemTemplate visual = ItemTemplateHolder.getInstance().getTemplate(dress.getChest());
            if((chest.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR
                    || visual.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
                    && chest.getTemplate().getBodyPart() != visual.getBodyPart())
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: невозможно комбинировать кирасу с полным доспехом!" : "Error: You can't change visual chest to full body and on the contrary!");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);

            if(legs == null && chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте поножи!" : "Error: Legs must be equiped.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);

            if(gloves == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте перчатки!" :"Error: Gloves must be equiped.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

            if(feet == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте ботинки!" : "Error: Feet must be equiped.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            List<Integer> visualItems = new ArrayList<>();
            visualItems.add(dress.getChest());
            if (dress.getLegs() != -1)
                visualItems.add(dress.getLegs());
            visualItems.add(dress.getGloves());
            visualItems.add(dress.getFeet());

            if(checkVisualItems(player, ArrayUtils.toArray(visualItems))
                    && AbstractCommunityBoard.getPay(player, dress.getPriceId(), dress.getPriceCount(), true))
            {
                player.getInventory().destroyItemByItemId(dress.getPriceId(), dress.getPriceCount());
                if (!destroyVisualItems(player, ArrayUtils.toArray(visualItems)))
                    return false;
                visuality(player, chest, dress.getChest(), dress.isCostume());

                if(dress.getLegs() != -1)
                    visuality(player, legs, dress.getLegs());

                visuality(player, gloves, dress.getGloves());
                visuality(player, feet, dress.getFeet());

                player.sendUserInfo(true);
                player.broadcastUserInfo(true);
                return true;
            }
            else {
                player.sendMessage(player.isLangRus() ? "Не хватает необходимых предметов или адены." : "You do not have the necessary items or adena.");
                return false;
            }

        }
        else if(command.equals("dress-cloak"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);

            DressCloakData cloak_data = DressCloakHolder.getInstance().getCloak(set);
            Inventory inv = player.getInventory();

            ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);

            if(cloak == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте плащ!" : "Error: Cloak must be equiped.");
                useVoicedCommand("dress-cloakpage", player, args);
                return false;
            }

            if(checkVisualItems(player, cloak_data.getCloakId())
                    && AbstractCommunityBoard.getPay(player, cloak_data.getPriceId(), cloak_data.getPriceCount(), true))
            {
                player.getInventory().destroyItemByItemId(cloak_data.getPriceId(), cloak_data.getPriceCount());
                if(!destroyVisualItems(player, cloak_data.getCloakId()))
                    return false;
                visuality(player, cloak, cloak_data.getCloakId());

                player.sendUserInfo(true);
                player.broadcastUserInfo(true);
                return true;
            }
            else {
                player.sendMessage(player.isLangRus() ? "Не хватает необходимых предметов или адены." : "You do not have the necessary items or adena.");
                return false;
            }

        }
        else if(command.equals("dress-shield"))
        {
            final int shield_id = Integer.parseInt(args.split(" ")[0]);

            DressShieldData shield_data = DressShieldHolder.getInstance().getShield(shield_id);
            Inventory inv = player.getInventory();

            ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);

            if(shield == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте щит!" : "Error: Shield must be equiped.");
                useVoicedCommand("dress-shieldpage", player, args);
                return false;
            }

            if(checkVisualItems(player, shield_data.getShieldId())
                    && AbstractCommunityBoard.getPay(player, shield_data.getPriceId(), shield_data.getPriceCount(), true))
            {
                player.getInventory().destroyItemByItemId(shield_data.getPriceId(), shield_data.getPriceCount());
                if (!destroyVisualItems(player, shield_data.getShieldId()))
                    return false;
                visuality(player, shield, shield_data.getShieldId());

                player.sendUserInfo(true);
                player.broadcastUserInfo(true);
                return true;
            }
            else {
                player.sendMessage(player.isLangRus() ? "Не хватает необходимых предметов или адены." : "You do not have the necessary items or adena.");
                return false;
            }

        }
        else if(command.equals("dress-weapon"))
        {
            final int set = Integer.parseInt(args.split(" ")[0]);

            DressWeaponData weapon_data = DressWeaponHolder.getInstance().getWeapon(set);
            Inventory inv = player.getInventory();

            ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

            if(weapon == null)
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оденьте оружие!" : "Error: Weapon must be equiped.");
                useVoicedCommand("dress-weaponpage", player, args);
                return false;
            }

            if(!weapon.getItemType().toString().equals(weapon_data.getType()))
            {
                player.sendMessage(player.isLangRus() ? "Ошибка: оружие должно быть одного типа." : "Error: Weapon must be equals type.");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if(checkVisualItems(player, weapon_data.getId())
                    && AbstractCommunityBoard.getPay(player, weapon_data.getPriceId(), weapon_data.getPriceCount(), true))
            {
//                player.getInventory().destroyItemByItemId(weapon_data.getPriceId(), weapon_data.getPriceCount());
                if (!destroyVisualItems(player, weapon_data.getId()))
                    return false;
                visuality(player, weapon, weapon_data.getId());

                player.sendUserInfo(true);
                player.broadcastUserInfo(true);
                return true;
            }
            else {
                player.sendMessage(player.isLangRus() ? "Не хватает необходимых предметов или адены." : "You do not have the necessary items or adena.");
                return false;
            }
        }
        else if(command.equals("undressme"))
        {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/undressme.htm");
            html.replace("%weapons%", Util.formatAdena(DressWeaponHolder.getInstance().size()));
            html.replace("%armors%", Util.formatAdena(DressArmorHolder.getInstance().size()));
            html.replace("%shields%", Util.formatAdena(DressShieldHolder.getInstance().size()));
            html.replace("%cloaks%", Util.formatAdena(DressCloakHolder.getInstance().size()));
            player.sendPacket(html);
            return true;
        }
        else if(command.equals("undressme-armor"))
        {
            Inventory inv = player.getInventory();
            ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
            ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
            ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

            tryUnvisual(player, chest, legs, gloves, feet);
            return true;
        }
        else if(command.equals("undressme-cloak"))
        {
            Inventory inv = player.getInventory();
            ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);

            tryUnvisual(player, cloak);
            return true;
        }
        else if(command.equals("undressme-shield"))
        {
            Inventory inv = player.getInventory();
            ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);

            tryUnvisual(player, shield);
            return true;
        }
        else if(command.equals("undressme-weapon"))
        {
            Inventory inv = player.getInventory();
            ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

            tryUnvisual(player, weapon);
            return true;
        }
        else
            return false;
    }

    private void tryUnvisual(Player player, ItemInstance... items)
    {
        boolean change = false;
        try {
            player.getInventory().writeLock();
            for (ItemInstance item : items) {
                if (item != null) {
                    change = true;
                    if (item.getVisualItemId() > 0)
                        player.getInventory().addItem(item.getVisualItemId(), 1);
                    visuality(player, item, 0);
                }
            }
        } finally {
            player.getInventory().writeUnlock();
        }

        if(change) // Лишний раз чтобы не отправлять пакеты!
        {
            player.sendUserInfo(true);
            player.broadcastUserInfo(true);
        }

        useVoicedCommand("undressme", player, null);
    }

    private Map<Integer, DressWeaponData> initMap(String type)
    {
        if(type.equals("Sword"))
            return SWORD;
        else if(type.equals("Blunt"))
            return BLUNT;
        else if(type.equals("Dagger"))
            return DAGGER;
        else if(type.equals("Bow"))
            return BOW;
        else if(type.equals("Pole"))
            return POLE;
        else if(type.equals("Fist"))
            return FIST;
        else if(type.equals("Dual Sword"))
            return DUAL;
        else if(type.equals("Dual Fist"))
            return DUALFIST;
        else if(type.equals("Big Sword"))
            return BIGSWORD;
        else if(type.equals("Rod"))
            return ROD;
        else if(type.equals("Big Blunt"))
            return BIGBLUNT;
        else if(type.equals("Crossbow"))
            return CROSSBOW;
        else if(type.equals("Rapier"))
            return RAPIER;
        else if(type.equals("Ancient Sword"))
            return ANCIENTSWORD;
        else if(type.equals("Dual Dagger"))
            return DUALDAGGER;
        else
        {
            _log.error("Dress me system: Unknown type: " + type);
            return null;
        }
    }

    private int parseWeapon()
    {
        int Sword = 0, Blunt = 0, Dagger = 0, Bow = 0, Pole = 0, DualSword = 0, DualFist = 0, BigSword = 0, Rod = 0, BigBlunt = 0, Crossbow = 0, Rapier = 0, AncientSword = 0, DualDagger = 0;

        for(DressWeaponData weapon : DressWeaponHolder.getInstance().getAllWeapons())
        {
            if(weapon.getType().equals("Sword"))
            {
                Sword++;
                SWORD.put(Sword, weapon);
            }
            else if(weapon.getType().equals("Blunt"))
            {
                Blunt++;
                BLUNT.put(Blunt, weapon);
            }
            else if(weapon.getType().equals("Dagger"))
            {
                Dagger++;
                DAGGER.put(Dagger, weapon);
            }
            else if(weapon.getType().equals("Bow"))
            {
                Bow++;
                BOW.put(Bow, weapon);
            }
            else if(weapon.getType().equals("Pole"))
            {
                Pole++;
                POLE.put(Pole, weapon);
            }
            else if(weapon.getType().equals("Dual Sword"))
            {
                DualSword++;
                DUAL.put(DualSword, weapon);
            }
            else if(weapon.getType().equals("Dual Fist"))
            {
                DualFist++;
                DUALFIST.put(DualFist, weapon);
            }
            else if(weapon.getType().equals("Big Sword"))
            {
                BigSword++;
                BIGSWORD.put(BigSword, weapon);
            }
            else if(weapon.getType().equals("Rod"))
            {
                Rod++;
                ROD.put(Rod, weapon);
            }
            else if(weapon.getType().equals("Big Blunt"))
            {
                BigBlunt++;
                BIGBLUNT.put(BigBlunt, weapon);
            }
            else if(weapon.getType().equals("Crossbow"))
            {
                Crossbow++;
                CROSSBOW.put(Crossbow, weapon);
            }
            else if(weapon.getType().equals("Rapier"))
            {
                Rapier++;
                RAPIER.put(Rapier, weapon);
            }
            else if(weapon.getType().equals("Ancient Sword"))
            {
                AncientSword++;
                ANCIENTSWORD.put(AncientSword, weapon);
            }
            else if(weapon.getType().equals("Dual Dagger"))
            {
                DualDagger++;
                DUALDAGGER.put(DualDagger, weapon);
            }
            else
                _log.error("Dress me system: Can't find type: " + weapon.getType());
        }

        _log.info("Dress me system: Load " + Sword + " Sword(s).");
        _log.info("Dress me system: Load " + Blunt + " Blunt(s).");
        _log.info("Dress me system: Load " + Dagger + " Dagger(s).");
        _log.info("Dress me system: Load " + Bow + " Bow(s).");
        _log.info("Dress me system: Load " + Pole + " Pole(s).");
        _log.info("Dress me system: Load " + DualSword + " Dual Sword(s).");
        _log.info("Dress me system: Load " + DualFist + " Dual Fist(s).");
        _log.info("Dress me system: Load " + BigSword + " Big Sword(s).");
        _log.info("Dress me system: Load " + Rod + " Rod(s).");
        _log.info("Dress me system: Load " + BigBlunt + " Big Blunt(s).");
        _log.info("Dress me system: Load " + Crossbow + " Crossbow(s).");
        _log.info("Dress me system: Load " + Rapier + " Rapier(s).");
        _log.info("Dress me system: Load " + AncientSword + " Ancient Sword(s).");
        _log.info("Dress me system: Load " + DualDagger + " Dual Dagger(s).");

        return 0;
    }

    private void visuality(Player player, ItemInstance item, int visual) {
        visuality(player, item, visual, false);
    }

    private void visuality(Player player, ItemInstance item, int visual, boolean isCostume)
    {
        if (visual == -1)
            return;
        item.setVisualItemId(visual);
        item.setCostume(isCostume);

        if(visual == 0)
        {
            item.setCustomFlags(item.getCustomFlags() & ~ItemInstance.FLAG_COSTUME);
            player.sendMessage(player.isLangRus() ? "Визуальное изменение с предмета" + item.getName() + " было удалено."
                    : "Visual change from " + item.getName() + " has been remove.");
        }
        else
            player.sendMessage(item.getName() + (player.isLangRus() ? " успешно изменил внешний вид." : " has been visual change."));

        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
    }

    @Override
    public void onInit()
    {
        SWORD = new HashMap<Integer, DressWeaponData>();
        BLUNT = new HashMap<Integer, DressWeaponData>();
        DAGGER = new HashMap<Integer, DressWeaponData>();
        BOW = new HashMap<Integer, DressWeaponData>();
        POLE = new HashMap<Integer, DressWeaponData>();
        FIST = new HashMap<Integer, DressWeaponData>();
        DUAL = new HashMap<Integer, DressWeaponData>();
        DUALFIST = new HashMap<Integer, DressWeaponData>();
        BIGSWORD = new HashMap<Integer, DressWeaponData>();
        ROD = new HashMap<Integer, DressWeaponData>();
        BIGBLUNT = new HashMap<Integer, DressWeaponData>();
        CROSSBOW = new HashMap<Integer, DressWeaponData>();
        RAPIER = new HashMap<Integer, DressWeaponData>();
        ANCIENTSWORD = new HashMap<Integer, DressWeaponData>();
        DUALDAGGER = new HashMap<Integer, DressWeaponData>();

        parseWeapon();
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return LostDreamCustom.AllowDressService ? _commandList : new String[0];
    }

    private boolean destroyVisualItems(Player player, int ... visualItems) {
        PcInventory inventory = player.getInventory();
        try {
            inventory.writeLock();
            if (!checkVisualItems(player, visualItems))
                return false;
            for (int itemId : visualItems) {
                if (itemId == -1)
                    continue;
                inventory.destroyItemByItemId(itemId, 1);
            }
            return true;
        } finally {
            inventory.writeUnlock();
        }
    }

    private boolean checkVisualItems(Player player, int ... visualItems) {
        PcInventory inventory = player.getInventory();
        for (int itemId : visualItems) {
            if (itemId == -1)
                continue;
            if (inventory.getItemByItemId(itemId) == null)
                return false;
        }
        return true;
    }
}
