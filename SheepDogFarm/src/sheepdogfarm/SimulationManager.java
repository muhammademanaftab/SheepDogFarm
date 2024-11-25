package sheepdogfarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationManager {

    private final Farm farm;
    private final int numSheeps;
    private final int numDogs;
    private final List<Sheep> sheepList;
    private final List<Dog> dogList;

    public SimulationManager(int rows, int cols, int numSheep, int numDogs) {
        this.farm = new Farm(rows, cols);
        this.numSheeps = numSheep;
        this.numDogs = numDogs;
        this.sheepList = new ArrayList<>();
        this.dogList = new ArrayList<>();
    }

    //simulation run krega 
    public void runSimulation() {
        FarmSimulation.setFarm(farm);
        ExecutorService executor = Executors.newCachedThreadPool();

        // dogs bnanay ke liay
        for (int i = 0; i < numSheeps; i++) {
            char sheepName = (char) ('A' + i);
            Sheep sheep = new Sheep(String.valueOf(sheepName), farm);
            sheepList.add(sheep);
        }

        //Sheeps bnanay ke liay
        for (int i = 0; i < numDogs; i++) {
            Dog dog = new Dog(String.valueOf(i + 1), farm);
            dogList.add(dog);
        }

//        threads ko run laiy
        for (Sheep sheep : sheepList) {
            executor.execute(() -> {
                Thread thread = Thread.currentThread();
                thread.setName(sheep.toString());
                FarmSimulation.registerThread(thread);
                sheep.run();
            });
        }

//        dogs ka thread run krne liay
        for (Dog dog : dogList) {
            executor.execute(() -> {
                Thread thread = Thread.currentThread();
                thread.setName(dog.toString());
                FarmSimulation.registerThread(thread);
                dog.run();
            });
        }

        printInitialPositions();
        while (FarmSimulation.isSimulationRunning()) {
            updateFarmState();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }

        closeExecutor(executor);
        printFinalState();
    }

    // Executor service run krne liay
    private void closeExecutor(ExecutorService executor) {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("Some threads not closed yet...");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

//    positions ko extend krne liay
    private void printInitialPositions() {
        System.out.println("Initial positions to Check:");
        for (Sheep sheep : sheepList) {
            System.out.println("Sheep " + sheep + " is in " + farm.getZone(sheep.getRow(), sheep.getCol())
                    + " at (" + sheep.getRow() + ", " + sheep.getCol() + ")");
        }
        for (Dog dog : dogList) {
            System.out.println("Dog " + dog + " is in " + farm.getZone(dog.getRow(), dog.getCol())
                    + " (" + dog.getRow() + ", " + dog.getCol() + ")");
        }
    }

    private void updateFarmState() {
        System.out.print("\033[H\033[2J");
        System.out.print("\u001B[0;0H");
        System.out.println("\n-------- Farm State --------");
        farm.printFarm();

    }

    private void printFinalState() {
        System.out.print("\033[H\033[2J");
        System.out.print("\u001B[0;0H");
        System.out.println("\n---- Final Farm State ----");
        farm.printFarm();
    }
}
