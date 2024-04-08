import java.util.ArrayList;


// implementation of node class for building my parse tree //

public class Node {
    String name;
    String value;
    ArrayList<Node> children;

    Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    Node(String name, String value) {
        this(name);
        this.value = value;
    }

    // adds new Node to list of children, uses add utility of ArrayLists (I love array lists) //
  
    void addChild(Node child) {
        children.add(child);
    }
}