///generating base width
function GenerateWidth(){
    var width=window.innerWidth;
    if(width<968) width=90;
    else {
        width-=400;
        width=parseInt(width/7);
    }
    return width;
}

function SetupJqWidgets(ds){
    var data = new Array();
    // create a data source and data adapter
    ///Sample Mock Data
	var Header = [];
	if (ds=="GENERAL"){
		 Header =
    [
        "GWP","NWP","GEP","NEP","Average GWP","Average NEP","Average GEP","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED CLAIMS","NIC","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","NET ACQUSITION COST","EXPENSE RATIO","ACQUSITION COST RATIO","CLAIM RATIO","COR","XOL COST","EXPENSES",  //HERE TO CHANGE
    ];
	}else if (ds=="MOTOR" ){
		Header =
    [
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","GWP OD","GWP TP","NWP OD","NWP TP","GEP OD","NEP OD","DISCOUNT GWP OD","DISCOUNT NWP OD","DISCOUNT GEP OD","DISCOUNT NEP OD","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED CLAIMS","NIC","ACTUAL GIC OD","IBNR GIC OD","NBNR GIC OD","TOTAL GIC OD","ACTUAL NIC OD","IBNR NIC OD","TOTAL NIC OD","ACTUAL GIC TP","ULR GIC TP","IBNER GIC TP","TOTAL GIC TP","ACTUAL NIC TP","ULR NIC TP","IBNER NIC TP","TOTAL NIC TP","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","EARNED POLICIES","NET ACQUISITION COST","EXPENSE RATIO","ACQUISITION COST RATIO","CLAIM RATIO","COR",  //HERE TO CHANGE
    ];
	}
	else if (ds=="HEALTH" ){
		Header =
    [
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","SEVERITY %","REPUDIATED CALIMS %","OPENING OS CLAIM","CLAIMS PAID","CLOSING OS CLAIMS","REGISTERED CLAIMS","NIC","ACTUAL GIC HEALTH","IBNR GIC HEALTH","NBNR GIC HEALTH","TOTAL GIC HEALTH","ACTUAL NIC HEALTH","IBNR NIC HEALTH","NBNR NIC HEALTH","TOTAL NIC HEALTH","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","XOL COST","EARNED POLICIES","LIVES COUNT","NET ACQISITION COST","EXPENSES","EXPENSES RATIO","ACQISITION COST RATIO","CLAIM RATIO","COR", 
    
    ];
	}
    
    var productNames =
    [
        "DateRange1","DateRange2","DateRange3"
    ];
    var priceValues =
    [
        "2000025", "100005", "300000", "300003", "400005", "300006", "300008", "200005", "500000", "1000075", "3000025", "400000"
    ];
    for (var i = 0; i < 500; i++) {
        var row = {};
        var productindex = Math.floor(Math.random() * Header.length);
        var price = Math.round(Math.random() * 10);
        var quantity = 1 + Math.round(Math.random() * 10);
        row["HeaderCost"] = Header[Math.floor(Math.random() * Header.length)];
        row["SubCost"] = "claim"+Math.floor(Math.random() * Header.length);
        row["productname"] = productNames[ Math.floor(Math.random() * 3)];
        row["ActualTotalCost"] = priceValues[Math.floor(Math.random()*3)];
        row["EstimatedBudgetCost"] = quantity;
        data[i] = row;
    }
    
    var source =
    {
        localdata: data,
        datatype: "array",
        datafields:
        [
            { name: 'HeaderCost', type: 'string' },
            { name: 'SubCost', type: 'string' },
            { name: 'productname', type: 'string' },
            { name: 'ActualTotalCost', type: 'number' },
            { name: 'EstimatedBudgetCost', type: 'number' }
        ]
    };
    var width=GenerateWidth();
    var dataAdapter = new $.jqx.dataAdapter(source);
    dataAdapter.dataBind();
    // create a pivot data source from the dataAdapter
    var pivotDataSource = new $.jqx.pivot(
        dataAdapter,
        {
            pivotValuesOnRows: false,
            rows: [{ dataField: 'HeaderCost',width:width}, { dataField: 'SubCost'}],
            columns: [{ dataField: 'productname',width:width*2}],
            values: [
                { dataField: 'ActualTotalCost',width:width,'function': 'sum', text: 'Actual', formatSettings: { prefix: '', decimalPlaces: 0} },
                { dataField: 'EstimatedBudgetCost',width:width,'function':'sum', text: 'Budget', formatSettings: { prefix: '', decimalPlaces: 0 } }
            ]
        });
    // create a pivot grid
    $('#divPivotGrid').jqxPivotGrid(
        {
            source: pivotDataSource,
            treeStyleRows: true, // change this property to switch between treestyle and olap style display
            autoResize:true,
            multipleSelectionEnabled: false,   
        });

}
// prepare sample data
            
