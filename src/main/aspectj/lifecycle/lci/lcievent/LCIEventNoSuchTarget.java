package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIEventNoSuchTarget extends LCIEvent {
    public LCIEventNoSuchTarget(String target) {
        super(target);
    }

    @Override
    public void perform(Component c) {

    }
}
