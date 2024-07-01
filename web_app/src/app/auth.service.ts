import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Email } from './model/email.model';
import { Phone } from './model/phone.model';
import { Password } from './model/password.model';
import { Country } from './model/country.model';
import { Name } from './model/name.model';

@Injectable({
	providedIn: 'root'
})
export class AuthService {

	API_URL: string = 'http://localhost:8080/api/';

	constructor(private http: HttpClient) { }

	isLoggedIn() {

		return localStorage.getItem('token') !== null;

	}

	login(email: Email | null, phone: Phone | null, password: Password) {

		let data: any = {
			"password": password.getPassword()
		};

		if (email !== null) {
			data['email'] = email.getFullEmail();
		}

		if (phone !== null) {
			data['phone'] = phone.getPhone();
		}

		return this.http.post(`${this.API_URL}login`, data);

	}

	register(user_name: Name, country: Country, email: Email, phone: Phone, password: Password) {

		return this.http.post(`${this.API_URL}register`, { user_name: user_name.getName(), country: country.getCode(), email: email.getFullEmail(), phone: phone.getPhone(), password: password.getPassword() });

	}

	generate(email: string, id: string) {

		let body = {
			"email": email,
			"id": id
		};

		let headers = {
			"Content-Type": "application/json"
		};

		return this.http.post(`${this.API_URL}generate`, body, { headers });

	}

	verify(id: number, token: string) {

		let body = {
			"id": id,
			"token": token
		};

		let headers = {
			"Content-Type": "application/json"
		};

		return this.http.post(`${this.API_URL}verify`, body, { headers });

	}

	validate(id: number, token: string) {

		let body = {
			"id": id,
			"token": token
		};

		let headers = {
			"Content-Type": "application/json"
		};

		return this.http.post(`${this.API_URL}validate`, body, { headers });

	}

	disable(id: number) {

		let body = {
			"id": id
		};

		let headers = {
			"Content-Type": "application/json"
		};

		return this.http.post(`${this.API_URL}disable`, body, { headers });

	}

}
