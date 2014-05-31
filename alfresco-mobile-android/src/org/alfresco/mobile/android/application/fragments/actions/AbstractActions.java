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
package org.alfresco.mobile.android.application.fragments.actions;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.platform.accounts.AlfrescoAccount;
import org.alfresco.mobile.android.platform.io.AlfrescoStorageManager;
import org.alfresco.mobile.android.platform.utils.SessionUtils;

import android.app.Activity;
import android.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;

public abstract class AbstractActions<T> implements ActionMode.Callback
{
    protected onFinishModeListerner mListener;

    protected ActionMode mode;

    protected WeakReference<Activity> activityRef = null;

    protected WeakReference<Fragment> fragmentRef;

    protected boolean multiSelectionEnable = true;
    
    protected List<T> selectedItems = new ArrayList<T>();

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        this.mode = mode;
        getMenu(menu);
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        mode.setTitle(createTitle());
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
        mListener.onFinish();
        selectedItems.clear();
    }

    public void finish()
    {
        mode.finish();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    protected abstract CharSequence createTitle();

    // ///////////////////////////////////////////////////////////////////////////////////
    // LIST MANAGEMENT
    // ///////////////////////////////////////////////////////////////////////////////////
    public void selectNode(T n)
    {
        if (!multiSelectionEnable){
            selectedItems.clear();
        }
        
        if (selectedItems.contains(n))
        {
            removeNode(n);
        }
        else
        {
            addNode(n);
        }
        if (selectedItems.isEmpty())
        {
            mode.finish();
        }
        else
        {
            mode.setTitle(createTitle());
            mode.invalidate();
        }
    }

    public void selectNodes(List<T> nodes)
    {
        selectedItems.clear();
        for (T node : nodes)
        {
            addNode(node);
        }
        mode.setTitle(createTitle());
        mode.invalidate();
    }

    protected void addNode(T n)
    {
        if (n == null) { return; }

        if (!selectedItems.contains(n))
        {
            selectedItems.add(n);
        }
    }

    protected void removeNode(T n)
    {
        selectedItems.remove(n);
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////////////
    private void getMenu(Menu menu)
    {
        menu.clear();
        getMenu(getActivity(), menu);
    }

    protected void getMenu(Activity activity2, Menu menu)
    {
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    public static File getDownloadFile(final Activity activity, final Node node)
    {
        if (activity != null && node != null && SessionUtils.getAccount(activity) != null)
        {
            File folder = AlfrescoStorageManager.getInstance(activity).getDownloadFolder(SessionUtils.getAccount(activity));
            if (folder != null) { return new File(folder, node.getName()); }
        }

        return null;
    }

    public static File getTempFile(final Activity activity, final Node node)
    {
        if (activity != null && node != null && SessionUtils.getAccount(activity) != null)
        {
            File folder = AlfrescoStorageManager.getInstance(activity).getTempFolder(SessionUtils.getAccount(activity));
            if (folder != null) { return new File(folder, node.getName()); }
        }

        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // LISTENER
    // ///////////////////////////////////////////////////////////////////////////////////
    public interface onFinishModeListerner
    {
        void onFinish();
    }

    public void setOnFinishModeListerner(onFinishModeListerner mListener)
    {
        this.mListener = mListener;
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////////////
    protected Activity getActivity()
    {
        return activityRef.get();
    }

    protected Fragment getFragment()
    {
        return fragmentRef.get();
    }
    
    protected AlfrescoSession getSession()
    {
        if (getActivity() == null) {return null;}
        return SessionUtils.getSession(getActivity());
    }

    protected AlfrescoAccount getAccount()
    {
        if (getActivity() == null) {return null;}
        return SessionUtils.getAccount(getActivity());
    }
    
    protected T getCurrentItem()
    {
        if (selectedItems == null || selectedItems.isEmpty()) {return null;}
        return selectedItems.get(0);
    }
    
    public boolean hasMultiSelectionEnabled()
    {
        return multiSelectionEnable;
    }
    
}
