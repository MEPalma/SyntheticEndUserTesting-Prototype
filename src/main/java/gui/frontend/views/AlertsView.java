package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.ItemsList;
import gui.frontend.components.base.Text;
import gui.frontend.components.ext.alert.AlertItemCmp;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class AlertsView extends Div {

    public static class AlertsViewData {
        public final List<AlertItemCmp.AlertItemCmpData> alertItemCmpData;

        public AlertsViewData(List<AlertItemCmp.AlertItemCmpData> alertItemCmpData) {
            this.alertItemCmpData = alertItemCmpData;
        }
    }

    public interface AlertsViewListener {
        AlertItemCmp.AlertItemCmpData onAction(AlertItemCmp.AlertItemCmpData alertItemCmpData);
    }

    private final ItemsList<JComponent> alertsList;
    private final Map<String, AlertItemCmp> alertItemCmpById;

    private final AlertItemCmp.AlertItemCmpListener alertItemCmpListener;

    private AlertsViewListener alertsViewListener;
    private AlertsViewData data;

    public AlertsView() {
        this.alertsList = new ItemsList<>();
        JScrollPane scrollPane = new JScrollPane(
                alertsList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(UIConstants.SCROLL_SPEED_INCREMENT);
        //
        this.alertItemCmpById = Collections.synchronizedMap(new HashMap<>());
        //
        this.alertItemCmpListener = new AlertItemCmp.AlertItemCmpListener() {
            @Override
            public void onAction(AlertItemCmp.AlertItemCmpData data) {
                var listener = alertsViewListener;
                if (listener != null) {
                    var cmp = alertItemCmpById.getOrDefault(data.getId(), null);
                    var newData = listener.onAction(data);
                    if (cmp != null && newData != null)
                        cmp.updateData(newData);
                }
            }
        };
        //
        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_M);
        titleStyle.setForeground(UIConstants.BLUE);
        Text title = new Text("Alerts", titleStyle);
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setAlertsViewListener(AlertsViewListener alertsViewListener) {
        this.alertsViewListener = alertsViewListener;
    }

    public synchronized AlertsViewData getData() {
        return data;
    }

    public synchronized void updateData(AlertsViewData data) {
        data.alertItemCmpData.sort(Collections.reverseOrder());
        this.data = data;
        this.alertItemCmpById.clear();
        List<JComponent> newCmps = new ArrayList<>(data.alertItemCmpData.size());
        for (var alertItemCmpData : data.alertItemCmpData) {
            AlertItemCmp cmp = new AlertItemCmp();
            cmp.updateData(alertItemCmpData);
            cmp.setListener(this.alertItemCmpListener);
            alertItemCmpById.put(alertItemCmpData.getId(), cmp);
            newCmps.add(getAlertItemCmpCell(cmp));
        }
        this.alertsList.setItems(newCmps);
        repaint();
        revalidate();
    }

    private static Div getAlertItemCmpCell(AlertItemCmp alertItemCmp) {
        Style style = Div.defaultStyle();
        style.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK));
        Div cell = new Div(new GridLayout(1, 1, 0, 0), style);
        cell.add(alertItemCmp);
        return cell;
    }
}
