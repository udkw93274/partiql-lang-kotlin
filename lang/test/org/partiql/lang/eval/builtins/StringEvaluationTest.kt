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
    fun regexpReplace() = assertEval("regexp_replace('abcd', 'b(.*)d', '$1')", "\"ac\"")

    @Test
    fun regexpMatches() = assertEval("regexp_matches('aaaa', 'a{2,}')", "true")

    @Test
    fun regexpMatchesFalse() = assertEval("regexp_matches('aaaa', 'b')", "false")

    @Test
    fun regexpSubstr() = assertEval("regexp_substr('hihihello', '(hi)*')", "\"hihi\"")

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
    fun lpad() = assertEval("lpad('abc', 2)", "\"  abc\"")

    @Test
    fun rpad() = assertEval("rpad('abc', 2, 'cd')", "\"abccdcd\"")

    @Test
    fun chr() = assertEval("chr(65)", "\"A\"")

    @Test
    fun encodeBase64() = assertEval("encode('iot', 'Base64')", "\"aW90\"")

    @Test
    fun decodeBase64() = assertEval("decode('aW90', 'Base64')", "\"iot\"")

    @Test
    fun encryptMD2() = assertEval("encrypt('iotgood', 'md2')", "\"1e58de9fa2048bbb18eae18b6318e14e\"")

    @Test
    fun encryptMD5() = assertEval("encrypt('iotgood', 'md5')", "\"90eb9fce1b753ae47082bc4c98bff5e9\"")

    @Test
    fun encryptSHA1() = assertEval("encrypt('iotgood', 'sha-1')", "\"edcf3d81bd181b9b37bc9748766b16345f7fb405\"")
    
    @Test
    fun encryptSHA384() = assertEval("encrypt('iotgood', 'sha-384')", "\"066805413173b9a06289ca6a28896e7985645a2a132e8c902e448cb214bf2c13636f9c4b724b71cb2100da2a92e807d3\"")

    @Test
    fun encryptSHA224() = assertEval("encrypt('iotgood', 'sha-224')", "\"5ea7f4c4762b634c40b463f2bed08b2861230a19490a6546c3f18246\"")
    
    @Test
    fun encryptSHA256() = assertEval("encrypt('iotgood', 'sha-256')", "\"9cc6ba5766c4eb622c93e09b7c217046ee81654b1c0c78a53f1472956731f8a7\"")

    @Test
    fun encryptSHA512() = assertEval("encrypt('iotgood', 'sha-512')", "\"0dadf7a8b9f6256de3c0fe91f5790bc8aeab9991f19a39344149c2f3ce55e9795a1767fb2ab8c1f4099fcf812f6b8ab4023df0b30fd7cc42364f0bc6db3e3143\"")

    @Test
    fun getitemList() = assertEval("get_item(`[1, 2, 3]`, 1)", "2")
    
    @Test
    fun getitemListString() = assertEval("get_item(`[1, \"2\", 3]`, 1)", "\"2\"")
    
    @Test
    fun getitemKeyValue() = assertEval("get_item(`{foo: \"bar\", a:\"b\"}`, 'foo')", "\"bar\"")
    
    @Test
    fun getitemString() = assertEval("get_item('abcde', 2)", "\"c\"")

    @Test
    fun flatList() = assertEval("flatlist(1, true, `{foo: \"bar\"}`, null, 'a', [1,2,3])", "[1, true, {foo: \"bar\"}, null, \"a\",1,2,3]")
    
    @Test
    fun list() = assertEval("list(1, true, `{foo: \"bar\"}`, null, 'a', [1,2,3])", "[1, true, {foo: \"bar\"}, null, \"a\",[1,2,3]]")
}
