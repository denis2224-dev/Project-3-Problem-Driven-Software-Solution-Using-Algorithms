import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IBuilding } from '../building.model';
import { BuildingService } from '../service/building.service';

import { BuildingFormGroup, BuildingFormService } from './building-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-building-update',
  templateUrl: './building-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class BuildingUpdate implements OnInit {
  readonly isSaving = signal(false);
  building: IBuilding | null = null;

  protected buildingService = inject(BuildingService);
  protected buildingFormService = inject(BuildingFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BuildingFormGroup = this.buildingFormService.createBuildingFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ building }) => {
      this.building = building;
      if (building) {
        this.updateForm(building);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const building = this.buildingFormService.getBuilding(this.editForm);
    if (building.id === null) {
      this.subscribeToSaveResponse(this.buildingService.create(building));
    } else {
      this.subscribeToSaveResponse(this.buildingService.update(building));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBuilding | null>): void {
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

  protected updateForm(building: IBuilding): void {
    this.building = building;
    this.buildingFormService.resetForm(this.editForm, building);
  }
}
