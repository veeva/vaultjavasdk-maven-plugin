package com.veeva.vault.sdk.vaultjavasdk;

/*
 * Copyright 2018 Veeva Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.veeva.vault.sdk.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.sdk.vaultjavasdk.utilities.VaultAPIService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import java.awt.FlowLayout;

/**
 * Goal that packages, imports, and deploys Vault Java SDK source code.
 *
 * 
 */
@Mojo( name = "ui-tool", requiresProject = false)
public class UIToolPlugin extends AbstractMojo {
	
	protected static boolean authStatus;
	
    private final JDialog dialog = new JDialog();
	private JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	
	
	//Login UI panels
	private JPanel loginPanel = new JPanel(new BorderLayout(5, 5));
	private JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	private JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	private JTextField vaultUrlInput = new JTextField("https://");
	private JTextField usernameInput = new JTextField();
	private JPasswordField passwordInput = new JPasswordField();
	
	//Login button UI panel
	private JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
	private JButton deployButton = new JButton("Deploy");
	private JButton packageButton = new JButton("Package");
	private JButton cancelLoginButton = new JButton("Cancel");
	
	//Log Output UI Panel
    private JPanel logOutputPanel  = new JPanel(new BorderLayout(5, 5));
    public static JTextArea outputTextField = new JTextArea();
    private JScrollPane outputTextScrollPane;
    
    protected Image img = new ImageIcon(getClass().getResource("/veeva-icon.png")).getImage();
    
	
	@Parameter( property = "apiVersion", defaultValue = "v18.3" )
	protected  String apiVersion = "";
	@Parameter( property = "vaulturl", defaultValue = "" )
	protected String vaultUrl = "";
	@Parameter( property = "username", defaultValue = "" )
	protected String username = "";
	@Parameter( property = "password", defaultValue = "" )
	protected String password = "";
	@Parameter( property = "sessionId", defaultValue = "" )
	protected String sessionId = "";
	@Parameter( property = "source", defaultValue = "javasdk" )
	protected String[] source;
	
    public void execute() throws MojoExecutionException
    {  
        apiVersion = "/api/" + apiVersion;
        dialog.setTitle("Deploy Vault Java SDK Code");
        dialog.setIconImage(img);
	    
	    getLog().info(vaultUrl);
	    
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        } catch(Exception e) {
            e.printStackTrace();
        }
	    
	    //Set login text fields and labels
	    
	    label.add(new JLabel("Vault URL:", SwingConstants.RIGHT));
	    
	    if (vaultUrl != "") {
	    	vaultUrlInput.setText(vaultUrl);
	    }
	    else {
	    	vaultUrlInput.setText("https://" + vaultUrl);
	    }
	    controls.add(vaultUrlInput);
	    
	    label.add(new JLabel("Username:", SwingConstants.RIGHT));
	    usernameInput.setText(username);
	    controls.add(usernameInput);
	    
