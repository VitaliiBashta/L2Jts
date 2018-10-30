package org.mmocore.gameserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Files {
    private static final Logger LOGGER = LoggerFactory.getLogger(Files.class);

    /**
     * Сохраняет строку в файл в кодировке UTF-8.<br>
     * Если такой файл существует, то перезаписывает его.
     *
     * @param path   путь к файлу
     * @param string сохраняемая строка
     */
    public static void writeFile(final String path, final String string) {
        try {
            final Path filePath = Paths.get(path);
            java.nio.file.Files.write(filePath, string.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("Error while saving file : " + path, e);
        }
    }

    public static boolean copyFile(final String srcFile, final String destFile) {
        try {
            final Path srcFilePath = Paths.get(srcFile);
            final Path destFilePath = Paths.get(destFile);
            java.nio.file.Files.copy(srcFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (IOException e) {
            LOGGER.error("Error while copying file : " + srcFile + " to " + destFile, e);
        }

        return false;
    }
}