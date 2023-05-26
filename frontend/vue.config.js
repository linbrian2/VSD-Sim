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

    // Add version number to environment variables
    config.plugin('define').tap(args => {
      args[0]['process.env']['VUE_APP_VERSION'] = JSON.stringify(require('./package.json').version);
      args[0]['process.env']['VUE_APP_BUILD_TIME'] = new Date().getTime() + '';
      return args;
    });
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
