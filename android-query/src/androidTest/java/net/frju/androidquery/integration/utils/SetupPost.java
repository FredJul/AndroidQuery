package net.frju.androidquery.integration.utils;

import net.frju.androidquery.gen.POST;
import net.frju.androidquery.integration.models.Post;

public class SetupPost {

    public static final int POST_1_ID = 1;
    public static final String POST_1_TITLE = "Hello post";
    public static final String POST_1_BODY = "This was a really good post";
    public static final long POST_1_TIMESTAMP = System.currentTimeMillis();
    public static final int POST_1_USER_ID = SetupUser.ANGIE_ID;

    public static final int POST_2_ID = 2;
    public static final String POST_2_TITLE = "Another post";
    public static final String POST_2_BODY = "A second post, but not as good as the first...";
    public static final long POST_2_TIMESTAMP = System.currentTimeMillis();
    public static final int POST_2_USER_ID = SetupUser.ANGIE_ID;

    public static final int POST_3_ID = 3;
    public static final String POST_3_TITLE = "Post belonging to Clyde";
    public static final String POST_3_BODY = "This isEqualTo a post belonging to Clyde";
    public static final long POST_3_TIMESTAMP = System.currentTimeMillis();
    public static final int POST_3_USER_ID = SetupUser.CLYDE_ID;

    public void tearDownTestPosts() {
        POST.delete().query();
    }

    public void setupTestPosts() {
        Post[] posts = {
                createPost(
                        POST_1_ID,
                        POST_1_TITLE,
                        POST_1_BODY,
                        POST_1_TIMESTAMP,
                        POST_1_USER_ID
                ),
                createPost(
                        POST_2_ID,
                        POST_2_TITLE,
                        POST_2_BODY,
                        POST_2_TIMESTAMP,
                        POST_2_USER_ID
                ),
                createPost(
                        POST_3_ID,
                        POST_3_TITLE,
                        POST_3_BODY,
                        POST_3_TIMESTAMP,
                        POST_3_USER_ID
                )
        };

        POST.insert(posts).query();
    }

    public static Post createPost(int id, String title,
                                  String body, long timestamp, int userId) {

        Post post = new Post();
        post.id = id;
        post.title = title;
        post.body = body;
        post.timestamp = timestamp;
        post.userId = userId;
        return post;
    }
}
