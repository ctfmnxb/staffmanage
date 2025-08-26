module.exports = {
  transpileDependencies: [],
  chainWebpack: config => {
    config.module
      .rule('js')
      .use('babel-loader')
      .loader('babel-loader')
      .tap(options => {
        options = options || {}
        return {
          ...options,
          presets: options.presets || ['@vue/cli-plugin-babel/preset']
        };
      });
  },
  devServer: {
    host: '0.0.0.0',
    hot: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
}
