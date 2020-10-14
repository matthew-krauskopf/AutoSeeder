import MyUtils.*;
import DBase.*;
import GUI.*;

import java.io.IOException;
import java.util.logging.*;

class Launch
{
    public static void main(String [] args) throws InterruptedException {
        if ((args.length > 0) && (args[0].equals("offline"))) {
            SU_GUI.main_menu();
        } else {
            // Turn logging off for htmlunit
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(Level.OFF);
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
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

            // Boot-up Database at launch
            if (DBase.DBManager.BootUp()) {
                SU_GUI.main_menu();
            }
            else {
                System.out.println("Error! Could not establish database connection...");
            }
        }
    }
}
