import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
/**
 @author Chelsea Dorich (Email: <a href="mailto:"robotqi@gmail.com>robotqi@gmail.com</a>)
 @version 1.1 05/14/2014
 @assignment.number A190-14
 @prgm.usage Called from the operating system
 @see "Gaddis, 2013, Starting out with Java, From Control Structures, 5th Edition"
 @see "<a href='http://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/index.html'>JavaDoc Documentation</a>

 */
public class CreateReportForm extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox firstnameThenLastnameCheckBox;
    private JCheckBox studentIDCheckBox;
    private JCheckBox quizTotalCheckBox;
    private JCheckBox lastnameThenFirstnameCheckBox;
    private JCheckBox asgnTotalCheckBox;
    private JCheckBox pointsTotalCheckBox;
    private JCheckBox letterGradeWithdrawnCheckBox;
    private JCheckBox fedStatusCheckBox;
    private JButton fileNameButton;
    private JLabel lblFileName;
    private JButton createFileButton;
    private JButton returnToMainFormButton;
    private JRadioButton commaRadioButton;
    private JRadioButton semiColonRadioButton;
    private JRadioButton tabRadioButton;
    private JRadioButton allStudentsRadioButton;
    private JRadioButton onlyCurrentStudentsRadioButton;
    private JRadioButton onlyFEDStudentsRadioButton;
    private JRadioButton studentIDRadioButton;
    private JRadioButton pointsTotalRadioButton;
    private JRadioButton lastnameFirstnameRadioButton;

    /**
     *
     */
    public CreateReportForm()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK()
    {
// add your code here
        dispose();
    }

    private void onCancel()
    {
// add your code here if necessary
        dispose();
    }

    /**
     * some class level blns for use with radio button selection identification
     */
    private boolean radioDelimiter = false;
    private boolean radioSortBy = false;
    private boolean radioButtonSelectStudents = false;

    /**
     * accepts total, parses to int and uses that numbet to find coresponding letter grade in ary
     * @param fltTotal total of ast and quiz points
     * @return strGrade returns grade letter
     */
    private String getLetterGrade(float fltTotal)
    {
        String strGrade = "";
        int intTotal = (int)fltTotal;
        String[] grades = {"F","F","F","F","F","F","D","C","B","A","A"};

       strGrade = grades[intTotal/10];
        return strGrade;
    }

    /**
     * usues an ary to get quiz total of points acheived, adds up all numbers and acheives a percentage by dividing by 100
     * @param strStuID looks up stats relating to ID to give student specific results
     * @return
     */
    private float getQuizTotal(String strStuID) {
        DBUpdt db = new DBUpdt();
        String strQuizNum = "";
        float fltTotal = 0;
        float quizScore = 0;
        db.openConnection("Gradebook");
        db.query("SELECT * FROM GRADES WHERE stuid = '" + strStuID + "'");

        while (db.moreRecords()) {
            for (int i = 1; i <= 15; i++) {
                if (i < 10) {
                    strQuizNum = "Q0" + i;
                } else {
                    strQuizNum = "Q" + i;
                }
                quizScore = Float.parseFloat(db.getField(strQuizNum));
                fltTotal = fltTotal + ((quizScore / 100) * 2);
            }
        }
        return fltTotal;
    }

    /**
     * gets file name to be saved from file name form
     */
    private void onFileName()
    {
        String fileName = "";
        FileName dialog = new FileName();
        dialog.pack();
        dialog.setVisible(true);
        fileName = dialog.strFileName;
        lblFileName.setText("data/" + fileName + ".txt");
        createFileButton.setEnabled(true);


    }
    /**
    gets the selected delim. radio and returns a charachter that correctly represents that selection
     */
    private String getDelim()
    {
        String delim = "";
        if (commaRadioButton.isSelected())
        {
            delim = ",";
            radioDelimiter = true;
        }

        else if (semiColonRadioButton.isSelected())
        {
            delim = ";";
            radioDelimiter = true;
        }

        else if (tabRadioButton.isSelected())
        {
            delim = "\t";
            radioDelimiter = true;
        }
        return delim;
    }

    /**
     * gets selected radio from 'select' group
     * @return
     */
    private String getSelect()
    {
        String selected = "";
        if (allStudentsRadioButton.isSelected())
        {
            selected = "";
            radioButtonSelectStudents = true;
        }

        else if (onlyCurrentStudentsRadioButton.isSelected())
        {
            selected = " WHERE enrlstatus = ''";
            radioButtonSelectStudents = true;
        }

        else if (onlyFEDStudentsRadioButton.isSelected())
        {
            selected = " WHERE fedstatus = 'FED'";
            radioButtonSelectStudents = true;
        }
        return selected;
    }

    /**
     * gets selected radio from the sort by group
     * @return correct sql query for that button
     */
    private String getSortBy()
    {
        String sortBy = "";
        if (studentIDRadioButton.isSelected())
        {
            sortBy = "GRADES.stuid";
            radioSortBy = true;
        }

        else if (lastnameFirstnameRadioButton.isSelected())
        {
            sortBy = "ROSTER.lastname,ROSTER.firstname";
            radioSortBy = true;
        }

        else if (pointsTotalRadioButton.isSelected())
        {
            sortBy = "GRADES.total";
            radioSortBy = true;
        }
        return sortBy;
    }
    /**
    gets delimeter, calculates score, and builds string to save to file from selected specifics.
     */
    private String saveData(String stuid)
    {
        String delim = getDelim();
        StringBuilder sb = new StringBuilder();
        float quizTotal = getQuizTotal(stuid);
        float assignmentTotal = getAsgnTotal(stuid);
        float totalPoints = assignmentTotal + quizTotal;
        String letterGrade = getLetterGrade(totalPoints);
        String stringRet = "";
        String withdrawn = "";

        DecimalFormat myFormat = new DecimalFormat("###.##");

        //Instantiate Database class and open connection to database
        DBUpdt db = new DBUpdt();
        db.openConnection("Gradebook");

        db.query("SELECT ROSTER.*, GRADES.* FROM ROSTER INNER JOIN GRADES ON ROSTER.stuid=GRADES.stuid WHERE " +
                "ROSTER.stuid = '" + stuid + "'");

        //Checks if combo box is selected and appends StringBuilder with appropriate string if true
        while(db.moreRecords()) {
            if (studentIDCheckBox.isSelected()) {
                sb.append(db.getField("stuid") + delim);
            }

            if (lastnameThenFirstnameCheckBox.isSelected()) {
                sb.append(db.getField("lastname") + delim + db.getField("firstname") + delim);
            }

            if (firstnameThenLastnameCheckBox.isSelected()) {
                sb.append(db.getField("firstname") +  delim +  db.getField("lastname") + delim);
            }

            if (quizTotalCheckBox.isSelected()) {
                sb.append(myFormat.format(quizTotal) + delim);
            }

            if (asgnTotalCheckBox.isSelected()) {
                sb.append(myFormat.format(assignmentTotal) + delim);
            }

            if (pointsTotalCheckBox.isSelected()) {
                sb.append(myFormat.format(totalPoints) + delim);
            }
            if (fedStatusCheckBox.isSelected()) {
                sb.append(db.getField("fedstatus") + delim);
            }
            if (letterGradeWithdrawnCheckBox.isSelected()) {
                withdrawn = db.getField("enrlstatus");

                if(!withdrawn.equals("Withdrawn"))
                {
                    sb.append(letterGrade + delim);
                }
                else
                {
                    sb.append(withdrawn + delim);
                }
            }
            stringRet = sb.toString();
            stringRet = stringRet.substring(0,stringRet.length() - 1);
            return stringRet;
        }
    return stringRet;
}

    /**
     * builds query to look for specific student in database and uses bln obstacle course to filter results
     * @throws IOException
     */
    private void createForm() throws IOException
    {

        String strSelect = getSelect();
        String strSortBy = getSortBy();
        String strPrintString = "";
        String strSaveDir = lblFileName.getText();
        getDelim(); //Exists to set boolean check for radio button selection

        if (radioDelimiter && radioSortBy && radioButtonSelectStudents)
        {

            DBUpdt db = new DBUpdt();
            PrintWriter outputFile = new PrintWriter(strSaveDir);

            //DB fields
            String strStuid = "";
            db.openConnection("Gradebook");
            //db.query("SELECT * FROM ROSTER ORDER BY lastname,firstname");
            System.out.println("SELECT ROSTER.*, GRADES.* FROM ROSTER INNER JOIN GRADES ON ROSTER.stuid=GRADES.stuid" + strSelect +
                    " ORDER BY " + strSortBy);
            db.query("SELECT ROSTER.*, GRADES.* FROM ROSTER INNER JOIN GRADES ON ROSTER.stuid=GRADES.stuid" + strSelect +
                    " ORDER BY " + strSortBy);

            while (db.moreRecords())
            {
                strStuid = db.getField("stuid");
                strPrintString = saveData(strStuid);

                System.out.println(strPrintString);

                outputFile.println(strPrintString);

            }
            outputFile.close();

            System.out.println("File successfully saved");
        }

        else
        {
            System.out.println("Error: Please select an option from all three groups on the right");
        }
    }


    /**
     * returns to main form
     */
    private void returnMainForm()
    {
        dispose();
    }

    /**
     * usues an ary to get asgnmt total of points acheived, adds up all numbers and acheives a percentage by dividing by 100
     * @param strStuID looks up stats relating to ID to give student specific results
     * @return
     */
    private float getAsgnTotal(String strStuID)
    {
        DBUpdt db = new DBUpdt();
        String strAstNum = "";
        float fltAstScore = 0;
        float fltAstTotal = 0;
        float[] fltAstValues = {2,2,2,3,3,3,4,4,4,5,5,6,7,9,11};

        db.openConnection("Gradebook");
        db.query("SELECT * FROM GRADES WHERE stuid = '" + strStuID + "'");

        while (db.moreRecords()) {
            for (int e = 1; e <= 15; e++) {
                if (e < 10) {
                    strAstNum = "A0" + e;
                } else {
                    strAstNum = "A" + e;
                }

                fltAstScore = Float.parseFloat(db.getField(strAstNum));

                fltAstTotal = fltAstTotal + ((fltAstScore/100) * fltAstValues[e-1]);
            }
        }
        return fltAstTotal;
    }
    /**
     * main form gets things going
     * @param args
     */
    public static void main(String[] args)
    {
        CreateReportForm dialog = new CreateReportForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
