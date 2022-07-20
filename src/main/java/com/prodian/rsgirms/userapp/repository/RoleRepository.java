
package com.prodian.rsgirms.userapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.userapp.model.Role;

/**
 * @author CSS
 *
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
	Role findByRole(String role);

}
