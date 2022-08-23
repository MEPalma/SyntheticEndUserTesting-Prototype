package common.api.user;

import common.api.serialization.JSONObjectMapper;
import common.api.twitteruser.UserResponse;
import common.api.twitteruser.putuser.UserSignup;
import org.junit.jupiter.api.Test;

public class TestUserMarshalling {

    @Test
    public void testTweetPublishRequest() throws Exception {
        UserSignup.UserSignupRequest request =
                new UserSignup.UserSignupRequest("somehandle", "somepasswd", "somebase64img");
        String jsonReq = JSONObjectMapper.JSONObjectMapper.writeValueAsString(request);
        UserSignup.UserSignupRequest rrequest = JSONObjectMapper.JSONObjectMapper.readValue(jsonReq, UserSignup.UserSignupRequest.class);
        assert rrequest.handle.equals(request.handle);
        assert rrequest.passwd.equals(request.passwd);
        assert rrequest.base64Img.equals(request.base64Img);
    }

    @Test
    public void testTweetPublishRequest2() throws Exception {
        UserSignup.UserSignupRequest request =
                new UserSignup.UserSignupRequest("somehandle", "somepasswd");
        String jsonReq = JSONObjectMapper.JSONObjectMapper.writeValueAsString(request);
        UserSignup.UserSignupRequest rrequest = JSONObjectMapper.JSONObjectMapper.readValue(jsonReq, UserSignup.UserSignupRequest.class);
        assert rrequest.handle.equals(request.handle);
        assert rrequest.passwd.equals(request.passwd);
        assert rrequest.base64Img == null;
    }

    @Test
    public void testUserSignupResponseSuccessGen() throws Exception {
        UserResponse err =
                new UserSignup.UserSignupResponseSuccess("somehandle");
        String jsonReq = JSONObjectMapper.JSONObjectMapper.writeValueAsString(err);
        UserResponse rrequest =
                JSONObjectMapper.JSONObjectMapper.readValue(jsonReq, UserResponse.class);
        assert rrequest instanceof UserSignup.UserSignupResponseSuccess;
    }

    @Test
    public void testUserSignupResponseInvalidHandleExists() throws Exception {
        UserSignup.UserSignupResponseInvalidHandleExists err =
                new UserSignup.UserSignupResponseInvalidHandleExists("somehandle");
        String jsonReq = JSONObjectMapper.JSONObjectMapper.writeValueAsString(err);
        UserSignup.UserSignupResponseInvalidHandleExists rrequest =
                JSONObjectMapper.JSONObjectMapper.readValue(jsonReq, UserSignup.UserSignupResponseInvalidHandleExists.class);
        assert rrequest.getType().equals("UserSignupResponseInvalidHandleExists");
        assert rrequest.getMessage().equals("Handle somehandle already exists. Choose another.");
    }

}
