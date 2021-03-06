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
public class Or extends Where {
    private final Where[] mWhere;

    public Where[] getCondition() {
        return mWhere;
    }

    /**
     * Specifies a SQLite OR operator
     *
     * @param where Combine multiple conditions
     */
    Or(Where... where) {
        mWhere = where;
    }
}
