export function getVideoUrl(cameraId) {
  const host = process.env.VUE_APP_CAMERA_HOST;
  return `${host}/live/${cameraId}.stream/playlist.m3u8`;
}
