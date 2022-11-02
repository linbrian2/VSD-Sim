const { InjectManifest } = require('workbox-webpack-plugin');

module.exports = {
  productionSourceMap: false,
  publicPath: process.env.VUE_APP_BASE_URL,
  devServer: {
    disableHostCheck: true
  },
  transpileDependencies: ['vuetify'],
  filenameHashing: true,
  chainWebpack: config => {
    // remove the prefetch plugin
    config.plugins.delete('prefetch');
    config.optimization.delete('splitChunks');
  },
  configureWebpack: {
    plugins: [
      new InjectManifest({
        swSrc: './service-worker.js',
        maximumFileSizeToCacheInBytes: 5000000
      })
    ]
  }
};
