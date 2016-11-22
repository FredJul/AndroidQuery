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

import android.content.ContentValues;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.User;
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.clause.Where;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;

import org.junit.Before;

import static net.frju.androidquery.operation.clause.Where.where;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class UpdateTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers(getSQLProvider());
        getSetupUser().setupFourTestUsers(getSQLProvider());
    }

    @org.junit.Test
    public void testSingleUpdate() {
        // setup
        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, timestamp);

        // exercise
        int updated = Update.getBuilder()
                .values(contentValues)
                .where(where(Q.User.USERNAME, Where.Exp.EQUAL_TO, SetupUser.CLYDE_USER_NAME))
                .query(User.class, getSQLProvider());

        // verify
        User user = Select.getBuilder()
                .where(where(Q.User.USERNAME, Where.Exp.EQUAL_TO, SetupUser.CLYDE_USER_NAME))
                .querySingle(User.class, getSQLProvider());

        assertEquals(true, user.getIsRegistered());
        assertEquals(timestamp, user.getTimestamp());

        assertEquals(updated, 1);
    }

    @org.junit.Test
    public void testBulkUpdate() {
        // setup
        long timestamp = System.currentTimeMillis();
        String newUsername = "CHANGED";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, timestamp);
        contentValues.put(Q.User.USERNAME, newUsername);

        // exercise
        int updated = Update.getBuilder()
                .values(contentValues)
                .query(User.class, getSQLProvider());

        // verify
        User[] users = Select.getBuilder()
                .query(User.class, getSQLProvider());

        for (User user : users) {
            assertEquals(timestamp, user.getTimestamp());
            assertEquals(true, user.getIsRegistered() );
            assertEquals(newUsername, user.getUsername());
        }

        assertEquals(updated, users.length);
    }

    @org.junit.Test
    public void testMoreThanUpdate() {
        // setup
        long newTimestamp = 0;
        String newUsername = "CHANGED";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, newTimestamp);
        contentValues.put(Q.User.USERNAME, newUsername);

        // exercise
        int updated = Update.getBuilder()
                .values(contentValues)
                .where(where(Q.User.TIMESTAMP, Where.Exp.MORE_THAN, SetupUser.CLYDE_TIMESTAMP))
                .query(User.class, getSQLProvider());

        // verify
        User[] users = Select.getBuilder()
                .where(where(Q.User.TIMESTAMP, Where.Exp.EQUAL_TO, newTimestamp))
                .query(User.class, getSQLProvider());

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        // with a timestamp of "0"
        assertEquals(3, users.length);

        assertEquals(updated, users.length);
    }
}