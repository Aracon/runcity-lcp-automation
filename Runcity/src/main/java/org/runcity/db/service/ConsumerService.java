package org.runcity.db.service;

import java.util.List;

import org.runcity.db.entity.Consumer;
import org.runcity.exception.DBException;
import org.springframework.security.access.annotation.Secured;

public interface ConsumerService {
	@Secured("ROLE_ADMIN")
	public List<Consumer> selectAll();

	public Consumer selectByUsername(String username);

	public Consumer selectByEmail(String email);
	
	public Consumer selectById(Long id);
	
	@Secured("ROLE_ADMIN")
	public Consumer add(Consumer c) throws DBException;
	
	@Secured("ROLE_ADMIN")
	public Consumer update(Consumer c) throws DBException;
		
	@Secured("ROLE_ADMIN")
	public void delete(List<Long> id) throws DBException;
	
	public boolean validatePassword(Consumer c, String password);
	
	public Consumer register(String username, String password, String credentials, String email, String locale) throws DBException;
	
	public Consumer getCurrent();
	
	public Consumer updateCurrentData(String username, String credentials, String email, String locale);

	public Consumer updateCurrentPassword(String newPassword) throws DBException;
	
	@Secured("ROLE_ADMIN")
	public List<Consumer> updatePassword(List<Long> id, String newPassword) throws DBException;
}