class Main {
    public static void main(String[] args) {
        // Initialize the scanner with the input file
        Scanner S = new Scanner(args[0]);

        // Initialize the parser with the scanner
        Parser P = new Parser(S);

        // Parse the input file and get the parse tree
        Node parseTree;
        try {
            parseTree = P.parseProcedure();
        } catch (ParseException e) {
            System.err.println("Parse error: " + e.getMessage());
            return;
        }

        // Print the parse tree
        printTree(parseTree, 0);

        Executor executor = new Executor(parseTree);
        executor.execute();
    }

    // prints our returned parse tree
    private static void printTree(Node node, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        
        System.out.println(node.name + (node.value != null ? " [" + node.value + "]" : ""));
        
        for (Node child : node.children) {
            printTree(child, level + 1);
        }
    }
}
