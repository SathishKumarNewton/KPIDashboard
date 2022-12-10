package com.prodian.rsgirms.dashboard.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RSA_DWH_POLICY_TYPE_NEW_MASTER")
public class PolicyTypeNew {
    @Id
	@Column(name = "POLICY_TYPE_NEW")
	private String policyTypeNew;
}
