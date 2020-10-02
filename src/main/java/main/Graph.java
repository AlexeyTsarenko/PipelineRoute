package main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class Graph {
    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }
}
