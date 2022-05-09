export default [
  {
    id: 7,
    name: 'N791:US 13 @ DE 1 N SMY RAMP',
    label: '7',
    position: { lat: 39.335377, lng: -75.629231 },
    phases: [2, 4, 5, 6],
    layout: {
      signals: {
        '2': { id: '7-S2', type: 0, location: { lat: 39.335299, lng: -75.629002 }, rotation: 34.49 },
        '4': { id: '7-S4', type: 1, location: { lat: 39.335203, lng: -75.62944 }, rotation: -55.29 },
        '5': { id: '7-S5', type: 1, location: { lat: 39.335269, lng: -75.629051 }, rotation: 34.49 },
        '6': { id: '7-S6', type: 0, location: { lat: 39.335469, lng: -75.629461 }, rotation: -145.6 }
      },
      detectors: {
        '2': {
          id: '7-D2',
          phaseId: 2,
          location: { lat: 39.334534, lng: -75.628348 },
          size: { width: 6, height: 6 },
          rotation: 34.49,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.333513, lng: -75.627451 },
              { lat: 39.334534, lng: -75.628348 },
              { lat: 39.335619, lng: -75.629314 }
            ]
          }
        },
        '3': {
          id: '7-D3',
          phaseId: 0,
          location: { lat: 39.335066, lng: -75.629226 },
          size: { width: 6, height: 6 },
          rotation: -124.99,
          route: {
            centerIdx: 3,
            coords: [
              { lat: 39.335043, lng: -75.629755 },
              { lat: 39.335106, lng: -75.629555 },
              { lat: 39.33511, lng: -75.629384 },
              { lat: 39.335066, lng: -75.629226 },
              { lat: 39.335022, lng: -75.629152 },
              { lat: 39.334729, lng: -75.628887 },
              { lat: 39.334158, lng: -75.628328 }
            ]
          }
        },
        '4': {
          id: '7-D4',
          phaseId: 4,
          location: { lat: 39.335215, lng: -75.629438 },
          size: { width: 6, height: 25 },
          rotation: -55.29,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.335043, lng: -75.629755 },
              { lat: 39.335215, lng: -75.629438 },
              { lat: 39.335235, lng: -75.629383 },
              { lat: 39.335309, lng: -75.629275 },
              { lat: 39.335371, lng: -75.62924 },
              { lat: 39.335494, lng: -75.629213 },
              { lat: 39.335619, lng: -75.629314 }
            ]
          }
        },
        '5': {
          id: '7-D5',
          phaseId: 5,
          location: { lat: 39.335274, lng: -75.629055 },
          size: { width: 6, height: 25 },
          rotation: 34.49,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.334406, lng: -75.628293 },
              { lat: 39.335274, lng: -75.629055 },
              { lat: 39.335351, lng: -75.62914 },
              { lat: 39.335371, lng: -75.62924 },
              { lat: 39.335363, lng: -75.62935 },
              { lat: 39.33532, lng: -75.629464 },
              { lat: 39.335153, lng: -75.629777 }
            ]
          }
        },
        '6': {
          id: '7-D6',
          phaseId: 6,
          location: { lat: 39.336282, lng: -75.630197 },
          size: { width: 6, height: 6 },
          rotation: -145.6,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.337055, lng: -75.630884 },
              { lat: 39.336282, lng: -75.630197 },
              { lat: 39.335201, lng: -75.629245 }
            ]
          }
        },
        '7': {
          id: '7-D7',
          phaseId: 0,
          location: { lat: 39.335153, lng: -75.629777 },
          size: { width: 6, height: 6 },
          rotation: -55.29
        },
        '9': {
          id: '7-D9',
          phaseId: 2,
          location: { lat: 39.334554, lng: -75.628312 },
          size: { width: 6, height: 6 },
          rotation: 34.49,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.333521, lng: -75.627392 },
              { lat: 39.334554, lng: -75.628312 },
              { lat: 39.335638, lng: -75.629278 }
            ]
          }
        },
        '10': {
          id: '7-D10',
          phaseId: 6,
          location: { lat: 39.336298, lng: -75.630162 },
          size: { width: 6, height: 6 },
          rotation: -145.6,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.337065, lng: -75.630848 },
              { lat: 39.336298, lng: -75.630162 },
              { lat: 39.33522, lng: -75.629211 }
            ]
          }
        },
        P1: {
          id: '7-DP1',
          phaseId: 0,
          location: { lat: 39.335619, lng: -75.629314 },
          size: { width: 6, height: 6 },
          rotation: 34.49
        },
        P3: {
          id: '7-DP3',
          phaseId: 0,
          location: { lat: 39.335638, lng: -75.629278 },
          size: { width: 6, height: 6 },
          rotation: 34.49
        },
        P5: {
          id: '7-DP5',
          phaseId: 0,
          location: { lat: 39.335201, lng: -75.629245 },
          size: { width: 6, height: 6 },
          rotation: -145.6
        },
        P7: {
          id: '7-DP7',
          phaseId: 0,
          location: { lat: 39.33522, lng: -75.629211 },
          size: { width: 6, height: 6 },
          rotation: -145.6
        }
      }
    }
  },
  {
    id: 4,
    name: 'N561:US 13 @ DUCK CREEK RD',
    label: '4',
    position: { lat: 39.32486, lng: -75.621768 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '4-S1', type: 1, location: { lat: 39.325081, lng: -75.621959 }, rotation: -154.95 },
        '2': { id: '4-S2', type: 0, location: { lat: 39.324664, lng: -75.621525 }, rotation: 25.07 },
        '3': { id: '4-S3', type: 1, location: { lat: 39.324723, lng: -75.622074 }, rotation: -64.66 },
        '4': { id: '4-S4', type: 1, location: { lat: 39.325012, lng: -75.621515 }, rotation: 114.89 },
        '5': { id: '4-S5', type: 1, location: { lat: 39.32465, lng: -75.621573 }, rotation: 25.07 },
        '6': { id: '4-S6', type: 0, location: { lat: 39.325061, lng: -75.622014 }, rotation: -154.95 }
      },
      detectors: {
        '1': {
          id: '4-D1',
          phaseId: 1,
          location: { lat: 39.32505, lng: -75.62194 },
          size: { width: 6, height: 25 },
          rotation: -154.95,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.325952, lng: -75.622481 },
              { lat: 39.32505, lng: -75.62194 },
              { lat: 39.324983, lng: -75.621902 },
              { lat: 39.324902, lng: -75.621817 },
              { lat: 39.324882, lng: -75.621632 },
              { lat: 39.324951, lng: -75.62142 },
              { lat: 39.325094, lng: -75.621025 }
            ]
          }
        },
        '2': {
          id: '4-D2',
          phaseId: 2,
          location: { lat: 39.323662, lng: -75.620957 },
          size: { width: 6, height: 6 },
          rotation: 25.07,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.323662, lng: -75.620957 },
              { lat: 39.323723, lng: -75.620991 },
              { lat: 39.324681, lng: -75.621559 },
              { lat: 39.325205, lng: -75.621868 }
            ]
          }
        },
        '3': {
          id: '4-D3',
          phaseId: 3,
          location: { lat: 39.32472, lng: -75.622074 },
          size: { width: 6, height: 25 },
          rotation: -64.66,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.324526, lng: -75.62261 },
              { lat: 39.32472, lng: -75.622074 },
              { lat: 39.324749, lng: -75.621977 },
              { lat: 39.324834, lng: -75.621835 },
              { lat: 39.324958, lng: -75.62177 },
              { lat: 39.325089, lng: -75.6218 },
              { lat: 39.32532, lng: -75.621939 }
            ]
          }
        },
        '4': {
          id: '4-D4',
          phaseId: 4,
          location: { lat: 39.324983, lng: -75.621543 },
          size: { width: 6, height: 25 },
          rotation: 114.89,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.325177, lng: -75.621003 },
              { lat: 39.324983, lng: -75.621543 },
              { lat: 39.324951, lng: -75.621633 },
              { lat: 39.324868, lng: -75.621744 },
              { lat: 39.324773, lng: -75.62179 },
              { lat: 39.32469, lng: -75.621777 },
              { lat: 39.324514, lng: -75.621676 }
            ]
          }
        },
        '5': {
          id: '4-D5',
          phaseId: 5,
          location: { lat: 39.324666, lng: -75.621598 },
          size: { width: 6, height: 25 },
          rotation: 25.07,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.324321, lng: -75.621393 },
              { lat: 39.324666, lng: -75.621598 },
              { lat: 39.324728, lng: -75.621636 },
              { lat: 39.324823, lng: -75.621755 },
              { lat: 39.324808, lng: -75.621962 },
              { lat: 39.324729, lng: -75.622142 },
              { lat: 39.324659, lng: -75.622331 }
            ]
          }
        },
        '6': {
          id: '4-D6',
          phaseId: 6,
          location: { lat: 39.325774, lng: -75.622424 },
          size: { width: 6, height: 6 },
          rotation: -154.95,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.326146, lng: -75.622645 },
              { lat: 39.325774, lng: -75.622424 },
              { lat: 39.325025, lng: -75.621974 },
              { lat: 39.324529, lng: -75.621683 }
            ]
          }
        },
        '7': {
          id: '4-D7',
          phaseId: 0,
          location: { lat: 39.324919, lng: -75.62151 },
          size: { width: 6, height: 6 },
          rotation: -64.66
        },
        '8': {
          id: '4-D8',
          phaseId: 0,
          location: { lat: 39.325017, lng: -75.621562 },
          size: { width: 6, height: 25 },
          rotation: 114.89
        },
        '9': {
          id: '4-D9',
          phaseId: 2,
          location: { lat: 39.323674, lng: -75.620916 },
          size: { width: 6, height: 6 },
          rotation: 25.07,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.323674, lng: -75.620916 },
              { lat: 39.323706, lng: -75.620935 },
              { lat: 39.3247, lng: -75.621523 },
              { lat: 39.325467, lng: -75.621973 }
            ]
          }
        },
        '10': {
          id: '4-D10',
          phaseId: 6,
          location: { lat: 39.325766, lng: -75.622468 },
          size: { width: 6, height: 6 },
          rotation: -154.95,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.326138, lng: -75.622688 },
              { lat: 39.325766, lng: -75.622468 },
              { lat: 39.325012, lng: -75.622015 },
              { lat: 39.324515, lng: -75.621723 }
            ]
          }
        },
        '11': {
          id: '4-D11',
          phaseId: 0,
          location: { lat: 39.324775, lng: -75.621397 },
          size: { width: 6, height: 6 },
          rotation: -9.25
        },
        P1: {
          id: '4-DP1',
          phaseId: 0,
          location: { lat: 39.325826, lng: -75.622238 },
          size: { width: 6, height: 6 },
          rotation: 25.07
        },
        P3: {
          id: '4-DP3',
          phaseId: 0,
          location: { lat: 39.325846, lng: -75.622202 },
          size: { width: 6, height: 6 },
          rotation: 25.07
        },
        P5: {
          id: '4-DP5',
          phaseId: 0,
          location: { lat: 39.323605, lng: -75.621124 },
          size: { width: 6, height: 6 },
          rotation: -154.95
        },
        P7: {
          id: '4-DP7',
          phaseId: 0,
          location: { lat: 39.323593, lng: -75.621169 },
          size: { width: 6, height: 6 },
          rotation: -154.95
        }
      }
    }
  },
  {
    id: 5,
    name: 'N526:US 13 @ REST AREA',
    label: '5',
    position: { lat: 39.320665, lng: -75.619277 },
    phases: [1, 2, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '5-S1', type: 1, location: { lat: 39.320863, lng: -75.619478 }, rotation: -155.57 },
        '2': { id: '5-S2', type: 0, location: { lat: 39.320575, lng: -75.619096 }, rotation: 26.33 },
        '4': { id: '5-S4', type: 1, location: { lat: 39.320788, lng: -75.619031 }, rotation: 117.31 },
        '5': { id: '5-S5', type: 1, location: { lat: 39.320516, lng: -75.61913 }, rotation: 26.33 },
        '6': { id: '5-S6', type: 0, location: { lat: 39.320753, lng: -75.619482 }, rotation: -155.57 }
      },
      detectors: {
        '1': {
          id: '5-D1',
          phaseId: 1,
          location: { lat: 39.320857, lng: -75.619471 },
          size: { width: 6, height: 25 },
          rotation: -155.57,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.322025, lng: -75.620165 },
              { lat: 39.320857, lng: -75.619471 },
              { lat: 39.320819, lng: -75.619446 },
              { lat: 39.320714, lng: -75.619291 },
              { lat: 39.320698, lng: -75.619124 },
              { lat: 39.320771, lng: -75.618928 },
              { lat: 39.320866, lng: -75.618673 }
            ]
          }
        },
        '2': {
          id: '5-D2',
          phaseId: 2,
          location: { lat: 39.319588, lng: -75.618398 },
          size: { width: 6, height: 6 },
          rotation: 26.33,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.319588, lng: -75.618398 },
              { lat: 39.320015, lng: -75.618716 },
              { lat: 39.320627, lng: -75.619108 },
              { lat: 39.320961, lng: -75.619317 }
            ]
          }
        },
        '4': {
          id: '5-D4',
          phaseId: 4,
          location: { lat: 39.32079, lng: -75.619028 },
          size: { width: 6, height: 25 },
          rotation: 117.31,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.320907, lng: -75.618732 },
              { lat: 39.32079, lng: -75.619028 },
              { lat: 39.320769, lng: -75.619091 },
              { lat: 39.320662, lng: -75.619244 },
              { lat: 39.320583, lng: -75.619288 },
              { lat: 39.320471, lng: -75.619281 },
              { lat: 39.320356, lng: -75.619209 }
            ]
          }
        },
        '5': {
          id: '5-D5',
          phaseId: 5,
          location: { lat: 39.320516, lng: -75.619133 },
          size: { width: 6, height: 25 },
          rotation: 26.33,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.319812, lng: -75.618661 },
              { lat: 39.320494, lng: -75.619114 },
              { lat: 39.320516, lng: -75.619133 },
              { lat: 39.320558, lng: -75.619243 },
              { lat: 39.320562, lng: -75.619388 },
              { lat: 39.320585, lng: -75.619495 },
              { lat: 39.320554, lng: -75.619607 }
            ]
          }
        },
        '6': {
          id: '5-D6',
          phaseId: 6,
          location: { lat: 39.321726, lng: -75.620034 },
          size: { width: 6, height: 6 },
          rotation: -155.57,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.322565, lng: -75.620537 },
              { lat: 39.321726, lng: -75.620034 },
              { lat: 39.320685, lng: -75.619412 },
              { lat: 39.320158, lng: -75.619082 }
            ]
          }
        }
      }
    }
  },
  {
    id: 6,
    name: 'N453:US 13 @ RD 487',
    label: '6',
    position: { lat: 39.315581, lng: -75.615092 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '6-S1', type: 1, location: { lat: 39.315723, lng: -75.615305 }, rotation: -145.91 },
        '2': { id: '6-S2', type: 0, location: { lat: 39.315469, lng: -75.614853 }, rotation: 34.53 },
        '3': { id: '6-S3', type: 1, location: { lat: 39.315327, lng: -75.615471 }, rotation: -52.37 },
        '4': { id: '6-S4', type: 1, location: { lat: 39.315791, lng: -75.614789 }, rotation: 127.53 },
        '5': { id: '6-S5', type: 1, location: { lat: 39.315436, lng: -75.614909 }, rotation: 34.53 },
        '6': { id: '6-S6', type: 0, location: { lat: 39.315698, lng: -75.615361 }, rotation: -145.91 }
      },
      detectors: {
        '1': {
          id: '6-D1',
          phaseId: 1,
          location: { lat: 39.315699, lng: -75.615277 },
          size: { width: 6, height: 25 },
          rotation: -145.91,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.316359, lng: -75.615874 },
              { lat: 39.315712, lng: -75.615303 },
              { lat: 39.315699, lng: -75.615277 },
              { lat: 39.315652, lng: -75.615165 },
              { lat: 39.315648, lng: -75.614985 },
              { lat: 39.315708, lng: -75.614862 },
              { lat: 39.315826, lng: -75.614663 }
            ]
          }
        },
        '2': {
          id: '6-D2',
          phaseId: 2,
          location: { lat: 39.314868, lng: -75.614394 },
          size: { width: 6, height: 6 },
          rotation: 34.53,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.314793, lng: -75.614283 },
              { lat: 39.31489, lng: -75.61436 },
              { lat: 39.315547, lng: -75.614949 },
              { lat: 39.315824, lng: -75.61518 }
            ]
          }
        },
        '3': {
          id: '6-D3',
          phaseId: 3,
          location: { lat: 39.315374, lng: -75.615379 },
          size: { width: 6, height: 25 },
          rotation: -52.37,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.315207, lng: -75.615673 },
              { lat: 39.315374, lng: -75.615379 },
              { lat: 39.31544, lng: -75.615265 },
              { lat: 39.315547, lng: -75.615149 },
              { lat: 39.315646, lng: -75.615098 },
              { lat: 39.315739, lng: -75.615113 },
              { lat: 39.315921, lng: -75.615277 }
            ]
          }
        },
        '4': {
          id: '6-D4',
          phaseId: 4,
          location: { lat: 39.31573, lng: -75.614875 },
          size: { width: 6, height: 25 },
          rotation: 127.53,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.315976, lng: -75.61449 },
              { lat: 39.31573, lng: -75.614875 },
              { lat: 39.315678, lng: -75.614991 },
              { lat: 39.315608, lng: -75.615059 },
              { lat: 39.315522, lng: -75.615097 },
              { lat: 39.315452, lng: -75.61511 },
              { lat: 39.315324, lng: -75.615015 }
            ]
          }
        },
        '5': {
          id: '6-D5',
          phaseId: 5,
          location: { lat: 39.315477, lng: -75.614955 },
          size: { width: 6, height: 25 },
          rotation: 34.53,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.314867, lng: -75.614393 },
              { lat: 39.315421, lng: -75.614891 },
              { lat: 39.315477, lng: -75.614955 },
              { lat: 39.315505, lng: -75.615021 },
              { lat: 39.315509, lng: -75.615147 },
              { lat: 39.315471, lng: -75.615308 },
              { lat: 39.315352, lng: -75.615506 }
            ]
          }
        },
        '6': {
          id: '6-D6',
          phaseId: 6,
          location: { lat: 39.316233, lng: -75.61581 },
          size: { width: 6, height: 6 },
          rotation: -145.91,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.316466, lng: -75.616018 },
              { lat: 39.316233, lng: -75.61581 },
              { lat: 39.315601, lng: -75.615258 },
              { lat: 39.315287, lng: -75.614979 }
            ]
          }
        },
        '9': {
          id: '6-D9',
          phaseId: 2,
          location: { lat: 39.31489, lng: -75.61436 },
          size: { width: 6, height: 6 },
          rotation: 34.53,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.314813, lng: -75.614243 },
              { lat: 39.314907, lng: -75.614331 },
              { lat: 39.315566, lng: -75.614911 },
              { lat: 39.315837, lng: -75.615153 }
            ]
          }
        },
        '10': {
          id: '6-D10',
          phaseId: 6,
          location: { lat: 39.316217, lng: -75.615838 },
          size: { width: 6, height: 6 },
          rotation: -145.91,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.316451, lng: -75.616054 },
              { lat: 39.316217, lng: -75.615838 },
              { lat: 39.315574, lng: -75.61529 },
              { lat: 39.315269, lng: -75.615019 }
            ]
          }
        },
        P1: {
          id: '6-DP1',
          phaseId: 0,
          location: { lat: 39.315949, lng: -75.615308 },
          size: { width: 6, height: 6 },
          rotation: 34.53
        },
        P3: {
          id: '6-DP3',
          phaseId: 0,
          location: { lat: 39.315967, lng: -75.615272 },
          size: { width: 6, height: 6 },
          rotation: 34.53
        },
        P5: {
          id: '6-DP5',
          phaseId: 0,
          location: { lat: 39.31534, lng: -75.615027 },
          size: { width: 6, height: 6 },
          rotation: -145.91
        },
        P7: {
          id: '6-DP7',
          phaseId: 0,
          location: { lat: 39.315323, lng: -75.615057 },
          size: { width: 6, height: 6 },
          rotation: -145.91
        }
      }
    }
  },
  {
    id: 101,
    name: 'K102:US 13 @ DE 300',
    label: '101',
    position: { lat: 39.306027, lng: -75.606789 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '101-S1', type: 1, location: { lat: 39.306407, lng: -75.607129 }, rotation: -145.15 },
        '2': { id: '101-S2', type: 0, location: { lat: 39.305885, lng: -75.606568 }, rotation: 34.42 },
        '3': { id: '101-S3', type: 1, location: { lat: 39.306219, lng: -75.606554 }, rotation: 124.86 },
        '4': { id: '101-S4', type: 1, location: { lat: 39.305805, lng: -75.607255 }, rotation: -54.65 },
        '5': { id: '101-S5', type: 1, location: { lat: 39.305857, lng: -75.606618 }, rotation: 34.42 },
        '6': { id: '101-S6', type: 0, location: { lat: 39.306383, lng: -75.607184 }, rotation: -145.15 }
      },
      detectors: {
        '1': {
          id: '101-D1',
          phaseId: 1,
          location: { lat: 39.306332, lng: -75.607064 },
          size: { width: 6, height: 25 },
          rotation: -145.15,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.306568, lng: -75.60728 },
              { lat: 39.306332, lng: -75.607064 },
              { lat: 39.306294, lng: -75.607037 },
              { lat: 39.30612, lng: -75.606851 },
              { lat: 39.306079, lng: -75.606704 },
              { lat: 39.306102, lng: -75.606595 },
              { lat: 39.306135, lng: -75.606519 }
            ]
          }
        },
        '2': {
          id: '101-D2',
          phaseId: 2,
          location: { lat: 39.305427, lng: -75.60619 },
          size: { width: 6, height: 6 },
          rotation: 34.42,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305016, lng: -75.605831 },
              { lat: 39.305427, lng: -75.60619 },
              { lat: 39.305972, lng: -75.606671 },
              { lat: 39.3065, lng: -75.607133 }
            ]
          }
        },
        '3': {
          id: '101-D3',
          phaseId: 3,
          location: { lat: 39.306174, lng: -75.606593 },
          size: { width: 6, height: 25 },
          rotation: 124.86,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.306206, lng: -75.606529 },
              { lat: 39.306174, lng: -75.606593 },
              { lat: 39.306138, lng: -75.606667 },
              { lat: 39.306081, lng: -75.60677 },
              { lat: 39.306021, lng: -75.606804 },
              { lat: 39.305958, lng: -75.606784 },
              { lat: 39.305813, lng: -75.606664 }
            ]
          }
        },
        '4': {
          id: '101-D4',
          phaseId: 4,
          location: { lat: 39.305853, lng: -75.607134 },
          size: { width: 6, height: 25 },
          rotation: -54.65,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305657, lng: -75.607503 },
              { lat: 39.305853, lng: -75.607134 },
              { lat: 39.306132, lng: -75.606556 }
            ]
          }
        },
        '5': {
          id: '101-D5',
          phaseId: 5,
          location: { lat: 39.305922, lng: -75.606675 },
          size: { width: 6, height: 25 },
          rotation: 34.42,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305314, lng: -75.606138 },
              { lat: 39.305922, lng: -75.606675 },
              { lat: 39.305954, lng: -75.606702 },
              { lat: 39.306026, lng: -75.606825 },
              { lat: 39.306014, lng: -75.607026 },
              { lat: 39.305942, lng: -75.607155 },
              { lat: 39.305815, lng: -75.607378 }
            ]
          }
        },
        '6': {
          id: '101-D6',
          phaseId: 6,
          location: { lat: 39.306792, lng: -75.607522 },
          size: { width: 6, height: 6 },
          rotation: -145.15,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.307305, lng: -75.607959 },
              { lat: 39.306792, lng: -75.607522 },
              { lat: 39.306279, lng: -75.607077 },
              { lat: 39.305789, lng: -75.606638 }
            ]
          }
        },
        '7': {
          id: '101-D7',
          phaseId: 3,
          location: { lat: 39.3062, lng: -75.606619 },
          size: { width: 6, height: 25 },
          rotation: 124.86,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.306225, lng: -75.606573 },
              { lat: 39.3062, lng: -75.606619 },
              { lat: 39.306109, lng: -75.606802 },
              { lat: 39.305773, lng: -75.607454 }
            ]
          }
        },
        '8': {
          id: '101-D8',
          phaseId: 4,
          location: { lat: 39.305875, lng: -75.607156 },
          size: { width: 6, height: 25 },
          rotation: -54.65,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305731, lng: -75.607425 },
              { lat: 39.305875, lng: -75.607156 },
              { lat: 39.305857, lng: -75.607194 },
              { lat: 39.305969, lng: -75.606981 },
              { lat: 39.306097, lng: -75.606902 },
              { lat: 39.30627, lng: -75.606938 },
              { lat: 39.306427, lng: -75.607076 }
            ]
          }
        },
        '9': {
          id: '101-D9',
          phaseId: 2,
          location: { lat: 39.305447, lng: -75.606159 },
          size: { width: 6, height: 6 },
          rotation: 34.42,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305035, lng: -75.605793 },
              { lat: 39.305447, lng: -75.606159 },
              { lat: 39.305985, lng: -75.606632 },
              { lat: 39.306513, lng: -75.607094 }
            ]
          }
        },
        '10': {
          id: '101-D10',
          phaseId: 6,
          location: { lat: 39.306777, lng: -75.607553 },
          size: { width: 6, height: 6 },
          rotation: -145.15,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.307292, lng: -75.607999 },
              { lat: 39.306777, lng: -75.607553 },
              { lat: 39.306268, lng: -75.607102 },
              { lat: 39.305775, lng: -75.606669 }
            ]
          }
        },
        '11': {
          id: '101-D11',
          phaseId: 0,
          location: { lat: 39.305922, lng: -75.607182 },
          size: { width: 6, height: 6 },
          rotation: 124.86
        },
        '12': {
          id: '101-D12',
          phaseId: 4,
          location: { lat: 39.305827, lng: -75.607111 },
          size: { width: 6, height: 12 },
          rotation: -54.65,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.305626, lng: -75.607483 },
              { lat: 39.305827, lng: -75.607111 },
              { lat: 39.305849, lng: -75.607066 },
              { lat: 39.305895, lng: -75.606968 },
              { lat: 39.305926, lng: -75.606892 },
              { lat: 39.305918, lng: -75.606804 },
              { lat: 39.305801, lng: -75.606696 }
            ]
          }
        },
        P1: {
          id: '101-DP1',
          phaseId: 0,
          location: { lat: 39.30634, lng: -75.606994 },
          size: { width: 6, height: 6 },
          rotation: 34.42
        },
        P3: {
          id: '101-DP3',
          phaseId: 0,
          location: { lat: 39.306358, lng: -75.606962 },
          size: { width: 6, height: 6 },
          rotation: 34.42
        },
        P5: {
          id: '101-DP5',
          phaseId: 0,
          location: { lat: 39.305898, lng: -75.606736 },
          size: { width: 6, height: 6 },
          rotation: -145.15
        },
        P7: {
          id: '101-DP7',
          phaseId: 0,
          location: { lat: 39.305884, lng: -75.606767 },
          size: { width: 6, height: 6 },
          rotation: -145.15
        }
      }
    }
  },
  {
    id: 70,
    name: 'K119:US 13 @ BELMONT AVE',
    label: '70',
    position: { lat: 39.291327, lng: -75.596581 },
    phases: [1, 2, 4, 5, 6, 8],
    layout: {
      signals: {
        '1': { id: '70-S1', type: 1, location: { lat: 39.291537, lng: -75.596679 }, rotation: -159.43 },
        '2': { id: '70-S2', type: 0, location: { lat: 39.291126, lng: -75.596399 }, rotation: 20.27 },
        '4': { id: '70-S4', type: 1, location: { lat: 39.291207, lng: -75.596899 }, rotation: -69.0 },
        '5': { id: '70-S5', type: 1, location: { lat: 39.291109, lng: -75.596456 }, rotation: 20.27 },
        '6': { id: '70-S6', type: 0, location: { lat: 39.291522, lng: -75.59674 }, rotation: -159.43 },
        '8': { id: '70-S8', type: 1, location: { lat: 39.2914, lng: -75.596259 }, rotation: 97.04 }
      },
      detectors: {
        '1': {
          id: '70-D1',
          phaseId: 1,
          location: { lat: 39.291468, lng: -75.596645 },
          size: { width: 6, height: 25 },
          rotation: -159.43,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.291876, lng: -75.596849 },
              { lat: 39.291468, lng: -75.596645 },
              { lat: 39.291427, lng: -75.596631 },
              { lat: 39.291377, lng: -75.596561 },
              { lat: 39.29135, lng: -75.596485 },
              { lat: 39.291354, lng: -75.596342 },
              { lat: 39.291372, lng: -75.596161 }
            ]
          }
        },
        '2': {
          id: '70-D2',
          phaseId: 0,
          location: { lat: 39.292139, lng: -75.597101 },
          size: { width: 6, height: 6 },
          rotation: -159.43
        },
        '4': {
          id: '70-D4',
          phaseId: 4,
          location: { lat: 39.291246, lng: -75.596789 },
          size: { width: 6, height: 25 },
          rotation: -69.0,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.291156, lng: -75.597079 },
              { lat: 39.291246, lng: -75.596789 },
              { lat: 39.291284, lng: -75.596663 },
              { lat: 39.291331, lng: -75.596594 },
              { lat: 39.291405, lng: -75.596564 },
              { lat: 39.291476, lng: -75.59659 },
              { lat: 39.291717, lng: -75.596698 }
            ]
          }
        },
        '5': {
          id: '70-D5',
          phaseId: 5,
          location: { lat: 39.291182, lng: -75.596487 },
          size: { width: 6, height: 25 },
          rotation: 20.27,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.290713, lng: -75.596264 },
              { lat: 39.291182, lng: -75.596487 },
              { lat: 39.291221, lng: -75.596508 },
              { lat: 39.291287, lng: -75.596561 },
              { lat: 39.291325, lng: -75.596624 },
              { lat: 39.291295, lng: -75.59678 },
              { lat: 39.291206, lng: -75.597075 }
            ]
          }
        },
        '6': {
          id: '70-D6',
          phaseId: 0,
          location: { lat: 39.292152, lng: -75.597061 },
          size: { width: 6, height: 6 },
          rotation: -159.43
        },
        '8': {
          id: '70-D8',
          phaseId: 8,
          location: { lat: 39.291389, lng: -75.596366 },
          size: { width: 6, height: 25 },
          rotation: 97.04,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.291421, lng: -75.596083 },
              { lat: 39.291389, lng: -75.596366 },
              { lat: 39.291378, lng: -75.59648 },
              { lat: 39.291331, lng: -75.596556 },
              { lat: 39.291273, lng: -75.59658 },
              { lat: 39.291213, lng: -75.59657 },
              { lat: 39.290864, lng: -75.596407 }
            ]
          }
        },
        '11': {
          id: '70-D11',
          phaseId: 4,
          location: { lat: 39.291155, lng: -75.596631 },
          size: { width: 6, height: 6 },
          rotation: -159.43,
          route: {
            centerIdx: 3,
            coords: [
              { lat: 39.291142, lng: -75.597071 },
              { lat: 39.29121, lng: -75.596779 },
              { lat: 39.291191, lng: -75.596689 },
              { lat: 39.291155, lng: -75.596631 },
              { lat: 39.291048, lng: -75.596581 },
              { lat: 39.290816, lng: -75.596473 }
            ]
          }
        },
        '12': {
          id: '70-D12',
          phaseId: 6,
          location: { lat: 39.291173, lng: -75.596591 },
          size: { width: 6, height: 6 },
          rotation: -159.43,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.291173, lng: -75.596591 },
              { lat: 39.291409, lng: -75.596703 },
              { lat: 39.290959, lng: -75.596493 }
            ]
          }
        },
        '13': {
          id: '70-D13',
          phaseId: 6,
          location: { lat: 39.291178, lng: -75.596553 },
          size: { width: 6, height: 6 },
          rotation: -159.43,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.291178, lng: -75.596553 },
              { lat: 39.291415, lng: -75.596665 },
              { lat: 39.290969, lng: -75.596453 }
            ]
          }
        },
        P1: {
          id: '70-DP1',
          phaseId: 8,
          location: { lat: 39.292024, lng: -75.596765 },
          size: { width: 6, height: 6 },
          rotation: 20.27,
          route: {
            centerIdx: 5,
            coords: [
              { lat: 39.291467, lng: -75.595851 },
              { lat: 39.291448, lng: -75.596148 },
              { lat: 39.291473, lng: -75.5963 },
              { lat: 39.291558, lng: -75.596475 },
              { lat: 39.291697, lng: -75.596604 },
              { lat: 39.292024, lng: -75.596765 }
            ]
          }
        },
        P3: {
          id: '70-DP3',
          phaseId: 2,
          location: { lat: 39.292017, lng: -75.596803 },
          size: { width: 6, height: 6 },
          rotation: 20.27,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.292017, lng: -75.596803 },
              { lat: 39.291241, lng: -75.596428 },
              { lat: 39.291921, lng: -75.596756 }
            ]
          }
        },
        P5: {
          id: '70-DP5',
          phaseId: 2,
          location: { lat: 39.292007, lng: -75.59684 },
          size: { width: 6, height: 6 },
          rotation: 20.27,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.292007, lng: -75.59684 },
              { lat: 39.291231, lng: -75.596474 },
              { lat: 39.291914, lng: -75.596802 }
            ]
          }
        }
      }
    }
  },
  {
    id: 67,
    name: 'K201:US 13 @ S SMYRNA RAMP',
    label: '67',
    position: { lat: 39.281833, lng: -75.593292 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '67-S1', type: 1, location: { lat: 39.282049, lng: -75.593409 }, rotation: -166.54 },
        '2': { id: '67-S2', type: 0, location: { lat: 39.281604, lng: -75.593093 }, rotation: 14.24 },
        '3': { id: '67-S3', type: 0, location: { lat: 39.281732, lng: -75.593681 }, rotation: -76.83 },
        '4': { id: '67-S4', type: 0, location: { lat: 39.281963, lng: -75.592943 }, rotation: 107.14 },
        '5': { id: '67-S5', type: 1, location: { lat: 39.281591, lng: -75.593153 }, rotation: 14.24 },
        '6': { id: '67-S6', type: 0, location: { lat: 39.282034, lng: -75.593473 }, rotation: -166.54 }
      },
      detectors: {
        '1': {
          id: '67-D1',
          phaseId: 1,
          location: { lat: 39.28198, lng: -75.593387 },
          size: { width: 6, height: 25 },
          rotation: -166.54,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.282781, lng: -75.593639 },
              { lat: 39.28198, lng: -75.593387 },
              { lat: 39.281944, lng: -75.593375 },
              { lat: 39.281904, lng: -75.593323 },
              { lat: 39.281875, lng: -75.593271 },
              { lat: 39.28184, lng: -75.593132 },
              { lat: 39.281914, lng: -75.5928 }
            ]
          }
        },
        '2': {
          id: '67-D2',
          phaseId: 2,
          location: { lat: 39.280622, lng: -75.592786 },
          size: { width: 6, height: 6 },
          rotation: 14.24,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.280622, lng: -75.592786 },
              { lat: 39.280694, lng: -75.592812 },
              { lat: 39.281692, lng: -75.59314 },
              { lat: 39.28219, lng: -75.593295 }
            ]
          }
        },
        '3': {
          id: '67-D3',
          phaseId: 3,
          location: { lat: 39.281759, lng: -75.593601 },
          size: { width: 6, height: 25 },
          rotation: -76.83,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.281703, lng: -75.593903 },
              { lat: 39.281759, lng: -75.593601 },
              { lat: 39.281783, lng: -75.593467 },
              { lat: 39.281833, lng: -75.593321 },
              { lat: 39.281901, lng: -75.593248 },
              { lat: 39.281973, lng: -75.593229 },
              { lat: 39.282233, lng: -75.593309 }
            ]
          }
        },
        '4': {
          id: '67-D4',
          phaseId: 4,
          location: { lat: 39.281926, lng: -75.593014 },
          size: { width: 6, height: 25 },
          rotation: 107.14,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.281995, lng: -75.592728 },
              { lat: 39.281926, lng: -75.593014 },
              { lat: 39.281907, lng: -75.593116 },
              { lat: 39.281884, lng: -75.593179 },
              { lat: 39.281836, lng: -75.593248 },
              { lat: 39.28167, lng: -75.593334 },
              { lat: 39.281332, lng: -75.593232 }
            ]
          }
        },
        '5': {
          id: '67-D5',
          phaseId: 5,
          location: { lat: 39.28165, lng: -75.593171 },
          size: { width: 6, height: 25 },
          rotation: 14.24,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.280725, lng: -75.592871 },
              { lat: 39.28165, lng: -75.593171 },
              { lat: 39.281691, lng: -75.593185 },
              { lat: 39.28178, lng: -75.593267 },
              { lat: 39.28183, lng: -75.593357 },
              { lat: 39.281837, lng: -75.593461 },
              { lat: 39.281786, lng: -75.593765 }
            ]
          }
        },
        '6': {
          id: '67-D6',
          phaseId: 6,
          location: { lat: 39.282708, lng: -75.593657 },
          size: { width: 6, height: 6 },
          rotation: -166.54,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.282922, lng: -75.593722 },
              { lat: 39.282708, lng: -75.593657 },
              { lat: 39.281937, lng: -75.593418 },
              { lat: 39.281335, lng: -75.593233 }
            ]
          }
        },
        '7': {
          id: '67-D7',
          phaseId: 3,
          location: { lat: 39.281726, lng: -75.593589 },
          size: { width: 6, height: 25 },
          rotation: -76.83,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.28167, lng: -75.593899 },
              { lat: 39.281726, lng: -75.593589 },
              { lat: 39.281757, lng: -75.593428 },
              { lat: 39.281887, lng: -75.592895 }
            ]
          }
        },
        '8': {
          id: '67-D8',
          phaseId: 4,
          location: { lat: 39.281961, lng: -75.593025 },
          size: { width: 6, height: 25 },
          rotation: 107.14,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.282043, lng: -75.59268 },
              { lat: 39.281961, lng: -75.593025 },
              { lat: 39.281862, lng: -75.593352 },
              { lat: 39.281801, lng: -75.593681 }
            ]
          }
        },
        '9': {
          id: '67-D9',
          phaseId: 2,
          location: { lat: 39.280629, lng: -75.592751 },
          size: { width: 6, height: 6 },
          rotation: 14.24,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.280629, lng: -75.592751 },
              { lat: 39.280709, lng: -75.592777 },
              { lat: 39.281697, lng: -75.593103 },
              { lat: 39.282205, lng: -75.593255 }
            ]
          }
        },
        '10': {
          id: '67-D10',
          phaseId: 6,
          location: { lat: 39.2827, lng: -75.593701 },
          size: { width: 6, height: 6 },
          rotation: -166.54,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.282917, lng: -75.593763 },
              { lat: 39.2827, lng: -75.593701 },
              { lat: 39.281933, lng: -75.593458 },
              { lat: 39.281332, lng: -75.593274 }
            ]
          }
        }
      }
    }
  },
  {
    id: 69,
    name: 'K278:US 13 @ CARTER RD',
    label: '69',
    position: { lat: 39.27846, lng: -75.592116 },
    phases: [1, 2, 3, 4, 5, 6, 7, 8],
    layout: {
      signals: {
        '1': { id: '69-S1', type: 1, location: { lat: 39.278742, lng: -75.592261 }, rotation: -163.95 },
        '2': { id: '69-S2', type: 0, location: { lat: 39.278242, lng: -75.591911 }, rotation: 16.06 },
        '3': { id: '69-S3', type: 1, location: { lat: 39.278593, lng: -75.591679 }, rotation: 109.41 },
        '4': { id: '69-S4', type: 0, location: { lat: 39.27834, lng: -75.592508 }, rotation: -74.02 },
        '5': { id: '69-S5', type: 1, location: { lat: 39.278223, lng: -75.591991 }, rotation: 16.06 },
        '6': { id: '69-S6', type: 0, location: { lat: 39.278726, lng: -75.592327 }, rotation: -163.95 },
        '7': { id: '69-S7', type: 1, location: { lat: 39.278369, lng: -75.59252 }, rotation: -74.02 },
        '8': { id: '69-S8', type: 0, location: { lat: 39.278627, lng: -75.591691 }, rotation: 109.41 }
      },
      detectors: {
        '1': {
          id: '69-D1',
          phaseId: 1,
          location: { lat: 39.278648, lng: -75.592227 },
          size: { width: 6, height: 25 },
          rotation: -163.95,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.279303, lng: -75.592467 },
              { lat: 39.278648, lng: -75.592227 },
              { lat: 39.278598, lng: -75.592213 },
              { lat: 39.278563, lng: -75.592173 },
              { lat: 39.278514, lng: -75.592066 },
              { lat: 39.278497, lng: -75.591875 },
              { lat: 39.278576, lng: -75.591587 }
            ]
          }
        },
        '3': {
          id: '69-D3',
          phaseId: 3,
          location: { lat: 39.278569, lng: -75.591767 },
          size: { width: 6, height: 25 },
          rotation: 109.41,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.278655, lng: -75.591446 },
              { lat: 39.278569, lng: -75.591767 },
              { lat: 39.278545, lng: -75.59185 },
              { lat: 39.278472, lng: -75.592048 },
              { lat: 39.2784, lng: -75.59213 },
              { lat: 39.27834, lng: -75.592149 },
              { lat: 39.278126, lng: -75.592077 }
            ]
          }
        },
        '4': {
          id: '69-D4',
          phaseId: 4,
          location: { lat: 39.27837, lng: -75.592379 },
          size: { width: 6, height: 25 },
          rotation: -74.02,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.27826, lng: -75.592862 },
              { lat: 39.27837, lng: -75.592379 },
              { lat: 39.278397, lng: -75.592267 },
              { lat: 39.278565, lng: -75.59158 }
            ]
          }
        },
        '5': {
          id: '69-D5',
          phaseId: 5,
          location: { lat: 39.278289, lng: -75.592036 },
          size: { width: 6, height: 25 },
          rotation: 16.06,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.277906, lng: -75.591901 },
              { lat: 39.278289, lng: -75.592036 },
              { lat: 39.278357, lng: -75.592067 },
              { lat: 39.278421, lng: -75.592116 },
              { lat: 39.278457, lng: -75.592169 },
              { lat: 39.27847, lng: -75.592252 },
              { lat: 39.278394, lng: -75.592621 }
            ]
          }
        },
        '6': {
          id: '69-D6',
          phaseId: 6,
          location: { lat: 39.279301, lng: -75.592512 },
          size: { width: 6, height: 6 },
          rotation: -163.95,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.279301, lng: -75.592512 },
              { lat: 39.2786, lng: -75.592253 },
              { lat: 39.278064, lng: -75.592057 }
            ]
          }
        },
        '7': {
          id: '69-D7',
          phaseId: 7,
          location: { lat: 39.278398, lng: -75.592389 },
          size: { width: 6, height: 25 },
          rotation: -74.02,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.278233, lng: -75.593142 },
              { lat: 39.278398, lng: -75.592389 },
              { lat: 39.278423, lng: -75.592296 },
              { lat: 39.278508, lng: -75.592183 },
              { lat: 39.278595, lng: -75.592132 },
              { lat: 39.278702, lng: -75.592099 },
              { lat: 39.279122, lng: -75.592249 }
            ]
          }
        },
        '8': {
          id: '69-D8',
          phaseId: 8,
          location: { lat: 39.278602, lng: -75.591779 },
          size: { width: 6, height: 25 },
          rotation: 109.41,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.278676, lng: -75.591483 },
              { lat: 39.278602, lng: -75.591779 },
              { lat: 39.278548, lng: -75.591936 },
              { lat: 39.278337, lng: -75.592871 }
            ]
          }
        },
        '9': {
          id: '69-D9',
          phaseId: 5,
          location: { lat: 39.278297, lng: -75.591997 },
          size: { width: 6, height: 25 },
          rotation: 16.06,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.277918, lng: -75.59186 },
              { lat: 39.278297, lng: -75.591997 },
              { lat: 39.278368, lng: -75.592025 },
              { lat: 39.278435, lng: -75.592079 },
              { lat: 39.278485, lng: -75.592145 },
              { lat: 39.278506, lng: -75.592263 },
              { lat: 39.27843, lng: -75.592623 }
            ]
          }
        },
        '11': {
          id: '69-D11',
          phaseId: 6,
          location: { lat: 39.279293, lng: -75.592553 },
          size: { width: 6, height: 6 },
          rotation: -163.95,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.279293, lng: -75.592553 },
              { lat: 39.27859, lng: -75.592294 },
              { lat: 39.278061, lng: -75.592099 }
            ]
          }
        },
        '12': {
          id: '69-D12',
          phaseId: 0,
          location: { lat: 39.278416, lng: -75.592539 },
          size: { width: 6, height: 25 },
          rotation: 109.41
        },
        '13': {
          id: '69-D13',
          phaseId: 0,
          location: { lat: 39.278445, lng: -75.592554 },
          size: { width: 6, height: 25 },
          rotation: 109.41
        },
        '14': {
          id: '69-D14',
          phaseId: 8,
          location: { lat: 39.278633, lng: -75.591792 },
          size: { width: 6, height: 25 },
          rotation: 109.41,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.27871, lng: -75.591481 },
              { lat: 39.278633, lng: -75.591792 },
              { lat: 39.278613, lng: -75.591871 },
              { lat: 39.278624, lng: -75.591945 },
              { lat: 39.278654, lng: -75.591998 },
              { lat: 39.278717, lng: -75.592064 },
              { lat: 39.278958, lng: -75.592149 }
            ]
          }
        },
        P1: {
          id: '69-DP1',
          phaseId: 2,
          location: { lat: 39.279328, lng: -75.592324 },
          size: { width: 6, height: 6 },
          rotation: 16.06,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.277645, lng: -75.591716 },
              { lat: 39.27834, lng: -75.591967 },
              { lat: 39.279328, lng: -75.592324 }
            ]
          }
        },
        P3: {
          id: '69-DP3',
          phaseId: 2,
          location: { lat: 39.279338, lng: -75.592287 },
          size: { width: 6, height: 6 },
          rotation: 16.06,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.277657, lng: -75.591682 },
              { lat: 39.278353, lng: -75.591931 },
              { lat: 39.279338, lng: -75.592287 }
            ]
          }
        },
        P5: {
          id: '69-DP5',
          phaseId: 0,
          location: { lat: 39.277615, lng: -75.591933 },
          size: { width: 6, height: 6 },
          rotation: -163.95
        },
        P7: {
          id: '69-DP7',
          phaseId: 0,
          location: { lat: 39.277625, lng: -75.591892 },
          size: { width: 6, height: 6 },
          rotation: -163.95
        },
        P8: {
          id: '69-DP8',
          phaseId: 0,
          location: { lat: 39.277641, lng: -75.591713 },
          size: { width: 6, height: 6 },
          rotation: 16.06
        }
      }
    }
  },
  {
    id: 68,
    name: 'K247:US 13 @ BIG OAK RD',
    label: '68',
    position: { lat: 39.269738, lng: -75.588819 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '68-S1', type: 1, location: { lat: 39.269949, lng: -75.588959 }, rotation: -162.73 },
        '2': { id: '68-S2', type: 0, location: { lat: 39.269573, lng: -75.588625 }, rotation: 17.32 },
        '3': { id: '68-S3', type: 0, location: { lat: 39.269915, lng: -75.588434 }, rotation: 117.66 },
        '4': { id: '68-S4', type: 0, location: { lat: 39.269575, lng: -75.589222 }, rotation: -68.66 },
        '5': { id: '68-S5', type: 1, location: { lat: 39.269557, lng: -75.588682 }, rotation: 17.32 },
        '6': { id: '68-S6', type: 0, location: { lat: 39.269933, lng: -75.589019 }, rotation: -162.73 }
      },
      detectors: {
        '1': {
          id: '68-D1',
          phaseId: 1,
          location: { lat: 39.269874, lng: -75.588926 },
          size: { width: 6, height: 25 },
          rotation: -162.73,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.270419, lng: -75.589154 },
              { lat: 39.269874, lng: -75.588926 },
              { lat: 39.269828, lng: -75.588908 },
              { lat: 39.269785, lng: -75.588824 },
              { lat: 39.269782, lng: -75.588746 },
              { lat: 39.269796, lng: -75.58861 },
              { lat: 39.269876, lng: -75.588433 }
            ]
          }
        },
        '2': {
          id: '68-D2',
          phaseId: 2,
          location: { lat: 39.268757, lng: -75.588316 },
          size: { width: 6, height: 6 },
          rotation: 17.32,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.268757, lng: -75.588316 },
              { lat: 39.269041, lng: -75.588433 },
              { lat: 39.269682, lng: -75.588689 },
              { lat: 39.27015, lng: -75.588874 }
            ]
          }
        },
        '3': {
          id: '68-D3',
          phaseId: 3,
          location: { lat: 39.269879, lng: -75.588525 },
          size: { width: 6, height: 25 },
          rotation: 117.66,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.269989, lng: -75.588248 },
              { lat: 39.269879, lng: -75.588525 },
              { lat: 39.269844, lng: -75.588597 },
              { lat: 39.269793, lng: -75.588713 },
              { lat: 39.269716, lng: -75.588821 },
              { lat: 39.26963, lng: -75.588881 },
              { lat: 39.269385, lng: -75.588773 }
            ]
          }
        },
        '4': {
          id: '68-D4',
          phaseId: 4,
          location: { lat: 39.269601, lng: -75.589157 },
          size: { width: 6, height: 25 },
          rotation: -68.66,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.269527, lng: -75.589382 },
              { lat: 39.269601, lng: -75.589157 },
              { lat: 39.269629, lng: -75.589058 },
              { lat: 39.269697, lng: -75.58891 },
              { lat: 39.269771, lng: -75.588811 },
              { lat: 39.269873, lng: -75.588777 },
              { lat: 39.270243, lng: -75.588914 }
            ]
          }
        },
        '5': {
          id: '68-D5',
          phaseId: 5,
          location: { lat: 39.269642, lng: -75.588718 },
          size: { width: 6, height: 25 },
          rotation: 17.32,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.269062, lng: -75.588484 },
              { lat: 39.269642, lng: -75.588718 },
              { lat: 39.269685, lng: -75.588738 },
              { lat: 39.26971, lng: -75.588811 },
              { lat: 39.269703, lng: -75.588914 },
              { lat: 39.269679, lng: -75.589013 },
              { lat: 39.269575, lng: -75.589321 }
            ]
          }
        },
        '6': {
          id: '68-D6',
          phaseId: 6,
          location: { lat: 39.270747, lng: -75.589323 },
          size: { width: 6, height: 6 },
          rotation: -162.73,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.270747, lng: -75.589323 },
              { lat: 39.270446, lng: -75.5892 },
              { lat: 39.269817, lng: -75.588956 },
              { lat: 39.269264, lng: -75.588726 }
            ]
          }
        },
        '9': {
          id: '68-D9',
          phaseId: 2,
          location: { lat: 39.26877, lng: -75.588272 },
          size: { width: 6, height: 6 },
          rotation: 17.32,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.26877, lng: -75.588272 },
              { lat: 39.26905, lng: -75.588394 },
              { lat: 39.26969, lng: -75.58865 },
              { lat: 39.27016, lng: -75.588842 }
            ]
          }
        },
        '10': {
          id: '68-D10',
          phaseId: 6,
          location: { lat: 39.270736, lng: -75.589365 },
          size: { width: 6, height: 6 },
          rotation: -162.73,
          route: {
            centerIdx: 0,
            coords: [
              { lat: 39.270736, lng: -75.589365 },
              { lat: 39.270435, lng: -75.589247 },
              { lat: 39.269808, lng: -75.588994 },
              { lat: 39.26925, lng: -75.588768 }
            ]
          }
        },
        P1: {
          id: '68-DP1',
          phaseId: 0,
          location: { lat: 39.269929, lng: -75.588787 },
          size: { width: 6, height: 6 },
          rotation: 17.32
        },
        P3: {
          id: '68-DP3',
          phaseId: 0,
          location: { lat: 39.269942, lng: -75.588751 },
          size: { width: 6, height: 6 },
          rotation: 17.32
        },
        P5: {
          id: '68-DP5',
          phaseId: 0,
          location: { lat: 39.269547, lng: -75.588843 },
          size: { width: 6, height: 6 },
          rotation: -162.73
        },
        P7: {
          id: '68-DP7',
          phaseId: 0,
          location: { lat: 39.269534, lng: -75.588883 },
          size: { width: 6, height: 6 },
          rotation: -162.73
        }
      }
    }
  },
  {
    id: 71,
    name: 'K288:US 13 @ HICKORY RIDGE',
    label: '71',
    position: { lat: 39.249244, lng: -75.587385 },
    phases: [1, 2, 3, 4, 5, 6],
    layout: {
      signals: {
        '1': { id: '71-S1', type: 1, location: { lat: 39.249494, lng: -75.587373 }, rotation: 168.3 },
        '2': { id: '71-S2', type: 0, location: { lat: 39.249019, lng: -75.587321 }, rotation: -11.69 },
        '3': { id: '71-S3', type: 0, location: { lat: 39.249227, lng: -75.586887 }, rotation: 70.02 },
        '4': { id: '71-S4', type: 0, location: { lat: 39.249281, lng: -75.587811 }, rotation: -97.79 },
        '5': { id: '71-S5', type: 1, location: { lat: 39.249027, lng: -75.58738 }, rotation: -11.69 },
        '6': { id: '71-S6', type: 0, location: { lat: 39.249503, lng: -75.587436 }, rotation: 168.3 }
      },
      detectors: {
        '1': {
          id: '71-D1',
          phaseId: 1,
          location: { lat: 39.249418, lng: -75.587393 },
          size: { width: 6, height: 25 },
          rotation: 168.3,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.249967, lng: -75.58726 },
              { lat: 39.249418, lng: -75.587393 },
              { lat: 39.249374, lng: -75.587408 },
              { lat: 39.249309, lng: -75.587381 },
              { lat: 39.249252, lng: -75.587332 },
              { lat: 39.249176, lng: -75.587215 },
              { lat: 39.249149, lng: -75.586984 }
            ]
          }
        },
        '2': {
          id: '71-D2',
          phaseId: 0,
          location: { lat: 39.248107, lng: -75.587571 },
          size: { width: 6, height: 6 },
          rotation: -11.69
        },
        '3': {
          id: '71-D3',
          phaseId: 3,
          location: { lat: 39.249259, lng: -75.587027 },
          size: { width: 6, height: 25 },
          rotation: 70.02,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.249215, lng: -75.58685 },
              { lat: 39.249259, lng: -75.587027 },
              { lat: 39.249271, lng: -75.587123 },
              { lat: 39.249277, lng: -75.587285 },
              { lat: 39.249238, lng: -75.587416 },
              { lat: 39.249182, lng: -75.587493 },
              { lat: 39.24895, lng: -75.587555 }
            ]
          }
        },
        '4': {
          id: '71-D4',
          phaseId: 4,
          location: { lat: 39.249272, lng: -75.587668 },
          size: { width: 6, height: 25 },
          rotation: -97.79,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.249299, lng: -75.588021 },
              { lat: 39.249272, lng: -75.587668 },
              { lat: 39.249265, lng: -75.587612 },
              { lat: 39.249262, lng: -75.587478 },
              { lat: 39.249304, lng: -75.587343 },
              { lat: 39.249364, lng: -75.587257 },
              { lat: 39.24963, lng: -75.587183 }
            ]
          }
        },
        '5': {
          id: '71-D5',
          phaseId: 5,
          location: { lat: 39.249102, lng: -75.587363 },
          size: { width: 6, height: 25 },
          rotation: -11.69,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 39.248755, lng: -75.587452 },
              { lat: 39.249102, lng: -75.587363 },
              { lat: 39.249144, lng: -75.587356 },
              { lat: 39.249209, lng: -75.587379 },
              { lat: 39.249269, lng: -75.587441 },
              { lat: 39.249297, lng: -75.587587 },
              { lat: 39.249314, lng: -75.587849 }
            ]
          }
        },
        '6': {
          id: '71-D6',
          phaseId: 0,
          location: { lat: 39.250346, lng: -75.587202 },
          size: { width: 6, height: 6 },
          rotation: 168.3
        },
        P1: {
          id: '71-DP1',
          phaseId: 2,
          location: { lat: 39.249424, lng: -75.587238 },
          size: { width: 6, height: 6 },
          rotation: -11.69,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.24856, lng: -75.587461 },
              { lat: 39.249138, lng: -75.587313 },
              { lat: 39.249424, lng: -75.587238 },
              { lat: 39.249642, lng: -75.587182 }
            ]
          }
        },
        P3: {
          id: '71-DP3',
          phaseId: 2,
          location: { lat: 39.249414, lng: -75.587198 },
          size: { width: 6, height: 6 },
          rotation: -11.69,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.248557, lng: -75.587413 },
              { lat: 39.249132, lng: -75.587272 },
              { lat: 39.249414, lng: -75.587198 },
              { lat: 39.249636, lng: -75.587146 }
            ]
          }
        },
        P5: {
          id: '71-DP5',
          phaseId: 6,
          location: { lat: 39.249115, lng: -75.58751 },
          size: { width: 6, height: 6 },
          rotation: 168.3,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.249977, lng: -75.587299 },
              { lat: 39.249385, lng: -75.587449 },
              { lat: 39.249115, lng: -75.58751 },
              { lat: 39.248786, lng: -75.587597 }
            ]
          }
        },
        P7: {
          id: '71-DP7',
          phaseId: 6,
          location: { lat: 39.249124, lng: -75.587551 },
          size: { width: 6, height: 6 },
          rotation: 168.3,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 39.24998, lng: -75.587345 },
              { lat: 39.24939, lng: -75.587487 },
              { lat: 39.249124, lng: -75.587551 },
              { lat: 39.248795, lng: -75.587639 }
            ]
          }
        }
      }
    }
  },
  {
    id: 72,
    name: 'S113:US 113 & RT 16',
    label: '72',
    position: { lat: 38.806743, lng: -75.438725 },
    phases: [1, 2, 3, 4, 5, 6, 7, 8],
    layout: {
      signals: {
        '1': { id: '72-S1', type: 1, location: { lat: 38.806965, lng: -75.438778 }, rotation: -179.09 },
        '2': { id: '72-S2', type: 0, location: { lat: 38.806506, lng: -75.438595 }, rotation: 0.85 },
        '3': { id: '72-S3', type: 1, location: { lat: 38.806762, lng: -75.438291 }, rotation: 91.92 },
        '4': { id: '72-S4', type: 0, location: { lat: 38.806702, lng: -75.439229 }, rotation: -88.94 },
        '5': { id: '72-S5', type: 1, location: { lat: 38.806508, lng: -75.438673 }, rotation: 0.85 },
        '6': { id: '72-S6', type: 0, location: { lat: 38.806963, lng: -75.43887 }, rotation: -179.09 },
        '7': { id: '72-S7', type: 1, location: { lat: 38.806735, lng: -75.439231 }, rotation: -88.94 },
        '8': { id: '72-S8', type: 0, location: { lat: 38.806797, lng: -75.438299 }, rotation: 91.92 }
      },
      detectors: {
        '1': {
          id: '72-D1',
          phaseId: 1,
          location: { lat: 38.806926, lng: -75.438773 },
          size: { width: 6, height: 25 },
          rotation: -179.09,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 38.807664, lng: -75.438791 },
              { lat: 38.806926, lng: -75.438773 },
              { lat: 38.80682, lng: -75.438695 },
              { lat: 38.806761, lng: -75.438627 },
              { lat: 38.806712, lng: -75.438531 },
              { lat: 38.806718, lng: -75.438088 }
            ]
          }
        },
        '2': {
          id: '72-D2',
          phaseId: 0,
          location: { lat: 38.805148, lng: -75.438584 },
          size: { width: 6, height: 6 },
          rotation: 0.85
        },
        '3': {
          id: '72-D3',
          phaseId: 3,
          location: { lat: 38.80676, lng: -75.438419 },
          size: { width: 6, height: 25 },
          rotation: 91.92,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 38.806771, lng: -75.438015 },
              { lat: 38.80676, lng: -75.438419 },
              { lat: 38.806736, lng: -75.438698 },
              { lat: 38.806712, lng: -75.438776 },
              { lat: 38.806652, lng: -75.438845 },
              { lat: 38.80634, lng: -75.438849 }
            ]
          }
        },
        '4': {
          id: '72-D4',
          phaseId: 2,
          location: { lat: 38.806985, lng: -75.438618 },
          size: { width: 6, height: 6 },
          rotation: 0.85,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 38.805917, lng: -75.438625 },
              { lat: 38.806683, lng: -75.438633 },
              { lat: 38.806985, lng: -75.438618 }
            ]
          }
        },
        '5': {
          id: '72-D5',
          phaseId: 5,
          location: { lat: 38.806574, lng: -75.438687 },
          size: { width: 6, height: 25 },
          rotation: 0.85,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 38.805919, lng: -75.438657 },
              { lat: 38.806574, lng: -75.438687 },
              { lat: 38.80663, lng: -75.438741 },
              { lat: 38.80671, lng: -75.438812 },
              { lat: 38.806779, lng: -75.438917 },
              { lat: 38.806771, lng: -75.439561 }
            ]
          }
        },
        '6': {
          id: '72-D6',
          phaseId: 0,
          location: { lat: 38.808378, lng: -75.438883 },
          size: { width: 6, height: 6 },
          rotation: -179.09
        },
        '7': {
          id: '72-D7',
          phaseId: 0,
          location: { lat: 38.806792, lng: -75.438418 },
          size: { width: 6, height: 25 },
          rotation: 91.92
        },
        '8': {
          id: '72-D8',
          phaseId: 2,
          location: { lat: 38.806985, lng: -75.438582 },
          size: { width: 6, height: 6 },
          rotation: 0.85,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 38.805921, lng: -75.438586 },
              { lat: 38.806683, lng: -75.438598 },
              { lat: 38.806985, lng: -75.438582 }
            ]
          }
        },
        '11': {
          id: '72-D11',
          phaseId: 4,
          location: { lat: 38.806701, lng: -75.439093 },
          size: { width: 6, height: 25 },
          rotation: -88.94,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 38.806688, lng: -75.439841 },
              { lat: 38.806701, lng: -75.439093 },
              { lat: 38.806716, lng: -75.438223 }
            ]
          }
        },
        P1: {
          id: '72-DP1',
          phaseId: 7,
          location: { lat: 38.806732, lng: -75.439094 },
          size: { width: 6, height: 25 },
          rotation: -88.94,
          route: {
            centerIdx: 1,
            coords: [
              { lat: 38.806726, lng: -75.439663 },
              { lat: 38.806732, lng: -75.439094 },
              { lat: 38.806739, lng: -75.438832 },
              { lat: 38.806788, lng: -75.438683 },
              { lat: 38.806886, lng: -75.438622 },
              { lat: 38.80727, lng: -75.438629 }
            ]
          }
        },
        P2: {
          id: '72-DP2',
          phaseId: 6,
          location: { lat: 38.80655, lng: -75.438847 },
          size: { width: 6, height: 6 },
          rotation: -179.09,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 38.80784, lng: -75.438858 },
              { lat: 38.806821, lng: -75.438849 },
              { lat: 38.80655, lng: -75.438847 }
            ]
          }
        },
        P3: {
          id: '72-DP3',
          phaseId: 6,
          location: { lat: 38.806551, lng: -75.438889 },
          size: { width: 6, height: 6 },
          rotation: -179.09,
          route: {
            centerIdx: 2,
            coords: [
              { lat: 38.80784, lng: -75.438902 },
              { lat: 38.806826, lng: -75.438893 },
              { lat: 38.806551, lng: -75.438889 }
            ]
          }
        }
      }
    }
  }
];
