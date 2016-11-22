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
package net.frju.androidquery.integration;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.integration.utils.Setup;
import net.frju.androidquery.integration.utils.SetupData;
import net.frju.androidquery.integration.utils.SetupLog;
import net.frju.androidquery.integration.utils.SetupPost;
import net.frju.androidquery.integration.utils.SetupUser;

import org.junit.Before;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class IntegrationTest extends ActivityInstrumentationTestCase2<Activity> {
    private Setup mSetup;
    private SetupUser mSetupUser;
    private SetupPost mSetupPost;
    private SetupLog mSetupLog;
    private SetupData mSetupData;

    protected DatabaseProvider getSQLProvider() {
        return mSetup.getSQLProvider();
    }

    protected SetupUser getSetupUser() {
        return mSetupUser;
    }

    protected SetupPost getSetupPost() {
        return mSetupPost;
    }

    protected SetupLog getSetupLog() {
        return mSetupLog;
    }

    public SetupData getSetupData() {
        return mSetupData;
    }

    @Before
    public void setUp() {
        getInstrumentation().getTargetContext().deleteDatabase(Setup.DATABASE_NAME);

        mSetupUser = new SetupUser();
        mSetupPost = new SetupPost();
        mSetupLog = new SetupLog();
        mSetupData = new SetupData();

        mSetup = new Setup(getInstrumentation().getTargetContext());
        mSetup.setUp();
    }

    public IntegrationTest() {
        super(Activity.class);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }
}