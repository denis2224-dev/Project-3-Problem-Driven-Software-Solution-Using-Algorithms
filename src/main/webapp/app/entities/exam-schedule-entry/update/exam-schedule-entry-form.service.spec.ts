import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../exam-schedule-entry.test-samples';

import { ExamScheduleEntryFormService } from './exam-schedule-entry-form.service';

describe('ExamScheduleEntry Form Service', () => {
  let service: ExamScheduleEntryFormService;

  beforeEach(() => {
    service = TestBed.inject(ExamScheduleEntryFormService);
  });

  describe('Service methods', () => {
    describe('createExamScheduleEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExamScheduleEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            exam: expect.any(Object),
            room: expect.any(Object),
            timeslot: expect.any(Object),
            timetableVersion: expect.any(Object),
          }),
        );
      });

      it('passing IExamScheduleEntry should create a new form with FormGroup', () => {
        const formGroup = service.createExamScheduleEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            exam: expect.any(Object),
            room: expect.any(Object),
            timeslot: expect.any(Object),
            timetableVersion: expect.any(Object),
          }),
        );
      });
    });

    describe('getExamScheduleEntry', () => {
      it('should return NewExamScheduleEntry for default ExamScheduleEntry initial value', () => {
        const formGroup = service.createExamScheduleEntryFormGroup(sampleWithNewData);

        const examScheduleEntry = service.getExamScheduleEntry(formGroup);

        expect(examScheduleEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewExamScheduleEntry for empty ExamScheduleEntry initial value', () => {
        const formGroup = service.createExamScheduleEntryFormGroup();

        const examScheduleEntry = service.getExamScheduleEntry(formGroup);

        expect(examScheduleEntry).toMatchObject({});
      });

      it('should return IExamScheduleEntry', () => {
        const formGroup = service.createExamScheduleEntryFormGroup(sampleWithRequiredData);

        const examScheduleEntry = service.getExamScheduleEntry(formGroup);

        expect(examScheduleEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExamScheduleEntry should not enable id FormControl', () => {
        const formGroup = service.createExamScheduleEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExamScheduleEntry should disable id FormControl', () => {
        const formGroup = service.createExamScheduleEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
