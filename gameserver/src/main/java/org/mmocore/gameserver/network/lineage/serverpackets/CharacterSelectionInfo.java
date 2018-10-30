package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterAccessDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.utils.AutoBan;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionInfo extends GameServerPacket {
    // d (SdSddddddddddffdQdddddddddddddddddddddddddddddddddddddddffdddchhd)
    private static final Logger logger = LoggerFactory.getLogger(CharacterSelectionInfo.class);

    private final String loginName;

    private final int sessionId;

    private final CharSelectInfoPackage[] characterPackages;

    public CharacterSelectionInfo(final String loginName, final int sessionId) {
        this.sessionId = sessionId;
        this.loginName = loginName;
        characterPackages = loadCharacterSelectInfo(loginName);
    }

    public static CharSelectInfoPackage[] loadCharacterSelectInfo(final String loginName) {
        final List<CharSelectInfoPackage> characterList = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id AND cs.active=1) WHERE account_name=? LIMIT 7");
            statement.setString(1, loginName);
            rset = statement.executeQuery();
            while (rset.next()) // fills the package
            {
                final CharSelectInfoPackage charInfopackage = restoreChar(rset);
                if (charInfopackage != null) {
                    if (!haveDoubleActiveCharacters(characterList, charInfopackage))
                        characterList.add(charInfopackage);
                    else
                        Log.audit("[CharacterSelectionInfo]", "Player acc name: '" + loginName + "' have double active char!");
                }
            }
        } catch (final Exception e) {
            logger.error("could not restore charinfo:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
    }

    private static boolean haveDoubleActiveCharacters(final Iterable<CharSelectInfoPackage> characterList, final CharSelectInfoPackage charInfopackage) {
        for (final CharSelectInfoPackage charSelectInfoPackage : characterList)
            if (charSelectInfoPackage.getObjectId() == charInfopackage.getObjectId())
                return true;

        return false;
    }

    private static int restoreBaseClassId(final int objId) {
        int classId = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT class_id FROM character_subclasses WHERE char_obj_id=? AND isBase=1");
            statement.setInt(1, objId);
            rset = statement.executeQuery();
            while (rset.next()) {
                classId = rset.getInt("class_id");
            }
        } catch (final Exception e) {
            logger.error("could not restore base class id:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return classId;
    }

    private static CharSelectInfoPackage restoreChar(final ResultSet chardata) {
        CharSelectInfoPackage charInfopackage = null;
        try {
            final int objectId = chardata.getInt("obj_Id");
            final int classid = chardata.getInt("class_id");
            int baseClassId = classid;
            final boolean useBaseClass = chardata.getInt("isBase") > 0;
            if (!useBaseClass) {
                baseClassId = restoreBaseClassId(objectId);
            }
            final boolean female = chardata.getInt("sex") == 1;
            final PlayerRace race = PlayerRace.values()[chardata.getInt("race")];
            final String name = chardata.getString("char_name");
            charInfopackage = new CharSelectInfoPackage(objectId, name);
            charInfopackage.setLevel(chardata.getInt("level"));
            charInfopackage.setMaxHp(chardata.getInt("maxHp"));
            charInfopackage.setCurrentHp(chardata.getDouble("curHp"));
            charInfopackage.setMaxMp(chardata.getInt("maxMp"));
            charInfopackage.setCurrentMp(chardata.getDouble("curMp"));

            charInfopackage.setX(chardata.getInt("x"));
            charInfopackage.setY(chardata.getInt("y"));
            charInfopackage.setZ(chardata.getInt("z"));
            charInfopackage.setPk(chardata.getInt("pkkills"));
            charInfopackage.setPvP(chardata.getInt("pvpkills"));

            charInfopackage.setFace(chardata.getInt("face"));
            charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
            charInfopackage.setHairColor(chardata.getInt("haircolor"));
            charInfopackage.setSex(female ? 1 : 0);

            charInfopackage.setExp(chardata.getLong("exp"));
            charInfopackage.setSp(chardata.getInt("sp"));
            charInfopackage.setClanId(chardata.getInt("clanid"));

            charInfopackage.setKarma(chardata.getInt("karma"));
            charInfopackage.setRace(race.ordinal());
            charInfopackage.setClassId(classid);
            charInfopackage.setBaseClassId(baseClassId);
            long deletetime = chardata.getLong("deletetime");
            int deletedays = 0;
            if (ServerConfig.DELETE_DAYS > 0) {
                if (deletetime > 0) {
                    deletetime = (int) (System.currentTimeMillis() / 1000 - deletetime);
                    deletedays = (int) (deletetime / 3600 / 24);
                    if (deletedays >= ServerConfig.DELETE_DAYS) {
                        CharacterDAO.getInstance().deleteCharByObjId(objectId);
                        return null;
                    }
                    deletetime = ServerConfig.DELETE_DAYS * 3600L * 24 - deletetime;
                } else {
                    deletetime = 0;
                }
            }
            charInfopackage.setDeleteTimer((int) deletetime);
            charInfopackage.setLastAccess(chardata.getLong("lastAccess") * 1000L);
            charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));

            final int newVitality = chardata.getInt("vitality") + (int) ((System.currentTimeMillis() - charInfopackage.getLastAccess()) / 15000.);

            charInfopackage.setVitalityPoints(Math.max(Math.min(newVitality, AllSettingsConfig.VITALITY_LEVELS[4]), 0));

            if (charInfopackage.getAccessLevel() < 0 && !AutoBan.isBanned(objectId)) {
                charInfopackage.setAccessLevel(0);
            }

            if (ExtConfig.EX_2ND_PASSWORD_CHECK) {
                CharacterAccessDAO.getInstance().select(charInfopackage);
            }
        } catch (final Exception e) {
            logger.error("", e);
        }

        return charInfopackage;
    }

    public CharSelectInfoPackage[] getCharInfo() {
        return characterPackages;
    }

    @Override
    protected void writeData() {
        final int size = characterPackages != null ? characterPackages.length : 0;

        writeD(size);
        writeD(ServerConfig.CHAR_NUMBER_PER_SERVER); // 0 - disable char create; default - 7
        writeC(0x00); //разрешает или запрещает создание игроков
        long lastAccess = -1L;
        int lastUsed = -1;
        for (int i = 0; i < size; i++) {
            if (lastAccess < characterPackages[i].getLastAccess()) {
                lastAccess = characterPackages[i].getLastAccess();
                lastUsed = i;
            }
        }

        for (int i = 0; i < size; i++) {
            final CharSelectInfoPackage charInfoPackage = characterPackages[i];

            writeS(charInfoPackage.getName());
            writeD(charInfoPackage.getCharId()); // ?
            writeS(loginName);
            writeD(sessionId);
            writeD(charInfoPackage.getClanId());
            writeD(0x00); // ??

            writeD(charInfoPackage.getSex());
            writeD(charInfoPackage.getRace());
            writeD(charInfoPackage.getBaseClassId());

            writeD(0x01); // active ??

            writeD(charInfoPackage.getX());
            writeD(charInfoPackage.getY());
            writeD(charInfoPackage.getZ());

            writeF(charInfoPackage.getCurrentHp());
            writeF(charInfoPackage.getCurrentMp());

            writeD(charInfoPackage.getSp());
            writeQ(charInfoPackage.getExp());
            final int lvl = charInfoPackage.getLevel();
            writeF(ExpDataHolder.getInstance().getExpPercent(lvl, charInfoPackage.getExp()));
            writeD(lvl);

            writeD(charInfoPackage.getKarma());
            writeD(charInfoPackage.getPk());
            writeD(charInfoPackage.getPvP());

            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);

            for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
                writeD(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));
            }

            writeD(charInfoPackage.getHairStyle());
            writeD(charInfoPackage.getHairColor());
            writeD(charInfoPackage.getFace());

            writeF(charInfoPackage.getMaxHp()); // hp max
            writeF(charInfoPackage.getMaxMp()); // mp max

            writeD(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
            writeD(charInfoPackage.getClassId());
            writeD(i == lastUsed ? 1 : 0);

            writeC(Math.min(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_RHAND), 127));
            writeH(charInfoPackage.getPaperdollAugmentation1Id(Inventory.PAPERDOLL_RHAND));
            writeH(charInfoPackage.getPaperdollAugmentation2Id(Inventory.PAPERDOLL_RHAND));
            final int weaponId = charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
            if (weaponId == 8190) // Transform id (на оффе отображаются только КВ трансформации или вообще не отображаются ;)
            {
                writeD(301);
            } else if (weaponId == 8689) {
                writeD(302);
            } else {
                writeD(0x00);
            }

            //TODO: Pet info?
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeF(0x00);
            writeF(0x00);

            writeD(charInfoPackage.getVitalityPoints());
        }
    }
}