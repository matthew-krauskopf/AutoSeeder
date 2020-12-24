import DBase.DBManager;
import GUI.SU_GUI;
import Backend.ReadFile;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

class Launch
{
    static SU_GUI gui = new SU_GUI();

    public static void main(String [] args) throws InterruptedException {
        // Turn logging off for htmlunit
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        Logger.getLogger("net.sourceforge.htmlunit").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        // Create tmp folder
        if (ReadFile.createTmpFolder()) {
            // Bootup webClient with Javascript
            gui.startWakeUpHTML();
            // Add graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    DBase.DBManager.shutdown();
                    SU_GUI.closeHTML();
                    ReadFile.deleteTmpFolder();
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
