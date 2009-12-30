
package edu.byu.plugins.editors;

import org.java.plugin.Plugin;
import org.archiviststoolkit.plugin.ATPlugin;
import org.archiviststoolkit.ApplicationFrame;
import org.archiviststoolkit.editor.*;
import org.archiviststoolkit.model.ArchDescriptionAnalogInstances;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.model.validators.ValidatorFactory;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.mydomain.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import edu.byu.plugins.editors.validators.*;
import edu.byu.plugins.editors.panels.AccessionsBasicInfoPanel;
import edu.byu.plugins.editors.panels.ResourceBasicInfoPanel;
import edu.byu.plugins.editors.panels.ResourceComponentBasicInfoPanel;
import edu.byu.plugins.editors.panels.DigitalObjectBasicInfoPanel;

/**
 * Archivists' Toolkit(TM) Copyright � 2005-2007 Regents of the University of California, New York University, & Five Colleges, Inc.
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
 * A simple plugin to test the functionality of 
 *
 * Created by IntelliJ IDEA.
 *                  
 * @author: Nathan Stevens
 * Date: Feb 10, 2009
 * Time: 1:07:45 PM
 */

public class EditorPanels extends Plugin implements ATPlugin {
    protected ApplicationFrame mainFrame;
	protected DomainEditorFields editorField;
//	protected DomainObject record;



	private AccessionsBasicInfoPanel accessionsBasicInfoPanel = null;
	private ResourceBasicInfoPanel resourceBasicInfoPanel = null;
	private ResourceComponentBasicInfoPanel resourceComponentBasicInfoPanel = null;
	private DigitalObjectBasicInfoPanel digitalObjectBasicInfoPanel = null;

	private JTable callingTable;
	private int selectedRow;
    protected ArchDescriptionAnalogInstances analogInstance;

	// the default constructor
    public EditorPanels() {
		System.out.println("Editor Panels instantiated");
	}

	// get the category this plugin belongs to
    public String getCategory() {
        return ATPlugin.EMBEDDED_EDITOR_CATEGORY;
        //return ATPlugin.DEFAULT_CATEGORY + " " + ATPlugin.EMBEDDED_EDITOR_CATEGORY;
        //return ATPlugin.IMPORT_CATEGORY;
    }

    // get the name of this plugin
    public String getName() {
        return "BYU Resource Editor Panel";
    }

