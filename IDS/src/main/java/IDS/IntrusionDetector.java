package IDS;

import org.pcap4j.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class IntrusionDetector {
    private final List<Rule> rules = new ArrayList<>();

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public void detectIntrusion(Packet packet) {
        for (Rule rule : rules) {
            if (rule.evaluate(packet)) {
                System.out.println("ALERT: Rule violation detected! " + rule.getDescription());

            }
        }
    }
}
