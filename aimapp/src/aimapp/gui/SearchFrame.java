package aimapp.gui;

//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TreeDemoProject/src/components/TreeDemo.java

import aimapp.AimApp;
import aimapp.components.CustomJTree;
import aimapp.components.projectPanel3;
import aimapp.objects.ClientInfo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.text.SimpleDateFormat;

public class SearchFrame extends projectPanel3 {
    final AimApp owner;
    String type;
    private final String _class;
    CustomJTree tree;
    PreparedStatement selectPS;
    DefaultMutableTreeNode rootTN = new DefaultMutableTreeNode("Clients");
    DefaultMutableTreeNode openTN = new DefaultMutableTreeNode("Open Clients");
    DefaultMutableTreeNode closedTN = new DefaultMutableTreeNode("Closed Clients");
    DefaultTreeModel treeModel = new DefaultTreeModel(rootTN);
    SimpleDateFormat sdf;
    
    //String[] fStr = new String[]{"Client name"};
    int group_index_openTN = 0;
    int group_index_closedTN = 0;
    String[] type_referral = new String[]{"Client name", "Month", "Staff name", "No group"};
    String[] type_client = new String[]{"Staff name", "Client name", "No group"};
    String[] fStr = new String[]{"Client name", "Month", "Staff name", "No group"};
    String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    
    JComboBox fCB;
    
    public SearchFrame(AimApp o, String type) {
        super("Search Frame");
        this.owner = o;
        this.type = type;
        this.sdf = new SimpleDateFormat(owner._dateFormat);
        this.setLayout(new BorderLayout(2,2));
        this._class = this.getClass().getName();
        
        tree = new CustomJTree(o, this, rootTN, type);
        JScrollPane treeView = new JScrollPane(tree);
        
        //add(genFilterPanel(), BorderLayout.NORTH);
        add(treeView, BorderLayout.CENTER);
        
        rootTN.add(openTN);
        rootTN.add(closedTN);
        
        if(type.equals("REFERRAL")) 
            fStr = type_referral;
        else if(type.equals("CLIENT"))
            fStr = type_client;
        
        try {
            if(type.equals("REFERRAL")) {
                group_index_openTN = Integer.parseInt(owner.getSysProperty("searchframe.fstr.index.referral.open"));
                group_index_closedTN = Integer.parseInt(owner.getSysProperty("searchframe.fstr.index.referral.closed"));
            } else if(type.equals("CLIENT")) {
                group_index_openTN = Integer.parseInt(owner.getSysProperty("searchframe.fstr.index.client.open"));
                group_index_closedTN = Integer.parseInt(owner.getSysProperty("searchframe.fstr.index.client.closed"));
            }
        } catch(IOException ioe) { System.out.println(_class+"/dbConnectionDialog - "+ioe); }
        
        
        //refreshTree();
        callOverrides();
    }
    
    private void callOverrides() {
        refreshTree();
    }
   
    
    public void createNodes(DefaultMutableTreeNode t, PreparedStatement ps, int index) {
        this.clearTree(t);
        
        java.sql.Date rsd;
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        
        ResultSet rs = owner.getdbConnection().executeQuery(ps);
        
        try {
            if (type.equals("REFERRAL")) {
                while(rs.next()) {
                    cal.setTime(rs.getDate("date_referral"));
                    String name = rs.getString("surname")+", "+rs.getString("forename");
                        if( name.trim().length()==0 | name.startsWith("null") )
                            name = "*no name";


                    if( fStr[index].equals("Month") ) {
                        addToTree(t, months[cal.get(Calendar.MONTH)], name, rs.getInt("priKey"));
                    } else if( fStr[index].equals("Staff name") ) {
                        addToTree(t, rs.getString("name_staff"), name, rs.getInt("priKey"));
                    } else if( fStr[index].equals("Client name") ) {
                        addToTree(t, name.substring(0,1), name, rs.getInt("priKey"));
                    } else if( fStr[index].equals("No group") ) {
                        addToTree(t, "No Group", name, rs.getInt("priKey"));
                    }
                }
            } else if (type.equals("CLIENT")) {
                while(rs.next()) {
                    String name = rs.getString("surname")+", "+rs.getString("forename");
                        if( name.trim().length()==0 | name.startsWith("null") )
                            name = "*no name";
                    
                    if( fStr[index].equals("Client name") ) {
                        addToTree(t, name.substring(0,1), name, rs.getInt("priKey"));
                    } else if( fStr[index].equals("No group") ) {
                        addToTree(t, "No Group", name, rs.getInt("priKey"));
                    } else if( fStr[index].equals("Staff name") ) {
                        addToTree(t, rs.getString("name_staff"), name, rs.getInt("priKey"));
                    }
                }
            }
        } catch(SQLException sqle) { owner.exceptionEncountered(_class+"/createNodes", sqle); }
        
    }
    
