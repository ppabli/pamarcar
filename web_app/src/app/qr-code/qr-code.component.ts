import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-qr-code',
	templateUrl: './qr-code.component.html',
	styleUrl: './qr-code.component.css'
})
export class QrCodeComponent {

	base = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=";

	@Input() url: string = '';
	qrCodeUrl: string = '';

	ngOnChanges(): void {
		if (this.url) {
			this.qrCodeUrl = `${this.base}${encodeURIComponent(this.url)}`;
		}
	}

}
