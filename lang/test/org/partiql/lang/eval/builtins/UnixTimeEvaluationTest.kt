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
    fun UnixtimeToTimeString() = assertEval("unixtime_to_string(1594579192000, 'yyyy MM dd hh:mm:ss z', 'Asia/Seoul')", "\"2020 07 13 03:39:52 KST\"")

    @Test
    fun UnixtimeToTimeStringUTC() = assertEval("unixtime_to_string(1594579192000, 'hh:mm:ss z')", "\"06:39:52 UTC\"")

    @Test
    fun TimeStringToUnixtime() = assertEval("string_to_unixtime('2020 07 13 03:39:52 KST', 'yyyy MM dd hh:mm:ss z', 'Asia/Seoul')", "1594579192000")

    @Test
    fun TimeStringToUnixtimeUTC() = assertEval("string_to_unixtime('2020 07 13 06:39:52.222', 'yyyy MM dd hh:mm:ss.SSS')", "1594622392222")

    @Test
    fun Extract() = assertEval("extract(MINUTE FROM 1594579192031)", "39")

    @Test
    fun DateDIFF() = assertEval("DIFF_TIME(SECOND, 1594579192031, 1594579198031)", "6")

    @Test
    fun DateDIFF2() = assertEval("DIFF_TIME(MINUTE, 1594579992031, 1594579198031)", "-13")

    @Test
    fun Add() = assertEval("ADD_TIME(minute, -10, 1594579198031)", "1594578598031")
        
    @Test
    fun isnull() = assertEval("isnull('a')", "false")

    @Test
    fun isnullNotNull() = assertEval("isnull(NULL)", "true")
}
