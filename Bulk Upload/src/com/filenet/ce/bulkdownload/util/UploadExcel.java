package com.filenet.ce.bulkdownload.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import sun.tools.jar.resources.jar;

import com.filenet.api.exception.EngineRuntimeException;

public class UploadExcel extends JPanel {

	final static Logger logger = Logger.getLogger(UploadExcel.class);
	private static final long serialVersionUID = 1L;
	static boolean uploadFlag = true;

	static private JTextField contentTextField = null;
	static private JLabel outputPath = null;
	static private JTextField outputPathTextField = null;
	static private JButton browseButton = null;
	static private JButton browseButtonOutput = null;
	static private JButton bulkUploadButton = null;
	static private JLabel selectedPath = null;
	static private JPanel parameterPanel = null;
	static private JPanel resultPanel = null;
	static private JButton clearButton = null;
	static private CEConnection ce = null;
	static private JLabel statusLabel = null;
	static public JProgressBar progressBar = null;
	static private int j = 0;

	public UploadExcel(CEConnection c) {
		super();
		ce = c;
		initialize();
	}

	private void initialize() {
		if (logger.isInfoEnabled()) {
			logger.info("<< UploadExcel  initialize ::");
		}
		statusLabel = new JLabel();
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		this.setSize(413, 265);
		this.add(getResultPanel(), BorderLayout.SOUTH);
		this.add(getParameterPanel(), BorderLayout.CENTER);
		this.add(statusLabel, BorderLayout.SOUTH);
	}

