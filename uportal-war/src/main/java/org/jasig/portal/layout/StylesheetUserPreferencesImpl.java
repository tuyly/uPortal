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

package org.jasig.portal.layout;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jasig.portal.layout.om.IStylesheetUserPreferences;

/**
 * Stylesheet user preferences setup for memory or serialized storage. All APIs and returned objects are thread safe.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class StylesheetUserPreferencesImpl implements IStylesheetUserPreferences, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final long stylesheetDescriptorId;
    private final ConcurrentMap<String, String> outputProperties = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, String> parameters = new ConcurrentHashMap<String, String>();
    //NodeId -> Name -> Value
    private final ConcurrentMap<String, ConcurrentMap<String, String>> layoutAttributes = new ConcurrentHashMap<String, ConcurrentMap<String,String>>();
    
    public StylesheetUserPreferencesImpl(long stylesheetDescriptorId) {
        this.stylesheetDescriptorId = stylesheetDescriptorId;
    }

    @Override
    public long getId() {
        return -1;
    }
    
    @Override
    public long getStylesheetDescriptorId() {
        return this.stylesheetDescriptorId;
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#setStylesheetUserPreferences(org.jasig.portal.layout.om.IStylesheetUserPreferences)
     */
    @Override
    public void setStylesheetUserPreferences(IStylesheetUserPreferences source) {
        final Properties properties = source.getOutputProperties();
        if (!this.outputProperties.equals(properties)) {
            this.outputProperties.clear();
        }
        //Really wish putAll worked for Properties
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            this.outputProperties.put((String)entry.getKey(), (String)entry.getValue());
        }

        final Map<String, String> parameters = source.getStylesheetParameters();
        if (!this.parameters.equals(parameters)) {
            this.parameters.clear();
        }
        this.parameters.putAll(parameters);

        synchronized (this.layoutAttributes) {
            final Map<String, Map<String, String>> attributes = source.getAllLayoutAttributes();
            if (!this.layoutAttributes.equals(attributes)) {
                this.layoutAttributes.clear();
            }
            
            if (!this.layoutAttributes.equals(attributes)) {
                this.layoutAttributes.clear();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#getOutputProperty(java.lang.String)
     */
    @Override
    public String getOutputProperty(String name) {
        return this.outputProperties.get(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#setOutputProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String setOutputProperty(String name, String value) {
        return this.outputProperties.put(name, value);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#removeOutputProperty(java.lang.String)
     */
    @Override
    public String removeOutputProperty(String name) {
        return this.outputProperties.remove(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#getOutputProperties()
     */
    @Override
    public Properties getOutputProperties() {
        final Properties props = new Properties();
        props.putAll(this.outputProperties);
        return props;
    }
    
    @Override
    public void clearOutputProperties() {
        this.outputProperties.clear();
    }

    @Override
    public String getStylesheetParameter(String name) {
        return this.parameters.get(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#setStylesheetParameter(java.lang.String, java.lang.String)
     */
    @Override
    public String setStylesheetParameter(String name, String value) {
        return this.parameters.put(name, value);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#removeStylesheetParameter(java.lang.String)
     */
    @Override
    public String removeStylesheetParameter(String name) {
        return this.parameters.remove(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#getStylesheetParameters()
     */
    @Override
    public Map<String, String> getStylesheetParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }
    
    @Override
    public void clearStylesheetParameters() {
        this.parameters.clear();
    }

    @Override
    public String getLayoutAttribute(String nodeId, String name) {
        final Map<String, String> nodeAttributes = this.layoutAttributes.get(nodeId);
        if (nodeAttributes == null) {
            return null;
        }
        
        return nodeAttributes.get(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#setLayoutAttribute(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String setLayoutAttribute(String nodeId, String name, String value) {
        ConcurrentMap<String, String> nodeAttributes;
        synchronized (this.layoutAttributes) {
            nodeAttributes = this.layoutAttributes.get(nodeId);
            if (nodeAttributes == null) {
                nodeAttributes = new ConcurrentHashMap<String, String>();
                this.layoutAttributes.put(nodeId, nodeAttributes);
            }
        }
        
        return nodeAttributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#removeLayoutAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public String removeLayoutAttribute(String nodeId, String name) {
        final Map<String, String> nodeAttributes = this.layoutAttributes.get(nodeId);
        if (nodeAttributes == null) {
            return null;
        }
        
        return nodeAttributes.remove(name);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#getLayoutAttributes(java.lang.String)
     */
    @Override
    public Map<String, String> getLayoutAttributes(String nodeId) {
        final Map<String, String> nodeAttributes = this.layoutAttributes.get(nodeId);
        if (nodeAttributes == null) {
            return Collections.emptyMap();
        }
        
        return Collections.unmodifiableMap(nodeAttributes);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.layout.om.IStylesheetUserPreferences#getAllLayoutAttributes()
     */
    @Override
    public Map<String, Map<String, String>> getAllLayoutAttributes() {
        final Map<String, Map<String, String>> layoutAttributesClone = new TreeMap<String, Map<String,String>>();
        
        for (final Map.Entry<String, ConcurrentMap<String, String>> layoutNodeAttribute : this.layoutAttributes.entrySet()) {
            final String nodeId = layoutNodeAttribute.getKey();
            final Map<String, String> attributes = layoutNodeAttribute.getValue();
            layoutAttributesClone.put(nodeId, Collections.unmodifiableMap(new TreeMap<String, String>(attributes)));
        }
        
        return Collections.unmodifiableMap(layoutAttributesClone);
    }
    
    @Override
    public void clearLayoutAttributes(String nodeId) {
        this.layoutAttributes.remove(nodeId);
    }

    @Override
    public void clearAllLayoutAttributes() {
        synchronized (this.layoutAttributes) {
            this.layoutAttributes.clear();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.layoutAttributes == null) ? 0 : this.layoutAttributes.hashCode());
        result = prime * result + ((this.outputProperties == null) ? 0 : this.outputProperties.hashCode());
        result = prime * result + ((this.parameters == null) ? 0 : this.parameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StylesheetUserPreferencesImpl other = (StylesheetUserPreferencesImpl) obj;
        if (this.layoutAttributes == null) {
            if (other.layoutAttributes != null)
                return false;
        }
        else if (!this.layoutAttributes.equals(other.layoutAttributes))
            return false;
        if (this.outputProperties == null) {
            if (other.outputProperties != null)
                return false;
        }
        else if (!this.outputProperties.equals(other.outputProperties))
            return false;
        if (this.parameters == null) {
            if (other.parameters != null)
                return false;
        }
        else if (!this.parameters.equals(other.parameters))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StylesheetUserPreferencesImpl [outputProperties=" + this.outputProperties
                + ", parameters=" + this.parameters + ", layoutAttributes=" + this.layoutAttributes + "]";
    }
}
