package aimapp;

import aimapp.gui.SearchFrame;
import aimapp.gui.ReferralFrame;
import aimapp.gui.ClientFrame;
import aimapp.reports.*;
import jdbcApp.actions.displayDBEditDialogAction;
import jdbcApp.reports.ReportSelectionFrame;

import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import jdbcApp.jdbcApp;

import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;

public class AimApp extends jdbcApp { 
    private final String _class;
    
    public String _dateFormat, _dateNull, _notSpecified;
    
    public JDesktopPane jdp;
    
    public AimApp(String propsStr, String psRBStr, Logger myLogger) { 
        super(propsStr, psRBStr, myLogger);
        this._class = this.getClass().getName();
        
        this.genMainPanel();
    }

 
    public static void main(String[] args) {
        final java.util.logging.Logger myLogger = java.util.logging.Logger.getLogger("AimApp");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break; 
                }
            }
        } catch (Exception e) {
            myLogger.logp(java.util.logging.Level.SEVERE, "AimApp", "main", e.toString());
            System.out.println(e);
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        JFrame aa = new AimApp("client.properties", "aimapp/preparedstatements.properties", myLogger);
        aa.setVisible(true);
    }
    
    /*
     * Pull the system variables from the file and store them locally
     */
    @Override public void assignSystemVariables() throws java.io.IOException { 
        this._dateFormat = getSysProperty("_date_format");
        this._dateNull = getSysProperty("_date_null");
        this._notSpecified = getSysProperty("_not_specified");
    }
    
    @Override public void writeSystemVariables() {
        saveSysProperty("sizeX", String.valueOf(this.getSize().width));
        saveSysProperty("sizeY", String.valueOf(this.getSize().height));
    }
    
    public String getSysVar(String name) {
        return "";
    }
    
    private void genMainPanel() {
        content.add(genMenuBar(), BorderLayout.NORTH);
        content.add(genDesktop(), BorderLayout.CENTER);
    }
    
    private JDesktopPane genDesktop() {
        jdp = new JDesktopPane();
        //jdp.setBackground(Color.red);

        return jdp;
    }
    
    private JMenuBar genMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        //**************** SYSTEM *******************
        JMenu sysMenu = new JMenu("System");
        //JMenuItem connectMI = new JMenuItem("Connection");
        JMenuItem dbMI = new JMenuItem("DB Edits");
        dbMI.addActionListener(new displayDBEditDialogAction(this));
        
        
        JMenuItem exitMI = new JMenuItem("Exit");
        exitMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                closeApp();
            }
        });
        
        sysMenu.add(dbMI);
        //sysMenu.add(connectMI);
        sysMenu.add(exitMI);
        
        //**************** REFERRALS *******************
        JMenu refMenu = new JMenu("Referrals");
        //menu.setMnemonic(KeyEvent.VK_N);
        //JMenuItem treeMI = new JMenuItem("Show Referrals");
        //JMenuItem newReferralMI = new JMenuItem("New Referral");
        JMenuItem newClientMI = new JMenuItem("New Client");
        JMenuItem clientTreeMI = new JMenuItem("Show clients");
        //treeMI.setMnemonic(KeyEvent.VK_N);
        /*treeMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                createFrame("TREE_REF", -1);
            }
        });
        newReferralMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                createFrame("REFERRAL", -1);
            }
        });*/
        newClientMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                createFrame("CLIENT", -1);
            }
        });
        clientTreeMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                createFrame("TREE_CLIENT", -1);
            }
        });

        //refMenu.add(treeMI);
        //refMenu.add(newReferralMI);
        refMenu.add(newClientMI);
        refMenu.add(clientTreeMI);
        
        //************************* REPORTS
        JMenu reportMenu = new JMenu("Reports");
        JMenuItem reportMI = new JMenuItem("Report Manager");
        
        reportMI.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                createFrame("REPORT_MANAGER", -1);
            }
        });
        
        reportMenu.add(reportMI);
        
        //****************************
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpContMI = new JMenuItem("Help Contents");
        JMenuItem aboutMI = new JMenuItem("About");
        
        helpMenu.add(helpContMI);
        helpMenu.add(aboutMI);
        
        menuBar.add(sysMenu);
        menuBar.add(refMenu);
        menuBar.add(reportMenu);
        //menuBar.add(helpMenu);
        return menuBar;
    }
    
    public void createFrame(String which, int num) {
        JInternalFrame jif = null;
        
        if(getdbConnectionStatus()==0) {
            if(which.equals("TREE_REF")) {
                jif = new SearchFrame(this, "REFERRAL");
                jif.setSize(200, jdp.getHeight()-20);
            } else if(which.equals("TREE_CLIENT")) {
                jif = new SearchFrame(this, "CLIENT");
                jif.setSize(200, jdp.getHeight()-20);
            } else if(which.equals("REFERRAL")) {
                System.out.println("make new referral frame!");
                jif = new ReferralFrame(this, num);
                //jif.setSize(515, 550);
                try {
                    jif.setSize(Integer.parseInt(getSysProperty("sizeX.referralFrame")), Integer.parseInt(getSysProperty("sizeY.referralFrame")));
                } catch(java.io.IOException ioe) { System.out.println(_class+"/createFrame - "+ioe); }
            } else if(which.equals("CLIENT")) {
                System.out.println("make new client frame!");
                
                jif = new ClientFrame(this, num);
                try {
                    jif.setSize(Integer.parseInt(getSysProperty("sizeX.clientFrame")), Integer.parseInt(getSysProperty("sizeY.clientFrame")));
                } catch(java.io.IOException ioe) { System.out.println(_class+"/createFrame - "+ioe); }
            } else if(which.equals("REPORT_MANAGER")) {
                System.out.println("make new report manager frame");
                
                //jif = new ReportSelectionFrame(this, "Report Manager");
                jif = genReportSelctionFrame();
                jif.setSize(200, 200);
            }
        }
        this.attemptAddingJIF(jif);
    }
    
    public void createNewEpisode(java.util.HashMap h) {
        JInternalFrame jif = new ReferralFrame(this, -1, h);
        jif.setSize(515, 550);
        attemptAddingJIF(jif);
    }
    
    private void attemptAddingJIF(JInternalFrame jif) {
        if(jif != null) {
            if(!frameExists(jif)) {
                jif.setVisible(true);
                jdp.add(jif);
            
                System.out.println("frame added??");

                try {
                    jif.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    System.out.println(e);  
                }
            } else {
                System.out.println("Frame exisits, nulling object!");
                jif = null;
            }
        }
    }
    
    public boolean frameExists(JInternalFrame obj) {
        JInternalFrame[] allFrames = jdp.getAllFrames();
        
        for (int i=0; i<allFrames.length; i++) {
            if(allFrames[i].toString().equals(obj.toString()))
                return true;
            else
                System.out.println("JInternalFrame not already on the desktop!");
        }
        return false;
    }
    
    public int findFrame(String query) {
        int index=-1;
        JInternalFrame[] allFrames = jdp.getAllFrames();
        
        for (int i=0; i<allFrames.length; i++) {
            if(allFrames[i].toString().contains(query))
                return i;
            else
                System.out.println("JInternalFrame not already on the desktop!");
        }
        return index;
    }
    
    public void refreshSearchFrame() {
        System.out.println(_class+"/refreshSearchFrame - starting");
        JInternalFrame[] allFrames = jdp.getAllFrames();
        
        int i = this.findFrame("SearchFrame");
        
        if(i!=-1)
            ((SearchFrame)allFrames[i]).refreshTree();
    }
    
    private ReportSelectionFrame genReportSelctionFrame() {
        ReportSelectionFrame rsf = new ReportSelectionFrame(this, "Report Manager");
        
        rsf.addReportObject( new report_01("First report", "some_file_path", getdbConnection()) );
        
        return rsf;
    }
}
