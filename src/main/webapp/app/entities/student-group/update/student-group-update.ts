import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { StudentGroupService } from '../service/student-group.service';
import { IStudentGroup } from '../student-group.model';

import { StudentGroupFormGroup, StudentGroupFormService } from './student-group-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-student-group-update',
  templateUrl: './student-group-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StudentGroupUpdate implements OnInit {
  readonly isSaving = signal(false);
  studentGroup: IStudentGroup | null = null;

  departmentsSharedCollection = signal<IDepartment[]>([]);

  protected studentGroupService = inject(StudentGroupService);
  protected studentGroupFormService = inject(StudentGroupFormService);
  protected departmentService = inject(DepartmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StudentGroupFormGroup = this.studentGroupFormService.createStudentGroupFormGroup();

  compareDepartment = (o1: IDepartment | null, o2: IDepartment | null): boolean => this.departmentService.compareDepartment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ studentGroup }) => {
      this.studentGroup = studentGroup;
      if (studentGroup) {
        this.updateForm(studentGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const studentGroup = this.studentGroupFormService.getStudentGroup(this.editForm);
    if (studentGroup.id === null) {
      this.subscribeToSaveResponse(this.studentGroupService.create(studentGroup));
    } else {
      this.subscribeToSaveResponse(this.studentGroupService.update(studentGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStudentGroup | null>): void {
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

  protected updateForm(studentGroup: IStudentGroup): void {
    this.studentGroup = studentGroup;
    this.studentGroupFormService.resetForm(this.editForm, studentGroup);

    this.departmentsSharedCollection.update(departments =>
      this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, studentGroup.department),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartment[]) =>
          this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, this.studentGroup?.department),
        ),
      )
      .subscribe((departments: IDepartment[]) => this.departmentsSharedCollection.set(departments));
  }
}
