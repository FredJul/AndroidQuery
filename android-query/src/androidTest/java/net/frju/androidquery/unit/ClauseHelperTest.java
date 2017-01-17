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
package net.frju.androidquery.unit;

import net.frju.androidquery.database.ClauseHelper;
import net.frju.androidquery.gen.Q;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;
import net.frju.androidquery.unit.mock.ClauseHelperStub;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class ClauseHelperTest {

    @Test
    public void testWhereQueryIsBuiltFromClauseCollection() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        Where where = Where.field(Q.USER.USERNAME).isEqualTo("sam");

        String clause = clauseHelper.getCondition(new Where[]{where});
        String[] args = clauseHelper.getConditionArgs(new Where[]{where});

        assertEquals("username IS ?", clause);
        assertEquals(1, args.length);
        assertEquals("sam", args[0]);
    }

    @Test
    public void testInQueryIsBuiltFromClauseCollection() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        Where where = Where.field(Q.USER.USERNAME).isIn("sam", "josh");

        String clause = clauseHelper.getCondition(new Where[]{where});
        String[] args = clauseHelper.getConditionArgs(new Where[]{where});

        assertEquals("username IN (?,?)", clause);
        assertEquals(2, args.length);
        assertEquals("sam", args[0]);
        assertEquals("josh", args[1]);
    }

    @Test
    public void testAndWhereQueryIsBuiltFromClauseCollection() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        Where where = Where.field(Q.USER.TIMESTAMP).isMoreThan(10)
                .and(Where.field(Q.USER.TIMESTAMP).isEqualTo(20));

        String clause = clauseHelper.getCondition(new Where[]{where});
        String[] args = clauseHelper.getConditionArgs(new Where[]{where});

        assertEquals("(timestamp > ? AND timestamp IS ?)", clause);
        assertEquals(2, args.length);
        assertEquals("10", args[0]);
        assertEquals("20", args[1]);
    }

    @Test
    public void tesOrAndWhereQueryIsBuiltFromClauseCollection() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        Where where = Where.field(Q.USER.USERNAME).isEqualTo("sam")
                .or(Where.field(Q.USER.USERNAME).isEqualTo("angie"))
                .and(Where.field(Q.USER.TIMESTAMP).isMoreThanOrEqualTo(1234567890));

        String clause = clauseHelper.getCondition(new Where[]{where});
        String[] args = clauseHelper.getConditionArgs(new Where[]{where});

        assertEquals("((username IS ? OR username IS ?) AND timestamp >= ?)", clause);
        assertEquals(3, args.length);
        assertEquals("sam", args[0]);
        assertEquals("angie", args[1]);
        assertEquals("1234567890", args[2]);
    }

    @Test
    public void testOrWhereInQueryIsBuiltFromClause() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        Where where = Where.field(Q.USER.USERNAME).isEqualTo("sam")
                .or(Where.field(Q.USER.TIMESTAMP).isIn(10, 20));

        String clause = clauseHelper.getCondition(new Where[]{where});
        String[] args = clauseHelper.getConditionArgs(new Where[]{where});

        assertEquals("(username IS ? OR timestamp IN (?,?))", clause);
        assertEquals(3, args.length);
        assertEquals("sam", args[0]);
        assertEquals("10", args[1]);
        assertEquals("20", args[2]);
    }

    @Test
    public void testOrderByAscBuiltFromClause() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        String orderBy = clauseHelper.getOrderBy(new OrderBy[]{new OrderBy(Q.USER.USERNAME, OrderBy.Order.ASC)});

        assertEquals("username ASC", orderBy);
    }

    @Test
    public void testOrderByDescBuiltFromClause() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        String orderBy = clauseHelper.getOrderBy(new OrderBy[]{new OrderBy(Q.USER.USERNAME, OrderBy.Order.DESC)});

        assertEquals("username DESC", orderBy);
    }

    @Test
    public void testLimitBuiltFromClause() {
        ClauseHelper clauseHelper = new ClauseHelperStub();

        String limit = clauseHelper.getLimit(new Limit(0, 1));

        assertEquals("0,1", limit);
    }
}