import java.sql.*;
import simpledb.remote.SimpleDriver;


public class TestSchema {
    public static void main(String[] args){
        Connection conn = null;

        try{
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);
            Statement stmt = conn.createStatement();

            String sql2 = "DROP TABLE OWNER CASCADE";
            stmt.executeQuery(sql2);

            String s = "create table DOG(DId int, DName varchar(20), BName varchar(20), Age int)";
            stmt.executeUpdate(s);
            System.out.println("Table DOG created");


            s = "insert into DOG(DId, DName, BName, Age) values ";
            String[] dogVals = {"(1, 'Molly', 'Burmeese', 1)",
                    "(2, 'Dozer', 'Bulldog', 13)",
                    "(1, 'Cody', 'Newfoundland', 9)"};


            for (int i = 0; i < dogVals.length; i++){
                stmt.executeUpdate(s + dogVals[i]);
            }
            System.out.println("Dog records inserted");


            String s1 = "create table OWNER(OId int, OName varchar(20))";
            stmt.executeUpdate(s1);

            s1 ="insert into OWNER(OId, OName) values";
            String[] ownerVals = {"(1, 'Marc')",
                    "(2, 'Dave')",
                    "(3, 'Brandon')",
                    "(4, 'Trevor')"};

            for (int i = 0; i <ownerVals.length; i++){
                stmt.executeUpdate(s1 + ownerVals[i]);
            }
            System.out.println("Owner records inserted");


            String s2 = "SELECT DName FROM DOG";
            ResultSet dogNames = stmt.executeQuery(s2);

            while(dogNames.next()){
                String name = dogNames.getString("Dname");
                System.out.println(name);
            }



        }

        catch(SQLException e) {
            e.printStackTrace();
        }

        finally{
            try{
                if (conn != null){
                    conn.close();
                }
            }

            catch(SQLException e){
                e.printStackTrace();
            }
        }


    }
}
