package db;
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

    }

}
