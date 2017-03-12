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
            if (columns.get(i).getType().equals("int")) {
                if (value.equals("NOVALUE")) {
                    columns.get(i).addValue("NOVALUE");
                }else if (value.equals("NaN")){
                    columns.get(i).addValue("NaN");
                }else {
                    columns.get(i).addValue(Integer.parseInt(value));
                }
            } else if (columns.get(i).getType().equals("float")) {
                if (value.equals("NOVALUE")) {
                    columns.get(i).addValue("NOVALUE");
                }else if (value.equals("NaN")) {
                    columns.get(i).addValue("NaN");
                }else {
                    columns.get(i).addValue(Float.parseFloat(value));
                }
            } else {
                value = value.replace("'","");
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

    //get the name of the table
    public String getTableName() {
        return tableName;
    }

    //set the name of the table
    public String setTableName(String na) {
        tableName = na;
        return tableName;
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

    @Override
    public String toString(){
        String s = "";
        for (int i=-1; i < sizeOfRows(); i++) {
            for (int j=0; j < sizeOfCols(); j++) {
                if (i < 0) {
                    s = s + getColumns().get(j).getName()+" "+getColumns().get(j).getType();
                }else {
                    if (getColumns().get(j).getType().equals("string")
                            && !getColumns().get(j).getRows().get(i).equals("NOVALUE")) {
                        s = s+"'";
                    }
                    s = s + getColumns().get(j).getRows().get(i);
                    if (getColumns().get(j).getType().equals("string")
                            && !getColumns().get(j).getRows().get(i).equals("NOVALUE")) {
                        s = s+"'";
                    }
                }
                if (j+1<sizeOfCols()) {
                    s = s + ",";
                }
            }
            s = s + "\n";
        }
        return s;
    }

    public static void main(String[] args) {
        Table t1 = new Table("t1");
        t1.addValue("1",0);
    }
}
