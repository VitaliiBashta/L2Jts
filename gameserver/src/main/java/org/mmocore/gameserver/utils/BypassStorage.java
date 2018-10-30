package org.mmocore.gameserver.utils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс хранилища и валидации ссылок в html.
 *
 * @author G1ta0
 */
public class BypassStorage {
    private static final Pattern htmlBypass = Pattern.compile("<(?:button|a)[^>]+?action=\"bypass +(?:-h +)?([^\"]+?)\"[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern htmlLink = Pattern.compile("<(?:button|a)[^>]+?action=\"link +([^\"]+?)\"[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern bbsWrite = Pattern.compile("<(?:button|a)[^>]+?action=\"write +(\\S+) +\\S+ +\\S+ +\\S+ +\\S+ +\\S+\"[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern directHtmlBypass = Pattern.compile("^(_mrsl|_diary|_match|manor_menu_select|_olympiad).*", Pattern.DOTALL);
    private static final Pattern directBbsBypass = Pattern.compile("^(_bbshome|_bbsgetfav|_bbsaddfav|_bbslink|_bbsloc|_bbsclan|_bbsmemo|_maillist|_friendlist).*", Pattern.DOTALL);
    private final List<ValidBypass> bypasses = new CopyOnWriteArrayList<>();
    /**
     * Переменная, которая хранит количество очисток, которые не должны выполняться, для нормальной работы мультибайпассов
     * После каждой попытки очистки счетчик уменьшается на 1 (при условии, что он больше 0).
     */
    private volatile int doNotClear = 0;

    public void parseHtml(final CharSequence html, final boolean bbs) {
        clear(bbs);

        Matcher m = htmlBypass.matcher(html);
        while (m.find()) {
            String bypass = m.group(1);
            int i = bypass.indexOf(" $");
            if (i > 0)
                bypass = bypass.substring(0, i);
            addBypass(new ValidBypass(bypass, i >= 0, bbs));
        }

        if (bbs) {
            m = bbsWrite.matcher(html);
            while (m.find()) {
                final String bypass = m.group(1);
                addBypass(new ValidBypass(bypass, true, true));
            }
        }
        m = htmlLink.matcher(html);
        while (m.find()) {
            String bypass = m.group(1);
            addBypass(new ValidBypass(bypass, false, bbs));
        }
    }

    public ValidBypass validate(final String bypass) {
        return validate(bypass, 0);
    }

    public ValidBypass validate(final String bypass, int multi) {

        ValidBypass ret = null;
        String[] bpArr = bypass.split("#");
        if (multi > 0) // если это мультибайпасс то откладываем очистку буфера на несколько шагов
            setDoNotClear(multi);

        if (directHtmlBypass.matcher(bypass).matches())
            ret = new ValidBypass(bypass, false, false);
        else if (directBbsBypass.matcher(bypass).matches())
            ret = new ValidBypass(bypass, false, true);
        else {
            final boolean args = bypass.indexOf(" ") > 0;
            for (final ValidBypass bp : bypasses) {
                //При передаче аргументов, мы можем проверить только часть команды до первого аргумента
                if (Objects.equals(bp.bypass, bypass) || (bp.args && args && bypass.startsWith(bp.bypass + " "))) {
                    ret = bp;
                    break;
                } else if (bp.bypass.contains("#")) {
                    for (String multiBypass : bpArr)
                        if (Objects.equals(multiBypass, bypass)) {
                            ret = bp;
                            break;
                        }
                }
            }
        }

        if (ret != null) {
            ret.bypass = bypass;
            clear(ret.bbs);
        }

        return ret;
    }

    private void addBypass(final ValidBypass bypass) {
        bypasses.add(bypass);
    }

    private void setDoNotClear(int arraySize) {
        doNotClear = arraySize;
    }

    private void clear(final boolean bbs) {
        if (doNotClear > 0) {
            doNotClear--;
            return;
        }
        bypasses.stream().filter(bp -> bp.bbs == bbs).forEach(bypasses::remove);
    }

	/*
	private void printList() {
		System.out.println("=== start ===");
		bypasses.forEach(bp -> System.out.println(bp.bypass));
		System.out.println("=== end ===");
	}
	*/

    public static class ValidBypass {
        public final boolean args;
        public final boolean bbs;
        public String bypass;

        public ValidBypass(final String bypass, final boolean args, final boolean bbs) {
            this.bypass = bypass;
            this.args = args;
            this.bbs = bbs;
        }
    }
}