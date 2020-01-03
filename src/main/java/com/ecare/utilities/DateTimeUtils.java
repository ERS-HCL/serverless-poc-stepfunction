package com.ecare.utilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	public static Long getGMTCurrentTimeInEpochSecond() {
		return Instant.now().getEpochSecond();
	}
	public static String getDateFromEPOCSeconds(Long epochSeconds, String zoneAlias) {
	if(zoneAlias == null) {
		zoneAlias="Asia/Calcutta";
	}
	ZonedDateTime zonedDateTime = Instant.ofEpochSecond(epochSeconds)
	            .atZone(ZoneId.of(zoneAlias));
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z");
	String formattedDateString = zonedDateTime.format(formatter);
	return formattedDateString;
	}
	
}
