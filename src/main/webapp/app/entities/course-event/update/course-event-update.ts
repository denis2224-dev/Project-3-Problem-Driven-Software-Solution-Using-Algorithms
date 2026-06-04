import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { CourseEventType } from 'app/entities/enumerations/course-event-type.model';
import { IProfessor } from 'app/entities/professor/professor.model';
import { ProfessorService } from 'app/entities/professor/service/professor.service';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ICourseEvent } from '../course-event.model';
import { CourseEventService } from '../service/course-event.service';

import { CourseEventFormGroup, CourseEventFormService } from './course-event-form.service';
import { StudentGroupService } from 'app/entities/student-group/service/student-group.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-course-event-update',
  templateUrl: './course-event-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CourseEventUpdate implements OnInit {
  readonly isSaving = signal(false);
  courseEvent: ICourseEvent | null = null;
  courseEventTypeValues = Object.keys(CourseEventType);

  coursesSharedCollection = signal<ICourse[]>([]);
  professorsSharedCollection = signal<IProfessor[]>([]);
  studentGroupsSharedCollection = signal<IStudentGroup[]>([]);

  protected courseEventService = inject(CourseEventService);
  protected courseEventFormService = inject(CourseEventFormService);
  protected courseService = inject(CourseService);
  protected professorService = inject(ProfessorService);
  protected studentGroupService = inject(StudentGroupService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CourseEventFormGroup = this.courseEventFormService.createCourseEventFormGroup();

  compareCourse = (o1: ICourse | null, o2: ICourse | null): boolean => this.courseService.compareCourse(o1, o2);

  compareProfessor = (o1: IProfessor | null, o2: IProfessor | null): boolean => this.professorService.compareProfessor(o1, o2);

  compareStudentGroup = (o1: IStudentGroup | null, o2: IStudentGroup | null): boolean =>
    this.studentGroupService.compareStudentGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ courseEvent }) => {
      this.courseEvent = courseEvent;
      if (courseEvent) {
        this.updateForm(courseEvent);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const courseEvent = this.courseEventFormService.getCourseEvent(this.editForm);
    if (courseEvent.id === null) {
      this.subscribeToSaveResponse(this.courseEventService.create(courseEvent));
    } else {
      this.subscribeToSaveResponse(this.courseEventService.update(courseEvent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICourseEvent | null>): void {
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

  protected updateForm(courseEvent: ICourseEvent): void {
    this.courseEvent = courseEvent;
    this.courseEventFormService.resetForm(this.editForm, courseEvent);

    this.coursesSharedCollection.update(courses => this.courseService.addCourseToCollectionIfMissing<ICourse>(courses, courseEvent.course));
    this.professorsSharedCollection.update(professors =>
      this.professorService.addProfessorToCollectionIfMissing<IProfessor>(professors, courseEvent.professor),
    );
    this.studentGroupsSharedCollection.update(studentGroups =>
      this.studentGroupService.addStudentGroupToCollectionIfMissing<IStudentGroup>(studentGroups, courseEvent.studentGroup),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.courseService
      .query()
      .pipe(map((res: HttpResponse<ICourse[]>) => res.body ?? []))
      .pipe(map((courses: ICourse[]) => this.courseService.addCourseToCollectionIfMissing<ICourse>(courses, this.courseEvent?.course)))
      .subscribe((courses: ICourse[]) => this.coursesSharedCollection.set(courses));

    this.professorService
      .query()
      .pipe(map((res: HttpResponse<IProfessor[]>) => res.body ?? []))
      .pipe(
        map((professors: IProfessor[]) =>
          this.professorService.addProfessorToCollectionIfMissing<IProfessor>(professors, this.courseEvent?.professor),
        ),
      )
      .subscribe((professors: IProfessor[]) => this.professorsSharedCollection.set(professors));

    this.studentGroupService
      .query()
      .pipe(map((res: HttpResponse<IStudentGroup[]>) => res.body ?? []))
      .pipe(
        map((studentGroups: IStudentGroup[]) =>
          this.studentGroupService.addStudentGroupToCollectionIfMissing<IStudentGroup>(studentGroups, this.courseEvent?.studentGroup),
        ),
      )
      .subscribe((studentGroups: IStudentGroup[]) => this.studentGroupsSharedCollection.set(studentGroups));
  }
}
