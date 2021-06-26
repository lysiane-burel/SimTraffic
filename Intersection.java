import org.w3c.dom.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
//import org.apache.commons.math3.distribution.PoissonDistribution;
//import org.apache.commons.math3.distribution.UniformIntegerDistribution;



public class Intersection {
    private final String id;
    private int offset;
    private final String programID;
    private String pathAdditional;
    private final ArrayList<Integer> PhaseDurations;
    private final ArrayList<String> PhaseStates;

    public Intersection(Element intersection) {
        this.id = intersection.getAttribute("id");
        this.offset = Integer.parseInt(intersection.getAttribute("offset"));
        this.programID = intersection.getAttribute("programID");
        NodeList phases = intersection.getElementsByTagName("phase");
        this.PhaseDurations = new ArrayList<>();
        this.PhaseStates = new ArrayList<>();
        for (int j = 0; j < phases.getLength(); j++) {
            Node phase = phases.item(j);
            Element phaseElement = (Element) phase;
            Integer duration = Integer.parseInt(phaseElement.getAttribute("duration"));
            String state = phaseElement.getAttribute("state");
            this.PhaseDurations.add(duration);
            this.PhaseStates.add(state);
        }
    }

    public void addToNetwork(Document documentOut) {
        Element element = documentOut.createElement("tlLogic");
        element.setAttribute("offset", String.valueOf(offset));
        element.setAttribute("type", "static");
        element.setAttribute("programID", programID);
        element.setAttribute("id", String.valueOf(id));
        Element phase;
        for (int i = 0; i < PhaseDurations.size(); i++) {
            phase = documentOut.createElement("phase");
            phase.setAttribute("duration", String.valueOf(PhaseDurations.get(i)));
            phase.setAttribute("state", PhaseStates.get(i));
            element.appendChild(phase);
        }
        Element root=(Element) documentOut.getElementsByTagName("net").item(0);
        root.insertBefore(element,root.getElementsByTagName("junction").item(0));
    }

    public Intersection(Intersection intersection1, Intersection intersection2) {
        this.id = intersection1.getId();
        this.offset = mean(intersection1.getOffset(), intersection2.getOffset());
        this.programID = intersection1.getProgramID();
        this.pathAdditional = intersection1.getPathAdditional();
        this.PhaseStates = intersection1.getPhaseStates();
        this.PhaseDurations = new ArrayList<>();
        ArrayList<Integer> phaseDurations1 = intersection1.getPhaseDurations();
        ArrayList<Integer> phaseDurations2 = intersection2.getPhaseDurations();
        Random random = new Random();
        for (int i = 0; i < phaseDurations1.size(); i++) {

            int a=random.nextInt(1);
            if (a==0){
                this.PhaseDurations.add(phaseDurations1.get(i));
            }else{
                this.PhaseDurations.add(phaseDurations2.get(i));
            }
        }
    }

    public int mean(int n1, int n2) {
        return (n1 + n2) / 2;
    }

    public String getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }

    public String getProgramID() {
        return programID;
    }

    public String getPathAdditional() {
        return pathAdditional;
    }

    public ArrayList<Integer> getPhaseDurations() {
        return PhaseDurations;
    }

    public ArrayList<String> getPhaseStates() {
        return PhaseStates;
    }

    // TODO position aleatoire et reunir toutes les variables aleatoires ici
    public void mutate() {
        Random random = new Random();
        int duration;
        int numberModifiedDurations = random.nextInt(PhaseDurations.size());
        List<Integer> range = IntStream.rangeClosed(0, PhaseDurations.size() - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(range);
        for (int i = 0; i < numberModifiedDurations; i++) {
            int index = range.get(i);
            if (PhaseStates.get(index).indexOf('y') == -1) {
                duration = random.nextInt(90)-25;
                set(index, duration);
            }
        }
        offset += random.nextInt(90)-25;
        if (offset < 3) {
            offset = 3;
        }
        if (offset > 60) {
            offset = 60;
        }
    }/*
    public int mutate(int i) {
		UniformIntegerDistribution x= new UniformIntegerDistribution(0,1);
    	int pid=x.sample();
    	int signe= 2*pid-1;
    	
    	int lambda=30;
    	PoissonDistribution y = new PoissonDistribution(i, lambda);
    	int pd= y.sample();
    	return signe*pd;
    }*/
    private void set(int position, int addedDuration) {
        PhaseDurations.set(position, PhaseDurations.get(position)+addedDuration);
        if (PhaseDurations.get(position) < 25) {
            PhaseDurations.set(position, 25);
        }
        if (PhaseDurations.get(position) > 90) {
            PhaseDurations.set(position, 90);
        }
    }

    // TODO interface pour score
    public String toCSV(){

        StringBuilder s= new StringBuilder(PhaseStates.size() + ",");
        for(String phase: PhaseStates){
            s.append(phase).append(",");
        }
        for(int duration: PhaseDurations){
            s.append(duration).append(",");
        }
        s.append(id).append(",");
        s.append("static,");
        s.append(offset).append(",");
        s.append(programID).append(",");
        s.append(Coordinate.getXY(id));
        return s.toString();
    }
}
