package org.mmocore.gameserver.phantoms;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomEquipHolder;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.phantoms.ai.AbstractPhantomAi;
import org.mmocore.gameserver.phantoms.ai.PhantomAiType;
import org.mmocore.gameserver.phantoms.ai.PhantomTownAi;
import org.mmocore.gameserver.phantoms.model.Phantom;
import org.mmocore.gameserver.phantoms.template.PhantomEquipTemplate;
import org.mmocore.gameserver.phantoms.template.PhantomTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 21.08.2016 4:43
 */
public class PhantomFactory {
    private static final int defaultNameColor = 0xFFFFFF;
    private static final int defaultTitleColor = 0xFFFF77;
    private static final PhantomFactory instance = new PhantomFactory();

    public static PhantomFactory getInstance() {
        return instance;
    }

    public Phantom create(PhantomTemplate template) {
        int objId = IdFactory.getInstance().getNextId();
        int nameColor = template.getNameColor() == 0 ? defaultNameColor : template.getNameColor();
        int titleColor = template.getTitleColor() == 0 ? defaultTitleColor : template.getTitleColor();
        String title = template.getTitle() == null ? "" : template.getTitle();
        Phantom phantom = new Phantom(objId, "bot_" + objId);
        phantom.getPlayerTemplateComponent().setPlayerRace(PlayerRace.values()[template.getRace()]);
        phantom.getPlayerTemplateComponent().setPlayerSex(PlayerSex.values()[template.getSex()]);
        SubClass sub = new SubClass();
        sub.setActive(true);
        sub.setBase(true);
        sub.setClassId(template.getClassId());
        phantom.getPlayerClassComponent().setActiveClass(sub);
        phantom.getPlayerClassComponent().setClassId(template.getClassId(), true, false);
        phantom.getPlayerClassComponent().setBaseClass(template.getClassId());
        phantom.getAppearanceComponent().setFace(template.getFace());
        phantom.getAppearanceComponent().setHairStyle(template.getHair());
        phantom.getAppearanceComponent().setHairColor(0); //TODO
        phantom.getAppearanceComponent().setNameColor(nameColor);
        phantom.getAppearanceComponent().setTitleColor(titleColor);
        phantom.setName(template.getName());
        phantom.setTitle(title);
        phantom.addExpAndSp(ExpDataHolder.getInstance().getExpForLevel(getLevel(template)), 0);
        phantom.setOnlineStatus(true);
        phantom.entering = false;
        phantom.setAI(getAi(phantom, template.getType()));
        phantom.setRunning();
        setEquip(phantom, template.getGrade());
        return phantom;
    }

    private void setEquip(Phantom phantom, ItemTemplate.Grade grade) {
        PhantomEquipTemplate template = PhantomEquipHolder.getInstance()
                .getClassEquip(phantom.getPlayerClassComponent().getActiveClassId());
        if (template == null)
            return;
        List<Integer> items = new ArrayList<>();
        items.add(template.getRandomWeaponId(grade));
        items.add(template.getRandomShieldId(grade));
        items.addAll(template.getRandomArmor(grade).getIds());
        for (int id : items) {
            if (id == 0) continue;
            ItemInstance item = ItemFunctions.createItem(id);
            item.setPhantomItem();
            if (grade != ItemTemplate.Grade.NONE)
                item.setEnchantLevel(getEnchant());
            phantom.getInventory().addItem(item);
            phantom.getInventory().equipItem(item);
        }
    }

    private int getLevel(PhantomTemplate template) {
        if (PhantomConfig.everybodyMaxLevel)
            return 85;
        switch (template.getGrade()) {
            case NONE:
                return Rnd.get(1, 19);
            case D:
                return Rnd.get(20, 39);
            case C:
                return Rnd.get(40, 51);
            case B:
                return Rnd.get(52, 60);
            case A:
                return Rnd.get(61, 75);
            case S:
                return Rnd.get(76, 79);
            case S80:
                return Rnd.get(80, 83);
            case S84:
                return Rnd.get(84, 85);
            default:
                return 1;
        }
    }

    private int getEnchant() {
        int enchant = PhantomConfig.minEnchant;
        for (; enchant < PhantomConfig.maxEnchant; enchant++)
            if (!Rnd.chance(PhantomConfig.enchantChance))
                break;
        return enchant;
    }

    private AbstractPhantomAi getAi(Phantom phantom, PhantomAiType type) {
        switch (type) {
            case TOWN:
                return new PhantomTownAi(phantom);
            default:
                return new PhantomTownAi(phantom);
        }
    }
}
