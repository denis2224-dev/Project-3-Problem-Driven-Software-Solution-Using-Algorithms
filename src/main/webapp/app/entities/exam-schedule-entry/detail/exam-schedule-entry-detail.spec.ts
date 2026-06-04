import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ExamScheduleEntryDetail } from './exam-schedule-entry-detail';

describe('ExamScheduleEntry Management Detail Component', () => {
  let comp: ExamScheduleEntryDetail;
  let fixture: ComponentFixture<ExamScheduleEntryDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./exam-schedule-entry-detail').then(m => m.ExamScheduleEntryDetail),
              resolve: { examScheduleEntry: () => of({ id: 619 }) },
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
    fixture = TestBed.createComponent(ExamScheduleEntryDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load examScheduleEntry on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ExamScheduleEntryDetail);

      // THEN
      expect(instance.examScheduleEntry()).toEqual(expect.objectContaining({ id: 619 }));
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
