package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LCIEventKey.class, name = "LCIEventKey"),
        @JsonSubTypes.Type(value = LCIEventMouse.class, name = "LCIEventMouse"),
        @JsonSubTypes.Type(value = LCIEventWarning.class, name = "LCIEventWarning"),
        @JsonSubTypes.Type(value = LCIEventStateValidation.class, name = "LCIEventStateValidation"),
        @JsonSubTypes.Type(value = LCIEventNoSuchTarget.class, name = "LCIEventNoSuchTarget"),
        @JsonSubTypes.Type(value = LCIEventIncorrectState.class, name = "LCIEventIncorrectState")
})
public abstract class LCIEvent {
    public String target;

    public LCIEvent() {
    }

    public LCIEvent(String target) {
        this.target = target;
    }

    public abstract void perform(Component c);
}
