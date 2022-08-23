package backend.controller.dataexchange;

import backend.persistence.entity.FollowKey;
import backend.persistence.entity.TwitterUser;
import common.api.error.ResponseException;
import common.api.follow.FollowResponse;
import common.api.follow.putfollow.FollowPut;
import common.api.follow.postfollow.FollowGet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class FollowExchange {

    private static FollowKey getFollowKey(
            EntityManager entityManager,
            String followingHandle,
            String followedHandle
    ) throws ResponseException {
        TwitterUser followingUser = UserExchange.getTwitterUser(entityManager, followingHandle);
        if (followingUser == null)
            throw new ResponseException(new FollowPut.FollowResponseNoSuchFollowing(followingHandle));

        TwitterUser followedUser = UserExchange.getTwitterUser(entityManager, followedHandle);
        if (followedUser == null)
            throw new FollowPut.FollowResponseNoSuchFollowed(followedHandle).except();

        return new FollowKey(followingUser, followedUser);
    }

    private static boolean existsFollow(EntityManager entityManager, FollowKey followKey) {
        var maybeFollow = entityManager.find(backend.persistence.entity.Follow.class, followKey);
        boolean exists = maybeFollow != null;
        if (exists)
            entityManager.detach(maybeFollow);
        return exists;
    }

    public static FollowResponse injectFollow(
            EntityManager entityManager,
            FollowPut.FollowAddRequest followRequest
    ) throws ResponseException {
        if (followRequest.followingHandler.equals(followRequest.followedHandler))
            throw new FollowPut.FollowResponseFailed("Invalid request").except();
        FollowKey followKey = FollowExchange.getFollowKey(
                entityManager,
                followRequest.followingHandler,
                followRequest.followedHandler
        );
        backend.persistence.entity.Follow newFollow = new backend.persistence.entity.Follow();
        newFollow.setFollowKey(followKey);
        try {
            entityManager.getTransaction().begin();
            if (!existsFollow(entityManager, followKey)) {
                entityManager.persist(newFollow);
                entityManager.getTransaction().commit();
            } else
                entityManager.getTransaction().rollback();
        } catch (Exception ex) {
            throw new FollowPut.FollowResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }
        return new FollowPut.FollowResponseSuccess();
    }

    public static FollowResponse removeFollow(
            EntityManager entityManager,
            FollowPut.FollowRemoveRequest followRequest
    ) throws ResponseException {
        if (followRequest.followingHandler.equals(followRequest.followedHandler))
            throw new FollowPut.FollowResponseFailed("Invalid request").except();
        FollowKey followKey = FollowExchange.getFollowKey(
                entityManager,
                followRequest.followingHandler,
                followRequest.followedHandler
        );
        backend.persistence.entity.Follow follow = new backend.persistence.entity.Follow();
        follow.setFollowKey(followKey);
        try {
            entityManager.getTransaction().begin();
            if (existsFollow(entityManager, followKey)) {
                entityManager.remove(follow);
                entityManager.getTransaction().commit();
            } else
                entityManager.getTransaction().rollback();
        } catch (Exception ex) {
            throw new FollowPut.FollowResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }
        return new FollowPut.FollowResponseSuccess();
    }

    public static FollowResponse getFollowing(
            EntityManager entityManager,
            FollowGet.FollowGetFollowingRequest followGetFollowingRequest
    ) throws ResponseException {
        try {
            entityManager.getTransaction().begin();
            entityManager.getTransaction().setRollbackOnly();
            Query query = backend.persistence.entity.Follow.sqlGetFollowingOfHandle(
                    entityManager,
                    followGetFollowingRequest.handle
            );
            List results = query.getResultList();
            entityManager.getTransaction().rollback();
            entityManager.close();
            return new FollowGet.FollowGetResponseHandles(results);
        } catch (PersistenceException pe) {
            String failure = "Could not retrieve following of " + followGetFollowingRequest.handle;
            throw new FollowGet.FollowGetResponseFailed(failure).except();
        } catch (Exception ex) {
            throw new FollowGet.FollowGetResponseFailed(ex.getMessage()).except();
        }
    }

    public static FollowResponse getFollowers(
            EntityManager entityManager,
            FollowGet.FollowGetFollowersRequest followGetFollowersRequest
    ) throws ResponseException {
        try {
            entityManager.getTransaction().begin();
            entityManager.getTransaction().setRollbackOnly();
            Query query = backend.persistence.entity.Follow.sqlGetFollowersOfHandle(
                    entityManager,
                    followGetFollowersRequest.handle
            );
            List results = query.getResultList();
            entityManager.getTransaction().rollback();
            entityManager.close();
            return new FollowGet.FollowGetResponseHandles(results);
        } catch (PersistenceException pe) {
            String failure = "Could not retrieve followers of " + followGetFollowersRequest.handle;
            throw new FollowGet.FollowGetResponseFailed(failure).except();
        } catch (Exception ex) {
            throw new FollowGet.FollowGetResponseFailed(ex.getMessage()).except();
        }
    }

    public static FollowResponse getFollowCount(
            EntityManager entityManager,
            FollowGet.FollowGetFollowersRequest followGetFollowersRequest
    ) throws ResponseException {
        try {
            entityManager.getTransaction().begin();
            entityManager.getTransaction().setRollbackOnly();
            long followersCount = (long) backend.persistence.entity.Follow.sqlGetFollowersCount(
                    entityManager,
                    followGetFollowersRequest.handle
            ).getSingleResult();
            long followingCount = (long) backend.persistence.entity.Follow.sqlGetFollowingCount(
                    entityManager,
                    followGetFollowersRequest.handle
            ).getSingleResult();
            entityManager.getTransaction().rollback();
            entityManager.close();
            return new FollowGet.FollowGetResponseCount(followingCount, followersCount);
        } catch (PersistenceException pe) {
            String failure = "Could not retrieve followers of " + followGetFollowersRequest.handle;
            throw new FollowGet.FollowGetResponseFailed(failure).except();
        } catch (Exception ex) {
            throw new FollowGet.FollowGetResponseFailed(ex.getMessage()).except();
        }
    }


}
