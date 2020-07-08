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

class AbsEvaluationTest : EvaluatorTestBase() {
    @Test
    fun absInteger() = assertEval("abs(-1)", "1")
    
    @Test
    fun absFloat() = assertEval("ABS(-0.1)", "0.1e0")

    @Test
    fun signNegative() = assertEval("sign(-0.1)", "-1")
    
    @Test
    fun signZero() = assertEval("sign(0.0)", "0")

    @Test
    fun signPositive() = assertEval("sign(10)", "1")

    @Test
    fun ceilInteger() = assertEval("ceil(10)", "10")

    @Test
    fun ceilFloat() = assertEval("ceil(0.1)", "1")

    @Test
    fun floorFloat() = assertEval("floor(1.234567890)", "1")

    @Test
    fun trunc() = assertEval("trunc(1.23, 1)", "1.2e0")

    @Test
    fun trunc2() = assertEval("trunc(1.234567890, 0)", "1e0")

    @Test
    fun trunc3() = assertEval("trunc(2.00, 5)", "2e0")

    @Test
    fun roundFloat() = assertEval("round(0.5)", "1")

    @Test
    fun mod() = assertEval("mod(10, 3)", "1")

    @Test
    fun nanvl1() = assertEval("nanvl(1.1, 3)", "1.1")

    @Test
    fun nanvl2() = assertEval("nanvl('s', 3)", "3")
    
    @Test
    fun sqrt() = assertEval("sqrt(9)", "3e0")

    @Test
    fun ln() = assertEval("ln(10)", "2.302585092994046e0")

    @Test
    fun exp() = assertEval("exp(0)", "1e0")

    @Test
    fun power() = assertEval("power(10, 2)", "100e0")

    @Test
    fun log() = assertEval("log(100, 10)", "2e0")



    @Test
    fun startsWith() = assertEval("startswith('abcd', 'ab')", "true")

    @Test
    fun endsWith() = assertEval("endswith('abcd', 'cd')", "true")

    @Test
    fun indexOf() = assertEval("indexof('abcd', 'bc')", "1")

    @Test
    fun replace() = assertEval("replace('abcb', 'b', 'xx')", "\"axxcxx\"")

    @Test
    fun numbytes() = assertEval("numbytes('abcb')", "4")
}
