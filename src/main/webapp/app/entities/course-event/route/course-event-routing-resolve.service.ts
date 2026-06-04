import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICourseEvent } from '../course-event.model';
import { CourseEventService } from '../service/course-event.service';

const courseEventResolve = (route: ActivatedRouteSnapshot): Observable<null | ICourseEvent> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CourseEventService);
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

export default courseEventResolve;
