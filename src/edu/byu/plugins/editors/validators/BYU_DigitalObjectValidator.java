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
 * Date: Oct 23, 2009
 * Time: 5:12:11 PM
 */

package edu.byu.plugins.editors.validators;

import org.archiviststoolkit.model.Accessions;
import org.archiviststoolkit.model.DigitalObjects;
import org.archiviststoolkit.model.validators.ATAbstractValidator;
import org.archiviststoolkit.util.ATPropertyValidationSupport;
import org.archiviststoolkit.mydomain.DigitalObjectDAO;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.util.ValidationUtils;

public class BYU_DigitalObjectValidator extends ATAbstractValidator {

	// Instance Creation ******************************************************

	/**
	 * Constructs an validator
	 *
	 * @param accession the accession to be validated
	 */
	public BYU_DigitalObjectValidator(Accessions accession) {
		this.objectToValidate = accession;
	}

	public BYU_DigitalObjectValidator() {
	}


	// Validation *************************************************************

	/**
	 * Validates this Validator's Order and returns the result
	 * as an instance of {@link com.jgoodies.validation.ValidationResult}.
	 *
	 * @return the ValidationResult of the accession validation
	 */
	public ValidationResult validate() {

		DigitalObjects digitalObject = (DigitalObjects)objectToValidate;

		ATPropertyValidationSupport support =
				new ATPropertyValidationSupport(digitalObject, "Digital Object");

		if (digitalObject.getParent() == null) {
			//Digital Object ID is manditory
            if (ValidationUtils.isBlank(digitalObject.getMetsIdentifier())) {
                support.addError("Unique Digital Object ID", "is mandatory");
            } else { // must check to see if this is a unique digital object ID
                if(!checkForUniqueDigitalObjectID(digitalObject)) {
                    support.addError("Unique Digital Object ID", "is mandatory");
                }
            }

            // A title or date is mandatory
			if(!checkForTitleOrDate(digitalObject)) {
				support.addError("Title or Date", "is mandatory");
            }
		} else {
			//digitalObject date or title are manditory
			if (!checkForTitleOrDateOrLabel(digitalObject))
				support.addError("Title, label or Date", "is mandatory");
		}

		//at least one date record is required
		if (digitalObject.getArchDescriptionDates().size() == 0)
			support.addError("At least one date record", "is mandatory");

		checkForStringLengths(digitalObject, support);

		return support.getResult();
	}

    /**
     * Method to check to see if another digital object has the same mets Identifier
     * but different unique id as the digital object being validated
     *
     * @param digitalObject The digital object to check for
     * @return true if the digital object has unique mets ID or false otherwise
     */
    private boolean checkForUniqueDigitalObjectID(DigitalObjects digitalObject) {
        try {
            DigitalObjectDAO access = new DigitalObjectDAO();
            DigitalObjects foundDigitalObject = access.findByMetsIdentifier(digitalObject);

            if(foundDigitalObject != null && (digitalObject.getIdentifier() == null || !digitalObject.getIdentifier().equals(foundDigitalObject.getIdentifier()))) {
                return false;
            } else {
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean checkForTitleOrDateOrLabel(DigitalObjects digitalObject) {
		if (ValidationUtils.isNotBlank(digitalObject.getTitle())) {
			return true;
		} else if (ValidationUtils.isNotBlank(digitalObject.getLabel())) {
			return true;
		} else if (digitalObject.getArchDescriptionDates().size() != 0) {
			return true;
		} else {
			return false;
		}
	}

    private boolean checkForTitleOrDate(DigitalObjects digitalObject) {
		if (ValidationUtils.isNotBlank(digitalObject.getTitle())) {
			return true;
		} else if (digitalObject.getArchDescriptionDates().size() != 0) {
			return true;
		} else {
			return false;
		}
	}

}