SetupJqWidgets("GENERAL");


////bootstrap base table
function generateData(ds){
    var data = new Array();
    // create a data source and data adapter
    ///Sample Mock Data
    var Header = [];
	if (ds=="GENERAL"){
		 Header =
    [
        "GWP","NWP","GEP","NEP","Average GWP","Average NEP","Average GEP","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED Claims","NIC","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","NET ACQUSITION COST","EXPENSE RATIO","ACQUSITION COST RATIO","CLAIM RATIO","COR","XOL COST","EXPENSES", //HERE TO CHANGE
        "GWP","NWP","GEP","NEP","Average GWP","Average NEP","Average GEP","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED Claims","NIC","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","NET ACQUSITION COST","EXPENSE RATIO","ACQUSITION COST RATIO","CLAIM RATIO","COR","XOL COST","EXPENSES",//HERE TO CHANGE
        "GWP","NWP","GEP","NEP","Average GWP","Average NEP","Average GEP","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS%","REGISTERED CLAIMS","REPUDIATED Claims","NIC","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","NET ACQUSITION COST","EXPENSE RATIO","ACQUSITION COST RATIO","CLAIM RATIO","COR","XOL COST","EXPENSES",//HERE TO CHANGE
    ];
	}else if (ds=="MOTOR" ){
		Header =
    [
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","GWP OD","GWP TP","NWP OD","NWP TP","GEP OD","NEP OD","DISCOUNT GWP OD","DISCOUNT NWP OD","DISCOUNT GEP OD","DISCOUNT NEP OD","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED CLAIMS","NIC","ACTUAL GIC OD","IBNR GIC OD","NBNR GIC OD","TOTAL GIC OD","ACTUAL NIC OD","IBNR NIC OD","TOTAL NIC OD","ACTUAL GIC TP","ULR GIC TP","IBNER GIC TP","TOTAL GIC TP","ACTUAL NIC TP","ULR NIC TP","IBNER NIC TP","TOTAL NIC TP","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","EARNED POLICIES","NET ACQUISITION COST","EXPENSE RATIO","ACQUISITION COST RATIO","CLAIM RATIO","COR",  //HERE TO CHANGE
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","GWP OD","GWP TP","NWP OD","NWP TP","GEP OD","NEP OD","DISCOUNT GWP OD","DISCOUNT NWP OD","DISCOUNT GEP OD","DISCOUNT NEP OD","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED CLAIMS","NIC","ACTUAL GIC OD","IBNR GIC OD","NBNR GIC OD","TOTAL GIC OD","ACTUAL NIC OD","IBNR NIC OD","TOTAL NIC OD","ACTUAL GIC TP","ULR GIC TP","IBNER GIC TP","TOTAL GIC TP","ACTUAL NIC TP","ULR NIC TP","IBNER NIC TP","TOTAL NIC TP","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","EARNED POLICIES","NET ACQUISITION COST","EXPENSE RATIO","ACQUISITION COST RATIO","CLAIM RATIO","COR",  //HERE TO CHANGE
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","GWP OD","GWP TP","NWP OD","NWP TP","GEP OD","NEP OD","DISCOUNT GWP OD","DISCOUNT NWP OD","DISCOUNT GEP OD","DISCOUNT NEP OD","GEP TP","NEP TP","SEVERITY %","REPUDIATED CLAIMS %","REGISTERED CLAIMS","REPUDIATED CLAIMS","NIC","ACTUAL GIC OD","IBNR GIC OD","NBNR GIC OD","TOTAL GIC OD","ACTUAL NIC OD","IBNR NIC OD","TOTAL NIC OD","ACTUAL GIC TP","ULR GIC TP","IBNER GIC TP","TOTAL GIC TP","ACTUAL NIC TP","ULR NIC TP","IBNER NIC TP","TOTAL NIC TP","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","EARNED POLICIES","NET ACQUISITION COST","EXPENSE RATIO","ACQUISITION COST RATIO","CLAIM RATIO","COR",  //HERE TO CHANGE
    ];
	}
	else if (ds=="HEALTH" ){
		Header =
    [
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","SEVERITY %","REPUDIATED CALIMS %","OPENING OS CLAIM","CLAIMS PAID","CLOSING OS CLAIMS","REGISTERED CLAIMS","NIC","ACTUAL GIC HEALTH","IBNR GIC HEALTH","NBNR GIC HEALTH","TOTAL GIC HEALTH","ACTUAL NIC HEALTH","IBNR NIC HEALTH","NBNR NIC HEALTH","TOTAL NIC HEALTH","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","XOL COST","EARNED POLICIES","LIVES COUNT","NET ACQISITION COST","EXPENSES","EXPENSES RATIO","ACQISITION COST RATIO","CLAIM RATIO","COR", //HERE TO CHANGE
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","SEVERITY %","REPUDIATED CALIMS %","OPENING OS CLAIM","CLAIMS PAID","CLOSING OS CLAIMS","REGISTERED CLAIMS","NIC","ACTUAL GIC HEALTH","IBNR GIC HEALTH","NBNR GIC HEALTH","TOTAL GIC HEALTH","ACTUAL NIC HEALTH","IBNR NIC HEALTH","NBNR NIC HEALTH","TOTAL NIC HEALTH","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","XOL COST","EARNED POLICIES","LIVES COUNT","NET ACQISITION COST","EXPENSES","EXPENSES RATIO","ACQISITION COST RATIO","CLAIM RATIO","COR", //HERE TO CHANGE
        "GWP","NWP","GEP","NEP","AVERAGE GWP","AVERAGE NEP","AVERAGE GEP","SEVERITY %","REPUDIATED CALIMS %","OPENING OS CLAIM","CLAIMS PAID","CLOSING OS CLAIMS","REGISTERED CLAIMS","NIC","ACTUAL GIC HEALTH","IBNR GIC HEALTH","NBNR GIC HEALTH","TOTAL GIC HEALTH","ACTUAL NIC HEALTH","IBNR NIC HEALTH","NBNR NIC HEALTH","TOTAL NIC HEALTH","NIC:NEP RATIO","CLAIM FREQUENCY","WRITTEN POLICIES","XOL COST","EARNED POLICIES","LIVES COUNT","NET ACQISITION COST","EXPENSES","EXPENSES RATIO","ACQISITION COST RATIO","CLAIM RATIO","COR", //HERE TO CHANGE
    ];
	}
    
    var productNames =
    [
        "DateRange1","DateRange2","DateRange3"
    ];
    var priceValues =
    [
        "2000025", "100005", "300000", "300003", "400005", "300006", "300008", "200005", "500000", "1000075", "3000025", "400000"
    ];
    for (var i = 0; i < Header.length-1; i++) {
        var row = {};
        row["HeaderCost"] = Header[i];
       /* row["Actual1"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        row["Budget1"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        row["Actual2"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        row["Budget2"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        row["Actual3"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        row["Budget3"] = priceValues[Math.floor(Math.random() * priceValues.length)];
        var condition = productNames[Math.floor(Math.random() * productNames.length)];
        var Actual="Actual"+parseInt(condition[condition.length-1])
        var Budget="Budget"+parseInt(condition[condition.length-1])
        row[Actual]=priceValues[Math.floor(Math.random() * priceValues.length)]
        row[Budget]=priceValues[Math.floor(Math.random() * priceValues.length)]*/
        data[i] = row;
    }
	console.log(data);
    return data
}