	private JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.setText("Browse");
			browseButton.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					browseButtonActionPerformed(evt);
				}
			});
		}
		return browseButton;
	}

	private JButton getBrowseButton2() {
		if (browseButtonOutput == null) {
			browseButtonOutput = new JButton();
			browseButtonOutput.setText("Browse");
			browseButtonOutput
					.addActionListener(new java.awt.event.ActionListener() {

						@Override
						public void actionPerformed(
								java.awt.event.ActionEvent evt) {
							browseButtonActionPerformedOutput(evt);

						}

					});
		}
		return browseButtonOutput;
	}

	private JButton getBulkDownloadButton() {
		if (bulkUploadButton == null) {
			bulkUploadButton = new JButton();
			bulkUploadButton.enable();
			bulkUploadButton.setText("Bulk Upload");
			bulkUploadButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent evt) {
							try {
								pathButtonActionPerformed(evt);
								outputPathTextField.setText(null);
								contentTextField.setText(null);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		}
		return bulkUploadButton;
	}

	private JPanel getParameterPanel() {
		if (parameterPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.gridx = 3;

			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 5;

			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 2;

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;

			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 4;

			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.weightx = 1.0;

			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 9;

			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.gridx = 3;

			parameterPanel = new JPanel();
			parameterPanel.setLayout(new GridBagLayout());
			parameterPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Excel Parameters", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));

			parameterPanel.add(getSelectedPath(), gridBagConstraints1);
			parameterPanel.add(getContentTextField(), gridBagConstraints2);
			parameterPanel.add(getBulkDownloadButton(), gridBagConstraints3);
			parameterPanel.add(getBrowseButton(), gridBagConstraints4);
			parameterPanel.add(getOutputPathPath(), gridBagConstraints6);
			parameterPanel.add(getOutputPathTextField(), gridBagConstraints5);
			parameterPanel.add(getProgressBar(), gridBagConstraints7);
			parameterPanel.add(getBrowseButton2(), gridBagConstraints8);

		}
		return parameterPanel;
	}

	private JLabel getOutputPathPath() {
		if (outputPath == null) {
			outputPath = new JLabel();
			outputPath.setText("Output Path: ");
		}
		return outputPath;

	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			// progressBar.setValue(0);
			progressBar.setStringPainted(true);
		}
		return progressBar;
	}

	public static void setMaximum(int i) {
		progressBar.setMaximum(i);
		progressBar.setValue(0);
	}

	public static void setProgressBar(int i) {
		progressBar.setValue(i);
		int p = progressBar.getMaximum();
		progressBar.paint(progressBar.getGraphics());
	}

	// try {
	// Thread.sleep(150);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	/*
	 * String str = progressBar.getString(); System.out.println(i);
	 * progressBar.firePropertyChange(str, j, i);
	 * progressBar.addPropertyChangeListener(str, new PropertyChangeListener() {
	 * 
	 * @Override public void propertyChange(PropertyChangeEvent evt) { // TODO
	 * Auto-generated method stub progressUpdate(evt,i); } });
	 * progressBar.addPropertyChangeListener(new PropertyChangeListener() {
	 * 
	 * @Override public void propertyChange(PropertyChangeEvent evt) { // TODO
	 * Auto-generated method stub progressUpdate(evt,i); } });
	 * 
	 * j=i;
	 */

	private JTextField getOutputPathTextField() {
		if (outputPathTextField == null) {
			outputPathTextField = new JTextField("");
			outputPathTextField.getText();
			System.out.println(outputPathTextField.getText());
		}
		return outputPathTextField;
	}

	private JPanel getResultPanel() {
		if (resultPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 3;
			resultPanel = new JPanel();
			resultPanel.setLayout(new GridBagLayout());
			resultPanel.add(getClearButton(), gridBagConstraints1);

		}
		return resultPanel;
	}

	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					clearButtonActionPerformed(evt);
				}
			});
		}
		return clearButton;
	}

	private JLabel getSelectedPath() {
		if (selectedPath == null) {
			selectedPath = new JLabel();
			selectedPath.setText("Select Path: ");
		}
		return selectedPath;

	}

	private JTextField getContentTextField() {
		if (contentTextField == null) {
			contentTextField = new JTextField("");
			contentTextField.getText();
			System.out.println(contentTextField.getText());

		}
		return contentTextField;
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		File f = fc.getSelectedFile();
		contentTextField.setText(f.getAbsolutePath());
		// bulkUploadButton.setVisible(uploadFlag);
	}

	private void browseButtonActionPerformedOutput(
			java.awt.event.ActionEvent evt) {
		JFileChooser fco = new JFileChooser();
		fco.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fco.showOpenDialog(null);
		fco.setAcceptAllFileFilterUsed(false);
		File f = fco.getSelectedFile();
		outputPathTextField.setText(f.getAbsolutePath());
		bulkUploadButton.setVisible(uploadFlag);
	}

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
		contentTextField.setText("");
		statusLabel.setText("");

	}

	private static void progressUpdate(PropertyChangeEvent evt, int i) {
		progressBar.setValue(i);
		System.out.println("Changed  to " + i);

	}

	private void pathButtonActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("<< Bulk upload Action Performed ::");
		}
		try {
			bulkUploadButton.setVisible(uploadFlag);
			if (checkRequireFields() == true) {

				if (logger.isInfoEnabled()) {
					logger.info("<< Check Is CE connected ::"
							+ ce.isConnected());
				}
				if (ce.isConnected()) {
					if (null != ce.getCm_TARGETOS()) {
						statusLabel.setText("");
						contentTextField.getText();
						ReadExcel readExcel = new ReadExcel();
						// System.out.println(readExcel +
						// "####################");
						if (logger.isInfoEnabled()) {
							logger.info("<< Selected File Path ::"
									+ contentTextField.getText());
							logger.info("<< Download location ::"
									+ outputPathTextField.getText());
						}
						readExcel.processExcelSheet(ce.fetchDomain(),
								ce.getCm_TARGETOS(),
								contentTextField.getText(),
								outputPathTextField.getText());
						// statusLabel.setText("Bulk Download completed successfully.");
						JOptionPane.showMessageDialog(null,
								"Bulk Upload completed successfully.",
								"Bulk Upload Status",
								JOptionPane.INFORMATION_MESSAGE);
						progressBar.setValue(0);
						if (logger.isInfoEnabled()) {
							logger.info("Bulk Upload completed successfully.");
						}
					} else {
						statusLabel
								.setText("Please select Object Sore in connection panel.");
						logger.warn("Please select Object Store in connection panel.");
					}
				} else {
					statusLabel
							.setText("Please check your filenet connection.");
					logger.warn("Please check your filenet connection.");

				}
			} else {
				/*
				 * statusLabel.setText("Please check your filenet connection.");
				 * logger.warn("Please check your filenet connection.");
				 */

			}
		} catch (EngineRuntimeException e) {
			statusLabel.setText(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean checkRequireFields() {
		if (logger.isInfoEnabled()) {
			logger.info("<< checkRequireFields ::");
		}
		boolean con = true;
		if (!outputPathTextField.getText().equals("")) {
			File f = new File(outputPathTextField.getText());
			if (!f.exists() || !f.isDirectory()) {
				statusLabel.setText("Output path is invalid.");
				con = false;
			}
		}
		if (outputPathTextField.getText().equals("")) {
			statusLabel.setText("Output path field can not be empty");
			con = false;
		}
		if (!contentTextField.getText().equals("")) {
			File f = new File(contentTextField.getText());
			if (!f.exists() || !f.isFile()) {
				statusLabel.setText("Invalid Input File.");
				con = false;
			}
		}

		if (contentTextField.getText().equals("")) {
			statusLabel.setText("Input File can not be empty.");
			con = false;

		}

		if (con == true) {
			statusLabel.setText("Bulk Upload in Progress....");
		}
		if (logger.isInfoEnabled()) {
			logger.info("<< checkRequireFields exit ::" + con);
		}
		return con;
	}

}
