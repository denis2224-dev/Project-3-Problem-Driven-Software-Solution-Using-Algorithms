import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IFaculty } from 'app/entities/faculty/faculty.model';
import { FacultyService } from 'app/entities/faculty/service/faculty.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDepartment } from '../department.model';
import { DepartmentService } from '../service/department.service';

import { DepartmentFormGroup, DepartmentFormService } from './department-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-department-update',
  templateUrl: './department-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DepartmentUpdate implements OnInit {
  readonly isSaving = signal(false);
  department: IDepartment | null = null;

  facultiesSharedCollection = signal<IFaculty[]>([]);

  protected departmentService = inject(DepartmentService);
  protected departmentFormService = inject(DepartmentFormService);
  protected facultyService = inject(FacultyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DepartmentFormGroup = this.departmentFormService.createDepartmentFormGroup();

  compareFaculty = (o1: IFaculty | null, o2: IFaculty | null): boolean => this.facultyService.compareFaculty(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ department }) => {
      this.department = department;
      if (department) {
        this.updateForm(department);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const department = this.departmentFormService.getDepartment(this.editForm);
    if (department.id === null) {
      this.subscribeToSaveResponse(this.departmentService.create(department));
    } else {
      this.subscribeToSaveResponse(this.departmentService.update(department));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDepartment | null>): void {
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

  protected updateForm(department: IDepartment): void {
    this.department = department;
    this.departmentFormService.resetForm(this.editForm, department);

    this.facultiesSharedCollection.update(faculties =>
      this.facultyService.addFacultyToCollectionIfMissing<IFaculty>(faculties, department.faculty),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.facultyService
      .query()
      .pipe(map((res: HttpResponse<IFaculty[]>) => res.body ?? []))
      .pipe(
        map((faculties: IFaculty[]) => this.facultyService.addFacultyToCollectionIfMissing<IFaculty>(faculties, this.department?.faculty)),
      )
      .subscribe((faculties: IFaculty[]) => this.facultiesSharedCollection.set(faculties));
  }
}
