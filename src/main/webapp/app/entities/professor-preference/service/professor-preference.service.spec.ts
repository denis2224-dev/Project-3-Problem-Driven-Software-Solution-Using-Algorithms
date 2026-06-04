import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfessorPreference } from '../professor-preference.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../professor-preference.test-samples';

import { ProfessorPreferenceService } from './professor-preference.service';

const requireRestSample: IProfessorPreference = {
  ...sampleWithRequiredData,
};

describe('ProfessorPreference Service', () => {
  let service: ProfessorPreferenceService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfessorPreference | IProfessorPreference[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfessorPreferenceService);
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

    it('should create a ProfessorPreference', () => {
      const professorPreference = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(professorPreference).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfessorPreference', () => {
      const professorPreference = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(professorPreference).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfessorPreference', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfessorPreference', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfessorPreference', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfessorPreferenceToCollectionIfMissing', () => {
      it('should add a ProfessorPreference to an empty array', () => {
        const professorPreference: IProfessorPreference = sampleWithRequiredData;
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing([], professorPreference);
        expect(expectedResult).toEqual([professorPreference]);
      });

      it('should not add a ProfessorPreference to an array that contains it', () => {
        const professorPreference: IProfessorPreference = sampleWithRequiredData;
        const professorPreferenceCollection: IProfessorPreference[] = [
          {
            ...professorPreference,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing(professorPreferenceCollection, professorPreference);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfessorPreference to an array that doesn't contain it", () => {
        const professorPreference: IProfessorPreference = sampleWithRequiredData;
        const professorPreferenceCollection: IProfessorPreference[] = [sampleWithPartialData];
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing(professorPreferenceCollection, professorPreference);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(professorPreference);
      });

      it('should add only unique ProfessorPreference to an array', () => {
        const professorPreferenceArray: IProfessorPreference[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const professorPreferenceCollection: IProfessorPreference[] = [sampleWithRequiredData];
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing(professorPreferenceCollection, ...professorPreferenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const professorPreference: IProfessorPreference = sampleWithRequiredData;
        const professorPreference2: IProfessorPreference = sampleWithPartialData;
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing([], professorPreference, professorPreference2);
        expect(expectedResult).toEqual([professorPreference, professorPreference2]);
      });

      it('should accept null and undefined values', () => {
        const professorPreference: IProfessorPreference = sampleWithRequiredData;
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing([], null, professorPreference, undefined);
        expect(expectedResult).toEqual([professorPreference]);
      });

      it('should return initial array if no ProfessorPreference is added', () => {
        const professorPreferenceCollection: IProfessorPreference[] = [sampleWithRequiredData];
        expectedResult = service.addProfessorPreferenceToCollectionIfMissing(professorPreferenceCollection, undefined, null);
        expect(expectedResult).toEqual(professorPreferenceCollection);
      });
    });

    describe('compareProfessorPreference', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfessorPreference(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6094 };
        const entity2 = null;

        const compareResult1 = service.compareProfessorPreference(entity1, entity2);
        const compareResult2 = service.compareProfessorPreference(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6094 };
        const entity2 = { id: 23125 };

        const compareResult1 = service.compareProfessorPreference(entity1, entity2);
        const compareResult2 = service.compareProfessorPreference(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6094 };
        const entity2 = { id: 6094 };

        const compareResult1 = service.compareProfessorPreference(entity1, entity2);
        const compareResult2 = service.compareProfessorPreference(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
