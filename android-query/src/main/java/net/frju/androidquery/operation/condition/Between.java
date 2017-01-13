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
package net.frju.androidquery.operation.condition;

public class Between extends Where {
    private final boolean mNot;
    private final String mColumn;
    private final Object mValue1;
    private final Object mValue2;

    public boolean hasNot() {
        return mNot;
    }

    public String getColumn() {
        return mColumn;
    }

    public Object getValue1() {
        return mValue1;
    }

    public Object getValue2() {
        return mValue2;
    }

    /**
     * Specifies a SQLite BETWEEN operator
     *
     * @param not true to do the negative
     * @param column The column to perform the operation on
     * @param value1 The values of the in operator
     * @param value2 The values of the in operator
     */
    Between(boolean not, String column, Object value1, Object value2) {
        mNot = not;
        mColumn = column;
        mValue1 = value1;
        mValue2 = value2;
    }
}