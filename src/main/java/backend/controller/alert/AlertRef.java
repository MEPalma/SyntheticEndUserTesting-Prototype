package backend.controller.alert;

import backend.persistence.entity.alert.Alert;

public class AlertRef implements Comparable<AlertRef> {
    private final String handle;
    private final Alert alert;

    public AlertRef(String handle, Alert alert) {
        this.handle = handle;
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public String getHandle() {
        return handle;
    }


    @Override
    public int hashCode() {
        return alert.getId().toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AlertRef alertRef)
            return hashCode() == alertRef.hashCode();
        return false;
    }

    @Override
    public int compareTo(AlertRef o) {
        return Long.compare(alert.getEpochCreated(), o.alert.getEpochCreated());
    }
}
