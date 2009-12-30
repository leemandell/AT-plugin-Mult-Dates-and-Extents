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
 * Created by JFormDesigner on Thu Oct 22 13:20:58 EDT 2009
 */

package edu.byu.plugins.editors.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.jgoodies.binding.PresentationModel;
import org.archiviststoolkit.mydomain.*;
import org.archiviststoolkit.swing.*;
import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.structure.ATFieldInfo;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.exceptions.ObjectNotRemovedException;
import org.archiviststoolkit.exceptions.DomainEditorCreationException;
import org.archiviststoolkit.ApplicationFrame;
import org.archiviststoolkit.editor.ArchDescriptionInstancesEditor;
import edu.byu.plugins.editors.ArchDescriptionDatesFields;

public class DigitalObjectBasicInfoPanel extends BYU_DomainEditorFields {

	private DigitalObjects digitalObject;


	public DigitalObjectBasicInfoPanel(PresentationModel detailsModel) {
		this.detailsModel = detailsModel;
		initComponents();
		resourcesTable.setClazzAndColumns(DigitalObjectsResources.PROPERTYNAME_RESOURCE_IDENTIFIER,
				DigitalObjectsResources.class,
				DigitalObjectsResources.PROPERTYNAME_RESOURCE_IDENTIFIER,
				DigitalObjectsResources.PROPERTYNAME_RESOURCE_TITLE);

	}

	public Component getInitialFocusComponent() {
		return label;
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
		System.out.println("Resource add date: " + this.digitalObject);
		addDateActionPerformed(dateTable, digitalObject);
	}

	private void removeDateActionPerformed(ActionEvent e) {
		try {
			this.removeRelatedTableRow(dateTable, digitalObject);
		} catch (ObjectNotRemovedException e1) {
			new ErrorDialog("Date not removed", e1).showDialog();
		}
	}

	private void changeRepositoryButtonActionPerformed() {
		Vector repositories = Repositories.getRepositoryList();
		ImageIcon icon = null;
		Repositories currentRepostory = ((Resources) editorField.getModel()).getRepository();
//		Resources model = (Resources) this.getModel();
        SelectFromList dialog = new SelectFromList(this.getParentEditor(), "Select a repository", repositories.toArray());
        dialog.setSelectedValue(currentRepostory);
        if (dialog.showDialog() == JOptionPane.OK_OPTION) {
            digitalObject.setRepository((Repositories)dialog.getSelectedValue());
            setRepositoryText(digitalObject);
            ApplicationFrame.getInstance().setRecordDirty(); // set the record dirty
        }
	}

	private void setRepositoryText(DigitalObjects model) {
		if (model.getRepository() == null) {
			this.repositoryName.setText("");
		} else {
			this.repositoryName.setText(model.getRepository().getShortName());
		}
	}

	private void fileVersionTableMouseClicked(MouseEvent e) {
		handleTableMouseClick(e, fileVersionsTable, FileVersions.class);
	}

	private void addFileVersionButtonActionPerformed() {
//		DigitalObjects digitalObjectModel = (DigitalObjects) super.getModel();
		FileVersions newFileVersions;
		DomainEditor dialogFileVersions = null;
		try {
			dialogFileVersions = DomainEditorFactory.getInstance().createDomainEditorWithParent(FileVersions.class, getParentEditor(), fileVersionsTable);
		} catch (DomainEditorCreationException e) {
			new ErrorDialog(getParentEditor(), "Error creating editor for FileVersions", e).showDialog();

		}
		dialogFileVersions.setNewRecord(true);

        int returnStatus;
        Boolean done = false;
        while (!done) {
            newFileVersions = new FileVersions(digitalObject);
            dialogFileVersions.setModel(newFileVersions, null);
            returnStatus = dialogFileVersions.showDialog();
            if (returnStatus == JOptionPane.OK_OPTION) {
                digitalObject.addFileVersion(newFileVersions);
                fileVersionsTable.getEventList().add(newFileVersions);
                done = true;
            } else if (returnStatus == StandardEditor.OK_AND_ANOTHER_OPTION) {
                digitalObject.addFileVersion(newFileVersions);
                fileVersionsTable.getEventList().add(newFileVersions);
            } else {
                done = true;
            }
        }
        dialogFileVersions.setNewRecord(false);
	}

