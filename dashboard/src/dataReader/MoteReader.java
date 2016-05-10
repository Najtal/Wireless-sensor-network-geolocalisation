package dataReader;

import app.AppContext;
import exception.FatalException;
import exception.MoteReaderException;
import model.RawModel;
import util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jvdur on 09/05/2016.
 */
public class MoteReader {

    private final RawModel rawModel;
    private final String comPort;
    private static final String SERIALDUMP_LINUX = "./tools/sky/serialdump-linux";
    private String fullCommand;
    private Process serialDumpProcess;
    private Reader reader;

    private MoteReader(RawModel rawModel) {

        // Init context variables
        this.rawModel = rawModel;
        this.comPort = AppContext.INSTANCE.getProperty("dataReaderComPort");

        // Build command string
        fullCommand = SERIALDUMP_LINUX + " " + "-b115200" + " " + comPort;
    }


    private void startReader() {

        // Reader
        BufferedReader input = null;
        try {

            String[] cmd = fullCommand.split(" ");

            // Execute command
            serialDumpProcess = Runtime.getRuntime().exec(cmd);

            // Init buffers
            input = new BufferedReader(new InputStreamReader(serialDumpProcess.getInputStream()));

            /*
            // TODO read the error stream
            final BufferedReader err = new BufferedReader
                    (new InputStreamReader(serialDumpProcess.getErrorStream()));
            */

            // Create reader
            this.reader = new Reader(input, rawModel);

            // Start reading
            reader.start();

        } catch (IOException e) {
            throw new MoteReaderException("The Mote Reader serialDumpProcess could not be initiated.",e);
        } catch (MoteReaderException fe) {
            try {
                input.close();
            } catch (IOException e) {
                restartReader(fe.getMessage() + " > FOLLOWED BY > " + e.getMessage());
            }
            restartReader(fe.getMessage());
        }


        // TODO: 09/05/2016 init the mote output reader
        // thread fill up the raw model
        // when data from a same batch are all fetch, trigger the model. The model will then clean raw data to analyzed model

    }


    private void restartReader(String reason) {
        this.reader.interrupt();
        this.reader = null;

        Log.logWarning("Mote reader has restarted due to: " + reason);

        startReader();
    }

}
