package backend.httpproxy.follow;

import backend.Backend;
import backend.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.fail;


public class TestFollowPut {

    public TestFollowPut() throws IOException {
        new Backend();
    }

    @Test
    public void testFollows() throws Exception {
        var h1 = "handle1";
        var h1Tok = TestUtils.registerAndLogin(h1, h1);

        var h2 = "handle2";
        var h2Tok = TestUtils.registerAndLogin(h2, h2);

        var h3 = "handle3";
        var h3Tok = TestUtils.registerAndLogin(h3, h3);

        var fs = TestUtils.getFollowers(h1Tok, h1);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h1);
        assert fs.size() == 0;

        TestUtils.followAdd(h1Tok, h1, h2);

        fs = TestUtils.getFollowers(h1Tok, h1);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h1);
        assert fs.size() == 1;
        assert fs.get(0).equals(h2);

        fs = TestUtils.getFollowers(h1Tok, h2);
        assert fs.size() == 1;
        assert fs.get(0).equals(h1);
        //
        fs = TestUtils.getFollowing(h1Tok, h2);
        assert fs.size() == 0;

        TestUtils.followAdd(h1Tok, h1, h3);

        fs = TestUtils.getFollowers(h1Tok, h1);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h1);
        assert fs.size() == 2;
        assert fs.get(0).equals(h2);
        assert fs.get(1).equals(h3);

        fs = TestUtils.getFollowers(h1Tok, h3);
        assert fs.size() == 1;
        assert fs.get(0).equals(h1);
        //
        fs = TestUtils.getFollowing(h1Tok, h3);
        assert fs.size() == 0;

        TestUtils.followRemove(h1Tok, h1, h2);

        fs = TestUtils.getFollowers(h1Tok, h1);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h1);
        assert fs.size() == 1;
        assert fs.get(0).equals(h3);

        fs = TestUtils.getFollowers(h1Tok, h2);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h2);
        assert fs.size() == 0;

        TestUtils.followRemove(h1Tok, h1, h3);

        fs = TestUtils.getFollowers(h1Tok, h1);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h1);
        assert fs.size() == 0;

        fs = TestUtils.getFollowers(h1Tok, h3);
        assert fs.size() == 0;
        //
        fs = TestUtils.getFollowing(h1Tok, h3);
        assert fs.size() == 0;
    }

    @Test
    public void testFollowCounts() throws Exception {
        var h1 = "handle1";
        var h1Tok = TestUtils.registerAndLogin(h1, h1);

        var h2 = "handle2";
        var h2Tok = TestUtils.registerAndLogin(h2, h2);

        var h3 = "handle3";
        var h3Tok = TestUtils.registerAndLogin(h3, h3);

        var counts = TestUtils.getFollowCount(h1Tok, h1);
        assert counts.followers == 0;
        assert counts.following == 0;

        TestUtils.followAdd(h1Tok, h1, h2);

        counts = TestUtils.getFollowCount(h1Tok, h1);
        assert counts.followers == 0;
        assert counts.following == 1;

        counts = TestUtils.getFollowCount(h1Tok, h2);
        assert counts.followers == 1;
        assert counts.following == 0;

        TestUtils.followAdd(h1Tok, h1, h3);

        counts = TestUtils.getFollowCount(h1Tok, h1);
        assert counts.followers == 0;
        assert counts.following == 2;

        counts = TestUtils.getFollowCount(h1Tok, h3);
        assert counts.followers == 1;
        assert counts.following == 0;

        TestUtils.followRemove(h1Tok, h1, h2);

        counts = TestUtils.getFollowCount(h1Tok, h1);
        assert counts.followers == 0;
        assert counts.following == 1;

        counts = TestUtils.getFollowCount(h1Tok, h2);
        assert counts.followers == 0;
        assert counts.following == 0;

        TestUtils.followRemove(h1Tok, h1, h3);

        counts = TestUtils.getFollowCount(h1Tok, h1);
        assert counts.followers == 0;
        assert counts.following == 0;

        counts = TestUtils.getFollowCount(h1Tok, h2);
        assert counts.followers == 0;
        assert counts.following == 0;

        counts = TestUtils.getFollowCount(h1Tok, h3);
        assert counts.followers == 0;
        assert counts.following == 0;
    }

    @Test
    public void testFollowAuthDirection() throws Exception {
        var h1 = "handle1";
        var h1Tok = TestUtils.registerAndLogin(h1, h1);

        var h2 = "handle2";
        var h2Tok = TestUtils.registerAndLogin(h2, h2);

        try {
            TestUtils.followAdd(h2Tok, h1, h2);
        } catch (Exception re) {
            // Success.
            return;
        }

        fail();
    }
}
