package agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.api.serialization.JSONObjectMapper;
import lifecycle.lci.LCI;
import lifecycle.lci.lcievent.*;
import lifecycle.lci.lcistate.LCIState;
import lifecycle.lci.lcistate.LCIStateFiniteRef;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrequencyAgent extends RandomAgent {

    private static class EventStatePair {
        public String event;
        public int eventFreq;
        public Map<String, Integer> nextStateFreqByStateRef;

        public EventStatePair() {
            this(null, 0);
        }

        public EventStatePair(String event, int eventFreq) {
            this.event = event;
            this.eventFreq = eventFreq;
            this.nextStateFreqByStateRef = new HashMap<>();
        }
    }

    private final Map<String, Map<String, EventStatePair>> eventFreqByStateRef;
    private EventStatePair currentEventStatePair;

    public FrequencyAgent(SignupLoginAgentCredential signupLoginAgentCredential, File logLCFileName) throws IOException {
        super(signupLoginAgentCredential);
        this.eventFreqByStateRef = new HashMap<>();
        this.currentEventStatePair = null;
        buildFrequencyMap(logLCFileName);
    }

    private void buildFrequencyMap(File logLCFileName) throws IOException {
        this.eventFreqByStateRef.clear();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(logLCFileName));
        LCI prevLci = null;
        for (String line : bufferedReader.lines().toList()) {
            try {
                LCI lci = JSONObjectMapper.JSONObjectMapper.readValue(line, LCI.class);
                if (lci.lciState instanceof LCIStateFiniteRef lciState) {
                    if (prevLci != null && prevLci.lciState instanceof LCIStateFiniteRef prevLciState)
                        updateFreqMapWith(prevLciState.refId, prevLci.lciEvent.target, lciState.refId);
                    prevLci = lci;
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (prevLci != null && prevLci.lciState instanceof LCIStateFiniteRef prevLciState)
            updateFreqMapWith(prevLciState.refId, prevLci.lciEvent.target, null);
        bufferedReader.close();
    }

    private void updateFreqMapWith(String state0, String event, String state1) {
        Map<String, EventStatePair> freqByEvent = this.eventFreqByStateRef.getOrDefault(state0, new HashMap<>());
        EventStatePair eventStatePair = freqByEvent.getOrDefault(event, new EventStatePair(event, 0));
        freqByEvent.put(event, eventStatePair);
        this.eventFreqByStateRef.put(state0, freqByEvent);
        //
        eventStatePair.eventFreq += 1;
        eventStatePair.nextStateFreqByStateRef.put(
                state1,
                eventStatePair.nextStateFreqByStateRef.getOrDefault(state1, 0) + 1
        );
    }

    @Override
    protected boolean quit() {
        return false;
    }

    private String weightedSelection(Map<String, Integer> choices) {
        double totalWeight = 0.0;
        for (int i : choices.values())
            totalWeight += i;
        double x = random.nextDouble() * totalWeight;
        for (var choice : choices.entrySet()) {
            x -= choice.getValue();
            if (x <= 0.0)
                return choice.getKey();
        }
        return null;
    }

    @Override
    protected LCIEvent selectNonCredentialEvent(LCIState lciState, Set<String> activeActions) {
        String eventTarget = null;
        if (lciState instanceof LCIStateFiniteRef lciStateFiniteRef) {
            var freqByEvent =
                    this.eventFreqByStateRef.getOrDefault(lciStateFiniteRef.refId, null);
            if (freqByEvent != null) {
                Map<String, Integer> choiceMap = new HashMap<>();
                for (var action : activeActions)
                    choiceMap.put(action, 1);
                for (var e : freqByEvent.entrySet())
                    if (choiceMap.containsKey(e.getKey()))
                        choiceMap.put(e.getKey(), e.getValue().eventFreq);
                eventTarget = weightedSelection(choiceMap);
                currentEventStatePair = freqByEvent.getOrDefault(eventTarget, null);
            }
        }
        if (eventTarget == null) {
            eventTarget = selectRandomAction(activeActions);
            currentEventStatePair = null;
        }
        return selectRandomUserEvent(eventTarget);
    }

    @Override
    protected void assertNonCredentialEvent(LCIState lciState) {
        String stateStr;
        try {
            stateStr = JSONObjectMapper.JSONObjectMapper.writeValueAsString(lciState);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (currentEventStatePair != null && lciState instanceof LCIStateFiniteRef lciStateFiniteRef) {
            var nextStateFreq = currentEventStatePair.nextStateFreqByStateRef;
            int freq = nextStateFreq.getOrDefault(lciStateFiniteRef.refId, -1);
            int sum = 0;
            for (var se : nextStateFreq.values())
                sum += se;

            if (freq == -1)
                tracker.pushUpdate(new LCIEventIncorrectState(currentEventStatePair.event, stateStr, nextStateFreq));
            else
                tracker.pushUpdate(new LCIEventStateValidation(currentEventStatePair.event, stateStr, (freq/sum), nextStateFreq));
        } else {
            String event = (currentEventStatePair != null) ? currentEventStatePair.event : null;
            var freqs = (currentEventStatePair != null) ? currentEventStatePair.nextStateFreqByStateRef : null;
            tracker.pushUpdate(new LCIEventWarning(event, "No expectations for state: " + stateStr, freqs));
        }
    }
}
