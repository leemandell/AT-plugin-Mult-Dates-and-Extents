/*
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
 * Created by JFormDesigner on Thu Sep 17 11:31:15 EDT 2009
 */

package edu.byu.plugins.editors.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.jgoodies.binding.PresentationModel;
import org.archiviststoolkit.mydomain.*;
import org.archiviststoolkit.swing.ATBasicComponentFactory;
import org.archiviststoolkit.swing.StandardEditor;
import org.archiviststoolkit.swing.SelectFromList;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.structure.ATFieldInfo;
import org.archiviststoolkit.exceptions.DomainEditorCreationException;
import org.archiviststoolkit.exceptions.ObjectNotRemovedException;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.dialog.LocationAssignmentAccessions;
import org.archiviststoolkit.dialog.ResourceLookup;
import org.archiviststoolkit.editor.AccessionFields;
import org.archiviststoolkit.ApplicationFrame;
import edu.byu.plugins.editors.ArchDescriptionDatesFields;
import edu.byu.plugins.editors.ArchDescPhysicalDescFields;
import edu.byu.plugins.editors.dialogs.BYU_ResourceLookup;

public class AccessionsBasicInfoPanel extends BYU_DomainEditorFields {

	private PresentationModel detailsModel;
	private Accessions accessionsModel;

	public AccessionsBasicInfoPanel(PresentationModel detailsModel) {
		this.detailsModel = detailsModel;
		initComponents();
		tableAccessionsResources.setClazzAndColumns(AccessionsResources.PROPERTYNAME_RESOURCE_IDENTIFIER,
				AccessionsResources.class,
				AccessionsResources.PROPERTYNAME_RESOURCE_IDENTIFIER,
				AccessionsResources.PROPERTYNAME_RESOURCE_TITLE);
	}

	private void changeRepositoryButtonActionPerformed() {
		Vector repositories = Repositories.getRepositoryList();
		Repositories currentRepostory = accessionsModel.getRepository();
		SelectFromList dialog = new SelectFromList(editorField.getParentEditor(), "Select a repository", repositories.toArray());
		dialog.setSelectedValue(currentRepostory);
		if (dialog.showDialog() == JOptionPane.OK_OPTION) {
			accessionsModel.setRepository((Repositories)dialog.getSelectedValue());
			setRepositoryText(accessionsModel);
		}
	}

	private void linkResourceActionPerformed(ActionEvent e) {
		BYU_ResourceLookup resourcePicker = new BYU_ResourceLookup(editorField.getParentEditor(), editorField);
		resourcePicker.showDialog(this);
	}

	private void removeResourceLinkActionPerformed(ActionEvent e) {
		try {
			this.removeRelatedTableRow(tableAccessionsResources, accessionsModel);
		} catch (ObjectNotRemovedException e1) {
			new ErrorDialog("Resource link not removed", e1).showDialog();
		}
	}

	public DomainSortableTable getTableAccessionsResources() {
		return tableAccessionsResources;
	}

	public JButton getChangeRepositoryButton() {
		return changeRepositoryButton;
	}

	private void addDeaccessionsActionPerformed() {
		Deaccessions newDeaccessions;
		DomainEditor dialogDeaccessions = null;
		try {
			dialogDeaccessions = DomainEditorFactory.getInstance().createDomainEditorWithParent(Deaccessions.class, editorField.getParentEditor(), false);
		} catch (DomainEditorCreationException e) {
			new ErrorDialog(editorField.getParentEditor(), "Error creating editor for Deaccessions", e).showDialog();
		}
		dialogDeaccessions.setNewRecord(true);

		boolean done = false;
		int returnStatus;
		while (!done) {
			newDeaccessions = new Deaccessions(accessionsModel);
			dialogDeaccessions.setModel(newDeaccessions, null);
			returnStatus = dialogDeaccessions.showDialog();
			if (returnStatus == JOptionPane.OK_OPTION) {
				accessionsModel.addDeaccessions(newDeaccessions);
				deaccessionsTable.updateCollection(accessionsModel.getDeaccessions());
				done = true;
			} else if (returnStatus == StandardEditor.OK_AND_ANOTHER_OPTION) {
				accessionsModel.addDeaccessions(newDeaccessions);
				deaccessionsTable.updateCollection(accessionsModel.getDeaccessions());
			} else {
				done = true;
			}
		}
	}

