import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 @author Chelsea Dorich (Email: <a href="mailto:"robotqi@gmail.com>robotqi@gmail.com</a>)
 @version 1.1 05/14/2014
 @assignment.number A190-14
 @prgm.usage Called from the operating system
 @see "Gaddis, 2013, Starting out with Java, From Control Structures, 5th Edition"
 @see "<a href='http://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/index.html'>JavaDoc Documentation</a>

 */
public class A19014 extends JDialog
{
    private JPanel contentPane;
    private JComboBox cboStudents;
    private JTextField textField1;
    private JButton createDBAndTablesButton;
    private JButton populateGradesButton;
    private JButton populateRosterButton;
    private JButton populateComboBoxButton;
    private JButton createReportButton;
    private JButton buttonExit;
    private JLabel lblEmail;
    private JLabel lblStuID;
    private JLabel lblQuiz;
    private JLabel lblAsgn;
    private JLabel lblTotal;
    private JLabel lblLetter;
    private JButton buttonOK;

    /**
     * public method with event handlers/action listeners
     */
    public A19014()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonExit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onExit();
            }
        });

createDBAndTablesButton.addActionListener(new ActionListener()
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        DBUpdt db = new DBUpdt();
        db.openConnection("GRADEBOOK");

        db.execute("DROP TABLE grades");
        db.execute("DROP TABLE roster");
                db.execute("CREATE TABLE grades (" +
                "stuid VARCHAR(7) NOT NULL, sem VARCHAR(6) NOT NULL, crs VARCHAR(4) NOT NULL, fedstatus VARCHAR(20) NOT NULL, "
                + "enrlstatus VARCHAR(20) NOT NULL, adddate CHAR(20) NOT NULL, A01 VARCHAR(6) DEFAULT '0', A02 VARCHAR(6) DEFAULT '0', " +
                        "A03 VARCHAR(6) DEFAULT '0', A04 VARCHAR(6) DEFAULT '0', A05 VARCHAR(6) DEFAULT '0', A06 VARCHAR(6) DEFAULT '0', " +
                        "A07 VARCHAR(6) DEFAULT '0', A08 VARCHAR(6) DEFAULT '0', A09 VARCHAR(6) DEFAULT '0', A10 VARCHAR(6) DEFAULT '0', " +
                        "A11 VARCHAR(6) DEFAULT '0', A12 VARCHAR(6) DEFAULT '0', A13 VARCHAR(6) DEFAULT '0', A14 VARCHAR(6) DEFAULT '0', " +
                        "A15 VARCHAR(6) DEFAULT '0', Q01 VARCHAR(6) DEFAULT '0', Q02 VARCHAR(6) DEFAULT '0', Q03 VARCHAR(6) DEFAULT '0', " +
                        "Q04 VARCHAR(6) DEFAULT '0', Q05 VARCHAR(6) DEFAULT '0', Q06 VARCHAR(6) DEFAULT '0', Q07 VARCHAR(6) DEFAULT '0', " +
                        "Q08 VARCHAR(6) DEFAULT '0', Q09 VARCHAR(6) DEFAULT '0', Q10 VARCHAR(6) DEFAULT '0', Q11 VARCHAR(6) DEFAULT '0', " +
                        "Q12 VARCHAR(6) DEFAULT '0', Q13 VARCHAR(6) DEFAULT '0', Q14 VARCHAR(6) DEFAULT '0', Q15 VARCHAR(6) DEFAULT '0', " +
                        "WTE VARCHAR(8) DEFAULT '0.00', lastaccess VARCHAR(50) NOT NULL, total VARCHAR(15) DEFAULT '0'"
                        + ")");
        db.execute("CREATE TABLE roster (" +
                "stuid VARCHAR(25) NOT NULL, firstname VARCHAR(30) NOT NULL, lastname VARCHAR(30) NOT NULL, email VARCHAR(30) NOT NULL)");
        db.close();

    }});

        populateGradesButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                INET net = new INET();
                DBUpdt db = new DBUpdt();
                String strURL = "http://faculty.sdmiramar.edu/jcouture/2014sp/cisc190/webct/manual/data-grades.txt";
                String strFile = "C:\\Users\\Chelsea\\A19014\\Data\\grades.txt";
                String[] strArray = new String[38];
                String strFileRaw = "";
                String strRecord = "";

                String strStudentId = "";
                String strSem = "";
                String strCrs = "";
                String strAstNum = "";
                String strQuiz = "";
                String WTE = "";
                String fedstatus = "";
                String strEnrlstatus = "";
                String strDateAdded = "";
                String lastaccess = "";
                String strTotal = "";

                if (!net.fileExists(strFile))
                {
                    try {
                        strFileRaw = net.getURLRaw(strURL);
                        net.saveToFile(strFile, strFileRaw);
                        db.status("success");
                    }
                    catch (Exception e1)
                    {
                        System.out.println("File download error");
                        e1.printStackTrace();
                    }
                }
                db.openConnection("Gradebook");

                try {

                    BufferedReader inputFile = new BufferedReader(new FileReader(strFile));
                    strRecord = inputFile.readLine();
                    while(inputFile.ready() && !strRecord.equals(null))
                    {
                        strArray = strRecord.split(";");

                        strStudentId = strArray[0];
                        strSem = strArray[1];
                        strCrs = strArray[2];
                        fedstatus = strArray[3];
                        strEnrlstatus = strArray[4];
                        strDateAdded = strArray[5];
                        WTE = strArray[36];
                        lastaccess = strArray[37];
                        strTotal = strArray[38];

                        db.addRecord("GRADES", "stuid", strStudentId);
                        db.setField("GRADES", "stuid", strStudentId, "sem", strSem);
                        db.setField("GRADES", "stuid", strStudentId, "crs", strCrs);
                        db.setField("GRADES", "stuid", strStudentId, "fedstatus", fedstatus);
                        db.setField("GRADES", "stuid", strStudentId, "enrlstatus", strEnrlstatus);
                        db.setField("GRADES", "stuid", strStudentId, "adddate", strDateAdded);

                        //Sets up loop that executes SQL statement for Assignment A01-A15
                        for (int i = 1; i <= 15; i++)
                        {
                            if (i <10)
                            {
                                strAstNum = "A0" + i;
                            }

                            else
                            {
                                strAstNum = "A" + i;
                            }

                            db.setField("GRADES", "stuid", strStudentId, strAstNum, strArray[5+i]);
                        }

                        //Sets up a loop that executes SQL statement for Quiz 01-15
                        for (int q = 1; q <= 15; q++)
                        {
                            if (q < 10)
                            {
                                strQuiz = "Q0" + q;
                            }
                            else
                            {
                                strQuiz = "Q" + q;
                            }
                        db.setField("GRADES", "stuid", strStudentId, strQuiz, strArray[20+q]);
                        }

                        db.setField("GRADES", "stuid", strStudentId, "WTE", WTE);
                        db.setField("GRADES", "stuid", strStudentId, "lastaccess", lastaccess);
                        db.setField("GRADES", "stuid", strStudentId, "total", strTotal);


                        strRecord = inputFile.readLine();
                    }
                }
                catch (IOException e1)
                {
                    System.out.println("IOException");
                    e1.printStackTrace();
                }


        }
        });
        populateRosterButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                INET net = new INET();
                DBUpdt db = new DBUpdt();
                String fileURL = "http://faculty.sdmiramar.edu/jcouture/2014sp/cisc190/webct/manual/data-roster.txt";
                String fileDir = "C:\\Users\\Chelsea\\A19014\\Data\\roster.txt";
                String fileRawContents = "";
                String fileLine = "";
                String[] splitArray;
                String stuid = "";
                String firstname = "";
                String lastname = "";
                String email = "";

                if (!net.fileExists(fileDir))
                {
                    try {
                        fileRawContents = net.getURLRaw(fileURL);
                        net.saveToFile(fileDir, fileRawContents);
                    }
                    catch (Exception e1)
                    {
                        System.out.println("File download error");
                        e1.printStackTrace();
                    }
                }

                final String DB_URL = "jdbc:derby:Gradebook";
                try {
                    db.dbConn = DriverManager.getConnection(DB_URL);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                db.openConnection("Gradebook");

                try {
                    BufferedReader inputFile = new BufferedReader(new FileReader(fileDir));
                    fileLine = inputFile.readLine();

                    while (inputFile.ready() && !fileLine.equals(null))
                    {
                        splitArray = fileLine.split(";");
                        stuid = splitArray[0];
                        firstname = splitArray[1];
                        lastname = splitArray[2];
                        email = splitArray[3];
                        db.addRecord("ROSTER", "stuid", stuid);
                        db.setField("ROSTER", "stuid", stuid, "firstname", firstname);
                        db.setField("ROSTER", "stuid", stuid, "lastname", lastname);
                        db.setField("ROSTER", "stuid", stuid, "email", email);
                        fileLine = inputFile.readLine();
                    }
                }
                catch (IOException e1)
                {
                    System.out.println("Java IO Exception");
                    e1.printStackTrace();
                }

            }
        });
        populateComboBoxButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DBUpdt db = new DBUpdt();
                String firstName = "";
                String lastName = "";
                db.openConnection("Gradebook");
                db.query("SELECT * FROM roster ORDER BY lastname, firstname");

                while (db.moreRecords())
                {
                    lastName = db.getField("lastname");
                    firstName = db.getField("firstname");
                    cboStudents.addItem(lastName + ", " + firstName);
                }
          comboBoxReady = true;
               updateLabels();
            }
        });
        createReportButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                try
                {
                    createReport();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }

            }
        }); }
    private void createReport() throws IOException
    {
        CreateReportForm dialog;
        dialog = new CreateReportForm();
        dialog.pack();
        dialog.setVisible(true);
    }



    /**
     * class level bln
     */
    private boolean comboBoxReady = false;

    /**
     * updates labals with various methods to acheive correct data for selected entry
     */
    private void updateLabels()
    {
        if (comboBoxReady)
        {
            DBUpdt db = new DBUpdt();
            String selectedStudent = cboStudents.getSelectedItem().toString();
            String[] splitArray;
            String lastName = "";
            String firstName = "";
            String stuid = "";
            String totalQuizzes = "";
            float quizTotal = 0;
            float assignmentTotal = 0;
            float totalScore = 0;

            splitArray = selectedStudent.split(",");
            lastName = splitArray[0];
            firstName = splitArray[1].trim();
            db.openConnection("Gradebook");
            db.query("SELECT * FROM ROSTER WHERE lastname = '" + lastName + "'");

            //finds first name
            while (db.moreRecords() && !db.getField("firstname").equals(firstName))
            {
                //scan through records
            }
            stuid = db.getField("stuid");
            quizTotal = getQuizTotal(stuid);
            assignmentTotal = getAsgnTotal(stuid);
            totalScore = quizTotal + assignmentTotal;

            lblEmail.setText(db.getField("email"));
           lblStuID.setText(db.getField("stuid"));
            lblQuiz.setText(Float.toString(quizTotal));
            lblAsgn.setText(Float.toString(assignmentTotal));
            lblTotal.setText(Float.toString(totalScore));
            lblLetter.setText(getLetterGrade(totalScore));
        }
    }

    /**
     * get the total for the assighnments
     * @param strStuID string for the students ID
     * @return the total for the assignments
     */
    private float getAsgnTotal(String strStuID)
    {
        DBUpdt db = new DBUpdt();
        String assignmentNum = "";
        float[] assignmentValues = {2,2,2,3,3,3,4,4,4,5,5,6,7,9,11};
        float assignmentScore = 0;
        float assignmentTotal = 0;
        db.openConnection("Gradebook");
        db.query("SELECT * FROM GRADES WHERE stuid = '" + strStuID + "'");

        while (db.moreRecords()) {
            for (int i = 1; i <= 15; i++) {
                if (i < 10) {
                    assignmentNum = "A0" + i;
                } else {
                    assignmentNum = "A" + i;
                }

                assignmentScore = Float.parseFloat(db.getField(assignmentNum));

                assignmentTotal = assignmentTotal + ((assignmentScore/100) * assignmentValues[i-1]);
            }
        }
        return assignmentTotal;
    }
    /**
     * get the total for the quizes
     * @param strStuID string for the students ID
     * @return the total for the quizes
     */
    private float getQuizTotal(String strStuID)
    {
        DBUpdt db = new DBUpdt();
        String quizNum = "";
        float quizTotal = 0;
        float quizScore = 0;
        float quizRet = 0;
        db.openConnection("Gradebook");
        db.query("SELECT * FROM GRADES WHERE stuid = '" + strStuID + "'");

        while (db.moreRecords()) {
            for (int i = 1; i <= 15; i++) {
                if (i < 10) {
                    quizNum = "Q0" + i;
                } else {
                    quizNum = "Q" + i;
                }
                quizScore = Float.parseFloat(db.getField(quizNum));
                quizTotal = quizTotal + ((quizScore / 100) * 2);
            }
        }
        return quizTotal;
    }
    /**
     * gets the letter grade for the given total
     * @param fltTotal represents the total for the class points
     * @return letter grade for the class
     */
    private String getLetterGrade(float fltTotal)
    {
        String letterGrade = "";
        int totalFloor = (int)fltTotal;
        String[] grades = {"F","F","F","F","F","F","D","C","B","A","A"};

        letterGrade = grades[totalFloor/10];
        return letterGrade;
    }

    private void onExit()
{dispose();}

    /**
     * main method sets up form
     * @param args
     */
    public static void main(String[] args)
    {
        A19014 dialog = new A19014();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
