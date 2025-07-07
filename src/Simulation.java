import java.util.Arrays;

public class Simulation {
    private double t;
    private double iR;
    private double L;
    private double w;
    private int activeTanks;
    private final Tank[] tanks;
    public final Parameters parameters;

    public Simulation(Parameters params) {
        parameters = params;
        tanks = new Tank[parameters.N];
        Arrays.setAll(tanks, (_) -> new Tank());
        t = 0.0;
        L = 0.0;
        iR = parameters.R;
        activeTanks = parameters.N;
        w = 0.0;
    }

    private void step(double deltaT, Event event){
        System.out.printf("Step start, t = %.3f) L = %.3f, tanks: [", t, L);

        L = 0.0;
        for (Tank tank : tanks) {
            System.out.printf("%.3f", tank.l);
            if (!tank.onDelivery) {
                tank.l += deltaT * parameters.r;
                L += tank.l;
            } else {
                System.out.print(" (on delivery)");
            }

            System.out.print(" ");
        }

        System.out.println("]");

        t += deltaT;

        System.out.printf("Step end, t = %.3f) L = %.3f, tanks: [", t, L);
        for (Tank tank : tanks) {
            System.out.printf("%.3f", tank.l);
            if (tank.onDelivery) System.out.print(" (on delivery)");
            System.out.print(" ");
        }
        System.out.println("]");

        if (event.type == Event.Type.REACH_CAP) {
            for (int i = 0; i < parameters.n; i++) {
                System.out.printf("Delivering tank with l = %.3f %n", tanks[0].l);
                w += tanks[0].l;
                L -= tanks[0].l;
                tanks[0].onDelivery = true;
                tanks[0].l = 0.0;
                tanks[0].returnTime = t + parameters.D;
                activeTanks --;
                sortTanks();
            }
        } else if (event.type == Event.Type.TANK_ARRIVE) {
            Tank tank = (Tank) event.obj;
            tank.onDelivery = false;
            activeTanks ++;
            System.out.println("Tank returned");
            sortTanks();
        }
    }

    private Tank getNextReturn() {
        Tank nextReturnTank = null;

        // Iterate through all tanks to find the one with the earliest return time
        for (Tank tank : tanks) {
            // Skip tanks that are not yet on delivery
            if (tank.onDelivery) {
                // If no tank has been found yet, or the current tank has an earlier return time, update nextReturnTank
                if (nextReturnTank == null || tank.returnTime < nextReturnTank.returnTime) {
                    nextReturnTank = tank;
                }
            }
        }

        return nextReturnTank;
    }

    public double run(double d) {
        System.out.printf("%n>>> Starting sim with %d tanks, fill rate %fC per tank, %d tanks delivering each time, and delivery time %f days.", parameters.N, parameters.r, parameters.n, parameters.D);
        while (t < d) {
            System.out.println();
            iR = activeTanks * parameters.r;
            double timeTillFull = (1.0 - L) / iR;

            Tank nextReturn = getNextReturn();
            if (nextReturn != null) {
                double timeTillNextReturn = nextReturn.returnTime - t;

                if (timeTillNextReturn < 0) {
                    throw new RuntimeException("[!] Tank was not returned! It should have been here in " + timeTillNextReturn + " days");
                }

                if (timeTillNextReturn < timeTillFull) {
                    System.out.printf("Stepping %.3f days till next tank return %n", timeTillNextReturn);
                    step(timeTillNextReturn, Event.of(Event.Type.TANK_ARRIVE, nextReturn));
                    continue;
                }
            }

            System.out.printf("Stepping %.3f days with %d active tanks at total rate of %.3fC till C is reached %n", timeTillFull, activeTanks, iR);
            step(timeTillFull, Event.of(Event.Type.REACH_CAP, null));
            continue;
        }

        double rate = w / t;
        System.out.printf("%n>>> Simulation completed after %.3f days, with %.3fC water sold. Rate per day: %f %n", t, w, rate);
        return rate;
    }

    private void sortTanks() {
        Arrays.sort(tanks, (tank1, tank2) -> {
            // Compare 'l' in descending order
            return Double.compare(tank2.l, tank1.l);
        });
    }

    public static class Parameters {
        public final int N;
        public final int n;
        public final double r;
        public final double D;

        public final double tau;
        public final double R;
        public final double T;

        public Parameters(int numTanks, int numToSellWhenFull, double ratePerTank, double deliveryTime) {
            N = numTanks;
            n = numToSellWhenFull;
            r = ratePerTank;
            D = deliveryTime;

            tau = 1 / r;
            R = N * r;
            T = 1 / R;
        }
    }
}