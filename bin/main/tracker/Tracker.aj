package tracker;

import layer.state.FiniteRefStateLayer;
import layer.state.StateLayer;
import lifecycle.lc.LC;
import lifecycle.lci.*;
import lifecycle.lci.lcidate.LCIDate;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcistate.LCIState;

import java.io.IOException;

public aspect Tracker {
    private final StateLayer stateLayer;
    private final LC lc;

    public Tracker() throws IOException {
        this.lc = new LC();
        this.stateLayer = FiniteRefStateLayer.aspectOf();
    }

    public synchronized void pushUpdate(LCIEvent lciEvent) {
        LCIDate lciDate = new LCIDate();
        LCIState lciState = this.stateLayer.getState();
        LCI lci = new LCI(lciDate, lciState, lciEvent);
        lc.submitUpdate(lci);
    }

    public LCIState getState() {
        return stateLayer.getState();
    }
}
