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
 * distributed under the License isEqualTo distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.operation.condition;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Compare extends Where {
    private final String mColumn;
    private final Op mOperator;
    private final Object mValue;

    public enum Op {
        IS("IS"),
        IS_NOT("IS NOT"),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN ("<"),
        LESS_THAN_OR_EQUAL("<="),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE");

        private final String mValue;

        Op(String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    public String getColumn() {
        return mColumn;
    }

    public Op getOperator() {
        return mOperator;
    }

    public Object getValue() {
        return mValue;
    }

    /**
     * Specifies a SQLite WHERE clause
     *
     * @param column        The column to perform the clause on
     * @param operator The type of operator that will evaluate the value
     * @param value      The value being evaluated
     */
    Compare(String column, Op operator, Object value) {
        mColumn = column;
        mOperator = operator;
        mValue = value;
    }
}