import MyUtils.*;
import DBase.*;
import GUI.*;

import java.io.IOException;

class Launch
{
    public static void main(String [] args) throws InterruptedException {

        try {
            Runtime.getRuntime().exec("MySQL\\bin\\mysqld.exe");
            // Add graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    DBase.DBManager.shutdown();
                }
            });
        } catch (Exception e) {
            System.out.println("Error! Failed to start database...");
            System.exit(1);
        }
        SU_GUI gui = new SU_GUI();
        gui.main_menu();
    }
}
