package db;
import java.util.ArrayList;

/**
 * Created by Tong Yin on 2/24/2017.
 */
public class Column<T> {
    private String name; //Column name
    private String type; //Column type
    private ArrayList<T> rows; //row list for the column

    public Column() {
        name = "";
        type = "";
        rows = new ArrayList<>();
    }

    public Column(String n, String t) {
        name = n;
        type = t;
        rows = new ArrayList<>();
    }

    //get number of rows in a column
    public int getNumOfRows() {
        return rows.size();
    }

    //get name of the column
    public String getName() {
        return name;
    }

    //get type of the column
    public String getType() {
        return type;
    }

    //add one more row at the end with some value
    public T addRow(T value) {
        rows.add(value);
        return rows.get(rows.size()-1);
    }
}
