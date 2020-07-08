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
import kotlin.math.*

internal class AbsExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("abs", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT && firstType != ExprValueType.FLOAT
              -> errNoContext("Argument 1 of abs was not Number.", internal = false)
        }
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> valueFactory.newInt(abs(firstValue.toInt()))
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(abs(firstValue.toDouble()))
            else -> errNoContext("Argument 1 of abs was not Number.", internal = false)
        }
    }
}

internal class SignExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sign", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT && firstType != ExprValueType.FLOAT
              -> errNoContext("Argument 1 of sign was not Number.", internal = false)
        }
        val firstValue = args[0].numberValue()
        return  valueFactory.newInt(sign(firstValue.toDouble()).toInt())
    }
}

internal class CeilExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ceil", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT && firstType != ExprValueType.FLOAT
              -> errNoContext("Argument 1 of ceil was not Number.", internal = false)
        }
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> valueFactory.newInt(ceil(firstValue.toDouble()).toInt())
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(ceil(firstValue.toDouble()).toInt())
            else -> errNoContext("Argument 1 of ceil was not Number.", internal = false)
        }
    }
}

internal class FloorExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("floor", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]

        return when (firstValue.type) {
            ExprValueType.INT -> firstValue
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(floor(firstValue.numberValue().toDouble()).toInt())
            else -> errNoContext("Argument 1 of floor was not Number.", internal = false)
        }
    }
}

internal class RoundExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("round", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT && firstType != ExprValueType.FLOAT
              -> errNoContext("Argument 1 of round was not Number.", internal = false)
        }
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> valueFactory.newInt(firstValue.toDouble().roundToInt())
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(firstValue.toDouble().roundToInt())
            else -> errNoContext("Argument 1 of round was not Number.", internal = false)
        }
    }
}

internal class TruncExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("trunc", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (firstValue, indicator) = extractArguments(args)
        val divider = Math.pow(10.0, indicator)

        return when (firstValue.type) {
            ExprValueType.INT -> firstValue
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(floor(firstValue.numberValue().toDouble()*divider)/divider)
            else -> errNoContext("Argument 1 of trunc was not Number.", internal = false)
        }
    }

    private fun extractArguments(args: List<ExprValue>): Pair<ExprValue, Double> {
        val firstType = args[0].type
        when {
            firstType != ExprValueType.DECIMAL &&  firstType != ExprValueType.INT && firstType != ExprValueType.FLOAT
              -> errNoContext("Argument 1 of trunc was not Number.", internal = false)
            args[1].type != ExprValueType.INT && args[1].intValue() < 0  -> errNoContext("Argument 2 of substring was not INT or small than 0.",
                                                                                internal = false)
        }
    
        return Pair(args[0], args[1].intValue().toDouble())
    }
}

internal class ModExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("mod", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val (target, divider) = extractArguments(args)
        return valueFactory.newInt(target % divider)
    }

    private fun extractArguments(args: List<ExprValue>): Pair<Int, Int> {
        when {
            args[0].type != ExprValueType.INT                   -> errNoContext("Argument 1 of mod was not INT.",
                                                                                internal = false)
            args[1].type != ExprValueType.INT                   -> errNoContext("Argument 2 of mod was not INT.",
                                                                                internal = false)
        }
    
        val target = args[0].intValue()
        val divider = args[1].intValue()
    
        if (divider == 0) {
            errNoContext("Argument 2 must not be 0.", internal = false)
        }
    
        return Pair(target, divider)
    }
}

internal class SqrtExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sqrt", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]
        return when (firstValue.type) {
            ExprValueType.INT -> valueFactory.newFloat(sqrt(firstValue.intValue().toDouble()))
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(sqrt(firstValue.numberValue().toDouble()))
            else -> errNoContext("Argument 1 of sqrt was not Number.", internal = false)
        }
    }
}

internal class PowerExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("power", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]
        val secondValue = args[1]
        return when {
            firstValue.type.isNumber == false -> errNoContext("Argument 1 of power was not Number.", internal = false)
            secondValue.type.isNumber == false -> errNoContext("Argument 2 of power was not Number.", internal = false)
            else -> valueFactory.newFloat(Math.pow(firstValue.numberValue().toDouble(), secondValue.numberValue().toDouble()))
        }
    }
}

internal class LogExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("log", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]
        val secondValue = args[1]
        return when {
            firstValue.type.isNumber == false -> errNoContext("Argument 1 of log was not Number.", internal = false)
            secondValue.type.isNumber == false -> errNoContext("Argument 2 of log was not Number.", internal = false)
            else -> valueFactory.newFloat(log(firstValue.numberValue().toDouble(), secondValue.numberValue().toDouble()))
        }
    }
}

internal class LnExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ln", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]
        return when (firstValue.type) {
            ExprValueType.INT -> valueFactory.newFloat(ln(firstValue.intValue().toDouble()))
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(ln(firstValue.numberValue().toDouble()))
            else -> errNoContext("Argument 1 of ln was not Number.", internal = false)
        }
    }
}

internal class ExpExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("exp", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstValue = args[0]
        return when (firstValue.type) {
            ExprValueType.INT -> valueFactory.newFloat(exp(firstValue.intValue().toDouble()))
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(exp(firstValue.numberValue().toDouble()))
            else -> errNoContext("Argument 1 of exp was not Number.", internal = false)
        }
    }
}

internal class NanvlExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("nanvl", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        if (args[0].type.isNumber == true) {
          return args[0]
        } else {
          return args[1]
        }
    }
}

internal class RandExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("rand", 0, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return valueFactory.newFloat(Math.random())
    }
}