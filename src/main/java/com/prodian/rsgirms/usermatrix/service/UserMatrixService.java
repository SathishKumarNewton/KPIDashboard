package com.prodian.rsgirms.usermatrix.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.Dashboard;
import com.prodian.rsgirms.dashboard.model.ModelMaster;
import com.prodian.rsgirms.dashboard.model.UserDashboard;
import com.prodian.rsgirms.dashboard.repository.DashboardRepository;
import com.prodian.rsgirms.dashboard.repository.ModelMasterRepository;
import com.prodian.rsgirms.dashboard.repository.UserDashboardRepository;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.repository.UserRepository;
import com.prodian.rsgirms.usermatrix.enums.DimensionEnum;
import com.prodian.rsgirms.usermatrix.enums.UserMatrixMasterValueEnum;
import com.prodian.rsgirms.usermatrix.model.Branch;
import com.prodian.rsgirms.usermatrix.model.BusinessType;
import com.prodian.rsgirms.usermatrix.model.Channel;
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
import com.prodian.rsgirms.usermatrix.model.UserMatrixRole;
import com.prodian.rsgirms.usermatrix.model.Zone;
import com.prodian.rsgirms.usermatrix.repository.BranchRepository;
import com.prodian.rsgirms.usermatrix.repository.BusinessTypeRepository;
import com.prodian.rsgirms.usermatrix.repository.ChannelRepository;
import com.prodian.rsgirms.usermatrix.repository.ClusterRepository;
import com.prodian.rsgirms.usermatrix.repository.MakeRepository;
import com.prodian.rsgirms.usermatrix.repository.ModelRepository;
import com.prodian.rsgirms.usermatrix.repository.ProductRepository;
import com.prodian.rsgirms.usermatrix.repository.StateRepository;
import com.prodian.rsgirms.usermatrix.repository.SubChannelRepository;
import com.prodian.rsgirms.usermatrix.repository.UserMatrixMasterDetailRepository;
import com.prodian.rsgirms.usermatrix.repository.UserMatrixMasterRepository;
import com.prodian.rsgirms.usermatrix.repository.UserMatrixRepository;
import com.prodian.rsgirms.usermatrix.repository.UserMatrixRoleRepository;
import com.prodian.rsgirms.usermatrix.repository.ZoneRepository;

@Service
public class UserMatrixService {

	@Autowired
	private ZoneRepository zoneRepository;

	@Autowired
	private ClusterRepository clusterRepository;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private UserMatrixRoleRepository userMatrixRoleRepository;

	@Autowired
	private UserMatrixRepository userMatrixRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private BusinessTypeRepository businessTypeRepository;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private SubChannelRepository subChannelRepository;

	@Autowired
	private MakeRepository makeRepository;

	@Autowired
	private ModelRepository modelRepository;

	@Autowired
	private UserMatrixMasterDetailRepository detailRepository;

	@Autowired
	private UserMatrixMasterRepository masterRepository;

	@Autowired
	private DashboardRepository dashboardRepository;
	
	@Autowired
	private UserDashboardRepository userDashboardRepository;
	
	@Autowired
	private ModelMasterRepository modelMasterRepository;

	public List<Zone> getAllZones() {
		return zoneRepository.findAll();
	}

	public List<Cluster> getAllClusters() {
		return clusterRepository.findAll();
	}

	public List<State> getAllStates() {
		return stateRepository.findAll();
	}

	public List<Branch> getAllBranches() {
		return branchRepository.findAll();
	}

	public List<UserMatrixRole> getAllUserMatrixRoles() {
		return userMatrixRoleRepository.findAll();
	}

	public void saveUserMatrix(UserMatrixRequest request) {
//		UserMatrixRole role = userMatrixRoleRepository.findAllById(request.getUserMatrixRoleIds()).get(0);
//		switch (role.getId()) {
//		case 1: {
//			// zone head
//			List<Integer> zoneIds = new ArrayList<>();
//			// all zone
////			if (request.getZoneIds().contains(0)) {
////				zoneIds = zoneRepository.findAll().stream().map(Zone::getId).collect(Collectors.toList());
////			}
//			// not all
////			else {
//			zoneIds = request.getZoneIds();
////			}
//			// cluster, state, branch under zone
//			Set<Cluster> clusters = new HashSet<Cluster>();//(zoneIds);
//			List<Integer> clusterIds = clusters.stream().map(Cluster::getId).collect(Collectors.toList());
//			// state under zone
//			Set<State> states = getStatesByClusterIds(clusterIds);
//			List<Integer> stateIds = states.stream().map(State::getId).collect(Collectors.toList());
//			// branches under zone
//			Set<Branch> branches = getBranchesByStateIds(stateIds);
//			saveUserMatrix(request, branches);
//			break;
//		}
//		case 2: {
//			// cluster head
//			// All cluster
//			List<Integer> clusterIds = new ArrayList<>();
////			if (request.getClusterIds() == 0) {
////				clusterIds = getClustersByZoneIds(Arrays.asList(request.getZoneIds())).stream().map(Cluster::getId)
////						.collect(Collectors.toList());
////			}
////			// not all
////			else {
//			clusterIds = request.getClusterIds();
////			}
//			// state under cluster
//			Set<State> states = getStatesByClusterIds(clusterIds);
//			List<Integer> stateIds = states.stream().map(State::getId).collect(Collectors.toList());
//			// branches under cluster,state
//			Set<Branch> branches = getBranchesByStateIds(stateIds);
//			saveUserMatrix(request, branches);
//			break;
//		}
//		case 3: {
//			// state head
//			// all branches under state
//			// branches
//			// all state
//			List<Integer> stateIds = new ArrayList<>();
////			if (request.getStateIds() == 0) {
////				stateIds = getStatesByClusterIds(Arrays.asList(request.getClusterIds())).stream().map(State::getId)
////						.collect(Collectors.toList());
////			}
////			// not all state
////			else {
//			stateIds = request.getStateIds();
////			}
//			Set<Branch> branches = getBranchesByStateIds(stateIds);
//			saveUserMatrix(request, branches);
//			break;
//		}
//		case 4: {
//			// branch head
//			// all branch
//			List<String> branchCodes = new ArrayList<>();
////			if (request.getBranchCodes().equals("0")) {
////				branchCodes = getBranchesByStateIds(Arrays.asList(request.getStateIds())).stream()
////						.map(Branch::getBranchCode).collect(Collectors.toList());
////			}
//			// not all branch
////			else {
//			branchCodes = request.getBranchCodes();
////			}
//			Set<Branch> branches = getBranchesByBranchCodes(branchCodes);
//			saveUserMatrix(request, branches);
//			break;
//		}
//		default: {
//			break;
//		}
//		}
	}

