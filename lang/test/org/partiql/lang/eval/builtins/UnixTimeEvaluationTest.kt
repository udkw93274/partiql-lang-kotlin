/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 *  You may not use this file except in compliance with the License.
 * A copy of the License is located at:
 *
 *      http://aws.amazon.com/apache2.0/
 *
 *  or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 *  language governing permissions and limitations under the License.
 */

package org.partiql.lang.eval.builtins

import org.junit.*
import org.partiql.lang.eval.*

class UnixTimeEvaluationTest : EvaluatorTestBase() {
    @Test
    fun TimestampToUnixtime() = assertEval("timestamp_to_unixtime(`2016-12-15T18:07:31.155Z`)", "1481825251155")

    @Test
    fun UnixtimeToTimestamp() = assertEval("unixtime_to_timestamp(1481825251155)", "2016-12-15T18:07:31.155Z")

    @Test
    fun ConvertReverse() = assertEval("timestamp_to_unixtime(unixtime_to_timestamp(1481825251155))", "1481825251155")
    
    @Test
    fun ConvertReverse2() = assertEval("unixtime_to_timestamp(timestamp_to_unixtime(`2016-12-15T18:07:31.155Z`))", "2016-12-15T18:07:31.155Z")
}
