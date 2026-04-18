import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      globals: true,
      setupFiles: ['./src/tests/setup.ts'],
      include: ['src/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
      environmentOptions: {
        jsdom: {
          resources: 'usable',
        },
      },
      coverage: {
        provider: 'v8',
        reporter: ['text', 'json', 'html', 'lcov'],
        reportsDirectory: './coverage',
        include: [
          'src/**/*.{js,ts,vue}',
        ],
        exclude: [
          'src/**/__tests__/**',
          'src/**/node_modules/**',
          'src/**/*.d.ts',
          'src/main.ts',
          'src/tests/**',
          'src/mock/**',
        ],
        all: true,
        lines: 60,
        functions: 60,
        branches: 50,
        statements: 60,
        perFile: true,
        watermarks: {
          lines: [50, 80],
          functions: [50, 80],
          branches: [40, 70],
          statements: [50, 80],
        },
      },
      reporters: ['default', 'html'],
      outputFile: {
        html: './test-results/index.html',
      },
      testTimeout: 10000,
      hookTimeout: 10000,
      teardownTimeout: 10000,
      slowTestThreshold: 300,
      singleThread: false,
      minThreads: 1,
      maxThreads: 4,
      watch: false,
      passWithNoTests: true,
      bail: 0,
      retry: 0,
    },
  }),
)
