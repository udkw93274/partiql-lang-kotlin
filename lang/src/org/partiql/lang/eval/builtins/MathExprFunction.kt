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
        return when (args[0].type) {
            ExprValueType.INT -> valueFactory.newInt(abs(args[0].numberValue().toInt()))
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newFloat(abs(args[0].numberValue().toDouble()))
            else -> errNoContext("Argument 1 of abs was not NUMBER.", internal = false)
        }
    }
}

internal class SignExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sign", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type.isNumber == false -> errNoContext("Argument 1 of sign was not NUMBER.", internal = false)
            else  -> valueFactory.newInt(sign(args[0].numberValue().toDouble()).toInt())
        }
    }
}

internal class CeilExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ceil", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when (args[0].type) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(ceil(args[0].numberValue().toDouble()).toInt())
            else -> errNoContext("Argument 1 of ceil was not NUMBER.", internal = false)
        }
    }
}

internal class FloorExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("floor", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when (args[0].type) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(floor(args[0].numberValue().toDouble()).toInt())
            else -> errNoContext("Argument 1 of floor was not NUMBER.", internal = false)
        }
    }
}

internal class RoundExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("round", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when (args[0].type) {
            ExprValueType.INT -> args[0]
            ExprValueType.DECIMAL, ExprValueType.FLOAT -> valueFactory.newInt(args[0].numberValue().toDouble().roundToInt())
            else -> errNoContext("Argument 1 of round was not NUMBER.", internal = false)
        }
    }
}

internal class TruncExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("trunc", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[1].type != ExprValueType.INT                 -> errNoContext("Argument 2 of trunc was not INT.",
                                                                                internal = false)
            args[0].type == ExprValueType.INT                 -> args[0]
            args[0].type == ExprValueType.DECIMAL || args[0].type == ExprValueType.FLOAT -> {
                var secondValue = args[1].numberValue().toDouble()
                if (secondValue < 0) { secondValue = 0.0 }
                val divider = Math.pow(10.0, secondValue)
                valueFactory.newFloat(floor(args[0].numberValue().toDouble() * divider) / divider)
            }
            else                                              -> errNoContext("Argument 1 of trunc was not NUMBER.", internal = false)
        }
    }
}

internal class ModExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("mod", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT                   -> errNoContext("Argument 1 of mod was not INT.",
                                                                                internal = false)
            args[1].type != ExprValueType.INT                   -> errNoContext("Argument 2 of mod was not INT.",
                                                                                internal = false)
            args[1].numberValue().toInt() == 0                  -> errNoContext("Argument 2 of mod must not be 0.",
                                                                                internal = false)
            else -> valueFactory.newInt(args[0].numberValue().toInt() % args[1].numberValue().toInt())
        }
    }
}

internal class SqrtExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sqrt", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type.isNumber -> valueFactory.newFloat(sqrt(args[0].numberValue().toDouble()))
            else -> errNoContext("Argument 1 of sqrt was not NUMBER.", internal = false)
        }
    }
}

internal class PowerExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("power", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type.isNumber == false -> errNoContext("Argument 1 of power was not NUMBER.", internal = false)
            args[1].type.isNumber == false -> errNoContext("Argument 2 of power was not NUMBER.", internal = false)
            else -> valueFactory.newFloat(Math.pow(args[0].numberValue().toDouble(), args[1].numberValue().toDouble()))
        }
    }
}

internal class LogExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("log", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        when {
            args[0].type.isNumber == false -> errNoContext("Argument 1 of log was not NUMBER.", internal = false)
            args[1].type.isNumber == false -> errNoContext("Argument 2 of log was not NUMBER.", internal = false)
        }
        val targetNumber = args[0].numberValue().toDouble()
        val base = args[1].numberValue().toDouble()
        return when {
            targetNumber == 0.0 -> valueFactory.newString("-inf")
            targetNumber < 0.0 || base <= 0.0 || base == 1.0 -> valueFactory.newString("nan")
            targetNumber == 1.0 -> valueFactory.newFloat(0.0)
            else -> valueFactory.newFloat(log(targetNumber, base))
        }
    }
}

