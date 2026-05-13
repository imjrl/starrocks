// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.hive.reader;

import com.starrocks.jni.connector.ColumnType;
import com.starrocks.jni.connector.ColumnValue;
import com.starrocks.jni.connector.ColumnValue.TypeValue;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestHiveColumnValue {

    private PrimitiveObjectInspector stringInspector;
    private Map<String, String> serdeProperties;

    @BeforeEach
    public void setUp() {
        stringInspector = new StringObjectInspector();
        serdeProperties = new HashMap<>();
    }

    @Test
    public void testGetStringWithNoNullFormat() {
        serdeProperties.clear();
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, "normal_value", "UTC", serdeProperties);
        Assertions.assertEquals("normal_value", hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }

    @Test
    public void testGetStringWithEmptyNullFormat() {
        serdeProperties.put("serialization.null.format", "");
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, "normal_value", "UTC", serdeProperties);
        Assertions.assertEquals("normal_value", hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }

    @Test
    public void testGetStringWithNullFormatMatch() {
        serdeProperties.put("serialization.null.format", "\\N");
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, "\\N", "UTC", serdeProperties);
        Assertions.assertNull(hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }

    @Test
    public void testGetStringWithNullFormatNoMatch() {
        serdeProperties.put("serialization.null.format", "\\N");
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, "normal_value", "UTC", serdeProperties);
        Assertions.assertEquals("normal_value", hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }

    @Test
    public void testGetStringWithNullValue() {
        serdeProperties.put("serialization.null.format", "\\N");
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, null, "UTC", serdeProperties);
        Assertions.assertNull(hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }

    @Test
    public void testGetStringWithDifferentNullFormats() {
        serdeProperties.put("serialization.null.format", "NULL");
        HiveColumnValue hiveColumnValue = new HiveColumnValue(stringInspector, "NULL", "UTC", serdeProperties);
        Assertions.assertNull(hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
        
        serdeProperties.put("serialization.null.format", "");
        hiveColumnValue = new HiveColumnValue(stringInspector, "NULL", "UTC", serdeProperties);
        Assertions.assertEquals("NULL", hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
        
        serdeProperties.put("serialization.null.format", "\\N");
        hiveColumnValue = new HiveColumnValue(stringInspector, "\\N", "UTC", serdeProperties);
        Assertions.assertNull(hiveColumnValue.getString(TypeValue.TYPE_VARCHAR));
    }
}