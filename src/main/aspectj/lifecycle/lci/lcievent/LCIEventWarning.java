package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lifecycle.lci.lcievent.LCIEvent;

import java.awt.*;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIEventWarning extends LCIEvent {
    public String message;
    public Map<String, Integer> freqs;

    public LCIEventWarning() {
    }

    public LCIEventWarning(String target, String message, Map<String, Integer> freqs) {
        super(target);
        this.message = message;
        this.freqs = freqs;
    }

    @Override
    public void perform(Component c) {

    }
}
