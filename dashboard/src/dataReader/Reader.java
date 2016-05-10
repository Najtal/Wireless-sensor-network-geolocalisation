package dataReader;

import exception.FatalException;
import exception.MoteReaderException;
import model.RawModel;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by jvdur on 10/05/2016.
 */
public class Reader extends Thread {

    private final RawModel rawModel;
    private BufferedReader input;

    Reader(BufferedReader input, RawModel rawModel) {
        super("MyThread");
        this.input = input;
        this.rawModel = rawModel;
    }

    public void run() {
        String line;
        try {
            while ((line = input.readLine()) != null) {
                String[] data = CliParser.parseIncomingLine(line);
                CliParser.handleData(data);
            }
            input.close();

        } catch (IOException e) {
            throw new MoteReaderException("Mote Reader Exception when reading from serialdump");
        }

        throw new FatalException("Mote Reader Serialdump process shut down, exiting");
    }

}
