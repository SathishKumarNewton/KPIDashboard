package com.prodian.rsgirms.usermatrix.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.dashboard.service.impl.DashboardServiceImpl;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.model.Branch;
import com.prodian.rsgirms.usermatrix.model.Cluster;
import com.prodian.rsgirms.usermatrix.model.Make;
import com.prodian.rsgirms.usermatrix.model.Model;
import com.prodian.rsgirms.usermatrix.model.Product;
import com.prodian.rsgirms.usermatrix.model.State;
import com.prodian.rsgirms.usermatrix.model.SubChannel;
import com.prodian.rsgirms.usermatrix.model.UserMatrix;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMaster;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterDetail;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.model.UserMatrixRequest;
import com.prodian.rsgirms.usermatrix.model.UserMatrixResponse;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

@Controller
public class UserMatrixController {

	@Autowired
	private UserMatrixService userMatrixService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private DashboardServiceImpl dashboardService;
	
	@Autowired
	private KpiDashboardService kpiDashboardService;
	
	@GetMapping("/admin/olduserMatrix")
	public ModelAndView olduserMatrixView() {
		ModelAndView model = new ModelAndView("admin/userMatrix");
		model.addObject("users", userMatrixService.getAllUsers());
		model.addObject("allUsers", userService.getAllUsers());
		model.addObject("zones", userMatrixService.getAllZones());
		model.addObject("clusters", userMatrixService.getAllClusters());
		model.addObject("states", userMatrixService.getAllStates());
		model.addObject("branches", userMatrixService.getAllBranches());
		model.addObject("roles", userMatrixService.getAllUserMatrixRoles());
		List<Product> products = userMatrixService.getAllProducts();
		Set<String> lobs = products.stream().map(Product::getSegmentNew).collect(Collectors.toSet());
		model.addObject("products", products);
		model.addObject("businessTypes", userMatrixService.getAllBusinessTypes());
		model.addObject("channels", userMatrixService.getAllChannels());
		model.addObject("subChannels", userMatrixService.getAllSubChannels());
		model.addObject("makes", userMatrixService.getAllMakes());
		model.addObject("models", userMatrixService.getAllModels());
		model.addObject("lobs", lobs);
		model.addObject("userMatrixResponseList", userMatrixService.getAllUserMatrixResponse());
		model.addObject("dashboardList", dashboardService.getAllDashboards());
		model.setViewName("admin/userMatrix");
		return model;
	}

	@PostMapping("/admin/saveUserMatrix")
	public String saveUserMatrix(@ModelAttribute UserMatrixRequest request) {
		userMatrixService.saveUserMatrix(request);
		return "redirect:/admin/userMatrix";
	}

	@PostMapping("/admin/saveUserMatrixMaster")
	public String saveUserMatrixMaster(@ModelAttribute UserMatrixMasterRequest request) {
		userMatrixService.saveUserMatrixMaster(request);
		return "redirect:/admin/userMatrix";
	}

	@GetMapping("/admin/getClustersByZoneIds")
	public @ResponseBody Set<Cluster> getClustersByZones(@RequestParam List<String> zoneNames) {
		return userMatrixService.getClustersByZoneIds(zoneNames);
	}

	@GetMapping("/admin/getStatesByClusterIds")
	public @ResponseBody Set<State> getStatesByClusters(@RequestParam List<String> clusters) {
		return userMatrixService.getStatesByClusterIds(clusters);
	}

	@GetMapping("/admin/getBranchesByStateIds")
	public @ResponseBody Set<Branch> getBranchesByStateIds(@RequestParam List<String> states) {
		return userMatrixService.getBranchesByStateIds(states);
	}

	@GetMapping("/admin/allUserMatrix")
	public @ResponseBody List<UserMatrix> allUserMatrix() {
		return userMatrixService.getAllUserMatrix();
	}

	@GetMapping("/admin/userMatrixByUserId")
	public @ResponseBody List<UserMatrixMasterDetail> userMatrixByUserId(@RequestParam Integer userId) {
		return userMatrixService.userMatrixByUserId(userId);
	}

	/*@GetMapping("/admin/getAllModels")
	public @ResponseBody List<Model> getAllModels() {
		return userMatrixService.getAllModels();
	}*/

	@GetMapping("/admin/getAllMakes")
	public @ResponseBody List<Make> getAllMakes() {
		return userMatrixService.getAllMakes();
	}

	@GetMapping("/admin/getSubchannelsByChannels")
	public @ResponseBody Set<SubChannel> getSubchannelsByChannels(@RequestParam List<String> channelNames) {
		return userMatrixService.getSubchannelsByChannels(channelNames);
	}

	@GetMapping("/admin/getModelByMakes")
	public @ResponseBody Set<Model> getModelByMakes(@RequestParam List<String> makes) {
		return userMatrixService.getModelByMakes(makes);
	}

	@PostMapping("/admin/deleteUserMatrix")
	public @ResponseBody void deleteUserMatrix(@RequestBody List<UserMatrixMaster> matrices) {
		userMatrixService.deleteUserMatrix(matrices);
	}

	@GetMapping("/admin/getUserMatrixChildByUserId")
	public @ResponseBody UserMatrixMasterRequest getUserMatrixChildByUserId(@RequestParam Integer userId) {
		return userMatrixService.getUserMatrixChildByUserId(userId,null);
	}
	
	@GetMapping("/admin/userMatrixResponseByDashboardId")
	public @ResponseBody List<UserMatrixResponse> getUserMatrixResponse(@RequestParam Integer dashboardId){
		if(dashboardId == null || dashboardId <=0) {
			return null;
		}
		return userMatrixService.getAllUserMatrixResponseByDashboardId(dashboardId);
	}
	
	@GetMapping("/admin/userMatrix")
	public ModelAndView userMatrixView() {
		ModelAndView model = new ModelAndView("admin/matrix");
//		model.addObject("users", userMatrixService.getAllUsers());
		model.addObject("users", userService.getAllUsers());
		model.addObject("allUsers", userService.getAllUsers());
//		model.addObject("zones", userMatrixService.getAllZones());
//		model.addObject("clusters", userMatrixService.getAllClusters());
//		model.addObject("states", userMatrixService.getAllStates());
//		model.addObject("branches", userMatrixService.getAllBranches());
		model.addObject("roles", userMatrixService.getAllUserMatrixRoles());
		List<Product> productsOld = userMatrixService.getAllProducts();
		Set<String> lobs = productsOld.stream().map(Product::getSegmentNew).collect(Collectors.toSet());
//		model.addObject("products", products);
//		model.addObject("businessTypes", userMatrixService.getAllBusinessTypes());
//		model.addObject("channels", userMatrixService.getAllChannels());
//		model.addObject("subChannels", userMatrixService.getAllSubChannels());
//		model.addObject("makes", userMatrixService.getAllMakes());
//		model.addObject("models", userMatrixService.getAllModels());
		model.addObject("lobs", lobs);
		model.addObject("userMatrixResponseList", userMatrixService.getAllUserMatrixResponse());
		model.addObject("dashboardList", dashboardService.getAllDashboards());
		
		KpiFiltersResponse res = kpiDashboardService.getKpiFilters();
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
		
		model.setViewName("admin/matrix");
		return model;
	}
	
	@GetMapping("/getAllModels")
	public @ResponseBody Set<String> getAllModels(){
		return userMatrixService.getAllModelMasters().stream().map(ModelMaster::getModel).collect(Collectors.toSet());
	}

}
