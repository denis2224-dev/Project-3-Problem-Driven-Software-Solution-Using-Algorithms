import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../professor-preference.test-samples';

import { ProfessorPreferenceFormService } from './professor-preference-form.service';

describe('ProfessorPreference Form Service', () => {
  let service: ProfessorPreferenceFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfessorPreferenceFormService);
  });

  describe('Service methods', () => {
    describe('createProfessorPreferenceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfessorPreferenceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            preferenceType: expect.any(Object),
            professor: expect.any(Object),
            timeslot: expect.any(Object),
          }),
        );
      });

      it('passing IProfessorPreference should create a new form with FormGroup', () => {
        const formGroup = service.createProfessorPreferenceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            preferenceType: expect.any(Object),
            professor: expect.any(Object),
            timeslot: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfessorPreference', () => {
      it('should return NewProfessorPreference for default ProfessorPreference initial value', () => {
        const formGroup = service.createProfessorPreferenceFormGroup(sampleWithNewData);

        const professorPreference = service.getProfessorPreference(formGroup);

        expect(professorPreference).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfessorPreference for empty ProfessorPreference initial value', () => {
        const formGroup = service.createProfessorPreferenceFormGroup();

        const professorPreference = service.getProfessorPreference(formGroup);

        expect(professorPreference).toMatchObject({});
      });

      it('should return IProfessorPreference', () => {
        const formGroup = service.createProfessorPreferenceFormGroup(sampleWithRequiredData);

        const professorPreference = service.getProfessorPreference(formGroup);

        expect(professorPreference).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfessorPreference should not enable id FormControl', () => {
        const formGroup = service.createProfessorPreferenceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfessorPreference should disable id FormControl', () => {
        const formGroup = service.createProfessorPreferenceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
