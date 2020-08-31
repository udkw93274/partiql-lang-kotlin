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
import java.util.Base64
import java.security.MessageDigest
import com.amazon.ion.*

internal class StartsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("startswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val compareType = args[1].type
        val compareString = args[1].stringValue()

        return when {
            targetType != ExprValueType.STRING                 -> errNoContext("Argument 1 of startswith was not STRING.",
                                                                                internal = false)
            compareType != ExprValueType.STRING                -> errNoContext("Argument 2 of startswith was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newBoolean(targetString.startsWith(compareString))
        }
    }
}

internal class EndsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("endswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val compareType = args[1].type
        val compareString = args[1].stringValue()

        return when {
            targetType != ExprValueType.STRING                 -> errNoContext("Argument 1 of endswith was not STRING.",
                                                                                internal = false)
            compareType != ExprValueType.STRING                -> errNoContext("Argument 2 of endswith was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newBoolean(targetString.endsWith(compareString))
        }
    }
}

internal class IndexOfExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("indexof", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val compareType = args[1].type
        val compareString = args[1].stringValue()
        return when {
            targetType!= ExprValueType.STRING                  -> errNoContext("Argument 1 of indexof was not STRING.",
                                                                                internal = false)
            compareType != ExprValueType.STRING                -> errNoContext("Argument 2 of indexof was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newInt(targetString.indexOf(compareString))
        }
    }
}

internal class ReplaceExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("replace", 3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val findStringType = args[1].type
        val findString = args[1].stringValue()
        val replaceStringType = args[2].type
        val replaceString = args[2].stringValue()
        return when {
            targetType != ExprValueType.STRING                       -> errNoContext("Argument 1 of replace was not STRING.",
                                                                                internal = false)
            findStringType != ExprValueType.STRING                   -> errNoContext("Argument 2 of replace was not STRING.",
                                                                                internal = false)
            replaceStringType != ExprValueType.STRING                -> errNoContext("Argument 3 of replace was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newString(targetString.replace(findString, replaceString))
        } 
    }
}

internal class RegexpReplaceExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_replace", 3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val findStringType = args[1].type
        val findString = args[1].stringValue()
        val replaceStringType = args[2].type
        val replaceString = args[2].stringValue()
        return when {
            targetType != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_replace was not STRING.",
                                                                                internal = false)
            findStringType != ExprValueType.STRING                   -> errNoContext("Argument 2 of regexp_replace was not STRING.",
                                                                                internal = false)
            replaceStringType != ExprValueType.STRING                -> errNoContext("Argument 3 of regexp_replace was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newString(targetString.replace(findString.toRegex(), replaceString))
        } 
    }
}

internal class RegexpMatchesExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_matches", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val findStringType = args[1].type
        val findString = args[1].stringValue()

        return when {
            targetType != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_matches was not STRING.",
                                                                                internal = false)
            findStringType != ExprValueType.STRING                   -> errNoContext("Argument 2 of regexp_matches was not STRING.",
                                                                                internal = false)
            else -> {
                val regex = findString.toRegex()
                valueFactory.newBoolean(regex.containsMatchIn(targetString))
            }
        } 
    }
}

internal class RegexpSubstrExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_substr", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val findStringType = args[1].type
        val findString = args[1].stringValue()

        return when {
            targetType != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_substr was not STRING.",
                                                                                internal = false)
            findStringType != ExprValueType.STRING                   -> errNoContext("Argument 2 of regexp_substr was not STRING.",
                                                                                internal = false)
            else -> {
                val match = Regex(findString).find(targetString)!!
                valueFactory.newString(match.groupValues.get(0))
            }
        } 
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

internal class FlatListExprFunction(private val valueFactory: ExprValueFactory): ExprFunction {
    override val name = "flatlist"
    override fun call(env: Environment, args: List<ExprValue>): ExprValue {
        var list :MutableList<ExprValue> = mutableListOf<ExprValue>()

        for (i in args) {
            val valueType = i.type
            if (valueType == ExprValueType.LIST) {
                val ionContainer = i.ionValue as IonContainer
                val iter = ionContainer.iterator();

                while (iter.hasNext()){
                    list.add(valueFactory.newFromIonValue(iter.next()))
                }
            } else {
                list.add(i)
            }
            
        }
        return valueFactory.newList(list)
    }
}

internal class ListExprFunction(private val valueFactory: ExprValueFactory): ExprFunction {
    override val name = "list"
    override fun call(env: Environment, args: List<ExprValue>): ExprValue {
        var list :MutableList<ExprValue> = mutableListOf<ExprValue>()
        for (i in args) {
            list.add(i)
        }
        return valueFactory.newList(list)
    }
}

