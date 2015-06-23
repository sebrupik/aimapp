package aimapp.gui;

//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TreeDemoProject/src/components/TreeDemo.java

import aimapp.AimApp;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.tree.TreePath;
import java.text.SimpleDateFormat;

public class JTreeFrame extends JInternalFrame implements TreeSelectionListener {
    final AimApp owner;
    JTree tree;
    PreparedStatement selectPS;
    DefaultMutableTreeNode rootTN = new DefaultMutableTreeNode("Clients");
    DefaultMutableTreeNode openTN = new DefaultMutableTreeNode("Open Clients");
    DefaultMutableTreeNode closedTN = new DefaultMutableTreeNode("Closed Clients");
    DefaultTreeModel treeModel = new DefaultTreeModel(rootTN);
    SimpleDateFormat sdf;
    
    String[] fStr = new String[]{"Staff name", "Client name", "Staff name"};
    //String[] fStr = new String[]{"Client name", "Month", "Staff name", "Post Code"};
    String[] months = new String[]{"January", "February", "March", "April", "June", "July", "August", "September", "October", "November", "December"};
    
    JComboBox fCB;
    JPopupMenu jpm;
    
    public JTreeFrame(AimApp o) {
        super("Tree Frame", true, true, true, true);
        this.owner = o;
        this.sdf = new SimpleDateFormat(owner._dateFormat);
        this.setLayout(new BorderLayout(2,2));
        
        tree = new JTree(rootTN);
        JScrollPane treeView = new JScrollPane(tree);
        jpm = this.genPopupMenu();
        
        add(genFilterPanel(), BorderLayout.NORTH);
        add(treeView, BorderLayout.CENTER);
        
        rootTN.add(openTN);
        rootTN.add(closedTN);
        
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                
                tree.setSelectionPath(selPath);

                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                        if(SwingUtilities.isRightMouseButton(e) == true) {
                            jpm.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                        }
                    }
                    else if(e.getClickCount() == 2) {
                        System.out.println("DOUBLE CLICK");
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                        if(dmtn.isLeaf()) {
                            clientInfo ci = (clientInfo)dmtn.getUserObject();
                            owner.createFrame("REFERRAL", ci.getCPK() );
                        } else {
                            System.out.println("You've not clicked on a leaf that I can work on!");
                        }
                    }
                }
            }
        };
        tree.addMouseListener(ml);
        
        //refreshTree();
        callOverrides();
    }
    
    private void callOverrides() {
        refreshTree();
    }
    
    @Override public void valueChanged(TreeSelectionEvent e) { }
    
    private JPopupMenu genPopupMenu() {
        JPopupMenu jpm = new JPopupMenu();
        
        JMenuItem newEpiMI = new JMenuItem("New Episode");
        JMenuItem delEntryMI = new JMenuItem("Delete entry");
        
        jpm.add(newEpiMI);
        jpm.add(delEntryMI);
        
        return jpm;
    }
    
    public void createNodes(DefaultMutableTreeNode t, PreparedStatement ps) {
        this.clearTree(t);
        
        //DefaultMutableTreeNode category = null;
        //DefaultMutableTreeNode leaf = null;
        java.sql.Date rsd;
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        
        ResultSet rs = owner.getdbConnection().executeQuery(ps);
        
        try {
            while(rs.next()) {
                cal.setTime(rs.getDate("date_referral"));

                if( ((String)fCB.getSelectedItem()).equals("Month") ) {
                    addToTree(t, months[cal.get(Calendar.MONTH)], sdf.format(cal.getTime()), rs.getInt("priKey"));
                } else if( ((String)fCB.getSelectedItem()).equals("Staff name") ) {
                    addToTree(t, rs.getString("name_staff"), sdf.format(cal.getTime()), rs.getInt("priKey"));
                } else if( ((String)fCB.getSelectedItem()).equals("Client name") ) {
                    String name = rs.getString("surname")+", "+rs.getString("forename");
                    if( name.trim().length()==0 | name.startsWith("null") )
                        name = "*no name";
                    addToTree(t, name.substring(0,1), name, rs.getInt("priKey"));
                }
            }
        } catch(SQLException sqle) { }
        
    }
    
    private void addToTree(DefaultMutableTreeNode t, String index, String node, int cpk) {
        System.out.println("Building a tree using "+index+" as a node");
        
        if(index == null || index.trim().length()==0)
            index="Not specified";
        
        boolean monthFound = attemptAddToNode(t, index, node, cpk);
        
        System.out.println("Is "+index+" already a node? :"+monthFound);
        
        if(!monthFound) {
            t.add(new DefaultMutableTreeNode(index));
            attemptAddToNode(t, index, node, cpk);
        }
    }
    
    private boolean attemptAddToNode(DefaultMutableTreeNode t, String index, String node, int cpk) {
        DefaultMutableTreeNode tmpNode;
        boolean b = false;
        for (Enumeration<DefaultMutableTreeNode> e = t.children(); e.hasMoreElements();) {
            tmpNode = e.nextElement();
            if(tmpNode.toString().equals(index)) {
                System.out.println("adding to month: "+index+" cpk: "+cpk);
                tmpNode.add(new DefaultMutableTreeNode(new clientInfo(node, cpk)));
                b = true;
            }
        }
        return b;
    }
    
    public void refreshTree() {
        //tree.clearSelection();
        //tree.collapseRow(0);
        this.createNodes(openTN, owner.getdbConnection().getPS("ps_select_referral_all_open"));
        this.createNodes(closedTN, owner.getdbConnection().getPS("ps_select_referral_all_closed"));
    }
    
    private void clearTree(DefaultMutableTreeNode t) {
        
        //for (Enumeration<DefaultMutableTreeNode> e = t.children(); e.hasMoreElements();) {
        //    t.removeNodeFromParent(e.nextElement());
        
        t.removeAllChildren();
        
        //for(int i=0; i<t.getChildCount();i++)
        //    t.remove(i);
    }
    
    private JPanel genFilterPanel() {
        JPanel fPanel = new JPanel();
        
        fCB = new JComboBox(fStr);
        //fCB.setSelectedIndex(-1);
        
        fPanel.add(fCB);
        
        
        fCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                String s = (String)fCB.getSelectedItem();
                
                refreshTree();
            }
        });
        
        return fPanel;
    }
    
    public String toString() {
        return "JTreeFrame";
    }
    
    
    private class clientInfo {
        String title;
        int cpk;
        
        public clientInfo(String title, int cpk) {
            this.title = title;
            this.cpk = cpk;
        }
        
        public String toString() { return title; }
        public int getCPK() { return cpk; }
    }
}
