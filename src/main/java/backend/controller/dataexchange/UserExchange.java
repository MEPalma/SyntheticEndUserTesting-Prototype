package backend.controller.dataexchange;

import backend.persistence.entity.TwitterUser;
import backend.utils.Hashing;
import com.sun.istack.NotNull;
import common.api.error.ResponseException;
import common.api.twitteruser.UserResponse;
import common.api.twitteruser.putuser.UserSignup;
import common.utils.DateUtils;

import javax.persistence.EntityManager;
import java.security.InvalidParameterException;
import java.util.List;

public class UserExchange {
    public static boolean isNotValidUserHandle(String handle) {
        return handle == null || handle.isEmpty() || !handle.chars().allMatch(Character::isLetterOrDigit);
    }

    public static UserResponse injectUser(EntityManager entityManager, @NotNull UserSignup.UserSignupRequest signupRequest)
            throws ResponseException {

        if (UserExchange.isNotValidUserHandle(signupRequest.handle))
            throw new UserSignup.UserSignupResponseInvalidHandle(signupRequest.handle).except();

        entityManager.getTransaction().begin();

        if (userExists(entityManager, signupRequest.handle))
            throw new UserSignup.UserSignupResponseInvalidHandleExists(signupRequest.handle).except();

        Hashing.HashResult hashResult;
        try {
            hashResult = Hashing.hashPasswd(signupRequest.passwd);
        } catch (InvalidParameterException pe) {
            throw new UserSignup.UserSignupResponseInvalidPassword().except();
        }

        try {
            TwitterUser twitterUser = new TwitterUser();
            twitterUser.setHandle(signupRequest.handle);
            twitterUser.setPasswdHash(hashResult.hash);
            twitterUser.setPasswdSalt(hashResult.salt);
            twitterUser.setEpochCreated(DateUtils.getEpochDateNow());
            twitterUser.setBase64Img(signupRequest.base64Img);
            //
            entityManager.persist(twitterUser);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            throw new UserSignup.UserSignupResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }

        return new UserSignup.UserSignupResponseSuccess(signupRequest.handle);
    }

    public static boolean userExists(EntityManager entityManager, String handle) {
        var maybeTUser = getTwitterUser(entityManager, handle);
        if (maybeTUser != null)
            entityManager.detach(maybeTUser);
        return maybeTUser != null;
    }

    public static List<TwitterUser> getAllTwitterUser(EntityManager entityManager) {
        return entityManager.createQuery("SELECT u from TwitterUser u").getResultList();
    }

    public static TwitterUser getTwitterUser(EntityManager entityManager, String handle) {
        return entityManager.find(TwitterUser.class, handle);
    }
}
