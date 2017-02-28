package db;
import java.util.ArrayList;

/**
 * Created by Tong Yin on 2/21/2017.
 */
public class Table {
    private String tableName;
    private ArrayList<Column> columns;


    public Table() {
        columns = new ArrayList<>();
        tableName = "";
    }

    public Table(String tn) {
        columns = new ArrayList<>();
        tableName = tn;
    }

    //add a new column to the end of the column list
    public void addNewColumn(String n, String t) {
        columns.add(new Column(n,t));
    }

    //add a column
    public void addColumn(Column col) {
        columns.add(col);
    }

    //add a column at a given position
    public void addColumn(Column col,int i) {
        columns.add(i,col);
    }

    //add a value at the end under a specific column i
    public void addValue(String value, int i) {
        try {
            if (columns.get(i).getType() == "int") {
                int v = Integer.parseInt(value);
                columns.get(i).addValue(v);
            } else if (columns.get(i).getType() == "float") {
                float v = Float.parseFloat(value);
                columns.get(i).addValue(v);
            } else {
                columns.get(i).addValue(value);
            }
        }catch (Exception e) {
            System.err.printf("IndexOutOfBounds error, cannot add value under the column that is not created");
        }
    }

    //get num of Rows
    public int sizeOfRows() {
        if (columns.size()==0) {
            return 0;
        }else {
            return columns.get(0).sizeOfRows();
        }
    }

    //get num of Columns
    public int sizeOfCols() {
        return columns.size();
    }

    //get the columns of a table
    public ArrayList<Column> getColumns() {
        return columns;
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
