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
package net.frju.androidquery.operation.condition;

/**
 * A Where is used to provide an expressive api for querying the database.
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Where {

    static public class Field {
        private final String mFieldDbName;

        private Field(String fieldDbName) {
            mFieldDbName = fieldDbName;
        }

        public Compare is(Object value) {
            return new Compare(mFieldDbName, Compare.Op.IS, value);
        }

        public Compare isNot(Object value) {
            return new Compare(mFieldDbName, Compare.Op.IS_NOT, value);
        }

        public Compare isMoreThan(Object value) {
            return new Compare(mFieldDbName, Compare.Op.MORE_THAN, value);
        }

        public Compare isMoreThanOrEqualTo(Object value) {
            return new Compare(mFieldDbName, Compare.Op.MORE_THAN_OR_EQUAL, value);
        }

        public Compare isLessThan(Object value) {
            return new Compare(mFieldDbName, Compare.Op.LESS_THAN, value);
        }

        public Compare isLessThanOrEqualTo(Object value) {
            return new Compare(mFieldDbName, Compare.Op.LESS_THAN_OR_EQUAL, value);
        }

        public Compare isLike(Object value) {
            return new Compare(mFieldDbName, Compare.Op.LIKE, value);
        }

        public Compare isNotLike(Object value) {
            return new Compare(mFieldDbName, Compare.Op.NOT_LIKE, value);
        }

        public In isIn(Object... values) {
            return new In(false, mFieldDbName, values);
        }

        public In isNotIn(Object... values) {
            return new In(true, mFieldDbName, values);
        }

        public Between isBetween(Object value1, Object value2) {
            return new Between(false, mFieldDbName, value1, value2);
        }

        public Between isNotBetween(Object value1, Object value2) {
            return new Between(true, mFieldDbName, value1, value2);
        }
    }

    public static Field field(String fieldDbName) {
        return new Field(fieldDbName);
    }

    public static And combinesWithAnd(Where... where) {
        return new And(where);
    }

    public static Or combinesWithOr(Where... where) {
        return new Or(where);
    }

    public And and(Where where) {
        return new And(this, where);
    }

    public Or or(Where where) {
        return new Or(this, where);
    }
}