package org.runcity.mvc.web.util;

import org.runcity.mvc.web.formdata.AbstractForm;
import org.springframework.validation.Errors;

public class FormColorPickerColumn extends FormStringColumn {
	public FormColorPickerColumn(AbstractForm form, ColumnDefinition definition, boolean required) {
		super(form, definition, required, 6, 6);
	}

	public FormColorPickerColumn(AbstractForm form, ColumnDefinition definition, boolean required, String value) {
		super(form, definition, required, 6, 6, value);
	}

	@Override
	public void validate(Errors errors) {
		super.validate(errors);
	}
}
