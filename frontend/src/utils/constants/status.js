export default Object.freeze({
  QUALITY_ERROR_NAMES: [
    'No error',
    'Missing data',
    'Flow status zero',
    'VOS exceeding threshold',
    'VOS consecutive constant',
    'VOS inconsistency',
    'Volume aggregation issue',
    'Timezone mismatch'
  ],

  DISTINCT_COLORS: [
    '#90ed7d',
    '#434348',
    '#f7a35c',
    '#8085e9',
    '#f15c80',
    '#e4d354',
    '#2b908f',
    '#f45b5b',
    '#91e8e1',
    '#7cb5ec'
  ],

  ERROR_COLOR_TYPES: [
    { value: 0, color: '#90ed7d', description: 'No error' },
    { value: 1, color: '#434348', description: 'Missing data' },
    { value: 2, color: '#f7a35c', description: 'Invalid data' },
    { value: 3, color: '#8085e9', description: 'VOS exceeding threshold' },
    { value: 4, color: '#f15c80', description: 'VOS consecutive constant' },
    { value: 5, color: '#e4d354', description: 'VOS inconsistency' },
    { value: 6, color: '#2b908f', description: 'Volume aggregation issue' },
    { value: 7, color: '#f45b5b', description: 'Timezone mismatch' }
  ]
});
