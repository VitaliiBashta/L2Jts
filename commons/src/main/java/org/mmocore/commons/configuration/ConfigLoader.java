package org.mmocore.commons.configuration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.logging.LoggerObject;
import org.mmocore.commons.utils.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Java-man
 */
public class ConfigLoader extends LoggerObject {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Конфиги для кастомных расширений клиентов. Не показываем варнинги об их отсутствии
     * TODO: вынести в аннотацию конфига
     */
    private final String[] clientCustoms = {
            "configuration\\custom\\custom.json", // EVA
            "configuration\\LostDreamCustom.json", // DarkEmpire
            "configuration\\phantoms.json", // DarkEmpire
            "configuration\\customBossSpawn.json" // DarkEmpire
    };

    private ConfigLoader() {
    }

    public static ConfigLoader createConfigLoader() {
        return new ConfigLoader();
    }

    public void load() {
        Reflections reflections = new Reflections("org.mmocore");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Configuration.class);
        ReflectionUtils.loadClassesWithConsumer(classes, object -> {
            Class<?> clazz = object.getClass();
            Configuration annotation = clazz.getAnnotation(Configuration.class);
            String path = annotation.value();
            Path configurationPath = Paths.get("configuration").resolve(path);

            if (Files.notExists(configurationPath)) {
                if (!Arrays.asList(clientCustoms).contains(configurationPath.toString()))
                    warn("file " + configurationPath.toAbsolutePath() + " not exists");
                return;
            }

            Map<Object, Object> objectMap = new HashMap<>();
            try (BufferedReader reader = Files.newBufferedReader(configurationPath)) {
                read(clazz.getSimpleName(), objectMap, reader);
            } catch (Exception e) {
                warn("Exception: " + e, e);
            }
            configurationClassWrite(clazz, objectMap);
        });
    }

    private void read(String className, Map<Object, Object> objectMap, BufferedReader reader) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(Feature.ALLOW_YAML_COMMENTS);
        JsonParser parser = jsonFactory.createParser(reader);

        logger.info("Loading {} config.", className);
        ObjectMapper mapper = new ObjectMapper();
        readElement(mapper.readTree(parser), objectMap);
    }

    private void readElement(final ObjectNode node, Map<Object, Object> objectMap) {
        node.fields().forEachRemaining(m -> addObject(objectMap, m.getKey(), m.getValue().textValue()));
    }

    private void configurationClassWrite(Class<?> clazz, Map<Object, Object> objectMap) {
        final ConfigParser configParser = ConfigParser.createConfigParser(clazz, objectMap);
        configParser.parse();
        objectMap.clear();
    }

    private void addObject(Map<Object, Object> objectMap, final Object name, final Object value) {
        if (objectMap.containsKey(name)) {
            error("object map contains! key - " + name + " rename key!");
        } else {
            objectMap.put(name, value);
        }
    }
}
