package naming;

import gui.frontend.components.base.EButton;
import gui.frontend.components.base.Text;
import gui.frontend.components.ext.alert.AlertItemCmp;

import javax.swing.*;
import java.awt.*;

public class Naming {
    public static final String ACTIVE_PREFIX = "~"; // ""<9dc7360acac9>";
    public static final String TEXT_SEPARATOR = "|"; //"<4ac07aa93e9f>";
    public static final String COMPONENT_SEPARATOR = "->"; //"<0036e585c4ff>";
    public static final String INDEX_SEPARATOR = "::"; //"<e5dc921d0763>";
    public static final String SUBTYPE_SEPARATOR = "_"; // "<b98352cacb91>";

    public static String getLogicalName(Component c) {
        if (c == null)
            return null;
        String name = getComponentName(c);
        var p = c.getParent();
        String parentName = getLogicalName(c.getParent());
        name = renameWithIndex(name, c, p);
        return (parentName != null) ? parentName + COMPONENT_SEPARATOR + name : name;
    }

    public static String getComponentName(Component c) {
        String res;
        if (c instanceof EButton cEButton)
            res = c.getClass().getSimpleName() + TEXT_SEPARATOR + cEButton.getText().strip();
        else if (c instanceof Text cText)
            res = c.getClass().getSimpleName() + TEXT_SEPARATOR + cText.getText().strip();
        else if (c instanceof AbstractButton cAbstractButton)
            res = c.getClass().getSimpleName() + TEXT_SEPARATOR + cAbstractButton.getText().strip();
        else if (c instanceof AlertItemCmp cAlertItemCmp) {
            res = cAlertItemCmp.getClass().getSimpleName();
            var data = cAlertItemCmp.getData();
            if (data != null)
                res += SUBTYPE_SEPARATOR + data.getClass().getSimpleName();
        } else if (c instanceof Frame cFrame)
            res = ACTIVE_PREFIX + c.getClass().getSimpleName() + TEXT_SEPARATOR + cFrame.getTitle().strip();
        else
            res = c.getClass().getSimpleName();
        return res;
    }

    private static String renameWithIndex(String name, Component child, Container parent) {
        if (parent != null) {
            var children = parent.getComponents();
            if (children.length > 1) {
                for (int i = 0; i < children.length; ++i) {
                    if (children[i] == child) {
                        name = i + INDEX_SEPARATOR + name;
                        break;
                    }
                }
            }
        }
        return name;
    }
}