	private Set<Branch> getBranchesByBranchCodes(List<String> branchCodes) {
		Set<Branch> branches = new LinkedHashSet<>();
		for (String code : branchCodes) {
			Branch branch = branchRepository.getBranchesByBranchCode(code);
			branches.add(branch);
		}
		return branches;
	}

	private void saveUserMatrix(UserMatrixRequest request, Set<Branch> branches) {
		List<UserMatrix> matrixs = new ArrayList<>();
		UserMatrixRole role = userMatrixRoleRepository.findById(request.getUserMatrixRoleIds().get(0)).get();
		for (Branch branch : branches) {
			UserMatrix matrix = new UserMatrix();
			matrix.setUserId(request.getUserIds().get(0));
			matrix.setUserMatrixRole(role.getRoleName());
			matrix.setZone(branch.getZone());
			matrix.setCluster(branch.getClusterName());
			matrix.setState(branch.getStateNew());
			matrix.setBranch(branch.getBranchCode());
			matrixs.add(matrix);
//			userMatrixRepository.save(matrix);
		}

		Set<Integer> userIds = matrixs.stream().map(UserMatrix::getUserId).collect(Collectors.toSet());
		Set<String> zones = matrixs.stream().map(UserMatrix::getZone).collect(Collectors.toSet());
		Set<String> clusters = matrixs.stream().map(UserMatrix::getCluster).collect(Collectors.toSet());
		Set<String> states = matrixs.stream().map(UserMatrix::getState).collect(Collectors.toSet());
		Set<String> branchs = matrixs.stream().map(UserMatrix::getBranch).collect(Collectors.toSet());
		System.out.println("users " + userIds.size() + " " + userIds);
		System.out.println("Zones " + zones.size() + " " + zones);
		System.out.println("clusters " + clusters.size() + " " + clusters);
		System.out.println("states " + states.size() + " " + states);
		System.out.println("branches " + branchs.size() + " " + branchs);
		System.out.println("total mappings " + matrixs.size());
		System.out.println();

	}

	public Set<Branch> getBranchesByStateIds(List<String> stateNames) {
		Set<Branch> branches = new LinkedHashSet<>();
//		Set<String> stateName = stateRepository.findAllById(stateIds).stream().map(State::getState)
//				.collect(Collectors.toSet());
		for (String state : stateNames) {
			List<Branch> branch = branchRepository.getBranchesByStateNew(state);
			if (branch != null && !branch.isEmpty()) {
				branches.addAll(branch);
			}
		}
		return branches;
	}

	public Set<State> getStatesByClusterIds(List<String> clusterNames) {
		Set<State> states = new LinkedHashSet<>();
//		Set<String> clusterNames = clusterRepository.findAllById(clusterIds).stream().map(Cluster::getClusterName)
//				.collect(Collectors.toSet());
		for (String clusterName : clusterNames) {
			List<State> state = stateRepository.getStateByClusterName(StringEscapeUtils.unescapeHtml(clusterName));
			if (state != null && !state.isEmpty()) {
				states.addAll(state);
			}
		}
		return states;
	}

	public Set<Cluster> getClustersByZoneIds(List<String> zoneNames) {
//		Set<String> zoneNames = zoneRepository.findAllById(zoneIds).stream().map(Zone::getZoneName)
//				.collect(Collectors.toSet());
		Set<Cluster> clusters = new LinkedHashSet<>();
		for (String zoneName : zoneNames) {
			List<Cluster> cluster = clusterRepository.getClusterByZoneName(zoneName);
			if (cluster != null && !cluster.isEmpty()) {
				clusters.addAll(cluster);
			}
		}
		return clusters;
	}

