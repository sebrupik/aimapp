package aimapp.reports;

import jdbcApp.dbConnection;
import jdbcApp.miscMethods;
import jdbcApp.reports.ReportObject;
import jdbcApp.reports.ReportObjectParameter;

import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 *
 * @author Seb
 */
public class report_01 extends ReportObject {
    private final String _class;
    JButton runBut; 
    java.sql.Date start, end;
    
    public report_01(String name, String path, dbConnection dbCon) {
        super(name, path, dbCon);
        
        this.buildParameterObjects();
        this._class = this.getClass().getName();
    }
    
    @Override public void setComponents(JButton runBut) {
        //this.progressBar = progressBar;
        this.runBut = runBut;
    }
    
    @Override public void buildParameterObjects() {
        objParamHM = new HashMap<>();
        
        objParamHM.put("Start Date", new ReportObjectParameter(1, "Start Date", "DATE", "The start date you want to run the report from"));
        objParamHM.put("End Date", new ReportObjectParameter(2, "End Date", "DATE", "The end date for your search"));
    }
    
    @Override public void extractParameters() {
        start = (java.sql.Date)objParamHM.get("Start Date").value;
        end = (java.sql.Date)objParamHM.get("End Date").value;
    }
  
    @Override public void clearTempTables() {
        emptyTable("tmp_referrals_filtered");

    }
    
    @Override public Void doInBackground() {
        //progressBar.setValue(0);
        boolean firstStmt = false;
        int progress = 0;
        setProgress(progress);
        
        this.extractParameters();
        
        try {
            emptyTable("tmp_referrals_filtered");
            emptyTable("tmp_referrals_filtered_not_accepted");

            writeln("<TABLE width=100%><TR bgcolor=\"#b2d4f9\"><TD><B>AIM Contract Monitoring</B></TD></TR></TABLE>"); 
            writeln("<TABLE width=100%><TR bgcolor=\"#ff7200\"><TD>Stats for period : "+start+" to "+end+"</TD></TR></TABLE>");
            
            execute("CREATE TEMPORARY TABLE IF NOT EXISTS tmp_referrals_filtered AS SELECT * FROM aim_data_referrals WHERE (date_referral BETWEEN '"+start+"' and '"+end+"') AND is_accepted='1'") ;
                System.out.println("are we in the first round??");
                firstStmt = true;
                
                writeResultSet("Total no clients referred", executeQuery("SELECT COUNT(DISTINCT(client_priKey)) FROM tmp_referrals_filtered"));
                writeResultSet("Source of referrals", executeQuery("SELECT referral_source, COUNT(priKey) FROM tmp_referrals_filtered GROUP BY referral_source"));
                writeResultSet("Postcodes", executeQuery("SELECT postcode, COUNT(priKey) FROM tmp_referrals_filtered GROUP BY postcode"));

                writeResultSet("Number of referrals", executeQuery("SELECT COUNT(priKey) AS 'Total referrals' FROM tmp_referrals_filtered"));
                writeResultSet("Number of referrals (with first contact)", executeQuery("SELECT COUNT(priKey) AS 'Total referrals' FROM tmp_referrals_filtered WHERE date_first_contact != '0001-01-01'"));

                simpleWriteArrayInt("Age at referral", new String[]{"18-29 yrs", "30-40 yrs", "41-50 yrs", "51-64 yrs", "65-74 yrs", "75+ yrs", "Unknown"}, this.processAgeRanges(this.calculateAgeAtDate(executeQuery("SELECT date_referral, dob FROM tmp_referrals_filtered"), "YYYY-MM-DD", "dob", "date_referral" )), false );

                writeResultSet("Ethnicity", executeQuery("SELECT ethnicity, COUNT(priKey) FROM tmp_referrals_filtered GROUP BY ethnicity"));
           
                setProgress(50);

                
                
            execute("CREATE TEMPORARY TABLE IF NOT EXISTS tmp_referrals_filtered_not_accepted AS SELECT * FROM aim_data_referrals WHERE (date_referral BETWEEN '"+start+"' and '"+end+"') AND is_accepted='0'") ;
                writeResultSet("Reason not accepted", executeQuery("SELECT not_accepted_reason, COUNT(priKey) FROM tmp_referrals_filtered_not_accepted GROUP BY not_accepted_reason"));
                simpleWriteArrayInt("Age at referral", new String[]{"18-29 yrs", "30-40 yrs", "41-50 yrs", "51-64 yrs", "65-74 yrs", "75+ yrs", "Unknown"}, this.processAgeRanges(this.calculateAgeAtDate(executeQuery("SELECT date_referral, dob FROM tmp_referrals_filtered_not_accepted"), "YYYY-MM-DD", "dob", "date_referral" )), false );
                writeResultSet("Gender", executeQuery("SELECT gender, COUNT(priKey) FROM tmp_referrals_filtered_not_accepted GROUP BY gender"));

                writeResultSet("Ethnicity", executeQuery("SELECT ethnicity, COUNT(priKey) FROM tmp_referrals_filtered_not_accepted GROUP BY ethnicity"));
            

            if(firstStmt) {
                writeResultSet("Types of issues raised", executeQuery("SELECT accepted_issues, COUNT(priKey) FROM tmp_referrals_filtered GROUP BY accepted_issues"));
                writeResultSet("IMCA", executeQuery("SELECT accepted_category_imca, COUNT(priKey) FROM tmp_referrals_filtered WHERE accepted_category='IMCA' GROUP BY accepted_category_imca"));

                writeResultSet("No. of referrals by legislative category", executeQuery("SELECT accepted_category, COUNT(priKey) FROM tmp_referrals_filtered GROUP BY accepted_category"));
                writeResultSet("No of cases closed", executeQuery("SELECT COUNT(priKey) as cases_closed FROM tmp_referrals_filtered WHERE date_closed='0001-01-01' "));

                writeResultSet("Active cases", executeQuery("SELECT COUNT(priKey) as active_cases FROM tmp_referrals_filtered WHERE date_closed!='0001-01-01' "));
            }
        } catch(java.sql.SQLException sqle) {
            System.out.println(_class+"/processResultSet - "+sqle);
        }
        
        setProgress(100);
        
        /*while(progress <100) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            //progressBar.setValue(progress);
            progress+=10;
            setProgress(progress);
            System.out.println(progress);
        }
           */ 
        return null;
        
    }
    
