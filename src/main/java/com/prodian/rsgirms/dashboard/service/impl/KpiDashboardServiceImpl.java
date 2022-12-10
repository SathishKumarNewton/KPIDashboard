
package com.prodian.rsgirms.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.prodian.rsgirms.dashboard.model.BranchMaster;
import com.prodian.rsgirms.dashboard.model.BusinessTypeMaster;
import com.prodian.rsgirms.dashboard.model.BusinessTypeMasterNow;
import com.prodian.rsgirms.dashboard.model.CampaignMaster;
import com.prodian.rsgirms.dashboard.model.CategorisationMaster;
import com.prodian.rsgirms.dashboard.model.ChannelMaster;
import com.prodian.rsgirms.dashboard.model.ChannelMasterNew;
import com.prodian.rsgirms.dashboard.model.ChannelMasterNow;
import com.prodian.rsgirms.dashboard.model.EngineCapacityMaster;
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
import com.prodian.rsgirms.dashboard.model.PolicyTypeNew;
import com.prodian.rsgirms.dashboard.model.ProductMaster;
import com.prodian.rsgirms.dashboard.model.StateGroupMasterNow;
import com.prodian.rsgirms.dashboard.model.SubChannelMaster;
import com.prodian.rsgirms.dashboard.model.SubChannelMasterNow;
import com.prodian.rsgirms.dashboard.model.SublineMaster;
import com.prodian.rsgirms.dashboard.model.VehicleAgeMaster;
import com.prodian.rsgirms.dashboard.repository.BranchMasterRepository;
import com.prodian.rsgirms.dashboard.repository.BusinessTypeMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.BusinessTypeMasterRepository;
import com.prodian.rsgirms.dashboard.repository.CampaignMasterRepository;
import com.prodian.rsgirms.dashboard.repository.CategorisationMasterRepository;
import com.prodian.rsgirms.dashboard.repository.ChannelMasterNewRepository;
import com.prodian.rsgirms.dashboard.repository.ChannelMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.ChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.EngineCapacityMasterRepository;
import com.prodian.rsgirms.dashboard.repository.FinMonthMasterRepository;
import com.prodian.rsgirms.dashboard.repository.FinYearMasterRepository;
import com.prodian.rsgirms.dashboard.repository.FuelTypeMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.IntermediaryMasterRepository;
import com.prodian.rsgirms.dashboard.repository.MakeMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.MakeMasterRepository;
import com.prodian.rsgirms.dashboard.repository.ModelGroupMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.ModelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.OaMasterRepository;
import com.prodian.rsgirms.dashboard.repository.PolicyCategoryMasterRepository;
import com.prodian.rsgirms.dashboard.repository.PolicyTypeMasterRepository;
import com.prodian.rsgirms.dashboard.repository.PolicyTypeNewRepository;
import com.prodian.rsgirms.dashboard.repository.ProductMasterRepository;
import com.prodian.rsgirms.dashboard.repository.StateGroupMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.SubChannelMasterNowRepository;
import com.prodian.rsgirms.dashboard.repository.SubChannelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.SublineMasterRepository;
import com.prodian.rsgirms.dashboard.repository.VehicleAgeMasterRepository;
import com.prodian.rsgirms.dashboard.response.GwpResponse;
import com.prodian.rsgirms.dashboard.response.KpiFiltersResponse;
import com.prodian.rsgirms.dashboard.service.KpiDashboardService;
import com.prodian.rsgirms.reports.gwp.model.BranchFilter;
import com.prodian.rsgirms.reports.gwp.model.ChannelFilter;
import com.prodian.rsgirms.reports.gwp.model.ClusterFilter;
import com.prodian.rsgirms.reports.gwp.model.LOBFilter;
import com.prodian.rsgirms.reports.gwp.model.MakeFilter;
import com.prodian.rsgirms.reports.gwp.model.ModelFilter;
import com.prodian.rsgirms.reports.gwp.model.StateFilter;
import com.prodian.rsgirms.reports.gwp.model.SubChannelFilter;
import com.prodian.rsgirms.reports.gwp.model.ZoneFilter;
import com.prodian.rsgirms.reports.gwp.repository.BranchFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.ChannelFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.ClusterFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.LOBFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.MakeFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.ModelFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.StateFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.SubChannelFilterRepository;
import com.prodian.rsgirms.reports.gwp.repository.ZoneFilterRepository;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

