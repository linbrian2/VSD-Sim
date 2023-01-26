function getRedBlueColor(percent) {
  const COLORS = [
    '#ff0000',
    '#ff7300',
    '#ffe100',
    '#aaff00',
    '#37ff00',
    '#00ff37',
    '#00ffaa',
    '#00e1ff',
    '#0073ff',
    '#0000ff'
  ];
  const total = COLORS.length;
  const idx = Math.min(total - 1, Math.max(0, Math.floor(percent * total)));
  return COLORS[idx];
}

export { getRedBlueColor };
