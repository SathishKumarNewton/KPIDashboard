
package com.prodian.rsgirms.userapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prodian.rsgirms.userapp.model.User;

/**
 * @author CSS
 *
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	User findByUserName(String userName);

	User findById(Integer userId);

}
