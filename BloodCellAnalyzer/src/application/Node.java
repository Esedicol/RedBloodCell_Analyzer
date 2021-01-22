package application;

/**
 * Node Object Class
 *
 * @author Emmanuel Sedicol
 *
 */

public class Node {

    private Node parent;
    private int value;
    private int id;
    private boolean isRoot;

    /**
     * Constructor for Node
     * @param value
     */
    public Node(int value, int id){
        this.setValue(value);
        this.setParent(null);
        this.setRoot(false);
        this.setId(id);
    }

    /*
     *-----------------------------------------------
     * Setters and Getters
     * ----------------------------------------------
     */
    public void setValue(int value) {
        if(value == 1 || value == 0)
        this.value = value;
        else
            this.value = 0;
    }

    public int getValue(){
        return this.value;
    }

    public void setParent(Node parent){
        this.parent = parent;
    }

    public Node getParent(){
        return this.parent;
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public void setRoot(boolean root) {
       this.isRoot = root;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