/**
 * @author S. Mohamed ismaiel
 * @created sep 29, 2020 03:34:23 PM
 * @version 1.0
 * @filename GWPReportServiceImpl.java
 * @package com.prodian.rsgirms.dashboard.model.service.impl
 */

@Service
public class KpiDashboardServiceImpl implements KpiDashboardService {

	@Autowired
	private LOBFilterRepository lobRepository;

	@Autowired
	private ZoneFilterRepository zoneRepository;

	@Autowired
	private ClusterFilterRepository clusterRepository;

	@Autowired
	private StateFilterRepository stateRepository;

	@Autowired
	private BranchFilterRepository branchRepository;

	@Autowired
	private ChannelFilterRepository channelRepository;

	@Autowired
	private SubChannelFilterRepository subChannelRepository;

	@Autowired
	private MakeFilterRepository makeRepository;

	@Autowired
	private ModelFilterRepository modelRepository;

	@Autowired
	private UserMatrixService userMatrixService;

	@Autowired
	private UserService userService;

	//@Autowired
	//private ProductRepository productRepository;
	
	@Autowired
	private BusinessTypeMasterRepository businessTypeMasterRepository;
	
	@Autowired
	private CampaignMasterRepository campaignMasterRepository;
	
	@Autowired
	private ChannelMasterRepository channelMasterRepository;
	
	@Autowired
	private FinMonthMasterRepository finMonthMasterRepository;
	
	@Autowired
	private FinYearMasterRepository finYearMasterRepository;
	
	@Autowired
	private MakeMasterRepository makeMasterRepository;
	
	@Autowired
	private ModelMasterRepository modelMasterRepository;
	
	@Autowired
	private OaMasterRepository oaMasterRepository;
	
	@Autowired
	private PolicyCategoryMasterRepository policyCategoryMasterRepository;
	
	@Autowired
	private PolicyTypeMasterRepository policyTypeMasterRepository;
	
	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private SubChannelMasterRepository subChannelMasterRepository;
	
	@Autowired
	private SublineMasterRepository sublineMasterRepository;
	
	@Autowired
	private BranchMasterRepository branchMasterRepository;
	
	@Autowired
	private IntermediaryMasterRepository intermediaryMasterRepository;
	
	
	@Autowired
	private ChannelMasterNowRepository channelMasterNowRepository;

	@Autowired
	private SubChannelMasterNowRepository subChannelMasterNowRepository;
	
	@Autowired
	private MakeMasterNowRepository makeMasterNowRepository;
	
	@Autowired
	private ModelGroupMasterNowRepository modelGroupMasterNowRepository;
	
	@Autowired
	private FuelTypeMasterNowRepository fuelTypeMasterNowRepository;
	
	@Autowired
	private StateGroupMasterNowRepository stateGroupMasterNowRepository;
	
	@Autowired
	private BusinessTypeMasterNowRepository businessTypeMasterNowRepository;

	@Autowired
	private ChannelMasterNewRepository channelMasterNewRepository;

	@Autowired
	private PolicyTypeNewRepository policyTypeNewRepository;

	@Autowired
	private CategorisationMasterRepository categorisationMasterRepository;

	@Autowired
	private EngineCapacityMasterRepository engineCapacityMasterRepository;

