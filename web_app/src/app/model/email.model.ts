import { FormControl, Validators } from "@angular/forms";

export class Email {

	private email: string;

	constructor(email: string) {
		this.email = email;
	}

	static validateEmail(email: string): boolean {

		const control = new FormControl(email, Validators.email);
		return control.errors === null;

	}

	getFullEmail(): string {
		return this.email;
	}

	getDomain(): string {
		return this.email.split('@')[1];
	}

	getUser(): string {
		return this.email.split('@')[0];
	}

}