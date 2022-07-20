
package com.prodian.rsgirms.reports.gwp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.reports.gwp.model.MakeFilter;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:40:15 PM
 * @version 1.0
 * @filename MakeFilterRepository.java
 * @package com.prodian.rsgirms.reports.gwp.repository
 */

@Repository
public interface MakeFilterRepository extends JpaRepository<MakeFilter, String> {

}
