/*
 * http://www.java-tips.org/java-se-tips/javax.swing/have-a-popup-attached-to-a-jtree.html
 */
package aimapp.components;

import jdbcApp.miscMethods;
import aimapp.AimApp;
import aimapp.objects.ClientInfo;
import aimapp.gui.SearchFrame;

//import aimapp.gui.JTreeFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomJTree extends JTree implements ActionListener {
    private final String _class;
    final String type;
    JPopupMenu popup;
    JMenuItem mi;
    final AimApp owner;
    final SearchFrame parent;
    JPopupMenu jpm;
    
    PreparedStatement referral_selectPS, referral_deletePS, client_deletePS, deletePS;
    
    public CustomJTree(AimApp o, SearchFrame parent, DefaultMutableTreeNode dmtn, String type) {
        super(dmtn);
        this._class = this.getClass().getName();
        this.owner = o;
        this.parent = parent;
        this.type = type;
        
        this.referral_selectPS = owner.getdbConnection().getPS("ps_select_referral_new_episode");
        this.referral_deletePS = owner.getdbConnection().getPS("ps_delete_referral");
        this.client_deletePS = owner.getdbConnection().getPS("ps_delete_client_perm");
        
        jpm = new JPopupMenu();
        
        JMenuItem newEpiMI = new JMenuItem("New episode");
        newEpiMI.addActionListener(this);
        newEpiMI.setActionCommand("New episode");
        
        JMenuItem delEntryMI = new JMenuItem("Delete entry");
        delEntryMI.addActionListener(this);
        delEntryMI.setActionCommand("Delete entry");
        
        
        
        JMenu sortJM = new JMenu("Group by...");
        
        JMenuItem no_group_MI = new JMenuItem("No group");
        no_group_MI.addActionListener(this);
        no_group_MI.setActionCommand("no_group");
        
        JMenuItem sort_client_name_MI = new JMenuItem("Client name");
        sort_client_name_MI.addActionListener(this);
        sort_client_name_MI.setActionCommand("sort_client_name");
        
        JMenuItem sort_staff_name_MI = new JMenuItem("Staff name");
        sort_staff_name_MI.addActionListener(this);
        sort_staff_name_MI.setActionCommand("sort_staff_name");
        
        JMenuItem sort_month_MI = new JMenuItem("Month");
        sort_month_MI.addActionListener(this);
        sort_month_MI.setActionCommand("sort_month");
        
        if(type.equals("REFERRAL")) {
            sortJM.add(no_group_MI);
            sortJM.add(sort_client_name_MI);
            sortJM.add(sort_staff_name_MI);
            sortJM.add(sort_month_MI);
        } else if(type.equals("CLIENT")) {
            sortJM.add(no_group_MI);
            sortJM.add(sort_staff_name_MI);
            sortJM.add(sort_client_name_MI);
        }
        
        if(type.equals("REFERRAL"))
            jpm.add(newEpiMI);
        
        jpm.add(delEntryMI);
        jpm.add(sortJM);
    
        addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased( MouseEvent e ) {
                if ( e.isPopupTrigger()) {
                    jpm.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                }
            }
            
            @Override public void mousePressed(MouseEvent e) {
                int selRow = getRowForLocation(e.getX(), e.getY());
                TreePath selPath = getPathForLocation(e.getX(), e.getY());
                
                setSelectionPath(selPath);

                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                        if(SwingUtilities.isRightMouseButton(e) == true) {
                            jpm.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                        }
                    }
                    else if(e.getClickCount() == 2) {
                        System.out.println("DOUBLE CLICK - tap tap");
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                        if(dmtn.isLeaf()) {
                            ClientInfo ci = (ClientInfo)dmtn.getUserObject();
                            if(getType().equals("REFERRAL")) 
                                owner.createFrame("REFERRAL", ci.getCPK() );
                            else if(getType().equals("CLIENT"))
                                owner.createFrame("CLIENT", ci.getCPK() );
                        } else {
                            System.out.println("You've not clicked on a leaf that I can work on!");
                        }
                    }
                }
            }
        });
    }
    
    @Override public void actionPerformed(ActionEvent ae) {
        DefaultMutableTreeNode dmtn, node;
        
        TreePath path = this.getSelectionPath();
        dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
        ClientInfo ci = null;
        if(dmtn.isLeaf())
            ci = (ClientInfo)dmtn.getUserObject();
        
        String target = openOrClosedParent(path);
       
        if (ae.getActionCommand().equals("New episode") & ci!=null) {
            try {
                referral_selectPS.setInt(1, ci.getCPK());
                java.util.HashMap h = miscMethods.rsToHashmap(owner.getdbConnection().executeQuery(referral_selectPS));
                referral_selectPS.clearParameters();
                
                owner.createNewEpisode(h);
            } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/actionPerformed", sqle);  }
        }
        if (ae.getActionCommand().equals("Delete entry") & ci !=null) {
            int n = JOptionPane.showConfirmDialog(this, "Would you like to delte this client entry?", "Delete entry", JOptionPane.YES_NO_OPTION);
            if(n==0) {  //yes
                try {
                    if(type.equals("REFERRAL")) {
                        deletePS = referral_deletePS;
                    } else if (type.equals("CLIENT")) {
                        deletePS = client_deletePS;
                    }
                    
                    deletePS.setInt(1, ci.getCPK());
                    owner.getdbConnection().executeUpdate(deletePS);
                    deletePS.clearParameters();
                    owner.refreshSearchFrame();
                }  catch(SQLException sqle) { owner.exceptionEncountered(_class+"/actionPerformed", sqle);  }
            }
        } 
        if (ae.getActionCommand().equals("no_group")) {
            if (target.equals("OPEN"))
                parent.setGroupIndex("OPEN", "No group");
            if (target.equals("CLOSED"))
                parent.setGroupIndex("CLOSED", "No group");
        }
        if (ae.getActionCommand().equals("sort_client_name")) {
            if (target.equals("OPEN"))
                parent.setGroupIndex("OPEN", "Client name");
            if (target.equals("CLOSED"))
                parent.setGroupIndex("CLOSED", "Client name");
        }
        if (ae.getActionCommand().equals("sort_staff_name")) {
            if (target.equals("OPEN"))
                parent.setGroupIndex("OPEN", "Staff name");
            if (target.equals("CLOSED"))
                parent.setGroupIndex("CLOSED", "Staff name");
        }
        if (ae.getActionCommand().equals("sort_month")) {
            if (target.equals("OPEN"))
                parent.setGroupIndex("OPEN", "Month");
            if (target.equals("CLOSED"))
                parent.setGroupIndex("CLOSED", "Month");
        }
    }
    
    private String openOrClosedParent(TreePath tp) {
        Object[] path = tp.getPath();
        
        for (int i=0; i<path.length; i++) {
            if( ((DefaultMutableTreeNode)path[i]).toString().equals("Open Clients") ) {
                return "OPEN";
            } else if( ((DefaultMutableTreeNode)path[i]).toString().equals("Closed Clients") ){
                return "CLOSED";
            }
        }
        
        return "UNKNOWN";
    }
    
    private String getType() { return type; }
}
