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
import static org.junit.Assert.assertTrue;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class CreateTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
    }

    @Test
    public void testSingleInsert() {
        // setup
        String USER_ID = "1234567890";
        long USER_TIMESTAMP = System.currentTimeMillis();
        boolean USER_IS_REGISTERED = true;

        User user = new User();
        user.username = USER_ID;
        user.isRegistered = USER_IS_REGISTERED;
        user.timestamp = USER_TIMESTAMP;

        // exercise
        Q.User.insert(user).query();

        // verify
        User responseUser = Q.User.select().queryFirst();

        assertTrue(user.username.equals(responseUser.username));
        assertTrue(user.timestamp == responseUser.timestamp);
        assertTrue(user.isRegistered == responseUser.isRegistered);
    }

    @Test
    public void testMultipleInsert() {
        // setup
        int ANGIE_ID = 1;
        String ANGIE_USERNAME = "angie";
        long ANGIE_TIMESTAMP = System.currentTimeMillis();
        boolean ANGIE_IS_REGISTERED = true;
        double ANGIE_RATING = 2.7;
        int ANGIE_COUNT = 1028;

        int SAM_ID = 2;
        String SAM_USERNAME = "sam";
        long SAM_TIMESTAMP = System.currentTimeMillis() + 1000;
        boolean SAM_IS_REGISTERED = false;
        double SAM_RATING = 2.7;
        int SAM_COUNT = 10024;

        User[] users = new User[]{
                SetupUser.createUser(
                        ANGIE_ID,
                        ANGIE_USERNAME,
                        ANGIE_TIMESTAMP,
                        ANGIE_IS_REGISTERED,
                        ANGIE_RATING,
                        ANGIE_COUNT,
                        0
                ),

                SetupUser.createUser(
                        SAM_ID,
                        SAM_USERNAME,
                        SAM_TIMESTAMP,
                        SAM_IS_REGISTERED,
                        SAM_RATING,
                        SAM_COUNT,
                        0
                ),
        };

        // exercise
        Q.User.insert(users).query();

        // verify
        User angieUser = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isEqualTo(ANGIE_USERNAME))
                .queryFirst();

        User samUser = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isEqualTo(SAM_USERNAME))
                .queryFirst();

        assertEquals(ANGIE_USERNAME, angieUser.username);
        assertEquals(ANGIE_TIMESTAMP, angieUser.timestamp);
        assertEquals(ANGIE_IS_REGISTERED, angieUser.isRegistered);
        assertEquals(ANGIE_RATING, angieUser.rating, 0.1f);
        assertEquals(ANGIE_COUNT, angieUser.count);

        assertEquals(SAM_USERNAME, samUser.username);
        assertEquals(SAM_TIMESTAMP, samUser.timestamp);
        assertEquals(SAM_IS_REGISTERED, samUser.isRegistered);
        assertEquals(SAM_RATING, samUser.rating, 0.1f);
        assertEquals(SAM_COUNT, samUser.count);
    }

    @Test
    public void testMoreThan500RowInsert() {
        int COLUMN_COUNT = 1350;
        int ANGIE_ID = 1000;
        String ANGIE_USERNAME = "angie";
        long ANGIE_TIMESTAMP = System.currentTimeMillis();
        boolean ANGIE_IS_REGISTERED = true;
        double ANGIE_RATING = 1.0;
        int ANGIE_COUNT = 300;

        User[] users = new User[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; i++) {
            users[i] = SetupUser.createUser(
                    ANGIE_ID++,
                    ANGIE_USERNAME,
                    ANGIE_TIMESTAMP + i,
                    ANGIE_IS_REGISTERED,
                    ANGIE_RATING,
                    ANGIE_COUNT,
                    0
            );
        }

        Q.User.insert(users).query();

        User[] usersInserted = Q.User.select().query().toArray();

        for (int i = 0; i < usersInserted.length; i++) {
            assertEquals(ANGIE_TIMESTAMP + i, usersInserted[i].timestamp);
        }

        assertEquals(COLUMN_COUNT, usersInserted.length);
    }
}