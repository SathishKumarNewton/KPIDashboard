
package com.prodian.rsgirms.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.scheduler.model.Cubes;

/**
 * @author CSS
 *
 */

@Repository
public interface CubeRepository extends JpaRepository<Cubes, Integer> {

	List<Cubes> findByStatus(String string);
    

}
