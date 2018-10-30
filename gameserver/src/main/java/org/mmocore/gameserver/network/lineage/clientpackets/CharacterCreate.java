package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.common.ItemName_Count;
import org.jts.dataparser.data.holder.setting.common.SettingUtils;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.xml.holder.PhantomHolder;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterCreateFail;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterCreateSuccess;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterSelectionInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterCreate extends L2GameClientPacket {
    // cSdddddddddddd
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterCreate.class);
    private String name;
    private int sex;
    private int classId;
    private int raceId;
    private int hairStyle;
    private int hairColor;
    private int face;

    @Override
    protected void readImpl() {
        name = readS();
        raceId = readD(); // race
        sex = readD();
        classId = readD();
        readD(); // int
        readD(); // str
        readD(); // con
        readD(); // men
        readD(); // dex
        readD(); // wit
        hairStyle = readD();
        hairColor = readD();
        face = readD();
    }

    @Override
    protected void runImpl() {
        for (final ClassId cid : ClassId.VALUES) {
            if (cid.getId() == classId && !cid.isOfLevel(ClassLevel.First)) {
                return;
            }
        }
        if (CharacterDAO.getInstance().accountCharNumber(getClient().getLogin()) >= 7) {
            sendPacket(CharacterCreateFail.REASON_TOO_MANY_CHARACTERS);
            return;
        }
        if (!Util.isMatchingRegexp(name, ServerConfig.CNAME_TEMPLATE)) {
            sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
            return;
        } else if (NpcNameLineHolder.getInstance().isBlackListContainsName(name) || !Util.checkIsAllowedName(name)) {
            sendPacket(CharacterCreateFail.REASON_INCORRECT_NAME);
            return;
        } else if (CharacterDAO.getInstance().getObjectIdByName(name) > 0 || PhantomHolder.getInstance().isNameExists(name)) {
            sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
            return;
        }

        if (!validateParams())
            return;

        final Player newChar = Player.create(classId, raceId, sex, getClient().getLogin(), name, hairStyle, hairColor, face);
        if (newChar == null) {
            sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
            return;
        }

        sendPacket(CharacterCreateSuccess.STATIC);

        initNewChar(getClient(), newChar);
    }

    /**
     * L2JServer hints
     *
     * @return bool
     */
    private boolean validateParams() {
        if (face > 2 || face < 0) {
            LOGGER.warn("Character Creation Failure: Character face={} is invalid. Possible client hack={}", face, getClient().getLogin());
            sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
            return false;
        }
        if (hairStyle < 0 || (sex == 0 && hairStyle > 4) || (sex != 0 && hairStyle > 6)) {
            LOGGER.warn("Character Creation Failure: Character hair style={} is invalid. Possible client hack={}", hairStyle, getClient().getLogin());
            sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
            return false;
        }
        if (hairColor > 3 || hairColor < 0) {
            LOGGER.warn("Character Creation Failure: Character hair color={} is invalid. Possible client hack={}", hairColor, getClient().getLogin());
            sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
            return false;
        }
        return true;
    }

    private void initNewChar(final GameClient client, final Player newChar) {
        newChar.getPlayerClassComponent().restoreCharSubClasses();

        final ClassId classId = newChar.getPlayerClassComponent().getClassId();
        final int[] location = SettingUtils.calculateStartPoint(classId.getPlayerClasses());
        final Location startLoc = new Location(location[0], location[1], location[2]);
        newChar.setLoc(startLoc);

        if (AllSettingsConfig.CHAR_TITLE) {
            newChar.setTitle(AllSettingsConfig.ADD_CHAR_TITLE);
        } else {
            newChar.setTitle("");
        }

        for (final ItemName_Count items : SettingUtils.calculateItemEquip(classId.getId(), AllSettingsConfig.ALT_REQUEST_CUSTOM_START_EQUIPMENT)) {
            ItemInstance item = ItemFunctions.createItem(LinkerFactory.getInstance().findClearValue(items.itemName));
            final long count = items.count;
            if (item.isStackable()) {
                item.setCount(count);
                newChar.getInventory().addItem(item);
            } else {
                for (long n = 0; n < count; n++) {
                    item = ItemFunctions.createItem(LinkerFactory.getInstance().findClearValue(items.itemName));
                    newChar.getInventory().addItem(item);
                }
                if (item.isEquipable())
                    newChar.getInventory().equipItem(item);
            }
            if (item.getItemId() == 5588) // tutorial book
                newChar.getShortCutComponent().registerShortCut(new ShortCut(11, 0, ShortCut.TYPE_ITEM, item.getObjectId(), -1, 1));
        }

        for (final SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(newChar, AcquireType.NORMAL))
            newChar.addSkill(SkillTable.getInstance().getSkillEntry(skill.getId(), skill.getLevel()), true);

        if (newChar.getSkillLevel(1001) > 0) // Soul Cry
            newChar.getShortCutComponent().registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1001, 1, 1));
        if (newChar.getSkillLevel(1177) > 0) // Wind Strike
            newChar.getShortCutComponent().registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1177, 1, 1));
        if (newChar.getSkillLevel(1216) > 0) // Self Heal
            newChar.getShortCutComponent().registerShortCut(new ShortCut(9, 0, ShortCut.TYPE_SKILL, 1216, 1, 1));

        // add attack, take, sit shortcut
        newChar.getShortCutComponent().registerShortCut(new ShortCut(0, 0, ShortCut.TYPE_ACTION, 2, -1, 1));
        newChar.getShortCutComponent().registerShortCut(new ShortCut(3, 0, ShortCut.TYPE_ACTION, 5, -1, 1));
        newChar.getShortCutComponent().registerShortCut(new ShortCut(10, 0, ShortCut.TYPE_ACTION, 0, -1, 1));
        // понял как на панельке отобразить. нц софт 10-11 панели сделали(by VISTALL)
        // fly transform
        newChar.getShortCutComponent().registerShortCut(new ShortCut(0, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 911, 1, 1));
        newChar.getShortCutComponent().registerShortCut(new ShortCut(3, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 884, 1, 1));
        newChar.getShortCutComponent().registerShortCut(new ShortCut(4, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 885, 1, 1));
        // air ship
        newChar.getShortCutComponent().registerShortCut(new ShortCut(0, ShortCut.PAGE_AIRSHIP, ShortCut.TYPE_ACTION, 70, 0, 1));

        if (AllSettingsConfig.startLevel > 0)
            newChar.setLevel(AllSettingsConfig.startLevel);

        newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
        newChar.setCurrentCp(0); // retail
        newChar.setOnlineStatus(false);

        newChar.store(false);
        newChar.getInventory().store();
        newChar.deleteMe();

        client.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(client.getLogin()));
    }
}