import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../solver-job.test-samples';

import { SolverJobFormService } from './solver-job-form.service';

describe('SolverJob Form Service', () => {
  let service: SolverJobFormService;

  beforeEach(() => {
    service = TestBed.inject(SolverJobFormService);
  });

  describe('Service methods', () => {
    describe('createSolverJobFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSolverJobFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            startedAt: expect.any(Object),
            finishedAt: expect.any(Object),
            progressPercent: expect.any(Object),
            message: expect.any(Object),
            hardConflictCount: expect.any(Object),
            softPenaltyScore: expect.any(Object),
            backtrackCount: expect.any(Object),
            domainReductionCount: expect.any(Object),
            runtimeMs: expect.any(Object),
          }),
        );
      });

      it('passing ISolverJob should create a new form with FormGroup', () => {
        const formGroup = service.createSolverJobFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            startedAt: expect.any(Object),
            finishedAt: expect.any(Object),
            progressPercent: expect.any(Object),
            message: expect.any(Object),
            hardConflictCount: expect.any(Object),
            softPenaltyScore: expect.any(Object),
            backtrackCount: expect.any(Object),
            domainReductionCount: expect.any(Object),
            runtimeMs: expect.any(Object),
          }),
        );
      });
    });

    describe('getSolverJob', () => {
      it('should return NewSolverJob for default SolverJob initial value', () => {
        const formGroup = service.createSolverJobFormGroup(sampleWithNewData);

        const solverJob = service.getSolverJob(formGroup);

        expect(solverJob).toMatchObject(sampleWithNewData);
      });

      it('should return NewSolverJob for empty SolverJob initial value', () => {
        const formGroup = service.createSolverJobFormGroup();

        const solverJob = service.getSolverJob(formGroup);

        expect(solverJob).toMatchObject({});
      });

      it('should return ISolverJob', () => {
        const formGroup = service.createSolverJobFormGroup(sampleWithRequiredData);

        const solverJob = service.getSolverJob(formGroup);

        expect(solverJob).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISolverJob should not enable id FormControl', () => {
        const formGroup = service.createSolverJobFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSolverJob should disable id FormControl', () => {
        const formGroup = service.createSolverJobFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
