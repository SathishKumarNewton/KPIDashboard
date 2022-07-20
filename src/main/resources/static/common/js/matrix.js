

$(function() {
			$(document).ready(function() {
			var lastSelectedMatrixUser = {};

			/*$('#reportType,#regionIds,#motorCities,#userIds,#roleIds,#zoneIds,#motorZoneIds,#clusters,#motorClusters,#states,#branchCodes,#t20Locations,#pincodes,#modelGroups,#modelClassifications,#vehicleAges,#noOfAgeWithRs,#addOns,#intermediaryCodes,#intermediaryNames,#oaCodes,#oaNames,#products,#lobs,#businessTypes,#channels,#subChannels,#makes,#models')
				.selectize({
				sortField : 'text'
			});
			
			$('#editRoles,#editZones,#editClusters,#editStates,#editBranchCodes,#editProducts,#editLobs,#editBusinessTypes,#editChannels,#editSubChannels,#editMakes,#editModels')
			.selectize({
				sortField : 'text'
			});
			
			$('#healthChannels,#healthSubChannels,#healthIntermediary,#healthRegions,#healthCities,#healthStates,#healthPolicyTypes,#healthPolicyCategories,#healthSumInsured,#healthMaxAges,#healthAddOns')
			.selectize({
				sortField : 'text'
			});
			
			$('#reportTypeList')
			.selectize({
				sortField : 'text'
			});*/
			
			$('select')
			.selectize({
				sortField : 'text'
			});
			
			$("#userMatrixForm")
			.validate(
			{
				ignore : ':hidden:not([class~=selectized]),:hidden > .selectized, .selectize-control .selectize-input input',
				rules : {
					dashboardId : {
						required : true
					},
					userIds : {
						required : true
					},
					/* userMatrixRoleName : {
						required : true
					},
					zoneIds : {
						required : true
					},
					clusterIds : {
						required : true
					},
					stateIds : {
						required : true
					},
					branchCodes : {
						required : true
					} */
				},
				messages : {
					dashboardId : {
						required: "Please select Report"
					},
					userIds :  {
						required: "Please select User"
					},
					/* userMatrixRoleName : {
						required : "Please select role"
					},
					zoneIds : {
						required : "Please select zone"
					},
					clusterIds : {
						required : "Please select cluster"
					},
					stateIds : {
						required : "Please select state"
					},
					branchCodes : {
						required : "Please select branch"
					} */
				},
				submitHandler : function(form) {
					form.submit();
					toastr.clear();
					toastr.success('Successfully Added!');
				}
			});

			
			$('#zoneIds,#motorZoneIds,#editZones').on('change',function() {
				var clusterIdsSelect = $("#clusters")[0].selectize;
				if($('#motorZoneids').length){
					clusterIdsSelect = $("#motorClusters")[0].selectize;
				}
				if($('#editUserMatrixModal').hasClass('show')){
					clusterIdsSelect = $("#editClusters")[0].selectize;
				}
				clusterIdsSelect.clear();
				clusterIdsSelect.clearOptions();
				clusterIdsSelect.load(function(callback) {
					var znIds = [];
					var $zoneIds = $('#zoneIds').selectize();
					if($('#motorZoneids').length){
						$zoneIds = $('#motorZoneIds').selectize();
					}
					if($('#editUserMatrixModal').hasClass('show')){
						$zoneIds = $('#editZones').selectize();
					}
					var zoneSelectize = $zoneIds[0].selectize;
					if($("#zoneIds option:selected").text().includes('All')||$("#zoneIds").val().length==0){
						if($("#zoneIds option:selected").text().includes('All') ){
							zoneSelectize.clear();							
							zoneSelectize.setValue('All',true);
							zoneSelectize.close();
						}
						znIds = Object.keys(zoneSelectize.options);
					}
					if($('#editUserMatrixModal').hasClass('show')&&
							($("#editZones option:selected").text().includes('All')||$("#editZones").val().length==0)){
						if($("#editZones option:selected").text().includes('All') ){
							zoneSelectize.clear();							
							zoneSelectize.setValue('All',true);
							zoneSelectize.close();
						}
						znIds = Object.keys(zoneSelectize.options);
					}
					if($('#motorZoneids').length && 
							($("#motorZoneids option:selected").text().includes('All')||$("#motorZoneids").val().length==0)){
						if($("#motorZoneids option:selected").text().includes('All') ){
							zoneSelectize.clear();							
							zoneSelectize.setValue('All',true);
							zoneSelectize.close();
						}
						znIds = Object.keys(zoneSelectize.options);
					}
					else{
						if($('#editUserMatrixModal').hasClass('show')){
							znIds = $("#editZones").val();
						}
						if($('#motorZoneids').length){
							znIds = $("#motorZoneids").val();
						}
						else{
							znIds = $("#zoneIds").val();
						}
					}
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getClustersByZoneIds',
						data : {
							zoneNames: function(){
								return znIds;
							}
						},
						contentType: 'application/json',
						success : function(data) {
							var clusterList = [];
							var clusters = [];
							for(var obj of data){
								var cluster = new Object();
								cluster['text'] = obj.clusterName;
								cluster['value'] = obj.clusterName;
								clusterList.push(cluster);
								clusters.push(obj.id);
							}
							if(clusterList){
								var cluster = new Object();
								cluster['text'] = 'All';
								cluster['value'] = 'All';
								clusterList.push(cluster);
							}
							callback(clusterList);
						},
						error : function(data) {
							callback();
						}
					}); 
				});
			});
			
			function loadClustersWithoutClear(){
				var clusterIdsSelect = $("#clusters")[0].selectize;
				if($('#editUserMatrixModal').hasClass('show')){
					clusterIdsSelect = $("#editClusters")[0].selectize;
				}
				//clusterIdsSelect.clear();
				clusterIdsSelect.clearOptions();
				clusterIdsSelect.load(function(callback) {
					var znIds = [];
					var $zoneIds = $('#zoneIds').selectize();				
					if($('#editUserMatrixModal').hasClass('show')){
						$zoneIds = $('#editZones').selectize();
					}
					var zoneSelectize = $zoneIds[0].selectize;
					if($("#zoneIds option:selected").text().includes('All')||$("#zoneIds").val().length==0){
						if($("#zoneIds option:selected").text().includes('All') ){
							zoneSelectize.clear();							
							zoneSelectize.setValue('All',true);
							zoneSelectize.close();
						}
						znIds = Object.keys(zoneSelectize.options);
					}
					if($('#editUserMatrixModal').hasClass('show')&&
							($("#editZones option:selected").text().includes('All')||$("#editZones").val().length==0)){
						if($("#editZones option:selected").text().includes('All') ){
							zoneSelectize.clear();							
							zoneSelectize.setValue('All',true);
							zoneSelectize.close();
						}
						znIds = Object.keys(zoneSelectize.options);
					}
					else{
						if($('#editUserMatrixModal').hasClass('show')){
							znIds = $("#editZones").val();
						}
						else{
							znIds = $("#zoneIds").val();
						}
					}
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getClustersByZoneIds',
						data : {
							zoneNames: function(){
								return znIds;
							}
						},
						contentType: 'application/json',
						success : function(data) {
							var clusterList = [];
							var clusters = [];
							for(var obj of data){
								var cluster = new Object();
								cluster['text'] = obj.clusterName;
								cluster['value'] = obj.clusterName;
								clusterList.push(cluster);
								clusters.push(obj.id);
							}
							if(clusterList){
								var cluster = new Object();
								cluster['text'] = 'All';
								cluster['value'] = 'All';
								clusterList.push(cluster);
							}
							callback(clusterList);
						},
						error : function(data) {
							callback();
						}
					}); 
				});
			}
					
			$('#clusters,#editClusters').on('change',function() {
				var stateIdsSelect = $("#states")[0].selectize
				if($('#editUserMatrixModal').hasClass('show')){
					stateIdsSelect = $("#editStates")[0].selectize;
				}
				stateIdsSelect.clear();
				stateIdsSelect.clearOptions();
				stateIdsSelect.load(function(callback) {
					var clIds = [];
					var $clusters = $('#clusters').selectize();
					if($('#editUserMatrixModal').hasClass('show')){
						$clusters = $("#editClusters").selectize();
					}
					var clusterSelectize = $clusters[0].selectize;
					if($("#clusters option:selected").text().includes('All') || $("#clusters").val().length==0){
						clIds = Object.keys(clusterSelectize.options);
						if($("#clusters option:selected").text().includes('All')){
							clusterSelectize.clear();
							clusterSelectize.setValue('All',true);
							clusterSelectize.close();							
						}
					}
					else{
						clIds = $("#clusters").val();					
					}
					if($('#editUserMatrixModal').hasClass('show')){			
						clIds = $("#editClusters").val();
						if($("#editClusters option:selected").text().includes('All')){
							clusterSelectize.clear();
							clusterSelectize.setValue('All',true);
							clusterSelectize.close();		
							clIds = Object.keys(clusterSelectize.options);
						}
						if($("#editClusters").val().length==0){
							clIds = Object.keys(clusterSelectize.options);
						}						
					}
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getStatesByClusterIds',
						data : {
							clusters : function(){
								return clIds;
							}
						},
						success : function(data) {
							var stateList = [];
							var states = [];
							for(var obj of data){
								var state = new Object();
								state['text'] = obj.state;
								state['value'] = [obj.state];
								stateList.push(state);
								states.push(obj.id);
							}
							if(stateList){
								var state = new Object();
								state['text'] = 'All';
								state['value'] = 'All';
								stateList.push(state);
							}
							callback(stateList);
						},
						error : function(data) {
							callback();
						}
					});
				});
			});
			
			function loadStatesWithoutClear(){
				var stateIdsSelect = $("#states")[0].selectize
				if($('#editUserMatrixModal').hasClass('show')){
					stateIdsSelect = $("#editStates")[0].selectize;
				}
				//stateIdsSelect.clear();
				stateIdsSelect.clearOptions();
				stateIdsSelect.load(function(callback) {
					var clIds = [];
					var $clusters = $('#clusters').selectize();
					if($('#editUserMatrixModal').hasClass('show')){
						$clusters = $("#editClusters").selectize();
					}
					var clusterSelectize = $clusters[0].selectize;
					if($("#clusters option:selected").text().includes('All') || $("#clusters").val().length==0){
						clIds = Object.keys(clusterSelectize.options);
						if($("#clusters option:selected").text().includes('All')){
							clusterSelectize.clear();
							clusterSelectize.setValue('All',true);
							clusterSelectize.close();
							
						}
					}
					else{
						clIds = $("#clusters").val();					
					}
					if($('#editUserMatrixModal').hasClass('show')){			
						clIds = $("#editClusters").val();
						if($("#editClusters option:selected").text().includes('All')){
							clusterSelectize.clear();
							clusterSelectize.setValue('All',true);
							clusterSelectize.close();	
							clIds = Object.keys(clusterSelectize.options);
						}
						if($("#editClusters").val().length==0){
							clIds = Object.keys(clusterSelectize.options);
						}						
					}
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getStatesByClusterIds',
						data : {
							clusters : function(){
								return clIds;
							}
						},
						success : function(data) {
							var stateList = [];
							var states = [];
							for(var obj of data){
								var state = new Object();
								state['text'] = obj.state;
								state['value'] = [obj.state];
								stateList.push(state);
								states.push(obj.id);
							}
							if(stateList){
								var state = new Object();
								state['text'] = 'All';
								state['value'] = 'All';
								stateList.push(state);
							}
							callback(stateList);
						},
						error : function(data) {
							callback();
						}
					});
				});
			}
			
			function loadBranchesWithoutClear(){
				var branchCodeSelect = $("#branchCodes")[0].selectize;
				if($('#editUserMatrixModal').hasClass('show')){
					branchCodeSelect = $("#editBranchCodes")[0].selectize;
				}
				//branchCodeSelect.clear();
				branchCodeSelect.clearOptions();
				branchCodeSelect.load(function(callback) {
				var stIds = [];
				var $stateIds = $('#states').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$stateIds = $('#editStates').selectize();
				}
				var stateSelectize = $stateIds[0].selectize;
				if($("#states option:selected").text().includes('All') || $("#states").val().length==0){
					stIds = Object.keys(stateSelectize.options);
					if($("#states option:selected").text().includes('All')){
						stateSelectize.clear();
						stateSelectize.setValue('All',true);
						stateSelectize.close();
					}			
				}else{
					stIds = $("#states").val();
				}
				if($('#editUserMatrixModal').hasClass('show')){
					stIds = $("#editStates").val();
					if($("#editStates option:selected").text().includes('All')){
						stateSelectize.clear();
						stateSelectize.setValue('All',true);
						stateSelectize.close();
						stIds = Object.keys(stateSelectize.options);
					}
					if($("#editStates").val().length==0){
						stIds = Object.keys(stateSelectize.options);
					}
				}
				$.ajax({
					type : "GET",
					contentType: 'application/json; charset=utf-8',
					url : '/admin/getBranchesByStateIds',
					data : {
						states : function(){
							return stIds;
						}
					},
					success : function(data) {
						var branchList = [];
						var branchCodes = [];
						for(var obj of data){
							var branch = new Object();
							branch['text'] = obj.revisedBranchName+" ("+ obj.branchCode+")";
							branch['value'] = [obj.branchCode];		
							branchList.push(branch);
							branchCodes.push(obj.branchCode);
						}
						if(branchList){
							var branch = new Object();
							branch['text'] = 'All';
							branch['value'] = 'All';
							branchList.push(branch);
						}
						callback(branchList);
					},
					error : function(data) {
						callback();
					}
				});
				});
			}
						
			$('#states,#editStates').on('change',function() {
				var branchCodeSelect = $("#branchCodes")[0].selectize;
				if($('#editUserMatrixModal').hasClass('show')){
					branchCodeSelect = $("#editBranchCodes")[0].selectize;
				}
				//branchCodeSelect.clear();
				branchCodeSelect.clearOptions();
				branchCodeSelect.load(function(callback) {
				var stIds = [];
				var $stateIds = $('#states').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$stateIds = $('#editStates').selectize();
				}
				var stateSelectize = $stateIds[0].selectize;
				if($("#states option:selected").text().includes('All') || $("#states").val().length==0){
					stIds = Object.keys(stateSelectize.options);
					if($("#states option:selected").text().includes('All')){
						stateSelectize.clear();
						stateSelectize.setValue('All',true);
						stateSelectize.close();
					}			
				}else{
					stIds = $("#states").val();
				}
				if($('#editUserMatrixModal').hasClass('show')){
					stIds = $("#editStates").val();
					if($("#editStates option:selected").text().includes('All')){
						stateSelectize.clear();
						stateSelectize.setValue('All',true);
						stateSelectize.close();
						stIds = Object.keys(stateSelectize.options);
					}
					if($("#editStates").val().length==0){
						stIds = Object.keys(stateSelectize.options);
					}
				}
				$.ajax({
					type : "GET",
					contentType: 'application/json; charset=utf-8',
					url : '/admin/getBranchesByStateIds',
					data : {
						states : function(){
							return stIds;
						}
					},
					success : function(data) {
						var branchList = [];
						var branchCodes = [];
						for(var obj of data){
							var branch = new Object();
							branch['text'] = obj.revisedBranchName+" ("+ obj.branchCode+")";
							branch['value'] = [obj.branchCode];		
							branchList.push(branch);
							branchCodes.push(obj.branchCode);
						}
						if(branchList){
							var branch = new Object();
							branch['text'] = 'All';
							branch['value'] = 'All';
							branchList.push(branch);
						}
						callback(branchList);
					},
					error : function(data) {
						callback();
					}
				});
				});
			});
			
			$('#branchCodes,#editBranchCodes').on('change',function() {
				var $branchCodes = $('#branchCodes').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$branchCodes = $('#editBranchCodes').selectize();
				}
				var branchSelectize = $branchCodes[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editBranchCodes option:selected").text().includes('All')){
					branchSelectize.clear();
					branchSelectize.setValue('All',true);
					branchSelectize.close();						
				}
				else if($("#branchCodes option:selected").text().includes('All') || $("#branchCodes").val().length==0){
					if($("#branchCodes option:selected").text().includes('All')){
						branchSelectize.clear();
						branchSelectize.setValue('All',true);
						branchSelectize.close();						
					}
				}
			});
			
			$('#products,#editProducts').on('change',function() {
				var $product = $('#products').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$product = $('#editProducts').selectize();
				}
				var productSelectize = $product[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editProducts option:selected").text().includes('All')){
					if($("#editProducts option:selected").text().includes('All')){
						productSelectize.clear();
						productSelectize.setValue('All',true);
						productSelectize.close();						
					}
				}
				else if($("#products option:selected").text().includes('All') || $("#products").val().length==0){
					if($("#products option:selected").text().includes('All')){
						productSelectize.clear();
						productSelectize.setValue('All',true);
						productSelectize.close();						
					}
				}
			});
			
			$('#businessTypes,#editBusinessTypes').on('change',function() {
				var $businessTypes = $('#businessTypes').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$businessTypes = $('#editBusinessTypes').selectize();
				}
				var businessTypesSelectize = $businessTypes[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editBusinessTypes option:selected").text().includes('All')){
						businessTypesSelectize.clear();
						businessTypesSelectize.setValue('All',true);
						businessTypesSelectize.close();						
				}
				else if($("#businessTypes option:selected").text().includes('All') || $("#businessTypes").val().length==0){
					if($("#businessTypes option:selected").text().includes('All')){
						businessTypesSelectize.clear();
						businessTypesSelectize.setValue('All',true);
						businessTypesSelectize.close();						
					}
				}
			});
			
			$('#lobs,#editLobs').on('change',function() {
				var $businessTypes = $('#lobs').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$businessTypes = $('#editLobs').selectize();
				}
				var businessTypesSelectize = $businessTypes[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editLobs option:selected").text().includes('All')){
						businessTypesSelectize.clear();
						businessTypesSelectize.setValue('All',true);
						businessTypesSelectize.close();						
				}
				else if($("#lobs option:selected").text().includes('All') || $("#lobs").val().length==0){
					if($("#lobs option:selected").text().includes('All')){
						businessTypesSelectize.clear();
						businessTypesSelectize.setValue('All',true);
						businessTypesSelectize.close();						
					}
				}
			});
			
			$('#channels,#editChannels').on('change',function() {
				var $channels = $('#channels').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$channels = $('#editChannels').selectize();
				}
				var channelsSelectize = $channels[0].selectize;
				var channelNames = [];
				if($("#channels option:selected").text().includes('All') || $("#channels").val().length==0){
					if($("#channels option:selected").text().includes('All')){
						channelsSelectize.clear();
						channelsSelectize.setValue('All',true);
						channelsSelectize.close();						
					}
					channelNames = Object.keys(channelsSelectize.options);
				}else{
					channelNames = $("#channels").val();
				}
				if($('#editUserMatrixModal').hasClass('show')){
					if($("#editChannels option:selected").text().includes('All')){
						channelsSelectize.clear();
						channelsSelectize.setValue('All',true);
						channelsSelectize.close();						
					}else if($("#editChannels").val().length==0 || $("#editChannels option:selected").text().includes('All')){
						channelNames = Object.keys(channelsSelectize.options);
					}else{
						channelNames = $("#editChannels").val();
					}					
				}
				var $subChannels = $('#subChannels').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$subChannels = $('#editSubChannels').selectize();
				}
				var subChannelsSelectize = $subChannels[0].selectize;
				subChannelsSelectize.clear();
				subChannelsSelectize.clearOptions();
				subChannelsSelectize.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getSubchannelsByChannels',
						data : {
							channelNames : function(){
								return channelNames;
							}
						},
						success : function(data) {
							var subChannelList = [];
							for(var obj of data){
								var subChannel = new Object();
								subChannel['text'] = obj.subChannel;
								subChannel['value'] = obj.subChannel;		
								subChannelList.push(subChannel);
							}
							if(subChannelList){
								var subChannel = new Object();
								subChannel['text'] = 'All';
								subChannel['value'] = 'All';
								subChannelList.push(subChannel);
							}
							callback(subChannelList);
						},
						error : function(data) {
							callback();
						} 
					}); 
					
				});
			});
			
			function loadSubChannelsWithoutClear(){
				var $channels = $('#channels').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$channels = $('#editChannels').selectize();
				}
				var channelsSelectize = $channels[0].selectize;
				var channelNames = [];
				if($("#channels option:selected").text().includes('All') || $("#channels").val().length==0){
					if($("#channels option:selected").text().includes('All')){
						channelsSelectize.clear();
						channelsSelectize.setValue('All',true);
						channelsSelectize.close();						
					}
					channelNames = Object.keys(channelsSelectize.options);
				}else{
					channelNames = $("#channels").val();
				}
				if($('#editUserMatrixModal').hasClass('show')){
					if($("#editChannels option:selected").text().includes('All')){
						channelsSelectize.clear();
						channelsSelectize.setValue('All',true);
						channelsSelectize.close();						
					}else if($("#editChannels").val().length==0 || $("#editChannels option:selected").text().includes('All')){
						channelNames = Object.keys(channelsSelectize.options);
					}else{
						channelNames = $("#editChannels").val();
					}					
				}
				var $subChannels = $('#subChannels').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$subChannels = $('#editSubChannels').selectize();
				}
				var subChannelsSelectize = $subChannels[0].selectize;
				//subChannelsSelectize.clear();
				subChannelsSelectize.clearOptions();
				subChannelsSelectize.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getSubchannelsByChannels',
						data : {
							channelNames : function(){
								return channelNames;
							}
						},
						success : function(data) {
							var subChannelList = [];
							for(var obj of data){
								var subChannel = new Object();
								subChannel['text'] = obj.subChannel;
								subChannel['value'] = obj.subChannel;		
								subChannelList.push(subChannel);
							}
							if(subChannelList){
								var subChannel = new Object();
								subChannel['text'] = 'All';
								subChannel['value'] = 'All';
								subChannelList.push(subChannel);
							}
							callback(subChannelList);
						},
						error : function(data) {
							callback();
						} 
					}); 
					
				});
			}
			
			$('#subChannels,#editSubChannels').on('change',function() {
				var $subChannels = $('#subChannels').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$subChannels = $('#editSubChannels').selectize();
				}
				var subChannelsSelectize = $subChannels[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editSubChannels option:selected").text().includes('All')){
					subChannelsSelectize.clear();
					subChannelsSelectize.setValue('All',true);
					subChannelsSelectize.close();						
				}
				else if($("#subChannels option:selected").text().includes('All') || $("#subChannels").val().length==0){
					if($("#subChannels option:selected").text().includes('All')){
						subChannelsSelectize.clear();
						subChannelsSelectize.setValue('All',true);
						subChannelsSelectize.close();						
					}
				}
			});
			
			$('#makes,#editMakes').on('change',function() {
				var $makes = $('#makes').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$makes = $('#editMakes').selectize();
				}
				var makesSelectize = $makes[0].selectize;
				var makes = [];
				if($('#editUserMatrixModal').hasClass('show')){
					if($("#editMakes option:selected").text().includes('All')){
						makesSelectize.clear();
						makesSelectize.setValue('All',true);
						makesSelectize.close();	
					}
					if( $("#editMakes").val().length==0){
						modelAllLoad();
						return;
					}
					else{
						makes = $("#editMakes").val();
					}
				}
				else if($("#makes option:selected").text().includes('All') || $("#makes").val().length==0){
					if($("#makes option:selected").text().includes('All')){
						makesSelectize.clear();
						makesSelectize.setValue('All',true);
						makesSelectize.close();	
					}
					if( $("#makes").val().length==0){
						modelAllLoad();
						return;
					}
					
				}else{
					makes = $("#makes").val();
				}
				var $models = $('#models').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$models = $('#editModels').selectize();
				}
				var modelsSelectize = $models[0].selectize;
				modelsSelectize.clear();
				modelsSelectize.clearOptions();
				modelsSelectize.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getModelByMakes',
						data : {
							makes : function(){
								return makes;
							}
						},
						success : function(data) {
							var modelList = [];
							for(var obj of data){
								var model = new Object();
								model['text'] = obj.modelName;
								model['value'] = obj.modelName;		
								modelList.push(model);
							}
							if(modelList){
								var model = new Object();
								model['text'] = 'All';
								model['value'] = 'All';
								modelList.push(model);
							}
							callback(modelList);
						},
						error : function(data) {
							callback();
						} 
					});
				});
			});
			
			function loadModelsWithoutClear(){
				var $makes = $('#makes').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$makes = $('#editMakes').selectize();
				}
				var makesSelectize = $makes[0].selectize;
				var makes = [];
				if($('#editUserMatrixModal').hasClass('show')){
					if($("#editMakes option:selected").text().includes('All')){
						makesSelectize.clear();
						makesSelectize.setValue('All',true);
						makesSelectize.close();	
					}
					if( $("#editMakes").val().length==0){
						modelAllLoad();
						return;
					}
					else{
						makes = $("#editMakes").val();
					}
				}
				else if($("#makes option:selected").text().includes('All') || $("#makes").val().length==0){
					if($("#makes option:selected").text().includes('All')){
						makesSelectize.clear();
						makesSelectize.setValue('All',true);
						makesSelectize.close();	
					}
					if( $("#makes").val().length==0){
						modelAllLoad();
						return;
					}
					
				}else{
					makes = $("#makes").val();
				}
				var $models = $('#models').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$models = $('#editModels').selectize();
				}
				var modelsSelectize = $models[0].selectize;
				//modelsSelectize.clear();
				modelsSelectize.clearOptions();
				modelsSelectize.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getModelByMakes',
						data : {
							makes : function(){
								return makes;
							}
						},
						success : function(data) {
							var modelList = [];
							for(var obj of data){
								var model = new Object();
								if(obj.modelName){
									model['text'] = obj.modelName;	
								}else{
									model['text'] = obj.modelCode;
								}
								model['value'] = obj.modelName;		
								modelList.push(model);
							}
							if(modelList){
								var model = new Object();
								model['text'] = 'All';
								model['value'] = 'All';
								modelList.push(model);
							}
							callback(modelList);
						},
						error : function(data) {
							callback();
						} 
					});
				});
			}
			
			/*var models = $("#models")[0].selectize;
			models.load(function(callback) {
				$.ajax({
					type : "GET",
					contentType : "application/json",
					url : '/admin/getAllModels',
					success : function(data) {
						var modelList = [];
						for(var obj of data){
							var model = new Object();
							model['text'] = obj.modelName;
							model['value'] = obj.modelName;
							modelList.push(model);
						}
						if(modelList){
							var model = new Object();
							model['text'] = 'All';
							model['value'] = 'All';
							modelList.push(model);
						}
						callback(modelList);
					},
					error : function(data) {
						callback();
					}
				});
			});*/
			
			var models = $("#gwpModels")[0].selectize;
			models.load(function(callback) {
				$.ajax({
					type : "GET",
					contentType : "application/json",
					url : '/getAllModels',
					success : function(data) {
						var modelList = [];
						for(var obj of data){
							var model = new Object();
							model['text'] = obj;
							model['value'] = obj;
							modelList.push(model);
						}
						if(modelList){
							var model = new Object();
							model['text'] = 'All';
							model['value'] = 'All';
							modelList.push(model);
						}
						callback(modelList);
					},
					error : function(data) {
						callback();
					}
				});
			});
			
			var models = $("#motorModels")[0].selectize;
			models.load(function(callback) {
				$.ajax({
					type : "GET",
					contentType : "application/json",
					url : '/getAllModels',
					success : function(data) {
						var modelList = [];
						for(var obj of data){
							var model = new Object();
							model['text'] = obj;
							model['value'] = obj;
							modelList.push(model);
						}
						if(modelList){
							var model = new Object();
							model['text'] = 'All';
							model['value'] = 'All';
							modelList.push(model);
						}
						callback(modelList);
					},
					error : function(data) {
						callback();
					}
				});
			});
			
			
			function modelAllLoad(){
				var models = $("#models")[0].selectize;
				if($('#editUserMatrixModal').hasClass('show')){
					models = $("#editModels")[0].selectize;
				}
				models.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/admin/getAllModels',
						success : function(data) {
							var modelList = [];
							for(var obj of data){
								var model = new Object();
								if(obj.modelName){
									model['text'] = obj.modelName;	
								}else{
									model['text'] = obj.modelCode;
								}
								model['value'] = obj.modelName;
								modelList.push(model);
							}
							if(modelList){
								var model = new Object();
								model['text'] = 'All';
								model['value'] = 'All';
								modelList.push(model);
							}
							callback(modelList);
						},
						error : function(data) {
							callback();
						}
					});
				});
			}
			
			$('#models,#editModels').on('change',function() {
				var $models = $('#models').selectize();
				if($('#editUserMatrixModal').hasClass('show')){
					$models = $('#editModels').selectize();
				}
				var modelsSelectize = $models[0].selectize;
				if($('#editUserMatrixModal').hasClass('show') && $("#editModels option:selected").text().includes('All')){
					modelsSelectize.clear();
					modelsSelectize.setValue('All',true);
					modelsSelectize.close();						
				}
				else if($("#models option:selected").text().includes('All') || $("#models").val().length==0){
					if($("#models option:selected").text().includes('All')){
						modelsSelectize.clear();
						modelsSelectize.setValue('All',true);
						modelsSelectize.close();						
					}
				}
			});
						
			$('#roleIds').on('change',function() {
				var cluster = $("#clusters").selectize();
				var clusterSelectize = cluster[0].selectize;
				var state = $("#states").selectize();
				var stateSelectize = state[0].selectize;
				var branch = $("#branchCodes").selectize();
				var branchSelectize = branch[0].selectize;
				if($("#roleIds option:selected").text() == 'ZONE_HEAD'){
					clusterSelectize.clear();
					stateSelectize.clear();
					branchSelectize.clear();
					clusterSelectize.disable();
					stateSelectize.disable();
					branchSelectize.disable();
				}else if($("#roleIds option:selected").text() == 'CLUSTER_HEAD'){
					clusterSelectize.enable();
					stateSelectize.clear();
					stateSelectize.disable();
					branchSelectize.clear();
					branchSelectize.disable();
				}else if($("#roleIds option:selected").text() == 'STATE_HEAD'){	
					clusterSelectize.enable();
					stateSelectize.enable();
					branchSelectize.clear();
					branchSelectize.disable();
				}else{
					clusterSelectize.enable();
					stateSelectize.enable();
					branchSelectize.enable();
				}
			});
		
			var collapsedGroups = {};
			
			var collapsedGroups = {};
			
			//matrixpage table
			var userMatrixListTable = $('#userMatrixListDT').DataTable( {
			    //data:generateData(),
			      columns: [
			          { data: '',"width": "40px"},
			          { data: 'userId'},
			          { data: 'userName'},
			          { data: 'role'},
			          { data: '',"width": "80px"},
			      ],
			      "scrollX": true,
			      "scrollY": true,
			      "scrollY": "440px",
			      "scrollCollapse": true,
			      "pageLength": 10,
			      "dom":'<"top align-items-end "f>rt<"bottom"lp><"clear">',
			      "language": {
			        "paginate": {
			          "previous": "&lt",
			          "next": "&gt;",
			        },
			        "searchPlaceholder": "Search...",
			        "lengthMenu": "Show _MENU_",
			        "search": "",
			      },
			      "pagingType": "simple_numbers",
			      'columnDefs': [
			          {
			          'targets': 0,
			          'className': 'dt-body-center',
			          'searchable':false,
			          'orderable':false,
			          'render': function (data, type, full, meta){
			              return '<input type="checkbox" class="checkbox-group1-child" value="' + $('<div/>').text(data).html() + '">';
			              },
			          },
			          {
			              'targets': 1,
			              'className': 'dt-body-center',
			              'searchable':false,
			              'orderable':false,
			              'visible':false
			          },
			          {
			              'targets': 4,
			              'searchable':false,
			              'orderable':false,
			              'className': 'dt-body-center',
			              'render': function (data, type, full, meta){
			                  return '<span class="fa fa-pencil" style="cursor:pointer"></span>';
			                  },
			          },
			      ],
			} );
			
