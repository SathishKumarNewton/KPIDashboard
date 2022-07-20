
package com.prodian.rsgirms.dashboard.service;

import com.prodian.rsgirms.dashboard.response.GwpResponse;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;

/**
 * @author S. Mohamed ismaiel
 * @created Sep 129, 2020 03:32:53 PM
 * @version 1.0
 * @filename KpiDashboardService.java
 * @package com.prodian.rsgirms.dashboard.service
 */

public interface KpiDashboardService {

	public GwpResponse getFiltersForDropdown();

	public KpiFiltersResponse getKpiFilters();

}
