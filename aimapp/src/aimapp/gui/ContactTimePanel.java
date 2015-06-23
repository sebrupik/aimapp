package aimapp.gui;

import jdbcApp.miscMethods;
import aimapp.AimApp;
import jdbcApp.components.JComboBoxHolder;
import aimapp.gui.actions.JTextFieldSelectTextListener;

import java.awt.BorderLayout;
//import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.GregorianCalendar;

public class ContactTimePanel extends JPanel {
    private final String _class;
    AimApp owner;
    ResultSet rSet;
    ArrayList local_cache;
    
    public int updatePK, client_priKey, referral_priKey;
    public boolean update_table, update_db;
    private PreparedStatement main_select, main_delete, main_insert, main_update;
    
    public JPanel historyPanel;
    public JTable historyTbl; 
    
    private JPanel detailPanel;
    private JTextField dateTxt, durationTxt;
    private JComboBoxHolder JCBHolder;
    
    public JPanel buttonPanel;
    public JButton addBut;

    
    public ContactTimePanel(AimApp owner, int client_priKey, int referral_priKey) {
        this._class = this.getClass().getName();
        this.owner = owner;
        this.client_priKey = client_priKey;
        this.referral_priKey = referral_priKey;
        
        if(referral_priKey == -1) {
            update_db=false;
        } else {
            update_db=true;
        }
        
        JCBHolder = new JComboBoxHolder();
        //initProjectPanels();
        
        setLayout(new BorderLayout(2,2));
        
        //add(detailPanel, BorderLayout.NORTH);
        //add(historyPanel, BorderLayout.CENTER); 
        
        main_select = owner.getdbConnection().getPS("ps_select_client_contact");
        main_delete = owner.getdbConnection().getPS("ps_delete_client_contact");
        main_insert = owner.getdbConnection().getPS("ps_insert_client_contact");
        main_update = owner.getdbConnection().getPS("ps_update_client_contact");
        
        local_cache = new ArrayList();
        //refreshTable();
        //initComboBoxes();
        callOverrides();
        addPanels();
    }
    
    private void callOverrides() {
        initProjectPanels();
        initComboBoxes();
    }
    private void addPanels() {
        add(detailPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);
    }
    
    public void genHistoryPanel() {
        System.out.println(_class+"/genHistoryPanel - entered");
        historyPanel = new JPanel();  historyPanel.setLayout(new BorderLayout(1,1));  //historyPanel.setBorder(BorderFactory.createTitledBorder("History"));
        
        historyTbl = new JTable(new DefaultTableModel() {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            @Override public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
            }  
        });
        historyTbl.setComponentPopupMenu(genTableMenu());
        
        historyPanel.add(new JScrollPane(historyTbl), BorderLayout.CENTER);
        
