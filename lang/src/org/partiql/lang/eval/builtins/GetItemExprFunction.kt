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

import com.amazon.ion.*
import org.partiql.lang.errors.*
import org.partiql.lang.eval.*
import org.partiql.lang.util.*

/**
 * Built in function to return the size of a container type, i.e. size of Lists, Structs and Bags. This function
 * propagates null and missing values as described in docs/Functions.md
 *
 * syntax: `size(<container>)` where container can be a BAG, STRUCT or LIST.
 */
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
                    errNoContext("Argument 2 of get_item was not INTEGER.", internal = false)
                }  
            }

            ExprValueType.STRING -> {
                val index = indexValue.intValue();
                if (indexValue.type == ExprValueType.INT) {
                    val stringValue = collection.stringValue()
                    valueFactory.newString(stringValue.get(index).toString())
                } else {
                    errNoContext("Argument 2 of get_item was not INTEGER.", internal = false)
                }  
            }

            else               ->{
                val errorContext = PropertyValueMap()
                errorContext[Property.EXPECTED_ARGUMENT_TYPES] = "LIST or BAG or STRUCT"
                errorContext[Property.ACTUAL_ARGUMENT_TYPES] = collection.type.name
                errorContext[Property.FUNCTION_NAME] = "size"

                err(message = "invalid argument type for size",
                    errorCode = ErrorCode.EVALUATOR_INCORRECT_TYPE_OF_ARGUMENTS_TO_FUNC_CALL,
                    errorContext = errorContext,
                    internal = false)
            }
        }
    }
}
