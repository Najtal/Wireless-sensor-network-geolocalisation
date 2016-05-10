package util;

import app.AppContext;

import java.util.logging.Level;

/**
 * Created by jvdur on 10/05/2016.
 */
public class Log {

    public static void logInfo(String msg) {
        AppContext.INSTANCE.getLogger().log(Level.INFO, msg);
        System.out.print("Logger-INFO : " + msg + "\n");
    }

}
