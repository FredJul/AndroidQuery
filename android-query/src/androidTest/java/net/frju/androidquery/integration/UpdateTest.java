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

import android.content.ContentValues;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.User;
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.condition.Where;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class UpdateTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();
    }

    @Test
    public void testSingleUpdate() {
        // setup
        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, timestamp);

        // exercise
        int updated = Q.User.update()
                .values(contentValues)
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME))
                .query();

        // verify
        User user = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME))
                .queryFirst();

        assertEquals(true, user.isRegistered);
        assertEquals(timestamp, user.timestamp);

        assertEquals(updated, 1);
    }

    @Test
    public void testListUpdate() {
        // setup
        long timestamp = System.currentTimeMillis();

        List<User> users = Q.User.select().query().toList();
        for (User user : users) {
            user.timestamp = timestamp;
        }

        // exercise
        int updated = Q.User.update()
                .model(users)
                .query();

        // verify
        users = Q.User.select().query().toList();
        assertEquals(4, users.size());

        for (User user : users) {
            assertEquals(timestamp, user.timestamp);
        }

        assertEquals(updated, 4);
    }

    @Test
    public void testBulkUpdate() {
        // setup
        long timestamp = System.currentTimeMillis();
        String newUsername = "CHANGED";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, timestamp);
        contentValues.put(Q.User.USERNAME, newUsername);

        // exercise
        int updated = Q.User.update()
                .values(contentValues)
                .query();

        // verify
        User[] users = Q.User.select()
                .query().toArray();

        for (User user : users) {
            assertEquals(timestamp, user.timestamp);
            assertEquals(true, user.isRegistered);
            assertEquals(newUsername, user.username);
        }

        assertEquals(updated, users.length);
    }

    @Test
    public void testMoreThanUpdate() {
        // setup
        long newTimestamp = 0;
        String newUsername = "CHANGED";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Q.User.IS_REGISTERED, true);
        contentValues.put(Q.User.TIMESTAMP, newTimestamp);
        contentValues.put(Q.User.USERNAME, newUsername);

        // exercise
        int updated = Q.User.update()
                .values(contentValues)
                .where(Where.field(Q.User.TIMESTAMP).isMoreThan(SetupUser.CLYDE_TIMESTAMP))
                .query();

        // verify
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).is(newTimestamp))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        // with a timestamp of "0"
        assertEquals(3, users.length);

        assertEquals(updated, users.length);
    }
}