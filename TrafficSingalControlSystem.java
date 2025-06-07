
import java.util.Arrays;
import java.util.List;

enum Signal {
    RED,
    GREEN,
    YELLOW;
}

class Road {
    // Each road will have id, name and trafficLight
    public String id;
    public String name;
    public TrafficLight trafficLight;

    public Road(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public TrafficLight getTrafficLight() {
        return this.trafficLight;
    }
}

class TrafficLight {
    public String id;
    public Signal currentSignal;
    public int redDuration;
    public int yellowDuration;
    public int greenDuration;

    public TrafficLight(String id, int redDuration, int yellowDuration, int greenDuration) {
        this.id = id;
        this.redDuration = redDuration;
        this.yellowDuration = yellowDuration;
        this.greenDuration = greenDuration;
        this.currentSignal = Signal.RED;
    }

    public Signal getCurrentSignal() {
        return this.currentSignal;
    }

    public void changeSignal(Signal newSignal) {
        this.currentSignal = newSignal;
        System.out.println("TrafficLight " + id + " changed to " + newSignal);
    }

    public int getSignalDuration(Signal signal) {
        switch (signal) {
            case Signal.RED:
                return redDuration;
            case Signal.YELLOW:
                return yellowDuration;
            case Signal.GREEN:
                return greenDuration;
            default:
                return greenDuration;
        }
    }
}

class TrafficController {
    private static TrafficController instance;
    private List<Road> roads;
    private boolean emergencyDetected = false;
    private Road emergencyRoad = null;

    private TrafficController() {
    }

    public static TrafficController getInstance() {
        if (instance == null) {
            instance = new TrafficController();
        }
        return instance;
    }

    public void setRoads(List<Road> roads) {
        this.roads = roads;
    }

    public void startControl() {
        // In interview, mention it as basic implementation -> In real-case, we need to
        // run all these parallelly
        while (true) {
            if (emergencyDetected) {
                // If emergency is detected, change road signals accordingly such that emergency
                // road is green and others are red
                System.out.println("\n⚠️ Emergency Mode Activated!");
                for (Road road : roads) {
                    TrafficLight light = road.getTrafficLight();
                    if (road.equals(emergencyRoad)) {
                        light.changeSignal(Signal.GREEN);
                    } else {
                        light.changeSignal(Signal.RED);
                    }
                }
                sleepForSeconds(10); // Emergency duration of 10 seconds
                System.out.println("✅ Emergency Cleared.\n");
                emergencyDetected = false;
                emergencyRoad = null;
            } else {
                for (Road road : roads) {
                    // Get traffic light of road
                    TrafficLight trafficLight = road.getTrafficLight();

                    // Change to green
                    trafficLight.changeSignal(Signal.GREEN);
                    sleepForSeconds(trafficLight.getSignalDuration(Signal.GREEN));

                    // Change to yellow
                    trafficLight.changeSignal(Signal.YELLOW);
                    sleepForSeconds(trafficLight.getSignalDuration(Signal.YELLOW));

                    // Change to red
                    trafficLight.changeSignal(Signal.RED);
                    sleepForSeconds(trafficLight.getSignalDuration(Signal.RED));
                }
            }
        }
    }

    private void sleepForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleEmergency(Road emergencyRoad) {
        emergencyDetected = true;
        this.emergencyRoad = emergencyRoad;
        System.out.println("Emergency detected! Priority to: " + emergencyRoad);
    }
}

class TrafficSingalControlSystem {
    public static void main(String[] args) {
        // Creation of traffic lights with different durations
        TrafficLight light1 = new TrafficLight("TL1", 10, 3, 7);
        TrafficLight light2 = new TrafficLight("TL2", 12, 3, 8);

        // Creation of roads and assign traffic lights
        Road road1 = new Road("R1", "Main Street");
        road1.setTrafficLight(light1);

        Road road2 = new Road("R2", "Second Avenue");
        road2.setTrafficLight(light2);

        // Get controller and set roads
        TrafficController controller = TrafficController.getInstance();
        controller.setRoads(Arrays.asList(road1, road2));

        // Start the traffic control process
        controller.startControl();
    }
}

/*
 * 
 * UML Diagram
 * 
 * +-------------------------------------------+
 * | TrafficController |
 * | (Singleton) |
 * +-------------------------------------------+
 * | - instance: TrafficController |
 * | - roads: List<Road> |
 * | - emergencyDetected: boolean |
 * | - emergencyRoad: Road |
 * +-------------------------------------------+
 * | + getInstance(): TrafficController |
 * | + setRoads(roads: List<Road>): void |
 * | + startControl(): void |
 * | + handleEmergency(emergencyRoad: Road): void|
 * +-------------------------------------------+
 * 
 * ▲
 * |
 * |
 * +-------------------------------------------+
 * | Road |
 * +-------------------------------------------+
 * | - id: String |
 * | - name: String |
 * | - trafficLight: TrafficLight |
 * +-------------------------------------------+
 * | + setTrafficLight(light: TrafficLight): void |
 * | + getTrafficLight(): TrafficLight |
 * +-------------------------------------------+
 * 
 * |
 * |
 * ▼
 * +-------------------------------------------+
 * | TrafficLight |
 * +-------------------------------------------+
 * | - id: String |
 * | - currentSignal: Signal |
 * | - redDuration: int |
 * | - yellowDuration: int |
 * | - greenDuration: int |
 * +-------------------------------------------+
 * | + changeSignal(newSignal: Signal): void |
 * | + getCurrentSignal(): Signal |
 * | + getSignalDuration(signal: Signal): int |
 * +-------------------------------------------+
 * 
 * ▲
 * |
 * |
 * +-------------------------------------------+
 * | Signal |
 * | (enum) |
 * +-------------------------------------------+
 * | + RED |
 * | + YELLOW |
 * | + GREEN |
 * +-------------------------------------------+
 * 
 */