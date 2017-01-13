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
import net.frju.androidquery.integration.models.User;
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.condition.Where;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class DeleteTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();
    }

    @Test
    public void testAllUsersAreDeleted() {
        int deletedRows = Q.User.delete().query();

        // verify
        User[] users = Q.User.select().query().toArray();

        // All of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 0 rows will be selected
        assertEquals(0, users.length);
        assertEquals(4, deletedRows);
    }

    @Test
    public void testSingleUserIsDeleted() {
        int deletedRows = Q.User.delete()
                .where(Where.field(Q.User.USERNAME).isEqualTo(SetupUser.ANGIE_USER_NAME))
                .query();

        // verify
        User[] users = Q.User.select().query().toArray();

        // 1 of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
        assertEquals(1, deletedRows);
    }

    @Test
    public void testUsersAreDeleted() {
        int deletedRows = Q.User.delete()
                .where(Where.field(Q.User.USERNAME).isIn(SetupUser.ANGIE_USER_NAME, SetupUser.CLYDE_USER_NAME, SetupUser.GILL_USER_NAME))
                .query();

        // verify
        User[] users = Q.User.select().query().toArray();

        // 3 of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
        assertEquals(3, deletedRows);
    }
}