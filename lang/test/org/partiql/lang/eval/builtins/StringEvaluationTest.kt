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

class StringEvaluationTest : EvaluatorTestBase() {
    @Test
    fun startsWith() = assertEval("startswith('abcd', 'ab')", "true")
    
    @Test
    fun startsWithFalse() = assertEval("startswith('abcd', 'cd')", "false")

    @Test
    fun endsWith() = assertEval("endswith('abcd', 'cd')", "true")

    @Test
    fun endsWithFalse() = assertEval("endswith('abcd', 'ab')", "false")

    @Test
    fun indexOf() = assertEval("indexof('abcd', 'bc')", "1")

    @Test
    fun indexOfNone() = assertEval("indexof('abcd', 'xx')", "-1")

    @Test
    fun replace() = assertEval("replace('abcb', 'b', 'xx')", "\"axxcxx\"")

    @Test
    fun concat() = assertEval("concat('aa', 'bb', 'cc', 'dd')", "\"aabbccdd\"")

    @Test
    fun numbytes() = assertEval("numbytes('abcb')", "4")

    @Test
    fun numbytesSpecial() = assertEval("numbytes('∀')", "3")
    
    @Test
    fun numbytesNone() = assertEval("numbytes('')", "0")

    @Test
    fun uuidLength() = assertEval("char_length(newuuid())", "36")
    
    @Test
    fun uuidNotSame() = assertEval("newuuid() != newuuid()", "true")

    @Test
    fun ltrim() = assertEval("ltrim('  abc  ')", "\"abc  \"")

    @Test
    fun rtrim() = assertEval("rtrim('  abc  ')", "\"  abc\"")

    @Test
    fun lpad() = assertEval("lpad('abc', 2)", "\"  abc\"")

    @Test
    fun rpad() = assertEval("rpad('abc', 2, 'cd')", "\"abccdcd\"")

    @Test
    fun getitemList() = assertEval("get_item(`[1, 2, 3]`, 1)", "2")
    
    @Test
    fun getitemListString() = assertEval("get_item(`[1, \"2\", 3]`, 1)", "\"2\"")
    
    @Test
    fun getitemKeyValue() = assertEval("get_item(`{foo: \"bar\", a:\"b\"}`, 'foo')", "\"bar\"")
    
    @Test
    fun getitemString() = assertEval("get_item('abcde', 2)", "\"c\"")
}
