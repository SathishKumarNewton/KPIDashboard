package com.prodian.rsgirms.dashboard.response;

import java.util.List;

import com.prodian.rsgirms.userapp.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyResponse {

	private String policyNo;
	
	private String endtno;
	
	private String productCode;

	private String monthFlag;
	
	private String finYear;
	
	private double policyCount;
	
	private double gwpOurShare;
	

}
