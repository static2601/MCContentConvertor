package MCContentConvertor;

import java.io.File;
import java.io.IOException;

public class BatchRunner{
    public BatchRunner() {}

    public static Process process = null;
    public static int runBat(String[] args, String batDir) {

        // execute bat file
        int code = -1;
        try {
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(new File(batDir));
            process = pb.start();
            System.out.println("processing bat...");
            System.out.println(process.children() + ", pid: " + process.pid());
            code = process.waitFor();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert process != null;
        process.destroy();
        return code;
    }
}
