import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IFaculty } from '../faculty.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../faculty.test-samples';

import { FacultyService } from './faculty.service';

const requireRestSample: IFaculty = {
  ...sampleWithRequiredData,
};

describe('Faculty Service', () => {
  let service: FacultyService;
  let httpMock: HttpTestingController;
  let expectedResult: IFaculty | IFaculty[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FacultyService);
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

    it('should create a Faculty', () => {
      const faculty = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(faculty).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Faculty', () => {
      const faculty = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(faculty).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Faculty', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Faculty', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Faculty', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addFacultyToCollectionIfMissing', () => {
      it('should add a Faculty to an empty array', () => {
        const faculty: IFaculty = sampleWithRequiredData;
        expectedResult = service.addFacultyToCollectionIfMissing([], faculty);
        expect(expectedResult).toEqual([faculty]);
      });

      it('should not add a Faculty to an array that contains it', () => {
        const faculty: IFaculty = sampleWithRequiredData;
        const facultyCollection: IFaculty[] = [
          {
            ...faculty,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFacultyToCollectionIfMissing(facultyCollection, faculty);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Faculty to an array that doesn't contain it", () => {
        const faculty: IFaculty = sampleWithRequiredData;
        const facultyCollection: IFaculty[] = [sampleWithPartialData];
        expectedResult = service.addFacultyToCollectionIfMissing(facultyCollection, faculty);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(faculty);
      });

      it('should add only unique Faculty to an array', () => {
        const facultyArray: IFaculty[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const facultyCollection: IFaculty[] = [sampleWithRequiredData];
        expectedResult = service.addFacultyToCollectionIfMissing(facultyCollection, ...facultyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const faculty: IFaculty = sampleWithRequiredData;
        const faculty2: IFaculty = sampleWithPartialData;
        expectedResult = service.addFacultyToCollectionIfMissing([], faculty, faculty2);
        expect(expectedResult).toEqual([faculty, faculty2]);
      });

      it('should accept null and undefined values', () => {
        const faculty: IFaculty = sampleWithRequiredData;
        expectedResult = service.addFacultyToCollectionIfMissing([], null, faculty, undefined);
        expect(expectedResult).toEqual([faculty]);
      });

      it('should return initial array if no Faculty is added', () => {
        const facultyCollection: IFaculty[] = [sampleWithRequiredData];
        expectedResult = service.addFacultyToCollectionIfMissing(facultyCollection, undefined, null);
        expect(expectedResult).toEqual(facultyCollection);
      });
    });

    describe('compareFaculty', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFaculty(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20867 };
        const entity2 = null;

        const compareResult1 = service.compareFaculty(entity1, entity2);
        const compareResult2 = service.compareFaculty(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20867 };
        const entity2 = { id: 15041 };

        const compareResult1 = service.compareFaculty(entity1, entity2);
        const compareResult2 = service.compareFaculty(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20867 };
        const entity2 = { id: 20867 };

        const compareResult1 = service.compareFaculty(entity1, entity2);
        const compareResult2 = service.compareFaculty(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
