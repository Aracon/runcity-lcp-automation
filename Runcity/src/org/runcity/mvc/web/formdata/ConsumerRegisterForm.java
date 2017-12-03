package org.runcity.mvc.web.formdata;

import org.apache.log4j.Logger;
import org.runcity.db.service.ConsumerService;
import org.runcity.mvc.web.util.*;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Errors;

public class ConsumerRegisterForm extends AbstractForm {
	private static final Logger logger = Logger.getLogger(ConsumerRegisterForm.class);

	private FormStringColumn username;
	private FormStringColumn credentials;
	private FormStringColumn password;
	private FormStringColumn password2;
	private FormStringColumn email;

	public ConsumerRegisterForm() {
		super("consumerRegisterForm", null, "/register", null);
		logger.trace("Creating form " + getFormName());
		setTitle("register.header");
		this.username = new FormPlainStringColumn(this, new ColumnDefinition("username", "user.username"), true, 4, 32);
		this.credentials = new FormPlainStringColumn(this, new ColumnDefinition("credentials", "user.credentials"),
				true, 4, 32);

		FormPasswordPair passwords = new FormPasswordPair(
				new FormPasswordColumn(this, new ColumnDefinition("password", "user.password"), true),
				new FormPasswordConfirmationColumn(this, new ColumnDefinition("password2", "user.password2"),
						true));

		this.password = passwords.getPassword();
		this.password2 = passwords.getPasswordConfirmation();

		this.email = new FormEmailColumn(this, new ColumnDefinition("email", "user.email"), true, 255);
	}

	public ConsumerRegisterForm(String username, String credentials, String password, String password2, String email) {
		this();
		setUsername(username);
		setCredentials(credentials);
		setPassword(password);
		setPassword2(password2);
		setEmail(email);
	}

	public String getPassword() {
		return password.getValue();
	}

	public void setPassword(String password) {
		this.password.setValue(password);
	}

	public String getPassword2() {
		return password2.getValue();
	}

	public void setPassword2(String password2) {
		this.password2.setValue(password2);
	}

	public String getUsername() {
		return username.getValue();
	}

	public void setUsername(String username) {
		this.username.setValue(username);
	}

	public String getCredentials() {
		return credentials.getValue();
	}

	public void setCredentials(String credentials) {
		this.credentials.setValue(credentials);
	}

	public String getEmail() {
		return email.getValue();
	}

	public void setEmail(String email) {
		this.email.setValue(email);
	}

	public FormStringColumn getUsernameColumn() {
		return username;
	}

	public FormStringColumn getPasswordColumn() {
		return password;
	}

	public FormStringColumn getPassword2Column() {
		return password2;
	}

	public FormStringColumn getCredentialsColumn() {
		return credentials;
	}

	public FormStringColumn getEmailColumn() {
		return email;
	}

	@Override
	public void validate(ApplicationContext context, Errors errors) {
		logger.debug("Validating " + getFormName());
		username.validate(errors);
		credentials.validate(errors);
		password.validate(errors);
		password2.validate(errors);
		email.validate(errors);

		ConsumerService consumerService = context.getBean(ConsumerService.class);
		if (consumerService.selectByUsername(username.getValue()) != null) {
			logger.debug(username.getName() + " is not unique");
			errors.rejectValue(username.getName(), "validation.userExists");
		}

		if (consumerService.selectByEmail(email.getValue()) != null) {
			logger.debug(email.getName() + " is not unique");
			errors.rejectValue(email.getName(), "validation.emailExists");
		}
	}
}
