package layer.declr;

import java.awt.*;
import java.awt.event.MouseListener;

public aspect MouseDeclrLayer extends DeclarationLayer<Component> {
    pointcut isAddMouseListener(Component c, MouseListener ml):
            call(void Component+.addMouseListener(MouseListener+)) && args(ml) && target(c);

    after (Component c, MouseListener ml): isAddMouseListener(c, ml) {
        putNamedComponent(c);
    }
}
