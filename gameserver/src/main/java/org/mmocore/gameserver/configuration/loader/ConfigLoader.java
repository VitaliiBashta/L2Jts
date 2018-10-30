package org.mmocore.gameserver.configuration.loader;

import org.mmocore.gameserver.configuration.parser.chatFilter.AbuseWorldsConfigParser;
import org.mmocore.gameserver.configuration.parser.gmAccess.GmAccessConfigParser;

/**
 * Create by Mangol on 24.11.2015.
 */
public class ConfigLoader {
    private static final org.mmocore.commons.configuration.ConfigLoader loader = org.mmocore.commons.configuration.ConfigLoader.createConfigLoader();

    public static void loading() {
        loader.load();
        AbuseWorldsConfigParser.getInstance().load();
        GmAccessConfigParser.loadGMAccess();
    }

    public static void reloading() {
        loading();
        /**
         * Основные настройки
         */
		/*AiConfigParser.getInstance().reload();
		AllSettingConfigParser.getInstance().reload();
		EventsConfigParser.getInstance().reload();
		ExtConfigParser.getInstance().reload();
		FormulasConfigParser.getInstance().reload();
		GeodataConfigParser.getInstance().reload();
		L2TopConfigParser.getInstance().reload();
		OlympiadConfigParser.getInstance().reload();
		OtherConfigParser.getInstance().reload();
		PtsDataConfigParser.getInstance().reload();
		PvpConfigParser.getInstance().reload();
		ResidenceConfigParser.getInstance().reload();
		ServerConfigParser.getInstance().reload();
		ServicesConfigParser.getInstance().reload();
		SpoilConfigParser.getInstance().reload();
		TelnetConfigParser.getInstance().reload();
		JtsProtectionConfigParser.getInstance().reload();*/

        /**
         * Блок коммунити
         */
		/*CBasicConfigParser.getInstance().reload();
		CTeleportConfigParser.getInstance().reload();
		CBufferConfigParser.getInstance().reload();
		CClassMasterConfigParser.getInstance().reload();
		CRatingConfigParser.getInstance().reload();
		CServiceParser.getInstance().reload();

		AcpConfigParser.getInstance().reload();


		AbuseWorldsConfigParser.getInstance().reload();
		GmAccessConfigParser.loadGMAccess();*/
    }
}
