/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits;


import com.nimbits.client.helper.EntityHelper;
import com.nimbits.client.model.point.Point;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/11/12
 * Time: 4:19 PM
 */


public class EntityHelperTest {


    private EntityHelper entityHelper = new EntityHelper();

    @Test
    public void testCreatePointWithName() throws Exception {
        Point name = entityHelper.createPointWithName("test");
        assertNotNull(name);
    }
}