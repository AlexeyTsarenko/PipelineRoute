package main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class Node {

    public Node(int id) {
        this.id = id;
    }

    private int id;

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    private Integer length = Integer.MAX_VALUE;

    Map<Node, Integer> adjacentNodes = new HashMap<>();


}
