/**
 *   ownCloud Android client application
 *
 *   @author masensio
 *   @author David A. Velasco
 *   Copyright (C) 2016 ownCloud GmbH.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.wecloud.android.ui.fragment;

import com.wecloud.android.datamodel.OCFile;
import com.owncloud.android.lib.resources.shares.OCShare;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in fragments handling {@link OCShare}s
 * to be communicated to the parent activity and potentially other fragments
 * contained in that activity.
 *
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface ShareFragmentListener {
    void copyOrSendPrivateLink(OCFile file);
    void showSearchUsersAndGroups();
    void showEditPrivateShare(OCShare share);
    void refreshSharesFromServer();
    void removeShare(OCShare share);
    void showAddPublicShare(String defaultLinkName);
    void showEditPublicShare(OCShare share);
    void copyOrSendPublicLink(OCShare share);
}
