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
import net.frju.androidquery.integration.models.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class ModelInitTest extends IntegrationTest {

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
    public void testUsersInit() {
        User[] users = Q.USER.select().queryAndInit();

        assertEquals(4, users.length);
        assertEquals(users[0].posts.length, 2);
    }
}
