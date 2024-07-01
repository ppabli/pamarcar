import { Component, Inject, Input } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../dialog/dialog.component';

@Component({
	selector: 'app-tfa-generate',
	templateUrl: './tfa-generate.component.html',
	styleUrl: './tfa-generate.component.css'
})
export class TfaGenerateComponent {

	qrURL: string = '';

	constructor(private authService: AuthService, private router: Router, private dialog: MatDialog) {

	}

	openDialog() {

		const dialogRef = this.dialog.open(DialogComponent, {
			data: {
				destination: '/dashboard',
				mode: 0
			}
		});

	}

	ngOnInit() {

		let user_string = localStorage.getItem("user");
		let user = user_string ? JSON.parse(user_string) : null;

		if (user) {

			let email = user.email;
			let id = user.id;

			this.authService.generate(email, id).subscribe((data: any) => {

				this.qrURL = data.data;

			});

		} else {

			this.router.navigate(['/login']);

		}

	}

}
