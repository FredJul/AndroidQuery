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
@SuppressWarnings("unchecked")
public class NaturalInnerJoin<J> extends Join {

    public NaturalInnerJoin(Class<J> table, Join join, Condition... conditions) {
        super(table, join, conditions);
    }

    public static NaturalInnerJoin naturalInnerJoin(Class<?> table, Condition... conditions) {
        return new NaturalInnerJoin(table, null, conditions);
    }

    public static NaturalInnerJoin naturalInnerJoin(Class<?> table, Join join, Condition... conditions) {
        return new NaturalInnerJoin(table, join, conditions);
    }
}