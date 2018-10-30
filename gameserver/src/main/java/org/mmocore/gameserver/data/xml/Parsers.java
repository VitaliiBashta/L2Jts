package org.mmocore.gameserver.data.xml;

import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.client.parser.*;
import org.mmocore.gameserver.data.xml.parser.*;
import org.mmocore.gameserver.data.xml.parser.custom.PremiumParser;
import org.mmocore.gameserver.data.xml.parser.custom.community.CBufferParser;
import org.mmocore.gameserver.data.xml.parser.custom.community.CBuyClanSkillParser;
import org.mmocore.gameserver.data.xml.parser.custom.community.CLeaseTransformParser;
import org.mmocore.gameserver.data.xml.parser.custom.community.CTeleportParser;
import org.mmocore.gameserver.data.xml.parser.other.ColorParser;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 20:55/30.11.2010
 */
public abstract class Parsers {
    public static void parseAll() {
        //ips
        IpConfigParser.getInstance().load();
        // cache
        StringHolder.getInstance().load();
        ColorParser.getInstance().load();
        CLeaseTransformParser.getInstance().load();
        CBuyClanSkillParser.getInstance().load();
        PremiumParser.getInstance().load();
        // client
        ItemNameLineParser.getInstance().load();
        NpcNameLineParser.getInstance().load();
        TransformDataLineParser.getInstance().load();
        ArmorgrpLineParser.getInstance().load();
        WeapongrpLineParser.getInstance().load();
        EtcitemgrpLineParser.getInstance().load();
        //Tests
        QuestDataParser.getInstance().load();

        //
        SkillTable.getInstance().load();
        OptionDataParser.getInstance().load();
        ItemParser.getInstance().load();

        //
        SuperPointParser.getInstance().load();
        NpcParser.getInstance().load();
        CustomNpcParser.getInstance().load();
        CustomBuyListParser.getInstance().load();

        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();

        StaticObjectParser.getInstance().load();
        DoorParser.getInstance().load();
        ZoneParser.getInstance().load();
        SpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();

        //
        ReflectionManager.getInstance();

        //
        AirshipDockParser.getInstance().load();
        SkillAcquireParser.getInstance().load();

        //
        ResidenceParser.getInstance().load();
        EventParser.getInstance().load();

        MultiSellParser.getInstance().load();
        CustomMultiSellParser.getInstance().load();

        // item support
        SoulCrystalParser.getInstance().load();
        ArmorSetsParser.getInstance().load();
        FishDataParser.getInstance().load();
        ProductItemParser.getInstance().load();
        RecommendationParser.getInstance().load();

        // etc
        PetitionGroupParser.getInstance().load();
        AnnouncementsParser.getInstance().load();

        CTeleportParser.getInstance().load();
        CBufferParser.getInstance().load();

        QuestCustomParamsParser.getInstance().load();

        // DressMe system
        DressArmorParser.getInstance().load();
        DressCloakParser.getInstance().load();
        DressShieldParser.getInstance().load();
        DressWeaponParser.getInstance().load();

        // Phantom system (with priority)
        PhantomArmorParser.getInstance().load();
        PhantomEquipParser.getInstance().load();
        PhantomParser.getInstance().load();
        PhantomSpawnParser.getInstance().load();
        PhantomPhraseParser.getInstance().load();
        TalismanEventParser.getInstance().load();
    }
}
