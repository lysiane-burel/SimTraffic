public class MainGenetique {
    private final static String pathCarrefour="Carrefour.net.xml";
    private final static String pathNode="true.nod.xml";//TODO integration
    private final static String pathCSV="output.csv";
    public static void mainGenetique() {

        int numberGeneration=3; //Integer.parseInt(args[0]);
        int numberIndividual=3; //Integer.parseInt(args[1]);
        algoGenetique algo= new algoGenetique(numberIndividual,pathCarrefour);
        Coordinate.init(pathNode);
        Individual best =algo.optimize(numberGeneration);
        best.toCSV(pathCSV);
        System.out.println("Best score is:");
        System.out.println(best.getScore());
    }
}
