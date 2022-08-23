package lifecycle.lci;

import lifecycle.lci.lcidate.LCIDate;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcistate.LCIState;

public class LCI {
    public LCIDate lciDate;
    public LCIState lciState;
    public LCIEvent lciEvent;

    public LCI() {

    }

    public LCI(LCIDate lciDate, LCIState lciState, LCIEvent lciEvent) {
        this.lciDate = lciDate;
        this.lciState = lciState;
        this.lciEvent = lciEvent;
    }
}
