package aimapp.gui.actions;

import aimapp.AimApp;
import jdbcApp.miscMethods;

import javax.swing.JComboBox;

public class JComboBoxEnablesOneDisablesAnotherAndChangesContent extends JComboBoxEnablesOneDisablesAnother {
    private final String _class;
    private AimApp owner;
    JComboBox target3;
    String mysql1, mysql2;
    
    public JComboBoxEnablesOneDisablesAnotherAndChangesContent(AimApp owner, JComboBox source, JComboBox target1, JComboBox target2, JComboBox target3, String[] trigger, String mysql1, String mysql2) {
        super(source, target1, target2, trigger);
        this._class = this.getClass().getName();
        this.owner = owner;
        this.target3 = target3;
        this.mysql1 = mysql1;
        this.mysql2 = mysql2;
    }
    
    @Override public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (containsTrigger((String)source.getSelectedItem())) {
            this.swapAround(target2, target1);
            this.changeContents(target3, mysql1);
        } else {
            this.swapAround(target1, target2);
            this.changeContents(target3, mysql2);
        }
    }
    
    private void changeContents(JComboBox target, String source) {
        System.out.println(_class+"/changeContents - starting");
        target.removeAllItems();
        String[] s = miscMethods.colToArray(owner.getdbConnection().executeQuery(source), 1);
        for (int i=0; i<s.length; i++)
            target.addItem(s[i]);
        
        System.out.println(_class+"/changeContents - finished");
    }
}