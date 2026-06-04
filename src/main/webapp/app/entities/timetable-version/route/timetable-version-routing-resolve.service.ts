import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TimetableVersionService } from '../service/timetable-version.service';
import { ITimetableVersion } from '../timetable-version.model';

const timetableVersionResolve = (route: ActivatedRouteSnapshot): Observable<null | ITimetableVersion> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TimetableVersionService);
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

export default timetableVersionResolve;
