package backend.controller;

import backend.Backend;
import common.api.error.ResponseException;
import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

import common.alert.UserLogin;
import common.api.twitteruser.UserResponse;
import common.api.twitteruser.postuser.UserSignOut;
import common.api.twitteruser.postuser.UserSignin;
import common.api.twitteruser.putuser.UserSignup;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static common.comms.HttpApiComms.BACKEND_CONN_ENDPOINT_PORT;
import static org.junit.jupiter.api.Assertions.fail;

public class TestController {

    private final Backend backend;
    private final Controller controller;

    public TestController() throws IOException {
        this.backend = new Backend();
        this.controller = this.backend.getController();
    }

    @Test
    public void testUserSignup() throws ResponseException {
        var handle = "SomeHandle";
        var passwd = "Something";
        var sr = new UserSignup.UserSignupRequest(handle, passwd);
        var result = this.controller.signup(sr);
        assert result instanceof UserSignup.UserSignupResponseSuccess;
        assert ((UserSignup.UserSignupResponseSuccess) result).handle.equals(handle);
    }

    @Test
    public void testUserSignupTwice() throws ResponseException {
        testUserSignup();
        try {
            testUserSignup();
            fail("Duplicate user insertion should fail");
        } catch (ResponseException ex) {
            if (!(ex.getResponseError() instanceof UserSignup.UserSignupResponseInvalidHandleExists))
                fail("Expected UserSignup.UserSignupResponseInvalidHandleExists but got " + ex.getResponseError());
        } catch (Exception ex) {
            fail("Expected SignupResponseInvalidHandleExists but got " + ex);
        }
    }

    @Test
    public void testUserSignupNoHandle() {
        try {
            this.controller.signup(new UserSignup.UserSignupRequest(null, null));
            fail("User signup with no handle should fail");
        } catch (ResponseException ex) {
            if (!(ex.getResponseError() instanceof UserSignup.UserSignupResponseInvalidHandle))
                fail("Expected UserSignup.UserSignupResponseInvalidHandle but got " + ex.getResponseError());
        } catch (Exception ex) {
            fail("Excepted SignupResponseInvalidHandle but got " + ex);
        }
    }

    @Test
    public void testUserSignupEmptyHandle() {
        try {
            this.controller.signup(new UserSignup.UserSignupRequest("", null));
            fail("User signup with empty handle should fail");
        } catch (ResponseException ex) {
            if (!(ex.getResponseError() instanceof UserSignup.UserSignupResponseInvalidHandle))
                fail("Expected UserSignup.UserSignupResponseInvalidHandle but got " + ex.getResponseError());
        } catch (Exception ex) {
            fail("Excepted SignupResponseInvalidHandle but got " + ex);
        }
    }

    @Test
    public void testUserSignupVoidHandle() {
        try {
            this.controller.signup(new UserSignup.UserSignupRequest("    ", null));
            fail("User signup with empty handle should fail");
        } catch (ResponseException ex) {
            if (!(ex.getResponseError() instanceof UserSignup.UserSignupResponseInvalidHandle))
                fail("Expected UserSignup.UserSignupResponseInvalidHandle but got " + ex.getResponseError());
        } catch (Exception ex) {
            fail("Excepted SignupResponseInvalidHandle but got " + ex);
        }
    }

    @Test
    public void testUserSignupInvalidHandle() {
        try {
            this.controller.signup(new UserSignup.UserSignupRequest("A3$", null));
            fail("User signup with empty handle should fail");
        } catch (ResponseException ex) {
            if (!(ex.getResponseError() instanceof UserSignup.UserSignupResponseInvalidHandle))
                fail("Expected UserSignup.UserSignupResponseInvalidHandle but got " + ex.getResponseError());
        } catch (Exception ex) {
            fail("Excepted SignupResponseInvalidHandle but got " + ex);
        }
    }

    @Test
    public void testLoginLogout() throws IOException {
        String handle = "SomeHandleTestLoginLogout";
        String passwd = handle + "Passwd";
        this.controller.signup(new UserSignup.UserSignupRequest(handle, passwd));

        UserResponse siRes = this.controller.signin(new UserSignin.UserSigninRequest(handle, passwd));
        assert siRes instanceof UserSignin.UserSigninSuccess;
        UserSignin.UserSigninSuccess signinSuccess = (UserSignin.UserSigninSuccess) siRes;
        String token = signinSuccess.token;

        Socket socket = new Socket("localhost", BACKEND_CONN_ENDPOINT_PORT);
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), false);
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        socketOut.println(JSONObjectMapper.writeValueAsString(new UserLogin.UserLoginRequest(token, handle)));
        socketOut.flush();

        UserLogin.UserLoginResponse loginResponse = JSONObjectMapper.readValue(socketIn.readLine(), UserLogin.UserLoginResponse.class);
        assert loginResponse instanceof UserLogin.UserLoginSuccess;

        UserResponse signOutResponse = controller.signOut(new UserSignOut.UserSignOutRequest(handle, token));
        assert signOutResponse instanceof UserSignOut.UserSignOutSuccess;
    }

}