/*			var userMatrixListTable = $('#userMatrixListDT').DataTable({
				 "ajax" : {
					"url": "/admin/userMatrixResponseByDashboardId",
					"data": function(data){
						return $('#reportTypeList option:selected').val();
					},
					"dataSrc": ""
				},
				"columns" : {
					"data": null,
					"data" : null,
					"data": "userId",
					"data": "userName",
					"data": "userMatrixRole",
					"data": null
				}, 
				"paging" : true,
				"lengthChange" : true,
				"searching" : true,
				"ordering" : true,
				"info" : true,
				"autoWidth" : false,
				"responsive" : true,
				"columnDefs" : [  {
					"orderable" : false,
					"className" : 'select-checkbox',
					"visible" : false,
					"targets" : 1
				},{
					"orderable" : false,
					"className" : 'details-control',
					"targets" : 0
				},{
					"className" : 'gwp-col',
					"targets" : 3
				}],
				"select" : {
					"style" : 'single'
				},
				dom : "<'row'<'col-sm-12'B>><'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
				"<'row'<'col-sm-12'tr>>" +
				"<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
				buttons : [ {
					text : 'Delete'	,
					action : function(e, dt, node, config) {
						
						var items = $.map(userMatrixListTable.rows('.selected')
								.data(), function(item) {
							return item;
						});				
						var matrixList = [];	
						
						for(var i = 0 ; i<=items.length; i++){
							if(i % 5 == 0){
								if(i==0){
									var matrix = new Object();
									continue;
								}
								matrixList.push(matrix);
								var matrix = new Object();
							}
							if(i % 6 == 2){
								matrix["userId"] = items[i];
							}
							if(i % 6 == 3){
								matrix["userName"] = items[i];
							}
							if(i % 6 == 4){
								matrix["userMatrixRole"] = items[i];
							}
							
						}
						if(matrixList.length){
							$('#deleteConfirmUserModal').modal('show');									
							$("#deleteUserContent").text('Are you sure to delete?');
							$('#confirmUserDelete').click(function() {
								$('#deleteConfirmUserModal').modal('hide');
								$('#deleteConfirmUserModal').modal('hide');
								$.ajax({
									type : "POST",
									contentType : "application/json",
									url : '/admin/deleteUserMatrix',
									data : JSON.stringify(matrixList),
									success : function(data) {											
										toastr.clear();
										toastr.warning(matrixList.length+' Matrix Deleted Sucessfully');
										userMatrixListTable.rows('.selected').remove().draw(false);
									},
									error : function(data){
										
									}
								});
								
							});
						}
					}
				}]
			});*/
			
			$('#userMatrixDeleteButton').on('click',function(){
				var items = $.map(userMatrixListTable.rows('.selected')
						.data(), function(item) {
					return item;
				});				
				var matrixList = [];	
				
				for(var i = 0 ; i<=items.length; i++){
					if(i % 5 == 0){
						if(i==0){
							var matrix = new Object();
							continue;
						}
						matrixList.push(matrix);
						var matrix = new Object();
					}
					if(i % 6 == 2){
						matrix["userId"] = items[i];
					}
					if(i % 6 == 3){
						matrix["userName"] = items[i];
					}
					if(i % 6 == 4){
						matrix["userMatrixRole"] = items[i];
					}
					
				}
				console.log(items)
				console.log(matrixList);
				if(matrixList.length){
//					$('#deleteConfirmUserModal').modal('show');									
//					$("#deleteUserContent").text('Are you sure to delete?');
//					$('#confirmUserDelete').click(function() {
//						$('#deleteConfirmUserModal').modal('hide');
//						$('#deleteConfirmUserModal').modal('hide');
						/*$.ajax({
							type : "POST",
							contentType : "application/json",
							url : '/admin/deleteUserMatrix',
							data : JSON.stringify(matrixList),
							success : function(data) {											
								toastr.clear();
								toastr.warning(matrixList.length+' Matrix Deleted Sucessfully');
								userMatrixListTable.rows('.selected').remove().draw(false);
							},
							error : function(data){
								
							}
						});*/
						
//					});
				}
			});
			
			var roleColumn = userMatrixListTable.column(4);
			roleColumn.visible(false);
			
			$('#reportTypeList').on('change', function () {
				//userMatrixListTable
				var column = userMatrixListTable.column(4);	
				if($("#reportTypeList option:selected").text() == 'GWP'){					
			        column.visible(true);
				}else{
					column.visible(false);
				}
				userMatrixListTable.columns(5).search( $("#reportTypeList option:selected").text() ).draw();
			});
			
			$('#userMatrixListDT tbody tr').on('click', 'tr', function () {
		        /*var tr = $(this).closest('tr');
		        var row = userMatrixListTable.row( tr );
		        console.log(row.data)*/
				
				var row = userMatrixListTable.rows('.selected').data()[0];
				console.log(row)
		 		lastSelectedMatrixUser['userId'] = row.data()[2];
		 		lastSelectedMatrixUser['userName'] = row.data()[3];
			});
						
			$('#userMatrixListDT tbody').on('click', 'td.details-control', function () {
		        var tr = $(this).closest('tr');
		        var row = userMatrixListTable.row( tr );
		        if ( row.child.isShown() ) {
		            // This row is already open - close it
		            //row.child.hide();
		            destroyChild(row);
		            tr.removeClass('shown');
		        }
		        else {
		            // Open this row
		            createChild(row,'child-table');
		            tr.addClass('shown');
		        }
		    } );
			
			$("#editUserMatrixModal").on('show.bs.modal', function () {
				var editModels = $("#editModels")[0].selectize;
				editModels.load(function(callback) {
				$.ajax({
					type : "GET",
					contentType : "application/json",
					url : '/admin/getAllModels',
					success : function(data) {
						var modelList = [];
						for(var obj of data){
							var model = new Object();
							if(obj.modelName){
								model['text'] = obj.modelName;	
							}else{
								model['text'] = obj.modelCode;
							}
							model['value'] = obj.modelName;
							modelList.push(model);
						}
						if(modelList){
							var model = new Object();
							model['text'] = 'All';
							model['value'] = 'All';
							modelList.push(model);
						}
						callback(modelList);
					},
					error : function(data) {
						callback();
					} 
				});
			  });
				
			$.ajax({
				type : "GET",
				contentType : "application/json",
				url : '/admin/getUserMatrixChildByUserId',
				data : {
					userId: function(){
						return lastSelectedMatrixUser.userId;
					}
				},
				success : function(data) {		
					if(data.userIds){
						$('#editUserIds').val(data.userIds);
					}
					if(data.userMatrixRoleName){
						var $vals = $('#editRoles').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.userMatrixRoleName, true);
						selectize.close();
					}else {
						var $vals = $('#editRoles').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					if(data.zones){
						var $vals = $('#editZones').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.zones, true);
						selectize.close();
						setTimeout(() => {  
							loadClustersWithoutClear();
						},100);
					}else{
						var $vals = $('#editZones').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						setTimeout(() => {  
							loadClustersWithoutClear();
						},100);
					}
					if(data.clusters){
						var $vals = $('#editClusters').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.clusters, true);
						selectize.close();
						setTimeout(() => {  
							loadStatesWithoutClear();
						},0);
					}else{
						var $vals = $('#editClusters').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						setTimeout(() => {  
							loadStatesWithoutClear();
						},0);
					}
					if(data.states){
						var $vals = $('#editStates').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.states, true);
						selectize.close();
						setTimeout(() => {  
							//loadBranchesWithoutClear();
						},0);
					}else{
						var $vals = $('#editStates').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						setTimeout(() => {  
							//loadBranchesWithoutClear();
						},0);
					}
					if(data.branchCodes){
						setTimeout(() => {  
						var $vals = $('#editBranchCodes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.branchCodes, true);
						selectize.close();
						},100);
					}else{
						setTimeout(() => {  
						var $vals = $('#editBranchCodes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						},100);
					}
					if(data.products){
						var $vals = $('#editProducts').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.products, true);
						selectize.close();
					}else{
						var $vals = $('#editProducts').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					if(data.businessTypes){
						var $vals = $('#editBusinessTypes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.businessTypes, true);
						selectize.close();
					}else{
						var $vals = $('#editBusinessTypes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					if(data.lobs){
						var $vals = $('#editLobs').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.lobs, true);
						selectize.close();
					}else{
						var $vals = $('#editLobs').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					if(data.channels){
						var $vals = $('#editChannels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.channels, true);
						selectize.close();
						setTimeout(() => {  
							loadSubChannelsWithoutClear();
						},1000);
					}else{
						var $vals = $('#editChannels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						setTimeout(() => {  
							loadSubChannelsWithoutClear();
						},1000);
					}
					if(data.subChannels){
						var $vals = $('#editSubChannels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.subChannels, true);
						selectize.close();
					}else{
						var $vals = $('#editSubChannels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					if(data.makes){
						var $vals = $('#editMakes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.makes, true);
						selectize.close();
						setTimeout(() => {  
							loadModelsWithoutClear();
						},100);
					}else{
						var $vals = $('#editMakes').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
						setTimeout(() => {  
							loadModelsWithoutClear();
						},100);
					}
					if(data.models){
						
						var $vals = $('#editModels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue(data.models, true);
						selectize.close();
					}else{
						var $vals = $('#editModels').selectize();
						var selectize = $vals[0].selectize;
						selectize.setValue('', true);
						selectize.close();
					}
					
				},
				error : function(data) {
					$('#editUserIds').val(lastSelectedMatrixUser.userIds);
				} 
			});
			});
			
			$('#generalDiv').hide();
			$('#gwpDiv').hide();
			$('#healthDiv').hide();
			$('#motorDiv').hide();
			
			$("#reportType").on('change', function () {
				var $reportType = $('#reportType').selectize();				
				var makesSelectize = $reportType[0].selectize;
				if($("#reportType option:selected").text().includes('General')){
					$('#generalDiv').show();
					$('#gwpDiv').hide();
					$('#healthDiv').hide();
					$('#motorDiv').hide();
				}else if($("#reportType option:selected").text().includes('GWP')){
					$('#gwpDiv').show();
					$('#generalDiv').hide();
					$('#healthDiv').hide();
					$('#motorDiv').hide();
				}else if($("#reportType option:selected").text().includes('Health')){
					$('#healthDiv').show();
					$('#generalDiv').hide();
					$('#gwpDiv').hide();
					$('#motorDiv').hide();
				}else if($("#reportType option:selected").text().includes('Motor')){
					$('#motorDiv').show();
					$('#generalDiv').hide();
					$('#gwpDiv').hide();
					$('#healthDiv').hide();
				}else{
					$('#generalDiv').hide();
					$('#gwpDiv').hide();
					$('#healthDiv').hide();
					$('#motorDiv').hide();
				}
				/* if($('#editUserMatrixModal').hasClass('show')){
					if($("#editMakes option:selected").text().includes('All')){
						makesSelectize.clear();
						makesSelectize.setValue('All',true);
						makesSelectize.close();	
					}
					if( $("#editMakes").val().length==0){
						modelAllLoad();
						return;
					}
					else{
						makes = $("#editMakes").val();
					}
				} */
				
			});
			
		});

		});
		function createChild ( row ) {
			var rowData = row.data();
		    // This is the table we'll convert into a DataTable
		    var table = $('<table class="display text-center" width="100%"/><thead><th>id</th><th>Dimension</th><th>Value</th></thead></table>');
 
		    // Display it the child row
		    row.child( table ).show();
		    
		    // Initialise as a DataTable
		    var userMatrixChildTable = table.DataTable( {
		    	"paging" : true,
		    	"searching" : true,
				"ordering" : true,
				"info" : true,
				"autoWidth" : false,
				"responsive" : true,
				"deferRender": true,
				"lengthChange" : true,
				"pageLength": 5,
				"lengthMenu": [ 5, 10, 25, 50 ],
				"dom" : "<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
				"<'row'<'col-sm-12'tr>>" +
				"<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>", 
		    	"columnDefs" : [{
					'visible' : false,
					'searchable' : false,
					'targets' : [ 0 ]
				} ],
				"order" : [ [ 0, "asc" ] ],				
		    	"ajax" : {		    		
		    		url : "/admin/userMatrixByUserId",
		    		contentType : "application/json",
		    		dataSrc : '',
		    		data: {
		    			"userId": rowData[2]
		    		}
		    	},
		    	columns: [
		    		{ "data": "id"},
		            { "data": "userDimension"},
		            { "data": "dimensionValue"}
		    	]
		    	
		    } );
		}
		
		function destroyChild(row) {
			var table = $("table", row.child());
		    table.detach();
		    table.DataTable().destroy();
		 
		    // And then hide the row
		    row.child.hide();
		}	

		$('#reportType').change(function(e){
			var selectSelectize = $('#userMatrixFormDiv select:not("#reportType")').selectize();
			console.log(selectSelectize.length);
			for(var i=0; i<selectSelectize.length; i++){
				var selectSelectizeVals = selectSelectize[i].selectize;
				selectSelectizeVals.clear();
			}
		});
		
		jQuery(document).ready(function() {
			var motorModel = $("#gwpModels");
			if(motorModel[0]){
				var models = motorModel[0].selectize;
				models.load(function(callback) {
					$.ajax({
						type : "GET",
						contentType : "application/json",
						url : '/getAllModels',
						success : function(data) {
							var modelList = [];
							for(var obj of data){
								var model = new Object();
								model['text'] = obj;
								model['value'] = obj;
								modelList.push(model);
							}
							if(modelList){
								var model = new Object();
								model['text'] = 'All';
								model['value'] = 'All';
								modelList.push(model);
							}
							callback(modelList);
						},
						error : function(data) {
							callback();
						}
					});
				});
			}
		});
		
