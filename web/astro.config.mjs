// @ts-check
import { defineConfig } from 'astro/config';
import tailwindcss from '@tailwindcss/vite';
import react from '@astrojs/react';

// https://astro.build/config
export default defineConfig({
	vite: {
		plugins: [tailwindcss()]
	},

	i18n: {
		locales: ['en', 'es'],
		defaultLocale: 'es',
		routing: {
			prefixDefaultLocale: true,
		},
	},

	integrations: [react()],

});