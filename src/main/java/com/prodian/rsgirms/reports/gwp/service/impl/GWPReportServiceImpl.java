
package com.prodian.rsgirms.reports.gwp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.response.GwpResponse;
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
import com.prodian.rsgirms.reports.gwp.service.GWPReportService;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;
import com.prodian.rsgirms.usermatrix.enums.UserMatrixMasterValueEnum;
import com.prodian.rsgirms.usermatrix.model.Branch;
import com.prodian.rsgirms.usermatrix.model.Cluster;
import com.prodian.rsgirms.usermatrix.model.Model;
import com.prodian.rsgirms.usermatrix.model.Product;
import com.prodian.rsgirms.usermatrix.model.State;
import com.prodian.rsgirms.usermatrix.model.SubChannel;
import com.prodian.rsgirms.usermatrix.model.UserMatrixMasterRequest;
import com.prodian.rsgirms.usermatrix.repository.ProductRepository;
import com.prodian.rsgirms.usermatrix.service.UserMatrixService;

/**
 * @author Zakir Hussain Syed
 * @created Aug 11, 2020 03:34:23 PM
 * @version 1.0
 * @filename GWPReportServiceImpl.java
 * @package com.prodian.rsgirms.reports.gwp.service.impl
 */

@Service
public class GWPReportServiceImpl implements GWPReportService {

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

	@Autowired
	private ProductRepository productRepository;

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

	public GwpResponse getFiltersForDropDown() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		UserDashboard dashboard = userMatrixService.getDashboardByUserIdAndDashboardName(user.getId(),RMSConstants.GWP_DASHBOARD);
		UserMatrixMasterRequest request = new UserMatrixMasterRequest();
		request.setDashboardId(Arrays.asList(dashboard.getDashboardId()));
		UserMatrixMasterRequest req = userMatrixService.getUserMatrixChildByUserId(user.getId(),request);
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
	}

}
