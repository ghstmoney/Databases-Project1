package simpledb.server;

import simpledb.remote.*;

import java.io.File;
import java.rmi.registry.*;

public class Startup {
    public static void main(String args[]) throws Exception {
        File oldDB = new File("C:\\Users\\Frank\\cs4432DB");
        deleteDirectory(oldDB);
        // configure and initialize the database
        SimpleDB.init(args[0]);

        // create a registry specific for the server on the default port
        Registry reg = LocateRegistry.createRegistry(1099);

        // and post the server entry in it
        RemoteDriver d = new RemoteDriverImpl();
        reg.rebind("simpledb", d);


        System.out.println("database server ready");
    }

    /**
     * Deletes old database
     * @param directoryToBeDeleted File descriptor that points to the old database
     * @return
     */
    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
