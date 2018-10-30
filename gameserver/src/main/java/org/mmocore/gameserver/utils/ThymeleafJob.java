package org.mmocore.gameserver.utils;

import org.apache.commons.io.output.StringBuilderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.Writer;
import java.util.Optional;

/**
 * Create by Mangol on 23.11.2015.
 */
public final class ThymeleafJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafJob.class);
    private final static ThymeleafJob INSTANCE = new ThymeleafJob();
    private TemplateEngine templateEngine;

    private ThymeleafJob() {
        initialize();
    }

    public static ThymeleafJob getInstance() {
        return INSTANCE;
    }

    private void initialize() {
        templateEngine = new TemplateEngine();
    }

    public String process(final String text, final Optional<Context> context) {
        if (!context.isPresent()) {
            return text;
        }
        final Writer writer = new StringBuilderWriter(new StringBuilder(text.length()));
        templateEngine.process(text, context.get(), writer);
        return writer.toString();
    }

    public String process(final String text, final Context context) {
        final Optional<Context> contextOptional = Optional.ofNullable(context);
        if (!contextOptional.isPresent()) {
            return text;
        }
        final Writer writer = new StringBuilderWriter(new StringBuilder(text.length()));
        templateEngine.process(text, contextOptional.get(), writer);
        return writer.toString();
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
}
