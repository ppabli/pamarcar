import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';

import { LoginComponent } from './login/login.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { RegisterComponent } from './register/register.component';
import { TfaGenerateComponent } from './tfa-generate/tfa-generate.component';
import { QrCodeComponent } from './qr-code/qr-code.component';
import { DialogComponent } from './dialog/dialog.component';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { DashboardComponent } from './dashboard/dashboard.component';

@NgModule({
	declarations: [
		AppComponent,
		LoginComponent,
		RegisterComponent,
		TfaGenerateComponent,
		QrCodeComponent,
		DialogComponent,
		HomeComponent,
		NavbarComponent,
  DashboardComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		MatSlideToggleModule,
		MatCardModule,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		ReactiveFormsModule,
		MatDialogModule,
		MatButtonModule,
		MatIconModule,
		MatToolbarModule,
		MatSelectModule,
		MatSidenavModule
	],
	providers: [
		provideClientHydration(),
		provideAnimationsAsync(),
		provideHttpClient(withFetch()),
	],
	bootstrap: [AppComponent]
})
export class AppModule { }
