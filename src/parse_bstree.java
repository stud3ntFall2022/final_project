import java.util.Stack;

public class parse_bstree {

    public static Node root;
    public static int result = 0;

    static class Node
    {
        Node left;
        Node right;
        Character data;
        int height;

        public Node(Character data)
        {
            this.left = null;
            this.right = null;
            this.data = data;
            this.height = 0;
        }

        public void setRootVal(Character val)
        {
            this.data= val;
        }
    }
    public Character getNodeValue(Node input)
    {

        return input.data;
    }

    public Node insertLeft(Node node, Character value)
    {
        if (node.left == null)
            node.left = new Node(value);
        else
            insertLeft(node.left, value);
        return node.left;
    }
    public Node insertRight(Node node, Character value)
    {
        if (node.right == null)
            node.right = new Node(value);
        else
            insertRight(node.right, value);
        return node.right;
    }
    public void insertNodes(Node node)
    {
        if (node != null) {
            if (node.right == null && (node.data == '-'))
                node.right = new Node('0');
            if (node.left == null && (node.data == '-'))
                node.left = new Node('0');

            insertNodes(node.right);
            insertNodes(node.left);
        }
    }

    public int countNode(Node node){
        if(node == null)
            return 0;
        return 1 + countNode(node.left) + countNode(node.right);
    }
    public void displayTree(Node node){
        if(node == null)
            return;
        else {
            displayTree(node.left);
            displayTree(node.right);
            System.out.println(" "+node.data);
        }
    }
    boolean isBalanced(Node node)
    {
        int leftHeight, rightHeight;
        if (node == null)
            return true;

        leftHeight = height(node.left);
        rightHeight = height(node.right);

        if (Math.abs(leftHeight - rightHeight) <= 1 && isBalanced(node.left) && isBalanced(node.right))
            return true;
        return false;
    }

    public int height(Node node)
    {
        if (node == null)
            return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public void updateTreeHeight(Node node)
    {
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        node.height = Math.max(leftHeight, rightHeight) + 1;
    }

    public int balanceCheck(Node node)
    {
        return height(node.right) - height(node.left);
    }

    private Node rightRotation(Node node)
    {
        Node left = node.left;

        node.left = left.right;
        left.right = node;

        updateTreeHeight(node);
        updateTreeHeight(left);

        return left;
    }
    public Node leftRotation(Node node)
    {
        Node right = node.right;

        node.right= right.left;
        right.left = node;

        updateTreeHeight(node);
        updateTreeHeight(right);

        return right;
    }
    public Node rebalanceTree(Node node)
    {
        int heightDiff = balanceCheck(node);
       //tree is balanced on the left side
        if (heightDiff < -1)
        {
            if (balanceCheck(node.left) <= 0) {
                node = rightRotation(node);
            } else {
                node.left = leftRotation(node.left);
                node = rightRotation(node);
            }
        }
        return node;
    }

    public int computeTree(Node root)
    {
        Node left = getLeftChild(root);
        Node right = getRightChild(root);

        if (left != null && right != null)
        {
            switch (root.data)
            {
                case '+':
                    return addnumbers(computeTree(root.left), computeTree(root.right));
                case '*':
                    return multnumbers(computeTree(root.left), computeTree(root.right));
                case '-':
                    return subnumbers(computeTree(root.left), computeTree(root.right));
            }
        }
        return root.data;
    }
    public static Node getRoot()
    {
        return root;
    }
    public void setRoot(Node insert)
    {
        this.root = insert;
    }

    public static Node getLeftChild(Node current)
    {
        if (current.left == null)
            return current;
        else
            current = getLeftChild(current.left);

        return current;
    }
    public static Node getRightChild(Node current)
    {
        if (current.right == null)
            return current;
        else
            current = getRightChild(current.right);

        return current;
    }
    public parse_bstree createTree(String input)
    {
        Node current = null;
        parse_bstree parseTree = new parse_bstree();
        Stack<Node> myStack = new Stack<Node>();
        Node root = new Node('R');
        current = root;
        myStack.push(current);

        for (int i = 0; i < input.length(); i ++)
        {
            if(input.charAt(i) == '(') {
                if (current.left != null) {
                    myStack.push(current);
                    current = parseTree.insertRight(current, null);
                } else{
                    myStack.push(current);
                    current = parseTree.insertLeft(current, null);
                }
            }
            else if(input.charAt(i) == '+' || input.charAt(i) == '*' || input.charAt(i) == '/' || input.charAt(i) == '-')
            {
                current.setRootVal(input.charAt(i));
                myStack.push(current);
                if( current.left == null)
                    current = parseTree.insertLeft(current,';');
                else
                    current = parseTree.insertRight(current,';');
            }else if(input.charAt(i) != '+' && input.charAt(i) != '*' && input.charAt(i) != '/' && input.charAt(i) != ')')
            {
                current.setRootVal(input.charAt(i));
                current = myStack.pop();
            }else if(input.charAt(i) == ')')
                current = myStack.pop();
        }
        int result = 0;
        if (isBalanced(root)) {
            result = computeTree(root);
        }else {
            while(balanceCheck(root)!=0) {
                Node new_root = rebalanceTree(root);
                root = rebalanceTree(new_root);
            }
            displayTree(root);
            insertNodes(root);

            result = computeTree(root);
        }

        setRoot(root);

        return parseTree;
    }
    public static int addnumbers(int one, int two)
    {
        if (Character.isDigit(one) && Character.isDigit(two))
        {
            int first = Character.getNumericValue(one);
            int second = Character.getNumericValue(two);
            result += first + second;

        } else if (Character.isDigit(one) || Character.isDigit(two))
        {
            if (Character.isDigit(one))
            {
                int first = Character.getNumericValue(one);
                int second = Character.getNumericValue(two);
                result += first;
            }else
            {
                if (Character.isDigit(two))
                {
                    int second = Character.getNumericValue(two);
                    result += second;
                }
            }
            int first = Character.getNumericValue(one);
            int second = Character.getNumericValue(two);
        }
        int first = Character.getNumericValue(one);
        int second = Character.getNumericValue(two);

        return result;
    }
    public static int subnumbers(int one, int two)
    {
        if (Character.isDigit(one) && Character.isDigit(two) ) {
            int first = Character.getNumericValue(one);
            int second = Character.getNumericValue(two);
            result -= first-second;
        }else if (Character.isDigit(one) || Character.isDigit(two)) {
            if (Character.isDigit(one)) {
                int first = Character.getNumericValue(one);
                result -= first;
            } else {
                if (Character.isDigit(two)) {
                    int second = Character.getNumericValue(two);
                    result -= second;
                }
            }
        }
        return result;
    }
    public static int multnumbers(int one, int two)
    {
        if (Character.isDigit(one) && Character.isDigit(two) ) {
            int first = Character.getNumericValue(one);
            int second = Character.getNumericValue(two);
            result = first*second;
        }else if (Character.isDigit(one) || Character.isDigit(two)) {
            if (Character.isDigit(one)) {
                int first = Character.getNumericValue(one);
                result *= first;
            } else {
                if (Character.isDigit(two)) {
                    int second = Character.getNumericValue(two);
                    result *= second;
                }
            }
        }
        return result;
    }
}
