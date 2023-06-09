/* global google */
// http://blog.mridey.com/2011/05/label-overlay-example-for-google-maps.html?m=1

// Define the overlay, derived from google.maps.OverlayView
export function MapLabel(opt_options) {
  // Initialization
  this.setValues(opt_options);

  // Label specific
  var span = (this.span_ = document.createElement('span'));
  span.style.cssText =
    'position: relative; left: -50%; top: -8px; ' +
    'white-space: nowrap; border: 1px solid blue; ' +
    'padding: 2px; background-color: white';

  var div = (this.div_ = document.createElement('div'));
  div.appendChild(span);
  div.style.cssText = 'position: absolute; display: none';
}

MapLabel.prototype = new google.maps.OverlayView();

// Implement onAdd
MapLabel.prototype.onAdd = function() {
  var pane = this.getPanes().overlayImage;
  pane.appendChild(this.div_);

  // Ensures the label is redrawn if the text or position is changed.
  var me = this;
  this.listeners_ = [
    google.maps.event.addListener(this, 'position_changed', function() {
      me.draw();
    }),
    google.maps.event.addListener(this, 'visible_changed', function() {
      me.draw();
    }),
    google.maps.event.addListener(this, 'clickable_changed', function() {
      me.draw();
    }),
    google.maps.event.addListener(this, 'text_changed', function() {
      me.draw();
    }),
    google.maps.event.addListener(this, 'zindex_changed', function() {
      me.draw();
    }),
    google.maps.event.addDomListener(this.div_, 'click', function() {
      if (me.get('clickable')) {
        google.maps.event.trigger(me, 'click');
      }
    })
  ];
};

// Implement onRemove
MapLabel.prototype.onRemove = function() {
  this.div_.parentNode.removeChild(this.div_);

  // Label is removed from the map, stop updating its position/text.
  for (var i = 0, I = this.listeners_.length; i < I; ++i) {
    google.maps.event.removeListener(this.listeners_[i]);
  }
};

// Implement draw
MapLabel.prototype.draw = function() {
  var projection = this.getProjection();
  var position = projection.fromLatLngToDivPixel(this.get('position'));

  var div = this.div_;
  div.style.left = position.x + 'px';
  div.style.top = position.y + 'px';

  var visible = this.get('visible');
  div.style.display = visible ? 'block' : 'none';

  var clickable = this.get('clickable');
  this.span_.style.cursor = clickable ? 'pointer' : '';

  var zIndex = this.get('zIndex');
  div.style.zIndex = zIndex;

  this.span_.innerHTML = this.get('text').toString();
};
