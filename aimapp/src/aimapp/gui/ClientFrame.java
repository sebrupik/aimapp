package aimapp.gui;

import aimapp.AimApp;
import aimapp.components.projectPanel3;
import aimapp.gui.actions.*;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import jdbcApp.components.JComboBoxHolder;
import jdbcApp.miscMethods;


public class ClientFrame  extends projectPanel3 {
    private final String _class;
    private AimApp owner;
    private int client_priKey;
    private JComboBoxHolder JCBHolder;
    private boolean updateBool; 
    JTabbedPane openTP, closedTP;
    
    PreparedStatement selectPS, select_referral_by_client_openPS, select_referral_by_client_closedPS, updatePS, insertPS, deletePS, deleteRemovePS, select_client_already_existsPS, delete_referral_perm_PS;
    
    JTextField forenameTxt, surnameTxt, dobTxt, postCodeTxt;
    
    public ClientFrame(AimApp owner, int client_priKey) {
        super("Client Frame : "+client_priKey);
        this._class = this.getClass().getName();
        this.owner = owner;
        this.client_priKey = client_priKey;
        this.selectPS = owner.getdbConnection().getPS("ps_select_client");
        this.select_referral_by_client_openPS = owner.getdbConnection().getPS("ps_select_referral_by_client_open");
        this.select_referral_by_client_closedPS = owner.getdbConnection().getPS("ps_select_referral_by_client_closed");
        this.updatePS = owner.getdbConnection().getPS("ps_update_client");
        this.insertPS = owner.getdbConnection().getPS("ps_insert_client");
        this.deletePS = owner.getdbConnection().getPS("ps_delete_client");
        this.select_client_already_existsPS = owner.getdbConnection().getPS("ps_select_client_already_exists");
        this.delete_referral_perm_PS = owner.getdbConnection().getPS("ps_delete_referral_perm");
        
        if(client_priKey == -1)
            updateBool = false;
        else 
            updateBool = true;
        
        JCBHolder = new JComboBoxHolder();
        
        this.setLayout(new BorderLayout(2, 2));
        this.add(genPersonalInfoPanel(), BorderLayout.NORTH);
        this.add(genReferralTabbedPane(), BorderLayout.CENTER);
        
        this.initComboBoxes();
        
        if(updateBool)
            refresh();
    }
    
    private JPanel genPersonalInfoPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel censusPanel = new JPanel(gridbag);  censusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        JCBHolder.addItem("GENDER", "SELECT gender FROM global_list_gender", new JComboBox());
        JCBHolder.addItem("ETHNICITY", "SELECT ethnicity FROM global_list_ethnicity", new JComboBox());
        
        forenameTxt = new JTextField("");  forenameTxt.addFocusListener(new UpperCaseMungerAdapter(forenameTxt, false));
        surnameTxt = new JTextField("");  surnameTxt.addFocusListener(new UpperCaseMungerAdapter(surnameTxt, false));
        dobTxt = new JTextField(owner._dateFormat); dobTxt.addFocusListener(new DateTxtCheck(dobTxt, owner._dateFormat, owner._dateNull));
        postCodeTxt = new JTextField("SO");  postCodeTxt.addFocusListener(new JTextFieldCursorAtEnd(postCodeTxt, "SO"));
        
        
        JLabel ePanelLbl[] = new JLabel[]{new JLabel("Forename"), new JLabel("Surname"), new JLabel("Staff Name"), new JLabel("Gender"), new JLabel("Ethnicity"), new JLabel("DoB"), new JLabel("Postcode (1st part)")};
        
