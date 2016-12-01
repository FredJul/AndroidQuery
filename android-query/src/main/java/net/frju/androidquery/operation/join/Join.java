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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.operation.join;

import net.frju.androidquery.operation.condition.Condition;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class Join<J> {
    private final Class<J> mTable;
    private final Join mJoin;
    private final Condition[] mConditions;

    public Class<J> getTable() {
        return mTable;
    }

    public Join getJoin() {
        return mJoin;
    }

    public Condition[] getClauses() {
        return mConditions;
    }

    public Join(Class<J> table, Join join, Condition... conditions) {
        mTable = table;
        mJoin = join;
        mConditions = conditions;
    }
}