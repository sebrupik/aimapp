package aimapp.gui;

import aimapp.AimApp;
import aimapp.gui.actions.JComboBoxEnablesOneDisablesAnotherAndChangesContent;
import aimapp.gui.actions.JComboBoxTextFieldAction;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import javax.swing.*;
import jdbcApp.components.JComboBoxHolder;
import jdbcApp.miscMethods;


public class ReferralPanel extends JPanel {
    private final String _class;
    private AimApp owner;
    private int client_priKey, referral_priKey; 
    private boolean updateBool = false; 
    
    PreparedStatement selectPS, updatePS, insertPS;
    private JComboBoxHolder JCBHolder;
    
    ReferralFrameAcceptedTab jtp;
    ReferralFrameAcceptedPanelY rfapY;
    ReferralFrameAcceptedPanelN rfapN;
    
    JTextField refDateTxt, contactDateTxt, closedDateTxt, refSrcTxt;
    
    
    public ReferralPanel(AimApp owner, int client_priKey, int referral_priKey) {
        this._class = this.getClass().getName();
        this.owner = owner;
        this.client_priKey = client_priKey;
        this.referral_priKey = referral_priKey;
        this.selectPS = owner.getdbConnection().getPS("ps_select_referral2");
        this.updatePS = owner.getdbConnection().getPS("ps_update_referral2");
        this.insertPS = owner.getdbConnection().getPS("ps_insert_referral2");
        
        System.out.println(_class+" - initialising!..");        
        
        if(referral_priKey == -1)
            updateBool = false;
        else 
            updateBool = true;
        
        JCBHolder = new JComboBoxHolder();
        
        this.setLayout(new BorderLayout(2, 2));
        this.add(genMainPanel(), BorderLayout.CENTER);
        
        //this.initComboBoxes();
        //System.out.println("Creating new ReferralFrame, update value: "+updateBool+", cpk :"+client_priKey);
        
        
        this.callOverrides();
        //refresh();
    }
    
    private void callOverrides() {
        this.initComboBoxes();
        System.out.println("Creating new ReferralFrame, update value: "+getUpdateBool()+", cpk :"+client_priKey);
        refresh();
    }
    
    private JPanel genMainPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel mPanel = new JPanel(gridbag);
        
        JCBHolder.addItem("NAME_STAFF", "SELECT fullname FROM aim_list_staff WHERE active=b'1' ORDER BY fullname ", new JComboBox());
        JCBHolder.addItem("REFERRAL_SOURCE", "SELECT referral_source FROM aim_list_referral_source ORDER BY referral_source", new JComboBox());
        
        JCBHolder.addItem("ACCEPTED_CATEGORY", "SELECT category FROM aim_list_category ORDER BY category", new JComboBox());
        JCBHolder.addItem("ACCEPTED_CATEGORY_IMCA", "SELECT category_imca FROM aim_list_category_imca ORDER BY category_imca", new JComboBox());
        JCBHolder.addItem("ACCEPTED_ISSUES", "SELECT issue FROM aim_list_issues ORDER BY issue", new JComboBox());
        JCBHolder.addItem("NOT_ACCEPTED", "SELECT not_accepted FROM aim_list_not_accepted", new JComboBox());
        
        refDateTxt = new JTextField(owner._dateFormat);  refDateTxt.addFocusListener(new dateTxtCheck(refDateTxt, owner._dateFormat));
        contactDateTxt = new JTextField(owner._dateFormat);  contactDateTxt.addFocusListener(new dateTxtCheck(contactDateTxt, owner._dateFormat));
        closedDateTxt = new JTextField(owner._dateFormat);  closedDateTxt.addFocusListener(new dateTxtCheck(closedDateTxt, owner._dateFormat));
        
        JPanel dPanel = new JPanel(new GridLayout(0,6)); 
        dPanel.add(new JLabel("Referral"));  dPanel.add(refDateTxt);  
        dPanel.add(new JLabel("First Contact"));  dPanel.add(contactDateTxt);  
        dPanel.add(new JLabel("Closed"));  dPanel.add(closedDateTxt);  
        
