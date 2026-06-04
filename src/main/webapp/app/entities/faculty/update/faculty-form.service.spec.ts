import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../faculty.test-samples';

import { FacultyFormService } from './faculty-form.service';

describe('Faculty Form Service', () => {
  let service: FacultyFormService;

  beforeEach(() => {
    service = TestBed.inject(FacultyFormService);
  });

  describe('Service methods', () => {
    describe('createFacultyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFacultyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
          }),
        );
      });

      it('passing IFaculty should create a new form with FormGroup', () => {
        const formGroup = service.createFacultyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
          }),
        );
      });
    });

    describe('getFaculty', () => {
      it('should return NewFaculty for default Faculty initial value', () => {
        const formGroup = service.createFacultyFormGroup(sampleWithNewData);

        const faculty = service.getFaculty(formGroup);

        expect(faculty).toMatchObject(sampleWithNewData);
      });

      it('should return NewFaculty for empty Faculty initial value', () => {
        const formGroup = service.createFacultyFormGroup();

        const faculty = service.getFaculty(formGroup);

        expect(faculty).toMatchObject({});
      });

      it('should return IFaculty', () => {
        const formGroup = service.createFacultyFormGroup(sampleWithRequiredData);

        const faculty = service.getFaculty(formGroup);

        expect(faculty).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFaculty should not enable id FormControl', () => {
        const formGroup = service.createFacultyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFaculty should disable id FormControl', () => {
        const formGroup = service.createFacultyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
