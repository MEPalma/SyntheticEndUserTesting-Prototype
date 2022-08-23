package lifecycle.lci.lcievent;

import java.awt.*;
import java.util.Map;

public class LCIEventIncorrectState extends LCIEvent {
    public String state;
    public Map<String, Integer> freqs;

    public LCIEventIncorrectState() {

    }

    public LCIEventIncorrectState(String target, String state, Map<String, Integer> freqs) {
        super(target);
        this.state = state;
        this.freqs = freqs;
    }

    @Override
    public void perform(Component c) {

    }
}
