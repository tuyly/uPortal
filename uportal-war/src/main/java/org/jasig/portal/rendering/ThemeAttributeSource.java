/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.rendering;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.IUserProfile;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.layout.om.IStylesheetUserPreferences;
import org.jasig.portal.user.IUserInstance;

/**
 * Handles converting the data stored in {@link ThemeStylesheetUserPreferences} into additional attributes
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ThemeAttributeSource extends StylesheetAttributeSource {
    @Override
    public IStylesheetDescriptor getStylesheetDescriptor(HttpServletRequest request) {
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        final IUserProfile userProfile = preferencesManager.getUserProfile();
        final int themeStylesheetId = userProfile.getThemeStylesheetId();
        
        return this.stylesheetDescriptorDao.getStylesheetDescriptor(themeStylesheetId);
    }
    
    @Override
    public IStylesheetUserPreferences getStylesheetUserPreferences(HttpServletRequest request) {
        return this.stylesheetUserPreferencesService.getThemeStylesheetUserPreferences(request);
    }
}

