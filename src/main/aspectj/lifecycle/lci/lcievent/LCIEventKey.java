package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.utils.DateUtils;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIEventKey extends LCIEvent {
    public int id;
    public int mods;
    public int keyCode;
    public char keyChar;

    public LCIEventKey() {

    }

    public LCIEventKey(String target, KeyEvent e) {
        this(target, e.getID(), e.getModifiersEx(), e.getKeyCode(), e.getKeyChar());
    }

    public LCIEventKey(String target, int id, int mods, int keyCode, char keyChar) {
        super(target);
        this.id = id;
        this.mods = mods;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    public static LCIEventKey simpleKeyStrokeOn(String target, char c) {
        return new LCIEventKey(target, KeyEvent.KEY_TYPED, 0, 0, c);
    }

    @Override
    public void perform(Component c) {
        if (c == null)
            return;
        if (c instanceof JTextComponent textComponent) {
            if(textComponent.isEditable())
                textComponent.setText(textComponent.getText() + this.keyChar);
        } else {
            KeyEvent event = new KeyEvent(c, this.id, DateUtils.getEpochTimeNow(), this.mods, this.keyCode, this.keyChar);
            c.dispatchEvent(event);
        }
    }
}
