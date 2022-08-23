package backend.persistence.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TwitterUser {
    @Id
    @Column(nullable = false)
    private String handle;

    @Column(nullable = false)
    private int epochCreated;

    @Column(nullable = false)
    private String passwdHash;

    @Column(nullable = false)
    private String passwdSalt;

    @Column(columnDefinition = "TEXT", length = 20_000)
    private String base64Img;

    public TwitterUser() {

    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public int getEpochCreated() {
        return epochCreated;
    }

    public void setEpochCreated(int epochCreated) {
        this.epochCreated = epochCreated;
    }

    public String getPasswdHash() {
        return passwdHash;
    }

    public void setPasswdHash(String passwdHash) {
        this.passwdHash = passwdHash;
    }

    public String getPasswdSalt() {
        return passwdSalt;
    }

    public void setPasswdSalt(String passwdSalt) {
        this.passwdSalt = passwdSalt;
    }

    public String getBase64Img() {
        return base64Img;
    }

    public void setBase64Img(String base64Img) {
        this.base64Img = base64Img;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitterUser user = (TwitterUser) o;

        return handle.equals(user.handle);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }
}
