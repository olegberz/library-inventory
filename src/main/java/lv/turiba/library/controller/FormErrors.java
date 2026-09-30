package lv.turiba.library.controller;

import lv.turiba.library.service.BusinessRuleException;
import org.springframework.validation.BindingResult;

/** Puts a business-rule message on the form: under its field, or at the top of the form. */
final class FormErrors {

    private FormErrors() {
    }

    static void add(BindingResult result, BusinessRuleException e) {
        if (e.getField() != null) {
            result.rejectValue(e.getField(), "business", e.getMessage());
        } else {
            result.reject("business", e.getMessage());
        }
    }
}
