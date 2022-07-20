package com.prodian.rsgirms.dashboard.response;

import java.util.List;

import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.BusinessTypeMaster;
import com.prodian.rsgirms.dashboard.model.BusinessTypeMasterNow;
import com.prodian.rsgirms.dashboard.model.CampaignMaster;
import com.prodian.rsgirms.dashboard.model.ChannelMaster;
import com.prodian.rsgirms.dashboard.model.ChannelMasterNow;
import com.prodian.rsgirms.dashboard.model.FinMonthMaster;
import com.prodian.rsgirms.dashboard.model.FinYearMaster;
import com.prodian.rsgirms.dashboard.model.FuelTypeMasterNow;
import com.prodian.rsgirms.dashboard.model.IntermediaryMaster;
import com.prodian.rsgirms.dashboard.model.MakeMaster;
import com.prodian.rsgirms.dashboard.model.MakeMasterNow;
import com.prodian.rsgirms.dashboard.model.ModelGroupMasterNow;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.OaMaster;
import com.prodian.rsgirms.dashboard.model.PolicyCategoryMaster;
import com.prodian.rsgirms.dashboard.model.PolicyTypeMaster;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.model.StateGroupMasterNow;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;
import com.prodian.rsgirms.dashboard.model.SubChannelMasterNow;
import com.prodian.rsgirms.dashboard.model.SublineMaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiFiltersResponse {
	
	private List<CampaignMaster> campaignMasters;
	private List<BusinessTypeMaster> businessTypeMasters;
	private List<FinMonthMaster> finMonthMasters;
	private List<FinYearMaster> finYearMasters;
	private List<MakeMaster> makeMasters;
	private List<ModelMaster> modelMasters;
	private List<OaMaster> oaMasters;
	private List<PolicyCategoryMaster> policyCategoryMasters;
	private List<PolicyTypeMaster> policyTypeMasters;
	private List<ProductMaster> productMasters;
	//private List<ChannelMaster> channelMasters;
	private List<String> channelMasters;
	//private List<SubChannelMaster> subChannelMasters;
	private List<String> subChannelMasters;
	private List<SublineMaster> sublineMasters;
	private List<BranchMaster> branchMasters;
	private List<IntermediaryMaster> intermediaryMasters;
	
	private List<ChannelMasterNow> channelMastersNow;
	private List<SubChannelMasterNow> subChannelMastersNow;
	private List<MakeMasterNow> makeMastersNow;
	private List<ModelGroupMasterNow> modelGroupMastersNow;
	private List<BusinessTypeMasterNow> businessTypeMastersNow;
	private List<FuelTypeMasterNow> fuelTypeMastersNow;
	private List<StateGroupMasterNow> stateGroupMastersNow;

}
