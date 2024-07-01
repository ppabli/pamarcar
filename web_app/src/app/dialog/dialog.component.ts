import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
	selector: 'app-dialog',
	templateUrl: './dialog.component.html',
	styleUrl: './dialog.component.css'
})
export class DialogComponent {

	@Input() destination: string = '';
	@Input() mode: number = 0;

	form: FormGroup = new FormGroup({
		token: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)])
	});

	constructor(private dialogRef: MatDialogRef<DialogComponent>, private auth: AuthService, private snackBar: MatSnackBar, private router: Router) { }

	submit() {

		if (!this.form.valid) {
			return;
		}

		let user_string = localStorage.getItem("user");

		if (!user_string) {
			return;
		}

		let user = JSON.parse(user_string);
		let id = user.id;

		if (this.mode == 0) {

			this.verify(id, this.form.value.token);

		} else {

			this.validate(id, this.form.value.token);

		}

	}

	verify(id: number, token: string) {

		this.auth.verify(id, token).subscribe((response: any) => {

			if (response.status == "success") {

				this.router.navigate([this.destination]);

				localStorage.setItem("user", JSON.stringify(response.data.user));

				this.dialogRef.close();

			} else {

				this.snackBar.open(response.message, 'Close', { duration: 2000 });

			}

		});

	}

	validate(id: number, token: string) {

		this.auth.validate(id, token).subscribe((response: any) => {

			if (response.status == "success") {

				this.router.navigate([this.destination]);

				localStorage.setItem("user", JSON.stringify(response.data.user));

				this.dialogRef.close();

			} else {

				this.snackBar.open(response.message, 'Close', { duration: 2000 });

			}

		});

	}

	close() {
		this.dialogRef.close();
	}

}
