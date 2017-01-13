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
package net.frju.androidquery.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public interface DbModelDescriptor {
    @NonNull
    String getTableDbName();

    @NonNull
    String getTableCreateQuery();

    @NonNull
    String[] getColumnsSqlArray();

    String getPrimaryKeyDbName();
    String[] getIndexNames();
    String getCreateIndexQuery();
    String[] getColumnNames();

    String[] getColumnNamesWithTablePrefix();

    void setIdToModel(@NonNull Object model, long id);

    @NonNull
    ContentValues getContentValues(@NonNull Object model);

    Object getPrimaryKeyValue(@NonNull Object model);

    boolean isPrimaryKeyAutoIncrement();

    <T> T getSingleResult(Cursor cursor);

    <T> T[] getArrayResult(Cursor cursor);
}