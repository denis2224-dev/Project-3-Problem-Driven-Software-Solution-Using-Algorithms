import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISolverJob } from '../solver-job.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../solver-job.test-samples';

import { RestSolverJob, SolverJobService } from './solver-job.service';

const requireRestSample: RestSolverJob = {
  ...sampleWithRequiredData,
  startedAt: sampleWithRequiredData.startedAt?.toJSON(),
  finishedAt: sampleWithRequiredData.finishedAt?.toJSON(),
};

describe('SolverJob Service', () => {
  let service: SolverJobService;
  let httpMock: HttpTestingController;
  let expectedResult: ISolverJob | ISolverJob[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SolverJobService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SolverJob', () => {
      const solverJob = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(solverJob).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SolverJob', () => {
      const solverJob = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(solverJob).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SolverJob', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SolverJob', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SolverJob', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addSolverJobToCollectionIfMissing', () => {
      it('should add a SolverJob to an empty array', () => {
        const solverJob: ISolverJob = sampleWithRequiredData;
        expectedResult = service.addSolverJobToCollectionIfMissing([], solverJob);
        expect(expectedResult).toEqual([solverJob]);
      });

      it('should not add a SolverJob to an array that contains it', () => {
        const solverJob: ISolverJob = sampleWithRequiredData;
        const solverJobCollection: ISolverJob[] = [
          {
            ...solverJob,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSolverJobToCollectionIfMissing(solverJobCollection, solverJob);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SolverJob to an array that doesn't contain it", () => {
        const solverJob: ISolverJob = sampleWithRequiredData;
        const solverJobCollection: ISolverJob[] = [sampleWithPartialData];
        expectedResult = service.addSolverJobToCollectionIfMissing(solverJobCollection, solverJob);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(solverJob);
      });

      it('should add only unique SolverJob to an array', () => {
        const solverJobArray: ISolverJob[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const solverJobCollection: ISolverJob[] = [sampleWithRequiredData];
        expectedResult = service.addSolverJobToCollectionIfMissing(solverJobCollection, ...solverJobArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const solverJob: ISolverJob = sampleWithRequiredData;
        const solverJob2: ISolverJob = sampleWithPartialData;
        expectedResult = service.addSolverJobToCollectionIfMissing([], solverJob, solverJob2);
        expect(expectedResult).toEqual([solverJob, solverJob2]);
      });

      it('should accept null and undefined values', () => {
        const solverJob: ISolverJob = sampleWithRequiredData;
        expectedResult = service.addSolverJobToCollectionIfMissing([], null, solverJob, undefined);
        expect(expectedResult).toEqual([solverJob]);
      });

      it('should return initial array if no SolverJob is added', () => {
        const solverJobCollection: ISolverJob[] = [sampleWithRequiredData];
        expectedResult = service.addSolverJobToCollectionIfMissing(solverJobCollection, undefined, null);
        expect(expectedResult).toEqual(solverJobCollection);
      });
    });

    describe('compareSolverJob', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSolverJob(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6466 };
        const entity2 = null;

        const compareResult1 = service.compareSolverJob(entity1, entity2);
        const compareResult2 = service.compareSolverJob(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6466 };
        const entity2 = { id: 24390 };

        const compareResult1 = service.compareSolverJob(entity1, entity2);
        const compareResult2 = service.compareSolverJob(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6466 };
        const entity2 = { id: 6466 };

        const compareResult1 = service.compareSolverJob(entity1, entity2);
        const compareResult2 = service.compareSolverJob(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
