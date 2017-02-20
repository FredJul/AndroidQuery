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
package net.frju.androidquery.integration;

import android.database.Cursor;

import net.frju.androidquery.gen.DATA;
import net.frju.androidquery.gen.LOG;
import net.frju.androidquery.gen.POST;
import net.frju.androidquery.gen.USER;
import net.frju.androidquery.integration.models.Data;
import net.frju.androidquery.integration.models.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class IndexTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();

        getSetupData().tearDownTestData();
        getSetupData().setupTestData();

        getSetupLog().tearDownTestLogs();
        getSetupLog().setupTestLogs();
    }

    @Test
    public void testPostIndexesAreCreated() {
        Cursor cursor = POST.raw("PRAGMA INDEX_LIST('Post');")
                .query();

        List<String> indexes = getIndexes(cursor);

        assertEquals(2, indexes.size()); // 2 because there is one index and one unique constraints
    }

    @Test
    public void testNoUserIndexesAreCreated() {
        Cursor cursor = USER.raw("PRAGMA INDEX_LIST('User');")
                .query();

        List<String> indexes = getIndexes(cursor);

        assertTrue(indexes.isEmpty());
    }

    @Test
    public void testAutoIncrementPrimaryKey() {
        Data[] data = DATA.select().query().toArray();

        assertEquals(3, data.length);
        assertNotEquals(0, data[0].id);
        assertNotEquals(0, data[1].id);
        assertNotEquals(0, data[2].id);
    }

    @Test
    public void testNoAutoIncrementPrimaryKey() {
        Log[] log = LOG.select().query().toArray();
        assertEquals(2, log.length);
    }

    private List<String> getIndexes(Cursor cursor) {
        List<String> indexes = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndex("name");
                if (index != -1) {
                    String indexName = cursor.getString(index);
                    indexes.add(indexName);
                }
            }
        } finally {
            cursor.close();
        }

        return indexes;
    }
}
