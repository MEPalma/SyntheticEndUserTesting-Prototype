package layer.event;

import layer.declr.DeclarationLayerPool;
import lifecycle.lci.lcievent.LCIEvent;
import tracker.Tracker;

import java.awt.*;

public abstract class EventLayer {
    private final Tracker tracker;

    public EventLayer() {
        this.tracker = Tracker.aspectOf();
    }

    protected String getTargetId(Component component) {
        var namedComponent = DeclarationLayerPool.aspectOf().getActiveNamedComponent(component);
        if (namedComponent != null)
            return namedComponent.getLogicalName();
        return null;
    }

    protected void dispatchEvent(LCIEvent event) {
        tracker.pushUpdate(event);
    }
}
