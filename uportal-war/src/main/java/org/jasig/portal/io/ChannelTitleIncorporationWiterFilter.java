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

package org.jasig.portal.io;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portal.layout.IUserLayoutManager;
import org.jasig.portal.layout.node.IUserLayoutChannelDescription;
import org.jasig.portal.portlet.rendering.IPortletExecutionManager;

/**
 * Provides for streaming token replacement with a character stream.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ChannelTitleIncorporationWiterFilter extends AbstractTokenReplacementFilter {
    public static final String TITLE_TOKEN_PREFIX = "UP:CHANNEL_TITLE-{";
    public static final int MAX_CHANNEL_ID_LENGTH = 32;
    public static final String TITLE_TOKEN_SUFFIX = "}";
    
    private final IPortletExecutionManager portletExecutionManager;
    private final IUserLayoutManager userLayoutManager;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    /**
     * @param wrappedWriter Writer to delegate writing to.
     * @param channelManager Used to load the dynamic channel title.
     * @param userLayoutManager Used to access the default title if no dynamic title is provided.
     */
    public ChannelTitleIncorporationWiterFilter(Writer wrappedWriter, IPortletExecutionManager portletExecutionManager, IUserLayoutManager userLayoutManager,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(wrappedWriter, TITLE_TOKEN_PREFIX, MAX_CHANNEL_ID_LENGTH, TITLE_TOKEN_SUFFIX);
        this.portletExecutionManager = portletExecutionManager;
        this.userLayoutManager = userLayoutManager;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.io.AbstractTokenReplacementFilter#replaceToken(java.lang.String)
     */
    @Override
    protected String replaceToken(String channelId) {
        final IUserLayoutChannelDescription channelNode = (IUserLayoutChannelDescription)this.userLayoutManager.getNode(channelId);
        final boolean disableDynamicTitle = Boolean.valueOf(channelNode.getParameterValue("disableDynamicTitle"));

        final String channelSubscribeId = channelNode.getChannelSubscribeId();
        
        String title = null;
        if (!disableDynamicTitle && channelSubscribeId != null) {
            title = this.portletExecutionManager.getPortletTitle(channelSubscribeId, this.httpServletRequest, this.httpServletResponse);
        }
        
        if (title == null) {
            title = channelNode.getTitle();
        }
        
        return title;
    }
}