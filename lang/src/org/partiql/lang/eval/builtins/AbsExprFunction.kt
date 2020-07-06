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
import kotlin.math.abs

internal class AbsExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("abs", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return extractArguments(args)
    }

    private fun extractArguments(args: List<ExprValue>): ExprValue {
        // type check
        val firstType = args[0].type
        println(firstType)
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT &&
                    firstType != ExprValueType.FLOAT  -> errNoContext("Argument 1 of abs was not Number.",
                                                                                internal = false)
        }
        val value = args[0].numberValue()
        return when (firstType) {
            ExprValueType.INT -> valueFactory.newInt(abs(value.toInt()))
            ExprValueType.DECIMAL -> valueFactory.newFloat(abs(value.toDouble()))
            ExprValueType.FLOAT -> valueFactory.newFloat(abs(value.toDouble()))
            else -> errNoContext("Argument 1 of abs was not Number.", internal = false)
        }
    }
}