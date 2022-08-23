package backend.persistence.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Follow {
    @EmbeddedId
    private FollowKey followKey;

    public Follow() {

    }

    public Follow(FollowKey followKey) {
        this.followKey = followKey;
    }

    public FollowKey getFollowKey() {
        return followKey;
    }

    public void setFollowKey(FollowKey followKey) {
        this.followKey = followKey;
    }

    public static Query sqlGetFollowersOfHandle(EntityManager entityManager, String handle) {
        final String template = """
                    SELECT f.followKey.followingTwitterUser.handle
                    FROM Follow f
                    WHERE f.followKey.followedTwitterUser.handle=:handle
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        return query;
    }

    public static Query sqlGetFollowingOfHandle(EntityManager entityManager, String handle) {
        final String template = """
                    SELECT f.followKey.followedTwitterUser.handle
                    FROM Follow f
                    WHERE f.followKey.followingTwitterUser.handle=:handle
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        return query;
    }

    public static Query sqlGetFollowersCount(EntityManager entityManager, String handle) {
        final String template = """
                    SELECT COUNT(f)
                    FROM Follow f
                    WHERE f.followKey.followedTwitterUser.handle=:handle
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        return query;
    }

    public static Query sqlGetFollowingCount(EntityManager entityManager, String handle) {
        final String template = """
                    SELECT COUNT(f)
                    FROM Follow f
                    WHERE f.followKey.followingTwitterUser.handle=:handle
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        return query;
    }

}
