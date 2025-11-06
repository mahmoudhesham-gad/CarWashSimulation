# Car Wash Simulation GUI

## Running the GUI

To run the GUI application:

```bash
cd src
java CarWashGUI
```

## Features

1. **Interactive Configuration**: On startup, you'll be prompted to enter:
   - Number of Service Bays (Pumps)
   - Waiting Area Capacity

2. **Real-time Visualization**:
   - **Service Bays Panel** (left): Shows each pump/bay
     - GREEN = Free
     - RED = Occupied with car ID
   
   - **Waiting Area Panel** (right): Shows waiting slots
     - GRAY = Empty slot
     - YELLOW = Occupied with car ID

3. **Add Cars at Runtime**:
   - Enter a car ID in the text field at the top
   - Click "Add Car" button
   - The car will be processed through the system automatically

4. **Activity Log**: Bottom panel shows all events:
   - Car arrivals
   - Cars entering waiting area or service area
   - Pump occupancy changes
   - Service completion

## How It Works

- Cars are added dynamically using the "Add Car" button
- Cars automatically move from arrival → waiting area → service bay
- Each pump services a car for 1.5-2.5 seconds
- The GUI updates in real-time showing the current state
- Multiple cars can be serviced simultaneously based on available pumps
- If all pumps are busy, cars wait in the waiting area

## Example Usage

1. Start the program
2. Enter "3" for pumps and "5" for waiting area
3. Add cars with IDs like: C1, C2, C3, etc.
4. Watch the simulation run in real-time!
