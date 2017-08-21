package eugene.board.model;

public class Column {

    private String name;
    private int limit;

    public Column(String name) {
        this.name = name;
        this.limit = Integer.MAX_VALUE;
    }

    public Column(String name, int limit) {
        this.name = name;
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

}
