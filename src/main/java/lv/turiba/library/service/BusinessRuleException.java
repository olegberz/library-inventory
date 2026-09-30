package lv.turiba.library.service;

/**
 * Thrown when an operation breaks a business rule
 * (duplicate ISBN, no free copies, record still in use, ...).
 * "field" names the form field the message belongs to, or is null for a general message.
 */
public class BusinessRuleException extends RuntimeException {

    private final String field;

    public BusinessRuleException(String message) {
        this(null, message);
    }

    public BusinessRuleException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
