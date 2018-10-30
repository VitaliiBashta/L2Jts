package org.mmocore.authserver.network.crypt;

import org.mmocore.commons.crypt.JacksumAPI;
import org.mmocore.commons.crypt.algorithm.AbstractChecksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class PasswordHash {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordHash.class);

    private final String hashAlgorithm;

    public PasswordHash(final String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * Сравнивает пароль и ожидаемый хеш
     *
     * @param password
     * @param expected
     * @return совпадает или нет
     */
    public boolean compare(final String password, final String expected) {
        try {
            return encrypt(password).equals(expected);
        } catch (Exception e) {
            LOGGER.error(hashAlgorithm + ": encryption error!", e);
            return false;
        }
    }

    /**
     * Получает пароль и возвращает хеш
     *
     * @param password
     * @return hash
     */
    public String encrypt(final String password) throws Exception {
        final AbstractChecksum checksum = JacksumAPI.getChecksumInstance(hashAlgorithm);
        checksum.setEncoding("BASE64");
        checksum.update(password.getBytes(StandardCharsets.UTF_8));

        return checksum.format("#CHECKSUM");
    }
}