        refSrcTxt = new JTextField(20); refSrcTxt.setEnabled(false);
        JCBHolder.getJCBox("REFERRAL_SOURCE").addActionListener(new JComboBoxTextFieldAction(JCBHolder.getJCBox("REFERRAL_SOURCE"), refSrcTxt, "Team (Specify)"));
        
        JPanel refPanel1 = new JPanel(gridbag);
        JLabel[] refPanel1Lbl = new JLabel[]{new JLabel("Staff Name")};
        buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(refPanel1Lbl[0], constraints);
        refPanel1.add(refPanel1Lbl[0]);
        buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("NAME_STAFF"), constraints);
        refPanel1.add(JCBHolder.getJCBox("NAME_STAFF"));
        
        
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
        
        buildConstraints(constraints, 0, 0, 1, 1, 100, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(dPanel, constraints);
        mPanel.add(dPanel);
        buildConstraints(constraints, 0, 1, 1, 1, 100, 10);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanel1, constraints);
        mPanel.add(refPanel1);
        buildConstraints(constraints, 0, 2, 1, 1, 100, 10);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(refPanel2, constraints);
        mPanel.add(refPanel2);
        buildConstraints(constraints, 0, 3, 1, 1, 100, 70);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(jtp, constraints);
        mPanel.add(jtp);
        
        return mPanel;
    }
    
    public void initComboBoxes() {
        System.out.println(_class+"/initComboBoxes - entered"); 
        JCBHolder.refreshJCBoxes(owner.getdbConnection());
        if(updateBool==false)
            setDefaultStaffName();
        System.out.println(_class+"/initComboBoxes - exited");
    }
    
    //****************************
    public void refresh() {
        System.out.println(_class+"/refresh - entered");
        try {
            if(referral_priKey != -1) {  //if it's -1 then we are dealing with a new referral
                selectPS.setInt(1, referral_priKey);
                populateFields(owner.getdbConnection().executeQuery(selectPS));
                selectPS.clearParameters();
            }
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/refresh", sqle); }
        System.out.println(_class+"/refresh - exited");
    }
    
    public void populateFields(ResultSet r) throws SQLException{
        if(r.first()) {
            refDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat) );
            contactDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_first_contact"), owner._dateFormat) );
            closedDateTxt.setText(miscMethods.convertDateToText(r.getDate("date_closed"), owner._dateFormat) );
            //forenameTxt.setText(r.getString("forename"));
            //surnameTxt.setText(r.getString("surname"));
            JCBHolder.setSelectedItem("NAME_STAFF", r.getString("name_staff"));
                    
            //JCBHolder.setSelectedItem("GENDER", r.getString("gender"));
            //postCodeTxt.setText(r.getString("postcode"));
            //dobTxt.setText(miscMethods.convertDateToText(r.getDate("dob"), owner._dateFormat) );
            //JCBHolder.setSelectedItem("ETHNICITY", r.getString("ethnicity"));
            
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
        return this.insertDB(client_priKey);
    }
    
    public int insertDB(int cpk) throws java.text.ParseException {
        int count=-1;
        if(mdsPresent()) {
            GregorianCalendar c = new GregorianCalendar();
            java.sql.Timestamp ts = new java.sql.Timestamp(c.getTimeInMillis());
            
            String[] obj;
            try {
                insertPS.setInt(1, cpk);
                insertPS.setInt(2, 0);
                insertPS.setTimestamp(3, ts);
                insertPS.setString(4, "user_name");
                insertPS.setString(5, "user_ref");
                insertPS.setDate(6, miscMethods.convertTextToDate(refDateTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setDate(7, miscMethods.convertTextToDate(contactDateTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setDate(8, miscMethods.convertTextToDate(closedDateTxt.getText(), owner._dateFormat, owner._dateNull));
                //insertPS.setString(8, forenameTxt.getText());
                //insertPS.setString(9, surnameTxt.getText());
                insertPS.setString(9, (String)JCBHolder.getSelectedItem("NAME_STAFF"));
                //insertPS.setString(11, (String)JCBHolder.getSelectedItem("GENDER"));
                //insertPS.setString(12, postCodeTxt.getText());
                //insertPS.setDate(13, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                //insertPS.setString(14, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                insertPS.setString(10, (String)JCBHolder.getSelectedItem("REFERRAL_SOURCE"));
                insertPS.setString(11, refSrcTxt.getText());
                
                

                if(jtp.getSelectedIndex() == 0) {
                    //obj = rfapY.getFields();
                    insertPS.setInt(12, 1);
                    //insertPS.setString(18, obj[0]);
                    //insertPS.setString(19, obj[1]);
                    //insertPS.setString(20, obj[2]);
                    insertPS.setString(13, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY"));
                    insertPS.setString(14, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY_IMCA"));
                    insertPS.setString(15, (String)JCBHolder.getSelectedItem("ACCEPTED_ISSUES"));
                    
                    insertPS.setString(16, "");
                    insertPS.setString(17, "");
                } else {
                    obj = rfapN.getFields();
                    insertPS.setInt(12, 0);
                    insertPS.setString(13, "");
                    insertPS.setString(14, "");
                    insertPS.setString(15, "");
                    
                    insertPS.setString(16, obj[0]);
                    insertPS.setString(17, obj[1]);
                }
                
                count = owner.getdbConnection().executeUpdate(insertPS);
                Integer[] PKs = miscMethods.colToArrayInt(owner.getdbConnection().executeQuery("SELECT priKey FROM aim_data_referrals WHERE timestamp='"+ts+"' ORDER BY priKey"), 1);
                int upk = PKs[PKs.length-1].intValue();
                //int upk = miscMethods.colToArrayInt(owner.getdbConnection().executeQuery("SELECT priKey FROM aim_data_referrals WHERE timestamp='"+ts+"' ORDER BY priKey"), 1)[0].intValue();
                rfapY.insertDB(upk);
                
                insertPS.clearParameters();
                
                JOptionPane.showMessageDialog(this, "Unique referral ID is: "+upk);
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
                updatePS.setTimestamp(1, new java.sql.Timestamp(c.getTimeInMillis()));
                updatePS.setString(2, "user_name");
                updatePS.setString(3, "user_ref");
                updatePS.setDate(4, miscMethods.convertTextToDate(refDateTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setDate(5, miscMethods.convertTextToDate(contactDateTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setDate(6, miscMethods.convertTextToDate(closedDateTxt.getText(), owner._dateFormat, owner._dateNull));
                //updatePS.setString(8, forenameTxt.getText());
                //updatePS.setString(9, surnameTxt.getText());
                updatePS.setString(7, (String)JCBHolder.getSelectedItem("NAME_STAFF"));
                //updatePS.setString(11, (String)JCBHolder.getSelectedItem("GENDER"));
                //updatePS.setString(12, postCodeTxt.getText());
                //updatePS.setDate(13, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                //updatePS.setString(14, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                updatePS.setString(8, (String)JCBHolder.getSelectedItem("REFERRAL_SOURCE"));
                updatePS.setString(9, refSrcTxt.getText());
                
                

                if(jtp.getSelectedIndex() == 0) {
                    //obj = rfapY.getFields();
                    updatePS.setInt(10, 1);
                    //updatePS.setString(18, obj[0]);
                    //updatePS.setString(19, obj[1]);
                    //updatePS.setString(20, obj[2]);
                    updatePS.setString(11, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY"));
                    updatePS.setString(12, (String)JCBHolder.getSelectedItem("ACCEPTED_CATEGORY_IMCA"));
                    updatePS.setString(13, (String)JCBHolder.getSelectedItem("ACCEPTED_ISSUES"));
                    
                    updatePS.setString(14, "");
                    updatePS.setString(15, "");
                    
                } else {
                    obj = rfapN.getFields();
                    updatePS.setInt(10, 0);
                    updatePS.setString(11, "");
                    updatePS.setString(12, "");
                    updatePS.setString(13, "");
                    
                    updatePS.setString(14, obj[0]);
                    updatePS.setString(15, obj[1]);
                }
                
                updatePS.setInt(16, referral_priKey );
                count = owner.getdbConnection().executeUpdate(updatePS);
                
                updatePS.clearParameters();
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/updateDB", sqle); }
        }
        return count;
    }
    
    private boolean mdsPresent() {
        //if(forenameTxt.getText().trim().length()==0 | surnameTxt.getText().trim().length()==0) {
        //    JOptionPane.showMessageDialog(this, "Minimum data set not present.");
        //    return false;
        //} else {
            return true;
        //}
    }
    
    @Override public void setEnabled(boolean tof) {
        System.out.println(_class+"/setEnabled - : "+tof);
        super.setEnabled(tof);
        refDateTxt.setEnabled(tof);
        contactDateTxt.setEnabled(tof);
        //closedDateTxt.setEnabled(tof);
        refSrcTxt.setEnabled(tof);
        
        this.JCBHolder.setEnabled(tof); 
        
        jtp.setEnabled(tof);
        rfapY.setEnabled(tof);
        rfapN.setEnabled(tof);
        System.out.println(_class+"/setEnabled - finished");
    }
    
    //****************************
    
    public boolean getUpdateBool() { return this.updateBool; }
    public int getReferralPriKey() { return this.referral_priKey; }
    
    private void setDefaultStaffName() {
        try {
            String username = ((DatabaseMetaData)owner.getdbConnection().getMetaData()).getUserName();
            username = username.substring(0, username.indexOf("@"));
            
            ResultSet r = owner.getdbConnection().executeQuery("SELECT fullname FROM aim_list_staff WHERE username='"+username+"'");
            if(r.next()) 
                JCBHolder.getJCBox("NAME_STAFF").setSelectedItem(r.getString("fullname"));
            
        } catch (SQLException sqle) { owner.exceptionEncountered(_class, sqle); }
    }
    
    public class dateTxtCheck implements java.awt.event.FocusListener {
        private JTextField tf;
        private String nullDateValue;
        public dateTxtCheck(JTextField tf, String nullDateValue) { this.tf= tf;  this.nullDateValue = nullDateValue; }
        
        @Override public void focusGained(java.awt.event.FocusEvent e) { tf.selectAll(); }
        @Override public void focusLost(java.awt.event.FocusEvent e) {
            try {
                //is it in null date format 0001-01-01
                if (!tf.getText().equals(nullDateValue)) {  
                    
                    //is it in the correct format dd-MM-yyyy 
                    if (miscMethods.convertTextToDate(tf.getText(), owner._dateFormat, owner._dateNull) == null) {
                        JOptionPane.showMessageDialog(tf, "This date is in the wrong format: "+tf.getText());
                        tf.setText(nullDateValue);
                    } else {
                        GregorianCalendar gNow = new GregorianCalendar();
                        GregorianCalendar gThen = new GregorianCalendar();
                    
                        java.sql.Date d1 = miscMethods.convertTextToDate(tf.getText(), owner._dateFormat, owner._dateNull);
                        java.sql.Date now = new java.sql.Date(gNow.getTimeInMillis());
                        gThen.setTimeInMillis(d1.getTime());

                        if (d1.compareTo(now) > 0 ) {
                            JOptionPane.showMessageDialog(tf, "This date is in the future: "+tf.getText());
                            System.out.println("This date is in the future: "+tf.getText());
                            //tf.grabFocus();
                        }
                        
                        if ( (gThen.get(java.util.Calendar.YEAR) - gNow.get(java.util.Calendar.YEAR)) <= -100 ) { //in the past
                            JOptionPane.showMessageDialog(tf, "This date is in the distant past!: "+tf.getText());
                        } 
                    }
                } else {
                    tf.setText(nullDateValue);
                }
            } catch (java.text.ParseException pe) { 
                System.out.println("customProPanelDBDialog/dateTxtCheck - "+pe); 
                JOptionPane.showMessageDialog(tf, "This date is in the wrong format: "+tf.getText());
                tf.setText(nullDateValue); 
                //tf.grabFocus();
            }
        }
    }
    
    public void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }
}