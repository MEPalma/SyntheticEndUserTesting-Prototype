package backend.persistence.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tweet {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private long epochCreated;

    @OneToOne
    private TwitterUser author;

    @OneToOne
    private Tweet retweetOf;

    @Column(columnDefinition = "TEXT", length = 20_000)
    private String text;

    @Column(columnDefinition = "TEXT", length = 20_000)
    private String base64Img;

    public Tweet() {
    }

    public Tweet(long epochCreated, TwitterUser author, Tweet retweetOf, String text, String base64Img) {
        this.epochCreated = epochCreated;
        this.author = author;
        this.retweetOf = retweetOf;
        this.text = text;
        this.base64Img = base64Img;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return id.equals(tweet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TwitterUser getAuthor() {
        return author;
    }

    public void setAuthor(TwitterUser author) {
        this.author = author;
    }

    public long getEpochCreated() {
        return epochCreated;
    }

    public void setEpochCreated(long epochCreated) {
        this.epochCreated = epochCreated;
    }

    public Tweet getRetweetOf() {
        return retweetOf;
    }

    public void setRetweetOf(Tweet retweetOf) {
        this.retweetOf = retweetOf;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBase64Img() {
        return base64Img;
    }

    public void setBase64Img(String base64Img) {
        this.base64Img = base64Img;
    }

    public static Query sqlGetTweetsOfUser(EntityManager entityManager, String handle, long minEpochCreated) {
        final String template = """
                    SELECT t
                    FROM Tweet t
                    WHERE t.author.handle=:handle AND t.epochCreated > :minEpochCreated
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        query.setParameter("minEpochCreated", minEpochCreated);
        return query;
    }

    public static Query sqlSampleRetweetsToUser(EntityManager entityManager, String handle, long minEpochCreated) {
        final String template = """
                    SELECT t
                    FROM Tweet t
                    WHERE t.retweetOf.author.handle=:handle AND t.epochCreated > :minEpochCreated
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        query.setParameter("minEpochCreated", minEpochCreated);
        return query;
    }

    public static Query sqlSampleTweetsOfFollowing(EntityManager entityManager, String handle, long minEpochCreated) {
        // TODO refactor to select first, then join!
        final String template = """
                    SELECT t
                    FROM Tweet AS t INNER JOIN Follow AS f ON t.author.handle = f.followKey.followedTwitterUser.handle
                    WHERE f.followKey.followingTwitterUser.handle=:handle AND t.epochCreated > :minEpochCreated
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("handle", handle);
        query.setParameter("minEpochCreated", minEpochCreated);
        return query;
    }

}
