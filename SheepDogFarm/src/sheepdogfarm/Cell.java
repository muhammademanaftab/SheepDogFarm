package sheepdogfarm;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cell {

    private final AtomicReference<FarmObject> occupant = new AtomicReference<>(null);

    private final AtomicBoolean gateCheck = new AtomicBoolean(false);

    
    //Is se chek hoga ke cell occupied ha ya nai
    public boolean isOccupied() {
        return occupant.get() != null;
    }

    //is se set hoga cell ka occupy hona
    public boolean setOccupied(FarmObject obj) {
        return occupant.compareAndSet(null, obj);
    }

    // is se clear hoga cell or andar hee set hojayga for concurrency takay lost na ho
    public boolean clearOccupant(FarmObject obj) {
        return occupant.compareAndSet(obj, null);
    }

    //is se occupant ko get krenge 
    public FarmObject getOccupant() {
        return occupant.get();
    }

    //gate ko check krne ke liay oska check
    public boolean isGate() {
        return gateCheck.get();
    }

    //cell ko empty krne ke liay setempty method
    public synchronized void setEmpty() {
        occupant.set(null);
    }

    //gate ko ko set krne ke liay yahan se set krnege is function ko call krke
    public void setAsGate() {
        gateCheck.set(true);
    }

    @Override
    public String toString() {
        if (isGate()) {
            return " ";
        }
        FarmObject currentOccupant = getOccupant();
        if (currentOccupant == null) {
            return " ";
        } else {
            return currentOccupant.toString();
        }
    }

}
