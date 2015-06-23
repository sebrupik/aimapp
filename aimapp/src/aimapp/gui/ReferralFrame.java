package aimapp.gui;

import aimapp.AimApp;
import jdbcApp.components.JComboBoxHolder;
import aimapp.components.projectPanel3;

import jdbcApp.miscMethods;

import aimapp.gui.actions.JComboBoxTextFieldAction;
import aimapp.gui.actions.UpperCaseMungerAdapter;
import aimapp.gui.actions.JTextFieldCursorAtEnd;
//import aimapp.gui.actions.JComboBoxEnablesOneDisablesAnother;
import aimapp.gui.actions.JComboBoxEnablesOneDisablesAnotherAndChangesContent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.*;


public class ReferralFrame extends projectPanel3 {
    private final String _class;
    private AimApp owner;
    private int client_priKey, referral_priKey;
    private boolean updateBool; 
    
    PreparedStatement selectPS, updatePS, insertPS;
    
    private JComboBoxHolder JCBHolder;
    JTextField refDateTxt, contactDateTxt, closedDateTxt, forenameTxt, surnameTxt, dobTxt, postCodeTxt, refSrcTxt;
    
    ReferralFrameAcceptedTab jtp;
    ReferralFrameAcceptedPanelY rfapY;
    ReferralFrameAcceptedPanelN rfapN;
    
    public ReferralFrame(AimApp owner, int client_priKey) {
        super("Referral Frame : "+client_priKey);
        this._class = this.getClass().getName();
        this.owner = owner;
        this.client_priKey = client_priKey;
        this.selectPS = owner.getdbConnection().getPS("ps_select_referral");
        this.updatePS = owner.getdbConnection().getPS("ps_update_referral");
        this.insertPS = owner.getdbConnection().getPS("ps_insert_referral");
        
        if(client_priKey == -1)
            updateBool = false;
        else 
            updateBool = true;
        
        JCBHolder = new JComboBoxHolder();
        
        this.setLayout(new BorderLayout(2, 2));
        this.add(genMainPanel(), BorderLayout.CENTER);
        this.add(genButtonPanel(), BorderLayout.SOUTH);
        this.initComboBoxes();
        System.out.println("Creating new ReferralFrame, update value: "+updateBool+", cpk :"+client_priKey);
        
        if(updateBool)
            refresh();
    }
    
    public ReferralFrame(AimApp owner, int client_priKey, HashMap userData) {
        this(owner, client_priKey);
        
        forenameTxt.setText( (String)((Object[])userData.get("forename"))[1] );
        surnameTxt.setText( (String)((Object[])userData.get("surname"))[1] );
        JCBHolder.setSelectedItem("GENDER", (String)((Object[])userData.get("gender"))[1] );
        JCBHolder.setSelectedItem("ETHNICITY", (String)((Object[])userData.get("ethnicity"))[1] );
        dobTxt.setText(miscMethods.convertDateToText((java.sql.Date)((Object[])userData.get("dob"))[1], owner._dateFormat) );
    }
    
    private JPanel genMainPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel mPanel = new JPanel(gridbag);

        JCBHolder.addItem("NAME_STAFF", "SELECT fullname FROM aim_list_staff WHERE active=b'1' ORDER BY fullname ", new JComboBox());
        JCBHolder.addItem("GENDER", "SELECT gender FROM global_list_gender", new JComboBox());
        JCBHolder.addItem("ETHNICITY", "SELECT ethnicity FROM global_list_ethnicity", new JComboBox());
        JCBHolder.addItem("REFERRAL_SOURCE", "SELECT referral_source FROM aim_list_referral_source ORDER BY referral_source", new JComboBox());
        
        JCBHolder.addItem("ACCEPTED_CATEGORY", "SELECT category FROM aim_list_category ORDER BY category", new JComboBox());
        JCBHolder.addItem("ACCEPTED_CATEGORY_IMCA", "SELECT category_imca FROM aim_list_category_imca ORDER BY category_imca", new JComboBox());
        JCBHolder.addItem("ACCEPTED_ISSUES", "SELECT issue FROM aim_list_issues ORDER BY issue", new JComboBox());
        JCBHolder.addItem("NOT_ACCEPTED", "SELECT not_accepted FROM aim_list_not_accepted", new JComboBox());
        

