export default {
  getMapOptions(showLabels, dark, mapPrefs) {
    /* console.log(mapPrefs); */
    let mapOptions = {
      center: mapPrefs.center,
      scrollwheel: true,
      disableDefaultUI: true,
      zoom: mapPrefs.zoom,
      mapTypeId: "roadmap",
      styles: [],
    }
    if (dark) {
      mapOptions.styles = [
        // https://snazzymaps.com/style/2/midnight-commander
        {
          featureType: "all",
          elementType: "labels.text.fill",
          stylers: [
            {
              color: "#ffffff",
            },
          ],
        },
        {
          featureType: "all",
          elementType: "labels.text.stroke",
          stylers: [
            {
              color: "#000000",
            },
            {
              lightness: 13,
            },
          ],
        },
        {
          featureType: "administrative",
          elementType: "geometry.fill",
          stylers: [
            {
              color: "#000000",
            },
          ],
        },
        {
          featureType: "administrative",
          elementType: "geometry.stroke",
          stylers: [
            {
              color: "#144b53",
            },
            {
              lightness: 14,
            },
            {
              weight: 1.4,
            },
          ],
        },
        {
          featureType: "landscape",
          elementType: "all",
          stylers: [
            {
              color: "#08304b",
            },
          ],
        },
        {
          featureType: "poi",
          elementType: "geometry",
          stylers: [
            {
              color: "#0c4152",
            },
            {
              lightness: 5,
            },
          ],
        },
        {
          featureType: "road.highway",
          elementType: "geometry.fill",
          stylers: [
            {
              color: "#000000",
            },
          ],
        },
        {
          featureType: "road.highway",
          elementType: "geometry.stroke",
          stylers: [
            {
              color: "#0b434f",
            },
            {
              lightness: 25,
            },
          ],
        },
        {
          featureType: "road.arterial",
          elementType: "geometry.fill",
          stylers: [
            {
              color: "#000000",
            },
          ],
        },
        {
          featureType: "road.arterial",
          elementType: "geometry.stroke",
          stylers: [
            {
              color: "#0b3d51",
            },
            {
              lightness: 16,
            },
          ],
        },
        {
          featureType: "road.local",
          elementType: "geometry",
          stylers: [
            {
              color: "#000000",
            },
          ],
        },
        {
          featureType: "transit",
          elementType: "all",
          stylers: [
            {
              color: "#146474",
            },
          ],
        },
        {
          featureType: "water",
          elementType: "all",
          stylers: [
            {
              color: "#021019",
            },
          ],
        },
      ]
    }
    if (!showLabels) {
      mapOptions.styles.push({
        featureType: "administrative.neighborhood",
        elementType: "labels",
        stylers: [
          { "visibility": "off" }
        ]
      },
      {
        featureType: "administrative.land_parcel",
        elementType: "labels",
        stylers: [
          { "visibility": "off" }
        ]
      },
      {
        featureType: "administrative.locality",
        elementType: "labels",
        stylers: [
          { "visibility": "off" }
        ]
      },)
    }
    return mapOptions
  },
};