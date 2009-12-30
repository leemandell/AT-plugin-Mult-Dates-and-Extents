/**
 * Archivists' Toolkit(TM) Copyright © 2005-2007 Regents of the University of California, New York University, & Five Colleges, Inc.  
 * All rights reserved. 
 *
 * This software is free. You can redistribute it and / or modify it under the terms of the Educational Community License (ECL) 
 * version 1.0 (http://www.opensource.org/licenses/ecl1.php) 
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ECL license for more details about permissions and limitations. 
 *
 *
 * Archivists' Toolkit(TM) 
 * http://www.archiviststoolkit.org 
 * info@archiviststoolkit.org 
 *
 * @author Lee Mandell
 * Date: Oct 16, 2009
 * Time: 11:46:49 AM
 */

package edu.byu.plugins.editors;

import org.archiviststoolkit.swing.ATBasicComponentFactory;
import org.archiviststoolkit.util.ATDateUtils;
import org.archiviststoolkit.ApplicationFrame;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.binding.adapter.Bindings;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class BYU_ATBasicComponentFactory extends ATBasicComponentFactory{

	/**
	  * This create a textfeild that is used to input iso dates. The only thing this does
	  * from a regular textfield is that it does checking of the date format on focus lost
	  * valid formats are yyyy, yyyy-mm, yyyy-mm-dd in the date format being used by the AT
	  * @param valueModel The vlaue model to bind to
	  * @return  The JTextfield
	  */
	 public static JTextField createISODateField (ValueModel valueModel) {
		 JTextField textField = new JTextField();
		 Bindings.bind(textField, valueModel, true);
		 textField.addFocusListener(new ISODateFocusListener(textField));
		 return textField;
	 }

	/**
	  * Listens to property changes in a ComponentValueModel and
	  * updates the associated component state.
	  * Also check that the iso date is valid. Valid dates formats are
	  * yyyy, yyyy-mm, yyyy-dd,
	  * @see com.jgoodies.binding.value.ComponentValueModel
	  */
	 private static final class ISODateFocusListener implements FocusListener {

		 private String oldValue = null;
		 private JTextComponent textComponent;

		 public ISODateFocusListener(JTextComponent textComponent) {
			 this.textComponent = textComponent;
		 }

		 public void focusGained(FocusEvent event) {
			 oldValue = textComponent.getText();
		 }

		 public void focusLost(FocusEvent event) {
			 String newValue = textComponent.getText();

			 // check that the iso date format is valid
			 if(newValue.length() > 0 && isValidISODate((JTextField)textComponent)) {
				 // now check if the value was changed
				 if (!oldValue.equals(newValue)) {
					 ApplicationFrame.getInstance().setRecordDirty();
				 } else {
					 textComponent.setText(oldValue);
				 }
			 }
		 }
	 }

	/**
	 * Method to check to see if the string that was passed in is a valid iso date.
	 * valid iso dates can be either yyyy, yyyy-mm, yyyy-mm-dd
	 *
	 * @param textField The textfield containing the iso date to check
	 * @return true if the date passes the check for an iso date
	 */
	private static boolean isValidISODate(JTextField textField) {
		String isoDate = textField.getText();
		SimpleDateFormat sdf = ApplicationFrame.applicationDateFormat;
		String pattern = sdf.toPattern();
		String newISODate = "";
		Boolean bc = false;
		if (isoDate.charAt(0) == '-') {
			System.out.println("we have a bc date");
			bc = true;
			isoDate = isoDate.substring(1);
		}

		// fisrt check to if it matches a iso date
		if(isoDate.matches("\\d{1,4}") || isoDate.matches("\\d{1,4}-\\d{1,2}") || isoDate.matches("\\d{1,4}-\\d{1,2}-\\d{1,2}")) {
			newISODate = formatISODate(isoDate);
		}

		// now check to see if it not in a date format that AT is using. if it is then convert to iso format
		if (ATDateUtils.isValidATDate(textField)) {
			try {
				SimpleDateFormat isodf = new SimpleDateFormat("yyyy-MM-dd");
				Date testDate = sdf.parse(isoDate);
				newISODate = isodf.format(testDate);
			} catch(ParseException pe) {
				newISODate = "";
			}
		} else if(pattern.contains("M/d") && isoDate.indexOf("/") != -1) { // assume format is mm/yyyy
			String[] sa = isoDate.split("/");
			newISODate = formatISODate(sa[1] + "-" + sa[0]);
		} else if(pattern.contains("M-d") && isoDate.indexOf("-") != -1) { // assume format is mm-yyyy
			String[] sa = isoDate.split("-");
			newISODate = formatISODate(sa[1] + "-" + sa[0]);
		} else if(pattern.contains("y/M") && isoDate.indexOf("/") != -1) { // assume format is yyyy/mm
			String[] sa = isoDate.split("/");
			newISODate = formatISODate(sa[0] + "-" + sa[1]);
		} else if(pattern.contains("d/M") && isoDate.indexOf("/") != -1) { // assume format is mm/yyyy
			String[] sa = isoDate.split("/");
			newISODate = formatISODate(sa[1] + "-" + sa[0]);
		} else if(pattern.contains("d-M") && isoDate.indexOf("-") != -1) { // assume format is mm-yyyy
			String[] sa = isoDate.split("-");
			newISODate = formatISODate(sa[1] + "-" + sa[0]);
		}

		// now check that the final date is in an iso format, if not display error to user
		if(newISODate.matches("\\d{4}") || newISODate.matches("\\d{4}-\\d{2}") || newISODate.matches("\\d{4}-\\d{2}-\\d{2}")) {
			String year = newISODate.substring(0,4);
			if (Integer.parseInt(year) > 2999) {
				String errorMessage = "The must be between -2999 and 2999";
				JOptionPane.showMessageDialog(null, errorMessage);
				textField.setText("");
				return false;
			}
			if (bc) {
				textField.setText("-" + newISODate);
			} else {
				textField.setText(newISODate);
			}
			return true;
		} else { // display error to user
			String errorMessage = "The date provided is an invalid ISO date format.\nValid formats are YYYY, YYYY-MM, YYYY-MM-DD";
			JOptionPane.showMessageDialog(null, errorMessage);
			textField.setText("");
			return false;
		}
	}

	/**
	 * Method to format an inputed date to the iso format of either
	 * yyyy, yyyy-mm, or yyyy-mm-dd
	 * @param date The input date to format
	 * @return The formated iso date
	 */
	private static String formatISODate(String date) {
		if(date.matches("\\d{1,4}")) { // matches yyyy
			return ATDateUtils.padYear(date);
		} else if (date.matches("\\d{1,4}-\\d{1,2}")) { // matches yyyy-mm format
			String[] sa = date.split("-");
			int month = Integer.parseInt(sa[1]);
			if (month < 1 || month > 12) {
				//bad month
				return "";
			} else {
				return ATDateUtils.padYear(sa[0]) + "-" + ATDateUtils.padMonthOrDay(sa[1]);
			}
		} else if (date.matches("\\d{1,4}-\\d{1,2}-\\d{1,2}")) {// matches yyyy-mm-dd
			String[] sa = date.split("-");
			String stringDate = ATDateUtils.padYear(sa[0]) + "-" + ATDateUtils.padMonthOrDay(sa[1]) + "-" + ATDateUtils.padMonthOrDay(sa[2]);
			SimpleDateFormat isodf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				isodf.setLenient(false);
				isodf.parse(stringDate);
			} catch (ParseException e) {
				return "";
			}
			return stringDate;
		} else { // return blank
			return "";
		}
	}


}
