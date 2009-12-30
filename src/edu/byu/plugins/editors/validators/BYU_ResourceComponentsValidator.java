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
 * Date: Dec 29, 2009
 * Time: 11:03:52 AM
 */

package edu.byu.plugins.editors.validators;

import org.archiviststoolkit.model.Accessions;
import org.archiviststoolkit.model.ResourcesComponents;
import org.archiviststoolkit.model.validators.ATAbstractValidator;
import org.archiviststoolkit.util.ATPropertyValidationSupport;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.util.ValidationUtils;

public class BYU_ResourceComponentsValidator extends ATAbstractValidator {

	// Instance Creation ******************************************************

	/**
	 * Constructs an validator
	 *
	 * @param accession the accession to be validated
	 */
	public BYU_ResourceComponentsValidator(Accessions accession) {
		this.objectToValidate = accession;
	}

	public BYU_ResourceComponentsValidator() {
	}


	// Validation *************************************************************

	/**
	 * Validates this Validator's Order and returns the result
	 * as an instance of {@link com.jgoodies.validation.ValidationResult}.
	 *
	 * @return the ValidationResult of the accession validation
	 */
	public ValidationResult validate() {

		ResourcesComponents modelToValidate = (ResourcesComponents)objectToValidate;

		ATPropertyValidationSupport support =
				new ATPropertyValidationSupport(modelToValidate, "Resource Component");

		if (ValidationUtils.isBlank(modelToValidate.getLevel()))
			support.addError("Level", "is mandatory");

		//digitalObject date or title are manditory
		if (!checkForTitleOrDate(modelToValidate))
			support.addError("Title or Date", "is mandatory");

		checkInclusiveDates(support, modelToValidate);
		checkBulkDates(support, modelToValidate);
		checkInclusiveVsBulkDates(support, modelToValidate);

		checkForStringLengths(modelToValidate, support);

		return support.getResult();
	}

	private boolean checkForTitleOrDate(ResourcesComponents resourceComponent) {
		if (ValidationUtils.isNotBlank(resourceComponent.getTitle())) {
			return true;
		} else if (resourceComponent.getArchDescriptionDates().size() > 0) {
			return true;

		} else {
			return false;
		}
	}


}
