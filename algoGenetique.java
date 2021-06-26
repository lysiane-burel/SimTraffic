import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class algoGenetique {
    private Individual[] individuals;
    private int intersectionSize;
    private int n; //number of individuals
    private Individual best;
    private final String pathCSVFirst="previousMeanSpeed.csv";

    private ArrayList<Node> separateLights(String pathCarrefour) {
        try {
            DocumentBuilderFactory documentBuilderFactoryOut = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilderOut = documentBuilderFactoryOut.newDocumentBuilder();
            Document documentOut = documentBuilderOut.parse(pathCarrefour);
            NodeList lights = documentOut.getElementsByTagName("tlLogic");
            ArrayList<Node> lights2=new ArrayList<>();
            while (lights.getLength()!=0) {
                Node element = lights.item(0);
                lights2.add(element.cloneNode(true));
                element.getParentNode().removeChild(element);
            }
            TransformerFactory transformerFactoryOut = TransformerFactory.newInstance();
            Transformer transformerOut = transformerFactoryOut.newTransformer();
            DOMSource domSourceOut = new DOMSource(documentOut);
            StreamResult streamResultOut = new StreamResult(new File("myCarrefour.xml"));
            transformerOut.transform(domSourceOut, streamResultOut);
            //System.out.println(lights.getLength());
            return lights2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void generate(int n, String pathCarrefour) {
        this.n = n;
        individuals = new Individual[n];
        Individual individual = new Individual(Objects.requireNonNull(separateLights(pathCarrefour)));
        intersectionSize=individual.getIntersections().size();
        try {
            best=individual.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File csvOutputFile = new File(pathCSVFirst);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        	best.score();
            pw.println(best.getScore());
            System.out.println(best.getScore());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < n; i++) {
            try {
                individuals[i] = individual.clone();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
            individuals[i].mutate();
        }
    }
    public algoGenetique(int n, String pathCarrefour) {
        System.out.println("Generating individuals:");
        this.generate(n,pathCarrefour);

    }

    private void sort() {
        Arrays.sort(individuals);
    }

    private void mutate() {
        for (int i = 0; i < n; i++) {
            individuals[i].mutate();
        }
    }

    private Individual[] selection(int nb) {
        return Arrays.copyOf(individuals, nb);
    }

    private Individual[] crossover(Individual[] selected,int nb) {
        Individual[] crossed=new Individual[nb];
        Random random=new Random();
        int index1;
        int index2=0;
        int a=selected.length;
        for(int i=0;i<nb;i++){
            index1=random.nextInt(a);
            while(index1==index2 && a!=1){
                index2=random.nextInt(a);
            }
            crossed[i]=new Individual(selected[index1],selected[index2],intersectionSize);
        }
        return crossed;
    }

    public Individual optimize(int rounds) {
        int a=n/3;
        int nb=n-a;
        for (int i = 0; i < rounds; i++) {
            System.out.println((new StringBuilder("Round "+(i+1)+" ")));
            Individual[] array1 = selection(a);
            Individual[] array2 = crossover(array1,nb);
            System.arraycopy(array1, 0, individuals, 0, a);
            System.arraycopy(array2, 0, individuals, a, nb);
            mutate();
            sort();
            if (individuals[0].compareTo(best)<0) try {
                best = individuals[0].clone();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        return best;
    }
}
