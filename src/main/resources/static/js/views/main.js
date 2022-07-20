$(function(){
  'use strict';

  var barChart1 = null;
  var barChart2 = null;
  var barChart3 = null;
  var barChart4 = null;
  var lineChart1 = null;
  
  
  var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth() + 1; //January is 0!

	var yy = today.getFullYear().toString().substr(-2);
	if (dd < 10) {
	  dd = '0' + dd;
	} 
	if (mm < 10) {
	  mm = '0' + mm;
	} 
	var today = dd + '/' + mm + '/' + yy;
	$("#smsTriggerDate").val(today);
	getDashboard();
	
  $(".dateDBoard").hide();
 // dashbaordTypechange();
  
  
 $("#dashboardType").change(function(){
	 
	 dashbaordTypechange();
	  
});
 
 
 
 function dashbaordTypechange(){
	 if($("#dashboardType").val()=="1"){
		  //$("#dashboardStatusYear").val($("#dashboardStatusYear option:first").val());
		  $('#dashboardStatusYear option:nth-child(2)').attr('selected', 'selected');
		  getDashboard();
		  $(".dateDBoard").hide();
		  $(".yearOrMonthDBorad").show();
	  }else{
		  getDateWiseDBoard();
		 // $(".yearOrMonthDBorad").hide();
		  $(".dateDBoard").show();
	  }
 }
  
  /*$('#refrshDashboard').click(function(){
		if($("#dashboardStatusYear").val()!="0"){
			  getDashboard();
		  }else{
			  toastr.clear();
			  toastr.clear();toastr.warning('Please Select Year.');
		  }
	});*/
  
  
  $('#getSmsDeliveryStatusSubmit').click(function(){
		if($("#smsTriggerDate").val()!="0"){
			getDashboard();
		  }else{
			  toastr.clear();
			  toastr.clear();toastr.warning('Please Select Year.');
		  }
	});
  
  function getDateWiseDBoard(){}
  
  $("#dashboardStatusYear").change(function(){
	  $(".yearSpan").text($("#dashboardStatusYear").val());
		  getDashboard();
  });
  
  function getDashboard(){
	  $('#barChart1').empty();
	  $('#barChart1').append("<canvas id='carPolicy_status'></canvas>");
	  
	 /* $('#barChart2').empty();
	  $('#barChart2').append("<canvas id='xgenPolicy_status'></canvas>");
	  
	  $('#lineChart1').empty();
	  $('#lineChart1').append("<canvas id='xgenGL_status'></canvas>");
	  
	  $('#barChart3').empty();
	  $('#barChart3').append("<canvas id='gridAutomation_status'></canvas>");*/
	  
	  /*$('#barChart4').empty();
	  $('#barChart4').append("<canvas id='policyPdfMailTrigger_status'></canvas>");*/
	  
  Chart.defaults.global.legend.display = true;
	$.ajax({
		type : "POST",
		url : "/rsRobo/getCarStatusChartDetails?date="+$("#smsTriggerDate").val(),
		success : function(response) {
			
			//$("#pieChart").hide();
			$("#barChart1").show();
			
			//$('#page_loader').hide();

			var ctx = document.getElementById("carPolicy_status");
			
			if(barChart1 != null ){
				barChart1.destroy();
			}
			barChart1 = new Chart(ctx, {
				type : 'bar',
				data : response,
				options : {
					legend : {
						display : true,
						onClick : function(e) {
							e.stopPropagation();
						}
					},
					title : {
						display : true,
						text : '',
					},
					scales : {
						yAxes : [ {
							ticks : {
								beginAtZero : true
								/*stepSize: 50*/
							},
							scaleLabel : {
                              display : true,
                              labelString : 'Count'
                          }

						} ],
						  xAxes : [ {
                            scaleLabel : {
                                display : true,
                                labelString : 'Car Type'
                            }
                        } ]
					},
					tooltips : {
						enabled : true,
						mode : 'single',
						callbacks : {
							afterLabel : function(tooltipItems, data) {
								return '';
							}
						}
					}
				}
			});
			ctx.onclick = function(evt) {
				/*var activePoints = minPieChart.getElementsAtEvent(evt);
				if (activePoints.length > 0) {
					console.log(activePoints[0]._model.label);
				}*/
			};
		},
		error : function() {
			$('#page_loader').hide();
			toastr.clear();
			toastr.error('ER001 : Unable to process Chart');
		}
	});
	
	
	
  }
	
  

});
