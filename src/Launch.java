import MyUtils.*;
import DBase.*;
import GUI.*;

import java.io.IOException;
import java.util.logging.*;

class Launch
{
    static SU_GUI gui = new SU_GUI();

    public static void main(String [] args) throws InterruptedException {
        if ((args.length > 0) && (args[0].equals("offline"))) {
            gui.launch();
        } else {
            // Turn logging off for htmlunit
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(Level.OFF);
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
            gui.startWakeUpHTML();
            try {
                Runtime.getRuntime().exec("MySQL\\bin\\mysqld.exe");
                // Add graceful shutdown
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run()
                    {
                        DBase.DBManager.shutdown();
                        gui.closeHTML();
                    }
                });
            } catch (Exception e) {
                System.out.println("Error! Failed to start database...");
                System.exit(1);
            }

            // Boot-up Database at launch
            if (DBase.DBManager.bootUp()) {
                gui.launch();
            }
            else {
                System.out.println("Error! Could not establish database connection...");
            }
        }
    }
}
