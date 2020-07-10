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

import org.partiql.lang.eval.*
import java.util.UUID

internal class StartsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("startswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (targetString, compareString) = extractArguments(args)
        if (targetString.startsWith(compareString)){
            return valueFactory.newBoolean(true)
        } else {
            return valueFactory.newBoolean(false)
        }
    }

    private fun extractArguments(args: List<ExprValue>): Pair<String, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of startswith was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                -> errNoContext("Argument 2 of startswith was not STRING.",
                                                                                internal = false)
        }
    
        return Pair(args[0].stringValue(), args[1].stringValue())
    }
}

internal class EndsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("endswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (targetString, compareString) = extractArguments(args)
        if (targetString.endsWith(compareString)){
            return valueFactory.newBoolean(true)
        } else {
            return valueFactory.newBoolean(false)
        }
    }

    private fun extractArguments(args: List<ExprValue>): Pair<String, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of endswith was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                -> errNoContext("Argument 2 of endswith was not STRING.",
                                                                                internal = false)
        }
    
        return Pair(args[0].stringValue(), args[1].stringValue())
    }
}

internal class IndexOfExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("indexof", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (targetString, compareString) = extractArguments(args)
        return valueFactory.newInt(targetString.indexOf(compareString))
    }

    private fun extractArguments(args: List<ExprValue>): Pair<String, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of indexof was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                -> errNoContext("Argument 2 of indexof was not STRING.",
                                                                                internal = false)
        }
    
        return Pair(args[0].stringValue(), args[1].stringValue())
    }
}

internal class ReplaceExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("replace", 3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (targetString, findString, replaceString) = extractArguments(args)
        return valueFactory.newString(targetString.replace(findString, replaceString))
    }

    private fun extractArguments(args: List<ExprValue>): Triple<String, String, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of replace was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                -> errNoContext("Argument 2 of replace was not STRING.",
                                                                                internal = false)
            args[2].type != ExprValueType.STRING                -> errNoContext("Argument 3 of replace was not STRING.",
                                                                                internal = false)
        }
    
        return Triple(args[0].stringValue(), args[1].stringValue(), args[2].stringValue())
    }
}

internal class ConcatExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("concat", (2..5), valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        var result:String = "";
        var index = 0;
        for (i in args) {
            index++;
            if (i.type != ExprValueType.STRING) {
                errNoContext("Argument "+index+" of concat was not STRING.",
                                                                                internal = false)
            }
            result+=i.stringValue();
        }
        return valueFactory.newString(result)
    }
}

internal class NumbytesExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("numbytes", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val inputString = extractArguments(args)
        val byteArray = inputString.toByteArray(Charsets.UTF_8)
        return valueFactory.newInt(byteArray.size)
    }

    private fun extractArguments(args: List<ExprValue>): String {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of numbytes was not STRING.",
                                                                                internal = false)
        }
        return args[0].stringValue()
    }
}

internal class NewuuidExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("newuuid", 0, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        var uuid: UUID? = UUID.randomUUID()
        return valueFactory.newString(uuid.toString())
    }
}