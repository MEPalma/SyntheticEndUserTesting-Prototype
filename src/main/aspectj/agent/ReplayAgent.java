package agent;

import common.api.serialization.JSONObjectMapper;
import lifecycle.lci.LCI;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcistate.LCIState;

import java.io.*;
import java.util.Set;

public class ReplayAgent extends Agent {

    private final BufferedReader bufferedReader;
    private LCI next;

    public ReplayAgent(File lcFileName) throws FileNotFoundException {
        this.bufferedReader = new BufferedReader(new FileReader(lcFileName));
    }

    private void loadNext() {
        try {
            String line = bufferedReader.readLine();
            next = JSONObjectMapper.JSONObjectMapper.readValue(line, LCI.class);
        } catch (Exception ignore) {
            next = null;
            try {
                bufferedReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void init() {
        loadNext();
    }

    @Override
    protected LCIEvent selectEvent(LCIState lciState, Set<String> activeAction) {
        assert activeAction.contains(next.lciEvent.target);
        LCIEvent selected = next.lciEvent;
        loadNext();
        return selected;
    }

    @Override
    protected void assertState(LCIState lciState) {
        assert lciState.equals(next.lciState);
    }

    @Override
    protected boolean quit() {
        return next == null;
    }
}
