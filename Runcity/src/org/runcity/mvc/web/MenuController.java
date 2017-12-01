package org.runcity.mvc.web;

import org.runcity.mvc.web.tabledata.CategoryTable;
import org.runcity.mvc.web.tabledata.ConsumerTable;
import org.runcity.util.DynamicLocaleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MenuController {
	@Autowired
	private DynamicLocaleList localeList;
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String redirectHome() {
		return "redirect:/secure/home";
	}
	
	@RequestMapping(value = "/secure/home", method = RequestMethod.GET)
	public String home() {
		return "/secure/home";
	}
	
	@RequestMapping(value = "/secure/categories", method = RequestMethod.GET)
	public String categories(Model model) {
		CategoryTable table = new CategoryTable("/api/v1/categoryTable", localeList);
		table.processModel(model);
		
		return "/secure/categories";
	}
	
	@RequestMapping(value = "/secure/users", method = RequestMethod.GET)
	public String users(Model model) {
		ConsumerTable table = new ConsumerTable("/api/v1/consumerTable");
		table.processModel(model);
		
		return "/secure/users";
	}
}
