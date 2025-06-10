import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

enum Direction {
    UP,
    DOWN,
    IDLE
}

class Elevator {
    String id;
    Direction direction;
    int currentFloor;
    int capacity;
    int currentLoad;
    List<Integer> upRequests;
    List<Integer> downRequests;

    Elevator(String id,int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.direction = Direction.IDLE;
        this.currentFloor = 0;
        this.currentLoad = 0;
        this.upRequests = new ArrayList<>();
        this.downRequests = new ArrayList<>();
    }

    public Direction getDirection() {
        return direction;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public boolean isOverloaded() {
        return currentLoad >= capacity;
    }

    public void addRequest(int floor) {
        System.out.println("currentLoad = " + currentLoad + " " + capacity + " " + isOverloaded());
        if (isOverloaded()) {
            System.out.println("Elevator " + id + " is at capacity! Request ignored.");
            return;
        }
        if (floor > currentFloor) {
            upRequests.add(floor);
            Collections.sort(upRequests);
        } else if (floor < currentFloor) {
            downRequests.add(floor);
            downRequests.sort(Collections.reverseOrder());
        }
        currentLoad++; // Simulate a new passenger added
    }

    public void processNextRequest() {
        if (direction == Direction.IDLE) {
            if (!upRequests.isEmpty()) {
                direction = Direction.UP;
            } else if (!downRequests.isEmpty()) {
                direction = Direction.DOWN;
            } else {
                return;
            }
        }
        if (direction == Direction.UP && !upRequests.isEmpty()) {
            int nextFloor = upRequests.remove(0);
            currentFloor = nextFloor;
            System.out.println("Elevator " + id + " moved to floor " + currentFloor);
            currentLoad = Math.max(0, currentLoad - 1); // simulate a passenger leaving
        } else if (direction == Direction.DOWN && !downRequests.isEmpty()) {
            int nextFloor = downRequests.remove(0);
            currentFloor = nextFloor;
            System.out.println("Elevator " + id + " moved to floor " + currentFloor);
            currentLoad = Math.max(0, currentLoad - 1);
        } else {
            if (direction == Direction.UP && !downRequests.isEmpty()) {
                direction = Direction.DOWN;
            } else if (direction == Direction.DOWN && !upRequests.isEmpty()) {
                direction = Direction.UP;
            } else {
                direction = Direction.IDLE;
            }
        }
    }

    // Handle request cancellation
    public void cancelRequest(int floor) {
        if (upRequests.remove(Integer.valueOf(floor)) || downRequests.remove(Integer.valueOf(floor))) {
            currentLoad = Math.max(0, currentLoad - 1); // simulate passenger cancellation
            System.out.println("Request for floor " + floor + " canceled in elevator " + id);
        } else {
            System.out.println("No such request for floor " + floor + " found in elevator " + id);
        }
    }
}

class ElevatorController {
    List<Elevator> elevators;

    ElevatorController(List<Elevator> elevators) {
        this.elevators = elevators;
    }

    public void incomingExternalRequest(int fromFloor, Direction direction) {
        // Find the best elevator to handle it
        Elevator bestElevator = findBestElevator(fromFloor, direction);
        // Assign request to that elevator
        bestElevator.addRequest(fromFloor);
    }
    
    public void incomingInternalRequest(Elevator elevator, int toFloor) {
        // Assign request to that elevator
        elevator.addRequest(toFloor);
    }

    private Elevator findBestElevator(int fromFloor, Direction direction) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            // Check if elevator is idle or moving in the same direction
            if (elevator.getDirection() == Direction.IDLE || elevator.getDirection() == direction) {
                int distance = Math.abs(elevator.getCurrentFloor() - fromFloor);
                if (distance < minDistance) {
                    bestElevator = elevator;
                    minDistance = distance;
                }
            }
        }

        // If no matching elevator found, fallback to any elevator (e.g., first one)
        if (bestElevator == null) {
            bestElevator = elevators.get(0);
        }

