package org.jts.dataparser;

import org.jts.dataparser.data.Parser;
import org.jts.dataparser.data.parser.*;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Camelion
 * @date : 25.08.12 12:54
 * <p/>
 * Главный загрузчик, который управляет последовательностью загрузки
 * датапака
 */
public class PTSDataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PTSDataLoader.class);

    public static void load() {
        try {
            LOGGER.info("/======== Loading PTS scripts data ========/");
            LinkerFactory.getInstance();
            RecipeParser.getInstance().load();
            AirshipParser.getInstance().load();
            AnnounceSphereParser.getInstance().load();
            AreaDataParser.getInstance().load();
            ArmorEnchantBonusDataParser.getInstance().load();
            AuctionDataParser.getInstance().load();
            BuilderCmdAliasParser.getInstance().load();
            CastleDataParser.getInstance().load();
            CategoryDataParser.getInstance().load();
            ConvertDataParser.getInstance().load();
            CubicDataParser.getInstance().load();
            CursedWeaponDataParser.getInstance().load();
            //DecoDataParser.getInstance().load();
            DoorDataParser.getInstance().load();
            DyeDataParser.getInstance().load();
            EnchantOptionParser.getInstance().load();
            ExpDataParser.getInstance().load();
            FieldCycleParser.getInstance().load();
            FishingDataParser.getInstance().load();
            //FormationInfoParser.getInstance().load();
            //FreewayInfoParser.getInstance().load();
            FStringParser.getInstance().load();
            InstantZoneDataParser.getInstance().load();
            ItemDataParser.getInstance().load();
            //ManorDataParser.getInstance().load();
            //MinigameParser.getInstance().load();
            //MonraceParser.getInstance().load();
            //MultisellParser.getInstance().load();
            VariationParser.getInstance().load();
            TransformParser.getInstance().load();
            //NpcDataParser.getInstance().load();
            PetDataParser.getInstance().load();
            //NpcPosParser.getInstance().load();
            SettingParser.getInstance().load();
            PCParameterParser.getInstance().load();
            //SkillDataParser.getInstance().load();
            //SkillAcquireParser.getInstance().load();
            UserBasicActionParser.getInstance().load();
            Parser.clear();
            LOGGER.info("/======== End loading PTS scripts data ========/");
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
    }
}