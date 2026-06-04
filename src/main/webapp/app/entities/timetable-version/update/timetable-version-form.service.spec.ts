import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../timetable-version.test-samples';

import { TimetableVersionFormService } from './timetable-version-form.service';

describe('TimetableVersion Form Service', () => {
  let service: TimetableVersionFormService;

  beforeEach(() => {
    service = TestBed.inject(TimetableVersionFormService);
  });

  describe('Service methods', () => {
    describe('createTimetableVersionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimetableVersionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            versionNumber: expect.any(Object),
            createdAt: expect.any(Object),
            totalHardConflicts: expect.any(Object),
            totalSoftPenalty: expect.any(Object),
            averageStudentGap: expect.any(Object),
            roomUtilization: expect.any(Object),
            timetable: expect.any(Object),
            solverJob: expect.any(Object),
          }),
        );
      });

      it('passing ITimetableVersion should create a new form with FormGroup', () => {
        const formGroup = service.createTimetableVersionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            versionNumber: expect.any(Object),
            createdAt: expect.any(Object),
            totalHardConflicts: expect.any(Object),
            totalSoftPenalty: expect.any(Object),
            averageStudentGap: expect.any(Object),
            roomUtilization: expect.any(Object),
            timetable: expect.any(Object),
            solverJob: expect.any(Object),
          }),
        );
      });
    });

    describe('getTimetableVersion', () => {
      it('should return NewTimetableVersion for default TimetableVersion initial value', () => {
        const formGroup = service.createTimetableVersionFormGroup(sampleWithNewData);

        const timetableVersion = service.getTimetableVersion(formGroup);

        expect(timetableVersion).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimetableVersion for empty TimetableVersion initial value', () => {
        const formGroup = service.createTimetableVersionFormGroup();

        const timetableVersion = service.getTimetableVersion(formGroup);

        expect(timetableVersion).toMatchObject({});
      });

      it('should return ITimetableVersion', () => {
        const formGroup = service.createTimetableVersionFormGroup(sampleWithRequiredData);

        const timetableVersion = service.getTimetableVersion(formGroup);

        expect(timetableVersion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimetableVersion should not enable id FormControl', () => {
        const formGroup = service.createTimetableVersionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimetableVersion should disable id FormControl', () => {
        const formGroup = service.createTimetableVersionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
