import { Component, Input } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Password } from '../model/password.model';
import { Email } from '../model/email.model';
import { Phone } from '../model/phone.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

	form: FormGroup = new FormGroup({
		user: new FormControl('', Validators.required),
		password: new FormControl('', Validators.required)
	});

	constructor(private authService: AuthService, private router: Router, private snackBar: MatSnackBar, private dialog: MatDialog) {

		if (this.authService.isLoggedIn()) {

			this.router.navigate(['/dashboard']);

		}

	}

	submit() {

		if (!this.form.valid) {
			this.snackBar.open('Invalid form', 'Close', { duration: 2000 });
			return;
		}

		let validEmail = Email.validateEmail(this.form.value.user);
		let validPhone = Phone.validatePhone(this.form.value.user);

		if (!validEmail && !validPhone) {
			this.snackBar.open('Invalid email or phone', 'Close', { duration: 2000 });
			return;
		}

		let password = new Password(this.form.value.password);
		let email = validEmail ? new Email(this.form.value.user) : null;
		let phone = validPhone ? new Phone(0, this.form.value.user) : null;

		this.authService.login(email, phone, password).subscribe((response: any) => {

			if (response.status === 'success') {

				localStorage.setItem('token', response.token);

				this.router.navigate(['/dashboard']);

			} else {

				this.snackBar.open(response.message, 'Close', { duration: 2000 });

			}

		});

	}

}
