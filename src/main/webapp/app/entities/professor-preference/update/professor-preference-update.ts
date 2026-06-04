import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ProfessorPreferenceType } from 'app/entities/enumerations/professor-preference-type.model';
import { IProfessor } from 'app/entities/professor/professor.model';
import { ProfessorService } from 'app/entities/professor/service/professor.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IProfessorPreference } from '../professor-preference.model';
import { ProfessorPreferenceService } from '../service/professor-preference.service';

import { ProfessorPreferenceFormGroup, ProfessorPreferenceFormService } from './professor-preference-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-professor-preference-update',
  templateUrl: './professor-preference-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfessorPreferenceUpdate implements OnInit {
  readonly isSaving = signal(false);
  professorPreference: IProfessorPreference | null = null;
  professorPreferenceTypeValues = Object.keys(ProfessorPreferenceType);

  professorsSharedCollection = signal<IProfessor[]>([]);
  timeslotsSharedCollection = signal<ITimeslot[]>([]);

  protected professorPreferenceService = inject(ProfessorPreferenceService);
  protected professorPreferenceFormService = inject(ProfessorPreferenceFormService);
  protected professorService = inject(ProfessorService);
  protected timeslotService = inject(TimeslotService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfessorPreferenceFormGroup = this.professorPreferenceFormService.createProfessorPreferenceFormGroup();

  compareProfessor = (o1: IProfessor | null, o2: IProfessor | null): boolean => this.professorService.compareProfessor(o1, o2);

  compareTimeslot = (o1: ITimeslot | null, o2: ITimeslot | null): boolean => this.timeslotService.compareTimeslot(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ professorPreference }) => {
      this.professorPreference = professorPreference;
      if (professorPreference) {
        this.updateForm(professorPreference);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const professorPreference = this.professorPreferenceFormService.getProfessorPreference(this.editForm);
    if (professorPreference.id === null) {
      this.subscribeToSaveResponse(this.professorPreferenceService.create(professorPreference));
    } else {
      this.subscribeToSaveResponse(this.professorPreferenceService.update(professorPreference));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfessorPreference | null>): void {
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

  protected updateForm(professorPreference: IProfessorPreference): void {
    this.professorPreference = professorPreference;
    this.professorPreferenceFormService.resetForm(this.editForm, professorPreference);

    this.professorsSharedCollection.update(professors =>
      this.professorService.addProfessorToCollectionIfMissing<IProfessor>(professors, professorPreference.professor),
    );
    this.timeslotsSharedCollection.update(timeslots =>
      this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, professorPreference.timeslot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.professorService
      .query()
      .pipe(map((res: HttpResponse<IProfessor[]>) => res.body ?? []))
      .pipe(
        map((professors: IProfessor[]) =>
          this.professorService.addProfessorToCollectionIfMissing<IProfessor>(professors, this.professorPreference?.professor),
        ),
      )
      .subscribe((professors: IProfessor[]) => this.professorsSharedCollection.set(professors));

    this.timeslotService
      .query()
      .pipe(map((res: HttpResponse<ITimeslot[]>) => res.body ?? []))
      .pipe(
        map((timeslots: ITimeslot[]) =>
          this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, this.professorPreference?.timeslot),
        ),
      )
      .subscribe((timeslots: ITimeslot[]) => this.timeslotsSharedCollection.set(timeslots));
  }
}
