package lifecycle.lci.lcistate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LCIState.class, name = "LCIState"),
        @JsonSubTypes.Type(value = LCIStateActiveActions.class, name = "LCIStateActiveActions"),
        @JsonSubTypes.Type(value = LCIStateFiniteRef.class, name = "LCIStateFiniteRef"),
})
public abstract class LCIState {

}