        refDateTxt = new JTextField(owner._dateFormat);  refDateTxt.addFocusListener(new dateTxtCheck(refDateTxt, owner._dateFormat));
        contactDateTxt = new JTextField(owner._dateFormat);  contactDateTxt.addFocusListener(new dateTxtCheck(contactDateTxt, owner._dateFormat));
        closedDateTxt = new JTextField(owner._dateFormat);  closedDateTxt.addFocusListener(new dateTxtCheck(closedDateTxt, owner._dateFormat));
        forenameTxt = new JTextField("");  forenameTxt.addFocusListener(new UpperCaseMungerAdapter(forenameTxt, false));
        surnameTxt = new JTextField("");  surnameTxt.addFocusListener(new UpperCaseMungerAdapter(surnameTxt, false));
        dobTxt = new JTextField(owner._dateFormat); dobTxt.addFocusListener(new dateTxtCheck(dobTxt, owner._dateFormat));
        postCodeTxt = new JTextField("SO");  postCodeTxt.addFocusListener(new JTextFieldCursorAtEnd(postCodeTxt, "SO"));
        
        JPanel censusPanel = new JPanel(gridbag);
        //censusPanel.setBorder(BorderFactory.createTitledBorder("Census Details"));
        
        JPanel dPanel = new JPanel(new GridLayout(0,6));
        //dPanel.setBorder(BorderFactory.createTitledBorder("Dates"));
        dPanel.add(new JLabel("Referral"));  dPanel.add(refDateTxt);  
        dPanel.add(new JLabel("First Contact"));  dPanel.add(contactDateTxt);  
        dPanel.add(new JLabel("Closed"));  dPanel.add(closedDateTxt);  
        