        return bestElevator;
    }

    public void start() {
        for (Elevator elevator : elevators) {
            new Thread(() -> {
                while (true) {
                    elevator.processNextRequest();
                    try {
                        Thread.sleep(1000); // 1 second delay to simulate real movement
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}


public class ElevatorSystem {
    public static void main(String[] args) {
        Elevator elevator1 = new Elevator("Elevator1", 4);
        Elevator elevator2 = new Elevator("Elevator2", 4);

        List<Elevator> elevators = new ArrayList<>();
        elevators.add(elevator1);
        elevators.add(elevator2);

        ElevatorController controller = new ElevatorController(elevators);
        controller.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnter command: ");
            System.out.println("1: External Request (format: 1 fromFloor direction)");
            System.out.println("2: Internal Request (format: 2 elevatorId toFloor)");
            System.out.println("3: Cancel Request (format: 3 elevatorId floor)");
            System.out.println("4: Exit");

            int choice = scanner.nextInt();
            if (choice == 4) {
                System.out.println("Exiting system!");
                break;
            }

            switch (choice) {
                case 1: {
                    int fromFloor = scanner.nextInt();
                    String dir = scanner.next();
                    Direction direction = dir.equalsIgnoreCase("up") ? Direction.UP : Direction.DOWN;
                    controller.incomingExternalRequest(fromFloor, direction);
                    break;
                }
                case 2: {
                    String elevatorId = scanner.next();
                    int toFloor = scanner.nextInt();
                    Elevator selected = elevators.stream()
                        .filter(e -> e.id.equals(elevatorId))
                        .findFirst()
                        .orElse(null);
                    if (selected != null) {
                        controller.incomingInternalRequest(selected, toFloor);
                    } else {
                        System.out.println("No such elevator!");
                    }
                    break;
                }
                case 3: {
                    String elevatorId = scanner.next();
                    int floor = scanner.nextInt();
                    Elevator selected = elevators.stream()
                        .filter(e -> e.id.equals(elevatorId))
                        .findFirst()
                        .orElse(null);
                    if (selected != null) {
                        selected.cancelRequest(floor);
                    } else {
                        System.out.println("No such elevator!");
                    }
                    break;
                }
                default:
                    System.out.println("Invalid choice!");
            }
        }

        scanner.close();
        System.out.println("Simulation finished!");
    }
}


/*

UML Diagram 

─────────────────────────────
|          Elevator           |
─────────────────────────────
| - id: String                |
| - direction: Direction      |
| - currentFloor: int         |
| - capacity: int             |
| - currentLoad: int          |
| - upRequests: List<Integer> |
| - downRequests: List<Integer>|
─────────────────────────────
| + getDirection(): Direction |
| + getCurrentFloor(): int    |
| + isOverloaded(): boolean   |
| + addRequest(int): void     |
| + processNextRequest(): void|
| + cancelRequest(int): void  |
─────────────────────────────

             ▲
             │ uses
             ▼

──────────────────────────────────────────────────────────────
|                  ElevatorController                         |
──────────────────────────────────────────────────────────────
| - elevators: List<Elevator>                                 |
──────────────────────────────────────────────────────────────
| + incomingExternalRequest(int, Direction): void             |
| + incomingInternalRequest(Elevator, int): void              |
| + findBestElevator(int, Direction): Elevator                |
| + start(): void                                             |
──────────────────────────────────────────────────────────────

             ▲
             │ entry point
             ▼

─────────────────────────────
|         ElevatorSystem      |
─────────────────────────────
| + main(String[]): void      |
─────────────────────────────

─────────────────────────────
|         Direction (enum)    |
─────────────────────────────
| + UP                        |
| + DOWN                      |
| + IDLE                      |
─────────────────────────────

==========================================

Database Diagram

─────────────────────────────
|         Elevators          |
─────────────────────────────
| id (PK)                    |
| current_floor: int         |
| direction: String          |
| capacity: int              |
| current_load: int          |
─────────────────────────────

──────────────────────────────────────────────────────────────
|          ElevatorRequests                                    |
──────────────────────────────────────────────────────────────
| id (PK)                                                      |
| elevator_id (FK → Elevators.id)                              |
| floor_number: int                                            |
| direction: String                                            |
| status: String (pending, done, canceled)                     |
──────────────────────────────────────────────────────────────

 */