    // Method to set the main frame
    public void setApplicationFrame(ApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // Method that display the window
    public void showPlugin() {
    }

    // method to display a plugin that needs a parent frame
    public void showPlugin(Frame owner) {
    }

    // method to display a plugin that needs a parent dialog
    public void showPlugin(Dialog owner) {
	}

    // Method to return the jpanels for plugins that are in an AT editor
    public HashMap getEmbeddedPanels() {

		HashMap<String, JPanel> panels = new HashMap<String,JPanel>();
		System.out.println("get embedded panels");

		if(editorField != null) {
			if (editorField instanceof AccessionFields) {
				System.out.println(editorField);
				if (accessionsBasicInfoPanel == null) {
					System.out.println("Panel null so creating new one" + "\n");
					accessionsBasicInfoPanel = new AccessionsBasicInfoPanel(editorField.detailsModel);
				}
				accessionsBasicInfoPanel.setEditorField(editorField);
				panels.put("Basic Information::0::yes", accessionsBasicInfoPanel);

			} else if (editorField instanceof ResourceFields) {
				System.out.println(editorField);
				if (resourceBasicInfoPanel == null) {
					System.out.println("Panel null so creating new one" + "\n");
					resourceBasicInfoPanel = new ResourceBasicInfoPanel(editorField.detailsModel);
				}
				resourceBasicInfoPanel.setEditorField(editorField);
				panels.put("Basic Information::0::yes", resourceBasicInfoPanel);

			} else if (editorField instanceof ResourceComponentsFields) {
				System.out.println(editorField);
				if (resourceComponentBasicInfoPanel == null) {
					System.out.println("Panel null so creating new one" + "\n");
					resourceComponentBasicInfoPanel = new ResourceComponentBasicInfoPanel(editorField.detailsModel);
				}
				resourceComponentBasicInfoPanel.setEditorField(editorField);
				panels.put("Basic Information::0::yes", resourceComponentBasicInfoPanel);

			} else if (editorField instanceof DigitalObjectFields) {
				System.out.println(editorField);
//				if (digitalObjectBasicInfoPanel == null) {
					System.out.println("Panel null so creating new one" + "\n");
					digitalObjectBasicInfoPanel = new DigitalObjectBasicInfoPanel(editorField.detailsModel);
//				}
				digitalObjectBasicInfoPanel.setEditorField(editorField);
				panels.put("Basic Information::0::yes", digitalObjectBasicInfoPanel);
			} else {
				System.out.println("We fell through the cracks");
			}

		}

//		try {
//			accessionsBasicInfoPanel = new temp(ATPluginUtils.getDetailsModel(Accessions.class));
//			panels.put("BYU Resource::0::no", accessionsBasicInfoPanel);
//		} catch (UnsupportedDomainEditorException e) {
//			new ErrorDialog("", e).showDialog();
//		}

		return panels;
    }

	public void setEditorField(ArchDescriptionFields archDescriptionFields) {
		this.editorField = archDescriptionFields;
	}

	// Method to set the editor field component
 	public void setEditorField(DomainEditorFields domainEditorFields) {
		this.editorField = domainEditorFields;
	}

	/**
     * Method to set the domain object for this plugin
     */
    public void setModel(DomainObject domainObject, InfiniteProgressPanel monitor) {
//        record = domainObject;
		System.out.println("setModel: " + Integer.toHexString(System.identityHashCode(domainObject)));
		if (domainObject instanceof Accessions) {
			Accessions accession = (Accessions)domainObject;
			System.out.println("Accession: " + accession);
			accessionsBasicInfoPanel.setModel(accession);

		} else if (domainObject instanceof Resources){
			Resources resource = (Resources)domainObject;
			System.out.println("Resource: " + Integer.toHexString(System.identityHashCode(resource)));
			resourceBasicInfoPanel.setModel(resource);

		} else if (domainObject instanceof ResourcesComponents) {
			ResourcesComponents component = (ResourcesComponents)domainObject;
			System.out.println("Component: " + component);
			resourceComponentBasicInfoPanel.setModel(component);

		} else if (domainObject instanceof DigitalObjects) {
			DigitalObjects digitalObject = (DigitalObjects)domainObject;
			System.out.println("Digital Object: " + digitalObject);
			digitalObjectBasicInfoPanel.setModel(digitalObject);
		}
//		accessionsBasicInfoPanel.setBean(resource);
//		resourceBasicInfoPanel.setModel((Resources)record);
    }

    /**
     * Method to get the table from which the record was selected
     * @param callingTable The table containing the record
     */
    public void setCallingTable(JTable callingTable) {
		this.callingTable = callingTable;
	}

    /**
     * Method to set the selected row of the calling table
     * @param selectedRow
     */
    public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

    /**
     * Method to set the current record number along with the total number of records
     * @param recordNumber The current record number
     * @param totalRecords The total number of records
     */
    public void setRecordPositionText(int recordNumber, int totalRecords) { }

    // Method to do a specific task in the plugin
    public void doTask(String task) {
    }

    // Method to get the list of specific task the plugin can perform
    public String[] getTaskList() {
        return null;
    }

    // Method to return the editor type for this plugin
    public String getEditorType() {
//		return ATPlugin.DIGITALOBJECT_EDITOR;
//		return ATPlugin.RESOURCE_EDITOR;
//		return ATPlugin.RESOURCE_COMPONENT_EDITOR;
//		return ATPlugin.RESOURCE_EDITOR + " " + ATPlugin.ACCESSION_EDITOR;
		return ATPlugin.ACCESSION_EDITOR + " " + ATPlugin.RESOURCE_EDITOR + " " + ATPlugin.RESOURCE_COMPONENT_EDITOR + ATPlugin.DIGITALOBJECT_EDITOR;
//		return null;
    }

    // code that is executed when plugin starts. not used here
    protected void doStart()  {
		ValidatorFactory validatorFactory = ValidatorFactory.getInstance();
		validatorFactory.addValidator(ArchDescriptionDates.class, new ArchDescriptionDatesValidator());
		validatorFactory.addValidator(ArchDescriptionPhysicalDescriptions.class, new ArchDescriptionPhysicalDescriptionsValidator());
		validatorFactory.addValidator(Resources.class, new BYU_ResourcesValidator());
		validatorFactory.addValidator(DigitalObjects.class, new BYU_DigitalObjectValidator());
		validatorFactory.addValidator(Accessions.class, new BYU_AccessionsValidator());
		validatorFactory.addValidator(ResourcesComponents.class, new BYU_ResourceComponentsValidator());
	}

    // code that is executed after plugin stops. not used here
    protected void doStop()  { }

    // main method for testing only
    public static void main(String[] args) {
        EditorPanels demo = new EditorPanels();
        demo.showPlugin();
    }
}
