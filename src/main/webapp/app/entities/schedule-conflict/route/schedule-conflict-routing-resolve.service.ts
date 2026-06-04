import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IScheduleConflict } from '../schedule-conflict.model';
import { ScheduleConflictService } from '../service/schedule-conflict.service';

const scheduleConflictResolve = (route: ActivatedRouteSnapshot): Observable<null | IScheduleConflict> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ScheduleConflictService);
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

export default scheduleConflictResolve;
