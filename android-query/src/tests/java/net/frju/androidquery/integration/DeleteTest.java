/**
 * Copyright 2013-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.integration;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.User;
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.clause.Where;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Select;

import org.junit.Before;

import static net.frju.androidquery.operation.clause.In.in;
import static net.frju.androidquery.operation.clause.Where.where;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class DeleteTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers(getSQLProvider());
        getSetupUser().setupFourTestUsers(getSQLProvider());
    }

    @org.junit.Test
    public void testAllUsersAreDeleted() {
        int deletedRows = Delete.getBuilder().query(User.class, getSQLProvider());

        // verify
        User[] users = Select.getBuilder().query(User.class, getSQLProvider());

        // All of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 0 rows will be selected
        assertEquals(0, users.length);
        assertEquals(4, deletedRows);
    }

    @org.junit.Test
    public void testSingleUserIsDeleted() {
        int deletedRows = Delete.getBuilder()
                .where(where(Q.User.USERNAME, Where.Op.EQUAL_TO, SetupUser.ANGIE_USER_NAME))
                .query(User.class, getSQLProvider());

        // verify
        User[] users = Select.getBuilder().query(User.class, getSQLProvider());

        // 1 of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
        assertEquals(1, deletedRows);
    }

    @org.junit.Test
    public void testUsersAreDeleted() {
        int deletedRows = Delete.getBuilder()
            .where(in(Q.User.USERNAME, SetupUser.ANGIE_USER_NAME, SetupUser.CLYDE_USER_NAME, SetupUser.GILL_USER_NAME))
                .query(User.class, getSQLProvider());

        // verify
        User[] users = Select.getBuilder().query(User.class, getSQLProvider());

        // 3 of the 4 users created by #setupFourTestUsers will be deleted by the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
        assertEquals(3, deletedRows);
    }
}