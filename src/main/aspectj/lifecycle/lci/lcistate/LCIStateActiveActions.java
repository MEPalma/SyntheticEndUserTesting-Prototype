package lifecycle.lci.lcistate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIStateActiveActions extends LCIState {
    public Set<String> activeActions;

    public LCIStateActiveActions() {

    }

    public LCIStateActiveActions(Set<String> activeActions) {
        this.activeActions = activeActions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LCIStateActiveActions s)
            return activeActions.equals(s.activeActions);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(activeActions);
    }
}
