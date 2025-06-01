export class Utils {

	static isValidDate(dateString) {

		if (!dateString) {
			return false;
		}

		const date = new Date(dateString);
		return date instanceof Date && !isNaN(date) && dateString === date.toISOString().split('T')[0];

	}

	static isValidEmail(email) {

		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return emailRegex.test(email);

	}

	static isValidPostalCode(postalCode) {

		const postalCodeRegex = /^\d{5}$/;
		return postalCodeRegex.test(postalCode);

	}

	static debounce(func, wait) {

		let timeout;

		return function executedFunction(...args) {
			const later = () => {
				clearTimeout(timeout);
				func(...args);
			};
			clearTimeout(timeout);
			timeout = setTimeout(later, wait);
		};

	}

	static throttle(func, limit) {

		let inThrottle;

		return function() {
			const args = arguments;
			const context = this;
			if (!inThrottle) {
				func.apply(context, args);
				inThrottle = true;
				setTimeout(() => inThrottle = false, limit);
			}
		};

	}

}