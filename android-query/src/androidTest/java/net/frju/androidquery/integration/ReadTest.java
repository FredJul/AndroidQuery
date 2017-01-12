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
import net.frju.androidquery.integration.utils.SetupUser;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.keyword.OrderBy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class ReadTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();
    }

    @Test
    public void testAllUsersAreSelected() {
        User[] users = Q.User.select().query().toArray();

        // 4 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 4 rows will be selected
        assertEquals(4, users.length);
    }

    @Test
    public void testEqualToSingleSelection() {
        User user = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME))
                .queryFirst();

        assertEquals(SetupUser.CLYDE_USER_NAME, user.username);
    }

    @Test
    public void testUsernameIsNullSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(null))
                .query().toArray();

        assertEquals(0, users.length);
    }

    @Test
    public void testIsNullSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.NULL_FIELD).is(null))
                .query().toArray();

        assertEquals(4, users.length);
    }

    @Test
    public void testEqualToBooleanSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.IS_REGISTERED).is(true))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testEqualToLongSelection() {
        User user = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).is(SetupUser.CLYDE_TIMESTAMP))
                .queryFirst();

        assertEquals(SetupUser.CLYDE_USER_NAME, user.username);
        assertEquals(SetupUser.CLYDE_TIMESTAMP, user.timestamp);
        assertEquals(SetupUser.CLYDE_IS_REGISTERED, user.isRegistered);
    }

    @Test
    public void testMoreThanSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).isMoreThan(SetupUser.CLYDE_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testMoreThanOrEqualToSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).isMoreThanOrEqualTo(SetupUser.CLYDE_TIMESTAMP))
                .query().toArray();

        // All 4 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 4 rows will be selected
        assertEquals(4, users.length);
    }

    @Test
    public void testLessThanSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).isLessThan(SetupUser.ANGIE_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testLessThanOrEqualToSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).isLessThanOrEqualTo(SetupUser.ANGIE_TIMESTAMP))
                .query().toArray();

        // 4 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 4 rows will be selected
        assertEquals(4, users.length);
    }

    @Test
    public void testLikeStartingWithSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isLike("jo%"))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testLikeEndingWithSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isLike("%e"))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testLikeContainingSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isLike("%lyd%"))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testBetweenSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.RATING).isNotBetween(10, 50))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testInStringSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).isIn(SetupUser.CLYDE_USER_NAME, SetupUser.ANGIE_USER_NAME))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testInLongSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.TIMESTAMP).isIn(SetupUser.CLYDE_TIMESTAMP, SetupUser.ANGIE_TIMESTAMP, SetupUser.GILL_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testOrWhereInQueryIsBuiltFromClause() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME)
                        .or(Where.field(Q.User.TIMESTAMP).isIn(SetupUser.GILL_TIMESTAMP, SetupUser.ANGIE_TIMESTAMP)))
                .query().toArray();


        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testAndEqualOperationsSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME),
                        Where.field(Q.User.IS_REGISTERED).is(SetupUser.CLYDE_IS_REGISTERED),
                        Where.field(Q.User.TIMESTAMP).is(SetupUser.CLYDE_TIMESTAMP)
                )
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testOrEqualOperationsSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME)
                        .or(Where.field(Q.User.USERNAME).is(SetupUser.ANGIE_USER_NAME)))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testAndOrEqualsOperationsSelection() {
        User[] users = Q.User.select()
                .where(Where.field(Q.User.USERNAME).is(SetupUser.CLYDE_USER_NAME)
                                .or(Where.field(Q.User.USERNAME).is(SetupUser.ANGIE_USER_NAME)),
                        Where.field(Q.User.TIMESTAMP).isMoreThanOrEqualTo(SetupUser.ANGIE_TIMESTAMP))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testNumericOrderByAscSelection() {
        User[] users = Q.User.select()
                .orderBy(Q.User.TIMESTAMP, OrderBy.Order.ASC)
                .query().toArray();

        // clyde, gill, josh, angie is the timestamp ascending order of the users created
        // by #setupFourTestUsers, therefore, we assert that the rows will be
        // selected in this order
        assertEquals(4, users.length);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[0].username);
        assertEquals(SetupUser.GILL_USER_NAME, users[1].username);
        assertEquals(SetupUser.JOSH_USER_NAME, users[2].username);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[3].username);
    }

    @Test
    public void testNumericDoubleOrderByAscSelection() {
        User[] users = Q.User.select()
                .orderBy(Q.User.COUNT, OrderBy.Order.ASC)
                .orderBy(Q.User.TIMESTAMP, OrderBy.Order.ASC)
                .query().toArray();

        // clyde, gill, josh, angie is the timestamp ascending order of the users created
        // by #setupFourTestUsers, therefore, we assert that the rows will be
        // selected in this order
        assertEquals(4, users.length);
        assertEquals(SetupUser.GILL_USER_NAME, users[0].username);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[1].username);
        assertEquals(SetupUser.JOSH_USER_NAME, users[2].username);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[3].username);
    }

    @Test
    public void testNumericOrderByDescSelection() {
        User[] users = Q.User.select()
                .orderBy(Q.User.TIMESTAMP, OrderBy.Order.DESC)
                .query().toArray();

        // angie, josh, gill, clyde is the timestamp descending order of the users created
        // by #setupFourTestUsers, therefore, we assert that the rows will be
        // selected in this order
        assertEquals(4, users.length);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[0].username);
        assertEquals(SetupUser.JOSH_USER_NAME, users[1].username);
        assertEquals(SetupUser.GILL_USER_NAME, users[2].username);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[3].username);
    }

    @Test
    public void testAlphaOrderByAscSelection() {
        User[] users = Q.User.select()
                .orderBy(Q.User.USERNAME, OrderBy.Order.ASC)
                .query().toArray();

        // angie, clyde, gill, josh is the username ascending order of the users created
        // by #setupFourTestUsers, therefore, we assert that the rows will be
        // selected in this order
        assertEquals(4, users.length);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[0].username);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[1].username);
        assertEquals(SetupUser.GILL_USER_NAME, users[2].username);
        assertEquals(SetupUser.JOSH_USER_NAME, users[3].username);
    }

    @Test
    public void testAlphaOrderByDescSelection() {
        User[] users = Q.User.select()
                .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
                .query().toArray();

        // josh, gill, clyde, angie is the username descending order of the users created
        // by #setupFourTestUsers, therefore, we assert that the rows will be
        // selected in this order
        assertEquals(4, users.length);
        assertEquals(SetupUser.JOSH_USER_NAME, users[0].username);
        assertEquals(SetupUser.GILL_USER_NAME, users[1].username);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[2].username);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[3].username);
    }

    @Test
    public void testOrderByRandom() {
        User[] users = Q.User.select()
                .orderBy(Q.User.USERNAME, OrderBy.Order.RANDOM)
                .query().toArray();

        // just check that the results are returned and no error is thrown
        // TODO: do 100 random queries and ensure that at least one of the ordering is different
        assertEquals(4, users.length);
    }

    @Test
    public void testLimitLowerBoundSelection() {
        User[] users = Q.User.select()
                .limit(0, 2)
                .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
                .query().toArray();

        assertEquals(2, users.length);
        assertEquals(SetupUser.JOSH_USER_NAME, users[0].username);
        assertEquals(SetupUser.GILL_USER_NAME, users[1].username);
    }

    @Test
    public void testLimitUpperBoundSelection() {
        User[] users = Q.User.select()
                .limit(2, 4)
                .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
                .query().toArray();

        assertEquals(2, users.length);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[0].username);
        assertEquals(SetupUser.ANGIE_USER_NAME, users[1].username);
    }
}