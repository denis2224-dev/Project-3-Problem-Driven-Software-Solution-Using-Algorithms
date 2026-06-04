import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../student-group.test-samples';

import { StudentGroupFormService } from './student-group-form.service';

describe('StudentGroup Form Service', () => {
  let service: StudentGroupFormService;

  beforeEach(() => {
    service = TestBed.inject(StudentGroupFormService);
  });

  describe('Service methods', () => {
    describe('createStudentGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStudentGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            year: expect.any(Object),
            groupSize: expect.any(Object),
            department: expect.any(Object),
          }),
        );
      });

      it('passing IStudentGroup should create a new form with FormGroup', () => {
        const formGroup = service.createStudentGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            year: expect.any(Object),
            groupSize: expect.any(Object),
            department: expect.any(Object),
          }),
        );
      });
    });

    describe('getStudentGroup', () => {
      it('should return NewStudentGroup for default StudentGroup initial value', () => {
        const formGroup = service.createStudentGroupFormGroup(sampleWithNewData);

        const studentGroup = service.getStudentGroup(formGroup);

        expect(studentGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewStudentGroup for empty StudentGroup initial value', () => {
        const formGroup = service.createStudentGroupFormGroup();

        const studentGroup = service.getStudentGroup(formGroup);

        expect(studentGroup).toMatchObject({});
      });

      it('should return IStudentGroup', () => {
        const formGroup = service.createStudentGroupFormGroup(sampleWithRequiredData);

        const studentGroup = service.getStudentGroup(formGroup);

        expect(studentGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStudentGroup should not enable id FormControl', () => {
        const formGroup = service.createStudentGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStudentGroup should disable id FormControl', () => {
        const formGroup = service.createStudentGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
