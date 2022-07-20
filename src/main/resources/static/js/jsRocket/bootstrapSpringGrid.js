
var omnicheckURL = "";

$(document).ready(function(){
	
	$("#reportTbl").hide();

	if($("#screenId").val()=="T"){
		getAvailableSlotsByChannel();
		getSelectedSlotsByChannel();
	}if($("#screenId").val()=="M"){
		getSelectedSlotsByChannelForMessage();
	}
	
	$("#channel option[value='5']").prop('disabled',true);
	$("#channel option[value='6']").prop('disabled',true);
	
	 $('#forgetPasswordModal').on('show.bs.modal', function(e) {
		 $('#forgetPasswordModal .input-lg').val('');
	 });
	 
	 $('#changePasswordModal').on('hidden.bs.modal', function () {
		 window.location.href = "/rsRobo/login";
	 });
	 
	$("input[name='processState']").on("change", function() {
		showLoader();
		var isChecked = $(this).is(':checked');
		var selectedData;
		var $switchLabel = $('.switch-label');

		if(isChecked) {
			selectedData = $switchLabel.attr('data-on');
		} else {
			selectedData = $switchLabel.attr('data-off');
		}

		$.ajax({
			type: "GET",
			url: "/rsRobo/controlroute",
			timeout : 100000,
			data: { processState: selectedData, processName: $(this).attr("value") },
			success: function(data){
				hideLoader();
				toastr.clear();
				toastr.warning(data);
				setTimeout(function() 
						{
						location.reload();  //Refresh page
						}, 8000);
			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
		});
	});

	$(".statusFilter").hide();
	// Activated the table
	var tableUser = $('#tableUser').DataTable({
		"autoWidth": false,
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getAllUsers",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableUser.row.add([
		            			                      obj.id,
		            			                      "<input type='checkbox' value='"+obj.id+"' id=''>",
		            			                      obj.username,
		            			                      obj.emailId,
		            			                      obj.phoneNo,
		            			                      obj.active,
		            			                      "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.id+"' data-target='#userModal'>Edit</a>",
		            			                      ]).draw();
		            		   });
		            	   }
		               },
	});


	$('#userModal').on('show.bs.modal', function(e) {

		var $modal = $(this),
		esseyId = e.relatedTarget.id;
		$.ajax({
			cache: false,
			type: 'POST',
			url: "/rsRobo/viewUser",
			data: { id: esseyId },
			success: function(data) {
				$(".modal-body #usernameModal").val( data.username );
				$(".modal-body #emailIdModal").val( data.emailId );
				$(".modal-body #phoneNoModal").val( data.phoneNo );
				$(".modal-body #roleNameModal").val( data.roleName );
				if(data.processName.indexOf(",")!=-1){
					$.each(data.processName.split(","), function(i,e){
						$(".modal-body #processNameModal option[value='" + e + "']").prop("selected", true);
					});
				}else{
					$(".modal-body #processNameModal").val( data.processName );
				}
			},
			error: function(e){
				toastr.clear();toastr.error("Record not found.");
			}
		});
	});


	var tableEmailConfig = $('#tableEmailConfig').DataTable({
		"autoWidth": false,
		"fnDrawCallback": function( oSettings ) {
			$("#tableEmailConfig tbody tr").find("td:eq(2),td:eq(3)").css("max-width","200px");
		},
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getEmailDetails",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableEmailConfig.row.add([
		            			                             obj.id,
		            			                             "<input type='checkbox' value='"+obj.id+"' id=''>",
		            			                             obj.processName,
		            			                             obj.toEmailIds,
		            			                             obj.ccEmailIds,
		            			                             obj.status,
		            			                             "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.id+"' data-target='#emailConfigModal'>Edit</a>",
		            			                             ]).draw();
		            		   });
		            	   }
		               },
	});

	$('#emailConfigModal').on('show.bs.modal', function(e) {

		var $modal = $(this),
		esseyId = e.relatedTarget.id;
		$.ajax({
			cache: false,
			type: 'POST',
			url: "/rsRobo/viewEmailConfigModal",
			data: { id: esseyId },
			success: function(data) {
				$(".emailConfigDynamicDivs").remove();
				$(".modal-body #processNameModal option").hide();
				$("#emailConfigId").val(data.id);
				if(data.toEmailIds.indexOf(",")!=-1){
					var toEmailIdsArr = data.toEmailIds.split(",");
					for(var i =0;i<toEmailIdsArr.length;i++){
						if(i==0){
							$(".modal-body #toEmailIdsModal_0").val( toEmailIdsArr[i] );
						}else{
							var txt ='<div class="input-group m-b-1 toemail_VW emailConfigDynamicDivs"><span class="input-group-addon">@</span> <input  name="toEmailIdsModal" type="text" class="form-control" placeholder="TO" value="'+toEmailIdsArr[i]+'" /><span class="input-group-addon" onclick="removeToEmail(this);" style="font-size:x-large;">-</span></div>';
							$("#toEmailIdsModalDiv").append(txt);
						}
					}
				}else{
					$(".modal-body #toEmailIdsModal_0").val( data.toEmailIds );
				}

				if(data.ccEmailIds!=null){
					if(data.ccEmailIds.indexOf(",")!=-1){
						var ccEmailIdsArr = data.ccEmailIds.split(",");
						for(var i =0;i<ccEmailIdsArr.length;i++){
							if(i==0){
								$(".modal-body #ccEmailIdsModal_0").val( ccEmailIdsArr[i] );
							}else{
								var txt ='<div class="input-group m-b-1 copyemail_VW emailConfigDynamicDivs"><span class="input-group-addon">@</span> <input  name="ccEmailIdsModal" type="text" class="form-control" placeholder="CC" value="'+ccEmailIdsArr[i]+'" /><span class="input-group-addon" onclick="removeCopyEmail(this);" style="font-size:x-large;">-</span></div>';
								$("#ccEmailIdsModalDiv").append(txt);
							}
						}
					}else{
						$(".modal-body #ccEmailIdsModal_0").val( data.ccEmailIds );
					}
				}

				if(data.processName.indexOf(",")!=-1){
					$.each(data.processName.split(","), function(i,e){
						$(".modal-body #processNameModal option[value='" + e + "']").prop("selected", true);
						$(".modal-body #processNameModal option[value='" + e + "']").show();
					});
				}else{
					$(".modal-body #processNameModal").val( data.processName );
					$(".modal-body #processNameModal option[value='" + data.processName + "']").show();
				}
			},
			error: function(e){
				toastr.clear();toastr.error("Record not found.");
			}
		});
	});

	var tableTransaction = $('#tableTransaction').DataTable( {
	
	} );

	$("#pickerDateBirth").datetimepicker({
		format: 'DD/MM/YYYY'
	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	$("#startDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:new Date(),
		showClose:true
		//language:"en"

	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	$("#endDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:new Date(),
		showClose:true
	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});

	$(window).load(function() {

	});

	$("#buttonRefresh").click(function(){

		tableUser.clear().draw();
		tableUser.ajax.reload();

	});

	$("#buttonEmailConfigRefresh").click(function(){
		tableEmailConfig.clear().draw();
		tableEmailConfig.ajax.reload();
	});

	$("#buttonInsert").click(function(){
		//toastr.clear();toastr.warning
		if (!validateUserName($("#username").val())) {
			toastr.clear();toastr.warning('Please enter a valid User Name.');
			return false;
		} else if (!validateEmail($("#emailId").val())) {
			toastr.clear();toastr.warning('Please enter a valid Email Id.');
			return false;
		} else if (!validatePhoneNumber($("#phoneNo").val())) {
			toastr.clear();toastr.warning('Please enter a valid Phone Number.');
			return false;
		} else if($("#roleName").val()=="0"){
			toastr.clear();toastr.warning("Please select a Role.")
			return false;
		} else if($("#processName").val()==null){
			toastr.clear();toastr.warning("Please select a Business Process.")
			return false;
		}

		$(this).callAjax("insertUser", "");

		$(".form-control").val("");

	});

	$("#buttonUpdate").click(function(){

		if (!validateEmail($("#emailIdModal").val())) {
			toastr.clear();toastr.warning('Please enter a valid Email Id.');
			return false;
		} else if (!validatePhoneNumber($("#phoneNoModal").val())) {
			toastr.clear();toastr.warning('Please enter a valid Phone Number.');
			return false;
		} else if($("#roleNameModal").val()==""){
			toastr.clear();toastr.warning("Please select a Role.")
			return false;
		} else if($("#processNameModal").val()==null){
			toastr.clear();toastr.warning("Please select a Business Process.")
			return false;
		}

		var valuesChecked = $("#tableUser input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		$(this).callUpdateAjax("updateUser", valuesChecked);

	});

	$("#buttonDelete").click(function(){

		var valuesChecked = $("#tableUser input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one user");
			return false;
		}else{
			$(this).callAjax("deleteUser", valuesChecked);
		}

	});

	$("#buttonEmailConfigDelete").click(function(){

		var valuesChecked = $("#tableEmailConfig input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one Process");
			return false;
		}else{
			$(this).callAjax("deleteEmailConfig", valuesChecked);
		}

	});

	$("#buttonEmailConfigActivate").click(function(){

		var valuesChecked = $("#tableEmailConfig input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one Process");
			return false;
		}else{
			$(this).callAjax("activateEmailConfig", valuesChecked);
		}

	});


	$("#buttonBlock").click(function(){

		var valuesChecked = $("#tableUser input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");
		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one user");
			return false;
		}else{
			$(this).callAjax("blockUser", valuesChecked);
		}

	});

	$("#buttonActivate").click(function(){

		var valuesChecked = $("#tableUser input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");
		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one user");
			return false;
		}else{
			$(this).callAjax("activateUser", valuesChecked);
		}

	});

	$("#buttonResetPwd").click(function(){

		var valuesChecked = $("#tableUser input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one user");
			return false;
		}else{
			if (confirm("Are you sure you want to reset password?")) {
				$(this).callAjax("resetPassword", valuesChecked);
			} else {
				return false;
			}
		}

	});

	$("#changePasswordBtn").click(function(){
		var value = $("#cpassword").val();
		if(value.length<4){
			toastr.clear();toastr.warning("Password should have min length 0f 4");
			return false;
		}else if($("#cpassword").val()!=$("#repassword").val()){
			toastr.clear();toastr.warning("Passwords should be same");
			return false;
		}else{
			$("#changePasswordBtn").prop("disabled",false);
			$(this).callPwdAjax("changePassword", "");
			$(".form-control").val("");
		}
	});


	$("#forgetPasswordBtn").click(function(){

		if($("#reg_username").val()==""){
			toastr.clear();toastr.warning("Username cannot be empty");
			return false;
		}else if($("#reg_email").val()==""){
			toastr.clear();toastr.warning("Email cannot be empty");
			return false;
		}else{
			$(this).callchangePwdAjax("getNewPassword", "");
		}

	});


	$("#buttonFilter").click(function(){

		if($("#startDate").val()=="") {
			toastr.clear();toastr.warning('Please enter a valid Start Date.');
			return false;
		} else if($("#endDate").val()=="") {
			toastr.clear();toastr.warning('Please enter a valid End Date.');
			return false;
		} else if($("#processName").val()==""){
			toastr.clear();toastr.warning("Please select a Process.")
			return false;
		}

		$(this).filterTransactionDetailAjax("filterTransactionDetails", "");

	});




	$.fn.callAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#processName").val()!=null && $("#processName").val()!=undefined){
			commaSeparatedValues =$("#processName").val().join(",");
		}

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { username: $("#username").val(), emailId: $("#emailId").val(), phoneNo: $("#phoneNo").val(), roleName: $("#roleName").val(), 
				processName: commaSeparatedValues, checked: checkeds },

				success: function(data){
					tableUser.clear().draw();
					tableUser.ajax.reload();
					tableEmailConfig.clear().draw();
					tableEmailConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}


	$.fn.callUpdateAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#processNameModal").val()!=null && $("#processNameModal").val()!=undefined){
			commaSeparatedValues =$("#processNameModal").val().join(",");
		}

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { username: $("#usernameModal").val(), emailId: $("#emailIdModal").val(), phoneNo: $("#phoneNoModal").val(), roleName: $("#roleNameModal").val(), 
				processName: commaSeparatedValues, checked: checkeds },

				success: function(data){
					tableUser.clear().draw();
					tableUser.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	} 

	$.fn.filterTransactionDetailAjax = function( method, checkeds ){
		showLoader();
		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			dataType: "json",
			timeout : 100000,
			data: { startDate: $("#startDate").val(), endDate: $("#endDate").val(), processName: $("#processName").val(), checked: checkeds },

			"success" :  function(data){
				hideLoader();
				tableTransaction.clear().draw();
				$.each(data, function(ind, obj){

					var FetchId="#viewTranModal";
					
						$("#tableTransaction thead").find("th:eq(3)").text("Transaction Status");
						
						tableTransaction.row.add([
						                          obj.id,
						                          "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.id+"' data-target='"+FetchId+"'>Fetch Results</a>",
						                          obj.processName,
						                          obj.transactionStatus,
						                          obj.startDate,
						                          obj.endDate
						                          ]).draw();


				});
				//toastr.clear();toastr.info("Action completed successfully.");
			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
		});

	}

	$('#viewTranModal').on('show.bs.modal', function(e) {

		var $modal = $(this),
		esseyId = e.relatedTarget.id;
		$.ajax({
			cache: false,
			type: 'POST',
			url: "/rsRobo/viewTransaction",
			data: { id: esseyId },
			success: function(data) {
				$(".modal-body #transactionId").val( data.id );
				$(".modal-body #externalTransactionRefNo").val( data.externalTransactionRefNo );
				$(".modal-body #transactionStatus").val( data.transactionStatus );
				$(".modal-body #processPhase").val( data.processPhase );
				$(".modal-body #processStatus").val( data.processStatus );
				$(".modal-body #processSuccessReason").val( data.processSuccessReason );
				$(".modal-body #processFailureReason").val( data.processFailureReason );
				$(".modal-body #totalSmsTobSent").val( data.totalSmsTobSent );
				$(".modal-body #slotExpiredConfigured").val( data.slotExpiredConfigured );
				$(".modal-body #slotNonExpiredConfigured").val( data.slotNonExpiredConfigured );
				$(".modal-body #totalSmsSent").val( data.totalSuccessRecords );
				/*$(".modal-body #errorFilePath").val( data.errorFileDownload );
				$(".modal-body #successFilePath").val( data.successFileDownload );
				$(".modal-body #uploadFilePath").val( data.uploadFileDownload );*/
				//$(".modal-body #glStartDate").val(moment(data.transactionStartDate ,"x").format("DD/MM/YYYY"));
			/*	$(".modal-body #ReprocessedFlag").val( data.reprocessedFlag );
				$(".modal-body #oldRunNo").val( data.oldRunNo );
				$(".modal-body #newRunNo").val( data.runNo );*/

				if( data.runNo !=null &&  data.runNo != "" )
				{
					$(".modal-body .hideDiv").hide();
				}	
				else
				{
					$(".modal-body .hideDiv").show();
				}	
				
				
				getTransactionExceptionLog(data.id,"vb64_exceptionLog");
			},
			error: function(e){
				toastr.clear();toastr.error("Record not found.");
			}
		});
	});

	

	$.fn.callPwdAjax = function( method, checkeds ){
		showLoader();
		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { userId: $("#userId").val(), password: $("#cpassword").val() },

			success: function(data){
				hideLoader();
				toastr.clear();toastr.info(data);
				location.reload();

			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
		});
	} 

	$.fn.callchangePwdAjax = function( method, checkeds ){
		showLoader();
		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { username: $("#reg_username").val(), email : $("#reg_email").val() },

			success: function(data){
				if(data=="Temporary password sent to ur email"){
					toastr.success(data);
					$('#forgetPasswordModal .input-lg').val('');
				}else{
					
					toastr.warning(data);
				}
				hideLoader();
			},
			error: function(e){
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				hideLoader();
			}
		});
	}

	$(".addToMail").click(function(){
		var txt ='<div class="input-group m-b-1 toemail"><span class="input-group-addon">@</span> <input  name="toEmailIds" type="text" class="form-control" placeholder="TO" /><span class="input-group-addon" onclick="removeToEmail(this);" style="font-size:x-large;">-</span></div>';
		$("#toEmailDiv").append(txt);
	});

	$(".addCcMail").click(function(){
		var txt ='<div class="input-group m-b-1 copyemail"><span class="input-group-addon">@</span> <input  name="ccEmailIds" type="text" class="form-control" placeholder="CC" /><span class="input-group-addon" onclick="removeCopyEmail(this);" style="font-size:x-large;">-</span></div>';
		$("#ccEmailDiv").append(txt);
	});

	$(".addToMail_Modal").click(function(){
		var txt ='<div class="input-group m-b-1 toemail_VW emailConfigDynamicDivs"><span class="input-group-addon">@</span> <input  name="toEmailIdsModal" type="text" class="form-control" placeholder="TO"  /><span class="input-group-addon" onclick="removeToEmail(this);" style="font-size:x-large;">-</span></div>';
		$("#toEmailIdsModalDiv").append(txt);
	});

	$(".addCcMail_Modal").click(function(){
		var txt ='<div class="input-group m-b-1 copyemail_VW emailConfigDynamicDivs"><span class="input-group-addon">@</span> <input  name="ccEmailIdsModal" type="text" class="form-control" placeholder="CC"  /><span class="input-group-addon" onclick="removeCopyEmail(this);" style="font-size:x-large;">-</span></div>';
		$("#ccEmailIdsModalDiv").append(txt);
	});

	$("#buttonEmailConfigInsert").click(function(){
		$(this).buttonEmailConfigInsertAjax("insertEmailConfig", "");
	});

	$("#buttonEmailConfigUpdate").click(function(){
		$(this).updateEmailConfigAjax("updateEmailConfig", "");
	});

	$.fn.buttonEmailConfigInsertAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#processName").val()!=null && $("#processName").val()!=undefined){
			commaSeparatedValues =$("#processName").val().join(",");
		}else{
			hideLoader();
			toastr.warning("Please Select atleast one process");
			return false;
		}

		var toEmailIds = "";
		$("input[name='toEmailIds']").each(function(){
			debugger;
			if($(this).val()!=""){
				toEmailIds +=$(this).val()+",";
			}
		});
		if(toEmailIds==""){
			hideLoader();
			toastr.warning("Please enter atleast one TO: Recipient");
			return false;
		}

		var ccEmailIds = "";
		$("input[name='ccEmailIds']").each(function(){
			debugger;
			if($(this).val()!=""){
				ccEmailIds +=$(this).val()+",";
			}
		});

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { toEmailIds: toEmailIds, ccEmailIds: ccEmailIds, processName: commaSeparatedValues},

			success: function(data){
				tableEmailConfig.clear().draw();
				tableEmailConfig.ajax.reload();
				toastr.clear();toastr.info(data);
				hideLoader();
				if(data=="Email configuration added successfully"){
				$(".toemail,.copyemail").remove();
				$("#toEmailIds,#ccEmailIds").val('');
				$("#processName option:selected").prop("selected", false);
				}
			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
		});
	}

	$.fn.updateEmailConfigAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#processNameModal").val()!=null && $("#processNameModal").val()!=undefined){
			commaSeparatedValues =$("#processNameModal").val().join(",");
		}else{
			hideLoader();
			toastr.warning("Please Select atleast one process");
			return false;
		}

		var toEmailIds = "";
		$("input[name='toEmailIdsModal']").each(function(){
			debugger;
			if($(this).val()!=""){
				toEmailIds +=$(this).val()+",";
			}
		});
		if(toEmailIds==""){
			hideLoader();
			toastr.warning("Please enter atleast one TO: Recipient");
			return false;
		}

		var ccEmailIds = "";
		$("input[name='ccEmailIdsModal']").each(function(){
			debugger;
			if($(this).val()!=""){
				ccEmailIds +=$(this).val()+",";
			}
		});

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { id : $("#emailConfigId").val(), toEmailIds: toEmailIds, ccEmailIds: ccEmailIds, processName: commaSeparatedValues},

			success: function(data){
				tableEmailConfig.clear().draw();
				tableEmailConfig.ajax.reload();
				toastr.clear();toastr.info(data);
				hideLoader();
			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
		});
	}
	
	$("#buttonApplicationConfigRefresh").click(function(){

		tableApplicationConfig.clear().draw();
		tableApplicationConfig.ajax.reload();

	});
	
	$("#buttonApplicationConfigInsert").click(function(){
		$(this).callAppProcessAjax("insertApplicationConfig", "");
	});
	
	$("#buttonApplicationConfigUpdate").click(function(){
		$(this).callAppProcessUpdateAjax("updateApplicationConfig", "");
	});
	
	$.fn.callAppProcessAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#appDetail_processName").val()!=null && $("#appDetail_processName").val()!=undefined && $("#appDetail_processName").val()!=""){
			commaSeparatedValues = $("#appDetail_processName").val();
		}else{
			hideLoader();
			toastr.warning("Please Select process");
			return false;
		}
		
		var appName ="";
		if($("#appDetail_appName").val()!=null && $("#appDetail_appName").val()!=undefined && $("#appDetail_appName").val()!=""){
			appName = $("#appDetail_appName").val();
		}else{
			hideLoader();
			toastr.warning("Please Select application");
			return false;
		}
		
		var url = "";
		if($("#appDetail_url").val()!=null && $("#appDetail_url").val()!=undefined && $("#appDetail_url").val()!=""){
			url = $("#appDetail_url").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter application url");
			return false;
		}
		
		var username = "";
		if($("#appDetail_username").val()!=null && $("#appDetail_username").val()!=undefined && $("#appDetail_username").val()!=""){
			username = $("#appDetail_username").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter username");
			return false;
		}
		
		var password = "";
		if($("#appDetail_password").val()!=null && $("#appDetail_password").val()!=undefined && $("#appDetail_password").val()!=""){
			password = $("#appDetail_password").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter password");
			return false;
		}

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { appName: appName, url: url, username :username,password:password,processName: commaSeparatedValues},

				success: function(data){
					tableApplicationConfig.clear().draw();
					tableApplicationConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					$("#appDetail_processName,#appDetail_appName,#appDetail_url,#appDetail_username,#appDetail_password").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	$.fn.callAppProcessUpdateAjax = function( method, checkeds ){
		showLoader();
		var commaSeparatedValues ="";
		if($("#appDetail_processNameVw").val()!=null && $("#appDetail_processNameVw").val()!=undefined){
			commaSeparatedValues = $("#appDetail_processNameVw").val();
		}else{
			hideLoader();
			toastr.warning("Please Select process");
			return false;
		}
		
		var appName ="";
		if($("#appDetail_appNameVw").val()!=null && $("#appDetail_appNameVw").val()!=undefined){
			appName = $("#appDetail_appNameVw").val();
		}else{
			hideLoader();
			toastr.warning("Please Select application");
			return false;
		}
		var isPasswordExpired = "";
		if($("#appDetail_isPasswordExpiredVw").val()!=null && $("#appDetail_isPasswordExpiredVw").val()!=undefined && $("#appDetail_isPasswordExpiredVw").val()!=""){
			isPasswordExpired = $("#appDetail_isPasswordExpiredVw").val();
		}else{
			hideLoader();
			//toastr.warning("Please Enter application url");
			return false;
		}
		
		var url = "";
		if($("#appDetail_urlVw").val()!=null && $("#appDetail_urlVw").val()!=undefined && $("#appDetail_urlVw").val()!=""){
			url = $("#appDetail_urlVw").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter application url");
			return false;
		}
		
		var username = "";
		if($("#appDetail_usernameVw").val()!=null && $("#appDetail_usernameVw").val()!=undefined && $("#appDetail_usernameVw").val()!=""){
			username = $("#appDetail_usernameVw").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter username");
			return false;
		}
		
		var password = "";
		if($("#appDetail_passwordVw").val()!=null && $("#appDetail_passwordVw").val()!=undefined && $("#appDetail_passwordVw").val()!=""){
			password = $("#appDetail_passwordVw").val();
		}else{
			hideLoader();
			toastr.warning("Please Enter password");
			return false;
		}
	

	$.ajax({
		type: "POST",
		url: "/rsRobo/" + method,
		timeout : 100000,
		data: { id: $("#appProcess_id").val() , appName: appName, url: url, username :username,password:password,processName: commaSeparatedValues, isPasswordExpired: isPasswordExpired},
			success: function(data){
				tableApplicationConfig.clear().draw();
				tableApplicationConfig.ajax.reload();
				toastr.clear();toastr.info(data);
				hideLoader();
			},
			error: function(e){
				hideLoader();
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
	});
}

	var tableApplicationConfig = $('#tableApplicationConfig').DataTable({
		"autoWidth": false,
		/* "fnDrawCallback": function( oSettings ) {
			 $("#tableProcessMail tbody tr").find("td:eq(2),td:eq(3)").css("max-width","200px");
		    },*/
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getApplicationConfigurationDetails",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableApplicationConfig.row.add([
		            			                      obj.id,
		            			                      "<input type='checkbox' value='"+obj.id+"' id=''>",
		            			                    /*  obj.processId,*/
		            			                      obj.processName,
		            			                      obj.appName,
		            			                      obj.status,
		            			                      "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.id+"' data-target='#applicationConfigModal'>Edit</a>",
		            			                      ]).draw();
		            		   });
		            	   }
		               },
		              
	});
	
	$('#applicationConfigModal').on('show.bs.modal', function(e) {

		var $modal = $(this),
		esseyId = e.relatedTarget.id;
		$.ajax({
			cache: false,
			type: 'POST',
			url: "/rsRobo/viewApplicationConfigModal",
			data: { id: esseyId },
			success: function(data) {
					$("#appProcess_id").val(data.id);
					$("#appDetail_processNameVw").val(data.processName);
					$("#appDetail_appNameVw").val(data.appId);
					$("#appDetail_urlVw").val(data.url);
					$("#appDetail_usernameVw").val(data.username);
					$("#appDetail_passwordVw").val(data.password);
					$("#appDetail_isPasswordExpiredVw").val(data.isPasswordExpired);
			},
			error: function(e){
				toastr.clear();toastr.error("Record not found.");
			}
		});
	});
	
	$("#buttonApplicationConfigDelete").click(function(){

		var valuesChecked = $("#tableApplicationConfig input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one Process");
			return false;
		}else{
			$(this).callAjax("deleteApplicationConfig", valuesChecked);
		}

	});

	$("#buttonApplicationConfigActivate").click(function(){

		var valuesChecked = $("#tableApplicationConfig input[type='checkbox']:checkbox:checked").map(
				function () {
					return this.value;
				}).get().join(",");

		if(valuesChecked==""){
			toastr.clear();toastr.warning("Please select atleast one Process");
			return false;
		}else{
			$(this).callAjax("activateApplicationConfig", valuesChecked);
		}

	});
	
	
	var tableProcessConfig = $('#tableProcessConfig').DataTable({
		"autoWidth": false,
		/* "fnDrawCallback": function( oSettings ) {
			 $("#tableProcessMail tbody tr").find("td:eq(2),td:eq(3)").css("max-width","200px");
		    },*/
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getProcessConfigurationDetails",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableProcessConfig.row.add([
		            			                      ind+1,
		            			                      obj.id,
		            			                      obj.processDesc,
		            			                      obj.processName,
		            			                      "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.id+"' desc_id='"+obj.processDesc+"' name_id='"+obj.processName+"'  data-target='#processConfigModal'>Edit</a>",
		            			                      ]).draw();
		            		   });
		            	   }
		               },
		              
	});
	
	
	$('#processConfigModal').on('show.bs.modal', function(e) {

		$("#processConfig_processDescEdit").val(e.relatedTarget.getAttribute('desc_id'));
		$("#processConfig_processNameEdit").val(e.relatedTarget.getAttribute('name_id'));
		$("#processConfig_processIdEdit").val(e.relatedTarget.id);
	});
	
	
	$("#buttonProcessConfigRefresh").click(function(){

		tableProcessConfig.clear().draw();
		tableProcessConfig.ajax.reload();

	});
	
	
	$("#buttonProcessConfigInsert").click(function(){
		$(this).callProcessConfigAjax("insertProcessConfig", "");
	});
	
	
	$.fn.callProcessConfigAjax = function( method, checkeds ){
		showLoader();
		/*var processId ="";
		if($("#processConfig_id").val()!=null && $("#processConfig_id").val()!=undefined && $("#processConfig_id").val()!=""){
			processId = $("#processConfig_id").val();
		}else{
			hideLoader();
			toastr.warning("Process Id cannot be empty");
			return false;
		}*/
		
		var processDesc ="";
		if($("#processConfig_desc").val()!=null && $("#processConfig_desc").val()!=undefined && $("#processConfig_desc").val()!=""){
			processDesc = $("#processConfig_desc").val();
		}else{
			hideLoader();
			toastr.warning("Process Desc cannot be empty");
			return false;
		}
		
		var processName ="";
		if($("#processConfig_name").val()!=null && $("#processConfig_name").val()!=undefined && $("#processConfig_name").val()!=""){
			processName = $("#processConfig_name").val();
		}else{
			hideLoader();
			toastr.warning("Process Name cannot be empty");
			return false;
		}
		

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { /*processId: processId,*/ processDesc :processDesc,processName:processName},

				success: function(data){
					tableProcessConfig.clear().draw();
					tableProcessConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					$("#processConfig_desc,#processConfig_name,#processConfig_id").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	
	$("#buttonProcessConfigUpdate").click(function(){
		$(this).callProcessConfigAjaxUpdate("updateProcessConfig");
	});
	
	$.fn.callProcessConfigAjaxUpdate = function( method ){
		showLoader();

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { processId: $("#processConfig_processIdEdit").val(),processDesc: $("#processConfig_processDescEdit").val() , processName: $("#processConfig_processNameEdit").val()},

				success: function(data){
					tableProcessConfig.clear().draw();
					tableProcessConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	} 
	
	$("#buttonProcessConfigDelete").click(function(){
		$(this).callProcessConfigAjaxDelete("processConfigDelete");
	});
	
	$.fn.callProcessConfigAjaxDelete = function( method ){
		showLoader();

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { processId: $("#processConfig_processIdEdit").val()},

				success: function(data){
					tableProcessConfig.clear().draw();
					tableProcessConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	} 
	
	var tableUserProcessConfig = $('#tableUserProcessConfig').DataTable({
		"autoWidth": false,
		/* "fnDrawCallback": function( oSettings ) {
			 $("#tableProcessMail tbody tr").find("td:eq(2),td:eq(3)").css("max-width","200px");
		    },*/
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getUserProcessDetails",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableUserProcessConfig.row.add([
		            			                      ind+1,
		            			                      obj.userId,
		            			                      obj.processID,
		            			                      "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.userId+"' process_id='"+obj.processID+"'  data-target='#userProcessConfigModal'>Delete</a>",
		            			                      ]).draw();
		            		   });
		            	   }
		               },
		              
	});
	
	
	$('#userProcessConfigModal').on('show.bs.modal', function(e) {

		$("#userProcessConfig_userIdEdit").val(e.relatedTarget.getAttribute('id'));
		$("#userProcessConfig_processIdEdit").val(e.relatedTarget.getAttribute('process_id'));
	});
	
	
	$("#buttonUserProcessConfigRefresh").click(function(){

		tableUserProcessConfig.clear().draw();
		tableUserProcessConfig.ajax.reload();

	});
	
	
	$("#buttonUserProcessConfigInsert").click(function(){
		$(this).callUserProcessConfigAjax("insertUserProcessConfig", "");
	});
	
	
	$.fn.callUserProcessConfigAjax = function( method, checkeds ){
		showLoader();
		
		var userid ="";
		if($("#userProcess_userid").val()!=null && $("#userProcess_userid").val()!=undefined && $("#userProcess_userid").val()!=""){
			userid = $("#userProcess_userid").val();
		}else{
			hideLoader();
			toastr.warning("User Id cannot be empty");
			return false;
		}
		
		var processId ="";
		if($("#UserProcess_processid").val()!=null && $("#UserProcess_processid").val()!=undefined && $("#UserProcess_processid").val()!=""){
			processId = $("#UserProcess_processid").val();
		}else{
			hideLoader();
			toastr.warning("Process Id cannot be empty");
			return false;
		}
		

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { userId : userid ,processId:processId},

				success: function(data){
					tableUserProcessConfig.clear().draw();
					tableUserProcessConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					$("#userProcess_userid,#UserProcess_processid").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	
	$("#buttonUserProcessConfigDelete").click(function(){
		$(this).callUserProcessConfigAjaxDelete("userProcessConfigDelete");
	});
	
	$.fn.callUserProcessConfigAjaxDelete = function( method ){
		showLoader();

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { userId: $("#userProcessConfig_userIdEdit").val(),processId: $("#userProcessConfig_processIdEdit").val() },

				success: function(data){
					tableUserProcessConfig.clear().draw();
					tableUserProcessConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	} 
	
	
	
	
	var tableParamConfig = $('#tableParamConfig').DataTable({
		"autoWidth": false,
		/* "fnDrawCallback": function( oSettings ) {
			 $("#tableProcessMail tbody tr").find("td:eq(2),td:eq(3)").css("max-width","200px");
		    },*/
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false}
		               ],
		               "ajax": {
		            	   "url": "/rsRobo/getParamConfigDetails",
		            	   "type": "POST",
		            	   "success" :  function(data){
		            		   $.each(data, function(ind, obj){

		            			   tableParamConfig.row.add([
		            			                      ind+1,
		            			                      obj.paramKey,
		            			                      obj.paramValue,
		            			                      "<a href='#myModal' class='btn btn-info' data-toggle='modal' id='"+obj.paramKey+"' paramValue_id='"+obj.paramValue+"'  data-target='#paramConfigModal'>Edit</a>",
		            			                      ]).draw();
		            		   });
		            	   }
		               },
		              
	});
	
	
	$('#paramConfigModal').on('show.bs.modal', function(e) {

		$("#paramConfig_paramKeyEdit").val(e.relatedTarget.getAttribute('id'));
		$("#paramConfig_paramValueEdit").val(e.relatedTarget.getAttribute('paramValue_id'));
	});
	
	
	$("#buttonParamConfigRefresh").click(function(){

		tableParamConfig.clear().draw();
		tableParamConfig.ajax.reload();

	});
	
	
	$("#buttonParamConfigInsert").click(function(){
		$(this).callParamConfigAjax("insertParamConfig", "");
	});
	
	
	$.fn.callParamConfigAjax = function( method, checkeds ){
		showLoader();
		
		var paramKey ="";
		if($("#paramConfig_paramKey").val()!=null && $("#paramConfig_paramKey").val()!=undefined && $("#paramConfig_paramKey").val()!=""){
			paramKey = $("#paramConfig_paramKey").val();
		}else{
			hideLoader();
			toastr.warning("Param key cannot be empty");
			return false;
		}
		
		var paramValue ="";
		if($("#paramConfig_paramValue").val()!=null && $("#paramConfig_paramValue").val()!=undefined && $("#paramConfig_paramValue").val()!=""){
			paramValue = $("#paramConfig_paramValue").val();
		}else{
			hideLoader();
			toastr.warning("Param value cannot be empty");
			return false;
		}
		

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { paramKey : paramKey ,paramValue:paramValue},

				success: function(data){
					tableParamConfig.clear().draw();
					tableParamConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					$("#paramConfig_paramKey,#paramConfig_paramValue").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	
	$("#buttonParamConfigDelete").click(function(){
		$(this).callParamConfigAjaxDelete("paramConfigDelete");
	});
	
	$.fn.callParamConfigAjaxDelete = function( method ){
		showLoader();

		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { paramKey: $("#paramConfig_paramKeyEdit").val(),paramValue: $("#paramConfig_paramValueEdit").val() },

				success: function(data){
					tableParamConfig.clear().draw();
					tableParamConfig.ajax.reload();
					toastr.clear();toastr.info(data);
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	} 
	
	
	$("#buttonParamConfigUpdate").click(function(){
		$(this).callParamConfigAjaxDelete("paramConfigUpdate");
	});

	function validateIntermediaryNo(sIntermediaryNo) {
		var filter = /^[0-9a-zA-Z',-]+$/;
		if (filter.test(sIntermediaryNo)) {
			return true;
		}
		else {
			return false;
		}
	}

	function validateUserName(sUserName) {
		var filter = /^[a-zA-Z]+$/;
		if (filter.test(sUserName)) {
			return true;
		}
		else {
			return false;
		}
	}

	function validateEmail(sEmail) {
		var filter = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
		if (filter.test(sEmail)) {
			return true;
		}
		else {
			return false;
		}
	}

	function validatePhoneNumber(sPhoneNumber) {
		var filter = /\(?([0-9]{3})\)?([ .-]?)([0-9]{3})\2([0-9]{4})/;
		if (filter.test(sPhoneNumber)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	function removeToEmail(thisObj){
		$(thisObj).parent().remove();
	}

	function removeCopyEmail(thisObj){
		$(thisObj).parent().remove();
	}

	function StatusFilter(thisObj){
		if($(thisObj).val()=="LifelineMigrationProcess"){
			$(".statusFilter").show();
		}else{
			$(".statusFilter").hide();
		}
	}
	
	function getAvailableSlotsByChannel(){

		$.ajax({
			type: "POST",
			url: "/rsRobo/getAvailableSlotsByChannel",
			timeout : 100000,
			data: { channelId: $("#channel").val(),pilotFlag : $("#timePilotFlag").val()},

				success: function(data){
					var txt = "";
					$.each(data, function(ind, obj){
						txt += '<div itemid="itm-'+(ind+1)+'" tslot-name='+obj.name+' channel-name='+obj.channelName+' class="btn btn-default box-item item-slot">'+obj.name+'</div>'
					})
					$("#container1").append(txt);
					
					  $('.box-item').draggable({
						    cursor: 'move',
						    helper: "clone"
						  });

						  $("#container1").droppable({
						    drop: function(event, ui) {
						      var itemid = $(event.originalEvent.toElement).attr("itemid");
						      $('.box-item').each(function() {
						        if ($(this).attr("itemid") === itemid) {
						          $(this).appendTo("#container1");
						        }
						      });
						    }
						  });

						 /* $("#container2").droppable({
						    drop: function(event, ui) {
						      var itemid = $(event.originalEvent.toElement).attr("itemid");
						      $('.box-item').each(function() {
						        if ($(this).attr("itemid") === itemid) {
						          $(this).appendTo("#container2");
						        }
						      });
						    }
						  });*/
				},
				error: function(e){
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	function getSelectedSlotsByChannel(){

		$.ajax({
			type: "POST",
			url: "/rsRobo/getSelectedSlotsByChannel",
			timeout : 100000,
			data: { channelId: $("#channel").val(),pilotFlag : $("#timePilotFlag").val()},

				success: function(data){
					var txt = "";
					$.each(data, function(ind, obj){
						txt += '<div itemid="itmS-'+(ind+1)+'" tslot-name='+obj.name+' channel-name='+obj.channelName+' class="btn btn-default box-item item-slot">'+obj.name+'</div>'
					})
					$("#container2").append(txt);
					
					$('.box-item').draggable({
					    cursor: 'move',
					    helper: "clone"
					  });
					
					 $("#container2").droppable({
						    drop: function(event, ui) {
						      var itemid = $(event.originalEvent.toElement).attr("itemid");
						      $('.box-item').each(function() {
						        if ($(this).attr("itemid") === itemid) {
						          $(this).appendTo("#container2");
						        }
						      });
						    }
						  });
				},
				error: function(e){
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
	
	$("#channel").on("change", function() {
		$(".item-slot").remove();
		getAvailableSlotsByChannel();
		getSelectedSlotsByChannel();
	});
	
	$("#timePilotFlag").on("change", function() {
		if($("#timePilotFlag").val()=="P"){
			//$("#channelMessage option[value=5]").hide();
			$("#channel option[value='5']").prop('disabled',true);
			$("#channel option[value='6']").prop('disabled',true);
		}else{
			//$("#channelMessage option[value=5]").show();
			$("#channel option[value='5']").prop('disabled',false);
			$("#channel option[value='6']").prop('disabled',false);
		}
		$(".item-slot").remove();
		if($("#channel").val()!=null){
			getAvailableSlotsByChannel();
			getSelectedSlotsByChannel();
		}
		
	});
	
	$("#buttonSlotButton").click(function(){
		showLoader();
		var slotNames="";
		$("#container2 .item-slot").each(function(){
			slotNames +=$(this).attr('tslot-name')+",";
		});
		
		if(slotNames==""){
			hideLoader();
			toastr.warning("Please select some slot");
			return false;
		}else{
			$.ajax({
				type: "POST",
				url: "/rsRobo/insertSlotValues",
				timeout : 100000,
				data: { channelId: $("#channel").val(),slotNames : slotNames,channelName : $("#channel option:selected").text(),pilotFlag : $("#timePilotFlag").val() },

					success: function(data){
						toastr.success(data);
						//$("#paramConfig_paramKey,#paramConfig_paramValue").val('');
						hideLoader();
					},
					error: function(e){
						hideLoader();
						toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
					}
			});
		}
		
	});
	   
	$("#channelMessage").on("change", function() {
		$(".item-slot").remove();
		getSelectedSlotsByChannelForMessage();
	});
	
	$("#pilotFlag").on("change", function() {
		$(".item-slot").remove();
		if($("#pilotFlag").val()=="N"){
			$(".nonPilot").hide();
		}else{
			$(".nonPilot").show();
		}
		getSelectedSlotsByChannelForMessage();
	});
	
	$("#downloadReport").click(function(){
		
		var contentType="application/octet-stream";
		var linkURL = "?contentType="+contentType+"&fromDate="+$("#fromDate").val()+"&toDate="+$("#toDate").val()+"&channelId="+$("#reportChannel").val()+"&pilotFlag="+$("#reportPilotFlag").val();
		$("#hiddenAnchor").attr("href","/rsRobo/getReportData"+linkURL);
		$("#hiddenAnchor")[0].click();
		
/*		$.ajax({
			type: "POST",
			url: "/rsRobo/getReportData",
			timeout : 100000,
			data: { fromDate: $("#fromDate").val(),toDate : $("#toDate").val(),channelId : $("#reportChannel").val(),pilotFlag : $("#reportPilotFlag").val() },

				success: function(data){
					toastr.success(data);
					//$("#paramConfig_paramKey,#paramConfig_paramValue").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});*/
	});
	
	function getSelectedSlotsByChannelForMessage(){
		$(".chnMsg").remove();
	$.ajax({
		type: "POST",
		url: "/rsRobo/getSelectedSlotsByChannel",
		timeout : 100000,
		data: { channelId: $("#channelMessage").val(),pilotFlag : $("#pilotFlag").val()},

			success: function(data){
				var txt = "",
				basictextarea="";
				engtextarea="";
				ncbtextarea="";
				tyretextarea="";
				nildeptextarea="";
				/*var txt = "<div class='col-xs chnMsg' ><strong>Slot</strong></div>",
				basictextarea="<div class='col-xs chnMsg' ><strong>Basic</strong></div>";
				engtextarea="<div class='col-xs chnMsg' ><strong>ENG</strong></div>";
				ncbtextarea="<div class='col-xs chnMsg' ><strong>NCB</strong></div>";
				tyretextarea="<div class='col-xs chnMsg' ><strong>TYRE</strong></div>";
				nildeptextarea="<div class='col-xs chnMsg' ><strong>NILDEP</strong></div>";*/
				$.each(data, function(ind, obj){
					basictextarea += '<div class="col-xs chnMsg nonPilot" style="height:50px;" ><textarea class="basicAddon"  id="0MMMMMM'+(obj.name).replace('+','')+'" add-on-value="0@@@@@@'+obj.name+'" ></textarea></div>';
					engtextarea += '<div class="col-xs chnMsg nonPilot" style="height:50px;" ><textarea  class="engAddon" id="1MMMMMM'+(obj.name).replace('+','')+'" add-on-value="1@@@@@@'+obj.name+'" ></textarea></div>';
					nildeptextarea += '<div class="col-xs chnMsg nonPilot" style="height:50px;" ><textarea class="nilAddon" id="4MMMMMM'+(obj.name).replace('+','')+'" add-on-value="4@@@@@@'+obj.name+'" ></textarea></div>';
					ncbtextarea += '<div class="col-xs chnMsg nonPilot" style="height:50px;" ><textarea class="ncbAddon" id="2MMMMMM'+(obj.name).replace('+','')+'" add-on-value="2@@@@@@'+obj.name+'" ></textarea></div>';
					tyretextarea += '<div class="col-xs chnMsg nonPilot" style="height:50px;" ><textarea class="tyreAddon" id="3MMMMMM'+(obj.name).replace('+','')+'"  add-on-value="3@@@@@@'+obj.name+'"></textarea></div>';
					txt += '<div class="col-xs chnMsg" style="height:50px;"><b>'+obj.name+'</b></div>';
				})
				$("#slotNameDiv").append(txt);
				$("#basicDiv").append(basictextarea);
				$("#engDiv").append(engtextarea);
				$("#ncbDiv").append(ncbtextarea);
				$("#tyreDiv").append(tyretextarea);
				$("#nildepDiv").append(nildeptextarea);
				getMessageTemplateDataByChannel();
				//$("#messageTemplate").append(textarea);
			},
			error: function(e){
				toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
			}
	});
	}
	
	$("#buttonSubmitButton").click(function(){
		showLoader();
		var basicAddon="",engAddon="",ncbAddon="",tyreAddon="",nilAddon="";
		
		var isAnyBasicIsEmpty="N";
		var focusId = ""; 
		$("#messageTemplate .basicAddon").each(function(){
			if($(this).val()==""){
				isAnyBasicIsEmpty="Y";
				//focusId = $(this);
				return false;
			}else{
				basicAddon +=$(this).attr('add-on-value')+'@@@@@@'+$(this).val()+";;";
			}
		});
		
		$("#messageTemplate .engAddon").each(function(){
			if($(this).val()!=""){
				engAddon +=$(this).attr('add-on-value')+'@@@@@@'+$(this).val()+";;";
			}
		});
		
		$("#messageTemplate .ncbAddon").each(function(){
			if($(this).val()!=""){
				ncbAddon +=$(this).attr('add-on-value')+'@@@@@@'+$(this).val()+";;";
			}
		});
		
		$("#messageTemplate .tyreAddon").each(function(){
			if($(this).val()!=""){
				tyreAddon +=$(this).attr('add-on-value')+'@@@@@@'+$(this).val()+";;";
			}
		});
		
		$("#messageTemplate .nilAddon").each(function(){
			if($(this).val()!=""){
				nilAddon +=$(this).attr('add-on-value')+'@@@@@@'+$(this).val()+";;";
			}
		});
		
		if(isAnyBasicIsEmpty=="Y"){
			hideLoader();
			toastr.warning("Basic Template Message Cannot be Empty");
			//$(focusId).focus();
			return false;
		}else{
			$.ajax({
				type: "POST",
				url: "/rsRobo/insertMessageTemplateValues",
				timeout : 100000,
				data: { channelId: $("#channelMessage").val(),basicAddon : basicAddon,engAddon : engAddon,ncbAddon:ncbAddon,tyreAddon:tyreAddon,nilAddon:nilAddon,pilotFlag : $("#pilotFlag").val() },

					success: function(data){
						toastr.success(data);
						//$("#paramConfig_paramKey,#paramConfig_paramValue").val('');
						hideLoader();
					},
					error: function(e){
						hideLoader();
						toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
					}
			});
		}
		
		
	});
	
	$("#fromDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:new Date(),
		showClose:true
		//language:"en"

	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	$("#toDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:new Date(),
		showClose:true
	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	var fdate = new Date();
	var tdate = new Date()
	$("#campWisefromDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:fdate.setDate(fdate.getDate() - 1),
		showClose:true
		//language:"en"

	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	$("#campWisetoDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:tdate.setDate(tdate.getDate() - 1),
		showClose:true
	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	
	var fpdate = new Date();
	var tpdate = new Date()
	$("#policyWisefromDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:fpdate.setDate(fpdate.getDate() - 1),
		showClose:true
		//language:"en"

	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	$("#policyWisetoDate").datetimepicker({
		format: 'DD/MM/YYYY',
		defaultDate:tpdate.setDate(tpdate.getDate() - 1),
		showClose:true
	}).on('dp.show dp.update', function () {
		$(".picker-switch").css('cursor','none');
		$(".picker-switch").removeAttr('title')
		    //.css('cursor', 'default')  <-- this is not needed if the CSS above is used
		    //.css('background', 'inherit')  <-- this is not needed if the CSS above is used
		    .on('click', function (e) {
		        e.stopPropagation();
		    });
		});
	
	function getMessageTemplateDataByChannel(){
	$.ajax({
		type: "POST",
		url: "/rsRobo/getMessageTemplateDataByChannel",
		timeout : 100000,
		data: { channelId: $("#channelMessage").val(),pilotFlag : $("#pilotFlag").val()},

			success: function(data){
				$.each(data, function(ind, obj){
					$("#"+obj.addonId+'MMMMMM'+(obj.timeSlot).replace('+','')).val(obj.textMessage);
				})
			}
	});
	}
	
	function getTransactionExceptionLog(id,exceptionTextAreaId){
		$.ajax({
			cache: false,
			type: 'POST',
			url: "/rsRobo/getTransactionExceptionLog",
			data: { id: id },
			success: function(data) {
				$("#"+exceptionTextAreaId).val(data);
			}
		});
		}
	
	
	var campaignWiseReportTbl = $('#campaignWiseReportTbl').DataTable({
		"autoWidth": false,
		 dom: 'Bfrtip',
		    buttons: [
		    {
		      extend: 'excel',
		      text: 'Export',
		      className: 'exportExcel',
		      filename: 'Export excel',
		      exportOptions: {
		        modifier: {
		          page: 'all'
		        }
		      }
		    }
		    ],
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false},
		            	   {
		            	        targets: [3,4,5,6],
		            	        className: 'dt-body-right'
		            	    },
		            	   {
		            	        targets: [1,2],
		            	        className: 'dt-body-center'
		            	    }
		               ]
		 
	});
	
	
	var policyWiseReportTbl = $('#policyWiseReportTbl').DataTable({
		"autoWidth": false,
		 dom: 'Bfrtip',
		    buttons: [
		    {
		      extend: 'excel',
		      text: 'Export',
		      className: 'exportExcel',
		      filename: 'Export excel',
		      exportOptions: {
		        modifier: {
		          page: 'all'
		        }
		      }
		    }
		    ],
		"columnDefs": [
		               {"targets": [ 0 ],
		            	   "visible": false,
		            	   "searchable": false},
		            	   {
		            	        targets: [8],
		            	        className: 'dt-body-right'
		            	    },
		            	   {
		            	        targets: [1,2,3,4,5,6,7],
		            	        className: 'dt-body-center'
		            	    }
		               ]
		 
	});
	
	$("#showCampaignWiseReport").click(function(){
		$("#reportTbl").show();
		$(this).callReportAjax("getCampaignWiseReportDetails", "");
	});
	
	$("#showPolicyWiseReport").click(function(){
		$("#reportTbl").show();
		$(this).callReportAjax("getPolicyWiseReportDetails", "");
	});
	
	
	$.fn.callReportAjax = function( method, checkeds ){
		showLoader();
		
		$.ajax({
			type: "POST",
			url: "/rsRobo/" + method,
			timeout : 100000,
			data: { fromDate: $("#policyWisefromDate").val(),toDate : $("#policyWisetoDate").val(),channelId : $("#policyreportChannel").val(),pilotFlag : $("#policyreportPilotFlag").val(),undeliveredOrClick : $("#policyReportType").val()},

				success: function(data){
					policyWiseReportTbl.clear().draw();
					//campaignWiseReportTbl.ajax.reload();
					$.each(data, function(ind, obj){

						policyWiseReportTbl.row.add([
         			                      ind+1,
         			                      obj.policyNo,
         			                      obj.pilotFlag,
         			                      obj.templateFlag,
         			                      obj.channelName,
         			                      obj.campaignName,
         			                      obj.smsSentTime,
         			                     obj.isMsgUndelivered,
         			                     obj.clickCount
         			                      ]).draw();
         		   });
					//toastr.clear();toastr.info(data);
					//$("#paramConfig_paramKey,#paramConfig_paramValue").val('');
					hideLoader();
				},
				error: function(e){
					hideLoader();
					toastr.clear();toastr.error("Internal Server Error. Please contact the Admin.");
				}
		});
	}
});








		
	
		
		
		
		
		
		
