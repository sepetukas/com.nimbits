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

package com.nimbits.mobile.main;

import com.nimbits.cloudplatform.client.enums.EntityType;

import java.util.*;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 10:21 AM
 */
public enum SettingOption {
    application("Application Options", EntityType.toTypeList()),
    point("General Point Settings", Arrays.asList(EntityType.point)),
    alert("Alerts", Arrays.asList(EntityType.point));


    private static final Map<String, SettingOption> lookup = new HashMap<String, SettingOption>(EntityType.values().length);

    static {
        for (SettingOption s : EnumSet.allOf(SettingOption.class))
            lookup.put(s.text, s);
    }


    final String text;
    final List<EntityType> types;

    SettingOption(String text, List<EntityType> types) {
        this.text = text;
        this.types = types;
    }

    public String getText() {
        return text;
    }

    public static SettingOption get(String text) {
        return lookup.get(text);
    }

    public static CharSequence[] toAndroidOptionArray(EntityType type) {

        List<String> l = toList(type);
        return toList(type).toArray(new CharSequence[l.size()]);
    }

    public boolean isTypeOption(EntityType type) {
        return types.contains(type);
    }

    private static List<String> toList(EntityType type) {

        List<String> values = new ArrayList<String>(); //don't set size

        for (SettingOption s : EnumSet.allOf(SettingOption.class)) {
            if (s.isTypeOption(type)) {
                values.add(s.text);
            }

        }

        return values;
    }
}
