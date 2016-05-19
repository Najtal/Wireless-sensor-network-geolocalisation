package dataReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jvdur on 19/05/2016.
 */
public class AIOR {

    private static final String SERIALDUMP_LINUX = "./tools/sky/serialdump-linux";
    private static String fullCommand;
    private Process serialDumpProcess;
    private InReader reader;

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
            this.reader = new InReader(input);

            // Start reading
            reader.start();

        } catch (IOException e) {
            System.out.println("IOException:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception fe) {
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

        System.out.println("Mote reader has restarted due to: " + reason);
        startReader();
    }


    class InReader extends Thread {

        private BufferedReader input;

        InReader(BufferedReader input) {
            super("MyThread");
            this.input = input;
        }

        public void run() {

            System.out.println("Reader started");

            String line;
            try {
                while ((line = input.readLine()) != null) {
                    String[] data = AIOR.parseIncomingLine(line);
                    AIOR.handleData(data);
                }
                input.close();

            } catch (IOException e) {
                System.out.println("IOException:" + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("The Reader has finish. It should not :/");
            System.out.println("Reader done");
        }

    }

    public static String[] parseIncomingLine(String line) {
        System.out.println("Parsing line: "+line);
        if (line == null) {
            System.err.println("Parsing null line");
            return null;
        }
        /* Split line into components */
        return line.split(",");
    }

    public static void handleData(String[] data) {

        if (data[0].equals("OUT")) {
            System.out.print("OUT:\t");
            for (int i=1;i<data.length;i++) {
                System.out.print(data[i] + "  ");
            }
        }
    }

    public static void main (String[] arg) {

        System.out.println("AIOR start");

        fullCommand = SERIALDUMP_LINUX + " " + "-b115200" + " " + arg[0];
        System.out.println("fullCommand: " + fullCommand);

        new AIOR().startReader();

    }


}
