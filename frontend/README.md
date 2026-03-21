# Vue 3 + TypeScript + Vite

Project bootstrapped using 
```bash
npm create vite@latest frontend --template vue-ts
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

## Tailwind CSS

This project uses [Tailwind CSS v4](https://tailwindcss.com/) for utility-first styling, integrated via the `@tailwindcss/vite` plugin. Tailwind is imported in `src/style.css`:

```css
@import "tailwindcss";
```

No separate `tailwind.config` file is needed — Tailwind v4 uses CSS-based configuration. Theme customizations (colors, radii, etc.) are defined directly in `src/style.css` using `@theme`.

## shadcn-vue

[shadcn-vue](https://www.shadcn-vue.com/) provides accessible, customizable UI components built on top of Tailwind CSS. The project is configured with the **new-york** style and **lucide** icons (see `components.json`).

Available components here: https://www.shadcn-vue.com/docs/components
### Adding components

To add a new shadcn-vue component:

```bash
npx shadcn-vue@latest add <component-name>
```

For example:

```bash
npx shadcn-vue@latest add button
```

Components are installed into `src/components/ui/` and can be freely customized. Utility functions used by shadcn-vue live in `src/lib/utils.ts`.

### Path aliases

The following aliases are configured for imports:

| Alias | Path |
|-------|------|
| `@/components` | `src/components` |
| `@/components/ui` | `src/components/ui` |
| `@/lib` | `src/lib` |
| `@/composables` | `src/composables` |
