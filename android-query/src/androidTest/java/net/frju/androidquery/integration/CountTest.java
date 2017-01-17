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
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.condition.Where;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class CountTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();
    }

    @Test
    public void testAllUsersAreCounted() {
        long count = Q.USER.count().query();
        assertEquals(4, count);
    }

    @Test
    public void testEqualToCount() {
        long count = Q.USER.count()
                .where(Where.field(Q.USER.TIMESTAMP).isEqualTo(SetupUser.CLYDE_TIMESTAMP))
                .query();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be counted
        assertEquals(1, count);
    }

    @Test
    public void testMoreThanCount() {
        long count = Q.USER.count()
                .where(Where.field(Q.USER.TIMESTAMP).isMoreThan(SetupUser.CLYDE_TIMESTAMP))
                .query();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be counted
        assertEquals(3, count);
    }
}