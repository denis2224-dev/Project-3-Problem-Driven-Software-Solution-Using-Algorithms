import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITimetableVersion } from '../timetable-version.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../timetable-version.test-samples';

import { RestTimetableVersion, TimetableVersionService } from './timetable-version.service';

const requireRestSample: RestTimetableVersion = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('TimetableVersion Service', () => {
  let service: TimetableVersionService;
  let httpMock: HttpTestingController;
  let expectedResult: ITimetableVersion | ITimetableVersion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TimetableVersionService);
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

    it('should create a TimetableVersion', () => {
      const timetableVersion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(timetableVersion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TimetableVersion', () => {
      const timetableVersion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(timetableVersion).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TimetableVersion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TimetableVersion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TimetableVersion', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTimetableVersionToCollectionIfMissing', () => {
      it('should add a TimetableVersion to an empty array', () => {
        const timetableVersion: ITimetableVersion = sampleWithRequiredData;
        expectedResult = service.addTimetableVersionToCollectionIfMissing([], timetableVersion);
        expect(expectedResult).toEqual([timetableVersion]);
      });

      it('should not add a TimetableVersion to an array that contains it', () => {
        const timetableVersion: ITimetableVersion = sampleWithRequiredData;
        const timetableVersionCollection: ITimetableVersion[] = [
          {
            ...timetableVersion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTimetableVersionToCollectionIfMissing(timetableVersionCollection, timetableVersion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TimetableVersion to an array that doesn't contain it", () => {
        const timetableVersion: ITimetableVersion = sampleWithRequiredData;
        const timetableVersionCollection: ITimetableVersion[] = [sampleWithPartialData];
        expectedResult = service.addTimetableVersionToCollectionIfMissing(timetableVersionCollection, timetableVersion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timetableVersion);
      });

      it('should add only unique TimetableVersion to an array', () => {
        const timetableVersionArray: ITimetableVersion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const timetableVersionCollection: ITimetableVersion[] = [sampleWithRequiredData];
        expectedResult = service.addTimetableVersionToCollectionIfMissing(timetableVersionCollection, ...timetableVersionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const timetableVersion: ITimetableVersion = sampleWithRequiredData;
        const timetableVersion2: ITimetableVersion = sampleWithPartialData;
        expectedResult = service.addTimetableVersionToCollectionIfMissing([], timetableVersion, timetableVersion2);
        expect(expectedResult).toEqual([timetableVersion, timetableVersion2]);
      });

      it('should accept null and undefined values', () => {
        const timetableVersion: ITimetableVersion = sampleWithRequiredData;
        expectedResult = service.addTimetableVersionToCollectionIfMissing([], null, timetableVersion, undefined);
        expect(expectedResult).toEqual([timetableVersion]);
      });

      it('should return initial array if no TimetableVersion is added', () => {
        const timetableVersionCollection: ITimetableVersion[] = [sampleWithRequiredData];
        expectedResult = service.addTimetableVersionToCollectionIfMissing(timetableVersionCollection, undefined, null);
        expect(expectedResult).toEqual(timetableVersionCollection);
      });
    });

    describe('compareTimetableVersion', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTimetableVersion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27241 };
        const entity2 = null;

        const compareResult1 = service.compareTimetableVersion(entity1, entity2);
        const compareResult2 = service.compareTimetableVersion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27241 };
        const entity2 = { id: 13722 };

        const compareResult1 = service.compareTimetableVersion(entity1, entity2);
        const compareResult2 = service.compareTimetableVersion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27241 };
        const entity2 = { id: 27241 };

        const compareResult1 = service.compareTimetableVersion(entity1, entity2);
        const compareResult2 = service.compareTimetableVersion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
