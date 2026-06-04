import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITimeslot } from '../timeslot.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../timeslot.test-samples';

import { TimeslotService } from './timeslot.service';

const requireRestSample: ITimeslot = {
  ...sampleWithRequiredData,
};

describe('Timeslot Service', () => {
  let service: TimeslotService;
  let httpMock: HttpTestingController;
  let expectedResult: ITimeslot | ITimeslot[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TimeslotService);
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

    it('should create a Timeslot', () => {
      const timeslot = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(timeslot).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Timeslot', () => {
      const timeslot = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(timeslot).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Timeslot', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Timeslot', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Timeslot', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTimeslotToCollectionIfMissing', () => {
      it('should add a Timeslot to an empty array', () => {
        const timeslot: ITimeslot = sampleWithRequiredData;
        expectedResult = service.addTimeslotToCollectionIfMissing([], timeslot);
        expect(expectedResult).toEqual([timeslot]);
      });

      it('should not add a Timeslot to an array that contains it', () => {
        const timeslot: ITimeslot = sampleWithRequiredData;
        const timeslotCollection: ITimeslot[] = [
          {
            ...timeslot,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTimeslotToCollectionIfMissing(timeslotCollection, timeslot);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Timeslot to an array that doesn't contain it", () => {
        const timeslot: ITimeslot = sampleWithRequiredData;
        const timeslotCollection: ITimeslot[] = [sampleWithPartialData];
        expectedResult = service.addTimeslotToCollectionIfMissing(timeslotCollection, timeslot);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timeslot);
      });

      it('should add only unique Timeslot to an array', () => {
        const timeslotArray: ITimeslot[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const timeslotCollection: ITimeslot[] = [sampleWithRequiredData];
        expectedResult = service.addTimeslotToCollectionIfMissing(timeslotCollection, ...timeslotArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const timeslot: ITimeslot = sampleWithRequiredData;
        const timeslot2: ITimeslot = sampleWithPartialData;
        expectedResult = service.addTimeslotToCollectionIfMissing([], timeslot, timeslot2);
        expect(expectedResult).toEqual([timeslot, timeslot2]);
      });

      it('should accept null and undefined values', () => {
        const timeslot: ITimeslot = sampleWithRequiredData;
        expectedResult = service.addTimeslotToCollectionIfMissing([], null, timeslot, undefined);
        expect(expectedResult).toEqual([timeslot]);
      });

      it('should return initial array if no Timeslot is added', () => {
        const timeslotCollection: ITimeslot[] = [sampleWithRequiredData];
        expectedResult = service.addTimeslotToCollectionIfMissing(timeslotCollection, undefined, null);
        expect(expectedResult).toEqual(timeslotCollection);
      });
    });

    describe('compareTimeslot', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTimeslot(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11059 };
        const entity2 = null;

        const compareResult1 = service.compareTimeslot(entity1, entity2);
        const compareResult2 = service.compareTimeslot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11059 };
        const entity2 = { id: 26281 };

        const compareResult1 = service.compareTimeslot(entity1, entity2);
        const compareResult2 = service.compareTimeslot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11059 };
        const entity2 = { id: 11059 };

        const compareResult1 = service.compareTimeslot(entity1, entity2);
        const compareResult2 = service.compareTimeslot(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
