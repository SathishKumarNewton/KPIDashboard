
package com.prodian.rsgirms.userapp.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zakir Hussain Syed
 * @created June 13, 2020 03:34:23 PM
 * @version 1.0
 * @filename Role.java
 * @package com.prodian.rsgirms.userapp.model 
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;
	
    @Column(name = "role")
    private String role;

}
