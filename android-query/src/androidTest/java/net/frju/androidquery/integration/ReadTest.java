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

import static net.frju.androidquery.operation.condition.And.and;
import static net.frju.androidquery.operation.condition.In.in;
import static net.frju.androidquery.operation.condition.Or.or;
import static net.frju.androidquery.operation.condition.Where.where;
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
                .where(where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME))
                .querySingle();

        assertEquals(SetupUser.CLYDE_USER_NAME, user.getUsername());
    }

    @Test
    public void testUsernameIsNullSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.USERNAME, Where.Op.IS, null))
                .query().toArray();

        assertEquals(0, users.length);
    }

    @Test
    public void testIsNullSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.NULL_FIELD, Where.Op.IS, null))
                .query().toArray();

        assertEquals(4, users.length);
    }

    @Test
    public void testEqualToBooleanSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.IS_REGISTERED, Where.Op.IS, true))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testEqualToLongSelection() {
        User user = Q.User.select()
                .where(where(Q.User.TIMESTAMP, Where.Op.IS, SetupUser.CLYDE_TIMESTAMP))
                .querySingle();

        assertEquals(SetupUser.CLYDE_USER_NAME, user.getUsername());
        assertEquals(SetupUser.CLYDE_TIMESTAMP, user.getTimestamp());
        assertEquals(SetupUser.CLYDE_IS_REGISTERED, user.getIsRegistered());
    }

    @Test
    public void testMoreThanSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.TIMESTAMP, Where.Op.MORE_THAN, SetupUser.CLYDE_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testMoreThanOrEqualToSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.TIMESTAMP, Where.Op.MORE_THAN_OR_IS, SetupUser.CLYDE_TIMESTAMP))
                .query().toArray();

        // All 4 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 4 rows will be selected
        assertEquals(4, users.length);
    }

    @Test
    public void testLessThanSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.TIMESTAMP, Where.Op.LESS_THAN, SetupUser.ANGIE_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testLessThanOrEqualToSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.TIMESTAMP, Where.Op.LESS_THAN_OR_IS, SetupUser.ANGIE_TIMESTAMP))
                .query().toArray();

        // 4 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 4 rows will be selected
        assertEquals(4, users.length);
    }

    @Test
    public void testLikeStartingWithSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.USERNAME, Where.Op.LIKE, "jo%"))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testLikeEndingWithSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.USERNAME, Where.Op.LIKE, "%e"))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testLikeContainingSelection() {
        User[] users = Q.User.select()
                .where(where(Q.User.USERNAME, Where.Op.LIKE, "%lyd%"))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testInStringSelection() {
        User[] users = Q.User.select()
                .where(in(Q.User.USERNAME, SetupUser.CLYDE_USER_NAME, SetupUser.ANGIE_USER_NAME))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testInLongSelection() {
        User[] users = Q.User.select()
                .where(in(Q.User.TIMESTAMP, SetupUser.CLYDE_TIMESTAMP, SetupUser.ANGIE_TIMESTAMP, SetupUser.GILL_TIMESTAMP))
                .query().toArray();

        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testOrWhereInQueryIsBuiltFromClause() {
        User[] users = Q.User.select()
                .where(or(
                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME),
                        in(Q.User.TIMESTAMP, SetupUser.GILL_TIMESTAMP, SetupUser.ANGIE_TIMESTAMP)
                ))
                .query().toArray();


        // 3 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 3 rows will be selected
        assertEquals(3, users.length);
    }

    @Test
    public void testAndEqualOperationsSelection() {
        User[] users = Q.User.select()
                .where(and(
                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME),
                        where(Q.User.IS_REGISTERED, Where.Op.IS, SetupUser.CLYDE_IS_REGISTERED),
                        where(Q.User.TIMESTAMP, Where.Op.IS, SetupUser.CLYDE_TIMESTAMP)
                ))
                .query().toArray();

        // 1 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 1 rows will be selected
        assertEquals(1, users.length);
    }

    @Test
    public void testOrEqualOperationsSelection() {
        User[] users = Q.User.select()
                .where(or(
                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME),
                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.ANGIE_USER_NAME)
                ))
                .query().toArray();

        // 2 of the users created by #setupFourTestUsers will match the
        // exercise clause, therefore, we assert that 2 rows will be selected
        assertEquals(2, users.length);
    }

    @Test
    public void testAndOrEqualsOperationsSelection() {
        User[] users = Q.User.select()
                .where(
                        and(
                                or(
                                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME),
                                        where(Q.User.USERNAME, Where.Op.IS, SetupUser.ANGIE_USER_NAME)
                                ),
                                and(
                                        where(Q.User.TIMESTAMP, Where.Op.MORE_THAN_OR_IS, SetupUser.ANGIE_TIMESTAMP)
                                )
                        )
                )
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
        assertEquals(SetupUser.CLYDE_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.GILL_USER_NAME, users[1].getUsername());
        assertEquals(SetupUser.JOSH_USER_NAME, users[2].getUsername());
        assertEquals(SetupUser.ANGIE_USER_NAME, users[3].getUsername());
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
        assertEquals(SetupUser.ANGIE_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.JOSH_USER_NAME, users[1].getUsername());
        assertEquals(SetupUser.GILL_USER_NAME, users[2].getUsername());
        assertEquals(SetupUser.CLYDE_USER_NAME, users[3].getUsername());
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
        assertEquals(SetupUser.ANGIE_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.CLYDE_USER_NAME, users[1].getUsername());
        assertEquals(SetupUser.GILL_USER_NAME, users[2].getUsername());
        assertEquals(SetupUser.JOSH_USER_NAME, users[3].getUsername());
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
        assertEquals(SetupUser.JOSH_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.GILL_USER_NAME, users[1].getUsername());
        assertEquals(SetupUser.CLYDE_USER_NAME, users[2].getUsername());
        assertEquals(SetupUser.ANGIE_USER_NAME, users[3].getUsername());
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
        assertEquals(SetupUser.JOSH_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.GILL_USER_NAME, users[1].getUsername());
    }

    @Test
    public void testLimitUpperBoundSelection() {
        User[] users = Q.User.select()
                .limit(2, 4)
                .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
                .query().toArray();

        assertEquals(2, users.length);
        assertEquals(SetupUser.CLYDE_USER_NAME, users[0].getUsername());
        assertEquals(SetupUser.ANGIE_USER_NAME, users[1].getUsername());
    }
}