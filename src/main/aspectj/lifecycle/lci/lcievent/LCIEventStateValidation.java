package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIEventStateValidation extends LCIEvent {
    public String state;
    public int prob;
    public Map<String, Integer> freqs;

    public LCIEventStateValidation() {

    }

    public LCIEventStateValidation(String target, String state, int prob, Map<String, Integer> freqs) {
        super(target);
        this.state = state;
        this.prob = prob;
        this.freqs = freqs;
    }

    @Override
    public void perform(Component c) {

    }
}
