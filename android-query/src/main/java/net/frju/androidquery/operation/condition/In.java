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
public class In extends Where {
    private final boolean mNot;
    private final String mColumn;
    private final Object[] mValues;

    public boolean hasNot() {
        return mNot;
    }

    public String getColumn() {
        return mColumn;
    }

    public Object[] getValues() {
        return mValues;
    }

    /**
     * Specifies a SQLite IN operator
     *
     * @param not true to do the negative
     * @param column The column to perform the operation on
     * @param values The values of the in operator
     */
    In(boolean not, String column, Object... values) {
        mNot = not;
        mColumn = column;
        mValues = values;
    }
}