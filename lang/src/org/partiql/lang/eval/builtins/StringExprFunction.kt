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
import org.partiql.lang.errors.*
import org.partiql.lang.util.*
import java.util.UUID
import java.util.Base64
import java.security.MessageDigest
import com.amazon.ion.*

internal class StartsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("startswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                 -> errNoContext("Argument 1 of startswith was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                 -> errNoContext("Argument 2 of startswith was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newBoolean(args[0].stringValue().startsWith(args[1].stringValue()))
        }
    }
}

internal class EndsWithExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("endswith", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                 -> errNoContext("Argument 1 of endswith was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                 -> errNoContext("Argument 2 of endswith was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newBoolean(args[0].stringValue().endsWith(args[1].stringValue()))
        }
    }
}

internal class IndexOfExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("indexof", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type!= ExprValueType.STRING                  -> errNoContext("Argument 1 of indexof was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                 -> errNoContext("Argument 2 of indexof was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newInt(args[0].stringValue().indexOf(args[1].stringValue()))
        }
    }
}

internal class ReplaceExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("replace", 3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                       -> errNoContext("Argument 1 of replace was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                       -> errNoContext("Argument 2 of replace was not STRING.",
                                                                                internal = false)
            args[2].type != ExprValueType.STRING                       -> errNoContext("Argument 3 of replace was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newString(args[0].stringValue().replace(args[1].stringValue(), args[2].stringValue()))
        } 
    }
}

internal class RegexpReplaceExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_replace", 3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_replace was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                       -> errNoContext("Argument 2 of regexp_replace was not STRING.",
                                                                                internal = false)
            args[2].type != ExprValueType.STRING                       -> errNoContext("Argument 3 of regexp_replace was not STRING.",
                                                                                internal = false)
            else -> valueFactory.newString(args[0].stringValue().replace(args[1].stringValue().toRegex(), args[2].stringValue()))
        } 
    }
}

internal class RegexpMatchesExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_matches", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_matches was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                       -> errNoContext("Argument 2 of regexp_matches was not STRING.",
                                                                                internal = false)
            else -> {
                val regex = args[1].stringValue().toRegex()
                valueFactory.newBoolean(regex.containsMatchIn(args[0].stringValue()))
            }
        } 
    }
}

internal class RegexpSubstrExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("regexp_substr", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                       -> errNoContext("Argument 1 of regexp_substr was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                       -> errNoContext("Argument 2 of regexp_substr was not STRING.",
                                                                                internal = false)
            else -> {
                val match = Regex(args[1].stringValue()).find(args[0].stringValue())!!
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
                errNoContext("Argument "+index+" of concat was not STRING.",  internal = false)
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
        for (i in args) { list.add(i) }
        
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
        return valueFactory.newString(UUID.randomUUID().toString())
    }
}

internal class LeadingPadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("leading_pad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        when {
            args[0].type != ExprValueType.STRING                      -> errNoContext("Argument 1 of leading_pad was not STRING.",
                                                                                internal = false)
            args[1].type!= ExprValueType.INT                          -> errNoContext("Argument 2 of leading_pad was not INT.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING    -> errNoContext("Argument 3 of leading_pad was not STRING.",
                                                                                internal = false)                                                                    
        }
        
        var padString = " "

        if (args.size == 3) {
            padString = args[2].stringValue()
        }

        var t = ""
        for (i in 1..args[1].intValue()){
            t+=padString
        }
        return valueFactory.newString(t+args[0].stringValue())
    }
}

internal class TrailingPadExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("trailing_pad", 2..3, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        when {
            args[0].type != ExprValueType.STRING                -> errNoContext("Argument 1 of trailing_pad was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.INT                   -> errNoContext("Argument 2 of trailing_pad was not INT.",
                                                                                internal = false)
            args.size == 3 && args[2].type != ExprValueType.STRING                -> errNoContext("Argument 3 of trailing_pad was not STRING.",
                                                                                internal = false)                                                                    
        }
        var padString = " "

        if (args.size == 3) {
            padString = args[2].stringValue()
        }

        var t = "";
        for (i in 1..args[1].intValue()){
            t+=padString
        }
        return valueFactory.newString(args[0].stringValue()+t)
    }
}