internal class NumbytesExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("numbytes", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of numbytes was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newInt(args[0].stringValue().toByteArray(Charsets.UTF_8).size)
        }
    }
}

internal class NewuuidExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("newuuid", 0, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val uuid: UUID? = UUID.randomUUID()
        return valueFactory.newString(uuid.toString())
    }
}

internal class LpadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("lpad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val padNumberType = args[1].type
        val padNumber = args[1].intValue()
        var padString = " "

        when {
            targetType != ExprValueType.STRING                      -> errNoContext("Argument 1 of lpad was not STRING.",
                                                                                internal = false)
            padNumberType != ExprValueType.INT                         -> errNoContext("Argument 2 of lpad was not INTEGER.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING    -> errNoContext("Argument 3 of lpad was not STRING.",
                                                                                internal = false)                                                                    
        }
        
        if (args.size == 3) {
            padString = args[2].stringValue()
        }

        var t = ""
        for (i in 1..padNumber){
            t+=padString
        }
        return valueFactory.newString(t+targetString)
    }
}

internal class RpadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("rpad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val padNumberType = args[1].type
        val padNumber = args[1].intValue()
        var padString = " "

        when {
            targetType != ExprValueType.STRING                -> errNoContext("Argument 1 of rpad was not STRING.",
                                                                                internal = false)
            padNumberType != ExprValueType.INT                -> errNoContext("Argument 2 of rpad was not INTEGER.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING                -> errNoContext("Argument 3 of rpad was not STRING.",
                                                                                internal = false)                                                                    
        }

        if (args.size == 3) {
            padString = args[2].stringValue()
        }

        var t = "";
        for (i in 1..padNumber){
            t+=padString
        }
        return valueFactory.newString(targetString+t)
    }
}

internal class ChrExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("chr", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT                -> errNoContext("Argument 1 of chr was not INTEGER.",
                                                                                internal = false)
            else -> valueFactory.newString(args[0].numberValue().toInt().toChar().toString())
        }
    }
}

internal class EncodeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("encode", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val encodeStringType = args[1].type
        val encodeString = args[1].stringValue().toUpperCase()

        return when {
            targetType != ExprValueType.STRING                      -> errNoContext("Argument 1 of encode was not STRING.",
                                                                                internal = false)
            encodeStringType != ExprValueType.STRING                -> errNoContext("Argument 2 of encode was not STRING.",
                                                                                internal = false)
            !(encodeString.equals("BASE64"))                        -> errNoContext("Argument 2 of encode must Base64.",
                                                                                internal = false)
            else -> valueFactory.newString(Base64.getEncoder().encodeToString(targetString.toByteArray()))
        }
    }
}

internal class DecodeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("decode", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val decodeStringType = args[1].type
        val decodeString = args[1].stringValue().toUpperCase()

        return when {
            targetType != ExprValueType.STRING                      -> errNoContext("Argument 1 of decode was not STRING.",
                                                                                internal = false)
            decodeStringType != ExprValueType.STRING                -> errNoContext("Argument 2 of decode was not STRING.",
                                                                                internal = false)
            !(decodeString.equals("BASE64"))                        -> errNoContext("Argument 2 of decode must Base64.",
                                                                                internal = false)
            else -> valueFactory.newString(String(Base64.getDecoder().decode(targetString)))
        }
    }
}

internal class EncryptExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("encrypt", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val targetType = args[0].type
        val targetString = args[0].stringValue()
        val encryptStringType = args[1].type
        val encryptString = args[1].stringValue().toUpperCase()
        
        return when {
            targetType != ExprValueType.STRING                      -> errNoContext("Argument 1 of encrypt was not STRING.",
                                                                                internal = false)
            encryptStringType != ExprValueType.STRING                -> errNoContext("Argument 2 of encrypt was not STRING.",
                                                                                internal = false)
            !(encryptString.equals("MD2") || encryptString.equals("MD5") || encryptString.equals("SHA-1") || encryptString.equals("SHA-224")
            || encryptString.equals("SHA-256") || encryptString.equals("SHA-384") || encryptString.equals("SHA-512"))                        -> errNoContext("Argument 2 of encrypt invalid encrypt type.",
                                                                                internal = false)
            else -> valueFactory.newString(hashString(targetString, encryptString))
        }
    }
}

private fun hashString(targetString : String, encryptType: String): String {
    val HEX_CHARS = "0123456789abcdef"
    val bytes = MessageDigest
            .getInstance(encryptType)
            .digest(targetString.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}
