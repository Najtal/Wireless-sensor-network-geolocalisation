package dataReader;

import app.AppContext;
import exception.FatalException;
import exception.MoteReaderException;
import model.RawModel;
import ucc.RssiDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jvdur on 10/05/2016.
 */
public class Reader extends Thread {

    private final RawModel rawModel;

    Reader(RawModel rawModel) {
        super("MyThread");
        this.rawModel = rawModel;
    }

    public void run() {

        try {

            System.out.println("Reader: start...");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int i = Integer.parseInt(AppContext.INSTANCE.getProperty("isSystemInAFileSize"));
            System.out.println("Reader: bufferInput ok");
            System.out.println("Reader: size = " + i);

            while(i > 0 || i==-1) {

                String line = "";
                char ch;
                do {
                    ch = (char) reader.read();
                    line += ch;//String.valueOf(ch);
                } while( ch != '\n');
                //String line = reader.readLine();

                if (!line.startsWith("OUT")) continue;

                System.out.print("Line: " + line + "\t");
                String[] data = CliParser.parseIncomingLine(line);

                RssiDTO rssi = CliParser.handleData(data, rawModel);
                if (rssi != null) {
                    RawModel.INSTANCE.addRssi(rssi);
                }

                i--;
            }



        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        String line;
        try {
            while ((line = input.readLine()) != null) {
                if (!line.startsWith("OUT")) {
                    continue;
                }
                String[] data = CliParser.parseIncomingLine(line);
                CliParser.handleData(data, rawModel);
            }
            input.close();

        } catch (IOException e) {
            throw new MoteReaderException("Mote Reader Exception when reading from serialdump");
        }

        throw new FatalException("Mote Reader Serialdump process shut down, exiting");
        */
    }

}
