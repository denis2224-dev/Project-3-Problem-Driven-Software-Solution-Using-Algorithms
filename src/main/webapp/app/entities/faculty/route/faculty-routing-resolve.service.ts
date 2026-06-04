import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IFaculty } from '../faculty.model';
import { FacultyService } from '../service/faculty.service';

const facultyResolve = (route: ActivatedRouteSnapshot): Observable<null | IFaculty> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(FacultyService);
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

export default facultyResolve;
