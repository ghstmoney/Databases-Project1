package simpledb.server;

import simpledb.remote.*;

import java.io.File;
import java.rmi.registry.*;

public class Startup {
    public static void main(String args[]) throws Exception {
        // macOS version (I'm assuming this is where it saves the DB folder
        //File oldDB = new File("~/cs4432DB");
        // Windows version
        File oldDB = new File("C:\\Users\\Frank\\cs4432DB");
        deleteDirectory(oldDB);
        System.out.println("deleted old DB");
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
     * @return true when the directory has been deleted
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
