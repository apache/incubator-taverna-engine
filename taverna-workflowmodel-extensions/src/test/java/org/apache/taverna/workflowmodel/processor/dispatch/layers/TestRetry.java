/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.dispatch.layers;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.*;

public class TestRetry {
    
    @Test
    public void defaultConfig() throws Exception {
        Retry retry = new Retry();
        JsonNode configuration = retry.getConfiguration();
        assertEquals(0, configuration.get("maxRetries").intValue());
        assertEquals(1000, configuration.get("initialDelay").intValue());
        assertEquals(5000, configuration.get("maxDelay").intValue());
        assertEquals(1.0, configuration.get("backoffFactor").doubleValue(), 0.001);
    }

    @Test
    public void customConfig() throws Exception {
        Retry retry = new Retry(15, 150, 1200, 1.2);
        JsonNode configuration = retry.getConfiguration();
        assertEquals(15, configuration.get("maxRetries").intValue());
        assertEquals(150, configuration.get("initialDelay").intValue());
        assertEquals(1200, configuration.get("maxDelay").intValue());
        assertEquals(1.2, configuration.get("backoffFactor").doubleValue(), 0.001);
    }
    
    @Test
    public void configureEmpty() throws Exception {
        Retry retry = new Retry(15, 150, 1200, 1.2);
        JsonNode empty = JsonNodeFactory.instance.objectNode();
        retry.configure(empty);
        // We would expect missing values to be replaced with the
        // DEFAULT values rather than the previous values
        JsonNode configuration = retry.getConfiguration();
        assertEquals(0, configuration.get("maxRetries").intValue());
        assertEquals(1000, configuration.get("initialDelay").intValue());
        assertEquals(5000, configuration.get("maxDelay").intValue());
        assertEquals(1.0, configuration.get("backoffFactor").doubleValue(), 0.001);
    }

    @Test
    public void configurePartly() throws Exception {
        Retry retry = new Retry(15, 150, 1200, 1.2);
        ObjectNode conf = JsonNodeFactory.instance.objectNode();
        conf.put("maxRetries", 15);
        conf.put("backoffFactor", 1.2);
        retry.configure(conf);
        // We would expect to see the new values
        JsonNode configuration = retry.getConfiguration();
        assertEquals(15, configuration.get("maxRetries").intValue());
        assertEquals(1.2, configuration.get("backoffFactor").doubleValue(), 0.001);
        // And the default values (not the previous values!)
        assertEquals(1000, configuration.get("initialDelay").intValue());
        assertEquals(5000, configuration.get("maxDelay").intValue());
    }    
    
    @Test(expected=IllegalArgumentException.class)
    public void invalidMaxRetries() throws Exception {
        Retry retry = new Retry();
        ObjectNode conf = JsonNodeFactory.instance.objectNode();
        conf.put("maxRetries", -15);
        retry.configure(conf);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidInitialDelay() throws Exception {
        Retry retry = new Retry();
        ObjectNode conf = JsonNodeFactory.instance.objectNode();
        conf.put("initialDelay", -15);
        retry.configure(conf);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void invalidMaxDelay() throws Exception {
        Retry retry = new Retry();
        ObjectNode conf = JsonNodeFactory.instance.objectNode();
        conf.put("maxDelay", 150);
        // Valid on its own, but less than the default initialDelay of 1000!
        retry.configure(conf);
    }

    
    @Test
    public void invalidConfigureRecovers() throws Exception {
        Retry retry = new Retry(15, 150, 1200, 1.2);
        ObjectNode conf = JsonNodeFactory.instance.objectNode();
        conf.put("maxRetries", -15);
        try { 
            retry.configure(conf);
        } catch (IllegalArgumentException ex) {
            // As expected
        }
        // We would expect the earlier values to persist
        JsonNode configuration = retry.getConfiguration();
        assertEquals(15, configuration.get("maxRetries").intValue());
        assertEquals(150, configuration.get("initialDelay").intValue());
        assertEquals(1200, configuration.get("maxDelay").intValue());
        assertEquals(1.2, configuration.get("backoffFactor").doubleValue(), 0.001);
    }
    
    // TODO: Testing the Retry layer without making a big dispatch stack and job context
    
    
}