internal class ChrExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("chr", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT                -> errNoContext("Argument 1 of chr was not INT.",
                                                                                internal = false)
            else -> valueFactory.newString(args[0].numberValue().toInt().toChar().toString())
        }
    }
}

internal class GetItemExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("get_item", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val collection = args.first()
        val indexValue = args[1]
        
        return when (collection.type) {
            ExprValueType.STRUCT -> {
                if (indexValue.type == ExprValueType.STRING) {
                    val ionStruct = collection.ionValue as IonStruct
                    valueFactory.newFromIonValue(ionStruct[indexValue.stringValue()])
                } else {
                    errNoContext("Argument 2 of get_item was not STRING.", internal = false)
                }  
            } 

            ExprValueType.LIST -> {
                if (indexValue.type == ExprValueType.INT) {
                    val ionContainer = collection.ionValue as IonContainer
                    val iter = ionContainer.iterator();
                    val index = indexValue.intValue();
                    var counter = 0;

                    while (iter.hasNext()){
                        if (counter == index) {
                            break;
                        } else {
                            iter.next();
                            counter++
                        }
                    }
                    valueFactory.newFromIonValue(iter.next())
                } else {
                    errNoContext("Argument 2 of get_item was not INT.", internal = false)
                }  
            }

            ExprValueType.STRING -> {
                val index = indexValue.intValue();
                if (indexValue.type == ExprValueType.INT) {
                    val stringValue = collection.stringValue()
                    valueFactory.newString(stringValue.get(index).toString())
                } else {
                    errNoContext("Argument 2 of get_item was not INT.", internal = false)
                }  
            }

            else               -> {
                val errorContext = PropertyValueMap()
                errorContext[Property.EXPECTED_ARGUMENT_TYPES] = "LIST or BAG or STRUCT"
                errorContext[Property.ACTUAL_ARGUMENT_TYPES] = collection.type.name
                errorContext[Property.FUNCTION_NAME] = "get_item"

                err(message = "invalid argument type for get_item",
                    errorCode = ErrorCode.EVALUATOR_INCORRECT_TYPE_OF_ARGUMENTS_TO_FUNC_CALL,
                    errorContext = errorContext,
                    internal = false)
            }
        }
    }
}

internal class EncodeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("encode", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                            -> errNoContext("Argument 1 of encode was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                            -> errNoContext("Argument 2 of encode was not STRING.",
                                                                                internal = false)
            args[1].stringValue().toUpperCase().equals("BASE64") == false   -> errNoContext("Argument 2 of encode must Base64.",
                                                                                internal = false)
            else -> valueFactory.newString(Base64.getEncoder().encodeToString(args[0].stringValue().toByteArray()))
        }
    }
}

internal class DecodeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("decode", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.STRING                            -> errNoContext("Argument 1 of decode was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                            -> errNoContext("Argument 2 of decode was not STRING.",
                                                                                internal = false)
            args[1].stringValue().toUpperCase().equals("BASE64") == false   -> errNoContext("Argument 2 of decode must Base64.",
                                                                                internal = false)
            else -> valueFactory.newString(String(Base64.getDecoder().decode(args[0].stringValue())))
        }
    }
}

internal class HashExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("hash", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        when {
            args[0].type != ExprValueType.STRING                      -> errNoContext("Argument 1 of hash was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                      -> errNoContext("Argument 2 of hash was not STRING.",
                                                                                internal = false)
        }

        val hashType = args[1].stringValue().toUpperCase()

        return when {
            (hashType.equals("MD2") || hashType.equals("MD5") || hashType.equals("SHA-1") || hashType.equals("SHA-224")
            || hashType.equals("SHA-256") || hashType.equals("SHA-384") || hashType.equals("SHA-512"))  == false 
                                  -> errNoContext("Argument 2 of encrypt invalid encrypt type.", internal = false)
            else -> valueFactory.newString(hashString(args[0].stringValue(), hashType))
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
