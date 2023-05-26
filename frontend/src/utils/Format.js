import Utils from '@/utils/Utils';

export default {
  formatTimestamp(timestamp) {
    if (timestamp) {
      const d = new Date(timestamp);
      if (Utils.isToday(d)) {
        return Utils.formatTime(d);
      } else {
        return Utils.formatDateAndTimeMinute(d);
      }
    } else {
      return '';
    }
  },

  formatDuration(seconds) {
    if (seconds) {
      return Utils.formatDuration(seconds, false);
    } else {
      return '';
    }
  },

  formatDistance(meters) {
    if (meters) {
      const miles = meters / 1609.34;
      return miles.toFixed(1) + ' mi';
    } else {
      return '';
    }
  },

  formatAddress(address) {
    // remove the street number
    return address.replace(/\d+/, '').replace(', USA', '');
  }
};
