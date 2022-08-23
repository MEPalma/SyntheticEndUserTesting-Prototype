package lifecycle.lci.lcistate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIStateFiniteRef extends LCIState {
    public String refId;

    public LCIStateFiniteRef() {

    }

    public LCIStateFiniteRef(String refId) {
        this.refId = refId;
    }

    @Override
    public String toString() {
        return refId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LCIStateFiniteRef s)
            return refId.equals(s.refId);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(refId);
    }
}
