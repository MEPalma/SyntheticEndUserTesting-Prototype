package layer.event;

import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcievent.LCIEventMouse;
import lifecycle.lci.lcievent.LCIEventNoSuchTarget;
import naming.NamedComponent;

import java.awt.*;
import java.awt.event.MouseEvent;

public aspect MouseEventLayer extends EventLayer {
    pointcut isMouseClickedEvent(MouseEvent e):
            execution(* java.awt.event.MouseListener+.*(MouseEvent)) && args(e);

    before (MouseEvent e): isMouseClickedEvent(e) {
        Component targetComponent = e.getComponent();
        String targetId = getTargetId(targetComponent);
        LCIEvent event = (targetId == null) ?
                new LCIEventNoSuchTarget(NamedComponent.compileLogicalName(targetComponent)) :
                new LCIEventMouse(targetId, e);
        dispatchEvent(event);
    }
}
