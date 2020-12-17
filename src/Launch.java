import DBase.DBManager;
import GUI.SU_GUI;

import java.io.IOException;
import java.util.logging.*;

class Launch
{
    static SU_GUI gui = new SU_GUI();

    public static void main(String [] args) throws InterruptedException {
        // Turn logging off for htmlunit
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        gui.startWakeUpHTML();
        // Add graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                DBase.DBManager.shutdown();
                SU_GUI.closeHTML();
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
