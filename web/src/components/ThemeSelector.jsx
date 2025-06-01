
export default function ThemeSelector() {

	const handleChange = (e) => {

		const actualTheme = localStorage.getItem('theme');
		const newTheme = actualTheme === 'dark' ? 'light' : 'dark';
		localStorage.setItem('theme', newTheme);

		document.documentElement.classList.toggle('dark', newTheme === 'dark');
		document.documentElement.classList.toggle('light', newTheme === 'light');
		document.documentElement.setAttribute('data-theme', newTheme);

	}

	return (

		<button onClick={handleChange} className="transition hover:text-black dark:hover:text-white" title="Cambiar tema">

			<svg id="icon-sun" className="hidden dark:inline-block w-6 h-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 3v1m0 16v1m8.66-10.34l-.71.71M4.05 19.95l-.71-.71M21 12h-1M4 12H3m16.66 4.95l-.71-.71M4.05 4.05l-.71.71M12 7a5 5 0 100 10 5 5 0 000-10z" />
			</svg>

			<svg id="icon-moon" className="inline-block dark:hidden w-6 h-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
				<path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 12.79A9 9 0 1111.21 3a7 7 0 009.79 9.79z" />
			</svg>

		</button>

	)

}