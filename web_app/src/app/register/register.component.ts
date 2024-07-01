import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Email } from '../model/email.model';
import { Password } from '../model/password.model';
import { Phone } from '../model/phone.model';
import { Country } from '../model/country.model';
import { Name } from '../model/name.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

	countries: Country[] = [
		new Country('United States', 'UE', 1),
		new Country('España', 'ES', 34),
	];

	form: FormGroup = new FormGroup({
		user_name: new FormControl('', [Validators.required, Validators.minLength(3)]),
		email: new FormControl('', [Validators.required, Validators.email]),
		country: new FormControl('', [Validators.required]),
		phone: new FormControl('', [Validators.required]),
		password: new FormControl('', [Validators.required, Validators.minLength(8)])
	});

	constructor(private authService: AuthService, private router: Router, private snackBar: MatSnackBar) { }

	submit() {

		if (!this.form.valid) {
			this.snackBar.open('Invalid form', 'Close', { duration: 2000 });
			return;
		}

		let { user_name, email, country, phone, password } = this.form.value;

		user_name = new Name(user_name);
		email = new Email(email);
		phone = new Phone(country.getPrefix(), phone);
		password = new Password(password);

		this.authService.register(user_name, country, email, phone, password).subscribe((response: any) => {

			if (response.status === 'success') {

				this.router.navigate(['/login']);

			} else {

				this.snackBar.open('Error', 'Close', { duration: 2000 });

			}

		});

	}

}
