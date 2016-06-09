package util;

import app.AppContext;

import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by jvdur on 10/05/2016.
 */
public class Log {

    private static StyledDocument logs;

    private static Style styleInfo;
    private static Style styleFine;
    private static Style styleWarning;
    private static Style styleSevere;

    private static ActionListener guiListener;


    static {
        logs = new DefaultStyledDocument();

        styleInfo = new StyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(styleInfo, Color.blue);

        styleFine = new StyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(styleFine, Color.green);

        styleWarning = new StyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(styleWarning, Color.yellow);

        styleSevere = new StyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(styleSevere, Color.red);
    }



    /**
     * Log an information message
     * @param msg
     */
    public static void logInfo(String msg) {
        AppContext.INSTANCE.getLogger().log(Level.INFO, msg);
        System.out.print("Logger-INFO : " + msg + "\n");

        try {
            logs.insertString(logs.getLength(), "\n"+msg, styleInfo);
            propagate();
        } catch (BadLocationException e) {
        }

    }

    /**
     * Log the message of an event that has been handled
     * @param msg The message to be logged
     */
    public static void logFine(String msg) {
        AppContext.INSTANCE.getLogger().log(Level.FINE, msg);
        System.out.print("Logger-FINE : " + msg + "\n");

        try {
            logs.insertString(logs.getLength(), "\n"+msg, styleFine);
            propagate();
        } catch (BadLocationException e) {
        }

    }

    /**
     * Log a warning message. Event not completely handled
     * @param msg The message to be logged
     */
    public static void logWarning(String msg) {
        AppContext.INSTANCE.getLogger().log(Level.WARNING, msg);
        System.out.print("Logger-WARNING : " + msg + "\n");

        try {
            logs.insertString(logs.getLength(), "\n"+msg, styleWarning);
            propagate();
        } catch (BadLocationException e) {
        }

    }

    /**
     * Log the message of a severe problem that could lead to a Fatal exception.
     * @param msg The message to be logged
     */
    public static void logSevere(String msg) {
        AppContext.INSTANCE.getLogger().log(Level.SEVERE, msg);
        System.out.print("Logger-SEVERE : " + msg + "\n");

        try {
            logs.insertString(logs.getLength(), "\n"+msg, styleWarning);
            propagate();
        } catch (BadLocationException e) {
        }


    }

    public static void registerLog(ActionListener al) {
        guiListener = al;
    }

    private static void propagate() {
        if (guiListener != null)
            guiListener.actionPerformed(null);
    }


    public static Document getLogs() {
        return logs;
    }
}
