package lifecycle.lci.lcidate;

import java.util.Calendar;

public class LCIDate {
    public final long whenMs;

    public LCIDate() {
        whenMs = Calendar.getInstance().getTimeInMillis();
    }

    public LCIDate(long whenMs) {
        this.whenMs = whenMs;
    }
}
