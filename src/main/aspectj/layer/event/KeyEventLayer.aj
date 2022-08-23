package layer.event;

import layer.declr.KeyboardDeclrLayer;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcievent.LCIEventKey;
import lifecycle.lci.lcievent.LCIEventNoSuchTarget;
import naming.NamedComponent;

import java.awt.*;
import java.awt.event.KeyEvent;

public aspect KeyEventLayer extends EventLayer {
    pointcut isPretendKeyTypedEvent(KeyEvent e):
            execution(* layer.declr.KeyboardDeclrLayer.PretendKeyListener+.keyTyped(KeyEvent)) &&
                    args(e);

    before (KeyEvent e): isPretendKeyTypedEvent(e) {
        Component targetComponent = e.getComponent();
        String targetId = getTargetId(targetComponent);
        LCIEvent event = (targetId == null) ?
                new LCIEventNoSuchTarget(NamedComponent.compileLogicalName(targetComponent)) :
                new LCIEventKey(targetId, e);
        dispatchEvent(event);
    }
}
