/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drcarlosamaral.controlecronicosserver;

/**
 *
 * @author Nicholas DiPiazza
 */
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

public class DateUtils {
	
	public void changeDatePickerFormater(DatePicker datePicker) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
		     String pattern = "dd/MM/yyyy";
		     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

		     {
		         datePicker.setPromptText(pattern.toLowerCase());
		     }

		     @Override public String toString(LocalDate date) {
		         if (date != null) {
		             return dateFormatter.format(date);
		         } else {
		             return "";
		         }
		     }

		     @Override public LocalDate fromString(String text) {
		         if (text != null && !text.isEmpty()) {
		        	 if (!text.contains("/") && (text.length() == 8 || text.length() == 6)) {
		        		 text = text.substring(0,2) + "/" + text.substring(2,4) + "/" + text.substring(4);
		        	 }
		             Locale locale = Locale.getDefault(Locale.Category.FORMAT);
		             Chronology chrono = datePicker.getChronology();
		             String pattern =
		                 DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT,
		                                                                      null, chrono, locale);
		             String prePattern = pattern.substring(0, pattern.indexOf("y"));
		             String postPattern = pattern.substring(pattern.lastIndexOf("y")+1);
		             int baseYear = LocalDate.now().getYear() - 99;
		             DateTimeFormatter df = new DateTimeFormatterBuilder()
		                         .parseLenient()
		                         .appendPattern(prePattern)
		                         .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear)
		                         .appendPattern(postPattern)
		                         .toFormatter();
		             return LocalDate.from(chrono.date(df.parse(text)));
		         } else {
		             return null;
		         }
		     }
		 });
	}

	public void changeDatePickerFormaterFuturo(DatePicker datePicker) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
		     String pattern = "dd/MM/yyyy";
		     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

		     {
		         datePicker.setPromptText(pattern.toLowerCase());
		     }

		     @Override public String toString(LocalDate date) {
		         if (date != null) {
		             return dateFormatter.format(date);
		         } else {
		             return "";
		         }
		     }

		     @Override public LocalDate fromString(String text) {
		         if (text != null && !text.isEmpty()) {
		        	 if (!text.contains("/") && (text.length() == 8 || text.length() == 6)) {
		        		 text = text.substring(0,2) + "/" + text.substring(2,4) + "/" + text.substring(4);
		        	 }
		             Locale locale = Locale.getDefault(Locale.Category.FORMAT);
		             Chronology chrono = datePicker.getChronology();
		             String pattern =
		                 DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT,
		                                                                      null, chrono, locale);
		             String prePattern = pattern.substring(0, pattern.indexOf("y"));
		             String postPattern = pattern.substring(pattern.lastIndexOf("y")+1);
		             int baseYear = LocalDate.now().getYear();
		             DateTimeFormatter df = new DateTimeFormatterBuilder()
		                         .parseLenient()
		                         .appendPattern(prePattern)
		                         .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear)
		                         .appendPattern(postPattern)
		                         .toFormatter();
		             return LocalDate.from(chrono.date(df.parse(text)));
		         } else {
		             return null;
		         }
		     }
		 });
	}
	
  public static synchronized Date asDate(LocalDate localDate) {
	if (localDate == null) return null;
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static synchronized Date asDate(LocalDateTime localDateTime) {
	if (localDateTime == null) return null;
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static synchronized LocalDate asLocalDate(Date date) {
	if (date == null) return null;
    // return Instant.ofEpochMilli(date.getTime()).atOffset(ZoneOffset.UTC).toLocalDate();
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static synchronized LocalDateTime asLocalDateTime(Date date) {
	if (date == null) return null;
    //  return Instant.ofEpochMilli(date.getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
  
  public static synchronized String asString(LocalDate localDate) {
	  if (localDate == null) return "";
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	  return localDate.format(formatter);
  }

  /**
   * Tem que passar uma data no formato "dd/MM/aaaa" ou "dd/MM/aa"
   * @param dataString
   * @return Retorna um Date pronto para o MongoDB ou null se não conseguir converter
   */
  public static synchronized Date asDate(String dataString) {
	  if (dataString == null || 
			  dataString.equals("") ||
			  !(dataString.length() == 10 || dataString.length() == 8)) return null;
	  int dia = Integer.parseInt(dataString.substring(0,2));
	  int mes = Integer.parseInt(dataString.substring(3,5));
	  int ano = Integer.parseInt(dataString.substring(6,8));
	  if (dataString.length() == 10) ano = Integer.parseInt(dataString.substring(6,10));
	  if (dataString.length() == 8) {
		  if (LocalDate.now().minusYears(ano + 2000).getYear() >= 0) {
			  ano = ano + 2000;
		  } else {
			  ano = ano + 1900;
		  }
	  }
	  Calendar ca = Calendar.getInstance();
	  ca.set(ano, mes, dia);
	  return ca.getTime();
  }
}