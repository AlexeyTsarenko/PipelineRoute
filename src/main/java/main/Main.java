package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {
    static List<String[]> relationsList;
    static List<String[]> taskList;

    public static void main(String[] args) throws IOException, SQLException {

        while (true) {
            System.out.println("1: Upload files");
            System.out.println("2: Calculate distance");
            Scanner sc = new Scanner(System.in);
            int var = sc.nextInt();
            switch (var) {
                case (1):
                    readTasks();
                    readData();
                    insertIntoDB();
                    break;
                case (2):
                    calculateDistance();
                    break;
            }
        }


           /* readTasks();
        for (String[] str : taskList) {
            readData();
            insertIntoDB();
            Graph graph = new Graph();
            List<Node> nodeList = createNodes();
            for (String[] strArr : relationsList) {
                createRelations(strArr, nodeList);
            }
            nodeList.forEach(graph::addNode);

            Node from = findNodeById(Integer.parseInt(str[0]), nodeList);
            calculateShortestPathFromSource(graph, from);
            Node to = findNodeById(Integer.parseInt(str[1]), nodeList);
            if(to.getLength() == 2147483647){
                System.out.println(false);
            }else{
                System.out.println("distance from " + str[0] + " to " + str[1] + " is " + to.getLength());
            }
        }*/
    }

    private static void calculateDistance() {
        for (String[] str : taskList) {

            Graph graph = new Graph();
            List<Node> nodeList = createNodes();
            for (String[] strArr : relationsList) {
                createRelations(strArr, nodeList);
            }
            nodeList.forEach(graph::addNode);

            Node from = findNodeById(Integer.parseInt(str[0]), nodeList);
            calculateShortestPathFromSource(graph, from);
            Node to = findNodeById(Integer.parseInt(str[1]), nodeList);
            if (to.getLength() == 2147483647) {
                System.out.println(false);
            } else {
                System.out.println("distance from " + str[0] + " to " + str[1] + " is " + to.getLength());
            }
        }
    }

    private static void createRelations(String[] strArr, List<Node> nodeList) {
        Node from = findNodeById(Integer.parseInt(strArr[0]), nodeList);
        Node to = findNodeById(Integer.parseInt(strArr[1]), nodeList);
        from.addDestination(to, Integer.parseInt(strArr[2]));
    }


    private static List<Node> createNodes() {
        List<Node> nodeList = new ArrayList<>();
        for (String[] strArr : relationsList) {
            int a = Integer.parseInt(strArr[0]);
            int b = Integer.parseInt(strArr[1]);
            addNodeIfNotExist(a, nodeList);
            addNodeIfNotExist(b, nodeList);
        }
        return nodeList;
    }

    private static void addNodeIfNotExist(int a, List<Node> nodeList) {
        Node node = findNodeById(a, nodeList);
        if (node == null) {
            nodeList.add(new Node(a));
        }
    }

    private static Node findNodeById(int a, List<Node> nodeList) {
        for (Node node : nodeList) {
            if (node.getId() == a) {
                return node;
            }
        }
        return null;
    }

    private static void insertIntoDB() throws SQLException {
        String url = "jdbc:h2:mem:";
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:~/test", "sa", "")) {
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE `relations`\n" +
                    "(\n" +
                    "    `id`         integer     NOT NULL AUTO_INCREMENT,\n" +
                    "    `surce_id`   integer     NOT NULL,\n" +
                    "    `dest_id`    integer     NOT NULL,\n" +
                    "    `length`     integer     NOT NULL,\n" +
                    "\n" +
                    "\n" +
                    "    PRIMARY KEY (`id`)\n" +
                    ")\n");


            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO relations VALUES (default, ?, ?, ?);");
            for (String[] strings : relationsList) {

                preparedStatement.setInt(1, Integer.parseInt(strings[0]));
                preparedStatement.setInt(2, Integer.parseInt(strings[1]));
                preparedStatement.setInt(3, Integer.parseInt(strings[2]));
                preparedStatement.execute();
            }
        }
    }

    private static void readData() throws IOException {
        relationsList = new ArrayList<>();
        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader("file1.csv"));
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(";");
            relationsList.add(data);
        }
        csvReader.close();
    }

    private static void readTasks() throws IOException {
        taskList = new ArrayList<>();
        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader("file2.csv"));
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(";");
            taskList.add(data);
        }
        csvReader.close();
    }

    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setLength(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {         //method will run til i have unsettled nodes
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair :             //going through all connected nodes of current node
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {           //if this node is unvisited
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);                 //new node is going to be visited
                }
            }
            settledNodes.add(currentNode);                            //old node is going to blacklist
        }
        return graph;
    }

    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {  /*returns the node with the
                                                                             lowest distance from the unsettled nodes set*/
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node : unsettledNodes) {
            int nodeDistance = node.getLength();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode,                             //  compares the actual distance with the newly
                                                 Integer edgeWeigh, Node sourceNode) {            // calculated one while following the newly explored path:
        Integer sourceDistance = sourceNode.getLength();
        if (sourceDistance + edgeWeigh < evaluationNode.getLength()) {
            evaluationNode.setLength(sourceDistance + edgeWeigh);
        }
    }
}
