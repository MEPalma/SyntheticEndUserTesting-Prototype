package agent;

import common.utils.DateUtils;
import layer.declr.DeclarationLayerPool;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcistate.LCIState;
import tracker.Tracker;

import java.awt.*;
import java.util.Set;

public abstract class Agent implements Runnable {
    protected final Tracker tracker = Tracker.aspectOf();
    protected final DeclarationLayerPool declarationLayerPool = DeclarationLayerPool.aspectOf();

    protected abstract void init();

    protected abstract LCIEvent selectEvent(LCIState lciState, Set<String> activeAction);

    protected abstract void assertState(LCIState lciState);

    protected abstract boolean quit();

    protected int maxPerformEventSecondsWait() {
        return 5;
    }

    protected void performEvent(LCIEvent lciEvent) {
        Component targetComponent = declarationLayerPool.getActiveComponent(lciEvent.target);
        if (targetComponent == null) {
            final long maxWaitTimeMs = maxPerformEventSecondsWait() * 1000L;
            final long segWaitTimeMs = 500;
            long waitTimeMs = 500;
            while (targetComponent == null && waitTimeMs < maxWaitTimeMs) {
                targetComponent = declarationLayerPool.getActiveComponent(lciEvent.target);
                sleep(segWaitTimeMs);
                waitTimeMs += segWaitTimeMs;
            }
        }
        if (targetComponent != null)
            lciEvent.perform(targetComponent);
    }

    protected void sleep(long ms) {
        final long targetMs = DateUtils.getEpochTimeNow() + ms;
        long waitTimeMs = targetMs - DateUtils.getEpochTimeNow();
        while (waitTimeMs > 0) {
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException ignore) {
                ;
            }
            waitTimeMs = targetMs - DateUtils.getEpochTimeNow();
        }
    }

    @Override
    public void run() {
        sleep(1_000);

        init();
        while (!quit()) {
            LCIState lciState = tracker.getState();
            assertState(lciState);

            LCIEvent lciEvent = selectEvent(lciState, declarationLayerPool.getActiveActions());
            performEvent(lciEvent);

            sleep(1_000);
        }
    }
}
