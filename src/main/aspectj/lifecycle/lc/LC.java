package lifecycle.lc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lifecycle.lci.LCI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class LC {
    public static final String LCs_FOLDER_REL_PATH = "./LCs";

    private final ReentrantLock lcUpdateLock;
    private final BufferedWriter lciFileBuffWriter;
    private final ObjectMapper objectMapper;

    public LC() throws IOException {
        this.lcUpdateLock = new ReentrantLock(true);
        File lcDir = new File(LCs_FOLDER_REL_PATH);
        if (!lcDir.isDirectory())
            lcDir.mkdirs();
        File lciFile = new File(LCs_FOLDER_REL_PATH + "/LC-" + UUID.randomUUID() + ".jsonl");
        if (!lciFile.exists()) {
            lciFile.createNewFile();
        }
        this.lciFileBuffWriter = new BufferedWriter(new FileWriter(lciFile, true));
        objectMapper = new ObjectMapper();
    }

    public void submitUpdate(LCI lci) {
        this.lcUpdateLock.lock();
        try {
            this.writeLCI(lci);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.lcUpdateLock.unlock();
        }
    }

    private void writeLCI(LCI lci) throws IOException {
        String jsonLine = objectMapper.writeValueAsString(lci);
        this.lciFileBuffWriter.write(jsonLine);
        this.lciFileBuffWriter.write("\n");
        this.lciFileBuffWriter.flush();
    }

}
