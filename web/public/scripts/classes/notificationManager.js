import { CONFIG } from '/scripts/config/constants.js';

export class NotificationManager {

	static show(message, type = 'info') {

		let container = document.getElementById('notification-container');

		if (!container) {
			container = document.createElement('div');
			container.id = 'notification-container';
			container.className = 'fixed top-4 right-4 flex flex-col gap-3 z-50';
			document.body.appendChild(container);
		}

		const notification = document.createElement('div');

		const typeClasses = {
			error: 'bg-red-600 text-white',
			warning: 'bg-yellow-600 text-white',
			success: 'bg-green-600 text-white',
			info: 'bg-blue-600 text-white'
		};

		notification.className = `px-6 py-4 rounded-lg shadow-lg transform transition-all duration-300 translate-x-full ${typeClasses[type] || typeClasses.info}`;
		notification.textContent = message;

		container.appendChild(notification);

		requestAnimationFrame(() => {
			notification.classList.remove('translate-x-full');
		});

		setTimeout(() => {
			notification.classList.add('translate-x-full');
			setTimeout(() => notification.remove(), 300);
		}, CONFIG.NOTIFICATION_DURATION);

	}

}