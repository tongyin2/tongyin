package db;
import edu.princeton.cs.introcs.In;
import java.util.ArrayList;


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

    //load a table
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
        return t;

    }

    public static void main(String[] args) {
        //example T7 T8 -> T9
        Table t1 = new Table("t1");
        Table t2 = new Table("t2");

        t1.addNewColumn("X","int");
        t1.addNewColumn("Y","int");
        t1.addNewColumn("Z","int");
        t1.addNewColumn("W","int");

        t1.addValue("1",0);
        t1.addValue("7",1);
        t1.addValue("2",2);
        t1.addValue("10",3);

        t1.addValue("7",0);
        t1.addValue("7",1);
        t1.addValue("4",2);
        t1.addValue("1",3);

        t1.addValue("1",0);
        t1.addValue("9",1);
        t1.addValue("9",2);
        t1.addValue("1",3);


        t2.addNewColumn("W","int");
        t2.addNewColumn("B","int");
        t2.addNewColumn("Z","int");

        t2.addValue("1",0);
        t2.addValue("7",1);
        t2.addValue("4",2);

        t2.addValue("7",0);
        t2.addValue("7",1);
        t2.addValue("3",2);

        t2.addValue("1",0);
        t2.addValue("9",1);
        t2.addValue("6",2);

        t2.addValue("1",0);
        t2.addValue("11",1);
        t2.addValue("9",2);

        t2.addValue("8",0);
        t2.addValue("6",1);
        t2.addValue("2",2);

        t2.addValue("1",0);
        t2.addValue("5",1);
        t2.addValue("4",2);

        //example T1 T2 -> T3
        Table t4 = new Table();
        Table t5 = new Table();

        t4.addNewColumn("X","int");
        t4.addNewColumn("Y","int");
        t4.addValue("2",0);
        t4.addValue("5",1);
        t4.addValue("8",0);
        t4.addValue("3",1);
        t4.addValue("13",0);
        t4.addValue("7",1);

        t5.addNewColumn("X","int");
        t5.addNewColumn("Z","int");
        t5.addValue("2",0);
        t5.addValue("4",1);
        t5.addValue("8",0);
        t5.addValue("9",1);
        t5.addValue("10",0);
        t5.addValue("1",1);
        t5.addValue("11",0);
        t5.addValue("1",1);

        //example T10 T11-> 0
        Table t6 = new Table();
        Table t7 = new Table();

        t6.addNewColumn("X","int");
        t6.addNewColumn("Y","int");
        t6.addValue("1",0);
        t6.addValue("7",1);
        t6.addValue("7",0);
        t6.addValue("7",1);
        t6.addValue("1",0);
        t6.addValue("9",1);

        t7.addNewColumn("X","int");
        t7.addNewColumn("Z","int");
        t7.addValue("3",0);
        t7.addValue("8",1);
        t7.addValue("4",0);
        t7.addValue("9",1);
        t7.addValue("5",0);
        t7.addValue("10",1);

        //example T12 T13 -> 9
        Table t8 = new Table();
        Table t9 = new Table();

        t8.addNewColumn("X","int");
        t8.addNewColumn("Y","int");
        t8.addValue("1",0);
        t8.addValue("7",1);
        t8.addValue("7",0);
        t8.addValue("7",1);
        t8.addValue("1",0);
        t8.addValue("9",1);

        t9.addNewColumn("A","int");
        t9.addNewColumn("B","int");
        t9.addValue("3",0);
        t9.addValue("8",1);
        t9.addValue("4",0);
        t9.addValue("9",1);
        t9.addValue("5",0);
        t9.addValue("10",1);

        Table t3 = Join(t1,t2);

        Table t = loadTable("examples/t1");
    }

}