        JLabel ePanelLbl[] = new JLabel[]{new JLabel("Forename"), new JLabel("Surname"), new JLabel("Staff Name"), new JLabel("Gender"), new JLabel("Ethnicity"), new JLabel("DoB"), new JLabel("Postcode (1st part)")};
        
        
        buildConstraints(constraints, 0, 0, 4, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(dPanel, constraints);
        censusPanel.add(dPanel);
        
        buildConstraints(constraints, 0, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[0], constraints);
        censusPanel.add(ePanelLbl[0]);
        buildConstraints(constraints, 1, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(forenameTxt, constraints);
        censusPanel.add(forenameTxt);
        buildConstraints(constraints, 2, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[1], constraints);
        censusPanel.add(ePanelLbl[1]);
        buildConstraints(constraints, 3, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(surnameTxt, constraints);
        censusPanel.add(surnameTxt);
        
        buildConstraints(constraints, 0, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[2], constraints);
        censusPanel.add(ePanelLbl[2]);
        buildConstraints(constraints, 1, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("NAME_STAFF"), constraints);
        censusPanel.add(JCBHolder.getJCBox("NAME_STAFF"));
        buildConstraints(constraints, 2, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[3], constraints);
        censusPanel.add(ePanelLbl[3]);
        buildConstraints(constraints, 3, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("GENDER"), constraints);
        censusPanel.add(JCBHolder.getJCBox("GENDER"));
        
        buildConstraints(constraints, 0, 3, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[4], constraints);
        censusPanel.add(ePanelLbl[4]);
        buildConstraints(constraints, 1, 3, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("ETHNICITY"), constraints);
        censusPanel.add(JCBHolder.getJCBox("ETHNICITY"));
        buildConstraints(constraints, 2, 3, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[5], constraints);
        censusPanel.add(ePanelLbl[5]);
        buildConstraints(constraints, 3, 3, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(dobTxt, constraints);
        censusPanel.add(dobTxt);
        
        buildConstraints(constraints, 2, 4, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[6], constraints);
        censusPanel.add(ePanelLbl[6]);
        buildConstraints(constraints, 3, 4, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(postCodeTxt, constraints);
        censusPanel.add(postCodeTxt);
        
        JPanel refPanel = new JPanel();
        //refPanel.setBorder(BorderFactory.createTitledBorder("Referral Details"));
        
        refSrcTxt = new JTextField(20); refSrcTxt.setEnabled(false);
        JCBHolder.getJCBox("REFERRAL_SOURCE").addActionListener(new JComboBoxTextFieldAction(JCBHolder.getJCBox("REFERRAL_SOURCE"), refSrcTxt, "Team (Specify)"));
        
        
        refPanel.add(new JLabel("Referral Source"));  refPanel.add(JCBHolder.getJCBox("REFERRAL_SOURCE"));
        refPanel.add(refSrcTxt);
        
        JPanel refPanel2 = new JPanel(gridbag);
        JLabel[] refPanelLbl = new JLabel[]{new JLabel("Referral Source"), new JLabel("Category"), new JLabel("IMCA"), new JLabel("Issues")};
        
        
        //JCBHolder.getJCBox("ACCEPTED_CATEGORY").addActionListener(new JComboBoxEnablesOneDisablesAnother(JCBHolder.getJCBox("ACCEPTED_CATEGORY"), JCBHolder.getJCBox("ACCEPTED_CATEGORY_IMCA"), JCBHolder.getJCBox("ACCEPTED_ISSUES"), new String[]{"IMCA", "DoLS"}));
        JCBHolder.getJCBox("ACCEPTED_CATEGORY").addActionListener(new JComboBoxEnablesOneDisablesAnotherAndChangesContent(owner, JCBHolder.getJCBox("ACCEPTED_CATEGORY"), JCBHolder.getJCBox("ACCEPTED_CATEGORY_IMCA"), JCBHolder.getJCBox("ACCEPTED_ISSUES"), JCBHolder.getJCBox("NOT_ACCEPTED"), new String[]{"IMCA", "DoLS"}, "SELECT not_accepted FROM aim_list_not_accepted_imca", "SELECT not_accepted FROM aim_list_not_accepted"));
        
        buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(refPanelLbl[0], constraints);
        refPanel2.add(refPanelLbl[0]);
        buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("REFERRAL_SOURCE"), constraints);
        refPanel2.add(JCBHolder.getJCBox("REFERRAL_SOURCE"));
        buildConstraints(constraints, 2, 0, 2, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(refSrcTxt, constraints);
        refPanel2.add(refSrcTxt);
        
        buildConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanelLbl[1], constraints);
        refPanel2.add(refPanelLbl[1]);
        buildConstraints(constraints, 1, 1, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(JCBHolder.getJCBox("ACCEPTED_CATEGORY"), constraints);
        refPanel2.add(JCBHolder.getJCBox("ACCEPTED_CATEGORY"));
        buildConstraints(constraints, 2, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanelLbl[2], constraints);
        refPanel2.add(refPanelLbl[2]);
        buildConstraints(constraints, 3, 1, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(JCBHolder.getJCBox("ACCEPTED_CATEGORY_IMCA"), constraints);
        refPanel2.add(JCBHolder.getJCBox("ACCEPTED_CATEGORY_IMCA"));
        
        buildConstraints(constraints, 2, 2, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanelLbl[3], constraints);
        refPanel2.add(refPanelLbl[3]);
        buildConstraints(constraints, 3, 2, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(JCBHolder.getJCBox("ACCEPTED_ISSUES"), constraints);
        refPanel2.add(JCBHolder.getJCBox("ACCEPTED_ISSUES"));
        
        
        
        
        jtp = new ReferralFrameAcceptedTab(); 
        rfapY = new ReferralFrameAcceptedPanelY(owner, JCBHolder, client_priKey, referral_priKey);
        rfapN =  new ReferralFrameAcceptedPanelN(JCBHolder);
        jtp.add("Accepted", rfapY);
        jtp.add("Not Accepted",rfapN);
                
        buildConstraints(constraints, 0, 0, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(censusPanel, constraints);
        mPanel.add(censusPanel);
        buildConstraints(constraints, 0, 1, 1, 1, 100, 10);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanel2, constraints);
        mPanel.add(refPanel2);
        buildConstraints(constraints, 0, 2, 1, 1, 100, 70);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(jtp, constraints);
        mPanel.add(jtp);
        
        return mPanel;
    }
    
    private JPanel genButtonPanel() {
        JPanel bPanel = new JPanel();
        JButton but;
        
        if(updateBool) {
            but = new JButton("Update");
            but.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) { 
                    try {
                        if(mdsPresent()) {
                            updateDB();
                            dispose();
                            owner.refreshSearchFrame();
                        }
                    } catch (java.text.ParseException pe) { owner.exceptionEncountered(_class+"/genButtonPanel", pe); }
                }
            });
        } else {
            but = new JButton("Add");
            but.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) { 
                    try {
                        if(insertDB()>0)
                            dispose();
                        owner.refreshSearchFrame();
                    } catch (java.text.ParseException pe) { owner.exceptionEncountered(_class+"/genButtonPanel", pe); }
                }
            });
        }
        
        bPanel.add(but);
        return bPanel;
    }
    
    
    //*******************
    public void initComboBoxes() {
        System.out.println(_class+"/initComboBoxes - entered"); 
        JCBHolder.refreshJCBoxes(owner.getdbConnection());
        setDefaultStaffName();
        System.out.println(_class+"/initComboBoxes - exited");
    }

    private void setDefaultStaffName() {
        try {
            String username = ((DatabaseMetaData)owner.getdbConnection().getMetaData()).getUserName();
            username = username.substring(0, username.indexOf("@"));
            
            ResultSet r = owner.getdbConnection().executeQuery("SELECT fullname FROM aim_list_staff WHERE username='"+username+"'");
            if(r.next()) 
                JCBHolder.getJCBox("NAME_STAFF").setSelectedItem(r.getString("fullname"));
            
        } catch (SQLException sqle) { owner.exceptionEncountered(_class, sqle); }
    }
    
