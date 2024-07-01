import { FormControl, Validators } from "@angular/forms";

export class Name {

	private name: string;

	constructor(name: string) {
		this.name = name;
	}

	static validateName(name: string): boolean {

		const control = new FormControl(name, Validators.pattern(/^[a-zA-Z ]{1,50}$/));
		return control.errors === null;

	}

	getName(): string {
		return this.name;
	}

}