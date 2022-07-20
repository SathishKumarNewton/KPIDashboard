
package com.prodian.rsgirms.reports.gwp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prodian.rsgirms.dashboard.response.GwpResponse;
import com.prodian.rsgirms.reports.gwp.service.impl.GWPReportServiceImpl;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename GWPReportController.java
 * @package com.prodian.rsgirms.reports.gwp.controller
 */

@Controller
public class GWPReportController {

	@Autowired
	private GWPReportServiceImpl service;

	// @GetMapping("/getFilters")
	@RequestMapping(value = "/getFilters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GwpResponse getFiltersForDropdown() {
//		GwpResponse gwpResponse = service.getFiltersForDropdown();
		GwpResponse gwpResponse = new GwpResponse();
		try {
			gwpResponse = service.getFiltersForDropDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gwpResponse;
	}

}
