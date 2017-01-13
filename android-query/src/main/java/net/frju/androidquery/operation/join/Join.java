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
package net.frju.androidquery.operation.join;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Join {
    public enum Type {
        INNER, LEFT_OUTER, CROSS_INNER, NATURAL_INNER, NATURAL_LEFT_OUTER
    }

    private final Class<?> mInitialTable;
    private final Class<?> mAddedTable;
    private final String mInitialTableColumn;
    private final String mAddedTableColumn;
    private final Type mType;

    public Type getType() {
        return mType;
    }

    public Class<?> getInitialTable() {
        return mInitialTable;
    }

    public Class<?> getAddedTable() {
        return mAddedTable;
    }

    public String getInitialTableColumn() {
        return mInitialTableColumn;
    }

    public String getAddedTableColumn() {
        return mAddedTableColumn;
    }

    public Join(Type type, Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        mType = type;
        mInitialTable = initialTable;
        mAddedTable = addedTable;
        mInitialTableColumn = initialTableColumn;
        mAddedTableColumn = addedTableColumn;
    }

    public static Join innerJoin(Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        return new Join(Type.INNER, initialTable, initialTableColumn, addedTable, addedTableColumn);
    }

    public static Join leftOuterJoin(Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        return new Join(Type.LEFT_OUTER, initialTable, initialTableColumn, addedTable, addedTableColumn);
    }

    public static Join crossInnerJoin(Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        return new Join(Type.CROSS_INNER, initialTable, initialTableColumn, addedTable, addedTableColumn);
    }

    public static Join naturalInnerJoin(Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        return new Join(Type.NATURAL_INNER, initialTable, initialTableColumn, addedTable, addedTableColumn);
    }

    public static Join naturalLeftOuterJoin(Class<?> initialTable, String initialTableColumn, Class<?> addedTable, String addedTableColumn) {
        return new Join(Type.NATURAL_LEFT_OUTER, initialTable, initialTableColumn, addedTable, addedTableColumn);
    }
}