/**
 * Copyright 2013-present memtrip LTD.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.integration;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.Log;
import net.frju.androidquery.integration.models.Post;
import net.frju.androidquery.integration.models.User;
import net.frju.androidquery.integration.utils.SetupLog;
import net.frju.androidquery.integration.utils.SetupPost;
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.keyword.OrderBy;

import org.junit.Before;
import org.junit.Test;

import static net.frju.androidquery.operation.condition.On.on;
import static net.frju.androidquery.operation.condition.Where.where;
import static net.frju.androidquery.operation.join.InnerJoin.innerJoin;
import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class JoinTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();

        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();

        getSetupPost().tearDownTestPosts();
        getSetupPost().setupTestPosts();

        getSetupLog().tearDownTestLogs();
        getSetupLog().setupTestLogs();
    }

    @Test
    public void testInnerJoin() {
        User[] users = Q.User.select()
                .join(innerJoin(Log.class, on("User.logId", "Log.id")))
                .query().toArray();


        assertEquals(1, users.length);
        assertEquals(SetupLog.LOG_1_ID, users[0].getLog().getId());
        assertEquals(SetupLog.LOG_1_TIMESTAMP, users[0].getLog().getTimestamp());
    }

    @Test
    public void testNestedInnerJoin() {
        Post[] posts = Q.Post.select()
                .join(
                        innerJoin(
                                User.class,
                                innerJoin(
                                        Log.class,
                                        on("User.logId", "Log.id")
                                ),
                                on("Post.userId", "User.id")
                        )
                )
                .query().toArray();


        assertEquals(2, posts.length);
        assertEquals(SetupUser.ANGIE_USER_NAME, posts[0].getUser().getUsername());
        assertEquals(SetupUser.ANGIE_LOG_ID, posts[0].getUser().getLog().getId());
    }

    @Test
    public void testJoinWithOrderBy() {
        Post[] posts = Q.Post.select()
                .join(
                        innerJoin(
                                User.class,
                                on("Post.userId", "User.id")
                        )
                )
                .orderBy("Post.id", OrderBy.Order.DESC)
                .query().toArray();

        assertEquals(3, posts.length);
        assertEquals(SetupPost.POST_3_ID, posts[0].getId());
        assertEquals(SetupPost.POST_2_ID, posts[1].getId());
        assertEquals(SetupPost.POST_1_ID, posts[2].getId());
    }

    @Test
    public void testJoinWithLimit() {
        Post[] posts = Q.Post.select()
                .join(
                        innerJoin(
                                User.class,
                                on("Post.userId", "User.id")
                        )
                )
                .limit(0, 2)
                .query().toArray();

        assertEquals(2, posts.length);
        assertEquals(SetupPost.POST_1_ID, posts[0].getId());
        assertEquals(SetupPost.POST_2_ID, posts[1].getId());
    }

    @Test
    public void testNaturalInnerJoin() {
        // TODO
    }

    @Test
    public void testCrossInnerJoin() {
        // TODO
    }

    @Test
    public void testLeftOuterJoin() {
        // TODO
    }

    @Test
    public void testNaturalLeftOuterJoin() {
        // TODO
    }

    @Test
    public void testJoinWithCondition() {
        User[] users = Q.User.select()
                .join(innerJoin(Log.class, on("User.logId", "Log.id")))
                .where(where(Q.User.USERNAME, Where.Op.IS, SetupUser.ANGIE_USER_NAME))
                .query().toArray();


        assertEquals(1, users.length);
        assertEquals(SetupLog.LOG_1_ID, users[0].getLog().getId());
        assertEquals(SetupLog.LOG_1_TIMESTAMP, users[0].getLog().getTimestamp());
    }
}
