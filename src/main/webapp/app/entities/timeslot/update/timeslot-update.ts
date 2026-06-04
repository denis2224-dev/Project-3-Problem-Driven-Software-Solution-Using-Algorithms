import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AcademicDayOfWeek } from 'app/entities/enumerations/academic-day-of-week.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TimeslotService } from '../service/timeslot.service';
import { ITimeslot } from '../timeslot.model';

import { TimeslotFormGroup, TimeslotFormService } from './timeslot-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-timeslot-update',
  templateUrl: './timeslot-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TimeslotUpdate implements OnInit {
  readonly isSaving = signal(false);
  timeslot: ITimeslot | null = null;
  academicDayOfWeekValues = Object.keys(AcademicDayOfWeek);

  protected timeslotService = inject(TimeslotService);
  protected timeslotFormService = inject(TimeslotFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TimeslotFormGroup = this.timeslotFormService.createTimeslotFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timeslot }) => {
      this.timeslot = timeslot;
      if (timeslot) {
        this.updateForm(timeslot);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const timeslot = this.timeslotFormService.getTimeslot(this.editForm);
    if (timeslot.id === null) {
      this.subscribeToSaveResponse(this.timeslotService.create(timeslot));
    } else {
      this.subscribeToSaveResponse(this.timeslotService.update(timeslot));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITimeslot | null>): void {
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

  protected updateForm(timeslot: ITimeslot): void {
    this.timeslot = timeslot;
    this.timeslotFormService.resetForm(this.editForm, timeslot);
  }
}
