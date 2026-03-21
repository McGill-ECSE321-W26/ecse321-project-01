# Vue 3 + TypeScript + Vite

Project bootstrapped using 
```bash
npm create vite@latest
> Vue (Select a framework)
> TypeScript (Select a variant)
```

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).

## Prerequisites

- [Node.js](https://nodejs.org/) v20 or later
- npm (included with Node.js)

## Running the Project

Install dependencies:
```bash
npm install
```

Start the development server:
```bash
npm run dev
```

Build for production:
```bash
npm run build
```

Preview the production build:
```bash
npm run preview
```

## Linting

Lint the codebase:
```bash
npm run lint
```

Lint and auto-fix issues:
```bash
npm run lint:fix
```

ESLint is configured with TypeScript and Vue recommended rules. The CI pipeline will run `npm run lint` on every push and pull request to `main`.
