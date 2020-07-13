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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


internal class UnixtimeToStringExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("unixtime_to_string", (2..3), valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {        
        val firstType = args[0].type
        val firstValue = args[0].numberValue()
        val secondType = args[1].type
        val secondValue = args[1].stringValue()
        
        when {
            firstType != ExprValueType.INT                         -> errNoContext("Argument 1 of unixtime_to_string was not INT.",
                                                                                internal = false)
            secondType != ExprValueType.STRING                     -> errNoContext("Argument 2 of unixtime_to_string was not STRING.",
                                                                                internal = false)
            args.size > 2 && args[2].type != ExprValueType.STRING  -> errNoContext("Argument 3 of unixtime_to_string was not STRING.",
                                                                                internal = false)
        }
        
        var date = Date(firstValue.toLong()); 
        var jdf = SimpleDateFormat(secondValue);

        if (args.size > 2) {
            jdf.setTimeZone(TimeZone.getTimeZone(args[2].stringValue()));
        } else {
            jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return valueFactory.newString(jdf.format(date))
    }
}

internal class StringToUnixtimeExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("string_to_unixtime", (2..3), valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {        
        val firstType = args[0].type
        val firstValue = args[0].stringValue()
        val secondType = args[1].type
        val secondValue = args[1].stringValue()
        
        when {
            firstType != ExprValueType.STRING                      -> errNoContext("Argument 1 of string_to_unixtime was not STRING.",
                                                                                internal = false)
            secondType != ExprValueType.STRING                     -> errNoContext("Argument 2 of string_to_unixtime was not STRING.",
                                                                                internal = false)
            args.size > 2 && args[2].type != ExprValueType.STRING  -> errNoContext("Argument 3 of string_to_unixtime was not STRING.",
                                                                                internal = false)
        }

        try {
            var jdf = SimpleDateFormat(secondValue);
            if (args.size > 2) {
                jdf.setTimeZone(TimeZone.getTimeZone(args[2].stringValue()));
            } else {
                jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
			var date = jdf.parse(firstValue);
            var timestamp = date.getTime();
            return valueFactory.newInt(timestamp)
		} catch (ex: Exception) {
            errNoContext("Date Format Parsing Error", internal = false)
		}
    }
}

internal class UnixtimeNowExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("unixtime_now", 0, valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {        
        return valueFactory.newInt(System.currentTimeMillis())
    }
}

// internal class UnixtimeToTimestampExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("unixtime_to_timestamp", 1, valueFactory) {
//     override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
//         val firstType = args[0].type
//         val firstValue = args[0].numberValue()
        
//         when {
//             firstType != ExprValueType.INT                   -> errNoContext("Argument 1 of unixtime_to_timestamp was not INT.",
//                                                                                 internal = false)
//         }
//         val time = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(firstValue.toLong()))
//         return valueFactory.newTimestamp(Timestamp.valueOf( time.toString() ) )
//     }
// }

// internal class TimestampToUnixtimeExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("timestamp_to_unixtime", 1, valueFactory) {
//     override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
//         validateArguments(args)

//         try {
//             val timestamp = args[0].timestampValue()
//             val time2 = Instant.parse(timestamp.toString())
//             return valueFactory.newInt(time2.getEpochSecond()*1000+time2.getNano()/1000000)
//         }
//         catch(ex: Exception) {
//             errNoContext("First argument of unixtime_to_timestamp is not a time format", internal = false)
//         }
//     }

//     private fun validateArguments(args: List<ExprValue>) {
//         when {
//             args[0].ionValue !is IonTimestamp -> errNoContext("First argument of timestamp_to_unixtime is not a timestamp.", internal = false)
//         }
//     }
// }