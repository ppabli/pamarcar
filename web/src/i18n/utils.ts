import { ui, defaultLang } from './ui';

export function getLangFromString(name: string) {
	if (name in ui) return name as keyof typeof ui;
	return defaultLang;
}

export function useTranslations(lang: keyof typeof ui) {
	return function t(key: keyof typeof ui[typeof defaultLang]) {
		return key in ui[lang] ? (ui[lang] as any)[key] : ui[defaultLang][key];
	}
}

export function getSupportedLangs() {
	return Object.keys(ui) as Array<keyof typeof ui>;
}