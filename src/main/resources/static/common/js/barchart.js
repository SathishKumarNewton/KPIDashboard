var chartColors = {
    high: '#EDAD28',
    low: '#3C9996'
  };
  Chart.defaults.global.defaultFontSize = 10;
  Chart.defaults.global.defaultFontColor = '#787474';
  Chart.defaults.global.defaultFont = "Georgia"
  var ctx = document.getElementById("barChart").getContext("2d");
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
     labels: ["January","February",	"March","April","May","June","july","August"],
      datasets: [{
        label: ['22 feb 1'], // Name the series
        data: [
              15,60,75,10,20,50,5,90
         ],
        backgroundColor: [
          chartColors.low,
          chartColors.low,
          chartColors.low,
          chartColors.low,
          chartColors.low,
          chartColors.low,
          chartColors.low,
          chartColors.low
        ],
      }],
    },
    options: {
        legend: {
            display: false,
        },
       responsive: true, // Instruct chart js to respond nicely.
       maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height 
     }
  });
  var colorChangeValue = 35; //Changing colors of data greater than 35
  var dataset = myChart.data.datasets[0];
  for (var i = 0; i < dataset.data.length; i++) {
    if (dataset.data[i] > colorChangeValue) {
      dataset.backgroundColor[i] = chartColors.high;
    }
  }
  myChart.update();

  
