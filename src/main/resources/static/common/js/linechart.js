var ctx = document.getElementById('lineChart').getContext("2d");

var gradientFill = ctx.createLinearGradient(0, 0, 0, 130);
gradientFill.addColorStop(0, "rgb(250,166,42,0.8)");
gradientFill.addColorStop(1, "rgb(250,166,42,0.1)");

var myChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: ["January","February",	"March","April","May","June","july","August"],
        datasets: [{
            fill: true,
            backgroundColor: gradientFill,
            borderWidth: 1,
            borderColor:'orange',
            data: [
                
                        {x: 0,y: 60}, 
                        {x: 10,y: 10}, 
                        {x: 20,y: 100}, 
                        {x: 30,y: 20},
                        {x: 40,y: 80},
                        {x: 50,y: 20},
                        {x: 60,y: 50},
                        {x: 0,y: 10},  
                  ],
        }]
    },
    options: {

        scales: {
            yAxes: [{
                ticks: {
                    fontColor: "rgba(0,0,0,0.5)",
                    fontStyle: "bold",
                    beginAtZero: true,
                    
                    fontSize:12
                },
            }],
            xAxes: [{
                ticks: {
                    fontSize:10,
                }
            }]
        },
        legend: {
            display:false,
        },
        responsive: true, // Instruct chart js to respond nicely.
        maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height 
    }
});

