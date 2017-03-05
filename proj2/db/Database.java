package db;
import edu.princeton.cs.introcs.In;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Database {
    private ArrayList<Table> tables;

    public Database() {
        // YOUR CODE HERE
        tables = new ArrayList<>();
    }

    //CREATE table
    public Table CreateTable(String name) {
        tables.add(new Table(name));
        return tables.get(0);
    }

    public String transact(String query) {
        Parse.eval(query);
        return "YOUR CODE HERE";
    }

    //join two tables
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

    //load a table (throw exception if filename is not found)
    public static Table loadTable(String filename) {
        In in = new In(filename+".tbl");
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

    //select columns from tables after joined
    public static Table selectCo(Table t1, String colExpr) {
        String[] exprs = colExpr.split("\\s*,\\s*");
        Table t2 = new Table();

        final Pattern expr_cal  = Pattern.compile("(\\S+)\\s*([-+*/])\\s*(\\S+)\\s+as\\s+(.+)\\s*");


        if (exprs.length == 1 && exprs[0].equals('*')) {
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
                        return null; // colum expr dont exist warning
                    }
                }
            }
        }

        return t2;
    }

    //do arithmetic operation given a string expression
    public static Column calculate(String ope1, String ope2, String ao,
                                   String colname, Table t1) {
        Column col = new Column(colname, "int");

        if (t1.findColumn(ope1) != null) {  //if column ope1 is found in the table
            String op1type = t1.findColumn(ope1).getType();
            String op2type;

            Pattern inte = Pattern.compile("\\d+"); // integer
            Pattern floa = Pattern.compile("\\d+\\.\\d+"); // float
            Matcher m;

            if (t1.findColumn(ope2) != null) {
                //ope2 is found
                op2type = t1.findColumn(ope2).getType();
            }else if ((m =floa.matcher(ope2)).matches()) {
                //ope2 is float
                op2type = "float";
            }else if ((m =inte.matcher(ope2)).matches()) {
                op2type = "int";
            }else {
                op2type = "string";
            }

            if (op1type.equals("string") ^ op2type.equals("string")) {
                // string operate on int/float (invalid)
                return null;
            }else if (op1type.equals("string") && op2type.equals("string")) {
                //string operate on string
                col.setType("string");
                String value;

                for (int i = 0; i < t1.findColumn(ope1).sizeOfRows(); i++) {
                    if (t1.findColumn(ope2) != null) {
                        //ope2 is string found in table
                        value = (String) t1.findColumn(ope2).getRows().get(i);
                    } else {
                        //ope2 is a literal
                        value = ope2;
                    }
                    switch (ao) {
                        case "+":
                            value = ((String) t1.findColumn(ope1).getRows().get(i)).
                                    concat(value);
                            break;
                        default:
                            //strings are doing operations are not +
                            return null;
                    }
                    col.addValue(value);
                }
            }else if (op1type.equals("int") && op2type.equals("int")){
                // int operate on int
                col.setType("int");
                int num;

                for (int i = 0; i < t1.findColumn(ope1).sizeOfRows(); i++) {
                    if (t1.findColumn(ope2) != null) {
                        //ope2 is string found in table
                        num = (Integer) t1.findColumn(ope2).getRows().get(i);
                    } else {
                        //ope2 is a literal
                        num = Integer.parseInt(ope2);
                    }
                    switch (ao) {
                        case "+":
                            num = ((Integer) t1.findColumn(ope1).getRows().get(i))+num;
                            break;
                        case "-":
                            num = ((Integer) t1.findColumn(ope1).getRows().get(i))-num;
                            break;
                        case "*":
                            num = ((Integer) t1.findColumn(ope1).getRows().get(i))*num;
                            break;
                        default:
                            num = ((Integer) t1.findColumn(ope1).getRows().get(i))/num;
                            break;
                    }
                    col.addValue(num);
                }
            }else {
                //at least one of ope1 and ope2 is float
                col.setType("float");
                float num;

                for (int i = 0; i < t1.findColumn(ope1).sizeOfRows(); i++) {
                    if (t1.findColumn(ope2) != null) {
                        //ope2 is string found in table
                        num = (Float) t1.findColumn(ope2).getRows().get(i);
                    } else {
                        //ope2 is a literal
                        num = Float.parseFloat(ope2);
                    }

                    if (t1.findColumn(ope1).getType().equals("int")) {
                        int value = (Integer) t1.findColumn(ope1).getRows().get(i);

                        switch (ao) {
                            case "+":
                                num = value + num;
                                break;
                            case "-":
                                num = value - num;
                                break;
                            case "*":
                                num = value * num;
                                break;
                            default:
                                num = value / num;
                                break;
                        }
                        col.addValue(num);
                    }else {
                        float value = (Float) t1.findColumn(ope1).getRows().get(i);

                        switch (ao) {
                            case "+":
                                num = value + num;
                                break;
                            case "-":
                                num = value - num;
                                break;
                            case "*":
                                num = value * num;
                                break;
                            default:
                                num = value / num;
                                break;
                        }
                        col.addValue(num);
                    }
                }
            }

            return col;
        }
        //ope1 not found
        return null;
    }

    //filter a given table by the provided condition
    public static Table filter(Table t1, String condition) {
        return null;
    }

    public static void main(String[] args) {

        Table teams = loadTable("examples/teams");
        Table records =loadTable("examples/records");
        Table fans = loadTable("examples/fans");
        Table t1 = Join(fans,teams);

        System.out.println(t1);

        System.out.println(selectCo(t1,"TeamName, Firstname + Lastname as Name,    Sport, Mascot + YOLO as newMascot"));


    }

}
