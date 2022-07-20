
///Total Leads Charts
var ctx = document.getElementById("LeadsChart").getContext('2d');
Chart.defaults.global.defaultFontSize = 16;
     var myChart = new Chart(ctx, {
       type: 'pie',
       data: {
         labels:['Lead1','Lead2','Lead3','Lead4'],
         datasets: [{

           backgroundColor: [
             "#004990",
             "#269996",
             "#006CD5",
             "#EDAD27"
           ],
           data: [40, 15, 8, 8],
          
         }]
       },
       options:{
         legend: {
             display: false,
         },
         responsive: true, // Instruct chart js to respond nicely.
         maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height 
       }
     });
