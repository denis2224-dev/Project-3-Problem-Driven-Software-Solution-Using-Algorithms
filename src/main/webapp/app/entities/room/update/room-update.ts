import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBuilding } from 'app/entities/building/building.model';
import { BuildingService } from 'app/entities/building/service/building.service';
import { RoomType } from 'app/entities/enumerations/room-type.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRoom } from '../room.model';
import { RoomService } from '../service/room.service';

import { RoomFormGroup, RoomFormService } from './room-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-room-update',
  templateUrl: './room-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RoomUpdate implements OnInit {
  readonly isSaving = signal(false);
  room: IRoom | null = null;
  roomTypeValues = Object.keys(RoomType);

  buildingsSharedCollection = signal<IBuilding[]>([]);

  protected roomService = inject(RoomService);
  protected roomFormService = inject(RoomFormService);
  protected buildingService = inject(BuildingService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RoomFormGroup = this.roomFormService.createRoomFormGroup();

  compareBuilding = (o1: IBuilding | null, o2: IBuilding | null): boolean => this.buildingService.compareBuilding(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ room }) => {
      this.room = room;
      if (room) {
        this.updateForm(room);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const room = this.roomFormService.getRoom(this.editForm);
    if (room.id === null) {
      this.subscribeToSaveResponse(this.roomService.create(room));
    } else {
      this.subscribeToSaveResponse(this.roomService.update(room));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRoom | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(room: IRoom): void {
    this.room = room;
    this.roomFormService.resetForm(this.editForm, room);

    this.buildingsSharedCollection.update(buildings =>
      this.buildingService.addBuildingToCollectionIfMissing<IBuilding>(buildings, room.building),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.buildingService
      .query()
      .pipe(map((res: HttpResponse<IBuilding[]>) => res.body ?? []))
      .pipe(
        map((buildings: IBuilding[]) => this.buildingService.addBuildingToCollectionIfMissing<IBuilding>(buildings, this.room?.building)),
      )
      .subscribe((buildings: IBuilding[]) => this.buildingsSharedCollection.set(buildings));
  }
}
