import { FormControl, Validators } from "@angular/forms";

export class Phone {

	private prefix: number;
	private phone: number;

	constructor(prefix: number, phone: number) {
		this.prefix = prefix;
		this.phone = phone;
	}

	static validatePhone(phone: number): boolean {

		const control = new FormControl(phone, Validators.pattern(/^\d{9}$/));
		return control.errors === null;

	}

	getFullPhone(): number {
		return this.prefix + this.phone;
	}

	getPhone(): number {
		return this.phone;
	}

	getPrefix(): number {
		return this.phone;
	}

}