package org.mmocore.authserver.configuration.config;

import com.google.common.base.Strings;
import org.mmocore.authserver.network.crypt.PasswordHash;
import org.mmocore.authserver.network.crypt.ScrambledKeyPair;
import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.commons.utils.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Create by Mangol on 09.12.2015.
 */
@Configuration("authserver.json")
public class LoginConfig {
    @Setting(ignore = true)
    public static final long LOGIN_TIMEOUT = 60 * 1000L;
    @Setting(ignore = true)
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginConfig.class);
    @Setting(name = "LoginserverHostname")
    public static String LOGIN_HOST = "127.0.0.1";
    @Setting(name = "LoginserverPort")
    public static int LOGIN_PORT = 2106;
    @Setting(name = "LoginHost")
    public static String GAME_SERVER_LOGIN_HOST = "127.0.0.1";
    @Setting(name = "LoginPort")
    public static int GAME_SERVER_LOGIN_PORT = 9014;
    @Setting(name = "XmlRpcServerEnabled")
    public static boolean XML_RPC_SERVER_ENABLED;
    @Setting(name = "XmlRpcServerHost")
    public static String XML_RPC_SERVER_HOST = "127.0.0.1";
    @Setting(name = "XmlRpcServerPort")
    public static int XML_RPC_SERVER_PORT = 5600;
    @Setting(name = "XmlRpcClientHost")
    public static String XML_RPC_CLIENT_HOST = "127.0.0.1";
    @Setting(name = "AcceptNewGameServer")
    public static boolean ACCEPT_NEW_GAMESERVER = true;
    @Setting(name = "AutoCreateAccounts")
    public static boolean AUTO_CREATE_ACCOUNTS = true;
    @Setting(name = "AccountTemplate")
    public static String ANAME_TEMPLATE = "[A-Za-z0-9]{4,14}";
    @Setting(name = "PasswordTemplate")
    public static String APASSWD_TEMPLATE = "[A-Za-z0-9]{4,16}";
    @Setting(name = "RSAKeyPairs", method = "rsaKey")
    public static int LOGIN_RSA_KEYPAIRS = 10;
    @Setting(name = "BlowFishKeys", method = "blowFishKey")
    public static int LOGIN_BLOWFISH_KEYS = 20;
    @Setting(name = "LegacyPasswordHash", splitter = ";", method = "legacyCrypt")
    public static String[] LEGACY_PASSWORD_HASH;
    @Setting(name = "LoginTryBeforeBan")
    public static int LOGIN_TRY_BEFORE_BAN = 10;
    @Setting(name = "LoginTryTimeout", increase = 1000L)
    public static long LOGIN_TRY_TIMEOUT = 5;
    @Setting(name = "IpBanTime", increase = 1000L)
    public static long IP_BAN_TIME = 300;
    @Setting(name = "LoginLog")
    public static boolean LOGIN_LOG = true;
    @Setting(name = "GameServerPingDelay", increase = 1000L)
    public static long GAME_SERVER_PING_DELAY = 30;
    @Setting(name = "GameServerPingRetry")
    public static int GAME_SERVER_PING_RETRY = 4;
    @Setting(name = "GameServerPingTimeoutAlert")
    public static int GAME_SERVER_PING_TIMEOUT_ALERT = 999;
    @Setting(name = "LogAuthAndFlagMail")
    public static boolean LOG_AUTH_AND_FLAG_MAIL;
    @Setting(ignore = true)
    public static PasswordHash[] LEGACY_CRYPT;
    @Setting(ignore = true)
    private static ScrambledKeyPair[] _keyPairs;
    @Setting(ignore = true)
    private static byte[][] _blowfishKeys;

    public static ScrambledKeyPair getScrambledRSAKeyPair() {
        return _keyPairs[Rnd.get(_keyPairs.length)];
    }

    public static byte[] getBlowfishKey() {
        return _blowfishKeys[Rnd.get(_blowfishKeys.length)];
    }

    private void legacyCrypt(final String[] value) {
        LEGACY_PASSWORD_HASH = value;
        List<PasswordHash> legacy = Collections.emptyList();
        for (final String method : value) {
            if (!Strings.isNullOrEmpty(method) && !"null".equalsIgnoreCase(method)) {
                if (legacy.isEmpty())
                    legacy = new ArrayList<>(1);
                legacy.add(new PasswordHash(method));
            }
        }
        LEGACY_CRYPT = legacy.toArray(new PasswordHash[legacy.size()]);
    }

    private void rsaKey(final int value) {
        LOGIN_RSA_KEYPAIRS = value;
        _keyPairs = new ScrambledKeyPair[LOGIN_RSA_KEYPAIRS];

        Optional<KeyPairGenerator> keygen = Optional.empty();
        try {
            keygen = Optional.of(KeyPairGenerator.getInstance("RSA"));
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
            keygen.get().initialize(spec);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            LOGGER.error("", e);
        }

        for (int i = 0; i < _keyPairs.length; i++) {
            _keyPairs[i] = new ScrambledKeyPair(keygen.get().generateKeyPair());
        }

        LOGGER.info("Cached {} KeyPairs for RSA communication", _keyPairs.length);
    }

    private void blowFishKey(final int value) {
        LOGIN_BLOWFISH_KEYS = value;
        _blowfishKeys = new byte[LOGIN_BLOWFISH_KEYS][16];

        for (int i = 0; i < _blowfishKeys.length; i++) {
            for (int j = 0; j < _blowfishKeys[i].length; j++) {
                _blowfishKeys[i][j] = (byte) (Rnd.get(255) + 1);
            }
        }

        LOGGER.info("Stored {} keys for Blowfish communication", _blowfishKeys.length);
    }
}