    private void addToTree(DefaultMutableTreeNode t, String index, String node, int cpk) {
        System.out.println("Building a tree using "+index+" as a node");
        
        if(index == null || index.trim().length()==0)
            index="Not specified";
        
        boolean nodeFound = attemptAddToNode(t, index, node, cpk);
        
        System.out.println("Is "+index+" already a node? :"+nodeFound);
        
        if(!nodeFound) {
            addNodeInSortedOrder(t, new DefaultMutableTreeNode(index));
            attemptAddToNode(t, index, node, cpk);
        }
    }
    
    private boolean attemptAddToNode(DefaultMutableTreeNode t, String index, String node, int cpk) {
        DefaultMutableTreeNode tmpNode;
        boolean b = false;
        for (Enumeration<DefaultMutableTreeNode> e = t.children(); e.hasMoreElements();) {
            tmpNode = e.nextElement();
            if(tmpNode.toString().equals(index)) {
                System.out.println("adding to node: "+index+" cpk: "+cpk);
                addNodeInSortedOrder(tmpNode, new DefaultMutableTreeNode(new ClientInfo(node, cpk)));
                b = true;
            }
        }
        return b;
    }
    
    private void addNodeInSortedOrder(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) { 
        int n = parent.getChildCount(); 
        if(n==0) { 
            parent.add(child); 
            return; 
        } 
        DefaultMutableTreeNode node=null; 
        for(int i=0;i<n;i++){ 
            node = (DefaultMutableTreeNode)parent.getChildAt(i); 
            if(node.toString().compareTo(child.toString())>0){ 
                parent.insert(child, i); 
                return; 
            } 
        } 
        parent.add(child);
    } 
    
    public void refreshTree() {
        //tree.clearSelection();
        //tree.collapseRow(0);
        if(type.equals("REFERRAL")) {
            this.createNodes(openTN, owner.getdbConnection().getPS("ps_select_referral_all_open"), group_index_openTN);
            this.createNodes(closedTN, owner.getdbConnection().getPS("ps_select_referral_all_closed"), group_index_closedTN);
        } else if(type.equals("CLIENT")) {
            this.createNodes(openTN, owner.getdbConnection().getPS("ps_select_client_all_open"), group_index_openTN);
            this.createNodes(closedTN, owner.getdbConnection().getPS("ps_select_client_all_closed"), group_index_closedTN);
        }
        
        ((DefaultTreeModel )tree.getModel()).nodeStructureChanged((TreeNode)openTN);
        ((DefaultTreeModel )tree.getModel()).nodeStructureChanged((TreeNode)closedTN);
    }
    
    private void clearTree(DefaultMutableTreeNode t) {
        t.removeAllChildren();
    }
    
    /*private JPanel genFilterPanel() {
        JPanel fPanel = new JPanel();
        
        fCB = new JComboBox(fStr);
        //fCB.setSelectedIndex(-1);
        
        fPanel.add(fCB);
        
        
        fCB.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { 
                String s = (String)fCB.getSelectedItem();
                
                refreshTree();
            }
        });
        
        return fPanel;
    }*/
    
    @Override public String toString() { return "SearchFrame"; }
    
    public void setGroupIndex(String tn, String option) {
        if(tn.equals("OPEN")) {
            this.group_index_openTN = this.indexOfGroup(option);
        } else if (tn.equals("CLOSED")) {
            this. group_index_closedTN = this.indexOfGroup(option);
        }
        refreshTree();
    }
    
    private int indexOfGroup(String option) {
        for (int i=0; i< fStr.length; i++) {
            if(fStr[i].equals(option))
                return i;
        }
        System.out.println(_class+"/indexOfGroup - can't find the index! Using default value");
        return 0;
    }

    @Override public void closingActions() { writeProperties(); }

    @Override public void writeProperties() {
        if(type.equals("REFERRAL")) {
            owner.saveSysProperty("searchframe.fstr.index.referral.open", String.valueOf(group_index_openTN));
            owner.saveSysProperty("searchframe.fstr.index.referral.closed", String.valueOf(group_index_closedTN));
        } else if(type.equals("CLIENT")) {
            owner.saveSysProperty("searchframe.fstr.index.client.open", String.valueOf(group_index_openTN));
            owner.saveSysProperty("searchframe.fstr.index.client.closed", String.valueOf(group_index_closedTN));
        }
    }
}