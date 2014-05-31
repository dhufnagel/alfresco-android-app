/*******************************************************************************
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco Mobile for Android.
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
 *******************************************************************************/
package org.alfresco.mobile.android.application.managers;

import java.io.File;

import org.alfresco.mobile.android.platform.intent.PrivateRequestCode;
import org.alfresco.mobile.android.platform.security.DataProtectionManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class DataProtectionManagerImpl extends DataProtectionManager
{
    // ////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ////////////////////////////////////////////////////
    public static DataProtectionManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new DataProtectionManagerImpl(context.getApplicationContext());
            }

            return (DataProtectionManagerImpl) mInstance;
        }
    }

    protected DataProtectionManagerImpl(Context applicationContext)
    {
        super(applicationContext);
    }

    // ////////////////////////////////////////////////////
    // Utility
    // ////////////////////////////////////////////////////
    private Intent createActionIntent(Activity activity, int intentAction, File f)
    {
        Intent intentI = null;
        switch (intentAction)
        {
            case DataProtectionManager.ACTION_SEND_ALFRESCO:
                intentI = ActionUtils.createSendFileToAlfrescoIntent(activity, f);
                break;
            case DataProtectionManager.ACTION_SEND:
                intentI = ActionUtils.createSendIntent(activity, f);
                break;
            case DataProtectionManager.ACTION_VIEW:
                intentI = ActionUtils.createViewIntent(activity, f);
                break;
            default:
                break;
        }

        if (intentI != null)
        {
            setRequiredDataProtectionFile(f);
        }
        return intentI;
    }

    public void executeAction(Activity activity, int intentAction, File f)
    {
        try
        {
            if (intentAction == DataProtectionManager.ACTION_NONE || intentAction == 0) { return; }
            activity.startActivityForResult(createActionIntent(activity, intentAction, f), PrivateRequestCode.DECRYPTED);
        }
        catch (ActivityNotFoundException e)
        {

        }
    }

    public void executeAction(Fragment fragment, int intentAction, File f)
    {
        try
        {
            if (intentAction == DataProtectionManager.ACTION_NONE || intentAction == 0) { return; }
            fragment.startActivityForResult(createActionIntent(fragment.getActivity(), intentAction, f),
                    PrivateRequestCode.DECRYPTED);
        }
        catch (ActivityNotFoundException e)
        {

        }
    }
}
