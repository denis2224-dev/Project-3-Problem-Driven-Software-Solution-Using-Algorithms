import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ProfessorPreferenceDetail } from './professor-preference-detail';

describe('ProfessorPreference Management Detail Component', () => {
  let comp: ProfessorPreferenceDetail;
  let fixture: ComponentFixture<ProfessorPreferenceDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./professor-preference-detail').then(m => m.ProfessorPreferenceDetail),
              resolve: { professorPreference: () => of({ id: 6094 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessorPreferenceDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load professorPreference on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProfessorPreferenceDetail);

      // THEN
      expect(instance.professorPreference()).toEqual(expect.objectContaining({ id: 6094 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
