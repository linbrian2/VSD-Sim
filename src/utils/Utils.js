export default {
  capitalize(s) {
    return s && s[0].toUpperCase() + s.slice(1);
  },

  addDay(date, days) {
    const result = new Date();
    result.setDate(date.getDate() + days);
    return result;
  },

  addSeconds(date, seconds) {
    return new Date(date.getTime() + seconds * 1000);
  },

  laterThan(time, secondAgo) {
    const earlierTime = this.addSeconds(new Date(), -secondAgo);
    return time >= earlierTime;
  },

  dateFromString(str, hms) {
    let y = str.substr(0, 4),
      m = str.substr(5, 2) - 1,
      d = str.substr(8, 2);
    let hh = hms !== undefined ? hms.h : 0;
    let mm = hms !== undefined ? hms.m : 0;
    let ss = hms !== undefined ? hms.s : 0;
    let date = new Date(y, m, d, hh, mm, ss);
    return date;
  },

  dateFromDateAndTimeString(currentDate, hhmmstr) {
    const y = currentDate.getFullYear();
    const m = currentDate.getMonth();
    const d = currentDate.getDate();
    let hh = parseInt(hhmmstr.substr(0, 2));
    let mm = parseInt(hhmmstr.substr(3, 2));
    let date = new Date(y, m, d, hh, mm, 0);
    return date;
  },

  dateFromTimeString(date, timeStr) {
    const Y = date.getFullYear();
    const M = date.getMonth();
    const D = date.getDate();

    let hh = 0,
      mm = 0,
      ss = 0;
    let items = timeStr.split(':');
    if (items.length == 3) {
      hh = parseInt(items[0]);
      mm = parseInt(items[1]);
      ss = parseInt(items[2]);
    } else if (items.length == 2) {
      hh = parseInt(items[0]);
      mm = parseInt(items[1]);
    } else {
      hh = parseInt(items[0]);
    }

    return new Date(Y, M, D, hh, mm, ss);
  },

  formatDate(d) {
    let s = d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2);
    return s;
  },

  formatDateWOYear(d) {
    let s = ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2);
    return s;
  },

  formatDateTime(d) {
    let dd = this.formatDate(d);
    let tt = this.formatTime(d);
    return dd + ' ' + tt;
  },

  formatDateLong(d) {
    let options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    return d.toLocaleDateString('en-US', options);
  },
  formatDateLong2(d) {
    let options = { year: 'numeric', month: 'long', day: 'numeric' };
    return d.toLocaleDateString('en-US', options);
  },

  formatDateLong3(d) {
    let options = { month: 'short', day: 'numeric' };
    return d.toLocaleDateString('en-US', options);
  },

  formatTime(d) {
    const h = d.getHours();
    const m = d.getMinutes();
    const s = d.getSeconds();
    let str = ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2) + ':' + ('0' + s).slice(-2);
    return str;
  },
  formatTimeAsMinute(d) {
    const h = d.getHours();
    const m = d.getMinutes();
    let str = ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2);
    return str;
  },
  formatTimeToMinutes(d) {
    const h = d.getHours();
    const m = d.getMinutes();
    let str = ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2);
    return str;
  },
  formatAMPMTime(time) {
    const d = new Date(time);
    let h = d.getHours();
    let m = d.getMinutes();
    const ampm = h >= 12 ? 'PM' : 'AM';

    h = h % 12;
    h = h ? h : 12; // the hour '0' should be '12'
    m = m < 10 ? '0' + m : m;
    const strTime = h + ':' + m + ' ' + ampm;

    return strTime;
  },

  get5MinIndex(startTime, timestamp) {
    return Math.floor((timestamp - startTime) / 300000);
  },

  get5MinIndexOf288(timestamp) {
    const d = new Date(timestamp);
    const h = d.getHours();
    const m = d.getMinutes();
    const idx = h * 12 + Math.floor(m / 5);
    return idx;
  },

  getTodayNumber() {
    let today = new Date();
    let number = today.getFullYear() * 10000 + (today.getMonth() + 1) * 100 + today.getDate();
    return number;
  },
  isToday(someDate) {
    const today = new Date();
    return (
      someDate.getDate() == today.getDate() &&
      someDate.getMonth() == today.getMonth() &&
      someDate.getFullYear() == today.getFullYear()
    );
  },
  isTodayAndBeyond(someDate) {
    if (this.isToday(someDate)) {
      return true;
    }

    const today = new Date();
    return someDate >= today;
  },
  yesterday() {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    return yesterday;
  },
  getSameTimeAsToday(currentDate) {
    const today = new Date();
    const Y = currentDate.getFullYear();
    const M = currentDate.getMonth();
    const D = currentDate.getDate();
    const h = today.getHours();
    const m = today.getMinutes();
    const s = today.getSeconds();
    const date = new Date(Y, M, D, h, m, s);
    return date;
  },
  getStartOfDay(date) {
    const Y = date.getFullYear();
    const M = date.getMonth();
    const D = date.getDate();
    return new Date(Y, M, D, 0, 0, 0);
  },
  getEndOfDay(date) {
    const Y = date.getFullYear();
    const M = date.getMonth();
    const D = date.getDate();
    return new Date(Y, M, D, 23, 59, 59);
  },
  toHHMMSS(durationInSeconds) {
    const sec_num = durationInSeconds;
    let hours = Math.floor(sec_num / 3600);
    let minutes = Math.floor((sec_num - hours * 3600) / 60);
    let seconds = sec_num - hours * 3600 - minutes * 60;

    if (hours < 10) {
      hours = '0' + hours;
    }
    if (minutes < 10) {
      minutes = '0' + minutes;
    }
    if (seconds < 10) {
      seconds = '0' + seconds;
    }
    return hours + ':' + minutes + ':' + seconds;
  },
  toHHMM(durationInMinutes) {
    let hours = Math.floor(durationInMinutes / 60);
    let minutes = Math.floor(durationInMinutes - hours * 60);

    let result = '';
    if (hours > 0) {
      result += hours + ' hr ';
    }
    result += minutes + ' min';

    return result;
  },
  durationToTimeStr(dur) {
    let h = Math.floor(dur / 3600);
    let m = Math.floor((dur % 3600) / 60);
    let s = Math.floor(dur % 60);
    let str = '';
    if (h) str += `${h}h `;
    if (m) str += `${m}m `;
    if (s) str += `${s}s`;
    return str;
  },
  lerpDate(date1, date2, ratio) {
    const d1 = date1.getTime();
    const d2 = date2.getTime();
    const d = d1 * (1.0 - ratio) + d2 * ratio;
    return new Date(d);
  },
  scale2Date(date, ratio) {
    const d1 = this.getStartOfDay(date);
    const d2 = this.getEndOfDay(date);
    return this.lerpDate(d1, d2, ratio);
  },
  date2Scale(date, currentDate) {
    const d1 = this.getStartOfDay(date);
    const d2 = this.getEndOfDay(date);
    const r = (currentDate.getTime() - d1.getTime()) / (d2.getTime() - d1.getTime());
    return r;
  },
  roundToMinutes(date, minutes) {
    const coeff = 1000 * 60 * minutes;
    const rounded = new Date(Math.round(date.getTime() / coeff) * coeff);
    return rounded;
  },
  pad(num, size) {
    num = num.toString();
    while (num.length < size) num = '0' + num;
    return num;
  },
  randomInt(min, max) {
    // min and max included
    return Math.floor(Math.random() * (max - min + 1) + min);
  },
  // Finds the insertion index for an item in an array.
  // Uses compareFn (similar to that provided to array.sort())
  getInsertionIndex(arr, item, compareFn) {
    const itemsCount = arr.length;
    // No items in array, so return insertion index 0
    if (itemsCount === 0) {
      return 0;
    }
    const lastItem = arr[itemsCount - 1];
    // In case the item is beyond the end of this array, or
    // identical to the last item.
    // We need this as for arrays with 1 item start and end will be
    // 0 so we never enter the while loop below.
    if (compareFn(item, lastItem) >= 0) {
      return itemsCount;
    }
    const getMidPoint = (start, end) => Math.floor((end - start) / 2) + start;
    let start = 0;
    let end = itemsCount - 1;
    let index = getMidPoint(start, end);
    // Binary search - start in middle, divide and conquer.
    while (start < end) {
      const curItem = arr[index];
      const comparison = compareFn(item, curItem);
      if (comparison === 0) {
        // Indentical item
        break;
      } else if (comparison < 0) {
        // Target is lower in array, move the index halfway down.
        end = index;
      } else {
        // Target is higher in array, move the index halfway up.
        start = index + 1;
      }
      index = getMidPoint(start, end);
    }
    return index;
  },
  formatNumberToTime(number) {
    return ('0' + number).slice(-2) + ':00';
  },
  fromNow(date) {
    let seconds = Math.floor((new Date() - date) / 1000);
    let years = Math.floor(seconds / 31536000);
    let months = Math.floor(seconds / 2592000);
    let days = Math.floor(seconds / 86400);
    if (days > 548) {
      return years + ' years ago';
    }
    if (days >= 320 && days <= 547) {
      return 'a year ago';
    }
    if (days >= 45 && days <= 319) {
      return months + ' months ago';
    }
    if (days >= 26 && days <= 45) {
      return 'a month ago';
    }
    let hours = Math.floor(seconds / 3600);
    if (hours >= 36 && days <= 25) {
      return days + ' days ago';
    }
    if (hours >= 22 && hours <= 35) {
      return 'a day ago';
    }

    let minutes = Math.floor(seconds / 60);
    if (minutes >= 90 && hours <= 21) {
      return hours + ' hours ago';
    }
    if (minutes >= 45 && minutes <= 89) {
      return 'an hour ago';
    }
    if (seconds >= 90 && minutes <= 44) {
      return minutes + ' minutes ago';
    }
    if (seconds >= 45 && seconds <= 89) {
      return 'a minute ago';
    }
    if (seconds >= 0 && seconds <= 45) {
      return 'a few seconds ago';
    }
  },
  /* Vision / Status? */
  formatDateAndTime(d) {
    if (this.isToday(d)) {
      return this.formatTime(d);
    }
    let date = this.formatDate(d);
    let time = this.formatTime(d);
    return date + ' ' + time;
  },
  formatDateAndTimeMinute(d) {
    if (this.isToday(d)) {
      return this.formatTime(d);
    }
    let date = this.formatDate(d);
    let time = this.formatTimeAsMinute(d);
    return date + ' ' + time;
  },
  getWebSocketUrl(path) {
    const l = window.location;
    return (l.protocol === 'https:' ? 'wss://' : 'ws://') + l.host + '/' + path;
  },
  formatDuration(durationInSecs, includeSeconds) {
    const totalSeconds = durationInSecs;
    const hours = Math.floor(totalSeconds / 3600.0);
    const minutes = Math.floor((totalSeconds - hours * 3600) / 60.0);
    const seconds = Math.floor(totalSeconds - hours * 3600 - minutes * 60);

    let durationStr = hours < 1 ? `${minutes}m` : `${hours}h ${minutes}m`;
    if (includeSeconds) {
      durationStr += ` ${seconds}s`;
    }

    return durationStr;
  },
  numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',');
  },
  toSentenceCase(text) {
    const result = text.replace(/([A-Z])/g, ' $1');
    const finalResult = result.charAt(0).toUpperCase() + result.slice(1);
    return finalResult;
  },
  interpRound(item0, item1, alpha) {
    const result = {};
    for (let key in item0) {
      const value = Math.round(item0[key] * (1 - alpha) + item1[key] * alpha);
      result[key] = value;
    }
    return result;
  },
  getSecondsTillNow(timestamp) {
    const now = new Date().getTime();
    const seconds = (now - timestamp) / 1000;
    return seconds;
  },
  obj2Arr(obj) {
    let result = [];
    for (const prop in obj) {
      result.push({ text: prop, value: obj[prop] });
    }
    return result;
  },
  isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
  },
  isEmpty(obj) {
    return !obj || Object.keys(obj).length === 0;
  },
  isStringEmpty(str) {
    return !str || str.length === 0;
  },
  isValueAllEmpty(obj) {
    let result = true;
    for (const prop in obj) {
      if (obj[prop]) {
        result = false;
        break;
      }
    }
    return result;
  },
  toTitleCase(str) {
    return str.replace(/\w\S*/g, function(txt) {
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
  },
  range(start, end) {
    return new Array(end - start).fill().map((d, i) => i + start);
  },
  arraysEqual(a, b) {
    if (a === b) return true;
    if (a == null || b == null) return false;
    if (a.length !== b.length) return false;

    // If you don't care about the order of the elements inside
    // the array, you should sort both arrays here.
    // Please note that calling sort on an array will modify that array.
    // you might want to clone your array first.
    for (let i = 0; i < a.length; ++i) {
      if (a[i] !== b[i]) return false;
    }
    return true;
  },

  /**
   * Returns a random integer between min (inclusive) and max (inclusive).
   * The value is no lower than min (or the next integer greater than min
   * if min isn't an integer) and no greater than max (or the next integer
   * lower than max if max isn't an integer).
   * Using Math.round() will give you a non-uniform distribution!
   */
  getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
  },

  // Returns a function, that, as long as it continues to be invoked, will not
  // be triggered. The function will be called after it stops being called for
  // N milliseconds. If `immediate` is passed, trigger the function on the
  // leading edge, instead of the trailing.
  debounce(func, wait, immediate) {
    var timeout;
    return function() {
      var context = this,
        args = arguments;
      clearTimeout(timeout);
      timeout = setTimeout(function() {
        timeout = null;
        if (!immediate) func.apply(context, args);
      }, wait);
      if (immediate && !timeout) func.apply(context, args);
    };
  },
  uniqueBy(a, key) {
    let seen = new Set();
    return a.filter(item => {
      let k = key(item);
      return seen.has(k) ? false : seen.add(k);
    });
  },
  loadFromLocalStorage(item) {
    const value = localStorage.getItem(item);
    return value ? JSON.parse(value) : null;
  },
  saveToLocalStorage(item, value) {
    if (value) {
      localStorage.setItem(item, JSON.stringify(value));
    }
  },
  shadeColor(color, percent) {
    let R = parseInt(color.substring(1, 3), 16);
    let G = parseInt(color.substring(3, 5), 16);
    let B = parseInt(color.substring(5, 7), 16);

    R = parseInt((R * (100 + percent)) / 100);
    G = parseInt((G * (100 + percent)) / 100);
    B = parseInt((B * (100 + percent)) / 100);

    R = R < 255 ? R : 255;
    G = G < 255 ? G : 255;
    B = B < 255 ? B : 255;

    const RR = R.toString(16).length == 1 ? '0' + R.toString(16) : R.toString(16);
    const GG = G.toString(16).length == 1 ? '0' + G.toString(16) : G.toString(16);
    const BB = B.toString(16).length == 1 ? '0' + B.toString(16) : B.toString(16);

    return '#' + RR + GG + BB;
  },
  onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
  },
  getNow() {
    const date = new Date();
    return date.toLocaleString('en-US', {
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric',
      hour12: true
    });
  },
  getStrokeColor(level) {
    var color = '#808080';
    switch (level) {
      case 0:
        color = '#0000FF';
        break;
      case 1:
        color = '#339900';
        break;
      case 2:
        color = '#00FF33';
        break;
      case 3:
        color = '#D7DF01';
        break;
      case 4:
        color = '#FFCC55';
        break;
      case 5:
        color = '#FF6600';
        break;
      case 6:
        color = '#FF0000';
        break;
    }
    return color;
  },

  isPositiveInteger(str) {
    if (typeof str !== 'string') {
      return false;
    }

    const num = Number(str);
    if (Number.isInteger(num) && num > 0) {
      return true;
    }

    return false;
  },

  deepCopy(data) {
    return JSON.parse(JSON.stringify(data));
  },
  downloadJSON(fileName, data) {
    const stringify = require('json-stringify-pretty-compact');
    let dataJSON = stringify(data, null, 2);
    var a = document.createElement('a');
    var file = new Blob([dataJSON], { type: 'application/json' });
    a.href = URL.createObjectURL(file);
    a.download = `${fileName}.json`;
    a.click();
  },
  parseArrStr(data) {
    if (data == '[]') return [];
    var dataSet = [];
    var tempArr = data
      .replace('[', '')
      .replace(']', '')
      .replace(' ', '')
      .split(',');
    for (var i = 0; i < tempArr.length; i++) {
      dataSet[i] = parseFloat(tempArr[i]);
    }
    return dataSet;
  },

  getUniqueKeyValues(data, key) {
    return data.map(item => item[key]).filter((value, index, self) => self.indexOf(value) === index);
  },

  formatXX(val) {
    if (val == 0) {
      return '00';
    } else if (val < 10) {
      return '0' + val;
    } else {
      return val;
    }
  },

  toggleFullScreen() {
    let doc = window.document;
    let docEl = doc.documentElement;

    let requestFullScreen =
      docEl.requestFullscreen ||
      docEl.mozRequestFullScreen ||
      docEl.webkitRequestFullScreen ||
      docEl.msRequestFullscreen;
    let cancelFullScreen =
      doc.exitFullscreen || doc.mozCancelFullScreen || doc.webkitExitFullscreen || doc.msExitFullscreen;

    if (
      !doc.fullscreenElement &&
      !doc.mozFullScreenElement &&
      !doc.webkitFullscreenElement &&
      !doc.msFullscreenElement
    ) {
      requestFullScreen.call(docEl);
    } else {
      cancelFullScreen.call(doc);
    }
  },

  /*
   * Takes a 3 or 6-digit hex color code, and an optional numeric alpha value
   */
  hexToRGBA(hex, alpha) {
    if (typeof hex !== 'string' || hex[0] !== '#') return null; // or return 'transparent'

    const stringValues =
      hex.length === 4
        ? [hex.slice(1, 2), hex.slice(2, 3), hex.slice(3, 4)].map(n => `${n}${n}`)
        : [hex.slice(1, 3), hex.slice(3, 5), hex.slice(5, 7)];
    const intValues = stringValues.map(n => parseInt(n, 16));

    return typeof alpha === 'number' ? `rgba(${intValues.join(', ')}, ${alpha})` : `rgb(${intValues.join(', ')})`;
  }
};
