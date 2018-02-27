package org.runcity.mvc.rest;

import java.util.Locale;

import org.runcity.mvc.rest.util.RestPostResponseBody;
import org.runcity.mvc.rest.util.RestResponseClass;
import org.runcity.mvc.validator.FormValidator;
import org.runcity.mvc.web.formdata.AbstractForm;
import org.runcity.util.DynamicLocaleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public abstract class AbstractRestController {
	@Autowired
	protected FormValidator formValidator;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected DynamicLocaleList localeList;
	
	protected Locale locale = LocaleContextHolder.getLocale();
	
	protected Errors validateForm(AbstractForm form, RestPostResponseBody result) {
		Errors errors = new BindException(form, form.getFormName());
		if (formValidator.supports(form.getClass())) {
			formValidator.validate(form, errors);
		}
		if (errors.hasErrors()) {
			result.setResponseClass(RestResponseClass.ERROR);
			result.parseErrors(errors);
		}
		return errors;
	}
}