        buildConstraints(constraints, 0, 0, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[0], constraints);
        censusPanel.add(ePanelLbl[0]);
        buildConstraints(constraints, 1, 0, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(forenameTxt, constraints);
        censusPanel.add(forenameTxt);
        buildConstraints(constraints, 2, 0, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[1], constraints);
        censusPanel.add(ePanelLbl[1]);
        buildConstraints(constraints, 3, 0, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(surnameTxt, constraints);
        censusPanel.add(surnameTxt);
        
        buildConstraints(constraints, 0, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[4], constraints);
        censusPanel.add(ePanelLbl[4]);
        buildConstraints(constraints, 1, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("ETHNICITY"), constraints);
        censusPanel.add(JCBHolder.getJCBox("ETHNICITY"));
        buildConstraints(constraints, 2, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[3], constraints);
        censusPanel.add(ePanelLbl[3]);
        buildConstraints(constraints, 3, 1, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("GENDER"), constraints);
        censusPanel.add(JCBHolder.getJCBox("GENDER"));
        
        buildConstraints(constraints, 0, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[6], constraints);
        censusPanel.add(ePanelLbl[6]);
        buildConstraints(constraints, 1, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(postCodeTxt, constraints);
        censusPanel.add(postCodeTxt);
        buildConstraints(constraints, 2, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ePanelLbl[5], constraints);
        censusPanel.add(ePanelLbl[5]);
        buildConstraints(constraints, 3, 2, 1, 1, 100, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(dobTxt, constraints);
        censusPanel.add(dobTxt);
        
        
        
        return censusPanel;
    }
    
    private JTabbedPane genReferralTabbedPane() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.LEFT);
        openTP = new JTabbedPane();   openTP.addMouseListener(new JTabbedPaneCreateNewReferralAction(owner, openTP, client_priKey)); 
        closedTP = new JTabbedPane();  closedTP.addMouseListener(new JTabbedPaneCreateNewReferralAction(owner, closedTP, client_priKey)); 
        
        openTP.setComponentPopupMenu(genRefTPPopupMenu(openTP));
        closedTP.setComponentPopupMenu(genRefTPPopupMenu(closedTP));
        
        openTP.addTab("Add", new JPanel());
        closedTP.addTab("Add", new JPanel());
        
        //tp.add(openTP, "Open Referrals");
        //tp.add(closedTP, "Closed Referrals");
        
        tp.addTab(null, new VerticalTextIcon("Open Referrals", false), openTP); 
        tp.addTab(null, new VerticalTextIcon("Closed Referrals", false), closedTP); 
        
        return tp;
    }
    
    private JPopupMenu genRefTPPopupMenu(JTabbedPane target) {
        JPopupMenu jpm  = new JPopupMenu();
        
        JMenuItem delMI = new JMenuItem("Delete tab");  delMI.addActionListener(new ReferralTabPopupMenuDeleteAction(owner, target, delete_referral_perm_PS));
        JMenuItem orphanMI = new JMenuItem("Orphan referral");
        JMenuItem assignMI = new JMenuItem("Assign orphaned referral");
        JMenuItem reassignMI = new JMenuItem("Reassign referral");
        
        jpm.add(delMI);
        jpm.addSeparator();
        //jpm.add(orphanMI);
        //jpm.add(assignMI);
        //jpm.add(reassignMI);
        
        return jpm;
    }
    
    public void initComboBoxes() {
        System.out.println(_class+"/initComboBoxes - entered"); 
        JCBHolder.refreshJCBoxes(owner.getdbConnection());
        //setDefaultStaffName();
        System.out.println(_class+"/initComboBoxes - exited");
    }
    
    public void refresh() {
        System.out.println(_class+"/refresh - entered");
        try {
            selectPS.setInt(1, client_priKey);
            populateFields(owner.getdbConnection().executeQuery(selectPS));
            
            select_referral_by_client_openPS.setInt(1, client_priKey);
            populateReferralTabs(owner.getdbConnection().executeQuery(select_referral_by_client_openPS), true);
            
            select_referral_by_client_closedPS.setInt(1, client_priKey);
            populateReferralTabs(owner.getdbConnection().executeQuery(select_referral_by_client_closedPS), false);
            
            selectPS.clearParameters();
            
            openTP.setSelectedIndex(this.lastButOneIndex(openTP));
            closedTP.setSelectedIndex(this.lastButOneIndex(closedTP));
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/refresh", sqle); }
        System.out.println(_class+"/refresh - exited");
    }
    
    private int lastButOneIndex(JTabbedPane tp) {
        int index;
        int c = tp.getTabCount();
        if(c > 1) {
            index= c-1;
        } else { 
            index=c;
        }
        return index-1;
    }
    
    public void populateFields(ResultSet r) throws SQLException {
        if(r.first()) {
            forenameTxt.setText(r.getString("forename"));
            surnameTxt.setText(r.getString("surname"));
            //JCBHolder.setSelectedItem("NAME_STAFF", r.getString("name_staff"));
            JCBHolder.setSelectedItem("GENDER", r.getString("gender"));
            postCodeTxt.setText(r.getString("postcode"));
            dobTxt.setText(miscMethods.convertDateToText(r.getDate("dob"), owner._dateFormat) );
            JCBHolder.setSelectedItem("ETHNICITY", r.getString("ethnicity"));
            
        }
    }
    
    private void populateReferralTabs(ResultSet r, boolean open) throws SQLException {
        int numTabs;
        ReferralPanel rp;
        if(open) {
            while(r.next()) {
                numTabs = openTP.getTabCount();
                openTP.insertTab(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat), null, new ReferralPanel(owner, client_priKey, r.getInt("priKey")), "", numTabs-1);
            }
        } else {
            while(r.next()) {
                rp = new ReferralPanel(owner, client_priKey, r.getInt("priKey"));
                rp.setEnabled(false);
                
                numTabs = closedTP.getTabCount();
                closedTP.insertTab(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat), null, rp, "", numTabs-1);
                
                //closedTP.insertTab(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat), null, new ReferralPanel(owner, client_priKey, r.getInt("priKey")), "", numTabs-1);
                //((ReferralPanel)closedTP.getTabComponentAt(numTabs-1)).setEnabled(false);
            }
        }
    }

//*****************************************************************************
    /**
     * 
     * 
     * @return The priKey value for this newly enter row
     * @throws java.text.ParseException 
     */
    public int insertDB() throws java.text.ParseException {
        int count=-1;
        if(mdsPresent()) {
            GregorianCalendar c = new GregorianCalendar();
            java.sql.Timestamp ts = new java.sql.Timestamp(c.getTimeInMillis());
            
            String[] obj;
            try {
                insertPS.setInt(1, 0);
                insertPS.setTimestamp(2, ts);
                insertPS.setString(3, forenameTxt.getText());
                insertPS.setString(4, surnameTxt.getText());
                insertPS.setString(5, (String)JCBHolder.getSelectedItem("GENDER"));
                insertPS.setString(6, postCodeTxt.getText());
                insertPS.setDate(7, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                insertPS.setString(8, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                 
                owner.getdbConnection().executeUpdate(insertPS);
                insertPS.clearParameters();
                
                return miscMethods.colToArrayInt(owner.getdbConnection().executeQuery("SELECT priKey FROM aim_data_clients WHERE timestamp='"+ts+"'"), 1)[0].intValue();
                
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/insertDB", sqle); 
            }
        }
        return count;
    }
    
    
    public int updateDB() throws java.text.ParseException {
        int count=-1;
        if(mdsPresent()) {
            GregorianCalendar c = new GregorianCalendar();
            java.sql.Timestamp ts = new java.sql.Timestamp(c.getTimeInMillis());
            
            String[] obj;
            try {
                updatePS.setTimestamp(1, ts);
                updatePS.setString(2, forenameTxt.getText());
                updatePS.setString(3, surnameTxt.getText());
                updatePS.setString(4, (String)JCBHolder.getSelectedItem("GENDER"));
                updatePS.setString(5, postCodeTxt.getText());
                updatePS.setDate(6, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
                updatePS.setString(7, (String)JCBHolder.getSelectedItem("ETHNICITY"));
                
                updatePS.setInt(8, client_priKey);
                
                count = owner.getdbConnection().executeUpdate(updatePS);
                
                updatePS.clearParameters();
                
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/updateDB", sqle); }
        }
        return count;
    }
    
    
    public void processReferralTabGroups(int c_pk) throws java.text.ParseException {
        this.processReferralTabs(c_pk, openTP);
        this.processReferralTabs(c_pk, closedTP);
    }
    
    /**
     *  Itterate through the open/closed tabs and decide whether a referral needs to be updated or inserted.
     */
    private void processReferralTabs(int c_pk, JTabbedPane target) throws java.text.ParseException {
        ReferralPanel rp;
        
        for (int i=0;i<target.getTabCount()-1; i++) {  //don't check the 'Add' tab
            rp = (ReferralPanel)target.getComponentAt(i);
            
            if( rp.getUpdateBool() == false ) {
                rp.insertDB(c_pk); 
            } else {
                rp.updateDB();
            }
        }
    }
    
    private boolean mdsPresent() {
        if(forenameTxt.getText().trim().length()==0 | surnameTxt.getText().trim().length()==0) {
            JOptionPane.showMessageDialog(this, "Minimum data set not present.");
            return false;
        } else {
            return true;
        }
    }
//*****************************************************************************
    
    @Override public void closingActions() {
        saveContents();
        writeProperties();
    }
    
    @Override public void writeProperties() {
        System.out.println(_class+"/writeProperties - writing properties");
        owner.saveSysProperty("sizeX.clientFrame", String.valueOf(this.getSize().width));
        owner.saveSysProperty("sizeY.clientFrame", String.valueOf(this.getSize().height)); 
    }
    
    private void saveContents() {
        try {
            if(updateBool) {
                int result = JOptionPane.showConfirmDialog(null, "Update client info", "Confirm update", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if(result==0) { //yes
                    updateDB();
                    processReferralTabGroups(client_priKey);
                }
            } else {
                int result = JOptionPane.showConfirmDialog(null, "Create new client", "Confirm creation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if(result==0) { //yes
                    System.out.println("You want to create a new client");

                    int[] cpk_ar = this.checkClientAlreadyExists();
                    if(cpk_ar.length > 0) {
                        ClientExistsDialog cef = new ClientExistsDialog(owner, this, cpk_ar);
                        cef.setVisible(true);
                    } else {
                        processReferralTabGroups(insertDB());
                    }
                    
                    /*
                    int c_pk = this.checkClientAlreadyExists();
                    if( c_pk == -1) {
                        //insertDB();
                        processReferralTabGroups(insertDB());
                    } else { // client already exists
                        System.out.println("the client already exisits");

                        int choice = JOptionPane.showConfirmDialog(null, "Merge referral data?", "Client already exists?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(choice==0) { //yes
                            System.out.println("You want to merge the new referral data with the eisiting client record");    

                            processReferralTabGroups(c_pk);

                        } else if (choice==1) { //no
                            System.out.println("You don't want to merge the data");    

                            int choice2 = JOptionPane.showConfirmDialog(null, "Create new client record?", "Client already exists?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (choice2==0) { //yes
                                System.out.println("You want to create a new client record, despite it looking like a duplicate");

                                //insertDB();
                                processReferralTabGroups(insertDB());
                            }
                        }
                    }*/
                }
            }
        } catch (java.text.ParseException pe) { owner.exceptionEncountered(_class+"/updateDB", pe);  }
    }
    
    private int[] checkClientAlreadyExists() {
        int cpk = -1;
        Integer[] ar;
        int[] cpk_ar = null;
        try {
            select_client_already_existsPS.setString(1, forenameTxt.getText());
            select_client_already_existsPS.setString(2, surnameTxt.getText());
            select_client_already_existsPS.setDate(3, miscMethods.convertTextToDate(dobTxt.getText(), owner._dateFormat, owner._dateNull));
            
            //cpk = miscMethods.colToArrayInt(owner.getdbConnection().executeQuery(select_client_already_existsPS), 1)[0];
            ar = miscMethods.colToArrayInt(owner.getdbConnection().executeQuery(select_client_already_existsPS), 1);
            System.out.println("some AR stats: length: "+ar.length);
            for (int i=0;i<ar.length;i++) {
                System.out.println("AR["+i+"] : "+ar[i]);
            }
            if(ar.length > 0) {
                cpk=ar[0];
            }
            select_client_already_existsPS.clearParameters();
            
            cpk_ar = new int[ar.length];
            for (int i=0;i<ar.length;i++)
                cpk_ar[i] = ar[i].intValue();

        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/checkClientAlreadyExists", sqle); 
        } catch(java.text.ParseException pe) { owner.exceptionEncountered(_class+"/checkClientAlreadyExists", pe);     
        }
        
        //return cpk;
                       
        return cpk_ar;
    }
}