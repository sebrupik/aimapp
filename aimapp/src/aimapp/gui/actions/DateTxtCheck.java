package aimapp.gui.actions;

import javax.swing.JTextField;
import jdbcApp.miscMethods;

public class DateTxtCheck implements java.awt.event.FocusListener {
    private JTextField tf;
    private String _dateFormat, _dateNull;
    public DateTxtCheck(JTextField tf, String _dateFormat, String _dateNull) { this.tf= tf;  this._dateFormat = _dateFormat;  this._dateNull = _dateNull; }

    @Override public void focusGained(java.awt.event.FocusEvent e) { tf.selectAll(); }
    @Override public void focusLost(java.awt.event.FocusEvent e) {
        try {
            if(!(tf.getText().equals(_dateNull)) && miscMethods.convertTextToDate(tf.getText(), _dateFormat, _dateNull) == null)
                tf.setText(_dateNull);
        } catch (java.text.ParseException pe) { 
            System.out.println("customProPanelDBDialog/dateTxtCheck - "+pe); 
            tf.setText(_dateNull); 
        }
    }
}