	private void removeFileVersionButtonActionPerformed() {
		try {
			this.removeRelatedTableRow(fileVersionsTable, (ArchDescription)super.getModel());
		} catch (ObjectNotRemovedException e) {
			new ErrorDialog("File version not removed", e).showDialog();
		}
	}

	public JButton getChangeRepositoryButton() {
		return changeRepositoryButton;
	}

	public DomainSortableTable getFileVersionsTable() {
		return fileVersionsTable;
	}

	public JButton getRemoveFileVersionButton() {
		return removeFileVersionButton;
	}

	public DomainSortableTable getDateTable() {
		return dateTable;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		panel16 = new JPanel();
		panel1 = new JPanel();
		label_resourcesLanguageCode4 = new JLabel();
		label = ATBasicComponentFactory.createTextField(detailsModel.getModel(DigitalObjects.PROPERTYNAME_LABEL),false);
		panel19 = new JPanel();
		label_resourcesTitle = new JLabel();
		scrollPane42 = new JScrollPane();
		title = ATBasicComponentFactory.createTextArea(detailsModel.getModel(ArchDescription.PROPERTYNAME_TITLE),false);
		resourcesPanel = new JPanel();
		resourcesLabel = new JLabel();
		scrollPane4 = new JScrollPane();
		resourcesTable = new DomainSortableTable();
		panel13 = new JPanel();
		panel17 = new JPanel();
		label_repositoryName4 = new JLabel();
		scrollPane8 = new JScrollPane();
		dateTable = new DomainSortableTable(ArchDescriptionDates.class);
		panel22 = new JPanel();
		addDate = new JButton();
		removeDate = new JButton();
		digitalObjectResourceRecordOnly = new JPanel();
		restrictionsApply = ATBasicComponentFactory.createCheckBox(detailsModel, ArchDescription.PROPERTYNAME_RESTRICTIONS_APPLY, DigitalObjects.class);
		label_resourcesLanguageCode3 = new JLabel();
		objectType = ATBasicComponentFactory.createComboBox(detailsModel, DigitalObjects.PROPERTYNAME_OBJECT_TYPE, DigitalObjects.class);
		actuateLabel2 = new JLabel();
		scrollPane43 = new JScrollPane();
		title2 = ATBasicComponentFactory.createTextArea(detailsModel.getModel(DigitalObjects.PROPERTYNAME_METS_IDENTIFIER),false);
		actuateLabel = new JLabel();
		actuate = ATBasicComponentFactory.createComboBox(detailsModel, DigitalObjects.PROPERTYNAME_EAD_DAO_ACTUATE, DigitalObjects.class);
		showLabel = new JLabel();
		show = ATBasicComponentFactory.createComboBox(detailsModel, DigitalObjects.PROPERTYNAME_EAD_DAO_SHOW, DigitalObjects.class);
		componentIDPanel = new JPanel();
		componentLabel1 = new JLabel();
		dateExpression2 = ATBasicComponentFactory.createTextField(detailsModel.getModel(DigitalObjects.PROPERTYNAME_COMPONENT_ID),false);
		panel5 = new JPanel();
		label_resourcesLanguageCode = new JLabel();
		languageCode = ATBasicComponentFactory.createComboBox(detailsModel, DigitalObjects.PROPERTYNAME_LANGUAGE_CODE, DigitalObjects.class);
		repositoryPanel = new JPanel();
		label_repositoryName = new JLabel();
		repositoryName = new JTextField();
		changeRepositoryButton = new JButton();
		panel2 = new JPanel();
		label1 = new JLabel();
		scrollPane6 = new JScrollPane();
		fileVersionsTable = new DomainSortableTable(FileVersions.class, FileVersions.PROPERTYNAME_FILE_VERSIONS_USE_STATEMENT);
		panel29 = new JPanel();
		addFileVersionButton = new JButton();
		removeFileVersionButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setBackground(new Color(200, 205, 232));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
			},
			new RowSpec[] {
				new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC
			}));
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{1, 3}});

		//======== panel16 ========
		{
			panel16.setOpaque(false);
			panel16.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel16.setLayout(new FormLayout(
				ColumnSpec.decodeSpecs("default:grow"),
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, 0.30000000000000004)
				}));

			//======== panel1 ========
			{
				panel1.setOpaque(false);
				panel1.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel1.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
					},
					RowSpec.decodeSpecs("default")));

				//---- label_resourcesLanguageCode4 ----
				label_resourcesLanguageCode4.setText("Label");
				label_resourcesLanguageCode4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_resourcesLanguageCode4, DigitalObjects.class, DigitalObjects.PROPERTYNAME_LABEL);
				panel1.add(label_resourcesLanguageCode4, cc.xy(1, 1));

				//---- label ----
				label.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel1.add(label, new CellConstraints(3, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.TOP, new Insets( 0, 0, 0, 5)));
			}
			panel16.add(panel1, cc.xy(1, 1));

			//======== panel19 ========
			{
				panel19.setOpaque(false);
				panel19.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel19.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
					},
					new RowSpec[] {
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC
					}));

				//---- label_resourcesTitle ----
				label_resourcesTitle.setText("Title");
				label_resourcesTitle.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_resourcesTitle, DigitalObjects.class, DigitalObjects.PROPERTYNAME_TITLE);
				panel19.add(label_resourcesTitle, cc.xy(1, 1));

				//======== scrollPane42 ========
				{
					scrollPane42.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					scrollPane42.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));

					//---- title ----
					title.setRows(4);
					title.setLineWrap(true);
					title.setWrapStyleWord(true);
					title.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					scrollPane42.setViewportView(title);
				}
				panel19.add(scrollPane42, cc.xywh(1, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

				//======== resourcesPanel ========
				{
					resourcesPanel.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					resourcesPanel.setOpaque(false);
					resourcesPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
					resourcesPanel.setLayout(new FormLayout(
						ColumnSpec.decodeSpecs("default:grow"),
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						}));

					//---- resourcesLabel ----
					resourcesLabel.setText("Resource Linked to this Digital Object");
					resourcesLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					resourcesPanel.add(resourcesLabel, cc.xy(1, 1));

					//======== scrollPane4 ========
					{
						scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						scrollPane4.setPreferredSize(new Dimension(300, 50));
						scrollPane4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));

						//---- resourcesTable ----
						resourcesTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
						resourcesTable.setFocusable(false);
						scrollPane4.setViewportView(resourcesTable);
					}
					resourcesPanel.add(scrollPane4, cc.xywh(1, 3, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
				}
				panel19.add(resourcesPanel, cc.xywh(1, 5, 3, 1));
			}
			panel16.add(panel19, cc.xy(1, 3));
		}
		add(panel16, cc.xy(1, 1));

		//======== panel13 ========
		{
			panel13.setOpaque(false);
			panel13.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel13.setLayout(new FormLayout(
				"left:default:grow",
				"fill:default:grow"));

			//======== panel17 ========
			{
				panel17.setOpaque(false);
				panel17.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel17.setLayout(new FormLayout(
					ColumnSpec.decodeSpecs("default:grow"),
					new RowSpec[] {
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.TOP, Sizes.DEFAULT, FormSpec.NO_GROW),
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.TOP, Sizes.DEFAULT, FormSpec.NO_GROW)
					}));

				//---- label_repositoryName4 ----
				label_repositoryName4.setText("Dates");
				label_repositoryName4.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel17.add(label_repositoryName4, cc.xy(1, 1));

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
				panel17.add(scrollPane8, cc.xy(1, 3));

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
				panel17.add(panel22, cc.xywh(1, 5, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

				//======== digitalObjectResourceRecordOnly ========
				{
					digitalObjectResourceRecordOnly.setOpaque(false);
					digitalObjectResourceRecordOnly.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.TOP, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- restrictionsApply ----
					restrictionsApply.setBackground(new Color(231, 188, 251));
					restrictionsApply.setText("Restrictions Apply");
					restrictionsApply.setOpaque(false);
					restrictionsApply.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					restrictionsApply.setText(ATFieldInfo.getLabel(DigitalObjects.class, ArchDescription.PROPERTYNAME_RESTRICTIONS_APPLY));
					digitalObjectResourceRecordOnly.add(restrictionsApply, cc.xywh(1, 1, 3, 1));

					//---- label_resourcesLanguageCode3 ----
					label_resourcesLanguageCode3.setText("Object Type");
					label_resourcesLanguageCode3.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					ATFieldInfo.assignLabelInfo(label_resourcesLanguageCode3, DigitalObjects.class, DigitalObjects.PROPERTYNAME_OBJECT_TYPE);
					digitalObjectResourceRecordOnly.add(label_resourcesLanguageCode3, cc.xy(1, 3));

					//---- objectType ----
					objectType.setMaximumSize(new Dimension(50, 27));
					objectType.setOpaque(false);
					objectType.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					digitalObjectResourceRecordOnly.add(objectType, cc.xywh(3, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

					//---- actuateLabel2 ----
					actuateLabel2.setText("Digital Object ID");
					actuateLabel2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					ATFieldInfo.assignLabelInfo(actuateLabel2, DigitalObjects.class, DigitalObjects.PROPERTYNAME_METS_IDENTIFIER);
					digitalObjectResourceRecordOnly.add(actuateLabel2, cc.xy(1, 5));

					//======== scrollPane43 ========
					{
						scrollPane43.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						scrollPane43.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));

						//---- title2 ----
						title2.setRows(3);
						title2.setLineWrap(true);
						title2.setWrapStyleWord(true);
						title2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						scrollPane43.setViewportView(title2);
					}
					digitalObjectResourceRecordOnly.add(scrollPane43, cc.xywh(3, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

					//---- actuateLabel ----
					actuateLabel.setText("EAD DAO Actuate");
					actuateLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					ATFieldInfo.assignLabelInfo(actuateLabel, DigitalObjects.class, DigitalObjects.PROPERTYNAME_EAD_DAO_ACTUATE);
					digitalObjectResourceRecordOnly.add(actuateLabel, cc.xy(1, 7));

					//---- actuate ----
					actuate.setOpaque(false);
					actuate.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					digitalObjectResourceRecordOnly.add(actuate, cc.xywh(3, 7, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

					//---- showLabel ----
					showLabel.setText("EAD DAO Show");
					showLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					ATFieldInfo.assignLabelInfo(showLabel, DigitalObjects.class, DigitalObjects.PROPERTYNAME_EAD_DAO_SHOW);
					digitalObjectResourceRecordOnly.add(showLabel, cc.xy(1, 9));

					//---- show ----
					show.setOpaque(false);
					show.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
					digitalObjectResourceRecordOnly.add(show, cc.xywh(3, 9, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

					//======== componentIDPanel ========
					{
						componentIDPanel.setBackground(new Color(200, 205, 232));
						componentIDPanel.setLayout(new FormLayout(
							new ColumnSpec[] {
								FormFactory.DEFAULT_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
							},
							RowSpec.decodeSpecs("default")));

						//---- componentLabel1 ----
						componentLabel1.setText("Component ID");
						componentLabel1.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						ATFieldInfo.assignLabelInfo(componentLabel1, DigitalObjects.class, DigitalObjects.PROPERTYNAME_COMPONENT_ID);
						componentIDPanel.add(componentLabel1, cc.xy(1, 1));

						//---- dateExpression2 ----
						dateExpression2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
						componentIDPanel.add(dateExpression2, cc.xy(3, 1));
					}
					digitalObjectResourceRecordOnly.add(componentIDPanel, cc.xywh(1, 11, 3, 1));
				}
				panel17.add(digitalObjectResourceRecordOnly, cc.xy(1, 7));
			}
			panel13.add(panel17, cc.xywh(1, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		}
		add(panel13, cc.xy(3, 1));

		//======== panel5 ========
		{
			panel5.setOpaque(false);
			panel5.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC
				}));

			//---- label_resourcesLanguageCode ----
			label_resourcesLanguageCode.setText("Language");
			label_resourcesLanguageCode.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			ATFieldInfo.assignLabelInfo(label_resourcesLanguageCode, DigitalObjects.class, DigitalObjects.PROPERTYNAME_LANGUAGE_CODE);
			panel5.add(label_resourcesLanguageCode, cc.xy(1, 1));

			//---- languageCode ----
			languageCode.setMaximumSize(new Dimension(50, 27));
			languageCode.setOpaque(false);
			languageCode.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel5.add(languageCode, cc.xywh(3, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

			//======== repositoryPanel ========
			{
				repositoryPanel.setOpaque(false);
				repositoryPanel.setLayout(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(Sizes.dluX(44)),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- label_repositoryName ----
				label_repositoryName.setText("Repository :");
				label_repositoryName.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				ATFieldInfo.assignLabelInfo(label_repositoryName, Accessions.class, Accessions.PROPERTYNAME_REPOSITORY);
				repositoryPanel.add(label_repositoryName, cc.xy(1, 1));

				//---- repositoryName ----
				repositoryName.setEditable(false);
				repositoryName.setFocusable(false);
				repositoryName.setBorder(null);
				repositoryName.setOpaque(false);
				repositoryName.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				repositoryName.setHorizontalAlignment(SwingConstants.LEFT);
				repositoryPanel.add(repositoryName, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

				//---- changeRepositoryButton ----
				changeRepositoryButton.setText("Change Repository");
				changeRepositoryButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				changeRepositoryButton.setOpaque(false);
				changeRepositoryButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						changeRepositoryButtonActionPerformed();
					}
				});
				repositoryPanel.add(changeRepositoryButton, cc.xy(5, 1));
			}
			panel5.add(repositoryPanel, cc.xywh(1, 3, 3, 1));
		}
		add(panel5, cc.xywh(1, 3, 3, 1));

		//======== panel2 ========
		{
			panel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
			panel2.setOpaque(false);
			panel2.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			panel2.setLayout(new FormLayout(
				ColumnSpec.decodeSpecs("default:grow"),
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC
				}));

			//---- label1 ----
			label1.setText("File Versions");
			label1.setForeground(new Color(0, 0, 102));
			label1.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
			ATFieldInfo.assignLabelInfo(label1, DigitalObjects.class, DigitalObjects.PROPERTYNAME_FILE_VERSIONS);
			panel2.add(label1, new CellConstraints(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets( 5, 5, 0, 0)));

			//======== scrollPane6 ========
			{
				scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane6.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));

				//---- fileVersionsTable ----
				fileVersionsTable.setPreferredScrollableViewportSize(new Dimension(200, 75));
				fileVersionsTable.setRowHeight(20);
				fileVersionsTable.setFocusable(false);
				fileVersionsTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						fileVersionTableMouseClicked(e);
					}
				});
				scrollPane6.setViewportView(fileVersionsTable);
			}
			panel2.add(scrollPane6, new CellConstraints(1, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets( 0, 10, 0, 5)));

			//======== panel29 ========
			{
				panel29.setBackground(new Color(231, 188, 251));
				panel29.setOpaque(false);
				panel29.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				panel29.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")));

				//---- addFileVersionButton ----
				addFileVersionButton.setText("Add File Version");
				addFileVersionButton.setOpaque(false);
				addFileVersionButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				addFileVersionButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addFileVersionButtonActionPerformed();
					}
				});
				panel29.add(addFileVersionButton, cc.xy(1, 1));

				//---- removeFileVersionButton ----
				removeFileVersionButton.setText("Remove File Version");
				removeFileVersionButton.setOpaque(false);
				removeFileVersionButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 13));
				removeFileVersionButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeFileVersionButtonActionPerformed();
					}
				});
				panel29.add(removeFileVersionButton, cc.xy(3, 1));
			}
			panel2.add(panel29, cc.xywh(1, 5, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
		}
		add(panel2, cc.xywh(1, 5, 3, 1));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel panel16;
	private JPanel panel1;
	private JLabel label_resourcesLanguageCode4;
	public JTextField label;
	private JPanel panel19;
	private JLabel label_resourcesTitle;
	private JScrollPane scrollPane42;
	public JTextArea title;
	private JPanel resourcesPanel;
	private JLabel resourcesLabel;
	private JScrollPane scrollPane4;
	private DomainSortableTable resourcesTable;
	private JPanel panel13;
	private JPanel panel17;
	private JLabel label_repositoryName4;
	private JScrollPane scrollPane8;
	private DomainSortableTable dateTable;
	private JPanel panel22;
	private JButton addDate;
	private JButton removeDate;
	private JPanel digitalObjectResourceRecordOnly;
	public JCheckBox restrictionsApply;
	private JLabel label_resourcesLanguageCode3;
	public JComboBox objectType;
	private JLabel actuateLabel2;
	private JScrollPane scrollPane43;
	public JTextArea title2;
	private JLabel actuateLabel;
	public JComboBox actuate;
	private JLabel showLabel;
	public JComboBox show;
	private JPanel componentIDPanel;
	private JLabel componentLabel1;
	public JTextField dateExpression2;
	private JPanel panel5;
	private JLabel label_resourcesLanguageCode;
	public JComboBox languageCode;
	private JPanel repositoryPanel;
	private JLabel label_repositoryName;
	public JTextField repositoryName;
	private JButton changeRepositoryButton;
	private JPanel panel2;
	private JLabel label1;
	private JScrollPane scrollPane6;
	private DomainSortableTable fileVersionsTable;
	private JPanel panel29;
	private JButton addFileVersionButton;
	private JButton removeFileVersionButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	public void setModel(DigitalObjects digitalObject) {
		this.digitalObject = digitalObject;
		System.out.println("DO set model editor fileds: " + this.getEditorField());
		dateTable.updateCollection(this.digitalObject.getArchDescriptionDates());
		fileVersionsTable.updateCollection(this.digitalObject.getFileVersions());
		setRepositoryText(this.digitalObject);

		//suppress d.o. resource record only fields for components
		if (this.digitalObject.getParent() != null) {
			digitalObjectResourceRecordOnly.setVisible(false);
			repositoryPanel.setVisible(false);
			componentIDPanel.setVisible(true);
			resourcesPanel.setVisible(false);
		} else { // this is a parent digital object
			digitalObjectResourceRecordOnly.setVisible(true);
			componentIDPanel.setVisible(false);
			resourcesPanel.setVisible(true);

			// display any resource that links this digital object
			displayLinkedResource();
		}
	}

	/**
	 * Method to display any resource linked to this digital objects
	 */
	private void displayLinkedResource() {
		// clear out any resource in the table
		resourcesTable.updateCollection(null);

		// now add any resource linked to this digital object
		ArchDescriptionDigitalInstances digitalInstance = digitalObject.getDigitalInstance();
		if(digitalInstance != null) {
			DigitalObjectDAO digitalObjectDAO = new DigitalObjectDAO();
			Resources resource = digitalObjectDAO.findResourceByDigitalObject(digitalInstance);

			if(resource != null) {
				resourcesTable.addDomainObject(new DigitalObjectsResources(resource,digitalObject));
			}
		}
	}

}