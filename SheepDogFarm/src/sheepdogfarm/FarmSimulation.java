package sheepdogfarm;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FarmSimulation {
    private static final AtomicBoolean simulationRunning = new AtomicBoolean(true);    
    private static final ArrayList<Thread> threadsWorking = new ArrayList<>();
    public static Farm farm;

    
//    simulation ko check krega or run krega 
    public static boolean isSimulationRunning() {
        return simulationRunning.get();
    }
    
    public static void registerThread(Thread thread) {
        threadsWorking.add(thread);
    }

    
      public static void stopSimulation() {
        simulationRunning.set(false);
        for (Thread thread : threadsWorking) {
            thread.interrupt();
        }
    }

    public static void setFarm(Farm newFarm) {
        farm = newFarm;
    }
    public static AtomicBoolean getState(){
        return simulationRunning;
    }
}