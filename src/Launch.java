import Backend.*;
import DBase.*;
import GUI.*;

import java.io.IOException;

class Launch
{
    static SU_GUI gui = new SU_GUI();

    public static void main(String [] args) throws InterruptedException {
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
