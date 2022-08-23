package agent;

import gui.frontend.components.base.EButton;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcievent.LCIEventKey;
import lifecycle.lci.lcievent.LCIEventMouse;
import lifecycle.lci.lcistate.LCIState;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RandomAgent extends SignupLoginAgent {

    private static final long SEED = 1L;
    protected final Random random;

    public RandomAgent(SignupLoginAgentCredential signupLoginAgentCredential) {
        super(signupLoginAgentCredential);
        this.random = new Random(RandomAgent.SEED);
    }

    @Override
    protected boolean quit() {
        return false;
    }

    protected String selectRandomAction(Collection<String> activeActions) {
        List<String> actionList = new LinkedList<>(activeActions);
        Collections.sort(actionList);
        int randomIndex = new Random().nextInt(activeActions.size());
        return actionList.get(randomIndex);
    }

    private char getRandomChar() {
        return (char) (65 + random.nextInt(25));
    }

    protected LCIEvent selectRandomUserEvent(String target) {
        Component component = declarationLayerPool.getActiveComponent(target);
        if (component instanceof AbstractButton || component instanceof EButton)
            return LCIEventMouse.simpleClickOn(target);
        else if (component instanceof JTextComponent)
            return LCIEventKey.simpleKeyStrokeOn(target, getRandomChar());
        else
            return switch (random.nextInt(2)) {
                case 0 -> LCIEventMouse.simpleClickOn(target);
                default -> LCIEventKey.simpleKeyStrokeOn(target, getRandomChar());
            };
    }

    @Override
    protected LCIEvent selectNonCredentialEvent(LCIState lciState, Set<String> activeActions) {
        String randomAction = selectRandomAction(activeActions);
        LCIEvent event = selectRandomUserEvent(randomAction);
        return event;
    }

    @Override
    protected void assertNonCredentialEvent(LCIState lciState) {

    }

}