	private void removeDeaccessionActionPerformed() {
		try {
			this.removeRelatedTableRow(deaccessionsTable, accessionsModel);
		} catch (ObjectNotRemovedException e) {
			new ErrorDialog("Deaccession not removed", e).showDialog();
		}
		deaccessionsTable.updateCollection(((AccessionsResourcesCommon) accessionsModel).getDeaccessions());
	}

	private void locationsTableMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			editRelatedRecord(locationsTable, AccessionsLocations.class, true);
		}
	}

	private void addLocationButtonActionPerformed(ActionEvent e) {
		LocationAssignmentAccessions dialog = new LocationAssignmentAccessions(editorField.getParentEditor(), (AccessionFields)editorField);
		dialog.setMainHeaderByClass(Accessions.class);
		dialog.showDialog();
		locationsTable.updateCollection(((Accessions) accessionsModel).getLocations());
	}

	private void removeLocationButtonActionPerformed(ActionEvent e) {
		try {
			this.removeRelatedTableRow(locationsTable, accessionsModel);
		} catch (ObjectNotRemovedException e1) {
			new ErrorDialog("Location not removed", e1).showDialog();
		}
		locationsTable.updateCollection(((Accessions) accessionsModel).getLocations());
	}

	public DomainSortableTable getDateTable() {
		return dateTable;
	}

	public DomainSortableTable getDeaccessionsTable() {
		return deaccessionsTable;
	}

	public DomainSortableTable getLocationsTable() {
		return locationsTable;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public DomainSortableTable getPhysicalDescriptionsTable() {
		return physicalDescriptionsTable;
	}

	private void dateTableMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			try {
				DomainEditor domainEditor = new DomainEditor(ArchDescriptionDates.class, editorField.getParentEditor(), "Dates", new ArchDescriptionDatesFields());
				domainEditor.setCallingTable(dateTable);
				domainEditor.setNavigationButtonListeners(domainEditor);
				editRelatedRecord(dateTable, ArchDescriptionDates.class, true, domainEditor);
			} catch (UnsupportedTableModelException e1) {
				new ErrorDialog("Error creating editor for Dates", e1).showDialog();
			}
		}
	}

	private void addDateActionPerformed(ActionEvent e) {
		addDateActionPerformed(dateTable, accessionsModel);
	}

	private void removeDateActionPerformed(ActionEvent e) {
		try {
			this.removeRelatedTableRow(dateTable, accessionsModel);
		} catch (ObjectNotRemovedException e1) {
			new ErrorDialog("Date not removed", e1).showDialog();
		}
	}

	private void physicalDescriptionMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			try {
				DomainEditor domainEditor = new DomainEditor(ArchDescriptionPhysicalDescriptions.class, editorField.getParentEditor(), "Physical Descriptions", new ArchDescPhysicalDescFields());
				domainEditor.setCallingTable(physicalDescriptionsTable);
				domainEditor.setNavigationButtonListeners(domainEditor);
				editRelatedRecord(physicalDescriptionsTable, ArchDescriptionPhysicalDescriptions.class, true, domainEditor);
			} catch (UnsupportedTableModelException e1) {
				new ErrorDialog("Error creating editor for Dates", e1).showDialog();
			}
		}
	}

	private void addPhysicalDescriptionActionPerformed() {
		addPhysicalDescriptionActionPerformed(physicalDescriptionsTable, accessionsModel);
	}

	private void removePhysicalDescriptionActionPerformed() {
		try {
			this.removeRelatedTableRow(physicalDescriptionsTable, accessionsModel);
		} catch (ObjectNotRemovedException e1) {
			new ErrorDialog("Physical Description not removed", e1).showDialog();
		}
	}

	private void deaccessionsTableMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			editRelatedRecord(deaccessionsTable, Deaccessions.class, true);
		}
	}