        System.out.println(_class+"/genHistoryPanel - exited");
    }
    
    public void initProjectPanels() {
        genHistoryPanel();
        genDetailsPanel();
    } 
    public void initComboBoxes() {
        System.out.println(_class+"/initComboBoxes - entered"); 
        JCBHolder.refreshJCBoxes(owner.getdbConnection());
        System.out.println(_class+"/initComboBoxes - exited");
    }
    
    //************************
    
    public void genDetailsPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        detailPanel = new JPanel(gridbag);
        //detailPanel.setLayout(new GridLayout(1,7));
        
        JLabel[] labels = new JLabel[]{new JLabel("Date"), new JLabel("Type"), new JLabel("Duration")};
        
        JCBHolder.addItem("CONTACT_TYPE", "SELECT contact_type FROM aim_list_contact_type ORDER BY contact_type", new JComboBox());
        
        dateTxt = new JTextField(owner._dateFormat);  dateTxt.addFocusListener(new dateTxtCheck(dateTxt, owner._dateFormat));
        durationTxt = new JTextField("0");  durationTxt.addFocusListener(new JTextFieldSelectTextListener(durationTxt));
        addBut = new JButton("Add");  addBut.addActionListener(new addAct());
        
        /*detailPanel.add(new JLabel("Date"));  detailPanel.add(dateTxt);
        detailPanel.add(new JLabel("Type"));  detailPanel.add(JCBHolder.getJCBox("CONTACT_TYPE"));
        detailPanel.add(new JLabel("Duration"));  detailPanel.add(durationTxt);
        detailPanel.add(addBut);*/
        
        buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labels[0], constraints);
        detailPanel.add(labels[0]);
        buildConstraints(constraints, 1, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(dateTxt, constraints);
        detailPanel.add(dateTxt);
        buildConstraints(constraints, 4, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labels[1], constraints);
        detailPanel.add(labels[1]);
        buildConstraints(constraints, 5, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(JCBHolder.getJCBox("CONTACT_TYPE"), constraints);
        detailPanel.add(JCBHolder.getJCBox("CONTACT_TYPE"));
        
        buildConstraints(constraints, 2, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(labels[2], constraints);
        detailPanel.add(labels[2]);
        buildConstraints(constraints, 3, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(durationTxt, constraints);
        detailPanel.add(durationTxt);
        buildConstraints(constraints, 6, 0, 1, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(addBut, constraints);
        detailPanel.add(addBut);
    }
    
    public JPopupMenu genTableMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteMI = new JMenuItem("Delete");  deleteMI.addActionListener(new popupDeleteAct());
        JMenuItem updateMI = new JMenuItem("Update");  updateMI.addActionListener(new popupUpdateAct());
        
        menu.add(deleteMI);
        menu.add(updateMI);
        
        return menu;
    }
    
    //*************************
    public void refreshTable() {
        if(update_db) {
            refreshTable(client_priKey);
        } else {
            populateFields(local_cache);
        }
    }
    
    public void refreshTable(int client_priKey) {
        this.client_priKey = client_priKey;
        try {
            main_select.setInt(1, client_priKey);
            populateFields( owner.getdbConnection().executeQuery(main_select) );
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/refreshTable", sqle); 
        }
    }
    
    
    public void clearTable() {
        System.out.println(_class+"/clearTable - entered");
        DefaultTableModel dtm = (DefaultTableModel)historyTbl.getModel();
        dtm.setColumnCount(0);
        dtm.setRowCount(0);
        System.out.println(_class+"/clearTable - exited");
    }
    
    public void clearFields() {
        //dateTxt.setText(owner._dateFormat);
        durationTxt.setText("0");
        JCBHolder.getJCBox("CONTACT_TYPE").setSelectedIndex(0);
    }
    
     
    public void populateFields(ResultSet r) {
        System.out.println(_class+"/populateFields - starting");
        rSet = r;
        
        DefaultTableModel dtm = (DefaultTableModel)historyTbl.getModel();
        this.populateFieldsInit(dtm);
        
        if(rSet !=null) {
            try {
                while(rSet.next()) {
                    //client_priKey = rSet.getInt("client_priKey");
                    dtm.addRow(new Object[]{miscMethods.convertDateToText(rSet.getDate("contact_date"), owner._dateFormat), rSet.getString("contact_type"), Integer.valueOf(rSet.getInt("contact_duration")) });
                }
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/populatefields", sqle); }
        }
        System.out.println(_class+"/populateFields - finished");
    }
    
    public void populateFields(ArrayList al) {
        DefaultTableModel dtm = (DefaultTableModel)historyTbl.getModel();
        this.populateFieldsInit(dtm);
        
        Iterator<Object[]> itr = al.iterator();
        while (itr.hasNext()) {
            dtm.addRow((Object[])itr.next());
        }
    }
    
    private void populateFieldsInit(DefaultTableModel dtm) {
        clearTable();
        String[] colNames = new String[]{"Date", "Type", "Duration"};
        
        for(int i=0; i<colNames.length; i++)
            dtm.addColumn(colNames[i]);
    }
    
    public int insertDB() throws java.lang.NumberFormatException, java.text.ParseException {
        return this.insertDB(referral_priKey); 
    }
    
    public int insertDB(int rpk) throws java.lang.NumberFormatException, java.text.ParseException {
        System.out.println(_class+"/insertDB - starting");
        int count = -1;
        try {
            GregorianCalendar c = new GregorianCalendar();
            
            if(update_db) {
                main_insert.setTimestamp(1, new java.sql.Timestamp(c.getTimeInMillis()));
                main_insert.setInt(2, client_priKey);
                main_insert.setInt(3, referral_priKey);
                main_insert.setDate(4, miscMethods.convertTextToDate(dateTxt.getText(), owner._dateFormat, owner._dateNull));
                main_insert.setString(5, (String)JCBHolder.getSelectedItem("CONTACT_TYPE"));
                main_insert.setInt(6, Integer.parseInt(durationTxt.getText()) );
            
                count = owner.getdbConnection().executeUpdate(main_insert);
            } else {
                Object[] obj;
                Iterator<Object[]> itr = local_cache.iterator();
                while (itr.hasNext()) {
                    obj = (Object[])itr.next();
                    
                    main_insert.setTimestamp(1, new java.sql.Timestamp(c.getTimeInMillis()));
                    main_insert.setInt(2, client_priKey);
                    main_insert.setInt(3, rpk);
                    main_insert.setDate(4, (java.sql.Date)obj[0]);
                    main_insert.setString(5, (String)obj[1]);
                    main_insert.setInt(6, (int)obj[2] );
            
                    owner.getdbConnection().executeUpdate(main_insert);
                }
            }
            
            main_insert.clearParameters();
            
        } catch (SQLException sqle) { owner.exceptionEncountered(_class+"/insertDB", sqle);
        }
        System.out.println(_class+"/insertDB - finished");
        return count;
    }    
    
    public void insertLocal() throws java.lang.NumberFormatException, java.text.ParseException {
        local_cache.add(new Object[]{miscMethods.convertTextToDate(dateTxt.getText(), owner._dateFormat, owner._dateNull), (String)JCBHolder.getSelectedItem("CONTACT_TYPE"), Integer.parseInt(durationTxt.getText())});
    }
       
    public int updateDB() throws java.lang.NumberFormatException, java.text.ParseException {
        System.out.println(_class+"/updateDB - starting");
        int count = -1;
        try {
            GregorianCalendar c = new GregorianCalendar();
            main_update.setTimestamp(1, new java.sql.Timestamp(c.getTimeInMillis()));
            main_update.setDate(2, miscMethods.convertTextToDate(dateTxt.getText(), owner._dateFormat, owner._dateNull));
            main_update.setString(3, (String)JCBHolder.getSelectedItem("CONTACT_TYPE"));
            main_update.setInt(4, Integer.parseInt(durationTxt.getText()) );
            main_update.setInt(5, updatePK);
            
            count = owner.getdbConnection().executeUpdate(main_update);
            main_update.clearParameters();
        } catch (SQLException sqle) { owner.exceptionEncountered(_class+"/updateDB", sqle);
        }
        System.out.println(_class+"/updateDB - finished");
        return count;
    } 
    
    public void updateLocal() throws java.lang.NumberFormatException, java.text.ParseException {
        Object[] obj = (Object[])local_cache.get(updatePK);
        obj[0] = miscMethods.convertTextToDate(dateTxt.getText(), owner._dateFormat, owner._dateNull);
        obj[1] = (String)JCBHolder.getSelectedItem("CONTACT_TYPE");
        obj[2] = Integer.parseInt(durationTxt.getText());
    }
    
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

    public String subString(String str, int len) {
        if(str.length()<len) 
            return str;
        
        return str.substring(0, len)+"...";
    }
    
    public String dateNotSpecified(java.sql.Date d1) {
        return this.dateNotSpecified(d1, owner._notSpecified);
    }
    
    public String dateNotSpecified(java.sql.Date d1, String s) {
        if( owner._dateNull.equals(miscMethods.convertDateToText( d1, owner._dateFormat) ) ) { return s;
        } else { return miscMethods.convertDateToText( d1, owner._dateFormat); }
    }
    
    public java.sql.Date dateNotSpecified(String str) throws java.text.ParseException {
        if(str.equals(owner._notSpecified))
            return miscMethods.convertTextToDate(owner._dateFormat, owner._dateFormat, owner._dateNull);
        else
            return miscMethods.convertTextToDate(str, owner._dateFormat, owner._dateNull);
    }
    
    public void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }
    
    @Override public void setEnabled(boolean tof) {
        super.setEnabled(tof);
        
        dateTxt.setEnabled(tof);
        durationTxt.setEnabled(tof);
        JCBHolder.setEnabled(tof);
        historyTbl.setEnabled(tof);
        addBut.setEnabled(tof);
    }
    
    //************************
    class popupUpdateAct extends AbstractAction {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            int row = historyTbl.getSelectedRow();
            if(update_db) {
                try {
                    if(rSet.absolute(row+1)) {
                        updatePK = rSet.getInt("priKey");

                        dateTxt.setText(miscMethods.convertDateToText(rSet.getDate("contact_date"), owner._dateFormat));
                        JCBHolder.setSelectedItem("CONTACT_TYPE", rSet.getString("contact_type"));
                        durationTxt.setText( String.valueOf(rSet.getInt("contact_duration")) );

                        update_table = true;
                        addBut.setText("Update");
                    }
                } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/popupUpdateAct", sqle); 
                }
            } else {
                updatePK=row;
                Object[] obj = (Object [])local_cache.get(row);
                dateTxt.setText(miscMethods.convertDateToText((java.sql.Date)obj[0], owner._dateFormat));
                JCBHolder.setSelectedItem("CONTACT_TYPE", (String)obj[1]);
                durationTxt.setText( String.valueOf(obj[2]) );
                
                update_table = true;
                addBut.setText("Update");
            }
        }
    }
    
    class popupDeleteAct extends AbstractAction {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            int row = historyTbl.getSelectedRow();
            if(update_db) {
                try {
                    int result = JOptionPane.showConfirmDialog(null, "Really delete entry?", "Confirm delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if(result==0) {
                        if(rSet.absolute(row+1)) {
                            main_delete.setInt(1, rSet.getInt("priKey"));

                            owner.getdbConnection().executeUpdate(main_delete);
                            main_delete.clearParameters();
                        }
                    }
                } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/popupUpdateAct", sqle); 
                }
            } else {
                local_cache.remove(row);
            }
            refreshTable();
        }
    }
    
    class addAct extends AbstractAction {
        @Override public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            try {
                if(update_table) {
                    if(update_db)
                        updateDB();
                    else
                        updateLocal();
                } else {
                    if(update_db)
                        insertDB();
                    else
                        insertLocal();
                }
                refreshTable();
                clearFields();
            } catch(java.text.ParseException pe) { owner.exceptionEncountered(_class+"/addAct", pe); }
            
            update_table = false;
            addBut.setText("Add");
        }
    }

}