/*$('#motorTable').DataTable( {
      //data:generateData("GENERAL"),
        columns: [
            { data: 'HeaderCost' },
            { data: 'Actual1' },
            { data: 'Budget1' },
            { data: 'Actual2' },
            { data: 'Budget2' },
            { data: 'Actual3' },
            { data: 'Budget3' },
        ],
        "scrollX": true,
        "pageLength": 10,
        "dom": '<"top d-flex justify-content-sm-start justify-content-md-end  align-items-end "f>rt<"bottom"lp><"clear">'

} );*/
/*$('#healthTable').DataTable( {
      //data:generateData("GENERAL"),
        columns: [
            { data: 'HeaderCost' },
            { data: 'Actual1' },
            { data: 'Budget1' },
            { data: 'Actual2' },
            { data: 'Budget2' },
            { data: 'Actual3' },
            { data: 'Budget3' },
        ],
        "scrollX": true,
        "pageLength": 10,
        "dom": '<"top d-flex justify-content-sm-start justify-content-md-end  align-items-end "f>rt<"bottom"lp><"clear">'

} );*/

/*$('#generalTable').DataTable( {
      //data:generateData("GENERAL"),
        columns: [
            { data: 'HeaderCost' },
            { data: 'Actual1' },
            { data: 'Budget1' },
            { data: 'Actual2' },
            { data: 'Budget2' },
            { data: 'Actual3' },
            { data: 'Budget3' },
        ],
        "scrollX": true,
        "pageLength": 10,
        "dom": '<"top d-flex justify-content-sm-start justify-content-md-end  align-items-end "f>rt<"bottom"lp><"clear">'

} );*/

