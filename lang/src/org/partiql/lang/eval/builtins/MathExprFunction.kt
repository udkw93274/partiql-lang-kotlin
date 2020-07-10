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
        val firstValue = args[0].numberValue()

        when {
            firstType.isNumber == false -> errNoContext("Argument 1 of sign was not Number.", internal = false)
        }
        return  valueFactory.newInt(sign(firstValue.toDouble()).toInt())
    }
}

internal class CeilExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ceil", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(ceil(firstValue.toDouble()).toInt())
            else -> errNoContext("Argument 1 of ceil was not Number.", internal = false)
        }
    }
}

internal class FloorExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("floor", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(floor(firstValue.toDouble()).toInt())
            else -> errNoContext("Argument 1 of floor was not Number.", internal = false)
        }
    }
}

internal class RoundExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("round", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when (firstType) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(firstValue.toDouble().roundToInt())
            else -> errNoContext("Argument 1 of round was not Number.", internal = false)
        }
    }
}

internal class TruncExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("trunc", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        val secondType = args[1].type
        val secondValue = args[1].numberValue()

        when {
            firstType.isNumber == false                   -> errNoContext("Argument 1 of trunc was not Number.",
                                                                                internal = false)
            secondType.isNumber == false                  -> errNoContext("Argument 2 of trunc was not Number.",
                                                                                internal = false)
        }

        val divider = Math.pow(10.0, secondValue.toDouble())

        return when (firstType) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(floor(firstValue.toDouble() * divider)/divider)
            else -> errNoContext("Argument 1 of trunc was not Number.", internal = false)
        }
    }
}

internal class ModExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("mod", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        val secondType = args[1].type
        val secondValue = args[1].numberValue()

        when {
            firstType != ExprValueType.INT                   -> errNoContext("Argument 1 of mod was not INT.",
                                                                                internal = false)
            secondType!= ExprValueType.INT                   -> errNoContext("Argument 2 of mod was not INT.",
                                                                                internal = false)
            secondValue.toInt() == 0                         -> errNoContext("Argument 2 must not be 0.",
                                                                                internal = false)
        }
        return valueFactory.newInt(firstValue.toInt() % secondValue.toInt())
    }
}

internal class SqrtExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sqrt", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when {
            firstType.isNumber -> valueFactory.newFloat(sqrt(firstValue.toDouble()))
            else -> errNoContext("Argument 1 of sqrt was not Number.", internal = false)
        }
    }
}

internal class PowerExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("power", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        val secondType = args[1].type
        val secondValue = args[1].numberValue()

        return when {
            firstType.isNumber == false -> errNoContext("Argument 1 of power was not Number.", internal = false)
            secondType.isNumber == false -> errNoContext("Argument 2 of power was not Number.", internal = false)
            else -> valueFactory.newFloat(Math.pow(firstValue.toDouble(), secondValue.toDouble()))
        }
    }
}

internal class LogExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("log", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        val secondType = args[1].type
        val secondValue = args[1].numberValue()

        return when {
            firstType.isNumber == false -> errNoContext("Argument 1 of log was not Number.", internal = false)
            secondType.isNumber == false -> errNoContext("Argument 2 of log was not Number.", internal = false)
            else -> valueFactory.newFloat(log(firstValue.toDouble(), secondValue.toDouble()))
        }
    }
}

internal class ExpExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("exp", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when {
            firstType.isNumber -> valueFactory.newFloat(exp(firstValue.toDouble()))
            else -> errNoContext("Argument 1 of ln exp not Number.", internal = false)
        }
    }
}

internal class LnExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ln", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()

        return when {
            firstType.isNumber -> valueFactory.newFloat(ln(firstValue.toDouble()))
            else -> errNoContext("Argument 1 of ln was not Number.", internal = false)
        }
    }
}

internal class NanvlExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("nanvl", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        if (firstType.isNumber) {
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