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

class MathEvaluationTest : EvaluatorTestBase() {
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
    fun floorInteger() = assertEval("floor(1)", "1")

    @Test
    fun floorFloat() = assertEval("floor(1.234567890)", "1")

    @Test
    fun roundInteger() = assertEval("round(5)", "5")
    
    @Test
    fun roundFloatUp() = assertEval("round(0.5)", "1")
    
    @Test
    fun roundFloatDown() = assertEval("round(0.4)", "0")

    @Test
    fun truncNormal() = assertEval("trunc(1.23, 1)", "1.2e0")

    @Test
    fun truncZero() = assertEval("trunc(1.234567890, 0)", "1e0")

    @Test
    fun truncFloat() = assertEval("trunc(2.00, 5)", "2e0")

    @Test
    fun truncSecondFloat() = assertEval("trunc(2.12, 1)", "2.1e0")

    @Test
    fun truncSecondNegative() = assertEval("trunc(2.12, -1)", "2e0")
    
    @Test
    fun truncInt() = assertEval("trunc(2, 5)", "2")

    @Test
    fun mod() = assertEval("mod(10, 3)", "1")

    @Test
    fun sqrt() = assertEval("sqrt(9)", "3e0")
    
    @Test
    fun sqrtFloat() = assertEval("sqrt(1.44)", "1.2e0")

    @Test
    fun power() = assertEval("power(10, 2)", "100e0")

    @Test
    fun powerNegative() = assertEval("power(10, -1)", "0.1e0")

    @Test
    fun logTarget0() = assertEval("log(0, 100)", "\"-inf\"")

    @Test
    fun nanvllog() = assertEval("nanvl(log(0,100), 3)", "3")

    @Test
    fun logTarget1() = assertEval("log(1, 100)", "0e0")

    @Test
    fun logTargetMinus() = assertEval("log(-1, 100)", "\"nan\"")

    @Test
    fun logBase0() = assertEval("log(10, 0)", "\"nan\"")

    @Test
    fun logBase1() = assertEval("log(10, 0)", "\"nan\"")
    
    @Test
    fun logBaseMinus() = assertEval("log(10, -2)", "\"nan\"")

    @Test
    fun exp() = assertEval("exp(0)", "1e0")
    
    @Test
    fun ln() = assertEval("ln(10)", "2.302585092994046e0")
    
    @Test
    fun lnMinus() = assertEval("ln(-1)", "\"nan\"")
    
    @Test
    fun ln1() = assertEval("ln(1)", "0e0")

    @Test
    fun ln0() = assertEval("ln(0)", "\"-inf\"")

    @Test
    fun nanvlln() = assertEval("nanvl(ln(-1), 3)", "3")

    @Test
    fun nanvlNumber() = assertEval("nanvl(1.1, 3)", "1.1")

    @Test
    fun nanvlNAN() = assertEval("nanvl('s', 3)", "3")

    @Test
    fun randomRange() = assertEval("(rand() <= 1) and (rand() >= 0)", "true")

    @Test
    fun randomNotSame() = assertEval("rand() != rand()", "true")
    
    @Test
    fun bitor() = assertEval("bitor(8,5)", "13")

    @Test
    fun bitand() = assertEval("bitand(13, 5)", "5")

    @Test
    fun bitxor() = assertEval("bitxor(13,5)", "8")

    @Test
    fun sin() = assertEval("sin(1)", "0.8414709848078965e0")

    @Test
    fun cosh() = assertEval("cosh(100)", "1.3440585709080678E43")
}
