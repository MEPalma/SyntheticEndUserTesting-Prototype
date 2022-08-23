package backend.persistence.db;

import backend.persistence.entity.*;
import backend.persistence.entity.alert.Alert;
import backend.persistence.entity.alert.FollowAlert;
import backend.persistence.entity.alert.LikeAlert;
import backend.persistence.entity.alert.RetweetAlert;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public final class Database {
    public static final Properties H_PROPS;
    static {
        H_PROPS = new Properties();
        //
        H_PROPS.setProperty(Environment.DRIVER, "org.postgresql.Driver");
        H_PROPS.setProperty(Environment.URL, "jdbc:postgresql://localhost:5432/SET_BaseSwingExample");
        H_PROPS.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");
        //
        H_PROPS.setProperty(Environment.USER, "SVFuwf12r"); // TODO use env value
        H_PROPS.setProperty(Environment.PASS, "cYE3Q4hzFC"); // TODO use env value
        //
        H_PROPS.setProperty(Environment.SHOW_SQL, "false");
        H_PROPS.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
        H_PROPS.setProperty(Environment.AUTOCOMMIT, "true");
    }

    public static final Configuration H_CONFIG;
    static {
        H_CONFIG = new Configuration();
        H_CONFIG.setProperties(H_PROPS);
    }

    public static final SessionFactory H_SESSION_FACTORY;
    static {
        H_CONFIG.addAnnotatedClass(TwitterUser.class);
        H_CONFIG.addAnnotatedClass(Tweet.class);
        H_CONFIG.addAnnotatedClass(FollowKey.class);
        H_CONFIG.addAnnotatedClass(Follow.class);
        H_CONFIG.addAnnotatedClass(TweetLikeKey.class);
        H_CONFIG.addAnnotatedClass(TweetLike.class);
        H_CONFIG.addAnnotatedClass(Alert.class);
        H_CONFIG.addAnnotatedClass(LikeAlert.class);
        H_CONFIG.addAnnotatedClass(FollowAlert.class);
        H_CONFIG.addAnnotatedClass(RetweetAlert.class);
        H_SESSION_FACTORY = H_CONFIG.buildSessionFactory();
    }
}
