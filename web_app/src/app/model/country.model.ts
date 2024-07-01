export class Country {

	private name: string;
	private code: string;
	private prefix: number;

	constructor(name: string, code: string, prefix: number) {
		this.name = name;
		this.code = code;
		this.prefix = prefix;
	}

	getCode(): string {
		return this.code;
	}

	getName(): string {
		return this.name;
	}

	getPrefix(): number {
		return this.prefix;
	}

}