package nl.bentels.loa.simulapms.model.person;

public class NoSuchPersonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String      id;

    public NoSuchPersonException(final String id) {
        super(String.format("Person with id %s does not exist", id));
        this.id = id;
    }

    public String getNonExistentId() {
        return id;
    }

}
