export default {
  getDarkTheme() {
    return {
      colors: [
        '#2b908f', // greenish blue
        '#90ee7e', // bright green
        '#f45b5b', // bright red
        '#7798BF', // grayish blue
        '#aaeeee', // light blue
        '#ff0066', // redish pink
        '#eeaaee', // darkish pink
        '#55BF3B', // green
        '#DF5353' // orange red
      ],
      chart: {
        plotBackgroundColor: undefined,
        backgroundColor: {
          linearGradient: {
            x1: 0,
            y1: 0,
            x2: 1,
            y2: 1
          },
          stops: [
            [0, '#2a2a2b'],
            [1, '#3e3e40']
          ]
        },
        style: {
          fontFamily: "'Unica One', sans-serif"
        },
        plotBorderColor: '#606063'
      },
      title: {
        style: {
          color: '#E0E0E3',
          textTransform: 'uppercase',
          fontSize: '20px'
        }
      },
      subtitle: {
        style: {
          color: '#E0E0E3',
          textTransform: 'uppercase'
        }
      },
      xAxis: {
        gridLineColor: '#707073',
        labels: {
          style: {
            color: '#E0E0E3'
          }
        },
        lineColor: '#707073',
        minorGridLineColor: '#505053',
        tickColor: '#707073',
        title: {
          style: {
            color: '#A0A0A3'
          }
        }
      },
      yAxis: {
        gridLineColor: '#707073',
        labels: {
          style: {
            color: '#E0E0E3'
          }
        },
        lineColor: '#707073',
        minorGridLineColor: '#505053',
        tickColor: '#707073',
        tickWidth: 1,
        title: {
          style: {
            color: '#A0A0A3'
          }
        }
      },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.85)',
        style: {
          color: '#F0F0F0'
        }
      },
      plotOptions: {
        series: {
          dataLabels: {
            color: '#F0F0F3',
            style: {
              fontSize: '13px'
            }
          },
          marker: {
            lineColor: '#333'
          }
        },
        boxplot: {
          fillColor: '#505053'
        },
        candlestick: {
          lineColor: 'white'
        },
        errorbar: {
          color: 'white'
        }
      },
      legend: {
        align: 'left',
        verticalAlign: 'top',
        floating: true,
        itemStyle: {
          color: 'white'
        },
        itemHoverStyle: {
          color: 'white'
        },
        itemHiddenStyle: {
          color: 'white'
        },
        title: {
          style: {
            color: 'white'
          }
        }
      },
      credits: {
        style: {
          color: '#666'
        }
      },
      labels: {
        style: {
          color: '#707073'
        }
      },
      drilldown: {
        activeAxisLabelStyle: {
          color: '#F0F0F3'
        },
        activeDataLabelStyle: {
          color: '#F0F0F3'
        }
      },
      navigation: {
        buttonOptions: {
          symbolStroke: '#DDDDDD',
          theme: {
            fill: '#505053'
          }
        }
      },
      rangeSelector: {
        buttonTheme: {
          fill: '#505053',
          stroke: '#000000',
          style: {
            color: '#CCC'
          },
          states: {
            hover: {
              fill: '#707073',
              stroke: '#000000',
              style: {
                color: 'white'
              }
            },
            select: {
              fill: '#000003',
              stroke: '#000000',
              style: {
                color: 'white'
              }
            }
          }
        },
        inputBoxBorderColor: '#505053',
        inputStyle: {
          backgroundColor: '#333',
          color: 'silver'
        },
        labelStyle: {
          color: 'silver'
        }
      },
      navigator: {
        handles: {
          backgroundColor: '#666',
          borderColor: '#AAA'
        },
        outlineColor: '#CCC',
        maskFill: 'rgba(255,255,255,0.1)',
        series: {
          color: '#7798BF',
          lineColor: '#A6C7ED'
        },
        xAxis: {
          gridLineColor: '#505053'
        }
      },
      scrollbar: {
        barBackgroundColor: '#808083',
        barBorderColor: '#808083',
        buttonArrowColor: '#CCC',
        buttonBackgroundColor: '#606063',
        buttonBorderColor: '#606063',
        rifleColor: '#FFF',
        trackBackgroundColor: '#404043',
        trackBorderColor: '#404043'
      }
    };
  },
  getLightTheme() {
    return {
      colors: [
        '#2b908f', // greenish blue
        '#90ee7e', // bright green
        '#f45b5b', // bright red
        '#7798BF', // grayish blue
        '#aaeeee', // light blue
        '#ff0066', // redish pink
        '#eeaaee', // darkish pink
        '#55BF3B', // green
        '#DF5353' // orange red
      ],
      chart: {
        backgroundColor: '#fff',
        borderWidth: 0,
        plotBackgroundColor: '#fff',
        plotShadow: false,
        plotBorderWidth: 0
      },
      title: {
        style: {
          color: '#274b6d', //#3E576F',
          fontSize: '16px'
        }
      },
      subtitle: {
        style: {
          color: '#4d759e'
        }
      },
      xAxis: {
        gridLineWidth: 0,
        lineColor: '#C0D0E0',
        tickColor: '#C0D0E0',
        labels: {
          style: {
            color: '#666',
            cursor: 'default',
            fontSize: '11px',
            lineHeight: '14px'
          }
        },
        title: {
          style: {
            color: '#4d759e',
            fontWeight: 'bold'
          }
        }
      },
      yAxis: {
        gridLineColor: '#e6e6e6',
        minorTickInterval: null,
        lineColor: '#C0D0E0',
        lineWidth: 1,
        tickWidth: 1,
        tickColor: '#C0D0E0',
        labels: {
          style: {
            color: '#666',
            cursor: 'default',
            fontSize: '11px',
            lineHeight: '14px'
          }
        },
        title: {
          style: {
            color: '#4d759e',
            fontWeight: 'bold'
          }
        }
      },
      legend: {
        align: 'left',
        verticalAlign: 'top',
        floating: true,
        itemStyle: {
          color: 'black'
        },
        itemHoverStyle: {
          color: 'black'
        },
        itemHiddenStyle: {
          color: 'black'
        },
        title: {
          style: {
            color: 'black'
          }
        }
      },
      labels: {
        style: {
          color: '#3E576F'
        }
      },
      navigation: {
        buttonOptions: {
          theme: {
            stroke: '#CCCCCC'
          }
        }
      }
    };
  }
};
