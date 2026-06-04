import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBuilding } from 'app/entities/building/building.model';
import { BuildingService } from 'app/entities/building/service/building.service';
import { IRoom } from '../room.model';
import { RoomService } from '../service/room.service';

import { RoomFormService } from './room-form.service';
import { RoomUpdate } from './room-update';

describe('Room Management Update Component', () => {
  let comp: RoomUpdate;
  let fixture: ComponentFixture<RoomUpdate>;
  let activatedRoute: ActivatedRoute;
  let roomFormService: RoomFormService;
  let roomService: RoomService;
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

    fixture = TestBed.createComponent(RoomUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    roomFormService = TestBed.inject(RoomFormService);
    roomService = TestBed.inject(RoomService);
    buildingService = TestBed.inject(BuildingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Building query and add missing value', () => {
      const room: IRoom = { id: 22394 };
      const building: IBuilding = { id: 28162 };
      room.building = building;

      const buildingCollection: IBuilding[] = [{ id: 28162 }];
      vitest.spyOn(buildingService, 'query').mockReturnValue(of(new HttpResponse({ body: buildingCollection })));
      const additionalBuildings = [building];
      const expectedCollection: IBuilding[] = [...additionalBuildings, ...buildingCollection];
      vitest.spyOn(buildingService, 'addBuildingToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ room });
      comp.ngOnInit();

      expect(buildingService.query).toHaveBeenCalled();
      expect(buildingService.addBuildingToCollectionIfMissing).toHaveBeenCalledWith(
        buildingCollection,
        ...additionalBuildings.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.buildingsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const room: IRoom = { id: 22394 };
      const building: IBuilding = { id: 28162 };
      room.building = building;

      activatedRoute.data = of({ room });
      comp.ngOnInit();

      expect(comp.buildingsSharedCollection()).toContainEqual(building);
      expect(comp.room).toEqual(room);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRoom>();
      const room = { id: 31469 };
      vitest.spyOn(roomFormService, 'getRoom').mockReturnValue(room);
      vitest.spyOn(roomService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ room });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(room);
      saveSubject.complete();

      // THEN
      expect(roomFormService.getRoom).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(roomService.update).toHaveBeenCalledWith(expect.objectContaining(room));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRoom>();
      const room = { id: 31469 };
      vitest.spyOn(roomFormService, 'getRoom').mockReturnValue({ id: null });
      vitest.spyOn(roomService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ room: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(room);
      saveSubject.complete();

      // THEN
      expect(roomFormService.getRoom).toHaveBeenCalled();
      expect(roomService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRoom>();
      const room = { id: 31469 };
      vitest.spyOn(roomService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ room });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(roomService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBuilding', () => {
      it('should forward to buildingService', () => {
        const entity = { id: 28162 };
        const entity2 = { id: 11549 };
        vitest.spyOn(buildingService, 'compareBuilding');
        comp.compareBuilding(entity, entity2);
        expect(buildingService.compareBuilding).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
