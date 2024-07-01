import { FormControl, Validators } from "@angular/forms";

export class Password {

	private password: string;

	constructor(password: string) {
		this.password = password;
	}

	static validatePassword(password: string): boolean {

		const control = new FormControl(password, Validators.minLength(8));
		return control.errors === null;

	}

	getPassword(): string {
		return this.password;
	}

}