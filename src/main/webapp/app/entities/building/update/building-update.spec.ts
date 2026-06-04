import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBuilding } from '../building.model';
import { BuildingService } from '../service/building.service';

import { BuildingFormService } from './building-form.service';
import { BuildingUpdate } from './building-update';

describe('Building Management Update Component', () => {
  let comp: BuildingUpdate;
  let fixture: ComponentFixture<BuildingUpdate>;
  let activatedRoute: ActivatedRoute;
  let buildingFormService: BuildingFormService;
  let buildingService: BuildingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(BuildingUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    buildingFormService = TestBed.inject(BuildingFormService);
    buildingService = TestBed.inject(BuildingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const building: IBuilding = { id: 11549 };

      activatedRoute.data = of({ building });
      comp.ngOnInit();

      expect(comp.building).toEqual(building);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBuilding>();
      const building = { id: 28162 };
      vitest.spyOn(buildingFormService, 'getBuilding').mockReturnValue(building);
      vitest.spyOn(buildingService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(building);
      saveSubject.complete();

      // THEN
      expect(buildingFormService.getBuilding).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(buildingService.update).toHaveBeenCalledWith(expect.objectContaining(building));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBuilding>();
      const building = { id: 28162 };
      vitest.spyOn(buildingFormService, 'getBuilding').mockReturnValue({ id: null });
      vitest.spyOn(buildingService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(building);
      saveSubject.complete();

      // THEN
      expect(buildingFormService.getBuilding).toHaveBeenCalled();
      expect(buildingService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBuilding>();
      const building = { id: 28162 };
      vitest.spyOn(buildingService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(buildingService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