    	label.add(new JLabel("Password:", SwingConstants.RIGHT));
    	passwordInput.setText(password);
	    controls.add(passwordInput);
	    
	    
	    if (label.getComponentCount() > 0) {
	    	
	    	//Initialize UI action listeners - these listen for button presses to "Package", "Deploy", and "Cancel".
	    	loginActionListener();
	    	fileChooserActionListener();
	    	cancelLoginActionListener();
			
	    
			//Add the deploy, package, and cancel buttons to the nested UI layout.
	    	buttonPanel.add(packageButton);
			buttonPanel.add(deployButton);
			buttonPanel.add(cancelLoginButton);
			loginPanel.add(buttonPanel, BorderLayout.SOUTH);
			
			//Add the login labels and fields to the nest UI layout.
	    	loginPanel.add(label, BorderLayout.WEST);
	    	loginPanel.add(controls,BorderLayout.CENTER);
	    	loginPanel.setPreferredSize(new Dimension(400, 100));
	    	loginPanel.setMaximumSize(loginPanel.getPreferredSize()); 
	    	loginPanel.setMinimumSize(loginPanel.getPreferredSize());
		  
		    mainPanel.add(loginPanel);
		    
		    
		    //Initialize the output text field and add it to the nested UI layout.
		    outputTextField.setBounds(20,75,250,200); 
		    outputTextField.setSize(400,400);
		    outputTextField.setLineWrap(true);
		    outputTextField.setWrapStyleWord(true);
		    outputTextField.setText("Welcome to the Vault Java SDK deployment tool.\n\n"
		    						+ "Please select \"Package\" to zip up your source code "
		    						+ "and \"Deploy\" to import/deploy the package to the specified vault.\n\n"
		    						+ "---------------------------------------------------------------------------\n\n");
		    outputTextScrollPane = new JScrollPane(outputTextField);
		    outputTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		    outputTextScrollPane.setPreferredSize(new Dimension(200, 250));
		    logOutputPanel.add(outputTextScrollPane, BorderLayout.CENTER);
		    logOutputPanel.setPreferredSize(new Dimension(400, 400));
		    logOutputPanel.setMaximumSize(logOutputPanel.getPreferredSize()); 
		    logOutputPanel.setMinimumSize(logOutputPanel.getPreferredSize());
		    
		    mainPanel.add(logOutputPanel);
		    
		    
		    //Finally, added the nested UI panels to the main JDialog box.
		    dialog.add(mainPanel);
		    dialog.setPreferredSize(new Dimension(525, 600));
		    dialog.setMaximumSize(dialog.getPreferredSize()); 
		    dialog.setMinimumSize(new Dimension(525,600));
	        dialog.setModal(true);
	        dialog.setVisible(true);

	    }
    }
    
    public void loginActionListener() {
    	deployButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
        	  
  		    outputTextField.setText("Welcome to the Vault Java SDK deployment tool.\n\n"
					+ "Please select \"Package\" to zip up your source code "
					+ "and \"Deploy\" to import/deploy the package to the specified vault.\n\n"
					+ "---------------------------------------------------------------------------\n\n");
		    	
    		vaultUrl = new String(vaultUrlInput.getText());
    		outputTextField.append("Connecting to Vault Url: " + vaultUrl + "\n");
	        System.out.println("Your Vault Url is: " + vaultUrl);
	        
    		username = new String(usernameInput.getText());
    		outputTextField.append("Username: " + username + "\n\n");
	        System.out.println("Your username is: " + username);

	        password = new String(passwordInput.getPassword());
	        
	        SwingWorker<String, Void> worker = new SwingWorker<String, Void>()
	        {
	            public String doInBackground()
	            {
					VaultAPIService vaultClient = new VaultAPIService(apiVersion, vaultUrl, username, password, sessionId);
					
					try {
						//Initializes an Authentication API connection.
						authStatus = vaultClient.initializeAPIConnection();
						
						if (authStatus == true) {
							//Uploads the defined VPK to the specified Vault
							String importSuccess = null;
							
							if (PackageManager.getPackagePath() != null) {
								System.out.println(PackageManager.getPackagePath());
								importSuccess = vaultClient.importPackage(PackageManager.getPackagePath());
							}
							else {
								outputTextField.append("There is no vsdk_code_package.vpk in '<PROJECT_DIRECTORY>/deploy-vpk/code/'." + "\n\n");
						        System.out.println("There is no vsdk_code_package.vpk in '<PROJECT_DIRECTORY>/deploy-vpk/code/'.");
							}
		
			//							
							if (importSuccess != null) {
								vaultClient.deployPackage(importSuccess);
							}			
						}
					} catch (MalformedURLException e) {
						getLog().info(e.toString());
					} catch (ProtocolException e) {
						getLog().info(e.toString());
					} catch (IOException e) {
						getLog().info(e.toString());
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            return "";
		        }
		    };
		    // execute the background thread
		    worker.execute();	
	    } 	
       });
    }
    
    public void cancelLoginActionListener() {
    	cancelLoginButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		dialog.dispose();
	        }
        }); 
    }
    
    public void fileChooserActionListener() {
    	packageButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		
			    outputTextField.setText("Welcome to the Vault Java SDK deployment tool.\n\n"
						+ "Please select \"Package\" to zip up your source code "
						+ "and \"Deploy\" to import/deploy the package to the specified vault.\n\n"
					    + "---------------------------------------------------------------------------\n\n");
	    		 JFileChooser choice = new JFileChooser(PackageManager.getProjectPath()) {
	    			    @Override
	    			    protected JDialog createDialog( Component parent ) throws HeadlessException {
	    			        JDialog dialog = super.createDialog( parent );
	    			        dialog.setIconImage( img );
	    			        return dialog;
	    			    }
	    		 };
	    		 
	    		 choice.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    		 choice.setMultiSelectionEnabled(true);
	    		 choice.setFileSystemView(FileSystemView.getFileSystemView());
	    		 choice.setDialogTitle("Select the source code directory or file(s) to package.");
	    		
	    		 
	    		 if (choice.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) { 
	    		      System.out.println("getCurrentDirectory(): " 
	    		         +  choice.getCurrentDirectory());
	    		      
	    		      String filePath = choice.getSelectedFile().getAbsolutePath();
	    		      
	    		      
	    		      ArrayList<String> filePathArray = new ArrayList<String>();
	    		      
	    		      for (File x : choice.getSelectedFiles()) {
	    		    	  filePathArray.add(x.getAbsolutePath());
	    		      }
	    		      
    			    try {
	    		    	PackageManager.createXMLFile(getUsername());  
						PackageManager.createZipFileArray(filePathArray);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		      
	    		      
	    		      System.out.println("getSelectedFile() : " 
	 	    		         +  filePath);
	    		      
	    		      }
    		    else {
    		      System.out.println("No Selection ");
    		      }    
	        }
        }); 
    }
    
    private String getUsername() {
    	return username;
    }
    
    public static void updateOutputText(String inputText) {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
            	outputTextField.append(inputText);
            }
        });
    }

}