$('#generalDiv').show();
$('#motorDiv').hide();
$('#healthDiv').hide();
$('#generalTable_wrapper').show();
$('#motorTable_wrapper').hide();
$('#healthTable_wrapper').hide();
$("#motorAdditionalDimensions").hide();
$('#reports').on('change',function (item){
	SetupJqWidgets($('#reports').val());
	generateData($('#reports').val());
	var dashboard = $('#reports option:selected').val();
	if(dashboard == 'GENERAL'){
		$('#generalDiv').show();
		$('#motorDiv').hide();
		$('#healthDiv').hide();
		$('#generalTable_wrapper').show();
		$('#motorTable_wrapper').hide();
		$('#healthTable_wrapper').hide();
		$("#generalTable").show();
		$("#motorTable,#healthTable").hide();
		$("#motorAdditionalDimensions").hide();
	}else if (dashboard == 'MOTOR'){
		$('#generalDiv').hide();
		$('#motorDiv').show();
		$('#healthDiv').hide();
		$('#healthTable_wrapper').hide();
		$('#motorTable_wrapper').show();
		$('#generalTable_wrapper').hide();
		$("#motorTable").show();
		$("#generalTable,#healthTable").hide();
		$("#motorAdditionalDimensions").show();
	}else if (dashboard == 'HEALTH'){
		$('#generalDiv').hide();
		$('#motorDiv').hide();
		$('#healthDiv').show();
		$('#motorTable_wrapper').hide();
		$('#healthTable_wrapper').show();
		$('#generalTable_wrapper').hide();
		$("#healthTable").show();
		$("#generalTable,#motorTable").hide();
		$("#motorAdditionalDimensions").hide();
	}
})



jQuery(document).ready(function() {
	var motorModel = $("#motorModel");
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
					/*if(modelList){
						var model = new Object();
						model['text'] = 'All';
						model['value'] = 'All';
						modelList.push(model);
					}*/
					callback(modelList);
				},
				error : function(data) {
					callback();
				}
			});
		});
	}
	
//	var monthlyMake = $("#monthlyMake");
//	if(monthlyMake[0]){
//		var monMake = monthlyMake[0].selectize;
//		models.load(function(callback) {
//			$.ajax({
//				type : "GET",
//				contentType : "application/json",
//				url : '/getAllModels',
//				success : function(data) {
//					var modelList = [];
//					for(var obj of data){
//						var model = new Object();
//						model['text'] = obj;
//						model['value'] = obj;
//						modelList.push(model);
//					}
//					/*if(modelList){
//						var model = new Object();
//						model['text'] = 'All';
//						model['value'] = 'All';
//						modelList.push(model);
//					}*/
//					callback(modelList);
//				},
//				error : function(data) {
//					callback();
//				}
//			});
//		});
//	}
});





