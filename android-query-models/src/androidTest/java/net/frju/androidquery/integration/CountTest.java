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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.frju.androidquery.model.Contact;
import net.frju.androidquery.model.RawContact;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CountTest {

    @Before
    public void setUp() {
        Contact.init(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testContactsCount() {
        long count = Contact.count().query();
        assertTrue(count > 0);
    }

    @Test
    public void testRawContactsCount() {
        long count = RawContact.count().query();
        assertTrue(count > 0);
    }
}