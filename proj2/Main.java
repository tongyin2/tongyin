import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import db.Database;

public class Main {
    private static final String EXIT   = "exit";
    private static final String PROMPT = "> ";

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Database db = new Database();
        System.out.println("Welcome to the DBMS designed by Tong Yin\n\n" +
                "In this database, an user is able to load/store tables from/into the .tbl\n" +
                "files in the 'examples' folder. An user can also create/delete tables in \n" +
                "this database. Moreover, an user can also manipulate tables by using the 'select'" +
                " command.\n\nFor information about the command line (DSL), \nplease consult the website: " +
                "http://datastructur.es/sp17/materials/proj/proj2/proj2.html\n");
        System.out.print(PROMPT);

        String line = "";
        while ((line = in.readLine()) != null) {
            if (EXIT.equals(line)) {
                break;
            }

            if (!line.trim().isEmpty()) {
                String result = db.transact(line);
                if (result.length() > 0) {
                    System.out.println(result);
                }
            }
            System.out.print(PROMPT);
        }

        in.close();
    }
}