    public void refresh() {
        System.out.println(_class+"/refresh - entered");
        try {
            selectPS.setInt(1, client_priKey);
            populateFields(owner.getdbConnection().executeQuery(selectPS));
            selectPS.clearParameters();
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/refresh", sqle); }
        System.out.println(_class+"/refresh - exited");
    }
    
    public void populateFields(ResultSet r) throws SQLException{
        if(r.first()) {
            refDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat) );
            contactDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_first_contact"), owner._dateFormat) );
            closedDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_closed"), owner._dateFormat) );
            forenameTxt.setText(r.getString("forename"));
            surnameTxt.setText(r.getString("surname"));
            JCBHolder.setSelectedItem("NAME_STAFF", r.getString("name_staff"));
            JCBHolder.setSelectedItem("GENDER", r.getString("gender"));
            postCodeTxt.setText(r.getString("postcode"));
            dobTxt.setText(miscMethods.convertDateToText(r.getDate("dob"), owner._dateFormat) );
            JCBHolder.setSelectedItem("ETHNICITY", r.getString("ethnicity"));
            
            JCBHolder.setSelectedItem("REFERRAL_SOURCE", r.getString("referral_source"));
            refSrcTxt.setText(r.getString("referral_source_specify"));
            
            JCBHolder.setSelectedItem("ACCEPTED_CATEGORY", r.getString("accepted_category"));
            JCBHolder.setSelectedItem("ACCEPTED_CATEGORY_IMCA", r.getString("accepted_category_imca"));
            JCBHolder.setSelectedItem("ACCEPTED_ISSUES", r.getString("accepted_issues"));
            
            
            if(r.getInt("is_accepted")==1) {
                //rfapY.populateFields(new String[]{r.getString("accepted_category"), r.getString("accepted_category_imca"), r.getString("accepted_issues")}, r.getInt("priKey"));
                rfapY.populateFields(new String[]{}, r.getInt("priKey"));
            } else {
                rfapN.populateFields(new String[]{r.getString("not_accepted_reason"), r.getString("not_accepted_reason_specify")});
            }
        }
    }
    
    public int insertDB() throws java.text.ParseException {
        int count=-1;
        if(mdsPresent()) {
            GregorianCalendar c = new GregorianCalendar();
            java.sql.Timestamp ts = new java.sql.Timestamp(c.getTimeInMillis());
            
            String[] obj;
            try {
                insertPS = owner.getdbConnection().getPS("ps_insert_referral");
                
                insertPS.setInt(1, 0);
                insertPS.setTimestamp(2, ts);
                insertPS.setString(3, "user_name");
                insertPS.setString(4, "user_ref");
                insertPS.setDate(5, miscMethods.convertTextToDate(refDateTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setDate(6, miscMethods.convertTextToDate(contactDateTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setDate(7, miscMethods.convertTextToDate(closedDateTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setString(8, forenameTxt.getText());
                insertPS.setString(9, surnameTxt.getText());
                insertPS.setString(10, (String)JCBHolder.getSelectedItem("NAME_STAFF"));
                insertPS.setString(11, (String)JCBHolder.getSelectedItem("GENDER"));
                insertPS.setString(12, postCodeTxt.getText());
                insertPS.setDate(13, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setString(14, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                insertPS.setString(15, (String)JCBHolder.getSelectedItem("REFERRAL_SOURCE"));
                insertPS.setString(16, refSrcTxt.getText());
                
                

                if(jtp.getSelectedIndex() == 0) {
                    //obj = rfapY.getFields();
                    insertPS.setInt(17, 1);
                    //insertPS.setString(18, obj[0]);
                    //insertPS.setString(19, obj[1]);
                    //insertPS.setString(20, obj[2]);
                    insertPS.setString(18, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY"));
                    insertPS.setString(19, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY_IMCA"));
                    insertPS.setString(20, (String)JCBHolder.getSelectedItem("ACCEPTED_ISSUES"));
                    
                    insertPS.setString(21, "");
                    insertPS.setString(22, "");
                } else {
                    obj = rfapN.getFields();
                    insertPS.setInt(17, 0);
                    insertPS.setString(18, "");
                    insertPS.setString(19, "");
                    insertPS.setString(20, "");
                    
                    insertPS.setString(21, obj[0]);
                    insertPS.setString(22, obj[1]);
                }
                
                count = owner.getdbConnection().executeUpdate(insertPS);
                int upk = miscMethods.colToArrayInt(owner.getdbConnection().executeQuery("SELECT priKey FROM aim_data_referrals WHERE timestamp='"+ts+"'"), 1)[0].intValue();
                rfapY.insertDB(upk);
                
                insertPS.clearParameters();
                
                JOptionPane.showMessageDialog(this, "Unique ID is: "+upk);
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/insertDB", sqle); 
            } catch(java.text.ParseException pe) { owner.exceptionEncountered(_class+"/insertDB", pe); 
            }
        }
        return count;
    }
    
    
    public int updateDB() throws java.text.ParseException {
        int count=-1;
        if(mdsPresent()) {
            GregorianCalendar c = new GregorianCalendar();
            String[] obj;
            try {
                updatePS = owner.getdbConnection().getPS("ps_update_referral");
                
                updatePS.setInt(1, 0);
                updatePS.setTimestamp(2, new java.sql.Timestamp(c.getTimeInMillis()));
                updatePS.setString(3, "user_name");
                updatePS.setString(4, "user_ref");
                updatePS.setDate(5, miscMethods.convertTextToDate(refDateTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setDate(6, miscMethods.convertTextToDate(contactDateTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setDate(7, miscMethods.convertTextToDate(closedDateTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setString(8, forenameTxt.getText());
                updatePS.setString(9, surnameTxt.getText());
                updatePS.setString(10, (String)JCBHolder.getSelectedItem("NAME_STAFF"));
                updatePS.setString(11, (String)JCBHolder.getSelectedItem("GENDER"));
                updatePS.setString(12, postCodeTxt.getText());
                updatePS.setDate(13, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setString(14, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                updatePS.setString(15, (String)JCBHolder.getSelectedItem("REFERRAL_SOURCE"));
                updatePS.setString(16, refSrcTxt.getText());
                
                

                if(jtp.getSelectedIndex() == 0) {
                    //obj = rfapY.getFields();
                    updatePS.setInt(17, 1);
                    //updatePS.setString(18, obj[0]);
                    //updatePS.setString(19, obj[1]);
                    //updatePS.setString(20, obj[2]);
                    updatePS.setString(18, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY"));
                    updatePS.setString(19, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY_IMCA"));
                    updatePS.setString(20, (String)JCBHolder.getSelectedItem("ACCEPTED_ISSUES"));
                    
                    updatePS.setString(21, "");
                    updatePS.setString(22, "");
                    
                } else {
                    obj = rfapN.getFields();
                    updatePS.setInt(17, 0);
                    updatePS.setString(18, "");
                    updatePS.setString(19, "");
                    updatePS.setString(20, "");
                    
                    updatePS.setString(21, obj[0]);
                    updatePS.setString(22, obj[1]);
                }
                
                updatePS.setInt(23, client_priKey );
                count = owner.getdbConnection().executeUpdate(updatePS);
                
                updatePS.clearParameters();
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/updateDB", sqle); }
        }
        return count;
    }
      
    /**
     * Minimum Data Set Present??
     * @return
     */
    private boolean mdsPresent() {
        if(forenameTxt.getText().trim().length()==0 | surnameTxt.getText().trim().length()==0) {
            JOptionPane.showMessageDialog(this, "Minimum data set not present.");
            return false;
        } else {
            return true;
        }
    }
    
    @Override public void closingActions() {
        writeProperties();
    }
    
    @Override public void writeProperties() {
        System.out.println(_class+"/writeProperties - writing properties");
        owner.saveSysProperty("sizeX.referralFrame", String.valueOf(this.getSize().width));
        owner.saveSysProperty("sizeY.referralFrame", String.valueOf(this.getSize().height));                
    }
    
    @Override public String toString() { return "ReferralFrame "+client_priKey; }
    
    public class dateTxtCheck implements java.awt.event.FocusListener {
        private JTextField tf;
        private String nullDateValue;
        public dateTxtCheck(JTextField tf, String nullDateValue) { this.tf= tf;  this.nullDateValue = nullDateValue; }
        
        @Override public void focusGained(java.awt.event.FocusEvent e) { tf.selectAll(); }
        @Override public void focusLost(java.awt.event.FocusEvent e) {
            try {
                if(!(tf.getText().equals(nullDateValue)) && miscMethods.convertTextToDate(tf.getText(), owner._dateFormat, owner._dateNull) == null)
                    tf.setText(nullDateValue);
            } catch (java.text.ParseException pe) { 
                System.out.println("customProPanelDBDialog/dateTxtCheck - "+pe); 
                tf.setText(nullDateValue); 
            }
        }
    }
    
}