	public List<UserMatrix> getAllUserMatrix() {
		return userMatrixRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public List<UserMatrixResponse> getAllUserMatrixResponse() {
		List<UserMatrixResponse> response = new ArrayList<>();
		List<UserMatrixMaster> matrices = masterRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		List<Integer> matrixUserIds = matrices.stream().map(UserMatrixMaster::getUserId).collect(Collectors.toList());
		List<Integer> dashboardIds = matrices.stream().map(UserMatrixMaster::getDashboardId)
				.collect(Collectors.toList());
		for (Integer dashboardId : dashboardIds) {
			Dashboard dashboard = dashboardRepository.findDashboardById(dashboardId);
			for (Integer userId : matrixUserIds) {
				Set<String> userMatixRolesByUserId = matrices.stream()
						.filter(f -> (f.getUserId() == userId && f.getDashboardId() == dashboardId))
						.map(UserMatrixMaster::getUserMatrixRole).collect(Collectors.toSet());
				User user = userRepository.findById(userId);
				for (String role : userMatixRolesByUserId) {
					UserMatrixResponse res = new UserMatrixResponse();
					res.setUserId(userId);
					res.setUserName(user.getUserName());
					res.setDashboardId(dashboard.getId());
					res.setDashboardName(dashboard.getDashboardName());
					List<UserMatrixMaster> matrix = masterRepository
							.getUserMatrixByUserIdAndUserMatrixRoleAndDashboardId(userId, role, dashboardId);
					if (role.equalsIgnoreCase("ZONE_HEAD")) {
						Set<String> zones = matrix.stream().map(UserMatrixMaster::getZone).collect(Collectors.toSet());
						if (!zones.isEmpty() && zones.size() == 1) {
							res.setZone(zones.iterator().next());
						} else if (!zones.isEmpty() && zones.size() > 1) {
							for (String zone : zones) {
								UserMatrixResponse res1 = new UserMatrixResponse();
								res1.setUserId(userId);
								res1.setUserName(user.getUserName());
								res1.setUserMatrixRole(role);
								res1.setZone(zone);
								res1.setDashboardId(dashboard.getId());
								res1.setDashboardName(dashboard.getDashboardName());
								response.add(res1);
							}
							continue;
						}

					} else if (role.equalsIgnoreCase("CLUSTER_HEAD")) {
						Set<String> clusters = matrix.stream().map(UserMatrixMaster::getCluster)
								.collect(Collectors.toSet());
						if (!clusters.isEmpty() && clusters.size() == 1) {
							res.setCluster(clusters.iterator().next());
						} else if (!clusters.isEmpty() && clusters.size() > 1) {
							for (String cluster : clusters) {
								UserMatrixResponse res1 = new UserMatrixResponse();
								res1.setUserId(userId);
								res1.setUserName(user.getUserName());
								res1.setUserMatrixRole(role);
								res1.setCluster(cluster);
								res1.setDashboardId(dashboard.getId());
								res1.setDashboardName(dashboard.getDashboardName());
								response.add(res1);
							}
							continue;
						}

					} else if (role.equalsIgnoreCase("STATE_HEAD")) {
						Set<String> states = matrix.stream().map(UserMatrixMaster::getState)
								.collect(Collectors.toSet());
						if (!states.isEmpty() && states.size() == 1) {
							res.setState(states.iterator().next());
						} else if (!states.isEmpty() && states.size() > 1) {
							for (String state : states) {
								UserMatrixResponse res1 = new UserMatrixResponse();
								res1.setUserId(userId);
								res1.setUserName(user.getUserName());
								res1.setUserMatrixRole(role);
								res1.setState(state);
								res1.setDashboardId(dashboard.getId());
								res1.setDashboardName(dashboard.getDashboardName());
								response.add(res1);
							}
							continue;
						}

					} else if (role.equalsIgnoreCase("BRANCH_HEAD")) {
						Set<String> branchCodes = matrix.stream().map(UserMatrixMaster::getBranch)
								.collect(Collectors.toSet());
						if (!branchCodes.isEmpty() && branchCodes.size() == 1) {
							res.setBranchCode(branchCodes.iterator().next());
						} else if (!branchCodes.isEmpty() && branchCodes.size() > 1) {
							for (String branch : branchCodes) {
								UserMatrixResponse res1 = new UserMatrixResponse();
								res1.setUserId(userId);
								res1.setUserName(user.getUserName());
								res1.setUserMatrixRole(role);
								res1.setBranchCode(branch);
								res1.setDashboardId(dashboard.getId());
								res1.setDashboardName(dashboard.getDashboardName());
								response.add(res1);
							}
							continue;
						}

					}
					if (!role.equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
						res.setUserMatrixRole(role);
					}

					response.add(res);

				}

			}
		}
		return response;
	}

	public List<UserMatrixMasterDetail> userMatrixByUserId(Integer userId) {
//		List<UserMatrixMaster> matrix = masterRepository.getUserMatrixByUserIdAndUserMatrixRole(userId, role);
//		if (role.equalsIgnoreCase("ZONE_HEAD") && assignedTo != null && !StringUtils.isEmpty(assignedTo)
//				&& !assignedTo.equalsIgnoreCase("All")) {
//			return matrix.stream().filter(f -> f.getZone().equalsIgnoreCase(assignedTo)).collect(Collectors.toList());
//		} else if (role.equalsIgnoreCase("CLUSTER_HEAD") && assignedTo != null && !StringUtils.isEmpty(assignedTo)
//				&& !assignedTo.equalsIgnoreCase("All")) {
//			return matrix.stream().filter(f -> f.getCluster().equalsIgnoreCase(assignedTo))
//					.collect(Collectors.toList());
//		} else if (role.equalsIgnoreCase("STATE_HEAD") && assignedTo != null && !StringUtils.isEmpty(assignedTo)
//				&& !assignedTo.equalsIgnoreCase("All")) {
//			return matrix.stream().filter(f -> f.getState().equalsIgnoreCase(assignedTo)).collect(Collectors.toList());
//		} else if (role.equalsIgnoreCase("BRANCH_HEAD") && assignedTo != null && !StringUtils.isEmpty(assignedTo)
//				&& !assignedTo.equalsIgnoreCase("All")) {
//			return matrix.stream().filter(f -> f.getBranch().equalsIgnoreCase(assignedTo)).collect(Collectors.toList());
//		} else {
//			return matrix;
//		}
//		UserMatrixMaster master = masterRepository.findByUserId(userId);
		List<UserMatrixMasterDetail> details = detailRepository.findAllByUserId(userId).stream()
				.filter(f -> !f.getDimensionValue().equals(UserMatrixMasterValueEnum.NA.getValue()))
				.collect(Collectors.toList());

		return details;
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public List<BusinessType> getAllBusinessTypes() {
		return businessTypeRepository.findAll();
	}

	public List<Channel> getAllChannels() {
		return channelRepository.findAll();
	}

	public List<SubChannel> getAllSubChannels() {
		return subChannelRepository.findAll();
	}

	public List<Make> getAllMakes() {
		return makeRepository.findAll();
	}

	public List<Model> getAllModels() {
		List<Model> models = new ArrayList<Model>();
		try {
			models = modelRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}

	public void saveUserMatrixMaster(UserMatrixMasterRequest request) {
		Integer dashboardId = request.getDashboardId().get(0);
		if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
			return;
		}
		for (Integer userId : request.getUserIds()) {
			UserMatrixMaster master = masterRepository.findByUserIdAndDashboardId(userId,dashboardId);
			if (master == null) {
				master = new UserMatrixMaster();

			} else {
				masterRepository.delete(master);
				List<UserMatrixMasterDetail> details = detailRepository.findByUserIdAndDashboardId(userId,dashboardId);
				if (details != null && !details.isEmpty()) {
					detailRepository.deleteAll(details);
				}
			}
			master.setDashboardId(request.getDashboardId().get(0));
			master.setUserId(userId);
			if (request.getUserMatrixRoleName() != null && !request.getUserMatrixRoleName().isEmpty()) {
				for (String role : request.getUserMatrixRoleName()) {
					master.setUserMatrixRole(role);
				}
			} else {
				master.setUserMatrixRole(UserMatrixMasterValueEnum.NA.getValue());
			}
			if (request.getZones() != null && !request.getZones().isEmpty()) {
				// zone all
				if (request.getZones().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setZone(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.ZONE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// zone specific
					master.setZone(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getZones()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.ZONE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// zone na
				master.setZone(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.ZONE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getClusters() != null && !request.getClusters().isEmpty()) {
				if (request.getClusters().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					// cluster all
					master.setCluster(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.CLUSTER.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// cluster specific
					master.setCluster(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getClusters()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.CLUSTER.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// cluster na
				master.setCluster(UserMatrixMasterValueEnum.NA.name());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.CLUSTER.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.name());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getStates() != null && !request.getStates().isEmpty()) {
				if (request.getStates().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setState(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.STATE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					master.setState(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getStates()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.STATE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// state na
				master.setState(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.STATE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getBranchCodes() != null && !request.getBranchCodes().isEmpty()) {
				// branch all
				if (request.getBranchCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setBranch(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.BRANCH.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// branch specific
					master.setBranch(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getBranchCodes()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.BRANCH.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// branch na
				master.setBranch(UserMatrixMasterValueEnum.NA.name());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.BRANCH.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.name());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getProducts() != null && !request.getProducts().isEmpty()) {
				// product all
				if (request.getProducts().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setProduct(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.PRODUCT.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// product specific
					master.setProduct(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getProducts()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.PRODUCT.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// product na
				master.setProduct(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.PRODUCT.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getBusinessTypes() != null && !request.getBusinessTypes().isEmpty()) {
				// business type all
				if (request.getBusinessTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setBusinessType(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.BUSINESS_TYPE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// business type specific
					master.setBusinessType(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getBusinessTypes()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.BUSINESS_TYPE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// business type na
				master.setBusinessType(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.BUSINESS_TYPE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getLobs() != null && !request.getLobs().isEmpty()) {
				// lob all
				if (request.getLobs().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setLob(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.LOB.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// lob specific
					master.setLob(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getLobs()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.LOB.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// lob na
				master.setLob(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.LOB.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getChannels() != null && !request.getChannels().isEmpty()) {
				// channel all
				if (request.getChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setChannel(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.CHANNEL.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// channel specific
					master.setChannel(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getChannels()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.CHANNEL.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// channel na
				master.setChannel(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.CHANNEL.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getSubChannels() != null && !request.getSubChannels().isEmpty()) {
				// sub channel all
				if (request.getSubChannels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setSubChannel(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.SUBCHANNEL.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// sub channel specific
					master.setSubChannel(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getSubChannels()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.SUBCHANNEL.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// sub channel na
				master.setSubChannel(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.SUBCHANNEL.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getMakes() != null && !request.getMakes().isEmpty()) {
				// make all
				if (request.getMakes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setMake(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.MAKE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// make specific
					master.setMake(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getMakes()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.MAKE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// make na
				master.setMake(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.MAKE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			if (request.getModels() != null && !request.getModels().isEmpty()) {
				// model all
				if (request.getModels().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setModel(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.MODEL.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// model specific
					master.setModel(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getModels()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.MODEL.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			} else {
				// model na
				master.setModel(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.MODEL.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getAddOns()!=null&&!request.getAddOns().isEmpty()) {
				if (request.getAddOns().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setAddOns(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.ADD_ONS.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// model specific
					master.setAddOns(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getAddOns()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.ADD_ONS.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				// model na
				master.setAddOns(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.ADD_ONS.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getCities()!=null&&!request.getCities().isEmpty()) {
				if (request.getCities().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setCity(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.CITY.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// city specific
					master.setCity(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getCities()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.CITY.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				// city na
				master.setCity(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.CITY.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getModelClasifications()!=null&&!request.getModelClasifications().isEmpty()) {
				if (request.getModelClasifications().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setModelClasification(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.MODEL_CLASSIFICATION.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// model class specific
					master.setModelClasification(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getModelClasifications()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.MODEL_CLASSIFICATION.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				// model class na
				master.setModelClasification(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.MODEL_CLASSIFICATION.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getModelGroups()!=null&&!request.getModelGroups().isEmpty()) {
				if (request.getModelGroups().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setModelGroup(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.MODEL_GROUP.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// model gr specific
					master.setModelGroup(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getModelGroups()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.MODEL_GROUP.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				// model gr na
				master.setModelGroup(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.MODEL_GROUP.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getOaCodes()!=null&&!request.getOaCodes().isEmpty()) {
				if (request.getOaCodes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setOaCode(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.OA_CODE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					// oa specific
					master.setOaCode(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getOaCodes()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.OA_CODE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				// oa na
				master.setOaCode(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.OA_CODE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getOaNames()!=null&&!request.getOaNames().isEmpty()) {
				if (request.getOaNames().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setOaName(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.OA_NAME.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					//  oa specific
					master.setOaName(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getOaNames()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.OA_NAME.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				//  oa na
				master.setOaName(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.OA_NAME.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getPolicyTypes()!=null&&!request.getPolicyTypes().isEmpty()) {
				if (request.getPolicyTypes().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setPolicyType(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.POLICY_TYPE.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					master.setPolicyType(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getPolicyTypes()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.POLICY_TYPE.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				master.setPolicyType(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.POLICY_TYPE.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getPolicyCategories()!=null&&!request.getPolicyCategories().isEmpty()) {
				if (request.getPolicyCategories().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setPolicyCategory(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.POLICY_CATEGORY.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					master.setPolicyCategory(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getPolicyCategories()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.POLICY_CATEGORY.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				master.setPolicyCategory(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.POLICY_CATEGORY.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			if(request.getRegion()!=null&&!request.getRegion().isEmpty()) {
				if (request.getRegion().contains(UserMatrixMasterValueEnum.ALL.getValue())) {
					master.setRegion(UserMatrixMasterValueEnum.ALL.getValue());
					UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
					detail.setUserId(userId);
					detail.setUserDimension(DimensionEnum.REGION.name());
					detail.setDimensionValue(UserMatrixMasterValueEnum.ALL.getValue());
					detail.setDashboardId(dashboardId);
					detailRepository.save(detail);
				} else {
					master.setRegion(UserMatrixMasterValueEnum.SPECIFIC.getValue());
					for (String val : request.getRegion()) {
						UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
						detail.setUserId(userId);
						detail.setUserDimension(DimensionEnum.REGION.name());
						detail.setDimensionValue(val);
						detail.setDashboardId(dashboardId);
						detailRepository.save(detail);
					}
				}
			}else {
				master.setRegion(UserMatrixMasterValueEnum.NA.getValue());
				UserMatrixMasterDetail detail = new UserMatrixMasterDetail();
				detail.setUserId(userId);
				detail.setUserDimension(DimensionEnum.REGION.name());
				detail.setDimensionValue(UserMatrixMasterValueEnum.NA.getValue());
				detail.setDashboardId(dashboardId);
				detailRepository.save(detail);
			}
			
			masterRepository.save(master);
		}

	}

	public Set<SubChannel> getSubchannelsByChannels(List<String> channelNames) {
		Set<SubChannel> subChannels = new LinkedHashSet<>();
		for (String channelName : channelNames) {
			List<SubChannel> subChannel = subChannelRepository.findByChannelName(channelName);
			subChannels.addAll(subChannel);
		}
		return subChannels;
	}

	public Set<Model> getModelByMakes(List<String> makes) {
		Set<Model> models = new LinkedHashSet<Model>();
		for (String make : makes) {
			List<Model> model = modelRepository.findByMake(make);
			models.addAll(model);
		}
		return models;
	}

	public void deleteUserMatrix(List<UserMatrixMaster> matrices) {
		Set<Integer> userIds = matrices.stream().map(UserMatrixMaster::getUserId).collect(Collectors.toSet());
		for (Integer userId : userIds) {
			UserMatrixMaster master = masterRepository.findByUserId(userId);
			masterRepository.delete(master);
			List<UserMatrixMasterDetail> details = detailRepository.findAllByUserId(userId);
			detailRepository.deleteAll(details);
		}
	}

	public List<User> getAllUsers() {
		List<Integer> matrixUserIds = masterRepository.findAll().stream().map(UserMatrixMaster::getUserId)
				.collect(Collectors.toList());
		List<User> users = userRepository.findAll().stream().filter(f -> !matrixUserIds.contains(f.getId()))
				.collect(Collectors.toList());
		return users;
	}

	public UserMatrixMasterRequest getUserMatrixChildByUserId(Integer userId, UserMatrixMasterRequest request) {
//		UserMatrixMaster master = masterRepository.findByUserId(userId);
		UserMatrixMasterRequest req = new UserMatrixMasterRequest();
		for(Integer dashboardId : request.getDashboardId()) {
			UserMatrixMaster master = masterRepository.findByUserIdAndDashboardId(userId, dashboardId);
//			List<UserMatrixMasterDetail> details = detailRepository.findAllByUserId(userId);
			List<UserMatrixMasterDetail> details = detailRepository.findByUserIdAndDashboardId(userId,dashboardId);
			Dashboard dashboard = dashboardRepository.findDashboardById(dashboardId);
			req.setUserIds(Arrays.asList(userId));
			User user = userRepository.findById(userId);
			req.setUserNames(Arrays.asList(user.getUserName()));
			if (master == null) {
				return req;
			}
			
			if(dashboard.getDashboardName().toUpperCase().contains(RMSConstants.MOTOR_DASHBOARD)) {
				
				if (master.getChannel() != null
						&& !master.getChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorChannel(values);
					req.setChannels(values);
				}if(request.getMotorChannel()!=null && !request.getMotorChannel().isEmpty()) {
					req.setMotorChannel(request.getMotorChannel());
				}
				
				if (master.getSubChannel() != null
						&& !master.getSubChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.SUBCHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorSubChannel(values);
					req.setSubChannels(values);
				}if(request.getMotorSubChannel()!=null && !request.getMotorSubChannel().isEmpty()) {
					req.setMotorSubChannel(request.getMotorSubChannel());
				}
				
				if (master.getRegion() != null
						&& !master.getRegion().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.REGION.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorRegion(values);
					req.setRegion(values);
				}if(request.getMotorRegion()!=null && !request.getMotorRegion().isEmpty()) {
					req.setMotorRegion(request.getMotorRegion());
				}
				
				if (master.getState() != null
						&& !master.getState().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.STATE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorState(values);
					req.setStates(values);
				}if(request.getMotorState()!=null && !request.getMotorState().isEmpty()) {
					req.setMotorState(request.getHealthState());
				}
				
				if (master.getCity() != null
						&& !master.getCity().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CITY.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorCity(values);
					req.setCities(values);
				}if(request.getMotorCity()!=null && !request.getMotorCity().isEmpty()) {
					req.setMotorCity(request.getMotorCity());
				}
				
				if (master.getZone() != null && !master.getZone().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.ZONE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorZone(values);
					req.setZones(values);
				}if(request.getMotorZone()!=null && !request.getMotorZone().isEmpty()) {
					req.setMotorZone(request.getMotorZone());
				}
				
				if (master.getCluster() != null
						&& !master.getCluster().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CLUSTER.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorCluster(values);
					req.setClusters(values);
				}if(request.getMotorCluster()!=null && !request.getMotorCluster().isEmpty()) {
					req.setMotorCluster(request.getMotorCluster());
				}
				
				if (master.getOaCode() != null
						&& !master.getOaCode().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.OA_CODE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorOaCode(values);
					req.setOaCodes(values);
				}if(request.getMotorOaCode()!=null && !request.getMotorOaCode().isEmpty()) {
					req.setMotorOaCode(request.getMotorOaCode());
				}
				
				if (master.getOaName() != null
						&& !master.getOaName().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.OA_NAME.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorOaName(values);
					req.setOaNames(values);
				}if(request.getMotorOaName()!=null && !request.getMotorOaName().isEmpty()) {
					req.setMotorOaName(request.getMotorOaName());
				}
				
				if (master.getBusinessType() != null
						&& !master.getBusinessType().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BUSINESS_TYPE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorBusinessType(values.get(0));
					req.setBusinessTypes(values);
				}if(request.getMotorBusinessType()!=null && !StringUtils.isEmpty(request.getMotorBusinessType())) {
					req.setMotorBusinessType(request.getMotorBusinessType());
				}
				
				if (master.getMake() != null && !master.getMake().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.MAKE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorMake(values);
					req.setMakes(values);
				}if(request.getMotorMake()!=null && !request.getMotorMake().isEmpty()) {
					req.setMotorMake(request.getMotorMake());
				}
				
				if (master.getModel() != null && !master.getModel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.MODEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorModel(values);
					req.setModels(values);
				}if(request.getMotorModel()!=null && !request.getMotorModel().isEmpty()) {
					req.setMotorModel(request.getMotorModel());
				}
				
				if (master.getProduct() != null
						&& !master.getProduct().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.PRODUCT.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMotorProduct(values);
					req.setProducts(values);
				}if(request.getMotorProduct()!=null && !request.getMotorProduct().isEmpty()) {
					req.setMotorProduct(request.getMotorProduct());
				}
				
				//static values get from ranges not added in matrix may applied filters from ui
				req.setMotorVehicleAge(request.getMotorVehicleAge());
				req.setMotorNoOfYearsWithRs(request.getMotorNoOfYearsWithRs());
				
				//pending filters will to be applied in matrix
				//intermediary code,intermediary name, pincode, t20 location, model group, model classification
				//campaign
				
			}else if(dashboard.getDashboardName().toUpperCase().contains(RMSConstants.HEALTH_DASHBOARD)) {
				
				if (master.getChannel() != null
						&& !master.getChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthChannel(values);
					req.setChannels(values);
				}if(request.getHealthChannel()!=null && !request.getHealthChannel().isEmpty()) {
					req.setHealthChannel(request.getHealthChannel());
				}
				
				if (master.getSubChannel() != null
						&& !master.getSubChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.SUBCHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthSubChannel(values);
					req.setSubChannels(values);
				}if(request.getHealthSubChannel()!=null && !request.getHealthSubChannel().isEmpty()) {
					req.setHealthSubChannel(request.getHealthSubChannel());
				}
				
				if (master.getRegion() != null
						&& !master.getRegion().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.REGION.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthRegion(values);
					req.setRegion(values);
				}if(request.getHealthRegion()!=null && !request.getHealthRegion().isEmpty()) {
					req.setHealthRegion(request.getHealthRegion());
				}
				
				if (master.getState() != null
						&& !master.getState().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.STATE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthState(values);
					req.setStates(values);
				}if(request.getHealthState()!=null && !request.getHealthState().isEmpty()) {
					req.setHealthState(request.getHealthState());
				}
				
				if (master.getCity() != null
						&& !master.getCity().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CITY.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthCity(values);
					req.setCities(values);
				}if(request.getHealthCity()!=null && !request.getHealthCity().isEmpty()) {
					req.setHealthCity(request.getHealthCity());
				}
				
				if (master.getBranch() != null
						&& !master.getBranch().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BRANCH.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthBranch(values);
					req.setBranchCodes(values);
				}if(request.getHealthBranch()!=null && !request.getHealthBranch().isEmpty()) {
					req.setHealthBranch(request.getHealthBranch());
				}
				
				if (master.getAddOns() != null
						&& !master.getAddOns().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.ADD_ONS.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthAddOn(values);
					req.setAddOns(values);
				}if(request.getHealthAddOn()!=null && !request.getHealthAddOn().isEmpty()) {
					req.setHealthAddOn(request.getHealthAddOn());
				}
				
				if (master.getProduct() != null
						&& !master.getProduct().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.PRODUCT.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralAddOn(values);
					req.setAddOns(values);
				}if(request.getHealthProduct()!=null && !request.getHealthProduct().isEmpty()) {
					req.setHealthProduct(request.getHealthProduct());
				}
				
				if (master.getPolicyType() != null
						&& !master.getPolicyType().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.POLICY_TYPE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthPolicyType(values);
					req.setPolicyTypes(values);
				}if(request.getHealthPolicyType()!=null && !request.getHealthPolicyType().isEmpty()) {
					req.setHealthPolicyType(request.getHealthPolicyType());
				}
				
				if (master.getPolicyCategory() != null
						&& !master.getPolicyCategory().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.POLICY_CATEGORY.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthPolicyCategory(values);
					req.setPolicyCategories(values);
				}if(request.getHealthPolicyCategory()!=null && !request.getHealthPolicyCategory().isEmpty()) {
					req.setHealthPolicyCategory(request.getHealthPolicyCategory());
				}
				
				if (master.getBusinessType() != null
						&& !master.getBusinessType().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BUSINESS_TYPE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setHealthBusinessType(values.get(0));
					req.setBusinessTypes(values);
				}if(request.getHealthBusinessType()!=null && !StringUtils.isEmpty(request.getHealthBusinessType())) {
					req.setHealthBusinessType(request.getHealthBusinessType());
				}
				
				//static values get from ui below not added in matrix may applied filters from ui
				req.setHealthNoOfYearsWithRs(request.getHealthNoOfYearsWithRs());
				req.setHealthSTPNSTP(request.getHealthSTPNSTP());
				req.setSumInsured(request.getHealthSumInsured());
				req.setHealthMaxAge(request.getHealthMaxAge());
				req.setHealthNoOfMigratedYears(request.getHealthNoOfMigratedYears());
				req.setHealthAgeBand(request.getHealthAgeBand());
				req.setHealthPreExistingDisease(request.getHealthPreExistingDisease());
				req.setHealthFamilySize(request.getHealthFamilySize());
				
				//pending filters will to be applied in matrix
				//intermediary, camapaign, subline
				
			}else if(dashboard.getDashboardName().toUpperCase().contains(RMSConstants.GENERAL_DASHBOARD)) {
				if (master.getChannel() != null
						&& !master.getChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralChannel(values);
					req.setChannels(values);
				}if(request.getGeneralChannel()!=null && !request.getGeneralChannel().isEmpty()) {
					req.setGeneralChannel(request.getGeneralChannel());
				}
				
				if (master.getSubChannel() != null
						&& !master.getSubChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.SUBCHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralSubChannel(values);
					req.setSubChannels(values);
				}if(request.getGeneralSubChannel()!=null && !request.getGeneralSubChannel().isEmpty()) {
					req.setGeneralSubChannel(request.getGeneralSubChannel());
				}
				
				if (master.getRegion() != null
						&& !master.getRegion().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.REGION.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralRegion(values);
					req.setRegion(values);
				}if(request.getGeneralRegion()!=null && !request.getGeneralRegion().isEmpty()) {
					req.setGeneralRegion(request.getGeneralRegion());
				}
				
				if (master.getState() != null
						&& !master.getState().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.STATE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralState(values);
					req.setStates(values);
				}if(request.getGeneralState()!=null && !request.getGeneralState().isEmpty()) {
					req.setGeneralState(request.getGeneralState());
				}
				
				if (master.getCity() != null
						&& !master.getCity().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CITY.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralCity(values);
					req.setCities(values);
				}if(request.getGeneralCity()!=null && !request.getGeneralCity().isEmpty()) {
					req.setGeneralCity(request.getGeneralCity());
				}
				
				if (master.getBranch() != null
						&& !master.getBranch().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BRANCH.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralBranch(values);
					req.setBranchCodes(values);
				}if(request.getGeneralBranch()!=null && !request.getGeneralBranch().isEmpty()) {
					req.setGeneralBranch(request.getGeneralBranch());
				}
				
				if (master.getAddOns() != null
						&& !master.getAddOns().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.ADD_ONS.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralAddOn(values);
					req.setAddOns(values);
				}if(request.getGeneralAddOn()!=null && !request.getGeneralAddOn().isEmpty()) {
					req.setGeneralAddOn(request.getGeneralAddOn());
				}
				
				if (master.getProduct() != null
						&& !master.getProduct().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.PRODUCT.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralProduct(values);
					req.setProducts(values);
				}if(request.getGeneralProduct()!=null && !request.getGeneralProduct().isEmpty()) {
					req.setGeneralProduct(request.getGeneralProduct());
				}
				
				if (master.getBusinessType() != null
						&& !master.getBusinessType().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BUSINESS_TYPE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setGeneralBusinessType(values.get(0));
					req.setBusinessTypes(values);
				}if(request.getGeneralBusinessType()!=null && !StringUtils.isEmpty(request.getGeneralBusinessType())) {
					req.setGeneralBusinessType(request.getGeneralBusinessType());
				}
				
				//static values get from ranges not added in matrix
				req.setGeneralNoOfYearsWithRs(request.getGeneralNoOfYearsWithRs());
				
				//pending filters will to be applied in matrix
				//intermediary, campaign
				
			}else {
				//gwp
				if (master.getUserMatrixRole() != null
						&& !master.getUserMatrixRole().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					req.setUserMatrixRoleName(Arrays.asList(master.getUserMatrixRole()));
				}
				if (master.getUserMatrixRole() != null
						&& !master.getUserMatrixRole().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					req.setUserMatrixRoleName(Arrays.asList(master.getUserMatrixRole()));
				}
				if (master.getZone() != null && !master.getZone().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.ZONE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setZones(values);
				}
				if (master.getCluster() != null
						&& !master.getCluster().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CLUSTER.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setClusters(values);
				}
				if (master.getState() != null && !master.getState().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.STATE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setStates(values);
				}
				if (master.getBranch() != null
						&& !master.getBranch().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BRANCH.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setBranchCodes(values);
				}
				if (master.getProduct() != null
						&& !master.getProduct().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.PRODUCT.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setProducts(values);
				}
				if (master.getBusinessType() != null
						&& !master.getBusinessType().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.BUSINESS_TYPE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setBusinessTypes(values);
				}
				if (master.getLob() != null && !master.getLob().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.LOB.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setLobs(values);
				}
				if (master.getChannel() != null
						&& !master.getChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.CHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setChannels(values);
				}
				if (master.getSubChannel() != null
						&& !master.getSubChannel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.SUBCHANNEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setSubChannels(values);
				}
				if (master.getMake() != null && !master.getMake().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.MAKE.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setMakes(values);
				}
				if (master.getModel() != null && !master.getModel().equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					List<String> values = details.stream()
							.filter(f -> f.getUserDimension().equalsIgnoreCase(DimensionEnum.MODEL.name()))
							.map(UserMatrixMasterDetail::getDimensionValue).collect(Collectors.toList());
					req.setModels(values);
				}
			}
		}
		return req;
	}

	public List<UserMatrixResponse> getAllUserMatrixResponseByDashboardId(Integer dashboardId) {
		List<UserMatrixResponse> response = new ArrayList<>();
		List<UserMatrixMaster> matrices = masterRepository.findByDashboardId(dashboardId);
		List<Integer> matrixUserIds = matrices.stream().map(UserMatrixMaster::getUserId).collect(Collectors.toList());
		for (Integer userId : matrixUserIds) {
			Set<String> userMatixRolesByUserId = matrices.stream().filter(f -> f.getUserId() == userId)
					.map(UserMatrixMaster::getUserMatrixRole).collect(Collectors.toSet());
			User user = userRepository.findById(userId);
			for (String role : userMatixRolesByUserId) {
				UserMatrixResponse res = new UserMatrixResponse();
				res.setUserId(userId);
				res.setUserName(user.getUserName());
				List<UserMatrix> matrix = userMatrixRepository.getUserMatrixByUserIdAndUserMatrixRole(userId, role);
				if (role.equalsIgnoreCase("ZONE_HEAD")) {
					Set<String> zones = matrix.stream().map(UserMatrix::getZone).collect(Collectors.toSet());
					if (!zones.isEmpty() && zones.size() == 1) {
						res.setZone(zones.iterator().next());
					} else if (!zones.isEmpty() && zones.size() > 1) {
						for (String zone : zones) {
							UserMatrixResponse res1 = new UserMatrixResponse();
							res1.setUserId(userId);
							res1.setUserName(user.getUserName());
							res1.setUserMatrixRole(role);
							res1.setZone(zone);
							response.add(res1);
						}
						continue;
					}

				} else if (role.equalsIgnoreCase("CLUSTER_HEAD")) {
					Set<String> clusters = matrix.stream().map(UserMatrix::getCluster).collect(Collectors.toSet());
					if (!clusters.isEmpty() && clusters.size() == 1) {
						res.setCluster(clusters.iterator().next());
					} else if (!clusters.isEmpty() && clusters.size() > 1) {
						for (String cluster : clusters) {
							UserMatrixResponse res1 = new UserMatrixResponse();
							res1.setUserId(userId);
							res1.setUserName(user.getUserName());
							res1.setUserMatrixRole(role);
							res1.setCluster(cluster);
							response.add(res1);
						}
						continue;
					}

				} else if (role.equalsIgnoreCase("STATE_HEAD")) {
					Set<String> states = matrix.stream().map(UserMatrix::getState).collect(Collectors.toSet());
					if (!states.isEmpty() && states.size() == 1) {
						res.setState(states.iterator().next());
					} else if (!states.isEmpty() && states.size() > 1) {
						for (String state : states) {
							UserMatrixResponse res1 = new UserMatrixResponse();
							res1.setUserId(userId);
							res1.setUserName(user.getUserName());
							res1.setUserMatrixRole(role);
							res1.setState(state);
							response.add(res1);
						}
						continue;
					}

				} else if (role.equalsIgnoreCase("BRANCH_HEAD")) {
					Set<String> branchCodes = matrix.stream().map(UserMatrix::getBranch).collect(Collectors.toSet());
					if (!branchCodes.isEmpty() && branchCodes.size() == 1) {
						res.setBranchCode(branchCodes.iterator().next());
					} else if (!branchCodes.isEmpty() && branchCodes.size() > 1) {
						for (String branch : branchCodes) {
							UserMatrixResponse res1 = new UserMatrixResponse();
							res1.setUserId(userId);
							res1.setUserName(user.getUserName());
							res1.setUserMatrixRole(role);
							res1.setBranchCode(branch);
							response.add(res1);
						}
						continue;
					}

				}
				if (!role.equalsIgnoreCase(UserMatrixMasterValueEnum.NA.getValue())) {
					res.setUserMatrixRole(role);
				}
				response.add(res);

			}
		}
		return response;
	}

	public UserDashboard getDashboardByUserIdAndDashboardName(int userId, String dashboardName) {
		return userDashboardRepository.findByUserIdAndDashboardName(userId,dashboardName);
		
	}

	public List<ModelMaster> getAllModelMasters() {
		return modelMasterRepository.findAll();
	}

}
