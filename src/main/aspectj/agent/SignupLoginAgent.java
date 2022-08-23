package agent;

import lifecycle.lci.LCI;
import lifecycle.lci.lcidate.LCIDate;
import lifecycle.lci.lcievent.LCIEvent;
import lifecycle.lci.lcievent.LCIEventKey;
import lifecycle.lci.lcievent.LCIEventMouse;
import lifecycle.lci.lcistate.LCIState;
import lifecycle.lci.lcistate.LCIStateFiniteRef;

import java.util.*;

public abstract class SignupLoginAgent extends Agent {

    public static final String ACCESS_VIEW_STATE = "AccessView";
    public static final String ACCESS_SIGNUP_BUTTON_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->AccessView->0::EButton|Signup";
    public static final String ACCESS_LOGIN_BUTTON_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->AccessView->1::EButton|Login";

    public static final String SIGNUP_VIEW_STATE = "SignUpView";
    public static final String SIGNUP_HANDLE_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->SignUpView->1::TextBox";
    public static final String SIGNUP_PASSWD_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->SignUpView->3::PasswdBox";
    public static final String SIGNUP_PASSWD_CHECK_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->SignUpView->5::PasswdBox";
    public static final String SIGNUP_BUTTON_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->SignUpView->6::EButton|Sign Up";

    public static final String LOGIN_VIEW_STATE = "LoginView";
    public static final String LOGIN_HANDLE_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->LoginView->1::TextBox";
    public static final String LOGIN_PASSWD_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->LoginView->3::PasswdBox";
    public static final String LOGIN_BUTTON_TARGET = "~MainFrame|->JRootPane->1::JLayeredPane->JPanel->MainView->1::Div->LoginView->4::EButton|Sign In";


    public record SignupLoginAgentCredential(String handle, String passwd) {
        @Override
        public String toString() {
            return "SignupLoginAgentCredential{" +
                    "handle='" + handle + '\'' +
                    ", passwd='" + passwd + '\'' +
                    '}';
        }
    }

    protected final SignupLoginAgentCredential signupLoginAgentCredential;
    private final Deque<LCI> currentCredentialLCI;
    private boolean isSingedUp;

    public SignupLoginAgent(SignupLoginAgentCredential signupLoginAgentCredential) {
        this.signupLoginAgentCredential = signupLoginAgentCredential;
        this.currentCredentialLCI = new LinkedList<>();
        this.isSingedUp = false;
        System.out.println(signupLoginAgentCredential);
    }

    private void typeSeqInto(char[] seq, String target, String state) {
        for (char c : seq) {
            this.currentCredentialLCI.add(new LCI(
                    new LCIDate(),
                    new LCIStateFiniteRef(state),
                    LCIEventKey.simpleKeyStrokeOn(target, c)
            ));
        }
    }

    private void clickOn(String target, String state) {
        this.currentCredentialLCI.add(new LCI(
                new LCIDate(),
                new LCIStateFiniteRef(state),
                LCIEventMouse.simpleClickOn(target)
        ));
    }

    private void prepCredentialSeq() {
        char[] handle = this.signupLoginAgentCredential.handle.toCharArray();
        char[] passwd = this.signupLoginAgentCredential.passwd.toCharArray();
        this.currentCredentialLCI.clear();
        if (!isSingedUp) {
            isSingedUp = true;
            clickOn(ACCESS_SIGNUP_BUTTON_TARGET, ACCESS_VIEW_STATE);
            typeSeqInto(handle, SIGNUP_HANDLE_TARGET, SIGNUP_VIEW_STATE);
            typeSeqInto(passwd, SIGNUP_PASSWD_TARGET, SIGNUP_VIEW_STATE);
            typeSeqInto(passwd, SIGNUP_PASSWD_CHECK_TARGET, SIGNUP_VIEW_STATE);
            clickOn(SIGNUP_BUTTON_TARGET, SIGNUP_VIEW_STATE);
        } else {
            clickOn(ACCESS_LOGIN_BUTTON_TARGET, ACCESS_VIEW_STATE);
        }
        typeSeqInto(handle, LOGIN_HANDLE_TARGET, LOGIN_VIEW_STATE);
        typeSeqInto(passwd, LOGIN_PASSWD_TARGET, LOGIN_VIEW_STATE);
        clickOn(LOGIN_BUTTON_TARGET, LOGIN_VIEW_STATE);
    }

    @Override
    protected void init() {
        isSingedUp = false;
        prepCredentialSeq();
    }

    protected boolean isOnCredentialSequenceState(LCIState lciState) {
        return lciState instanceof LCIStateFiniteRef ref && ref.refId.equals(ACCESS_VIEW_STATE);
    }

    @Override
    protected LCIEvent selectEvent(LCIState lciState, Set<String> activeAction) {
        if (this.currentCredentialLCI.size() > 0) {
            LCI lci = this.currentCredentialLCI.removeFirst();
            assert activeAction.contains(lci.lciEvent.target);
            return lci.lciEvent;
        } else if (isOnCredentialSequenceState(lciState)) {
            this.prepCredentialSeq();
            return selectEvent(lciState, activeAction);
        } else return selectNonCredentialEvent(lciState, activeAction);
    }

    protected abstract LCIEvent selectNonCredentialEvent(LCIState lciState, Set<String> activeActions);

    @Override
    protected void assertState(LCIState lciState) {
        if (this.currentCredentialLCI.size() > 0) {
            LCI lci = this.currentCredentialLCI.peekFirst();
            assert lciState.equals(lci.lciState);
        } else assertNonCredentialEvent(lciState);
    }

    protected abstract void assertNonCredentialEvent(LCIState lciState);
}
