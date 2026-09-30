package lv.turiba.library.service;

/** Thrown when a record with the requested id does not exist. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " was not found");
    }
}
