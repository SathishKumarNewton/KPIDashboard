package com.prodian.rsgirms.dashboard.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

@Controller
public class MotorDashboardController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private KpiDashboardService kpiDashboardService;

	//private Connection connection = null;
	
	@Autowired
	private UserMatrixService userMatrixService;
	
	@Autowired
	private UserDashboardRepository userDashboardRepository;
	
	
	@GetMapping("/motorRollOverRate")
	public ModelAndView getKpiDashBoard() {
		ModelAndView model = new ModelAndView("motorRollOverRate");

//		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
		KpiFiltersResponse res = new KpiFiltersResponse();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		
		List<Integer> dashboardIds = userDashboardRepository.findByUserId(userId).stream().map(UserDashboard::getDashboardId)
				.collect(Collectors.toList());
		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
		req.setDashboardId(dashboardIds);
		UserMatrixMasterRequest filterRequest = userMatrixService.getUserMatrixChildByUserId(userId,req);
		model.addObject("filters",filterRequest);
		model.addObject("channels",res.getChannelMasters());
		model.addObject("businessTypes",res.getBusinessTypeMasters());
		model.addObject("campaigns",res.getCampaignMasters());
		model.addObject("subChannels",res.getSubChannelMasters());
		model.addObject("finMonths",res.getFinMonthMasters());
		model.addObject("finYears",res.getFinYearMasters());
		//model.addObject("makes",res.getMakeMasters());
		List<ModelMaster> models = res.getModelMasters();
		model.addObject("models",(models==null||models.isEmpty())?"":models.stream().map(ModelMaster::getModel).collect(Collectors.toSet()));
		model.addObject("makes",(models==null||models.isEmpty())?"":models.stream().map(ModelMaster::getMake).collect(Collectors.toSet()));
		model.addObject("modelGroups",(models==null||models.isEmpty())?"":models.stream().map(ModelMaster::getModelGroup).collect(Collectors.toSet()));
		model.addObject("modelClassifications",(models==null||models.isEmpty())?"":models.stream().map(ModelMaster::getModelClassification).collect(Collectors.toSet()));
		
		model.addObject("oas",res.getOaMasters());
		model.addObject("policyCategories",res.getPolicyCategoryMasters());
		model.addObject("policyTypes",res.getPolicyTypeMasters());
		
		List<ProductMaster> products = res.getProductMasters();
		model.addObject("products",products);
		List<ProductMaster> motorProducts = (products==null || products.isEmpty()) ? new ArrayList<ProductMaster>() 
			:products.stream().filter(p->(p.getProductCode().equalsIgnoreCase("VGC")
			||p.getProductCode().equalsIgnoreCase("VPC")
			||p.getProductCode().equalsIgnoreCase("VMC")
			||p.getProductCode().equalsIgnoreCase("VOC"))).collect(Collectors.toList());
		List<ProductMaster> healthProducts = (products==null || products.isEmpty()) ? new ArrayList<ProductMaster>() 
				:products.stream().filter(p->(p.getProductCode().equalsIgnoreCase("AME")
				||p.getProductCode().equalsIgnoreCase("IHP")
				||p.getProductCode().equalsIgnoreCase("APA")
				||p.getProductCode().equalsIgnoreCase("AHC")
				||p.getProductCode().equalsIgnoreCase("BMG")
				||p.getProductCode().equalsIgnoreCase("AMC"))).collect(Collectors.toList());
		model.addObject("motorProducts",motorProducts);
		model.addObject("healthProducts",healthProducts);
		model.addObject("subLines",res.getSublineMasters());
		model.addObject("branches",res.getBranchMasters());
		model.addObject("cities",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getRaDescription).collect(Collectors.toSet()));
		model.addObject("regions",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getRegion).collect(Collectors.toSet()));
		model.addObject("states",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getStateNew).collect(Collectors.toSet()));
		model.addObject("clusters",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getClusterName).collect(Collectors.toSet()));
		model.addObject("subClusters",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getSubCluster).collect(Collectors.toSet()));
		model.addObject("zones",(res.getBranchMasters()==null||res.getBranchMasters().isEmpty())?new ArrayList<BranchMaster>():res.getBranchMasters().stream().map(BranchMaster::getZone).collect(Collectors.toSet()));
		model.addObject("userName",user.getName() + " " + user.getLastName());
	
		//KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
		
		/*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		int userId = user.getId();
		
		List<Integer> dashboardIds = userDashboardRepository.findByUserId(userId).stream().map(UserDashboard::getDashboardId)
				.collect(Collectors.toList());
		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
		req.setDashboardId(dashboardIds);
		UserMatrixMasterRequest filterRequest = userMatrixService.getUserMatrixChildByUserId(userId,req);
//		res.getChannelMasters().stream().filter(f->filterRequest.getChannels())
		model.addObject("channels",res.getChannelMasters());
		model.addObject("businessTypes",res.getBusinessTypeMasters());
		model.addObject("campaigns",res.getCampaignMasters());
		model.addObject("subChannels",res.getSubChannelMasters());
		model.addObject("finMonths",res.getFinMonthMasters());
		model.addObject("finYears",res.getFinYearMasters());
		//model.addObject("makes",res.getMakeMasters());
		List<ModelMaster> models = res.getModelMasters();
		model.addObject("models",models.stream().map(ModelMaster::getModel).collect(Collectors.toSet()));
		model.addObject("makes",models.stream().map(ModelMaster::getMake).collect(Collectors.toSet()));
		model.addObject("modelGroups",models.stream().map(ModelMaster::getModelGroup).collect(Collectors.toSet()));
		model.addObject("modelClassifications",models.stream().map(ModelMaster::getModelClassification).collect(Collectors.toSet()));
		
		model.addObject("oas",res.getOaMasters());
		model.addObject("policyCategories",res.getPolicyCategoryMasters());
		model.addObject("policyTypes",res.getPolicyTypeMasters());
		List<ProductMaster> products = res.getProductMasters();
		model.addObject("products",products);
		List<ProductMaster> motorProducts = products.stream().filter(p->(p.getProductCode().equalsIgnoreCase("VGC")
													||p.getProductCode().equalsIgnoreCase("VPC")
													||p.getProductCode().equalsIgnoreCase("VMC")
													||p.getProductCode().equalsIgnoreCase("VOC"))).collect(Collectors.toList());
		List<ProductMaster> healthProducts = products.stream().filter(p->(p.getProductCode().equalsIgnoreCase("AME")
				||p.getProductCode().equalsIgnoreCase("IHP")
				||p.getProductCode().equalsIgnoreCase("APA")
				||p.getProductCode().equalsIgnoreCase("AHC")
				||p.getProductCode().equalsIgnoreCase("BMG")
				||p.getProductCode().equalsIgnoreCase("AMC"))).collect(Collectors.toList());
		model.addObject("motorProducts",motorProducts);
		model.addObject("healthProducts",healthProducts);
		model.addObject("subLines",res.getSublineMasters());
		model.addObject("branches",res.getBranchMasters());
		model.addObject("cities",res.getBranchMasters().stream().map(BranchMaster::getRaDescription).collect(Collectors.toSet()));
		model.addObject("regions",res.getBranchMasters().stream().map(BranchMaster::getRegion).collect(Collectors.toSet()));
		model.addObject("states",res.getBranchMasters().stream().map(BranchMaster::getStateNew).collect(Collectors.toSet()));
		model.addObject("clusters",res.getBranchMasters().stream().map(BranchMaster::getClusterName).collect(Collectors.toSet()));
		model.addObject("subClusters",res.getBranchMasters().stream().map(BranchMaster::getSubCluster).collect(Collectors.toSet()));
		model.addObject("zones",res.getBranchMasters().stream().map(BranchMaster::getZone).collect(Collectors.toSet()));
		model.addObject("userName",user.getName() + " " + user.getLastName());*/
		return model;
	}
	
}
