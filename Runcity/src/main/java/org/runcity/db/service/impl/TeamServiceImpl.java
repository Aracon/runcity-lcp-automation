package org.runcity.db.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.runcity.db.entity.ControlPoint;
import org.runcity.db.entity.Event;
import org.runcity.db.entity.Game;
import org.runcity.db.entity.Route;
import org.runcity.db.entity.RouteItem;
import org.runcity.db.entity.Team;
import org.runcity.db.entity.Volunteer;
import org.runcity.db.entity.enumeration.EventStatus;
import org.runcity.db.entity.enumeration.EventType;
import org.runcity.db.entity.enumeration.TeamStatus;
import org.runcity.db.entity.util.TeamRouteItem;
import org.runcity.db.repository.EventRepository;
import org.runcity.db.repository.RouteRepository;
import org.runcity.db.repository.TeamRepository;
import org.runcity.db.service.TeamService;
import org.runcity.exception.DBException;
import org.runcity.util.ResponseBody;
import org.runcity.util.ResponseClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional(rollbackFor = { DBException.class })
public class TeamServiceImpl implements TeamService {
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RouteRepository routeRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	private void initialize(Team t, Team.SelectMode selectMode) {
		if (t == null) {
			return;
		}
		switch (selectMode) {
		case NONE:
			break;
		}
	}
	
	private void initialize(Collection<Team> teams, Team.SelectMode selectMode) {
		if (teams == null || selectMode == Team.SelectMode.NONE) {
			return;
		}
		for (Team t : teams) {
			initialize(t, selectMode);
		}
	}

	@Override
	public Team selectById(Long id, Team.SelectMode selectMode) {
		Team result = teamRepository.findOne(id);
		initialize(result, selectMode);
		return result;
	}

	@Override
	public Team selectByNumberGame(String number, Game game, Team.SelectMode selectMode) {
		Team result = teamRepository.selectByGameAndNumber(number, game);
		initialize(result, selectMode);
		return result;
	}

	@Override
	public TeamRouteItem selectByNumberCP(String number, ControlPoint controlPoint, Team.SelectMode selectMode) {
		TeamRouteItem result = teamRepository.selectByCPAndNumber(number, controlPoint);
		if (result != null) {
			initialize(result.getTeam(), selectMode);
		}
		return result;
	}
	
	@Override
	public Team addOrUpdate(Team team) throws DBException {
		try {
			if (team.getId() != null) {
				Team prev = selectById(team.getId(), Team.SelectMode.NONE);
				prev.update(team);
				return teamRepository.save(prev);
			} else {
				return teamRepository.save(team);
			}
		} catch (Throwable t) {
			throw new DBException(t);
		}
	}

	private void delete(Long id) {
		teamRepository.delete(id);
	}
	
	@Override
	public void delete(List<Long> id) {
		for (Long i : id) {
			delete(i);
		}
	}

	@Override
	public List<Team> selectTeams(Long route, Team.SelectMode selectMode) {
		List<Team> result = selectTeams(routeRepository.findOne(route), selectMode);
		initialize(result, selectMode);
		return result;
	}

	@Override
	public List<Team> selectTeams(Route route, Team.SelectMode selectMode) {
		List<Team> result = teamRepository.findByRoute(route);
		initialize(result, selectMode);
		return result;
	}

	@Override
	public void processTeamByVolunteer(Team team, RouteItem ri, Volunteer volunteer, ResponseBody result) throws DBException {
		MessageSource messageSource = result.getMessageSource();
		Locale locale = result.getCurrentLocale();
		
		Team lock = teamRepository.selectForUpdate(team);
		
		if (!ObjectUtils.nullSafeEquals(team.getStatusData(), lock.getStatusData())) {
			result.setResponseClass(ResponseClass.ERROR);
			result.addCommonMsg("common.concurrencyError");
			return;
		}
		
		if (lock.getStatus() != TeamStatus.ACTIVE) {
			result.setResponseClass(ResponseClass.ERROR);
			result.addCommonMsg("teamProcessing.invalidStatus", TeamStatus.getDisplayName(lock.getStatus(), messageSource, locale));
			return;			
		}
		
		if (!ObjectUtils.nullSafeEquals(lock.getLeg(), ri.getLegNumber())) {
			result.setResponseClass(ResponseClass.ERROR);
			result.addCommonMsg("teamProcessing.invalidLeg", lock.getLeg());
			return;	
		}
		
		Date now = volunteer.now();
		Event pass = new Event(null, EventType.TEAM_CP, EventStatus.POSTED, now, null, volunteer, team);
		lock.setLeg(ri.getLegNumber() + 1);
		
		pass = eventRepository.save(pass);
		lock = teamRepository.save(lock);
		if (pass == null || team == null) {
			throw new DBException();
		}
	}
}
