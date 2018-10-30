package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 07.12.2015.
 */
@Configuration("telnet.json")
public class TelnetConfig {
    @Setting(name = "EnableTelnet")
    public static boolean IS_TELNET_ENABLED;

    @Setting(name = "TelnetEncoding")
    public static String TELNET_DEFAULT_ENCODING;

    @Setting(name = "BindAddress")
    public static String TELNET_HOSTNAME;

    @Setting(name = "Port")
    public static int TELNET_PORT;

    @Setting(name = "Password")
    public static String TELNET_PASSWORD;
}