    /*
        * Executed in event dispatch thread
        */
    @Override public void done() {
        Toolkit.getDefaultToolkit().beep();
        runBut.setEnabled(true);
        view();
        //taskOutput.append("Done!\n");
    }
    
    
    //*****************************
    
    private int[] calculateAgeAtDate(ResultSet rs, String pat, String item1, String item2) {
        System.out.println(_class+"/calculateAgeAtDate - starting");
        int index =0;
        int[] output;
        GregorianCalendar gc = new GregorianCalendar();
        System.out.println("here1 ?");
        try {
            System.out.println("here2 ?");
            rs.last();
            System.out.println("here3 ?");
            output = new int[rs.getRow()];
            System.out.println("here4 ? "+output.length);
            rs.beforeFirst();
            System.out.println("here5 ?");

            while (rs.next()) {
                System.out.println("going around");
                gc.setTimeInMillis( rs.getDate(item2).getTime() );
                output[index] = miscMethods.calcAge(rs.getString(item1),gc, pat);
                index++;
            }
        } catch (Exception pe) {
            System.out.println("miscMethods/calcAge - Bad date "+pe);
            return null;
        } 
        
        return output;
    }
    
    
    private int[] processAgeRanges(int[] ages) {
        System.out.println(_class+"/processAgeRanges - starting");
        int[] values = new int[7];
        
        for(int i=0; i<ages.length; i++) {
            if(ages[i] >= 18 & ages[i] >= 29)
                values[0]++;
            else if(ages[i] >= 30 & ages[i] <=40)
                values[1]++;
            else if(ages[i] >= 41 & ages[i] <=50)
                values[2]++;
            else if(ages[i] >= 51 & ages[i] <=64)
                values[3]++;
            else if(ages[i] >= 65 & ages[i] <=74)
                values[4]++;
            else if(ages[i] >= 75)
                values[5]++;
            else
                values[6]++;
        }
    
        System.out.println(_class+"/processAgeRanges - finished and about to return");
        return values;    
    }
}
