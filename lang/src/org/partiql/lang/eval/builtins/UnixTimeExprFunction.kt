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
import com.amazon.ion.Timestamp.*
import org.partiql.lang.eval.*
import org.partiql.lang.syntax.*
import org.partiql.lang.util.*
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.time.*

private const val SECONDS_PER_MINUTE = 60

internal class UnixtimeToStringExprFunction(valueFactory: ExprValueFactory): NullPropagatingExprFunction("unixtime_to_string", (2..3), valueFactory) {
    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {                
        when {
            args[0].type != ExprValueType.INT                         -> errNoContext("Argument 1 of unixtime_to_string was not INT.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                      -> errNoContext("Argument 2 of unixtime_to_string was not STRING.",
                                                                                internal = false)
            args.size > 2 && args[2].type != ExprValueType.STRING  -> errNoContext("Argument 3 of unixtime_to_string was not STRING.",
                                                                                internal = false)
        }
        
        var date = Date(args[0].numberValue().toLong()); 
        var jdf = SimpleDateFormat(args[1].stringValue());

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
        when {
            args[0].type != ExprValueType.STRING                      -> errNoContext("Argument 1 of string_to_unixtime was not STRING.",
                                                                                internal = false)
            args[1].type != ExprValueType.STRING                      -> errNoContext("Argument 2 of string_to_unixtime was not STRING.",
                                                                                internal = false)
            args.size > 2 && args[2].type != ExprValueType.STRING  -> errNoContext("Argument 3 of string_to_unixtime was not STRING.",
                                                                                internal = false)
        }

        try {
            var jdf = SimpleDateFormat(args[1].stringValue());
            if (args.size > 2) {
                jdf.setTimeZone(TimeZone.getTimeZone(args[2].stringValue()));
            } else {
                jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
			var date = jdf.parse(args[0].stringValue());
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

internal class AddTimeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("add_time", 3, valueFactory) {
    companion object {
        @JvmStatic private val precisionOrder = listOf(Precision.YEAR,
                                                       Precision.MONTH,
                                                       Precision.DAY,
                                                       Precision.MINUTE,
                                                       Precision.SECOND)

        @JvmStatic private val datePartToPrecision = mapOf(DatePart.YEAR to Precision.YEAR,
                                                           DatePart.MONTH to Precision.MONTH,
                                                           DatePart.DAY to Precision.DAY,
                                                           DatePart.HOUR to Precision.MINUTE,
                                                           DatePart.MINUTE to Precision.MINUTE,
                                                           DatePart.SECOND to Precision.SECOND)
    }

    private fun Timestamp.hasSufficientPrecisionFor(requiredPrecision: Precision): Boolean {
        val requiredPrecisionPos = precisionOrder.indexOf(requiredPrecision)
        val precisionPos = precisionOrder.indexOf(precision)

        return precisionPos >= requiredPrecisionPos
    }

    private fun Timestamp.adjustPrecisionTo(datePart: DatePart): Timestamp {
        val requiredPrecision = datePartToPrecision[datePart]!!

        if (this.hasSufficientPrecisionFor(requiredPrecision)) {
            return this
        }

        return when (requiredPrecision) {
            Precision.YEAR     -> Timestamp.forYear(this.year)
            Precision.MONTH    -> Timestamp.forMonth(this.year, this.month)
            Precision.DAY      -> Timestamp.forDay(this.year, this.month, this.day)
            Precision.SECOND   -> Timestamp.forSecond(this.year,
                                                      this.month,
                                                      this.day,
                                                      this.hour,
                                                      this.minute,
                                                      this.second,
                                                      this.localOffset)
            Precision.MINUTE   -> Timestamp.forMinute(this.year,
                                                      this.month,
                                                      this.day,
                                                      this.hour,
                                                      this.minute,
                                                      this.localOffset)
            else                -> errNoContext("invalid date part for date_add: ${datePart.toString().toLowerCase()}",
                                                internal = false)
        }
    }

    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val datePart = args[0].datePartValue()
        val interval = args[1].intValue()
        val timestamp = unixTimeToScalaTimestamp(args[2], "add_time", 3)

        try {
            val addedTimestamp = when (datePart) {
                DatePart.YEAR   -> timestamp.adjustPrecisionTo(datePart).addYear(interval)
                DatePart.MONTH  -> timestamp.adjustPrecisionTo(datePart).addMonth(interval)
                DatePart.DAY    -> timestamp.adjustPrecisionTo(datePart).addDay(interval)
                DatePart.HOUR   -> timestamp.adjustPrecisionTo(datePart).addHour(interval)
                DatePart.MINUTE -> timestamp.adjustPrecisionTo(datePart).addMinute(interval)
                DatePart.SECOND -> timestamp.adjustPrecisionTo(datePart).addSecond(interval)
                else            -> errNoContext("invalid date part for date_add: ${datePart.toString().toLowerCase()}",
                                                internal = false)
            }

            try {
                val instantTime = Instant.parse(addedTimestamp.toString())
                return valueFactory.newInt(instantTime.getEpochSecond()*1000+instantTime.getNano()/1000000)
            }
            catch(ex: Exception) {
                errNoContext("First argument of unixtime_to_timestamp is not a time format", internal = false)
            }
        } catch (e: IllegalArgumentException) {
            // illegal argument exception are thrown when the resulting timestamp go out of supported timestamp boundaries
            throw EvaluationException(e, internal = false)
        }
    }
}

/**
 * Difference in date parts between two timestamps. If the first timestamp is later than the second the result is negative.
 *
 * Syntax: `DATE_DIFF(<date part>, <timestamp>, <timestamp>)`
 * Where date part is one of the following keywords: `year, month, day, hour, minute, second`
 *
 * Timestamps without all date parts are considered to be in the beginning of the missing parts to make calculation possible.
 * For example:
 * - 2010T is interpreted as 2010-01-01T00:00:00.000Z
 * - date_diff(month, `2010T`, `2010-05T`) results in 4
 *
 * If one of the timestamps has a time component then they are a day apart only if they are 24h apart, examples:
 * - date_diff(day, `2010-01-01T`, `2010-01-02T`) results in 1
 * - date_diff(day, `2010-01-01T23:00Z`, `2010-01-02T01:00Z`) results in 0 as they are only 2h apart
 */
internal class DiffTimeExprFunction(valueFactory: ExprValueFactory) : NullPropagatingExprFunction("diff_time", 3, valueFactory) {

    // Since we don't have a date part for `milliseconds` we can safely set the OffsetDateTime to 0 as it won't
    // affect any of the possible calculations.
    //
    // If we introduce the `milliseconds` date part this will need to be
    // revisited
    private fun Timestamp.toJava() = OffsetDateTime.of(year,
                                                       month,
                                                       day,
                                                       hour,
                                                       minute,
                                                       second,
                                                       0,
                                                       ZoneOffset.ofTotalSeconds((localOffset ?: 0) * 60))

    private fun yearsSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Period.between(left.toLocalDate(), right.toLocalDate()).years

    private fun monthsSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Period.between(left.toLocalDate(), right.toLocalDate()).toTotalMonths()

    private fun daysSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Duration.between(left, right).toDays()

    private fun hoursSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Duration.between(left, right).toHours()

    private fun minutesSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Duration.between(left, right).toMinutes()

    private fun secondsSince(left: OffsetDateTime, right: OffsetDateTime): Number =
        Duration.between(left, right).toMillis() / 1_000

    override fun eval(env: Environment, args: List<ExprValue>): ExprValue {
        val datePart = args[0].datePartValue()
        val left = unixTimeToScalaTimestamp(args[1], "diff_time",2)
        val right = unixTimeToScalaTimestamp(args[2], "diff_time",3)

        val leftAsJava = left.toJava()
        val rightAsJava = right.toJava()

        val difference = when (datePart) {
            DatePart.YEAR   -> yearsSince(leftAsJava, rightAsJava)
            DatePart.MONTH  -> monthsSince(leftAsJava, rightAsJava)
            DatePart.DAY    -> daysSince(leftAsJava, rightAsJava)
            DatePart.HOUR   -> hoursSince(leftAsJava, rightAsJava)
            DatePart.MINUTE -> minutesSince(leftAsJava, rightAsJava)
            DatePart.SECOND -> secondsSince(leftAsJava, rightAsJava)
            else            -> errNoContext("invalid date part for date_diff: ${datePart.toString().toLowerCase()}",
                                            internal = false)
        }

        return valueFactory.newInt(difference.toLong())
    }
}

fun unixTimeToScalaTimestamp(value: ExprValue, callerName: String, argumentIndex: Int): Timestamp {
    try {
        if (value.type == ExprValueType.INT) {
            var date = Date(value.numberValue().toLong()); 
            var jdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS");
            jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            jdf.format(date)
            var tempTimeString = jdf.format(date);
            tempTimeString = tempTimeString.substring(0, 10) + "T" + tempTimeString.substring(10, tempTimeString.length)+"Z"
            return Timestamp.valueOf(tempTimeString)
        } else {
            errNoContext("Argument "+argumentIndex+" of "+callerName+" was not INTEGER.", internal = false)
        }
    } catch(e : Exception) {
        errNoContext("Argument "+argumentIndex+" of "+callerName+" was not INTEGER.", internal = false)
    }
}

internal class IsNullExprFunction(private val valueFactory: ExprValueFactory) : ArityCheckingTrait, ExprFunction {
    override val name: String = "isnull"
    override val arity: IntRange = (1..1)

    override fun call(env: Environment, args: List<ExprValue>): ExprValue {
        checkArity(args)

        return when {
            args[0].type == ExprValueType.NULL -> valueFactory.newBoolean(true)
            else                        -> valueFactory.newBoolean(false)
        }
    }
}