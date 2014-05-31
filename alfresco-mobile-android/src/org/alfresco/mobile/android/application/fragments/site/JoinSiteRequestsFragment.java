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
package org.alfresco.mobile.android.application.fragments.site;

import java.util.ArrayList;
import java.util.Map;

import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.application.R;
import org.alfresco.mobile.android.application.fragments.builder.AlfrescoFragmentBuilder;
import org.alfresco.mobile.android.async.site.member.CancelPendingMembershipEvent;
import org.alfresco.mobile.android.async.site.member.SitesPendingMembershipEvent;
import org.alfresco.mobile.android.platform.utils.MessengerUtils;
import org.alfresco.mobile.android.platform.utils.SessionUtils;
import org.alfresco.mobile.android.ui.site.SitesPendingMembershipFoundationFragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.otto.Subscribe;

/**
 * This Fragment is responsible to display the list of join site request. <br/>
 * 
 * @author Jean Marie Pascal
 */
public class JoinSiteRequestsFragment extends SitesPendingMembershipFoundationFragment
{
    /** Public Fragment TAG. */
    public static final String TAG = JoinSiteRequestsFragment.class.getName();

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public JoinSiteRequestsFragment()
    {
        emptyListMessageId = R.string.empty_joinsiterequest;
    }

    public static JoinSiteRequestsFragment newInstanceByTemplate(Bundle b)
    {
        JoinSiteRequestsFragment fr = new JoinSiteRequestsFragment();
        fr.setArguments(b);
        return fr;
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        setSession(SessionUtils.getSession(getActivity()));
        SessionUtils.checkSession(getActivity(), getSession());

        mTitle = getString(R.string.joinsiterequest_list_title);
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setTitle(mTitle);

        return d;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // RESULT
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    protected ArrayAdapter<?> onAdapterCreation()
    {
        return new JoinSiteRequestAdapter(this, R.layout.sdk_list_button_row, new ArrayList<Site>(0));
    }

    @Override
    @Subscribe
    public void onResult(SitesPendingMembershipEvent event)
    {
        super.onResult(event);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST MANAGEMENTS
    // ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    public void remove(Site site)
    {
        if (adapter != null)
        {
            ((ArrayAdapter<Site>) adapter).remove(site);
            if (adapter.isEmpty())
            {
                displayEmptyView();
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onCancelPendingMembershipEvent(CancelPendingMembershipEvent event)
    {
        int messageId = R.string.error_general;
        if (!event.hasException)
        {
            Site joinSiteRequest = event.data;
            messageId = R.string.action_cancel_join_site_request;
            remove(joinSiteRequest);
        }
        else
        {
            Log.w(TAG, Log.getStackTraceString(event.exception));
        }

        MessengerUtils.showLongToast(getActivity(), String.format(getString(messageId), event.oldSite.getShortName()));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(Activity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS & HELPERS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(Activity activity)
        {
            super(activity);
        }

        public Builder(Activity activity, Map<String, Object> configuration)
        {
            super(activity, configuration);
            menuTitleId = R.string.joinsiterequest_list_title;
            templateArguments = new String[] {};
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return JoinSiteRequestsFragment.newInstanceByTemplate(b);
        };

    }
}
