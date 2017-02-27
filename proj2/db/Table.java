package db;
import java.util.ArrayList;

/**
 * Created by Tong Yin on 2/21/2017.
 */
public class Table {
    private String tableName;
    private ArrayList<Column> columns;
    private int numOfRow;

    public Table() {
        columns = new ArrayList<>();
        numOfRow = 0;
        tableName = "";
    }

    public Table(String tn) {
        columns = new ArrayList<>();
        numOfRow = 0;
        tableName = tn;
    }

    //add a new column to the end of the column list
    public void addColumn(String n, String t) {
        columns.add(new Column(n,t));
    }

    //add a value at the end under a specific column i
    public void addValue(String value, int i) {
        try {
            if (columns.get(i).getType() == "int") {
                int v = Integer.parseInt(value);
                columns.get(i).addRow(v);
            } else if (columns.get(i).getType() == "float") {
                float v = Float.parseFloat(value);
                columns.get(i).addRow(v);
            } else {
                columns.get(i).addRow(value);
            }
            numOfRow = columns.get(i).getNumOfRows();
        }catch (Exception e) {
            System.err.printf("IndexOutOfBounds error, cannot add value under the column that is not created");
        }
    }

    //get num of Rows
    public int SizeOfRow() {
        return numOfRow;
    }

    //get num of Columns
    public int SizeOfCol() {
        return columns.size();
    }

    //find and return a column given its name
    public Column findColumn(String Colname) {
        for (Column col:columns) {
            if (Colname.equals(col.getName())) {
                return col;
            }

        }
        return null;
    }
    public static void main(String[] args) {
        Table t1 = new Table("t1");
        t1.addValue("1",0);
    }
}
