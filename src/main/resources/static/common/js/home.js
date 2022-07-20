$(function() {
			$(document).ready(function() {
			var seletedUserId;
			var seletedUserName;
			var seletedFisrtName;
			var seletedLastName;
			var selectedEmail;
			var selectedStatus;			
			
			/*var userTable = $('#userList').DataTable(
			{
				"paging" : true,
				"lengthChange" : true,
				"searching" : true,
				"ordering" : true,
				"info" : true,
				"autoWidth" : false,
				"responsive" : true,
				"order" : [ [ 1, "desc" ] ],
				"deferRender": true,
				"pageLength": 10,
				"lengthMenu": [ 5, 10, 25, 50, 75, 100 ],
				"columnDefs" : [ {
					"orderable" : false,
					"className" : 'select-checkbox',
					"targets" : 0
				} ],
				"select" : {
					style : 'single'
				},
				dom : "<'row'<'col-sm-12'B>><'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
				"<'row'<'col-sm-12'tr>>" +
				"<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
				buttons : [ {
					text : 'Delete',
					className: "btn-primary",
					action : function(e, dt, node, config) {

						var users = $.map(userTable.rows('.selected')
								.data(), function(item) {
							return item;
						});
						var userObjList = [];
						// html table columns 9						
						for (var i = 0; i <= users.length; i++) {
							if (i % 8 == 0) {//checkbox//row start
								if (i == 0) {
									var user = new Object();
									continue;
								}
								userObjList.push(user);
								var user = new Object();
							}
							if (i % 8 == 1) {//id
								user["id"] = users[i];
							}
							if (i % 8 == 2) {//user name
								user["userName"] = users[i];
							}
							if (i % 8 == 3) {//first name
								user["name"] = users[i];
							}
							if (i % 8 == 4) {//last name
								user["lastName"] = users[i];
							}
							if (i % 8 == 5) {//email
								user["email"] = users[i];
							}
							if (i % 8 == 6) {//status
								if (users[i] == 'Active') {
									user["active"] = true;
								} else {
									user["active"] = false;
								}
							}
							if (i % 8 == 7) {//edit

							}

						}
														
						if(userObjList.length){
							$('#deleteConfirmUserModal').modal('show');
							$("#deleteUserContent").text('Are you sure to delete a user?');
							$('#confirmUserDelete').click(function() {
								$('#deleteConfirmUserModal').modal('hide');
								$.ajax({
									type : "POST",
									contentType : "application/json",
									url : '/admin/deleteUser',
									data : JSON.stringify(userObjList),
									success : function(data) {											
										toastr.clear();
										toastr.warning(userObjList.length+' User\'s Deleted Sucessfully');
										setTimeout(() => {  
											location.reload();
										},1000);
									},
									error : function(data){
										location.replace('/admin/home');
									}
								});
							});
						}						
					}
				} ]
			});*/
			
			var userTable = $('#userList').DataTable( {
			      //data:generateData(),
			        columns: [
			            { data: '',"width": "60px"},
			            { data: 'userId'},
			            { data: 'userName' },
			            { data: 'firstName' },
			            { data: 'lastName' },
			            { data: 'email' },
			            { data: 'status',"width":"80px"},
			            { data: '',"width": "80px" },
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
						"select" : {
							style : 'single'
						},		
			          "pagingType": "simple_numbers",
						"order" : [ [ 1, "desc" ] ],
			          'columnDefs': [
			            {
			            'targets': 0,
			            'searchable':false,
			            'orderable':false,
			            'className': 'text-center',
			            'render': function (data, type, full, meta){
			                return '<input type="checkbox" name="id[]" class="checkbox-group1-child" value="' + $('<div/>').text(data).html() + '">';
			                },
			            },
//			            {
//			                'targets': 6,
//			                'className': 'dt-body-center',
//			                'render': function (data, type, full, meta){
//			                    if(data==true)
//			                        return 'Active';
//			                    else return 'Not Active';
//			                },
//			            },
			            {
			                'targets': 7,
			                'searchable':false,
			                'orderable':false,
			                'className': 'td-small',
			                'render': function (data, type, full, meta){
			                    return '<button class="btn btn-outline-default resetText  explore_fullview_on" style="background-color: transparent;" data-toggle="modal" id="userEditButton" data-target="#editUserModal"><span class="fa fa-pencil" style="cursor:pointer"></span></button>';
			                    },
			            },
			        ],
			} );
			
			var dashboardTable = $('#dashboardList').DataTable( {
			    //data:generateData(),
			      columns: [
			          { data: '',"width": "40px"},
			          { data: 'dashboardId'},
			          { data: 'dashboardName'},
			          { data: 'dashboardUrl',"width":"380px"},
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
					"select" : {
						style : 'single'
					},
			      "pagingType": "simple_numbers",
			      'columnDefs': [
			          {
			          'targets': 0,
			          'className': 'dt-body-center',
			          'searchable':false,
			          'orderable':false,
			          'render': function (data, type, full, meta){
			              return '<input type="checkbox" class="checkbox-group2-child" value="' + $('<div/>').text(data).html() + '">';
			              },
			          },
			          {
			            'targets': 3,
			            'className': 'dashboard-url',
			            'render': function (data, type, full, meta){
			                return `<div class="d-flex justify-content-center align-items-center h-100">
			                        <div class="list-group-item d-flex align-items-center">
			                        <span class="fa fa-external-link pr-1"></span>
			                        <a href=${data} style="display:inline-block; width:260px; overflow:hidden">
			                            ${data}
			                        </a>
			                        </div>
			                        <span class="pl-2 fa fa-link dashboard-url-icon"></span>
			                        </div>`;
			                },
			            },
			          {
			              'targets': 4,
			              'searchable':false,
			              'orderable':false,
			              'className': 'dt-body-center',
			              'render': function (data, type, full, meta){
			                  return '<button class="btn btn-outline-default resetText  explore_fullview_on" style="background-color: transparent;" data-toggle="modal" id="dashboardEditButton" data-target="#editDashboardModal"><span class="fa fa-pencil" style="cursor:pointer"></span></button>';
			                  },
			          },
			      ],
			} );
			
			$("#userEditButton").click(function(event) {
				$("#editUserModal").css('visibility', 'visible');
				$('.explore_fullview_off').show();
			});
			
			$("#dashboardEditButton").click(function(event) {
				$("#editDashboardModal").css('visibility', 'visible');
				$('.explore_fullview_off').show();
			});
			
			var seletedDashboardId;
			var seletedDashboardName;
			var seletedDashboardURL;
			/*var dashboardTable = $('#dashboardList').DataTable(
			{
				"paging" : true,
				"lengthChange" : true,
				"searching" : true,
				"ordering" : true,
				"info" : true,
				"autoWidth" : false,
				"responsive" : true,
				"order" : [ [ 1, "desc" ] ],
				"deferRender": true,
				"pageLength": 10,
				"lengthMenu": [ 5, 10, 25, 50, 75, 100 ],
				"columnDefs" : [ {
					"orderable" : false,
					"className" : 'select-checkbox',
					"targets" : 0
				} ],
				"select" : {
					style : 'single'
				},
				//dom : '<"row"B><"row"l<""f>>rtip',
				dom : "<'row'<'col-sm-12'B>><'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
				"<'row'<'col-sm-12'tr>>" +
				"<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
				buttons : [ {
					text : 'Delete',
					action : function(e, dt, node, config) {

						var items = $.map(dashboardTable.rows(
								'.selected').data(), function(item) {
							return item
						});

						var dashboardObjList = [];
						// html table columns 5								
						for (var i = 0; i <= items.length; i++) {
							if (i % 5 == 0) {//checkbox//row start
								if (i == 0) {
									var obj = new Object();
									continue;
								}
								dashboardObjList.push(obj);
								var obj = new Object();
							}
							if (i % 5 == 1) {//id
								obj["id"] = items[i];
							}
							if (i % 5 == 2) {//name
								obj["dashboardName"] = items[i];
							}
							if (i % 5 == 3) {//url
								obj["dashboardURL"] = items[i];
							}
							if (i % 5 == 4) {//edit

							}
						}
						if(dashboardObjList.length){
							$('#deleteConfirmDashboardModal').modal('show');									
							$("#deleteDashboardContent").text('Are you sure to delete a dashboard?');
							$('#confirmDashboardDelete').click(function() {
								$('#deleteConfirmDashboardModal').modal('hide');
								$.ajax({
										type : "POST",
										contentType : "application/json",
										url : '/admin/deleteDashboard',
										data : JSON.stringify(dashboardObjList),
										success : function(data) {
											dashboardTable.rows('.selected').remove().draw(
													false);
											toastr.clear();
											toastr.warning(dashboardObjList.length+' Dashboard\'s Deleted Sucessfully');
										}
								});
							});
						}													
					}
				} ]
			});*/

			//selected row in dashboard list
		$('#dashboardList tbody').on('click', 'tr', function(item) {
			$(this).toggleClass('selected');
			setTimeout(() => {  var lastSeletedDashboardObj =  dashboardTable.rows(
			'.selected').data()[0];
			/*lastSelectedDashboardId = lastSeletedDashboardObj[1];
			lastSelectedDashboardName = lastSeletedDashboardObj[2];
			lastSelectedDashboardURL = lastSeletedDashboardObj[3];*/
			
			lastSelectedDashboardId = lastSeletedDashboardObj["dashboardId"];
			lastSelectedDashboardName = lastSeletedDashboardObj["dashboardName"];
			lastSelectedDashboardURL = lastSeletedDashboardObj["dashboardUrl"];
							
			//set values in edit model
			$("#editDashboardModalId").val(lastSelectedDashboardId);
			$("#editDashboardModalName").val(lastSelectedDashboardName);
			$("#editDashboardModalURL").val(lastSelectedDashboardURL); 
			
			console.log(lastSeletedDashboardObj);
			}, 1);
			

		});
		//selected row in user list
		$('#userList tbody').on('click', 'tr', function(item) {
			$(this).toggleClass('selected');	
			setTimeout(() => {  
				var lastSeletedUserObj =  userTable.rows('.selected').data()[0];
				if(lastSeletedUserObj){
					/*lastSelectedUserId = lastSeletedUserObj[1];
					lastSelectedUserName = lastSeletedUserObj[2];
					lastSelectedFirstName = lastSeletedUserObj[3];
					lastSelectedLastName = lastSeletedUserObj[4];
					lastSelectedEmail = lastSeletedUserObj[5];
					lastSelectedStatus = lastSeletedUserObj[6];*/
					lastSelectedUserId = lastSeletedUserObj["userId"];
					lastSelectedUserName = lastSeletedUserObj["userName"];
					lastSelectedFirstName = lastSeletedUserObj["firstName"];
					lastSelectedLastName = lastSeletedUserObj["lastName"];
					lastSelectedEmail = lastSeletedUserObj["email"];
					lastSelectedStatus = lastSeletedUserObj["status"];
				}
				
				if(lastSelectedStatus == 'Active'){
					lastSelectedStatus = 'true';
				}else if(lastSelectedStatus == 'InActive'){
					lastSelectedStatus = 'false';
				}
														
				$('#editUserModalId').val(lastSelectedUserId);
				$('#editUserModalUserName').val(lastSelectedUserName);
				$('#editUserModalFirstName').val(lastSelectedFirstName);
				$('#editUserModalLastName').val(lastSelectedLastName);
				$('#editUserModalEmail').val(lastSelectedEmail);
				$('#editUserModalStatus').val(lastSelectedStatus);	
				
				}, 1);
			
			

			});
		
			$('#userDel').click(function(){
				var users = $.map(userTable.rows('.selected')
						.data(), function(item) {
					return item;
				});
				var userObjList = [];
				// html table columns 9
				for (var i = 0; i <= users.length; i++) {
					if (i % 8 == 0) {//checkbox//row start
						if (i == 0) {
							var user = new Object();
							continue;
						}
						userObjList.push(user);
						var user = new Object();
					}
					if (i % 8 == 1) {//id
						user["id"] = users[i];
					}
					if (i % 8 == 2) {//user name
						user["userName"] = users[i];
					}
					if (i % 8 == 3) {//first name
						user["name"] = users[i];
					}
					if (i % 8 == 4) {//last name
						user["lastName"] = users[i];
					}
					if (i % 8 == 5) {//email
						user["email"] = users[i];
					}
					if (i % 8 == 6) {//status
						if (users[i] == 'Active') {
							user["active"] = true;
						} else {
							user["active"] = false;
						}
					}
					if (i % 8 == 7) {//edit

					}

				}					
				for (var i = 0; i <= users.length; i++) {
					var user = new Object();
					if(users[i]){
						user["id"] = users[i]["userId"];
						user["userName"] = users[i]["userName"];
						user["name"] = users[i]["firstName"];
						user["lastName"] = users[i]["lastName"];
						user["email"] = users[i]["email"];
						if(users[i]["status"]){
							if(users[i]["status"]=="Active"){
								user["status"]= true;
							}else{
								user["status"]=false;
							}
						}		
						userObjList.push(user);
					}
				}
				if(userObjList.length){
					//$('#deleteConfirmUserModal').modal('show');
					//$("#deleteUserContent").text('Are you sure to delete a user?');
					//$('#confirmUserDelete').click(function() {
						//$('#deleteConfirmUserModal').modal('hide');
						$.ajax({
							type : "POST",
							contentType : "application/json",
							url : '/admin/deleteUser',
							data : JSON.stringify(userObjList),
							success : function(data) {											
								//toastr.clear();
								//toastr.warning(userObjList.length+' User\'s Deleted Sucessfully');
								setTimeout(() => {  
									location.reload();
								},1000);
							},
							error : function(data){
								location.replace('/admin/home');
							}
						});
					//});
				}
			});
			
			$('#dashDel').click(function(){
				var items = $.map(dashboardTable.rows(
						'.selected').data(), function(item) {
					return item
				});
		
				var dashboardObjList = [];
				// html table columns 5								
				for (var i = 0; i <= items.length; i++) {
					if (i % 5 == 0) {//checkbox//row start
						if (i == 0) {
							var obj = new Object();
							continue;
						}
						dashboardObjList.push(obj);
						var obj = new Object();
					}
					if (i % 5 == 1) {//id
						obj["id"] = items[i];
					}
					if (i % 5 == 2) {//name
						obj["dashboardName"] = items[i];
					}
					if (i % 5 == 3) {//url
						obj["dashboardURL"] = items[i];
					}
					if (i % 5 == 4) {//edit
		
					}
				}
				console.log(items)
				for (var i = 0; i <= items.length; i++) {
					console.log(items[i])					
						var obj = new Object();
					if(items[i]){
						obj["id"] = items[i]["dashboardId"];
						obj["dashboardName"] = items[i]["dashboardName"];
						obj["dashboardURL"] = items[i]["dashboardUrl"];
						dashboardObjList.push(obj);
					}
				}
				console.log(dashboardObjList);
				if(dashboardObjList.length){
					//$('#deleteConfirmDashboardModal').modal('show');									
					//$("#deleteDashboardContent").text('Are you sure to delete a dashboard?');
					//$('#confirmDashboardDelete').click(function() {
						//$('#deleteConfirmDashboardModal').modal('hide');
						$.ajax({
								type : "POST",
								contentType : "application/json",
								url : '/admin/deleteDashboard',
								data : JSON.stringify(dashboardObjList),
								success : function(data) {
									dashboardTable.rows('.selected').remove().draw(
											false);
									//toastr.clear();
									//toastr.warning(dashboardObjList.length+' Dashboard\'s Deleted Sucessfully');
								}
						});
					//});
				}
			});
			
			$('.toastUpdateSuccess').click(function() {
				toastr.clear();
			    toastr.success('Update Successfully');
			});
			
			$(function() {
				
			$("#editUserForm").validate({
			    rules: {
			      userName: {
			        required: true,
			        minlength: 5 ,
			        remote: {
                        url: "/admin/checkUsernameExistByUser",
                        type: "post",
                        data: {
                        	id: function() {
                                return $( "#userModalId" ).val();
                            },
                            userName: function() {
                            	return $("#userModalUserName").val();
                            }
                        }
                     }
			      }, 
			      name: {
			    	  required: true
			      },
			      lastName: {
			    	  required: true
			      },
			      email: {
			    	  required: true,
			    	  email: true
			      },
			      action: "Please provide some data"
			    },
			    messages: {
			      userName: {
			        required: "Please enter some data",
			        minlength: "Your data must be at least 5 characters",
			        remote: "Username already exist. Please try another one"
			      },
				  name: {
				        required: "Please enter some data"
				  },
				  lastName: {
				        required: "Please enter some data"
				  },
				  email: {
				        required: "Please enter some data"
				  },
			      action: "Please provide some data"
			    },
			    submitHandler: function(form) {
			        form.submit();
			        toastr.clear();
			        toastr.success('Successfully Updated!');
			      }
			  });
			
			$("#addUserForm").validate({
			    rules: {
			      userName: {
			        required: true,
			        minlength: 5,
			        remote: {
                        url: "/admin/checkUserExist",
                        type: "post"
                     }
			      },
			      password: {
			    	  required: true,
			    	  minlength: 5
			      },
			      name: {
			    	  required: true
			      },
			      lastName: {
			    	  required: true
			      },
			      email: {
			    	  required: true,
			    	  email: true
			      },
			      action: "Please provide some data"
			    },
			    messages: {
			      userName: {
			        required: "Please enter some data",
			        minlength: "Your data must be at least 5 characters",
			        remote: "Username already exist. Please try another one"
			      },
			      password: {
				        required: "Please enter some data",
				        minlength: "Your data must be at least 5 characters"
				  },
				  name: {
				        required: "Please enter some data"
				  },
				  lastName: {
				        required: "Please enter some data"
				  },
				  email: {
				        required: "Please enter some data"
				  },
			      action: "Please provide some data"
			    },
			    submitHandler: function(form) {
			        form.submit();
			        toastr.clear();
			        toastr.success('Successfully Added!');
			      }
			  });
			
			$("#addDashboardForm").validate({
			    rules: {
			    	dashboardName: {
				    	  required: true
				      },
				    dashboardURL: {
				    	  required: true,
				    	  url: true
				    }
			    },
			    messages: {
			    	dashboardName: {
				    	  required: "Please enter some data"
				      },
				    dashboardURL: {
				    	  required: "Please enter some data",
				    	  url: "Please enter valid URL"
				    },
			    },
			    submitHandler: function(form) {
			        form.submit();
			        toastr.clear();
			        toastr.success('Successfully Added!');
			      }
			  });
			});
			
			$("#editDashboardForm").validate({
			    rules: {
			    	id: {
			    		required: true,
			    		number: true
			    	},
			    	dashboardName: {
				    	  required: true
				      },
				    dashboardURL: {
				    	  required: true,
				    	  url: true
				    }
			    },
			    messages: {
			    	dashboardName: {
				    	  required: "Please enter some data"
				      },
				    dashboardURL: {
				    	  required: "Please enter some data",
				    	  url: "Please enter valid URL"
				    },
			    },
			    submitHandler: function(form) {
			        form.submit();
			        toastr.clear();
			        toastr.success('Successfully Updated!');
			      }
			  });
			function redirect(url){
				
			}
			});
		});