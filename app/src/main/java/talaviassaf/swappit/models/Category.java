package talaviassaf.swappit.models;

@SuppressWarnings("unused")
class Category {

    private final String hebrew;
    private final String type;
    private final int id;

    public Category(String hebrew, int id, String type) {

        this.hebrew = hebrew;
        this.type = type;
        this.id = id;
    }

    public String getHebrew() {

        return hebrew;
    }

    public int getId() {

        return id;
    }

    public String getType() {
        return type;
    }
}