internal class ExpExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("exp", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type.isNumber -> valueFactory.newFloat(exp(args[0].numberValue().toDouble()))
            else -> errNoContext("Argument 1 of exp not NUMBER.", internal = false)
        }
    }
}

internal class LnExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("ln", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        if (args[0].type.isNumber) {
            return when {
                args[0].numberValue().toDouble() < 0.0 -> valueFactory.newString("nan")
                args[0].numberValue().toDouble() == 0.0 -> valueFactory.newString("-inf")
                else -> valueFactory.newFloat(ln(args[0].numberValue().toDouble()))
            }
        } else {
            errNoContext("Argument 1 of ln was not NUMBER.", internal = false)
        }
    }
}

internal class NanvlExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("nanvl", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        return when {
            firstType.isNumber -> args[0]
            else -> args[1]
        }
    }
}

internal class RandExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("rand", 0, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return valueFactory.newFloat(Math.random())
    }
}

internal class BitORExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("bitor", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT -> errNoContext("Argument 1 of bitor was not INT.", internal = false)
            args[1].type != ExprValueType.INT -> errNoContext("Argument 2 of bitor was not INT.", internal = false)
            else -> valueFactory.newInt(args[0].numberValue().toInt() or args[1].numberValue().toInt())
        }
    }
}

internal class BitANDExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("bitand", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT -> errNoContext("Argument 1 of bitand was not INT.", internal = false)
            args[1].type != ExprValueType.INT -> errNoContext("Argument 2 of bitand was not INT.", internal = false)
            else -> valueFactory.newInt(args[0].numberValue().toInt() and args[1].numberValue().toInt())
        }
    }
}

internal class BitXORExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("bitxor", 2, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        return when {
            args[0].type != ExprValueType.INT -> errNoContext("Argument 1 of bitxor was not INT.", internal = false)
            args[1].type != ExprValueType.INT -> errNoContext("Argument 2 of bitxor was not INT.", internal = false)
            else -> valueFactory.newInt(args[0].numberValue().toInt() xor args[1].numberValue().toInt())
        }
    }
}

internal class SinExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sin", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(sin(extractArguments(args[0], "sin"))) }
}

internal class SinhExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("sinh", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(sinh(extractArguments(args[0], "sinh"))) }
}

internal class CosExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("cos", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(cos(extractArguments(args[0], "cos"))) }
}

internal class CoshExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("cosh", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(cosh(extractArguments(args[0], "cosh"))) }
}

internal class TanExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("tan", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(tan(extractArguments(args[0], "tan"))) }
}

internal class TanhExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("tanh", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(tanh(extractArguments(args[0], "tanh"))) }
}

internal class AsinExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("asin", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(asin(extractArguments(args[0], "asin"))) }
}

internal class AcosExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("acos", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(acos(extractArguments(args[0], "acos"))) }
}

internal class AtanExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("atan", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(atan(extractArguments(args[0], "atan"))) }
}

internal class AtanhExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("atanh", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { return  valueFactory.newFloat(atanh(extractArguments(args[0], "atanh"))) }
}

internal class Atan2ExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("atan2", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue { 
        return when {
            args[1].type.isNumber == false -> errNoContext("Argument 2 of atan2 was not NUMBER.", internal = false)
            else -> valueFactory.newFloat(atan2(extractArguments(args[0], "atan2"), args[1].numberValue().toDouble())) 
        }
    }
}

private fun extractArguments(input: ExprValue, functionName: String): Double {
    return when {
        input.type.isNumber == false -> errNoContext("Argument 1 of "+functionName+" was not NUMBER.", internal = false)
        else -> input.numberValue().toDouble()
    }
}

