import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfessor } from '../professor.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../professor.test-samples';

import { ProfessorService } from './professor.service';

const requireRestSample: IProfessor = {
  ...sampleWithRequiredData,
};

describe('Professor Service', () => {
  let service: ProfessorService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfessor | IProfessor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfessorService);
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

    it('should create a Professor', () => {
      const professor = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(professor).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Professor', () => {
      const professor = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(professor).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Professor', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Professor', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Professor', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfessorToCollectionIfMissing', () => {
      it('should add a Professor to an empty array', () => {
        const professor: IProfessor = sampleWithRequiredData;
        expectedResult = service.addProfessorToCollectionIfMissing([], professor);
        expect(expectedResult).toEqual([professor]);
      });

      it('should not add a Professor to an array that contains it', () => {
        const professor: IProfessor = sampleWithRequiredData;
        const professorCollection: IProfessor[] = [
          {
            ...professor,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfessorToCollectionIfMissing(professorCollection, professor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Professor to an array that doesn't contain it", () => {
        const professor: IProfessor = sampleWithRequiredData;
        const professorCollection: IProfessor[] = [sampleWithPartialData];
        expectedResult = service.addProfessorToCollectionIfMissing(professorCollection, professor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(professor);
      });

      it('should add only unique Professor to an array', () => {
        const professorArray: IProfessor[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const professorCollection: IProfessor[] = [sampleWithRequiredData];
        expectedResult = service.addProfessorToCollectionIfMissing(professorCollection, ...professorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const professor: IProfessor = sampleWithRequiredData;
        const professor2: IProfessor = sampleWithPartialData;
        expectedResult = service.addProfessorToCollectionIfMissing([], professor, professor2);
        expect(expectedResult).toEqual([professor, professor2]);
      });

      it('should accept null and undefined values', () => {
        const professor: IProfessor = sampleWithRequiredData;
        expectedResult = service.addProfessorToCollectionIfMissing([], null, professor, undefined);
        expect(expectedResult).toEqual([professor]);
      });

      it('should return initial array if no Professor is added', () => {
        const professorCollection: IProfessor[] = [sampleWithRequiredData];
        expectedResult = service.addProfessorToCollectionIfMissing(professorCollection, undefined, null);
        expect(expectedResult).toEqual(professorCollection);
      });
    });

    describe('compareProfessor', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfessor(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2234 };
        const entity2 = null;

        const compareResult1 = service.compareProfessor(entity1, entity2);
        const compareResult2 = service.compareProfessor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2234 };
        const entity2 = { id: 26501 };

        const compareResult1 = service.compareProfessor(entity1, entity2);
        const compareResult2 = service.compareProfessor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2234 };
        const entity2 = { id: 2234 };

        const compareResult1 = service.compareProfessor(entity1, entity2);
        const compareResult2 = service.compareProfessor(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
