package dataReader;

/**
 * Created by jvdur on 10/05/2016.
 */
public class CliParser {

    public static String[] parseIncomingLine(String line) {

        if (line == null) {
            System.err.println("Parsing null line");
            return null;
        }

        /* Split line into components */
        return line.split(" ");

    }

}