	@Autowired
	private VehicleAgeMasterRepository vehicleAgeMasterRepository;
	
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public GwpResponse getFiltersForDropdown() {

		GwpResponse gwpResponse = new GwpResponse();

		List<LOBFilter> uniquelobList = lobRepository.findAll().stream().filter(distinctByKey(p -> p.getLob()))
				.collect(Collectors.toList());
		List<LOBFilter> uniqueproductList = lobRepository.findAll().stream().filter(distinctByKey(p -> p.getProduct()))
				.collect(Collectors.toList());
		List<LOBFilter> uniqueproductdescList = lobRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getProductDesc())).collect(Collectors.toList());
		List<ZoneFilter> uniquezoneList = zoneRepository.findAll().stream().filter(distinctByKey(p -> p.getZoneName()))
				.collect(Collectors.toList());
		List<ClusterFilter> uniqueclusterList = clusterRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getClusterName())).collect(Collectors.toList());
		List<StateFilter> uniquestateList = stateRepository.findAll().stream().filter(distinctByKey(p -> p.getState()))
				.collect(Collectors.toList());
		List<BranchFilter> uniquebranchCodeList = branchRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getBranchCode())).collect(Collectors.toList());
		List<ChannelFilter> uniquechannelList = channelRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getChannelName())).collect(Collectors.toList());
		List<SubChannelFilter> uniquesubChannelList = subChannelRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getSubChannel())).collect(Collectors.toList());
		List<MakeFilter> uniquemakeFilterList = makeRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getMakeName())).collect(Collectors.toList());
		List<ModelFilter> uniquemodelFilterList = modelRepository.findAll().stream()
				.filter(distinctByKey(p -> p.getModelName())).collect(Collectors.toList());

		List<String> lobList = new ArrayList<>();
		uniquelobList.forEach(lob -> lobList.add(lob.getLob()));
		gwpResponse.setLob(lobList);

		List<String> zoneList = new ArrayList<>();
		uniquezoneList.forEach(zone -> zoneList.add(zone.getZoneName()));
		gwpResponse.setZone(zoneList);

		List<String> clusterList = new ArrayList<>();
		uniqueclusterList.forEach(cluster -> clusterList.add(cluster.getClusterName()));
		gwpResponse.setCluster(clusterList);

		List<String> stateList = new ArrayList<>();
		uniquestateList.forEach(state -> stateList.add(state.getState()));
		gwpResponse.setState(stateList);

		List<String> branchCodeList = new ArrayList<>();
		uniquebranchCodeList.forEach(branch -> branchCodeList.add(branch.getBranchCode()));
		gwpResponse.setBranchcode(branchCodeList);

		List<String> channelList = new ArrayList<>();
		uniquechannelList.forEach(channel -> channelList.add(channel.getChannelName()));
		gwpResponse.setChannel(channelList);

		List<String> subChannelList = new ArrayList<>();
		uniquesubChannelList.forEach(subChannel -> subChannelList.add(subChannel.getSubChannel()));
		gwpResponse.setSubchannel(subChannelList);

		List<String> prodcuList = new ArrayList<>();
		uniqueproductList.forEach(product -> prodcuList.add(product.getProduct()));
		gwpResponse.setProduct(prodcuList);

		List<String> productDescList = new ArrayList<>();
		uniqueproductdescList.forEach(productdesc -> productDescList.add(productdesc.getProductDesc()));
		gwpResponse.setProductDesc(productDescList);

		List<String> makeList = new ArrayList<>();
		uniquemakeFilterList.forEach(make -> makeList.add(make.getMakeName()));
		gwpResponse.setMake(makeList);

		List<String> modelList = new ArrayList<>();
		uniquemodelFilterList.forEach(model -> modelList.add(model.getModelName()));
		gwpResponse.setModel(modelList);

		return gwpResponse;
	}

	@Override
	public KpiFiltersResponse getKpiFilters() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		
		KpiFiltersResponse res = new KpiFiltersResponse();
		List<BusinessTypeMaster> businessTypeMasters = businessTypeMasterRepository.findAll();
		List<CampaignMaster> campaignMasters = campaignMasterRepository.findAll();
		//List<ChannelMaster> channelMasters = channelMasterRepository.findAll();
		List<String> channelMasters = subChannelMasterRepository.getUniqueChannels();
		System.out.println("channelMasters size--->"+channelMasters.size());
		List<FinMonthMaster> finMonthMasters = finMonthMasterRepository.findAll();
		List<FinYearMaster> finYearMasters = finYearMasterRepository.findAll();
		List<MakeMaster> makeMasters = makeMasterRepository.findAll();
		List<ModelMaster> modelMasters = modelMasterRepository.findAll();
		List<OaMaster> oaMasters = oaMasterRepository.findAll();
		List<PolicyCategoryMaster> policyCategoryMasters = policyCategoryMasterRepository.findAll();
		List<PolicyTypeMaster> policyTypeMasters = policyTypeMasterRepository.findAll();
		List<ProductMaster> productMasters = productMasterRepository.findAll();
		//List<SubChannelMaster> subChannelMasters = subChannelMasterRepository.findAll();
		List<String> subChannelMasters = subChannelMasterRepository.getUniqueSubChannels();
		List<SublineMaster> sublineMasters = sublineMasterRepository.findAll();
		List<BranchMaster> branchMasters = branchMasterRepository.findAll();
		List<IntermediaryMaster> intermediaryMasters = intermediaryMasterRepository.findAll();
		//List<IntermediaryMaster> intermediaryMasters = intermediaryMasterRepository.findByIntermediaryCode("AG021209");
		
		List<ChannelMasterNow> channelMastersNow = channelMasterNowRepository.findAll();
		List<SubChannelMasterNow> subChannelMastersNow = subChannelMasterNowRepository.findAll();
		List<MakeMasterNow> makeMastersNow = makeMasterNowRepository.findAll();
		List<String> list = modelGroupMasterNowRepository.getModelGroups();
		List<CategorisationMaster> categorisationMasters = categorisationMasterRepository.findAll();
		List<ChannelMasterNew> channelMasterNews = channelMasterNewRepository.findAll();
		List<EngineCapacityMaster> engineCapacityMasters = engineCapacityMasterRepository.findAll();
		List<PolicyTypeNew> policyTypeNews = policyTypeNewRepository.findAll();
		List<VehicleAgeMaster> vehicleAgeMasters = vehicleAgeMasterRepository.findAll();
        System.out.println("Size"+channelMasterNews.size());
		System.out.println("Size"+engineCapacityMasters.size());
		System.out.println("Size"+policyTypeNews.size());
		System.out.println("Size"+vehicleAgeMasters.size());
		System.out.println("Size"+categorisationMasters.size());

		List<ModelGroupMasterNow> modelGroupMastersNow = new ArrayList<>();
		for(String modelGrp : list){
			ModelGroupMasterNow mgObj = new ModelGroupMasterNow();
			mgObj.setModelGroup(modelGrp);
			modelGroupMastersNow.add(mgObj);
		}
		List<BusinessTypeMasterNow> businessTypeMastersNow = businessTypeMasterNowRepository.findAll();
		List<String> fuelList = fuelTypeMasterNowRepository.getFuelTypes();
		List<FuelTypeMasterNow> fuelTypeMastersNow = new ArrayList<>();
		for(String fuel : fuelList){
			FuelTypeMasterNow obj = new FuelTypeMasterNow();
			obj.setFuleType(fuel);
			fuelTypeMastersNow.add(obj);
		}
		
		List<StateGroupMasterNow> stateGroupMastersNow = stateGroupMasterNowRepository.findAll();
		res.setChannelMastersNow(channelMastersNow);
		res.setSubChannelMastersNow(subChannelMastersNow);
		res.setMakeMastersNow(makeMastersNow);
		res.setModelGroupMastersNow(modelGroupMastersNow);
		res.setBusinessTypeMastersNow(businessTypeMastersNow);
		res.setFuelTypeMastersNow(fuelTypeMastersNow);
		res.setStateGroupMastersNow(stateGroupMastersNow);		
		res.setBusinessTypeMasters(businessTypeMasters);
		res.setCampaignMasters(campaignMasters);
		res.setChannelMasters(channelMasters);
		res.setSubChannelMasters(subChannelMasters);
		res.setFinMonthMasters(finMonthMasters);
		res.setFinYearMasters(finYearMasters);
		res.setMakeMasters(makeMasters);
		res.setModelMasters(modelMasters);
		res.setOaMasters(oaMasters);
		res.setPolicyCategoryMasters(policyCategoryMasters);
		res.setPolicyTypeMasters(policyTypeMasters);
		res.setSublineMasters(sublineMasters);
		res.setProductMasters(productMasters);
		res.setBranchMasters(branchMasters);
		res.setIntermediaryMasters(intermediaryMasters);
		res.setChannelMasterNews(channelMasterNews);
		res.setEngineCapacityMasters(engineCapacityMasters);
		res.setCategorisationMasters(categorisationMasters);
		res.setPolicyTypeNews(policyTypeNews);
		res.setVehicleAgeMasters(vehicleAgeMasters);
		return res;
	}

	/*public GwpResponse getFiltersForDropDown() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		UserMatrixMasterRequest req = userMatrixService.getUserMatrixChildByUserId(user.getId());
		GwpResponse res = new GwpResponse();
		if (req.getZones() != null && req.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			Set<String> zones = zoneRepository.findAll().stream().map(ZoneFilter::getZoneName)
					.collect(Collectors.toSet());
			res.setZone(new ArrayList<>(zones));
		} else {
			res.setZone(req.getZones());
		}
		if (req.getClusters() != null && req.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> clusters = new ArrayList<>();
			if (req.getZones() != null) {
				clusters = userMatrixService.getClustersByZoneIds(req.getZones()).stream().map(Cluster::getClusterName)
						.collect(Collectors.toList());
			} else {
				List<String> zones = zoneRepository.findAll().stream().map(ZoneFilter::getZoneName)
						.collect(Collectors.toList());
				clusters = userMatrixService.getClustersByZoneIds(zones).stream().map(Cluster::getClusterName)
						.collect(Collectors.toList());
			}
			req.setClusters(clusters);
		} else {
			res.setCluster(req.getClusters());
		}
		if(req.getStates()!=null && req.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> states = new ArrayList<>();
			if(req.getClusters()!=null) {
				states = userMatrixService.getStatesByClusterIds(req.getClusters()).stream().map(State::getState)
						.collect(Collectors.toList());				
			}else {
				List<String> clusters = clusterRepository.findAll().stream().map(ClusterFilter::getClusterName)
						.collect(Collectors.toList());
				states = userMatrixService.getStatesByClusterIds(clusters).stream().map(State::getState)
						.collect(Collectors.toList());
			}
			res.setState(states);
		}else {
			res.setState(req.getStates());
		}
		if(req.getBranchCodes()!=null && req.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> branches = new ArrayList<>();
			if(req.getStates()!=null) {
				branches = userMatrixService.getBranchesByStateIds(req.getStates()).stream().map(Branch::getBranchCode)
						.collect(Collectors.toList());				
			}else{
				List<String> states = stateRepository.findAll().stream().map(StateFilter::getState)
						.collect(Collectors.toList());
				branches = userMatrixService.getBranchesByStateIds(states).stream().map(Branch::getBranchCode)
						.collect(Collectors.toList());
			}
			res.setBranchcode(branches);
		}else {
			res.setBranchcode(req.getBranchCodes());
		}
		if (req.getProducts() != null && req.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			Set<String> productSet = productRepository.findAll().stream().map(Product::getProduct)
					.collect(Collectors.toSet());
			Set<String> productdescSet = productRepository.findAll().stream().map(Product::getProductDescription)
					.collect(Collectors.toSet());
			res.setProduct(new ArrayList<>(productSet));
			res.setProductDesc(new ArrayList<>(productdescSet));
		} else {
			List<String> productDesc = productRepository.findAllById(req.getProducts()).stream().map(Product::getProductDescription)
			.collect(Collectors.toList());
			res.setProduct(req.getProducts());
			res.setProductDesc(productDesc);
		}
		if (req.getChannels() != null && req.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			Set<String> channels = channelRepository.findAll().stream().map(ChannelFilter::getChannelName)
					.collect(Collectors.toSet());
			res.setChannel(new ArrayList<>(channels));
		} else {
			res.setChannel(req.getChannels());
		}
		if (req.getSubChannels() != null && req.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> subCh = new ArrayList<>();
			if (req.getChannels() != null && !req.getChannels().isEmpty()
					&& !req.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				subCh = userMatrixService.getSubchannelsByChannels(req.getChannels()).stream()
						.map(SubChannel::getSubChannel).collect(Collectors.toList());
			} else {
				subCh = subChannelRepository.findAll().stream().map(SubChannelFilter::getSubChannel)
						.collect(Collectors.toSet()).stream().collect(Collectors.toList());
			}
			res.setSubchannel(subCh);
		} else {
			List<String> subCh = null;
			if (req.getChannels() != null && !req.getChannels().isEmpty()) {
				subCh = userMatrixService.getSubchannelsByChannels(req.getChannels()).stream()
						.map(SubChannel::getSubChannel).collect(Collectors.toList());
			} else {
				subCh = req.getSubChannels();
			}
			res.setSubchannel(subCh);
		}
		if (req.getLobs() != null && req.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			Set<String> lobs = lobRepository.findAll().stream().map(LOBFilter::getLob).collect(Collectors.toSet());
			res.setLob(new ArrayList<>(lobs));
		} else {
			res.setLob(req.getLobs());
		}
		if (req.getMakes() != null && req.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> makes = makeRepository.findAll().stream().map(MakeFilter::getMakeName)
					.collect(Collectors.toSet()).stream().collect(Collectors.toList());
			res.setMake(makes);
		} else {
			res.setMake(req.getMakes());
		}
		if (req.getModels() != null && req.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
			List<String> mods = new ArrayList<>();
			if (req.getMakes() != null && !req.getMakes().isEmpty()
					&& !req.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
				mods = userMatrixService.getModelByMakes(req.getMakes()).stream().map(Model::getModelName)
						.collect(Collectors.toList());
			} else {
				mods = modelRepository.findAll().stream().map(ModelFilter::getModelName).collect(Collectors.toSet())
						.stream().collect(Collectors.toList());
				
			}
			res.setModel(mods);
		} else {
			res.setModel(req.getModels());
		}
		return res;
	}*/

}
