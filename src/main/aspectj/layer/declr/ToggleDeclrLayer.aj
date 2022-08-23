package layer.declr;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public aspect ToggleDeclrLayer extends DeclarationLayer<Component> {

    public static class PretendToggleListener extends MouseAdapter {
        @Override
        public synchronized void mouseClicked(MouseEvent e) {

        }
    }

    pointcut isToggleDecl():
            call(javax.swing.JToggleButton+.new(..));

    Object around(): isToggleDecl() {
        Component cmp = (Component) proceed();

        var listeners = cmp.getMouseListeners();
        for (var listener : listeners)
            cmp.removeMouseListener(listener);

        cmp.addMouseListener(new PretendToggleListener());
        for (var listener : listeners)
            cmp.addMouseListener(listener);

        putNamedComponent(cmp);
        return cmp;
    }
}
