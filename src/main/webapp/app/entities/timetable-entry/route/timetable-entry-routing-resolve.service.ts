import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TimetableEntryService } from '../service/timetable-entry.service';
import { ITimetableEntry } from '../timetable-entry.model';

const timetableEntryResolve = (route: ActivatedRouteSnapshot): Observable<null | ITimetableEntry> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TimetableEntryService);
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

export default timetableEntryResolve;
