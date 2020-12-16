import Backend.*;
import DBase.*;
import GUI.*;

import java.io.IOException;

class Launch
{
    static SU_GUI gui = new SU_GUI();

    public static void main(String [] args) throws InterruptedException {
        if ((args.length > 0) && (args[0].equals("offline"))) {
            gui.launch();
        } else {
            if (!DBase.DBManager.startup()) {
                System.out.println("Error! Failed to start database...");
                System.exit(1);
            }
            // Add graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    DBase.DBManager.shutdown();
                }
            });

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
