package layer.declr;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public aspect KeyboardDeclrLayer extends DeclarationLayer<Component> {

    public static class PretendKeyListener implements KeyListener {
        @Override
        public synchronized void keyTyped(KeyEvent e) {

        }

        @Override
        public synchronized void keyPressed(KeyEvent e) {

        }

        @Override
        public synchronized void keyReleased(KeyEvent e) {

        }
    }

    public static class PretendTextInputMouseListener extends MouseAdapter {
        @Override
        public synchronized void mouseClicked(MouseEvent e) {

        }
    }

    pointcut isNewTextInput():
            call(gui.frontend.components.base.TextInput+.new(..));

    pointcut isNewTextBox():
            call(gui.frontend.components.base.TextBox+.new(..));

    pointcut isNewPasswordBox():
            call(gui.frontend.components.base.PasswdBox+.new(..));

    Object around(): isNewTextInput() || isNewTextBox() || isNewPasswordBox() {
        Component cmp = (Component) proceed();

        var keyListeners = cmp.getKeyListeners();
        for (var listener : keyListeners)
            cmp.removeKeyListener(listener);

        cmp.addKeyListener(new KeyboardDeclrLayer.PretendKeyListener());
        for (var listener : keyListeners)
            cmp.addKeyListener(listener);

        var mouseListeners = cmp.getMouseListeners();
        for (var listener : mouseListeners)
            cmp.removeMouseListener(listener);

        cmp.addMouseListener(new PretendTextInputMouseListener());
        for (var listener : mouseListeners)
            cmp.addMouseListener(listener);

        putNamedComponent(cmp);
        return cmp;
    }

}
