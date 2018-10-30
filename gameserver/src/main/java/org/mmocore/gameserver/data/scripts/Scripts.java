package org.mmocore.gameserver.data.scripts;

import com.google.common.reflect.ClassPath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.mmocore.commons.compiler.Compiler;
import org.mmocore.commons.compiler.MemoryClassLoader;
import org.mmocore.commons.listener.ListenerList;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.handler.bypass.BypassHolder;
import org.mmocore.gameserver.listener.ScriptListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Scripts {
    private static final Logger _log = LoggerFactory.getLogger(Scripts.class);
    private final Compiler compiler = new Compiler();
    private final Map<String, Class<?>> _classes = new TreeMap<>();
    private final ScriptListenerImpl _listeners = new ScriptListenerImpl();
    private Scripts() {
        load();
    }

    public static Scripts getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Вызывается при загрузке сервера. Загрузает все скрипты в data/scripts. Не инициирует объекты и обработчики.
     */
    private void load() {
        _log.info("Scripts: Loading...");

        final List<Class<?>> classes = new ArrayList<>();

        boolean result = false;

        final File f = new File("./lib/scripts-1.0.jar");
        final ClassLoader scriptsLoader;

        // Инициализиуем класс лоадер.
        try {
            scriptsLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        if (f.exists()) {
            try (InputStream inputStream = new FileInputStream(f); JarInputStream stream = new JarInputStream(inputStream)) {
                JarEntry entry;
                while ((entry = stream.getNextJarEntry()) != null) {
                    //Вложенные класс
                    if (entry.getName().contains(ClassUtils.INNER_CLASS_SEPARATOR) || !entry.getName().endsWith(".class")) {
                        continue;
                    }

                    final String name = entry.getName().replace(".class", "").replace("/", ".");
                    final Class<?> clazz = scriptsLoader.loadClass(name);

                    if (Modifier.isAbstract(clazz.getModifiers())) {
                        continue;
                    }

                    classes.add(clazz);

                    _log.info("Script {} was loaded.", name);
                }
                result = true;
            } catch (final Exception e) {
                _log.error("Fail to load scripts.jar!", e);
                classes.clear();
            }
        }

        if (!result) {
            result = load(classes);
        }

        if (!result) {
            _log.error("Scripts: Failed loading scripts!");
            Runtime.getRuntime().exit(0);
            return;
        }

        final ClassLoader loader = ClasspathHelper.contextClassLoader();
        try {
            ClassPath.from(loader).getTopLevelClassesRecursive("org.mmocore.gameserver.scripts").stream().forEach(info -> {
                final Class<?> clazz = info.load();
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    return;
                }
                classes.add(clazz);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        _log.info("Scripts: Loaded " + classes.size() + " classes.");

        for (final Class<?> clazz : classes) {
            String name = clazz.getName();
            name = name.replace("org.mmocore.gameserver.scripts.", "");
            _classes.put(name, clazz);
        }
    }

    /**
     * Вызывается при загрузке сервера. Инициализирует объекты и обработчики.
     */
    public void init() {
        _classes.values().forEach(this::initClass);

        _listeners.callInit();
    }

    /**
     * Перезагрузить все скрипты в data/scripts/target
     *
     * @param target путь до класса, или каталога со скриптами
     * @return true, если скрипты перезагружены успешно
     */
    public List<Class<?>> load(final File target) {
        Collection<File> scriptFiles = Collections.emptyList();

        if (target.isFile()) {
            scriptFiles = new ArrayList<File>(1);
            scriptFiles.add(target);
        } else if (target.isDirectory()) {
            scriptFiles = FileUtils.listFiles(target, FileFilterUtils.suffixFileFilter(".java"), FileFilterUtils.directoryFileFilter());
        }

        if (scriptFiles.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Class<?>> classes = new ArrayList<Class<?>>();
        final Compiler compiler = new Compiler();

        if (compiler.compile(scriptFiles)) {
            final MemoryClassLoader classLoader = compiler.getClassLoader();
            classLoader.getLoadedClasses().filter(name -> !name.contains(ClassUtils.INNER_CLASS_SEPARATOR)).forEach(name -> {
                try {
                    final Class<?> clazz = classLoader.loadClass(name);
                    if (Modifier.isAbstract(clazz.getModifiers())) {
                        return;
                    }
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    _log.error("Scripts: Can't load script class: " + name, e);
                    classes.clear();
                }
            });
        }

        return classes;
    }

    /**
     * Перезагрузить все скрипты в data/scripts/target
     *
     * @param classes, для загруженных скриптов
     * @return true, если загрузка прошла успешно
     */
    private boolean load(final List<Class<?>> classes) {
        Collection<File> scriptFiles = Collections.emptyList();

        File file = new File(ServerConfig.DATAPACK_ROOT, "data/scripts/" + "".replace(".", "/") + ".java");
        if (file.isFile()) {
            scriptFiles = new ArrayList<>(1);
            scriptFiles.add(file);
        } else {
            file = new File(ServerConfig.DATAPACK_ROOT, "data/scripts/" + "");
            if (file.isDirectory()) {
                scriptFiles = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".java"), FileFilterUtils.directoryFileFilter());
            }
        }

        if (scriptFiles.isEmpty()) {
            return false;
        }

        final MutableBoolean success = new MutableBoolean(compiler.compile(scriptFiles));

        if (success.isTrue()) {
            final MemoryClassLoader classLoader = compiler.getClassLoader();
            classLoader.getLoadedClasses().filter(className -> !className.contains(ClassUtils.INNER_CLASS_SEPARATOR)).forEach(
                    className ->
                    {
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (Modifier.isAbstract(clazz.getModifiers()))
                                return;

                            classes.add(clazz);
                        } catch (ClassNotFoundException e) {
                            success.setValue(false);
                            _log.error("Scripts: Can't load script class: " + className, e);
                        }
                    });

            classLoader.clear();
        }

        return success.booleanValue();
    }

    private Object initClass(final Class<?> clazz) {
        Object o = null;

        try {
            if (ClassUtils.isAssignable(clazz, ScriptListener.class)) {
                o = clazz.newInstance();

                _listeners.add((ScriptListener) o);
            }

            for (final Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Bypass.class)) {
                    final Bypass an = method.getAnnotation(Bypass.class);
                    if (o == null) {
                        o = clazz.newInstance();
                    }
                    final Class[] par = method.getParameterTypes();
                    if (par.length == 0 || par[0] != Player.class || par[1] != NpcInstance.class || par[2] != String[].class) {
                        _log.error("Wrong parameters for bypass method: " + method.getName() + ", class: " + clazz.getSimpleName());
                        continue;
                    }

                    BypassHolder.getInstance().registerBypass(an.value(), o, method);
                }
            }

            if (clazz.isAnnotationPresent(ChronicleCheck.class)) {
                final ChronicleCheck chronicle = clazz.getAnnotation(ChronicleCheck.class);
                if (!ServerConfig.CHRONICLE_VERSION.equals(chronicle.value())) {
                    _log.warn("Class: " + clazz.getSimpleName() + "is not checked for current chronicle: " + ServerConfig.CHRONICLE_VERSION.name());
                }
            }
        } catch (final Exception e) {
            _log.error("Can't initiate {} class {}", clazz.getName(), e);
            e.printStackTrace();
        }
        return o;
    }

    public Map<String, Class<?>> getClasses() {
        return _classes;
    }

    public Object callScripts(final Player caller, final String className, final String methodName, final Object[] args, final Map<String, Object> variables) {
        final Class<?> clazz = _classes.get(className);
        if (clazz == null) {
            _log.error("Script class " + className + " not found!");
            return null;
        }

        final Object o;
        try {
            o = clazz.newInstance();
        } catch (final Exception e) {
            _log.error("Scripts: Failed creating instance of " + clazz.getName(), e);
            return null;
        }

        if (variables != null && !variables.isEmpty()) {
            for (final Map.Entry<String, Object> param : variables.entrySet()) {
                try {
                    FieldUtils.writeField(o, param.getKey(), param.getValue());
                } catch (final Exception e) {
                    _log.error("Scripts: Failed setting fields for " + clazz.getName(), e);
                }
            }
        }

        if (caller != null) {
            try {
                final Field field;
                if ((field = FieldUtils.getField(clazz, "self")) != null) {
                    FieldUtils.writeField(field, o, caller.getRef());
                }
            } catch (final Exception e) {
                _log.error("Scripts: Failed setting field for " + clazz.getName(), e);
            }
        }

        Object ret = null;
        try {
            final Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i] != null ? args[i].getClass() : null;
            }

            ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
        } catch (final NoSuchMethodException nsme) {
            _log.error("Scripts: No such method " + clazz.getName() + '.' + methodName + "()!");
        } catch (final InvocationTargetException ite) {
            _log.error("Scripts: Error while calling " + clazz.getName() + '.' + methodName + "()", ite.getTargetException());
        } catch (final Exception e) {
            _log.error("Scripts: Failed calling " + clazz.getName() + '.' + methodName + "()", e);
        }

        return ret;
    }

    private static class SingletonHolder {
        protected static final Scripts _instance = new Scripts();
    }

    public class ScriptListenerImpl extends ListenerList<Scripts> {
        public void callInit() {
            getListeners(OnInitScriptListener.class).forEach(OnInitScriptListener::onInit);
        }

        public void callReload() {
            getListeners(OnReloadScriptListener.class).forEach(OnReloadScriptListener::onReload);
        }
    }
}