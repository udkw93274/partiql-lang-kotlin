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
import com.amazon.ion.*
import java.time.*
import java.time.format.*

internal class UnixtimeToTimestampExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("unixtime_to_timestamp", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        
        when {
            firstType != ExprValueType.INT                   -> errNoContext("Argument 1 of unixtime_to_timestamp was not INT.",
                                                                                internal = false)
        }
        val time = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(firstValue.toLong()))
        return valueFactory.newTimestamp(Timestamp.valueOf( time.toString() ) )
    }
}

internal class TimestampToUnixtimeExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("timestamp_to_unixtime", 1, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        validateArguments(args)

        try {
            val timestamp = args[0].timestampValue()
            val time2 = Instant.parse(timestamp.toString())
            return valueFactory.newInt(time2.getEpochSecond()*1000+time2.getNano()/1000000)
        }
        catch(ex: Exception) {
            errNoContext("First argument of unixtime_to_timestamp is not a time format", internal = false)
        }
    }

    private fun validateArguments(args: List<ExprValue>) {
        when {
            args[0].ionValue !is IonTimestamp -> errNoContext("First argument of timestamp_to_unixtime is not a timestamp.", internal = false)
        }
    }
}