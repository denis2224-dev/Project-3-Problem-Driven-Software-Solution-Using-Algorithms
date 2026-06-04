import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IBuilding } from '../building.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../building.test-samples';

import { BuildingService } from './building.service';

const requireRestSample: IBuilding = {
  ...sampleWithRequiredData,
};

describe('Building Service', () => {
  let service: BuildingService;
  let httpMock: HttpTestingController;
  let expectedResult: IBuilding | IBuilding[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BuildingService);
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

    it('should create a Building', () => {
      const building = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(building).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Building', () => {
      const building = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(building).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Building', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Building', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Building', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addBuildingToCollectionIfMissing', () => {
      it('should add a Building to an empty array', () => {
        const building: IBuilding = sampleWithRequiredData;
        expectedResult = service.addBuildingToCollectionIfMissing([], building);
        expect(expectedResult).toEqual([building]);
      });

      it('should not add a Building to an array that contains it', () => {
        const building: IBuilding = sampleWithRequiredData;
        const buildingCollection: IBuilding[] = [
          {
            ...building,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, building);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Building to an array that doesn't contain it", () => {
        const building: IBuilding = sampleWithRequiredData;
        const buildingCollection: IBuilding[] = [sampleWithPartialData];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, building);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(building);
      });

      it('should add only unique Building to an array', () => {
        const buildingArray: IBuilding[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const buildingCollection: IBuilding[] = [sampleWithRequiredData];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, ...buildingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const building: IBuilding = sampleWithRequiredData;
        const building2: IBuilding = sampleWithPartialData;
        expectedResult = service.addBuildingToCollectionIfMissing([], building, building2);
        expect(expectedResult).toEqual([building, building2]);
      });

      it('should accept null and undefined values', () => {
        const building: IBuilding = sampleWithRequiredData;
        expectedResult = service.addBuildingToCollectionIfMissing([], null, building, undefined);
        expect(expectedResult).toEqual([building]);
      });

      it('should return initial array if no Building is added', () => {
        const buildingCollection: IBuilding[] = [sampleWithRequiredData];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, undefined, null);
        expect(expectedResult).toEqual(buildingCollection);
      });
    });

    describe('compareBuilding', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBuilding(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28162 };
        const entity2 = null;

        const compareResult1 = service.compareBuilding(entity1, entity2);
        const compareResult2 = service.compareBuilding(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28162 };
        const entity2 = { id: 11549 };

        const compareResult1 = service.compareBuilding(entity1, entity2);
        const compareResult2 = service.compareBuilding(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28162 };
        const entity2 = { id: 28162 };

        const compareResult1 = service.compareBuilding(entity1, entity2);
        const compareResult2 = service.compareBuilding(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
