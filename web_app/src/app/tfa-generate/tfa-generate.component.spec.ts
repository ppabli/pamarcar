import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TfaGenerateComponent } from './tfa-generate.component';

describe('TfaGenerateComponent', () => {
  let component: TfaGenerateComponent;
  let fixture: ComponentFixture<TfaGenerateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TfaGenerateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TfaGenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
