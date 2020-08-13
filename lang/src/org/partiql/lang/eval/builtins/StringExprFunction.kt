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

internal class ConcatExprFunction(private val valueFactory: ExprValueFactory): ExprFunction {
    override val name = "concat"
    override fun call(env: Environment, args: List<ExprValue>): ExprValue {
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

internal class LtrimExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("ltrim", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {

        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of ltrim was not STRING.",
                                                                                internal = false)
        }
        return valueFactory.newString(args[0].stringValue().trimStart())
    }
}

internal class RtrimExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("rtrim", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {

        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of rtrim was not STRING.",
                                                                                internal = false)
        }
        return valueFactory.newString(args[0].stringValue().trimEnd())
    }
}

internal class LpadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("lpad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        var (targetString, padNumber, padString) = extractArguments(args)
        var tempString = ""
        for (i in 1..padNumber){
            tempString+=padString
        }
        return valueFactory.newString(tempString+targetString)
    }

    private fun extractArguments(args: List<ExprValue>): Triple<String, Int, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of lpad was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.INT                -> errNoContext("Argument 2 of lpad was not INTEGER.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING                -> errNoContext("Argument 3 of lpad was not STRING.",
                                                                                internal = false)                                                                    
        }
        if (args.size == 3){
            return Triple(args[0].stringValue(), args[1].intValue(), args[2].stringValue())
        } else {
            return Triple(args[0].stringValue(), args[1].intValue(), " ")
        }
    }
}

internal class RpadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("rpad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        var (targetString, padNumber, padString) = extractArguments(args)
        var tempString = ""
        for (i in 1..padNumber){
            tempString+=padString
        }
        return valueFactory.newString(targetString+tempString)
    }
    private fun extractArguments(args: List<ExprValue>): Triple<String, Int, String> {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of rpad was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.INT                -> errNoContext("Argument 2 of rpad was not INTEGER.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING                -> errNoContext("Argument 3 of rpad was not STRING.",
                                                                                internal = false)                                                                    
        }
        if (args.size == 3){
            return Triple(args[0].stringValue(), args[1].intValue(), args[2].stringValue())
        } else {
            return Triple(args[0].stringValue(), args[1].intValue(), " ")
        }
    }
}