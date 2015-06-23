package aimapp.gui;

import aimapp.AimApp;
import aimapp.components.projectPanel3;
import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.JTabbedPane;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import jdbcApp.miscMethods;

public class ClientExistsDialog extends JDialog {
    private final String _class;
    private AimApp owner;
    private ClientFrame parent;
    private int[] cpk_ar;
    
    private PreparedStatement selectClientPS, selectReferralOpenPS, selectReferralClosedPS, updatePS;
    private JTabbedPane ctp;
    
    public ClientExistsDialog(AimApp owner, ClientFrame parent, int[] cpk_ar) {
        //super("Client exists");
        super(owner, "Client Exists", true);
        this._class = this.getClass().getName();
        this.owner = owner;
        this.parent = parent;
        this.cpk_ar = cpk_ar;
        
        this.selectClientPS = owner.getdbConnection().getPS("ps_select_client");
        this.selectReferralOpenPS = owner.getdbConnection().getPS("ps_select_referral_by_client_open");
        this.selectReferralClosedPS = owner.getdbConnection().getPS("ps_select_referral_by_client_closed");
        
        this.setLayout(new BorderLayout(2, 2));
        this.add(genMergePanel(), BorderLayout.CENTER);
        this.add(genButtonPanel(), BorderLayout.SOUTH);
        this.setSize(300, 200);
        
        
        this.processCPK_ar();
    }
    
    private JPanel genMergePanel() {
        JPanel mPanel = new JPanel(new BorderLayout(2,2));  mPanel.setBorder(BorderFactory.createTitledBorder("Possible merges"));
        
        ctp = new JTabbedPane();
        
        mPanel.add(ctp);
        
        return mPanel;
    }
    
    private JPanel genButtonPanel() {
        JPanel bPanel = new JPanel();
        
        JButton mergeBut = new JButton("Merge");
        JButton createBut = new JButton("Create new");
        JButton cancelBut = new JButton("Cancel");
        
        mergeBut.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                try {
                    parent.processReferralTabGroups(cpk_ar[ctp.getSelectedIndex()]); 
                    dispose();
                } catch (java.text.ParseException pe) { owner.exceptionEncountered(_class+"/mergeBut", pe); }
            }
        });
        createBut.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                try {
                    parent.processReferralTabGroups(parent.insertDB());
                    dispose();
                } catch (java.text.ParseException pe) { owner.exceptionEncountered(_class+"/createBut", pe); }    
            }
        });
        cancelBut.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                dispose();
            }
        });
        
        bPanel.add(mergeBut);
        bPanel.add(createBut);
        bPanel.add(cancelBut);
        
        return bPanel;
    }
    
    private void processCPK_ar() {
        try {
            for (int i=0;i<cpk_ar.length;i++) {
                selectClientPS.setInt(1, cpk_ar[i]);
                this.processCRS( owner.getdbConnection().executeQuery(selectClientPS) );
                selectClientPS.clearParameters();
            }
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/processCPK_ar", sqle); }
    }
    
    private void processCRS(ResultSet rs) throws SQLException {
        if(rs.next()) {
            ctp.add(rs.getString("forename")+" "+rs.getString("surname")+" -- "+rs.getInt("priKey"), createClientDetailPanel(rs.getInt("priKey")));
        }
    }
    
    private JPanel createClientDetailPanel(int pk) throws SQLException {
        JPanel cPanel = new JPanel(new GridLayout(0,2));
        JPanel openPanel = new JPanel(new GridLayout(0,1));  openPanel.setBorder(BorderFactory.createTitledBorder("Open referrals"));
        JPanel closedPanel = new JPanel(new GridLayout(0,1));  closedPanel.setBorder(BorderFactory.createTitledBorder("Closed referrals"));
        
        ResultSet r;
        
        selectReferralOpenPS.setInt(1, pk);
        r = owner.getdbConnection().executeQuery(selectReferralOpenPS);
        while(r.next()) {
            openPanel.add(new JLabel(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat)));
        }
        
        selectReferralClosedPS.setInt(1, pk);
        r = owner.getdbConnection().executeQuery(selectReferralClosedPS);
        while(r.next()) {
            closedPanel.add(new JLabel(miscMethods.convertDateToText(r.getDate("date_referral"), owner._dateFormat)));
        }
        
        selectReferralOpenPS.clearParameters();
        selectReferralClosedPS.clearParameters();
        
        cPanel.add(new JScrollPane(openPanel));
        cPanel.add(new JScrollPane(closedPanel));
        
        return cPanel;
    }
}