//	public AccessionsBasicInfoPanel() {
//		initComponents();
//	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		panel11 = new JPanel();
		panel12 = new JPanel();
		label_accessionNumber1 = new JLabel();
		accessionNumber1 = ATBasicComponentFactory.createTextField(detailsModel.getModel(Accessions.PROPERTYNAME_ACCESSION_NUMBER_1));
		accessionNumber2 = ATBasicComponentFactory.createTextField(detailsModel.getModel(Accessions.PROPERTYNAME_ACCESSION_NUMBER_2));
		accessionNumber3 = ATBasicComponentFactory.createTextField(detailsModel.getModel(Accessions.PROPERTYNAME_ACCESSION_NUMBER_3));
		accessionNumber4 = ATBasicComponentFactory.createTextField(detailsModel.getModel(Accessions.PROPERTYNAME_ACCESSION_NUMBER_4));
		panel34 = new JPanel();
		label_accessionDate = new JLabel();
		accessionDate = ATBasicComponentFactory.createDateField(detailsModel.getModel(Accessions.PROPERTYNAME_ACCESSION_DATE));
		panel2 = new JPanel();
		panel15 = new JPanel();
		OtherAccessionsLabel = new JLabel();
		scrollPane4 = new JScrollPane();
		tableAccessionsResources = new DomainSortableTable();
		panel19 = new JPanel();
		linkResource = new JButton();
		removeResourceLink = new JButton();
		panel27 = new JPanel();
		label_resourceType = new JLabel();
		resourceType = ATBasicComponentFactory.createComboBox(detailsModel, Accessions.PROPERTYNAME_RESOURCE_TYPE, Accessions.class, 10);
		label_title = new JLabel();
		scrollPane42 = new JScrollPane();
		title = ATBasicComponentFactory.createTextArea(detailsModel.getModel(ArchDescription.PROPERTYNAME_TITLE));
		label_repositoryName5 = new JLabel();
		scrollPane9 = new JScrollPane();
		physicalDescriptionsTable = new DomainSortableTable(ArchDescriptionPhysicalDescriptions.class);
		panel23 = new JPanel();
		addPhysicalDescription = new JButton();
		removePhysicalDescription = new JButton();
		panel14 = new JPanel();
		label_repositoryName = new JLabel();
		repositoryName = new JTextField();
		changeRepositoryButton = new JButton();
		panel13 = new JPanel();
		label_repositoryName4 = new JLabel();
		scrollPane8 = new JScrollPane();
		dateTable = new DomainSortableTable(ArchDescriptionDates.class);
		panel22 = new JPanel();
		addDate = new JButton();
		removeDate = new JButton();
		label_repositoryName3 = new JLabel();
		scrollPane6 = new JScrollPane();
		deaccessionsTable = new DomainSortableTable(Deaccessions.class);
		panel18 = new JPanel();
		addDeaccessions = new JButton();
		removeDeaccession = new JButton();
		label_repositoryName2 = new JLabel();
		scrollPane7 = new JScrollPane();
		locationsTable = new DomainSortableTable(AccessionsLocations.class);
		panel26 = new JPanel();
		addButton = new JButton();
		removeLocationButton = new JButton();
		label_title2 = new JLabel();
		scrollPane43 = new JScrollPane();
		title2 = ATBasicComponentFactory.createTextArea(detailsModel.getModel(Accessions.PROPERTYNAME_GENERAL_ACCESSION_NOTE));
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setMinimumSize(new Dimension(640, 380));
		setBackground(new Color(200, 205, 232));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
			},
			RowSpec.decodeSpecs("fill:default:grow")));
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{1, 3}});

		//======== panel11 ========
		{
			panel11.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel11.setBackground(new Color(200, 205, 232));
			panel11.setMinimumSize(new Dimension(200, 206));
			panel11.setPreferredSize(new Dimension(200, 278));
			panel11.setLayout(new FormLayout(
				ColumnSpec.decodeSpecs("default:grow"),
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC
				}));

			//======== panel12 ========
			{
				panel12.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel12.setBackground(new Color(200, 205, 232));
				panel12.setMinimumSize(new Dimension(200, 22));
				panel12.setPreferredSize(new Dimension(200, 22));
				panel12.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("50px:grow"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("50px:grow"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("50px:grow"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("50px:grow")
					},
					RowSpec.decodeSpecs("default")));
				((FormLayout)panel12.getLayout()).setColumnGroups(new int[][] {{3, 5, 7, 9}});

				//---- label_accessionNumber1 ----
				label_accessionNumber1.setText("Accession No.");
				label_accessionNumber1.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_accessionNumber1, Accessions.class, Accessions.PROPERTYNAME_ACCESSION_NUMBER);
				panel12.add(label_accessionNumber1, cc.xywh(1, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

				//---- accessionNumber1 ----
				accessionNumber1.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel12.add(accessionNumber1, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

				//---- accessionNumber2 ----
				accessionNumber2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel12.add(accessionNumber2, cc.xy(5, 1));

				//---- accessionNumber3 ----
				accessionNumber3.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel12.add(accessionNumber3, cc.xy(7, 1));

				//---- accessionNumber4 ----
				accessionNumber4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel12.add(accessionNumber4, cc.xy(9, 1));
			}
			panel11.add(panel12, cc.xy(1, 1));

			//======== panel34 ========
			{
				panel34.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel34.setBackground(new Color(200, 205, 232));
				panel34.setPreferredSize(new Dimension(200, 22));
				panel34.setLayout(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, FormSpec.NO_GROW),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
					},
					RowSpec.decodeSpecs("default")));

				//---- label_accessionDate ----
				label_accessionDate.setText("Accession Date");
				label_accessionDate.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_accessionDate, Accessions.class, Accessions.PROPERTYNAME_ACCESSION_DATE);
				panel34.add(label_accessionDate, cc.xy(1, 1));

				//---- accessionDate ----
				accessionDate.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				accessionDate.setColumns(10);
				panel34.add(accessionDate, cc.xywh(3, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
			}
			panel11.add(panel34, cc.xywh(1, 3, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

			//======== panel2 ========
			{
				panel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
				panel2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel2.setBackground(new Color(182, 187, 212));
				panel2.setMinimumSize(new Dimension(200, 92));
				panel2.setPreferredSize(new Dimension(200, 119));
				panel2.setLayout(new FormLayout(
					"default:grow",
					"fill:default:grow"));

				//======== panel15 ========
				{
					panel15.setOpaque(false);
					panel15.setBorder(Borders.DLU2_BORDER);
					panel15.setMinimumSize(new Dimension(200, 88));
					panel15.setPreferredSize(new Dimension(200, 115));
					panel15.setLayout(new FormLayout(
						ColumnSpec.decodeSpecs("default:grow"),
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- OtherAccessionsLabel ----
					OtherAccessionsLabel.setText("Resources Linked to this accession");
					OtherAccessionsLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					panel15.add(OtherAccessionsLabel, cc.xy(1, 1));

					//======== scrollPane4 ========
					{
						scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						scrollPane4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						scrollPane4.setPreferredSize(new Dimension(200, 54));

						//---- tableAccessionsResources ----
						tableAccessionsResources.setPreferredScrollableViewportSize(new Dimension(450, 50));
						tableAccessionsResources.setFocusable(false);
						tableAccessionsResources.setSelectionBackground(Color.magenta);
						scrollPane4.setViewportView(tableAccessionsResources);
					}
					panel15.add(scrollPane4, cc.xy(1, 3));

					//======== panel19 ========
					{
						panel19.setOpaque(false);
						panel19.setMinimumSize(new Dimension(100, 29));
						panel19.setLayout(new FormLayout(
							new ColumnSpec[] {
								FormFactory.DEFAULT_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.DEFAULT_COLSPEC
							},
							RowSpec.decodeSpecs("default")));

						//---- linkResource ----
						linkResource.setText("Link Resource");
						linkResource.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						linkResource.setOpaque(false);
						linkResource.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								linkResourceActionPerformed(e);
							}
						});
						panel19.add(linkResource, cc.xy(1, 1));

						//---- removeResourceLink ----
						removeResourceLink.setText("Remove Link");
						removeResourceLink.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						removeResourceLink.setOpaque(false);
						removeResourceLink.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								removeResourceLinkActionPerformed(e);
							}
						});
						panel19.add(removeResourceLink, cc.xy(3, 1));
					}
					panel15.add(panel19, cc.xywh(1, 5, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
				}
				panel2.add(panel15, cc.xy(1, 1));
			}
			panel11.add(panel2, cc.xy(1, 5));

			//======== panel27 ========
			{
				panel27.setOpaque(false);
				panel27.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
					},
					RowSpec.decodeSpecs("default")));

				//---- label_resourceType ----
				label_resourceType.setText("Resource Type");
				label_resourceType.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_resourceType, Accessions.class, Accessions.PROPERTYNAME_RESOURCE_TYPE);
				panel27.add(label_resourceType, cc.xywh(1, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

				//---- resourceType ----
				resourceType.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				resourceType.setOpaque(false);
				panel27.add(resourceType, cc.xywh(3, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
			}
			panel11.add(panel27, cc.xy(1, 7));

			//---- label_title ----
			label_title.setText("Title");
			label_title.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			ATFieldInfo.assignLabelInfo(label_title, Accessions.class, Accessions.PROPERTYNAME_TITLE);
			panel11.add(label_title, cc.xy(1, 9));

			//======== scrollPane42 ========
			{
				scrollPane42.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane42.setPreferredSize(new Dimension(200, 68));
				scrollPane42.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				//---- title ----
				title.setRows(4);
				title.setLineWrap(true);
				title.setWrapStyleWord(true);
				title.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane42.setViewportView(title);
			}
			panel11.add(scrollPane42, cc.xy(1, 11));

			//---- label_repositoryName5 ----
			label_repositoryName5.setText("Physical Description");
			label_repositoryName5.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel11.add(label_repositoryName5, cc.xy(1, 13));

			//======== scrollPane9 ========
			{
				scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane9.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane9.setPreferredSize(new Dimension(200, 104));

				//---- physicalDescriptionsTable ----
				physicalDescriptionsTable.setPreferredScrollableViewportSize(new Dimension(200, 100));
				physicalDescriptionsTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						physicalDescriptionMouseClicked(e);
					}
				});
				scrollPane9.setViewportView(physicalDescriptionsTable);
			}
			panel11.add(scrollPane9, cc.xywh(1, 15, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

			//======== panel23 ========
			{
				panel23.setBackground(new Color(231, 188, 251));
				panel23.setOpaque(false);
				panel23.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel23.setMinimumSize(new Dimension(100, 29));
				panel23.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- addPhysicalDescription ----
				addPhysicalDescription.setText("Add Description");
				addPhysicalDescription.setOpaque(false);
				addPhysicalDescription.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				addPhysicalDescription.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addPhysicalDescriptionActionPerformed();
					}
				});
				panel23.add(addPhysicalDescription, cc.xy(1, 1));

				//---- removePhysicalDescription ----
				removePhysicalDescription.setText("Remove Description");
				removePhysicalDescription.setOpaque(false);
				removePhysicalDescription.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				removePhysicalDescription.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removePhysicalDescriptionActionPerformed();
					}
				});
				panel23.add(removePhysicalDescription, cc.xy(3, 1));
			}
			panel11.add(panel23, cc.xywh(1, 17, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

			//======== panel14 ========
			{
				panel14.setOpaque(false);
				panel14.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- label_repositoryName ----
				label_repositoryName.setText("Repository");
				label_repositoryName.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_repositoryName, Accessions.class, Accessions.PROPERTYNAME_REPOSITORY);
				panel14.add(label_repositoryName, cc.xy(1, 1));

				//---- repositoryName ----
				repositoryName.setEditable(false);
				repositoryName.setFocusable(false);
				repositoryName.setBorder(null);
				repositoryName.setOpaque(false);
				repositoryName.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				repositoryName.setHorizontalAlignment(SwingConstants.LEFT);
				panel14.add(repositoryName, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

				//---- changeRepositoryButton ----
				changeRepositoryButton.setText("Change Repository");
				changeRepositoryButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				changeRepositoryButton.setOpaque(false);
				changeRepositoryButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						changeRepositoryButtonActionPerformed();
					}
				});
				panel14.add(changeRepositoryButton, cc.xy(5, 1));
			}
			panel11.add(panel14, cc.xy(1, 19));
		}
		add(panel11, cc.xy(1, 1));

		//======== panel13 ========
		{
			panel13.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel13.setBackground(new Color(200, 205, 232));
			panel13.setPreferredSize(new Dimension(200, 317));
			panel13.setLayout(new FormLayout(
				ColumnSpec.decodeSpecs("default:grow"),
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, 0.4),
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, 0.4),
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, 0.19999999999999998)
				}));
			((FormLayout)panel13.getLayout()).setRowGroups(new int[][] {{9, 15, 21}});

			//---- label_repositoryName4 ----
			label_repositoryName4.setText("Dates");
			label_repositoryName4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel13.add(label_repositoryName4, cc.xy(1, 1));

			//======== scrollPane8 ========
			{
				scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane8.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane8.setPreferredSize(new Dimension(200, 104));

				//---- dateTable ----
				dateTable.setPreferredScrollableViewportSize(new Dimension(200, 100));
				dateTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						dateTableMouseClicked(e);
					}
				});
				scrollPane8.setViewportView(dateTable);
			}
			panel13.add(scrollPane8, cc.xywh(1, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

			//======== panel22 ========
			{
				panel22.setBackground(new Color(231, 188, 251));
				panel22.setOpaque(false);
				panel22.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel22.setMinimumSize(new Dimension(100, 29));
				panel22.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- addDate ----
				addDate.setText("Add Date");
				addDate.setOpaque(false);
				addDate.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				addDate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addDateActionPerformed(e);
					}
				});
				panel22.add(addDate, cc.xy(1, 1));

				//---- removeDate ----
				removeDate.setText("Remove Date");
				removeDate.setOpaque(false);
				removeDate.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				removeDate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeDateActionPerformed(e);
					}
				});
				panel22.add(removeDate, cc.xy(3, 1));
			}
			panel13.add(panel22, cc.xywh(1, 5, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

			//---- label_repositoryName3 ----
			label_repositoryName3.setText("Deaccessions");
			label_repositoryName3.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel13.add(label_repositoryName3, cc.xy(1, 7));

			//======== scrollPane6 ========
			{
				scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane6.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane6.setPreferredSize(new Dimension(200, 104));

				//---- deaccessionsTable ----
				deaccessionsTable.setPreferredScrollableViewportSize(new Dimension(200, 100));
				deaccessionsTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						deaccessionsTableMouseClicked(e);
					}
				});
				scrollPane6.setViewportView(deaccessionsTable);
			}
			panel13.add(scrollPane6, cc.xywh(1, 9, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

			//======== panel18 ========
			{
				panel18.setBackground(new Color(231, 188, 251));
				panel18.setOpaque(false);
				panel18.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel18.setMinimumSize(new Dimension(100, 29));
				panel18.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- addDeaccessions ----
				addDeaccessions.setText("Add Deaccession");
				addDeaccessions.setOpaque(false);
				addDeaccessions.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				addDeaccessions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addDeaccessionsActionPerformed();
					}
				});
				panel18.add(addDeaccessions, cc.xy(1, 1));

				//---- removeDeaccession ----
				removeDeaccession.setText("Remove Deaccession");
				removeDeaccession.setOpaque(false);
				removeDeaccession.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				removeDeaccession.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeDeaccessionActionPerformed();
					}
				});
				panel18.add(removeDeaccession, cc.xy(3, 1));
			}
			panel13.add(panel18, cc.xywh(1, 11, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

			//---- label_repositoryName2 ----
			label_repositoryName2.setText("Locations");
			label_repositoryName2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			ATFieldInfo.assignLabelInfo(label_repositoryName2, Accessions.class, Accessions.PROPERTYNAME_LOCATIONS);
			panel13.add(label_repositoryName2, cc.xy(1, 13));

			//======== scrollPane7 ========
			{
				scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane7.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane7.setPreferredSize(new Dimension(200, 64));

				//---- locationsTable ----
				locationsTable.setPreferredScrollableViewportSize(new Dimension(200, 60));
				locationsTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						locationsTableMouseClicked(e);
					}
				});
				scrollPane7.setViewportView(locationsTable);
			}
			panel13.add(scrollPane7, cc.xywh(1, 15, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

			//======== panel26 ========
			{
				panel26.setOpaque(false);
				panel26.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- addButton ----
				addButton.setText("Add Location");
				addButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				addButton.setOpaque(false);
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addLocationButtonActionPerformed(e);
					}
				});
				panel26.add(addButton, cc.xy(1, 1));

				//---- removeLocationButton ----
				removeLocationButton.setText("Remove Location");
				removeLocationButton.setOpaque(false);
				removeLocationButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				removeLocationButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeLocationButtonActionPerformed(e);
					}
				});
				panel26.add(removeLocationButton, cc.xy(3, 1));
			}
			panel13.add(panel26, cc.xywh(1, 17, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

			//---- label_title2 ----
			label_title2.setText("General accession note");
			label_title2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			ATFieldInfo.assignLabelInfo(label_title2, Accessions.class, Accessions.PROPERTYNAME_GENERAL_ACCESSION_NOTE);
			panel13.add(label_title2, cc.xy(1, 19));

			//======== scrollPane43 ========
			{
				scrollPane43.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane43.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane43.setPreferredSize(new Dimension(200, 68));

				//---- title2 ----
				title2.setRows(4);
				title2.setLineWrap(true);
				title2.setWrapStyleWord(true);
				title2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				scrollPane43.setViewportView(title2);
			}
			panel13.add(scrollPane43, cc.xy(1, 21));
		}
		add(panel13, cc.xywh(3, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel panel11;
	private JPanel panel12;
	private JLabel label_accessionNumber1;
	public JTextField accessionNumber1;
	public JTextField accessionNumber2;
	public JTextField accessionNumber3;
	public JTextField accessionNumber4;
	private JPanel panel34;
	private JLabel label_accessionDate;
	private JFormattedTextField accessionDate;
	private JPanel panel2;
	private JPanel panel15;
	private JLabel OtherAccessionsLabel;
	private JScrollPane scrollPane4;
	private DomainSortableTable tableAccessionsResources;
	private JPanel panel19;
	private JButton linkResource;
	private JButton removeResourceLink;
	private JPanel panel27;
	private JLabel label_resourceType;
	public JComboBox resourceType;
	private JLabel label_title;
	private JScrollPane scrollPane42;
	public JTextArea title;
	private JLabel label_repositoryName5;
	private JScrollPane scrollPane9;
	private DomainSortableTable physicalDescriptionsTable;
	private JPanel panel23;
	private JButton addPhysicalDescription;
	private JButton removePhysicalDescription;
	private JPanel panel14;
	private JLabel label_repositoryName;
	public JTextField repositoryName;
	private JButton changeRepositoryButton;
	private JPanel panel13;
	private JLabel label_repositoryName4;
	private JScrollPane scrollPane8;
	private DomainSortableTable dateTable;
	private JPanel panel22;
	private JButton addDate;
	private JButton removeDate;
	private JLabel label_repositoryName3;
	private JScrollPane scrollPane6;
	private DomainSortableTable deaccessionsTable;
	private JPanel panel18;
	private JButton addDeaccessions;
	private JButton removeDeaccession;
	private JLabel label_repositoryName2;
	private JScrollPane scrollPane7;
	private DomainSortableTable locationsTable;
	private JPanel panel26;
	private JButton addButton;
	private JButton removeLocationButton;
	private JLabel label_title2;
	private JScrollPane scrollPane43;
	public JTextArea title2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	public void setModel(Accessions accessionsModel) {
		this.accessionsModel = accessionsModel;
		tableAccessionsResources.updateCollection(this.accessionsModel.getResources());
		deaccessionsTable.updateCollection(this.accessionsModel.getDeaccessions());
		locationsTable.updateCollection(this.accessionsModel.getLocations());
		dateTable.updateCollection(this.accessionsModel.getArchDescriptionDates());
		physicalDescriptionsTable.updateCollection(this.accessionsModel.getPhysicalDesctiptions());
		setRepositoryText(this.accessionsModel);

	}

	private void setRepositoryText(Accessions model) {
		if (model.getRepository() == null) {
			this.repositoryName.setText("");
		} else {
			this.repositoryName.setText(model.getRepository().getShortName());
		}
	}

	public Component getInitialFocusComponent() {
		return null;
	}


}
