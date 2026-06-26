import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        host: '127.0.0.1',
        port: 8088,
        proxy: {
            '/flask_api': {
                target: process.env.FLASK_API_URL || 'http://127.0.0.1:5001',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/flask_api/, '')
            },
            '/spring_api': {
                target: process.env.SPRING_API_URL || 'http://127.0.0.1:8888',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/spring_api/, '')
            }
        }
    },
    build:{
        chunkSizeWarningLimit: 1800,
    },
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ElementPlusResolver()],
        }),
    ],
})
