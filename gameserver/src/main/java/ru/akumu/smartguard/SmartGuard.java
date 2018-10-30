package ru.akumu.smartguard;

import org.jts.protection.network.ProtectionGameCrypt;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.handler.admincommands.impl.SmartGuardMenu;
import ru.akumu.smartguard.core.SmartCore;
import ru.akumu.smartguard.core.log.GuardLog;
import ru.akumu.smartguard.wrappers.ServerInterface;

import java.lang.reflect.Method;
import java.util.Arrays;

public class SmartGuard {
    public static boolean IS_LOADING_FINISHED = false;

    public static void main(String[] args) {
        try {
            if (!SmartGuard.class.getProtectionDomain().getCodeSource().equals(ProtectionGameCrypt.class.getProtectionDomain().getCodeSource())) {
                GuardLog.getLogger().severe("Warning! File [smrt.jar] is not first in your classpath list, SmartGuard will not work properly!");
                return;
            }
        } catch (Exception e) {
            GuardLog.logException(e);
        }

        if (args.length == 0) {
            GuardLog.getLogger().severe("Main class not specified!");
            return;
        }

        if (!SmartCore.init(new ServerInterface())) {
            GuardLog.getLogger().severe("Failed to initialize SmartGuard.");
            return;
        }

        try {
            Class<?> c = Class.forName(args[0]);
            Method main = c.getDeclaredMethod("main", String[].class);
            String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
            main.invoke(null, (Object) mainArgs);

            GuardLog.getLogger().info("SmartGuard has been initialized.");
        } catch (Exception e) {
            GuardLog.getLogger().severe("GameServer failed to start!");
            GuardLog.logException(e);
            return;
        }

        try {
            AdminCommandHandler.getInstance().registerAdminCommandHandler(new SmartGuardMenu());
        } catch (Exception e) {
            GuardLog.getLogger().severe("Error initializing SmartGuard AdminCommandHandler!");
            GuardLog.logException(e);
        }

        IS_LOADING_FINISHED = true;
    }
}