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
package net.frju.androidquery.operation.condition;

/**
 * A Condition is used to provide an expressive api for querying the database.
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Condition {

    public static Where where(String column, Where.Op operator, Object value) {
        return new Where(column, operator, value);
    }

    public static And and(Condition... condition) {
        return new And(condition);
    }

    public static Or or(Condition... condition) {
        return new Or(condition);
    }

    public static In in(String column, Object... values) {
        return new In(column, values);
    }

    public static On on(String column1, String column2) {
        return new On(column1, column2);
    }
}
