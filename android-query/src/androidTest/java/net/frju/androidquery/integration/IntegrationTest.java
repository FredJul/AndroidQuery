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
package net.frju.androidquery.integration;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.utils.SetupData;
import net.frju.androidquery.integration.utils.SetupLog;
import net.frju.androidquery.integration.utils.SetupPost;
import net.frju.androidquery.integration.utils.SetupUser;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
@RunWith(AndroidJUnit4.class)
public abstract class IntegrationTest {
    private SetupUser mSetupUser;
    private SetupPost mSetupPost;
    private SetupLog mSetupLog;
    private SetupData mSetupData;

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
        mSetupUser = new SetupUser();
        mSetupPost = new SetupPost();
        mSetupLog = new SetupLog();
        mSetupData = new SetupData();

        Q.init(InstrumentationRegistry.getTargetContext());
    }
}