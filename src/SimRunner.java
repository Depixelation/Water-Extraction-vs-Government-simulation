public class SimRunner {
    public static void main(String[] args) {
        Simulation sa1 = new Simulation(new Simulation.Parameters(4, 1, 1.0 / 12, 1.0));
        double a1 = sa1.run(100);
        Simulation sa2 = new Simulation(new Simulation.Parameters(4, 4, 1.0 / 12, 1.0));
        double a2 = sa2.run(100);

        Simulation sb1 = new Simulation(new Simulation.Parameters(4, 1, 1.0 / 3, 9.0));
        double b1 = sb1.run(100);
        Simulation sb2 = new Simulation(new Simulation.Parameters(4, 4, 1.0 / 3, 9.0));
        double b2 = sb2.run(100);

        System.out.printf("""
                
                Results for N = 4: 
                T/D %f: %f/d (n=4) | %f/d (n=1)
                T/D %f: %f/d (n=4) | %f/d (n=1)
                """,
                sa1.parameters.T / sa1.parameters.D, a2, a1,
                sb2.parameters.T / sb2.parameters.D, b2, b1
        );
    }
}
