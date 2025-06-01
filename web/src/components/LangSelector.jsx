
export default function LangSelector({ currentLocale, supportedLocales }) {

	const handleChange = (e) => {

		const newLocale = e.target.value;
		const pathname = window.location.pathname;

		const getRelativeLocaleUrl = (locale, path) => {
			return `/${locale}${path.replace(/^\/[a-z]{2}/, '')}`;
		};

		const newPath = getRelativeLocaleUrl(newLocale, pathname);
		window.location.href = newPath;

	};

	return (
		<select id="lang" className="bg-transparent border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-sm" value={currentLocale} onChange={handleChange}>
			{
				supportedLocales.map((lang) => (
					<option key={lang} value={lang}>
						{lang.toUpperCase()}
					</option>
				))
			}
		</select>
	);
}
