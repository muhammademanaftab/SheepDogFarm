package sheepdogfarm;


//simulation run krega, rows or cols kee basis pr
public class SheepFarmSimulation {
    public static void main(String[] args) {
        int rows = 14;
        int cols = 14;
        int numSheep = 10;
        int numDogs = 5;

        SimulationManager simulationManager = new SimulationManager(rows, cols, numSheep, numDogs);
        simulationManager.runSimulation();
    }
}
