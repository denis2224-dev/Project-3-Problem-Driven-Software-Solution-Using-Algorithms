import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IExam } from '../exam.model';
import { ExamService } from '../service/exam.service';

const examResolve = (route: ActivatedRouteSnapshot): Observable<null | IExam> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ExamService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default examResolve;
