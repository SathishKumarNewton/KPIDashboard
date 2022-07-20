////bootstrap base table
function generateData(){
    var a={
        userId:43,
        userName:'Gokul_N',
        firstName:'Gokul',
        lastName:'Ganesh',
        email:'gokul@gmail.com',

        ///dashbaord detail
        dashboardId:430,
        dashboardName:'Motor KPI',
        dashboardUrl:'http://meet.google.com/34823j4j34j8329492j348',

        //role
        role:'Zone_Head'
    }
    var data=[]
    for(var i=0;i<30;i++){
        data.push(a);
    }
    return data
}

//homepage table1


//homepage table2



//Admin table
$('#admin_Table').DataTable( {
    //data:generateData(),
      columns: [
          { data: '',"width": "40px"},
          { data: 'userId',"width": "80px"},
          { data: 'userName'},
          { data: 'dashboardId',"width": "120px"},
          { data: 'dashboardName',"width": "140px"},
          { data: 'dashboardUrl'},
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
      'columnDefs': [
          {
          'targets': 0,
          'className': 'dt-body-center',
          'searchable':false,
          'orderable':false,
          'render': function (data, type, full, meta){
              return '<input type="checkbox" class="checkbox-group1-child"  value="' + $('<div/>').text(data).html() + '">';
              },
          },
          {
            'targets': 5,
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
      ],
} );


