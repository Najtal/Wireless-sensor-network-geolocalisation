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
    private int mode;
    private int lastTimeStampInMilli;

    Reader(RawModel rawModel, int mode) {
        super("MyThread");
        this.rawModel = rawModel;
        this.mode = mode;
        this.lastTimeStampInMilli = 0;
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

                if (mode == 2) {
                    // Take of the time and sleep
                    String time = line.split(" ")[0];
                    line = line.split(" ")[1];

                    String minute = time.split(":")[0];
                    String second = time.split(":")[1];

                    int minutes = Integer.parseInt(minute);
                    double seconds = Double.parseDouble(second);
                    int timeStampInMilli = (int) (1000*((minutes*60)+seconds));

                    int diff = timeStampInMilli - lastTimeStampInMilli;
                    lastTimeStampInMilli = timeStampInMilli;

                    try {
                        this.sleep(diff);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

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

    }

}
