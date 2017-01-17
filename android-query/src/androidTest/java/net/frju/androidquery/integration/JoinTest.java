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
 * distributed under the License isEqualTo distributed on an "AS IS" BASIS,
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

import static net.frju.androidquery.operation.join.Join.innerJoin;
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
        User[] users = Q.USER.select()
                .join(innerJoin(User.class, Q.USER.LOG_ID, Log.class, Q.LOG.ID))
                .query().toArray();


        assertEquals(1, users.length);
        assertEquals(SetupLog.LOG_1_ID, users[0].log.id);
        assertEquals(SetupLog.LOG_1_TIMESTAMP, users[0].log.timestamp);
    }

    @Test
    public void testNestedInnerJoin() {
        Post[] posts = Q.POST.select()
                .join(
                        innerJoin(
                                Post.class,
                                Q.POST.USER_ID,
                                User.class,
                                Q.USER.ID
                        ),
                        innerJoin(
                                User.class,
                                Q.USER.LOG_ID,
                                Log.class,
                                Q.LOG.ID
                        )
                )
                .query().toArray();


        assertEquals(2, posts.length);
        assertEquals(SetupUser.ANGIE_USER_NAME, posts[0].user.username);
        assertEquals(SetupUser.ANGIE_LOG_ID, posts[0].user.log.id);
    }

    @Test
    public void testJoinWithOrderBy() {
        Post[] posts = Q.POST.select()
                .join(
                        innerJoin(
                                Post.class,
                                Q.POST.USER_ID,
                                User.class,
                                Q.USER.ID
                        )
                )
                .orderBy("Post.id", OrderBy.Order.DESC)
                .query().toArray();

        assertEquals(3, posts.length);
        assertEquals(SetupPost.POST_3_ID, posts[0].id);
        assertEquals(SetupPost.POST_2_ID, posts[1].id);
        assertEquals(SetupPost.POST_1_ID, posts[2].id);
    }

    @Test
    public void testJoinWithLimit() {
        Post[] posts = Q.POST.select()
                .join(
                        innerJoin(
                                Post.class,
                                Q.POST.USER_ID,
                                User.class,
                                Q.USER.ID
                        )
                )
                .limit(0, 2)
                .query().toArray();

        assertEquals(2, posts.length);
        assertEquals(SetupPost.POST_1_ID, posts[0].id);
        assertEquals(SetupPost.POST_2_ID, posts[1].id);
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
        User[] users = Q.USER.select()
                .join(innerJoin(
                        User.class,
                        Q.USER.LOG_ID,
                        Log.class,
                        Q.LOG.ID
                ))
                .where(Where.field(Q.USER.USERNAME).isEqualTo(SetupUser.ANGIE_USER_NAME))
                .query().toArray();


        assertEquals(1, users.length);
        assertEquals(SetupLog.LOG_1_ID, users[0].log.id);
        assertEquals(SetupLog.LOG_1_TIMESTAMP, users[0].log.timestamp);
    }
}
