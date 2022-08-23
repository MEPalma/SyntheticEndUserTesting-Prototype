package lifecycle.lci.lcievent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
public class LCIEventMouse extends LCIEvent {
    public int id;
    public int mods;
    public int clickCount;
    public boolean popupTrigger;
    public int button;

    public LCIEventMouse() {

    }

    public LCIEventMouse(String target, MouseEvent e) {
        this(target, e.getID(), e.getModifiersEx(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
    }

    public LCIEventMouse(String target, int id, int mods, int clickCount, boolean popupTrigger, int button) {
        super(target);
        this.id = id;
        this.mods = mods;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
        this.button = button;
    }

    public static LCIEventMouse simpleClickOn(String target) {
        return new LCIEventMouse(target, MouseEvent.MOUSE_CLICKED, MouseEvent.NOBUTTON, 1, false, MouseEvent.BUTTON1);
    }

    @Override
    public void perform(Component c) {
        if (this.id == MouseEvent.MOUSE_CLICKED && c instanceof AbstractButton abstractButton) {
            abstractButton.doClick();
        } else {
            Rectangle cBounds;
            if (c instanceof JComponent j)
                cBounds = j.getVisibleRect();
            else
                cBounds = c.getBounds();
            int mouseX = cBounds.x + (cBounds.width / 2);
            int mouseY = cBounds.y + (cBounds.height / 2);

            MouseEvent event = new MouseEvent(c, this.id, DateUtils.getEpochTimeNow(), this.mods, mouseX, mouseY, this.clickCount, this.popupTrigger, this.button);
            c.dispatchEvent(event);
        }
    }
}
