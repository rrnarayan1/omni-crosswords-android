package com.rohanNarayan.omnicrosswords.ui.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun toFormattedDate(date: Long): String {
    return DateTimeFormatter.ofPattern("EE M/d/yy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochSecond(date))
}

fun toTime(elapsedTimeInSeconds: Long): String {
    val mins = elapsedTimeInSeconds / 60
    val secs = elapsedTimeInSeconds % 60
    return "%02d:%02d".format(mins, secs)
}