package org.mmocore.gameserver.data.htm;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;

/**
 * Кэширование html диалогов.
 * <p>
 * В кеше список вот так
 * admin/admhelp.htm
 * admin/admin.htm
 * admin/admserver.htm
 * admin/banmenu.htm
 * admin/charmanage.htm
 *
 * @author G1ta0
 * @author VISTALL
 * @author Java-man
 */
public class HtmCache {
    public static final int DISABLED = 0; // все диалоги кешируются при загрузке сервера
    public static final int LAZY = 1; // диалоги кешируются по мере обращения
    public static final int ENABLED = 2; // кеширование отключено (только для тестирования)

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmCache.class);

    private static final HtmlCompressor HTML_COMPRESSOR = createHtmlCompressor();

    private static final String NOT_EXIST_OR_EMPTY_HTML = StringUtils.EMPTY;

    private final Cache<String, String>[] cache = new Cache[Language.VALUES.length];

    private HtmCache() {
    }

    public static HtmCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static HtmlCompressor createHtmlCompressor() {
        final HtmlCompressor htmlCompressor = new HtmlCompressor();

        htmlCompressor.setEnabled(true);                   //if false all compression is off (default is true)
        htmlCompressor.setRemoveComments(true);            //if false keeps HTML comments (default is true)
        htmlCompressor.setRemoveMultiSpaces(true);         //if false keeps multiple whitespace characters (default is true)
        htmlCompressor.setRemoveIntertagSpaces(true);      //removes iter-tag whitespace characters
        htmlCompressor.setRemoveQuotes(true);              //removes unnecessary tag attribute quotes
        htmlCompressor.setSimpleDoctype(false);             //simplify existing doctype
        htmlCompressor.setRemoveScriptAttributes(false);    //remove optional attributes from script tags
        htmlCompressor.setRemoveStyleAttributes(false);     //remove optional attributes from style tags
        htmlCompressor.setRemoveLinkAttributes(true);      //remove optional attributes from link tags
        htmlCompressor.setRemoveFormAttributes(true);      //remove optional attributes from form tags
        htmlCompressor.setRemoveInputAttributes(true);     //remove optional attributes from input tags
        htmlCompressor.setSimpleBooleanAttributes(true);   //remove values from boolean tag attributes
        htmlCompressor.setRemoveJavaScriptProtocol(false);  //remove "javascript:" from inline event handlers
        htmlCompressor.setRemoveHttpProtocol(false);        //replace "http://" with "//" inside tag attributes
        htmlCompressor.setRemoveHttpsProtocol(false);       //replace "https://" with "//" inside tag attributes
        htmlCompressor.setPreserveLineBreaks(false);        //preserves original line breaks
        htmlCompressor.setRemoveSurroundingSpaces("all"); //remove spaces around provided tags

        htmlCompressor.setCompressCss(false);               //compress inline css
        htmlCompressor.setCompressJavaScript(false);        //compress inline javascript

        htmlCompressor.setGenerateStatistics(true);

        return htmlCompressor;
    }

    private void initializeCache(final EmbeddedCacheManager cacheManager) {
        for (int i = 0; i < cache.length; i++)
            cache[i] = cacheManager.getCache(getClass().getName() + '.' + Language.VALUES[i].name());
    }

    public void load() {
        initializeCache(GameServer.getInstance().getCacheManager());

        switch (ServerConfig.HTM_CACHE_MODE) {
            case ENABLED:
                for (final Language lang : Language.VALUES) {
                    final File root = new File(ServerConfig.DATAPACK_ROOT, "data/html-" + lang.getShortName());
                    if (!root.exists()) {
                        LOGGER.info("HtmCache: Not find html dir for lang: {}", lang);
                        continue;
                    }

                    load(lang, root, root.getAbsolutePath() + '/');
                }
                for (int i = 0; i < cache.length; i++) {
                    final Cache<String, String> cache = this.cache[i];
                    LOGGER.info(String.format("HtmCache: parsing %d documents; lang: %s.", cache.size(), Language.VALUES[i]));
                }
                break;
            case LAZY:
                LOGGER.info("HtmCache: lazy cache mode.");
                break;
            case DISABLED:
            default:
                LOGGER.info("HtmCache: disabled.");
                break;
        }
    }

    private void load(final Language lang, final File f, final String rootPath) {
        if (!f.exists()) {
            LOGGER.info("HtmCache: dir not exists: {}", f);
            return;
        }

        final Collection<File> files = FileUtils.listFiles(f, new String[]{".htm"}, true);
        for (final File file : files) {
            try {
                putContent(lang, file, rootPath);
            } catch (final IOException e) {
                LOGGER.info("HtmCache: file error", e);
            }
        }
    }

    public void putContent(final Language lang, final File f, final String rootPath) throws IOException {
        final String path = f.getAbsolutePath().substring(rootPath.length()).replace("\\", "/");

        if (cache[lang.ordinal()].containsKey(path))
            LOGGER.info("Htm {} was added twice.", path);

        String content = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        content = HtmlUtils.bbParse(content);
        content = HtmlUtils.compress(HTML_COMPRESSOR, content);

        cache[lang.ordinal()].putAsync(path.toLowerCase(), content);
    }

    /**
     * Получить html.
     *
     * @param fileName путь до html относительно data/html
     * @param player
     * @return пустую строку, если диалога не существует
     */
    public String getHtml(final String fileName, final Player player) {
        final Language lang = player == null ? Language.ENGLISH : player.getLanguage();
        final Optional<String> cache = getCache(fileName, lang);

        return cache.isPresent() ? cache.get() : NOT_EXIST_OR_EMPTY_HTML;
    }

    private Optional<String> getCache(final String file, final Language lang) {
        if (file == null) {
            return null;
        }

        final String fileLower = file.toLowerCase();
        Optional<String> cache;

        switch (ServerConfig.HTM_CACHE_MODE) {
            case ENABLED:
                cache = get(lang, fileLower, true);
                break;
            case LAZY:
                cache = get(lang, fileLower, false);

                if (!cache.isPresent()) {
                    cache = loadLazy(lang, file);
                    if (lang != Language.ENGLISH && NOT_EXIST_OR_EMPTY_HTML.equals(cache.get())) {
                        cache = loadLazy(Language.ENGLISH, file);
                    }
                } else if (lang != Language.ENGLISH && NOT_EXIST_OR_EMPTY_HTML.equals(cache.get())) {
                    cache = get(Language.ENGLISH, fileLower, false);
                    if (!cache.isPresent()) {
                        cache = loadLazy(Language.ENGLISH, file);
                    }
                }
                break;
            case DISABLED:
            default:
                cache = get(lang, fileLower, true);

                if (!cache.isPresent()) {
                    cache = loadDisabled(lang, file);
                    if (!cache.isPresent() && lang != Language.ENGLISH) {
                        cache = loadDisabled(Language.ENGLISH, file);
                    }
                }
                break;
        }

        return cache;
    }

    private Optional<String> loadDisabled(final Language lang, final String file) {
        String cache = null;
        final File f = new File(ServerConfig.DATAPACK_ROOT, "data/html-" + lang.getShortName() + '/' + file);
        if (f.exists()) {
            try {
                cache = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                cache = HtmlUtils.bbParse(cache);
            } catch (final IOException e) {
                LOGGER.error("HtmCache: File error: {}", f, e);
            }
        }

        return Optional.ofNullable(cache);
    }

    private Optional<String> loadLazy(final Language lang, final String file) {
        String cache = NOT_EXIST_OR_EMPTY_HTML;

        final File f = new File(ServerConfig.DATAPACK_ROOT, "data/html-" + lang.getShortName() + '/' + file);
        if (f.exists()) {
            try {
                cache = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                cache = HtmlUtils.bbParse(cache);
            } catch (final IOException e) {
                LOGGER.error("HtmCache: File error: {}", f, e);
            }
        }

        this.cache[lang.ordinal()].put(file, cache);

        return Optional.ofNullable(cache);
    }

    private Optional<String> get(final Language lang, final String f, final boolean lookInEngCache) {
        String element = cache[lang.ordinal()].get(f);

        if (lookInEngCache && (element == null || element == NOT_EXIST_OR_EMPTY_HTML))
            element = cache[Language.ENGLISH.ordinal()].get(f);

        return Optional.ofNullable(element);
    }

    public void clear() {
        for (final Cache<String, String> subCache : cache)
            subCache.clear();
    }

    public void doStop() {
        for (final Cache<String, String> subCache : cache)
            subCache.stop();
    }

    private static class LazyHolder {
        private static final HtmCache INSTANCE = new HtmCache();
    }
}
