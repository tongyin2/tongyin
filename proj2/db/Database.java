package db;
import edu.princeton.cs.introcs.In;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Database {
    private static ArrayList<Table> tbles;

    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }

        eval(args[0]);
    }

    protected static void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
        }
    }

    private static void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    private static void createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }

        String colSentence = joiner.toString() + " and " + cols[cols.length-1];
        //System.out.printf("You are trying to create a table named %s with the columns %s\n", name, colSentence);

        if (findTable(name)==null) {
            Table t = new Table(name);
            for (int i = 0; i < cols.length; i++) {
                String[] theCol = cols[i].split("\\s+");
                t.addNewColumn(theCol[0], theCol[1]);
            }
            tbles.add(t);
        }else {
            System.out.printf("table %s already exist. Please try another table name\n",name);
        }
    }

    private static void createSelectedTable(String name, String exprs, String tables, String conds) {
        /*System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);*/

        //
        if (findTable(name)==null) {
            Table t = select(exprs,tables,conds);
            if (t!=null) {
                t.setTableName(name);
                tbles.add(t);
            }
        }else {
            System.err.printf("table %s already exist. Please try another table name\n",name);
        }
    }

    private static void loadTable(String name) {
        //System.out.printf("You are trying to load the table named %s\n", name);
        try {
            Table t = findTable(name);
            if (t==null) {
                t = loadtable(name);
                tbles.add(t);
            }else {
                for (int i=0; i<tbles.size(); i++) {
                    if (tbles.get(i).getTableName().equals(name)) {
                        tbles.set(i,loadtable(name));
                    }
                }
            }

        }catch (Exception e) {
            System.err.printf("File %s is not found, and therefore cannot be loaded\n",name);
        }
    }

    private static void storeTable(String name) {
        //System.out.printf("You are trying to store the table named %s\n", name);
        Table t = findTable(name);
        if (t==null) {
            System.err.printf("Table %s is not in database and therefore cannot be stored\n",name);
        }else {
            File newfile = new File("examples/"+name+".tbl");
            if (newfile.exists()) {
                //overwrite file
                newfile.delete();
                newfile = new File("examples/"+name+".tbl");
            }

            try {
                FileWriter out = new FileWriter(newfile,false);
                out.write(t.toString());
                out.close();
            }catch (IOException e) {
                e.printStackTrace();;
            }
        }

    }

    private static void dropTable(String name) {
        //System.out.printf("You are trying to drop the table named %s\n", name);
        Table t = findTable(name);
        if (t!=null) {
            tbles.remove(t);
        }else {
            System.err.printf("Table %s doesn't exist in database, cannot perform dropTable\n",name);
        }
    }

    private static void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return;
        }

        //System.out.printf("You are trying to insert the row \"%s\" into the table %s\n", m.group(2), m.group(1));

        //
        insert(m.group(1),m.group(2));
    }

    private static void printTable(String name) {
        //System.out.printf("You are trying to print the table named %s\n", name);
        Table t = findTable(name);
        if (t!=null) {
            System.out.println(t);
        }else {
            System.err.printf("table %s doesn't exist\n",name);
        }
    }

    private static void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return;
        }

        select(m.group(1), m.group(2), m.group(3));
    }

    private static Table select(String exprs, String tables, String conds) {
        /*System.out.printf("You are trying to select these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", exprs, tables, conds);*/

        String[] tbls = (tables.trim()).split("\\s*,\\s*");//get table names

        //check if database contains the tables specified in tbls array
        for (String tn:tbls) {
            if (findTable(tn) == null) {
                System.err.printf("table %s is not in database\n",tn);
                return null;
            }
        }

        //do join method
        Table t1;
        Table t2;
        if (tbls.length==1) {
            t1 = findTable(tbls[0]);
        }else {
            t1 = findTable(tbls[0]);
            t2 = findTable(tbls[1]);
            for (int i=0; i<tbls.length-1; i++) {
                t1 = Join(t1,t2);
                if ((i+2)<tbls.length) {
                    t2 = findTable(tbls[i+2]);
                }
            }
        }

        //do select columns from the joined table
        Table t3 = selectCo(t1,exprs);
        if (t3 == null) {
            return null;
        }else {
            if (conds != null) {//filter the table by its condition
                t3 = filter(t3,conds);
            }
        }

        if (t3!=null) {
            System.out.println(t3);
        }
        return t3;
    }

    public Database() {
        // YOUR CODE HERE
        tbles = new ArrayList<>();
    }

    //check if the database contains certain table
    public static Table findTable(String name) {

        for (Table t:tbles) {
            if (t.getTableName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public String transact(String query) {
        eval(query);
        return "";
    }

    /*join two tables*/
    public static Table Join(Table t1, Table t2) {
        Table t1_common = new Table(); //will hold t1's common columns
        Table t2_common = new Table(); //will hold t2's common columns
        Table t3 = new Table(); // create the table to be returned
        /*hold common row indexes of the two tables created above */
        ArrayList<Integer> Com_Row_Indexs = new ArrayList<>();
        /*hold t1 t2's common column indexes seperatly*/
        ArrayList<Integer> t1_Com_Col_Indexs = new ArrayList<>();
        ArrayList<Integer> t2_Com_Col_Indexs = new ArrayList<>();

        /*extract common columns from t1 t2, and mark their column index*/
        for (int i=0; i < t1.sizeOfCols(); i++) {
            for (int j=0; j < t2.sizeOfCols(); j++) {
                if (t1.getColumns().get(i).getName().equals(
                        t2.getColumns().get(j).getName())) {
                    t1_common.getColumns().add(t1.getColumns().get(i));
                    t2_common.getColumns().add(t2.getColumns().get(j));
                    t1_Com_Col_Indexs.add(i);
                    t2_Com_Col_Indexs.add(j);
                }
            }
        }

        /*if no common columns found, put every row index into Com_Row_Indexes*/
        if (t1_common.sizeOfCols()==0) {
            for (int i=0; i < t1.sizeOfRows(); i++) {
                for (int j=0; j < t2.sizeOfRows(); j++) {
                    Com_Row_Indexs.add(i);
                    Com_Row_Indexs.add(j);
                }
            }
        }

        /*get row indexes of the common columns of t1 t2 if their rows are equal*/
        for (int i=0; i < t1_common.sizeOfRows(); i++) {
            for (int j=0; j < t2_common.sizeOfRows(); j++) {
                int x = 0;
                while (x < t1_common.sizeOfCols()) {
                    if (t1_common.getColumns().get(x).getRows().get(i).equals(
                            t2_common.getColumns().get(x).getRows().get(j))) {
                        x++;
                        if (x == t1_common.sizeOfCols()) {
                            Com_Row_Indexs.add(i);
                            Com_Row_Indexs.add(j);
                        }
                    }else {
                        x = t1_common.sizeOfCols();
                    }
                }
            }
        }


        /*add the common columns and fill each row of them*/
        for (int i=0; i < t1_common.sizeOfCols(); i++) {
            t3.getColumns().add(new Column(t1_common.getColumns().get(i).getName(),
                    t1_common.getColumns().get(i).getType()));
            for (int j=0; j < Com_Row_Indexs.size(); j=j+2) {
                t3.getColumns().get(i).getRows().add(t1_common.getColumns().get(i).getRows().get(
                        Com_Row_Indexs.get(j)));
            }
        }

        /*add the un-common columns from t1 and fill their rows*/
        for (int i=0; i < t1.sizeOfCols(); i++) {
            if (t1_Com_Col_Indexs.contains(i)==false) {
                t3.getColumns().add(new Column(t1.getColumns().get(i).getName(),
                        t1.getColumns().get(i).getType()));

                for (int j=0; j < Com_Row_Indexs.size(); j=j+2) {
                    t3.getColumns().get(t3.sizeOfCols()-1).getRows().add(
                            t1.getColumns().get(i).getRows().get(
                                    Com_Row_Indexs.get(j))
                    );
                }
            }
        }

        /*add un-common columns from t2 and fill their rows*/
        for (int i=0; i < t2.sizeOfCols(); i++) {
            if (t2_Com_Col_Indexs.contains(i)==false) {
                t3.getColumns().add(new Column(t2.getColumns().get(i).getName(),
                        t2.getColumns().get(i).getType()));

                for (int j=1; j < Com_Row_Indexs.size(); j=j+2) {
                    t3.getColumns().get(t3.sizeOfCols()-1).getRows().add(
                            t2.getColumns().get(i).getRows().get(
                                    Com_Row_Indexs.get(j))
                    );
                }
            }
        }

        return t3;
    }

    /*load a table (throw exception if filename is not found)*/
    public static Table loadtable(String filename) {
        In in = new In("examples/"+filename+".tbl");
        int c = -1;

        Table t = new Table(filename);

        while (in.hasNextLine()) {
            String row = in.readLine();
            String[] cells = row.split(",");

            if (c < 0) {
                for (int i=0; i < cells.length; i++) {
                    String[] NaT = cells[i].split("\\s+");
                    t.addNewColumn(NaT[0],NaT[1]);
                }
            }else {
                for (int i=0; i < cells.length; i++) {
                    t.addValue(cells[i],i);
                }
            }
            c++;
        }
        in.close();
        return t;
    }

    /*select columns from tables after joined*/
    public static Table selectCo(Table t1, String colExpr) {
        String[] exprs = colExpr.split("\\s*,\\s*");
        Table t2 = new Table();

        final Pattern expr_cal  = Pattern.compile("(\\S+)\\s*([-+*/])\\s*(\\S+)\\s+as\\s+(.+)\\s*");


        if (exprs.length == 1 && exprs[0].equals("*")) {
            t2 = t1; // if expression is
        }else {
             for (String ex : exprs) {
                Matcher m = expr_cal.matcher(ex);
                if (m.matches()) {
                    //if one of the column expressions matches " ... as ...", do calculations
                    Column col = calculate(m.group(1),m.group(3),
                            m.group(2),m.group(4),t1);
                    if (col == null){
                        return null; //calculation failed
                    }else {
                        t2.addColumn(col);
                    }
                } else {
                    if (t1.findColumn(ex) != null) {
                        Column col = t1.findColumn(ex);
                        Column col2 = new Column(col.getName(), col.getType());
                        for (int i = 0; i < col.getRows().size(); i++) {
                            col2.addValue(col.getRows().get(i));
                        }
                        t2.addColumn(col2);
                    } else {
                        System.err.printf("column %s doesn't exist\n",ex);
                        return null; // colum expr dont exist warning
                    }
                }
            }
        }

        return t2;
    }

    //do arithmetic operation given a string expression
    private static Column calculate(String ope1, String ope2, String ao,
                                   String colname, Table t1) {
        Column col = new Column(colname, "string");

        if (t1.findColumn(ope1) != null) {  //if column ope1 is found in the table
            String op1type = t1.findColumn(ope1).getType();
            String op2type;
            boolean isliteral;

            Pattern inte = Pattern.compile("-?\\d+"); // integer
            Pattern floa = Pattern.compile("-?((\\d*.\\d+)|(\\d+.\\d*))"); // float
            Pattern liter = Pattern.compile("'(.+)'");
            Matcher m;

            /*get ope2's type based on the input*/
            if ((m = liter.matcher(ope2)).matches()) {
                //if ope2 is a string literal
                op2type = "string";
                isliteral = true;
            }else if (t1.findColumn(ope2) != null) {
                //ope2 is found
                op2type = t1.findColumn(ope2).getType();
                isliteral = false;
            }else if ((m =floa.matcher(ope2)).matches()) {
                //ope2 is float literal
                op2type = "float";
                isliteral = true;
            }else if ((m =inte.matcher(ope2)).matches()) {
                //ope2 is int literal
                op2type = "int";
                isliteral = true;
            }else {
                //ope2 is not specified correctly
                System.err.printf("%s is not specified correctly\n",ope2);
                return null;
            }

            for (int i = 0; i < t1.findColumn(ope1).sizeOfRows(); i++) {

                boolean isNaN1 = false;
                boolean isNaN2 = false;

                if (op1type.equals("string") ^ op2type.equals("string")) {
                    // string operate on int/float (invalid)
                    System.err.printf("Cannot perform %s %s %s because %s has type %s, " +
                            "%s has type %s\n",ope1,ao,ope2,ope1,op1type,ope2,op2type);
                    return null;
                }else if (op1type.equals("string") && op2type.equals("string")) {
                    //string operate on string
                    col.setType("string");
                    String value;

                    if (ao.equals("+")) {
                        /*get ope2's value*/
                        if (isliteral) {
                            value = ope2.replace("'","");
                        }else {
                            value = (String) t1.findColumn(ope2).getRows().get(i);
                        }
                        if (value.equals("NOVALUE")) {
                            value = "";
                        }

                        /*get ope1's value*/
                        String x1 = (String) t1.findColumn(ope1).getRows().get(i);
                        if (x1.equals("NOVALUE")) {
                            x1 = "";
                        }

                        if (value.equals("NaN") || x1.equals("NaN")) {
                            col.addValue("NaN");
                        }else {
                            col.addValue(x1.concat(value));
                        }

                    }else {
                        //strings are doing operations are not +
                        System.err.printf("Cannot perform %s %s %s because %s has type %s, " +
                                "%s has type %s\n",ope1,ao,ope2,ope1,op1type,ope2,op2type);
                        return null;
                    }
                }else if (op1type.equals("int") && op2type.equals("int")) {
                    // int operate on int
                    col.setType("int");
                    int num;

                    /*get ope2's value*/
                    if (isliteral) {
                        num = Integer.parseInt(ope2);
                    }else {
                        if ("NOVALUE".getClass().isInstance(t1.findColumn(ope2).getRows().get(i))) {
                            if (((String) t1.findColumn(ope2).getRows().get(i)).equals("NaN")) {
                                isNaN2 = true;
                            }
                            num = 0;
                        }else {
                            num = (Integer) t1.findColumn(ope2).getRows().get(i);
                        }
                    }

                    /*get ope1's value*/
                    int x1;
                    if ("NOVALUE".getClass().isInstance(t1.findColumn(ope1).getRows().get(i))) {
                        if (((String) t1.findColumn(ope1).getRows().get(i)).equals("NaN")) {
                            isNaN1=true;
                        }
                        x1 = 0;
                    }else {
                        x1 = (Integer) t1.findColumn(ope1).getRows().get(i);
                    }

                    if (isNaN1 || isNaN2) {
                        col.addValue("NaN");
                    }else {
                        switch (ao) {
                            case "+":
                                col.addValue(x1+num);
                                break;
                            case "-":
                                col.addValue(x1-num);
                                break;
                            case "*":
                                col.addValue(x1*num);
                                break;
                            default:
                                if (num==0) {
                                    col.addValue("NaN");
                                }else {
                                    col.addValue(x1/num);
                                }
                                break;
                        }

                    }

                }else {
                    //at least one of ope1 and ope2 is float
                    col.setType("float");
                    float num;

                    /*get ope2's value*/
                    if (isliteral) {
                        num = Float.parseFloat(ope2);
                    }else {
                        if ("NOVALUE".getClass().isInstance(t1.findColumn(ope2).getRows().get(i))) {
                            if (((String) t1.findColumn(ope2).getRows().get(i)).equals("NaN")) {
                                isNaN2 = true;
                            }
                            num = 0.0f;
                        }else {
                            if (t1.findColumn(ope2).getType().equals("int")) {
                                num = (float)((Integer) t1.findColumn(ope2).getRows().get(i));
                            }else {
                                num = (Float) t1.findColumn(ope2).getRows().get(i);
                            }
                        }
                    }

                    /*get ope1's value*/
                    float x1;
                    if ("NOVALUE".getClass().isInstance(t1.findColumn(ope1).getRows().get(i))) {
                        if (((String) t1.findColumn(ope1).getRows().get(i)).equals("NaN")) {
                            isNaN1 = true;
                        }
                        x1 = 0.0f;
                    }else {
                        if (t1.findColumn(ope1).getType().equals("int")) {
                            x1 = (float)((Integer) t1.findColumn(ope1).getRows().get(i));
                        }else {
                            x1 = (Float) t1.findColumn(ope1).getRows().get(i);
                        }
                    }

                    if (isNaN1 || isNaN2) {
                        col.addValue("NaN");
                    }else {
                        switch (ao) {
                            case "+":
                                col.addValue(x1+num);
                                break;
                            case "-":
                                col.addValue(x1-num);
                                break;
                            case "*":
                                col.addValue(x1*num);
                                break;
                            default:
                                if (num==0) {
                                    col.addValue("NaN");
                                }else {
                                    col.addValue(x1/num);
                                }
                                break;
                        }

                    }
                }
            }
            return col;

        }
        //if ope1 is not found in the table
        System.err.printf("%s is not found in the table\n",ope1);
        return null;
    }

    /*filter a given table by the provided condition*/
    public static Table filter(Table t1, String condition) {
        String[] condis = condition.split("\\s*and\\s*");
        Table t2 = new Table();

        Pattern condiPat = Pattern.compile("(.+?)\\s*([<>]=?|(?:={2}))\\s*(.+?)\\s*"); //single condition pattern

        /*RowIndex hold the row index to be included in the final table*/
        ArrayList<Integer> RowIndex = new ArrayList<>();
        for (int i=0; i<t1.sizeOfRows(); i++) {
            RowIndex.add(i);
        }

        for (String ex : condis) {
            Matcher m = condiPat.matcher(ex);
            if (m.matches()) {
                //condition pattern matches
                RowIndex = DoCompare(m.group(1),m.group(3),m.group(2),t1,RowIndex);
                if (RowIndex==null) {
                    //some column name are not found or comparison cannot proceed due to incomparable types
                    return null;
                }

            }else {
                //condition pattern don't match
                System.err.printf("Condition is not specified correctly\n");
                return null;
            }
        }

        //copy rows specified by the RowIndex array into t2
        for (int i=0; i<t1.sizeOfCols(); i++) {
            t2.addNewColumn(t1.getColumns().get(i).getName(),t1.getColumns().get(i).getType());
            for (int j:RowIndex) {
                t2.getColumns().get(i).addValue(t1.getColumns().get(i).getRows().get(j));
            }
        }

        return t2;
    }

    private static ArrayList<Integer> DoCompare(String ope1, String ope2, String co, Table t1, ArrayList<Integer> rowindex) {

        if (t1.findColumn(ope1)!=null) {
            String op1type = t1.findColumn(ope1).getType();
            String op2type;
            boolean isliteral;

            Pattern inte = Pattern.compile("-?\\d+"); // integer
            Pattern floa = Pattern.compile("-?((\\d*.\\d+)|(\\d+.\\d*))"); // float
            Pattern liter = Pattern.compile("'(.+)'");
            Matcher m;

            /*get ope2's type based on the input*/
            if ((m = liter.matcher(ope2)).matches()) {
                //if ope2 is a string literal
                op2type = "string";
                isliteral = true;
            }else if (t1.findColumn(ope2) != null) {
                //ope2 is found
                op2type = t1.findColumn(ope2).getType();
                isliteral = false;
            }else if ((m =inte.matcher(ope2)).matches()) {
                //ope2 is float literal
                op2type = "int";
                isliteral = true;
            }else if ((m =floa.matcher(ope2)).matches()) {
                //ope2 is int literal
                op2type = "float";
                isliteral = true;
            }else {
                //ope2 is not specified correctly
                System.err.printf("In condition, %s is not specified correctly\n",ope2);
                return null;
            }

            for (Iterator<Integer> iterator = rowindex.iterator(); iterator.hasNext();) {
                Integer i = iterator.next();
                int result;
                boolean isNaN1 = false;
                boolean isNaN2 = false;
                boolean isNOVA = false;

                if (op1type.equals("string") ^ op2type.equals("string")) {
                    //cannot do comparison between string and int/float
                    System.err.printf("Cannot perform %s %s %s because %s has type %s, " +
                            "%s has type %s\n",ope1,co,ope2,ope1,op1type,ope2,op2type);
                    return null;
                } else {
                    //can do the comparison
                    /*check if ope1 is NaN or NOVALUE*/
                    if ("NaN".getClass().isInstance(t1.findColumn(ope1).getRows().get(i))) {
                        if (((String) t1.findColumn(ope1).getRows().get(i)).equals("NaN")) {
                            isNaN1 = true;
                        }else if (((String) t1.findColumn(ope1).getRows().get(i)).equals("NOVALUE")) {
                            isNOVA = true;
                            iterator.remove();
                            continue;
                        }
                    }
                    /*check if ope2 is NaN or NOVALUE (ope2 has to be column exp)*/
                    if (isliteral==false) {
                        if ("NaN".getClass().isInstance(t1.findColumn(ope2).getRows().get(i))) {
                            if (((String) t1.findColumn(ope2).getRows().get(i)).equals("NaN")) {
                                isNaN2 = true;
                            } else if (((String) t1.findColumn(ope2).getRows().get(i)).equals("NOVALUE")) {
                                isNOVA = true;
                                iterator.remove();
                                continue;
                            }
                        }
                    }



                    if (op1type.equals("string") && op2type.equals("string")) {
                        //compare string with string
                        if (isNaN1 == false && isNaN2 == false) {
                            if (isliteral == false) {
                                result = ((String) t1.findColumn(ope1).getRows().get(i)).compareTo(
                                        (String) t1.findColumn(ope2).getRows().get(i));
                            }else {
                                result = ((String) t1.findColumn(ope1).getRows().get(i)).compareTo(
                                        ope2.replace("'",""));
                            }
                        }else if (isNaN1 == true && isNaN2 == true) {
                            result = 0;
                        }else if (isNaN1 == true && isNaN2 == false) {
                            result = 1;
                        }else {
                            result = -1;
                        }

                    }else {
                        //compare int/float to int/float
                        float x1;
                        if (op1type.equals("int")) {
                            x1 = (Integer) t1.findColumn(ope1).getRows().get(i);
                        }else {
                            x1 = (Float) t1.findColumn(ope1).getRows().get(i);
                        }

                        float x2;
                        if (op2type.equals("int")) {
                            if (isliteral) {
                                x2 = Integer.parseInt(ope2);
                            }else {
                                x2 = (Integer) t1.findColumn(ope2).getRows().get(i);
                            }
                        }else {
                            if (isliteral) {
                                x2 = Float.parseFloat(ope2);
                            }else {
                                x2 = (Float) t1.findColumn(ope2).getRows().get(i);
                            }
                        }

                        if (x1 == x2) {
                            result = 0;
                        }else if (x1 > x2) {
                            result = 1;
                        }else {
                            result = -1;
                        }
                    }
                }
                if (co.equals("==")) {
                    if (result != 0) {
                        iterator.remove();
                    }
                }else if (co.equals("!=")) {
                    if (result == 0) {
                        iterator.remove();
                    }
                }else if (co.equals("<")) {
                    if (result >=0) {
                        iterator.remove();
                    }
                }else if (co.equals(">")) {
                    if (result <=0) {
                        iterator.remove();
                    }
                }else if (co.equals("<=")) {
                    if (result >0) {
                        iterator.remove();
                    }
                }else {
                    if (result <0) {
                        iterator.remove();
                    }
                }
            }
            return rowindex;
        }
        //ope1 is not found in table
        System.err.printf("In condition, %s is not found in the table\n",ope1);
        return null;
    }

    public static boolean insert(String name, String exprs) {
        Table t = findTable(name);
        Pattern inte = Pattern.compile("-?\\d+"); // integer
        Pattern floa = Pattern.compile("-?((\\d*\\.\\d+)|(\\d+\\.\\d*))"); // float
        Pattern liter = Pattern.compile("'(.+)'");
        Matcher m;

        if (t==null) {
            //check if table exist or not
            System.err.printf("Table %s is not in the database\n",name);
            return false;
        }else {
            String[] values = exprs.trim().split(COMMA);
            String itype;

            if (values.length!=t.sizeOfCols()) {
                //check if the values
                System.err.printf("Table %s has column size %d. The values you gave have " +
                        "size %d\n",name,t.sizeOfCols(),values.length);
                return false;
            }else {
                //check columns match or not
                for (int i=0; i<values.length; i++) {
                    if ((m = inte.matcher(values[i])).matches()) {
                        itype = "int";
                    }else if ((m = liter.matcher(values[i])).matches()) {
                        itype = "string";
                    }else if ((m = floa.matcher(values[i])).matches()) {
                        itype = "float";
                    }else {
                        System.err.printf("The value %s doesn't belong to any of the types: " +
                                "int, float, string\n",values[i]);
                        return false;
                    }

                    if ((t.getColumns().get(i).getType().equals(itype))!=true) {
                        System.err.printf("Value %s doesn't match the column type\n",values[i]);
                        return false;
                    }
                }

                //insert values into the table
                for (int i=0; i<values.length; i++) {
                    if ((t.getColumns().get(i).getType()).equals("string")) {
                        t.getColumns().get(i).addValue(values[i].replace("'",""));
                    }else if ((t.getColumns().get(i).getType()).equals("int")) {
                        int x = Integer.parseInt(values[i]);
                        t.getColumns().get(i).addValue(x);
                    }else {
                        float x = Float.parseFloat(values[i]);
                        t.getColumns().get(i).addValue(x);
                    }
                }
                return true;
            }
        }
    }

    /*
    public static void main(String[] args) {

        Database db = new Database();
        loadTable("t1");
        loadTable("t2");
        insert("t1","2,5");

        select("*","t1,t2",null);

    }*/

}
