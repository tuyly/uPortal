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

package org.jasig.portal.io.xml.layout;

import org.dom4j.Element;
import org.jasig.portal.io.xml.crn.AbstractDom4jImporter;
import org.jasig.portal.layout.IUserLayoutStore;
import org.jasig.portal.utils.Tuple;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Imports a layout
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class LayoutImporter extends AbstractDom4jImporter {
    private IUserLayoutStore userLayoutStore;
    
    @Autowired
    public void setUserLayoutStore(IUserLayoutStore userLayoutStore) {
        this.userLayoutStore = userLayoutStore;
    }

    @Override
    protected void importDataElement(Tuple<String, Element> data) {
        userLayoutStore.importLayout(data.second);
    }
}
